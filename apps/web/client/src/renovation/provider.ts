import { createClient, type SupabaseClient, type AuthChangeEvent, type Session } from '@supabase/supabase-js';
import { AccountBinding, type LearnerIdentity } from './account';
import { localLearnerApi } from './api';
import { browserStorage, type JourneyStorage } from './storage';
import { LearnerIdentityDeletion, type IdentityDeletionTransport } from './identity-deletion';
import { IdentityDeletionBeginSchema, IdentityDeletionResponseSchema } from '@german-master/contracts';
import { AccountRecovery } from './account-recovery';

type Auth = Pick<SupabaseClient['auth'], 'getSession' | 'getUser' | 'signOut'>;
const uuid = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

/** Credentials stay in the auth provider, never in learner stores or frozen requests.
 * The server remains responsible for signature and current auth-session verification. */
export class VerifiedLearnerProvider {
  private active: LearnerIdentity | null = null;
  private generation = 0;
  private operation = 0;
  private online = false;
  private verifiedExpiry = 0;
  private binding: AccountBinding | null = null;
  private sessionId: string | null = null;
  private verifiedEmail: string | null = null;
  constructor(private readonly auth: Auth, private readonly projectRef: string, private readonly now = () => Date.now(), private readonly saved: JourneyStorage = browserStorage) {
    if (!/^[a-z]{20}$/.test(projectRef)) throw Error('Invalid auth project reference');
  }

  invalidate() { this.operation++; this.active = null; this.binding = null; this.sessionId = null; this.online = false; this.verifiedEmail = null; }

  emailFor(account: AccountBinding) {
    try { this.assertVerified(account); return this.verifiedEmail; }
    catch { return null; }
  }

  /** SIGNED_IN also means session recovery on tab visibility, not just login.
   * Event data only decides whether to retain local UI; bind still verifies it. */
  observeAuthEvent(event: AuthChangeEvent, session: Session | null) {
    if (event === 'SIGNED_OUT' || event === 'PASSWORD_RECOVERY') { this.invalidate(); return false; }
    if (!['INITIAL_SESSION','SIGNED_IN','USER_UPDATED','TOKEN_REFRESHED'].includes(event)) return false;
    let sessionId: unknown;
    try { sessionId = JSON.parse(atob(session!.access_token.split('.')[1].replace(/-/g,'+').replace(/_/g,'/'))).session_id; } catch { /* Untrusted/missing event: fail closed. */ }
    const retained = !!this.active && session?.user.id.toLowerCase() === this.active.subject
      && typeof sessionId === 'string' && uuid.test(sessionId) && (this.sessionId === null || this.sessionId === sessionId);
    if (!retained) this.invalidate();
    return retained;
  }

  private get savedKey() { return `gm-v2-last-verified-${this.projectRef}`; }
  hasSavedAccount() {
    try {const subject=this.saved.getItem(this.savedKey);return !!subject&&uuid.test(subject);} catch {return false;}
  }
  assertVerified(account: AccountBinding) {
    account.assertCurrent();
    if(!this.online || this.verifiedExpiry <= this.now()) throw Error('Sign in before syncing');
  }
  localBinding() {
    const subject = this.saved.getItem(this.savedKey);
    if (!subject || !uuid.test(subject)) return null;
    this.invalidate();
    this.active = { subject: subject.toLowerCase(), generation: ++this.generation };
    this.binding = new AccountBinding(this.active, () => this.active);
    return this.binding;
  }

  async bind() {
    const operation = ++this.operation;
    // Same-session background verification must not temporarily disable a
    // previously verified transport. Every request still verifies a credential.
    let credential: Awaited<ReturnType<VerifiedLearnerProvider['credential']>>;
    try { credential = await this.credential(); }
    catch(error) { if(operation===this.operation)this.online=false;throw error; }
    if (operation !== this.operation) throw Error('Sign-in changed');
    this.saved.setItem(this.savedKey, credential.subject);
    if (!this.active || this.active.subject !== credential.subject || (this.sessionId !== null && this.sessionId !== credential.sessionId)) {
      this.active = { subject: credential.subject, generation: ++this.generation };
      this.binding = new AccountBinding(this.active, () => this.active);
    }
    this.sessionId = credential.sessionId;
    this.verifiedEmail = credential.email;
    this.online = true;
    this.verifiedExpiry = credential.expiresAt;
    return this.binding!;
  }

  private async credential() {
    const { data, error } = await this.auth.getSession();
    const session = data.session;
    if (error || !session || !uuid.test(session.user.id) || !session.access_token
      || session.access_token.length > 16384 || !/^[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+$/.test(session.access_token)
      || !Number.isSafeInteger(session.expires_at) || session.expires_at! * 1000 <= this.now())
      throw Error('Sign in before syncing');
    let claims;
    try { claims = JSON.parse(atob(session.access_token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))); }
    catch { throw Error('Invalid account credential'); }
    if (claims.iss !== `https://${this.projectRef}.supabase.co/auth/v1` || claims.aud !== 'authenticated'
      || claims.role !== 'authenticated' || claims.sub !== session.user.id || !uuid.test(claims.session_id)
      || claims.exp !== session.expires_at) throw Error('Invalid account credential');
    const verified = await this.auth.getUser(session.access_token);
    if (verified.error || !verified.data.user || verified.data.user.is_anonymous
      || verified.data.user.id !== session.user.id || session.expires_at! * 1000 <= this.now())
      throw Error('Account verification unavailable');
    return { subject: session.user.id.toLowerCase(), email: verified.data.user.email ?? null, sessionId: claims.session_id as string, token: session.access_token, expiresAt: session.expires_at! * 1000 };
  }

  api(account: AccountBinding, origin: string, send: typeof fetch = fetch) {
    const url = new URL(origin);
    if (url.protocol !== 'https:' || url.username || url.password || url.pathname !== '/' || url.search || url.hash)
      throw Error('HTTPS API origin required');
    const transport: typeof fetch = async (input, init) => {
      account.assertCurrent();
      new LearnerIdentityDeletion(account,account.storage(this.saved)).assertActive();
      if (!this.online) throw Error('Sign in before syncing');
      const credential = await this.credential();
      account.assertCurrent();
      if (credential.subject !== account.identity.subject || credential.sessionId !== this.sessionId) { this.invalidate(); throw Error('Account changed'); }
      if (typeof input !== 'string' || !input.startsWith('/v2/') || input.startsWith('//')) throw Error('Invalid learner route');
      const headers = new Headers(init?.headers);
      headers.set('Authorization', `Bearer ${credential.token}`);
      headers.set('X-Learner-Subject', account.identity.subject);
      const response = await send(new URL(input, url), { ...init, headers, cache: 'no-store', credentials: 'omit', redirect: 'error' });
      account.assertCurrent();
      new LearnerIdentityDeletion(account,account.storage(this.saved)).assertActive();
      if (response.status === 401) { this.online = false; throw Error('Sign in before syncing'); }
      return response;
    };
    const api = localLearnerApi(account, transport);
    // Real service deletion stays closed until host-auth deletion is integrated.
    delete api.deleteLearner;
    return api;
  }

  identityDeletion(account:AccountBinding,origin:string,send:typeof fetch=fetch):IdentityDeletionTransport {
    const url=new URL(origin);
    if(url.protocol!=='https:' || url.username || url.password || url.pathname!=='/' || url.search || url.hash) throw Error('HTTPS API origin required');
    const request=async (path:string,body:unknown,headers:Headers)=> {
      headers.set('Content-Type','application/json');
      const response=await send(new URL(path,url),{method:'POST',body:JSON.stringify(body),headers,cache:'no-store',credentials:'omit',redirect:'error',signal:AbortSignal.timeout(15000)});
      if(!response.ok)throw Error('Identity deletion unavailable');
      return IdentityDeletionResponseSchema.parse(await response.json());
    };
    return {
      begin:async (frozen,proof)=> {
        account.assertCurrent();if(!this.online)throw Error('Sign in before deleting');
        const credential=await this.credential();account.assertCurrent();
        if(credential.subject!==account.identity.subject)throw Error('Account changed');
        const receipt=await request('/v2/me/identity-deletion:begin',IdentityDeletionBeginSchema.parse({...frozen,confirmation:'delete_identity',proof}),
          new Headers({Authorization:`Bearer ${credential.token}`,'X-Learner-Subject':account.identity.subject}));
        // Deletion may have invalidated the session while the response was in flight.
        // The frozen capability/UUID, rather than the surviving SDK binding, owns it.
        return receipt;
      },
      recover:frozen=>request('/v2/me/identity-deletion:status',frozen,new Headers()),
    };
  }

  async clearDeletedIdentity(account:AccountBinding) {
    const marker=new LearnerIdentityDeletion(account,account.storage(this.saved)).read();
    if(marker?.receipt?.status!=='identity_deleted')throw Error('Identity deletion is not confirmed');
    // Clear only captured-subject credentials; another signed-in account is untouched.
    const {data,error}=await this.auth.getSession();if(error)throw Error('Local credential cleanup unavailable');
    if(data.session?.user.id.toLowerCase()===account.identity.subject) {
      const result=await this.auth.signOut({scope:'local'});if(result.error)throw Error('Local credential cleanup unavailable');
    }
    if(this.active?.subject===account.identity.subject)this.invalidate();
  }

  async forgetDeletedIdentity(account:AccountBinding) {
    if(!new LearnerIdentityDeletion(account,account.storage(this.saved)).read()?.complete)throw Error('Local deletion is not complete');
    if(this.saved.getItem(this.savedKey)===account.identity.subject)this.saved.setItem(this.savedKey,'');
  }

  async revoke(account: AccountBinding) {
    account.assertCurrent();
    const credential = await this.credential();
    account.assertCurrent();
    if (credential.subject !== account.identity.subject) { this.invalidate(); throw Error('Account changed'); }
    const { error } = await this.auth.signOut({ scope: 'local' });
    if (error) throw Error('Sign-out revocation unavailable');
    this.saved.setItem(this.savedKey, '');
    this.invalidate();
  }
}

/** Dedicated 2.0 provider storage; never reuses legacy auth or accepts a secret key. */
export function createLearnerProvider(projectRef: string, publishableKey: string) {
  if (!/^[a-z]{20}$/.test(projectRef) || !publishableKey.startsWith('sb_publishable_')) throw Error('Publishable auth configuration required');
  const client = createClient(`https://${projectRef}.supabase.co`, publishableKey, {
    auth: { storageKey: `gm-v2-auth-${projectRef}`, flowType: 'pkce' },
    global: { fetch: (url, options) => fetch(url,{...options,signal:AbortSignal.timeout(10000),cache:'no-store',redirect:'error'}) },
  });
  const provider = new VerifiedLearnerProvider(client.auth, projectRef);
  const recovery = new AccountRecovery(client.auth);
  const { data } = client.auth.onAuthStateChange((event, session) => { recovery.observe(event, session); provider.observeAuthEvent(event, session); });
  void client.auth.initialize().then(() => recovery.initializationFinished()).catch(() => recovery.initializationFinished());
  return { client, provider, recovery, dispose: async () => { data.subscription.unsubscribe(); provider.invalidate(); await client.auth.dispose(); } };
}

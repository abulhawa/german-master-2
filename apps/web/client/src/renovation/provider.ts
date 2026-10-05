import { createClient, type SupabaseClient } from '@supabase/supabase-js';
import { AccountBinding, type LearnerIdentity } from './account';
import { localLearnerApi } from './api';

type Auth = Pick<SupabaseClient['auth'], 'getSession' | 'getUser' | 'signOut'>;
const uuid = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

/** Credentials stay in the auth provider, never in learner stores or frozen requests.
 * The server remains responsible for signature and current auth-session verification. */
export class VerifiedLearnerProvider {
  private active: LearnerIdentity | null = null;
  private generation = 0;
  private operation = 0;
  private expiresAt = 0;
  constructor(private readonly auth: Auth, private readonly projectRef: string, private readonly now = () => Date.now()) {
    if (!/^[a-z]{20}$/.test(projectRef)) throw Error('Invalid auth project reference');
  }

  invalidate() { this.operation++; this.active = null; }

  async bind() {
    this.invalidate();
    const operation = this.operation;
    const credential = await this.credential();
    if (operation !== this.operation) throw Error('Sign-in changed');
    this.active = { subject: credential.subject, generation: ++this.generation };
    this.expiresAt = credential.expiresAt;
    return new AccountBinding(this.active, () => this.expiresAt > this.now() ? this.active : null);
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
    return { subject: session.user.id.toLowerCase(), token: session.access_token, expiresAt: session.expires_at! * 1000 };
  }

  api(account: AccountBinding, origin: string, send: typeof fetch = fetch) {
    const url = new URL(origin);
    if (url.protocol !== 'https:' || url.username || url.password || url.pathname !== '/' || url.search || url.hash)
      throw Error('HTTPS API origin required');
    const transport: typeof fetch = async (input, init) => {
      account.assertCurrent();
      const credential = await this.credential();
      account.assertCurrent();
      if (credential.subject !== account.identity.subject) { this.invalidate(); throw Error('Account changed'); }
      this.expiresAt = credential.expiresAt;
      if (typeof input !== 'string' || !input.startsWith('/v2/') || input.startsWith('//')) throw Error('Invalid learner route');
      const headers = new Headers(init?.headers);
      headers.set('Authorization', `Bearer ${credential.token}`);
      headers.set('X-Learner-Subject', account.identity.subject);
      const response = await send(new URL(input, url), { ...init, headers, cache: 'no-store', credentials: 'omit', redirect: 'error' });
      account.assertCurrent();
      if (response.status === 401) { this.invalidate(); throw Error('Sign in before syncing'); }
      return response;
    };
    const api = localLearnerApi(account, transport);
    // Real service deletion stays closed until host-auth deletion is integrated.
    delete api.deleteLearner;
    return api;
  }

  async revoke(account: AccountBinding) {
    account.assertCurrent();
    const credential = await this.credential();
    account.assertCurrent();
    if (credential.subject !== account.identity.subject) { this.invalidate(); throw Error('Account changed'); }
    const { error } = await this.auth.signOut({ scope: 'local' });
    if (error) throw Error('Sign-out revocation unavailable');
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
  const { data } = client.auth.onAuthStateChange(event => {
    if (event === 'SIGNED_OUT' || event === 'SIGNED_IN' || event === 'PASSWORD_RECOVERY' || event === 'USER_UPDATED') provider.invalidate();
  });
  return { client, provider, dispose: async () => { data.subscription.unsubscribe(); provider.invalidate(); await client.auth.dispose(); } };
}

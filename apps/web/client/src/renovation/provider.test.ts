import { afterEach, expect, it, vi } from 'vitest';
import type { SupabaseClient, Session } from '@supabase/supabase-js';
import { VerifiedLearnerProvider } from './provider';
import { LearnerSignOut } from './signout';
import { LearnerIdentityDeletion } from './identity-deletion';
const project = 'zgmyrpzwgtydwlzponih';
const subject = '00000000-0000-4000-8000-000000000020';
const other = '00000000-0000-4000-8000-000000000021';
function fixture() {
  let now = 1000;
  let user = subject;
  let rejected = false;
  let expiry = 10;
  const token = () => `e30.${btoa(JSON.stringify({ iss: `https://${project}.supabase.co/auth/v1`, aud: 'authenticated', role: 'authenticated', sub: user, session_id: subject, exp: expiry })).replace(/=/g,'').replace(/\+/g,'-').replace(/\//g,'_')}.c2ln`;
  const auth = {
    getSession: vi.fn(async () => ({ data: { session: { user: { id: user }, access_token: token(), expires_at: expiry } }, error: null })),
    getUser: vi.fn(async () => ({ data: { user: { id: user, is_anonymous: false } }, error: rejected ? Error('rejected') : null })),
    signOut: vi.fn(async () => ({ error: rejected ? Error('offline') : null })),
  };
  const provider = new VerifiedLearnerProvider(auth as unknown as SupabaseClient['auth'], project, () => now);
  return { provider, auth, token, refresh:()=>{expiry=20;}, expire: () => { now = 10000; }, switch: () => { user = other; }, reject: (value: boolean) => { rejected = value; } };
}
afterEach(()=>localStorage.clear());
it('re-verifies a recovered session without replacing its account binding',async()=>{
  const f=fixture(), account=await f.provider.bind();
  for(const event of ['SIGNED_IN','TOKEN_REFRESHED','USER_UPDATED'] as const){
    if(event==='TOKEN_REFRESHED')f.refresh();
    const session={user:{id:subject},access_token:f.token()} as Session;
    expect(f.provider.observeAuthEvent(event,session)).toBe(true);
    expect(await f.provider.bind()).toBe(account);
    account.assertCurrent();
  }
  expect(f.auth.getUser).toHaveBeenCalledTimes(4);
  f.reject(true);await expect(f.provider.bind()).rejects.toThrow('verification');
  expect(()=>f.provider.assertVerified(account)).toThrow('Sign in');
  const send=vi.fn();await expect(f.provider.api(account,'https://api.example',send).profile()).rejects.toThrow('Sign in');
  expect(send).not.toHaveBeenCalled();
});
it.each(['different subject','different session','missing session','sign-out'] as const)('invalidates old authority immediately on %s',async change=>{
  const f=fixture(), account=await f.provider.bind();
  const claims={iss:`https://${project}.supabase.co/auth/v1`,aud:'authenticated',role:'authenticated',sub:change==='different subject'?other:subject,session_id:change==='different session'?other:subject,exp:10};
  const session={user:{id:claims.sub},access_token:`e30.${btoa(JSON.stringify(claims))}.c2ln`} as Session;
  expect(f.provider.observeAuthEvent(change==='sign-out'?'SIGNED_OUT':'SIGNED_IN',change==='missing session'?null:session)).toBe(false);
  expect(()=>account.assertCurrent()).toThrow('Sign in');
});
it('confirmed old-subject cleanup preserves a newly signed-in account and its saved reference',async()=> {
  const f=fixture(),a=await f.provider.bind();
  const saved={getItem:(key:string)=>localStorage.getItem(key),setItem:(key:string,value:string)=>localStorage.setItem(key,value)};
  const marker=new LearnerIdentityDeletion(a,a.storage(saved));
  await marker.deliver({begin:async request=>({apiVersion:'v2',requestId:request.requestId,status:'identity_deleted',completedAt:'2026-10-05T20:00:00Z'}),recover:vi.fn()},async()=>{}, {email:'learner@example.com',password:'fresh'});
  f.switch();const b=await f.provider.bind();
  await f.provider.clearDeletedIdentity(a);await f.provider.forgetDeletedIdentity(a);
  expect(f.auth.signOut).not.toHaveBeenCalled();b.assertCurrent();
  expect(f.provider.localBinding()?.identity.subject).toBe(other);
});
it('uses verified credentials for reads and frozen submissions; never sends fixture authorization', async () => {
  const f = fixture(); const account = await f.provider.bind();
  const request = { apiVersion: 'v2' as const, requestId: subject, questionCount: 1, capabilities: ['short_answer@1'] };
  const send = vi.fn(async (input, init) => {
    expect(input.toString()).toBe('https://api.example/v2/sessions');
    expect(new Headers(init.headers).get('Authorization')).toBe(`Bearer ${f.token()}`);
    expect(new Headers(init.headers).get('X-Learner-Subject')).toBe(subject);
    expect(init.redirect).toBe('error'); expect(init.credentials).toBe('omit');
    expect(JSON.parse(init.body)).toEqual(request);
    return new Response('', { status: 503 });
  });
  const api = f.provider.api(account, 'https://api.example', send);
  await expect(api.createSession(request)).rejects.toThrow();
  await expect(api.createFocusedSession(request)).rejects.toThrow();
  expect(send).toHaveBeenCalledTimes(2);
  expect(f.auth.getUser).toHaveBeenCalledTimes(3);
  f.switch(); await expect(api.profile()).rejects.toThrow('Account changed');
  expect(send).toHaveBeenCalledTimes(2);
});
it('rejects expiry, stale body results, auth rejection and old same-subject generations', async () => {
  const f = fixture(); const account = await f.provider.bind();
  const api = f.provider.api(account, 'https://api.example', vi.fn(async () => ({ ok: true, json: async () => {
    f.provider.invalidate(); return {};
  } }) as Response));
  await expect(api.profile()).rejects.toThrow('Sign in');
  const renewed = await f.provider.bind(); expect(() => account.assertCurrent()).toThrow();
  f.expire(); expect(() => renewed.assertCurrent()).not.toThrow();
  expect(()=>f.provider.assertVerified(renewed)).toThrow('Sign in');
  const send=vi.fn();await expect(f.provider.api(renewed,'https://api.example',send).profile()).rejects.toThrow('Sign in');
  expect(send).not.toHaveBeenCalled();
  await expect(f.provider.bind()).rejects.toThrow('Sign in');
  const g = fixture(); g.reject(true); await expect(g.provider.bind()).rejects.toThrow('verification');
});
it('cold local binding uses only the saved verified subject and blocks delivery before provider reads',async()=> {
  const f=fixture();expect(f.provider.localBinding()).toBeNull();
  const first=await f.provider.bind();
  const store=first.storage({getItem:key=>localStorage.getItem(key),setItem:(key,value)=>localStorage.setItem(key,value)});
  store.setItem('frozen','original request');
  f.expire();f.reject(true);
  const restarted=fixture();restarted.reject(true);
  const local=restarted.provider.localBinding()!;expect(local.identity.subject).toBe(subject);
  const send=vi.fn();await expect(restarted.provider.api(local,'https://api.example',send).profile()).rejects.toThrow('Sign in');
  expect(restarted.auth.getSession).not.toHaveBeenCalled();expect(send).not.toHaveBeenCalled();
  expect(local.storage({getItem:key=>localStorage.getItem(key),setItem:(key,value)=>localStorage.setItem(key,value)}).getItem('frozen')).toBe('original request');
  restarted.reject(false);const verified=await restarted.provider.bind();expect(verified).toBe(local);verified.assertCurrent();
  restarted.provider.invalidate();expect(()=>verified.assertCurrent()).toThrow();
});
it('leaves sign-out retryable after failed revocation and preserves synced drafts', async () => {
  const f = fixture(); const account = await f.provider.bind(); const values = new Map<string,string>();
  const storage = { getItem: (key: string) => values.get(key) ?? null, setItem: (key: string, value: string) => { values.set(key, value); } };
  storage.setItem('draft', 'saved'); const signout = new LearnerSignOut(account, storage);
  const sync = vi.fn(async () => {}); const cleanup = vi.fn(async () => {});
  const revoke = async () => { f.reject(false); f.auth.signOut.mockResolvedValueOnce({ error: Error('offline') }); await f.provider.revoke(account); };
  await expect(signout.finish(false, sync, cleanup, () => {}, revoke)).rejects.toThrow('revocation');
  expect(signout.read()?.complete).toBe(false); expect(storage.getItem('draft')).toBe('saved');
  await signout.finish(false, sync, cleanup, () => {}, () => f.provider.revoke(account));
  expect(signout.read()?.complete).toBe(true); expect(sync).toHaveBeenCalledOnce(); expect(cleanup).not.toHaveBeenCalled();
  expect(f.auth.signOut).toHaveBeenLastCalledWith({ scope: 'local' }); expect(() => account.assertCurrent()).toThrow();
});

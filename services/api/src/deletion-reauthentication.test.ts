import {it,expect,vi} from 'vitest';
import {passwordDeletionReauthentication,createPasswordDeletionReauthentication,type PasswordReauthenticationClient} from './deletion-reauthentication';
const project='zgmyrpzwgtydwlzponih',subject='aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',session='bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb';
const now=Date.parse('2026-10-05T12:00:00Z');
const token=`e30.${Buffer.from(JSON.stringify({iss:`https://${project}.supabase.co/auth/v1`,aud:'authenticated',role:'authenticated',sub:subject,session_id:session,exp:Math.floor(now/1000)+600})).toString('base64url')}.c2ln`;
const credentials={email:'learner@example.invalid',password:'synthetic-password'};
function client():PasswordReauthenticationClient {
  return {signInWithPassword:vi.fn(async()=>({data:{session:{access_token:token}},error:null})),
    getUser:vi.fn(async()=>({data:{user:{id:subject}},error:null})),signOut:vi.fn(async()=>({error:null}))};
}
it('uses a new password session and verifies online identity/current session before issuing server-timed proof',async()=>{
  const clients:PasswordReauthenticationClient[]=[];const live=vi.fn(async()=>true);
  const verify=passwordDeletionReauthentication(project,()=>{const auth=client();clients.push(auth);return auth;},live,()=>now);
  expect(await verify(credentials)).toEqual({subject,authenticatedAt:now});
  expect(await verify(credentials)).toEqual({subject,authenticatedAt:now});expect(clients).toHaveLength(2);
  for(const auth of clients) {expect(auth.signInWithPassword).toHaveBeenCalledWith(credentials);expect(auth.signOut).toHaveBeenCalledWith({scope:'local'});}
  expect(live).toHaveBeenCalledWith(subject,session);
});
it('rejects supplied timestamps/tokens and malformed credentials before provider calls',async()=>{
  const factory=vi.fn(client);const verify=passwordDeletionReauthentication(project,factory,async()=>true,()=>now);
  for(const proof of [null,[],{token}, {...credentials,authenticatedAt:now},{email:'bad',password:'x'}]) expect(await verify(proof)).toBeNull();
  expect(factory).not.toHaveBeenCalled();
  expect(()=>createPasswordDeletionReauthentication(project,'sb_secret_synthetic',async()=>true)).toThrow('publishable');
});
it('fails closed on failed password, wrong verified identity, absent session and temporary-session cleanup failures',async()=>{
  for(const failure of ['password','identity','session','cleanup','offline']) {
    const auth=client();
    if(failure==='password') auth.signInWithPassword=vi.fn(async()=>({data:{session:null},error:Error('invalid')}));
    if(failure==='identity') auth.getUser=vi.fn(async()=>({data:{user:{id:session}},error:null}));
    if(failure==='cleanup') auth.signOut=vi.fn(async()=>({error:Error('offline')}));
    if(failure==='offline') auth.signInWithPassword=vi.fn(async()=>{throw Error('offline');});
    expect(await passwordDeletionReauthentication(project,()=>auth,async()=>failure!=='session',()=>now)(credentials)).toBeNull();
    expect(auth.signOut).toHaveBeenCalledWith({scope:'local'});
  }
});

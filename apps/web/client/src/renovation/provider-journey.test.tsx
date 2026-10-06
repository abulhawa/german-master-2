import { afterEach, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import type { SupabaseClient } from '@supabase/supabase-js';
import { ProviderLearnerJourney } from './provider-journey';
import { VerifiedLearnerProvider, type createLearnerProvider } from './provider';
vi.mock('./journey',()=>({default:({account,revoke}:{account:{identity:{subject:string}};revoke:()=>Promise<void>})=><><p>Bound learner {account.identity.subject}</p><button onClick={()=>void revoke()}>Revoke session</button></>}));
const project='zgmyrpzwgtydwlzponih', subject='00000000-0000-4000-8000-000000000020';
function fixture() {
  let signedIn=false; let callback: (event:string)=>void=()=>{};
  const token=`e30.${btoa(JSON.stringify({iss:`https://${project}.supabase.co/auth/v1`,aud:'authenticated',role:'authenticated',sub:subject,session_id:subject,exp:9999999999})).replace(/=/g,'').replace(/\+/g,'-').replace(/\//g,'_')}.c2ln`;
  const auth={
    getSession:vi.fn(async()=>({data:{session:signedIn?{user:{id:subject},access_token:token,expires_at:9999999999}:null},error:null})),
    getUser:vi.fn(async()=>({data:{user:{id:subject,is_anonymous:false}},error:null})),
    signOut:vi.fn(async()=>{signedIn=false;callback('SIGNED_OUT');return {error:null};}),
    signInWithPassword:vi.fn(async()=>{signedIn=true;callback('SIGNED_IN');return {error:null};}),
    signUp:vi.fn(async()=>({data:{session:null},error:null})),
    onAuthStateChange:(listener:(event:string)=>void)=>{callback=listener;queueMicrotask(()=>listener('INITIAL_SESSION'));return {data:{subscription:{unsubscribe:vi.fn()}}};},
  };
  const provider=new VerifiedLearnerProvider(auth as unknown as SupabaseClient['auth'],project);
  const host={client:{auth},provider,dispose:()=>provider.invalidate()} as unknown as ReturnType<typeof createLearnerProvider>;
  return {auth,host,emit:(event:string)=>callback(event)};
}
afterEach(()=>{cleanup();localStorage.clear();});
it('registers without binding an unconfirmed identity or retaining its password',async()=> {
  const f=fixture();render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  await waitFor(()=>expect(screen.getByRole('button',{name:'Create account'})).toBeEnabled());
  fireEvent.click(screen.getByRole('button',{name:'Create account'}));
  fireEvent.change(screen.getByLabelText('Email'),{target:{value:'disposable@example.test'}});
  fireEvent.change(screen.getByLabelText('Password'),{target:{value:'synthetic-test-password'}});
  fireEvent.submit(screen.getByRole('button',{name:'Register'}).closest('form')!);
  await screen.findByText('Check your email to confirm your account, then sign in.');
  expect(f.auth.signUp).toHaveBeenCalledWith({email:'disposable@example.test',password:'synthetic-test-password',options:{emailRedirectTo:'https://api.example'}});
  expect(screen.queryByText(/Bound learner/)).toBeNull();
  expect(screen.getByLabelText('Password')).toHaveValue('');
});
it('mounts learner only after online verification and removes it on provider sign-out',async()=> {
  const f=fixture();render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  await waitFor(()=>expect(screen.getByRole('button',{name:'Sign in'})).toBeEnabled());
  expect(screen.queryByText(/Bound learner/)).toBeNull();
  fireEvent.change(screen.getByLabelText('Email'),{target:{value:'learner@example.test'}});
  fireEvent.change(screen.getByLabelText('Password'),{target:{value:'synthetic-test-password'}});
  fireEvent.submit(screen.getByRole('button',{name:'Sign in'}).closest('form')!);
  await screen.findByText(`Bound learner ${subject}`);
  expect(f.auth.signInWithPassword).toHaveBeenCalledWith({email:'learner@example.test',password:'synthetic-test-password'});
  expect(f.auth.getUser).toHaveBeenCalledOnce();
  fireEvent.click(screen.getByRole('button',{name:'Revoke session'}));
  await screen.findByLabelText('Password');
  expect(screen.queryByText(/Bound learner/)).toBeNull();expect(screen.getByLabelText('Password')).toHaveValue('');
});
it('does not mount a stale verified response after an intervening sign-out',async()=> {
  const f=fixture();let complete!:(value:Awaited<ReturnType<typeof f.auth.getUser>>)=>void;
  f.auth.getUser.mockImplementation(()=>new Promise(resolve=>{complete=resolve;}));
  render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  await waitFor(()=>expect(screen.getByRole('button',{name:'Sign in'})).toBeEnabled());
  fireEvent.submit(screen.getByRole('button',{name:'Sign in'}).closest('form')!);
  await waitFor(()=>expect(complete).toBeTypeOf('function'));
  f.emit('SIGNED_OUT');complete({data:{user:{id:subject,is_anonymous:false}},error:null});
  await waitFor(()=>expect(screen.getByRole('button',{name:'Sign in'})).toBeEnabled());
  expect(screen.queryByText(/Bound learner/)).toBeNull();
});

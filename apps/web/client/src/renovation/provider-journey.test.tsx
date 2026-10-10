import { afterEach, expect, it, vi } from 'vitest';
import { act, cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { useEffect, useState, type ReactNode } from 'react';
import type { LearnerApi } from './api';
import type { SupabaseClient, Session, AuthChangeEvent } from '@supabase/supabase-js';
import { ProviderLearnerJourney } from './provider-journey';
import { VerifiedLearnerProvider, type createLearnerProvider } from './provider';
import { AccountRecovery } from './account-recovery';
import { guestAttemptCount, guestUnattachedAttemptCount } from './guest-starter';
const reads=vi.hoisted(()=>({enabled:false}));
vi.mock('./journey',()=>({default:({account,revoke,api,accountSecurity}:{account:{identity:{subject:string}};revoke:()=>Promise<void>;api:LearnerApi;accountSecurity?:(locale:'en'|'de',blocked:boolean)=>ReactNode})=>{
  const [detail,setDetail]=useState(false);
  const [data,setData]=useState('');
  useEffect(()=>{if(!reads.enabled)return;let alive=true;void api.profile().then(()=>{if(alive)setData('Data loaded');}).catch(()=>{if(alive)setData('Data blocked');});return()=>{alive=false;};},[api]);
  return <><p>Bound learner {account.identity.subject}</p><p>{data}</p>{accountSecurity?.('en',false)}<button onClick={()=>setDetail(true)}>Open detail</button>{detail&&<h1>Retained detail</h1>}<button onClick={()=>void revoke()}>Revoke session</button></>;
}}));
const project='zgmyrpzwgtydwlzponih', subject='00000000-0000-4000-8000-000000000020';
function fixture(initiallySignedIn=false) {
  let signedIn=initiallySignedIn; let callback: (event:AuthChangeEvent,session:Session|null)=>void=()=>{};
  const token=`e30.${btoa(JSON.stringify({iss:`https://${project}.supabase.co/auth/v1`,aud:'authenticated',role:'authenticated',sub:subject,session_id:subject,exp:9999999999})).replace(/=/g,'').replace(/\+/g,'-').replace(/\//g,'_')}.c2ln`;
  const session=()=>signedIn?{user:{id:subject},access_token:token,expires_at:9999999999} as Session:null;
  const auth={
    getSession:vi.fn(async()=>({data:{session:signedIn?{user:{id:subject},access_token:token,expires_at:9999999999}:null},error:null})),
    getUser:vi.fn(async()=>({data:{user:{id:subject,is_anonymous:false}},error:null})),
    signOut:vi.fn(async()=>{signedIn=false;callback('SIGNED_OUT',null);return {error:null};}),
    signInWithPassword:vi.fn(async()=>{signedIn=true;callback('SIGNED_IN',session());return {error:null};}),
    signUp:vi.fn(async()=>({data:{session:null},error:null})),
    resetPasswordForEmail:vi.fn(async()=>({data:{},error:null})),
    updateUser:vi.fn(async()=>({data:{user:{id:subject}},error:null})),
    onAuthStateChange:(listener:typeof callback)=>{callback=listener;queueMicrotask(()=>listener('INITIAL_SESSION',session()));return {data:{subscription:{unsubscribe:vi.fn()}}};},
  };
  const provider=new VerifiedLearnerProvider(auth as unknown as SupabaseClient['auth'],project);
  const host={client:{auth},provider,dispose:()=>provider.invalidate()} as unknown as ReturnType<typeof createLearnerProvider>;
  return {auth,host,emit:(event:AuthChangeEvent)=>callback(event,session())};
}
afterEach(()=>{cleanup();localStorage.clear();window.history.replaceState(null,'','/');vi.unstubAllGlobals();reads.enabled=false;});
it('shows the verified account email and opens a prefilled reset with an Account return path',async()=>{
  const f=fixture(true);
  f.auth.getUser.mockResolvedValue({data:{user:Object.assign({id:subject,is_anonymous:false},{email:'verified@example.test'})},error:null});
  render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  await screen.findByText('verified@example.test');
  fireEvent.click(screen.getByRole('button',{name:'Reset password',exact:true}));
  expect(screen.getByLabelText('Email')).toHaveValue('verified@example.test');
  expect(f.auth.resetPasswordForEmail).not.toHaveBeenCalled();
  fireEvent.submit(screen.getByRole('button',{name:'Send email'}).closest('form')!);
  await screen.findByText(/If this address is eligible/);
  expect(f.auth.resetPasswordForEmail).toHaveBeenCalledWith('verified@example.test',{redirectTo:new URL('/?auth=recovery',window.location.origin).href});
  fireEvent.click(screen.getByRole('button',{name:'Back to Account'}));
  await screen.findByText('verified@example.test');
});
it('keeps the current email until a verified email-change event, without changing learner identity',async()=>{
  const f=fixture(true);
  f.auth.getUser.mockResolvedValue({data:{user:Object.assign({id:subject,is_anonymous:false},{email:'current@example.test'})},error:null});
  render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  await screen.findByText('current@example.test');
  fireEvent.click(screen.getByRole('button',{name:'Change email',exact:true}));
  fireEvent.change(screen.getByLabelText('New email address'),{target:{value:'new@example.test'}});
  fireEvent.submit(screen.getByRole('button',{name:'Send verification emails'}).closest('form')!);
  await screen.findByText(/Change requested/);
  expect(f.auth.updateUser).toHaveBeenCalledWith({email:'new@example.test'},{emailRedirectTo:new URL('/',window.location.origin).href});
  expect(screen.getByText('current@example.test')).toBeInTheDocument();
  fireEvent.click(screen.getByRole('button',{name:'Back to Account'}));
  await screen.findByText('current@example.test');
  f.auth.getUser.mockResolvedValue({data:{user:Object.assign({id:subject,is_anonymous:false},{email:'new@example.test'})},error:null});
  await act(async()=>f.emit('USER_UPDATED'));
  await screen.findByText('new@example.test');
  expect(screen.queryByText('current@example.test')).toBeNull();
  expect(screen.getByText(`Bound learner ${subject}`)).toBeInTheDocument();
});
it('does not update email when the verified subject changes during the request',async()=>{
  const f=fixture(true);
  f.auth.getUser.mockResolvedValue({data:{user:Object.assign({id:subject,is_anonymous:false},{email:'current@example.test'})},error:null});
  render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  await screen.findByText('current@example.test');
  fireEvent.click(screen.getByRole('button',{name:'Change email',exact:true}));
  f.auth.getUser.mockResolvedValueOnce({data:{user:{id:'00000000-0000-4000-8000-000000000021',is_anonymous:false}},error:null});
  fireEvent.change(screen.getByLabelText('New email address'),{target:{value:'new@example.test'}});
  fireEvent.submit(screen.getByRole('button',{name:'Send verification emails'}).closest('form')!);
  await screen.findByRole('alert');
  expect(f.auth.updateUser).not.toHaveBeenCalled();
});
it('automatically retries cold local reads after verification without losing the selected view',async()=>{
  reads.enabled=true;localStorage.setItem(`gm-v2-last-verified-${project}`,subject);
  const f=fixture(true);
  let release!:(value:Awaited<ReturnType<typeof f.auth.getUser>>)=>void;
  f.auth.getUser.mockImplementationOnce(()=>new Promise(resolve=>{release=resolve;}));
  vi.stubGlobal('fetch',vi.fn(async()=>new Response(JSON.stringify({apiVersion:'v2',revision:1,setupCompleted:true,preferences:{locale:'en',timezone:'Europe/Berlin',level:'B1',sessionQuestionCount:15}}),{status:200})));
  render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  await screen.findByText('Data blocked');fireEvent.click(screen.getByRole('button',{name:'Open detail'}));
  await waitFor(()=>expect(release).toBeTypeOf('function'));
  await act(async()=>release({data:{user:{id:subject,is_anonymous:false}},error:null}));
  await screen.findByText('Data loaded');
  expect(screen.getByRole('heading',{name:'Retained detail'})).toBeInTheDocument();
});
async function openAuth() {
  fireEvent.click(await screen.findByRole('button',{name:'I already have an account'}));
  await waitFor(()=>expect(screen.getByRole('button',{name:'Sign in'})).toBeEnabled());
}
it('registers without binding an unconfirmed identity or retaining its password',async()=> {
  const f=fixture();render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  await openAuth();
  await waitFor(()=>expect(screen.getByRole('button',{name:'Create account'})).toBeEnabled());
  fireEvent.click(screen.getByRole('button',{name:'Create account'}));
  fireEvent.change(screen.getByLabelText('Email'),{target:{value:'disposable@example.test'}});
  fireEvent.change(screen.getByLabelText('Password'),{target:{value:'synthetic-test-password'}});
  fireEvent.submit(screen.getByRole('button',{name:'Register'}).closest('form')!);
  await screen.findByText('Check your email to confirm your account, then sign in.');
  expect(f.auth.signUp).toHaveBeenCalledWith({email:'disposable@example.test',password:'synthetic-test-password',options:{emailRedirectTo:new URL('/',window.location.origin).href}});
  expect(screen.queryByText(/Bound learner/)).toBeNull();
  expect(screen.getByLabelText('Password')).toHaveValue('');
});
it.each(['SIGNED_IN','TOKEN_REFRESHED','USER_UPDATED'] as const)('retains detail during and after same-session %s verification',async event=>{
  const f=fixture();render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  await openAuth();fireEvent.submit(screen.getByRole('button',{name:'Sign in'}).closest('form')!);
  await screen.findByText(`Bound learner ${subject}`);
  fireEvent.click(screen.getByRole('button',{name:'Open detail'}));
  let release!:(value:Awaited<ReturnType<typeof f.auth.getUser>>)=>void;
  f.auth.getUser.mockImplementationOnce(()=>new Promise(resolve=>{release=resolve;}));
  await act(async()=>f.emit(event));
  await waitFor(()=>expect(release).toBeTypeOf('function'));
  expect(screen.getByRole('heading',{name:'Retained detail'})).toBeInTheDocument();
  await act(async()=>release({data:{user:{id:subject,is_anonymous:false}},error:null}));
  expect(screen.getByRole('heading',{name:'Retained detail'})).toBeInTheDocument();
  expect(f.auth.getUser).toHaveBeenCalledTimes(2);
});
it('retains detail on failed same-session verification and still removes it on sign-out',async()=>{
  const f=fixture();render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  await openAuth();fireEvent.submit(screen.getByRole('button',{name:'Sign in'}).closest('form')!);
  await screen.findByText(`Bound learner ${subject}`);fireEvent.click(screen.getByRole('button',{name:'Open detail'}));
  f.auth.getUser.mockRejectedValueOnce(Error('offline'));
  await act(async()=>f.emit('SIGNED_IN'));
  expect(screen.getByRole('heading',{name:'Retained detail'})).toBeInTheDocument();
  await act(async()=>f.emit('SIGNED_OUT'));
  expect(screen.queryByRole('heading',{name:'Retained detail'})).toBeNull();
});
it('mounts learner only after online verification and removes it on provider sign-out',async()=> {
  const f=fixture();render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  expect(await screen.findByRole('button',{name:'Try German Master'})).toBeEnabled();
  await openAuth();
  expect(screen.queryByText(/Bound learner/)).toBeNull();
  fireEvent.change(screen.getByLabelText('Email'),{target:{value:'learner@example.test'}});
  fireEvent.change(screen.getByLabelText('Password'),{target:{value:'synthetic-test-password'}});
  fireEvent.submit(screen.getByRole('button',{name:'Sign in'}).closest('form')!);
  await screen.findByText(`Bound learner ${subject}`);
  expect(f.auth.signInWithPassword).toHaveBeenCalledWith({email:'learner@example.test',password:'synthetic-test-password'});
  expect(f.auth.getUser).toHaveBeenCalledOnce();
  fireEvent.click(screen.getByRole('button',{name:'Revoke session'}));
  await screen.findByRole('button',{name:'Try German Master'});
  expect(screen.queryByText(/Bound learner/)).toBeNull();expect(screen.queryByLabelText('Password')).toBeNull();
});
it('requires explicit consent after authentication and attaches guest evidence only on confirmation',async()=> {
  const send=vi.fn(async (_input:RequestInfo|URL,init?:RequestInit)=> {
    const request=JSON.parse(String(init?.body));
    return new Response(JSON.stringify({apiVersion:'v2',acknowledgments:request.attempts.map((attempt:any,index:number)=>({
      attemptId:attempt.attemptId,status:'accepted',evaluation:{outcome:'correct',policyVersion:'deterministic-v1/de-nfc-trim-v1',
        explanation:{en:'ok',de:'ok'},acceptedAnswer:attempt.answer,assisted:attempt.assistance.length>0},serverSequence:index+1,
    }))}),{status:200,headers:{'Content-Type':'application/json'}});
  });
  vi.stubGlobal('fetch',send);
  const f=fixture();render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  fireEvent.click(await screen.findByRole('button',{name:'Try German Master'}));
  fireEvent.change(screen.getByLabelText('Your answer'),{target:{value:'Berufe'}});
  fireEvent.click(screen.getByRole('button',{name:'Check answer'}));
  expect(guestAttemptCount()).toBe(1);expect(guestUnattachedAttemptCount(subject)).toBe(1);
  fireEvent.click(screen.getByRole('button',{name:'Close practice'}));
  fireEvent.click(screen.getByRole('button',{name:'Save and return Home'}));
  fireEvent.click(screen.getByRole('button',{name:'Sign in'}));
  fireEvent.change(screen.getByLabelText('Email'),{target:{value:'learner@example.test'}});
  fireEvent.change(screen.getByLabelText('Password'),{target:{value:'synthetic-test-password'}});
  fireEvent.submit(screen.getByRole('button',{name:'Sign in'}).closest('form')!);
  expect(await screen.findByRole('heading',{name:'Save your guest practice?'})).toBeInTheDocument();
  expect(screen.queryByText(/Bound learner/)).toBeNull();expect(send).not.toHaveBeenCalled();
  fireEvent.click(screen.getByRole('button',{name:'Save 1 attempt'}));
  await screen.findByRole('heading',{name:'Guest practice saved'});
  expect(guestUnattachedAttemptCount(subject)).toBe(0);
  fireEvent.click(screen.getByRole('button',{name:'Continue'}));
  await screen.findByText(`Bound learner ${subject}`);
  expect(send).toHaveBeenCalledOnce();
});
it('offers a fresh recovery email directly after an expired callback',async()=>{
  window.history.replaceState(null,'','/?auth=recovery');
  const f=fixture(), recovery=new AccountRecovery(f.auth as unknown as SupabaseClient['auth']);
  recovery.initializationFinished();
  const host={...f.host,recovery} as ReturnType<typeof createLearnerProvider>;
  render(<ProviderLearnerJourney host={host} origin="https://api.example" />);
  expect(await screen.findByText(/This link has expired or could not be opened/)).toBeInTheDocument();
  fireEvent.click(screen.getByRole('button',{name:'Request a new recovery email'}));
  expect(screen.getByRole('heading',{name:'Reset your password'})).toBeInTheDocument();
  expect(screen.getByLabelText('Email')).toBeInTheDocument();
});
it('does not mount a stale verified response after an intervening sign-out',async()=> {
  const f=fixture();let complete!:(value:Awaited<ReturnType<typeof f.auth.getUser>>)=>void;
  f.auth.getUser.mockImplementation(()=>new Promise(resolve=>{complete=resolve;}));
  render(<ProviderLearnerJourney host={f.host} origin="https://api.example" />);
  await openAuth();
  fireEvent.submit(screen.getByRole('button',{name:'Sign in'}).closest('form')!);
  await waitFor(()=>expect(complete).toBeTypeOf('function'));
  f.emit('SIGNED_OUT');complete({data:{user:{id:subject,is_anonymous:false}},error:null});
  await waitFor(()=>expect(screen.getByRole('button',{name:'Try German Master'})).toBeEnabled());
  expect(screen.queryByText(/Bound learner/)).toBeNull();
});

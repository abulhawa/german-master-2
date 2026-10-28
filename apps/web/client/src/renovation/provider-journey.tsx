import { useLearnerRoute, navigateLearner, readLearnerRoute, backInLearner } from './navigation';
import { useEffect, useMemo, useRef, useState, useSyncExternalStore } from 'react';
import { useAppearance } from './appearance';
import LearnerJourney from './journey';
import type { AccountBinding } from './account';
import { createLearnerProvider } from './provider';
import { FoundationButton, PracticeCard } from "../foundation/controls";
import { LearnerSignOut } from './signout';
import { browserStorage } from './storage';

import { GuestStarterJourney, buildGuestRoundAdoptionRequest, guestAttemptCount, hasGuestSaveIntent, clearGuestPractice } from './guest-starter';
import { installAdoptedRound } from './adopted-round';
import { readJourney } from './storage';

import { providerCopy as copy } from './provider-locales';
import { StudyArt } from './study-art';
import { shellCopy } from './shell-locales';
import { AccountRecovery } from './account-recovery';
import { RecoveryEmail, RecoveryPassword } from './account-recovery-ui';
import { recoveryCopy } from './recovery-locales';
import { AccountEmailChange } from './account-email-change';
import { GoogleIcon } from '../components/auth/auth-dialog/google-icon';
import { MicrosoftIcon } from '../components/auth/auth-dialog/microsoft-icon';
import { releaseCapabilities } from './release-capabilities';

/** Explicitly configured host. The fixture preview and legacy release remain separate. */
export function ProviderLearnerJourney({host,origin,deletionEnabled=false}:{host:ReturnType<typeof createLearnerProvider>;origin:string;deletionEnabled?:boolean}) {
  const appearance = useAppearance();
  const [account,setAccount] = useState<AccountBinding|null>(null);
  const [busy,setBusy] = useState(true);
  const [failed,setFailed] = useState(false);
  const [oauthFailed,setOAuthFailed] = useState(()=>new URLSearchParams(window.location.search).has('error'));
  const [retry,setRetry] = useState(0);
  const route=useLearnerRoute();
  const showLogin=route.view==='sign-in'||route.view==='register'||oauthFailed;
  function finishSignIn(){if(['sign-in','register'].includes(readLearnerRoute().view))navigateLearner('home',null,true);}
  const [local,setLocal] = useState(false);
  const loginLock=useRef(false);
  const [locale,setLocale] = useState<'en'|'de'>('en');
  const [email,setEmail] = useState(''); const [password,setPassword] = useState('');
  const register=route.view==='register';
  const [confirmation,setConfirmation] = useState(false);
  const emailHelp=route.view==='reset'||route.view==='resend'?route.view:null;

  const [emailChange,setEmailChange]=useState<AccountBinding|null>(null);
  const recovery = useMemo(()=>host.recovery ?? new AccountRecovery(host.client.auth),[host]);
  const recoveryState = useSyncExternalStore(recovery.subscribe,recovery.getSnapshot);
  const [attachDismissed,setAttachDismissed] = useState(false);

  const attachmentLock=useRef(false);

  const [attachBusy,setAttachBusy] = useState(false);
  const [attachFailed,setAttachFailed] = useState(false);
  const [attachResult,setAttachResult] = useState<{saved:number;remaining:number;validationOnly:boolean}|null>(null);
  const c=copy[locale];
  useEffect(()=>{setPassword('');},[route.view]);
  useEffect(()=> {
    const observer=new MutationObserver(()=> {
      const lang=document.querySelector('main[lang]')?.getAttribute('lang');
      if(lang==='en'||lang==='de') setLocale(lang);
    });
    observer.observe(document.getElementById('root') ?? document.body,{subtree:true,attributes:true,attributeFilter:['lang'],childList:true});
    return ()=>observer.disconnect();
  },[]);
  useEffect(()=> {
    let alive=true; let ticket=0;
    if(recovery.getSnapshot().stage==='none')try {const binding=host.provider.localBinding();if(binding){setAccount(binding);setLocal(true);}} catch {setFailed(true);}
    function verify(allowLocal=false, resume=false, retain=false) {
      if(recovery.getSnapshot().stage!=='none')return;
      const current=++ticket;if(!retain)setAccount(null);setBusy(true);setFailed(false);
      if(allowLocal) {
        try {const saved=host.provider.localBinding();if(saved){setAccount(saved);setLocal(true);}} catch {setFailed(true);}
      }
      void host.provider.bind().then(binding=> {
        if(!alive || current!==ticket || recovery.getSnapshot().stage!=='none') return;
        // A fresh, verified same-subject sign-in can resume a completed sign-out.
        const saved=new LearnerSignOut(binding,binding.storage(browserStorage));
        if(resume && saved.read()?.complete) saved.resume();
        if(!retain){setAttachDismissed(false);setAttachResult(null);setAttachFailed(false);}setAccount(binding);setLocal(false);finishSignIn();
      }).catch(error=> {if(alive&&current===ticket){
        setFailed(!(error instanceof Error && error.message==='Sign in before syncing'));
        if(retain)setLocal(true);
      }})
        .finally(()=> {if(alive&&current===ticket)setBusy(false);});
    }
    const {data}=host.client.auth.onAuthStateChange((event,session)=> {
      if(!alive) return;
      recovery.observe(event,session);
      if(recovery.getSnapshot().stage!=='none') {++ticket;host.provider.invalidate();setEmailChange(null);setAccount(null);setBusy(false);setPassword('');return;}
      if(event==='SIGNED_OUT') {++ticket;host.provider.invalidate();setEmailChange(null);setAccount(null);setBusy(false);setPassword('');finishSignIn();}
      else if(event==='INITIAL_SESSION'||event==='SIGNED_IN'||event==='USER_UPDATED'||event==='TOKEN_REFRESHED') {
        // SDK callbacks hold the auth lock; schedule provider reads after callback return.
        const retain=host.provider.observeAuthEvent(event,session);
        if(!retain){setEmailChange(null);setAccount(null);}
        queueMicrotask(()=> {if(alive)verify(event==='INITIAL_SESSION'&&!retain,event==='SIGNED_IN',retain);});
      }
    });
    return ()=> {alive=false;++ticket;data.subscription.unsubscribe();host.provider.invalidate();};
  },[host,retry,recovery]);
  // Retaining the account on cold local -> verified recovery must still notify
  // the learner to retry its initially blocked reads, without remounting it.
  const api=useMemo(()=>account ? host.provider.api(account,origin) : undefined,[host,account,origin,local]);
  const identityDeletion=useMemo(()=>account&&deletionEnabled?host.provider.identityDeletion(account,origin):undefined,[host,account,origin,deletionEnabled]);
  async function signIn() {
    if(busy||loginLock.current) return;loginLock.current=true;setBusy(true);setFailed(false);setOAuthFailed(false);
    try {
      setConfirmation(false);
      if(register) {
        const {data,error}=await host.client.auth.signUp({email,password,options:{emailRedirectTo:new URL('/',window.location.origin).href}});
        if(error) throw Error('Registration failed');
        if(!data.session) {setConfirmation(true);navigateLearner('sign-in',null,true);}
      } else {
        const {error}=await host.client.auth.signInWithPassword({email,password});if(error)throw Error('Sign-in failed');
      }
    }
    catch {setFailed(true);}
    finally {loginLock.current=false;setPassword('');setBusy(false);}
  }
  async function signInWithProvider(provider:'google'|'azure') {
    if(busy||loginLock.current)return;
    loginLock.current=true;setBusy(true);setFailed(false);setOAuthFailed(false);setConfirmation(false);setPassword('');
    try {
      const {error}=await host.client.auth.signInWithOAuth({provider,options:{
        redirectTo:new URL('/',window.location.origin).href,
        ...(provider==='azure'?{scopes:'email'}:{}),
      }});
      if(error)throw error;
    } catch {setFailed(true);}
    finally {loginLock.current=false;setBusy(false);}
  }
  async function attachGuest() {
    if(!api?.adoptGuestRound || !account || local || attachBusy) return;

    if(attachmentLock.current)return;attachmentLock.current=true;

    setAttachBusy(true);setAttachFailed(false);
    try {

      if(!navigator.locks)throw Error('Safe account saving requires a writer lock');
      await navigator.locks.request(account.ownerLock,async()=>{
        account.assertCurrent();
        const request=buildGuestRoundAdoptionRequest(account.identity.subject);if(!request)throw Error('Save consent unavailable');
        const storage=account.storage(browserStorage),prior=readJourney(storage).practice;
        if(prior && prior.session?.id!==request.requestId && !prior.completionReceipt && (!prior.session || prior.index<prior.session.questions.length))throw Error('An account round is unfinished');
        const saved=await api.adoptGuestRound!(request);
        account.assertCurrent();
        installAdoptedRound(storage,saved);
        clearGuestPractice();
        setAttachResult({saved:saved.confirmedCount,remaining:0,validationOnly:true});
      });

    } catch { setAttachFailed(true); } finally { attachmentLock.current=false;setAttachBusy(false); }

  }

  useEffect(()=>{if(account && api?.adoptGuestRound && !local && !showLogin && !attachResult && !attachFailed && hasGuestSaveIntent())void attachGuest();},[account,api,local,showLogin]);

  function continueSaved() {
    try {const binding=host.provider.localBinding();if(binding){setAccount(binding);setLocal(true);finishSignIn();}}
    catch {setFailed(true);}
  }
  function openAuth(mode:'sign-in'|'register') {
    setOAuthFailed(false);setConfirmation(false);setFailed(false);navigateLearner(mode);
  }
  function returnHome() {
    const leavingRecovery=recovery.getSnapshot().stage!=='none';

    recovery.finish();navigateLearner(hasGuestSaveIntent()?'practice':'home');

    setPassword('');setConfirmation(false);setFailed(false);setOAuthFailed(false);if(leavingRecovery)setRetry(v=>v+1);
  }
  if(recoveryState.stage!=='none') return <RecoveryPassword recovery={recovery} state={recoveryState} locale={locale} home={returnHome} finish={()=>{
    const completed=recovery.getSnapshot().stage==='complete';recovery.finish();navigateLearner(completed?'home':'reset',null,true);setFailed(false);setRetry(v=>v+1);
  }}/>;
  if(emailHelp) return <main className="gm-foundation gm-auth" data-theme={appearance.theme} lang={locale}><div className="gm-column"><RecoveryEmail auth={host.client.auth} locale={locale} initialEmail={email} mode={emailHelp} home={returnHome} backLabel={account&&!showLogin?c.backAccount:undefined} back={()=>backInLearner(account?'account':'sign-in')}/></div></main>;
  const pendingRound=hasGuestSaveIntent();
  const accountGuestAttempts=pendingRound?guestAttemptCount():0;
  if(route.view==='email-change'&&emailChange&&account===emailChange&&!local&&!showLogin) {
    const currentEmail=host.provider.emailFor(account);
    if(currentEmail)return <main className="gm-foundation gm-auth" data-theme={appearance.theme} lang={locale}><div className="gm-column"><AccountEmailChange auth={host.client.auth} account={account} currentEmail={currentEmail} authorize={()=>host.provider.assertVerified(account)} locale={locale} home={returnHome} back={()=>backInLearner('account')}/></div></main>;
  }
  if(account&&!showLogin&&!local&&!attachDismissed&&(pendingRound||attachResult)) {
    const remaining=attachResult?.remaining ?? accountGuestAttempts;
    return <main className="gm-foundation" data-theme={appearance.theme} lang={locale}><div className="gm-column"><PracticeCard>
      {attachResult ? <>
        <h1>{remaining===0?c.attachSavedTitle:c.attachPartialTitle}</h1>
        <p role="status">{remaining===0?(locale==='de'?'Deine Übung ist gespeichert. Du kannst sie fortsetzen.':'Your round is saved. You can continue practising.'):
          (attachResult.validationOnly?c.attachPartial(attachResult.saved,remaining):c.attachPartialOther(attachResult.saved,remaining))}</p>
        <FoundationButton onClick={()=>setAttachDismissed(true)}>{c.continue}</FoundationButton>
      </> : <>
        <h1>{locale==='de'?'Übung speichern':'Save your practice?'}</h1><p>{locale==='de'?'Deine gesamte Übung bleibt erhalten, auch unbeantwortete Aufgaben.':'Keep your whole round, including unanswered questions.'}</p>
        {attachFailed&&<p role="alert"><strong>{c.attachFailedTitle}</strong> {c.attachFailed}</p>}
        <FoundationButton disabled={attachBusy||!api?.adoptGuestRound} onClick={()=>void attachGuest()}>{locale==='de'?'Übung speichern':'Save practice'}</FoundationButton>
        <FoundationButton className="gm-secondary" disabled={attachBusy} onClick={()=>setAttachDismissed(true)}>{c.notNow}</FoundationButton>
      </>}
      <FoundationButton className="gm-secondary" disabled={attachBusy} onClick={()=>setAttachDismissed(true)}>{c.home}</FoundationButton>
    </PracticeCard></div></main>;
  }
  if(account&&!showLogin) return <LearnerJourney accountSecurity={(language,blocked)=>{
    const labels=copy[language], address=local?null:host.provider.emailFor(account);
    return <PracticeCard><h2>{labels.signInDetails}</h2>
      {pendingRound && <FoundationButton disabled={blocked} onClick={()=>setAttachDismissed(false)}>{language==='de'?'Übung speichern':'Save practice'}</FoundationButton>}
      {address ? <><p className="gm-account-email"><strong>{labels.email}</strong><br/>{address}</p>
        <details className="gm-account-disclosure"><summary>{language==='de'?'Anmeldung verwalten':'Manage sign-in'}</summary>
        <FoundationButton className="gm-secondary" disabled={blocked} onClick={()=>{setEmailChange(account);navigateLearner('email-change');}}>{labels.changeEmail}</FoundationButton>
        <p>{labels.resetHelp}</p>
        <FoundationButton className="gm-secondary" disabled={blocked} onClick={()=>{setEmail(address);navigateLearner('reset');}}>{labels.resetPassword}</FoundationButton></details></>
        : <p>{labels.emailUnavailable}</p>}
    </PracticeCard>;
  }} onReauthenticate={()=>openAuth('sign-in')} localAccess={local} account={account} api={api} revoke={()=>host.provider.revoke(account)} authorizeResume={()=>host.provider.assertVerified(account)} identityDeletion={identityDeletion} clearDeletedIdentity={identityDeletion?()=>host.provider.clearDeletedIdentity(account):undefined} forgetDeletedIdentity={identityDeletion?()=>host.provider.forgetDeletedIdentity(account):undefined} />;
  if(!showLogin) return <GuestStarterJourney onAuth={openAuth} hasSavedAccount={host.provider.hasSavedAccount()} onResumeSaved={continueSaved}/>;
  const guestAttempts=guestAttemptCount();
  return <main className="gm-foundation gm-auth" data-theme={appearance.theme} lang={locale}><div className="gm-auth-art"><a className="gm-brand-home" href="#/learn/home" aria-label={`German Master — ${c.home}`} onClick={event=>{event.preventDefault();if(!busy)returnHome();}} aria-disabled={busy}><strong>German Master.</strong></a><StudyArt/><p>{shellCopy[locale].learningNote}</p></div><div className="gm-column"><PracticeCard>
    <h1>{register?c.registerTitle:c.title}</h1><FoundationButton disabled={busy} onClick={()=>setLocale(locale==='en'?'de':'en')}>English / Deutsch</FoundationButton>
    {guestAttempts>0&&<p role="status">{c.guestPreserved(guestAttempts)}</p>}
    <FoundationButton className="gm-secondary" disabled={busy} onClick={returnHome}>{c.home}</FoundationButton>
    {host.provider.hasSavedAccount()&&<FoundationButton disabled={loginLock.current} onClick={continueSaved}>{c.resume}</FoundationButton>}
    {releaseCapabilities.socialSignIn && <div className="gm-answer-group">
      <FoundationButton className="gm-secondary gm-provider-button" disabled={busy} onClick={()=>void signInWithProvider('google')}><GoogleIcon className="gm-provider-icon" width="20" height="20"/>{c.google}</FoundationButton>
      <FoundationButton className="gm-secondary gm-provider-button" disabled={busy} onClick={()=>void signInWithProvider('azure')}><MicrosoftIcon className="gm-provider-icon" width="20" height="20"/>{c.microsoft}</FoundationButton>
    </div>}
    {releaseCapabilities.socialSignIn && <p>{c.emailAlternative}</p>}
    <form className="gm-answer-group" onSubmit={event=> {event.preventDefault();void signIn();}}>
      <label className="gm-field" htmlFor="v2-email">{c.email}<input id="v2-email" type="email" autoComplete="username" required value={email} disabled={busy} onChange={event=>setEmail(event.target.value)} /></label>
      <label className="gm-field" htmlFor="v2-password">{c.password}<input id="v2-password" type="password" autoComplete={register?'new-password':'current-password'} required minLength={register?8:undefined} value={password} disabled={busy} onChange={event=>setPassword(event.target.value)} /></label>
      <button className="gm-button" type="submit" disabled={busy}>{register?c.register:c.signIn}</button>
    </form>
    <FoundationButton disabled={busy} onClick={()=>{navigateLearner(register?'sign-in':'register');setPassword('');setConfirmation(false);setFailed(false);}}>{register?c.backToSignIn:c.createAccount}</FoundationButton>
    <FoundationButton disabled={busy} onClick={()=>{setPassword('');navigateLearner('reset');}}>{recoveryCopy[locale].forgot}</FoundationButton>
    <FoundationButton disabled={busy} onClick={()=>{setPassword('');navigateLearner('resend');}}>{recoveryCopy[locale].resend}</FoundationButton>
    {confirmation&&<p role="status">{c.confirmation}</p>}
    {busy&&<p role="status">{c.checking}</p>}{(failed||oauthFailed)&&<><p role="alert">{c.failed}</p><FoundationButton disabled={busy} onClick={()=>setRetry(v=>v+1)}>{c.retry}</FoundationButton></>}
  </PracticeCard></div></main>;
}

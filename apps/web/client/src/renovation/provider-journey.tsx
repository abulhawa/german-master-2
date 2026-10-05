import { useEffect, useMemo, useRef, useState } from 'react';
import LearnerJourney from './journey';
import type { AccountBinding } from './account';
import { createLearnerProvider } from './provider';
import { FoundationButton, PracticeCard } from '../foundation/preview';
import { LearnerSignOut } from './signout';
import { browserStorage } from './storage';

import { providerCopy as copy } from './provider-locales';

/** Explicitly configured host. The fixture preview and legacy release remain separate. */
export function ProviderLearnerJourney({host,origin}:{host:ReturnType<typeof createLearnerProvider>;origin:string}) {
  const [account,setAccount] = useState<AccountBinding|null>(null);
  const [busy,setBusy] = useState(true);
  const [failed,setFailed] = useState(false);
  const [retry,setRetry] = useState(0);
  const [showLogin,setShowLogin] = useState(false);
  const [local,setLocal] = useState(false);
  const loginLock=useRef(false);
  const [locale,setLocale] = useState<'en'|'de'>('en');
  const [email,setEmail] = useState(''); const [password,setPassword] = useState('');
  const c=copy[locale];
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
    try {const binding=host.provider.localBinding();if(binding){setAccount(binding);setLocal(true);}} catch {setFailed(true);}
    function verify(allowLocal=false, resume=false) {
      const current=++ticket;setAccount(null);setBusy(true);setFailed(false);
      if(allowLocal) {
        try {const saved=host.provider.localBinding();if(saved){setAccount(saved);setLocal(true);}} catch {setFailed(true);}
      }
      void host.provider.bind().then(binding=> {
        if(!alive || current!==ticket) return;
        // A fresh, verified same-subject sign-in can resume a completed sign-out.
        const saved=new LearnerSignOut(binding,binding.storage(browserStorage));
        if(resume && saved.read()?.complete) saved.resume();
        setAccount(binding);setLocal(false);setShowLogin(false);
      }).catch(error=> {if(alive&&current===ticket){
        setFailed(!(error instanceof Error && error.message==='Sign in before syncing'));
      }})
        .finally(()=> {if(alive&&current===ticket)setBusy(false);});
    }
    const {data}=host.client.auth.onAuthStateChange(event=> {
      if(!alive) return;
      if(event==='SIGNED_OUT') {++ticket;host.provider.invalidate();setAccount(null);setBusy(false);setPassword('');}
      else if(event==='INITIAL_SESSION'||event==='SIGNED_IN'||event==='USER_UPDATED'||event==='TOKEN_REFRESHED') {
        // SDK callbacks hold the auth lock; schedule provider reads after callback return.
        host.provider.invalidate();setAccount(null);queueMicrotask(()=> {if(alive)verify(event==='INITIAL_SESSION',event==='SIGNED_IN');});
      }
    });
    return ()=> {alive=false;++ticket;data.subscription.unsubscribe();host.provider.invalidate();};
  },[host,retry]);
  const api=useMemo(()=>account ? host.provider.api(account,origin) : undefined,[host,account,origin]);
  async function signIn() {
    if(busy||loginLock.current) return;loginLock.current=true;setBusy(true);setFailed(false);
    try {const {error}=await host.client.auth.signInWithPassword({email,password});if(error)throw Error('Sign-in failed');}
    catch {setFailed(true);}
    finally {loginLock.current=false;setPassword('');setBusy(false);}
  }
  function continueSaved() {
    try {const binding=host.provider.localBinding();if(binding){setAccount(binding);setLocal(true);setShowLogin(false);}}
    catch {setFailed(true);}
  }
  if(account&&!showLogin) return <><div className="gm-foundation" lang={locale}><div className="gm-column">
    {local&&<p role="status">{c.local}</p>}<FoundationButton onClick={()=>setShowLogin(true)}>{c.reauth}</FoundationButton>
  </div></div><LearnerJourney account={account} api={api} revoke={()=>host.provider.revoke(account)} authorizeResume={()=>host.provider.assertVerified(account)} /></>;
  return <main className="gm-foundation" lang={locale}><div className="gm-column"><PracticeCard>
    <h1>{c.title}</h1><FoundationButton disabled={busy} onClick={()=>setLocale(locale==='en'?'de':'en')}>English / Deutsch</FoundationButton>
    {host.provider.hasSavedAccount()&&<FoundationButton disabled={loginLock.current} onClick={continueSaved}>{c.resume}</FoundationButton>}
    <form className="gm-answer-group" onSubmit={event=> {event.preventDefault();void signIn();}}>
      <label className="gm-field" htmlFor="v2-email">{c.email}<input id="v2-email" type="email" autoComplete="username" required value={email} disabled={busy} onChange={event=>setEmail(event.target.value)} /></label>
      <label className="gm-field" htmlFor="v2-password">{c.password}<input id="v2-password" type="password" autoComplete="current-password" required value={password} disabled={busy} onChange={event=>setPassword(event.target.value)} /></label>
      <button className="gm-button" type="submit" disabled={busy}>{c.signIn}</button>
    </form>
    {busy&&<p role="status">{c.checking}</p>}{failed&&<><p role="alert">{c.failed}</p><FoundationButton disabled={busy} onClick={()=>setRetry(v=>v+1)}>{c.retry}</FoundationButton></>}
  </PracticeCard></div></main>;
}

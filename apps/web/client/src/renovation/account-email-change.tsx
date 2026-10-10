import { useEffect, useRef, useState } from 'react';
import type { SupabaseClient } from '@supabase/supabase-js';
import type { AccountBinding } from './account';
import { FoundationButton, PracticeCard } from '../foundation/controls';
import { providerCopy } from './provider-locales';

export function AccountEmailChange({auth,account,currentEmail,authorize,locale,back}: {
  auth: SupabaseClient['auth']; account: AccountBinding; currentEmail: string;
  authorize: () => void; locale:'en'|'de'; back:()=>void;
}) {
  const c=providerCopy[locale];
  const [email,setEmail]=useState(''),[busy,setBusy]=useState(false),[sent,setSent]=useState(false),[failed,setFailed]=useState(false),[cooldown,setCooldown]=useState(false);
  const lock=useRef(false),heading=useRef<HTMLHeadingElement>(null);
  useEffect(()=>{heading.current?.focus();},[]);
  useEffect(()=>{if(cooldown){const timer=setTimeout(()=>setCooldown(false),60_000);return()=>clearTimeout(timer);}},[cooldown]);
  const same=email.trim().toLowerCase()===currentEmail.toLowerCase();
  async function request() {
    if(lock.current||sent||cooldown||same)return;
    lock.current=true;setBusy(true);setFailed(false);
    try {
      authorize();const verified=await auth.getUser();authorize();
      if(verified.error||verified.data.user?.id!==account.identity.subject)throw Error('Account changed');
      const result=await auth.updateUser({email:email.trim()},{emailRedirectTo:new URL('/',window.location.origin).href});
      authorize();if(result.error)throw result.error;
      // Request acceptance is not email verification; keep the current address.
      setSent(true);setEmail('');
    } catch {setFailed(true);}
    finally {lock.current=false;setBusy(false);setCooldown(true);}
  }
  return <PracticeCard><h1 ref={heading} tabIndex={-1}>{c.changeEmail}</h1>
    <p className="gm-account-email"><strong>{c.email}</strong><br/>{currentEmail}</p><p>{c.emailChangeHelp}</p>
    <form className="gm-answer-group" onSubmit={event=>{event.preventDefault();void request();}}>
      <label className="gm-field" htmlFor="new-account-email">{c.newEmail}<input id="new-account-email" type="email" autoComplete="email" required disabled={busy||sent} value={email} onChange={event=>setEmail(event.target.value)}/></label>
      <button className="gm-button" type="submit" disabled={busy||sent||cooldown||same}>{c.requestEmailChange}</button>
    </form>
    {sent&&<p role="status">{c.verifyEmailChange}</p>}{failed&&<p role="alert">{c.emailChangeFailed}</p>}
    <FoundationButton disabled={busy} onClick={back}>{c.backAccount}</FoundationButton>
  </PracticeCard>;
}

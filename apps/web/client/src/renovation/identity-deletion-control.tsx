import {useEffect,useRef,useState} from 'react';
import type {IdentityDeletionProof} from '@german-master/contracts';
import {FoundationButton,PracticeCard} from '../foundation/preview';
import {identityDeletionCopy} from './identity-deletion-locales';
import type {IdentityDeletionMarker} from './identity-deletion';
export function IdentityDeletionControl({locale,marker,blocked,deliver}:{locale:'en'|'de';marker:IdentityDeletionMarker|null;blocked:boolean;deliver:(proof?:IdentityDeletionProof)=>Promise<void>}) {
  const c=identityDeletionCopy[locale];const lock=useRef(false);
  const [confirm,setConfirm]=useState(false),[busy,setBusy]=useState(false),[failed,setFailed]=useState(false);
  const [email,setEmail]=useState(''),[password,setPassword]=useState('');
  const heading=useRef<HTMLHeadingElement>(null),begin=useRef<HTMLButtonElement>(null),prior=useRef(false);
  const terminal=!!marker;
  useEffect(()=>{if(confirm||terminal)heading.current?.focus();else if(prior.current)begin.current?.focus();prior.current=confirm;},[confirm,terminal]);
  async function run(proof?:IdentityDeletionProof) {
    if(lock.current||blocked)return;lock.current=true;setBusy(true);setFailed(false);setPassword('');
    try {await deliver(proof);setConfirm(false);}catch{setFailed(true);}finally{lock.current=false;setBusy(false);}
  }
  return <PracticeCard><h2 ref={heading} tabIndex={-1}>{c.title}</h2><p>{marker?.complete?c.completed:marker?c.pending:c.help}</p>
    {marker&&<FoundationButton disabled={busy||blocked} onClick={()=>void run()}>{marker.receipt?.status==='identity_deleted'?c.cleanup:c.recover}</FoundationButton>}
    {!marker?.complete&&marker?.receipt?.status!=='identity_deleted'&&<>
      {!confirm?<button ref={begin} className="gm-button" disabled={busy||blocked} onClick={()=>setConfirm(true)}>{marker?c.retry:c.begin}</button>:<form className="gm-answer-group" onSubmit={event=>{event.preventDefault();void run({email,password});}}>
        <p>{c.confirm}</p><label className="gm-field">{c.email}<input type="email" autoComplete="username" maxLength={320} required value={email} disabled={busy} onChange={e=>setEmail(e.target.value)}/></label>
        <label className="gm-field">{c.password}<input type="password" autoComplete="current-password" maxLength={4096} required value={password} disabled={busy} onChange={e=>setPassword(e.target.value)}/></label>
        <button className="gm-button" type="submit" disabled={busy||blocked}>{c.submit}</button><FoundationButton disabled={busy} onClick={()=>{setPassword('');setConfirm(false);}}>{c.cancel}</FoundationButton>
      </form>}
    </>}{failed&&<p role="alert">{c.error}</p>
  }</PracticeCard>;
}

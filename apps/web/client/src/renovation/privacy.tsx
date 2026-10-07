import { useState, useRef, useEffect } from 'react';
import type { LearnerExport } from '@german-master/contracts';
import { FoundationButton, PracticeCard } from "../foundation/controls";
import { privacyCopy } from './privacy-locales';
import { deletionCopy, signoutCopy, accountSignoutCopy } from './privacy-locales';

function usePrivacyFocus(confirm:boolean,terminal:boolean) {
  const heading=useRef<HTMLHeadingElement>(null);const begin=useRef<HTMLButtonElement>(null);const prior=useRef(false);
  useEffect(()=> {
    if(confirm||terminal) heading.current?.focus();else if(prior.current) begin.current?.focus();
    prior.current=confirm;
  },[confirm,terminal]);
  return {heading,begin};
}

export function PrivacySignOut({locale,blocked,signedOut,complete,leave,resume,authenticated=false}:{locale:'en'|'de';blocked:boolean;signedOut:boolean;complete:boolean;leave:(remove:boolean)=>Promise<void>;resume:()=>void;authenticated?:boolean}) {
  const c=(authenticated?accountSignoutCopy:signoutCopy)[locale];const locked=useRef(false);
  const [confirm,setConfirm]=useState(false);const [busy,setBusy]=useState(false);const [error,setError]=useState(false);
  const {heading,begin}=usePrivacyFocus(confirm,signedOut);
  async function run(remove:boolean) {
    if(locked.current||blocked) return;locked.current=true;setBusy(true);setError(false);
    try {await leave(remove);} catch {setError(true);} finally {locked.current=false;setBusy(false);}
  }
  return <PracticeCard><h2 ref={heading} tabIndex={-1}>{c.title}</h2><p>{signedOut?c.signedOut:c.help}</p>
    {signedOut ? complete ? <FoundationButton disabled={busy} onClick={()=>{try {resume();} catch {setError(true);}}}>{c.resume}</FoundationButton> : <FoundationButton disabled={busy} onClick={()=>void run(true)}>{c.retry}</FoundationButton> : <>
      <FoundationButton disabled={busy||blocked} onClick={()=>void run(false)}>{c.sync}</FoundationButton>
      {!confirm ? <button ref={begin} className="gm-button" disabled={busy||blocked} onClick={()=>setConfirm(true)}>{c.remove}</button> : <>
        <p>{c.confirm}</p><FoundationButton disabled={busy||blocked} onClick={()=>void run(true)}>{c.confirmRemove}</FoundationButton>
        <FoundationButton disabled={busy} onClick={()=>setConfirm(false)}>{c.cancel}</FoundationButton>
      </>}
    </>}
    {error&&<p role="alert">{c.error}</p>}
  </PracticeCard>;
}

export function PrivacyDeletion({locale,pending,confirmed,complete=false,blocked,remove}:{locale:'en'|'de';pending:boolean;confirmed:boolean;complete?:boolean;blocked:boolean;remove:()=>Promise<void>}) {
  const c=deletionCopy[locale];
  const [confirm,setConfirm]=useState(false); const [busy,setBusy]=useState(false); const [error,setError]=useState(false);
  const {heading,begin}=usePrivacyFocus(confirm,pending||blocked);
  const locked=useRef(false);
  async function run() {
    if(locked.current||blocked) return;
    locked.current=true;setBusy(true);setError(false);
    try { await remove(); } catch { setError(true); }
    finally { locked.current=false;setBusy(false); }
  }
  return <PracticeCard><h2 ref={heading} tabIndex={-1}>{c.title}</h2><p>{complete?c.completed:confirmed?c.confirmed:pending?c.pending:c.help}</p>
    {blocked&&<p role="alert">{c.damaged}</p>}
    {pending ? !complete && <FoundationButton disabled={busy||blocked} onClick={()=>void run()}>{confirmed?c.cleanup:c.retry}</FoundationButton> : <>
      {!confirm ? <button ref={begin} className="gm-button" disabled={busy||blocked} onClick={()=>setConfirm(true)}>{c.begin}</button> : <>
        <p>{c.confirm}</p><FoundationButton disabled={busy||blocked} onClick={()=>void run()}>{c.delete}</FoundationButton>
        <FoundationButton disabled={busy} onClick={()=>setConfirm(false)}>{c.cancel}</FoundationButton>
      </>}
    </>}
    {error&&<p role="alert">{c.error}</p>}
  </PracticeCard>;
}

export function downloadLearnerExport(data: LearnerExport) {
  const url = URL.createObjectURL(new Blob([JSON.stringify(data,null,2)+'\n'],{type:'application/json'}));
  const link = document.createElement('a'); link.href=url; link.download='german-master-learner-export.json';
  document.body.appendChild(link);
  try { link.click(); } finally { link.remove(); setTimeout(()=>URL.revokeObjectURL(url),1000); }
}

export function PrivacyExport({locale,blocked,exportData}:{locale:'en'|'de';blocked:boolean;exportData:(sync:boolean)=>Promise<LearnerExport>}) {
  const c=privacyCopy[locale]; const locked=useRef(false);
  const [busy,setBusy]=useState(false); const [status,setStatus]=useState<'saved'|'error'|null>(null);
  async function run(sync:boolean) {
    if(locked.current || blocked) return;
    locked.current=true;setBusy(true);setStatus(null);
    try { downloadLearnerExport(await exportData(sync));setStatus('saved'); }
    catch { setStatus('error'); }
    finally { locked.current=false;setBusy(false); }
  }
  return <PracticeCard><h2>{c.title}</h2><p>{c.help}</p>
    <FoundationButton disabled={blocked||busy} onClick={()=>void run(false)}>{c.export}</FoundationButton>
    <FoundationButton disabled={blocked||busy} onClick={()=>void run(true)}>{c.syncExport}</FoundationButton>
    {status&&<p role={status==='error'?'alert':'status'}>{c[status]}</p>}
  </PracticeCard>;
}

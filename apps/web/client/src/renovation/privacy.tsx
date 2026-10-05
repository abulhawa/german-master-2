import { useState, useRef } from 'react';
import type { LearnerExport } from '@german-master/contracts';
import { FoundationButton, PracticeCard } from '../foundation/preview';
import { privacyCopy } from './privacy-locales';

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

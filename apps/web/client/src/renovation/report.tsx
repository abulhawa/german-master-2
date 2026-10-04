import { useRef, useState } from 'react';
import { ContentReportRequestSchema, ContentReportReceiptSchema, type ContentReportRequest, type Session } from '@german-master/contracts';
import { FoundationButton } from '../foundation/preview';
import type { LearnerApi } from './api';
import type { JourneyStorage } from './storage';
import { reportCopy } from './report-locales';

export const REPORT_KEY = 'german-master-v2-fixture-content-report';
type Record = {request: ContentReportRequest; recorded: boolean};
export function ContentReport({question,locale,storage,send}: {question?: Session['questions'][number];locale:'en'|'de';storage:JourneyStorage;send: LearnerApi['report']}) {
  const [loaded] = useState(() => {
    try { const raw=storage.getItem(REPORT_KEY); if (!raw) return {record:null,failed:false};
      const parsed=JSON.parse(raw); if (parsed.version !== 1 || typeof parsed.recorded !== 'boolean') throw Error();
      return {record:{request:ContentReportRequestSchema.parse(parsed.request),recorded:parsed.recorded} as Record,failed:false};
    } catch { return {record:null,failed:true}; }
  });
  const [record,setRecord]=useState(loaded.record);
  const [failed,setFailed]=useState(loaded.failed);
  const [busy,setBusy]=useState(false);
  const lock=useRef(false);
  const [category,setCategory]=useState<ContentReportRequest['category']>('incorrect_answer');
  const c=reportCopy[locale];
  const pending=record && !record.recorded;
  const recorded=record?.recorded && record.request.sessionQuestionId === question?.id;
  function save(value:Record) { storage.setItem(REPORT_KEY,JSON.stringify({version:1,...value})); setRecord(value); }
  async function submit() {
    if (lock.current || !send || loaded.failed) return;
    lock.current=true; setBusy(true); setFailed(false);
    try {
      const request=pending ? record.request : question ? {apiVersion:'v2' as const,reportId:crypto.randomUUID(),sessionQuestionId:question.id,exerciseRevision:question.exercise.revision,category} : null;
      if (!request) return;
      save({request,recorded:false});
      const receipt=ContentReportReceiptSchema.parse(await send(request));
      if (receipt.reportId !== request.reportId) throw Error('Report receipt mismatch');
      save({request,recorded:true});
    } catch { setFailed(true); } finally {lock.current=false;setBusy(false);}
  }
  if (!question && !pending && !failed) return null;
  return <details open={!!pending || failed}><summary>{c.title}</summary>
    {failed && <p role="alert">{c.error}</p>}
    {recorded ? <p role="status">{c.recorded}</p> : <>
      {pending ? <p role="status">{c.pending}</p> : <label>{c.category}<select value={category} disabled={busy || loaded.failed} onChange={e=>setCategory(e.target.value as ContentReportRequest['category'])}>
        {(['incorrect_answer','ambiguous_prompt','other'] as const).map(value=><option key={value} value={value}>{c[value]}</option>)}
      </select></label>}
      <FoundationButton disabled={busy || !send || loaded.failed} onClick={()=>void submit()}>{pending ? c.retry : c.send}</FoundationButton>
    </>}
  </details>;
}

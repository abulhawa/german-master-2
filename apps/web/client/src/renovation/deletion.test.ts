import { afterEach, expect, it, vi } from 'vitest';
import { randomUUID } from 'node:crypto';
import { AccountBinding, type LearnerIdentity } from './account';
import { LearnerDeletion, deletionGuard, DELETION_KEY } from './deletion';
import type { LearnerApi } from './api';
import { localLearnerApi } from './api';

afterEach(()=> { localStorage.clear(); vi.unstubAllGlobals(); });
function fixture() {
  let current: LearnerIdentity | null = {subject:randomUUID(),generation:0};
  const account = new AccountBinding(current,()=>current);
  const storage = account.storage(localStorage);
  return {account,storage,deletion:new LearnerDeletion(account,storage),switch:()=>{current=null;}};
}
it('freezes before delivery, blocks all normal requests, preserves work after loss and replays after restart',async()=> {
  const f=fixture();f.storage.setItem('work','draft and pending answers');
  const requests: unknown[]=[];let lost=true;
  const api={deleteLearner:vi.fn(async request=> {
    expect(f.deletion.read()?.request).toEqual(request); requests.push(request);
    if(lost) throw Error('response lost');
    return {apiVersion:'v2',requestId:request.requestId,subject:f.account.identity.subject,status:'deleted',deletedAt:'2026-10-05T12:00:00Z'};
  }),submit:vi.fn(),profile:vi.fn()} as unknown as LearnerApi;
  const cleanup=vi.fn(async()=> {f.storage.setItem('work','');});
  await expect(f.deletion.deliver(api,cleanup)).rejects.toThrow('lost');
  expect(cleanup).not.toHaveBeenCalled();expect(f.storage.getItem('work')).toContain('draft');
  const guarded=deletionGuard(api,f.deletion);
  await expect(guarded.profile()).rejects.toThrow('deletion');await expect(guarded.submit({} as never)).rejects.toThrow('deletion');
  expect(api.submit).not.toHaveBeenCalled();expect(api.profile).not.toHaveBeenCalled();
  lost=false;const reopened=new LearnerDeletion(f.account,f.storage);await reopened.deliver(api,cleanup);
  expect(requests[0]).toEqual(requests[1]);expect(reopened.read()?.receipt?.status).toBe('deleted');
  expect(f.storage.getItem('work')).toBe('');await reopened.deliver(api,cleanup);expect(requests).toHaveLength(2);
  expect(()=>reopened.assertActive()).toThrow('deletion');
});
it('does not deliver before durable freeze and retries identical bytes after receipt save or cleanup failure',async()=> {
  const f=fixture();let fail=true;
  const storage={getItem:f.storage.getItem,setItem:(k:string,v:string)=>{if(fail) throw Error('disk');f.storage.setItem(k,v);}};
  const deletion=new LearnerDeletion(f.account,storage);
  const api={deleteLearner:vi.fn(async request=> {fail=true;return {apiVersion:'v2',requestId:request.requestId,subject:f.account.identity.subject,status:'deleted',deletedAt:'2026-10-05T12:00:00Z'};})} as unknown as LearnerApi;
  const cleanup=vi.fn(async()=>{});
  await expect(deletion.deliver(api,cleanup)).rejects.toThrow('disk');expect(api.deleteLearner).not.toHaveBeenCalled();
  fail=false;await expect(deletion.deliver(api,cleanup)).rejects.toThrow('disk');expect(cleanup).not.toHaveBeenCalled();
  const frozen=deletion.read()?.request;fail=false;
  api.deleteLearner=vi.fn(async request=>({apiVersion:'v2',requestId:request.requestId,subject:f.account.identity.subject,status:'deleted',deletedAt:'2026-10-05T12:00:00Z'}));
  await expect(deletion.deliver(api,async()=>{throw Error('cleanup');})).rejects.toThrow('cleanup');
  expect(api.deleteLearner).toHaveBeenCalledWith(frozen);await deletion.deliver(api,cleanup);expect(api.deleteLearner).toHaveBeenCalledTimes(1);
});
it('rejects foreign/stale receipts and corrupt records without removing saved work',async()=> {
  const f=fixture();const cleanup=vi.fn(async()=>{});
  const api={deleteLearner:vi.fn(async request=>({apiVersion:'v2',requestId:request.requestId,subject:randomUUID(),status:'deleted',deletedAt:'2026-10-05T12:00:00Z'}))} as unknown as LearnerApi;
  await expect(f.deletion.deliver(api,cleanup)).rejects.toThrow('mismatch');expect(cleanup).not.toHaveBeenCalled();
  f.switch();await expect(f.deletion.deliver(api,cleanup)).rejects.toThrow('Sign in');expect(api.deleteLearner).toHaveBeenCalledTimes(1);
  f.storage.setItem(DELETION_KEY,'broken');expect(()=>f.deletion.assertActive()).toThrow();
});
it('sends DELETE with subject and frozen body and validates the linked receipt',async()=> {
  const f=fixture(); const request={apiVersion:'v2' as const,requestId:randomUUID(),confirmation:'delete_owned_data' as const};
  vi.stubGlobal('fetch',vi.fn(async (_url,init)=> {
    expect(init.method).toBe('DELETE');expect(init.body).toBe(JSON.stringify(request));expect(init.headers['X-Learner-Subject']).toBe(f.account.identity.subject);
    return {ok:true,json:async()=>({apiVersion:'v2',requestId:request.requestId,subject:f.account.identity.subject,status:'deleted',deletedAt:'2026-10-05T12:00:00Z'})};
  }));
  expect((await localLearnerApi(f.account).deleteLearner!(request)).status).toBe('deleted');
});

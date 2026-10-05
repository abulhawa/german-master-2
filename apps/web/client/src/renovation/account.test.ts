import { afterEach, expect, it, vi } from 'vitest';
import { randomUUID } from 'node:crypto';
import { AccountBinding, FIXTURE_SUBJECT, type LearnerIdentity } from './account';
import { emptyJourney, readJourney, saveJourney } from './storage';
import { PROFILE_PENDING_KEY } from './setup';
import { REPORT_KEY } from './report';
import { localLearnerApi } from './api';

afterEach(() => { localStorage.clear(); vi.unstubAllGlobals(); });
it('partitions every saved write kind and owner lock by subject while retaining the primary fixture', async () => {
  let current: LearnerIdentity | null = {subject:randomUUID(),generation:0};
  const a = new AccountBinding(current, () => current);
  const b = new AccountBinding({subject:randomUUID(),generation:1}, () => current);
  const sa = a.storage(localStorage); const sb = b.storage(localStorage);
  const journey = {...emptyJourney(),locale:'de' as const}; saveJourney(sa,journey);
  sa.setItem(PROFILE_PENDING_KEY,'frozen preferences'); sa.setItem(REPORT_KEY,'frozen report');
  expect(readJourney(sb).practice).toBeNull(); expect(readJourney(sb).locale).toBe('en');
  expect(sb.getItem(PROFILE_PENDING_KEY)).toBeNull(); expect(sb.getItem(REPORT_KEY)).toBeNull();
  expect(a.ownerLock).not.toBe(b.ownerLock);
  const da = a.reserve(); const db = b.reserve();
  try { expect(da.name).not.toBe(db.name); await da.table('records').put({id:'marker',value:'A'});
    expect(await db.table('records').get('marker')).toBeUndefined();
  } finally { await da.delete(); await db.delete(); }
  current = null; expect(() => a.assertCurrent()).toThrow('Sign in');
  expect(readJourney(sa)).toEqual(journey); // Local work remains available after expiry.
  current = {subject:a.identity.subject,generation:2}; expect(() => a.assertCurrent()).toThrow('Sign in');
  new AccountBinding(current, () => current).assertCurrent();
  const primary = new AccountBinding({subject:FIXTURE_SUBJECT,generation:0}, () => ({subject:FIXTURE_SUBJECT,generation:0}));
  primary.storage(localStorage).setItem(PROFILE_PENDING_KEY,'primary'); expect(localStorage.getItem(PROFILE_PENDING_KEY)).toBe('primary');
});
it('rejects a stale response when identity changes while its body is being read', async () => {
  let current: LearnerIdentity | null = {subject:randomUUID(),generation:0};
  const account = new AccountBinding(current, () => current); let complete!: (value: unknown) => void;
  vi.stubGlobal('fetch', vi.fn(async (_input, init) => {
    expect(init.headers['X-Learner-Subject']).toBe(account.identity.subject);
    return {ok:true,json:() => new Promise(resolve => {complete = resolve;})};
  }));
  const api = localLearnerApi(account); const response = api.profile();
  await vi.waitFor(() => expect(complete).toBeTypeOf('function'));
  current = null; complete({apiVersion:'v2',revision:0,setupCompleted:false,preferences:{locale:'en',timezone:'UTC',level:'B1',sessionQuestionCount:5}});
  await expect(response).rejects.toThrow('Sign in');
  await expect(api.createSession({apiVersion:'v2',requestId:randomUUID(),questionCount:1,capabilities:['short_answer@1']})).rejects.toThrow('Sign in');
  expect(fetch).toHaveBeenCalledTimes(1);
});

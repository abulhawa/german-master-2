import {afterEach,expect,it,vi} from 'vitest';
import {randomUUID} from 'node:crypto';
import {AccountBinding} from './account';
import {LearnerSignOut} from './signout';

afterEach(()=>localStorage.clear());
it('failed sync preserves access; explicit successful sync retains drafts under the same subject across restart',async()=> {
  const identity={subject:randomUUID(),generation:0};const account=new AccountBinding(identity,()=>identity);
  const storage=account.storage(localStorage);storage.setItem('draft','unsubmitted');
  const signout=new LearnerSignOut(account,storage);const cleanup=vi.fn(async()=>{});const frozen=vi.fn();
  await expect(signout.finish(false,async()=>{throw Error('offline');},cleanup,frozen)).rejects.toThrow('offline');
  signout.assertActive();expect(storage.getItem('draft')).toBe('unsubmitted');expect(cleanup).not.toHaveBeenCalled();
  const sync=vi.fn(async()=>{});await signout.finish(false,sync,cleanup,frozen);
  expect(sync).toHaveBeenCalledOnce();expect(cleanup).not.toHaveBeenCalled();expect(()=>signout.assertActive()).toThrow('signed out');
  const reopened=new LearnerSignOut(account,storage);expect(reopened.read()?.complete).toBe(true);reopened.resume();reopened.assertActive();expect(storage.getItem('draft')).toBe('unsubmitted');
});
it('freezes local removal before cleanup and resumes it without syncing or accessing another subject',async()=> {
  const identity={subject:randomUUID(),generation:0};const account=new AccountBinding(identity,()=>identity);
  const storage=account.storage(localStorage);storage.setItem('draft','A');
  const foreign=new AccountBinding({subject:randomUUID(),generation:0},()=>identity).storage(localStorage);foreign.setItem('draft','B');
  const signout=new LearnerSignOut(account,storage);const sync=vi.fn(async()=>{});
  await expect(signout.finish(true,sync,async()=>{expect(signout.read()?.complete).toBe(false);throw Error('disk');},()=>{})).rejects.toThrow('disk');
  expect(sync).not.toHaveBeenCalled();expect(()=>signout.resume()).toThrow('Finish');
  await new LearnerSignOut(account,storage).finish(false,sync,async()=>{storage.setItem('draft','');},()=>{});
  expect(sync).not.toHaveBeenCalled();expect(storage.getItem('draft')).toBe('');expect(foreign.getItem('draft')).toBe('B');
});

import { afterEach, expect, it, vi } from 'vitest';
import type { Session, SupabaseClient } from '@supabase/supabase-js';
import { AccountRecovery } from './account-recovery';

const subject='00000000-0000-4000-8000-000000000020';
const other='00000000-0000-4000-8000-000000000021';
const session=(id:string)=>({user:{id}} as Session);
function fixture() {
  window.history.replaceState(null,'','/?auth=recovery');
  const auth={
    getUser:vi.fn(async()=>({data:{user:{id:subject}},error:null})),
    updateUser:vi.fn(async()=>({error:null})),
  };
  return {auth,recovery:new AccountRecovery(auth as unknown as SupabaseClient['auth'])};
}
afterEach(()=>{window.history.replaceState(null,'','/');vi.restoreAllMocks();});
it('keeps a recovery callback pending through INITIAL_SESSION and binds only its recovery subject',async()=>{
  const {auth,recovery}=fixture();
  recovery.observe('INITIAL_SESSION',session(other));
  expect(recovery.getSnapshot()).toEqual({stage:'waiting',subject:null});
  recovery.observe('PASSWORD_RECOVERY',session(subject));
  expect(recovery.getSnapshot()).toEqual({stage:'ready',subject});
  await recovery.update('synthetic-password');
  expect(auth.updateUser).toHaveBeenCalledWith({password:'synthetic-password'});
  expect(recovery.getSnapshot().stage).toBe('complete');
  recovery.finish();
  expect(window.location.search).toBe('');
});
it('marks unsuccessful exchange invalid without authorizing an existing account',()=>{
  const {recovery}=fixture();
  recovery.observe('INITIAL_SESSION',session(other));
  recovery.initializationFinished();
  expect(recovery.getSnapshot()).toEqual({stage:'invalid',subject:null});
});
it('blocks password changes when server-verified identity differs from recovered identity',async()=>{
  const {auth,recovery}=fixture();
  recovery.observe('PASSWORD_RECOVERY',session(subject));
  auth.getUser.mockResolvedValueOnce({data:{user:{id:other}},error:null});
  await expect(recovery.update('synthetic-password')).rejects.toThrow('recovery_expired');
  expect(auth.updateUser).not.toHaveBeenCalled();
  expect(recovery.getSnapshot().stage).toBe('invalid');
});
it('rejects the completed operation when identity changes during the update',async()=>{
  const {auth,recovery}=fixture();
  recovery.observe('PASSWORD_RECOVERY',session(subject));
  let resolve!:(value:{error:null})=>void;
  auth.updateUser.mockImplementationOnce(()=>new Promise(done=>{resolve=done;}));
  const saving=recovery.update('synthetic-password');
  await vi.waitFor(()=>expect(resolve).toBeTypeOf('function'));
  recovery.observe('SIGNED_IN',session(other));
  resolve({error:null});
  await expect(saving).rejects.toThrow('recovery_expired');
  expect(recovery.getSnapshot().stage).toBe('invalid');
});

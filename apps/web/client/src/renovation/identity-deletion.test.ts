import {it,expect,vi} from 'vitest';
import {AccountBinding} from './account';
import {LearnerIdentityDeletion,IDENTITY_DELETION_KEY} from './identity-deletion';
const subject='00000000-0000-4000-8000-000000000020';
function fixture() {
 const values=new Map<string,string>();let active=true;
 const account=new AccountBinding({subject,generation:1},()=>active?{subject,generation:1}:null);
 const storage={getItem:(key:string)=>values.get(key)??null,setItem:(key:string,value:string)=>{values.set(key,value);}};
 return {account,storage,values,switch:()=>{active=false;},marker:()=>new LearnerIdentityDeletion(account,storage)};
}
it('freezes before HTTP, preserves work on loss, and recovers without a surviving binding across restart and cleanup failure',async()=>{
 const f=fixture();f.storage.setItem('draft','saved');let request:any;
 const begin=vi.fn(async(value:any)=>{request=value;expect(f.marker().read()?.request).toEqual(value);throw Error('response lost');});
 const recover=vi.fn(async(value:any)=>({apiVersion:'v2' as const,requestId:value.requestId,status:'identity_deleted' as const,completedAt:'2026-10-05T20:00:00Z'}));
 const cleanup=vi.fn(async()=>{throw Error('local write failed');});
 await expect(f.marker().deliver({begin,recover},cleanup,{email:'learner@example.com',password:'transient'})).rejects.toThrow('lost');
 expect(request.recoveryCapability).toHaveLength(43);expect(f.values.get(IDENTITY_DELETION_KEY)).not.toContain('transient');
 expect(()=>f.marker().assertActive()).toThrow();expect(cleanup).not.toHaveBeenCalled();f.switch();
 await expect(f.marker().deliver({begin,recover},cleanup)).rejects.toThrow('local write');
 expect(f.marker().read()?.receipt?.status).toBe('identity_deleted');expect(f.storage.getItem('draft')).toBe('saved');
 const finish=vi.fn(async()=>{f.storage.setItem('draft','');});
 await f.marker().deliver({begin,recover},finish);
 expect(f.marker().read()?.complete).toBe(true);expect(begin).toHaveBeenCalledTimes(1);expect(recover).toHaveBeenCalledTimes(1);
 expect(()=>f.marker().assertActive()).toThrow();
});
it('keeps pending recovery read-only and rejects foreign receipts and failed marker saves before delivery',async()=>{
 const f=fixture();const begin=vi.fn(async(value:any)=>({apiVersion:'v2' as const,requestId:value.requestId,status:'pending' as const}));
 const recover=vi.fn(begin),cleanup=vi.fn();await f.marker().deliver({begin,recover},cleanup,{email:'a@b.co',password:'fresh'});
 await f.marker().deliver({begin,recover},cleanup);expect(cleanup).not.toHaveBeenCalled();expect(begin).toHaveBeenCalledTimes(2);
 await expect(f.marker().deliver({begin,recover:async()=>({apiVersion:'v2',requestId:subject,status:'identity_deleted',completedAt:'2026-10-05T20:00:00Z'})},cleanup)).rejects.toThrow('mismatch');
 const broken=new LearnerIdentityDeletion(f.account,{getItem:()=>null,setItem:()=>{throw Error('full');}});
 await expect(broken.deliver({begin,recover},cleanup,{email:'a@b.co',password:'fresh'})).rejects.toThrow('full');expect(begin).toHaveBeenCalledTimes(2);
});

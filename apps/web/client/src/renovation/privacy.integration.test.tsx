import {expect,it,vi} from 'vitest';
import {PGlite} from '@electric-sql/pglite';
import {randomUUID} from 'node:crypto';
import type {AddressInfo} from 'node:net';
import {FoundationStore} from '../../../../../services/api/src/store';
import {createApi} from '../../../../../services/api/src/server';
import {AccountBinding} from './account';
import {LearnerDeletion,deletionGuard} from './deletion';
import {localLearnerApi} from './api';

it('actual owned DELETE replays identical bytes after lost response and keeps the foreign learner intact',async()=> {
  const pg=new PGlite();const store=new FoundationStore(pg);await store.initialize();
  const a={subject:randomUUID(),generation:0};const b={subject:randomUUID(),generation:1};let active=a;
  const server=createApi(store,async()=>active.subject);await new Promise<void>(resolve=>server.listen(0,'127.0.0.1',resolve));
  const base=`http://127.0.0.1:${(server.address() as AddressInfo).port}`;const realFetch=fetch;let lose=true;const bytes:string[]=[];
  vi.stubGlobal('fetch',async(input:string,init?:RequestInit)=> {
    if(init?.method==='DELETE') bytes.push(init.body as string);
    const response=await realFetch(new URL(input,base),init);
    if(init?.method==='DELETE' && lose) {lose=false;throw Error('accepted response lost');}return response;
  });
  const map=new Map<string,string>();const baseStorage={getItem:(k:string)=>map.get(k)??null,setItem:(k:string,v:string)=>{map.set(k,v);}};
  const accountA=new AccountBinding(a,()=>active);const accountB=new AccountBinding(b,()=>active);
  const storage=accountA.storage(baseStorage);const api=localLearnerApi(accountA);const foreign=localLearnerApi(accountB);
  const preferences={locale:'en' as const,timezone:'UTC',level:'B1' as const,sessionQuestionCount:5 as const};
  try {
    await api.saveProfile({apiVersion:'v2',requestId:randomUUID(),expectedRevision:0,preferences});
    active=b;await foreign.saveProfile({apiVersion:'v2',requestId:randomUUID(),expectedRevision:0,preferences});const before=await foreign.profile();
    active=a;storage.setItem('draft','preserved until receipt');const deletion=new LearnerDeletion(accountA,storage);
    const cleanup=vi.fn(async()=>{storage.setItem('draft','');});
    await expect(deletion.deliver(api,cleanup)).rejects.toThrow('lost');expect(cleanup).not.toHaveBeenCalled();
    expect(storage.getItem('draft')).toContain('preserved');await expect(deletionGuard(api,deletion).saveProfile({} as never)).rejects.toThrow('deletion');
    const reopened=new LearnerDeletion(accountA,storage);await reopened.deliver(api,cleanup);expect(bytes).toHaveLength(2);expect(bytes[0]).toBe(bytes[1]);
    expect(reopened.read()?.receipt?.status).toBe('deleted');expect(storage.getItem('draft')).toBe('');
    await expect(api.profile()).rejects.toThrow();active=b;expect(await foreign.profile()).toEqual(before);
  } finally {vi.unstubAllGlobals();await new Promise<void>(resolve=>server.close(()=>resolve()));await pg.close();}
});

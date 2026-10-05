import {it,expect,vi} from 'vitest';
import {PGlite} from '@electric-sql/pglite';
import {readFile} from 'node:fs/promises';
import {randomUUID} from 'node:crypto';
import {FoundationStore} from '../../../../../services/api/src/store';
import {createApi} from '../../../../../services/api/src/server';
import {IdentityDeletionService} from '../../../../../services/api/src/identity-deletion';
import {identityDeletionHttp,serializedDeletionWorker} from '../../../../../services/api/src/identity-deletion-http';
import {LearnerIdentityDeletion} from './identity-deletion';
import {VerifiedLearnerProvider} from './provider';
import type {SupabaseClient} from '@supabase/supabase-js';

it('client recovers admitted HTTP deletion after lost response and restart without any provider reads',async()=>{
 const db=new PGlite(),store=new FoundationStore(db);await store.initialize();
 await db.exec(await readFile(new URL('../../../../../db/baseline/identity-deletion.sql',import.meta.url),'utf8'));
 const subject=randomUUID(),project='zgmyrpzwgtydwlzponih';let exists=true;
 const service=new IdentityDeletionService(db,()=>store,{inspect:async()=>({exists,activeSessions:exists}),remove:async()=>{exists=false;}},async()=>({subject,authenticatedAt:Date.now()}));
 const auth=async()=>exists?subject:null;
 const server=createApi(store,auth,undefined,identityDeletionHttp(service,auth,undefined,serializedDeletionWorker(service)));
 await new Promise<void>(resolve=>server.listen(0,'127.0.0.1',resolve));
 const base=`http://127.0.0.1:${(server.address() as {port:number}).port}`;
 const values=new Map<string,string>(),saved={getItem:(key:string)=>values.get(key)??null,setItem:(key:string,value:string)=>{values.set(key,value);}};
 const token=`e30.${btoa(JSON.stringify({iss:`https://${project}.supabase.co/auth/v1`,aud:'authenticated',role:'authenticated',sub:subject,session_id:subject,exp:9999999999})).replace(/=/g,'').replace(/\+/g,'-').replace(/\//g,'_')}.c2ln`;
 const sdk={getSession:vi.fn(async()=>({data:{session:{user:{id:subject},access_token:token,expires_at:9999999999}},error:null})),getUser:vi.fn(async()=>({data:{user:{id:subject,is_anonymous:false}},error:null})),signOut:vi.fn(async()=>({error:null}))};
 const provider=new VerifiedLearnerProvider(sdk as unknown as SupabaseClient['auth'],project,()=>1000,saved);
 let lose=true;const send:typeof fetch=async(input,init)=>{
   const url=new URL(String(input));expect(url.search).toBe('');
   const response=await fetch(`${base}${url.pathname}`,init);
   if(url.pathname.endsWith(':begin')&&lose){lose=false;throw Error('lost receipt');}return response;
 };
 try {
   const account=await provider.bind(),storage=account.storage(saved);storage.setItem('draft','saved');
   const cleanup=vi.fn(async()=>{storage.setItem('draft','');});
   await expect(new LearnerIdentityDeletion(account,storage).deliver(provider.identityDeletion(account,'https://api.example',send),cleanup,{email:'learner@example.com',password:'fresh'})).rejects.toThrow('lost');
   expect(exists).toBe(false);expect(cleanup).not.toHaveBeenCalled();expect(storage.getItem('draft')).toBe('saved');
   const restarted=new VerifiedLearnerProvider(sdk as unknown as SupabaseClient['auth'],project,()=>1000,saved),local=restarted.localBinding()!;
   sdk.getUser.mockClear();sdk.getSession.mockClear();
   await new LearnerIdentityDeletion(local,local.storage(saved)).deliver(restarted.identityDeletion(local,'https://api.example',send),cleanup);
   expect(cleanup).toHaveBeenCalledOnce();expect(sdk.getUser).not.toHaveBeenCalled();expect(sdk.getSession).not.toHaveBeenCalled();
   expect(new LearnerIdentityDeletion(local,local.storage(saved)).read()?.complete).toBe(true);
 }finally{await new Promise<void>(resolve=>server.close(()=>resolve()));await db.close();}
});

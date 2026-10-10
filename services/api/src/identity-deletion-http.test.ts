import {initialSchemaSection} from './initial-schema';
import {it,expect,vi} from 'vitest';
import {PGlite} from '@electric-sql/pglite';
import {randomUUID,randomBytes} from 'node:crypto';
import {createApi} from './server';
import {FoundationStore} from './store';
import {IdentityDeletionService} from './identity-deletion';
import {identityDeletionHttp,serializedDeletionWorker} from './identity-deletion-http';

it('recovers a lost HTTP receipt without provider auth and never runs a worker from recovery',async()=> {
  const db=new PGlite(),store=new FoundationStore(db);await store.initialize();
  await db.exec(await initialSchemaSection('identity-deletion.sql'));
  const subject=randomUUID(),requestId=randomUUID(),recoveryCapability=randomBytes(32).toString('base64url');
  let exists=true;const remove=vi.fn(async()=>{exists=false;});
  const service=new IdentityDeletionService(db,()=>store,{inspect:async()=>({exists,activeSessions:exists}),remove},
    async proof=>(proof as {password:string}).password==='fresh'?{subject,authenticatedAt:Date.now()}:null);
  const auth=vi.fn(async()=>exists?subject:null);
  const server=createApi(store,auth,'https://learner.example',identityDeletionHttp(service,auth));
  await new Promise<void>(resolve=>server.listen(0,'127.0.0.1',resolve));
  const base=`http://127.0.0.1:${(server.address() as {port:number}).port}`;
  const input={apiVersion:'v2',requestId,recoveryCapability};
  const send=(suffix:string,data:unknown,headers:Record<string,string>={})=>fetch(`${base}/v2/me/identity-deletion:${suffix}`,{
    method:'POST',headers:{'Content-Type':'application/json',...headers},body:JSON.stringify(data)});
  try {
    const begin={...input,confirmation:'delete_identity',proof:{email:'learner@example.com',password:'fresh'}};
    expect((await send('begin',begin,{'X-Learner-Subject':randomUUID()})).status).toBe(409);
    expect((await send('begin',begin,{'X-Learner-Subject':subject,Origin:'https://wrong.example'})).status).toBe(403);
    expect((await send('begin',{...begin,proof:{...begin.proof,password:'wrong'}},{'X-Learner-Subject':subject})).status).toBe(401);
    const preflight=await fetch(`${base}/v2/me/identity-deletion:status`,{method:'OPTIONS',headers:{Origin:'https://learner.example',
      'Access-Control-Request-Method':'POST','Access-Control-Request-Headers':'content-type'}});
    expect(preflight.status).toBe(204);expect(preflight.headers.get('access-control-allow-origin')).toBe('https://learner.example');
    expect(await (await send('begin',begin,{'X-Learner-Subject':subject})).json()).toEqual({apiVersion:'v2',requestId,status:'pending'});
    expect(await (await send('status',input)).json()).toEqual({apiVersion:'v2',requestId,status:'pending'});
    expect(remove).not.toHaveBeenCalled();
    // Explicit private host delivery, then recreate the service as after restart.
    await serializedDeletionWorker(service)(requestId.toUpperCase());
    const restarted=new IdentityDeletionService(db,()=>store,{inspect:async()=>({exists,activeSessions:exists}),remove},async()=>null);
    const recovered=await (await send('status',input)).json();
    expect(recovered).toMatchObject({apiVersion:'v2',requestId,status:'identity_deleted'});
    expect(recovered.completedAt).toBeTruthy();
    expect(await restarted.recover(requestId,recoveryCapability)).toMatchObject(recovered);
    expect(remove).toHaveBeenCalledTimes(1);
    expect((await send('status',{...input,recoveryCapability:randomBytes(32).toString('base64url')})).status).toBe(404);
    expect((await send('status',{...input,password:'secret'})).status).toBe(400);
    expect((await send('status',{...input,recoveryCapability:'x'.repeat(9000)})).status).toBe(413);
    expect((await fetch(`${base}/v2/me/identity-deletion:status?capability=${recoveryCapability}`)).status).toBe(404);
  } finally {await new Promise<void>(resolve=>server.close(()=>resolve()));await db.close();}
});

it('bounds unauthenticated recovery traffic and resets its limit after a fixed window',async()=> {
  let now=0;const recover=vi.fn(async()=>{throw Error('private detail');});
  const service={recover} as unknown as IdentityDeletionService;
  const server=createApi({} as FoundationStore,async()=>null,undefined,identityDeletionHttp(service,async()=>null,()=>now));
  await new Promise<void>(resolve=>server.listen(0,'127.0.0.1',resolve));
  const url=`http://127.0.0.1:${(server.address() as {port:number}).port}/v2/me/identity-deletion:status`;
  const send=()=>fetch(url,{method:'POST',headers:{'Content-Type':'application/json','X-Forwarded-For':randomUUID()},
    body:JSON.stringify({apiVersion:'v2',requestId:randomUUID(),recoveryCapability:randomBytes(32).toString('base64url')})});
  try {
    for(let i=0;i<10;i++) {const response=await send();expect(response.status).toBe(500);expect(JSON.stringify(await response.json())).not.toContain('private detail');}
    const limited=await send();expect(limited.status).toBe(429);expect(limited.headers.get('retry-after')).toBe('60');
    expect(recover).toHaveBeenCalledTimes(10);now=60_000;expect((await send()).status).toBe(500);
  }finally {await new Promise<void>(resolve=>server.close(()=>resolve()));}
});

it('serializes private worker delivery and permits explicit continuation after a failed provider call',async()=> {
  let release!:()=>void;const gate=new Promise<void>(resolve=>{release=resolve;});
  const calls:string[]=[];
  const service={continue:async(id:string)=>{calls.push(id);if(id==='first'){await gate;throw Error('timeout');}return id;}} as unknown as IdentityDeletionService;
  const run=serializedDeletionWorker(service);
  const first=run('first').catch(()=>null),second=run('second');
  await Promise.resolve();expect(calls).toEqual(['first']);release();await first;expect(await second).toBe('second');
  expect(calls).toEqual(['first','second']);
});

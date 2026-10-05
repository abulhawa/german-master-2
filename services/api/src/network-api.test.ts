import {it,expect,vi} from 'vitest';
import type {AddressInfo} from 'node:net';
import type {Pool,PoolClient} from 'pg';
import {PostgresDatabase} from './postgres';
import {createNetworkApi} from './network-api';

it('network startup refuses absent baseline without running fixture DDL',async()=> {
  const query=vi.fn(async(sql:string)=> {
    if(sql.includes('schema_baseline')) throw Error('baseline absent');
    return {rows:[{present:false}]};
  });
  const db=new PostgresDatabase({connect:async()=>({query,release:vi.fn()}) as unknown as PoolClient,end:vi.fn()} as unknown as Pick<Pool,'connect'|'end'>);
  await expect(createNetworkApi(db,async()=>null)).rejects.toThrow('baseline absent');
  expect(query.mock.calls.every(([sql])=>sql.startsWith('SELECT'))).toBe(true);
});

it('network host keeps real learner deletion disabled even for a verified account',async()=> {
  const query=vi.fn(async(sql:string)=>({rows:sql.includes('schema_baseline')?[{version:1}]:[{present:true}]}));
  const db=new PostgresDatabase({connect:async()=>({query,release:vi.fn()}) as unknown as PoolClient,end:vi.fn()} as unknown as Pick<Pool,'connect'|'end'>);
  const server=await createNetworkApi(db,async()=> 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');
  await new Promise<void>(resolve=>server.listen(0,'127.0.0.1',resolve));
  try {
    const response=await fetch(`http://127.0.0.1:${(server.address() as AddressInfo).port}/v2/me`,{method:'DELETE'});
    expect(response.status).toBe(403);expect((await response.json()).code).toBe('deletion_not_enabled');
    expect(query.mock.calls.every(([sql])=>sql.startsWith('SELECT'))).toBe(true);
  } finally {await new Promise<void>((resolve,reject)=>server.close(error=>error?reject(error):resolve()));}
});

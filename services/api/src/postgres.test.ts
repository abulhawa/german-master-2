import {it,expect,vi} from 'vitest';
import type {Pool,PoolClient} from 'pg';
import {PostgresDatabase,safeSequence} from './postgres';

it('scoped transactions keep context and writes on the acquired connection and release after commit',async()=> {
  const query=vi.fn(async(_sql:string,_values?:unknown[])=>({rows:[]}));const release=vi.fn();
  const client={query,release} as unknown as PoolClient;
  const connect=vi.fn(async()=>client);
  const db=new PostgresDatabase({connect,end:vi.fn()} as unknown as Pick<Pool,'connect'|'end'>);
  const subject='aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa';
  await db.forSubject(subject).transaction(async tx=>{await tx.query('SELECT $1::uuid',[subject]);});
  expect(connect).toHaveBeenCalledTimes(1);
  expect(query.mock.calls.map(call=>call[0])).toEqual(['BEGIN',"SELECT set_config('gm.subject', $1, true)",'SELECT $1::uuid','COMMIT']);
  expect(release).toHaveBeenCalledWith(false);
});

it('does not retry ambiguous commits and discards a connection if rollback fails',async()=> {
  const query=vi.fn(async(sql:string)=> {
    if(sql==='COMMIT' || sql==='ROLLBACK') throw Error('connection lost');
    return {rows:[]};
  });const release=vi.fn();const connect=vi.fn(async()=>({query,release}) as unknown as PoolClient);
  const db=new PostgresDatabase({connect,end:vi.fn()} as unknown as Pick<Pool,'connect'|'end'>);
  await expect(db.transaction(async tx=>tx.query('SELECT 1'))).rejects.toThrow('connection lost');
  expect(connect).toHaveBeenCalledTimes(1);expect(release).toHaveBeenCalledWith(true);
});

it('rejects insecure TLS and malformed subject before connecting',()=> {
  expect(()=>PostgresDatabase.connect({ssl:false})).toThrow('TLS');
  expect(()=>PostgresDatabase.connect({ssl:{rejectUnauthorized:false}})).toThrow('TLS');
  expect(()=>PostgresDatabase.connect({connectionString:'postgres://localhost/db?sslmode=no-verify',ssl:{rejectUnauthorized:true}})).toThrow('TLS');
  const connect=vi.fn();const db=new PostgresDatabase({connect,end:vi.fn()} as unknown as Pick<Pool,'connect'|'end'>);
  expect(()=>db.forSubject('unverified')).toThrow();expect(connect).not.toHaveBeenCalled();
});

it('preserves integer contract sequences without precision loss',()=> {
  expect(safeSequence('42')).toBe(42);
  expect(safeSequence('9007199254740991')).toBe(Number.MAX_SAFE_INTEGER);
  expect(()=>safeSequence('9007199254740992')).toThrow('range');
});

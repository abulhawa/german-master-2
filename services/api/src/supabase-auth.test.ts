import {it,expect,vi} from 'vitest';
import type {IncomingMessage} from 'node:http';
import {verifiedSupabaseAuth,createSupabaseAuthenticate} from './supabase-auth';
import {currentAuthSession} from './supabase-auth';
import {PGlite} from '@electric-sql/pglite';
import {readFile} from 'node:fs/promises';
const project='zgmyrpzwgtydwlzponih',subject='aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',session='bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb';
const claims={iss:`https://${project}.supabase.co/auth/v1`,aud:'authenticated',role:'authenticated',sub:subject,session_id:session,exp:2000000000};
function request(overrides:Record<string,unknown>={}) {
  const token=`e30.${Buffer.from(JSON.stringify({...claims,...overrides})).toString('base64url')}.c2ln`;
  return {headers:{authorization:`Bearer ${token}`}} as IncomingMessage;
}
it('accepts only online verified identity with a current owned session',async()=> {
  const getUser=vi.fn(async()=>({data:{user:{id:subject}},error:null}));const live=vi.fn(async()=>true);
  const authenticate=verifiedSupabaseAuth(project,{getUser},live,()=>1000000000000);
  expect(await authenticate(request())).toBe(subject);expect(live).toHaveBeenCalledWith(subject,session);
  live.mockResolvedValue(false);expect(await authenticate(request())).toBeNull();
  getUser.mockResolvedValue({data:{user:{id:session}},error:null});expect(await authenticate(request())).toBeNull();
});
it('rejects wrong project, audience, role, expiry, missing session and fixture credentials before online calls',async()=> {
  const getUser=vi.fn();const live=vi.fn();const authenticate=verifiedSupabaseAuth(project,{getUser},live,()=>1000000000000);
  for(const value of [{iss:'https://other.supabase.co/auth/v1'},{aud:'service_role'},{role:'service_role'},{exp:1},{session_id:null},{sub:'invalid'}])
    expect(await authenticate(request(value))).toBeNull();
  expect(await authenticate({headers:{authorization:'Bearer foundation-local-demo'}} as IncomingMessage)).toBeNull();
  expect(getUser).not.toHaveBeenCalled();expect(live).not.toHaveBeenCalled();
});
it('fails closed on auth/session outages and refuses elevated server keys',async()=> {
  const authenticate=verifiedSupabaseAuth(project,{getUser:async()=>{throw Error('offline');}},async()=>true);
  expect(await authenticate(request())).toBeNull();
  expect(()=>createSupabaseAuthenticate(project,'sb_secret_fixture',async()=>true)).toThrow('Publishable');
});

it('session verifier reads only current owned session identifiers and cannot read token material or mutate auth',async()=> {
  const db=new PGlite();
  try {
    // Minimal isolated auth-session shape; not a claim of Supabase schema parity.
    await db.exec('CREATE SCHEMA auth; CREATE TABLE auth.sessions(id uuid PRIMARY KEY,user_id uuid,not_after timestamptz,refresh_secret text)');
    await db.exec(await readFile(new URL('../../../db/baseline/auth-session-access.sql',import.meta.url),'utf8'));
    await db.query('INSERT INTO auth.sessions VALUES($1,$2,NULL,$3)',[session,subject,'synthetic-test-only']);
    const scoped={query:<T>(sql:string,values?:unknown[])=>db.transaction(async tx=> {
      await tx.exec('SET LOCAL ROLE gm_auth_verifier');return tx.query<T>(sql,values);
    })};
    const exists=currentAuthSession(scoped);
    expect(await exists(subject,session)).toBe(true);expect(await exists(session,session)).toBe(false);
    await expect(scoped.query('SELECT refresh_secret FROM auth.sessions')).rejects.toThrow();
    await expect(scoped.query('DELETE FROM auth.sessions')).rejects.toThrow();
    await db.query("UPDATE auth.sessions SET not_after=statement_timestamp()-interval '1 second'");
    expect(await exists(subject,session)).toBe(false);
    await db.exec('DELETE FROM auth.sessions');expect(await exists(subject,session)).toBe(false);
  } finally {await db.close();}
});

import {initialSchemaSection} from './initial-schema';
import {it,expect} from 'vitest';
import {PGlite} from '@electric-sql/pglite';
import {currentAuthSession} from './supabase-auth';
import {identityPresence} from './supabase-identity-deletion';

it('private invoker views work without auth schema lookup access and retain column/RLS/write boundaries',async()=>{
  const db=new PGlite();const subject='aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',session='bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb';
  try {
    await db.exec(`CREATE SCHEMA auth;CREATE TABLE auth.users(id uuid PRIMARY KEY,email text);
      CREATE TABLE auth.sessions(id uuid PRIMARY KEY,user_id uuid,not_after timestamptz,refresh_secret text);
      ALTER TABLE auth.users ENABLE ROW LEVEL SECURITY;ALTER TABLE auth.sessions ENABLE ROW LEVEL SECURITY;`);
    for(const file of ['auth-session-access.sql','identity-verifier-access.sql','auth-verifier-views.sql'])
      await db.exec(await initialSchemaSection(file));
    await db.exec('REVOKE USAGE ON SCHEMA auth FROM gm_auth_verifier,gm_identity_verifier');
    await db.query('INSERT INTO auth.users VALUES($1,$2)',[subject,'disposable@example.test']);
    await db.query('INSERT INTO auth.sessions VALUES($1,$2,NULL,$3)',[session,subject,'test-only']);
    const scoped=(role:string)=>({query:<T>(sql:string,values?:unknown[])=>db.transaction(async tx=>{
      await tx.exec(`SET LOCAL ROLE ${role}`);return tx.query<T>(sql,values);
    })});
    const verifier=scoped('gm_auth_verifier'),observer=scoped('gm_identity_verifier');
    expect(await currentAuthSession(verifier,true)(subject,session)).toBe(true);
    expect(await identityPresence(observer,true)(subject)).toEqual({exists:true,activeSessions:true});
    await expect(verifier.query('SELECT id FROM auth.sessions')).rejects.toThrow('permission denied');
    await expect(verifier.query('SELECT refresh_secret FROM gm_auth.session_identity')).rejects.toThrow();
    await expect(observer.query('SELECT * FROM gm_auth.session_identity')).rejects.toThrow('permission denied');
    await expect(observer.query('DELETE FROM gm_auth.identity_subject')).rejects.toThrow('permission denied');
    await db.exec('DROP POLICY gm_identity_users_read ON auth.users');
    await expect(identityPresence(observer,true)(subject)).rejects.toMatchObject({code:'identity_provider_unavailable'});
    await db.exec('CREATE POLICY gm_identity_users_read ON auth.users FOR SELECT TO gm_identity_verifier USING(true)');
    await db.exec('ALTER VIEW gm_auth.identity_subject SET (security_invoker=false)');
    await expect(identityPresence(observer,true)(subject)).rejects.toMatchObject({code:'identity_provider_unavailable'});
    await db.exec('ALTER VIEW gm_auth.session_identity SET (security_invoker=false)');
    expect(await currentAuthSession(verifier,true)(subject,session)).toBe(false);
  }finally{await db.close();}
});

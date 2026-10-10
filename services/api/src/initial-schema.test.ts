import {it,expect} from 'vitest';
import {PGlite} from '@electric-sql/pglite';
import {readFile} from 'node:fs/promises';

it('complete initial schema installs empty with restricted roles, invoker views and RLS',async()=>{
  const db=new PGlite();
  try {
    // Local stand-ins for provider-owned objects; no network or provider credentials.
    await db.exec(`CREATE SCHEMA auth;
      CREATE TABLE auth.users(id uuid PRIMARY KEY);
      CREATE TABLE auth.sessions(id uuid PRIMARY KEY,user_id uuid,not_after timestamptz);
      ALTER TABLE auth.users ENABLE ROW LEVEL SECURITY;
      ALTER TABLE auth.sessions ENABLE ROW LEVEL SECURITY;`);
    await db.exec(await readFile(new URL('../../../db/0001_initial_schema.sql',import.meta.url),'utf8'));
    expect((await db.query('SELECT version FROM gm.schema_baseline')).rows).toEqual([{version:1}]);
    expect((await db.query("SELECT to_regclass('gm.schema_migration') AS ledger")).rows).toEqual([{ledger:null}]);
    expect((await db.query(`SELECT relname FROM pg_class c JOIN pg_namespace n ON n.oid=c.relnamespace
      WHERE n.nspname IN ('gm','gm_privacy') AND c.relkind='r' AND NOT c.relrowsecurity`)).rows).toEqual([]);
    for(const table of ['learner_profile','attempt','learning_target','accepted_evidence'])
      expect((await db.query(`SELECT count(*)::int AS count FROM gm.${table}`)).rows).toEqual([{count:0}]);
    expect((await db.query(`SELECT rolname FROM pg_roles WHERE rolname IN
      ('gm_backend','gm_auth_verifier','gm_privacy_worker','gm_identity_verifier')
      AND (rolsuper OR rolbypassrls OR rolcanlogin OR rolcreatedb OR rolcreaterole)`)).rows).toEqual([]);
    expect((await db.query(`SELECT proconfig,prosecdef FROM pg_proc
      WHERE oid='gm.reject_mutation()'::regprocedure`)).rows).toEqual([{proconfig:['search_path=""'],prosecdef:false}]);
    expect((await db.query(`SELECT count(*)::int AS count FROM pg_class c JOIN pg_namespace n ON n.oid=c.relnamespace
      WHERE n.nspname='gm_auth' AND c.relkind='v' AND 'security_invoker=true'=ANY(c.reloptions)`)).rows).toEqual([{count:3}]);
    await db.transaction(async tx=>{
      await tx.exec('SET LOCAL ROLE gm_backend');
      expect((await tx.query('SELECT * FROM gm.learner_profile')).rows).toEqual([]);
      await expect(tx.exec('INSERT INTO gm.topic VALUES(gen_random_uuid(),\'{}\')')).rejects.toThrow();
    });
  }finally{await db.close();}
});

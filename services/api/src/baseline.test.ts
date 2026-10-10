import {initialSchemaSection} from './initial-schema';
import {expect,it} from 'vitest';
import {PGlite} from '@electric-sql/pglite';
import {randomUUID} from 'node:crypto';
import {FoundationStore} from './store';


it('clean baseline initializes idempotently without upgrade history or fixture data',async()=> {
  const clean=new PGlite();const development=new PGlite();
  try {
    const sql=await initialSchemaSection('v2.sql');expect(sql).not.toMatch(/^\s*(?:ALTER TABLE|UPDATE gm\.|DROP (?:TABLE|SCHEMA|TRIGGER))\b/m); // Dynamic RLS enablement below is deliberate initial setup.
    await clean.exec(sql);await new FoundationStore(clean).initialize();await new FoundationStore(clean).initialize();await new FoundationStore(development).initialize();
    expect((await clean.query("SELECT to_regclass('gm.schema_migration') AS ledger")).rows).toEqual([{ledger:null}]);
    expect((await clean.query('SELECT version FROM gm.schema_baseline')).rows).toEqual([{version:1}]);
    expect((await clean.query('SELECT count(*) AS count FROM gm.learning_target')).rows[0]).toEqual({count:0});
    expect((await clean.query(`SELECT relname FROM pg_class r JOIN pg_namespace n ON n.oid=r.relnamespace WHERE n.nspname='gm' AND r.relkind='r' AND NOT r.relrowsecurity`)).rows).toEqual([]);
    await clean.exec('CREATE ROLE baseline_probe NOLOGIN');
    expect((await clean.query(`SELECT has_schema_privilege('baseline_probe','gm','USAGE') AS allowed`)).rows).toEqual([{allowed:false}]);
    await clean.exec('GRANT USAGE ON SCHEMA gm TO baseline_probe; GRANT SELECT ON gm.learner_profile TO baseline_probe');
    const subject=randomUUID();await clean.query(`INSERT INTO gm.learner_profile(user_id,locale,timezone) VALUES($1,'en','UTC')`,[subject]);
    await clean.transaction(async tx=> {await tx.exec('SET LOCAL ROLE baseline_probe');expect((await tx.query('SELECT * FROM gm.learner_profile')).rows).toEqual([]);});
  } finally {await clean.close();await development.close();}
});

it.each(['fresh baseline','installed baseline remediation'])('%s supports owned pack, Skip, completion, export and deletion while keeping shared revisions immutable',async(mode)=> {
  const clean=new PGlite();const development=new PGlite();
  try {
    const baseline=await initialSchemaSection('v2.sql');
    await clean.exec(mode==='fresh baseline' ? baseline : baseline.replace(" SET search_path = ''",''));
    const definition=await clean.query('SELECT prosrc,proowner,proacl FROM pg_proc WHERE oid=\'gm.reject_mutation()\'::regprocedure');
    if(mode==='installed baseline remediation') {
      expect((await clean.query('SELECT proconfig FROM pg_proc WHERE oid=\'gm.reject_mutation()\'::regprocedure')).rows).toEqual([{proconfig:null}]);
      await clean.exec("ALTER FUNCTION gm.reject_mutation() SET search_path = '';");
      expect(await clean.query('SELECT prosrc,proowner,proacl FROM pg_proc WHERE oid=\'gm.reject_mutation()\'::regprocedure')).toEqual(definition);
    }
    expect((await clean.query('SELECT proconfig,prosecdef FROM pg_proc WHERE oid=\'gm.reject_mutation()\'::regprocedure')).rows).toEqual([{proconfig:['search_path=""'],prosecdef:false}]);
    // A caller-controlled path must not replace the built-ins used by the privacy trigger.
    await clean.exec(`CREATE SCHEMA hostile;
      CREATE FUNCTION hostile.current_setting(text,boolean) RETURNS text LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'shadow function invoked'; END $$;
      CREATE FUNCTION hostile.to_jsonb(anyelement) RETURNS jsonb LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'shadow function invoked'; END $$;
      SET search_path = hostile, gm, pg_catalog;`);
    await new FoundationStore(development).initialize();
    // Fixture rows are loaded by this isolated test only; the baseline contains no catalog.
    for(const table of ['topic','skill','learning_target','exercise','exercise_revision','content_release','content_release_exercise','revision_evidence_identity']) {
      for(const row of (await development.query<Record<string,unknown>>(`SELECT * FROM gm.${table}`)).rows) {
        const names=Object.keys(row);await clean.query(`INSERT INTO gm.${table} (${names.map(n=>`"${n}"`).join(',')}) VALUES (${names.map((_,i)=>`$${i+1}`).join(',')})`,Object.values(row));
      }
    }
    const store=new FoundationStore(clean,()=>new Date('2026-10-05T12:00:00Z'));const owner=randomUUID();const other=randomUUID();
    await store.profile(other);
    const pack=await store.preparePack(owner,{apiVersion:'v2',requestId:randomUUID(),questionCount:5,capabilities:['short_answer@1','choice@1','cloze@1','word_order@1','multi_slot@1']});
    const session=pack.sessions[0];const q=session.questions[0];
    await store.expose(owner,{eventId:randomUUID(),deviceId:randomUUID(),sessionQuestionId:q.id,exerciseRevision:q.exercise.revision,disposition:'skip',occurredAt:'2026-10-05T12:00:00Z'},randomUUID());
    await store.complete(owner,session.id,{apiVersion:'v2',requestId:randomUUID(),mode:'partial'});
    expect((await store.exportLearner(owner)).exposures).toHaveLength(1);
    await expect(clean.query('UPDATE gm.exercise_revision SET provenance=$1 WHERE exercise_id=$2',['changed',q.exercise.id])).rejects.toThrow('immutable');
    const request={apiVersion:'v2' as const,requestId:randomUUID(),confirmation:'delete_owned_data' as const};
    const receipt=await store.deleteLearner(owner,request);expect(await store.deleteLearner(owner,request)).toEqual(receipt);
    expect((await store.profile(other)).setupCompleted).toBe(false);await expect(store.profile(owner)).rejects.toThrow();
    expect((await clean.query('SELECT count(*) AS count FROM gm.exercise_revision')).rows[0]).toEqual({count:5});
  } finally {await clean.close();await development.close();}
});

import {it,expect} from 'vitest';
import {PGlite} from '@electric-sql/pglite';
import {readFile} from 'node:fs/promises';
import {randomUUID} from 'node:crypto';
import {FoundationStore} from './store';
import type {SqlDatabase,SqlTransaction} from './database';
import {runtimeManifestHash,runtimeMembers} from './runtime-catalog';
import metadata from '../../../content/foundation/metadata.json';
import editorial from '../../../content/foundation/review.json';

it('backend role serves only its verified subject and cannot modify shared content or escape ownership',async()=> {
  const db=new PGlite();const a=randomUUID(),b=randomUUID();
  try {
    await db.exec(await readFile(new URL('../../../db/baseline/v2.sql',import.meta.url),'utf8'));
    await db.exec(await readFile(new URL('../../../db/baseline/backend-access.sql',import.meta.url),'utf8'));
    const fixture=new PGlite();
    try {
      await new FoundationStore(fixture).initialize();
      for(const table of ['topic','skill','learning_target','exercise','exercise_revision','content_release','content_release_exercise','revision_evidence_identity']) {
        for(const row of (await fixture.query<Record<string,unknown>>(`SELECT * FROM gm.${table}`)).rows) {
          // Synthetic role-test approval only; the original fixture remains unpublished.
          if(table==='learning_target') row.status='published';
          if(table==='exercise_revision') row.review_status='approved';
          if(table==='content_release') {row.status='published';row.published_at=new Date();}
          const names=Object.keys(row);
          await db.query(`INSERT INTO gm.${table} (${names.map(n=>`"${n}"`).join(',')}) VALUES (${names.map((_,i)=>`$${i+1}`).join(',')})`,Object.values(row));
        }
      }
    } finally {await fixture.close();}
    const targets=metadata.targets.map(t=>({...t,topicId:editorial.topics[0].id,level:'B1' as const}));
    const members=await runtimeMembers(db,editorial.releaseId);
    const manifestHash=runtimeManifestHash(editorial.releaseId,targets,members);
    await db.query('UPDATE gm.content_release SET manifest_hash=$2 WHERE id=$1',[editorial.releaseId,manifestHash]);
    const catalog={releaseId:editorial.releaseId,targets,manifestHash};
    const scoped=(subject:string):SqlDatabase=> {
      const transaction=<T>(work:(tx:SqlTransaction)=>Promise<T>)=>db.transaction(async tx=> {
        await tx.exec('SET LOCAL ROLE gm_backend');
        await tx.query("SELECT set_config('gm.subject',$1,true)",[subject]);
        return work(tx);
      });
      return {baselineOnly:true,transaction,query:<T>(sql:string,values?:unknown[])=>transaction(tx=>tx.query<T>(sql,values)),exec:sql=>transaction(tx=>tx.exec(sql))};
    };
    const owned=scoped(a);const store=new FoundationStore(owned,undefined,undefined,undefined,catalog);
    await store.initialize();await store.profile(a);await new FoundationStore(scoped(b)).profile(b);
    expect((await owned.query('SELECT user_id FROM gm.learner_profile')).rows).toEqual([{user_id:a}]);
    const pack=await store.preparePack(a,{apiVersion:'v2',requestId:randomUUID(),questionCount:5,capabilities:['short_answer@1','choice@1','cloze@1','word_order@1','multi_slot@1']});
    const session=pack.sessions[0],question=session.questions[0];
    await store.expose(a,{eventId:randomUUID(),deviceId:randomUUID(),sessionQuestionId:question.id,exerciseRevision:question.exercise.revision,disposition:'skip',occurredAt:new Date().toISOString()},randomUUID());
    await store.complete(a,session.id,{apiVersion:'v2',requestId:randomUUID(),mode:'partial'});
    expect((await store.exportLearner(a)).exposures).toHaveLength(1);
    expect((await store.targets(a)).targets.length).toBeGreaterThan(0);
    await expect(owned.exec('UPDATE gm.session_question SET position=99')).rejects.toThrow();
    await expect(owned.query("INSERT INTO gm.learner_profile(user_id,locale,timezone) VALUES($1,'en','UTC')",[randomUUID()])).rejects.toThrow();
    await expect(owned.query('UPDATE gm.learner_profile SET user_id=$1 WHERE user_id=$2',[b,a])).rejects.toThrow();
    await expect(owned.exec('DELETE FROM gm.topic')).rejects.toThrow();
    await expect(owned.exec('CREATE TABLE gm.escape(id int)')).rejects.toThrow();
    await db.transaction(async tx=> {await tx.exec('SET LOCAL ROLE gm_backend');expect((await tx.query('SELECT * FROM gm.learner_profile')).rows).toEqual([]);});
    const request={apiVersion:'v2' as const,requestId:randomUUID(),confirmation:'delete_owned_data' as const};
    const receipt=await store.deleteLearner(a,request);expect(await store.deleteLearner(a,request)).toEqual(receipt);
    expect((await new FoundationStore(scoped(b)).profile(b)).setupCompleted).toBe(false);
    await expect(owned.exec('DELETE FROM gm.deleted_learner')).rejects.toThrow();
  } finally {await db.close();}
});

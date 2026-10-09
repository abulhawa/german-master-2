import { expect, it } from 'vitest';
import { readFileSync } from 'node:fs';
import { randomUUID } from 'node:crypto';
import { PGlite } from '@electric-sql/pglite';
import { buildBasicCandidate, basicContentHash } from './basic-content';
import { FoundationStore } from './store';
import { runtimeManifestHash, runtimeMembers, RuntimeCatalogSchema } from './runtime-catalog';
import { grade } from '@german-master/learning-engine';
import previousJson from '../../../content/production/starter-catalog.json';
import b1 from '../../../content/drafts/initial-30.json';
import b2 from '../../../content/drafts/b2-basics.json';
import type { Rubric } from '@german-master/learning-engine';
import type { Exercise, SessionRequest } from '@german-master/contracts';

const input={...b1,targets:[...b1.targets,...b2.targets]};
const previous=RuntimeCatalogSchema.parse(previousJson);
const read=(path:string)=>readFileSync(new URL(path,import.meta.url),'utf8');
const request=():SessionRequest=>({apiVersion:'v2',requestId:randomUUID(),questionCount:15,
  capabilities:['short_answer@1','choice@1','cloze@1','multi_slot@1','word_order@1','gap_choice@1','matching@1']});
async function prepare(db:PGlite, publish=false) {
  await new FoundationStore(db).initialize();
  await db.exec(read('../../../db/seed/production-starter.sql'));
  return buildBasicCandidate(input,previous,await runtimeMembers(db,previous.releaseId),publish?{
    contentHash:basicContentHash(input,previous),authorizedBy:'Synthetic local acceptance',
    authorizedAt:'2026-10-07T12:00:00Z',authorization:'Local test only; no production approval.',independentReview:'pending'}:undefined);
}
it('installs an additive draft with an exact manifest, retains old revisions and cannot allocate live practice',async()=> {
  const db=new PGlite();
  try {
    const candidate=await prepare(db);
    const old=await runtimeMembers(db,previous.releaseId);
    await db.exec(candidate.sql);
    const rows=await runtimeMembers(db,candidate.config.releaseId);
    expect(rows).toHaveLength(125);
    expect(candidate.config.targets).toHaveLength(65);
    expect(runtimeManifestHash(candidate.config.releaseId,candidate.config.targets,rows)).toBe(candidate.config.manifestHash);
    expect(await runtimeMembers(db,previous.releaseId)).toEqual(old);
    expect(rows.filter(r=>r.level==='B2')).toHaveLength(60);
    const b2Rows = rows.filter(r => r.level === 'B2');
    expect(b2Rows.every(r => r.revision === 2 && (r.payload as Exercise).type === 'gap_choice')).toBe(true);
    expect(b2Rows.filter(r => r.review_status === 'pending')).toHaveLength(60);
    expect(rows.filter(r=>r.review_status==='pending')).toHaveLength(120);
    const converted=rows.filter(r=>r.revision===2 && r.level==='B1');
    expect(converted).toHaveLength(40);
    expect(converted.filter(r=>(r.payload as Exercise).type==='choice')).toHaveLength(20);
    expect(converted.filter(r=>(r.payload as Exercise).type==='gap_choice')).toHaveLength(20);
    await expect(new FoundationStore(db,undefined,undefined,undefined,candidate.config).createSession(randomUUID(),request()))
      .rejects.toMatchObject({code:'content_unavailable'});
    expect((await db.query('SELECT * FROM gm.practice_session')).rows).toHaveLength(0);
    await expect(db.query('UPDATE gm.exercise_revision SET payload=$1 WHERE exercise_id=$2',[{},b2.targets[0].variants[0].exercise.id]))
      .rejects.toThrow('immutable');
    expect(()=>buildBasicCandidate(input,{...previous,manifestHash:'0'.repeat(64)},old)).toThrow('Previous release');
    expect(()=>buildBasicCandidate(input,previous,old,{contentHash:'0'.repeat(64),authorizedBy:'Synthetic local acceptance',
      authorizedAt:'2026-10-07T12:00:00Z',authorization:'Local test only.',independentReview:'pending'})).toThrow('does not match content');
  } finally {await db.close();}
});
it('keeps the committed unpublished candidate synchronized with the generator',async()=> {
  const db=new PGlite();
  try {
    const candidate=await prepare(db);
    expect(JSON.parse(read('../../../content/candidates/basics/catalog.json'))).toEqual(candidate.config);
    expect(read('../../../content/candidates/basics/seed.sql').replace(/\r\n/g, '\n')).toBe(candidate.sql);
  } finally {await db.close();}
});
it('locally simulates activation: B1 and B2 allocate distinct targets, grade and replay; old sessions stay pinned',async()=> {
  const db=new PGlite();
  try {
    const candidate=await prepare(db,true);
    const owner=randomUUID(), oldStore=new FoundationStore(db,undefined,undefined,undefined,previous);
    const oldRequest={...request(),questionCount:1};
    const oldSession=await oldStore.createSession(owner,oldRequest);
    // Synthetic LOCAL ONLY authorization, distinct from independent review.
    await db.exec(candidate.sql);
    const rows=await runtimeMembers(db,candidate.config.releaseId);
    const config={...candidate.config,manifestHash:runtimeManifestHash(candidate.config.releaseId,candidate.config.targets,rows)};
    await db.query('UPDATE gm.content_release SET manifest_hash=$2 WHERE id=$1',[config.releaseId,config.manifestHash]);
    const store=new FoundationStore(db,undefined,undefined,undefined,config);
    expect(await store.createSession(owner,oldRequest)).toEqual(oldSession);
    for(const level of ['B1','B2'] as const) {
      const learner=randomUUID();
      await store.saveProfile(learner,{apiVersion:'v2',requestId:randomUUID(),expectedRevision:0,
        preferences:{locale:'en',timezone:'Europe/Berlin',level,sessionQuestionCount:15}});
      const catalog=await store.catalog(learner);
      expect(catalog.targets.filter(t=>t.availableQuestionCount>0)).toHaveLength(level==='B1'?35:30);
      const sessionInput=request(), session=await store.createSession(learner,sessionInput);
      expect(session.questions).toHaveLength(15);
      expect(new Set(session.questions.map(q=>q.exercise.targetId)).size).toBe(15);
      for(const question of session.questions) {
        const row=rows.find(r=>r.exercise_id===question.exercise.id)!;
        expect(row.level).toBe(level);
        const rubric=row.rubric as Rubric;
        const attempt={attemptId:randomUUID(),sessionQuestionId:question.id,exerciseRevision:question.exercise.revision,
          deviceId:randomUUID(),answer:rubric.acceptedAnswers[0],assistance:[],answeredAt:new Date().toISOString(),clientSequence:0};
        const receipt=await store.submit(learner,attempt,randomUUID());
        expect(receipt.status).toBe('accepted');
        expect((await store.submit(learner,attempt,randomUUID())).status).toBe('duplicate');
      }
      expect(await store.createSession(learner,sessionInput)).toEqual(session);
      if(level==='B1') {
        const focused=await store.createSession(learner,{...request(),questionCount:1,
          focus:{type:'target',id:'10000000-0000-4000-8000-000000000015'}});
        expect(focused.questions).toHaveLength(1);
        expect(focused.questions[0].exercise.type).toBe('gap_choice');
        expect(focused.questions[0].exercise.revision).toBe(2);
        const verbFocused=await store.createSession(learner,{...request(),questionCount:1,
          focus:{type:'target',id:'10000000-0000-4000-8000-000000000020'}});
        expect(verbFocused.questions).toHaveLength(1);
        expect(verbFocused.questions[0].exercise.type).toBe('gap_choice');
        expect(verbFocused.questions[0].exercise.revision).toBe(2);
        const pluralFocused=await store.createSession(learner,{...request(),questionCount:1,
          focus:{type:'target',id:'10000000-0000-4000-8000-000000000000'}});
        expect(pluralFocused.questions).toHaveLength(1);
        expect(pluralFocused.questions[0].exercise.type).toBe('choice');
        expect(pluralFocused.questions[0].exercise.revision).toBe(2);
      }
      const evidence=(await db.query('SELECT * FROM gm.accepted_evidence WHERE user_id=$1',[learner])).rows;
      expect(evidence).toHaveLength(15);
    }
    for(const row of rows) {
      const rubric=row.rubric as Rubric;
      for(const answer of rubric.acceptedAnswers)
        expect(grade(row.payload as Exercise,rubric,answer,[]).outcome).toBe('correct');
    }
  } finally {await db.close();}
});

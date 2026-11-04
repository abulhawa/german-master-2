import {it,expect} from 'vitest';
import {readFileSync} from 'node:fs';
import {randomUUID} from 'node:crypto';
import {PGlite} from '@electric-sql/pglite';
import {prepareReviewedEnrichment} from '../scripts/prepare-reviewed-enrichment';
import {FoundationStore} from './store';
import {runtimeMembers,runtimeManifestHash} from './runtime-catalog';
import {importEnrichment} from './enrichment-import';
import {grade,type Rubric} from '@german-master/learning-engine';
import {SessionSchema,type Exercise} from '@german-master/contracts';

it('installs/replays a hash-bound safe wave, grades/saves and rolls back configuration without losing old sessions',async()=>{
 const {candidate,bundle,manifest,previous,previousMembers}=await prepareReviewedEnrichment();
 expect(manifest.newQuestions).toBe(22);
 expect(manifest.decisions.filter(d=>d.decision==='approved').flatMap(d=>d.questions).filter(q=>q.correction).length).toBeGreaterThan(0);
 const db=new PGlite();
 try{
  await new FoundationStore(db).initialize();
  for(const path of ['db/seed/production-starter.sql','db/seed/production-basics.sql'])await db.exec(readFileSync(path,'utf8'));
  const owner=randomUUID(),oldStore=new FoundationStore(db,undefined,undefined,undefined,previous);
  const request={apiVersion:'v2' as const,requestId:randomUUID(),questionCount:5,capabilities:['choice@1' as const,'gap_choice@1' as const]};
  const oldSession=await oldStore.createSession(owner,request);
  await expect(importEnrichment(db,{...candidate.config,manifestHash:'0'.repeat(64)},candidate.sql)).rejects.toThrow('manifest mismatch');
  expect((await db.query('SELECT id FROM gm.content_release WHERE id=$1',[candidate.config.releaseId])).rows).toHaveLength(0);
  expect(await importEnrichment(db,candidate.config,candidate.sql)).toBe('installed');
  expect(await importEnrichment(db,candidate.config,candidate.sql)).toBe('already-installed');
  await expect(importEnrichment(db,{...candidate.config,manifestHash:'0'.repeat(64)},candidate.sql)).rejects.toThrow('never overwrite');
  expect(await runtimeMembers(db,previous.releaseId)).toEqual(previousMembers);
  const rows=await runtimeMembers(db,candidate.config.releaseId);
  expect(runtimeManifestHash(candidate.config.releaseId,candidate.config.targets,rows)).toBe(candidate.config.manifestHash);
  const additions=rows.filter(r=>r.exercise_id.startsWith('81000000'));
  expect(additions).toHaveLength(22);expect(additions.every(r=>r.kind==='lexical'&&r.review_status==='approved')).toBe(true);
  for(const r of additions)for(const answer of (r.rubric as Rubric).acceptedAnswers)
   expect(grade(r.payload as Exercise,r.rubric as Rubric,answer,[]).outcome).toBe('correct');
  const store=new FoundationStore(db,undefined,undefined,undefined,candidate.config);
  await store.saveProfile(owner,{apiVersion:'v2',requestId:randomUUID(),expectedRevision:0,preferences:{locale:'en',timezone:'Europe/Berlin',level:additions[0].level as 'B1'|'B2',sessionQuestionCount:15}});
  const session=await store.createSession(owner,{...request,questionCount:1,requestId:randomUUID(),focus:{type:'target',id:additions[0].target_id}});
  const question=session.questions[0],row=additions.find(r=>r.exercise_id===question.exercise.id)!;
  const attempt={attemptId:randomUUID(),sessionQuestionId:question.id,exerciseRevision:question.exercise.revision,deviceId:randomUUID(),answer:(row.rubric as Rubric).acceptedAnswers[0],assistance:[],answeredAt:'2026-11-04T12:00:00Z',clientSequence:0};
  expect((await store.submit(owner,attempt,randomUUID())).status).toBe('accepted');
  expect((await store.submit(owner,attempt,randomUUID())).status).toBe('duplicate');
  expect(await oldStore.createSession(owner,request)).toEqual(oldSession);
  // Restore previous configuration: old allocations work, new saved answers/revisions remain available.
  expect((await oldStore.createSession(owner,{...request,requestId:randomUUID()})).contentReleaseId).toBe(previous.releaseId);
  expect((await db.query('SELECT * FROM gm.accepted_evidence WHERE user_id=$1',[owner])).rows).toHaveLength(1);
  await expect(db.query('UPDATE gm.exercise_revision SET payload=$1 WHERE exercise_id=$2',[{},row.exercise_id])).rejects.toThrow('immutable');
  for(const level of ['B1','B2']){
   const packs=bundle.packs.filter((p:any)=>p.level===level&&p.learningContext==='general');
   const questions=packs.flatMap((p:any)=>p.session.questions);
   const offline=SessionSchema.parse({...packs[0].session,questions});
   expect(packs.every((p:any)=>p.session.contentReleaseId===candidate.config.releaseId)).toBe(true);
   const draft={type:'choice' as const,optionId:'0'};
   const adoption={apiVersion:'v2' as const,requestId:randomUUID(),deviceId:randomUUID(),contentReleaseId:offline.contentReleaseId,questions:offline.questions.map(q=>({exerciseId:q.exercise.id,exerciseRevision:q.exercise.revision})),attempts:[],skippedIndices:[],index:0,draft,assisted:false,order:[]};
   const adopted=await store.adoptGuestRound(owner,adoption);
   expect(adopted.session.questions).toHaveLength(questions.length);
   expect(adopted.draft).toEqual(draft);
   expect(await store.adoptGuestRound(owner,adoption)).toEqual(adopted);
   expect(offline.questions.length).toBeLessThanOrEqual(125);
   expect(()=>SessionSchema.parse({...offline,questions:Array.from({length:126},(_,i)=>({...offline.questions[0],id:randomUUID()}))})).toThrow();
  }
  for(const [file,value]of Object.entries({'catalog.json':candidate.config,'offline.json':bundle,'manifest.json':manifest}))
   expect(JSON.parse(readFileSync(`content/candidates/enrichment-wave-1/${file}`,'utf8'))).toEqual(value);
  expect(readFileSync('content/candidates/enrichment-wave-1/seed.sql','utf8').replaceAll('\r\n','\n')).toBe(candidate.sql);
 }finally{await db.close();}
},60000);

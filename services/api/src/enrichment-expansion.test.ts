import {it,expect} from 'vitest';
import {readFileSync} from 'node:fs';
import {randomUUID} from 'node:crypto';
import {PGlite} from '@electric-sql/pglite';
import {prepareReviewedExpansion} from '../scripts/prepare-reviewed-expansion';
import {FoundationStore} from './store';
import {importEnrichment} from './enrichment-import';
import {runtimeMembers,runtimeManifestHash} from './runtime-catalog';
import {grade,type Rubric} from '@german-master/learning-engine';
import {SessionSchema,type Exercise} from '@german-master/contracts';

it('installs the full eligible reviewed subset, excludes unresolved targets and saves bounded whole rounds',async()=>{
 const {candidate,bundle,manifest,previous,previousMembers}=await prepareReviewedExpansion();
 expect(manifest.reviewedQuestions).toBe(2588);expect(manifest.newQuestions).toBe(2466);
 expect(manifest.excludedTargets).toBe(50);expect(manifest.appliedCorrections).toBe(91);
 expect(manifest.decisions.filter(d=>d.decision==='approved')).toHaveLength(1233);
 const db=new PGlite();try{
  await new FoundationStore(db).initialize();
  for(const file of ['db/seed/production-starter.sql','db/seed/production-basics.sql','content/candidates/enrichment-wave-1/seed.sql'])await db.exec(readFileSync(file,'utf8'));
  expect(await importEnrichment(db,candidate.config,candidate.sql)).toBe('installed');
  expect(await importEnrichment(db,candidate.config,candidate.sql)).toBe('already-installed');
  expect(await runtimeMembers(db,previous.releaseId)).toEqual(previousMembers);
  const rows=await runtimeMembers(db,candidate.config.releaseId);
  expect(rows).toHaveLength(2613);expect(runtimeManifestHash(candidate.config.releaseId,candidate.config.targets,rows)).toBe(candidate.config.manifestHash);
  const approved=new Set(manifest.decisions.filter(d=>d.decision==='approved').map(d=>d.targetId));
  const excluded=new Set(manifest.decisions.filter(d=>d.decision==='excluded-material-defect').map(d=>d.targetId));
  expect(rows.some(r=>excluded.has(r.target_id))).toBe(false);
  for(const row of rows.filter(r=>approved.has(r.target_id)))for(const answer of (row.rubric as Rubric).acceptedAnswers)
   expect(grade(row.payload as Exercise,row.rubric as Rubric,answer,[]).outcome).toBe('correct');
  const store=new FoundationStore(db,undefined,undefined,undefined,candidate.config),oldStore=new FoundationStore(db,undefined,undefined,undefined,previous);
  for(const level of ['B1','B2']){
   const packs=bundle.packs.filter((p:any)=>p.level===level&&p.learningContext==='general');
   const session=SessionSchema.parse({...packs[0].session,questions:packs.flatMap((p:any)=>p.session.questions).slice(-125)});
   expect(session.questions).toHaveLength(125);
   const request={apiVersion:'v2' as const,requestId:randomUUID(),deviceId:randomUUID(),contentReleaseId:session.contentReleaseId,questions:session.questions.map(q=>({exerciseId:q.exercise.id,exerciseRevision:q.exercise.revision})),attempts:[],skippedIndices:[],index:0,assisted:false,order:[]};
   const owner=randomUUID(),saved=await store.adoptGuestRound(owner,request);
   expect(saved.session.questions).toHaveLength(125);expect(await oldStore.adoptGuestRound(owner,request)).toEqual(saved);
  }
  for(const [name,value]of Object.entries({'catalog.json':candidate.config,'offline.json':bundle,'manifest.json':manifest}))expect(JSON.parse(readFileSync(`content/candidates/enrichment-wave-2/${name}`,'utf8'))).toEqual(value);
  expect(readFileSync('content/candidates/enrichment-wave-2/seed.sql','utf8').replaceAll('\r\n','\n')).toBe(candidate.sql);
 }finally{await db.close();}
},120000);

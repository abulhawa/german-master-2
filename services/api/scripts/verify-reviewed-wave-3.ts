import {readFileSync} from 'node:fs';
import {randomUUID} from 'node:crypto';
import {PGlite} from '@electric-sql/pglite';
import {SessionSchema,type Exercise} from '@german-master/contracts';
import {grade,type Rubric} from '@german-master/learning-engine';
import {FoundationStore} from '../src/store';
import {runtimeMembers,runtimeManifestHash,RuntimeCatalogSchema} from '../src/runtime-catalog';
import {importEnrichment} from '../src/enrichment-import';

const read=(path:string)=>JSON.parse(readFileSync(path,'utf8'));
const check=(ok:unknown,message:string)=>{if(!ok)throw Error('Wave 3 candidate acceptance: '+message);};
const path='content/candidates/enrichment-wave-3/';
const prev=RuntimeCatalogSchema.parse(read('content/candidates/enrichment-wave-2/catalog.json'));
const config=RuntimeCatalogSchema.parse(read(path+'catalog.json'));
const manifest=read(path+'manifest.json');
const bundle=read(path+'offline.json');
const seed=readFileSync(path+'seed.sql','utf8');
check(manifest.readOnlyCandidate&&manifest.active===false&&manifest.publicationApproved===false,'Candidate must remain inactive');
check(manifest.previousReleaseId===prev.releaseId&&manifest.releaseId===config.releaseId,'Release identity mismatch');
check(manifest.newQuestions===3006&&manifest.newTargets===1503,'Unexpected candidate size');
check(manifest.excludedQuestions===188,'Unresolved material questions not excluded');
check(bundle.packs.every((p:any)=>p.session.questions.length<=125),'Oversized pack');
const approved=new Set(manifest.decisions.filter((d:any)=>d.decision==='ready-for-candidate').map((d:any)=>d.targetId));
const excluded=new Set(manifest.decisions.filter((d:any)=>d.decision==='excluded-material-defect').map((d:any)=>d.targetId));
const db=new PGlite();
try{
  await new FoundationStore(db).initialize();
  for(const f of ['db/seed/production-starter.sql','db/seed/production-basics.sql','content/candidates/enrichment-wave-1/seed.sql','content/candidates/enrichment-wave-2/seed.sql'])await db.exec(readFileSync(f,'utf8'));
  const previousMembers=await runtimeMembers(db,prev.releaseId);
  // Must not publish the release when its manifest does not match.
  let rejected=false;
  try{await importEnrichment(db,{...config,manifestHash:'0'.repeat(64)},seed);}
  catch(e){rejected=true;}
  check(rejected,'Corrupted manifest should be rejected');
  const absent=await db.query('SELECT id FROM gm.content_release WHERE id=$1',[config.releaseId]);
  check(absent.rows.length===0,'Failed import left new release installed');
  check((await importEnrichment(db,config,seed))==='installed','First import failed');
  check((await importEnrichment(db,config,seed))==='already-installed','Idempotent replay failed');
  check(JSON.stringify(await runtimeMembers(db,prev.releaseId))===JSON.stringify(previousMembers),'Previous release changed');
  const rows=await runtimeMembers(db,config.releaseId);
  check(rows.length===5619,'Expected 5619 total previous and new immutable revisions');
  check(runtimeManifestHash(config.releaseId,config.targets,rows)===config.manifestHash,'Runtime manifest digest mismatch');
  check(!rows.some((r:any)=>excluded.has(r.target_id)),'Excluded material target leaked');
  const newRows=rows.filter((r:any)=>approved.has(r.target_id));
  check(newRows.length===3006,'New row identity count mismatch');
  for(const row of newRows)for(const answer of (row.rubric as Rubric).acceptedAnswers){
    check(grade(row.payload as Exercise,row.rubric as Rubric,answer,[]).outcome==='correct','Accepted answer fails grading: '+row.exercise_id);
  }
  const store=new FoundationStore(db,undefined,undefined,undefined,config);
  const oldStore=new FoundationStore(db,undefined,undefined,undefined,prev);
  const pools:any[]=[];
  for(const level of ['B1','B2']){
    const packs=bundle.packs.filter((p:any)=>p.level===level&&p.learningContext==='general');
    const questions=packs.flatMap((p:any)=>p.session.questions);
    check(new Set(questions.map((q:any)=>q.exercise.id)).size===questions.length,'Duplicate question in '+level+' offline pool');
    const frozen=SessionSchema.parse({...packs[0].session,questions:questions.slice(-125)});
    const oversized={...frozen,questions:questions.slice(-126)};
    check(!SessionSchema.safeParse(oversized).success,'Session accepted 126 questions');
    const request={apiVersion:'v2' as const,requestId:randomUUID(),deviceId:randomUUID(),contentReleaseId:frozen.contentReleaseId,
      questions:frozen.questions.map(q=>({exerciseId:q.exercise.id,exerciseRevision:q.exercise.revision})),attempts:[],skippedIndices:[],index:0,assisted:false,order:[]};
    const owner=randomUUID(),saved=await store.adoptGuestRound(owner,request);
    check(saved.session.questions.length===125,'Saved round lost questions');
    check(JSON.stringify(await oldStore.adoptGuestRound(owner,request))===JSON.stringify(saved),'Previous selector cannot recover new saved round');
    pools.push({level,libraryQuestions:questions.length,adopted:125});
  }
  console.log(JSON.stringify({verifiedNewQuestions:newRows.length,previousQuestions:previousMembers.length,newTotal:rows.length,gradedAcceptedAnswers:true,repeatableImport:true,previousReleasePreserved:true,savedRoundLimit:125,excludedTargets:excluded.size,pools},null,2));
}finally{await db.close();}

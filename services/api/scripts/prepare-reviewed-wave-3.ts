import {readFileSync,writeFileSync,mkdirSync,existsSync} from 'node:fs';
import {execFileSync} from 'node:child_process';
import {createHash} from 'node:crypto';
import {PGlite} from '@electric-sql/pglite';
import {SessionSchema} from '@german-master/contracts';
import {validateDraftCatalog} from '../../../packages/learning-engine/src/content-review';
import {FoundationStore} from '../src/store';
import {runtimeMembers,RuntimeCatalogSchema} from '../src/runtime-catalog';
import {buildBasicCandidate,basicContentHash} from '../src/basic-content';

const directory='content/candidates/enrichment-wave-3';
const reviewCommit='0f8d39ef501c10062b574a9d5c1483f75b3b04b3';
const releaseId='92000000-0000-4000-8000-000000000001';
const read=(p:string)=>JSON.parse(readFileSync(p,'utf8'));
const sha=(s:string)=>createHash('sha256').update(s.replaceAll('\r\n','\n')).digest('hex');
const stable=(v:any):any=>Array.isArray(v)?v.map(stable):v&&typeof v==='object'?Object.fromEntries(Object.keys(v).sort().map(k=>[k,stable(v[k])])):v;
const check=(ok:unknown,message:string)=>{if(!ok)throw Error(message);};
const gitText=(commit:string,path:string)=>execFileSync('git',['show',commit+':'+path],{encoding:'utf8',maxBuffer:16*1024*1024});
const gitJson=(commit:string,path:string)=>JSON.parse(gitText(commit,path));
const review=(p:string)=>gitJson(reviewCommit,'content/review/enrichment/'+p);

export async function prepareReviewedWave3(){
  mkdirSync(directory,{recursive:true});
  // Always independently run exact-source/objective/option/recheck eligibility audit.
  execFileSync('node',['tools/enrichment-wave-3-preflight.mjs','--report',directory+'/readiness.json'],{stdio:'inherit'});
  const readiness=read(directory+'/readiness.json');
  check(readiness.reviewCommit===reviewCommit&&readiness.reviewedQuestions===5682&&readiness.alreadyPublishedQuestions===2488,'Unexpected review freeze');
  const previous=RuntimeCatalogSchema.parse(read('content/candidates/enrichment-wave-2/catalog.json'));
  check(previous.releaseId==='91000000-0000-4000-8000-000000000001','Wrong base release');
  const oldIds=new Set(previous.targets.map((t:any)=>t.id));
  const selected:any[]=[],mapping=new Map<string,number>(),kinds=new Map<string,'lexical'|'grammar'>(),contexts=new Map<string,'general'|'work'>();
  const posTopics=['nouns','verbs','adjectives','adverbs','conjunctions','prepositions','particles'];
  const topicNames=[['Nouns in context','Nomen im Kontext'],['Verbs in context','Verben im Kontext'],['Adjectives in context','Adjektive im Kontext'],['Adverbs in context','Adverbien im Kontext'],['Connectors in context','Konjunktionen im Kontext'],['Prepositions in context','Präpositionen im Kontext'],['Particles in context','Partikeln im Kontext']];
  const choices=readiness.decisions.filter((d:any)=>d.decision==='ready-for-candidate');
  const batches=new Map<string,any[]>();
  for(const decision of choices){
    check(!oldIds.has(decision.targetId),'Already-published target selected '+decision.targetId);
    if(!batches.has(decision.batchId))batches.set(decision.batchId,[]);
    batches.get(decision.batchId)!.push(decision);
  }
  for(const [batch,decisions] of batches){
    const snapshot=decisions[0].sourceSnapshot;
    const catalog=gitJson(snapshot,'content/enrichment/batches/'+batch+'.catalog.json');
    const paths=snapshot==='671e9d19f4f5a61d7916ed96b3b41433b768fd39'?batch+'.overlay.json':'extension-proposals/'+batch+'.overlay.json';
    const overlayFile='content/review/enrichment/'+paths;
    let overlay:any=null;
    try{execFileSync('git',['cat-file','-e',reviewCommit+':'+overlayFile],{stdio:'pipe'});overlay=review(paths);}
    catch{/* Absence is permitted for unchanged reviewed targets. */}
    for(const decision of decisions){
      check(decision.sourceSnapshot===snapshot,'Mixed source snapshots in '+batch);
      const original=catalog.targets.find((t:any)=>t.id===decision.targetId);
      check(original,'Source target missing '+decision.targetId);
      const target=structuredClone(original);
      check(decision.questions.length===target.variants.length,'Incomplete target review '+target.id);
      for(const variant of target.variants){
        const q=decision.questions.find((q:any)=>q.variantKey===variant.variantKey);
        check(q&&q.exerciseId===variant.exercise.id,'Candidate exercise does not match reviewed identity');
        if(!q.correction)continue;
        const patch=overlay?.patches?.find((p:any)=>p.variantKey===variant.variantKey);
        check(patch&&patch.status==='ai-rechecked-inactive'&&patch.exerciseId===variant.exercise.id&&patch.expectedPrompt===variant.exercise.prompt,'Unsafe correction precondition '+variant.variantKey);
        check(sha(JSON.stringify(stable(patch)))===q.correction,'Correction hash mismatch '+variant.variantKey);
        variant.exercise.prompt=patch.proposedPrompt;
        if(patch.proposedInstruction)variant.exercise.instruction=patch.proposedInstruction;
        if(patch.proposedHint)variant.exercise.hint=patch.proposedHint;
        if(patch.proposedExplanation)variant.rubric.explanation=patch.proposedExplanation;
        if(patch.proposedOptions){
          if(variant.exercise.type==='choice')variant.exercise.options=patch.proposedOptions;
          else {check(variant.exercise.type==='gap_choice'&&variant.exercise.slots.length===1,'Unsupported corrected choice');variant.exercise.slots[0].options=patch.proposedOptions;}
        }
        variant.rubric.completedAnswer={schemaVersion:1,parts:[{text:patch.proposedCompletedAnswer,emphasis:false}]};
      }
      selected.push(target);
      mapping.set(target.id,posTopics.indexOf(decision.pos));
      kinds.set(target.id,decision.kind);
      contexts.set(target.id,decision.context);
    }
  }
  check(selected.length===readiness.readyForCandidateTargets,'Incomplete selected targets');
  const context=read('content/drafts/initial-30.json');
  const input={...context,targets:[...context.targets,...selected]};
  const validation=validateDraftCatalog(input),selectedIds=new Set(selected.map(t=>t.id));
  const db=new PGlite();
  try{
    await new FoundationStore(db).initialize();
    for(const file of ['db/seed/production-starter.sql','db/seed/production-basics.sql','content/candidates/enrichment-wave-1/seed.sql','content/candidates/enrichment-wave-2/seed.sql'])await db.exec(readFileSync(file,'utf8'));
    const previousMembers=await runtimeMembers(db,previous.releaseId);
    const candidate=buildBasicCandidate(input,previous,previousMembers,{
      contentHash:basicContentHash(input,previous),
      authorizedBy:'Owner direction 2026-11-04',
      authorizedAt:'2026-11-04T22:00:00Z',
      authorization:'Reviewed = editorially approved; only exact semantically rechecked corrections or nonblocking originals may be included.',
      independentReview:'pending'
    },{
      releaseId,prefix:'92000000',learningContext:'general',
      learningContextForTarget:id=>contexts.get(id)!,
      topics:posTopics.map((key,i)=>({key,title:{en:topicNames[i][0],de:topicNames[i][1]}})),
      topicForTarget:id=>mapping.get(id)!,kindForTarget:id=>kinds.get(id)!,
      includeTarget:id=>selectedIds.has(id)
    });
    check(candidate.newVariants===readiness.readyForCandidateQuestions,'Unexpected new question count');
    const bundle=read('content/candidates/enrichment-wave-2/offline.json');
    const groups=new Map<string,any[]>();
    for(const member of candidate.members.filter((m:any)=>selectedIds.has(m.target_id))){
      const key=member.level+':'+member.topic_id+':'+contexts.get(member.target_id);
      if(!groups.has(key))groups.set(key,[]);
      groups.get(key)!.push(member);
    }
    let pageId=20000;
    for(const rows of groups.values())for(let offset=0;offset<rows.length;offset+=125){
      const page=rows.slice(offset,offset+125),first=page[0];
      bundle.packs.push({
        level:first.level,learningContext:contexts.get(first.target_id)!,topicId:first.topic_id,title:first.topic_title,
        session:{apiVersion:'v2',id:'92000000-0000-4000-8000-'+String(pageId++).padStart(12,'0'),contentReleaseId:releaseId,questions:page.map((m:any)=>({id:m.exercise_id,exercise:m.payload}))},
        rubrics:page.map((m:any)=>({exerciseId:m.exercise_id,exerciseRevision:m.revision,...m.rubric as object})),suggestions:{}
      });
    }
    for(const pack of bundle.packs){if(pack.learningContext==='general')pack.session.contentReleaseId=releaseId;SessionSchema.parse(pack.session);}
    const pools=['B1','B2'].map(level=>{
      const packs=bundle.packs.filter((p:any)=>p.level===level&&p.learningContext==='general');
      const questions=packs.flatMap((p:any)=>p.session.questions);
      check(new Set(questions.map((q:any)=>q.exercise.id)).size===questions.length,'Duplicate library identities');
      SessionSchema.parse({...packs[0].session,questions:questions.slice(0,125)});
      return {level,libraryQuestions:questions.length,maximumRoundQuestions:125,packs:packs.length};
    });
    const manifest={
      schemaVersion:1,readOnlyCandidate:true,active:false,publicationApproved:false,
      reviewCommit,reviewedQuestions:readiness.reviewedQuestions,
      previousReleaseId:previous.releaseId,previousManifestHash:previous.manifestHash,
      releaseId:candidate.config.releaseId,manifestHash:candidate.config.manifestHash,
      newTargets:candidate.newTargets,newQuestions:candidate.newVariants,
      alreadyPublishedQuestions:readiness.alreadyPublishedQuestions,
      excludedTargets:readiness.excludedMaterialDefectTargets,
      excludedQuestions:readiness.excludedMaterialDefectQuestions,
      appliedCorrections:choices.reduce((n:any,d:any)=>n+d.questions.filter((q:any)=>q.correction).length,0),
      canonicalValidatorSha256:sha(readFileSync('packages/learning-engine/src/content-review.ts','utf8')),
      canonicalValidatedTargetsWithContext:validation.targets,
      seedSha256:sha(candidate.sql),offlineSha256:sha(JSON.stringify(bundle,null,2)+'\n'),
      readinessSha256:sha(readFileSync(directory+'/readiness.json','utf8')),
      pools,decisions:readiness.decisions,
      contextPolicy:'Exact source POS to lexical topic; connectors category stays grammar; explicit work_vocabulary stays gated; General unchanged.',
      gates:['Verify generated candidate with unchanged validators and grade-all/round-trip tests','Prove additive import and rollback before production write','Test web and native 125-question freezing, offline retry, account isolation and signed-in resume','No Android Play upload']
    };
    for(const [name,value] of Object.entries({'catalog.json':candidate.config,'offline.json':bundle,'manifest.json':manifest})){
      writeFileSync(directory+'/'+name,JSON.stringify(value,null,2)+'\n');
    }
    writeFileSync(directory+'/seed.sql',candidate.sql);
    console.log(JSON.stringify({releaseId,newQuestions:manifest.newQuestions,newTargets:manifest.newTargets,excludedQuestions:manifest.excludedQuestions,appliedCorrections:manifest.appliedCorrections,pools},null,2));
    return manifest;
  }finally{await db.close();}
}
if(process.argv[1]?.replaceAll('\\','/').endsWith('/prepare-reviewed-wave-3.ts'))await prepareReviewedWave3();

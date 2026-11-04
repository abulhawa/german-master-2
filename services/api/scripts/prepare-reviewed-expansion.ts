import {readFileSync,writeFileSync,mkdirSync} from 'node:fs';
import {createHash} from 'node:crypto';
import {PGlite} from '@electric-sql/pglite';
import {SessionSchema} from '@german-master/contracts';
import {validateDraftCatalog} from '../../../packages/learning-engine/src/content-review';
import {FoundationStore} from '../src/store';
import {runtimeMembers,RuntimeCatalogSchema} from '../src/runtime-catalog';
import {buildBasicCandidate,basicContentHash} from '../src/basic-content';

const directory='content/candidates/enrichment-wave-2';
const sha=(s:string)=>createHash('sha256').update(s.replaceAll('\r\n','\n')).digest('hex');
const read=(p:string)=>JSON.parse(readFileSync(p,'utf8'));
const check=(ok:unknown,message:string)=>{if(!ok)throw Error(message);};
const stable=(v:any):any=>Array.isArray(v)?v.map(stable):v&&typeof v==='object'?Object.fromEntries(Object.keys(v).sort().map(k=>[k,stable(v[k])])):v;
const indexText=readFileSync(`${directory}/evidence/index.json`,'utf8');
check(sha(indexText)==='cfb887d51e94c0a086973e76fb8d2a290dd2bdd28a0fbc3bc453f90127807b1e','Frozen review index changed');
const index=JSON.parse(indexText);
function evidence(name:string){const entry=index[name];check(entry,'Unpinned evidence file');const text=readFileSync(`${directory}/evidence/${name}`,'utf8');check(sha(text)===entry.sha256,`Evidence changed: ${name}`);return JSON.parse(text);}

export async function prepareReviewedExpansion(){
 const previous=RuntimeCatalogSchema.parse(read('content/candidates/enrichment-wave-1/catalog.json'));
 const oldIds=new Set(previous.targets.map(t=>t.id));
 const proof=evidence('review/correction-rechecks.json');
 const selected:any[]=[],decisions:any[]=[];
 const posTopics=['nouns','verbs','adjectives','adverbs','conjunctions','prepositions','particles'];
 const topicNames=[['Nouns in context','Nomen im Kontext'],['Verbs in context','Verben im Kontext'],['Adjectives in context','Adjektive im Kontext'],['Adverbs in context','Adverbien im Kontext'],['Connectors in context','Konjunktionen im Kontext'],['Prepositions in context','PrÃ¤positionen im Kontext'],['Particles in context','Partikeln im Kontext']];
 const mapping=new Map<string,number>(),kinds=new Map<string,'lexical'|'grammar'>(),contexts=new Map<string,'general'|'work'>();let reviewedQuestions=0,corrections=0;
 for(const queue of [evidence('review/queue.json'),evidence('review/extension-queue.json')])for(const entry of queue.entries){
  if(entry.state!=='complete')continue;
  const batch=entry.batchId,sourceName=`source/${batch}.catalog.json`;
  check(index[sourceName].commit===queue.snapshot&&index[sourceName].sha256===entry.catalogSha256,'Queue source binding mismatch');
  const catalog=evidence(sourceName),findings=evidence(`review/${batch}.findings.json`);
  check(findings.state==='complete'&&findings.snapshot===queue.snapshot&&findings.catalogSha256===entry.catalogSha256,'Incomplete or unbound findings');
  const variants=catalog.targets.flatMap((t:any)=>t.variants);
  check(findings.reviewed.length===variants.length&&new Set(findings.reviewed.map((r:any)=>r.variantKey)).size===variants.length,'Incomplete/duplicate review');
  reviewedQuestions+=variants.length;
  const overlayName=[`review/${batch}.overlay.json`,`review/extension-proposals/${batch}.overlay.json`].find(n=>index[n]);
  const overlay=overlayName?evidence(overlayName):null;
  if(overlay)check(overlay.snapshot===queue.snapshot&&overlay.catalogSha256===entry.catalogSha256&&overlay.active===false,'Overlay source binding mismatch');
  for(const original of catalog.targets){
   const target=structuredClone(original),questions:any[]=[];let blocked=false;
   const pos=target.provenance.match(/apps\/web\/data\/pos\/([a-z]+)\.jsonl:/)?.[1];
   check((target.category==='vocabulary'||target.category.endsWith('_vocabulary')||target.category==='connectors')&&posTopics.includes(pos),'Unmapped lexical objective');
   const kind=target.category==='connectors'?'grammar':'lexical',learningContext=target.category==='work_vocabulary'?'work':'general';
   for(const v of target.variants){
    const row=findings.reviewed.find((r:any)=>r.variantKey===v.variantKey);
    const choices=v.exercise.options??v.exercise.slots.flatMap((s:any)=>s.options);
    check(row?.exerciseId===v.exercise.id&&row.targetId===target.id&&row.optionAssessments.length===choices.length&&choices.every((o:any,i:number)=>row.optionAssessments[i].toLowerCase().startsWith(o.text.toLowerCase())),'Question/options review mismatch');
    const safe=row.disposition==='no-blocker-found'||row.disposition.startsWith('optional-')||['revise-style','editorial-typography','source-metadata-error-exercises-correct','context-precision-advisory'].includes(row.disposition);
    let correction:string|null=null;
    if(!safe){
     const patch=overlay?.patches.find((p:any)=>p.variantKey===v.variantKey);
     const recheck=proof.entries.find((r:any)=>r.batchId===batch&&r.variantKey===v.variantKey);
     const semantic=recheck?.scope==='corrected-prompt-options-instruction-hint-explanation-and-completion';
     if(!patch||!recheck||!semantic){blocked=true;questions.push({variantKey:v.variantKey,exerciseId:v.exercise.id,disposition:row.disposition,decision:'excluded-unresolved-material-defect'});continue;}
     correction=sha(JSON.stringify(stable(patch)));
     check(proof.snapshot===queue.snapshot&&patch.status==='ai-rechecked-inactive'&&recheck.decision==='supported-within-stated-task'&&recheck.proposalSha256===correction&&recheck.catalogSha256===entry.catalogSha256&&recheck.exerciseId===v.exercise.id&&patch.exerciseId===v.exercise.id&&patch.expectedPrompt===v.exercise.prompt,'Correction precondition or semantic recheck mismatch');
     v.exercise.prompt=patch.proposedPrompt;
     for(const [field,key]of [['instruction','proposedInstruction'],['hint','proposedHint']] as const)if(patch[key])v.exercise[field]=patch[key];
     if(patch.proposedExplanation)v.rubric.explanation=patch.proposedExplanation;
     if(patch.proposedOptions){if(v.exercise.type==='choice')v.exercise.options=patch.proposedOptions;else{check(v.exercise.type==='gap_choice'&&v.exercise.slots.length===1,'Unsupported option correction');v.exercise.slots[0].options=patch.proposedOptions;}}
     v.rubric.completedAnswer={schemaVersion:1,parts:[{text:patch.proposedCompletedAnswer,emphasis:false}]};
     corrections++;
    }
    questions.push({variantKey:v.variantKey,exerciseId:v.exercise.id,disposition:row.disposition,decision:'approved',correction});
   }
   // Each immutable objective needs its two distinct variants. A material blocker
   // excludes that objective, including the otherwise sound sibling; report it.
   const decision=oldIds.has(target.id)?'already-published':blocked?'excluded-material-defect':'approved';
   decisions.push({batchId:batch,sourceSnapshot:queue.snapshot,catalogSha256:entry.catalogSha256,findingsSha256:index[`review/${batch}.findings.json`].sha256,targetId:target.id,decision,kind,pos,topic:pos,learningContext,questions});
   if(decision==='approved'){selected.push(target);mapping.set(target.id,posTopics.indexOf(pos));kinds.set(target.id,kind);contexts.set(target.id,learningContext);}
  }
 }
 check(reviewedQuestions===2588,'Unexpected review snapshot coverage');
 const context=read('content/drafts/initial-30.json'),input={...context,targets:[...context.targets,...selected]};
 const validation=validateDraftCatalog(input),selectedIds=new Set(selected.map(t=>t.id));
 const db=new PGlite();try{
  await new FoundationStore(db).initialize();
  for(const file of ['db/seed/production-starter.sql','db/seed/production-basics.sql','content/candidates/enrichment-wave-1/seed.sql'])await db.exec(readFileSync(file,'utf8'));
  const previousMembers=await runtimeMembers(db,previous.releaseId);
  const candidate=buildBasicCandidate(input,previous,previousMembers,{contentHash:basicContentHash(input,previous),authorizedBy:'Owner direction 2026-11-04',authorizedAt:'2026-11-04T12:00:00Z',authorization:'Reviewed = editorially approved. Direct incremental production authorized; unresolved material defects excluded.',independentReview:'pending'},
   {releaseId:'91000000-0000-4000-8000-000000000001',prefix:'91000000',learningContext:'general',learningContextForTarget:id=>contexts.get(id)!,topics:posTopics.map((key,i)=>({key,title:{en:topicNames[i][0],de:topicNames[i][1]}})),topicForTarget:id=>mapping.get(id)!,kindForTarget:id=>kinds.get(id)!,includeTarget:id=>selectedIds.has(id)});
  const bundle=read('content/candidates/enrichment-wave-1/offline.json');
  const groups=new Map<string,any[]>();
  for(const member of candidate.members.filter(m=>selectedIds.has(m.target_id))){const key=member.level+':'+member.topic_id+':'+contexts.get(member.target_id);if(!groups.has(key))groups.set(key,[]);groups.get(key)!.push(member);}
  let pageId=10000;
  for(const rows of groups.values())for(let offset=0;offset<rows.length;offset+=125){
   const page=rows.slice(offset,offset+125),first=page[0];
   bundle.packs.push({level:first.level,learningContext:contexts.get(first.target_id)!,topicId:first.topic_id,title:first.topic_title,session:{apiVersion:'v2',id:`91000000-0000-4000-8000-${String(pageId++).padStart(12,'0')}`,contentReleaseId:candidate.config.releaseId,questions:page.map(m=>({id:m.exercise_id,exercise:m.payload}))},rubrics:page.map(m=>({exerciseId:m.exercise_id,exerciseRevision:m.revision,...m.rubric as object})),suggestions:{}});
  }
  for(const pack of bundle.packs){if(pack.learningContext==='general')pack.session.contentReleaseId=candidate.config.releaseId;SessionSchema.parse(pack.session);}
  const pools=['B1','B2'].map(level=>{const packs=bundle.packs.filter((p:any)=>p.level===level&&p.learningContext==='general'),questions=packs.flatMap((p:any)=>p.session.questions);check(new Set(questions.map((q:any)=>q.exercise.id)).size===questions.length,'Duplicate library identity');SessionSchema.parse({...packs[0].session,questions:questions.slice(0,125)});return {level,libraryQuestions:questions.length,maximumRoundQuestions:125,packs:packs.length};});
  const manifest={schemaVersion:1,reviewCommit:'71064b6bde57f1b2cf88d36885521545cd9f4003',reviewedQuestions,previousReleaseId:previous.releaseId,previousManifestHash:previous.manifestHash,releaseId:candidate.config.releaseId,manifestHash:candidate.config.manifestHash,newTargets:candidate.newTargets,newQuestions:candidate.newVariants,alreadyPublishedQuestions:22,excludedTargets:decisions.filter(d=>d.decision==='excluded-material-defect').length,appliedCorrections:decisions.filter(d=>d.decision==='approved').flatMap(d=>d.questions).filter(q=>q.correction).length,canonicalValidatorSha256:sha(readFileSync('packages/learning-engine/src/content-review.ts','utf8')),canonicalValidatedTargetsWithContext:validation.targets,seedSha256:sha(candidate.sql),offlineSha256:sha(JSON.stringify(bundle,null,2)+'\n'),pools,decisions,contextPolicy:'Pinned POS supplies topics; vocabulary objectives are lexical, explicit connectors objectives remain grammar. Explicit work_vocabulary targets retain Work and stay gated. Other reviewed vocabulary is General; a colleague in an example does not imply Work.'};
  return {candidate,bundle,manifest,previous,previousMembers};
 }finally{await db.close();}
}
if(process.argv[1]?.replaceAll('\\','/').endsWith('/prepare-reviewed-expansion.ts')){
 const {candidate,bundle,manifest}=await prepareReviewedExpansion();mkdirSync(directory,{recursive:true});
 for(const [name,value]of Object.entries({'catalog.json':candidate.config,'offline.json':bundle,'manifest.json':manifest}))writeFileSync(`${directory}/${name}`,JSON.stringify(value,null,2)+'\n');
 writeFileSync(`${directory}/seed.sql`,candidate.sql);
 console.log(JSON.stringify({reviewed:manifest.reviewedQuestions,newQuestions:manifest.newQuestions,alreadyPublished:22,excludedTargets:manifest.excludedTargets,corrections:manifest.appliedCorrections,pools:manifest.pools}));
}

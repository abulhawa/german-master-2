import {createHash} from 'node:crypto';
import {readFileSync,writeFileSync,mkdirSync} from 'node:fs';
import {PGlite} from '@electric-sql/pglite';
import {FoundationStore} from '../src/store';
import {runtimeMembers,RuntimeCatalogSchema} from '../src/runtime-catalog';
import {buildBasicCandidate,basicContentHash} from '../src/basic-content';
import {validateDraftCatalog} from '../../../packages/learning-engine/src/content-review';
import {SessionSchema} from '@german-master/contracts';

// Frozen Git exports make offline checks reproducible in shallow CI checkouts.
// Never check out or write the independent review branch.
export const reviewCommit='71064b6bde57f1b2cf88d36885521545cd9f4003';
const reviewRef=reviewCommit;
const sha=(s:string)=>createHash('sha256').update(s.replaceAll('\r\n','\n')).digest('hex');
const evidenceRoot='content/candidates/enrichment-wave-1/evidence';
const indexRaw=readFileSync(`${evidenceRoot}/index.json`,'utf8');
if(sha(indexRaw)!=='9e345316f6e2e0b73a4f5f26e64ee134346c7a340bdec78c1bf03416ac9d5b3e')throw Error('Frozen evidence index changed');
const evidenceIndex=JSON.parse(indexRaw);
const raw=(ref:string,path:string)=>{
 const match=Object.entries(evidenceIndex).find(([,v]:[string,any])=>v.commit===ref&&v.path===path);
 if(!match)throw Error('Unpinned evidence');
 const text=readFileSync(`${evidenceRoot}/${match[0]}`,'utf8');
 if(sha(text)!==(match[1] as any).sha256)throw Error('Frozen evidence hash mismatch');
 return text;
};
const evidence=(name:string)=>JSON.parse(raw(reviewRef,`content/review/enrichment/${name}`));
const stable=(v:any):any=>Array.isArray(v)?v.map(stable):v&&typeof v==='object'?Object.fromEntries(Object.keys(v).sort().map(k=>[k,stable(v[k])])):v;
const require=(ok:unknown,message:string)=>{if(!ok)throw Error(message);};
const local=(p:string)=>JSON.parse(readFileSync(p,'utf8'));
export async function prepareReviewedEnrichment() {
 const queue=evidence('queue.json'),batch='legacy-0001';
 const entry=queue.entries.find((e:any)=>e.batchId===batch);
 require(entry?.state==='complete','Batch review incomplete');
 const sourceRaw=raw(queue.snapshot,`content/enrichment/batches/${batch}.catalog.json`);
 require(sha(sourceRaw)===entry.catalogSha256,'Source catalog hash mismatch');
 const source=JSON.parse(sourceRaw),findingsRaw=raw(reviewRef,`content/review/enrichment/${batch}.findings.json`);
 const findings=JSON.parse(findingsRaw),overlay=evidence(`${batch}.overlay.json`),rechecks=evidence('correction-rechecks.json');
 require(findings.state==='complete'&&findings.snapshot===queue.snapshot&&findings.catalogSha256===entry.catalogSha256,'Findings binding mismatch');
 require(overlay.snapshot===queue.snapshot&&overlay.catalogSha256===entry.catalogSha256&&overlay.active===false&&rechecks.snapshot===queue.snapshot,'Correction evidence binding mismatch');
 const variants=source.targets.flatMap((t:any)=>t.variants);
 require(findings.reviewed.length===variants.length&&new Set(findings.reviewed.map((r:any)=>r.variantKey)).size===variants.length,'Incomplete/duplicate review');
 for(const t of source.targets)for(const v of t.variants){
  const r=findings.reviewed.find((r:any)=>r.variantKey===v.variantKey);
  const choices=v.exercise.options??v.exercise.slots.flatMap((s:any)=>s.options);
  require(r?.exerciseId===v.exercise.id&&r.targetId===t.id&&r.optionAssessments.length===choices.length&&choices.every((o:any,i:number)=>r.optionAssessments[i].toLowerCase().startsWith(o.text.toLowerCase())),'Review identity/options mismatch');
 }
 // Explicit everyday noun objectives. Employment, legal and specialist work targets are outside this wave.
 const ids=new Set([101,102,103,104,105,108,109,110,111,112,117].map(n=>`80000000-0000-4000-8000-${String(n).padStart(12,'0')}`));
 const selected:any[]=[],decisions:any[]=[];
 for(const original of source.targets){
  if(!ids.has(original.id)) {decisions.push({targetId:original.id,decision:'excluded-wave-scope'});continue;}
  const target=structuredClone(original),proof:any[]=[];let blocked=false;
  for(const v of target.variants){
   const finding=findings.reviewed.find((r:any)=>r.variantKey===v.variantKey);
   const optional=finding.disposition==='no-blocker-found'||finding.disposition.startsWith('optional-')||['revise-style','editorial-typography'].includes(finding.disposition);
   if(optional){proof.push({variantKey:v.variantKey,exerciseId:v.exercise.id,disposition:finding.disposition,correction:null});continue;}
   const patch=overlay.patches.find((p:any)=>p.variantKey===v.variantKey);
   const recheck=rechecks.entries.find((r:any)=>r.batchId===batch&&r.variantKey===v.variantKey);
   if(!patch||!recheck){blocked=true;break;}
   const patchHash=sha(JSON.stringify(stable(patch)));
   require(patch.status==='ai-rechecked-inactive'&&recheck.decision==='supported-within-stated-task'&&recheck.scope==='corrected-prompt-options-instruction-hint-explanation-and-completion'&&recheck.proposalSha256===patchHash&&recheck.catalogSha256===entry.catalogSha256&&recheck.exerciseId===v.exercise.id&&patch.exerciseId===v.exercise.id&&patch.expectedPrompt===v.exercise.prompt,'Correction precondition/recheck mismatch');
   const before=JSON.stringify(v.rubric.acceptedAnswers);
   v.exercise.prompt=patch.proposedPrompt;
   for(const [field,key] of [['instruction','proposedInstruction'],['hint','proposedHint']] as const)if(patch[key])v.exercise[field]=patch[key];
   if(patch.proposedExplanation)v.rubric.explanation=patch.proposedExplanation;
   if(patch.proposedOptions){if(v.exercise.type==='choice')v.exercise.options=patch.proposedOptions;else {require(v.exercise.type==='gap_choice'&&v.exercise.slots.length===1,'Unsupported correction');v.exercise.slots[0].options=patch.proposedOptions;}}
   v.rubric.completedAnswer={schemaVersion:1,parts:[{text:patch.proposedCompletedAnswer,emphasis:false}]};
   require(JSON.stringify(v.rubric.acceptedAnswers)===before,'Accepted identities changed');
   proof.push({variantKey:v.variantKey,exerciseId:v.exercise.id,disposition:finding.disposition,correction:patchHash,recheck});
  }
  if(blocked){decisions.push({targetId:target.id,decision:'excluded-unresolved-material-defect'});continue;}
  selected.push(target);decisions.push({targetId:target.id,decision:'approved',standard:'owner-approved AI editorial review',kind:'lexical',topic:'everyday-vocabulary',learningContext:'general',questions:proof});
 }
 require(selected.length>0,'Empty safe wave');
 const context=local('content/drafts/initial-30.json');
 const input={...context,targets:[...context.targets,...selected]};
 const validated=validateDraftCatalog(input); // unchanged validator; original minimum/multi-format context retained
 const selectedIds=new Set(selected.map(t=>t.id));
 // Freeze this wave's base even after the active runtime switches to it.
 const previous=RuntimeCatalogSchema.parse(local('content/production/basics-catalog.json'));
 const db=new PGlite();
 try{
  await new FoundationStore(db).initialize();
  for(const path of ['db/seed/production-starter.sql','db/seed/production-basics.sql'])await db.exec(readFileSync(path,'utf8'));
  const previousMembers=await runtimeMembers(db,previous.releaseId);
  const candidate=buildBasicCandidate(input,previous,previousMembers,{contentHash:basicContentHash(input,previous),authorizedBy:'Owner direction 2026-11-04',authorizedAt:'2026-11-04T11:00:00Z',authorization:'Reviewed = editorially approved; incremental release subject to technical gates. See enrichment-reviewed-release.md.',independentReview:'pending'},
   {releaseId:'90000000-0000-4000-8000-000000000001',prefix:'90000000',learningContext:'general',topics:[{key:'everyday-vocabulary',title:{en:'Everyday vocabulary',de:'Wortschatz im Alltag'}}],topicForTarget:()=>0,kindForTarget:()=> 'lexical',includeTarget:id=>selectedIds.has(id)});
  const bundle=local('content/guest/practice.json');
  // Regenerate identically before and after activation; reject an unrelated base.
  bundle.packs=bundle.packs.filter((p:any)=>p.topicId!=='90000000-0000-4000-8000-000000000600');
  for(const pack of bundle.packs)if(pack.learningContext==='general')pack.session.contentReleaseId=previous.releaseId;
  require(sha(JSON.stringify(bundle,null,2)+'\n')==='058254350463ab99270884335cd93a59e2782ff5d43100c0fb6292734bbab9c1','Offline base changed since this wave was prepared');
  for(const level of ['B1','B2']){
   const rows=candidate.members.filter(m=>selectedIds.has(m.target_id)&&m.level===level);
   if(!rows.length)continue;
   bundle.packs.push({level,learningContext:'general',topicId:rows[0].topic_id,title:rows[0].topic_title,session:{apiVersion:'v2',id:rows[0].topic_id,contentReleaseId:candidate.config.releaseId,questions:rows.map(m=>({id:m.exercise_id,exercise:m.payload}))},rubrics:rows.map(m=>({exerciseId:m.exercise_id,exerciseRevision:m.revision,...m.rubric as object})),suggestions:{}});
  }
  // Both clients concatenate eligible packs. Pin every General pack to the additive
  // release containing both old and new members, including the first pack's header.
  for(const pack of bundle.packs)if(pack.learningContext==='general')pack.session.contentReleaseId=candidate.config.releaseId;
  const bounds:any[]=[];
  for(const level of ['B1','B2'])for(const learningContext of ['general','work']){
   const packs=bundle.packs.filter((p:any)=>p.level===level&&p.learningContext===learningContext);
   const questions=packs.flatMap((p:any)=>p.session.questions);
   if(!questions.length)continue;
   SessionSchema.parse({...packs[0].session,questions});
   require(new Set(questions.map((q:any)=>q.exercise.id)).size===questions.length,'Duplicate offline exercise');
   bounds.push({level,learningContext,questions:questions.length});
  }
  const manifest={schemaVersion:1,reviewCommit:reviewRef,sourceCommit:queue.snapshot,sourceCatalogSha256:entry.catalogSha256,findingsSha256:sha(findingsRaw),canonicalValidatorSha256:sha(readFileSync('packages/learning-engine/src/content-review.ts','utf8')),previousReleaseId:previous.releaseId,previousManifestHash:previous.manifestHash,releaseId:candidate.config.releaseId,manifestHash:candidate.config.manifestHash,seedSha256:sha(candidate.sql),offlineSha256:sha(JSON.stringify(bundle,null,2)+'\n'),newTargets:candidate.newTargets,newQuestions:candidate.newVariants,validatedWithContext:{targets:validated.targets,questions:validated.variants},bounds,decisions,technicalReleaseApproved:false,productionDeployed:false};
  return {candidate,bundle,manifest,previous,previousMembers};
 }finally{await db.close();}
}
if(process.argv[1]?.replaceAll('\\','/').endsWith('/prepare-reviewed-enrichment.ts')){
 const {candidate,bundle,manifest}=await prepareReviewedEnrichment();
 const dir='content/candidates/enrichment-wave-1';mkdirSync(dir,{recursive:true});
 for(const [name,value]of Object.entries({'catalog.json':candidate.config,'offline.json':bundle,'manifest.json':manifest}))writeFileSync(`${dir}/${name}`,JSON.stringify(value,null,2)+'\n');
 writeFileSync(`${dir}/seed.sql`,candidate.sql);
 console.log(JSON.stringify({targets:manifest.newTargets,questions:manifest.newQuestions,bounds:manifest.bounds,productionDeployed:false}));
}

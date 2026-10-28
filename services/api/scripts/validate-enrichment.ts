import {readFileSync,readdirSync} from 'node:fs';
import {execFileSync} from 'node:child_process';
import {createHash} from 'node:crypto';
import {fileURLToPath} from 'node:url';
import {z} from 'zod';
import {validateDraftCatalog} from '../../../packages/learning-engine/src/content-review';

const root=new URL('../../../',import.meta.url);
const read=(file:string)=>JSON.parse(readFileSync(new URL(file,root),'utf8'));
const sha=(value:string)=>createHash('sha256').update(value).digest('hex');
execFileSync(process.execPath,[fileURLToPath(new URL('tools/content/enrichment.mjs',root)),'verify'],{stdio:'inherit'});
const rowSchema=z.object({sourceId:z.string(),sourceHash:z.string().regex(/^[a-f0-9]{64}$/),
  decision:z.enum(['pending','authored','duplicate','deferred','out-of-scope']),targetIds:z.array(z.string().uuid()),
  notes:z.string().trim().min(1),corrections:z.string(),rightsBasis:z.string().trim().min(1)}).strict();
const reportSchema=z.object({schemaVersion:z.literal(1),batchId:z.string(),state:z.enum(['draft','ready-for-review']),
  catalogSha256:z.string().regex(/^[a-f0-9]{64}$/),aiReviewer:z.string().trim().min(1),reviewedAt:z.string().date(),
  reviewNotes:z.string().trim().min(1),rows:z.array(rowSchema).max(20)}).strict();
const plan=read('content/enrichment/plan.json');
const sourceCache=new Map<string,string[]>();
const uuid=(prefix:string,n:number)=>`${prefix}-0000-4000-8000-${String(n).padStart(12,'0')}`;
const baseNames=['initial-30','b2-basics','workplace','general-expansion'];
const base=baseNames.flatMap(name=>validateDraftCatalog({...read(`content/drafts/${name}.json`),
  // Small existing pilot shares the established multi-format validation context.
  targets:[...read('content/drafts/initial-30.json').targets,...(name==='initial-30'?[]:read(`content/drafts/${name}.json`).targets)]}).catalog.targets)
  .filter((target,index,all)=>all.findIndex(t=>t.id===target.id)===index);
const targets=new Map(base.map(target=>[target.id,target]));
const prompts=new Set(base.flatMap(target=>target.variants.map(v=>v.exercise.prompt.normalize('NFC').trim())));
const usedSourceIds=new Set<string>();let batches=0;let drafts=0;let questions=0;
const files=readdirSync(new URL('content/enrichment/batches/',root));
for(const name of files){if(!name.endsWith('.catalog.json')&&!name.endsWith('.report.json')&&name!=='.gitkeep')throw Error(`Unexpected batch artifact: ${name}`);}
const catalogNames=files.filter(name=>name.endsWith('.catalog.json')).sort();
for(const report of files.filter(name=>name.endsWith('.report.json')))if(!files.includes(report.replace('.report.json','.catalog.json')))throw Error(`Orphan report: ${report}`);
for(const name of catalogNames){
  const id=name.replace('.catalog.json','');const batch=plan.batches.find((b:{id:string})=>b.id===id);if(!batch)throw Error(`Unplanned batch: ${id}`);
  if(!sourceCache.has(batch.sourceFile))sourceCache.set(batch.sourceFile,readFileSync(new URL(batch.sourceFile,root),'utf8').replaceAll('\r\n','\n').split('\n'));
  const input={records:batch.sourceLines.map((line:number,index:number)=>{
    const record=JSON.parse(sourceCache.get(batch.sourceFile)![line-1]);
    return {sourceId:`${batch.sourceFile}:L${line}`,sourceHash:sha(JSON.stringify(record)),record,
      proposedTargetId:uuid('80000000',batch.number*100+index),
      proposedExerciseIds:[0,1].map(v=>uuid('81000000',batch.number*10000+index*10+v))};
  })};
  const raw=readFileSync(new URL(`content/enrichment/batches/${name}`,root),'utf8').replaceAll('\r\n','\n');
  const catalog=JSON.parse(raw);
  if(catalog.schemaVersion!==1||catalog.status!=='agent-authored-draft'||catalog.publicationApproved!==false||!Array.isArray(catalog.targets)||catalog.targets.length>20)throw Error(`Invalid draft envelope: ${id}`);
  const report=reportSchema.parse(read(`content/enrichment/batches/${id}.report.json`));
  if(report.batchId!==id||report.catalogSha256!==sha(raw)||report.rows.length!==input.records.length)throw Error(`Report/hash mismatch: ${id}`);
  const authoredIds=new Set<string>();
  for(const row of report.rows){
    const source=input.records.find((record:{sourceId:string})=>record.sourceId===row.sourceId);
    if(!source||source.sourceHash!==row.sourceHash||usedSourceIds.has(row.sourceId))throw Error(`Invalid/repeated source: ${row.sourceId}`);
    usedSourceIds.add(row.sourceId);
    if(row.decision==='pending'&&(report.state==='ready-for-review'||row.targetIds.length))throw Error('Unfinished row cannot be marked ready or reference completed targets');
    if(row.decision==='authored'){
      if(row.targetIds.length!==1)throw Error('Author one precise target per seed; defer additional objectives');
      const target=catalog.targets.find((t:{id:string})=>t.id===row.targetIds[0]);
      if(!target||target.level!==source.record.level||target.variants.length!==2)throw Error('Authored row must link two variants at the seed level');
      if(!targets.has(target.id)&&target.id!==source.proposedTargetId)throw Error('Use the allocated new target ID');
      if(JSON.stringify(target.variants.map((v:{exercise:{id:string}})=>v.exercise.id).sort())!==JSON.stringify([...source.proposedExerciseIds].sort()))throw Error('Use allocated immutable exercise IDs');
      authoredIds.add(target.id);
    }else if(row.targetIds.length&&!row.targetIds.every((targetId:string)=>targets.has(targetId)))throw Error('Skipped row may only reference known targets');
  }
  for(const target of catalog.targets){
    if(!authoredIds.has(target.id)||target.review.status!=='pending'||target.review.reviewedHash!==null)throw Error('Keep every authored target pending publication review');
    for(const variant of target.variants){
      if(!['choice','gap_choice','word_order','matching'].includes(variant.exercise.type)||!variant.exercise.hint||!variant.rubric.completedAnswer)throw Error('Require supported selection format, hint and completed answer');
      const prompt=variant.exercise.prompt.normalize('NFC').trim();if(prompts.has(prompt))throw Error(`Duplicate prompt: ${prompt}`);prompts.add(prompt);
    }
    const prior=targets.get(target.id);
    if(prior){
      for(const key of ['title','description','objective','category','level'] as const)if(JSON.stringify(prior[key])!==JSON.stringify(target[key]))throw Error(`Existing objective metadata changed: ${target.id}`);
      targets.set(target.id,{...prior,review:{status:'pending',reviewer:null,date:null,notes:'Unpublished enrichment variants',reviewedHash:null},variants:[...prior.variants,...target.variants]});
    }else targets.set(target.id,target);
    questions+=target.variants.length;
  }
  if(catalog.targets.length!==authoredIds.size)throw Error('Duplicate target in batch');
  if(report.state==='ready-for-review')batches++;else drafts++;
}
// Existing corpus supplies the >=30-target/multi-format context; no policy is weakened.
validateDraftCatalog({schemaVersion:1,status:'agent-authored-draft',publicationApproved:false,targets:[...targets.values()]});
console.log(`Enrichment validation passed: ${batches} ready batches / ${drafts} partial batches / ${questions} new unpublished questions. Structural evidence only; no publication.`);

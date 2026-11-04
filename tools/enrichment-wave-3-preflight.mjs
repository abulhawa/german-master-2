#!/usr/bin/env node
// Read-only release preflight. No GitHub writes, databases, network calls, or publication.
import {execFileSync} from 'node:child_process';
import {readFileSync,writeFileSync,appendFileSync} from 'node:fs';
import {createHash} from 'node:crypto';

const REVIEW_COMMIT='0f8d39ef501c10062b574a9d5c1483f75b3b04b3';
const SOURCE_ORIGINAL='671e9d19f4f5a61d7916ed96b3b41433b768fd39';
const SOURCE_EXTENSION='b7a6507e135895b1ebb671b73538a9a6d3742304';
const EXPECTED_PRIOR_RELEASE='91000000-0000-4000-8000-000000000001';
const REVIEW_DIR='content/review/enrichment/';
const fail=(message)=>{throw Error('Wave 3 preflight: '+message);};
const assert=(ok,message)=>{if(!ok)fail(message);};
const sha256=(text)=>createHash('sha256').update(text.replaceAll('\r\n','\n')).digest('hex');
const stable=(o)=>Array.isArray(o)?o.map(stable):o&&typeof o==='object'?Object.fromEntries(Object.keys(o).sort().map(k=>[k,stable(o[k])])):o;
const fromGit=(commit,path)=>{
  assert(/^[a-f0-9]{40}$/.test(commit)&&!path.includes('..'),'Invalid pinned object');
  try{return execFileSync('git',['show',commit+':'+path],{encoding:'utf8',maxBuffer:16*1024*1024});}
  catch(error){fail('Pinned Git object unavailable '+commit.slice(0,9)+':'+path+' (fetch repository history without shallow truncation)');}
};
const fromReview=(path)=>JSON.parse(fromGit(REVIEW_COMMIT,REVIEW_DIR+path));
const isSafe=(s)=>s==='no-blocker-found'||s.startsWith('optional-')||
  ['revise-style','editorial-typography','source-metadata-error-exercises-correct','context-precision-advisory'].includes(s);

function main(){
  const previous=JSON.parse(readFileSync('content/candidates/enrichment-wave-2/manifest.json','utf8'));
  const published=JSON.parse(readFileSync('content/production/catalog.json','utf8'));
  assert(previous.releaseId===EXPECTED_PRIOR_RELEASE,'Unexpected previous release ID');
  assert(previous.reviewedQuestions===2588,'Previous reviewed tally differs');
  assert(published.releaseId===EXPECTED_PRIOR_RELEASE,'Production source selector not at wave 2');
  const publishedTargets=new Set(published.targets.map(t=>t.id));
  assert(publishedTargets.size===published.targets.length,'Duplicate previously published target IDs');
  const oldDecisions=new Map(previous.decisions.map(d=>[d.targetId,d]));
  assert(oldDecisions.size===previous.decisions.length,'Duplicate previously reviewed target decisions');
  const base=fromReview('queue.json'),extension=fromReview('extension-queue.json');
  const oldCoverage=fromReview('coverage.json'),extCoverage=fromReview('extension-coverage.json');
  const proof=fromReview('correction-rechecks.json');
  assert(base.snapshot===SOURCE_ORIGINAL&&extension.snapshot===SOURCE_EXTENSION,'Unexpected authoring snapshot');
  assert(oldCoverage.reviewedQuestions===1536&&extCoverage.reviewedQuestions===4146,'Incomplete reviewed coverage');
  assert(oldCoverage.completedBatches===49&&extCoverage.completedBatches===126,'Incomplete reviewed batches');
  const entries=[...base.entries.map(e=>({...e,sourceSnapshot:SOURCE_ORIGINAL})),
                 ...extension.entries.map(e=>({...e,sourceSnapshot:SOURCE_EXTENSION}))];
  assert(entries.length===175&&entries.every(e=>e.state==='complete'),'All 175 reviewed batches required');
  const seen=new Set(),decisions=[],blocking=[],collisions=[],titles=new Map();
  let counted=0,alreadyPublished=0,ready=0,excluded=0,semanticCorrections=0;
  let publishedUnknown=0,previouslyExcludedReady=0;
  const expectedProof=new Map(proof.entries.map(e=>[e.batchId+'|'+e.variantKey,e]));
  for(const entry of entries){
    const sourceText=fromGit(entry.sourceSnapshot,'content/enrichment/batches/'+entry.batchId+'.catalog.json');
    assert(sha256(sourceText)===entry.catalogSha256,'Catalog mismatch '+entry.batchId);
    const catalog=JSON.parse(sourceText);
    const findings=fromReview(entry.batchId+'.findings.json');
    assert(findings.state==='complete'&&findings.snapshot===entry.sourceSnapshot&&findings.catalogSha256===entry.catalogSha256,'Review binding mismatch '+entry.batchId);
    const sourceVariants=catalog.targets.flatMap(t=>t.variants);
    const rows=new Map(findings.reviewed.map(r=>[r.variantKey,r]));
    assert(rows.size===sourceVariants.length&&findings.reviewed.length===sourceVariants.length,'Missing or duplicate findings '+entry.batchId);
    const queued=new Set(entry.reviewedVariantKeys);
    assert(queued.size===sourceVariants.length&&sourceVariants.every(v=>queued.has(v.variantKey)),'Queue coverage mismatch '+entry.batchId);
    const overlayPath=entry.sourceSnapshot===SOURCE_ORIGINAL?entry.batchId+'.overlay.json':'extension-proposals/'+entry.batchId+'.overlay.json';
    let overlay=null;
    try{overlay=fromReview(overlayPath);}
    catch(error){if(!String(error).includes('Pinned Git object unavailable'))throw error;}
    if(overlay)assert(overlay.active===false&&overlay.snapshot===entry.sourceSnapshot&&overlay.catalogSha256===entry.catalogSha256,'Overlay binding mismatch '+entry.batchId);
    for(const target of catalog.targets){
      let blocked=false;const questions=[];let corrections=0;
      assert(!seen.has(target.id),'Duplicate target '+target.id);seen.add(target.id);
      const pos=target.provenance?.split('apps/web/data/pos/')[1]?.split('.jsonl:')[0]??null;
      assert(['nouns','verbs','adjectives','adverbs','conjunctions','prepositions','particles'].includes(pos),'Unsupported POS mapping '+target.id);
      const kind=target.category==='connectors'?'grammar':'lexical';
      const context=target.category==='work_vocabulary'?'work':'general';
      const title=String(target.title?.de??'').trim().toLowerCase().normalize('NFC');
      const collisionKey=target.level+'|'+title;
      if(titles.has(collisionKey))collisions.push({targetId:target.id,otherTargetId:titles.get(collisionKey),title,kind});
      else titles.set(collisionKey,target.id);
      for(const v of target.variants){
        const row=rows.get(v.variantKey);
        assert(row?.targetId===target.id&&row.exerciseId===v.exercise.id,'Exercise review identity mismatch '+v.variantKey);
        const options=v.exercise.options??v.exercise.slots?.flatMap(s=>s.options)??[];
        assert(options.length===row.optionAssessments?.length&&options.every((o,i)=>row.optionAssessments[i].toLowerCase().startsWith(o.text.toLowerCase())),'Options review mismatch '+v.variantKey);
        counted++;
        let correction=null;
        if(!isSafe(row.disposition)){
          const patch=overlay?.patches?.find(p=>p.variantKey===v.variantKey);
          const recheck=expectedProof.get(entry.batchId+'|'+v.variantKey);
          const hash=patch?sha256(JSON.stringify(stable(patch))):null;
          const corrected=Boolean(patch&&recheck&&proof.snapshot===entry.sourceSnapshot&&
            recheck.decision==='supported-within-stated-task'&&
            recheck.scope==='corrected-prompt-options-instruction-hint-explanation-and-completion'&&
            recheck.proposalSha256===hash&&recheck.catalogSha256===entry.catalogSha256&&
            recheck.exerciseId===v.exercise.id&&patch.exerciseId===v.exercise.id&&
            patch.expectedPrompt===v.exercise.prompt&&patch.status==='ai-rechecked-inactive');
          if(corrected){correction=hash;corrections++;semanticCorrections++;}
          else {blocked=true;blocking.push({batchId:entry.batchId,targetId:target.id,variantKey:v.variantKey,disposition:row.disposition,proposalPresent:Boolean(patch),reason:!patch?'no-supported-proposal':'no-exact-semantic-recheck'});}
        }
        questions.push({variantKey:v.variantKey,exerciseId:v.exercise.id,disposition:row.disposition,correction});
      }
      const isPublished=publishedTargets.has(target.id);
      const decision=isPublished?'already-published':blocked?'excluded-material-defect':'ready-for-candidate';
      if(isPublished)alreadyPublished+=questions.length;
      else if(blocked)excluded+=questions.length;
      else {ready+=questions.length;if(oldDecisions.get(target.id)?.decision==='excluded-material-defect')previouslyExcludedReady++;}
      if(oldDecisions.has(target.id)&&!isPublished&&oldDecisions.get(target.id).decision!=='excluded-material-defect')publishedUnknown++;
      decisions.push({batchId:entry.batchId,sourceSnapshot:entry.sourceSnapshot,catalogSha256:entry.catalogSha256,targetId:target.id,kind,context,pos,decision,questions,appliedCorrections:corrections});
    }
  }
  assert(counted===5682,'Question count mismatch '+counted);
  assert(alreadyPublished+ready+excluded===5682,'Coverage reconciliation failed');
  assert(publishedUnknown===0,'Previously approved target missing from production catalog');
  assert(alreadyPublished===2488,'Expected precisely 2488 already published reviewed questions, got '+alreadyPublished);
  const report={schemaVersion:1,mode:'read-only-preflight',reviewCommit:REVIEW_COMMIT,priorReleaseId:EXPECTED_PRIOR_RELEASE,reviewedQuestions:counted,alreadyPublishedQuestions:alreadyPublished,readyForCandidateQuestions:ready,excludedMaterialDefectQuestions:excluded,readyForCandidateTargets:decisions.filter(d=>d.decision==='ready-for-candidate').length,excludedMaterialDefectTargets:decisions.filter(d=>d.decision==='excluded-material-defect').length,semanticRecheckedCorrections:semanticCorrections,previouslyExcludedTargetsNowReady:previouslyExcludedReady,materialBlockingVariants:blocking.length,identityCollisionAdvisories:collisions.length,workQuestionsReady:decisions.filter(d=>d.decision==='ready-for-candidate'&&d.context==='work').reduce((n,d)=>n+d.questions.length,0),decisions,blocking,collisions,notes:['No actual corrections applied, no catalog/bundle/SQL generated, no production changes.','All material blockers must be excluded or resolved with exact-hash semantic rechecks before candidate generation.','Context and POS are preliminary mapping checks; preserve all existing released revisions and round limits.','A new immutable release ID and offline web/native release are still required.']};
  const outputArg=process.argv.indexOf('--report');
  if(outputArg>=0){const dest=process.argv[outputArg+1];assert(dest&&!dest.startsWith('-'),'--report requires a local path');writeFileSync(dest,JSON.stringify(report,null,2)+'\n');}
  const summary={reviewCommit:REVIEW_COMMIT.slice(0,12),reviewed:counted,published:alreadyPublished,ready,excluded,readyTargets:report.readyForCandidateTargets,excludedTargets:report.excludedMaterialDefectTargets,materialBlockingVariants:blocking.length,exactSemanticCorrections:semanticCorrections,workReady:report.workQuestionsReady,collisions:collisions.length,previouslyExcludedTargetsNowReady};
  console.log(JSON.stringify(summary,null,2));
  if(process.env.GITHUB_STEP_SUMMARY)appendFileSync(process.env.GITHUB_STEP_SUMMARY,'## Wave 3 read-only preflight\n\n'+Object.entries(summary).map(([k,v])=>'- '+k+': **'+v+'**').join('\n')+'\n\nNo deployment or content activation occurred.\n');
}
main();

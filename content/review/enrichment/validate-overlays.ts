/** Apply proposals only in memory and validate against the unchanged engine. */
import { readFileSync, readdirSync, writeFileSync } from 'node:fs';
import { createHash } from 'node:crypto';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { validateDraftCatalog } from '../../../packages/learning-engine/src/content-review';

const here=dirname(fileURLToPath(import.meta.url)), root=resolve(here,'../../..');
const read=(p:string)=>JSON.parse(readFileSync(p,'utf8'));
const sha=(text:string)=>createHash('sha256').update(text.replaceAll('\r\n','\n')).digest('hex');
const queue=read(resolve(here,'queue.json'));
const source=resolve(root,'.local/enrichment-validation',queue.snapshot);
const initial=read(resolve(source,'content/drafts/initial-30.json'));
const reports:Array<Record<string,unknown>>=[];
for (const name of readdirSync(here).filter(n=>n.endsWith('.overlay.json')).sort()) {
  const overlay=read(resolve(here,name));
  if (overlay.active!==false||overlay.candidateReady!==false||overlay.snapshot!==queue.snapshot)
    throw Error('Inactive, pinned proposal required');
  const original=readFileSync(resolve(source,`content/enrichment/batches/${overlay.batchId}.catalog.json`),'utf8');
  if(sha(original)!==overlay.catalogSha256) throw Error('Source hash mismatch');
  const catalog=JSON.parse(original);
  const byKey=new Map<string,any>(catalog.targets.flatMap((t:any)=>t.variants.map((v:any)=>[v.variantKey,v])));
  const changed:Array<Record<string,unknown>>=[];
  for(const patch of overlay.patches) {
    const variant=byKey.get(patch.variantKey);
    if(!variant||variant.exercise.id!==patch.exerciseId||variant.exercise.prompt!==patch.expectedPrompt)
      throw Error('Patch precondition mismatch');
    const acceptedBefore=JSON.stringify(variant.rubric.acceptedAnswers);
    variant.exercise.prompt=patch.proposedPrompt;
    if(patch.proposedInstruction) variant.exercise.instruction=patch.proposedInstruction;
    if(patch.proposedHint) variant.exercise.hint=patch.proposedHint;
    if(patch.proposedExplanation) variant.rubric.explanation=patch.proposedExplanation;
    if(patch.proposedOptions) {
      if(variant.exercise.type==='choice') variant.exercise.options=patch.proposedOptions;
      else if(variant.exercise.type==='gap_choice'&&variant.exercise.slots.length===1)
        variant.exercise.slots[0].options=patch.proposedOptions;
      else throw Error('Option overlay requires one supported choice group');
    }
    if(patch.proposedAcceptedAnswer&&JSON.stringify(patch.proposedAcceptedAnswer)!==acceptedBefore.slice(1,-1))
      throw Error('Preserve accepted option identity');
    variant.rubric.completedAnswer={schemaVersion:1,parts:[{text:patch.proposedCompletedAnswer,emphasis:false}]};
    changed.push({variantKey:patch.variantKey,exerciseId:patch.exerciseId,
      exerciseSha256:sha(JSON.stringify(variant.exercise)),rubricSha256:sha(JSON.stringify(variant.rubric))});
  }
  // The existing initial corpus supplies canonical minimum-target/multi-format
  // context; the validator is unchanged and the original source stays immutable.
  const result=validateDraftCatalog({...initial,targets:[...initial.targets,...catalog.targets]});
  reports.push({batchId:overlay.batchId,catalogSha256:overlay.catalogSha256,
    overlaySha256:sha(readFileSync(resolve(here,name),'utf8')),
    correctedCatalogSha256:sha(JSON.stringify(catalog)),changedVariants:changed,
    canonicalTargetsWithContext:result.targets,canonicalVariantsWithContext:result.variants});
}
const report={snapshot:queue.snapshot,active:false,publicationApproved:false,
  validatedOverlayFiles:reports.length,correctedVariants:reports.reduce((n,r)=>n+(r.changedVariants as unknown[]).length,0),
  checks:['Exact source hash and prompt/identity preconditions','Original accepted option identities retained',
          'Corrected in-memory exercise/rubric schema and grading conformance with original canonical validator'],
  limitations:['Proposals need substantive editorial recheck; schema validation does not prove correct German.',
              'No corrected source catalogs, runtime bundles, SQL or publication approvals were written.'],reports};
writeFileSync(resolve(here,'overlay-validation.json'),JSON.stringify(report,null,2)+'\n');
console.log(JSON.stringify({overlays:reports.length,correctedVariants:report.correctedVariants,active:false}));

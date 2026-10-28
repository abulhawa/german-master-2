import {readFileSync,writeFileSync} from 'node:fs';
import {validateDraftCatalog} from '../../../packages/learning-engine/src/content-review';
const read=(name:string)=>JSON.parse(readFileSync(new URL(`../../../content/drafts/${name}.json`,import.meta.url),'utf8'));
const expansion=read('general-expansion');
// The existing editorial validator expects a >=30-target, multi-format catalog.
// Validate the pilot together with existing drafts without changing that policy.
const existing=[...read('initial-30').targets,...read('b2-basics').targets];
const reviewed=validateDraftCatalog({...expansion,targets:[...existing,...expansion.targets]});
const existingPrompts=new Set(existing.flatMap(t=>t.variants.map((v:any)=>v.exercise.prompt.normalize('NFC').trim())));
const seen=new Set<string>();
const lines=['# General German expansion pilot — 28 October 2026','',
  '20 AI-authored questions: 10 B1 and 10 B2, covering 10 targets. All editorial reviews are pending. Level labels are authoring intentions, not certified assessments. Publication is not approved. No live database or learner bundle is changed.','',
  'Check grammar, naturalness, every distractor, alternate answers, hint usefulness, explanation, level and context variety. Structural validation grades every accepted answer; it does not establish editorial correctness.',''];
for(const t of expansion.targets){
  lines.push(`## ${t.title.de} (${t.level})`,'',`Target: ${t.id}`,`Content SHA-256: ${reviewed.targetHashes.get(t.id)}`,'',`Review: ${t.review.status}`,'');
  for(const v of t.variants){
    const prompt=v.exercise.prompt.normalize('NFC').trim();
    if(existingPrompts.has(prompt)||seen.has(prompt))throw Error('Duplicate prompt in expansion');
    seen.add(prompt);
    const slot=v.exercise.slots[0];
    const answer=v.rubric.acceptedAnswers[0].selections[0].optionId;
    lines.push(`### ${v.exercise.id}@${v.exercise.revision}`,'',prompt,'',
      `Choices: ${slot.options.map((o:any)=>o.text).join(' / ')}`,
      `Accepted answer: ${slot.options.find((o:any)=>o.id===answer).text}`,'',
      v.rubric.explanation.de,'',v.rubric.explanation.en,'',`Ambiguity/review: ${v.ambiguityNotes}`,'');
  }
}
writeFileSync(new URL('../../../content/drafts/GENERAL-EXPANSION-REVIEW.md',import.meta.url),lines.join('\n'));
console.log(`Expansion validation passed: ${expansion.targets.length} targets / ${seen.size} variants; ${expansion.targets.filter((t:any)=>t.review.status==='approved').length} approved; unpublished.`);

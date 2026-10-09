import { readFileSync, writeFileSync } from 'node:fs';
import { validateDraftCatalog } from '../../../packages/learning-engine/src/content-review';

const {catalog, targetHashes, targets, variants, editoriallyApproved} = validateDraftCatalog(JSON.parse(
  readFileSync(new URL('../../../content/drafts/initial-30.json',import.meta.url),'utf8')));
const lines = ['# Initial German-language review workbook', '',
  'Generated from initial-30.json. GPT-6 AI-editorially approved drafts with target content hashes. Validation is structural evidence and not human certification.', '',
  `Targets: ${targets}; variants: ${variants}; AI-editorially approved: ${editoriallyApproved}.`, '',
  'For each target, check objective, B1/B2 suitability, grammar, naturalness, ambiguity, all accepted alternatives, distractors, hint leakage, explanation, context variation and provenance. Record reviewer, date, approved/changes-requested status and checklist findings in the JSON. Publication remains separately authorized.', ''];
for (const target of catalog.targets) {
  lines.push(`## ${target.title.de}`, '', `Target: ${target.id} · ${target.level} · ${target.category}`, '',
    target.objective, '', `Review: ${target.review.status}; reviewer: ${target.review.reviewer ?? 'pending'}`, '', `Content SHA-256 for sign-off: ${targetHashes.get(target.id)}`, '', target.provenance, '');
  for (const v of target.variants) lines.push(`### ${v.variantKey}`, '',
    `Revision: ${v.exercise.id}@${v.exercise.revision}; context: ${v.contextKey}; transfer: ${v.transferKey ?? 'none'}`, '',
    v.exercise.prompt, '', v.exercise.instruction.de, '',
    '```json', JSON.stringify(v.exercise,null,2), '```', '',
    'Accepted answers (editorial only):', '', '```json', JSON.stringify(v.rubric.acceptedAnswers,null,2), '```','',
    v.rubric.explanation.de, '', `Ambiguity: ${v.ambiguityNotes}`, '');
}
writeFileSync(new URL('../../../content/drafts/REVIEW.md',import.meta.url),lines.join('\n').trimEnd()+'\n');
console.log(`Draft validation passed: ${targets} targets / ${variants} variants; ${editoriallyApproved} AI-editorially approved. No publication.`);

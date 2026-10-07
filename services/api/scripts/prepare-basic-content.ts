import { readFileSync, writeFileSync, mkdirSync } from 'node:fs';
import { PGlite } from '@electric-sql/pglite';
import { FoundationStore } from '../src/store';
import { runtimeMembers, RuntimeCatalogSchema } from '../src/runtime-catalog';
import { buildBasicCandidate, basicContentHash, type BasicPublicationAuthorization } from '../src/basic-content';
import previousJson from '../../../content/production/starter-catalog.json';
import { validateDraftCatalog } from '../../../packages/learning-engine/src/content-review';

// Isolated in-memory database only. Never reads credentials or touches production.
const read = (path:string) => readFileSync(new URL(path,import.meta.url),'utf8');
const b1=JSON.parse(read('../../../content/drafts/initial-30.json'));
const b2=JSON.parse(read('../../../content/drafts/b2-basics.json'));
const input={...b1,targets:[...b1.targets,...b2.targets]};
const previous=RuntimeCatalogSchema.parse(previousJson);
const publishPath=process.argv[2];
const publication=publishPath?JSON.parse(readFileSync(publishPath,'utf8')) as BasicPublicationAuthorization:undefined;
const db=new PGlite();
try {
  await new FoundationStore(db).initialize();
  await db.exec(read('../../../db/seed/production-starter.sql'));
  const candidate=buildBasicCandidate(input,previous,await runtimeMembers(db,previous.releaseId),publication);
  await db.exec(candidate.sql);
  const directory=new URL('../../../content/candidates/basics/',import.meta.url);
  mkdirSync(directory,{recursive:true});
  writeFileSync(publication?new URL('../../../content/production/basics-catalog.json',import.meta.url):new URL('catalog.json',directory),JSON.stringify(candidate.config,null,2)+'\n');
  writeFileSync(publication?new URL('../../../db/seed/production-basics.sql',import.meta.url):new URL('seed.sql',directory),candidate.sql);
  const reviewed=validateDraftCatalog(input);
  const lines=['# Basic practice candidate: editorial review','',
    '65 targets / 125 exercises: existing five B1 exercises, 60 prepared ChatGPT B1 exercises, 60 new Codex B2 exercises. This workbook describes source review drafts; independent German review remains pending. The separately owner-authorized live release is recorded in docs/operations/basic-content-release.md.', '',
    'The B2 label selects a scaffolded practice pack for B2 learners; it is not a claim that every individual form is exclusive to B2 or that this assesses proficiency.', '',
    'Check naturalness, requested form, alternatives, hint leakage, explanation, level suitability and context variation. Record independent sign-off in the source JSON with its matching content hash. AI editorial work is not human approval. The SQL installs only drafts in an isolated review database; do not use it to activate production.', '',
    '## Additions', ''];
  for(const t of reviewed.catalog.targets) {
    lines.push(`### ${t.level}: ${t.title.de}`, '', `Target: ${t.id}; category: ${t.category}; review: ${t.review.status}.`,
      '', `Content hash: ${reviewed.targetHashes.get(t.id)}`, '', t.provenance, '');
    for(const v of t.variants) lines.push(`**${v.variantKey}** — ${v.exercise.prompt}`, '',
      `Accepted: \`${JSON.stringify(v.rubric.acceptedAnswers)}\``, '',
      `Hint: ${v.exercise.hint.de}`, '', `Explanation: ${v.rubric.explanation.de}`, '', v.ambiguityNotes, '');
  }
  writeFileSync(new URL('REVIEW.md',directory),lines.join('\n')+'\n');
  console.log(`Prepared ${publication?'owner-authorized release':'unpublished candidate'}: ${candidate.config.targets.length} targets / ${candidate.members.length} revisions. Existing five revisions retained. Production configuration untouched.`);
  console.log(`Authorization content hash: ${basicContentHash(input,previous)}`);
} finally {await db.close();}

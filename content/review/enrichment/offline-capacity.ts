/** In-memory contract probe only. Produces no installable bundle or SQL. */
import { readFileSync, readdirSync, writeFileSync } from 'node:fs';
import { createHash } from 'node:crypto';
import { resolve, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';
import { SessionSchema } from '@german-master/contracts';

const here = dirname(fileURLToPath(import.meta.url));
const root = resolve(here, '../../..');
const snapshot = process.argv[2];
if (!/^[a-f0-9]{40}$/.test(snapshot ?? '')) throw Error('Supply a full previously validated commit SHA');
const source = resolve(root, '.local/enrichment-validation', snapshot, 'content/enrichment/batches');
const hash = (value: unknown) => createHash('sha256').update(JSON.stringify(value)).digest('hex');
const uuid = (value: string) => {
  const s = createHash('sha256').update(value).digest('hex');
  return `${s.slice(0,8)}-${s.slice(8,12)}-4${s.slice(13,16)}-8${s.slice(17,20)}-${s.slice(20,32)}`;
};
const levels = new Map<string, Array<{batchId:string; variantKey:string; exercise:any}>>();
const categories = new Map<string, number>();
for (const name of readdirSync(source).filter(n => n.endsWith('.catalog.json')).sort()) {
  const batchId = name.replace('.catalog.json','');
  const report = JSON.parse(readFileSync(resolve(source, `${batchId}.report.json`),'utf8'));
  if (report.state !== 'ready-for-review') continue;
  const catalog = JSON.parse(readFileSync(resolve(source, name),'utf8'));
  for (const target of catalog.targets) {
    categories.set(target.category, (categories.get(target.category) ?? 0) + 1);
    for (const variant of target.variants) {
      const items = levels.get(target.level) ?? [];
      items.push({batchId, variantKey:variant.variantKey, exercise:variant.exercise});
      levels.set(target.level, items);
    }
  }
}
const pages: Array<Record<string, unknown>> = [];
const allIds = new Set<string>();
let oversizedRejected = 0;
for (const [level, items] of [...levels].sort(([a],[b])=>a.localeCompare(b))) {
  const release = uuid(`validation-only:${snapshot}`);
  const makeSession = (slice: typeof items, page:number) => ({apiVersion:'v2',
    id:uuid(`validation-only:${snapshot}:${level}:${page}`), contentReleaseId:release,
    questions:slice.map(item=>({id:item.exercise.id, exercise:item.exercise}))});
  if (items.length > 125) {
    if (SessionSchema.safeParse(makeSession(items.slice(0,126), -1)).success)
      throw Error('Expected current 125-question contract bound');
    oversizedRejected++;
  }
  for (let start=0; start<items.length; start+=125) {
    const slice = items.slice(start,start+125), page = start/125;
    SessionSchema.parse(makeSession(slice,page));
    for (const item of slice) {
      if (allIds.has(item.exercise.id)) throw Error(`Duplicate exercise identity ${item.exercise.id}`);
      allIds.add(item.exercise.id);
    }
    pages.push({level, page, questionCount:slice.length,
      referencesSha256:hash(slice.map(i=>[i.batchId,i.variantKey,i.exercise.id,i.exercise.revision])),
      firstVariant:slice[0].variantKey, lastVariant:slice.at(-1)!.variantKey,
      mappingStatus:'unassigned-topic-and-context', installable:false});
  }
}
const evidence = {snapshot, active:false, publicationApproved:false, installable:false,
  purpose:'In-memory SessionSchema capacity probe; these level-only pages are not learner sessions or final packs.',
  questionsChecked:allIds.size, pagesChecked:pages.length, maxQuestionsPerSavedPayload:125,
  oversized126QuestionPayloadsRejected:oversizedRejected,
  sourceCategories:Object.fromEntries(categories), pages,
  integrationBlockers:[
    'Assign reviewed topic and general/work context, then select or page within the actual learner focus.',
    'The current basic candidate builder rejects category=vocabulary; its lexical/grammar classification also needs an explicit policy for this category.',
    'Candidate builder defaults reuse the basics release identity; a new additive immutable release needs its own identity and preserved existing revisions.',
    'Finish semantic review and apply/recheck inactive correction overlays before producing candidate SQL or a shared client bundle.',
    'Validate any future queue and pack persistence on both web and Android; this source contract probe does not prove UI lifecycle behavior.'
  ]};
writeFileSync(resolve(here,`offline-capacity-${snapshot.slice(0,12)}.json`),JSON.stringify(evidence,null,2)+'\n');
console.log(JSON.stringify({snapshot,questions:allIds.size,pages:pages.length,oversizedRejected,installable:false}));

import { PGlite } from '@electric-sql/pglite';
import { readFile } from 'node:fs/promises';
import { foundationCatalog } from '../src/catalog';
import metadata from '../../../content/foundation/metadata.json';
import editorial from '../../../content/foundation/review.json';
import { runtimeManifestHash, runtimeMembers } from '../src/runtime-catalog';

const production = process.argv.includes('--production');
const remap = (id: string) => id.replace(/^00000000-/, production ? '30000000-' : '20000000-');
function mapped(value: unknown): any {
  if (Array.isArray(value)) return value.map(mapped);
  if (value && typeof value === 'object') return Object.fromEntries(Object.entries(value).map(([key, child]) => [key, mapped(child)]));
  return typeof value === 'string' ? remap(value) : value;
}
const literal = (value: unknown) => value === null ? 'NULL' : typeof value === 'number' ? String(value)
  : `'${(typeof value === 'object' ? JSON.stringify(value) : String(value)).replaceAll("'", "''")}'`;
const db = new PGlite();
try {
  await db.exec(await readFile(new URL('../../../db/baseline/v2.sql', import.meta.url), 'utf8'));
  const { session, rubrics } = foundationCatalog();
  const releaseId = remap(session.contentReleaseId);
  const targets = metadata.targets.map(target => {
    const source = editorial.targets.find(t => t.id === target.id)!;
    return { ...mapped(target), topicId: remap(source.topicId), level: source.level as 'B1' | 'B2' };
  });
  const topics = [...new Set(editorial.targets.map(t => t.topicId))];
  for (const topic of topics) await db.query('INSERT INTO gm.topic VALUES ($1,$2)', [remap(topic), {en:'German at work',de:'Deutsch im Beruf'}]);
  for (const target of editorial.targets) {
    await db.query('INSERT INTO gm.skill VALUES ($1,$2,$3,NULL)', [remap(target.skillId), remap(target.topicId), target.objective]);
    await db.query("INSERT INTO gm.learning_target VALUES ($1,$2,$3,$4,$5,'published')", [remap(target.id), remap(target.skillId), target.kind, target.level, target.objective]);
  }
  for (const question of session.questions) {
    const exercise = mapped(question.exercise);
    const target = editorial.targets.find(t => t.exerciseId === question.exercise.id)!;
    await db.query('INSERT INTO gm.exercise VALUES ($1,$2)', [exercise.id, exercise.targetId]);
    const rubric = rubrics.get(`${question.exercise.id}@${question.exercise.revision}`)!;
    await db.query('INSERT INTO gm.exercise_revision VALUES ($1,$2,$3,$4,$5,$6,$7,$8)', [exercise.id, exercise.revision, exercise.type, exercise, rubric,
      rubric.normalizationVersion, production
        ? 'Original German Master renovation starter content; agent editorial checks of grammar and rubrics; owner-authorized production-first release, 6 October 2026. Independent German review remains pending.'
        : 'Disposable staging engineering catalog; original renovation examples; agent editorial checks; owner-authorized staging use only; NOT independent German approval or pilot content.', 'approved']);
    await db.query('INSERT INTO gm.revision_evidence_identity VALUES ($1,$2,$3,$4,$5)', [exercise.id, exercise.revision, target.evidenceIdentity.variantKey, target.evidenceIdentity.contextKey, target.evidenceIdentity.transferKey]);
  }
  await db.query("INSERT INTO gm.content_release VALUES ($1,'published',$2,'2026-10-06T12:00:00Z')", [releaseId, '0'.repeat(64)]);
  for (const question of session.questions) await db.query('INSERT INTO gm.content_release_exercise VALUES ($1,$2,$3)', [releaseId, remap(question.exercise.id), question.exercise.revision]);
  const manifestHash = runtimeManifestHash(releaseId, targets, await runtimeMembers(db, releaseId));
  await db.query('UPDATE gm.content_release SET manifest_hash=$2 WHERE id=$1', [releaseId, manifestHash]);
  const statements = [production ? '-- Product starter catalog for zgmyrpzwgtydwlzponih. Agent review; independent review pending. No learner or identity data.'
    : '-- Staging engineering content only. Run once on zgmyrpzwgtydwlzponih; no learner or identity data.', 'BEGIN;'];
  for (const table of ['topic', 'skill', 'learning_target', 'exercise', 'exercise_revision', 'content_release', 'content_release_exercise', 'revision_evidence_identity']) {
    for (const row of (await db.query<Record<string, unknown>>(`SELECT * FROM gm.${table}`)).rows) {
      statements.push(`INSERT INTO gm.${table} (${Object.keys(row).map(key => `"${key}"`).join(',')}) VALUES (${Object.values(row).map(literal).join(',')});`);
    }
  }
  statements.push('COMMIT;');
  console.log(JSON.stringify({ catalog: { releaseId, manifestHash, targets }, sql: statements.join('\n') + '\n' }));
} finally { await db.close(); }

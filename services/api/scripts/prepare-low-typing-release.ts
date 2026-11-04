import { createHash } from 'node:crypto';
import { readFileSync, writeFileSync, mkdirSync } from 'node:fs';
import { PGlite } from '@electric-sql/pglite';
import { SessionSchema, type Exercise } from '@german-master/contracts';
import { grade, type Rubric } from '@german-master/learning-engine';
import { FoundationStore } from '../src/store';
import { RuntimeCatalogSchema, runtimeMembers, runtimeManifestHash, type RuntimeMember } from '../src/runtime-catalog';

const root = 'content/candidates/low-typing-conversion/';
const previousDir = 'content/candidates/enrichment-wave-3/';
const releaseId = '93000000-0000-4000-8000-000000000001';
const approvedAt = '2026-11-05T00:00:00Z';
const legacyTypes = new Set(['short_answer', 'cloze', 'multi_slot']);
const selectionTypes = new Set(['choice', 'gap_choice', 'word_order', 'matching']);
const read = (path: string) => JSON.parse(readFileSync(path, 'utf8'));
const sha = (s: string) => createHash('sha256').update(s.replaceAll('\r\n', '\n')).digest('hex');
const ensure = (condition: unknown, message: string) => { if (!condition) throw Error('Low-typing release: ' + message); };
const quote = (value: unknown): string => value === null ? 'NULL'
  : "'" + (typeof value === 'object' ? JSON.stringify(value) : String(value)).replaceAll("'", "''") + "'";
const insert = (table: string, fields: string[], values: unknown[]) =>
  'INSERT INTO gm.' + table + ' (' + fields.join(',') + ') VALUES (' + values.map(quote).join(',') + ');';

type Conversion = {
  exerciseId: string;
  targetId: string;
  previous: { type: string; revision: number; prompt: string };
  next: { exercise: Exercise; rubric: Rubric };
  source: { path: string; review: { status: string; notes?: string; reviewedHash?: string } };
};

export async function prepareLowTypingRelease() {
  const input = read(root + 'draft.json');
  const conversions = input.conversions as Conversion[];
  const prev = RuntimeCatalogSchema.parse(read(previousDir + 'catalog.json'));
  ensure(prev.releaseId === '92000000-0000-4000-8000-000000000001', 'Wrong source release');
  ensure(input.status === 'draft' && input.publicationApproved === false, 'The staged input must remain an unpublished source');
  ensure(conversions.length === 103 && new Set(conversions.map(c => c.exerciseId)).size === 103, 'Wrong conversion inventory');
  const converted = new Map(conversions.map(c => [c.exerciseId, c]));
  const db = new PGlite();
  try {
    await new FoundationStore(db).initialize();
    for (const path of [
      'db/seed/production-starter.sql',
      'db/seed/production-basics.sql',
      'content/candidates/enrichment-wave-1/seed.sql',
      'content/candidates/enrichment-wave-2/seed.sql',
      'content/candidates/enrichment-wave-3/seed.sql',
    ]) await db.exec(readFileSync(path, 'utf8'));
    const oldMembers = await runtimeMembers(db, prev.releaseId);
    ensure(oldMembers.length === 5619, 'Unexpected previous member count');
    ensure(runtimeManifestHash(prev.releaseId, prev.targets, oldMembers) === prev.manifestHash, 'Previous release manifest mismatch');
    const oldTyped = oldMembers.filter(member => legacyTypes.has((member.payload as Exercise).type));
    ensure(oldTyped.length === 103, 'The published library contains unexpected typed revisions');
    ensure(oldTyped.every(member => converted.has(member.exercise_id)), 'Unconverted typed exercise remains');
    const writes: string[] = [
      '-- Immutable low-typing selection release; no UPDATE or DELETE of existing learner, exercise, or historical release rows.',
      '-- Use only after comparing the previous release manifest and verifying the matching source revision hashes.',
      'BEGIN;',
    ];
    const newMembers = oldMembers.map(member => {
      const c = converted.get(member.exercise_id);
      if (!c) return member;
      const old = member.payload as Exercise;
      ensure(old.id === c.exerciseId && old.targetId === c.targetId, 'Exercise/target mismatch ' + c.exerciseId);
      ensure(member.revision === c.previous.revision && old.type === c.previous.type && old.prompt === c.previous.prompt,
        'Previous revision changed ' + c.exerciseId);
      const e = c.next.exercise, rubric = c.next.rubric;
      ensure(e.id === old.id && e.targetId === old.targetId && e.revision > old.revision, 'New revision identity mismatch ' + e.id);
      ensure(selectionTypes.has(e.type) && e.type === (rubric.acceptedAnswers[0] as any).type, 'Unexpected input type ' + e.id);
      ensure(c.source.review.status === 'approved-ai', 'Review pending ' + e.id);
      ensure(rubric.acceptedAnswers.length > 0, 'Missing answer ' + e.id);
      for (const answer of rubric.acceptedAnswers) {
        ensure(grade(e, rubric, answer, []).outcome === 'correct', 'Ungradeable answer ' + e.id);
      }
      const provenance = member.provenance + ' Immutable GPT-6 AI-reviewed low-typing conversion, 2026-11-05; '
        + c.source.path + '; recognition-based response, not unrestricted written production. Prior revision retained.';
      writes.push(insert('exercise_revision',
        ['exercise_id', 'revision', 'type', 'payload', 'rubric', 'normalization_version', 'provenance', 'review_status'],
        [e.id, e.revision, e.type, e, rubric, rubric.normalizationVersion, provenance, 'approved']));
      writes.push(insert('revision_evidence_identity',
        ['exercise_id', 'revision', 'variant_key', 'context_key', 'transfer_key'],
        [e.id, e.revision, member.variant_key, member.context_key + '-low-typing-' + e.revision, null]));
      return { ...member, revision: e.revision, payload: e, rubric, provenance,
        normalization_version: rubric.normalizationVersion, review_status: 'approved',
        context_key: member.context_key + '-low-typing-' + e.revision, transfer_key: null } as RuntimeMember;
    });
    const hash = runtimeManifestHash(releaseId, prev.targets, newMembers);
    const config = RuntimeCatalogSchema.parse({ releaseId, manifestHash: hash, targets: prev.targets });
    writes.push(insert('content_release', ['id', 'status', 'manifest_hash', 'published_at'],
      [releaseId, 'published', hash, approvedAt]));
    for (const m of newMembers) writes.push(insert('content_release_exercise',
      ['release_id', 'exercise_id', 'revision'], [releaseId, m.exercise_id, m.revision]));
    writes.push('COMMIT;', '');
    const sql = writes.join('\n');
    await db.exec(sql);
    const actual = await runtimeMembers(db, releaseId);
    ensure(actual.length === oldMembers.length, 'New release row count mismatch');
    ensure(runtimeManifestHash(releaseId, prev.targets, actual) === hash, 'Generated manifest differs from actual SQL rows');
    ensure(JSON.stringify(await runtimeMembers(db, prev.releaseId)) === JSON.stringify(oldMembers), 'Previous release mutated');
    ensure(actual.every(member => !legacyTypes.has((member.payload as Exercise).type)), 'Typing remains in published release');

    const bundle = read(previousDir + 'offline.json');
    let patched = 0;
    const seen = new Set<string>();
    for (const pack of bundle.packs) {
      const map = new Map<string, Conversion>();
      for (const q of pack.session.questions) {
        const c = converted.get(q.exercise.id);
        if (!c) continue;
        ensure(q.exercise.revision === c.previous.revision, 'Offline revision mismatch ' + c.exerciseId);
        q.exercise = c.next.exercise;
        map.set(c.exerciseId, c);
        seen.add(c.exerciseId);
        patched++;
      }
      pack.rubrics = pack.rubrics.map((rubric: any) => {
        const c = map.get(rubric.exerciseId);
        return c ? { exerciseId: c.exerciseId, exerciseRevision: c.next.exercise.revision, ...c.next.rubric } : rubric;
      });
      pack.session.contentReleaseId = releaseId;
      SessionSchema.parse(pack.session);
      ensure(pack.session.questions.length <= 125, 'Offline pack too large');
    }
    ensure(seen.size === 103, 'Offline bundle did not cover all converted questions');
    ensure(patched === 103, 'Offline conversion repeats an exercise across packs');
    ensure(bundle.packs.every((p: any) => p.session.questions.every((q: any) => !legacyTypes.has(q.exercise.type))), 'Typed pack remains');
    const offline = JSON.stringify(bundle, null, 2) + '\n';
    const catalog = JSON.stringify(config, null, 2) + '\n';
    const manifest = {
      schemaVersion: 1, releaseId, previousReleaseId: prev.releaseId,
      previousManifestHash: prev.manifestHash, manifestHash: hash,
      memberCount: newMembers.length, convertedQuestions: conversions.length,
      unchangedQuestionCount: newMembers.length - conversions.length,
      editorialRecheck: { status: 'approved-ai', starterQuestions: 3,
        priorAiReviewedQuestions: 100, promptEdits: conversions.filter(c => c.previous.prompt !== c.next.exercise.prompt).length },
      reviewedDraftSha256: sha(readFileSync(root + 'draft.json', 'utf8')),
      seedSha256: sha(sql), offlineSha256: sha(offline), catalogSha256: sha(catalog),
      oldRevisionsPreserved: true, noTypedAnswers: true,
      contentReleaseAt: approvedAt,
      productionDeployed: false, androidPlayPublication: false,
    };
    mkdirSync(root, { recursive: true });
    writeFileSync(root + 'seed.sql', sql);
    writeFileSync(root + 'catalog.json', catalog);
    writeFileSync(root + 'offline.json', offline);
    writeFileSync(root + 'manifest.json', JSON.stringify(manifest, null, 2) + '\n');
    writeFileSync('content/production/catalog.json', catalog);
    writeFileSync('content/guest/practice.json', offline);
    console.log(JSON.stringify({
      releaseId, old: oldMembers.length, converted: conversions.length,
      new: actual.length, patched, digest: hash,
      offlineBytes: offline.length, sqlBytes: sql.length,
    }));
    return manifest;
  } finally {
    await db.close();
  }
}
if (process.argv[1]?.replaceAll('\\', '/').endsWith('/prepare-low-typing-release.ts')) {
  await prepareLowTypingRelease();
}

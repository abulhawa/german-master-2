import { expect, it } from 'vitest';
import { readFileSync } from 'node:fs';
import { createHash } from 'node:crypto';
import { SessionSchema } from '@german-master/contracts';
import { prepareLowTypingRelease } from '../scripts/prepare-low-typing-release';

const read = (path: string) => readFileSync(path, 'utf8');
const json = (path: string) => JSON.parse(read(path));
const hash = (value: string) => createHash('sha256').update(value.replaceAll('\r\n', '\n')).digest('hex');

it('generates one immutable, selection-only new release; preserves pinned previous release and saved-round bounds', async () => {
  const manifest = await prepareLowTypingRelease();
  const root = 'content/candidates/low-typing-conversion/';
  expect(manifest.convertedQuestions).toBe(103);
  expect(manifest.memberCount).toBe(5619);
  expect(manifest.oldRevisionsPreserved).toBe(true);
  expect(manifest.noTypedAnswers).toBe(true);
  const offline = json(root + 'offline.json');
  const catalog = json(root + 'catalog.json');
  expect(catalog.releaseId).toBe(manifest.releaseId);
  expect(catalog.manifestHash).toBe(manifest.manifestHash);
  expect(json('content/guest/practice.json')).toEqual(offline);
  expect(json('content/production/catalog.json')).toEqual(catalog);
  expect(hash(read(root + 'offline.json'))).toBe(manifest.offlineSha256);
  expect(hash(read(root + 'seed.sql'))).toBe(manifest.seedSha256);
  expect(hash(read(root + 'catalog.json'))).toBe(manifest.catalogSha256);
  expect(hash(read(root + 'draft.json'))).toBe(manifest.reviewedDraftSha256);
  const types = new Set<string>();
  for (const pack of offline.packs) {
    SessionSchema.parse(pack.session);
    expect(pack.session.contentReleaseId).toBe(manifest.releaseId);
    expect(pack.session.questions.length).toBeLessThanOrEqual(125);
    const rubrics = new Map(pack.rubrics.map((r: any) => [r.exerciseId, r]));
    for (const q of pack.session.questions) {
      types.add(q.exercise.type);
      expect(rubrics.has(q.exercise.id)).toBe(true);
    }
  }
  expect(types.has('short_answer')).toBe(false);
  expect(types.has('cloze')).toBe(false);
  expect(types.has('multi_slot')).toBe(false);
}, 120000);

import { it, expect } from 'vitest';
import { PGlite } from '@electric-sql/pglite';
import { randomUUID } from 'node:crypto';
import { mkdtemp, rm } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import type { AddressInfo } from 'node:net';
import { TargetPageSchema, SyncPageSchema, type SessionRequest, type Attempt } from '@german-master/contracts';
import { FoundationStore } from './store';
import { createApi } from './server';
import targetFixture from '../../../contracts/v2/examples/target-page.json';
import syncFixture from '../../../contracts/v2/examples/sync-page.json';

it('validates shared transport fixtures and rejects invalid instants and client fields', () => {
  expect(TargetPageSchema.parse(targetFixture).targets[0].state).toBe('needs_practice');
  expect(SyncPageSchema.parse(syncFixture).changes[0].target).toEqual(targetFixture.targets[0]);
  for (const generatedAt of ['2026-02-30T12:00:00Z', '2026-10-04T12:00:00+02:00', 'tomorrow'])
    expect(TargetPageSchema.safeParse({ ...targetFixture, generatedAt }).success).toBe(false);
  expect(TargetPageSchema.safeParse({ ...targetFixture, pending: true }).success).toBe(false);
  expect(TargetPageSchema.safeParse({ ...targetFixture, targets: [{ ...targetFixture.targets[0], state: 'due' }] }).success).toBe(false);
});

const user = randomUUID(), other = randomUUID();
const request = (): SessionRequest => ({ apiVersion: 'v2', requestId: randomUUID(), questionCount: 1, capabilities: ['short_answer@1'] });
async function answer(store: FoundationStore, now: string, owner = user) {
  const q = (await store.createSession(owner, request())).questions[0];
  const input: Attempt = { attemptId: randomUUID(), deviceId: randomUUID(), clientSequence: 1,
    sessionQuestionId: q.id, exerciseRevision: q.exercise.revision,
    answer: { type: 'short_answer', text: 'wrong' }, assistance: [], answeredAt: now };
  expect((await store.submit(owner, input, randomUUID())).status).toBe('accepted');
  return input;
}

it('freezes snapshot pagination, bridges concurrent ingestion and refreshes due only in fresh reads', async () => {
  const db = new PGlite(); let now = '2026-10-04T12:00:00.123Z';
  const store = new FoundationStore(db, () => new Date(now));
  try {
    await store.initialize();
    const input = await answer(store, now);
    const first = await store.targets(user, 1);
    expect(first.targets[0]).toMatchObject({ state: 'needs_practice', isDue: false, lastSequence: 1 });
    const storedBefore = (await db.query('SELECT * FROM gm.learner_target_state')).rows;
    const deltaBefore = await store.sync(user);
    now = new Date(Date.parse(first.targets[0].schedule[0].dueAt) - 1).toISOString();
    expect((await store.targets(user)).targets[0].isDue).toBe(false);
    now = first.targets[0].schedule[0].dueAt;
    const fresh = await store.targets(user);
    expect(fresh.targets[0]).toMatchObject({ state: 'needs_practice', isDue: true });
    expect((await db.query('SELECT * FROM gm.learner_target_state')).rows).toEqual(storedBefore);
    expect(await store.sync(user)).toEqual(deltaBefore);
    expect(deltaBefore.changes[0].target.isDue).toBe(false);
    expect((await store.submit(user, input, randomUUID())).status).toBe('duplicate');
    expect(await store.sync(user, 50, first.syncCursor)).toMatchObject({ changes: [], hasMore: false, nextCursor: first.syncCursor });
    await answer(store, now);
    const during = await store.sync(user, 50, first.syncCursor);
    expect(during.changes).toHaveLength(1);
    const ids = first.targets.map(t => t.targetId);
    let cursor = first.nextPageCursor;
    while (cursor) {
      const page = await store.targets(user, 100, cursor);
      expect(await store.targets(user, 1, cursor)).toEqual(page);
      expect(page.generatedAt).toBe(first.generatedAt);
      expect(page.syncCursor).toBe(first.syncCursor);
      expect(page.targets[0]).toMatchObject({ state: 'new', exposureCount: 0 });
      ids.push(...page.targets.map(t => t.targetId)); cursor = page.nextPageCursor;
    }
    expect(ids).toHaveLength(5); expect(new Set(ids).size).toBe(5);
    expect(JSON.stringify(fresh)).not.toMatch(/learnerId|rubric|answer|pending|decisions/);
    const q = (await store.createSession(user, request())).questions[0];
    const [racingSnapshot, accepted] = await Promise.all([
      store.targets(user),
      store.submit(user, { ...input, attemptId: randomUUID(), sessionQuestionId: q.id, answeredAt: now }, randomUUID()),
    ]);
    expect(accepted.status).toBe('accepted');
    const tail = await store.sync(user, 50, racingSnapshot.syncCursor);
    expect(racingSnapshot.targets[0].exposureCount + tail.changes.length).toBe(3);
  } finally { await db.close(); }
});

it('orders sparse owned deltas, rejects foreign/wrong-kind cursors and survives close/reopen', async () => {
  const directory = await mkdtemp(join(tmpdir(), 'gm-reads-'));
  let db = new PGlite(directory);
  const now = '2026-10-04T12:00:00Z'; let store = new FoundationStore(db, () => new Date(now));
  try {
    await store.initialize(); const baseline = await store.targets(user, 2);
    await answer(store, now); await answer(store, now, other);
    const q = (await store.createSession(user, request())).questions[0];
    expect((await store.expose(user, { eventId: randomUUID(), deviceId: randomUUID(), sessionQuestionId: q.id,
      exerciseRevision: 1, disposition: 'skip', occurredAt: now }, randomUUID())).status).toBe('accepted');
    const first = await store.sync(user, 1, baseline.syncCursor);
    expect(first.hasMore).toBe(true); expect(first.changes.map(c => c.sequence)).toEqual([1]);
    const last = await store.sync(user, 1, first.nextCursor);
    expect(last.hasMore).toBe(false); expect(last.changes.map(c => c.sequence)).toEqual([3]);
    expect(last.changes[0].target.exposureCount).toBe(2);
    for (const cursor of [baseline.syncCursor, first.nextCursor])
      await expect(store.sync(other, 1, cursor)).rejects.toMatchObject({ code: 'invalid_cursor' });
    await expect(store.targets(other, 1, baseline.nextPageCursor)).rejects.toMatchObject({ code: 'invalid_cursor' });
    await expect(store.targets(user, 1, first.nextCursor)).rejects.toMatchObject({ code: 'invalid_cursor' });
    await expect(store.sync(user, 1, baseline.nextPageCursor)).rejects.toMatchObject({ code: 'invalid_cursor' });
    const page = await store.targets(user, 1, baseline.nextPageCursor);
    await db.close(); db = new PGlite(directory); store = new FoundationStore(db, () => new Date('2026-10-05T00:00:00Z'));
    await store.initialize(); await store.initialize();
    expect(await store.targets(user, 50, baseline.nextPageCursor)).toEqual(page);
    expect(await store.sync(user, 1, baseline.syncCursor)).toEqual(first);
    expect(await store.sync(user, 1, first.nextCursor)).toEqual(last);
    expect(await store.sync(user, 1, last.nextCursor)).toMatchObject({ changes: [], nextCursor: last.nextCursor });
    const expiredStore = new FoundationStore(db, () => new Date('2026-12-01T00:00:00Z'));
    await expect(expiredStore.sync(user, 1, baseline.syncCursor)).rejects.toMatchObject({ code: 'invalid_cursor' });
    expect(await expiredStore.targets(user, 1, baseline.nextPageCursor)).toEqual(page);
    const replacement = await expiredStore.targets(user);
    expect(replacement.syncCursor).not.toBe(last.nextCursor);
    expect(await expiredStore.sync(user, 1, replacement.syncCursor)).toMatchObject({ changes: [] });
    await expect(db.query('DELETE FROM gm.sync_cursor')).rejects.toThrow();
  } finally { await db.close(); await rm(directory, { recursive: true, force: true }); }
});

it('expires at the exact HTTP boundary, recovers a frozen snapshot and accepts pending writes once', async () => {
  const db = new PGlite(); let now = Date.parse('2026-10-04T12:00:00Z');
  const store = new FoundationStore(db, () => new Date(now), 1000);
  await store.initialize();
  const server = createApi(store, async r => r.headers.authorization === `Bearer ${user}` ? user : null);
  await new Promise<void>(resolve => server.listen(0, '127.0.0.1', resolve));
  const base = `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
  const get = (path: string) => fetch(base + path, { headers: { Authorization: `Bearer ${user}` } });
  const post = (path: string, input: unknown) => fetch(base + path, { method: 'POST',
    headers: { Authorization: `Bearer ${user}`, 'Content-Type': 'application/json' }, body: JSON.stringify(input) });
  try {
    const sessionRequest = request();
    const session = await store.createSession(user, sessionRequest);
    const q = session.questions[0];
    const pending: Attempt = { attemptId: randomUUID(), deviceId: randomUUID(), clientSequence: 1,
      sessionQuestionId: q.id, exerciseRevision: q.exercise.revision,
      answer: { type: 'short_answer', text: 'wrong' }, assistance: [], answeredAt: new Date(now).toISOString() };
    const before = TargetPageSchema.parse(await (await get('/v2/targets?limit=1')).json());
    const frozen = await (await get(`/v2/targets?cursor=${before.nextPageCursor}`)).json();
    now += 999;
    expect((await get(`/v2/sync?cursor=${before.syncCursor}`)).status).toBe(200);
    now += 1;
    const expired = await get(`/v2/sync?cursor=${before.syncCursor}`);
    expect(expired.status).toBe(400); expect((await expired.json()).code).toBe('invalid_cursor');
    expect(await (await get(`/v2/targets?cursor=${before.nextPageCursor}`)).json()).toEqual(frozen);
    const replacement = TargetPageSchema.parse(await (await get('/v2/targets?limit=1')).json());
    expect(replacement.syncCursor).not.toBe(before.syncCursor);
    expect((await post('/v2/sessions', sessionRequest)).status).toBe(200);
    const submit = () => post('/v2/attempts:batch', { apiVersion: 'v2', attempts: [pending] });
    expect((await (await submit()).json()).acknowledgments[0].status).toBe('accepted');
    // Simulate lost acknowledgment: recovery does not invalidate or rewrite the frozen submission.
    expect((await (await submit()).json()).acknowledgments[0].status).toBe('duplicate');
    let page = replacement; const targets = [...page.targets];
    while (page.nextPageCursor) {
      page = TargetPageSchema.parse(await (await get(`/v2/targets?cursor=${page.nextPageCursor}`)).json());
      expect(page.syncCursor).toBe(replacement.syncCursor);
      expect(page.generatedAt).toBe(replacement.generatedAt);
      targets.push(...page.targets);
    }
    expect(targets).toHaveLength(5); expect(targets.every(t => t.exposureCount === 0)).toBe(true);
    const tail = SyncPageSchema.parse(await (await get(`/v2/sync?cursor=${replacement.syncCursor}`)).json());
    expect(tail.changes).toHaveLength(1); expect(tail.changes[0].target.exposureCount).toBe(1);
    expect((await db.query('SELECT * FROM gm.accepted_evidence WHERE user_id=$1', [user])).rows).toHaveLength(1);
    expect((await get(`/v2/sync?cursor=${before.syncCursor}`)).status).toBe(400);
  } finally { await new Promise<void>(resolve => server.close(() => resolve())); await db.close(); }
});

it('upgrades undated legacy cursor records without changing frozen pages or evidence', async () => {
  const db = new PGlite(); const now = new Date('2026-10-04T12:00:00Z');
  const store = new FoundationStore(db, () => now);
  try {
    await store.initialize(); const snapshot = await store.targets(user, 1);
    const page = await store.targets(user, 1, snapshot.nextPageCursor);
    // Reconstruct migration-004 cursor layout in this isolated database.
    await db.exec(`ALTER TABLE gm.sync_cursor DROP COLUMN expires_at;
      ALTER TABLE gm.sync_cursor ADD UNIQUE (user_id,sequence);
      DELETE FROM gm.schema_migration WHERE version=5;`);
    await store.initialize(); await store.initialize();
    await expect(store.sync(user, 1, snapshot.syncCursor)).rejects.toMatchObject({ code: 'invalid_cursor' });
    expect(await store.targets(user, 1, snapshot.nextPageCursor)).toEqual(page);
    const fresh = await store.targets(user);
    expect(fresh.syncCursor).not.toBe(snapshot.syncCursor);
    expect((await store.sync(user, 1, fresh.syncCursor)).changes).toEqual([]);
    expect((await db.query('SELECT * FROM gm.accepted_evidence')).rows).toEqual([]);
  } finally { await db.close(); }
});

it('validates authenticated HTTP reads, pagination bounds and unknown or repeated parameters', async () => {
  const db = new PGlite(); const store = new FoundationStore(db);
  await store.initialize();
  const server = createApi(store, async r => [user, other].find(id => r.headers.authorization === `Bearer ${id}`) ?? null);
  await new Promise<void>(resolve => server.listen(0, '127.0.0.1', resolve));
  const base = `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
  const get = (path: string, token: string = user) => fetch(base + path, { headers: { Authorization: `Bearer ${token}` } });
  try {
    const response = await get('/v2/targets?limit=2');
    expect(response.status).toBe(200); expect(response.headers.get('cache-control')).toBe('no-store');
    const page = TargetPageSchema.parse(await response.json()); expect(page.targets).toHaveLength(2);
    const next = TargetPageSchema.parse(await (await get(`/v2/targets?cursor=${page.nextPageCursor}`)).json());
    expect(next.generatedAt).toBe(page.generatedAt);
    expect(SyncPageSchema.parse(await (await get(`/v2/sync?cursor=${page.syncCursor}`)).json()).changes).toEqual([]);
    await answer(store, new Date().toISOString());
    const synced = SyncPageSchema.parse(await (await get(`/v2/sync?cursor=${page.syncCursor}&limit=1`)).json());
    expect(synced.changes[0].target).toMatchObject({ state: 'needs_practice', exposureCount: 1 });
    expect(await (await get(`/v2/sync?cursor=${page.syncCursor}&limit=1`)).json()).toEqual(synced);
    expect((await get('/v2/sync', 'invalid')).status).toBe(401);
    const foreign = await get(`/v2/targets?cursor=${page.nextPageCursor}`, other);
    expect(foreign.status).toBe(400); expect((await foreign.json()).code).toBe('invalid_cursor');
    expect((await get(`/v2/sync?cursor=${page.syncCursor}`, other)).status).toBe(400);
    for (const query of ['limit=0','limit=101','limit=1.5','limit=1e1','limit=01','cursor=','cursor=no','limit=2&limit=3','userId=x',`cursor=${randomUUID()}`])
      expect((await get('/v2/targets?' + query)).status, query).toBe(400);
    expect((await get('/v2/unknown')).status).toBe(404);
    await expect(store.targets(user, 0)).rejects.toMatchObject({ code: 'invalid_request' });
  } finally { await new Promise<void>(resolve => server.close(() => resolve())); await db.close(); }
});

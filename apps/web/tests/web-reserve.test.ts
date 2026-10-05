import { afterEach, expect, it, vi } from 'vitest';
import { createHash, webcrypto, randomUUID } from 'node:crypto';
import { type PreparedPack } from '@german-master/contracts';
import { packCanonical } from '@german-master/learning-engine';
import { foundationCatalog } from '../../../services/api/src/catalog';
import { WebReserve } from '../client/src/renovation/reserve';
import { sessionRequest } from '../client/src/foundation/api';
import { emptyJourney, saveJourney, STORAGE_KEY } from '../client/src/renovation/storage';
import { OfflineRepository } from '../client/src/renovation/offline';

const caches: WebReserve[] = [];
const cache = (name = `reserve-test-${randomUUID()}`) => {
  vi.stubGlobal('crypto', webcrypto);
  const value = new WebReserve(name); caches.push(value); return value;
};
function packFor(requestId: string): PreparedPack {
  const { session, rubrics } = foundationCatalog();
  const payload = {
    apiVersion: 'v2' as const, packId: requestId, contentReleaseId: session.contentReleaseId,
    evaluatorVersion: 'deterministic-v1' as const, normalizationVersion: 'de-nfc-trim-v1' as const,
    issuedAt: '2026-10-05T10:00:00Z', expiresAt: '2026-10-12T10:00:00Z',
    sessions: [0, 1].map(() => ({ ...session, id: randomUUID(), questions: session.questions.map(q => ({ ...q, id: randomUUID() })) })),
    rubrics: session.questions.map(q => ({ exerciseId: q.exercise.id, exerciseRevision: q.exercise.revision, ...rubrics.get(`${q.exercise.id}@${q.exercise.revision}`)! })),
  };
  return { ...payload, contentHash: createHash('sha256').update(packCanonical(payload)).digest('hex') };
}
afterEach(async () => {
  for (const value of caches.splice(0)) await value.delete();
  vi.unstubAllGlobals(); localStorage.clear();
});

it('freezes before HTTP; response loss and restart replay the same request without touching learner writes', async () => {
  const first = cache(); const request = sessionRequest(); const pack = packFor(request.requestId);
  saveJourney(localStorage, emptyJourney());
  localStorage.setItem('german-master-v2:pending-test', 'unchanged profile/report writes');
  const before = localStorage.getItem(STORAGE_KEY);
  const download = vi.fn(async input => {
    expect((await first.read()).pending).toEqual(input);
    throw Error('Accepted but response lost');
  });
  await expect(first.prepare(request, download)).rejects.toThrow('response lost');
  expect(download).toHaveBeenCalledTimes(1);
  const restarted = cache(first.name); first.close();
  const retry = vi.fn(async () => pack);
  expect(await restarted.prepare(sessionRequest(), retry)).toEqual(pack);
  expect(retry).toHaveBeenCalledWith(request);
  expect(await restarted.read()).toEqual({ pending: null, pack });
  expect(localStorage.getItem(STORAGE_KEY)).toBe(before);
  expect(localStorage.getItem('german-master-v2:pending-test')).toContain('unchanged');
});

it('retains the old complete reserve and frozen request on corruption, wrong linkage and aborted replacement', async () => {
  const db = cache(); const first = sessionRequest(); const old = packFor(first.requestId);
  await db.prepare(first, async () => old);
  const next = sessionRequest(); const replacement = packFor(next.requestId);
  await db.freeze(next);
  await expect(db.accept(next, { ...replacement, expiresAt: old.issuedAt })).rejects.toThrow();
  await expect(db.accept(next, old)).rejects.toThrow('request mismatch');
  const abort = () => { throw Error('Quota/storage failure'); };
  db.table('records').hook('updating', abort);
  await expect(db.accept(next, replacement)).rejects.toThrow('Quota/storage failure');
  db.table('records').hook('updating').unsubscribe(abort);
  expect(await db.read()).toEqual({ pending: next, pack: old });
  db.close(); const restarted = cache(db.name);
  expect(await restarted.read()).toEqual({ pending: next, pack: old });
  await restarted.accept(next, replacement);
  expect(await restarted.read()).toEqual({ pending: null, pack: replacement });
});

it('serializes competing freezes and atomic replay; late responses cannot overwrite a newer request', async () => {
  const db = cache(); const a = sessionRequest(); const b = sessionRequest();
  const other = cache(db.name);
  const [frozen, competing] = await Promise.all([db.freeze(a), other.freeze(b)]);
  expect(frozen).toEqual(competing);
  const pack = packFor(frozen.requestId);
  await Promise.all([db.accept(frozen, pack), other.accept(frozen, pack)]);
  await other.freeze(b);
  await expect(db.accept(frozen, pack)).rejects.toThrow('request changed');
  expect((await db.read()).pending).toEqual(b);
  expect((await db.read()).pack).toEqual(pack);
});

it('checks cached integrity on reopen without deleting corrupt bytes or retrying the network', async () => {
  const db = cache(); const request = sessionRequest(); const pack = packFor(request.requestId);
  await db.prepare(request, async () => pack);
  const corrupted = { ...pack, contentHash: '0'.repeat(64) };
  await db.table('records').update('reserve', { pack: corrupted }); db.close();
  const restarted = cache(db.name); const download = vi.fn();
  await expect(restarted.read()).rejects.toThrow('hash mismatch');
  await expect(restarted.prepare(sessionRequest(), download)).rejects.toThrow('hash mismatch');
  expect(download).not.toHaveBeenCalled();
  expect((await restarted.table('records').get('reserve')).pack).toEqual(corrupted);
});

it('uses the original seven-day deadline and keeps the pack after exact expiry for late work', async () => {
  const db = cache(); const request = sessionRequest(); const pack = packFor(request.requestId);
  await db.prepare(request, async () => pack);
  expect((await db.status(new Date(pack.issuedAt))).available).toBe(2);
  expect((await db.status(new Date(Date.parse(pack.expiresAt) - 1))).available).toBe(2);
  expect((await db.status(new Date(pack.expiresAt))).available).toBe(0);
  expect((await db.status(new Date(Date.parse(pack.issuedAt) - 1))).available).toBe(0);
  expect((await db.read()).pack).toEqual(pack);
});

it('consumes sessions atomically across tabs, rolls back failed starts and preserves started packs across replacement/expiry', async () => {
  const db = cache(); const repo = new OfflineRepository(db); const request = sessionRequest(); const pack = packFor(request.requestId);
  await db.prepare(request, async () => pack);
  const abort = () => { throw Error('Start save failed'); };
  db.table('records').hook('updating', abort);
  await expect(repo.start(randomUUID(), new Date(pack.issuedAt))).rejects.toThrow('Start save failed');
  db.table('records').hook('updating').unsubscribe(abort);
  expect(await repo.list()).toEqual([]);
  expect((await db.status(new Date(pack.issuedAt))).available).toBe(2);
  const other = new OfflineRepository(cache(db.name));
  const starts = await Promise.all([repo.start(randomUUID(), new Date(pack.issuedAt)), other.start(randomUUID(), new Date(pack.issuedAt))]);
  expect(new Set(starts).size).toBe(2);
  expect(await repo.list()).toEqual(starts);
  expect((await repo.read(starts[0])).practice.deliveryOrder).toBeLessThan((await repo.read(starts[1])).practice.deliveryOrder);
  await expect(repo.start(randomUUID(), new Date(pack.issuedAt))).rejects.toThrow('exhausted');
  const fresh = sessionRequest(); await db.prepare(fresh, async () => packFor(fresh.requestId));
  expect((await repo.read(starts[0])).pack).toEqual(pack);
  await expect(repo.start(randomUUID(), new Date(pack.expiresAt))).rejects.toThrow('No valid');
  expect((await repo.read(starts[0])).pack).toEqual(pack);
});

it('saves provisional feedback and frozen multiple events together; restart and receipt failure preserve replay order and prompt', async () => {
  const db = cache(); const request = sessionRequest(); const pack = packFor(request.requestId);
  await db.prepare(request, async () => pack); let repo = new OfflineRepository(db);
  const id = await repo.start(randomUUID(), new Date(pack.issuedAt));
  const { session } = await repo.read(id);
  const answer = pack.rubrics.find(r => r.exerciseId === session.questions[0].exercise.id)!.acceptedAnswers[0];
  await repo.draft(id, answer, true); await repo.answer(id, answer); await repo.next(id); await repo.skip(id); await repo.end(id);
  const saved = (await repo.read(id)).practice;
  expect(saved.events.map(e => e.kind)).toEqual(['attempt', 'skip', 'completion']);
  expect(saved.events[0]).toMatchObject({ provisional: { outcome: 'correct', assisted: true }, receipt: null });
  db.close(); const restarted = cache(db.name); repo = new OfflineRepository(restarted);
  const calls: string[] = [];
  const api = {
    submit: vi.fn(async (attempt: any) => { calls.push('attempt'); return { attemptId: attempt.attemptId, status: 'duplicate' as const,
      serverSequence: 1, evaluation: { ...(saved.events[0].kind === 'attempt' ? saved.events[0].provisional : {}), outcome: 'incorrect' as const } }; }),
    expose: vi.fn(async (event: any) => { calls.push('skip'); return { eventId: event.eventId, status: 'accepted' as const, serverSequence: 2 }; }),
    complete: vi.fn(async (_: string, input: any) => { calls.push('completion'); return { apiVersion: 'v2' as const, requestId: input.requestId,
      sessionId: id, mode: input.mode, plannedCount: 5, gradedCount: 1, skippedCount: 1, correctCount: 0, completedAt: pack.expiresAt }; }),
  };
  const abort = () => { throw Error('Receipt save failed'); };
  restarted.table('practices').hook('updating', abort);
  await expect(repo.sync(id, api as any)).rejects.toThrow('Receipt save failed');
  restarted.table('practices').hook('updating').unsubscribe(abort);
  expect(calls).toEqual(['attempt']); expect((await repo.read(id)).practice).toEqual(saved);
  await repo.sync(id, api as any);
  expect(calls).toEqual(['attempt', 'attempt', 'skip', 'completion']);
  const confirmed = (await repo.read(id)).practice;
  expect(confirmed.index).toBe(saved.index); expect(confirmed.feedback).toEqual(saved.feedback);
  expect(confirmed.events[0]).toMatchObject({ provisional: { outcome: 'correct' }, receipt: { evaluation: { outcome: 'incorrect' } } });
  expect(confirmed.events.every(e => e.receipt)).toBe(true);
  expect(confirmed.events.map(e => e.request)).toEqual(saved.events.map(e => e.request));
  await repo.sync(id, api as any); expect(calls).toHaveLength(4);
});

it('a failed answer save creates neither feedback nor an outbox event; rejected delivery stops before completion', async () => {
  const db = cache(); const request = sessionRequest(); const pack = packFor(request.requestId);
  await db.prepare(request, async () => pack); const repo = new OfflineRepository(db);
  const id = await repo.start(randomUUID(), new Date(pack.issuedAt));
  const before = (await repo.read(id)).practice;
  const answer = pack.rubrics.find(r => r.exerciseId === pack.sessions[0].questions[0].exercise.id)!.acceptedAnswers[0];
  const abort = () => { throw Error('Answer save failed'); };
  db.table('practices').hook('updating', abort);
  await expect(repo.answer(id, answer)).rejects.toThrow('Answer save failed');
  db.table('practices').hook('updating').unsubscribe(abort);
  expect((await repo.read(id)).practice).toEqual(before);
  await repo.answer(id, answer); await repo.end(id);
  const api = { submit: vi.fn(async (input: any) => ({ attemptId: input.attemptId, status: 'rejected', error: {
    code: 'invalid', message: 'Rejected', requestId: randomUUID(), retryable: false } })), expose: vi.fn(), complete: vi.fn() };
  await expect(repo.sync(id, api as any)).rejects.toThrow('not accepted');
  expect(api.complete).not.toHaveBeenCalled();
  expect((await repo.read(id)).practice.events[0].receipt).toMatchObject({status:'rejected'});
  expect((await repo.read(id)).practice.events[1].receipt).toBeNull();
  await expect(repo.sync(id, api as any)).rejects.toThrow('requires review');
  expect(api.submit).toHaveBeenCalledTimes(1);
});

it('preserves and blocks a corrupted saved event rather than uploading or discarding it', async () => {
  const db = cache(); const request = sessionRequest(); const pack = packFor(request.requestId);
  await db.prepare(request, async () => pack); const repo = new OfflineRepository(db);
  const id = await repo.start(randomUUID(), new Date(pack.issuedAt));
  const q = (await repo.read(id)).session.questions[0];
  await repo.answer(id, pack.rubrics.find(r => r.exerciseId === q.exercise.id)!.acceptedAnswers[0]);
  const raw = await db.table('practices').get(id);
  raw.events[0].request.sessionQuestionId = randomUUID(); await db.table('practices').put(raw);
  const api = { submit: vi.fn(), expose: vi.fn(), complete: vi.fn() };
  await expect(repo.sync(id, api)).rejects.toThrow('linkage mismatch');
  expect(api.submit).not.toHaveBeenCalled();
  expect(await db.table('practices').get(id)).toEqual(raw);
});

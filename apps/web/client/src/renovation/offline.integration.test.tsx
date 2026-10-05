import { expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { PGlite } from '@electric-sql/pglite';
import { randomUUID } from 'node:crypto';
import { syncSavedWork } from './sync';
import { PROFILE_PENDING_KEY } from './setup';
import { REPORT_KEY } from './report';
import { emptyJourney, readJourney, saveJourney } from './storage';
import { prepareAttempt } from '../foundation/api';
import type { AddressInfo } from 'node:net';
import { FoundationStore } from '../../../../../services/api/src/store';
import { createApi } from '../../../../../services/api/src/server';
import { localLearnerApi } from './api';
import { WebReserve } from './reserve';
import { OfflineRepository } from './offline';
import { OfflineDesk } from './offline-desk';
import { snapshot, pull } from './storage';
import { sessionRequest } from '../foundation/api';

it('downloads once, practises all five forms without HTTP across restart, then reconciles ordered frozen writes after response loss', async () => {
  const pg = new PGlite(); let now = Date.now();
  const store = new FoundationStore(pg, () => new Date(now)); await store.initialize();
  const owner = randomUUID(); const deviceId = randomUUID();
  const server = createApi(store, async () => owner);
  await new Promise<void>(resolve => server.listen(0, '127.0.0.1', resolve));
  const base = `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
  const originalFetch = globalThis.fetch;
  let offline = false; let loseAnswer = false; const sent: { path: string; body: string }[] = [];
  vi.stubGlobal('fetch', async (input: string, init?: RequestInit) => {
    if (offline) throw Error('Airplane mode');
    const url = new URL(input, base);
    if (init?.method === 'POST') sent.push({ path: url.pathname, body: init.body as string });
    const response = await originalFetch(url, init);
    if (loseAnswer && url.pathname === '/v2/attempts:batch') { loseAnswer = false; throw Error('Accepted response lost'); }
    return response;
  });
  let db = new WebReserve(`offline-http-${randomUUID()}`); let repo = new OfflineRepository(db);
  const api = localLearnerApi();
  const props = { api, locale: 'en' as const, deviceId, questionCount: 5, blocked: false };
  try {
    let ui = render(<OfflineDesk {...props} db={db} />);
    await waitFor(() => expect(screen.getByText('Download two sessions')).toBeEnabled());
    fireEvent.click(screen.getByText('Download two sessions'));
    await waitFor(() => expect(screen.getByText('Start downloaded practice')).toBeEnabled());
    expect(sent.map(s => s.path)).toEqual(['/v2/packs']);
    offline = true;
    fireEvent.click(screen.getByText('Start downloaded practice'));
    await screen.findByText('Question 1 / 5');
    const [id] = await repo.list(); const pack = (await repo.read(id)).pack;
    for (let index = 0; index < 5; index++) {
      if (index === 2) {
        ui.unmount(); db.close(); db = new WebReserve(db.name); repo = new OfflineRepository(db);
        ui = render(<OfflineDesk {...props} db={db} />);
        fireEvent.click(await screen.findByText('Open saved session 1'));
        await screen.findByText('Question 3 / 5');
      }
      const { session } = await repo.read(id); const exercise = session.questions[index].exercise;
      const answer = pack.rubrics.find(r => r.exerciseId === exercise.id && r.exerciseRevision === exercise.revision)!.acceptedAnswers[0];
      if (index === 0 && exercise.hint) { fireEvent.click(screen.getByText('Hint')); await screen.findByText(exercise.hint.en); }
      switch (answer.type) {
        case 'short_answer': fireEvent.change(screen.getByLabelText('Your answer'), { target: { value: answer.text } }); break;
        case 'choice': if (exercise.type === 'choice') fireEvent.click(screen.getByLabelText(exercise.options.find(o => o.id === answer.optionId)!.text)); break;
        case 'word_order': if (exercise.type === 'word_order') for (const token of answer.tokenIds)
          fireEvent.click(screen.getByRole('button', { name: exercise.tokens.find(t => t.id === token)!.text, exact: true })); break;
        default: if (exercise.type === 'cloze' || exercise.type === 'multi_slot') for (const slot of answer.values)
          fireEvent.change(screen.getByLabelText(exercise.slots.find(s => s.id === slot.slotId)!.label), { target: { value: slot.text } });
      }
      await waitFor(() => expect(screen.getByText('Check answer')).toBeEnabled());
      fireEvent.click(screen.getByText('Check answer'));
      await screen.findByText('Correct');
      fireEvent.click(screen.getByText('Continue'));
      if (index < 4) await screen.findByText(`Question ${index + 2} / 5`);
    }
    await screen.findByText('Session saved');
    expect(screen.getByText('Provisional correct: 5')).toBeVisible();
    expect(screen.getAllByRole('listitem')).toHaveLength(5);
    fireEvent.click(screen.getByText('End session'));
    await waitFor(async () => expect((await repo.read(id)).practice.ended).toBe(true));
    expect(sent).toHaveLength(1);
    expect((await pg.query('SELECT * FROM gm.accepted_evidence')).rows).toHaveLength(0);
    const before = (await repo.read(id)).practice;
    expect(before.events).toHaveLength(6);
    // Started work remains uploadable after the original pack deadline.
    now = Date.parse(pack.expiresAt) + 1; offline = false; loseAnswer = true;
    fireEvent.click(screen.getByText('Sync saved work (6)'));
    await screen.findByRole('alert');
    expect(sent).toHaveLength(2);
    expect((await repo.read(id)).practice).toEqual(before);
    await waitFor(() => expect(screen.getByText('Sync saved work (6)')).toBeEnabled());
    fireEvent.click(screen.getByText('Sync saved work (6)'));
    await waitFor(async () => expect((await repo.read(id)).practice.events.every(e => e.receipt)).toBe(true), { timeout: 5000 });
    expect(sent[1]).toEqual(sent[2]);
    expect(sent.at(-1)?.path).toBe(`/v2/sessions/${id}/complete`);
    expect((await pg.query('SELECT * FROM gm.accepted_evidence')).rows).toHaveLength(5);
    const after = (await repo.read(id)).practice;
    expect(after.events.map(e => e.request)).toEqual(before.events.map(e => e.request));
    expect(after.events.at(-1)?.receipt).toMatchObject({ mode: 'full', gradedCount: 5, skippedCount: 0, correctCount: 5 });
    expect(after.feedback).toEqual(before.feedback);
  } finally {
    cleanup(); vi.unstubAllGlobals(); await db.delete();
    await new Promise<void>((resolve, reject) => server.close(error => error ? reject(error) : resolve())); await pg.close();
  }
});

it('coordinates profile, online answer, offline Skip/end, report and download over real HTTP after restart', async () => {
  const pg = new PGlite(); const store = new FoundationStore(pg); await store.initialize();
  const subject = randomUUID(); const server = createApi(store, async () => subject);
  await new Promise<void>(resolve => server.listen(0, '127.0.0.1', resolve));
  const base = `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
  const originalFetch = globalThis.fetch; const writes: {path: string; body: string}[] = [];
  let lose = false;
  vi.stubGlobal('fetch', async (input: string, init?: RequestInit) => {
    const url = new URL(input, base);
    if (init?.method === 'POST') writes.push({path: url.pathname, body: init.body as string});
    const response = await originalFetch(url, init);
    if (lose && url.pathname === '/v2/attempts:batch' && response.ok) { lose = false; throw Error('Accepted response lost'); }
    return response;
  });
  let db = new WebReserve(`coordinator-http-${randomUUID()}`);
  const data = new Map<string,string>(); let failReceipt = false;
  const storage = {getItem: (key: string) => data.get(key) ?? null, setItem: (key: string, value: string) => {
    if (failReceipt && key === PROFILE_PENDING_KEY && value === '') throw Error('Receipt disk full');
    data.set(key,value);
  }};
  const api = localLearnerApi();
  try {
    const profile = await api.profile();
    const preferences = {...profile.preferences, sessionQuestionCount: 5 as const};
    const pendingProfile = {apiVersion:'v2',requestId:randomUUID(),expectedRevision:profile.revision,preferences};
    await db.prepare({...sessionRequest(),questionCount:1}, request => api.preparePack!(request));
    const offline = new OfflineRepository(db); const offlineId = await offline.start(randomUUID(),new Date());
    await offline.skip(offlineId); await offline.end(offlineId);
    const beforeOffline = (await offline.read(offlineId)).practice;
    const request = {...sessionRequest(),questionCount:1}; const session = await api.createSession(request);
    const exercise = session.questions[0].exercise;
    const pack = (await db.read()).pack!;
    const answer = pack.rubrics.find(r => r.exerciseId === exercise.id)!.acceptedAnswers[0];
    const pending = prepareAttempt(session,0,answer,true,randomUUID());
    saveJourney(storage,{...emptyJourney(),practice:{request,session,index:0,draft:answer,assisted:true,pending,pendingExposure:null,
      evaluation:null,rejected:false,confirmedCount:0,correctCount:0,skippedCount:0}});
    storage.setItem(PROFILE_PENDING_KEY,JSON.stringify(pendingProfile));
    const report = {apiVersion:'v2',reportId:randomUUID(),sessionQuestionId:session.questions[0].id,exerciseRevision:exercise.revision,category:'other'};
    storage.setItem(REPORT_KEY,JSON.stringify({version:1,request:report,recorded:false}));
    const download = {...sessionRequest(),questionCount:1}; await db.freeze(download);
    writes.length = 0; failReceipt = true;
    await expect(syncSavedWork(api,storage,db)).rejects.toThrow('Receipt disk full');
    expect(writes.map(w => w.path)).toEqual(['/v2/profile']);
    expect(JSON.parse(storage.getItem(PROFILE_PENDING_KEY)!)).toEqual(pendingProfile);
    failReceipt = false; lose = true;
    await expect(syncSavedWork(api,storage,db)).rejects.toThrow('Accepted response lost');
    expect(writes.map(w => w.path)).toEqual(['/v2/profile','/v2/profile','/v2/attempts:batch']);
    expect(readJourney(storage).practice!.pending).toEqual(pending);
    expect((await offline.read(offlineId)).practice).toEqual(beforeOffline);
    expect(JSON.parse(storage.getItem(REPORT_KEY)!).recorded).toBe(false);
    expect((await db.read()).pending).toEqual(download);
    db.close(); db = new WebReserve(db.name);
    await syncSavedWork(api,storage,db);
    expect(writes.slice(3).map(w => w.path)).toEqual(['/v2/attempts:batch','/v2/exposures:batch',`/v2/sessions/${offlineId}/complete`,'/v2/content-reports','/v2/packs']);
    const attempts = writes.filter(w => w.path === '/v2/attempts:batch'); expect(attempts[0].body).toBe(attempts[1].body);
    expect(readJourney(storage).practice).toMatchObject({pending,assisted:true,draft:answer,confirmedCount:1});
    expect((await new OfflineRepository(db).read(offlineId)).practice.events.every(e => e.receipt)).toBe(true);
    expect(JSON.parse(storage.getItem(REPORT_KEY)!)).toEqual({version:1,request:report,recorded:true});
    expect((await db.read()).pending).toBeNull(); expect((await db.read()).pack!.packId).toBe(download.requestId);
    expect((await pg.query('SELECT * FROM gm.accepted_evidence')).rows).toHaveLength(2);
    const count = writes.length; await syncSavedWork(api,storage,db); expect(writes).toHaveLength(count);
  } finally {
    vi.unstubAllGlobals(); await db.delete();
    await new Promise<void>((resolve,reject) => server.close(error => error ? reject(error) : resolve())); await pg.close();
  }
});

it('converges two device reserves after lost response and expired auth without double-crediting the same day', async () => {
  const pg = new PGlite(); let now = Date.now();
  const store = new FoundationStore(pg, () => new Date(now)); await store.initialize();
  const owner = randomUUID(); let authenticated = true;
  const server = createApi(store, async () => authenticated ? owner : null);
  await new Promise<void>(resolve => server.listen(0, '127.0.0.1', resolve));
  const base = `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
  const originalFetch = globalThis.fetch;
  let offline = false; let loseAnswer = false;
  const sent: { path: string; body: string }[] = [];
  vi.stubGlobal('fetch', async (input: string, init?: RequestInit) => {
    if (offline) throw Error('Device network unavailable');
    const url = new URL(input, base);
    if (init?.method === 'POST') sent.push({ path: url.pathname, body: init.body as string });
    const response = await originalFetch(url, init);
    if (loseAnswer && url.pathname === '/v2/attempts:batch' && response.ok) {
      loseAnswer = false; throw Error('Accepted response lost');
    }
    return response;
  });
  const api = localLearnerApi();
  let dbA = new WebReserve(`device-a-http-${randomUUID()}`);
  const dbB = new WebReserve(`device-b-http-${randomUUID()}`);
  let repoA = new OfflineRepository(dbA); const repoB = new OfflineRepository(dbB);
  try {
    let confirmedA = await snapshot(api); let confirmedB = await snapshot(api);
    for (const db of [dbA, dbB]) await db.prepare({ ...sessionRequest(), questionCount: 1 }, request => api.preparePack!(request));
    offline = true;
    const deviceA = randomUUID(); const deviceB = randomUUID();
    const idA = await repoA.start(deviceA, new Date(now)); const idB = await repoB.start(deviceB, new Date(now));
    expect(idA).not.toBe(idB);
    for (const [repo, id] of [[repoA, idA], [repoB, idB]] as const) {
      const { session, pack } = await repo.read(id); const exercise = session.questions[0].exercise;
      const answer = pack.rubrics.find(r => r.exerciseId === exercise.id && r.exerciseRevision === exercise.revision)!.acceptedAnswers[0];
      await repo.answer(id, answer); await repo.next(id); await repo.end(id);
    }
    const beforeA = (await repoA.read(idA)).practice; const beforeB = (await repoB.read(idB)).practice;
    const target = (await repoA.read(idA)).session.questions[0].exercise.targetId;
    expect((await repoB.read(idB)).session.questions[0].exercise.targetId).toBe(target);
    expect(beforeA.events[0].request).toMatchObject({ deviceId: deviceA });
    expect(beforeB.events[0].request).toMatchObject({ deviceId: deviceB });
    expect(sent.map(s => s.path)).toEqual(['/v2/packs', '/v2/packs']);
    now = Date.parse((await repoA.read(idA)).pack.expiresAt) + 1;
    offline = false; loseAnswer = true;
    await expect(repoA.sync(idA, api)).rejects.toThrow('Accepted response lost');
    expect((await repoA.read(idA)).practice).toEqual(beforeA);
    authenticated = false;
    await expect(repoA.sync(idA, api)).rejects.toThrow();
    await expect(repoB.sync(idB, api)).rejects.toThrow();
    expect((await repoA.read(idA)).practice).toEqual(beforeA);
    expect((await repoB.read(idB)).practice).toEqual(beforeB);
    expect((await pg.query('SELECT * FROM gm.accepted_evidence')).rows).toHaveLength(1);
    // Restart device A while its accepted answer still has no saved receipt.
    dbA.close(); dbA = new WebReserve(dbA.name); repoA = new OfflineRepository(dbA);
    authenticated = true;
    await repoB.sync(idB, api); await repoA.sync(idA, api);
    const afterA = (await repoA.read(idA)).practice; const afterB = (await repoB.read(idB)).practice;
    expect(afterA.events[0].receipt).toMatchObject({ status: 'duplicate' });
    expect(afterB.events[0].receipt).toMatchObject({ status: 'accepted' });
    expect(afterA.events.map(e => e.request)).toEqual(beforeA.events.map(e => e.request));
    expect(afterB.events.map(e => e.request)).toEqual(beforeB.events.map(e => e.request));
    expect(afterA.events.every(e => e.receipt)).toBe(true); expect(afterB.events.every(e => e.receipt)).toBe(true);
    expect((await pg.query('SELECT * FROM gm.accepted_evidence')).rows).toHaveLength(2);
    const answerWrites = sent.filter(s => s.path === '/v2/attempts:batch').map(s => s.body);
    expect(answerWrites.filter(body => body === answerWrites[0])).toHaveLength(3); // lost, 401, replay
    await pull(api, confirmedA, value => { confirmedA = value; });
    await pull(api, confirmedB, value => { confirmedB = value; });
    expect(confirmedA.targets).toEqual(confirmedB.targets);
    expect(confirmedA.targets.find(t => t.targetId === target)).toMatchObject({
      state: 'learning', qualifyingCheckCount: 1, everMastered: false, lastSequence: 2,
    });
  } finally {
    vi.unstubAllGlobals(); await dbA.delete(); await dbB.delete();
    await new Promise<void>((resolve, reject) => server.close(error => error ? reject(error) : resolve())); await pg.close();
  }
});

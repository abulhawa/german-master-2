import { expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { PGlite } from '@electric-sql/pglite';
import { randomUUID } from 'node:crypto';
import type { AddressInfo } from 'node:net';
import { FoundationStore } from '../../../../../services/api/src/store';
import { createApi } from '../../../../../services/api/src/server';
import { localLearnerApi } from './api';
import { WebReserve } from './reserve';
import { OfflineRepository } from './offline';
import { OfflineDesk } from './offline-desk';

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
    fireEvent.click(screen.getByText('Sync saved work (6)'));
    await waitFor(async () => expect((await repo.read(id)).practice.events.every(e => e.receipt)).toBe(true));
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

import { expect, it } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import type { AddressInfo } from 'node:net';
import type { Answer, Exercise } from '@german-master/contracts';
import type { Rubric } from '@german-master/learning-engine';
import { grade } from '@german-master/learning-engine';
import { basicPreview } from '../../../../../services/api/scripts/basic-preview';
import { createApi } from '../../../../../services/api/src/server';
import { ExerciseInput } from '../foundation/exercise-input';
import { sessionRequest } from '../foundation/api';
import { localLearnerApi } from './api';
import { OwnedLearnerJourney } from './journey';
import { emptyJourney, readJourney, saveJourney } from './storage';
import { fixtureAccount } from './account';
import { WebReserve } from './reserve';

function select(exercise: Exercise, answer: Answer) {
  if (exercise.type === 'choice' && answer.type === 'choice') {
    fireEvent.click(screen.getByRole('radio', { name: exercise.options.find(o => o.id === answer.optionId)!.text }));
  } else if (exercise.type === 'gap_choice' && answer.type === 'gap_choice') {
    for (const selection of answer.selections) {
      const slot = exercise.slots.find(s => s.id === selection.slotId)!;
      fireEvent.click(within(screen.getByRole('group', { name: slot.label })).getByRole('radio',
        { name: slot.options.find(o => o.id === selection.optionId)!.text }));
    }
  } else if (exercise.type === 'word_order' && answer.type === 'word_order') {
    for (const id of answer.tokenIds) {
      const token = exercise.tokens.find(t => t.id === id)!;
      fireEvent.click(screen.getByRole('button', { name: token.text, exact: true }));
    }
  } else throw Error('Unexpected authored format');
}

it('renders all 120 authored revisions 3–5 and preserves partial gap drafts across remount', async () => {
  const preview = await basicPreview();
  try {
    const converted = preview.members.filter(m => m.revision >= 3);
    expect(converted).toHaveLength(120);
    for (const member of converted) {
      const exercise = member.payload as Exercise, rubric = member.rubric as Rubric;
      let draft: Answer | null = null, ready: Answer | null = null;
      const props = { exercise, locale: 'de' as const, onDraft: (a: Answer | null) => { draft = a; }, onAnswer: (a: Answer | null) => { ready = a; } };
      let ui = render(<ExerciseInput {...props} />);
      const accepted = rubric.acceptedAnswers[0];
      if (exercise.type === 'gap_choice' && accepted.type === 'gap_choice' && exercise.slots.length > 1) {
        select(exercise, { ...accepted, selections: accepted.selections.slice(0, 1) });
        expect(ready).toBeNull();
        ui.unmount(); ui = render(<ExerciseInput {...props} initialAnswer={draft} />);
        expect(screen.getAllByRole('radio').filter(r => (r as HTMLInputElement).checked)).toHaveLength(1);
      }
      select(exercise, accepted);
      expect(ready).toEqual(accepted);
      expect(grade(exercise, rubric, ready!, []).outcome).toBe('correct');
      expect(screen.queryByRole('textbox')).toBeNull();
      ui.unmount();
    }
  } finally { cleanup(); await preview.db.close(); }
});

for (const [family, targetId, level] of [
  ['plural', '10000000-0000-4000-8000-000000000000', 'B1'],
  ['adjective', '10000000-0000-4000-8000-000000000015', 'B1'],
  ['verb', '10000000-0000-4000-8000-000000000020', 'B1'],
  ['B2 passive', '40000000-0000-4000-8000-000000000000', 'B2'],
  ['B2 verb-preposition', '40000000-0000-4000-8000-000000000020', 'B2'],
  ['B2 workplace verb', '40000000-0000-4000-8000-000000000025', 'B2'],
] as const) {
  it(`${family}: real learner controls resume a current draft and replay response loss to one confirmed result`, async () => {
    const preview = await basicPreview();
    const subject = fixtureAccount.identity.subject;
    const server = createApi(preview.store, async req => req.headers.authorization === 'Bearer foundation-local-demo' ? subject : null);
    await new Promise<void>(resolve => server.listen(0, '127.0.0.1', resolve));
    const origin = `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
    let lose = true; const attempts: string[] = [];
    const transport: typeof fetch = async (input, init) => {
      const url = new URL(String(input), origin);
      const response = await fetch(url, init);
      if (url.pathname === '/v2/attempts:batch') {
        attempts.push(String(init?.body));
        if (lose) { lose = false; throw Error('Accepted response lost'); }
      }
      return response;
    };
    const api = localLearnerApi(undefined, transport);
    const db = new WebReserve(`basic-preview-${crypto.randomUUID()}`);
    const values = new Map<string,string>();
    const storage = { getItem: (k:string) => values.get(k) ?? null, setItem: (k:string,v:string) => { values.set(k,v); } };
    try {
      await api.saveProfile({apiVersion:'v2',requestId:crypto.randomUUID(),expectedRevision:0,
        preferences:{locale:'en',timezone:'Europe/Berlin',level,sessionQuestionCount:5}});
      const request = {...sessionRequest(),questionCount:1,focus:{type:'target' as const,id:targetId}};
      const session = await api.createFocusedSession(request);
      const exercise = session.questions[0].exercise;
      expect(exercise.revision).toBe(family === 'plural' ? 3 : 4);
      expect(JSON.stringify(session)).not.toContain('acceptedAnswers');
      const rubric = preview.members.find(m => m.exercise_id === exercise.id)!.rubric as Rubric;
      const state = emptyJourney();
      state.practice = {request,session,index:0,draft:null,assisted:false,pending:null,evaluation:null,
        rejected:false,confirmedCount:0,correctCount:0,pendingExposure:null,skippedCount:0};
      saveJourney(storage,state);
      const props = {api,storage,reserve:db};
      let ui = render(<OwnedLearnerJourney {...props} />);
      await waitFor(()=>expect(screen.getByRole('button',{name:'Continue practice'})).toBeEnabled());
      fireEvent.click(screen.getByRole('button',{name:'Continue practice'}));
      await screen.findByRole('heading',{name:exercise.prompt});
      expect(screen.getByRole('button',{name:'Check answer'})).toBeDisabled();
      select(exercise,rubric.acceptedAnswers[0]);
      await waitFor(()=>expect(readJourney(storage).practice!.draft).toEqual(rubric.acceptedAnswers[0]));
      ui.unmount(); ui = render(<OwnedLearnerJourney {...props} />);
      await waitFor(()=>expect(screen.getByRole('button',{name:'Continue practice'})).toBeEnabled());
      fireEvent.click(screen.getByRole('button',{name:'Continue practice'}));
      await waitFor(()=>expect(screen.getByRole('button',{name:'Check answer'})).toBeEnabled());
      fireEvent.click(screen.getByRole('button',{name:'Check answer'}));
      await waitFor(()=>expect(attempts).toHaveLength(1));
      await waitFor(()=>expect(readJourney(storage).practice!.pending).not.toBeNull());
      ui.unmount(); ui = render(<OwnedLearnerJourney {...props} />);
      await waitFor(()=>expect(screen.getByRole('button',{name:'Continue practice'})).toBeEnabled());
      fireEvent.click(screen.getByRole('button',{name:'Continue practice'}));
      fireEvent.click(await screen.findByRole('button',{name:'Retry',exact:true}));
      await waitFor(()=>expect(readJourney(storage).practice!.confirmedCount).toBe(1));
      expect(attempts[1]).toBe(attempts[0]);
      expect(readJourney(storage).practice!.evaluation?.outcome).toBe('correct');
      expect((await preview.db.query('SELECT id FROM gm.accepted_evidence')).rows).toHaveLength(1);
      fireEvent.click(screen.getByRole('button',{name:'Continue',exact:true}));
      await waitFor(()=>expect(screen.getByRole('button',{name:'Confirm session completion',exact:true})).toBeEnabled());
      fireEvent.click(screen.getByRole('button',{name:'Confirm session completion',exact:true}));
      await waitFor(()=>expect(readJourney(storage).practice!.completionReceipt).toMatchObject({mode:'full',gradedCount:1,correctCount:1}));
    } finally { cleanup(); await db.delete(); await new Promise<void>(resolve=>server.close(()=>resolve())); await preview.db.close(); }
  });
}

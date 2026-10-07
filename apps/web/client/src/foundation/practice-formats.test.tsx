import { cleanup, fireEvent, render, screen, within } from '@testing-library/react';
import { afterEach, expect, it } from 'vitest';
import { SessionSchema, type Answer } from '@german-master/contracts';
import source from '@german-master/contracts/examples/practice-formats-session.json';
import { ExerciseInput } from './exercise-input';
import { emptyJourney, JourneySchema, readyAnswer } from '../renovation/storage';

afterEach(cleanup);
const session = SessionSchema.parse(source);
it('restores partial gap selections, permits revisions and refuses unsaved changes', () => {
  const exercise = session.questions[2].exercise;
  let saved: Answer | null = null, ready: Answer | null = null, fail = false;
  const ui = () => <ExerciseInput exercise={exercise} locale="en" initialAnswer={saved}
    onDraft={draft => { if(fail) return false; saved=draft; return true; }} onAnswer={answer=>{ready=answer;}} />;
  let view = render(ui());
  fireEvent.click(screen.getByLabelText('arbeiten'));
  expect(ready).toBeNull();
  const journey = emptyJourney();
  journey.practice = {request:{apiVersion:'v2',requestId:crypto.randomUUID(),questionCount:4,capabilities:['gap_choice@1']},
    session,index:2,draft:saved,assisted:false,pending:null,evaluation:null,rejected:false,confirmedCount:0,correctCount:0,pendingExposure:null,skippedCount:0};
  saved = JourneySchema.parse(JSON.parse(JSON.stringify(journey))).practice!.draft;
  view.unmount(); view = render(ui());
  expect(screen.getByLabelText('arbeiten')).toBeChecked();
  fireEvent.click(screen.getByLabelText('gehst'));
  expect(readyAnswer(exercise,ready)).not.toBeNull();
  fail = true; fireEvent.click(screen.getByLabelText('geht'));
  expect(screen.getByLabelText('gehst')).toBeChecked();
  fail = false; fireEvent.click(screen.getByLabelText('geht'));
  expect(screen.getByLabelText('geht')).toBeChecked();
  view.unmount();
});
it('supports matching, reassigning and unpairing, then restores saved pairs', () => {
  const exercise = session.questions[3].exercise;
  let saved: Answer | null = null, ready: Answer | null = null;
  const ui = () => <ExerciseInput exercise={exercise} locale="en" initialAnswer={saved}
    onDraft={draft=>{saved=draft;}} onAnswer={answer=>{ready=answer;}} />;
  const view = render(ui());
  const pair = (left:string,right:string) => {fireEvent.click(screen.getByRole('button',{name:left,exact:true}));fireEvent.click(screen.getByRole('button',{name:right,exact:true}));};
  pair('eine Entscheidung','treffen'); pair('eine Frage','stellen'); pair('Verantwortung','übernehmen');
  expect(ready?.type).toBe('matching');
  expect(document.activeElement).toHaveTextContent('Verantwortung');
  pair('eine Frage','treffen');
  expect(ready).toBeNull();
  expect(screen.queryByRole('button',{name:'Remove pair: eine Entscheidung'})).not.toBeInTheDocument();
  fireEvent.click(screen.getByRole('button',{name:'Remove pair: eine Frage'}));
  view.unmount();render(ui());
  expect(screen.getByText('Verantwortung → übernehmen')).toBeInTheDocument();
  expect(within(screen.getByRole('group',{name:'Right items'})).getByRole('button',{name:'treffen'})).toBeDisabled();
});
it('orders repeated words by ID, removes one word and restores a partial order', () => {
  const exercise = session.questions[1].exercise;
  let saved: Answer | null = {type:'word_order',tokenIds:['2','4','3','1','5','0']};
  let ready: Answer | null = saved;
  const view = render(<ExerciseInput exercise={exercise} locale="en" initialAnswer={saved} onDraft={draft=>{saved=draft;}} onAnswer={a=>{ready=a;}} />);
  fireEvent.click(screen.getByRole('button',{name:'Remove: sie (4)'}));
  expect(ready).toBeNull();
  expect(document.activeElement).toHaveAttribute('aria-label','Remove: Ich (1)');
  expect(saved).toEqual({type:'word_order',tokenIds:['2','4','3','5','0']});
  view.unmount();
  render(<ExerciseInput exercise={exercise} locale="en" initialAnswer={saved} onAnswer={a=>{ready=a;}} />);
  fireEvent.click(screen.getByRole('button',{name:'sie',exact:true}));
  expect(ready?.type).toBe('word_order');
});

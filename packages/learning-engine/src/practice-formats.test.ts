import { expect, it } from 'vitest';
import { SessionSchema, OfflineRubricSchema, type Answer } from '@german-master/contracts';
import source from '../../../contracts/v2/examples/practice-formats-session.json';
import rubrics from '../../../contracts/v2/examples/practice-formats-rubrics.json';
import { answerReady, grade, validateAnswer, validateExerciseInputs } from './index';

const session = SessionSchema.parse(source);
for (const [index, question] of session.questions.entries()) {
  it(`grades complete ${question.exercise.type} answers, including authored alternatives`, () => {
    const rubric = OfflineRubricSchema.parse(rubrics[index]);
    for (const answer of rubric.acceptedAnswers) {
      expect(answerReady(question.exercise, answer)).toEqual(answer);
      expect(grade(question.exercise, rubric, answer, []).outcome).toBe('correct');
      expect(grade(question.exercise, rubric, answer, ['hint']).assisted).toBe(true);
    }
  });
}
it('rejects incomplete, duplicate and foreign gap selections without marking them wrong', () => {
  const exercise = session.questions[2].exercise;
  const selections = [{slotId:'wir', optionId:'b'}];
  expect(answerReady(exercise, {type:'gap_choice', selections})).toBeNull();
  expect(() => validateAnswer(exercise, {type:'gap_choice', selections:[...selections, ...selections]})).toThrow('invalid_slots');
  expect(() => validateAnswer(exercise, {type:'gap_choice', selections:[...selections, {slotId:'du',optionId:'foreign'}]})).toThrow('unknown_option');
  expect(grade(exercise, OfflineRubricSchema.parse(rubrics[2]),
    {type:'gap_choice', selections:[...selections, {slotId:'du',optionId:'a'}]}, []).outcome).toBe('incorrect');
});
it('matches by identity regardless of pair order and rejects duplicated right assignments', () => {
  const exercise = session.questions[3].exercise;
  const rubric = OfflineRubricSchema.parse(rubrics[3]);
  const accepted = rubric.acceptedAnswers[0];
  if (accepted.type !== 'matching') throw Error('fixture');
  expect(grade(exercise, rubric, {...accepted, pairs:[...accepted.pairs].reverse()}, []).outcome).toBe('correct');
  expect(answerReady(exercise, {type:'matching',pairs:accepted.pairs.slice(0,1)})).toBeNull();
  expect(() => validateAnswer(exercise, {...accepted, pairs:accepted.pairs.map(p => ({...p,rightId:'make'}))})).toThrow('invalid_pairs');
  const swapped = accepted.pairs.map(p => ({...p,rightId:p.rightId === 'ask' ? 'make' : p.rightId === 'make' ? 'ask' : p.rightId}));
  expect(grade(exercise, rubric, {type:'matching',pairs:swapped}, []).outcome).toBe('incorrect');
});
it('keeps repeated word text distinct and checks editorial input identities', () => {
  const exercise = session.questions[1].exercise;
  expect(answerReady(exercise,{type:'word_order',tokenIds:['2','4','3','1','1','0']})).toBeNull();
  const gap = session.questions[2].exercise;
  if(gap.type !== 'gap_choice') throw Error('fixture');
  expect(() => validateExerciseInputs({...gap,slots:[...gap.slots,gap.slots[0]]})).toThrow('duplicate_input_id');
  expect(() => validateExerciseInputs({...gap,slots:[{...gap.slots[0],options:[gap.slots[0].options[0],gap.slots[0].options[0]]}]})).toThrow('duplicate_input_id');
  const matching = session.questions[3].exercise;
  if(matching.type !== 'matching') throw Error('fixture');
  expect(() => validateExerciseInputs({...matching,right:matching.right.slice(1)})).toThrow('unequal_matching_sides');
});

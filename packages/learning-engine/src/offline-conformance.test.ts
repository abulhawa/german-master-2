import { expect, it } from 'vitest';
import { ExerciseSchema, OfflineRubricSchema } from '@german-master/contracts';
import fixtures from '../../../contracts/v2/examples/offline-grading.json';
import { grade } from './index';

it.each(fixtures.cases)('shared offline grading: $name', fixture => {
  const exercise = ExerciseSchema.parse(fixture.exercise);
  const rubric = OfflineRubricSchema.parse(fixture.rubric);
  if ('error' in fixture.expected) {
    expect(() => grade(exercise, rubric, fixture.answer, fixture.assistance as ('hint' | 'reveal')[])).toThrow(fixture.expected.error);
  } else {
    expect(grade(exercise, rubric, fixture.answer, fixture.assistance as ('hint' | 'reveal')[])).toEqual(fixture.expected);
  }
});

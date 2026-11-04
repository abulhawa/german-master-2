import { readFileSync } from 'node:fs';
import { expect, it } from 'vitest';
import { SessionSchema, type Exercise } from '@german-master/contracts';
import { grade, type Rubric } from '@german-master/learning-engine';

type Conversion = {
  exerciseId: string;
  targetId: string;
  level: string;
  previous: { type: string; revision: number; prompt: string };
  next: { exercise: Exercise; rubric: Rubric };
  source: { path: string; review: { status: string; reviewedHash?: string } };
  editorialChanges: { promptChanged: boolean; answerFormatChanged: boolean };
};
const read = (path: string) => JSON.parse(readFileSync(path, 'utf8'));
const draft = read('content/candidates/low-typing-conversion/draft.json') as {
  status: string; publicationApproved: boolean; previousActiveReleaseId: string;
  questionCount: number; conversions: Conversion[];
};
const historical = read('content/candidates/enrichment-wave-1/offline.json');
const current = read('content/candidates/enrichment-wave-3/offline.json');
const typing = new Set(['short_answer', 'cloze', 'multi_slot']);
const selection = new Set(['choice', 'word_order', 'gap_choice', 'matching']);
const exercises = (bundle: any) => bundle.packs.flatMap((pack: any) =>
  pack.session.questions.map((q: any) => q.exercise));
const typedById = (bundle: any) => new Map<string, any>(exercises(bundle)
  .filter((exercise: any) => typing.has(exercise.type))
  .map((exercise: any) => [exercise.id, exercise]));

it('covers exactly the typed legacy revisions inherited by the latest release', () => {
  expect(draft.status).toBe('draft');
  expect(draft.publicationApproved).toBe(false);
  expect(draft.previousActiveReleaseId).toBe('92000000-0000-4000-8000-000000000001');
  const original = typedById(historical);
  const inherited = typedById(current);
  expect(original.size).toBe(103);
  expect(inherited.size).toBe(103);
  expect(draft.questionCount).toBe(103);
  expect(draft.conversions.map(entry => entry.exerciseId).sort())
    .toEqual([...inherited.keys()].sort());
  expect(new Set(draft.conversions.map(entry => entry.exerciseId)).size).toBe(103);
  for (const [id, existing] of inherited) {
    const originalExercise = original.get(id);
    expect(existing.revision).toBe(originalExercise?.revision);
    expect(existing.type).toBe(originalExercise?.type);
  }
});

it('keeps old revisions immutable and requires reviewed selectable revisions', () => {
  const original = typedById(historical);
  const b1 = read('content/drafts/initial-30.json');
  const b2 = read('content/drafts/b2-basics.json');
  const authored = new Map<string, any>([...b1.targets, ...b2.targets]
    .flatMap((target: any) => target.variants
      .map((variant: any) => [variant.exercise.id, { target, variant }])));
  let reused = 0, starter = 0;
  for (const entry of draft.conversions) {
    const prev = original.get(entry.exerciseId);
    expect(prev).toBeDefined();
    expect(entry.previous).toEqual({ type: prev.type, revision: prev.revision, prompt: prev.prompt });
    expect(entry.next.exercise.id).toBe(prev.id);
    expect(entry.next.exercise.targetId).toBe(prev.targetId);
    expect(entry.next.exercise.revision).toBeGreaterThan(prev.revision);
    expect(selection.has(entry.next.exercise.type)).toBe(true);
    expect(entry.next.rubric.acceptedAnswers.length).toBeGreaterThan(0);
    for (const answer of entry.next.rubric.acceptedAnswers) {
      expect(grade(entry.next.exercise, entry.next.rubric, answer, []).outcome)
        .toBe('correct');
    }
    expect(JSON.stringify(entry.next.exercise)).not.toContain('acceptedAnswers');
    if (authored.has(entry.exerciseId)) {
      const { target, variant } = authored.get(entry.exerciseId);
      expect(entry.source.review.status).toBe('approved-ai');
      expect(target.review.status).toBe('approved');
      expect(entry.source.review.reviewedHash).toBe(target.review.reviewedHash);
      expect(entry.next.exercise).toEqual(variant.exercise);
      expect(entry.next.rubric).toEqual(variant.rubric);
      reused += 1;
    } else {
      expect(entry.source.review.status).toBe('approved-ai');
      starter += 1;
    }
  }
  expect(reused).toBe(100);
  expect(starter).toBe(3);
});

it('renders all staged questions without typing and supports complete answer grading', () => {
  const session = SessionSchema.parse({
    apiVersion: 'v2',
    id: '93000000-0000-4000-8000-000000000001',
    contentReleaseId: '93000000-0000-4000-8000-000000000002',
    questions: draft.conversions.map(entry => ({
      id: entry.exerciseId,
      exercise: entry.next.exercise
    }))
  });
  expect(session.questions).toHaveLength(103);
  for (const { exercise } of session.questions) {
    expect(['choice', 'gap_choice']).toContain(exercise.type);
  }
});

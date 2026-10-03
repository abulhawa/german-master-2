import { AnswerSchema, EvaluationSchema, type Answer, type Evaluation, type Exercise, type LocalizedText } from "@german-master/contracts";
export * from "./evidence";

export const NORMALIZATION_VERSION = "de-nfc-trim-v1";
export const EVALUATOR_VERSION = "deterministic-v1";
export type Rubric = {
  normalizationVersion: typeof NORMALIZATION_VERSION;
  acceptedAnswers: Answer[];
  explanation: LocalizedText;
};

export class GradingError extends Error {
  constructor(public readonly code: string) { super(code); }
}

export function normalize(text: string, version: string): string {
  if (version !== NORMALIZATION_VERSION) throw new GradingError("unsupported_policy");
  // Do not fold case, remove punctuation, collapse internal spaces or transliterate umlauts.
  return text.normalize("NFC").trim();
}

function sameIds(actual: string[], expected: string[]): boolean {
  return actual.length === expected.length && new Set(actual).size === actual.length &&
    expected.every(id => actual.includes(id));
}

/** Validate linkage before comparing content; malformed answers are not wrong answers. */
export function validateAnswer(exercise: Exercise, input: unknown): Answer {
  const parsed = AnswerSchema.safeParse(input);
  if (!parsed.success || parsed.data.type !== exercise.type) throw new GradingError("answer_type_mismatch");
  const answer = parsed.data;
  if (exercise.type === "choice" && answer.type === "choice" &&
      !exercise.options.some(o => o.id === answer.optionId)) throw new GradingError("unknown_option");
  if (exercise.type === "word_order" && answer.type === "word_order" &&
      !sameIds(answer.tokenIds, exercise.tokens.map(t => t.id))) throw new GradingError("invalid_tokens");
  if ((exercise.type === "cloze" || exercise.type === "multi_slot") &&
      (answer.type === "cloze" || answer.type === "multi_slot") &&
      !sameIds(answer.values.map(v => v.slotId), exercise.slots.map(s => s.id)))
    throw new GradingError("invalid_slots");
  return answer;
}

function equivalent(answer: Answer, accepted: Answer, policy: string): boolean {
  if (answer.type !== accepted.type) return false;
  if (answer.type === "short_answer" && accepted.type === "short_answer")
    return normalize(answer.text, policy) === normalize(accepted.text, policy);
  if (answer.type === "choice" && accepted.type === "choice") return answer.optionId === accepted.optionId;
  if (answer.type === "word_order" && accepted.type === "word_order")
    return answer.tokenIds.every((id, i) => id === accepted.tokenIds[i]);
  if ((answer.type === "cloze" || answer.type === "multi_slot") &&
      (accepted.type === "cloze" || accepted.type === "multi_slot"))
    return answer.values.every(v => accepted.values.some(a => a.slotId === v.slotId &&
      normalize(a.text, policy) === normalize(v.text, policy)));
  return false;
}

/** Pure, revision-pinned evaluation. This does not reduce evidence or confirm mastery. */
export function grade(exercise: Exercise, rubric: Rubric, input: unknown, assistance: readonly ("hint" | "reveal")[]): Evaluation {
  normalize("", rubric.normalizationVersion);
  if (!rubric.acceptedAnswers.length) throw new GradingError("invalid_rubric");
  rubric.acceptedAnswers.forEach(a => validateAnswer(exercise, a));
  const answer = validateAnswer(exercise, input);
  return EvaluationSchema.parse({
    outcome: rubric.acceptedAnswers.some(a => equivalent(answer, a, rubric.normalizationVersion)) ? "correct" : "incorrect",
    policyVersion: `${EVALUATOR_VERSION}/${rubric.normalizationVersion}`,
    assisted: assistance.length > 0,
    explanation: rubric.explanation,
    acceptedAnswer: rubric.acceptedAnswers[0],
  });
}

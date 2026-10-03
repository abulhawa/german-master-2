import { SessionSchema, AnswerSchema, type Session } from "@german-master/contracts";
import { NORMALIZATION_VERSION, grade, type Rubric } from "@german-master/learning-engine";
import sample from "../../../contracts/v2/examples/session.json";
import editorial from "../../../content/foundation/review.json";

const german = [
  "Der Plural lautet „Berufe“; Nomen werden großgeschrieben.",
  "„Mit“ verlangt den Dativ. Bei einem Kollegen heißt es „dem neuen Kollegen“.",
  "Ein Ziel mit „das Büro“ verlangt „in das“, meist verkürzt zu „ins“.",
  "Nach „weil“ steht das konjugierte Verb am Ende.",
  "Bei „arbeiten“ steht vor -st und -t ein e: arbeitest, arbeitet.",
];

/** Server-only unpublished catalog. Never import this module into client bundles. */
export function foundationCatalog(): { session: Session; rubrics: Map<string, Rubric> } {
  const session = SessionSchema.parse(sample);
  if (session.contentReleaseId !== editorial.releaseId || editorial.publicationApproved)
    throw Error("Local fixture must remain a pinned unpublished draft");
  const rubrics = new Map<string, Rubric>();
  for (const [i, question] of session.questions.entries()) {
    const target = editorial.targets.find(t => t.exerciseId === question.exercise.id && t.revision === question.exercise.revision);
    if (!target || target.id !== question.exercise.targetId || target.policy !== NORMALIZATION_VERSION)
      throw Error("Editorial revision linkage mismatch");
    const acceptedAnswers = [AnswerSchema.parse(target.acceptedAnswer)];
    if (question.exercise.type === "cloze") {
      for (const text of editorial.alternatives.preposition.slice(1))
        acceptedAnswers.push({ type: "cloze", values: [{ slotId: "preposition", text }] });
    }
    const rubric: Rubric = { normalizationVersion: NORMALIZATION_VERSION, acceptedAnswers,
      explanation: { en: target.explanation, de: german[i] } };
    grade(question.exercise, rubric, acceptedAnswers[0], []);
    rubrics.set(`${question.exercise.id}@${question.exercise.revision}`, rubric);
  }
  return { session, rubrics };
}

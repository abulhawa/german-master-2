import { describe, it, expect } from "vitest";
import { SessionSchema, AnswerSchema } from "@german-master/contracts";
import sample from "../../../contracts/v2/examples/session.json";
import { foundationCatalog } from "../../../services/api/src/catalog";
import { grade, normalize, validateAnswer, NORMALIZATION_VERSION } from "./index";

const { session, rubrics } = foundationCatalog();
describe("revision-pinned grading", () => {
  for (const { exercise } of session.questions) {
    const rubric = rubrics.get(`${exercise.id}@${exercise.revision}`)!;
    it(`grades ${exercise.type} and marks assistance without changing correctness`, () => {
      for (const answer of rubric.acceptedAnswers) {
        expect(grade(exercise, rubric, answer, []).outcome).toBe("correct");
        expect(grade(exercise, rubric, answer, ["hint"]).assisted).toBe(true);
        expect(grade(exercise, rubric, answer, ["reveal"]).assisted).toBe(true);
      }
      expect(grade(exercise, rubric, rubric.acceptedAnswers[0], []).assisted).toBe(false);
    });
  }
  it("normalizes NFC and outer spaces only", () => {
    expect(normalize("  Bu\u0308ro  ", NORMALIZATION_VERSION)).toBe("Büro");
    expect(normalize("Buro", NORMALIZATION_VERSION)).not.toBe("Büro");
    expect(normalize("in  das", NORMALIZATION_VERSION)).toBe("in  das");
    expect(() => normalize("text", "unknown-policy")).toThrow("unsupported_policy");
    const exercise = session.questions[0].exercise;
    const rubric = rubrics.get(`${exercise.id}@1`)!;
    for (const text of ["berufe", "Berufe.", "Beruf"])
      expect(grade(exercise, rubric, { type: "short_answer", text }, []).outcome).toBe("incorrect");
    expect(grade(exercise, rubric, { type: "short_answer", text: " Berufe " }, []).outcome).toBe("correct");
  });
  it("matches slots by identity, accepting a complete reversed slot list", () => {
    const exercise = session.questions[4].exercise;
    const rubric = rubrics.get(`${exercise.id}@1`)!;
    expect(grade(exercise, rubric, { type: "multi_slot", values: [{ slotId: "ihr", text: "arbeitet" }, { slotId: "du", text: "arbeitest" }] }, []).outcome).toBe("correct");
    expect(grade(exercise, rubric, { type: "multi_slot", values: [{ slotId: "du", text: "arbeitet" }, { slotId: "ihr", text: "arbeitest" }] }, []).outcome).toBe("incorrect");
    expect(() => validateAnswer(exercise, { type: "multi_slot", values: [{ slotId: "du", text: "arbeitest" }, { slotId: "du", text: "arbeitet" }] })).toThrow("invalid_slots");
  });
  it("rejects wrong type, unknown options and incomplete token sets", () => {
    expect(() => validateAnswer(session.questions[0].exercise, { type: "choice", optionId: "dem" })).toThrow("answer_type_mismatch");
    expect(() => validateAnswer(session.questions[1].exercise, { type: "choice", optionId: "unknown" })).toThrow("unknown_option");
    expect(() => validateAnswer(session.questions[3].exercise, { type: "word_order", tokenIds: ["1","3"] })).toThrow("invalid_tokens");
    expect(() => validateAnswer(session.questions[3].exercise, { type: "word_order", tokenIds: ["1","3","2","4","0","missing"] })).toThrow("invalid_tokens");
    expect(AnswerSchema.safeParse({ type: "word_order", tokenIds: ["1","1"] }).success).toBe(false);
  });
  it("retains draft status and does not add rubric fields to online questions", () => {
    expect(SessionSchema.parse(sample)).toEqual(session);
    expect(JSON.stringify(session)).not.toContain("acceptedAnswer");
    expect(JSON.stringify(session)).not.toContain("normalizationVersion");
  });
  it("grades valid wrong choices, orders and cloze answers as incorrect", () => {
    const wrong = [
      { index: 1, answer: { type: "choice", optionId: "den" } },
      { index: 2, answer: { type: "cloze", values: [{ slotId: "preposition", text: "in  das" }] } },
      { index: 3, answer: { type: "word_order", tokenIds: ["1","3","2","4","5","0"] } },
    ];
    for (const {index, answer} of wrong) {
      const exercise = session.questions[index].exercise;
      expect(grade(exercise, rubrics.get(`${exercise.id}@1`)!, answer, []).outcome).toBe("incorrect");
    }
  });
});

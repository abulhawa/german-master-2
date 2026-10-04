import { describe, it, expect } from "vitest";
import {
  SessionSchema,
  AttemptBatchSchema,
  AttemptBatchResponseSchema,
  AnswerSchema,
} from "@german-master/contracts";
import editorial from "../../../../../content/foundation/review.json";
import session from "@german-master/contracts/examples/session.json";
import response from "@german-master/contracts/examples/attempt-response.json";
import corpus from "@german-master/contracts/examples/conformance.json";
import batch from "@german-master/contracts/examples/attempt-batch.json";
import { render, screen, fireEvent, cleanup } from "@testing-library/react";
import Preview, { ExerciseInput } from "./preview";

describe("shared v2 contract", () => {
  for (const example of corpus)
    it(example.name, () =>
      expect(SessionSchema.safeParse(example.session).success).toBe(
        example.valid,
      ),
    );
  it("parses accepted, duplicate and rejected acknowledgments", () =>
    expect(
      AttemptBatchResponseSchema.parse(response).acknowledgments.map(
        (a) => a.status,
      ),
    ).toEqual(["accepted", "duplicate", "rejected"]));
  it("parses all five typed answer forms", () =>
    expect(
      AttemptBatchSchema.parse(batch).attempts.map((a) => a.answer.type),
    ).toEqual(["short_answer", "choice", "cloze", "word_order", "multi_slot"]));
  it("rejects client grades and oversized batches", () => {
    expect(
      AttemptBatchSchema.safeParse({
        ...batch,
        attempts: [{ ...batch.attempts[0], correct: true }],
      }).success,
    ).toBe(false);
    expect(
      AttemptBatchSchema.safeParse({
        ...batch,
        attempts: Array(51).fill(batch.attempts[0]),
      }).success,
    ).toBe(false);
  });
});
it("renders the five forms, prepares answers and clears state between exercises", () => {
  render(<Preview />);
  fireEvent.change(screen.getByLabelText("Your answer"), {
    target: { value: "Berufe" },
  });
  fireEvent.click(screen.getByText("Inspect answer"));
  expect(screen.getByRole("status").textContent).toContain("Backend grading");
  fireEvent.click(screen.getByText("Next exercise"));
  expect(screen.getByText("Inspect answer")).toBeDisabled();
  fireEvent.click(screen.getByLabelText("dem"));
  fireEvent.click(screen.getByText("Next exercise"));
  fireEvent.change(screen.getByLabelText("Präposition"), {
    target: { value: "ins" },
  });
  fireEvent.click(screen.getByText("Next exercise"));
  for (const word of ["weil", "ich", "heute", "im", "Büro", "arbeite"])
    fireEvent.click(screen.getByRole("button", { name: word }));
  expect(screen.getByText("Inspect answer")).not.toBeDisabled();
  fireEvent.click(screen.getByText("Reset order"));
  expect(screen.getByText("Inspect answer")).toBeDisabled();
  fireEvent.click(screen.getByText("Next exercise"));
  fireEvent.change(screen.getByLabelText("du"), {
    target: { value: "arbeitest" },
  });
  expect(screen.getByText("Inspect answer")).toBeDisabled();
  fireEvent.change(screen.getByLabelText("ihr"), {
    target: { value: "arbeitet" },
  });
  expect(screen.getByText("Inspect answer")).not.toBeDisabled();
  cleanup();
});

it("keeps editorial answers linked to immutable revisions and valid controls", () => {
  const skills = new Set(editorial.skills.map((s) => s.id));
  const topics = new Set(editorial.topics.map((t) => t.id));
  for (const target of editorial.targets) {
    expect(skills.has(target.skillId)).toBe(true);
    expect(topics.has(target.topicId)).toBe(true);
    const exercise = SessionSchema.parse(session).questions.find(
      (q) => q.exercise.id === target.exerciseId,
    )?.exercise;
    expect(exercise?.targetId).toBe(target.id);
    expect(exercise?.revision).toBe(target.revision);
    const answer = AnswerSchema.parse(target.acceptedAnswer);
    expect(answer.type).toBe(exercise?.type);
    if (exercise?.type === "choice" && answer.type === "choice")
      expect(exercise.options.map((o) => o.id)).toContain(answer.optionId);
    if (exercise?.type === "word_order" && answer.type === "word_order")
      expect([...answer.tokenIds].sort()).toEqual(
        exercise.tokens.map((t) => t.id).sort(),
      );
    if (
      (exercise?.type === "cloze" || exercise?.type === "multi_slot") &&
      (answer.type === "cloze" || answer.type === "multi_slot")
    )
      expect(answer.values.map((v) => v.slotId).sort()).toEqual(
        exercise.slots.map((s) => s.id).sort(),
      );
  }
  expect(editorial.publicationApproved).toBe(false);
});

it("reorders token identities without submitting and keeps order unchanged when saving fails", () => {
  const exercise = SessionSchema.parse(session).questions[3].exercise;
  if (exercise.type !== "word_order") throw Error("Expected word order fixture");
  const ids = exercise.tokens.map(t => t.id);
  let saved = ids;
  let failed = false;
  let answer: unknown = null;
  const input = () => <ExerciseInput exercise={exercise} locale="en" initialAnswer={{ type: "word_order", tokenIds: saved }}
    onDraft={draft => { if (failed) return false; if (draft?.type === "word_order") saved = draft.tokenIds; return true; }}
    onAnswer={value => { answer = value; }} />;
  render(input());
  const first = exercise.tokens[0].text;
  expect(screen.getByRole("button", { name: `Move left: ${first} (1)` })).toBeDisabled();
  expect(screen.getByRole("button", { name: `Move right: ${exercise.tokens.at(-1)!.text} (${ids.length})` })).toBeDisabled();
  fireEvent.click(screen.getByRole("button", { name: `Move right: ${first} (1)` }));
  expect(saved).toEqual([ids[1], ids[0], ...ids.slice(2)]);
  expect(answer).toEqual({ type: "word_order", tokenIds: saved });
  cleanup(); render(input()); // Restored order after remount.
  failed = true;
  fireEvent.click(screen.getByRole("button", { name: `Move left: ${first} (2)` }));
  expect(saved[0]).toBe(ids[1]);
  expect(screen.getByRole("button", { name: `Move left: ${first} (2)` })).toBeEnabled();
  failed = false;
  fireEvent.click(screen.getByRole("button", { name: `Move left: ${first} (2)` }));
  expect(saved).toEqual(ids);
  cleanup();
});

import { it, expect } from "vitest";
import { selectQuestions, type SelectionCandidate } from "./selection";
const now = "2026-10-04T12:00:00Z";
const candidate = (targetId: string, state: SelectionCandidate["state"] = "new", dueAt: string | null = null): SelectionCandidate =>
  ({ targetId, state, dueAt, exerciseId: targetId, revision: 1, lastInformativeAt: null });

it("allocates 8/5/2 and interleaves weaknesses with a manageable opening", () => {
  const input = [...Array.from({ length: 10 }, (_, i) => candidate(`w${i}`, "needs_practice", now)),
    ...Array.from({ length: 10 }, (_, i) => candidate(`d${i}`, "mastered", now)),
    ...Array.from({ length: 10 }, (_, i) => candidate(`n${i}`))];
  const output = selectQuestions(input, 15, now);
  expect(output.filter(c => c.state === "needs_practice")).toHaveLength(8);
  expect(output.filter(c => c.state === "mastered")).toHaveLength(5);
  expect(output.filter(c => c.state === "new")).toHaveLength(2);
  expect(output[0].state).toBe("mastered");
  expect(output.map(c => c.state === "needs_practice" ? "w" : "m").join("")).not.toContain("wwww");
  expect(selectQuestions([...input].reverse(), 15, now)).toEqual(output);
});
it("reallocates pools, refreshes deadline eligibility and marks early extra practice as reinforcement", () => {
  const input = [candidate("new"), candidate("weak", "needs_practice", "2026-10-04T12:00:01Z"), candidate("due", "learning", now)];
  expect(selectQuestions(input, 3, now).map(c => [c.targetId, c.role])).toEqual([
    ["due", "assessment"], ["new", "assessment"], ["weak", "reinforcement"]]);
  expect(selectQuestions(input, 1, "2026-10-04T12:00:01Z")[0].targetId).toBe("weak");
});
it("bounds backlog urgency, uses stable ties and never manufactures duplicate targets", () => {
  const input = [candidate("b", "mastered", "2020-01-01T00:00:00Z"), candidate("a", "mastered", "2026-08-01T00:00:00Z")];
  expect(selectQuestions([...input, { ...input[0], exerciseId: "variant" }], 10, now).map(c => c.targetId)).toEqual(["a", "b"]);
});

it("prioritizes missing transfer evidence before presentation history within a target", () => {
  const state = candidate("grammar", "learning", now);
  const qualified = [{variantKey:"seen",contextKey:"known",transferKey:"transfer-a"}];
  const choices: SelectionCandidate[] = [
    {...state,exerciseId:"seen",variantKey:"seen",contextKey:"known",targetKind:"grammar",
      transferKey:"transfer-a",qualifyingChecks:qualified,presentationCount:0},
    {...state,exerciseId:"unseen",variantKey:"fresh",contextKey:"new",targetKind:"grammar",
      transferKey:"transfer-b",qualifyingChecks:qualified,presentationCount:4},
  ];
  expect(selectQuestions(choices,1,now)[0].exerciseId).toBe("unseen");
  expect(selectQuestions([...choices].reverse(),1,now)[0].exerciseId).toBe("unseen");
  const complete=choices.map(c=>({...c,qualifyingChecks:[...qualified,{variantKey:"fresh",contextKey:"new",transferKey:"transfer-b"}]}));
  expect(selectQuestions(complete,1,now)[0].exerciseId).toBe("seen");
});
it("does not claim another variant supplies lexical context evidence", () => {
  const first={...candidate("lexical"),exerciseId:"old",variantKey:"old",contextKey:"context-a",
    targetKind:"lexical",qualifyingChecks:[{variantKey:"old",contextKey:"context-a"}],presentationCount:0};
  const second={...first,exerciseId:"new",variantKey:"new",contextKey:"context-b",presentationCount:2};
  expect(selectQuestions([first,second],1,now)[0].exerciseId).toBe("new");
});

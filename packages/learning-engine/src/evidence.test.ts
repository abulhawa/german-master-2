import { describe, expect, it } from "vitest";
import { EVIDENCE_POLICY_VERSION, reduceEvidence, type AcceptedEvidence, type ReductionContext } from "./index";
import { foundationCatalog } from "../../../services/api/src/catalog";

const at = (day: number) => new Date(Date.UTC(2026, 0, day + 1, 12)).toISOString();
const context: ReductionContext = { learnerId: "learner", targetId: "target", targetKind: "lexical",
  timeZone: "Europe/Berlin", now: at(200), policyVersion: EVIDENCE_POLICY_VERSION };
function evidence(sequence: number, day: number, patch: Partial<AcceptedEvidence> = {}): AcceptedEvidence {
  return { id: `event-${sequence}`, learnerId: "learner", targetId: "target", receivedSequence: sequence,
    receivedAt: at(day), sessionIssuedAt: at(0), answeredAt: at(day), timeZone: "Europe/Berlin",
    kind: "assessment", outcome: "correct", assisted: false, evaluationVersion: "deterministic-v1/de-nfc-trim-v1",
    variantKey: sequence % 2 ? "recall" : "choice", contextKey: "work", ...patch } as AcceptedEvidence;
}
const reduce = (events: AcceptedEvidence[], patch: Partial<ReductionContext> = {}) =>
  reduceEvidence({ ...context, ...patch }, events);
const retained = () => [evidence(1, 0), evidence(2, 1), evidence(3, 4), evidence(4, 14)];

describe("retained-evidence-v1", () => {
  it("starts new, and exposes skips without creating evidence or a schedule", () => {
    expect(reduce([])).toMatchObject({ state: "new", schedule: null, isDue: false, exposureCount: 0 });
    expect(reduce([evidence(1, 0, { kind: "exposure" })])).toMatchObject({ state: "new",
      exposureCount: 1, qualifyingChecks: [], independentFailures: [], schedule: null });
  });
  it("advances the provisional 1/3/7/14/30 calendar-day ladder only at spaced checks", () => {
    const events = [...retained(), evidence(5, 28), evidence(6, 58)];
    const days = [1, 3, 7, 14, 30, 30];
    for (let i = 0; i < events.length; i++) {
      const snapshot = reduce(events.slice(0, i + 1));
      expect(snapshot.schedule).toMatchObject({ intervalDays: days[i], intervalStep: Math.min(i, 4) });
      expect(snapshot.qualifyingChecks).toHaveLength(i + 1);
    }
    expect(reduce(retained())).toMatchObject({ state: "mastered", everMastered: true, lapseCount: 0 });
  });
  it("does not count repetitions, premature checks, or their variants toward mastery", () => {
    const events = [evidence(1, 0, { variantKey: "one" }), evidence(2, 0),
      evidence(3, 1, { variantKey: "one" }), evidence(4, 2), evidence(5, 4, { variantKey: "one" }),
      evidence(6, 14, { variantKey: "one" })];
    const snapshot = reduce(events);
    expect(snapshot.state).toBe("improving");
    expect(snapshot.qualifyingChecks).toHaveLength(4);
    expect(snapshot.decisions.map(d => d.effect)).toEqual([
      "qualifying_success", "same_day", "qualifying_success", "before_review", "qualifying_success", "qualifying_success",
    ]);
    expect(reduce([...retained().slice(0, 3), evidence(4, 11)])).toMatchObject({ state: "improving", everMastered: false });
  });
  it("accepts lexical context diversity but requires editorial transfer diversity for concepts", () => {
    const contexts = retained().map((e, i) => ({ ...e, variantKey: "one", contextKey: i % 2 ? "home" : "work" }));
    expect(reduce(contexts).state).toBe("mastered");
    expect(reduce(contexts, { targetKind: "concept" }).state).toBe("improving");
    expect(reduce(retained().map(e => ({ ...e, transferKey: "same-transfer" })), { targetKind: "concept" }).state)
      .toBe("improving");
    expect(reduce(retained().map((e, i) => ({ ...e, transferKey: i % 2 ? "new-situation" : "original-situation" })),
      { targetKind: "concept" }).state).toBe("mastered");
  });
  it("the current single-variant foundation catalog never satisfies the mastery gate", () => {
    for (const { exercise } of foundationCatalog().session.questions) {
      const events = retained().map(e => ({ ...e, targetId: exercise.targetId,
        variantKey: `${exercise.id}@${exercise.revision}`, contextKey: "foundation" }));
      expect(reduce(events, { targetId: exercise.targetId, targetKind: "concept" }).state).toBe("improving");
      expect(reduce(events, { targetId: exercise.targetId }).state).toBe("improving");
    }
  });
  it("assisted correct and wrong answers request an independent check without failures or ladder gains", () => {
    for (const outcome of ["correct", "incorrect"] as const) {
      expect(reduce([evidence(1, 0, { assisted: true, outcome })])).toMatchObject({ state: "new",
        schedule: { intervalDays: 1, intervalStep: 0 }, qualifyingChecks: [], independentFailures: [] });
    }
    const events = [...retained(), evidence(5, 15, { assisted: true }), evidence(6, 16)];
    const before = reduce(retained());
    const after = reduce(events);
    expect(after.state).toBe("mastered");
    expect(after.schedule?.localDate).toBe("2026-01-17");
    expect(after.nextQualifyingAt).toBe(before.nextQualifyingAt);
    expect(after.qualifyingChecks).toHaveLength(4);
    expect(after.decisions.at(-1)?.effect).toBe("before_review");
    // Assistance never postpones an already overdue review.
    expect(reduce([...retained(), evidence(5, 50, { assisted: true })]).schedule).toEqual(before.schedule);
  });
  it("reinforcement and exposure neither improve nor damage independent retention history", () => {
    const original = reduce(retained());
    for (const patch of [{ kind: "reinforcement", outcome: "incorrect" }, { kind: "reinforcement" },
      { kind: "exposure" }] as Partial<AcceptedEvidence>[]) {
      const after = reduce([...retained(), evidence(5, 28, patch)]);
      expect(after).toMatchObject({ state: "mastered", lapseCount: 0, exposureCount: 5 });
      expect(after.schedule).toEqual(original.schedule);
      expect(after.qualifyingChecks).toEqual(original.qualifyingChecks);
    }
  });
  it("retains mastery transitions across a lapse and requires later distinct occasions for recovery", () => {
    const events = [...retained(), evidence(5, 28, { outcome: "incorrect" }), evidence(6, 28),
      evidence(7, 29), evidence(8, 30), evidence(9, 33), evidence(10, 43)];
    expect(reduce(events.slice(0, 5))).toMatchObject({ state: "needs_practice", everMastered: true,
      lapseCount: 1, schedule: { intervalDays: 1, intervalStep: 0 }, qualifyingChecks: [] });
    expect(reduce(events.slice(0, 7)).state).toBe("needs_practice");
    expect(reduce(events.slice(0, 8)).state).toBe("improving");
    const recovered = reduce(events);
    expect(recovered).toMatchObject({ state: "mastered", lapseCount: 1, everMastered: true });
    expect(recovered.independentFailures).toHaveLength(1);
    expect(recovered.transitions.map(t => t.to)).toEqual([
      "learning", "improving", "mastered", "needs_practice", "improving", "mastered",
    ]);
    const again = reduce([...events, evidence(11, 57, { outcome: "incorrect" }), evidence(12, 58, { outcome: "incorrect" })]);
    expect(again.lapseCount).toBe(2);
    expect(again.independentFailures).toHaveLength(3);
  });
  it("first independent failure enters needs practice and successive failures reset recovery", () => {
    const events = [evidence(1, 0, { outcome: "incorrect" }), evidence(2, 1),
      evidence(3, 2, { outcome: "incorrect" }), evidence(4, 3)];
    expect(reduce(events)).toMatchObject({ state: "needs_practice", lapseCount: 0, everMastered: false });
    expect(reduce([...events, evidence(5, 4)]).state).toBe("improving");
  });
  it("uses server receipt ordering and excludes late answers predating a later failure", () => {
    const events = [evidence(1, 0), evidence(2, 5, { outcome: "incorrect" }), evidence(3, 6, { answeredAt: at(1) })];
    const snapshot = reduce(events);
    expect(reduce([...events].reverse())).toEqual(snapshot);
    expect(snapshot.state).toBe("needs_practice");
    expect(snapshot.decisions.at(-1)?.effect).toBe("before_failure");
    expect(snapshot.qualifyingChecks).toHaveLength(0);
  });
  it("retains implausibly timed outcomes but excludes successful timing from retention", () => {
    const cases = [
      { answeredAt: undefined, reason: "missing" },
      { answeredAt: at(0), sessionIssuedAt: at(1), reason: "before_session" },
      { answeredAt: at(3), reason: "after_receipt" },
    ];
    for (const { reason, ...patch } of cases) {
      const snapshot = reduce([evidence(1, 2, patch)]);
      expect(snapshot).toMatchObject({ state: "learning", qualifyingChecks: [], schedule: { intervalDays: 1 } });
      expect(snapshot.decisions[0]).toMatchObject({ timing: reason, effect: "unqualified_timing", localDate: null });
      expect(reduce([evidence(1, 2, { ...patch, outcome: "incorrect" })]).state).toBe("needs_practice");
    }
    expect(reduce([evidence(1, 50, { answeredAt: at(0) })]).schedule?.localDate).toBe("2026-02-21");
  });
  it("calculates today and due boundaries in the saved timezone across DST", () => {
    const event = evidence(1, 0, { receivedAt: "2026-03-28T12:00:00Z", answeredAt: "2026-03-28T12:00:00Z" });
    const spring = reduce([event], { now: "2026-03-29T00:00:00Z" });
    expect(spring.schedule).toMatchObject({ dueAt: "2026-03-28T23:00:00.000Z", localDate: "2026-03-29" });
    expect(spring.isDue).toBe(true);
    const afterSpring = reduce([evidence(1, 0, { receivedAt: "2026-03-29T12:00:00Z", answeredAt: "2026-03-29T12:00:00Z" })],
      { now: "2026-03-30T00:00:00Z" });
    expect(afterSpring.schedule?.dueAt).toBe("2026-03-29T22:00:00.000Z");
    const fall = reduce([evidence(1, 0, { receivedAt: "2026-10-25T12:00:00Z", answeredAt: "2026-10-25T12:00:00Z" })],
      { now: "2026-10-25T22:59:59Z" });
    expect(fall.schedule?.dueAt).toBe("2026-10-25T23:00:00.000Z");
    expect(fall.isDue).toBe(false);
    const us = reduce([evidence(1, 0, { timeZone: "America/New_York", receivedAt: "2026-01-02T02:00:00Z",
      answeredAt: "2026-01-02T02:00:00Z" })]);
    expect(us.qualifyingChecks[0].localDate).toBe("2026-01-01");
    expect(us.schedule?.dueAt).toBe("2026-01-02T05:00:00.000Z");
  });
  it("handles a timezone's skipped calendar day at the first real following day", () => {
    const snapshot = reduce([evidence(1, 0, { timeZone: "Pacific/Apia", sessionIssuedAt: "2011-12-29T10:00:00Z",
      receivedAt: "2011-12-29T22:00:00Z", answeredAt: "2011-12-29T22:00:00Z" })]);
    expect(snapshot.schedule).toMatchObject({ localDate: "2011-12-31", dueAt: "2011-12-30T10:00:00.000Z" });
  });
  it("timezone changes cannot move an existing UTC retention gate earlier", () => {
    const events = [evidence(1, 0), evidence(2, 0, { timeZone: "Pacific/Kiritimati",
      receivedAt: "2026-01-01T14:00:00Z", answeredAt: "2026-01-01T14:00:00Z" })];
    expect(reduce(events).decisions.at(-1)?.effect).toBe("before_review");
    expect(reduce(events).qualifyingChecks).toHaveLength(1);
  });
  it("time alone changes due status, never mastery or transitions", () => {
    const before = reduce(retained(), { now: at(15) });
    const after = reduce(retained(), { now: at(100) });
    expect(before.isDue).toBe(false);
    expect(after.isDue).toBe(true);
    expect({ ...before, isDue: true }).toEqual(after);
  });
  it("rejects ambiguous identity, ownership, chronology, policy and metadata", () => {
    expect(() => reduce([evidence(1, 0), evidence(1, 1)])).toThrow("invalid_evidence_identity");
    expect(() => reduce([evidence(1, 0), evidence(2, 1, { id: "event-1" })])).toThrow("invalid_evidence_identity");
    expect(() => reduce([evidence(1, 0, { learnerId: "other" })])).toThrow("evidence_scope_mismatch");
    expect(() => reduce([evidence(1, 0, { targetId: "other" })])).toThrow("evidence_scope_mismatch");
    expect(() => reduce([evidence(1, 2), evidence(2, 1)])).toThrow("invalid_receipt_order");
    expect(() => reduce([evidence(1, 201)])).toThrow("invalid_receipt_order");
    expect(() => reduce([evidence(1, 0, { sessionIssuedAt: at(1) })])).toThrow("invalid_receipt_order");
    expect(() => reduce([evidence(1, 0, { receivedSequence: 0 })])).toThrow("invalid_evidence_identity");
    expect(() => reduce([evidence(1, 0, { timeZone: "unknown" })])).toThrow("invalid_time_zone");
    expect(() => reduce([evidence(1, 0, { timeZone: undefined })])).toThrow("invalid_time_zone");
    expect(() => reduce([evidence(1, 0, { answeredAt: "2026-02-30T12:00:00Z" })])).toThrow("invalid_instant");
    expect(() => reduce([evidence(1, 0, { variantKey: "" })])).toThrow("invalid_evaluation_evidence");
    expect(() => reduce([], { policyVersion: "future" as typeof EVIDENCE_POLICY_VERSION })).toThrow("unsupported_evidence_policy");
  });
  it("replays identical immutable evidence without mutating inputs", () => {
    const events = retained();
    const original = structuredClone(events);
    events.forEach(Object.freeze); Object.freeze(events);
    const snapshot = reduce(events);
    expect(reduce(structuredClone(original))).toEqual(snapshot);
    expect(events).toEqual(original);
  });
  it("assistance, skips and reinforcement cannot manufacture mastery across many histories", () => {
    for (let seed = 1; seed <= 30; seed++) {
      const events: AcceptedEvidence[] = [];
      for (let i = 1; i <= 20; i++) {
        const kind = (seed * i) % 3;
        events.push(evidence(i, i * 7, kind === 0 ? { assisted: true } : kind === 1 ?
          { kind: "exposure" } : { kind: "reinforcement" }));
      }
      expect(reduce(events)).toMatchObject({ state: "new", everMastered: false, qualifyingChecks: [] });
    }
  });
});

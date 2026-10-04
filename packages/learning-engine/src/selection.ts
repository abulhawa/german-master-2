import type { LearningState } from "./evidence";

export const SELECTION_POLICY_VERSION = "mixed-selection-v1";
export type SelectionCandidate = {
  targetId: string; exerciseId: string; revision: number;
  state: LearningState; dueAt: string | null; lastInformativeAt: string | null;
};
type Pool = "weak" | "due" | "new" | "extra";
/** Small-catalog policy: one question per target, no invented variants or repetition.
 * Deadlines, not cached isDue flags, determine eligibility at selection time. */
export function selectQuestions(candidates: readonly SelectionCandidate[], count: number, now: string) {
  const clock = Date.parse(now);
  if (!Number.isFinite(clock) || !Number.isInteger(count) || count < 1) throw Error("invalid_selection_context");
  const deadline = (value: string | null) => {
    if (value === null) return null;
    const parsed = Date.parse(value);
    if (!Number.isFinite(parsed)) throw Error("invalid_selection_timestamp");
    return parsed;
  };
  const pool = (c: SelectionCandidate): Pool => {
    const due = deadline(c.dueAt);
    if (due !== null && due <= clock) return c.state === "needs_practice" ? "weak" : "due";
    return c.state === "new" ? "new" : "extra";
  };
  const urgency = (c: SelectionCandidate) => Math.min(30, Math.max(0, (clock - (deadline(c.dueAt) ?? clock)) / 86400000));
  const ranked = [...candidates].sort((a, b) => urgency(b) - urgency(a) ||
    (deadline(a.lastInformativeAt) ?? -Infinity) - (deadline(b.lastInformativeAt) ?? -Infinity) ||
    a.targetId.localeCompare(b.targetId) || a.exerciseId.localeCompare(b.exerciseId) || a.revision - b.revision);
  const pools: Record<Pool, SelectionCandidate[]> = { weak: [], due: [], new: [], extra: [] };
  const seen = new Set<string>();
  for (const c of ranked) {
    if (seen.has(c.targetId)) continue;
    seen.add(c.targetId); pools[pool(c)].push(c);
  }
  const selected: (SelectionCandidate & { role: "assessment" | "reinforcement" })[] = [];
  const take = (name: Pool, limit: number) => {
    while (limit-- > 0 && pools[name].length && selected.length < count)
      selected.push({ ...pools[name].shift()!, role: name === "extra" ? "reinforcement" : "assessment" });
  };
  // Reserve roughly 8/5/2 slots at 15 questions; reallocate unavailable pools.
  const newSlots = Math.floor(count * 2 / 15), dueSlots = Math.floor(count * 5 / 15);
  take("weak", count - newSlots - dueSlots);
  take("due", dueSlots); take("new", newSlots);
  for (const name of ["weak", "due", "new", "extra"] as const) take(name, count);
  // Start with manageable material and interleave weaknesses when the catalog permits.
  const ordered: typeof selected = [];
  let consecutiveWeak = 0;
  while (selected.length) {
    const manageable = selected.findIndex(c => c.state !== "needs_practice");
    const index = (ordered.length === 0 || consecutiveWeak >= 3) && manageable >= 0 ? manageable : 0;
    const [next] = selected.splice(index, 1);
    ordered.push(next);
    consecutiveWeak = next.state === "needs_practice" ? consecutiveWeak + 1 : 0;
  }
  return ordered;
}

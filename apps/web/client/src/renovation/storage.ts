import { z } from "zod";
import { AnswerSchema, AttemptSchema, EvaluationSchema, ExposureEventSchema, SessionRequestSchema, FocusedSessionRequestSchema, SessionSchema, ConfirmedTargetSchema, type Answer, type Exercise } from "@german-master/contracts";
import { SyncCursorReset, type LearnerApi } from "./api";
import { SessionCompletionRequestSchema, SessionCompletionReceiptSchema } from '@german-master/contracts';

// One atomic record scoped to the public local fixture; never used for production accounts.
export const STORAGE_KEY = "german-master-v2:local-fixture:journey-v1";
const DraftSchema = z.union([AnswerSchema, z.object({ type: z.literal("word_order"), tokenIds: z.array(z.string()) })]);
export const JourneySchema = z.object({
  version: z.literal(1), deviceId: z.string().uuid(), locale: z.enum(["en", "de"]), theme: z.enum(["system", "light", "dark"]),
  confirmed: z.object({ targets: z.array(ConfirmedTargetSchema), cursor: z.string(), generatedAt: z.string() }).nullable(),
  practice: z.object({ request: z.union([SessionRequestSchema, FocusedSessionRequestSchema]), session: SessionSchema.nullable(), index: z.number().int().min(0),
    draft: DraftSchema.nullable(), assisted: z.boolean(), pending: AttemptSchema.nullable(), evaluation: EvaluationSchema.nullable(),
    rejected: z.boolean(), confirmedCount: z.number().int().min(0), correctCount: z.number().int().min(0),
    pendingExposure: ExposureEventSchema.nullable().default(null), skippedCount: z.number().int().min(0).default(0),
    completion: SessionCompletionRequestSchema.nullable().optional(), completionReceipt: SessionCompletionReceiptSchema.nullable().optional(),
  }).nullable(),
});
export type Journey = z.infer<typeof JourneySchema>;
export type JourneyStorage = Pick<Storage, "getItem" | "setItem">;
// Access the browser property inside the guarded operations; privacy settings can throw on access.
export const browserStorage: JourneyStorage = {
  getItem: key => window.localStorage.getItem(key),
  setItem: (key, value) => window.localStorage.setItem(key, value),
};
export function emptyJourney(): Journey {
  return { version: 1, deviceId: crypto.randomUUID(), locale: "en", theme: "system", confirmed: null, practice: null };
}
export function readJourney(storage: JourneyStorage): Journey {
  const raw = storage.getItem(STORAGE_KEY);
  return raw ? JourneySchema.parse(JSON.parse(raw)) : emptyJourney();
}
export function saveJourney(storage: JourneyStorage, state: Journey) {
  storage.setItem(STORAGE_KEY, JSON.stringify(JourneySchema.parse(state)));
}
export function readyAnswer(exercise: Exercise, draft: Answer | null): Answer | null {
  if (!draft || draft.type !== exercise.type) return null;
  switch (draft.type) {
    case "short_answer": return draft.text.trim() ? draft : null;
    case "choice": return exercise.type === "choice" && exercise.options.some(o => o.id === draft.optionId) ? draft : null;
    case "word_order": return exercise.type === "word_order" && draft.tokenIds.length === exercise.tokens.length ? draft : null;
    default: return (exercise.type === "cloze" || exercise.type === "multi_slot") && exercise.slots.every(s => draft.values.some(v => v.slotId === s.id && v.text.trim())) ? draft : null;
  }
}
export async function snapshot(api: LearnerApi): Promise<NonNullable<Journey["confirmed"]>> {
  let page = await api.targets();
  const { syncCursor: cursor, generatedAt } = page;
  const targets = [...page.targets];
  const seen = new Set<string>();
  while (page.nextPageCursor) {
    if (seen.has(page.nextPageCursor)) throw Error("Repeated snapshot cursor");
    seen.add(page.nextPageCursor);
    page = await api.targets(page.nextPageCursor);
    if (page.syncCursor !== cursor || page.generatedAt !== generatedAt) throw Error("Snapshot changed during pagination");
    targets.push(...page.targets);
  }
  return { targets, cursor, generatedAt };
}
export async function pull(api: LearnerApi, confirmed: NonNullable<Journey["confirmed"]>, commit: (value: NonNullable<Journey["confirmed"]>) => void) {
  let next = confirmed;
  const seen = new Set<string>();
  for (;;) {
    let page;
    try { page = await api.sync(next.cursor); }
    catch (error) {
      if (!(error instanceof SyncCursorReset)) throw error;
      const fresh = await snapshot(api);
      commit(fresh); // Replace only confirmed data after every snapshot page succeeds.
      return fresh;
    }
    const targets = new Map(next.targets.map(t => [t.targetId, t]));
    for (const change of page.changes) {
      const prior = targets.get(change.target.targetId);
      if (!prior || change.target.lastSequence >= prior.lastSequence) targets.set(change.target.targetId, change.target);
    }
    next = { ...next, targets: [...targets.values()], cursor: page.nextCursor };
    commit(next); // Payload and cursor are saved together before another page is requested.
    if (!page.hasMore) return next;
    if (seen.has(page.nextCursor)) throw Error("Repeated sync cursor");
    seen.add(page.nextCursor);
  }
}

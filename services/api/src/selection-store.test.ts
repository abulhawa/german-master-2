import { beforeAll, afterAll, it, expect } from "vitest";
import { PGlite } from "@electric-sql/pglite";
import { randomUUID } from "node:crypto";
import type { Attempt, SessionRequest } from "@german-master/contracts";
import { FoundationStore } from "./store";
import answers from "../../../contracts/v2/examples/attempt-batch.json";

let db: PGlite, store: FoundationStore;
let now = "2026-10-04T12:00:00Z";
const user = randomUUID(), other = randomUUID();
const request = (count = 1): SessionRequest => ({ apiVersion: "v2", requestId: randomUUID(), questionCount: count,
  capabilities: ["short_answer@1", "choice@1", "cloze@1", "word_order@1", "multi_slot@1"] });
beforeAll(async () => { db = new PGlite(); store = new FoundationStore(db, () => new Date(now)); await store.initialize(); });
afterAll(async () => { await db.close(); });

it("selects different queues from owned persisted histories and refreshes due eligibility without ingestion", async () => {
  const initial = await store.createSession(user, request(5));
  const choice = initial.questions[1];
  const failed: Attempt = { ...answers.attempts[1], attemptId: randomUUID(), sessionQuestionId: choice.id,
    assistance: [], answer: { type: "choice", optionId: "der" }, answeredAt: now };
  expect((await store.submit(user, failed, randomUUID())).status).toBe("accepted");
  const short = initial.questions[0];
  expect((await store.submit(user, { ...failed, attemptId: randomUUID(), sessionQuestionId: short.id,
    answer: { type: "short_answer", text: "Berufe" } }, randomUUID())).status).toBe("accepted");
  // Both schedules are still future, so a genuinely new target wins.
  expect((await store.createSession(user, request())).questions[0].exercise.targetId).toBe(initial.questions[2].exercise.targetId);
  expect((await store.createSession(other, request())).questions[0].exercise.targetId).toBe(short.exercise.targetId);
  const before = (await db.query<{ snapshot: { isDue: boolean } }>("SELECT snapshot FROM gm.learner_target_state WHERE user_id=$1", [user])).rows;
  expect(before.every(r => !r.snapshot.isDue)).toBe(true);
  now = "2026-10-04T22:00:00Z"; // Saved Europe/Berlin next local day, exactly due.
  expect((await store.createSession(user, request())).questions[0].exercise.targetId).toBe(choice.exercise.targetId);
  const mixed = await store.createSession(user, request(3));
  expect(mixed.questions.map(q => q.exercise.targetId)).toEqual([short.exercise.targetId, choice.exercise.targetId, initial.questions[2].exercise.targetId]);
  expect((await db.query<{ snapshot: unknown }>("SELECT snapshot FROM gm.learner_target_state WHERE user_id=$1", [user])).rows).toEqual(before.map(r => ({ snapshot: r.snapshot })));
  expect(JSON.stringify(mixed)).not.toContain("acceptedAnswer");
});

it("pins replay across clock/history/availability changes and serializes concurrent creation", async () => {
  const input = request(3);
  const [first, duplicate] = await Promise.all([store.createSession(user, input), store.createSession(user, input)]);
  expect(duplicate).toEqual(first);
  now = "2026-11-04T12:00:00Z";
  await db.query("UPDATE gm.learning_target SET status='retired' WHERE id=$1", [first.questions[0].exercise.targetId]);
  expect(await store.createSession(user, input)).toEqual(first);
  await expect(store.createSession(user, { ...input, questionCount: 2 })).rejects.toMatchObject({ code: "session_conflict" });
  const fresh = await store.createSession(user, request(3));
  expect(fresh.questions.some(q => q.exercise.targetId === first.questions[0].exercise.targetId)).toBe(false);
  const pinned = (await db.query<{ engine_version: string }>("SELECT engine_version FROM gm.practice_session WHERE id=$1", [fresh.id])).rows[0];
  expect(pinned.engine_version).toBe("mixed-selection-v2");
});

it("filters capabilities before allocation and rolls back unavailable sessions", async () => {
  const input = { ...request(2), capabilities: ["choice@1" as const] };
  await expect(store.createSession(other, input)).rejects.toMatchObject({ code: "insufficient_content" });
  expect((await db.query("SELECT id FROM gm.practice_session WHERE user_id=$1 AND request_id=$2", [other, input.requestId])).rows).toHaveLength(0);
  const session = await store.createSession(other, { ...input, requestId: randomUUID(), questionCount: 1 });
  expect(session.questions[0].exercise.type).toBe("choice");
  const roles = (await db.query<{ evidence_role: string }>("SELECT evidence_role FROM gm.session_question WHERE session_id=$1", [session.id])).rows;
  expect(roles[0].evidence_role).toBe("assessment");
});

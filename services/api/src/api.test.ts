import { beforeAll, afterAll, describe, it, expect } from "vitest";
import { PGlite } from "@electric-sql/pglite";
import { randomUUID } from "node:crypto";
import { mkdtemp, rm } from "node:fs/promises";
import { join } from "node:path";
import { tmpdir } from "node:os";
import type { AddressInfo } from "node:net";
import { AttemptBatchResponseSchema, SessionSchema, type Session, type Attempt, type SessionRequest } from "@german-master/contracts";
import answers from "../../../contracts/v2/examples/attempt-batch.json";
import { FoundationStore, canonical } from "./store";
import { createApi } from "./server";

const user = "00000000-0000-4000-8000-000000000010";
const other = "00000000-0000-4000-8000-000000000011";
const request = (): SessionRequest => ({ apiVersion: "v2", requestId: randomUUID(), questionCount: 5,
  capabilities: ["short_answer@1", "choice@1", "cloze@1", "word_order@1", "multi_slot@1"] });
const attemptFor = (session: Session, index = 0): Attempt => {
  const question = session.questions.find(q => q.exercise.id === `00000000-0000-4000-8000-${String(100 + index).padStart(12, "0")}`)!;
  return { ...answers.attempts[index], attemptId: randomUUID(), sessionQuestionId: question.id,
    answer: answers.attempts[index].answer as Attempt["answer"], assistance: [] };
};

describe("isolated PostgreSQL-backed HTTP session", () => {
  let db: PGlite;
  let store: FoundationStore;
  let server: ReturnType<typeof createApi>;
  let base: string;
  let session: Session;
  let fullAttempts: Attempt[];
  let fullResults: unknown;
  const post = async (path: string, input: unknown, token: string = user) => {
    const response = await fetch(base + path, { method: "POST", headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` }, body: JSON.stringify(input) });
    return { status: response.status, body: await response.json() };
  };
  beforeAll(async () => {
    db = new PGlite(); store = new FoundationStore(db, () => new Date("2026-10-03T12:00:00Z"));
    await store.initialize();
    server = createApi(store, async req => [user, other].find(id => req.headers.authorization === `Bearer ${id}`) ?? null);
    await new Promise<void>(resolve => server.listen(0, "127.0.0.1", resolve));
    base = `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
  });
  afterAll(async () => { await new Promise<void>(resolve => server.close(() => resolve())); await db.close(); });
  it("creates owned solution-free questions, replays a request and conflicts on changes", async () => {
    const input = request();
    const created = await post("/v2/sessions", input);
    expect(created.status).toBe(200);
    session = SessionSchema.parse(created.body);
    expect(JSON.stringify(created.body)).not.toContain("acceptedAnswer");
    expect(new Set(session.questions.map(q => q.id)).size).toBe(5);
    expect((await post("/v2/sessions", input)).body).toEqual(created.body);
    expect((await post("/v2/sessions", { ...input, questionCount: 4 })).status).toBe(409);
    expect((await post("/v2/sessions", { ...request(), capabilities: ["choice@1"] })).body.code).toBe("insufficient_content");
    expect((await post("/v2/sessions", input, other)).body.id).not.toBe(session.id);
  });
  it("grades all five answers and returns identical persisted evaluations on retries", async () => {
    fullAttempts = session.questions.map((_, i) => attemptFor(session, i));
    fullAttempts[2].answer = { type: "cloze", values: [{ slotId: "preposition", text: "in das" }] };
    fullAttempts[4].assistance = ["hint"];
    const result = await post("/v2/attempts:batch", { apiVersion: "v2", attempts: fullAttempts });
    expect(result.status).toBe(200);
    const parsed = AttemptBatchResponseSchema.parse(result.body);
    expect(parsed.acknowledgments.map(a => a.status)).toEqual(Array(5).fill("accepted"));
    expect(parsed.acknowledgments.map(a => a.status !== "rejected" && a.evaluation.outcome)).toEqual(Array(5).fill("correct"));
    expect(parsed.acknowledgments[4].status !== "rejected" && parsed.acknowledgments[4].evaluation.assisted).toBe(true);
    fullResults = parsed;
    const replay = AttemptBatchResponseSchema.parse((await post("/v2/attempts:batch", { apiVersion: "v2", attempts: fullAttempts })).body);
    expect(replay.acknowledgments.map(a => ({ ...a, status: "accepted" }))).toEqual(parsed.acknowledgments);
    expect((await db.query<{ n: number }>("SELECT count(*)::int AS n FROM gm.attempt")).rows[0].n).toBe(5);
    expect((await db.query<{ status: string }>("SELECT status FROM gm.practice_session WHERE id=$1", [session.id])).rows[0].status).toBe("completed");
  });
  it("rejects altered replay, second question submission, foreign ownership and wrong revision independently", async () => {
    const fresh = SessionSchema.parse((await post("/v2/sessions", request())).body);
    const items = [
      { ...fullAttempts[0], answer: { type: "short_answer", text: "wrong" } },
      attemptFor(session),
      { ...attemptFor(fresh), exerciseRevision: 99 },
      { ...attemptFor(fresh, 1), answer: { type: "choice", optionId: "not-a-choice" } },
      attemptFor(fresh, 2),
    ];
    const response = (await post("/v2/attempts:batch", { apiVersion: "v2", attempts: items })).body;
    expect(response.acknowledgments.map((a: any) => a.error?.code ?? a.status)).toEqual([
      "attempt_conflict", "question_already_answered", "revision_mismatch", "unknown_option", "accepted",
    ]);
    const foreign = (await post("/v2/attempts:batch", { apiVersion: "v2", attempts: [fullAttempts[0]] }, other)).body;
    expect(foreign.acknowledgments[0].error.code).toBe("question_unavailable");
    expect(JSON.stringify(foreign)).not.toContain("evaluation");
    expect(canonical({ b: 1, a: 2 })).toBe(canonical({ a: 2, b: 1 }));
  });
  it("authenticates subjects and rejects client grades, malformed JSON and unbounded requests", async () => {
    expect((await post("/v2/sessions", request(), "invalid")).status).toBe(401);
    expect((await post("/v2/attempts:batch", { apiVersion: "v2", attempts: [{ ...fullAttempts[0], correct: true }] })).status).toBe(400);
    expect((await post("/v2/attempts:batch", { apiVersion: "v2", attempts: Array(51).fill(fullAttempts[0]) })).status).toBe(400);
    expect((await post("/v2/sessions", { ...request(), extra: "x".repeat(128 * 1024) })).status).toBe(413);
    const invalid = await fetch(base + "/v2/sessions", { method: "POST", headers: { "Content-Type": "application/json", Authorization: `Bearer ${user}` }, body: "{" });
    expect(invalid.status).toBe(400);
  });
  it("treats changed assistance as an attempt conflict and rejects repeated slot IDs", async () => {
    const changed = await store.submit(user, { ...fullAttempts[0], assistance: ["reveal"] }, randomUUID());
    expect(changed.status === "rejected" && changed.error.code).toBe("attempt_conflict");
    const fresh = await store.createSession(user, request());
    const malformed = { ...attemptFor(fresh, 4), answer: { type: "multi_slot" as const, values: [{ slotId: "du", text: "arbeitest" }, { slotId: "du", text: "arbeitet" }] } };
    const result = await store.submit(user, malformed, randomUUID());
    expect(result.status === "rejected" && result.error.code).toBe("invalid_slots");
    expect((await db.query("SELECT id FROM gm.attempt WHERE user_id=$1 AND id=$2", [user, malformed.attemptId])).rows).toHaveLength(0);
  });
  it("serializes simultaneous first submissions and replays the winner once", async () => {
    const fresh = await store.createSession(user, request());
    const first = attemptFor(fresh), second = attemptFor(fresh);
    const results = await Promise.all([store.submit(user, first, randomUUID()), store.submit(user, second, randomUUID())]);
    expect(results.map(r => r.status).sort()).toEqual(["accepted", "rejected"]);
    const retry = await store.submit(user, first, randomUUID());
    expect(retry.status).toBe("duplicate");
    expect(retry.status !== "rejected" && results[0].status !== "rejected" && retry.serverSequence === results[0].serverSequence).toBe(true);
  });
  it("rolls back answer and device when evaluation insertion fails, then accepts a retry", async () => {
    const fresh = await store.createSession(user, request());
    const input = { ...attemptFor(fresh), deviceId: randomUUID() };
    await db.exec("CREATE TRIGGER test_fail BEFORE INSERT ON gm.attempt_evaluation FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation()");
    await expect(store.submit(user, input, randomUUID())).rejects.toThrow("immutable row");
    expect((await db.query("SELECT id FROM gm.attempt WHERE user_id=$1 AND id=$2", [user, input.attemptId])).rows).toHaveLength(0);
    expect((await db.query("SELECT id FROM gm.device WHERE user_id=$1 AND id=$2", [user, input.deviceId])).rows).toHaveLength(0);
    await db.exec("DROP TRIGGER test_fail ON gm.attempt_evaluation");
    expect((await store.submit(user, input, randomUUID())).status).toBe("accepted");
  });
  it("enforces immutable revisions and cross-owner foreign keys in PostgreSQL", async () => {
    await expect(db.exec("UPDATE gm.exercise_revision SET revision=2")).rejects.toThrow("immutable row");
    await expect(db.exec("DELETE FROM gm.attempt_evaluation")).rejects.toThrow("immutable row");
    await expect(db.query("INSERT INTO gm.attempt (user_id,id,question_id,device_id,payload,received_at) VALUES ($1,$2,$3,$4,$5,now())", [other, randomUUID(), session.questions[0].id, fullAttempts[0].deviceId, fullAttempts[0]])).rejects.toThrow();
    expect(fullResults).toBeTruthy();
  });
});

it("preserves accepted acknowledgments across a local database close/reopen", async () => {
  const directory = await mkdtemp(join(tmpdir(), "gm-foundation-test-"));
  let db = new PGlite(directory);
  try {
    let store = new FoundationStore(db);
    await store.initialize();
    const sessionRequest = request();
    const session = await store.createSession(user, sessionRequest);
    const input = attemptFor(session);
    const accepted = await store.submit(user, input, randomUUID());
    await db.close();
    db = new PGlite(directory); store = new FoundationStore(db);
    await store.initialize();
    expect(await store.createSession(user, sessionRequest)).toEqual(session);
    expect(await store.submit(user, input, randomUUID())).toEqual({ ...accepted, status: "duplicate" });
  } finally { await db.close(); await rm(directory, { recursive: true, force: true }); }
});

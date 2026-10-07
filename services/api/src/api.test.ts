import { beforeAll, afterAll, describe, it, expect } from "vitest";
import { PGlite } from "@electric-sql/pglite";
import { randomUUID } from "node:crypto";
import { mkdtemp, rm } from "node:fs/promises";
import { join } from "node:path";
import { tmpdir } from "node:os";
import type { AddressInfo } from "node:net";
import { AttemptBatchResponseSchema, SessionSchema, type Session, type Attempt, type GuestAttachmentRequest, type SessionRequest } from "@german-master/contracts";
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
  it('rejects a mismatched or malformed captured subject before request parsing or mutation', async () => {
    const before = await store.profile(other);
    for (const expected of [user, 'invalid-subject']) {
      const response = await fetch(base + '/v2/profile', {method:'POST',headers:{Authorization:`Bearer ${other}`,'Content-Type':'application/json','X-Learner-Subject':expected},body:'invalid JSON deliberately not read'});
      expect(response.status).toBe(409); expect((await response.json()).code).toBe('account_changed');
    }
    expect(await store.profile(other)).toEqual(before);
    const response = await fetch(base + '/v2/profile', {headers:{Authorization:`Bearer ${user}`,'X-Learner-Subject':other}});
    expect(response.status).toBe(409);
    const allowed = await fetch(base + '/v2/profile', {headers:{Authorization:`Bearer ${user}`,'X-Learner-Subject':user.toUpperCase()}});
    expect(allowed.status).toBe(200);
  });
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
  it("freezes full completion counts and replays after reinitialization without learning credit", async () => {
    const request = {apiVersion:'v2' as const, requestId:randomUUID(), mode:'full' as const};
    const before = await db.query('SELECT * FROM gm.accepted_evidence ORDER BY received_sequence');
    const result = await post(`/v2/sessions/${session.id}/complete`, request);
    expect(result.status).toBe(200);
    expect(result.body).toMatchObject({requestId:request.requestId,sessionId:session.id,mode:'full',plannedCount:5,gradedCount:5,skippedCount:0,correctCount:5});
    await store.initialize();
    expect(await post(`/v2/sessions/${session.id}/complete`,request)).toEqual(result);
    expect((await post(`/v2/sessions/${session.id}/complete`,{...request,mode:'partial'})).body.code).toBe('completion_conflict');
    expect((await post(`/v2/sessions/${session.id}/complete`,request,other)).status).toBe(404);
    expect((await db.query('SELECT * FROM gm.accepted_evidence ORDER BY received_sequence')).rows).toEqual(before.rows);
  });
  it("rejects premature full completion and freezes honest partial counts including Skip", async () => {
    const fresh = await store.createSession(user, request());
    const completion = {apiVersion:'v2' as const,requestId:randomUUID(),mode:'full' as const};
    const path = `/v2/sessions/${fresh.id}/complete`;
    expect((await post(path,completion)).body.code).toBe('session_incomplete');
    const answer = attemptFor(fresh);
    await store.submit(user,answer,randomUUID());
    const question = fresh.questions.find(q=>q.id !== answer.sessionQuestionId)!;
    await store.expose(user,{eventId:randomUUID(),sessionQuestionId:question.id,exerciseRevision:question.exercise.revision,deviceId:answer.deviceId,disposition:'skip',occurredAt:'2026-10-03T12:00:00Z'},randomUUID());
    const result = await post(path,{...completion,mode:'partial'});
    expect(result.body).toMatchObject({plannedCount:5,gradedCount:1,skippedCount:1,correctCount:1});
    const remaining = fresh.questions.find(q=>q.id !== question.id && q.id !== answer.sessionQuestionId)!;
    const late = await store.submit(user,{...answer,attemptId:randomUUID(),sessionQuestionId:remaining.id,exerciseRevision:remaining.exercise.revision},randomUUID());
    expect(late.status === 'rejected' && late.error.code).toBe('session_ended');
    expect((await store.submit(user,answer,randomUUID())).status).toBe('duplicate');
    expect((await post(path,{...completion,mode:'partial'})).body).toEqual(result.body);
    expect((await post(path,{...completion,requestId:randomUUID(),mode:'partial'})).status).toBe(409);
    const next = await store.createSession(user,request());
    expect((await post(`/v2/sessions/${next.id}/complete`,{...completion,mode:'partial'})).status).toBe(409);
    expect((await post(path,{...completion,gradedCount:99})).status).toBe(400);
  });
  it("rolls back completion receipt with a failed session status update, then serializes retries", async () => {
    const fresh = await store.createSession(user,request());
    const input = {apiVersion:'v2' as const,requestId:randomUUID(),mode:'partial' as const};
    await db.exec(`CREATE TRIGGER test_completion_failure BEFORE UPDATE ON gm.practice_session FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation()`);
    try { await expect(store.complete(user,fresh.id,input)).rejects.toThrow(); }
    finally { await db.exec('DROP TRIGGER test_completion_failure ON gm.practice_session'); }
    expect((await db.query('SELECT id FROM gm.session_completion WHERE session_id=$1',[fresh.id])).rows).toHaveLength(0);
    const result = await Promise.all([store.complete(user,fresh.id,input),store.complete(user,fresh.id,input)]);
    expect(result[0]).toEqual(result[1]);
    expect((await db.query('SELECT status FROM gm.practice_session WHERE id=$1',[fresh.id])).rows[0]).toEqual({status:'ended'});
    expect((await db.query('SELECT id FROM gm.session_completion WHERE session_id=$1',[fresh.id])).rows).toHaveLength(1);
  });
  it("serves authenticated immutable packs while ordinary sessions remain solution-free", async () => {
    const input=request();
    expect((await post('/v2/packs',input,'invalid')).status).toBe(401);
    const result=await post('/v2/packs',input);
    expect(result.status).toBe(200); expect(result.body.sessions).toHaveLength(2);
    expect(result.body.rubrics).toHaveLength(5);
    expect(await post('/v2/packs',input)).toEqual(result);
    expect((await post('/v2/packs',{...input,questionCount:4})).body.code).toBe('pack_conflict');
    expect((await post('/v2/packs',{...input,evaluatorVersion:'other'})).status).toBe(400);
    const normal=await post('/v2/sessions',request());
    expect(JSON.stringify(normal.body)).not.toContain('acceptedAnswers');
  });
  it("attaches guest attempts only after published revision validation and replays safely", async () => {
    const release=(await db.query<{id:string}>("SELECT id FROM gm.content_release LIMIT 1")).rows[0].id;
    await db.query("UPDATE gm.content_release SET status='published',published_at=$1 WHERE id=$2",["2026-10-03T10:00:00Z",release]);
    const source=await store.createSession(user,request());
    const first=source.questions.find(q=>q.exercise.type==="short_answer")!,second=source.questions.find(q=>q.exercise.type==="choice")!;
    const deviceId=randomUUID();
    const guest:GuestAttachmentRequest={apiVersion:"v2",requestId:randomUUID(),deviceId,attempts:[
      {attemptId:randomUUID(),contentReleaseId:release,exerciseId:first.exercise.id,exerciseRevision:first.exercise.revision,
        answer:{type:"short_answer",text:"Berufe"},assistance:[],answeredAt:"2026-10-03T11:00:00Z",clientSequence:0},
      {attemptId:randomUUID(),contentReleaseId:release,exerciseId:second.exercise.id,exerciseRevision:second.exercise.revision,
        answer:{type:"choice",optionId:"dem"},assistance:["hint"],answeredAt:"2026-10-03T11:01:00Z",clientSequence:1},
    ]};
    const attached=await post("/v2/guest-attempts:attach",guest);
    expect(attached.status).toBe(200);
    const parsed=AttemptBatchResponseSchema.parse(attached.body);
    expect(parsed.acknowledgments.map(a=>a.status)).toEqual(["accepted","accepted"]);
    expect(parsed.acknowledgments.map(a=>a.status!=="rejected"&&a.evaluation.outcome)).toEqual(["correct","correct"]);
    const evidence=(await db.query<{n:number}>("SELECT count(*)::int AS n FROM gm.accepted_evidence WHERE user_id=$1",[user])).rows[0].n;
    const replay=AttemptBatchResponseSchema.parse((await post("/v2/guest-attempts:attach",{...guest,requestId:randomUUID()})).body);
    expect(replay.acknowledgments.map(a=>a.status)).toEqual(["duplicate","duplicate"]);
    expect((await db.query<{n:number}>("SELECT count(*)::int AS n FROM gm.accepted_evidence WHERE user_id=$1",[user])).rows[0].n).toBe(evidence);
    const changed={...guest,requestId:randomUUID(),attempts:[{...guest.attempts[0],answer:{type:"short_answer" as const,text:"changed"}}]};
    expect((await post("/v2/guest-attempts:attach",changed)).body.acknowledgments[0].error.code).toBe("attempt_conflict");
    const stale={...guest,requestId:randomUUID(),attempts:[{...guest.attempts[0],attemptId:randomUUID(),exerciseRevision:99}]};
    expect((await post("/v2/guest-attempts:attach",stale)).body.acknowledgments[0].error.code).toBe("revision_unavailable");
    expect((await post("/v2/guest-attempts:attach",{...guest,evaluation:{outcome:"correct"}})).status).toBe(400);
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

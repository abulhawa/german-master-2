import { beforeAll, afterAll, it, expect } from "vitest";
import { PGlite } from "@electric-sql/pglite";
import { randomUUID } from "node:crypto";
import { mkdtemp, rm } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { LearnerProfileSchema, type ProfileRequest, type Attempt } from "@german-master/contracts";
import { FoundationStore } from "./store";
import { createApi } from "./server";
import answers from "../../../contracts/v2/examples/attempt-batch.json";
let db: PGlite, store: FoundationStore, server: ReturnType<typeof createApi>, base: string, directory: string;
const user = randomUUID(), other = randomUUID();
let now = "2026-10-04T12:00:00Z";
const request = (): ProfileRequest => ({ apiVersion: "v2", requestId: randomUUID(), expectedRevision: 0,
  preferences: { locale: "de", timezone: "America/New_York", level: "B1", sessionQuestionCount: 5 } });
const sessionRequest = () => ({ apiVersion: "v2" as const, requestId: randomUUID(), questionCount: 1, capabilities: ["short_answer@1" as const] });
beforeAll(async () => {
  directory = await mkdtemp(join(tmpdir(), "gm-profile-"));
  db = new PGlite(directory); store = new FoundationStore(db, () => new Date(now)); await store.initialize();
  server = createApi(store, async r => r.headers.authorization === "Bearer fixture" ? user : null);
  await new Promise<void>(resolve => server.listen(0, "127.0.0.1", resolve));
  base = `http://127.0.0.1:${(server.address() as { port: number }).port}`;
});
afterAll(async () => { await new Promise<void>(resolve => server.close(() => resolve())); await db.close(); await rm(directory, { recursive: true }); });
it("requires authenticated strict profile transport and validates timezones without writes", async () => {
  expect((await fetch(`${base}/v2/profile`)).status).toBe(401);
  const headers = { Authorization: "Bearer fixture", "Content-Type": "application/json" };
  expect((await fetch(`${base}/v2/profile?owner=${other}`, { headers })).status).toBe(400);
  const read = await fetch(`${base}/v2/profile`, { headers });
  expect(read.headers.get("cache-control")).toBe("no-store");
  expect(LearnerProfileSchema.parse(await read.json())).toMatchObject({ revision: 0, setupCompleted: false });
  for (const input of [{ ...request(), userId: other }, { ...request(), preferences: { ...request().preferences, level: "C1" } },
    ...["Invalid/Zone", "+02:00", " Europe/Berlin "].map(timezone => ({ ...request(), preferences: { ...request().preferences, timezone } }))]) {
    expect((await fetch(`${base}/v2/profile`, { method: "POST", headers, body: JSON.stringify(input) })).status).toBe(400);
  }
  expect((await store.profile(user)).revision).toBe(0);
  const response = await fetch(`${base}/v2/profile`, { method: "POST", headers, body: JSON.stringify(request()) });
  expect(LearnerProfileSchema.parse(await response.json())).toMatchObject({ revision: 1, setupCompleted: true });
  expect((await store.profile(other)).revision).toBe(0);
});
it("serializes revision conflicts and preserves original replay after later writes and restart", async () => {
  const owner = randomUUID(); const input = request();
  const outcomes = await Promise.allSettled([store.saveProfile(owner, input), store.saveProfile(owner, { ...input, requestId: randomUUID() })]);
  expect(outcomes.filter(r => r.status === "fulfilled")).toHaveLength(1);
  expect(outcomes.find(r => r.status === "rejected")).toMatchObject({ reason: { code: "profile_revision_conflict" } });
  const original = await store.saveProfile(owner, input);
  await expect(store.saveProfile(owner, { ...input, preferences: { ...input.preferences, locale: "en" } })).rejects.toMatchObject({ code: "profile_request_conflict" });
  const second = await store.saveProfile(owner, { ...request(), expectedRevision: 1, preferences: { ...input.preferences, timezone: "Asia/Tokyo" } });
  expect(await store.saveProfile(owner, input)).toEqual(original);
  expect(await store.profile(owner)).toEqual(second);
  await db.close(); db = new PGlite(directory); store = new FoundationStore(db, () => new Date(now)); await store.initialize();
  expect(await store.saveProfile(owner, input)).toEqual(original);
  expect(await store.profile(owner)).toEqual(second);
});
it("rolls back preference writes when saving the replay record fails", async () => {
  const owner = randomUUID(); await store.profile(owner);
  await db.exec("CREATE TRIGGER fail_profile_insert BEFORE INSERT ON gm.profile_request FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation()");
  await expect(store.saveProfile(owner, request())).rejects.toThrow();
  expect((await store.profile(owner)).revision).toBe(0);
  await db.exec("DROP TRIGGER fail_profile_insert ON gm.profile_request");
});
it("preserves session replay and historical timezone evidence while bounding only new content", async () => {
  const owner = randomUUID(); await store.saveProfile(owner, request());
  const input = sessionRequest(); const session = await store.createSession(owner, input);
  const attempt: Attempt = { ...answers.attempts[0], attemptId: randomUUID(), sessionQuestionId: session.questions[0].id,
    answer: { type: "short_answer", text: "Berufe" }, answeredAt: now, assistance: [] };
  expect((await store.submit(owner, attempt, randomUUID())).status).toBe("accepted");
  const before = await store.rebuild(owner, session.questions[0].exercise.targetId);
  await store.saveProfile(owner, { ...request(), expectedRevision: 1, preferences: { ...request().preferences, level: "B2", timezone: "Asia/Tokyo" } });
  expect(await store.rebuild(owner, session.questions[0].exercise.targetId)).toEqual(before);
  expect(await store.createSession(owner, input)).toEqual(session);
  const catalog = await store.catalog(owner);
  expect(catalog.targets.filter(t => t.availableQuestionCount > 0)).toHaveLength(1);
  const repeat = await store.createSession(owner, sessionRequest());
  expect((await db.query<{ evidence_role: string }>("SELECT evidence_role FROM gm.session_question WHERE id=$1", [repeat.questions[0].id])).rows[0].evidence_role).toBe("reinforcement");
  now = "2026-10-04T12:01:00Z";
  expect((await store.submit(owner, { ...attempt, attemptId: randomUUID(), sessionQuestionId: repeat.questions[0].id, answeredAt: now }, randomUUID())).status).toBe("accepted");
  const events = await db.query<{ payload: { timeZone: string } }>("SELECT payload FROM gm.accepted_evidence WHERE user_id=$1 ORDER BY received_sequence", [owner]);
  expect(events.rows.map(r => r.payload.timeZone)).toEqual(["America/New_York", "Asia/Tokyo"]);
  const fresh = randomUUID(); await store.saveProfile(fresh, { ...request(), preferences: { ...request().preferences, level: "B2" } });
  expect((await store.catalog(fresh)).targets.every(t => t.availableQuestionCount === 0)).toBe(true);
  await expect(store.createSession(fresh, sessionRequest())).rejects.toMatchObject({ code: "insufficient_content" });
});

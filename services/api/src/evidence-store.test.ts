import { beforeEach, afterEach, it, expect } from "vitest";
import { PGlite } from "@electric-sql/pglite";
import { randomUUID } from "node:crypto";
import { readFile, mkdtemp, rm } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";
import type { AddressInfo } from "node:net";
import { ExposureBatchSchema, ExposureBatchResponseSchema, type Attempt, type ExposureEvent } from "@german-master/contracts";
import { FoundationStore } from "./store";
import { createApi } from "./server";
import answers from "../../../contracts/v2/examples/attempt-batch.json";
import exposureCorpus from "../../../contracts/v2/examples/exposure-conformance.json";

const user = "00000000-0000-4000-8000-000000000010";
const other = "00000000-0000-4000-8000-000000000011";
let db: PGlite, store: FoundationStore;
let now: string;
const request = () => ({ apiVersion: "v2" as const, requestId: randomUUID(), questionCount: 1,
  capabilities: ["short_answer@1" as const] });
async function question(owner = user) {
  const session = await store.createSession(owner, request());
  return session.questions[0];
}
async function attempt(owner = user): Promise<Attempt> {
  const q = await question(owner);
  return { ...answers.attempts[0], attemptId: randomUUID(), sessionQuestionId: q.id,
    answer: { type: "short_answer", text: "Berufe" }, assistance: [], answeredAt: now };
}
async function exposure(disposition: "skip" | "exposure" = "skip"): Promise<ExposureEvent> {
  const q = await question();
  return { eventId: randomUUID(), sessionQuestionId: q.id, exerciseRevision: 1,
    deviceId: randomUUID(), disposition, occurredAt: now };
}
async function counts() {
  const result: Record<string, number> = {};
  for (const table of ["attempt", "attempt_evaluation", "exposure_event", "accepted_evidence", "learner_target_state", "review_schedule", "sync_change", "device"])
    result[table] = (await db.query<{ n: number }>(`SELECT count(*)::int AS n FROM gm.${table}`)).rows[0].n;
  return result;
}
beforeEach(async () => {
  now = "2026-10-04T12:00:00Z";
  db = new PGlite(); store = new FoundationStore(db, () => new Date(now)); await store.initialize();
});
afterEach(async () => { await db.close(); });

it("matches the shared exposure transport acceptance corpus", () => {
  for (const example of exposureCorpus)
    expect(ExposureBatchSchema.safeParse(example.batch).success, example.name).toBe(example.valid);
});

it("pins editorial identities, saved timezone, issuance and policy; rebuilds the exact projection", async () => {
  const input = await attempt();
  await db.query("UPDATE gm.learner_profile SET timezone='America/New_York' WHERE user_id=$1", [user]);
  expect((await store.submit(user, input, randomUUID())).status).toBe("accepted");
  const event = (await db.query<{ payload: any }>("SELECT payload FROM gm.accepted_evidence")).rows[0].payload;
  expect(event).toMatchObject({ timeZone: "America/New_York", sessionIssuedAt: new Date(now).toISOString(),
    variantKey: "beruf-plural", contextKey: "profession-noun", kind: "assessment",
    evaluationVersion: "deterministic-v1/de-nfc-trim-v1" });
  const snapshot = await store.rebuild(user, event.targetId);
  expect(snapshot.state).toBe("learning");
  expect(snapshot.qualifyingChecks).toHaveLength(1);
  expect(snapshot.schedule?.dueAt).toBe("2026-10-05T04:00:00.000Z");
  expect((await db.query<{ snapshot: unknown }>("SELECT snapshot FROM gm.learner_target_state")).rows[0].snapshot).toEqual(snapshot);
  expect((await db.query<{ payload: unknown }>("SELECT payload FROM gm.sync_change")).rows[0].payload).toEqual(snapshot);
  await db.query("UPDATE gm.learner_profile SET timezone='Asia/Tokyo' WHERE user_id=$1", [user]);
  expect(await store.rebuild(user, event.targetId)).toEqual(snapshot);
  await expect(db.exec("UPDATE gm.revision_evidence_identity SET variant_key='fake-diversity'")).rejects.toThrow("immutable row");
  await expect(db.exec("DELETE FROM gm.accepted_evidence")).rejects.toThrow("immutable row");
});

it("duplicate acknowledgments do not create evidence, projections or sync deltas", async () => {
  const input = await attempt();
  const accepted = await store.submit(user, input, randomUUID());
  const before = await counts();
  now = "2026-11-01T12:00:00Z";
  expect(await store.submit(user, input, randomUUID())).toEqual({ ...accepted, status: "duplicate" });
  expect(await counts()).toEqual(before);
});

it("rolls back the entire ingestion when the final sync insertion fails", async () => {
  const input = await attempt();
  const before = await counts();
  await db.exec("CREATE TRIGGER fail_sync BEFORE INSERT ON gm.sync_change FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation()");
  await expect(store.submit(user, input, randomUUID())).rejects.toThrow("immutable row");
  expect(await counts()).toEqual(before);
  expect((await db.query<{ status: string }>("SELECT status FROM gm.practice_session")).rows[0].status).toBe("active");
  await db.exec("DROP TRIGGER fail_sync ON gm.sync_change");
  expect((await store.submit(user, input, randomUUID())).status).toBe("accepted");
});

it("competing same-target sessions retain both receipts and only one qualifying date", async () => {
  const first = await attempt(), second = await attempt();
  const results = await Promise.all([store.submit(user, first, randomUUID()), store.submit(user, second, randomUUID())]);
  expect(results.map(r => r.status)).toEqual(["accepted", "accepted"]);
  const target = (await db.query<{ target_id: string }>("SELECT target_id FROM gm.accepted_evidence LIMIT 1")).rows[0].target_id;
  const snapshot = await store.rebuild(user, target);
  expect(snapshot.exposureCount).toBe(2);
  expect(snapshot.qualifyingChecks).toHaveLength(1);
  expect(snapshot.decisions.map(d => d.effect)).toEqual(["qualifying_success", "same_day"]);
  expect((await counts()).sync_change).toBe(2);
  expect((await db.query<{ snapshot: unknown }>("SELECT snapshot FROM gm.learner_target_state")).rows[0].snapshot).toEqual(snapshot);
});

it("assistance requests earlier practice without shortening the retention gate", async () => {
  const first = await attempt(); await store.submit(user, first, randomUUID());
  now = "2026-10-05T12:00:00Z";
  await store.submit(user, await attempt(), randomUUID());
  const target = (await db.query<{ target_id: string }>("SELECT target_id FROM gm.accepted_evidence LIMIT 1")).rows[0].target_id;
  const before = await store.rebuild(user, target);
  const assisted = { ...await attempt(), assistance: ["hint" as const] };
  await store.submit(user, assisted, randomUUID());
  const after = await store.rebuild(user, target);
  expect(after.nextQualifyingAt).toBe(before.nextQualifyingAt);
  expect(after.schedule!.dueAt < before.schedule!.dueAt).toBe(true);
  expect(after.qualifyingChecks).toHaveLength(2);
});

it("four spaced catalog checks remain improving without invented diversity", async () => {
  let target = "";
  for (const date of ["04", "05", "08", "19"]) {
    now = `2026-10-${date}T12:00:00Z`;
    const input = await attempt(); await store.submit(user, input, randomUUID());
    target = (await db.query<{ target_id: string }>("SELECT target_id FROM gm.accepted_evidence LIMIT 1")).rows[0].target_id;
  }
  const snapshot = await store.rebuild(user, target);
  expect(snapshot.qualifyingChecks).toHaveLength(4);
  expect(snapshot.state).toBe("improving");
  expect(snapshot.everMastered).toBe(false);
});

it("a server-pinned reinforcement records grading without independent assessment", async () => {
  await db.exec("ALTER TABLE gm.session_question ALTER COLUMN evidence_role SET DEFAULT 'reinforcement'");
  const input = await attempt();
  await db.exec("ALTER TABLE gm.session_question ALTER COLUMN evidence_role SET DEFAULT 'assessment'");
  expect((await store.submit(user, input, randomUUID())).status).toBe("accepted");
  const target = (await db.query<{ target_id: string }>("SELECT target_id FROM gm.accepted_evidence")).rows[0].target_id;
  expect(await store.rebuild(user, target)).toMatchObject({ state: "new", exposureCount: 1,
    qualifyingChecks: [], schedule: null, decisions: [{ effect: "reinforcement" }] });
});

it("a failed skip sync write rolls back exposure, device and completion", async () => {
  const event = await exposure();
  const before = await counts();
  await db.exec("CREATE TRIGGER fail_skip_sync BEFORE INSERT ON gm.sync_change FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation()");
  await expect(store.expose(user, event, randomUUID())).rejects.toThrow("immutable row");
  expect(await counts()).toEqual(before);
  expect((await db.query<{ status: string }>("SELECT status FROM gm.practice_session")).rows[0].status).toBe("active");
  await db.exec("DROP TRIGGER fail_skip_sync ON gm.sync_change");
  expect((await store.expose(user, event, randomUUID())).status).toBe("accepted");
});

it("exposure is nonterminal; skip completes without an evaluation and is replay-safe", async () => {
  const event = await exposure("exposure");
  const accepted = await store.expose(user, event, randomUUID());
  expect(accepted.status).toBe("accepted");
  const before = await counts();
  expect(await store.expose(user, event, randomUUID())).toEqual({ ...accepted, status: "duplicate" });
  expect(await counts()).toEqual(before);
  const skip = { ...event, eventId: randomUUID(), disposition: "skip" as const };
  expect((await store.expose(user, skip, randomUUID())).status).toBe("accepted");
  expect((await db.query<{ status: string }>("SELECT status FROM gm.practice_session")).rows[0].status).toBe("completed");
  const target = (await db.query<{ target_id: string }>("SELECT target_id FROM gm.accepted_evidence LIMIT 1")).rows[0].target_id;
  expect(await store.rebuild(user, target)).toMatchObject({ state: "new", exposureCount: 2, schedule: null });
  expect((await counts()).attempt_evaluation).toBe(0);
  const changed = await store.expose(user, { ...skip, disposition: "exposure" }, randomUUID());
  expect(changed.status === "rejected" && changed.error.code).toBe("exposure_conflict");
  const input = { ...await attempt(), sessionQuestionId: event.sessionQuestionId };
  expect((await store.submit(user, input, randomUUID())).status).toBe("rejected");
});

it("serializes skip versus answer and isolates ownership", async () => {
  const input = await attempt();
  const event = { ...await exposure(), sessionQuestionId: input.sessionQuestionId };
  await question(other);
  const foreign = await store.expose(other, event, randomUUID());
  expect(foreign.status === "rejected" && foreign.error.code).toBe("question_unavailable");
  const results = await Promise.all([store.submit(user, input, randomUUID()), store.expose(user, event, randomUUID())]);
  expect(results.map(r => r.status).sort()).toEqual(["accepted", "rejected"]);
  expect((await counts()).accepted_evidence).toBe(1);
});

it("validates the exposure HTTP contract and rejects client mastery metadata", async () => {
  const server = createApi(store, async req => req.headers.authorization === `Bearer ${user}` ? user : null);
  await new Promise<void>(resolve => server.listen(0, "127.0.0.1", resolve));
  const base = `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
  try {
    const event = await exposure();
    const post = (value: unknown, token = user) => fetch(base + "/v2/exposures:batch", {
      method: "POST", headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` }, body: JSON.stringify(value) });
    const batch = { apiVersion: "v2", events: [event] };
    const response = await post(batch);
    expect(response.status).toBe(200);
    expect(ExposureBatchResponseSchema.parse(await response.json()).acknowledgments[0].status).toBe("accepted");
    expect((await post({ ...batch, events: [{ ...event, kind: "assessment", timeZone: "UTC" }] })).status).toBe(400);
    expect((await post({ ...batch, events: Array(51).fill(event) })).status).toBe(400);
    expect((await post(batch, "invalid")).status).toBe(401);
  } finally { await new Promise<void>(resolve => server.close(() => resolve())); }
});

it("upgrades an old saved database conservatively and does not duplicate migrated evidence", async () => {
  const input = await attempt(); await store.submit(user, input, randomUUID());
  // Reconstruct the prior schema in another embedded database, retaining real fixture rows.
  const old = new PGlite();
  try {
    await old.exec(await readFile(new URL("../../../db/migrations/001_target_foundation.sql", import.meta.url), "utf8"));
    const tables = ["topic", "skill", "learning_target", "exercise", "exercise_revision", "content_release",
      "content_release_exercise", "learner_profile", "device", "practice_session", "session_question", "attempt", "attempt_evaluation"];
    for (const table of tables) {
      const columns = (await old.query<{ column_name: string }>("SELECT column_name FROM information_schema.columns WHERE table_schema='gm' AND table_name=$1 ORDER BY ordinal_position", [table])).rows.map(r => r.column_name);
      const rows = (await db.query<Record<string, unknown>>(`SELECT ${columns.join(",")} FROM gm.${table}`)).rows;
      for (const row of rows) await old.query(`INSERT INTO gm.${table} (${columns.join(",")}) OVERRIDING SYSTEM VALUE VALUES (${columns.map((_, i) => `$${i + 1}`).join(",")})`, columns.map(c => row[c]));
    }
    const migrated = new FoundationStore(old, () => new Date(now)); await migrated.initialize();
    const target = (await old.query<{ target_id: string }>("SELECT target_id FROM gm.accepted_evidence")).rows[0].target_id;
    expect(await migrated.rebuild(user, target)).toMatchObject({ state: "learning", qualifyingChecks: [], exposureCount: 1 });
    await migrated.initialize();
    expect((await old.query("SELECT id FROM gm.accepted_evidence")).rows).toHaveLength(1);
    expect((await migrated.submit(user, input, randomUUID())).status).toBe("duplicate");
  } finally { await old.close(); }
});

it("persists projections, schedules and acknowledgment identity through close/reopen", async () => {
  const directory = await mkdtemp(join(tmpdir(), "gm-evidence-test-"));
  let saved = new PGlite(directory);
  try {
    let repository = new FoundationStore(saved, () => new Date(now)); await repository.initialize();
    const q = (await repository.createSession(user, request())).questions[0];
    const input = { ...await attempt(), sessionQuestionId: q.id };
    const accepted = await repository.submit(user, input, randomUUID());
    const before = (await saved.query<{ snapshot: unknown }>("SELECT snapshot FROM gm.learner_target_state")).rows[0].snapshot;
    await saved.close(); saved = new PGlite(directory);
    repository = new FoundationStore(saved, () => new Date(now)); await repository.initialize();
    expect(await repository.rebuild(user, q.exercise.targetId)).toEqual(before);
    expect(await repository.submit(user, input, randomUUID())).toEqual({ ...accepted, status: "duplicate" });
    expect((await saved.query("SELECT sequence FROM gm.sync_change")).rows).toHaveLength(1);
  } finally { await saved.close(); await rm(directory, { recursive: true, force: true }); }
});

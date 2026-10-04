import { beforeAll, afterAll, it, expect } from "vitest";
import { PGlite } from "@electric-sql/pglite";
import { randomUUID } from "node:crypto";
import { CatalogSchema, AttemptSchema, type FocusedSessionRequest } from "@german-master/contracts";
import { FoundationStore } from "./store";
import { createApi } from "./server";
import answers from "../../../contracts/v2/examples/attempt-batch.json";

let db: PGlite, store: FoundationStore, server: ReturnType<typeof createApi>, base: string;
const user = randomUUID(), other = randomUUID();
const target = "00000000-0000-4000-8000-000000000200";
const topic = "00000000-0000-4000-8000-000000000600";
let now = "2026-10-04T12:00:00Z";
const request = (): FocusedSessionRequest => ({ apiVersion: "v2", requestId: randomUUID(), questionCount: 1,
  capabilities: ["short_answer@1", "choice@1", "cloze@1", "word_order@1", "multi_slot@1"], focus: { type: "target", id: target } });
beforeAll(async () => {
  db = new PGlite(); store = new FoundationStore(db, () => new Date(now)); await store.initialize();
  server = createApi(store, async r => r.headers.authorization === "Bearer fixture" ? user : null);
  await new Promise<void>(resolve => server.listen(0, "127.0.0.1", resolve));
  base = `http://127.0.0.1:${(server.address() as { port: number }).port}`;
});
afterAll(async () => { await new Promise<void>(resolve => server.close(() => resolve())); await db.close(); });

it("serves authenticated, bounded solution-free metadata with explicit draft status", async () => {
  expect((await fetch(`${base}/v2/catalog`)).status).toBe(401);
  expect((await fetch(`${base}/v2/catalog?limit=1`, { headers: { Authorization: "Bearer fixture" } })).status).toBe(400);
  const response = await fetch(`${base}/v2/catalog`, { headers: { Authorization: "Bearer fixture" } });
  expect(response.headers.get("cache-control")).toBe("no-store");
  const catalog = CatalogSchema.parse(await response.json());
  expect(catalog.targets).toHaveLength(5); expect(catalog.topics).toHaveLength(1);
  expect(catalog.status).toBe("unpublished_local_draft");
  expect(JSON.stringify(catalog)).not.toMatch(/acceptedAnswer|rubric|Berufe|arbeitest|normalizationVersion/);
  expect(catalog.targets.every(t => catalog.topics.some(p => p.id === t.topicId))).toBe(true);
});

it("filters topic and target pools, enforces capabilities and rejects unknown scopes without writing", async () => {
  const one = await store.createSession(user, request());
  expect(one.questions.map(q => q.exercise.targetId)).toEqual([target]);
  const focusedTopic = await store.createSession(other, { ...request(), questionCount: 5, focus: { type: "topic", id: topic } });
  expect(focusedTopic.questions).toHaveLength(5);
  for (const input of [ { ...request(), focus: { type: "target" as const, id: randomUUID() } },
    { ...request(), focus: { type: "topic" as const, id: target } }, { ...request(), capabilities: ["choice@1" as const] },
    { ...request(), questionCount: 2 } ]) {
    await expect(store.createSession(user, input)).rejects.toMatchObject({ code: "insufficient_content" });
    expect((await db.query("SELECT id FROM gm.practice_session WHERE request_id=$1", [input.requestId])).rows).toHaveLength(0);
  }
});

it("replays concurrent focused requests and conflicts on changed focus or mixed mode", async () => {
  const input = request();
  const [first, duplicate] = await Promise.all([store.createSession(user, input), store.createSession(user, input)]);
  expect(duplicate).toEqual(first);
  await expect(store.createSession(user, { ...input, focus: { type: "topic", id: topic } })).rejects.toMatchObject({ code: "session_conflict" });
  const { focus, ...mixed } = input;
  await expect(store.createSession(user, mixed)).rejects.toMatchObject({ code: "session_conflict" });
  expect((await store.createSession(other, input)).id).not.toBe(first.id);
});

it("keeps focused extra practice as reinforcement and refreshes assessment at the due deadline", async () => {
  const owner = randomUUID();
  const first = await store.createSession(owner, request());
  const attempt = AttemptSchema.parse({ ...answers.attempts[0], attemptId: randomUUID(), sessionQuestionId: first.questions[0].id, answeredAt: now });
  expect((await store.submit(owner, attempt, randomUUID())).status).toBe("accepted");
  const extraInput = request(), extra = await store.createSession(owner, extraInput);
  const role = async (id: string) => (await db.query<{ evidence_role: string }>("SELECT evidence_role FROM gm.session_question WHERE session_id=$1", [id])).rows[0].evidence_role;
  expect(await role(extra.id)).toBe("reinforcement");
  const before = (await store.targets(owner)).targets.find(t => t.targetId === target)!;
  expect((await store.submit(owner, { ...attempt, attemptId: randomUUID(), sessionQuestionId: extra.questions[0].id }, randomUUID())).status).toBe("accepted");
  expect((await store.targets(owner)).targets.find(t => t.targetId === target)!.qualifyingCheckCount).toBe(before.qualifyingCheckCount);
  now = before.schedule[0].dueAt;
  expect(await role((await store.createSession(owner, request())).id)).toBe("assessment");
  await db.query("UPDATE gm.learning_target SET status='retired' WHERE id=$1", [target]);
  expect(await store.createSession(owner, extraInput)).toEqual(extra);
  await expect(store.createSession(owner, request())).rejects.toMatchObject({ code: "insufficient_content" });
  expect((await store.catalog()).targets.some(t => t.id === target)).toBe(false);
});

it("validates focus transport and returns a target-only session over HTTP", async () => {
  const post = (body: unknown) => fetch(`${base}/v2/sessions`, { method: "POST", headers: { "Content-Type": "application/json", Authorization: "Bearer fixture" }, body: JSON.stringify(body) });
  for (const focus of [{ type: "target", id: "invalid" }, { type: "learner", id: topic }, { type: "topic", id: topic, answers: [] }])
    expect((await post({ ...request(), focus })).status).toBe(400);
  const response = await post({ ...request(), focus: { type: "target", id: "00000000-0000-4000-8000-000000000201" } });
  expect(response.status).toBe(200);
  const session = await response.json();
  expect(session.questions).toHaveLength(1); expect(session.questions[0].exercise.type).toBe("choice");
});

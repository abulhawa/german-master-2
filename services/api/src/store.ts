import { PGlite } from "@electric-sql/pglite";
import { readFile } from "node:fs/promises";
import { randomUUID, createHash } from "node:crypto";
import { SessionSchema, type Session, type SessionRequest, type Attempt, type Acknowledgment, type AttemptAcknowledgment, type Exercise } from "@german-master/contracts";
import { grade, EVALUATOR_VERSION, GradingError, type Rubric } from "@german-master/learning-engine";
import { foundationCatalog } from "./catalog";
import editorial from "../../../content/foundation/review.json";

export class ApiFailure extends Error {
  constructor(public readonly code: string, public readonly status: number) { super(code); }
}

/** Stable object-key ordering; array order and raw submitted text remain significant. */
export function canonical(value: unknown): string {
  if (Array.isArray(value)) return `[${value.map(canonical).join(",")}]`;
  if (value !== null && typeof value === "object") return `{${Object.entries(value).sort(([a], [b]) => a.localeCompare(b))
    .map(([key, v]) => `${JSON.stringify(key)}:${canonical(v)}`).join(",")}}`;
  return JSON.stringify(value);
}

/** Local PostgreSQL demonstration. Transactions serialize writes and enforce first submission. */
export class FoundationStore {
  constructor(public readonly db: PGlite, private readonly clock = () => new Date()) {}

  async initialize() {
    const exists = await this.db.query<{ present: boolean }>("SELECT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'gm') AS present");
    if (exists.rows[0].present) return;
    const migration = await readFile(new URL("../../../db/migrations/001_target_foundation.sql", import.meta.url), "utf8");
    const catalog = foundationCatalog();
    await this.db.transaction(async tx => {
      await tx.exec(migration);
      for (const topic of editorial.topics) await tx.query("INSERT INTO gm.topic VALUES ($1,$2)", [topic.id, topic.title]);
      for (const skill of editorial.skills) await tx.query("INSERT INTO gm.skill VALUES ($1,$2,$3,$4)", [skill.id, skill.topicId, skill.title, skill.parentId]);
      for (const target of editorial.targets)
        await tx.query("INSERT INTO gm.learning_target VALUES ($1,$2,$3,$4,$5,'draft')", [target.id, target.skillId, target.kind, target.level, target.objective]);
      await tx.query("INSERT INTO gm.content_release VALUES ($1,'draft',$2,NULL)", [catalog.session.contentReleaseId,
        createHash("sha256").update(canonical({ session: catalog.session, rubrics: [...catalog.rubrics] })).digest("hex")]);
      for (const { exercise } of catalog.session.questions) {
        const rubric = catalog.rubrics.get(`${exercise.id}@${exercise.revision}`)!;
        await tx.query("INSERT INTO gm.exercise VALUES ($1,$2)", [exercise.id, exercise.targetId]);
        await tx.query("INSERT INTO gm.exercise_revision VALUES ($1,$2,$3,$4,$5,$6,$7,$8)",
          [exercise.id, exercise.revision, exercise.type, exercise, rubric, rubric.normalizationVersion, editorial.provenance, editorial.status]);
        await tx.query("INSERT INTO gm.content_release_exercise VALUES ($1,$2,$3)", [catalog.session.contentReleaseId, exercise.id, exercise.revision]);
      }
    });
  }

  async createSession(userId: string, request: SessionRequest): Promise<Session> {
    return this.db.transaction(async tx => {
      const previous = await tx.query<{ id: string; request_payload: unknown }>(
        "SELECT id,request_payload FROM gm.practice_session WHERE user_id=$1 AND request_id=$2", [userId, request.requestId]);
      let sessionId: string;
      const catalog = foundationCatalog();
      if (previous.rows.length) {
        if (canonical(previous.rows[0].request_payload) !== canonical(request)) throw new ApiFailure("session_conflict", 409);
        sessionId = previous.rows[0].id;
      } else {
        const questions = catalog.session.questions.filter(q => request.capabilities.includes(`${q.exercise.type}@1`));
        if (questions.length < request.questionCount) throw new ApiFailure("insufficient_content", 409);
        sessionId = randomUUID();
        await tx.query("INSERT INTO gm.learner_profile VALUES ($1,'en','Europe/Berlin') ON CONFLICT DO NOTHING", [userId]);
        await tx.query("INSERT INTO gm.practice_session VALUES ($1,$2,$3,$4,$5,$6,'active')",
          [sessionId, userId, request.requestId, request, catalog.session.contentReleaseId, EVALUATOR_VERSION]);
        for (const [position, { exercise }] of questions.slice(0, request.questionCount).entries())
          await tx.query("INSERT INTO gm.session_question VALUES ($1,$2,$3,$4,$5,$6,$7)",
            [randomUUID(), userId, sessionId, catalog.session.contentReleaseId, exercise.id, exercise.revision, position]);
      }
      const questions = await tx.query<{ id: string; payload: Exercise }>(
        "SELECT q.id,r.payload FROM gm.session_question q JOIN gm.exercise_revision r ON r.exercise_id=q.exercise_id AND r.revision=q.revision WHERE q.session_id=$1 AND q.user_id=$2 ORDER BY q.position", [sessionId, userId]);
      return SessionSchema.parse({ apiVersion: "v2", id: sessionId, contentReleaseId: catalog.session.contentReleaseId,
        questions: questions.rows.map(q => ({ id: q.id, exercise: q.payload })) });
    });
  }

  async submit(userId: string, attempt: Attempt, requestId: string): Promise<Acknowledgment> {
    try {
      return await this.db.transaction(async tx => {
        const prior = await tx.query<{ payload: Attempt; received_sequence: number; evaluation: AttemptAcknowledgment["evaluation"] }>(
          "SELECT a.payload,a.received_sequence,e.evaluation FROM gm.attempt a JOIN gm.attempt_evaluation e ON e.user_id=a.user_id AND e.attempt_id=a.id WHERE a.user_id=$1 AND a.id=$2 ORDER BY e.evaluated_at LIMIT 1", [userId, attempt.attemptId]);
        if (prior.rows.length) {
          if (canonical(prior.rows[0].payload) !== canonical(attempt)) throw new ApiFailure("attempt_conflict", 409);
          return { attemptId: attempt.attemptId, status: "duplicate", evaluation: prior.rows[0].evaluation, serverSequence: prior.rows[0].received_sequence };
        }
        const question = await tx.query<{ revision: number; payload: Exercise; rubric: Rubric }>(
          "SELECT q.revision,r.payload,r.rubric FROM gm.session_question q JOIN gm.exercise_revision r ON r.exercise_id=q.exercise_id AND r.revision=q.revision WHERE q.id=$1 AND q.user_id=$2", [attempt.sessionQuestionId, userId]);
        // Same rejection for missing and foreign questions: do not disclose another learner's data.
        if (!question.rows.length) throw new ApiFailure("question_unavailable", 403);
        const pinned = question.rows[0];
        if (pinned.revision !== attempt.exerciseRevision) throw new ApiFailure("revision_mismatch", 409);
        const submitted = await tx.query("SELECT id FROM gm.attempt WHERE user_id=$1 AND question_id=$2", [userId, attempt.sessionQuestionId]);
        if (submitted.rows.length) throw new ApiFailure("question_already_answered", 409);
        const evaluation = grade(pinned.payload, pinned.rubric, attempt.answer, attempt.assistance);
        await tx.query("INSERT INTO gm.device VALUES ($1,$2) ON CONFLICT DO NOTHING", [userId, attempt.deviceId]);
        const now = this.clock().toISOString();
        const inserted = await tx.query<{ received_sequence: number }>(
          "INSERT INTO gm.attempt (user_id,id,question_id,device_id,payload,received_at) VALUES ($1,$2,$3,$4,$5,$6) RETURNING received_sequence",
          [userId, attempt.attemptId, attempt.sessionQuestionId, attempt.deviceId, attempt, now]);
        await tx.query("INSERT INTO gm.attempt_evaluation VALUES ($1,$2,$3,$4,$5)",
          [userId, attempt.attemptId, EVALUATOR_VERSION, evaluation, now]);
        await tx.query("UPDATE gm.practice_session s SET status='completed' WHERE s.user_id=$1 AND s.id=(SELECT session_id FROM gm.session_question WHERE id=$2 AND user_id=$1) AND NOT EXISTS (SELECT 1 FROM gm.session_question q WHERE q.session_id=s.id AND NOT EXISTS (SELECT 1 FROM gm.attempt a WHERE a.user_id=$1 AND a.question_id=q.id))", [userId, attempt.sessionQuestionId]);
        return { attemptId: attempt.attemptId, status: "accepted", evaluation, serverSequence: inserted.rows[0].received_sequence };
      });
    } catch (error) {
      if (!(error instanceof ApiFailure) && !(error instanceof GradingError)) throw error;
      return { attemptId: attempt.attemptId, status: "rejected", error: {
        code: error.code, message: "This attempt could not be accepted.", requestId, retryable: false,
      } };
    }
  }
}

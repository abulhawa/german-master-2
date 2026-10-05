import { PGlite, type Transaction } from "@electric-sql/pglite";
import { readFile } from "node:fs/promises";
import { randomUUID, createHash } from "node:crypto";
import { SessionSchema, type Session, type SessionRequest, type FocusedSessionRequest, CatalogSchema, type Attempt, type Acknowledgment, type AttemptAcknowledgment, type Exercise, type ExposureEvent, type ExposureAcknowledgment } from "@german-master/contracts";
import { grade, EVALUATOR_VERSION, GradingError, type Rubric, reduceEvidence, EVIDENCE_POLICY_VERSION, type AcceptedEvidence, type TargetSnapshot, selectQuestions, SELECTION_POLICY_VERSION, type SelectionCandidate } from "@german-master/learning-engine";
import { foundationCatalog } from "./catalog";
import metadata from "../../../content/foundation/metadata.json";
import editorial from "../../../content/foundation/review.json";
import { TargetPageSchema, SyncPageSchema, type TargetPage, LearnerProfileSchema, ProfileRequestSchema, type ProfileRequest, type LearnerProfile } from "@german-master/contracts";
import { confirmedTarget } from "./reads";
import { ContentReportRequestSchema, ContentReportReceiptSchema, type ContentReportRequest } from '@german-master/contracts';
import { SessionCompletionRequestSchema, SessionCompletionReceiptSchema, type SessionCompletionRequest, type SessionCompletionReceipt } from '@german-master/contracts';
import { SessionRequestSchema, PreparedPackSchema, type PreparedPack, type OfflineRubric } from '@german-master/contracts';

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

type PinnedQuestion = {
  revision: number; payload: Exercise; rubric: Rubric; target_id: string; target_kind: string;
  variant_key: string; context_key: string; transfer_key: string | null;
  evidence_role: "assessment" | "reinforcement"; issued_at: Date; timezone: string;
};
type EvidenceDetail = Omit<Extract<AcceptedEvidence, { kind: "assessment" | "reinforcement" }>,
  "id" | "learnerId" | "targetId" | "receivedSequence" | "receivedAt" | "sessionIssuedAt" | "timeZone"> |
  { kind: "exposure"; answeredAt?: string };

/** Local PostgreSQL demonstration. Transactions serialize writes and enforce first submission. */
export class FoundationStore {
  constructor(public readonly db: PGlite, private readonly clock = () => new Date(),
    private readonly cursorLifetimeMs = 7 * 24 * 60 * 60 * 1000,
    private readonly pageLifetimeMs = 7 * 24 * 60 * 60 * 1000) {
    if (!Number.isSafeInteger(cursorLifetimeMs) || cursorLifetimeMs <= 0) throw new Error('Invalid cursor lifetime');
    if (!Number.isSafeInteger(pageLifetimeMs) || pageLifetimeMs <= 0) throw new Error('Invalid page lifetime');
  }

  async initialize() {
    const exists = await this.db.query<{ present: boolean }>("SELECT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'gm') AS present");
    if (exists.rows[0].present) { await this.upgradeEvidence(); await this.upgradeReads(); await this.upgradeProfile(); return; }
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
    await this.upgradeEvidence();
    await this.upgradeReads();
    await this.upgradeProfile();
  }

  private async upgradeReads() {
    await this.db.transaction(async tx => {
      const found = await tx.query("SELECT version FROM gm.schema_migration WHERE version=3");
      if (!found.rows.length) await tx.exec(await readFile(new URL("../../../db/migrations/003_owned_reads.sql", import.meta.url), "utf8"));
      const expiry = await tx.query("SELECT version FROM gm.schema_migration WHERE version=5");
      if (!expiry.rows.length) await tx.exec(await readFile(new URL("../../../db/migrations/005_cursor_expiry.sql", import.meta.url), "utf8"));
      const retention = await tx.query("SELECT version FROM gm.schema_migration WHERE version=6");
      if (!retention.rows.length) {
        await tx.exec(await readFile(new URL("../../../db/migrations/006_read_retention.sql", import.meta.url), "utf8"));
        // Undated legacy pages receive one bounded upgrade grace period.
        await tx.query('ALTER TABLE gm.target_page DISABLE TRIGGER immutable_target_page');
        await tx.query('UPDATE gm.target_page SET expires_at=$1', [new Date(this.clock().getTime() + this.pageLifetimeMs)]);
        await tx.query('ALTER TABLE gm.target_page ENABLE TRIGGER immutable_target_page');
        await tx.query('ALTER TABLE gm.target_page ALTER COLUMN expires_at DROP DEFAULT');
      }
    });
  }

  private async upgradeProfile() {
    await this.db.transaction(async tx => {
      const found = await tx.query("SELECT version FROM gm.schema_migration WHERE version=4");
      if (!found.rows.length) await tx.exec(await readFile(new URL("../../../db/migrations/004_owned_profile.sql", import.meta.url), "utf8"));
      const reports = await tx.query('SELECT version FROM gm.schema_migration WHERE version=7');
      if (!reports.rows.length) await tx.exec(await readFile(new URL('../../../db/migrations/007_content_reports.sql', import.meta.url), 'utf8'));
      const completions = await tx.query('SELECT version FROM gm.schema_migration WHERE version=8');
      if (!completions.rows.length) await tx.exec(await readFile(new URL('../../../db/migrations/008_session_completion.sql', import.meta.url), 'utf8'));
      const packs = await tx.query('SELECT version FROM gm.schema_migration WHERE version=9');
      if (!packs.rows.length) await tx.exec(await readFile(new URL('../../../db/migrations/009_prepared_packs.sql', import.meta.url), 'utf8'));
    });
  }

  async complete(userId: string, sessionId: string, input: SessionCompletionRequest): Promise<SessionCompletionReceipt> {
    const request = SessionCompletionRequestSchema.parse(input);
    return this.db.transaction(async tx => {
      const owned = await tx.query('SELECT id FROM gm.practice_session WHERE user_id=$1 AND id=$2', [userId, sessionId]);
      if (!owned.rows.length) throw new ApiFailure('session_unavailable', 404);
      await this.lockLearner(tx, userId);
      const prior = await tx.query<{id: string; session_id: string; payload: unknown; receipt: unknown}>(
        'SELECT * FROM gm.session_completion WHERE user_id=$1 AND (id=$2 OR session_id=$3)', [userId, request.requestId, sessionId]);
      if (prior.rows.length) {
        const row = prior.rows[0];
        if (row.id !== request.requestId || row.session_id !== sessionId || canonical(row.payload) !== canonical(request))
          throw new ApiFailure('completion_conflict', 409);
        return SessionCompletionReceiptSchema.parse(row.receipt);
      }
      const counts = await tx.query<{planned: number; graded: number; skipped: number; correct: number}>(`
        SELECT count(*)::int AS planned,
          count(a.id)::int AS graded, count(x.id)::int AS skipped,
          count(a.id) FILTER (WHERE e.evaluation->>'outcome'='correct')::int AS correct
        FROM gm.session_question q
        LEFT JOIN gm.attempt a ON a.user_id=q.user_id AND a.question_id=q.id
        LEFT JOIN gm.attempt_evaluation e ON e.user_id=a.user_id AND e.attempt_id=a.id
        LEFT JOIN gm.exposure_event x ON x.user_id=q.user_id AND x.question_id=q.id AND x.disposition='skip'
        WHERE q.user_id=$1 AND q.session_id=$2`, [userId, sessionId]);
      const c = counts.rows[0];
      if (request.mode === 'full' && c.graded + c.skipped !== c.planned) throw new ApiFailure('session_incomplete', 409);
      const receipt = SessionCompletionReceiptSchema.parse({apiVersion:'v2', requestId:request.requestId, sessionId,
        mode:request.mode, plannedCount:c.planned, gradedCount:c.graded, skippedCount:c.skipped, correctCount:c.correct,
        completedAt:this.clock().toISOString()});
      await tx.query('INSERT INTO gm.session_completion VALUES ($1,$2,$3,$4,$5)', [userId,request.requestId,sessionId,request,receipt]);
      await tx.query('UPDATE gm.practice_session SET status=$3 WHERE user_id=$1 AND id=$2', [userId,sessionId,request.mode === 'full' ? 'completed' : 'ended']);
      return receipt;
    });
  }

  async report(userId: string, input: ContentReportRequest) {
    const request = ContentReportRequestSchema.parse(input);
    return this.db.transaction(async tx => {
      const previous = await tx.query<{payload: unknown}>('SELECT payload FROM gm.content_report WHERE user_id=$1 AND id=$2', [userId,request.reportId]);
      if (previous.rows.length) {
        if (canonical(previous.rows[0].payload) !== canonical(request)) throw new ApiFailure('report_conflict',409);
      } else {
        const question = await tx.query<{exercise_id:string; revision:number}>('SELECT exercise_id,revision FROM gm.session_question WHERE user_id=$1 AND id=$2', [userId,request.sessionQuestionId]);
        if (!question.rows.length) throw new ApiFailure('question_unavailable',404);
        if (question.rows[0].revision !== request.exerciseRevision) throw new ApiFailure('revision_mismatch',409);
        const count = await tx.query<{n:number}>('SELECT count(*)::int AS n FROM gm.content_report WHERE user_id=$1 AND received_at>$2',[userId,new Date(this.clock().getTime()-3600000)]);
        if (count.rows[0].n >= 10) throw new ApiFailure('report_rate_limited',429);
        await tx.query('INSERT INTO gm.content_report VALUES ($1,$2,$3,$4,$5,$6,$7,$8)',[userId,request.reportId,request.sessionQuestionId,question.rows[0].exercise_id,request.exerciseRevision,request.category,request,this.clock()]);
      }
      return ContentReportReceiptSchema.parse({apiVersion:'v2',reportId:request.reportId,status:'recorded'});
    });
  }

  private async profileIn(tx: Transaction, userId: string): Promise<LearnerProfile> {
    const result = await tx.query<{ locale: string; timezone: string; level: string; session_question_count: number; revision: number; setup_completed: boolean }>(
      "SELECT * FROM gm.learner_profile WHERE user_id=$1", [userId]);
    const row = result.rows[0];
    return LearnerProfileSchema.parse({ apiVersion: "v2", revision: row.revision, setupCompleted: row.setup_completed,
      preferences: { locale: row.locale, timezone: row.timezone, level: row.level, sessionQuestionCount: row.session_question_count } });
  }

  async profile(userId: string) {
    return this.db.transaction(async tx => { await this.readOwner(tx, userId); return this.profileIn(tx, userId); });
  }

  async saveProfile(userId: string, input: ProfileRequest) {
    const parsed = ProfileRequestSchema.safeParse(input);
    if (!parsed.success) throw new ApiFailure("invalid_request", 400);
    // Require an IANA-style name (including UTC); never accept numeric offsets or whitespace.
    const timezone = input.preferences.timezone;
    if (timezone !== "UTC" && !/^[A-Za-z_]+(?:\/[A-Za-z0-9_+\-]+)+$/.test(timezone)) throw new ApiFailure("invalid_timezone", 400);
    try { new Intl.DateTimeFormat("en", { timeZone: timezone }).format(); }
    catch { throw new ApiFailure("invalid_timezone", 400); }
    return this.db.transaction(async tx => {
      await this.readOwner(tx, userId);
      const prior = await tx.query<{ request: ProfileRequest; response: LearnerProfile }>(
        "SELECT request,response FROM gm.profile_request WHERE user_id=$1 AND request_id=$2", [userId, input.requestId]);
      if (prior.rows.length) {
        if (canonical(prior.rows[0].request) !== canonical(input)) throw new ApiFailure("profile_request_conflict", 409);
        return LearnerProfileSchema.parse(prior.rows[0].response);
      }
      const current = await this.profileIn(tx, userId);
      if (current.revision !== input.expectedRevision) throw new ApiFailure("profile_revision_conflict", 409);
      const p = input.preferences;
      await tx.query(`UPDATE gm.learner_profile SET locale=$2,timezone=$3,level=$4,session_question_count=$5,
        revision=revision+1,setup_completed=true WHERE user_id=$1`, [userId, p.locale, p.timezone, p.level, p.sessionQuestionCount]);
      const response = await this.profileIn(tx, userId);
      await tx.query("INSERT INTO gm.profile_request VALUES ($1,$2,$3,$4)", [userId, input.requestId, input, response]);
      return response;
    });
  }

  private async readOwner(tx: Transaction, userId: string) {
    await tx.query("INSERT INTO gm.learner_profile (user_id,locale,timezone) VALUES ($1,'en','Europe/Berlin') ON CONFLICT DO NOTHING", [userId]);
    await this.lockLearner(tx, userId);
  }

  private async syncCursor(tx: Transaction, userId: string, sequence: number, now: Date): Promise<string> {
    const rows = await tx.query<{ id: string }>(`SELECT id FROM gm.sync_cursor
      WHERE user_id=$1 AND sequence=$2 AND expires_at>$3 ORDER BY expires_at DESC,id LIMIT 1`, [userId, sequence, now]);
    if (rows.rows.length) return rows.rows[0].id;
    const id = randomUUID();
    await tx.query('INSERT INTO gm.sync_cursor (id,user_id,sequence,expires_at) VALUES ($1,$2,$3,$4)',
      [id, userId, sequence, new Date(now.getTime() + this.cursorLifetimeMs)]);
    return id;
  }

  private async pruneReads(tx: Transaction, now: Date) {
    await tx.query('DELETE FROM gm.target_page WHERE expires_at<=$1', [now]);
    await tx.query('DELETE FROM gm.sync_cursor WHERE expires_at<=$1', [now]);
  }

  /** Local maintenance only: evidence, projections and idempotency records are untouched. */
  async cleanupReads() {
    const now = this.clock();
    await this.db.transaction(tx => this.pruneReads(tx, now));
  }

  /** Frozen pages share a watermark; concurrent ingestion cannot fall between snapshot and sync. */
  async targets(userId: string, limit = 50, cursor?: string): Promise<TargetPage> {
    if (!Number.isInteger(limit) || limit < 1 || limit > 100) throw new ApiFailure('invalid_request', 400);
    return this.db.transaction(async tx => {
      await this.readOwner(tx, userId);
      const now = this.clock();
      if (cursor) {
        const page = await tx.query<{ payload: TargetPage }>("SELECT payload FROM gm.target_page WHERE id=$1 AND user_id=$2 AND expires_at>$3", [cursor, userId, now]);
        if (!page.rows.length) throw new ApiFailure("invalid_cursor", 400);
        return TargetPageSchema.parse(page.rows[0].payload);
      }
      await this.pruneReads(tx, now);
      const generatedAt = now.toISOString();
      const watermark = (await tx.query<{ n: number }>("SELECT COALESCE(max(sequence),0)::int AS n FROM gm.sync_change WHERE user_id=$1", [userId])).rows[0].n;
      const syncCursor = await this.syncCursor(tx, userId, watermark, now);
      const rows = await tx.query<{ id: string; snapshot: TargetSnapshot | null; last_sequence: number }>(
        `SELECT t.id,st.snapshot,COALESCE(st.last_sequence,0)::int AS last_sequence FROM gm.learning_target t
         LEFT JOIN gm.learner_target_state st ON st.target_id=t.id AND st.user_id=$1 ORDER BY t.id`, [userId]);
      const targets = rows.rows.map(r => confirmedTarget(r.id, r.snapshot, r.last_sequence, generatedAt));
      let nextPageCursor = '';
      let first!: TargetPage;
      // Materialize one immutable chain with a shared deadline; reads never renew it.
      for (let offset = Math.max(0, Math.floor((targets.length - 1) / limit) * limit); offset >= 0; offset -= limit) {
        first = TargetPageSchema.parse({ apiVersion: 'v2', generatedAt, targets: targets.slice(offset, offset + limit), nextPageCursor, syncCursor });
        const id = randomUUID();
        await tx.query("INSERT INTO gm.target_page (id,user_id,payload,expires_at) VALUES ($1,$2,$3,$4)",
          [id, userId, first, new Date(now.getTime() + this.pageLifetimeMs)]);
        nextPageCursor = id;
      }
      return first;
    });
  }

  /** Stable historical payloads; clock passage is not a new sync event. */
  async sync(userId: string, limit = 50, cursor?: string) {
    if (!Number.isInteger(limit) || limit < 1 || limit > 100) throw new ApiFailure('invalid_request', 400);
    return this.db.transaction(async tx => {
      await this.readOwner(tx, userId);
      let after = 0;
      const now = this.clock();
      await this.pruneReads(tx, now);
      if (cursor) {
        const position = await tx.query<{ sequence: number }>("SELECT sequence FROM gm.sync_cursor WHERE id=$1 AND user_id=$2 AND expires_at>$3", [cursor, userId, now]);
        if (!position.rows.length) throw new ApiFailure("invalid_cursor", 400);
        after = position.rows[0].sequence;
      }
      const rows = await tx.query<{ sequence: number; target_id: string; payload: TargetSnapshot; received_sequence: number }>(
        `SELECT c.sequence,c.target_id,c.payload,e.received_sequence FROM gm.sync_change c
         JOIN gm.accepted_evidence e ON e.user_id=c.user_id AND e.id=c.evidence_id
         WHERE c.user_id=$1 AND c.sequence>$2 ORDER BY c.sequence LIMIT $3`, [userId, after, limit + 1]);
      const changes = rows.rows.slice(0, limit).map(r => ({ sequence: r.sequence, operation: 'upsert' as const,
        target: confirmedTarget(r.target_id, r.payload, r.received_sequence) }));
      return SyncPageSchema.parse({ apiVersion: 'v2', changes, hasMore: rows.rows.length > limit,
        nextCursor: await this.syncCursor(tx, userId, changes.at(-1)?.sequence ?? after, now) });
    });
  }

  private async lockLearner(tx: Transaction, userId: string) {
    // Serialize receipt ordering and projection updates for one learner. Network adapter
    // must retain this lock and prove behavior with independent connections.
    const profile = await tx.query("SELECT user_id FROM gm.learner_profile WHERE user_id=$1 FOR UPDATE", [userId]);
    if (!profile.rows.length) throw new ApiFailure("question_unavailable", 403);
  }

  private async pinnedQuestion(tx: Transaction, userId: string, questionId: string, revision: number) {
    const question = await tx.query<PinnedQuestion>(
      `SELECT q.revision,q.evidence_role,r.payload,r.rubric,e.target_id,t.kind AS target_kind,
        i.variant_key,i.context_key,i.transfer_key,s.issued_at,p.timezone
       FROM gm.session_question q JOIN gm.exercise_revision r ON r.exercise_id=q.exercise_id AND r.revision=q.revision
       JOIN gm.exercise e ON e.id=q.exercise_id JOIN gm.learning_target t ON t.id=e.target_id
       JOIN gm.revision_evidence_identity i ON i.exercise_id=q.exercise_id AND i.revision=q.revision
       JOIN gm.practice_session s ON s.id=q.session_id JOIN gm.learner_profile p ON p.user_id=q.user_id
       WHERE q.id=$1 AND q.user_id=$2`, [questionId, userId]);
    if (!question.rows.length) throw new ApiFailure("question_unavailable", 403);
    if (question.rows[0].revision !== revision) throw new ApiFailure("revision_mismatch", 409);
    return question.rows[0];
  }

  private async ensureOpen(tx: Transaction, userId: string, questionId: string) {
    const answered = await tx.query(`SELECT id FROM gm.attempt WHERE user_id=$1 AND question_id=$2
      UNION ALL SELECT id FROM gm.exposure_event WHERE user_id=$1 AND question_id=$2 AND disposition='skip'`, [userId, questionId]);
    if (answered.rows.length) throw new ApiFailure("question_already_answered", 409);
    const ended = await tx.query(`SELECT c.id FROM gm.session_completion c JOIN gm.session_question q
      ON q.user_id=c.user_id AND q.session_id=c.session_id WHERE q.user_id=$1 AND q.id=$2`, [userId, questionId]);
    if (ended.rows.length) throw new ApiFailure('session_ended', 409);
  }

  private async completeSession(tx: Transaction, userId: string, questionId: string) {
    await tx.query(`UPDATE gm.practice_session s SET status='completed' WHERE s.user_id=$1
      AND s.id=(SELECT session_id FROM gm.session_question WHERE id=$2 AND user_id=$1)
      AND NOT EXISTS (SELECT 1 FROM gm.session_question q WHERE q.session_id=s.id
        AND NOT EXISTS (SELECT 1 FROM gm.attempt a WHERE a.user_id=$1 AND a.question_id=q.id)
        AND NOT EXISTS (SELECT 1 FROM gm.exposure_event x WHERE x.user_id=$1 AND x.question_id=q.id AND x.disposition='skip'))`, [userId, questionId]);
  }

  private async saveEvidence(tx: Transaction, userId: string, sourceId: string, pinned: PinnedQuestion,
    now: string, detail: EvidenceDetail, source: "attempt" | "exposure") {
    const sequence = (await tx.query<{ n: number }>("SELECT nextval(pg_get_serial_sequence('gm.accepted_evidence','received_sequence'))::int AS n")).rows[0].n;
    const id = randomUUID();
    const event: AcceptedEvidence = { id, learnerId: userId, targetId: pinned.target_id,
      receivedSequence: sequence, receivedAt: now, sessionIssuedAt: new Date(pinned.issued_at).toISOString(),
      timeZone: pinned.timezone, ...detail };
    await tx.query(`INSERT INTO gm.accepted_evidence
      (user_id,id,target_id,attempt_id,exposure_id,received_sequence,policy_version,payload)
      OVERRIDING SYSTEM VALUE VALUES ($1,$2,$3,$4,$5,$6,$7,$8)`,
      [userId, id, pinned.target_id, source === "attempt" ? sourceId : null,
        source === "exposure" ? sourceId : null, sequence, EVIDENCE_POLICY_VERSION, event]);
    const snapshot = await this.rebuildIn(tx, userId, pinned.target_id, now);
    await tx.query(`INSERT INTO gm.learner_target_state VALUES ($1,$2,$3,$4,$5)
      ON CONFLICT (user_id,target_id) DO UPDATE SET policy_version=$3,last_sequence=$4,snapshot=$5`,
      [userId, pinned.target_id, EVIDENCE_POLICY_VERSION, sequence, snapshot]);
    if (snapshot.schedule) await tx.query(`INSERT INTO gm.review_schedule VALUES ($1,$2,$3,$4)
      ON CONFLICT (user_id,target_id) DO UPDATE SET due_at=$3,schedule=$4`,
      [userId, pinned.target_id, snapshot.schedule.dueAt, snapshot.schedule]);
    else await tx.query("DELETE FROM gm.review_schedule WHERE user_id=$1 AND target_id=$2", [userId, pinned.target_id]);
    await tx.query("INSERT INTO gm.sync_change (user_id,target_id,evidence_id,operation,payload) VALUES ($1,$2,$3,'upsert',$4)",
      [userId, pinned.target_id, id, snapshot]);
    return sequence;
  }

  private async rebuildIn(tx: Transaction, userId: string, targetId: string, now: string): Promise<TargetSnapshot> {
    const rows = await tx.query<{ payload: AcceptedEvidence }>(
      "SELECT payload FROM gm.accepted_evidence WHERE user_id=$1 AND target_id=$2 ORDER BY received_sequence", [userId, targetId]);
    const target = await tx.query<{ kind: string; timezone: string }>(
      "SELECT t.kind,p.timezone FROM gm.learning_target t CROSS JOIN gm.learner_profile p WHERE t.id=$1 AND p.user_id=$2", [targetId, userId]);
    if (!target.rows.length) throw new ApiFailure("target_unavailable", 404);
    return reduceEvidence({ learnerId: userId, targetId, targetKind: target.rows[0].kind === "lexical" ? "lexical" : "concept",
      timeZone: target.rows[0].timezone, now, policyVersion: EVIDENCE_POLICY_VERSION }, rows.rows.map(r => r.payload));
  }

  /** Read-only replay. Stored event timezones/editorial identities remain authoritative. */
  async rebuild(userId: string, targetId: string) {
    return this.db.transaction(tx => this.rebuildIn(tx, userId, targetId, this.clock().toISOString()));
  }

  private async upgradeEvidence() {
    await this.db.transaction(async tx => {
      const exists = await tx.query<{ present: boolean }>("SELECT to_regclass('gm.schema_migration') IS NOT NULL AS present");
      if (exists.rows[0].present) return;
      await tx.exec(await readFile(new URL("../../../db/migrations/002_evidence_projection.sql", import.meta.url), "utf8"));
      for (const target of editorial.targets) {
        const identity = target.evidenceIdentity;
        await tx.query("INSERT INTO gm.revision_evidence_identity VALUES ($1,$2,$3,$4,$5)",
          [target.exerciseId, target.revision, identity.variantKey, identity.contextKey, identity.transferKey]);
      }
      const old = await tx.query<{ user_id: string; id: string; payload: Attempt; received_at: Date; evaluation: AttemptAcknowledgment["evaluation"] }>(
        `SELECT a.*,e.evaluation FROM gm.attempt a JOIN gm.attempt_evaluation e
         ON e.user_id=a.user_id AND e.attempt_id=a.id ORDER BY a.received_sequence`);
      for (const row of old.rows) {
        const pinned = await this.pinnedQuestion(tx, row.user_id, row.payload.sessionQuestionId, row.payload.exerciseRevision);
        // Prior sessions lack reliable issuance metadata. Keep outcomes/exposure,
        // but never retrospectively grant spaced-success credit.
        await this.saveEvidence(tx, row.user_id, row.id, pinned, new Date(row.received_at).toISOString(), {
          kind: pinned.evidence_role, outcome: row.evaluation.outcome as "correct" | "incorrect",
          assisted: row.evaluation.assisted, evaluationVersion: row.evaluation.policyVersion,
          variantKey: pinned.variant_key, contextKey: pinned.context_key,
          ...(pinned.transfer_key ? { transferKey: pinned.transfer_key } : {}),
        }, "attempt");
      }
      await tx.query("INSERT INTO gm.schema_migration VALUES (2)");
    });
  }

  async expose(userId: string, event: ExposureEvent, requestId: string): Promise<ExposureAcknowledgment> {
    try {
      return await this.db.transaction(async tx => {
        await this.lockLearner(tx, userId);
        const prior = await tx.query<{ payload: ExposureEvent; received_sequence: number }>(
          `SELECT x.payload,e.received_sequence FROM gm.exposure_event x JOIN gm.accepted_evidence e
           ON e.user_id=x.user_id AND e.exposure_id=x.id WHERE x.user_id=$1 AND x.id=$2`, [userId, event.eventId]);
        if (prior.rows.length) {
          if (canonical(prior.rows[0].payload) !== canonical(event)) throw new ApiFailure("exposure_conflict", 409);
          return { eventId: event.eventId, status: "duplicate", serverSequence: prior.rows[0].received_sequence };
        }
        const pinned = await this.pinnedQuestion(tx, userId, event.sessionQuestionId, event.exerciseRevision);
        await this.ensureOpen(tx, userId, event.sessionQuestionId);
        await tx.query("INSERT INTO gm.device VALUES ($1,$2) ON CONFLICT DO NOTHING", [userId, event.deviceId]);
        await tx.query("INSERT INTO gm.exposure_event VALUES ($1,$2,$3,$4,$5,$6)",
          [userId, event.eventId, event.sessionQuestionId, event.deviceId, event.disposition, event]);
        const sequence = await this.saveEvidence(tx, userId, event.eventId, pinned, this.clock().toISOString(),
          { kind: "exposure", answeredAt: event.occurredAt }, "exposure");
        await this.completeSession(tx, userId, event.sessionQuestionId);
        return { eventId: event.eventId, status: "accepted", serverSequence: sequence };
      });
    } catch (error) {
      if (!(error instanceof ApiFailure)) throw error;
      return { eventId: event.eventId, status: "rejected", error: {
        code: error.code, message: "This exposure could not be accepted.", requestId, retryable: false,
      } };
    }
  }

  /** Explicit unpublished fixture metadata; never exposes rubrics or accepted forms. */
  async catalog(userId?: string) {
    const profile = userId ? await this.profile(userId) : null;
    const releaseId = foundationCatalog().session.contentReleaseId;
    const rows = await this.db.query<{ id: string; topic_id: string; title: unknown; level: string; count: number }>(
      `SELECT t.id,s.topic_id,tp.title,t.level,LEAST(1,count(DISTINCT e.id))::int AS count
       FROM gm.learning_target t JOIN gm.skill s ON s.id=t.skill_id JOIN gm.topic tp ON tp.id=s.topic_id
       LEFT JOIN gm.exercise e ON e.target_id=t.id
       LEFT JOIN gm.content_release_exercise cr ON cr.exercise_id=e.id AND cr.release_id=$1
       WHERE t.status<>'retired' AND cr.exercise_id IS NOT NULL
       GROUP BY t.id,s.topic_id,tp.title,t.level ORDER BY t.id`, [releaseId]);
    const practisedTargets = new Set(userId ? (await this.db.query<{ target_id: string }>("SELECT target_id FROM gm.learner_target_state WHERE user_id=$1", [userId])).rows.map(r => r.target_id) : []);
    const topics = new Map(rows.rows.map(r => [r.topic_id, { id: r.topic_id, title: r.title }]));
    return CatalogSchema.parse({ apiVersion: 'v2', contentReleaseId: releaseId, status: 'unpublished_local_draft',
      topics: [...topics.values()], targets: rows.rows.map(r => {
        const text = metadata.targets.find(t => t.id === r.id);
        if (!text) throw Error('Missing catalog metadata');
        return { ...text, topicId: r.topic_id, level: r.level, availableQuestionCount: profile && profile.preferences.level !== r.level && !practisedTargets.has(r.id) ? 0 : r.count };
      }) });
  }

  async createSession(userId: string, request: SessionRequest | FocusedSessionRequest): Promise<Session> {
    return this.db.transaction(tx => this.createSessionIn(tx,userId,request));
  }

  /** Allocate both sessions, rubrics and the replay record in one transaction. */
  async preparePack(userId: string, input: SessionRequest): Promise<PreparedPack> {
    const request = SessionRequestSchema.parse(input);
    return this.db.transaction(async tx => {
      await this.readOwner(tx,userId);
      const prior = await tx.query<{payload:unknown;response:unknown}>('SELECT payload,response FROM gm.prepared_pack WHERE user_id=$1 AND id=$2',[userId,request.requestId]);
      if (prior.rows.length) {
        if(canonical(prior.rows[0].payload) !== canonical(request)) throw new ApiFailure('pack_conflict',409);
        return PreparedPackSchema.parse(prior.rows[0].response);
      }
      const sessions = [];
      for(let i=0;i<2;i++) sessions.push(await this.createSessionIn(tx,userId,{...request,requestId:randomUUID()}));
      const rows = await tx.query<{exercise_id:string;revision:number;rubric:Rubric}>(`SELECT DISTINCT r.exercise_id,r.revision,r.rubric
        FROM gm.session_question q JOIN gm.exercise_revision r ON r.exercise_id=q.exercise_id AND r.revision=q.revision
        WHERE q.user_id=$1 AND q.session_id=ANY($2::uuid[]) ORDER BY r.exercise_id,r.revision`,[userId,sessions.map(s=>s.id)]);
      const rubrics: OfflineRubric[] = rows.rows.map(r=>({exerciseId:r.exercise_id,exerciseRevision:r.revision,...r.rubric}));
      const now = this.clock();
      const payload = {apiVersion:'v2' as const,packId:request.requestId,contentReleaseId:sessions[0].contentReleaseId,
        evaluatorVersion:EVALUATOR_VERSION,normalizationVersion:'de-nfc-trim-v1' as const,
        issuedAt:now.toISOString(),expiresAt:new Date(now.getTime()+7*24*60*60*1000).toISOString(),sessions,rubrics};
      const response = PreparedPackSchema.parse({...payload,contentHash:createHash('sha256').update(canonical(payload)).digest('hex')});
      await tx.query('INSERT INTO gm.prepared_pack VALUES ($1,$2,$3,$4)',[userId,request.requestId,request,response]);
      for(const session of sessions) await tx.query('INSERT INTO gm.prepared_pack_session VALUES ($1,$2,$3)',[userId,request.requestId,session.id]);
      return response;
    });
  }

  private async createSessionIn(tx: Transaction, userId: string, request: SessionRequest | FocusedSessionRequest): Promise<Session> {
      await tx.query("INSERT INTO gm.learner_profile (user_id,locale,timezone) VALUES ($1,'en','Europe/Berlin') ON CONFLICT DO NOTHING", [userId]);
      await this.lockLearner(tx, userId);
      const previous = await tx.query<{ id: string; request_payload: unknown; release_id: string }>(
        "SELECT id,request_payload,release_id FROM gm.practice_session WHERE user_id=$1 AND request_id=$2", [userId, request.requestId]);
      let sessionId: string;
      const catalog = foundationCatalog();
      let releaseId = catalog.session.contentReleaseId;
      if (previous.rows.length) {
        if (canonical(previous.rows[0].request_payload) !== canonical(request)) throw new ApiFailure("session_conflict", 409);
        sessionId = previous.rows[0].id;
        releaseId = previous.rows[0].release_id;
      } else {
        const now = this.clock().toISOString();
        const profile = await this.profileIn(tx, userId);
        const focus = "focus" in request ? request.focus : null;
        const eligible = await tx.query<SelectionCandidate>(
          `SELECT e.target_id AS "targetId",r.exercise_id AS "exerciseId",r.revision,
            COALESCE(st.snapshot->>'state','new') AS state,sc.due_at AS "dueAt",
            st.snapshot->>'lastInformativeAt' AS "lastInformativeAt"
           FROM gm.content_release_exercise cr JOIN gm.exercise_revision r
             ON r.exercise_id=cr.exercise_id AND r.revision=cr.revision
           JOIN gm.exercise e ON e.id=r.exercise_id
           JOIN gm.learning_target t ON t.id=e.target_id
           JOIN gm.revision_evidence_identity i ON i.exercise_id=r.exercise_id AND i.revision=r.revision
           LEFT JOIN gm.learner_target_state st ON st.target_id=e.target_id AND st.user_id=$1
           LEFT JOIN gm.review_schedule sc ON sc.target_id=e.target_id AND sc.user_id=$1
           WHERE cr.release_id=$2 AND t.status<>'retired' AND (t.level=$6 OR st.user_id IS NOT NULL) AND r.type || '@1' = ANY($3::text[])
             AND ($4::text IS NULL OR ($4='target' AND t.id=$5::uuid)
               OR ($4='topic' AND t.skill_id IN (SELECT id FROM gm.skill WHERE topic_id=$5::uuid)))`,
          [userId, catalog.session.contentReleaseId, request.capabilities, focus?.type ?? null, focus?.id ?? null, profile.preferences.level]);
        const questions = selectQuestions(eligible.rows.map(c => ({ ...c,
          dueAt: c.dueAt ? new Date(c.dueAt).toISOString() : null })), request.questionCount, now);
        if (questions.length < request.questionCount) throw new ApiFailure("insufficient_content", 409);
        sessionId = randomUUID();
        await tx.query("INSERT INTO gm.practice_session (id,user_id,request_id,request_payload,release_id,engine_version,status,issued_at) VALUES ($1,$2,$3,$4,$5,$6,'active',$7)",
          [sessionId, userId, request.requestId, request, catalog.session.contentReleaseId, SELECTION_POLICY_VERSION, now]);
        for (const [position, question] of questions.entries())
          await tx.query("INSERT INTO gm.session_question (id,user_id,session_id,release_id,exercise_id,revision,position,evidence_role) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)",
            [randomUUID(), userId, sessionId, catalog.session.contentReleaseId, question.exerciseId, question.revision, position, question.role]);
      }
      const questions = await tx.query<{ id: string; payload: Exercise }>(
        "SELECT q.id,r.payload FROM gm.session_question q JOIN gm.exercise_revision r ON r.exercise_id=q.exercise_id AND r.revision=q.revision WHERE q.session_id=$1 AND q.user_id=$2 ORDER BY q.position", [sessionId, userId]);
      return SessionSchema.parse({ apiVersion: "v2", id: sessionId, contentReleaseId: releaseId,
        questions: questions.rows.map(q => ({ id: q.id, exercise: q.payload })) });
  }

  async submit(userId: string, attempt: Attempt, requestId: string): Promise<Acknowledgment> {
    try {
      return await this.db.transaction(async tx => {
        await this.lockLearner(tx, userId);
        const prior = await tx.query<{ payload: Attempt; received_sequence: number; evaluation: AttemptAcknowledgment["evaluation"] }>(
          "SELECT a.payload,a.received_sequence,e.evaluation FROM gm.attempt a JOIN gm.attempt_evaluation e ON e.user_id=a.user_id AND e.attempt_id=a.id WHERE a.user_id=$1 AND a.id=$2 ORDER BY e.evaluated_at LIMIT 1", [userId, attempt.attemptId]);
        if (prior.rows.length) {
          if (canonical(prior.rows[0].payload) !== canonical(attempt)) throw new ApiFailure("attempt_conflict", 409);
          return { attemptId: attempt.attemptId, status: "duplicate", evaluation: prior.rows[0].evaluation, serverSequence: prior.rows[0].received_sequence };
        }
        const pinned = await this.pinnedQuestion(tx, userId, attempt.sessionQuestionId, attempt.exerciseRevision);
        await this.ensureOpen(tx, userId, attempt.sessionQuestionId);
        const evaluation = grade(pinned.payload, pinned.rubric, attempt.answer, attempt.assistance);
        await tx.query("INSERT INTO gm.device VALUES ($1,$2) ON CONFLICT DO NOTHING", [userId, attempt.deviceId]);
        const now = this.clock().toISOString();
        const inserted = await tx.query<{ received_sequence: number }>(
          "INSERT INTO gm.attempt (user_id,id,question_id,device_id,payload,received_at) VALUES ($1,$2,$3,$4,$5,$6) RETURNING received_sequence",
          [userId, attempt.attemptId, attempt.sessionQuestionId, attempt.deviceId, attempt, now]);
        await tx.query("INSERT INTO gm.attempt_evaluation VALUES ($1,$2,$3,$4,$5)",
          [userId, attempt.attemptId, EVALUATOR_VERSION, evaluation, now]);
        await this.saveEvidence(tx, userId, attempt.attemptId, pinned, now, {
          kind: pinned.evidence_role, outcome: evaluation.outcome as "correct" | "incorrect",
          assisted: evaluation.assisted, evaluationVersion: evaluation.policyVersion,
          variantKey: pinned.variant_key, contextKey: pinned.context_key,
          ...(pinned.transfer_key ? { transferKey: pinned.transfer_key } : {}),
          answeredAt: attempt.answeredAt,
        }, "attempt");
        await this.completeSession(tx, userId, attempt.sessionQuestionId);
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

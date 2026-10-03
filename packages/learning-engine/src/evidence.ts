/** Server-only evidence policy. Inputs are accepted, immutable events, not client claims. */
export const EVIDENCE_POLICY_VERSION = "retained-evidence-v1";
const INTERVAL_DAYS = [1, 3, 7, 14, 30] as const;
export type LearningState = "new" | "learning" | "needs_practice" | "improving" | "mastered";

type EvidenceIdentity = {
  id: string;
  learnerId: string;
  targetId: string;
  receivedSequence: number;
  receivedAt: string;
  sessionIssuedAt: string;
  answeredAt?: string;
  /** Saved profile timezone at ingestion; never supplied by an answer payload. */
  timeZone: string;
};
export type AcceptedEvidence = EvidenceIdentity & (
  | { kind: "exposure" }
  | { kind: "assessment" | "reinforcement"; outcome: "correct" | "incorrect";
      assisted: boolean; evaluationVersion: string;
      /** Pinned editorial identities, not exercise IDs or shuffled token orders. */
      variantKey: string; contextKey: string; transferKey?: string }
);
export type QualifyingCheck = {
  evidenceId: string; localDate: string; timeZone: string;
  variantKey: string; contextKey: string; transferKey?: string;
};
export type EvidenceDecision = {
  evidenceId: string; localDate: string | null;
  timing: "validated" | "missing" | "before_session" | "after_receipt";
  effect: "exposure" | "assisted" | "reinforcement" | "failure" | "unqualified_timing" |
    "same_day" | "before_review" | "before_failure" | "qualifying_success";
};
export type TargetSnapshot = {
  policyVersion: typeof EVIDENCE_POLICY_VERSION;
  learnerId: string; targetId: string; state: LearningState;
  exposureCount: number; lastPracticeAt: string | null; lastInformativeAt: string | null;
  qualifyingChecks: QualifyingCheck[];
  independentFailures: { evidenceId: string; receivedAt: string; localDate: string; timeZone: string }[];
  transitions: { evidenceId: string; receivedAt: string; from: LearningState; to: LearningState }[];
  everMastered: boolean; lapseCount: number;
  /** Retention gate stays fixed when assistance brings the next practice review forward. */
  nextQualifyingAt: string | null;
  schedule: { dueAt: string; localDate: string; timeZone: string; intervalStep: number;
    intervalDays: number } | null;
  decisions: EvidenceDecision[];
  /** Computed using the injected clock; passage of time never changes learning state. */
  isDue: boolean;
};
export type ReductionContext = {
  learnerId: string; targetId: string; targetKind: "lexical" | "concept";
  timeZone: string; now: string; policyVersion: typeof EVIDENCE_POLICY_VERSION;
};

function instant(value: string): number {
  if (typeof value !== "string" || !/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d{3})?Z$/.test(value))
    throw Error("invalid_instant");
  const ms = Date.parse(value);
  if (!Number.isFinite(ms) || new Date(ms).toISOString().replace(".000Z", "Z") !== value.replace(".000Z", "Z"))
    throw Error("invalid_instant");
  return ms;
}

function formatter(timeZone: string): Intl.DateTimeFormat {
  if (!nonempty(timeZone)) throw Error("invalid_time_zone");
  try {
    return new Intl.DateTimeFormat("en-US", { timeZone, year: "numeric", month: "2-digit", day: "2-digit" });
  } catch { throw Error("invalid_time_zone"); }
}
function dateAt(ms: number, format: Intl.DateTimeFormat): string {
  const parts = format.formatToParts(ms);
  const part = (type: string) => parts.find(p => p.type === type)!.value;
  return `${part("year").padStart(4, "0")}-${part("month")}-${part("day")}`;
}
function plusDays(date: string, days: number): string {
  const ms = Date.parse(`${date}T00:00:00Z`) + days * 86400000;
  return new Date(ms).toISOString().slice(0, 10);
}
function daysBetween(first: string, last: string): number {
  return (Date.parse(`${last}T00:00:00Z`) - Date.parse(`${first}T00:00:00Z`)) / 86400000;
}
/** Earliest instant of the local calendar day, including DST and midnight offset changes.
 * A skipped calendar day resolves to the first following local day. */
function startOfDay(date: string, format: Intl.DateTimeFormat): string {
  const nominal = Date.parse(`${date}T00:00:00Z`);
  let low = nominal - 36 * 3600000, high = nominal + 36 * 3600000;
  while (low < high) {
    const mid = Math.floor((low + high) / 2);
    if (dateAt(mid, format) < date) low = mid + 1;
    else high = mid;
  }
  return new Date(low).toISOString();
}
function nonempty(value: string): boolean { return typeof value === "string" && value.trim().length > 0; }

/** Rebuild from receipt sequence, never caller array order or client answer time.
 * Reject duplicate identities: idempotency belongs to ingestion, before this boundary. */
export function reduceEvidence(context: ReductionContext, input: readonly AcceptedEvidence[]): TargetSnapshot {
  if (context.policyVersion !== EVIDENCE_POLICY_VERSION) throw Error("unsupported_evidence_policy");
  if (!nonempty(context.learnerId) || !nonempty(context.targetId) ||
      !["lexical", "concept"].includes(context.targetKind)) throw Error("invalid_target_context");
  formatter(context.timeZone);
  const now = instant(context.now);
  const events = [...input].sort((a, b) => a.receivedSequence - b.receivedSequence);
  const ids = new Set<string>(), sequences = new Set<number>(), usedDates = new Set<string>();
  let previousReceipt = -Infinity;
  let failureAt: number | null = null;
  const result: TargetSnapshot = {
    policyVersion: EVIDENCE_POLICY_VERSION, learnerId: context.learnerId, targetId: context.targetId,
    state: "new", exposureCount: 0, lastPracticeAt: null, lastInformativeAt: null,
    qualifyingChecks: [], independentFailures: [], transitions: [], everMastered: false,
    lapseCount: 0, nextQualifyingAt: null, schedule: null, decisions: [], isDue: false,
  };
  for (const event of events) {
    if (event.learnerId !== context.learnerId || event.targetId !== context.targetId)
      throw Error("evidence_scope_mismatch");
    if (!nonempty(event.id) || ids.has(event.id) || sequences.has(event.receivedSequence) ||
        !Number.isSafeInteger(event.receivedSequence) || event.receivedSequence < 1)
      throw Error("invalid_evidence_identity");
    ids.add(event.id); sequences.add(event.receivedSequence);
    const received = instant(event.receivedAt), issued = instant(event.sessionIssuedAt);
    if (received < previousReceipt || received > now || issued > received) throw Error("invalid_receipt_order");
    previousReceipt = received;
    const format = formatter(event.timeZone), receiptDate = dateAt(received, format);
    const answered = event.answeredAt === undefined ? null : instant(event.answeredAt);
    const timing: EvidenceDecision["timing"] = answered === null ? "missing" : answered < issued ?
      "before_session" : answered > received ? "after_receipt" : "validated";
    const localDate = timing === "validated" ? dateAt(answered!, format) : null;
    const decision: EvidenceDecision = { evidenceId: event.id, localDate, timing, effect: "exposure" };
    result.decisions.push(decision);
    result.exposureCount++;
    result.lastPracticeAt = new Date(received).toISOString();
    const schedule = (days: number, step: number, onlyEarlier = false) => {
      const nextDate = plusDays(receiptDate, days), dueAt = startOfDay(nextDate, format);
      if (!onlyEarlier || !result.schedule || dueAt < result.schedule.dueAt)
        result.schedule = { dueAt, localDate: dateAt(instant(dueAt), format), timeZone: event.timeZone,
          intervalStep: step, intervalDays: days };
    };
    const transition = (to: LearningState) => {
      if (to === result.state) return;
      result.transitions.push({ evidenceId: event.id, receivedAt: event.receivedAt, from: result.state, to });
      result.state = to;
    };
    if (event.kind === "exposure") continue;
    if (!["assessment", "reinforcement"].includes(event.kind) ||
        !["correct", "incorrect"].includes(event.outcome) || typeof event.assisted !== "boolean" ||
        !nonempty(event.evaluationVersion) || !nonempty(event.variantKey) || !nonempty(event.contextKey) ||
        (event.transferKey !== undefined && !nonempty(event.transferKey))) throw Error("invalid_evaluation_evidence");
    if (event.kind === "reinforcement") { decision.effect = "reinforcement"; continue; }
    if (event.assisted) {
      decision.effect = "assisted";
      schedule(1, result.schedule?.intervalStep ?? 0, true);
      continue;
    }
    result.lastInformativeAt = event.receivedAt;
    if (event.outcome === "incorrect") {
      decision.effect = "failure";
      if (result.state === "mastered") result.lapseCount++;
      const failureDate = localDate ?? receiptDate;
      failureAt = answered !== null && timing === "validated" ? answered : received;
      result.independentFailures.push({ evidenceId: event.id, receivedAt: event.receivedAt,
        localDate: failureDate, timeZone: event.timeZone });
      result.qualifyingChecks = [];
      transition("needs_practice");
      schedule(1, 0);
      result.nextQualifyingAt = result.schedule!.dueAt;
      continue;
    }
    if (timing !== "validated") {
      decision.effect = "unqualified_timing";
      schedule(1, result.schedule?.intervalStep ?? 0, true);
      if (result.state === "new") transition("learning");
      continue;
    }
    // A late success cannot undo a later accepted failure or become a second check that day.
    if (failureAt !== null && (answered! <= failureAt || localDate! <= dateAt(failureAt, format))) {
      decision.effect = "before_failure"; continue;
    }
    if (usedDates.has(localDate!) || (result.qualifyingChecks.length > 0 &&
        localDate! <= result.qualifyingChecks.at(-1)!.localDate)) {
      decision.effect = "same_day"; continue;
    }
    if (result.nextQualifyingAt && answered! < instant(result.nextQualifyingAt)) {
      decision.effect = "before_review"; continue;
    }
    decision.effect = "qualifying_success";
    usedDates.add(localDate!);
    result.qualifyingChecks.push({ evidenceId: event.id, localDate: localDate!, timeZone: event.timeZone,
      variantKey: event.variantKey, contextKey: event.contextKey, transferKey: event.transferKey });
    const checks = result.qualifyingChecks;
    const diverse = context.targetKind === "concept" ?
      new Set(checks.map(c => c.transferKey).filter(Boolean)).size >= 2 :
      new Set(checks.map(c => c.variantKey)).size >= 2 || new Set(checks.map(c => c.contextKey)).size >= 2;
    if (checks.length >= 4 && daysBetween(checks[0].localDate, checks.at(-1)!.localDate) >= 14 && diverse) {
      transition("mastered"); result.everMastered = true;
    } else if (checks.length >= 2) transition("improving");
    else if (result.state !== "needs_practice") transition("learning");
    const step = Math.min(checks.length - 1, INTERVAL_DAYS.length - 1);
    schedule(INTERVAL_DAYS[step], step);
    result.nextQualifyingAt = result.schedule!.dueAt;
  }
  result.isDue = result.schedule !== null && instant(result.schedule.dueAt) <= now;
  return result;
}

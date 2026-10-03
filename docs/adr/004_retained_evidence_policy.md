# ADR 004 Versioned retained-evidence reducer

Status: Accepted for the offline engine demonstration, 4 October 2026.

## Decision

Implement blueprint Sections 12 and 19 as the pure server-owned `retained-evidence-v1` policy in `packages/learning-engine`. Rebuild one learner/primary target from immutable accepted events in server receipt-sequence order. Caller array order and client answer order never determine replay. Reject duplicate IDs/sequences, mixed ownership, unsupported policy versions, invalid metadata and impossible receipt chronology. Ingestion must deduplicate before this boundary. The clock is injected; no ambient system clock or timezone participates.

The projection records policy version, qualifying checks, independent failures, transitions, previous mastery, lapse count, exposure count, last practice, last informative attempt, next retention eligibility and review schedule. It preserves the underlying evidence; resetting the current success cycle after failure does not erase earlier mastery transitions. A subsequent rebuild must use the same named policy, pinned editorial metadata, evaluation versions, session times and saved profile timezones. A policy update needs an explicit new version and migration/replay decision.

## Operational choices within the blueprint

- First unassisted failure enters Needs practice; first qualifying success enters Learning. Two spaced successes enter Improving. After failure, the first later qualifying success keeps Needs practice and the second enters Improving.
- Every accepted event adds exposure. Explicit exposure/skip events do nothing else. Reinforcement correct or incorrect is not an independent retention assessment. Assisted correct or incorrect requests an independent next-day review, without creating an independent failure or advancing the ladder. These are internal engine types, not new public skip endpoints.
- Independent correct assessments qualify at most once per local date and only at/after the previously established retention gate. Four checks must span at least 14 local calendar days. Lexical targets additionally need two pinned editorial variant identities or contexts. Concept targets need two explicit editorial transfer identities; exercise IDs, shuffled tokens and changed revision numbers alone do not demonstrate transfer. Missing diversity leaves the target Improving. The current catalog must not invent a second identity to pass this gate.
- After qualifying success, intervals are 1, 3, 7, 14, then 30 calendar days; subsequent successful reviews remain at 30 days. The first success sets ladder step 0. A failure clears current-cycle checks, resets step 0 and schedules tomorrow. Each transition from Mastered to Needs practice records one lapse; repeated failures within that lapse do not manufacture additional cycles.
- Due dates are UTC instants at the start of the next scheduled local day in the event's saved profile timezone. Calendar-day arithmetic accounts for DST; a historically skipped day resolves to the first following real day. A timezone change never moves an existing UTC retention gate earlier. The injected clock only derives `isDue`; elapsed time cannot demote Mastered.
- Assistance may bring the practice due date earlier, while a separate `nextQualifyingAt` retains the established spacing gate. It cannot postpone an overdue review or accelerate the retention ladder. Skip/reinforcement does not change either date.
- Scheduling is anchored to server receipt time, not an arbitrary client clock. A supplied answer time qualifies only within the session-issued/received interval. Missing or out-of-window timing retains correctness/exposure but cannot supply a retention success. An independent failure still records difficulty using receipt time when answer timing is implausible. Malformed timestamps are rejected. A late success predating the latest accepted failure cannot repair it. Same-day and premature successes do not move the schedule or contribute diversity.

These thresholds are a proposed operational policy, not a calibrated probability or a claim of fluency. Tests use synthetic histories and do not approve German content.

## Next boundary

This slice is the pure reducer only. The existing API still accepts and grades answers without invoking it. Next, persist accepted evidence with pinned editorial classification and saved timezone; atomically write the attempt, evaluation, projection, schedule and sync delta. Rebuild/replay and concurrency tests must prove that boundary before adding server-selected sessions. Explicit skip/exposure transport contracts must be introduced alongside their ingestion endpoint. Production auth, network PostgreSQL contention, durable clients, content approval and publication remain separate gates.

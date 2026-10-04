# ADR 005 Transactional accepted evidence and projections

Status: Accepted for the isolated local demonstration, 4 October 2026.

## Decision

Connect the existing grader and `retained-evidence-v1` reducer inside the PostgreSQL transaction that accepts each answer. Persist immutable accepted evidence, derived target state, separate review schedule, and an immutable sync change before acknowledgment. Save the question's server-owned assessment/reinforcement role, session issuance, profile timezone at ingestion, evaluator version and explicit editorial variant/context/transfer identities. Map schema `grammar` targets to the reducer's `concept` gate. Freeze target educational definitions and exercise linkage; meaningful changes require new identities.

Use a learner-profile row lock before idempotency/first-submission checks. This intentionally serializes all receipt/projection writes for one learner and prevents competing same-target sessions from overwriting evidence. Sequence gaps after rollback are valid. Attempt acknowledgment sequences stay compatible with the original attempt sequence; exposure acknowledgment sequences refer to accepted evidence. Sync sequence is separate and is not yet exposed as a transport cursor.

Add the strict versioned exposure batch contract and ingestion endpoint together. `exposure` is nonterminal; `skip` is terminal without an evaluation. Both feed exposure-only evidence. An answer and skip compete for one terminal submission, using the same learner lock. Replays preserve original evaluation/sequence without new projections or sync changes; altered payloads conflict. The public transport cannot supply editorial identities, evidence roles, grades, mastery or timezone.

All target evidence is rebuilt in receipt order on ingestion. The read-only `rebuild` method independently reproduces the projection; the injected clock recalculates `isDue` without changing mastery. Stored snapshots and sync payloads report due eligibility at ingestion time, not indefinitely current eligibility. Future read/selection endpoints must refresh it from UTC schedule deadlines.

## Local compatibility

Keep migration 001 unchanged. Migration 002, identity seeding, old-attempt backfill and the schema ledger commit together. Existing sessions lack trustworthy issuance and historical timezone, so backfill conservatively omits educational answer timing, preserves outcomes/exposure and uses current saved timezone. It never retroactively grants qualifying retention successes. New sessions save issuance. Existing pending sessions use a conservative first-receipt/upgrade bound. This compatibility path applies only to saved local fixtures, not historical learner migration.

The one-variant catalog receives descriptive editorial identities. Revisions, exercise IDs and repeated correct answers do not create additional diversity. Independent German-language approval remains pending.

## Limits

PGlite proves isolated PostgreSQL transaction/constraint/trigger behavior, including competing promises in one adapter and filesystem restart. It does not establish network multi-connection locking, production roles or authenticated identity. The local launcher remains loopback-only, fixture-authenticated and ephemeral. Full-history reduction is a demonstration implementation. Public sync retrieval, durable clients, adaptive selection, full learner navigation and account deletion remain open. No publication, production migration/cutover, store release or live inference is authorized by this decision.

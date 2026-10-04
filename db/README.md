# Isolated target-centered reset database

`migrations/001_target_foundation.sql` introduces the first used subset of the blueprint in private schema `gm`: topic → skill → target → immutable exercise revision → pinned draft release → owned session/question → attempt/evaluation. Ownership is enforced by composite foreign keys as well as API checks. Unique `(user_id, question_id)` enforces first submission. Versioned rubrics and received sequences preserve evidence; triggers reject mutations of pinned revisions, release members, questions, attempts and evaluations.

The API's local repository runs this migration and seeds only the five unpublished foundation drafts inside one transaction. PGlite 0.5.8 executes PostgreSQL in WASM; tests use a fresh isolated database and a temporary filesystem database for close/reopen replay. This is real PostgreSQL constraint/trigger behavior, not a SQL mock. It does not establish parity with a hosted multi-connection PostgreSQL deployment.

No migration runner targets network databases. Do not apply this to the inherited production environment. The local demonstration connection owns its private schema; production needs a separate migration role, least-privilege API grants, verified identity and a network PostgreSQL adapter. No client database access is provided. Identity provider integration, full lexical/provenance resources, prerequisites/cycle validation, public sync transport and privacy deletion remain future slices. The append-only guard needs an explicit audited privileged deletion process before account deletion ships.

This migration is a local draft. Preserve its history once adopted by a persistent reset environment; subsequent changes then require a new numbered migration. Draft content is never presented as independently approved or published.

## Evidence migration

`002_evidence_projection.sql` adds a local schema ledger, issuance timestamps, immutable editorial identities and question evidence roles, explicit skip/exposure events, accepted evidence, target state, review schedules and ordered sync changes. Initialization upgrades a prior local saved database transactionally and records version 2 only after seeding/backfill succeeds. Reinitialization does not replay backfill twice. Target definitions, exercise linkage and session issuance are immutable; session completion status may change.

Historical foundation sessions did not save issuance time or historical profile timezone. Their issuance is conservatively bounded by first receipt (or upgrade time for unanswered sessions); existing outcomes are backfilled with missing educational answer time and current saved timezone. No historical spaced-success credit is fabricated. This is local demonstration compatibility, not a migration of real learners. The five identities remain agent-authored drafts with one variant/transfer per target.

The service locks one learner profile before ingestion, allocates receipt sequence inside the transaction, and atomically saves source, evaluation where applicable, evidence, projection, schedule and sync delta. Sequence gaps after rollback are normal. Sync records contain owned projection payloads, not raw answers. Full-history reduction is intentionally simple for this small demonstration; production-scale projection/replay and migration orchestration remain open.

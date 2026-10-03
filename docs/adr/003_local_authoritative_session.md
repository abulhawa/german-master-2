# ADR 003 Local authoritative session boundary

Status: Accepted for the foundation demonstration, 3 October 2026.

## Decision

Introduce `packages/learning-engine` with a pure deterministic grader and `services/api` with session creation and attempt ingestion. Use the existing five agent-reviewed drafts, with versioned NFC/outer-space normalization and explicit alternatives, to exercise the first complete backend session. Server evaluations include assistance and the evaluator/normalizer versions. No mastery or scheduling policy runs on clients.

Add the first used target-centered PostgreSQL schema under root `db`. Execute it locally using pinned PGlite 0.5.8 (PostgreSQL in WASM). This gives offline PostgreSQL constraint/trigger/transaction tests and filesystem close/reopen evidence without production credentials or a workstation PostgreSQL service. The isolated local launcher deliberately uses an ephemeral database. The public fixture token is confined to development/debug previews and the loopback demonstration; it is not product authentication.

The repository serializes transactions in one embedded PostgreSQL instance. Composite ownership references and unique first-submission constraints reinforce API validation. Store raw payloads and evaluations in the same transaction, replay exact original results and reject changed payloads. Mark full completion only after all owned questions have an accepted attempt. Preserve revisions and evidence with append-only triggers; corrections require new revisions/audit entries.

## Limits and next boundary

PGlite is a local execution/test adapter, not a replacement for the blueprint's isolated network PostgreSQL environments. Before staging, introduce a verified identity adapter, a least-privilege API role, versioned deployment migrations and a network PostgreSQL repository with multi-connection contention checks. Do not infer those from single-instance tests.

Web and Android backend previews use the same server contracts; original static renderer previews remain. Their pending attempts survive a retry within the mounted preview, not process death. Production navigation, identity/signing and legacy infrastructure remain intact. The shared `Evaluation.assisted` field is a required draft-contract addition, generated into TypeScript/Kotlin before publication.

This implements the authoritative grading demonstration only. Independent German-language review, 30 targets, broader schema/content acceptance, evidence reduction, scheduling, adaptive selection, durable sync, production authentication and release gates stay open. No production reset/cutover, published release or AI inference is part of this decision.

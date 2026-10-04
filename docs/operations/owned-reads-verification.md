# Owned target and sync read verification

4 October 2026. Continuation from clean `dc1d927`, matching remote main. Existing server-selection hosted workflows were confirmed successful before implementation.

## Implemented evidence

Generated v2 confirmed summary/page contracts, authenticated `/v2/targets` and `/v2/sync`, and local migration 003. [ADR 007](../adr/007_owned_target_sync_transport.md) defines cursor scope, frozen snapshot semantics, sync polling and ingestion-time versus current due flags.

- Real accepted answer history produces confirmed Needs practice summaries. Before the saved UTC deadline, due is false; exactly at the deadline it is true in a fresh target read. Stored projections and sync deltas remain byte-equivalent through clock changes. Duplicate submissions produce no additional delta.
- One-target pages cover all five target IDs without duplicates. Following a stored page replays the original time/watermark/content despite a new limit, clock movement or ingestion. New evidence accepted during pagination is returned after the original sync watermark. A concurrent snapshot/submission test verifies evidence is covered by either snapshot or subsequent sync.
- Sparse owned sequences (1 and 3, with another learner at 2) paginate without advancing over undelivered changes. Answer and skip/exposure updates preserve increasing target summaries. Empty pulls retain the polling cursor. Foreign, unknown and wrong-kind cursors fail; immutable cursor records reject deletion.
- Filesystem-backed PGlite close/reopen preserves snapshot continuation pages and sync positions/payloads. Repeated initialization does not rerun migration 003. Existing pre-evidence upgrade tests continue through the new migration.
- Loopback HTTP tests validate authentication, owned cursor retrieval, accepted-state sync replay, no-store caching, bounds, unknown/duplicate query parameters and malformed cursors. Responses omit learner identity, solutions, answers and pending client work.
- Shared target/sync fixtures round-trip in TypeScript and Kotlin. Canonical UTC timestamps with three fractional digits preserve server precision; both reject invalid dates, offsets and client pending fields. Due remains a flag rather than a state enum.

## Local verification

- Root `npm ci`, `npm run check`, `npm test`, `npm run build` passed with Node 22.23.3/npm 10.9.9. Backend: seven files / 61 tests. Web: 78 files / 301 tests. Backend type check and the four read tests repeated after final HTTP/concurrency assertions.
- Android offline `testDebugUnitTest lintDebug assembleDebug --no-daemon --max-workers=4` passed. 26 suites / 99 tests, zero failures/errors/skips. Lint: zero errors / 16 existing warnings. Debug assembly passed. No device/emulator/TalkBack verification is claimed.
- Built loopback launcher smoke passed: frozen two-target snapshot page, accepted incorrect answer, one owned sync delta after the snapshot watermark and unchanged continuation time. The temporary in-memory launcher was stopped.
- Existing installation audit: 12 findings (six moderate/six high); stale Browserslist data and Android deprecations remain. No dependency changes or live services were used. No browser rendering was reverified because no learner UI changed.
- Diff whitespace verification and Gitleaks 8.30.1 staged/full-history scans passed with zero findings (779 commits at implementation head `3d5ab87`). Implementation is committed and pushed to main. All three hosted workflows passed at that exact commit: [Web checks](https://github.com/abulhawa/german-master-2/actions/runs/37193710368), [Android checks](https://github.com/abulhawa/german-master-2/actions/runs/37193710406) and [Repository safety](https://github.com/abulhawa/german-master-2/actions/runs/37193710348).

## Limits and next action

This is confirmed read transport for the isolated five-draft catalog. Production identity/network PostgreSQL, independent connection contention, cursor retention/expiry/reset, content/account tombstones and durable client reconciliation remain open. No client reads these endpoints yet. M0/M1 exit gates remain open; M2 stays in progress. No production mutation, deployment, content publication, store release or AI/Groq call occurred.

Next: implement the isolated 2.0 web learner flow using confirmed reads, server-selected sessions and server feedback, with an honest shorter-session offer for this catalog. Preserve confirmed/pending separation and leave production cutover separate.

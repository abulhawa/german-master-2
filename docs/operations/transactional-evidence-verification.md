# Transactional evidence verification

Verified locally on 4 October 2026, starting from clean `b5b288a` and matching remote main. Prior evidence-policy Web and Repository safety runs were inspected and both report success at `a520eabcdfe2eb844298b4583925c7471a4c4a1f`.

## Implemented boundary

`services/api/src/store.ts` now invokes the existing reducer per accepted answer/exposure inside the source transaction. Migration 002 persists immutable evidence identities/roles/issuance, target projections, review schedules and ordered sync changes. Learner row locking orders ingestion. Exposure transport is generated into TypeScript and Kotlin from the versioned schema, with an authenticated batch endpoint and shared acceptance corpus. See [ADR 005](../adr/005_transactional_evidence.md).

The new `evidence-store.test.ts` checks:

- Saved editorial identities, issuance, evaluator version and profile timezone; exact database/sync projection equality with independent replay; timezone changes leave existing gates intact.
- Original acknowledgment on replay with unchanged database counts, including after filesystem close/reopen.
- Failure at the final sync insertion rolls back answer/evaluation/evidence/state/schedule/device and leaves the session active. Skip has the same rollback coverage. A retry succeeds after removing the fault.
- Concurrent promises for separate sessions of the same target retain both events and one qualifying local date. Competing answer/skip accepts one terminal event only; foreign ownership discloses no evaluation.
- Assisted practice can shorten the practice schedule without shortening the independent retention gate. Server-pinned reinforcement does not supply independent evidence. Four sufficiently spaced correct answers from the one-variant catalog remain Improving.
- Nonterminal exposure, terminal skip without grading, exact event replay, changed-payload conflict, strict HTTP auth/body validation and TypeScript/Kotlin shared conformance.
- A reconstructed migration-001 database upgrades with outcome/exposure preservation and no retrospective qualifying success. Reinitialization does not duplicate backfill or acknowledgments.

## Local checks

- Root `npm ci`: passed with Node 22.23.3/npm 10.9.9; inherited dependency deprecation/audit notices remain.
- Root `npm run check`: generated contracts/tokens, web TypeScript and backend TypeScript passed.
- Root `npm test`: backend 4 files / 51 tests; web 78 files / 301 tests; all passed. The first targeted backend run found a timestamp-format assertion mismatch (`Z` versus `.000Z`); the expectation was corrected to canonical server issuance. Subsequent complete checks passed.
- Root `npm run build`: web client, inherited API/server bundles and isolated foundation launcher passed.
- Built `services/api/dist/local.js` smoke check on isolated loopback port 5017: created two questions, accepted a correct short answer, accepted a skip and replayed the skip with its original sequence. The smoke process was stopped afterward.
- Android `testDebugUnitTest lintDebug assembleDebug --offline --no-daemon --continue --max-workers=4`: passed; 26 suites / 98 tests, zero failures/errors/skips, lint zero errors / 16 warnings. New generated exposure contracts decode and round-trip the shared acceptance corpus. No emulator/device verification or new UI behavior is claimed.
- Whitespace checks and staged/full-history Gitleaks scans passed with zero findings; full history covered 775 commits at implementation `b5650c9`. All three hosted workflows passed at exact implementation `b5650c9`: [Web](https://github.com/abulhawa/german-master-2/actions/runs/37166238284), [Android](https://github.com/abulhawa/german-master-2/actions/runs/37166238314) and [Repository safety](https://github.com/abulhawa/german-master-2/actions/runs/37166238283). The root checkpoint records the final handoff.

All ordinary checks used local fixtures/mocks. No production database/environment was loaded, no AI/Groq inference calls occurred, no content was published and no store release was uploaded.

## Limits and next slice

The PGlite adapter serializes one embedded PostgreSQL instance. Competing promises exercise the repository boundary; they do not prove network multi-connection contention. Production identity, least-privilege roles and network PostgreSQL adapter/migrations remain required before staging. The loopback launcher remains in-memory. A persisted database is exercised only by tests.

`isDue` in a stored/synced snapshot is as of ingestion; replay derives it from the injected clock. Future reads/selection must refresh eligibility from the UTC deadline. Full-history reduction is deliberately simple for this small catalog. Public sync retrieval/cursors, account deletion, client outboxes and skip UI remain open. No independent content approval, milestone exit or complete learner journey is claimed.

Next: server-selected sessions with differing learner histories, preserving revision/capability/idempotency semantics and using current schedule eligibility. Follow the updated root checkpoint.

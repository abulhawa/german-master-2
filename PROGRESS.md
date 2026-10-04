# German Master 2.0 renovation checkpoint

Last updated: 4 October 2026. This is the current handoff; update it after every implementation slice. Git history and actual working files take precedence over stale checkpoint claims.

## Resume instruction

The owner wants to continue the renovation across new Codex chats with a simple "continue" request. Read this file and `docs/operations/continuation.md`, then implement the next unfinished slice. Do not repeat the architecture exercise or ask which feature to start when the next action is already specified.

## Product and accepted decisions

- B1–B2 German practice companion: find recurring weaknesses, practise them, verify retained improvement.
- One monorepo with separate React web and native Kotlin Android clients.
- One authoritative backend for confirmed grading, mastery, scheduling and sync; this target architecture is not implemented yet.
- Main learner areas: Home, Practice, Progress, Topics. Import, speech assessment, unrestricted essay grading and AI chat are deferred.
- No real users or historical learner migration. Use new isolated environments for the reset; preserve legacy assets and Android application/signing identity.
- The owner explicitly requested the new repo be public, including the imported Android history.

## Working repository

- Local root: `C:/Projects/german-master-2`
- Remote: `https://github.com/abulhawa/german-master-2`
- Primary branch: `main`
- This owned-read continuation started at `dc1d927` with a clean checkout and matching remote. Server-selection workflows were confirmed successful at `7f4a2ec`. The current slice exposes owned confirmed target snapshots and ordered sync changes with durable local cursors.
- Original repos remain preserved at `C:/Projects/german-master` and `C:/Projects/GermanVerbMaster-Android`. Do new work here, not in those repos.

## Completed and verified

- Both codebases imported under `apps/web` and `apps/android` through full-history Git subtrees without squashing.
- Tags `legacy-web-baseline` and `legacy-android-baseline` retain original revision references; complete local recovery bundles were verified under ignored `.local`.
- Imported file trees matched originals before bootstrap edits. Original repos remained clean.
- Root npm workspace with one lockfile preserves all inherited dependency versions and overrides.
- Complete blueprint, executive summary, reading editions, repository ADR and development/verification guides committed.
- Separate web, Android and repository-safety workflows configured and active. Hosted run outcomes have not yet been established; inspect them before claiming CI is green.
- Local web install, TypeScript check, and web/API/server build passed. All 76 web test files and 280 tests passed.
- Local Android debug assembly passed. All 22 Android unit suites and 90 tests passed with no failures, errors or skips.
- Gitleaks 8.30.1 full history scan passed with zero findings. No Groq inference calls were made.
- Durable continuation instructions and checkpoint added so subsequent chats can resume from repository files.
- Android lint crash repaired by converting all four Gradle scripts to Groovy; AGP 9.4.1 and Gradle 9.8.0 pinned. No checks were suppressed. The Analytics locale lint error was fixed, and blocking errors/release lint are enabled.
- Final offline Android verification passed: 22 suites / 90 tests, zero failures/errors/skips; debug assembly passed; lint reported zero errors and 16 warnings. [Detailed repair evidence](docs/operations/android-lint-repair.md) includes intermediate failures and runtime limits.
- Both legacy Git bundles restored into disposable bare repositories, passed full integrity checks and matched baseline tag trees. [Recovery evidence](docs/operations/legacy-recovery.md).
- Owner clarified that the hard reset allows current/replacement dependencies and design changes within the renovation scope. Recorded in `AGENTS.md`; preservation, identity and release boundaries remain.
- At the owner's request, saved four visual concept boards covering six web/Android screens under [docs/design/mockups](docs/design/mockups/README.md), with exact prompts and review notes. Includes desktop Home/feedback, native Home/Progress/Topics, and offline dark feedback. Visually inspected concepts with illustrative data; no UI implementation, content approval or accessibility acceptance is claimed. Four built-in generation calls and one edit; zero Groq calls.
- First M1 foundation implemented: `contracts` is an npm workspace with generated TypeScript/Zod and Kotlin transport models from versioned JSON Schema, draft OpenAPI session/attempt boundaries, and shared conformance/answer/acknowledgment examples. Both clients render the same five exercise forms from one solution-free session fixture.
- Shared semantic design tokens generate scoped CSS and native Kotlin bindings with contrast checks. Reusable input/card/action components power isolated web `/foundation` development preview and native debug-only `FoundationPreviewActivity`. No client grading or mastery policy was added.
- Five original target-centered examples have an agent editorial review, with provenance, accepted forms and ambiguity constraints in `content/foundation`. They are unpublished drafts; independent German-language review and the broader 30-target M1 gate remain open.
- Accessible M0 preservation advanced: identified legacy production deployment and preserved its exact source/history in a verified local bundle; exported seven public database tables in a read-only transaction and verified exact row hashes through an isolated JSON archive recovery. Full PostgreSQL/auth/storage restore and off-machine recovery remain open. [Inventory and blockers](docs/operations/m0-inventory.md).
- Foundation verification: root install/check/build passed; web 77 files / 297 tests passed with two workers; Android offline 25 suites / 95 tests passed, debug assembly passed, lint zero errors / 16 warnings. Shared native rendering is verified under Robolectric, not on a device. Browser verification covered 320px light/dark reflow, 48px controls, keyboard selection, focus after continuation, English/German and a 200% CSS-zoom simulation. [Detailed evidence](docs/operations/foundation-verification.md).

## Remaining limitations

The historical Kotlin script/UAST lint crash is resolved locally. Sixteen lint warnings and Gradle 10 deprecations remain. No device/emulator was attached; native real HTTP/device/TalkBack behavior and release assembly remain unverified. Deployment source identity and public database-row export have evidence, but live API/environment parity, full PostgreSQL/auth/storage restore, off-machine recovery, published Android version and release signing continuity remain open. The five content drafts are not independently approved or published. Authoritative sample grading, retained-evidence reduction and transactional state/schedule/sync persistence are implemented locally; production identity/network database, production selection/content acceptance, durable sync and the complete 2.0 learner journey remain open.

Hosted verification after the repair:

- [Web run 37118868488](https://github.com/abulhawa/german-master-2/actions/runs/37118868488) passed at `b7d3f6a`: dependency install, type check, 76 files / 280 tests and build. The subsequent application change only affects Android SDK workflow setup.
- [Android run 37118948356](https://github.com/abulhawa/german-master-2/actions/runs/37118948356) passed at `c584e0f`: SDK setup/install, unit tests, debug assembly and blocking lint; overall job conclusion was success.
- [Repository safety run 37118948332](https://github.com/abulhawa/german-master-2/actions/runs/37118948332) passed at `c584e0f`.
- Initial post-repair Android run `37118851725` exposed the removed SDK `tools` package in `setup-android@v3`; it was superseded/cancelled after upgrading the setup action and explicitly requesting `platform-tools`. Earlier run `37116424351` records the historical lint crash. These failed/superseded runs are not passing evidence.

Implementation commits `b7d3f6a` (lint/toolchain repair) and `c584e0f` (hosted SDK setup) are pushed to `main`; the final evidence/checkpoint update is committed and pushed at handoff. No product milestone beyond this baseline repair is claimed.

Foundation implementation `578959d` is committed and pushed to `main`. All three hosted workflows passed at that exact commit:

- [Web checks 37125291293](https://github.com/abulhawa/german-master-2/actions/runs/37125291293): install, type/generation checks, unit/integration tests and build.
- [Android checks 37125291209](https://github.com/abulhawa/german-master-2/actions/runs/37125291209): generated contract/token guards, unit tests, debug assembly and blocking lint.
- [Repository safety 37125291112](https://github.com/abulhawa/german-master-2/actions/runs/37125291112): full-history secret scan.

The hosted-results checkpoint update is documentation-only and accompanies this handoff. The product foundation is implemented; M0/M1 exit gates and the authoritative backend remain open.

## Milestone status

| Milestone | Status | Remaining gate |
|---|---|---|
| M0 Preserve and baseline | In progress | Live deployment/API settings, full PostgreSQL/auth/storage recovery, off-machine backup and store/signing records |
| M1 Contracts design and content | In progress | Foundation demonstration implemented; independent review, 30 targets, target-centered database model, complete contract/design acceptance remain |
| M2 Authoritative engine | In progress | Grading, idempotent attempts/exposures and transactional evidence/state/schedules/sync implemented locally; local small-catalog selection implemented; production adapters and broader selection acceptance remain |
| M3 Web vertical slice | Not started | Complete 2.0 learner journey |
| M4 Android parity | Not started | Native 2.0 journey using product API |
| M5 Offline and operations | Not started | Verified durable outboxes, cross-device reconciliation, privacy and release recovery |
| M6 Pilot and readiness | Not started | Reviewed content, usability and delayed-retention follow-up |

The monorepo bootstrap, shared foundation and backend session demonstration are implemented. M0/M1 remain open and M2 is in progress. Legacy backend/schema/tooling remain inside `apps/web`; `contracts`, pure `packages/learning-engine`, isolated `services/api` and the first used target-centered schema under `db` now exist. The local PGlite PostgreSQL adapter and fixture authentication do not establish a production backend.

## Authoritative backend session slice

- Pure deterministic grading for all five forms, explicit alternatives, versioned NFC/outer-space normalization, answer linkage and assistance recording; no client mastery policy.
- Local API creates owned solution-free sessions, enforces capabilities and pins immutable revisions. Session/attempt replay returns original data, changed submissions conflict, foreign questions disclose no evaluation, and first submission is transactionally enforced. Completion requires an accepted attempt for every question.
- Root PostgreSQL schema links topics/skills/targets/revisions/releases to owned sessions/questions/attempts/evaluations, with composite ownership constraints and append-only guards. PGlite 0.5.8 supports offline PostgreSQL tests, including rollback/concurrent submission/restart recovery. No production migration ran.
- Web `/foundation?backend=1` and Android debug activity with `--ez backend true` submit and display server feedback. Static previews remain available. Pending payloads are frozen for same-ID retries within the preview; process-death durability remains unimplemented.
- Local verification passed: root install/check/build; web 78 files / 301 tests, backend 2 files / 19 tests; Android 26 suites / 97 tests, debug assembly, lint zero errors / 16 warnings. Full backend session completed over HTTP in tests, through the built launcher and in the browser. Native parity uses mocked server evaluations in Robolectric, not a device. [Evidence and limitations](docs/operations/backend-session-verification.md), [local setup](services/api/README.md), [decision](docs/adr/003_local_authoritative_session.md).
- Implementation `4877d22` is committed and pushed to `main`. All three hosted workflows passed at that exact commit: [Web checks 37132271389](https://github.com/abulhawa/german-master-2/actions/runs/37132271389) (install, generation/type checks, 19 backend tests, 301 web tests and build), [Android checks 37132271319](https://github.com/abulhawa/german-master-2/actions/runs/37132271319) (generated guards, unit tests, debug assembly and blocking lint), and [Repository safety 37132271371](https://github.com/abulhawa/german-master-2/actions/runs/37132271371). Staged scan and local full-history Gitleaks scan passed with zero findings (771 commits at implementation head). No AI inference, production cutover, content publication or store release occurred.

## Exact next implementation slice

1. Inspect checkout/recent commits and hosted owned-read results. Preserve newer work; grading, evidence policy, transactional persistence, local mixed selection, confirmed reads, previews, Android repair and mockups are implemented.
2. Owned target/sync transport is implemented locally with generated contracts, frozen snapshot pages/watermarks, owned cursors, restart recovery and UTC due refresh. Preserve this work; pending client reconciliation is still open.
3. Implement the isolated 2.0 web Home → Practice → confirmed summary/Progress journey using `/v2/targets`, `/v2/sync` and server-selected sessions. Offer an honest shorter session for this five-target catalog instead of inventing questions to reach fifteen; add only the contract/catalog metadata needed for that flow. Level/topic filtering, seeded variant selection and broader diversity require reviewed catalog/contract expansion. Production auth/network PostgreSQL adapters, least-privilege roles and multi-connection contention must be verified before staging; no production reset/cutover is authorized. Independent German-language review, 30-target expansion and complete M1 acceptance remain required before publication/pilot.
4. Resume M0 dependent checks when full export/restore tooling, private backup destination and Play/signing records are available. Use the specific blockers in `docs/operations/m0-inventory.md`; do not repeat successful row/source recovery or claim it satisfies full restore.

The next demonstration is the isolated complete web learner journey. M0/M1 remain open and M2 remains in progress. Ignored private recovery artifacts remain local.

## Pure evidence and scheduling policy slice

- Implemented `retained-evidence-v1` in `packages/learning-engine/src/evidence.ts`, with injected clock, saved event timezones, immutable accepted evidence and deterministic server receipt-sequence replay. Includes five learner states, spaced/local-date gates, editorial transfer/context diversity, independent failure and recovery, retained mastery transitions/lapse cycles, and the 1/3/7/14/30-day ladder.
- Assisted practice can request an earlier review while leaving retention eligibility fixed. Skip/exposure and reinforcement cannot advance mastery. Implausible answer timing is excluded from retention successes; late success cannot repair a later failure. Existing single-variant catalog targets remain below Mastered.
- At this historical handoff it was a pure server-engine slice only; the transactional-evidence slice below now invokes it and persists projections/schedules/sync changes with a public skip/exposure ingestion endpoint. M2 remains in progress. [Policy decision](docs/adr/004_retained_evidence_policy.md), [verification and reproduction evidence](docs/operations/evidence-policy-verification.md).
- Local root install/check/build passed; backend 3 files / 37 tests, web 78 files / 301 tests passed. Two install EPERM failures were resolved by stopping stale project preview binary holders. Android/browser/device checks were not rerun because client code/contracts did not change. No live inference, production mutation, content publication or store release occurred.
- Implementation `a520eab` is committed and pushed to `main`. Both applicable hosted workflows passed at that exact commit: [Web checks 37160726354](https://github.com/abulhawa/german-master-2/actions/runs/37160726354) (install, generated/type checks, 37 backend tests, 301 web tests and build) and [Repository safety 37160726305](https://github.com/abulhawa/german-master-2/actions/runs/37160726305). Android CI was not triggered by this server-only change. Local staged and full-history Gitleaks scans passed with zero findings (773 commits at implementation head). This final hosted-results checkpoint is documentation-only and committed/pushed at handoff.


## Transactional evidence and scheduling slice

- Accepted answers now atomically commit source/evaluation, immutable accepted evidence, `retained-evidence-v1` target projection, review schedule, ordered sync change and session completion. Learner row locking serializes ingestion. Replays return original acknowledgments without another event/projection/sync change.
- Added migration 002 with saved issuance, immutable pinned editorial identities and question assessment/reinforcement roles. Events save the profile timezone at ingestion. Grammar maps to the concept diversity gate. Historical local fixtures upgrade conservatively without granting retrospective spaced-success credit; initialization/backfill is replay-safe. Targets/exercise linkage/session issuance are frozen.
- Added generated TypeScript/Kotlin exposure contracts, shared conformance fixtures and `POST /v2/exposures:batch`. Exposure is nonterminal; skip finishes a question without grading. Both record exposure only, preserve event idempotency and compete with answers for first terminal submission. Skip UI and public sync retrieval remain open.
- New PostgreSQL tests cover exact projection replay, saved timezone/issuance/identities, duplicate acknowledgment, late final-write rollback, competing same-target sessions, assisted retention gates, reinforcement, single-variant mastery limits, skip/answer race, ownership, strict HTTP transport, old-schema upgrade and close/reopen recovery. [Decision](docs/adr/005_transactional_evidence.md), [verification](docs/operations/transactional-evidence-verification.md).
- Local root install/check/build passed; backend 4 files / 51 tests and web 78 files / 301 tests passed. Android offline 26 suites / 98 tests, debug assembly and lint passed (zero errors / 16 warnings). Built loopback launcher accepted an answer and replay-safe skip. Implementation `b5650c9` is committed and pushed to `main`. All three hosted workflows passed at that exact commit: [Web checks 37166238284](https://github.com/abulhawa/german-master-2/actions/runs/37166238284), [Android checks 37166238314](https://github.com/abulhawa/german-master-2/actions/runs/37166238314) and [Repository safety 37166238283](https://github.com/abulhawa/german-master-2/actions/runs/37166238283). Staged and full-history Gitleaks scans passed with zero findings (775 commits at implementation head). M0/M1 gates remain open and M2 remains in progress. No production mutation, content publication, store release or AI/Groq inference occurred.

## Server-selected session slice

- Implemented server-only `mixed-selection-v1` with weak/due/new quotas, bounded overdue urgency, stable identity ties, manageable opening and interleaving. Missing pools are reallocated; at most one question per distinct target is used in this five-draft catalog. Non-due extra practice is pinned reinforcement, so it cannot inflate retained mastery.
- Session creation now reads owned persisted states and UTC schedules under the learner lock, filters capabilities and retired targets, pins database revisions/editorial identities and saves selection version/roles. Eligibility refreshes from injected time without changing saved projections. Replays preserve stored allocation, roles and release after history/clock changes; concurrent duplicate requests return one session.
- Contrasting accepted histories demonstrate new selection before deadline, due weakness at deadline and due/weak/new mixed ordering. Pure synthetic tests exercise full 8/5/2 quotas and bounded backlog behavior. Filesystem restart also replays session creation. [Decision](docs/adr/006_server_selected_sessions.md), [verification](docs/operations/server-selection-verification.md).
- Local root install/check/test/build passed: backend six files / 57 tests; web 78 files / 301 tests. Final backend check/test/build repeated after release replay correction. No client/contracts changed, so Android/browser/device checks were not repeated. Existing dependency audit warnings remain. M2 stays in progress; M0/M1 gates remain open. No production mutation, deployment, content publication, store release or AI/Groq inference occurred.
- Implementation `7f4a2ec` is committed and pushed to `main`. Both applicable hosted workflows passed at that exact commit: [Web checks 37191977392](https://github.com/abulhawa/german-master-2/actions/runs/37191977392) (install, generation/type checks, backend/web tests and builds) and [Repository safety 37191977396](https://github.com/abulhawa/german-master-2/actions/runs/37191977396). Android CI was not triggered by this server-only change. Staged and full-history Gitleaks scans passed with zero findings (777 commits at implementation head). This hosted-results checkpoint is documentation-only and committed/pushed at handoff. Next action: owned target/sync transport as described above.

## Owned confirmed target/sync transport slice

- Generated v2 TypeScript/Zod and Kotlin confirmed summaries, frozen target pages and sync upserts. Added authenticated `GET /v2/targets` and `GET /v2/sync` with strict bounded query transport and no-store caching. Summaries omit raw answers, learner identity, internal evidence history and client pending work.
- Migration 003 stores immutable owner-scoped opaque cursors and frozen target pages. Snapshots share captured UTC time and sync watermark; accepted writes during pagination remain retrievable. Sync preserves ordered ingestion-time payloads and reusable polling positions across close/reopen. Fresh snapshots compute due at UTC deadlines without mutating projections or sync changes. Transport timestamps now preserve canonical millisecond precision in both languages.
- Local root install/check/test/build passed: backend seven files / 61 tests, web 78 files / 301 tests. Final backend type check and four read tests passed after HTTP/concurrency assertions. Android offline 26 suites / 99 tests, debug assembly and lint passed (zero errors / 16 existing warnings). Built loopback launcher smoke passed for accepted answer, owned delta and frozen snapshot continuation. [Decision](docs/adr/007_owned_target_sync_transport.md), [verification](docs/operations/owned-reads-verification.md).
- Cursor retention/expiry/reset, tombstones, production authentication/network PostgreSQL, multi-connection contention and durable client reconciliation remain open. No learner UI/device/browser journey acceptance is claimed. M2 remains in progress; M0/M1 gates remain open. No production mutation, deployment, content publication, store release or AI/Groq inference occurred.
- Implementation is awaiting final staged scan/commit/push and hosted verification. Exact next action: isolated web learner flow with honest shorter-session handling, as described above.

## Local toolchain and checks

- Web verified with Node 22.23.3 and npm 10.9.9. Portable local installation currently under ignored `.local/tools/node-v22.23.3-win-x64`; availability is machine-specific.
- Run root `npm ci`, `npm run check`, `npm test`, and relevant `npm run build`. Use mocks/fixtures, no production credentials. Leave `DATABASE_URL` unset for the inherited test fixture default; an empty string prevents some suites from loading.
- Android now verified with Gradle 9.8.0, AGP 9.4.1, Java 21, compile/target SDK 37, and the checked-in version catalog. This machine has Java under `C:/Users/ali_a/.gradle/jdks/eclipse_adoptium-21-amd64-windows.2` and SDK under `C:/Users/ali_a/AppData/Local/Android/Sdk`.
- Android SDK package naming currently uses `platforms;android-37.0`; CI also installs build tools 37.0.0 and the declared NDK. Discover actual available requirements when resolving the lint issue.
- From `apps/android`, use the wrapper for relevant tests, `lintDebug` and `assembleDebug`. Reports/build outputs are local and ignored.

## Update this checkpoint at handoff

Record the current slice and state, affected files, checks with actual results, remaining blockers, decisions changed and the next concrete implementation action. Mark completed work once. Link detailed evidence instead of pasting logs. Commit and push checkpoint updates with the authorized repository changes; if any work remains uncommitted or unpushed, state that explicitly. Never put credentials or private account details in this public file.

# German Master 2.0 renovation checkpoint

Last updated: 3 October 2026. This is the current handoff; update it after every implementation slice. Git history and actual working files take precedence over stale checkpoint claims.

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
- This foundation continuation started at `9b78812` with a clean checkout and matching remote. The Android repair/mockup slice is complete; this slice implements the first shared exercise foundation.
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

The historical Kotlin script/UAST lint crash is resolved locally. Sixteen lint warnings and Gradle 10 deprecations remain. No device/emulator was attached; device/TalkBack behavior and release assembly remain unverified. Deployment source identity and public database-row export have evidence, but live API/environment parity, full PostgreSQL/auth/storage restore, off-machine recovery, published Android version and release signing continuity remain open. The five content drafts are not independently approved or published. The authoritative backend and complete 2.0 learner journey remain unimplemented.

Hosted verification after the repair:

- [Web run 37118868488](https://github.com/abulhawa/german-master-2/actions/runs/37118868488) passed at `b7d3f6a`: dependency install, type check, 76 files / 280 tests and build. The subsequent application change only affects Android SDK workflow setup.
- [Android run 37118948356](https://github.com/abulhawa/german-master-2/actions/runs/37118948356) passed at `c584e0f`: SDK setup/install, unit tests, debug assembly and blocking lint; overall job conclusion was success.
- [Repository safety run 37118948332](https://github.com/abulhawa/german-master-2/actions/runs/37118948332) passed at `c584e0f`.
- Initial post-repair Android run `37118851725` exposed the removed SDK `tools` package in `setup-android@v3`; it was superseded/cancelled after upgrading the setup action and explicitly requesting `platform-tools`. Earlier run `37116424351` records the historical lint crash. These failed/superseded runs are not passing evidence.

Implementation commits `b7d3f6a` (lint/toolchain repair) and `c584e0f` (hosted SDK setup) are pushed to `main`; the final evidence/checkpoint update is committed and pushed at handoff. No product milestone beyond this baseline repair is claimed.

## Milestone status

| Milestone | Status | Remaining gate |
|---|---|---|
| M0 Preserve and baseline | In progress | Live deployment/API settings, full PostgreSQL/auth/storage recovery, off-machine backup and store/signing records |
| M1 Contracts design and content | In progress | Foundation demonstration implemented; independent review, 30 targets, target-centered database model, complete contract/design acceptance remain |
| M2 Authoritative engine | Not started | Grading, evidence reduction, scheduling, selection and idempotent attempts |
| M3 Web vertical slice | Not started | Complete 2.0 learner journey |
| M4 Android parity | Not started | Native 2.0 journey using product API |
| M5 Offline and operations | Not started | Verified durable outboxes, cross-device reconciliation, privacy and release recovery |
| M6 Pilot and readiness | Not started | Reviewed content, usability and delayed-retention follow-up |

The monorepo bootstrap and first cross-client foundation demonstration are implemented. M0 and M1 remain open. Legacy backend/schema/tooling remain inside `apps/web`; `contracts` now exists, but the proposed learning-engine package, separate API service and reset database have not yet been implemented.

## Exact next implementation slice

1. Inspect checkout/recent commits and hosted foundation results. Preserve newer work; do not restart the completed Android repair, mockups or contract-preview implementation.
2. Implement authoritative grading of this pinned five-question sample session in a pure `packages/learning-engine` and isolated `services/api` boundary, using shared reviewed-draft fixtures and versioned normalization. Cover answer linkage, alternatives, assistance, ownership and attempt replay/conflict behavior with local fixtures. Clients submit answers and display server evaluations; they must not confirm mastery independently.
3. Add the target-centered reset database model locally, then exercise one complete backend-graded session before broadening the learner journey. No production reset/cutover is authorized. Independent German-language content review, 30-target expansion and complete M1 acceptance remain required before publication/pilot.
4. Resume M0 dependent checks when full export/restore tooling, private backup destination and Play/signing records are available. Use the specific blockers in `docs/operations/m0-inventory.md`; do not repeat successful row/source recovery or claim it satisfies full restore.

The next demonstration is one complete session graded by the authoritative backend, then the complete learner journey. The first M1 foundation slice is implemented and locally verified; full milestone exit gates remain in blueprint Sections 23–26. This checkpoint will be committed/pushed with the foundation slice; hosted outcomes at its new commit must be checked separately.

## Local toolchain and checks

- Web verified with Node 22.23.3 and npm 10.9.9. Portable local installation currently under ignored `.local/tools/node-v22.23.3-win-x64`; availability is machine-specific.
- Run root `npm ci`, `npm run check`, `npm test`, and relevant `npm run build`. Use mocks/fixtures, no production credentials. Leave `DATABASE_URL` unset for the inherited test fixture default; an empty string prevents some suites from loading.
- Android now verified with Gradle 9.8.0, AGP 9.4.1, Java 21, compile/target SDK 37, and the checked-in version catalog. This machine has Java under `C:/Users/ali_a/.gradle/jdks/eclipse_adoptium-21-amd64-windows.2` and SDK under `C:/Users/ali_a/AppData/Local/Android/Sdk`.
- Android SDK package naming currently uses `platforms;android-37.0`; CI also installs build tools 37.0.0 and the declared NDK. Discover actual available requirements when resolving the lint issue.
- From `apps/android`, use the wrapper for relevant tests, `lintDebug` and `assembleDebug`. Reports/build outputs are local and ignored.

## Update this checkpoint at handoff

Record the current slice and state, affected files, checks with actual results, remaining blockers, decisions changed and the next concrete implementation action. Mark completed work once. Link detailed evidence instead of pasting logs. Commit and push checkpoint updates with the authorized repository changes; if any work remains uncommitted or unpushed, state that explicitly. Never put credentials or private account details in this public file.

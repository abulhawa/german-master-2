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
- Latest implementation baseline before this handoff: `9c1b0ab`, public visibility documentation. Later continuation-file commits do not change application behavior.
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

## Known blocker

Android `lintAnalyzeDebug` crashes inside Kotlin script/UAST analysis: `findFirCompiledSymbol only works on compiled declarations, but the given declaration is not compiled`. Application source was not changed and lint was not disabled. Full details are in `docs/operations/bootstrap-verification.md`. Treat it as an unresolved toolchain diagnostic, not a passing check or a proved application defect.

## Milestone status

| Milestone | Status | Remaining gate |
|---|---|---|
| M0 Preserve and baseline | In progress | Triage lint; inspect actual hosted CI; establish deployment/store identity and content/database preservation evidence where access permits |
| M1 Contracts design and content | Not started | Working exercise/API contracts, cross-language fixtures, target model, tokens and initial reviewed content |
| M2 Authoritative engine | Not started | Grading, evidence reduction, scheduling, selection and idempotent attempts |
| M3 Web vertical slice | Not started | Complete 2.0 learner journey |
| M4 Android parity | Not started | Native 2.0 journey using product API |
| M5 Offline and operations | Not started | Verified durable outboxes, cross-device reconciliation, privacy and release recovery |
| M6 Pilot and readiness | Not started | Reviewed content, usability and delayed-retention follow-up |

The monorepo bootstrap is complete, but M0 as a whole is not. Existing backend, schema, content tooling and shared TypeScript code remain inside `apps/web` intentionally. Do not claim the proposed engine/API/shared packages exist.

## Exact next implementation slice

1. Check working tree, recent commits and hosted workflow results. Preserve any newer or unrelated work.
2. Diagnose and repair Android lint with the smallest supported toolchain/configuration correction. Consult current official Android/Kotlin documentation; reproduce against the preserved source if needed. Keep lint enabled and do not upgrade the whole app indiscriminately.
3. Verify affected Android tests/lint/debug assembly and record reproducible results. If an external toolchain blocker remains after meaningful diagnosis, document it and continue independent M0 work rather than stalling the entire renovation.
4. Complete accessible M0 inventory/preservation evidence and identify genuine access blockers. Never run an old database-reset script to establish a baseline.
5. Start M1 with the first working typed exercise/API contracts and shared grading fixtures. Introduce actual packages with their implementations; do not add empty folders to simulate progress.

This slice should produce code/configuration and verified evidence, not another roadmap document. If step 2 is already fixed in newer commits, skip to the next unfinished step. The broader backlog and milestone exit gates are in blueprint Sections 23–26.

## Local toolchain and checks

- Web verified with Node 22.23.3 and npm 10.9.9. Portable local installation currently under ignored `.local/tools/node-v22.23.3-win-x64`; availability is machine-specific.
- Run root `npm ci`, `npm run check`, `npm test`, and relevant `npm run build`. Use mocks/fixtures, no production credentials. Leave `DATABASE_URL` unset for the inherited test fixture default; an empty string prevents some suites from loading.
- Android verified with Gradle 9.4.1, Java 21, compile/target SDK 37, and the checked-in version catalog. This machine has Java under `C:/Users/ali_a/.gradle/jdks/eclipse_adoptium-21-amd64-windows.2` and SDK under `C:/Users/ali_a/AppData/Local/Android/Sdk`.
- Android SDK package naming currently uses `platforms;android-37.0`; CI also installs build tools 37.0.0 and the declared NDK. Discover actual available requirements when resolving the lint issue.
- From `apps/android`, use the wrapper for relevant tests, `lintDebug` and `assembleDebug`. Reports/build outputs are local and ignored.

## Update this checkpoint at handoff

Record the current slice and state, affected files, checks with actual results, remaining blockers, decisions changed and the next concrete implementation action. Mark completed work once. Link detailed evidence instead of pasting logs. Commit and push checkpoint updates with the authorized repository changes; if any work remains uncommitted or unpushed, state that explicitly. Never put credentials or private account details in this public file.

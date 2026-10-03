# Authoritative foundation session verification

Date: 3 October 2026. The slice began at clean `05e912f` on `main`; remote main matched. Hosted foundation checks at `578959d` were rechecked through GitHub API and all three remained successful. This evidence covers the next local implementation, not a production deployment.

## Implemented boundary

The pure learning engine grades all five typed forms using pinned server-only draft rubrics. `de-nfc-trim-v1` preserves case, umlauts, punctuation and internal spaces; only NFC and surrounding whitespace are normalized. `ins` and `in das` are explicit reviewed-draft alternatives. Slots are matched by identity with complete unique coverage; choice/token IDs must belong to the pinned exercise. Malformed linkage is rejected separately from incorrect content. Hint/reveal events produce `assisted: true` without changing grammatical correctness. No mastery or scheduling projection is implemented.

The isolated API derives subjects from an injected authenticator and returns solution-free owned sessions. Public fixture authentication is limited to the loopback launcher and development/debug clients. Session request and attempt IDs are replay-safe; changed payloads conflict. Valid batch items commit independently. Missing and foreign questions use the same rejection without evaluations. First accepted submission owns the question, and received sequences order evidence. Transactional insertion covers device, attempt, evaluation and full-session completion. PostgreSQL constraints and append-only triggers protect linkage/revisions/evidence.

The root database is the used target-centered subset in `db/migrations/001_target_foundation.sql`. PGlite 0.5.8 runs PostgreSQL locally in WASM. Tests use an ephemeral instance plus a temporary filesystem instance to verify acknowledgment recovery after close/reopen. The API launcher remains in-memory and resets on restart. See ADR 003 and `db/README.md` for the intentionally unimplemented production database/identity/role boundary.

## Local checks

Node 22.23.3 / npm 10.9.9:

- Root `npm ci` passed after stopping the pre-existing Vite development server that held the Windows LightningCSS binary open. The first install failed with EPERM; that failure is not passing evidence.
- Root `npm run check` passed: contract/token generation guards, inherited web TypeScript and new engine/API TypeScript.
- Root `npm test` passed: backend 17 tests initially, web 78 files / 301 tests. After adding two additional grading/linkage assertions, `npm run test:backend` passed 2 files / 19 tests. After final feedback-label/refactoring changes, the four affected web preview tests passed again. No unrelated full-suite rerun was required.
- Root `npm run build` passed after final edits: web/PWA, legacy API/server, and isolated API launcher. The isolated build bundles internal workspace TypeScript and leaves only Zod/PGlite external; its repository-relative migration path is preserved.
- Executed the built launcher on loopback port 5002 under Node, then submitted a fresh five-question session over HTTP: five correct server evaluations, followed by five duplicate acknowledgments preserving the originals.
- Backend tests cover five answer forms, valid wrong answers, normalization boundaries, explicit alternatives, slot identity/duplicates, unknown choices/tokens, assistance changes, solution omission, capability shortage, session replay/conflict, attempt replay/conflict, foreign ownership, revision mismatch, client-grade rejection, invalid JSON, batch/body limits, simultaneous first submissions, transaction rollback and restart recovery. A deliberately failing evaluation insert rolled back both answer and newly registered device; a later retry succeeded.

Android (Java 21 / Gradle 9.8.0 / AGP 9.4.1), offline:

- `testDebugUnitTest lintDebug assembleDebug` passed after final native edits.
- 26 unit suites / 97 tests, zero failures/errors/skips. Two new Robolectric Compose tests submit every form against an injected fake server, complete a session, preserve hint assistance and retry the same attempt after a simulated timeout. Shared contract tests decode the required `Evaluation.assisted` field.
- Debug APK assembled; lint zero errors / 16 existing warnings. The first run found the missing `includeSubdomains` attribute in the new debug network-security file; adding `includeSubdomains="false"` fixed it. No checks were disabled.
- `adb devices` listed no attached device. Native real HTTP transport, TalkBack and process-death behavior are not device-verified. Native UI tests use mocked acknowledgments, not the live local API.

## Browser and UI review

Used agent-browser 0.27.0 against the local Vite/API servers:

- Loaded meaningful content without a Vite overlay; browser error inspection returned no errors.
- Exercised correction for lowercase `berufe` versus accepted `Berufe`, positive `dem`, explicit alternative `in das`, ordered word tokens and two-slot completion, reaching the five-answer summary.
- Inspected desktop dark and 320px German light feedback screenshots. Narrow view reported document width 305px at a 320px viewport, with no horizontal overflow. Buttons/inputs keep the foundation 48px minimum. Content and feedback wrap vertically and remain scrollable.
- Web tests verify focus at the next question, typed input reset, inability to modify a pending answer, retries with identical raw payload, permanent rejection blocking, acknowledgment-ID validation, hint assistance and complete session summary. No local comparison decides the displayed outcome.
- Reused scoped semantic tokens and foundation card/input/action primitives; all new interface copy has English/German variants. Prompt/answer text remains German. This preserves the established intentional departure from legacy Radix/class-based theme guidance for the isolated foundation preview; no full learner navigation acceptance is claimed. Applied React review checklist for lazy preview loading, stable API/request identity, derived rendering and accessible controls/status/focus.

## Remaining limits and next slice

This is a backend-graded draft-content demonstration. Production auth/network PostgreSQL, least-privilege server grants, multi-connection contention, full reset model/content acceptance, mastery/evidence reduction, scheduling, adaptive selection, skip/exposure commands, partial completion, durable outboxes, sync/privacy and the full learner journey remain open. Independent German-language approval and the 30-target M1 gate remain required. M0 recovery/store/signing blockers remain in `m0-inventory.md`.

Zero Groq/API inference calls; zero cached inference results used. No production database, deployment, store release or content publication was changed.

Hosted results for this slice will be recorded in `PROGRESS.md` after the implementation push; earlier hosted foundation success is not evidence for this commit.

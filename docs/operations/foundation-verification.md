# First shared foundation verification

Verified locally on 3 October 2026. The scope is a cross-client rendering/contract demonstration, not the complete 2.0 product or an authoritative grading service.

## Implemented

- A real `@german-master/contracts` npm workspace: JSON Schema exercise/answer/session/API models, draft OpenAPI, checked-in generator v1 and generated TypeScript/Zod/Kotlin types. Generated structural checks prevent Kotlin serializer coercion. Both languages use one acceptance/rejection corpus and answer/acknowledgment fixtures.
- Five supported exercise forms: typed short answer, choice, labelled cloze, word ordering and multi-slot completion. Web imports the shared session; Gradle copies only `session.json` into application assets. Editorial solutions and test fixtures stay outside application assets.
- Semantic design tokens with generated CSS/Kotlin bindings and contrast checks, shared minimum control/column dimensions, reusable components, system/light/dark web modes and native system theme. English/German UI resources are available.
- Independent development/debug preview entry points. Inputs prepare typed answer payloads, hints expand, continuation resets input state, and answer inspection explicitly says backend grading is next. No grade, mastery, persistence or sync is claimed.
- Five original agent-reviewed content drafts with stable target/skill/topic linkage and provenance. Web tests check linkage and accepted-answer/control compatibility. Independent German-language approval is pending.

## Passing checks

- Root `npm ci`, `npm run check` (including generation drift/contrast guards), and `npm run build`: pass. The build includes web/PWA, inherited API and server bundles; it does not establish the new backend.
- Web: 77 files / 297 tests pass with two workers. The initial unconstrained run had five legacy test timeouts while Android was also building. A complete bounded rerun passed; the config now limits workers to two without raising timeouts or suppressing checks.
- Offline Android `testDebugUnitTest assembleDebug lintDebug`: 25 suites / 95 tests, zero failures/errors/skips; debug assembly passes; lint zero errors / 16 inherited warnings. Gradle 10 deprecations remain. Added German resources exposed six inherited translation gaps, which were filled rather than suppressing lint.
- Kotlin tests decode shared acceptance/rejection samples, all five answers and accepted/duplicate/rejected acknowledgments, and round-trip the transport data. A Robolectric Compose test navigates through all five shared prompts. A separate Roborazzi simulated render passes; local image is under `apps/android/app/build/outputs/foundation/preview.png`.
- Web browser: actual page loads, no recorded browser errors/overlay, short answer inspection, keyboard radio selection, input reset, prompt focus after continuation, locale change and light/dark rendering. At 320px there is no horizontal overflow; controls measure 48px or more. A 200% CSS-zoom simulation at 640px also has no horizontal overflow. This is a simulation, not full assistive-technology/browser-zoom acceptance.

The initial dark screenshot revealed inherited typography colors overriding the preview; scoped semantic colors corrected that issue and a new dark screenshot was inspected. Local images are ignored `.local/foundation-web-narrow.png` and `.local/foundation-web-dark.png`.

## Limits and next evidence

No device/emulator was attached. Robolectric is simulated native rendering, not runtime verification on a device; TalkBack, native focus transitions, font scaling and full accessibility acceptance remain open. The debug preview still runs within the inherited Android Application, which owns background workers; demonstrate on an isolated device without production configuration.

M0 deployment/database/store evidence and remaining blockers are in [m0-inventory.md](m0-inventory.md). No production deployment, reset, content publication, signing change or store upload occurred. Zero Groq or other live inference calls were made.

Both CI jobs now check generated foundation files, and content paths trigger affected checks. Hosted success for a new commit is separate from these local results. Next implement an authoritative backend-graded sample session, preserving immutable revisions and replay-safe attempts, before expanding the learner journey.

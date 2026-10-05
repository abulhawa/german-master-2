# Local offline practice verification

5 October 2026. Continuation began clean on `main` at `fa30875`. Hosted Web, Android and Repository safety workflows all passed at that exact commit. This corrects the previous checkpoint's outdated no-push/no-hosted-result wording; no new hosted result is claimed for this implementation.

Implemented in the development web and native fixture learners:

- Atomic web IndexedDB reserve download/retry, whole-pack validation on download/read, two-slot consumption and pinned started-session persistence.
- Provisional deterministic feedback for all five forms, saved drafts/assistance, full/partial completion and separate confirmed results.
- Ordered durable answer/Skip/completion queues. Receipt-save failure and response loss preserve frozen replay; rejected events remain saved and stop further delivery. Pack expiry does not delete started sessions or prevent late delivery.
- Native completed offline sessions remain in the AtomicFile archive while the second reserved session is used. Web started sessions likewise survive reserve replacement. Existing online practice, profile and content-report writes are preserved.

Web tests use fake IndexedDB for restart, transaction rollback, competing connections, integrity corruption, expiry, stale responses and receipt failures. Actual component plus HTTP/PGlite integration downloads a real pack, answers all five forms without HTTP across remount/database reopen, ends fully, advances server time beyond pack expiry and replays after an accepted-but-lost response with five unique graded evidence rows. This establishes offline API behavior within an already loaded shell, not airplane-mode cold launch.

Native authoritative JVM HTTP integration consumes both sessions without write calls during practice, restarts mid-session, retains assistance and partial drafts, preserves a pending profile, and syncs after expiry. Accepted-response loss and receipt-save failure retry identical attempts. Five graded attempts plus one Skip produce six unique evidence rows. AtomicFile roundtrip includes both completed pinned sessions. This is Robolectric/JVM evidence, not device/process-kill acceptance.

The shared `contracts/v2/examples/offline-grading.json` has 36 explicit expected cases. TypeScript and Kotlin compare full evaluations and malformed-answer errors. Synthetic normalization cases are test data, not reviewed or published catalog additions.

A temporary isolated browser harness avoided altering existing learner tab locks. The actual component downloaded/started practice; 320px reflow had no horizontal overflow, buttons measured about 49px, Enter submitted a typed answer and moved focus to provisional feedback, and browser console errors were empty. Screenshot: ignored `.local/offline-web-feedback.png`. The temporary harness was removed. This is a component visual/keyboard smoke, not complete viewport/WCAG/native TalkBack acceptance. The agent-browser CLI was unavailable, so the available CUA browser API was used.

Verification counts and final tool results are recorded in the newest `PROGRESS.md` checkpoint. Offline npm install used local Node 22.23.3/npm 10.9.9. No live AI/Groq calls, production mutations, deployments, content publication, destructive resets, signing changes or store uploads occurred. No attached Android device/emulator was available at final inventory.

Next required engineering: isolated v2 web offline shell/cold-launch verification, coordinated reconciliation spanning all pending write kinds, account partitions/expired auth/two-device checks and privacy/recovery. M0 preservation resources, independent content/design approval and an identified isolated staging/auth environment remain external gates.

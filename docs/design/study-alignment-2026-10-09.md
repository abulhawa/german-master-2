# Study workspace and low-typing practice alignment

9 October 2026. Implemented the owner's approved scope: Android Home follows the newer [web study workspace](study-workspace.md); web and Android practice follow the updated [S04 concepts](mockups/README.md). The October 3 Home board is historical, not the Home implementation target.

## Result

- Android Home leads with a blue mixed-session panel and one Start/Continue action, followed by actual catalog topics and up to three relevant next-focus targets. Available questions, confirmed evidence and stale/unknown snapshots drive its states; no sample counts or mastery claims are introduced.
- Home, Progress and Topics use a fixed native navigation bar. Account is in the header. Preferences, refresh/sync, downloads, export/deletion and sign-out retain their original handlers under Account. Pending-write recovery and expired-login actions remain visible where needed. Practice hides the main navigation.
- Both clients retain authored choice rows and the selected indicator while saving and after grading. Selection does not submit; Check remains explicit and submitted answers cannot be edited. Rounded controls, target labels, readable prompts, outcome text/icons, explanation panels and prominent Continue actions follow S04. Hint actions disappear after grading; previously revealed hints remain readable. Report and session recovery remain available.
- Confirmed and local feedback remain distinct. Android offline answers and web guest/downloaded practice show provisional wording; only server receipts confirm progress. Word ordering, matching, frozen retries, completion and saved drafts continue using existing repositories and contracts.
- New native copy has English/German resource keys. Shared semantic colors and 48px/dp minimum controls are preserved, including visible selection when disabled. The native timezone preference is single-line for keyboard traversal.

## Verification

Root offline installation, generated/type checks, web/API build and both learner builds pass. The full backend run passed 192 tests and exposed a pre-existing Windows CRLF comparison in the generated candidate test; normalizing only that test comparison fixes it, and its three-test file passes. Content and generated SQL are unchanged. The full web run passed 398 existing tests; the new deferred-response selection test had an incorrect fixture property, corrected to `policyVersion`. The final 50 affected UI tests pass. All 20 real HTTP client tests pass, including offline restart, provisional grading, response-loss replay and reconciliation; the five offline integration tests pass again after final localization cleanup.

Android's full 43-suite/167-test run passes. Subsequent targeted tests cover Account navigation, unavailable content, retained/locked selection, offline provisional feedback and next-question focus. Debug and isolated preview assemblies and blocking lint pass; lint retains 17 debug/18 preview warnings and zero errors. Final targeted results are recorded in the checkpoint; targeted reruns do not claim a second complete suite on every refinement.

The local B2 browser journey uses the real disposable candidate API through loopback forwarding: topic/target selection, Check, confirmed feedback, reload/resume, Continue, completion, Account and German dark-mode practice. Selected radio state remains visible and disabled; browser errors are empty. Desktop and 320px screenshots were inspected; enlarged 32px learner text at 320px has no horizontal overflow. The existing keyboard/reflow/semantic accessibility harness and offline-shell cold launch/late-sync/cache-isolation acceptance pass.

Pixel 10 Pro runtime inspection covers the blue Home, fixed navigation, Account/preferences, retained B2 selection, confirmed feedback, light/dark themes, English/German and font scale 2.0. Enlarged feedback wraps and Continue remains reachable by scrolling. Continue reaches the next German question with its prompt focused. Configuration recreation may return to Home; Continue practice restores the durable answer/feedback. No new TalkBack acceptance is claimed or requested. Native provisional-feedback coverage is a Robolectric/Compose check, not physical offline certification.

Screenshots and logs are ignored local evidence in `.local/study-alignment/` and `.local/study-alignment-*.log`. User-facing copies accompany the chat. Comparison uses actual authored candidate questions, not the illustrative dative exercise from the boards.

## Boundaries

The Pixel's original preview cache is preserved and restored; temporary acceptance data is retained separately. Original font scale, density, night mode and loopback routing are restored, and the original installed application is untouched. The updated isolated preview remains installed. The existing local API state is preserved by using a separate disposable acceptance host.

No API/schema/grading changes, content publication, production mutation, push, deployment or Play release. M1 remains active until final owner design/web accessibility acceptance and hosted verification are recorded. TalkBack remains accepted for now under the existing owner decision.

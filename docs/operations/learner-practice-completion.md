# Learner practice and authoritative native HTTP verification

Resumed on 4 October 2026: the paused device answer received authoritative Correct/Assisted feedback. The original session subsequently finished with 4 graded, 1 confirmed Skip and 4 correct; summary led to confirmed Progress with zero Mastered targets. Direct HTTP replay of the final saved multi-slot attempt returned duplicate, sequence 4. This closes the paused retry/summary smoke; it does not establish device word-order completion, report UI or accessibility acceptance. The pause narrative below is historical. Current evidence and checks: [content/reports continuation](content-review-and-reporting.md).

4 October 2026. Started from clean `2110efb`. The product reset remains incomplete; these changes affect the isolated learner previews.

## Native authoritative HTTP

`NativeAuthoritativeHttpTest` launches `services/api/scripts/native-recovery-harness.ts` as a disposable Node child process. It uses the actual routes, generated transport, learning engine and PGlite migrations. No production configuration, AI or credentials are loaded. Test-only clock/cleanup controls use stdin, not HTTP admin routes. Fresh snapshots are forced to one target per page before the real route parser runs.

Three scenarios persist a frozen answer, Skip or unanswered session request together with response-lost preferences. At cursor expiry, the first continuation request advances the clock to the page deadline and cleans the caches. HTTP failure preserves the complete AtomicFile record. Repository restart performs an explicit successful recovery of all five targets/cursor without uploading writes. Subsequent operation replay keeps one evidence event, the original session identity and one preferences revision.

A separate real-service journey sets a 15-question preference, starts the five available mixed questions, answers all five forms, restarts the repository midway, preserves assistance, reaches the correct summary and refreshes authoritative Progress. The five exposures do not become Mastered.

Reproduce using installed Java/SDK and Node 22:

```powershell
$env:JAVA_HOME='C:/Users/ali_a/.gradle/jdks/eclipse_adoptium-21-amd64-windows.2'
$env:ANDROID_HOME='C:/Users/ali_a/AppData/Local/Android/Sdk'
$env:GRADLE_USER_HOME='C:/Users/ali_a/.gradle'
$env:GM_TEST_NODE='C:/Users/ali_a/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/bin/node.exe'
apps/android/gradlew.bat -p apps/android :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --offline --console=plain
```

Run root dependency installation first. `GM_TEST_NODE` is an executable path, never a token; omit it if `node` is on PATH. Android CI now installs root dependencies and watches backend/schema/lockfile changes because the native integration depends on them.

## Learner behavior

Native mixed and topic requests use the lesser of preference and available question count; one-target focus remains one question. A persisted creation request is never resized on retry. Tests cover reduced/empty catalog availability and restart replay.

Word-order controls use immutable token IDs with localized direction and position labels. They retain the existing semantic colors and at least 48px native/web controls. The web preserves keyed DOM focus through a move and commits drafts before showing a new order; native controls use the durable repository. Boundaries are disabled, and submitting still requires the complete token set.

Web Enter ignores composition (including key code 229), repeats, blank/incomplete inputs and locked submissions. Feedback remains for explicit Continue. Native IME Done uses the same readiness guard; unit UI coverage exercises it. Both summaries list unique session targets, including exposures, without claiming retained gains. Native summary leads to confirmed Progress while retaining the saved summary.

The changes follow blueprint sections 8–9. They retain the shared foundation primitives and token styling. Legacy sidebar/analytics conventions are intentionally superseded by the existing isolated 2.0 learner shell; no release route replacement is claimed. The React review found no conditional hooks or new dependencies; small derived lists remain simple render-time values.

## Verification

- Offline root install (`npm ci --offline --ignore-scripts`), generated/type checks, root tests: backend 9 files/74 tests; web 80 files/340 tests; real web HTTP 1 file/6 cases, all passed.
- Root web/API/server build passed after the approved outside-sandbox rerun of the known Windows `uv_os_get_passwd` restriction. Production environment validation was not enabled.
- Offline Android debug: 35 suites/135 tests, zero failures/errors/skips; debug assembly and blocking lint passed, zero errors/16 existing warnings.
- Embedded browser shared foundation: English desktop keyboard move/right then left retained focus; German dark at 320px had no horizontal overflow and minimum observed order-control height 49.14px. No captured console errors. Screenshots were visually inspected. The actual learner route stayed at the ownership-waiting screen in the embedded browser, so full browser journey/lifecycle acceptance is not claimed. Chrome control was unavailable.

## Side-by-side device preview

The connected device contains an installed `com.germanverbmaster.android` version 29/0.2.08 with a signing identity different from the local debug key. Android safely rejected an in-place debug update; no uninstall or data clearing was performed. An installed version is not proof of the maximum published Play version or upload signing continuity.

The `learnerPreview` build installs as `com.germanverbmaster.android.preview`. Its manifest uses a plain Application, disables backup and removes the legacy launcher/OAuth handler so legacy workers and callbacks cannot run in the fixture app. The learner preview is its launcher and uses separate private storage. Release identity, version and signing configuration remain unchanged. The shared debug Kotlin directories are added through `AndroidSourceSet.kotlin`, as required by [built-in Kotlin configuration](https://developer.android.com/build/migrate-to-built-in-kotlin). CI assembles and lints both debug and learnerPreview variants.

Final preview assembly and blocking lint passed after fixing Kotlin source inclusion and the missing German app label. The initial source mapping produced a ClassNotFoundException on device, which was corrected before the successful launch. No custom-variant unit-test task exists under the current AGP default; the shared learner is covered by the debug unit suite.

Actual Pixel 10 Pro runtime verified setup, Home and starting five mixed questions with preference 15. A typed `Berufe` draft and hint assistance survived force-stop/cold restart and appeared again on resume. Removing only the test API tunnel froze a pending assisted answer; another force-stop and cold restart preserved its exact payload with no evaluation. The owner paused work while **Retry answer** was visible. Device replay/feedback and full session completion remain unfinished. TalkBack/font-scale acceptance is separate and was not performed.

The loopback API/web servers were left running at pause to preserve the API's in-memory owned session. Inspect them before resuming; do not restart the API or clear the preview data as routine cleanup. Complete the existing pending retry before starting unrelated device tests. Raw fixture cache/UI evidence stays in ignored `.local`, not the public repository.

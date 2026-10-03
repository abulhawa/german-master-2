# Android lint repair

Verified locally on 3 October 2026.

## Change

Updated AGP from 9.2.1 to 9.4.1 and the Gradle wrapper from 9.4.1 to 9.8.0. AGP 9.4.1 was verified against Google's Maven metadata; [AGP 9.4 compatibility](https://developer.android.com/build/releases/agp-9-4-0-release-notes) requires Gradle 9.6 or newer. [Gradle 9.8.0](https://docs.gradle.org/9.8.0/release-notes.html) is the current stable release. Java 21, SDK 37, NDK, Kotlin/Compose plugins and application dependencies remain as declared in the imported project.

The Kotlin script/UAST crash persisted on both AGP 9.3.2 / Gradle 9.5.1 and AGP 9.4.1 / Gradle 9.8.0. Moving the app's local-properties variable into the Android block did not fix it. Converting only the app build script also left the crash. Converting the root build, settings, app build and applied screenshot script to the supported Groovy DSL allowed lint to finish. No detector was disabled, baseline added, or script excluded. The version catalog remains TOML. Build settings, identity, signing configuration, dependencies and screenshot task behavior were preserved; local-properties input streams now close reliably.

The first completed lint report exposed one `NonObservableLocale` error in Analytics. Replaced its default-locale fallback with `LocalLocale.current.platformLocale`. Blocking lint errors and release lint checks are now enabled. This supersedes the inherited non-blocking lint policy.

## Verification

From the root, with Java 21 and the installed Android SDK available:

```powershell
./apps/android/gradlew.bat -p apps/android lintDebug testDebugUnitTest assembleDebug --offline --console=plain --continue --max-workers=4
```

Final combined run: **BUILD SUCCESSFUL**, 65 actionable tasks (19 executed, 46 up-to-date).

- Unit tests: 22 suites, 90 tests, zero failures/errors/skips.
- Debug lint: report generated, zero errors and 16 warnings. Warnings remain follow-up debt; this is not a warning-free result.
- Debug assembly: passed and produced the APK.
- No emulator/device behavior or release assembly was verified. Observable locale behavior was compiled and lint-checked, not manually exercised on a device.
- Checks used local fixtures, no production connection and no AI API calls. A configuration-only `help` invocation downloaded an uncached plugin dependency before the final offline runs.

One intermediate combined run failed in incremental `packageDebug` without a detailed cause. A standalone assembly rerun passed, and the final combined run also passed. No application packaging workaround was introduced; recurrence requires diagnosis with a stack trace.

The first hosted run after the repair exposed an independent SDK setup failure: `setup-android@v3` requested the removed `tools` package. Its logs confirmed `Failed to find package 'tools'` and sdkmanager exit code 1; the run was then superseded/cancelled by the workflow repair. Updated to `setup-android@v4` with explicit `platform-tools`, following the [action documentation](https://github.com/android-actions/setup-android). The replacement hosted run passed SDK setup and installed the declared SDK requirements. Final hosted outcomes are recorded in `PROGRESS.md`.

Logs and lint/test reports remain ignored local build artifacts. The original bootstrap report remains an accurate historical record of its earlier failed lint run.

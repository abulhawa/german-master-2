# Monorepo bootstrap verification

Verification date: 3 October 2026.

## Imported baselines

| Source | Original revision | Monorepo path |
|---|---|---|
| Web/backend | `f1ccc88113d6f636b080117b11af0e24c5fb9a87` | `apps/web` |
| Android | `3d09b26b1becf5cba6d3b06788e4b72c1623a265` | `apps/android` |

Git subtree imports retained complete source histories without squashing. Both imported trees matched their original source revisions before bootstrap configuration edits. The only intentional web import change is replacing its nested npm lockfile with the root workspace lockfile. Root dependency overrides preserve the original workspace resolution policy.

## Local verification

| Check | Result |
|---|---|
| Root dependency installation | Passed using `npm ci`; 722 packages installed |
| Web TypeScript check | Passed from the root workspace command |
| Web unit and integration suite | Passed: 76 test files and 280 tests |
| Web/API/server bundle | Passed in development configuration without production secrets |
| Full imported Git history secret scan | Passed: Gitleaks 8.30.1 reported zero findings; release archive checksum verified |
| Android debug assembly | Passed; `app-debug.apk` created |
| Android unit tests | Passed: 22 suites and 90 tests, zero failures/errors/skips |
| Android lint | Blocked by lint toolchain crash during Kotlin build-script analysis; see below |

Web verification used Node.js 22.23.3 and npm 10.9.9. Routine tests used in-memory mocks, with live Groq inference disabled and no production database connection. No AI API calls were made. Android verification uses the imported Gradle wrapper and a local Java 21 installation.

The first local web test attempt supplied an empty database variable, overriding the existing fixture default and preventing five suites from loading. Removing that empty variable restored the intended mocked setup. The subsequent complete suite passed; no application source change was needed.

## Android lint limitation

The combined `testDebugUnitTest lintDebug assembleDebug --continue` run completed unit tests and debug assembly, but returned a failing overall status because `lintAnalyzeDebug` crashed. Its diagnostic is `findFirCompiledSymbol only works on compiled declarations, but the given declaration is not compiled`, with Kotlin script/UAST and `LintDriver.checkBuildScripts` frames. The diagnostic identifies an unexpected lint/library failure rather than a reported application lint violation.

The Android application source and version catalog were not changed to hide or work around the crash. CI keeps lint enabled as a separate visible step. Investigate the AGP/lint/Kotlin combination and reproduce with the legacy baseline before selecting a supported toolchain fix. Successful unit tests and debug assembly do not imply lint or device behavior is verified.

## Limits and follow-up

This verifies the monorepo's imported development baseline, not German Master 2.0 product behavior. Runtime browser/device UX, production configuration, live database policies, store credentials, content rights and the new learning model still need the M0/M1 work in the blueprint. No database, deployment, signing configuration or old repository was modified.

GitHub workflows check the web workspace, Android debug tasks and history safety independently. Production release automation is intentionally not enabled. Hosted CI results are separate from the local checks above.

Local Git bundles for both complete legacy histories were created and verified under ignored `.local`. They are recovery copies and are not pushed as artifacts. The source baseline tags are published with the new repo.

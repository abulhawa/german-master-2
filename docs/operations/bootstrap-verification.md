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
| Android unit tests, lint and debug assembly | In progress at bootstrap; final result recorded below before handoff |

Web verification used Node.js 22.23.3 and npm 10.9.9. Routine tests used in-memory mocks, with live Groq inference disabled and no production database connection. No AI API calls were made. Android verification uses the imported Gradle wrapper and a local Java 21 installation.

The first local web test attempt supplied an empty database variable, overriding the existing fixture default and preventing five suites from loading. Removing that empty variable restored the intended mocked setup. The subsequent complete suite passed; no application source change was needed.

## Limits and follow-up

This verifies the monorepo's imported development baseline, not German Master 2.0 product behavior. Runtime browser/device UX, production configuration, live database policies, store credentials, content rights and the new learning model still need the M0/M1 work in the blueprint. No database, deployment, signing configuration or old repository was modified.

GitHub workflows check the web workspace, Android debug tasks and history safety independently. Production release automation is intentionally not enabled. Hosted CI results are separate from the local checks above.

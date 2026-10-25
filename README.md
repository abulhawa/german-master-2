# German Master 2.0

One monorepo for the React web learner, native Kotlin Android learner, versioned exercise contracts, authoritative TypeScript learning engine, PostgreSQL/Supabase backend, content and verification.

## Current status (25 October 2026)

The clean backend recovery and authorized production cutover were completed on
24 October. The earlier production 503 incident and preview-only cutover plan
below the historical checkpoints are not current next actions.

Web and native Android implement optional-sign-in practice, Home/Progress/Topics,
the approved mobile design, Settings/themes and the shared offline grammar
reference. The updated preview with Google/Microsoft buttons, icons and Home exits
was installed on the owner's Pixel on 25 October. Social-provider activation and
real login acceptance remain unfinished; UI presence is not working OAuth.

M2 remains the sole active milestone. Outstanding account export, isolation,
offline/native lifecycle and owner UX/release acceptance are recorded in
[PROGRESS.md](PROGRESS.md). Android publication remains on owner hold. Do not infer
a current live deployment revision from source or an installed preview.

## Current backlog and work order

Use the [25 October backlog reassessment](docs/operations/backlog-reassessment-2026-10-25.md)
for every legacy issue/PR and GM-001 through GM-024. Legacy Phase 0-4 plans and
imported verb-first roadmaps are historical, not the active work queue.

Finish the owner-requested provider setup and concrete release blockers, close
M2's remaining gate, then complete M3-M5 acceptance in order and the M6 pilot.
Do not reopen bootstrap, perform another reset, expand features or publish Android
from this work order. [Issue #20](https://github.com/abulhawa/german-master-2/issues/20)
remains open; its stale GitHub body needs the proposed rewrite in the reassessment.

Read [AGENTS.md](AGENTS.md), the latest checkpoint, relevant blueprint sections and
ADRs before behavior changes. GitHub Project-board drafts were not accessible in
this review; no GitHub issues or preserved repositories were modified.

## Repository layout

```text
apps/web/                 React/Vite web app and archived/transitional legacy modules
apps/android/             Native Kotlin/Compose Android app
services/api/             Authoritative versioned learner API and server
packages/learning-engine/ Shared TypeScript learning, grading and selection rules
contracts/                Versioned API and exercise contracts; generated native models
content/                  Draft, candidate, published and starter catalogs
db/                       Canonical clean v2 SQL baseline and seed material
docs/product/             Design and learning principles
docs/operations/          Recovery, setup, acceptance and deployment notes
.github/workflows/        Web, backend, Android and safety CI checks
```

## Development

Use **Node.js 24** and npm 10 or later from the repository root:

```powershell
npm ci
npm run check
npm test
npm run build:production
```

For Android open `apps/android` in Android Studio; its Gradle wrapper and version catalog are local to that directory. On Windows:

```powershell
cd apps/android
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

Run ordinary CI and local tests against fixtures/mocks. Never put production passwords, API secret keys, signing material or private screenshot paths in Git. Production deployments from `main` are automatic in Vercel; treat a merge as a potential live release, not just a code-history operation.

## History

Original repository histories were imported without squashing, with `legacy-web-baseline` and `legacy-android-baseline` tags retained. Original repositories remain available, and Android signing/versioning remain owner controlled. Do not rewrite imported history, reset hosted data, change production bindings or publish a Play release without its specific acceptance boundary.

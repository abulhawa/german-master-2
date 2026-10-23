# German Master 2.0

One monorepo for the React web learner, native Kotlin Android learner, versioned exercise contracts, authoritative TypeScript learning engine, PostgreSQL/Supabase backend, content and verification.

## Current status (23 October 2026)

**Recovery and product simplification in progress.** See [recovery execution plan](docs/operations/recovery-execution-2026-10-23.md) and [issue #20](https://github.com/abulhawa/german-master-2/issues/20) first. This section supersedes the old bootstrap-only description.

- The current production Vercel deployment is built from `main` commit `a9f0c3ae`. Its five-question local guest B1 starter works, but the account API responded HTTP **503** at the recovery checkpoint because production still targets the **inactive** older v2 Supabase project.
- The clean replacement Supabase project is **ACTIVE_HEALTHY**, with initial schema baseline 1, 29 RLS-enabled application tables, 65 published B1/B2 learning targets, 125 exercise revisions and two published content releases. At inspection there were no replacement Auth users or learner attempts. **Do not re-seed or reset it.**
- A safe project-binding code correction is under review; production credentials, SMTP, redirects and real end-to-end account lifecycle still need isolated verification before any domain/backend cutover.
- Android release is **BLOCKED** by the owner's UX acceptance hold. Passing unit tests is not release authorization.

The original web and Android repositories, source history and application signing identity are preserved. Historical entries in [PROGRESS.md](PROGRESS.md) are evidence, not necessarily current state. Do not infer readiness from historical milestone acceptance.

## Work order

1. Restore and verify the **authenticated** backend against the clean instance, using separately scoped credentials and a nonproduction test host. Do not switch Vercel just because a guest session works.
2. Review one coherent learner journey: Home / Start, level or topic focus, question, correction, summary, useful next practice. Make guest and signed-in outcomes understandable without exposing server, offline queue, or receipt terminology on learning screens.
3. Verify responsive signed-in web and real-device Android navigation, progress, offline and privacy behavior. Conduct a small learner pilot before adding features.
4. Stage, approve and perform a reversible production cutover separately; do not delete the old v2 database or release Android automatically.

Read root [AGENTS.md](AGENTS.md), the recovery plan and the relevant sections of [product blueprint](docs/product/blueprint.md) before changing behavior. Scope each PR to a learner problem and verifiable acceptance criteria.

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

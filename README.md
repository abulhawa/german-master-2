# German Master 2.0

One product repository for the React web application, native Kotlin Android application, and the German Master 2.0 product baseline.

## Current state

This is the history-preserving monorepo foundation. Both existing codebases have been imported without changing their application behavior. The existing API, database model, content tools and TypeScript shared utilities remain inside `apps/web` during the transition. The new mastery engine and v2 API described in the blueprint are not implemented yet.

The original repositories remain intact. This repository is public at the project owner's explicit request, including the imported Android source and history. The original Android repository retains its private visibility. No production environment, deployment, database, store listing or signing configuration has been changed.

## Layout

```text
apps/web/          Existing React application and transitional backend/tooling
apps/android/      Existing native Android Gradle project
docs/product/      2.0 source of truth and executive summary
docs/adr/          Repository decisions and transition plan
docs/operations/   Setup, verification and release safeguards
.github/workflows/ Independent web and Android checks
package.json       Root npm workspace and web command entrypoints
package-lock.json  Single TypeScript dependency lockfile
```

Future API, learning-engine, contract and token packages are created when their first working implementation is introduced. Do not add placeholder libraries that imply those features already exist.

## Product documentation

To resume in a new Codex chat, open this repository and say **"Continue the German Master renovation."** Codex should read [PROGRESS.md](PROGRESS.md) and the [continuation guide](docs/operations/continuation.md), verify the current checkout, and implement the next unfinished slice. The checkpoint is updated at each handoff.

- [Complete blueprint](docs/product/blueprint.md)
- [Executive summary](docs/product/executive-summary.md)
- [Polished reading edition](docs/product/German-Master-2-Blueprint.html)
- [Repository decision and next restructuring steps](docs/adr/002-monorepo-bootstrap.md)
- [Development setup](docs/operations/development.md)
- [Bootstrap verification](docs/operations/bootstrap-verification.md)

Legacy product documents under `apps/web/docs` explain the imported implementation. The 2.0 blueprint and newer ADRs govern new product development.

## Development

Use Node.js 22 and npm 10. Run from this repository root:

```powershell
npm ci
npm run check
npm test
npm run build
npm run dev
```

The web commands run inside `apps/web`, keeping its existing relative paths valid. Its development mode uses the existing local in-memory database mock. Do not supply production credentials for routine builds or tests. Production builds require explicitly configured environment values and deployment review.

Open `apps/android` as the project in Android Studio. It retains its own Gradle wrapper and version catalog. See the development guide for Java, Android SDK and local configuration requirements. Run debug verification from that directory:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

No root command resets a database or publishes an Android app. CI builds debug artifacts only; release promotion and environment provisioning are separate future work.

## History and releases

The full original histories are merged as Git subtrees without squashing. Tags `legacy-web-baseline` and `legacy-android-baseline` point to the imported source commits. New product work happens in this repository; imports are not ongoing bidirectional synchronization.

Web/API and Android will retain independent release versions. Do not enable production deployment from this repository before the blueprint's cutover gates are met.

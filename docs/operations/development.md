# Development setup

## Web

Install Node.js 22 and npm 10. The initial verification uses Node 22.23.3 and npm 10.9.9. The root package uses npm workspaces with `apps/web` as the initial TypeScript workspace. Run `npm ci` at the root; the root lockfile is authoritative.

```powershell
npm run check
npm test
npm run build
npm run dev
```

These forward to the existing web scripts in their own working directory. No root script publishes, seeds or resets any database. Local development uses the inherited `USE_DEV_DB_MOCK=1` configuration. A development build can be created without production secrets; production validation must remain enabled for actual deployment builds.

Keep any local web environment file in `apps/web/.env`, ignored by Git. Do not copy the original workstation's `.env` into source control or create production environment values for tests. Existing `.env.example` documents the imported configuration; the 2.0 environment model remains to be implemented.

## Android

Open `apps/android` directly in Android Studio. Use the checked-in Gradle wrapper 9.4.1, the version catalog's AGP/Kotlin versions, and the daemon's Java 21 requirement. Source compile compatibility remains Java 17 as declared by the imported app.

The app currently requests compile/target SDK 37 and NDK 29.0.14206865. Install matching SDK tools rather than silently lowering the target. Configure only local `sdk.dir` if your environment does not provide an SDK location. `local.properties.example` describes optional auth and signing settings; keep real values outside Git. Debug checks do not need release signing or Play publishing credentials.

```powershell
Set-Location apps/android
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

The imported Gradle configuration disables blocking release lint. CI separately invokes debug lint, but hardening lint to a blocking release policy is still backlog work. Release signing, store track verification and production environment creation require the later release runbook.

## Git and history

Work in the new monorepo; do not attempt to synchronize changes back to the old repos automatically. Trace history with Git logs and the two legacy tags. Subtree import commits retain the original hashes as parents. Follow new relative paths after imports when using file history tools.

## CI

Web and Android workflows are independent and run on pull requests, relevant main pushes and manual dispatch. Documentation-only edits do not rebuild the clients. The workflows build local/debug artifacts and never deploy or publish. Contract/design implementation changes should expand path filters to verify both clients when those shared assets are added.

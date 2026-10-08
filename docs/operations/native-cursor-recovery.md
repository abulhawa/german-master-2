# Native local cursor recovery verification

4 October 2026. Implements ADR 019 in the debug learner repository and public-fixture transport.

`NativeSyncTest` covers replacement/removal after reset, full AtomicFile round-trip and repository restart with the replacement cursor. Cases preserve assisted drafts, saved token order, position/counters, pending attempts or Skip events, unacknowledged session requests and concurrent pending profile requests, with no write calls during refresh. A cache serialized without the new nullable cursor loads and upgrades through a full snapshot without a sync request.

Failure tests retain confirmed data and cursor through target continuation errors and local save failure, then retry from persisted state. A successfully saved delta survives a later reset and failed snapshot pagination. A failed delta save prevents requesting the next page. Ordinary sync failure starts no snapshot. Repeated sync/snapshot cursors, watermark/timestamp changes and duplicate snapshot targets fail safely.

Multi-page deltas add new targets while retaining newer evidence when an older target version arrives. A subsequent snapshot failure retains that merged data and cursor together.

`LearnerHttpTest` uses an ephemeral JVM loopback HTTP server to verify the v2 sync route, fixture authorization/no-store headers and strict generated parsing. Only a complete valid HTTP 400 `invalid_cursor` error on sync produces the reset signal. The same error on targets/profile, other statuses/codes, malformed errors and malformed success responses do not. This server supplies mock responses, not the authoritative TypeScript service. Robolectric repository restart is not physical process-kill or device runtime evidence.

Reproduce from the repository root with the installed Java 21 and Android SDK paths:

```powershell
$env:JAVA_HOME='<local-toolchain-path>
$env:ANDROID_HOME='<local-toolchain-path>
$env:GRADLE_USER_HOME='<local-toolchain-path>
apps/android/gradlew.bat -p apps/android :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --offline --console=plain
```

Production auth/network PostgreSQL, account switching, coordinated outboxes, offline reconciliation, real native HTTP against the authoritative API, device/TalkBack/font-scale acceptance and M0/content gates remain open. No production mutation, deployment, AI/Groq inference, publication or store upload is used.

Final verification: root generated contract/token guards passed. Full offline Android checks passed with 34 suites / 132 tests, zero failures/errors/skips, debug assembly and blocking lint (zero errors, 16 existing warnings). Git whitespace check passed. The installed adb tool reports no attached device/emulator after the approved outside-sandbox inventory check; device acceptance was not performed. Compose test API deprecation notices remain. Web/backend/contracts implementations were unchanged, so their full suites were not repeated. No new hosted CI result is claimed.

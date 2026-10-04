# Native learner shell verification

4 October 2026. Scope: debug setup/preferences, Home and server-confirmed Progress; no release navigation changes.

## Local implementation and checks

- Root generated contract/token guard passed; no generated models or shared behavior changed. Web/backend implementation is unchanged, so their full suites were not repeated.
- Android offline unit tests, blocking lint and debug assembly are run with Java 21, Gradle 9.8.0, installed SDK and local fixtures. Final reports: 29 suites / 109 tests, zero failures/errors/skips; lint zero errors / 16 existing warnings; debug assembly passed.
- Repository tests use a real AtomicFile for restart and corrupt-file preservation. They cover accepted-response loss, exact request replay after repository reconstruction, rereading a later profile revision, storage failure before HTTP, failed acknowledgment persistence, interrupted pagination retaining the old snapshot/pending write and invalid timezone preventing upload.
- Compose tests under Robolectric verify that a due Needs practice target is counted once on Home, authoritative Progress groups/check counts, unavailable catalog copy, pending-write edit locks and explicit profile reload recovery.
- Local HTTP transport test starts an ephemeral loopback server, checks authenticated/no-store GET profile/catalog/targets and POST profile routes, exact encoded frozen write and generated response decoding. This is transport verification against a mock server, not Android traffic against the PGlite service or production.
- Existing foundation previews/tests are included in the full Android suite. No Groq/AI calls are used.
- `adb devices` reported no attached device/emulator. Native real-device HTTP, process-kill behavior, TalkBack, font scaling and visual acceptance are not claimed. AtomicFile/repository reconstruction is the restart evidence; Robolectric source/test assertions do not replace device acceptance.

## Local reproduction

Start the isolated API as described in `services/api/README.md`, assemble/install the debug APK, then on an available device use:

```powershell
adb reverse tcp:5001 tcp:5001
adb shell am start -n com.germanverbmaster.android/.learner.LearnerPreviewActivity
```

The debug-only activity uses the existing public local fixture token on loopback port 5001. It shows the shared-account/unpublished-data label. Configure B1/B2, English/German, five/fifteen preferred questions and IANA timezone; save, open Progress, refresh or edit preferences. B2 availability can be zero in this unpublished B1 catalog. Native Start Practice is deferred and explicitly described in the shell.

The separate app-private `german-master-v2-local-learner.json` preserves pending writes and confirmed data; legacy Room and foundation entry points remain intact. Do not treat deleting this file or clearing application storage as routine recovery: it can discard pending work. If the ephemeral local backend resets, use explicit current-profile reload to resolve a stale pending revision once the service is available.

## Limits

Single-activity fixture storage does not establish production account partitions, cross-process coordination or durable offline attempt outboxes. Profile reads are not integrated into target sync. Fresh frozen snapshots are supported; delta ingestion/cursor expiry/reset are deferred. No milestone exit gate, learner usability, independent language review, production cutover, deployment or store acceptance is claimed. Next implement native server-selected Practice, frozen attempts/exposures, feedback, Skip and distinct summary counts.

## Hosted confirmation

Implementation `1b1050e` is committed and pushed to main. [Android checks](https://github.com/abulhawa/german-master-2/actions/runs/37207322904) and [Repository safety](https://github.com/abulhawa/german-master-2/actions/runs/37207322906) both completed successfully at that exact commit. Android CI ran generated guards, unit tests, debug assembly and blocking lint; Web CI was not triggered by this Android-only slice. Staged and full-history Gitleaks scans found no secrets. The final hosted-results checkpoint is documentation-only and committed/pushed at handoff.

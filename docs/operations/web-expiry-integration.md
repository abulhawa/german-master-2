# Web refresh against an expiring local API

4 October 2026. This verification extends ADRs 016 and 017 without changing learner behavior or retention policy.

`expiry.integration.test.tsx` mounts the actual owned learner component, using its unchanged transport, refresh handler, serializer and persisted journey. An ephemeral loopback HTTP server uses the real authoritative store and in-memory PGlite migrations/catalog. Authentication accepts only the public fixture token. Time is injected into the server with a one-second cursor lifetime; no wall-clock wait or external service is needed.

Three cases retain a pending assisted attempt, Skip event or profile request after its server acceptance but before its acknowledgment is saved locally. At the exact expiry boundary the real sync endpoint returns HTTP 400. Recovery requests one-target pages; injected loss of the second page leaves the entire previous journey unchanged and displays the existing refresh error. Remount reloads that saved cursor, completes all five pages and atomically replaces the confirmed snapshot. Draft, assistance, stable request IDs, pending payloads and counters remain identical. Recovery sends no writes. Attempt/Skip snapshots include the accepted exposure, and explicit replay returns duplicate with exactly one evidence row. Frozen profile replay leaves the profile revision unchanged. A subsequent button-triggered pull uses the recovered cursor.

Run from the repository root:

```powershell
npm run test:web-http
```

The suite is also part of root `npm test` and the existing Web checks workflow. Its separate Node-mode Vitest configuration uses the existing DOM setup and React transform, allowing server migration file URLs to remain intact. The usual client Vite suite excludes this integration file to avoid browser asset rewriting of server filesystem imports.

This is component runtime plus real local HTTP/PostgreSQL integration, not Chrome visual/browser lifecycle acceptance. Web Lock acquisition is bypassed by mounting the owned component; actual multi-tab ownership has separate recorded evidence. Native device, TalkBack/font-scale, production identity/network PostgreSQL, physical cursor/page cleanup, offline reconciliation, independent content review and M0 recovery gates remain open. No production data, AI inference, deployment, publication or store upload is used.

The retention extension adds three real expired-continuation cases, one for each pending write kind. Both cursor and page lifetimes are one second. After recovery obtains its first page, the next continuation advances injected time to the exact page deadline and runs real transactional `cleanupReads()`. The test verifies the page table is empty and the HTTP continuation returns 400. Only one cursorless snapshot is started before failure: no automatic retry loop occurs. The entire persisted journey remains identical and refresh sends no writes. Attempt/Skip cases explicitly press Refresh in the same mounted component; pending profile setup retries through remount because that screen has no refresh control. The existing refresh completes recovery and its subsequent full snapshot with all five targets, preserving frozen pending writes and accepted-response-loss replay semantics. No learner implementation changes were needed. Physical local read-cache cleanup is covered by this extension; production retention remains open.

Final local verification: offline root install (`npm ci --offline --ignore-scripts --no-audit --no-fund`), root generated/type checks, root `npm test` (backend nine files / 72 tests; web 80 files / 338 tests; HTTP integration one file / three tests), root web/API/server build and Git whitespace check passed. The first sandbox build failed before compilation at `uv_os_get_passwd`; the approved outside-sandbox rerun passed. Dependency deprecation and stale Browserslist notices remain. Android implementation is unchanged and its checks were not repeated. No new hosted CI result is claimed.

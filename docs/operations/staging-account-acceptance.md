# Prepared real account acceptance

6 October 2026. Execution remains pending separate approvals. Use only the
dedicated staging project `zgmyrpzwgtydwlzponih`; preserve legacy production
routing documented in `live-release-inventory.md`.

## Prerequisites and approvals

1. Completed: owner-approved single-function advisor correction, with actual
   metadata verification and zero security advisor findings on 6 October 2026.
   Performance/load findings remain open.
2. Separately authorize scoped credential retrieval/provisioning through a
   protected secret channel: backend, session-verifier and identity-verifier
   connections, publishable client key and, only for approved deletion tests,
   server-only admin capability. Verify NOLOGIN role inheritance, TLS, RLS and
   column-only observer grants. Never print or commit credentials.
3. Identify two owner-controlled test identities A/B and approve their creation
   and recovery-email activity. Do not infer permission to create accounts or
   send recovery email from this plan. Record opaque subjects only in evidence.
4. Separately approve staging content publication and API/client deployment if
   needed for real practice. Independent German review of the 30-target catalog
   remains pending; a synthetic catalog is only engineering evidence.
5. Before deleting A, separately approve its live identity deletion and review
   retention/log/backup/storage handling. Keep default deletion controls disabled
   until the required acceptance is established. B is the preservation control.

## Execute and retain evidence after prerequisites

| Gate | Scenario | Required evidence |
|---|---|---|
| M2 | Independent backend/verifier connections; A cannot read/write B or publish content | Actual roles, sanitized transaction results, denied operations; representative load and plans separately |
| M3/M4 | Register, confirm where configured, sign in, recovery and fresh reauthentication | Actual provider outcomes on each client; registration/recovery UI gaps must be recorded rather than credited from legacy UI |
| M3/M4 | Resume and finish mixed practice; view confirmed Progress | Same immutable revision/session IDs, server receipts and matching confirmed outcomes |
| M4/M5 | Native Keystore restart, offline cold launch, force-stop with pending work | Captured before/after pending IDs and confirmed exactly-once replay; physical device evidence |
| M5 | A expires offline; switch to B; reauthenticate A and reconcile web/native | No A uploads under B, A work preserved, both clients converge without lost attempts |
| M3–M5 | Explicit sync/sign-out versus removal; export | Provider session revocation, saved-account isolation and owned export; no credentials in artifacts |
| M3–M5 | Approved deletion of A, response loss/restart/session-free receipt recovery | Learning tombstone before provider deletion, authoritative user/session absence, B intact, local cleanup only after receipt; storage behavior checked |
| M0/M5 | Full protected backup/restore rehearsal | Auth/storage/grants/functions and deletion jobs/tombstones preserved before uploads resume; public-row export alone is insufficient |

## Preserved Pixel continuation

Current host inventory: `adb devices -l` lists no device; TCP 5001 has no
listener. No APK install, force-stop, discard, fixture restart or settings change
was performed. Reconnect the original device and locate its original surviving
fixture process/session before submitting anything. If the process was lost,
report that fact and prepare an explicit replacement acceptance run without
discarding preserved app data; the export is not a database restore.

Saved session: `7440ed09-8f49-4844-9d5c-ff9b5264cf65`, question 4/5,
draft `weil ich heute im Büro arbeite`, three graded/three correct, confirmed
report. Verify session ownership/state at the server, then submit this word order,
complete the `du`/`ihr` multi-slot question through the UI, finish all five and
inspect summary plus confirmed Progress. Retain request/receipt IDs and device
screens; distinguish expected answers from actual server results.

Repeat the remaining core path at 200% font and with TalkBack where practical;
record input labels, token positions/move controls, feedback announcements,
focus, multi-slot submission and summary/Progress. Check recreation and
force-stop preserve work; restore original device settings. Deferred scroll
ergonomics remains deferred unless it prevents this required journey.

Independent content/design approval, off-machine full recovery/Play custody,
reviewed deployed content and pilot/retention evidence remain separate gates.
This preparation closes none of them.

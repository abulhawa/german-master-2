# Identity deletion clients and acceptance checkpoint

5–6 October 2026. Local implementation continues ADR 031. Configured deletion
controls remain disabled by default; this is not live provider acceptance.

## Implemented

Both clients persist a subject-bound UUID and cryptographically random 32-byte
base64url recovery capability before delivery. Password proof stays transient.
The same durable marker blocks normal learner operations and uploads across
restart. Fresh authenticated begin may deliver an admitted job through the
single-process serialized private worker; status never dispatches work.

Session-free POST recovery validates the identity-specific receipt and original
request ID. Pending or ambiguous results preserve local work. Only a saved
`identity_deleted` receipt permits local cleanup. Cleanup failure retains that
receipt. The last-account reference is forgotten after the terminal local
marker is saved, so failed cleanup remains discoverable. Cleanup is scoped to
the captured subject; a newly signed-in account keeps its saved reference and
credentials. Normal provider sign-out and learning-only deletion remain separate.

Prepared English/German controls offer explicit confirmation, transient current
password proof, read-only status recovery, fresh-proof retry and local cleanup.
They are behind the existing configured-host boundary. No release route or
production deletion control was enabled.

## Verification

Node 22.23.3 root checks passed. Full backend: 25 files / 161 tests; full web:
90 files / 379 tests; real web HTTP: four files / 13 tests. Subsequent focused
web controls/provider checks: four files / nine tests; the final old-subject
cleanup/new-account test passed with the provider suite (five tests).
Root offline dependency installation and web/API/server build passed.

Full offline native unit tests: 40 suites / 157 tests, zero failures, errors or
skips; debug lint and assembly passed. Later provider/identity refinements and
debug/preview builds passed, followed by final manifest lint/debug/preview
assembly. Existing Gradle/Compose warnings remain.

The web provider transport/client marker and native repository/AtomicFile plus
HTTP transport pass against the actual loopback TypeScript/PGlite service.
They recover a lost completed response after restart/session loss. Focused
tests cover pending recovery, failed persistence before delivery, wrong receipts,
cleanup failure, ordinary upload barriers and another account's isolation.
Provider/Keystore results are mocked or compiled, not live identity acceptance.

## Pixel 10 Pro acceptance and saved continuation

The isolated preview APK was updated beside the original app, preserving both
applications' data. The old completed 4-graded/1-Skip fixture cache was backed
up locally before its explicit preview discard; it had no pending answer,
Skip or draft. A new actual loopback session started with five forms.

Short-answer, choice and cloze were each submitted and received Correct server
feedback (three graded / three correct). Typed IME Enter worked at 200% font.
Font changes/activity recreation preserved the session and draft. A revision
report survived a removed loopback connection and force-stop, then confirmed on
explicit retry with the same report ID, same session and unchanged graded count.
This closes the physical fixture report/restart/retry subrequirement.

The large-font keyboard check exposed a preview-only manifest omission:
`LearnerPreviewActivity` lacked `adjustResize`, which already exists on the
main activity. Both preview manifests now specify it. The rebuilt APK was
visually checked: the active input stays above the keyboard and the scrolled
header starts below the system status area. No speculative clipping change was
retained.

**Deferred at owner request:** vertical scroll drift/position restoration and
overall native scrolling ergonomics still need acceptance before production.
Do not spend another continuation on this ahead of the explicit next action
unless it blocks a required core journey. TalkBack and full large-font core
journey acceptance remain open.

At owner-requested wrap-up, session `7440ed09-8f49-4844-9d5c-ff9b5264cf65`
is on question 4/5 (word order), with ordered draft `weil ich heute im Büro
arbeite`, three graded/three correct, no pending answer, and a confirmed report.
Question 4 has not been submitted; multi-slot, full completion and Progress
acceptance have not been claimed. The device font and stay-awake settings were
restored to their original values. Loopback API remains on port 5001; recheck
its process/session before resuming. It is in-memory: do not restart it as an
attempt-recovery shortcut.

Ignored evidence is under `.local/device-acceptance-handoff.json`,
`device-report-pending.json`, `device-report-confirmed.json`,
`device-cache-preserved-completed.json`, `device-acceptance-server-export.json`
and the font/keyboard PNGs. The owned export is evidence, not a full database
restore or proof that the fixture server can be recreated from it.

## Remaining gates

No full M0–M6 milestone closes. Live staging identity/session/storage deletion,
retention and restore approval, registration/password recovery, real account
reconciliation/Keystore, reviewed content, independent design/accessibility,
private full recovery/Play custody and pilot evidence remain open. See the
separate approved [staging installation evidence](staging-initial-install-review.md).

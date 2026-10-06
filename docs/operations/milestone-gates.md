# Renovation acceptance gates

## 6 October follow-up evidence

Owner subsequently approved the single-function correction; it was applied as
`v2_reject_mutation_fixed_search_path`. Actual metadata confirms the empty path
with unchanged body/owner/ACL/19 triggers; security advisor now reports zero
findings. The staging search-path security subrequirement closes. Performance
findings and M2 identity/catalog/contention/load remain open; no full milestone
closes. The awaiting-approval description below is superseded by this result.

Hosted Web/Android/accessibility/safety/CodeQL pass for product commit
`3c147367`; safety/CodeQL pass for inventory merge `f019309a`. The earlier
runner-allocation blocker is resolved. The merged live release inventory remains
the authoritative legacy production routing record.

The single-function search-path correction is locally verified and awaiting
explicit staging approval; actual security advisor still reports the warning.
No device is attached and the saved fixture listener is absent on this host;
the saved Pixel session remains untouched, with remaining core/accessibility
acceptance blocked. See [correction review](staging-advisor-remediation.md) and
[prepared account/device acceptance](staging-account-acceptance.md).
These resolve hosted evidence and prepare live work; no full milestone closes.

Reconciled against blueprint section 23 on 5 October 2026. A local fixture implementation, a passing build, a deployed product and learner acceptance are distinct evidence. No full product milestone closes in this continuation. The monorepo bootstrap is complete.

Identity-deletion continuation now has locally verified durable server jobs,
fresh-password verification, a concrete hard-delete provider adapter and private
receipt recovery. Prepared auth-session grants now include the SELECT policy
required by Supabase auth RLS. Opt-in versioned begin/status HTTP routes and
serialized private worker delivery now pass actual local HTTP/PGlite checks.
Durable web/native identity deletion markers/UI, explicit host dispatch,
actual staging auth/session/storage deletion and retention/
restore acceptance remain open; configured deletion stays disabled. No full
gate closes from this preparation. Local Chromium keyboard/focus/320px/reflow
acceptance passes for the existing fixture; manual assistive-technology/design
acceptance stays open. See [ADR 031](../adr/031_identity_deletion_continuation.md)
and [web accessibility evidence](web-accessibility-acceptance.md).

| Milestone | Evidence available | Requirements still preventing exit |
|---|---|---|
| M0 Preserve and baseline | Source/history restores, deployed-source bundle, exact public-row recovery, local/earlier hosted builds | Full PostgreSQL/functions/grants/auth/storage restore, private off-machine recovery, live environment/API parity, Play published maximum and signing/key recovery |
| M1 Contracts/design/content | Both clients parse five initial forms, generated tokens/contracts, target-centered local schema; separate validated 30-target/60-variant editorial workbook | Independent German review of the 30 targets/variants, reviewed-catalog client acceptance, recorded design approval and accessibility acceptance |
| M2 Authoritative engine | Different owned histories produce different queues; transactional grading/evidence/schedule/sync and idempotent replay; scoped network adapter/server verifier; published runtime catalog routing pass locally | Actual reviewed catalog acceptance, approved network/identity connection, least-privilege and independent-connection contention, representative staging load |
| M3 Web vertical slice | Local setup/Home/Practice/summary/Progress/Topics/detail; restart and frozen-write HTTP recovery; typed Enter, word-order controls, durable reports and full/partial completion | Production auth/account lifecycle; release routing still uses legacy app; full keyboard/viewport/WCAG acceptance |
| M4 Android parity | Debug learner journey, server outcomes, AtomicFile persistence; real authoritative JVM HTTP including reports; device retry/4-graded+1-Skip summary/Progress smoke; local full/partial completion | Production API/auth replacement in the release app; verified production outbox/account lifecycle; report UI, TalkBack/large-font/keyboard-inset acceptance; same reviewed content |
| M5 Offline/operations | Owned packs; web IndexedDB/native AtomicFile reserves and atomic slot consumption; local offline practice, shared 36-case provisional grading, ordered durable delivery; whole-client fixture reconciliation with per-receipt commits; subject-partitioned stores and captured identity-generation/server-subject guards with local expiry/switch/reauth preservation; isolated asset-only web shell with hosted Chromium offline browser restart/reload, late sync and safe update acceptance; two web fixture stores converge after response loss/auth rejection | Verified production auth/account integration, native/physical two-device and real expired-auth/account-switch reconciliation, device/accessibility acceptance, host-auth deletion/sign-out and release recovery (owned export, durable local deletion and explicit fixture sign-out are implemented on both clients) |
| M6 Pilot/readiness | Blueprint defines pilot and delayed follow-up | About 120 reviewed targets/two variants, representative learners and access needs, observed usefulness/correction disputes, delayed retention evidence, protected staging/release assets and no critical defects |

M5 has local groundwork in progress; the old "Not started" label understated the implemented pending-write and cursor recovery mechanisms. Its airplane-mode/restart/two-device/expired-auth exit gate remains open. M6 has not started.

## Offline continuation — 5 October 2026

Web reserve caching and both fixture clients' start/consumption/expiry UI, provisional feedback and ordered multiple-event offline delivery are implemented. Actual web component/HTTP and native repository/authoritative HTTP tests cover restart and response/receipt loss; native consumes both sessions and retains a partial assisted draft. Shared TypeScript/Kotlin grading conformance passes 36 cases. No full milestone closes: a loaded local shell with failed HTTP is not cold-launch/PWA/device airplane-mode acceptance, and fixture ownership is not production account isolation. Evidence: [offline practice](offline-practice.md), [ADR 022](../adr/022_local_offline_practice.md).

Before the first real staging or production database, consolidate development migrations into a clean v2 baseline. The current migration chain remains useful for disposable fixture tests; no genuine operational compatibility requirement is claimed.

## Requirements closed in this continuation

- Initial 30-target/60-variant draft inventory, executable contract/rubric validation, reviewer workbook and content-hash sign-off checks. Independent approval remains zero; the M1 content exit gate stays open.
- Revision-linked category reports persist and replay explicitly in both local clients; actual API tests enforce ownership, immutable revision linkage, replay/conflict and a rolling new-report limit without learning evidence. Native transport/repository report replay also passes against the actual local service.
- The original frozen assisted device answer retried successfully. Device practice reached a 4-graded/1-Skip/4-correct summary and confirmed Progress; a final accepted attempt replay returned duplicate. All-five-renderer device and accessibility acceptance remain open.

- Native repository and transport together recover over real loopback HTTP against the TypeScript/PGlite service, including exact cursor expiry, deleted continuation pages, failed refresh, restart, complete replacement and unchanged pending writes. Explicit write replay produces one evidence event/profile revision. This is JVM/Robolectric integration, not device accessibility evidence.
- Native mixed practice caps new requests to owned catalog availability and the saved preference. Empty/unknown availability blocks new creation; existing frozen requests replay unchanged after availability changes. Topic focus follows the same preferred bound, target focus uses one question.
- Both learners can move word-order tokens left/right, with identified positions, disabled boundaries and persistent drafts. Reordering does not submit or grade. Failed web draft save keeps the previous visible order.
- Web Enter checks only ready text inputs, ignores IME composition/repeat and cannot advance feedback. Native IME Done checks ready typed/slot answers. Summaries list unique covered targets, separate graded/correct/Skip counts and explain that retention requires later independent checks; native summary offers confirmed Progress.

## Priorities and owner-dependent gates

The content gate is on the critical path: the separate draft validation/review workspace now has 30 targets/two variants and sign-off hashes, with zero independent approvals. The immutable five-target fixture remains intact. Independent German-language review is required before any publication or pilot. See content/drafts/REVIEW.md and content-review-and-reporting.md.

Local full/partial completion and Close choices are implemented and verified on both clients (5 October); production/device acceptance remains open. Prepared sessions, per-session offline outboxes, whole-client fixture reconciliation and subject/generation isolation now work locally; the isolated web shell has hosted browser acceptance. Owned confirmed-data export and explicit sync-then-export are now implemented in both fixture learners; the local API also has transactional owned deletion/replay with an abandoned-upload tombstone. Durable subject-bound deletion UI and explicit fixture sign-out/removal choices are implemented. Continue verified auth integration in the owner-selected new german-master-v2-staging project; creation was separately approved and completed; credentials, schema and deployment remain separately gated. Revision-linked category reporting has durable explicit retries in both local clients and owned idempotent/rate-limited storage; production moderation remains open. Production account work needs a specifically identified isolated environment, not reuse of legacy production. These are engineering requirements, not optional import, speech, essay or chat features.

Owner input/resources needed: a German-language reviewer and design acceptance; a private off-machine backup destination/full restore tooling; Play published-version/signing custody records; approved credentials/schema setup for the identified isolated staging/auth environment; and later pilot participants. Deployment/cutover, destructive resets, content publication and store upload each retain their authorization boundary.

## Privacy continuation — 5 October 2026

Versioned owned export and both fixture export controls are implemented. Server export includes preferences, sessions, accepted answers/evaluations, Skip/exposure events, confirmed state, reports and completion receipts; local drafts/pending work are excluded unless explicitly synced. Web browser verification confirms a successful download, no captured console errors and a 320px layout without horizontal overflow. Native authoritative JVM HTTP verifies explicit sync/export and draft preservation; Android save-picker/device acceptance remains open. Disposable API deletion removes owned data transactionally, replays the original receipt and blocks abandoned writes; ownership/shared-content preservation and rollback pass. Both fixture clients now expose durable deletion and explicit sign-out choices. Host-auth deletion/reauthentication, retention/privacy review and release routing remain open. No full milestone closes. See [ADR 026](../adr/026_owned_privacy_foundation.md).

## Dedicated staging preparation — 5 October 2026

The owner selected and separately approved creation of german-master-v2-staging in ali's Org, Frankfurt/eu-central-1; project zgmyrpzwgtydwlzponih was created and verified healthy. Credentials, schema and deployment remain separately gated. The clean empty v2 baseline and non-secret setup plan are prepared and locally verified. No external schema, auth or deployment mutation occurred. Local deletion/sign-out and baseline requirements close; no full milestone closes. See [ADR 027](../adr/027_durable_privacy_and_staging_baseline.md) and [staging preparation](v2-staging-preparation.md).

## Network backend continuation — 5 October 2026

The network SQL adapter, transaction-local verified subject scope, non-owner backend policies, separate column-limited auth-session role and online Supabase server verifier are implemented. PGlite checks role ownership and pack/Skip/completion/export/deletion; driver/verifier mocks and isolated auth fixtures cover composition, TLS, precision, revocation and expiry. No actual network connection, independent contention, live Supabase auth or schema/advisor acceptance is claimed. See ADR 028.

## Configured learner and runtime catalog continuation — 5 October 2026

Both isolated preview entry points now support dedicated provider authentication, credential-aware HTTPS transports, subject/generation guards and durable current-device revocation after explicit sync/removal. Native encrypted Keystore-backed session storage is implemented but has no device acceptance. Web auth-host component/gateway tests and local browser sign-in-layout checks pass; native provider/gateway/repository tests and compilation pass. These are mocked auth results, not live lifecycle acceptance. Configured offline/expired-auth cold launch is locally implemented and tested; real provider/device acceptance remains open. Dedicated v2 web artifacts and an opt-in Android release path are prepared without deployment/signing/store changes. Deployed routing remains unchanged; real identity deletion stays hidden/disabled.

## Saved-account and release artifact continuation — 5 October 2026

Configured hosts reopen only the previously verified subject's saved work, keep network delivery gated, preserve frozen writes and durable privacy markers, and expose explicit reauthentication. Mounted web and native AtomicFile tests practise actual locally generated packs with expired mocked auth across restart. Native persisted legacy jobs are paused without constructing legacy auth/data dependencies in the opt-in v2 release. Dedicated product builds do not authorize cutover. These close local cold-host and build-routing engineering requirements; no complete M0–M6 gate closes. Live provider/Keystore/two-device/accessibility, reviewed content, host identity deletion/retention/recovery, staging operations and pilot gates remain open. See [ADR 030](../adr/030_offline_account_hosts_and_v2_artifacts.md).

Explicit published runtime catalogs replace hardcoded foundation selection for network practice. Hash/member/status validation, non-fixture IDs, selected snapshots and pinned session/pack replay pass against synthetic local SQL catalogs. Generated client status contracts accept published catalogs. This closes local provider transport/revocation and catalog-routing engineering requirements; no full M0–M6 exit gate closes. M2 still needs actual independently reviewed content, approved network setup/advisors/contention/load. M3/M4/M5 still need deployed release-host acceptance, real identity/reconciliation, actual provider offline cold-launch acceptance, retention/host deletion and physical accessibility. M0 recovery/Play custody, M1 independent German/design acceptance and M6 pilot remain open. See [ADR 029](../adr/029_client_provider_and_runtime_catalog.md).

## Client and real staging checkpoint — 6 October 2026

Both clients now have durable identity-specific deletion markers, transient
proof transports and session-free receipt recovery with cleanup barriers.
Opt-in network admission delivers through the private serialized worker.
Local web/native actual HTTP restart/receipt-loss checks pass; controls stay
disabled by default pending live lifecycle/retention acceptance.

Actual Pixel fixture reporting survives disconnected loopback and force-stop,
then confirms on explicit retry without changing learning evidence. Three
forms received Correct feedback, and 200% font/IME/recreation preserved work.
The preview-only adjustResize omission was fixed and visually verified.
All-five-form, full completion, TalkBack and complete large-font acceptance
remain open. At owner-requested wrap, the fourth-question word-order draft
is saved. Vertical scroll drift is explicitly deferred before production.
See [client/device evidence](identity-deletion-clients.md).

The first clean v2 staging baseline/access/privacy installation was separately
approved and executed. Actual role/RLS/empty-state metadata passes; initial
advisors report security/performance findings requiring remediation. No full
milestone closes and no live identity, contention/load or restore acceptance
is claimed. See [staging installation evidence](staging-initial-install-review.md).

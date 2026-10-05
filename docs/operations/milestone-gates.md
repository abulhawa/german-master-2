# Renovation acceptance gates

Reconciled against blueprint section 23 on 5 October 2026. A local fixture implementation, a passing build, a deployed product and learner acceptance are distinct evidence. No full product milestone closes in this continuation. The monorepo bootstrap is complete.

| Milestone | Evidence available | Requirements still preventing exit |
|---|---|---|
| M0 Preserve and baseline | Source/history restores, deployed-source bundle, exact public-row recovery, local/earlier hosted builds | Full PostgreSQL/functions/grants/auth/storage restore, private off-machine recovery, live environment/API parity, Play published maximum and signing/key recovery |
| M1 Contracts/design/content | Both clients parse five initial forms, generated tokens/contracts, target-centered local schema; separate validated 30-target/60-variant editorial workbook | Independent German review of the 30 targets/variants, reviewed-catalog client acceptance, recorded design approval and accessibility acceptance |
| M2 Authoritative engine | Different owned histories produce different queues; transactional grading/evidence/schedule/sync and idempotent replay pass locally | Reviewed catalog acceptance, production verified identity/network PostgreSQL adapter, least-privilege and independent-connection contention, representative staging load |
| M3 Web vertical slice | Local setup/Home/Practice/summary/Progress/Topics/detail; restart and frozen-write HTTP recovery; typed Enter, word-order controls, durable reports and full/partial completion | Production auth/account lifecycle; release routing still uses legacy app; full keyboard/viewport/WCAG acceptance |
| M4 Android parity | Debug learner journey, server outcomes, AtomicFile persistence; real authoritative JVM HTTP including reports; device retry/4-graded+1-Skip summary/Progress smoke; local full/partial completion | Production API/auth replacement in the release app; account-partitioned coordinated outbox; report UI, TalkBack/large-font/keyboard-inset acceptance; same reviewed content |
| M5 Offline/operations | Local frozen pending writes, acknowledgment durability, cursor/page recovery; two-session owned packs with validated hashes/versions and native reserve persistence | Web IndexedDB reserve, offline start/consumption/expiry UI, offline provisional grading conformance, multiple-event transactional outboxes, two-device/expired-auth/account-switch reconciliation, export/deletion/sign-out, release recovery |
| M6 Pilot/readiness | Blueprint defines pilot and delayed follow-up | About 120 reviewed targets/two variants, representative learners and access needs, observed usefulness/correction disputes, delayed retention evidence, protected staging/release assets and no critical defects |

M5 has local groundwork in progress; the old "Not started" label understated the implemented pending-write and cursor recovery mechanisms. Its airplane-mode/restart/two-device/expired-auth exit gate remains open. M6 has not started.

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

Local full/partial completion and Close choices are implemented and verified on both clients (5 October); production/device acceptance remains open. Continue prepared sessions/outbox reconciliation. Revision-linked category reporting now has durable explicit retries in both local clients and owned idempotent/rate-limited storage; production moderation and account partitions remain open. Production account work needs a specifically identified isolated environment, not reuse of legacy production. These are engineering requirements, not optional import, speech, essay or chat features.

Owner input/resources needed: a German-language reviewer and design acceptance; a private off-machine backup destination/full restore tooling; Play published-version/signing custody records; the intended isolated staging/auth environment; and later pilot participants. Deployment/cutover, destructive resets, content publication and store upload each retain their authorization boundary.

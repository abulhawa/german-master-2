# Renovation acceptance gates

Latest accepted scope, 10 October: the owner accepted current web/Android design and manual web screen-reader review. TalkBack remains accepted under the 9 October decision. These are owner acceptance statements, not agent-observed uninterrupted assistive-technology journeys or complete WCAG certification. See [M1 closure evidence](m1-acceptance-2026-10-10.md) and the newest checkpoint.

Reconciled on 10 October 2026: M0 is complete under its owner-approved preservation/recovery scope. M1 closes with fresh hash-bound AI editorial review of all 60 changed targets, delivered signup/recovery acceptance, passing engineering checks and owner design/manual web accessibility acceptance. M2 is the next active milestone; M3–M5 remain queued and M6 is not started. The product reset remains unfinished.

## Current direction and evidence rules

This is a hard reset with no existing learners to migrate. Do not add legacy-user migrations or compatibility work. Preserve application identity/signing, original repositories, source provenance, immutable content revisions and operational recovery. Clean provisioning, destructive resets, publication and store releases remain separately authorized operations.

New practice defaults to MCQ, ordering, gap choices and one-to-one matching on both clients. Shared contracts, renderers, grading, drafts and offline paths are implemented. The unpublished candidate contains 65 targets / 125 exercises, including 60 B1/B2 targets / 120 freshly AI-reviewed variants; current authored revisions are 3–5. Published content remains unchanged by the 10 October review. See [authoring guide](../content/practice-formats.md), [ADR 033](../adr/033_low_typing_practice.md) and [fresh editorial evidence](m1-acceptance-2026-10-10.md).

The public domain already serves the renovated web product; legacy web routing is not a remaining prerequisite. Production sign-in, confirmed practice/Progress, logout/login, export and hosted API identity deletion have recorded disposable-account acceptance. Confirmation was administrator-assisted: delivered-email/self-service confirmation and browser deletion-dialog recovery are still unproven. See [production lifecycle evidence](confirmed-user-lifecycle-2026-10-06.md).

The 65-target/125-exercise B1/B2 catalog was owner-authorized and published with AI editorial checks; the owner explicitly replaced the independent-human review gate with ChatGPT AI editorial sign-off on 9 October 2026. The review is recorded by target hash and identified as AI work, not human certification. This does not retroactively change the owner-authorized earlier publication. See [basic content release](basic-content-release.md).

Local verification, hosted CI, deployment and learner acceptance are distinct. The [9 October recheck](m1-acceptance-2026-10-09.md) records fetched PR #11 head `bce5cc3`, failing hosted Web/Android checks and passing accessibility/safety/CodeQL/Vercel checks. Local repairs pass full root/Android verification and B2 client tests; physical phone B2/large-text and local browser evidence are partial acceptance. Subsequent fresh fetch confirms repairs pushed at `d9715d0`; all hosted Actions and Vercel statuses pass. Current status below supersedes [archived gate snapshots](milestone-gates-history.md).

## Milestone status

| Milestone | Current status and evidence | Remaining exit requirements |
| --- | --- | --- |
| M0 Preservation/baseline | Complete: preserved histories/content, separate database baseline, verified laptop backups and isolated PostgreSQL/Auth-data/Storage recovery; source/deployed identities recorded; owner-managed signing. | No remaining M0 gate under owner clarification. Live Play maximum remains an owner release check; broader operational recovery/rollback acceptance remains in M5/M6. |
| M1 Contracts/design/content | Complete, 10 October: shared generated contracts/tokens and four low-typing formats; fresh hash-bound AI editorial approval for all 60 changed targets / 120 variants; candidate SQL validated in isolated PGlite; delivered signup confirmation and password recovery; owner accepted both-client design and manual web screen-reader review. TalkBack accepted for current scope. | No remaining M1 gate under accepted scope. Publication remains separate; successful confirmation-resend delivery is a lifecycle follow-up, not inferred from initial signup delivery. |
| M2 Authoritative engine | Active — closing next: server-owned grading/evidence/scheduling, capability-filtered sessions, frozen replay, prepared packs, scoped network roles and recorded live web/API outcomes. New-format HTTP/pack checks pass locally. | Representative independent-connection PostgreSQL contention/load, selection-query cost and remaining operational/performance verification. Reuse existing role/identity evidence and freshly reviewed content; do not repeat established setup as missing. |
| M3 Web vertical slice | Queued — partial implementation preserved: renovated product deployed; guest entry/practice, account journey, topics/Progress, durable drafts/reports/completion, offline shell and recorded confirmed-account lifecycle. | Production converted-content acceptance, successful confirmation-resend delivery, browser deletion-dialog recovery and verification of subsequent releases. Delivered initial signup/recovery and owner design/manual web accessibility acceptance are recorded; do not reopen them as unproven. |
| M4 Android parity | Queued — partial implementation preserved: native learner/repository/provider foundation, authoritative JVM HTTP, draft/outbox recovery and four low-typing controls; debug tests/lint/assembly pass. Matching passes simulated font scale 2.0 at 320dp. | Production/guest parity and full real account/content lifecycle; device/process-death/display-size/magnification/keyboard acceptance within the remaining production/parity scope; release readiness with signing continuity. No new store release is claimed. |
| M5 Offline/operations | Queued — partial implementation preserved: both clients implement owned reserves, provisional grading, frozen outboxes, subject isolation and retry-safe reconciliation; web has recorded offline cold-launch/restart/late-sync acceptance. | Native device and real two-device/expired-auth/account-switch acceptance, retention/restore/privacy/moderation operations and release recovery. Local or simulated tests do not close physical-device gates. |
| M6 Pilot/readiness | Not started: blueprint defines representative learners, access needs and delayed-retention follow-up. | Broader reviewed coverage (about 120 targets with two variants is the blueprint goal), usable converted practice on both clients, participant feedback, delayed-retention evidence and no unresolved critical defects. Current 65 targets/125 exercises do not meet that coverage goal. |
| M7 Targeted expansion | Deferred until usefulness gate. | Expand only after the core loop and pilot establish usefulness; import/speech/full writing are not the current priority. |

## Shared next-work order

Owner decision, 8 October 2026: close milestones sequentially, M0 → M1 → M2 → M3 → M4 → M5 → M6. Only one milestone is active. Later implementation and evidence remain preserved; queued does not mean complete or unstarted. Do not relax exit gates to reduce the number of open milestones.

1. Completed: close M0 with [verified private backup and recovery evidence](m0-closure.md). Laptop storage and owner-managed Android signing are accepted owner decisions; do not reinstate superseded requirements.
2. Completed: close M1 with fresh AI editorial hashes, delivered signup/recovery acceptance and owner design/manual web accessibility acceptance. Do not treat source approval as publication authorization.
3. If the active milestone needs owner access or external resources, record the exact blocker and request only the missing resource. Continue useful work within that milestone; do not activate a second lane.
4. Close active M2 next, then M3 through M5 in order against their remaining requirements above, reusing valid existing evidence and verifying changed behavior. A blocker does not authorize a second active milestone.
5. Activate M6 only after M0–M5 close; complete reviewed coverage, representative pilot and delayed follow-up. M7 stays deferred.

Publication, deployment, destructive operations and store releases retain their existing authorization boundaries. No new external operation is authorized by this sequencing decision.

## Source-of-truth order

Owner decisions → product blueprint/accepted ADRs → this current milestone table → latest root `PROGRESS.md` checkpoint for the exact implementation action and verification → linked evidence. Older checkpoint entries and archived gate snapshots record history, not competing current priorities. [Continuation instructions](continuation.md) use this order. This document summarizes existing evidence; no production, provider or device state was changed or reverified during alignment.

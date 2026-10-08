# Renovation acceptance gates

Reconciled on 8 October 2026 against root `PROGRESS.md`, blueprint section 23 and verified repair commit `e95363b`. Bootstrap is complete; the full product renovation remains unfinished. No complete M0–M6 exit is newly declared by this reconciliation.

## Current direction and evidence rules

This is a hard reset with no existing learners to migrate. Do not add legacy-user migrations or compatibility work. Preserve application identity/signing, original repositories, source provenance, immutable content revisions and operational recovery. Clean provisioning, destructive resets, publication and store releases remain separately authorized operations.

New practice defaults to MCQ, ordering, gap choices and one-to-one matching on both clients. Their reusable contracts, renderers, grading, drafts and offline paths are implemented and locally verified. Three B1 draft topics are converted: adjective endings and irregular present-tense verbs (20 gap-choice exercises), and plurals (20 choice exercises), covering 20 targets at revision 2. The synchronized unpublished candidate still contains 65 targets/125 exercises. Typed controls remain for unconverted content; published revision 1 content is unchanged. See [authoring guide](../content/practice-formats.md) and [ADR 033](../adr/033_low_typing_practice.md).

The public domain already serves the renovated web product; legacy web routing is not a remaining prerequisite. Production sign-in, confirmed practice/Progress, logout/login, export and hosted API identity deletion have recorded disposable-account acceptance. Confirmation was administrator-assisted: delivered-email/self-service confirmation and browser deletion-dialog recovery are still unproven. See [production lifecycle evidence](confirmed-user-lifecycle-2026-10-06.md).

The 65-target/125-exercise B1/B2 catalog was owner-authorized and published with AI editorial checks; independent German review remains pending. Do not call it fully reviewed, or treat independent approval as a prerequisite that retroactively invalidates the owner's publication decision. Independent review remains necessary for content-quality/pilot exit. See [basic content release](basic-content-release.md).

Local verification, hosted CI, deployment and learner acceptance are distinct. The low-typing foundation and content-conversion commits were pushed previously; repair `e95363b` is committed locally. Its root generated/type checks, full backend/web/HTTP tests and web/API build pass locally; no fresh hosted-check or deployment acceptance is claimed. Earlier verified deployments/CI are historical evidence, not proof for the latest commit. Current status below supersedes [archived gate snapshots](milestone-gates-history.md).

## Milestone status

| Milestone | Current status and evidence | Remaining exit requirements |
| --- | --- | --- |
| M0 Preservation/baseline | Bootstrap complete: monorepo/imported histories, source recovery evidence, clean v2 baseline, dedicated product environment and recorded deployment inventory. | Full operational restore/private backup verification; Play published-version and signing/key custody. No learner migration requirement. |
| M1 Contracts/design/content | In progress: four low-typing formats on both clients, shared generated contracts/tokens, redesigned web workspace; 65 targets/125 exercises published under explicit owner authorization; three B1 draft topics (20 targets/40 revision-2 exercises) converted and locally validated in the unpublished candidate. | Preview the three converted topics on both clients and convert the remaining content; independent German review, reviewed-content acceptance on both clients, design and complete accessibility acceptance. The former 30-target draft is historical groundwork. |
| M2 Authoritative engine | In progress: server-owned grading/evidence/scheduling, capability-filtered sessions, frozen replay, prepared packs, scoped network roles and recorded live web/API outcomes. New-format HTTP/pack checks pass locally. | Reviewed converted content; representative independent-connection contention/load and remaining operational/performance verification. Do not repeat already verified role connections as missing setup. |
| M3 Web vertical slice | In progress: renovated product deployed; guest entry/practice, account journey, topics/Progress, durable drafts/reports/completion, offline shell and recorded confirmed-account lifecycle. | Converted-content product acceptance; delivered-email/self-service confirmation, browser deletion/recovery UI acceptance, full assistive-technology/design acceptance and verification of subsequent pushed releases. |
| M4 Android parity | In progress: native learner/repository/provider foundation, authoritative JVM HTTP, draft/outbox recovery and four low-typing controls; debug tests/lint/assembly pass. Matching passes simulated font scale 2.0 at 320dp. | Production/guest parity and full real account/content lifecycle; device/process-death/TalkBack/display-size/magnification/keyboard acceptance; release readiness with signing continuity. No new store release is claimed. |
| M5 Offline/operations | In progress: both clients implement owned reserves, provisional grading, frozen outboxes, subject isolation and retry-safe reconciliation; web has recorded offline cold-launch/restart/late-sync acceptance. | Native device and real two-device/expired-auth/account-switch acceptance, retention/restore/privacy/moderation operations and release recovery. Local or simulated tests do not close physical-device gates. |
| M6 Pilot/readiness | Not started: blueprint defines representative learners, access needs and delayed-retention follow-up. | Broader reviewed coverage (about 120 targets with two variants is the blueprint goal), usable converted practice on both clients, participant feedback, delayed-retention evidence and no unresolved critical defects. Current 65 targets/125 exercises do not meet that coverage goal. |
| M7 Targeted expansion | Deferred until usefulness gate. | Expand only after the core loop and pilot establish usefulness; import/speech/full writing are not the current priority. |

## Shared next-work order

1. Completed: convert B1 adjective endings, irregular present-tense verbs and plurals into authored low-typing revision-2 drafts with bilingual feedback; synchronize the unpublished candidate and review workbooks, and pass local validation.
2. Next: preview and validate all three converted families on web and Android against the unpublished local candidate; resolve German editorial ambiguity, then extend the validated authoring pattern across the remaining catalog.
3. Complete Android production/guest parity and real-device accessibility/lifecycle acceptance. Address ordinary registration/deletion acceptance and operational gates alongside the content stream where they do not block independent work.
4. Publish reviewed converted content and provision any required clean schema only under the applicable release authorization. Verify hosted checks and deployed behavior separately from Git push.
5. Expand reviewed coverage and run the representative pilot/delayed follow-up before optional feature expansion.

ChatGPT can prepare topic content, distractors, bilingual copy and editorial notes using [the authoring guide](../content/practice-formats.md). Codex integrates validated drafts into shared contracts/backend and both clients. Coordinate against the same topic and current Git state; do not restart repository/design analysis or independently invent grading/mastery policy.

## Source-of-truth order

Owner decisions → product blueprint/accepted ADRs → this current milestone table → latest root `PROGRESS.md` checkpoint for the exact implementation action and verification → linked evidence. Older checkpoint entries and archived gate snapshots record history, not competing current priorities. [Continuation instructions](continuation.md) use this order. This document summarizes existing evidence; no production, provider or device state was changed or reverified during alignment.

# Backlog reassessment - 25 October 2026

Owner-approved disposition, executed 25 October 2026. Based on the owner directions in AGENTS.md, the latest PROGRESS
entries, blueprint sections 1/3/23/24 and the milestone gate record. This is a
planning/documentation review, not fresh runtime or release acceptance.

## Inventory and limits

- Monorepo: one open issue, [#20](https://github.com/abulhawa/german-master-2/issues/20); no open PRs.
- Preserved web repo: 21 open issues, nine overlapping epic pairs, seven open PRs,
  five old phase milestones. Every open issue/PR is covered below.
- Preserved Android repo: no open issues or PRs returned.
- GitHub Project-board drafts and status fields could not be inspected: CLI lacks
  read:project permission; the connected GitHub tool has no Project capability.
  Do not claim a complete board inventory or change credentials for this audit.
- Closed issue history is not a fresh work queue. Original repositories remain
  preserved references; this review does not authorize editing or archiving them.

## Product decision

Keep the B1-B2 weakness-practice companion: Home, Practice, Progress, Topics;
optional sign-in; offline grammar reference; shared web/native behavior; drafts,
explicit guest attachment and account isolation. Google/Microsoft login is an
owner-authorized exception to the feature freeze and remains unfinished.

M0/M1 stay closed under their accepted scope. M2 is the sole active milestone;
M3-M5 contain implemented work with remaining acceptance, M6 is queued, M7 deferred.
Do not convert implemented source into a claim that a milestone or release passed.

Do not revive the A1-C2 verb database roadmap, Leitner/schema rewrite, daily goals,
streak pressure, sequential quests, teacher product, certificates or premium tiers
as prerequisites for fixing the present product. Future usefulness is not ruled
out, but an old issue is not authorization to build it now.

## Proposed disposition of every legacy web issue

All numbers in this table refer to [abulhawa/german-master](https://github.com/abulhawa/german-master/issues), not the monorepo.
Retire means close as superseded/not planned, with a link to this decision after
owner approval; it never means the old acceptance criteria were completed.

| Issues | Old work | Decision | What survives |
| --- | --- | --- | --- |
| #3, #16 | Analytics baseline | Consolidate and retire old pair | GM-020: minimal deduplicated learning/reliability events; privacy and clear denominators. No new analytics dashboard requirement. |
| #4 | Leitner spaced repetition engine | Retire as superseded | GM-008 through GM-011 and M2 already own authoritative grading/scheduling/replay. Do not add a second engine, Drizzle extension or queue-regeneration job. |
| #5, #18 | Adaptive UI, goals, heatmap, streaks | Retire old trajectory | Existing mixed practice and useful Progress; preserve simple controls. No new goals/streak widgets. |
| #6, #19 | Focus verbs, exports, weekly digest | Consolidate and retire old pair | GM-013 target-level Progress. Account data export remains required; filtered study exports and email coaching are optional, deferred ideas. |
| #7, #20 | Audio comprehension/TTS drills | Remove from active backlog; park one idea for M7 | Consider only after pilot evidence and accessible audio/content review. Not a release blocker. |
| #8, #21 | Pronunciation scoring | Remove from active backlog; park one idea for M7 | Speech assessment is a current non-goal. Old unspecified industry benchmark is not an actionable acceptance gate. |
| #9, #22 | Contextual verb quests/cloze/insights | Retire as superseded | Existing target-based mixed exercises, explanations and grammar reference; GM-012/014/022. No sequential quest-unlock system. |
| #11, #24 | Teacher dashboards/classrooms | Drop from current product roadmap | Different audience and product; no classroom/roster/assignment implementation planned. |
| #13, #26 | Premium plans/paywalls/Stripe | Drop as a delivery commitment | Monetization may be reconsidered after usefulness; no billing integration, trials or entitlement work now. |
| #14, #27 | Certificates, badges, LinkedIn/Europass | Drop from current product roadmap | No certified proficiency claims or credential-validation service. |
| #67 | Refine remaining POS templates | Retire legacy implementation task | Preserve relevant content defects as specific target/revision fixes under GM-022. Do not resume bulk legacy template generation. |
| #77 | Regular/irregular Partizip II filters | Defer, remove from active work | Only reconsider with a demonstrated learner need in existing Topics/practice; not a prerequisite for release. |

This accounts for all 21 open legacy issues. Nine paired epics overlap; their
bodies are not necessarily identical and their prior completion claims are not
transferred to GM2.

## Legacy PRs and phase milestones

| PR in legacy web repo | Recommendation |
| --- | --- |
| #64 Streamline app for enrichment administration | Retire legacy application PR after review/approval. Preserve useful internal content-review concepts; no new learner/admin product track. |
| #206 source-map-js to 1.2.2 | Superseded for active monorepo: lockfile already 1.2.2. |
| #205 brace-expansion to 2.1.7 | Superseded for active monorepo: lockfile already 2.1.7. |
| #204 moment to 2.31.0 | Superseded for active monorepo: lockfile already 2.31.0. |
| #203 undici to 7.30.0 | Superseded for active monorepo: lockfile already 7.30.0. |
| #202 ip-address to 10.7.2 | Superseded for active monorepo: lockfile already 10.7.3. |
| #207 vite to 8.3.4 | Legacy PR is not mergeable policy for GM2. Current root lock is 8.0.16; assess any needed update in the active repo separately. No security or upgrade-readiness conclusion from version numbers alone. |

The five legacy Phase 0-4 milestones are an obsolete planning structure for GM2.
Recommend retiring them with the associated issues, preserving history. Do not
merge any legacy PR merely to clear the backlog or modify the preserved repos in
this review.

## Disposition of all blueprint starter items

| ID | Decision now |
| --- | --- |
| GM-001 Preserve baseline | Completed under M0; remove from active queue, retain recovery evidence. |
| GM-002 Deployment/store identity audit | Baseline completed under M0. Keep current store/version and signing-continuity checks only at a release boundary; owner holds signing. |
| GM-003 Monorepo bootstrap | Completed; do not reopen repository/framework selection. |
| GM-004 Versioned contracts | Foundation completed; maintenance when behavior changes. |
| GM-005 Shared tokens | Foundation implemented and updated with approved design; remaining owner UX acceptance is distinct. |
| GM-006 New database baseline | Implemented/restored; no new database, reset or schema rebuild needed. |
| GM-007 Initial content review | Completed under accepted AI editorial scope; do not restore an independent-human prerequisite. |
| GM-008 Deterministic grading | Implemented; retain M2 conformance/operational acceptance, no rewrite. |
| GM-009 Mastery/scheduling reducer | Implemented; retain M2 evidence/replay acceptance, no client policy fork. |
| GM-010 Adaptive sessions | Implemented; retain remaining representative selection-cost/load acceptance. |
| GM-011 Idempotent attempt commit | Implemented; retain independent-connection contention/retry acceptance in M2. |
| GM-012 Web journey | Implemented; verify remaining actual account/content acceptance, address concrete UX defects. |
| GM-013 Progress/detail | Implemented; confirm real account outcomes and useful next action, no dashboard expansion. |
| GM-014 Topics/focused practice | Implemented; preserve public access and shared engine policy. |
| GM-015 Android API integration | Implemented in learner host; remaining live native account/API acceptance. |
| GM-016 Native core screens | Implemented/redesigned; owner UX and remaining device acceptance stay open. TalkBack scope remains accepted. |
| GM-017 Offline session preparation | Implemented; real disconnected/restart acceptance remains. |
| GM-018 Cross-device reconciliation | Implemented mechanisms; real two-device/expired-auth/account-switch evidence remains. |
| GM-019 Privacy/export/sign-out | Partially accepted; inspect export artifact, verify isolation and native lifecycle. Identity deletion remains disabled and separately gated. |
| GM-020 Useful telemetry | Keep minimal, queued. Avoid new dashboard/vendor or event volume without a question it answers. |
| GM-021 Release pipeline | Existing pipeline/deployment implemented; resolve demonstrated check failures and retain usable rollback acceptance. Passing build is not release approval. |
| GM-022 Reviewed pilot content | Queued for M6: existing 65 targets/125 revisions vs blueprint goal about 120 targets with two variants. Do not use arbitrary corpus-size targets to delay fixing current flows. |
| GM-023 Learner pilot | Keep queued; participant feedback and delayed retention cannot be replaced by fixture tests. |
| GM-024 Personal ingestion | Deferred M7; no import pipeline now. |

## Issue #20: keep, rewrite, narrow

[Monorepo #20](https://github.com/abulhawa/german-master-2/issues/20) is the one
remaining tracker. Its 503 incident, inactive-backend production binding,
preview-only cutover plan and uncompleted recovery checkboxes are historical.
The 24 October authorized recovery/cutover has recorded evidence. Do not repeat
those steps, republish content, or claim the issue is done.

Proposed title: **GM2 release readiness: finish account/offline acceptance and owner UX review**.

Proposed remaining checklist:

- [ ] Close M2's bounded independent-connection contention, selection-cost and operational evidence gaps, reusing existing evidence.
- [ ] Finish the explicitly requested Google/Microsoft provider setup and real web/native callbacks, keeping email/password and guest practice.
- [ ] Inspect an actual account export; verify second-account isolation, real disconnect/reconnect, native account/content lifecycle and two-device/expired-auth behavior in the appropriate sequential milestones.
- [ ] Resolve demonstrated user-facing parity/reliability defects and relevant hosted CI failures. Do not turn known test stalls into an open-ended testing program.
- [ ] Complete owner UX review of the installed preview; retain Android release hold until explicit approval.
- [ ] Complete separately authorized deletion/operational acceptance before enabling it; do not remove privacy obligations as obsolete.
- [ ] Complete queued content coverage and small learner pilot when prior milestones close.

Completed history to retain in the rewritten issue: clean backend recovery and
production cutover, optional-sign-in practice, shared navigation/feedback,
mobile redesign, Settings/themes, offline grammar reference, provider-button/Home
UI and preview installation. Deployment/device evidence remains dated; this audit
did not freshly check the live website or social login.

## Recommended work order

1. Finish the current bounded social-provider setup and concrete release blockers
   as owner-requested corrections; do not open another feature lane.
2. Close M2's exact remaining gate, then finish M3-M5 acceptance sequentially.
3. Owner UX/release decision and M6 pilot/coverage; retain release boundaries.
4. Reconsider deferred features only when learner evidence justifies them.

Remove from the active queue: completed bootstrap, another hard reset/backend
migration, separate welcome/Try paths, signup promotions, old engine plans,
repeated broad design exploration and a reopened TalkBack campaign. Preserve
actual privacy/offline/reliability acceptance and maintenance obligations.

## Execution - owner authorized 25 October 2026

Closed all 21 listed legacy web issues as not planned, all seven listed PRs
without merging, and all five legacy phase milestones. No open issues or PRs
remain in that repository after verification. No source branches or repository
history were deleted. Deferred concepts remain recorded here, not as active tasks.

The CLI lacked Project scope, but the signed-in browser displayed Product Roadmap
(project 2): nine legacy backlog cards and 13 historical Done cards. Closed the
obsolete project; the settings page now offers Re-open this project. Its workflows
are disabled and it is removed from the open-project list. No additional GitHub
permissions were granted and no uninspected project was changed.

Updated monorepo issue #20 to current release-readiness work and the browser
navigation defect. It remains open. Imported roadmap/sync documents are marked
historical to prevent recreating retired work. No new parallel backlog created.

# Continuing German Master recovery

Read root `AGENTS.md`, then the short current `PROGRESS.md`. Refresh [issue #20](https://github.com/abulhawa/german-master-2/issues/20), its comments and linked PRs before reporting status or implementing overlapping work. The [recovery runbook](recovery-execution-2026-10-23.md) describes the gated sequence. Old continuation instructions are retained in [history](history/continuation-through-2026-10-23.md); their paths are relative to their original location and their next actions are historical.

## Current work order

1. Recover the authenticated backend on the existing clean replacement. Preserve schema/content and use the four restricted connections. Do not create a project, reset data, republish content or transfer old accounts.
2. Use the [stable protected preview](https://german-master-v2-git-recovery-issue-2-c7c627-abulhawas-projects.vercel.app/) and [account acceptance record](recovery-account-acceptance-2026-10-24.md). CLI/MCP setup, isolated callbacks, owner signup/recovery handoffs, real profile/practice and downloaded-answer sync have evidence. Finish fresh login restoration and the remaining gaps; leave saved SMTP settings alone.
3. Verify practice/persistence, logout/login, offline, privacy and real web/native journeys. Distinguish API/fixture evidence from real browser/device acceptance.
4. Simplify the learner path and then run a small learner pilot before new features. Google and Microsoft sign-in are explicitly planned later; do not add OAuth providers during recovery.
5. A production cutover requires a separate explicit decision with verified environment bindings and rollback steps. Android release remains on owner hold. Preview authorization is not production or store-release approval.

## Handoff discipline

Inspect the current branch, dirty work and recent commits before editing. PRs #21/#22 were integrated on the pushed recovery branch without merging them on GitHub; refresh their hosted status and avoid recreating their changes. Normal tests stay offline. Preserve ignored private credentials, captures and recovery artifacts; public reports contain no personal machine paths, credentials or private inbox details.

Keep only M2 active. Update the current checkpoint with changes, evidence limits, blockers and the exact next action, then commit completed verified work locally before handoff. Push, production changes, destructive operations and releases keep their separate authorization boundaries. Do not reopen accepted TalkBack scope.

## Latest owner direction and live state — 24 October 2026

The owner subsequently authorized main/production deployment on the existing Vercel project/domains and accepted potential disruption. Production dpl_C2C7QCLbkvMY8kam7Xt2npjcKqmX at main 2f71508 is READY against the clean replacement. Homepage/guest-answer checks and unauthenticated API 401 checks passed. Earlier preview-only and future-cutover wording above is historical. See the latest PROGRESS.md section for evidence limits. Android release and identity deletion remain held. Owner requested stopping further allowance use for studies; do not continue discretionary checks without a new request.

## Latest direction — practice without sign-in (24 October 2026)

Prioritize user-facing release blockers only. Owner authorized immediate main/production deployment: open directly to practice Home; remove welcome/Try/guest selection and signup promotions. Account-backed pages explain the account requirement and offer Sign in / Create account. Preserve drafts and explicit account attachment. See the latest PROGRESS.md entry and practice-presentation.md. Android parity remains pending; no Android release authorization.

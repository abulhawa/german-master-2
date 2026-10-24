# Continuing German Master recovery

Read root `AGENTS.md`, then the short current `PROGRESS.md`. Refresh [issue #20](https://github.com/abulhawa/german-master-2/issues/20), its comments and linked PRs before reporting status or implementing overlapping work. The [recovery runbook](recovery-execution-2026-10-23.md) describes the gated sequence. Old continuation instructions are retained in [history](history/continuation-through-2026-10-23.md); their paths are relative to their original location and their next actions are historical.

## Current work order

1. Recover the authenticated backend on the existing clean replacement. Preserve schema/content and use the four restricted connections. Do not create a project, reset data, republish content or transfer old accounts.
2. Complete the owner-authorized isolated Vercel preview and replacement signup/confirmation/recovery test using the owner's Outlook inbox. SMTP is saved/enabled; leave its settings alone. Verify actual delivery rather than repeatedly configuring it. Isolated callback configuration and actual signed-in lifecycle remain acceptance requirements.
3. Verify practice/persistence, logout/login, offline, privacy and real web/native journeys. Distinguish API/fixture evidence from real browser/device acceptance.
4. Simplify the learner path and then run a small learner pilot before new features. Google and Microsoft sign-in are explicitly planned later; do not add OAuth providers during recovery.
5. A production cutover requires a separate explicit decision with verified environment bindings and rollback steps. Android release remains on owner hold. Preview authorization is not production or store-release approval.

## Handoff discipline

Inspect the current branch, dirty work and recent commits before editing. PRs #21/#22 were locally integrated without merging them on GitHub; refresh their hosted status and avoid recreating their changes. Normal tests stay offline. Preserve ignored private credentials, captures and recovery artifacts; public reports contain no personal machine paths, credentials or private inbox details.

Keep only M2 active. Update the current checkpoint with changes, evidence limits, blockers and the exact next action, then commit completed verified work locally before handoff. Push, production changes, destructive operations and releases keep their separate authorization boundaries. Do not reopen accepted TalkBack scope.

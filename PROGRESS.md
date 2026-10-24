# German Master 2.0 current checkpoint

Updated 24 October 2026. Recovery tracking: [issue #20](https://github.com/abulhawa/german-master-2/issues/20). Earlier checkpoints are preserved in [history](docs/operations/history/progress-through-2026-10-23.md); their paths are relative to repository root.

## Priority and authorization

Recover and verify the existing clean replacement, then simplify the learner experience. Keep the monorepo, contracts, engine, schema, published content, identity and offline guarantees. M2 is the sole active milestone. New features remain frozen; Google/Microsoft sign-in is queued for later. Android release remains blocked until explicit owner UX approval.

Owner authorized isolated Vercel previews and the owner's Outlook inbox for signup/recovery acceptance. The owner entered passwords and completed email handoffs personally. No permission to change production bindings, deploy production, retire the old project, delete existing accounts, change billing or release Android. Leave saved SMTP settings alone.

## Verified progress

- Vercel CLI 65.2.0 is installed user-wide and authenticated. Existing Vercel app guidance/tools are available. The shared official MCP endpoint is user-configured and OAuth-authenticated, with write confirmations configured; its own documentation search and authenticated team lookup pass after tool refresh. No duplicate standalone skills installed.
- Recovery branch `recovery/issue-20-local-20261024` is pushed through `b67e4c0`; PR #21/#22 changes are integrated there without merging those GitHub PRs or changing `main`. The branch also retains the Home language selector and persistent guest-prompt fixes.
- Protected replacement preview at [the stable test URL](https://german-master-v2-git-recovery-issue-2-c7c627-abulhawas-projects.vercel.app/) runs `b67e4c0` as deployment `dpl_GkJvnxzNVb3QpbZ3A93PSbGAd5GK`. Its seven branch variables select replacement-verification and the clean project, with only backend/Auth database credentials. Identity deletion is disabled; no admin secret is installed. Exact isolated signup/recovery callbacks were added to the replacement; existing production callback and SMTP were retained.
- First preview exposed signed-in requests blocked by hosting protection because fetch omitted cookies. `cc20b2c` uses same-origin hosting cookies while retaining verified bearer authority, current-session/subject guards, cross-origin cookie exclusion and refused redirects. Protection remains enabled. Browser Home/topics/profile and real practice now work; application-level unauthenticated catalog/profile return 401.
- Replacement inventory before acceptance: baseline 1, 29 RLS-enabled tables, 65 targets, 125 revisions and two already-published releases; security advisor clean and four restricted TLS connections pass. Do not reset or republish content. Acceptance now has one confirmed test user/profile, five allocated sessions, three test attempts and two partial-completion receipts. These are acceptance records, not learner-outcome evidence.
- Owner signup/confirmation and recovery handoffs returned to signed-in Home. Agent verified authoritative correct/incorrect submissions, a two-of-fifteen partial summary, Progress, feedback persistence across reload, duplicate-tab ownership handoff, downloaded-session local answer retention after reload and successful sync of its answer/completion with zero pending events. No full network-disconnection or physical Android claim.
- Export UI reports downloaded; file capture timed out and browser policy blocked download-list inspection. File contents/artifact validation remains open. Sign-out after sync passed; fresh password sign-in passed and restored the saved review checks, needs-practice target and account preferences.
- New acceptance findings: shared signed-in incorrect copy is being aligned to Not quite / Noch nicht ganz; downloaded availability stayed at zero until reload when issuance was slightly ahead of the cached check. A bounded worker reproduced stale time-boundary status and implemented guarded local issuance/expiry refresh with two deterministic regressions. Engine validity/hash rules, account ownership and network retry policies are unchanged. Final combined checks pass. The refinements are deployed; the browser loaded the matching final deployment bundle and a fresh two-session download became available without reload while retaining the earlier saved session.

## Verification and next action

Final combined verification at `b67e4c0`: root generated/web/backend type checks, 198 backend tests, 431 web tests, 20 HTTP tests and production learner/API build pass. The two deterministic boundary regressions and 62 focused copy/journey tests also pass; staged secret scan has no findings. No Android source changed or native suite repeated.

Production remains deployment `dpl_5iYh7e2NDy5KENpEvRsxozGUhByd` at `a9f0c3ae`. Its ten production environment entries were compared before/after preview setup and were unchanged. No live API-health repair or production cutover is claimed.

Exact next action: close the remaining acceptance gaps: export artifact, actual disconnected/reconnect behaviour, second-account boundaries, real native journey and separately authorized deletion. Final signed-in 390px override did not apply to the claimed browser tab, so no new mobile viewport acceptance is claimed. Present a rollback-aware production cutover proposal only after relevant gates and explicit owner acceptance; do not cut over automatically.

Evidence: [account acceptance](docs/operations/recovery-account-acceptance-2026-10-24.md), [earlier verification](docs/operations/recovery-verification-2026-10-24.md), [continuation](docs/operations/continuation.md).

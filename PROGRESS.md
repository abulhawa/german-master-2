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

## Production cutover — 24 October 2026

Owner explicitly authorized integrating recovery into main, changing the existing production backend binding, and deploying immediately; the site has no active learners. This supersedes the earlier preview-only boundary above. Android release hold remains.

Main now includes the recovery fixes through 2f71508. Existing Vercel project deployed production dpl_C2C7QCLbkvMY8kam7Xt2npjcKqmX from that main revision, READY, with germanmaster.qortxai.com and gvm.qortxai.com assigned. Production configuration selects the clean replacement, GM_ENVIRONMENT=production, its publishable key, all four restricted database connections and its server-only admin key. Both identity-deletion switches remain disabled. No schema/content reset, SMTP change, account deletion, DNS change or Android release occurred. Previous deployment/configuration metadata are retained privately; the prior deployment points to an inactive backend and is not a working account-service rollback.

Verification: fresh root type checks and production build passed; all four restricted TLS/read-only database checks passed. Both live domains return homepage 200 and unauthenticated catalog/profile 401 authentication_required, replacing the previous 503. Real production browser guest practice accepted Berufe and rendered Correct. Signed-in production login was not repeated; earlier signup/recovery/practice/sync acceptance was on the same replacement through preview. Two local test reruns stalled and were stopped; no fresh full-suite pass is claimed. Prior exact application revision passed 198 backend, 431 web and 20 HTTP tests. Fresh main GitHub CI was still running at handoff.

Owner requested stopping further allowance consumption to preserve studies. Stop discretionary testing/investigation. On a future explicit resume, inspect hosted CI results first; retain export/disconnect/second-account/native acceptance gaps and disabled deletion. Production deployment is complete; M2 is not closed.

Minimal favicon follow-up: added a conventional favicon.ico wrapping the existing 64px PNG without changing its artwork or page UI. Learner production build and emitted ICO integrity check passed. Pushing main deploys it to the existing production domains; Vercel dashboard refresh timing remains outside application control.

## Guest-mode navigation hotfix (24 October 2026)

After production cutover, a critical visitor flow was reported: entering "Try German Master" left no route to the original welcome/Home screen, and Sign in / Create account disappeared from guest Home unless an unattached local answer made the save prompt visible. This affected returning guests, skipped-only practice, and completed/dismissed prompts.

Branch `fix/guest-exit-and-sign-in-20261024` changes guest-only presentation: direct Sign in on focused practice, permanent sign-in and registration from guest Home, explicit return to the welcome page with a resumable unfinished session, and account access when guest storage is damaged. Drafts, graded attempts, optional attachment and backend rules remain untouched. Focused regressions cover no-answer navigation, draft preservation, completed/dismissed prompts, reload and damaged guest storage. Responsive header actions wrap.

Verification/deployment boundary: GitHub PR #23 CI and review must pass before merging to main, which automatically deploys production. No database or Android release changes. Next action: examine PR #23 checks; merge the verified fix; confirm live guest exit, sign-in entry and resumed draft on germanmaster.qortxai.com.

## Practice access correction — 24 October 2026

Owner changed product direction: release blockers only, user-facing issues first. Practice opens without sign-in or a separate welcome/Try/guest gate. Account-dependent pages offer contextual Sign in / Create account and explain the requirement. No account promotions during/after practice. Owner explicitly authorized immediate main/production deployment of this correction.

Implemented on web: direct practice Home, contextual Progress/Downloads/Account gates, return to an active draft, no completion signup prompt, and English/German copy. Local storage schemas, grading, account isolation and explicit transfer remain intact. Signed-out content remains the existing five-question reviewed pack; full catalog access is not added by this patch. Android parity is still pending under the release hold.

Validation: root generated/web/backend type checks and production build passed; 70 focused mounted practice/auth/attachment/signed-in journey regressions passed. A fixture-configured production bundle passed desktop 1280px and mobile 390px browser checks for direct Home, all three contextual account pages, canceled sign-in with retained draft, grading and promotion-free summary; no horizontal overflow or page errors. This uses synthetic local configuration, not live account acceptance. Broader local test runs stalled and were stopped; a dependency reinstall also hit a Windows file lock and was restored with an offline install. No fresh full-suite pass is claimed. Existing storage/ownership tests remain intact.

Deploy this commit through main on the existing german-master-v2 production project, then verify the live unsigned-in browser flow and hosted CI. Existing account-lifecycle/native acceptance gaps remain open.

Production result: commit `200f9b1` is deployed READY as
`dpl_7H18XttVS66kCGMJnX3Ks4ChvT1A` on the existing production domains.
Actual public-site browser checks passed at 1280px and 390px: direct Home,
Progress/Downloads/Account messages and both auth options, cancel sign-in with
retained choice draft, correct grading, and completion without signup promotion.
No page errors or horizontal overflow. No real account was created or used.
Hosted accessibility acceptance and the web build/browser job passed; full web/
backend CI results were still pending when this entry was written. Updated the
existing tab-return helper to use Start practice and the choice-based draft.
Exact next action: inspect the remaining hosted CI results; then continue only
user-facing release blockers. Android parity and prior lifecycle gaps stay open.

## Web / Android parity correction — 24 October 2026

Standing owner direction: always keep web and native Android user-facing
behavior in sync, completing both clients in the same slice. Recorded in root
AGENTS.md and the shared practice presentation contract. A release hold affects
publication, not implementation parity; unavoidable differences must be named
as release blockers.

Android now follows the web practice-access change: direct practice Home,
Start/Continue/Repeat, contextual Progress/Downloads/Account pages with the
account-required message and Sign in / Create account, and no practice Home or
completion account promotions. Close and system Back retain the choice draft;
auth cancellation returns to practice Home and can resume it. Completion has
Finish and the same counts. Account-entry wording no longer calls this guest
mode, and a verified sign-in clears stale auth-entry mode.

Also corrected a pre-existing backend mismatch: Android release configuration
and signup/recovery callback eligibility now select the clean replacement used
by production web, instead of the retired project. Canonical HTTPS callbacks,
publishable-key validation and verified subject ownership remain in force.
No hosted Auth, SMTP, database or content settings changed. Synthetic offline
release-configuration checks accept the clean project and refuse the retired
project; this is configuration validation, not a live signup/recovery pass.

Verification after the final backend guard correction: five mounted Robolectric
practice-access regressions (including system Back and auth cancellation), four
provider and four signed-in shell regressions passed (13 total). Debug assembly
and lint passed with zero lint errors (19 existing warnings). Earlier web behavior
has live desktop/mobile acceptance and hosted Web checks passed at 53df03c.
There is no connected device or configured emulator; no native device runtime
or renewed TalkBack acceptance is claimed. Android remains unpublished and the
owner's explicit UX/release hold remains. Full native account-lifecycle and
existing export/disconnect/second-account acceptance gaps are still open.

Exact next action: keep both clients aligned for the next user-facing blocker;
complete real native UX/account acceptance on an available device before asking
for Android release approval. Do not restart the stopped screenshot review or
accepted TalkBack scope without a new request or demonstrated critical defect.

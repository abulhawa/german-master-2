# German Master 2.0 current checkpoint

Updated 24 October 2026. Tracking: [issue #20](https://github.com/abulhawa/german-master-2/issues/20).
Older milestone and deployment entries are preserved in the [historical progress log](docs/operations/history/progress-through-2026-10-23.md); they are snapshots, not current status. Historical paths in that log are relative to the repository root.

## Priority and boundaries

Recover the authenticated backend against the existing clean replacement, then simplify the learner experience. Keep the monorepo, contracts, learning engine, schema, published content and offline guarantees. New features are frozen. M2 remains the sole active milestone; no gate is closed by this checkpoint. Android release remains blocked pending explicit owner UX approval. Production cutover and old-project retirement still require separate decisions.

Owner authorized an isolated Vercel test deployment on 24 October and use of the owner's Outlook inbox for signup/recovery test emails. This does not authorize changing production bindings, production deployment, deleting existing accounts or a store release. Leave saved SMTP settings unchanged. Google and Microsoft sign-in are planned later, after recovery; do not implement them during this slice.

## Current evidence

- GitHub PRs [#21](https://github.com/abulhawa/german-master-2/pull/21) and [#22](https://github.com/abulhawa/german-master-2/pull/22) remain open/unmerged; all reported checks pass at heads `6e500cb` and `a76068f`. Both are integrated only on the local recovery branch. Local integration is not a hosted PR merge or production deployment.
- Replacement project is ACTIVE_HEALTHY. Fresh read-only inventory: baseline migration `20261010172945 / 0001_initial_schema`, 29 application tables all RLS-enabled, 65 targets, 125 revisions, zero Auth users/profiles/attempts before lifecycle testing. Security advisor reports no findings. Existing published releases must not be republished from stale historical notes.
- All four restricted replacement login roles pass read-only queries over certificate-verified TLS, without superuser/BYPASSRLS. Built loopback replacement runtime returns 401/authentication_required for both `/v2/profile` and `/v2/catalog`; this is not signed-in acceptance.
- Dashboard confirms custom SMTP enabled with saved Resend configuration. Public Auth settings confirm email provider/signup enabled and email confirmation required. No SMTP settings changed. Redirect configuration currently includes only the production origin; isolated callback setup remains necessary.
- Production deployment `dpl_5iYh7e2NDy5KENpEvRsxozGUhByd` is READY at `a9f0c3ae`. Production still has the issue-reported old-project binding; live API behavior has not been rechecked in this slice. READY is not backend-health evidence.
- Guest starter changes suppress the repeated account promotion, label the five-question B1 starter honestly, and remove the nonfunctional setup action. Review caught the lost route to language selection; a concise Home language selector restores it. Regression checks cover locale persistence, prompt dismissal across remount and a renewed optional prompt after a fresh repeat. Guest attempts remain preserved.

## Exact next action

Finish the authorized isolated preview configuration and verify actual signup/confirmation, sign-in, recovery, practice/persistence, logout/login, offline and privacy behavior against the replacement only. Capture acceptance evidence and remaining web/device limitations. Present a concrete rollback-aware production cutover proposal only after those checks; do not cut over automatically.

Browser verification also passes the completion/prompt/language/reload flow at desktop and 390px without horizontal overflow. Guest correction is committed as `c22398f`. Details: [24 October verification](docs/operations/recovery-verification-2026-10-24.md).

For execution details see [continuation](docs/operations/continuation.md) and [recovery runbook](docs/operations/recovery-execution-2026-10-23.md). Local verification passes: offline root dependency install (zero reported vulnerabilities), generated/web/backend type checks, 198 backend tests, 428 web tests, 20 HTTP integration tests and production learner/API build. No Android source changed; native suites were not repeated. One bounded worker reviewed and repaired the guest language/prompt regressions; no further workers were dispatched after the shared five-hour remaining allowance crossed the orchestration threshold. No paid usage or reset credit used. The isolated preview is waiting for owner completion of the Vercel CLI login; no preview or test email has been created/sent yet.

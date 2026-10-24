# Recovery integration and verification - 24 October 2026

Tracking: [issue #20](https://github.com/abulhawa/german-master-2/issues/20). Local integration and read-only replacement-host checks do not establish completed recovery or production acceptance.

## Integrated work

- PR #21 head `6e500cb`: explicit authorized-project resolution, fail-closed replacement mode, preserved scoped connection checks, current README and staged runbook.
- PR #22 head `a76068f`: accurate five-question B1 wording, explicit repeat action and persistent suppression of a second account promotion after Finish.
- Review caught a regression: removing Change setup also removed the route to interface language. Commit `c22398f` restores a labelled Home language selector using existing localized options and persistence. Tests cover locale persistence, dismissal across remount and a renewed optional offer after a fresh repeat. Guest attempt history, grading and attachment consent remain preserved.
- Both GitHub PRs remain open/unmerged; all reported checks pass at the heads above. Local integration is not hosted merge, production deployment or owner acceptance.

## Evidence and limits

| Boundary | Evidence |
| --- | --- |
| Replacement health | ACTIVE_HEALTHY; security advisor reports no findings |
| Read-only hosted inventory | Single migration `20261010172945 / 0001_initial_schema`; all 29 application tables have RLS; 65 targets, 125 revisions; zero Auth users/profiles/attempts before lifecycle testing |
| Restricted connections | All four gm_app login roles pass read-only baseline/private-view queries over verified TLS; no superuser/BYPASSRLS |
| API composition | Built replacement runtime returns 401/authentication_required for unauthenticated profile and catalog; signed-in writes remain untested |
| Auth settings | Email provider/signup enabled, confirmation required; dashboard confirms saved custom SMTP enabled with Resend. No SMTP mutation or delivery claim |
| Redirects | Only production-origin allowlist found; isolated callback still required |
| Offline checks | Root dependency installation (zero reported vulnerabilities), generated/web/backend types, 198 backend tests, 428 web tests, 20 HTTP tests and production learner/API build pass |
| Guest browser | First answer and plain feedback, completion with four skips, Finish without duplicate promotion, Home language selection and German state after reload. Desktop/390px inspected; no horizontal overflow at 390px. Private captures retained |
| Native | No Android source change or repeated native suite; no signed-in physical-device or renewed TalkBack acceptance claim |
| Production | READY deployment `dpl_5iYh7e2NDy5KENpEvRsxozGUhByd` at `a9f0c3ae`; no binding change or fresh live HTTP-health claim |

## Authorized next step

Owner authorized an isolated Vercel test deployment and the owner's Outlook inbox for signup/recovery tests. Keep production unchanged. The connector cannot supply the isolated environment variables, so the CLI is the fallback. CLI 65.2.0 is installed privately; owner completion of device login is required. No preview, callback mutation, signup, recovery email or learner-data mutation has occurred in this slice.

After login, inspect the exact existing team/project and preview environment. Configure replacement-only scoped secrets privately, ensure browser/API use the same secure preview origin, and preserve deployment protection. Verify actual account lifecycle, practice/persistence, logout/login, offline and privacy with separately controlled deletion. Add only the exact isolated callback required, retaining existing callbacks. Do not merge hosted PRs or cut over production automatically.

Leave the saved SMTP settings alone, as the owner clarified. Google and Microsoft sign-in are planned later; they are outside the active recovery feature freeze.

## Durable handoff

Older root progress and continuation logs are preserved in `docs/operations/history/`; content equality against the prior commit was checked with line endings normalized. Root `PROGRESS.md` is now the short current snapshot. Historical progress paths are relative to repository root; continuation paths are relative to their original operations directory. No prior evidence was discarded.

One bounded worker reviewed/repaired the guest changes; no nested/remote delegation. No further discretionary workers after five-hour remaining allowance crossed the personal threshold. Usage attribution is approximate, with no enforced task cap claimed. No paid usage, reset credits, live AI/Groq calls, content publication or store release.

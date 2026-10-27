# Release readiness assessment — 27 October 2026

## Latest: owner-authorized activation for live verification

Owner completed both secret fields and approved callback/provider configuration.
Google and Microsoft/Azure are enabled in Supabase, the GM2 callback is saved in
both existing OAuth clients, and native return URLs are saved. Owner then explicitly
requested commit/push and will verify signup/sign-in on the live site. Web/native
social buttons are enabled in this candidate; Work stays disabled. This supersedes
the pending-credential and disabled-social statements in historical sections below.

Focused activation validation: root generated/type checks, 25 web provider tests,
10 native access tests and configured preview assembly pass. Production build is
checked before push. Physical draft preservation through cancelled auth, correct
feedback and full process restart passed on the Pixel. Live OAuth exchange, actual
export/account isolation and full lifecycle acceptance remain unclaimed. No store
release/tag is authorized; retain Android owner UX hold. Main push is authorized
for the owner's live verification, including its existing automatic web deployment.

## Follow-up execution: local checks green, provider acceptance in progress

This update supersedes the earlier failing local results below. Root `npm test`
now passes 199 backend, 458 web and 20 HTTP tests. Root generated/type checks and
production build pass. Offline-shell and accessibility browser checks pass.
Native full tests: 212 total, zero failures/errors, one existing skip; debug lint,
assembly and preview build pass. A native nanosecond timestamp crash on Check
answer was corrected to the shared millisecond format and covered by regression.
Old native starter/exit/count/duplicate-topic test assumptions were reconciled
without weakening draft assertions. No owner UX acceptance is inferred.

Explicit owner approval covers the GM2 callback in the existing Google/Microsoft
apps, exact native production/preview redirects, and enabling configured providers.
Google's GM2 callback is saved and its provider is enabled with the owner-entered
replacement credential. Microsoft app callback is saved; owner secret entry is
pending. Supabase now allows `com.germanverbmaster.android.auth://callback` and
`com.germanverbmaster.android.preview.auth://callback`. Do not copy credentials
into source, evidence or chat. The old exposed Google credential must be retired
through an owner-coordinated rotation after dependent legacy integrations are
accounted for; adding a replacement does not revoke the old credential.

Source social gates remain disabled until real handoff acceptance. Actual
Google/Microsoft account creation/return/cancellation, real privacy/export and
native lifecycle acceptance remain open. Phone now authorizes ADB; preview
replacement install preserves app data. A local-fixture build was initially
installed; a production-public-configured replacement was built and replacement-installed
successfully. Only the latter can substantiate live native Auth behavior. Device auto-lock still needs
an unlocked test interval. No push, production deployment, store upload or tag.

Verdict: **not ready to freeze/tag a release candidate**. M2 remains active.
Scope is the [finite stabilization checklist](stabilize-release-2026-10-26.md).
This is a bounded status/verification slice, not a renewed renovation.

## Source and observed hosted state

Inspected starting source: `dd8c87355d136b4a1752ae93b309985260787c35` on main,
initially clean. GitHub main matches that revision. Issue #20 and its comments
were refreshed; its 25 October provider/Work requirements are superseded by the
26 October release gates. No issue or external message was posted.

Read-only Vercel inspection found production deployment
`dpl_959ot7XCCsrCYknBdKEZrqfVqnKx` READY at that same revision in project
`german-master-v2`. Thus the earlier checkpoint's statement that the stabilization
source was not deployed is stale. READY is hosting/build state, not acceptance
of authentication, persistence or data integrity. No deployment was performed in
this assessment. Work and social authentication remain disabled in source.

The preceding production artifact `dpl_DjyDDVbxWZzFAPDEDWVKEwCAENa1` at
`b041ad00a06fef0b5c1f745cb8da542d703e146f` is listed as a rollback candidate.
It predates stabilization; its compatibility with current persisted work and
current environment bindings has NOT been verified. Do not blindly promote it.
Record a verified compatible web/API rollback pair before any next rollout.

## Completed in this slice

A deterministic native lifecycle fixture reproduced loss of an unfinished round
when navigating from Home to Settings and back Home. Native navigation discarded
any started round outside focused practice. It now clears only completed rounds;
explicit unfinished-round discard remains unchanged. This aligns with existing
web retention behavior. Regression checks compare exact session, index, draft
and word order through recreation/auth cancellation; web has a matching Settings
round-retention test. The fixture pins a bundled choice, so this is not proof of
all exercise formats or physical-device behavior. The earlier shuffled failure
was not independently traced for every possible selected exercise.

The guardrail manifest and runbook now point to the finite checklist and avoid
stale off-machine backup, signing-custody and content-republication requirements.
The manifest explicitly identifies its repository-only evidence scope. Private
credentials and live settings were not read or changed.

## Fresh verification

| Check | Result and scope |
|---|---|
| `npm run check` | Pass: generated contracts/design/grammar, web/backend TypeScript |
| `npm run build:production` | Pass: learner-product web + production API bundle |
| `npm run test:backend` through root `npm test` | 199/199 pass |
| Web unit suite through root `npm test` | 457/457 pass before the added parity regression |
| Updated guest-starter suite | 14/14 pass, including the new web parity regression |
| Separate `npm run test:web-http` | 20/20 pass |
| HTTP stage in complete `npm test` | 19/20 pass; cold-host offline resume intermittently fails at `offline.integration.test.tsx:46`, disabled Start downloaded practice; unresolved |
| Native access + practice tests | 20/20 pass after fix (10 access, 10 repository/practice), Robolectric |
| Native `lintDebug`, `assembleDebug` | Pass offline after fix |
| `tools/release/readiness.mjs` | Repository guardrails pass; not runtime approval |
| Physical device | ADB reports unauthorized; no device acceptance or replacement installation |

The root full command is **not green** because its final HTTP stage failed.
A passing isolated rerun does not clear the intermittent failure. Dependencies
were already present; no fresh `npm ci` or dependency update occurred.

## Current hosted CI findings

At the inspected main revision, CodeQL and repository safety pass. The following
hosted failures were inspected, not rerun or declared harmless:

- [Web checks](https://github.com/abulhawa/german-master-2/actions/runs/40247638355):
  browser offline-shell verification times out waiting for the Account button at
  `apps/web/scripts/verify-offline-shell.mjs:60`.
- [Web accessibility](https://github.com/abulhawa/german-master-2/actions/runs/40247638353):
  Home logo link has a 32px target, below the existing 44px acceptance threshold.
- [Android checks](https://github.com/abulhawa/german-master-2/actions/runs/40247638383):
  11 runtime failures across grammar, screenshot, entry, focus, access and topics
  tests. The two focused local classes passing does not clear this broader suite.
  Screenshot review remains paused; preserve artifacts and triage functional
  failures against current behavior without reopening visual redesign/TalkBack.

## Remaining completion gates

| Gate | Exact remaining evidence |
|---|---|
| Reliable offline resume | Reproduce the cold-host intermittent enabled-state failure; fix cause or demonstrate the invalid harness assumption, preserving exact draft/event assertions |
| Browser/CI acceptance | Reconcile Account navigation in offline-shell test; address the 44px Home target; run affected browser checks against the candidate |
| Native functional acceptance | Reconcile remaining hosted functional failures; test fixed round retention on an authorized device; broader acceptance remains open |
| Real lifecycle/privacy | Approved production-equivalent test accounts: signup, failed/cancelled auth, zero/partial save, Continue, restart, offline/reconnect, sync, inspected export, logout/second account, concurrent tabs and retries on both clients |
| Content | Retain prior model-assisted bundled B1/B2 spot-review evidence; do not equate it with current live inventory or independent human/learner review; resolve only serious demonstrated errors |
| Release promise | Same-client adopted draft resume exists. Cross-device unfinished-draft resume is unavailable; verify and communicate this limitation without implying confirmed-progress sync restores drafts |
| Candidate/rollback | Exact passing source + artifacts, compatible rollback pair and environment binding review; no tag until these gates pass |
| Publication | Explicit candidate web rollout authorization; Android owner UX hold remains separate, plus live Play maximum versionCode before upload |
| Pilot | Small real-learner pilot after stable web release; no participants or pilot evidence yet |

Next action: resolve the reproducible/intermittent offline and browser acceptance
findings, then run the bounded lifecycle matrix against the resulting exact
candidate. Obtain device authorization for physical acceptance. Retain Work/social
gates and the Android publication hold. Do not add features or republish content.

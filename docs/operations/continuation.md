# Continuing German Master recovery

## Current work order — 26 October 2026

Follow the current PROGRESS.md front section and
[stabilization checklist](stabilize-release-2026-10-26.md). Retain architecture,
complete the three core integrity fixes and bounded lifecycle acceptance. Work
content and social authentication stay hidden until verified; activation is not a
core release gate. Web release readiness is independent of Android Play approval.
No new features or repeated broad audits. The owner prioritizes job applications
after this checkpoint; do not resume discretionary app work without a new request.

All older work orders below are historical where superseded. Do not restart
recovery, provider activation or content publication from them.

Current planning: [25 October backlog reassessment](backlog-reassessment-2026-10-25.md).
Older recovery/cutover and OAuth-deferral instructions below are historical where
superseded by later owner decisions. M2 remains active; use the latest checkpoint.
Owner-approved legacy cleanup is applied: issues/PRs/phase milestones and the
old Product Roadmap project are closed. Browser access resolved the CLI Project
scope limitation. Use monorepo issue #20 for remaining work.

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

## Standing parity rule — 24 October 2026

Complete shared user-facing fixes on web and Android in the same slice. The
practice-access flow is now aligned in Android source and mounted native UI
tests, including contextual account gates and draft-preserving Back/cancel.
Android release/callback guards also target the clean production replacement.
See the latest PROGRESS.md evidence and limits. Do not defer client parity as an
optional follow-up or imply that source/test parity lifts the Android release
hold. No device/emulator acceptance or native live Auth acceptance is claimed.

## Latest device evidence — 24 October 2026

Owner-authorized Pixel 10 Pro installation of the aligned learnerPreview build
from 12e7230 succeeded, preserving existing preview data and the separate store
app. The app was opened and direct practice Home verified from the device UI
hierarchy. See PROGRESS.md. This is basic install/launch evidence, not complete
native account acceptance or permission to publish Android.

## Navigation blocker correction — 24 October 2026

Retain the renovated Home / Progress / Topics shell even signed out. Account
stays secondary; Downloads belongs within Account. Both client sources now
implement this, with public Topics for the existing bundled pack and contextual
account gates. See latest PROGRESS.md for tests and rollout. The Pixel
disconnected during the new build; do not conflate the earlier installed preview
with this navigation fix. Reconnect and verify the replacement install next.

## Latest Pixel result — 24 October 2026

The navigation fix and dark system-bar correction are now installed in the
preview on the Pixel. Physical Home / Topics / Progress/account-gate navigation
and preservation of the existing unfinished session passed. The app is open on
Home. The earlier disconnected-install blocker above is resolved. See latest
PROGRESS.md for final native checks and pending hosted Android CI/UX acceptance.

## Mobile-first design implementation — 24 October 2026

Owner accepted the new interactive direction and requested implementation while
away, without a phone. Native Android and mobile web now implement that direction;
see the latest PROGRESS.md entry and docs/product/mobile-design-2026-10-24.md.
Final local native tests/build/lint and web checks/build pass. The preview APK is
built with the existing production public configuration but has not been installed
or published. Native images are Robolectric renders, not device acceptance.
Next: owner review/device acceptance of this build, retaining the Android hold
and outstanding lifecycle gates. No push or deployment is authorized by this
implementation checkpoint alone.

## Settings access follow-up — 24 October 2026

Settings is now visibly labelled on both clients. Language/theme controls work
signed out, and native appearance persists independently of account data. The
corrected production-configured preview was replacement-installed on the Pixel.
See latest PROGRESS.md for tests and the auto-lock limit on physical verification.
Do not present the missing settings controls as a future optional feature.

## Grammar reference restoration — 24 October 2026

The owner's requested old grammar menu is implemented on web and Android using
the same bundled reference JSON, including safe lookup during practice. See the
latest PROGRESS.md and docs/product/grammar-reference.md for evidence and limits.
The new preview is built, but the phone was disconnected; web is not deployed.
Do not redo the import, invent new grammar explanations or report a live rollout.
Next: install/review the preview when connected, then follow release authorization.


## Social sign-in checkpoint — 25 October 2026

Owner now requests Google and Microsoft signup/login in addition to email.
Implementation is saved as work in progress on both clients; see latest PROGRESS.
Legacy Supabase: Google enabled, Azure disabled. Clean replacement: both disabled.
Existing Google OAuth web app was found in GermanVerbMaster Auth; its callbacks
still target legacy. Entra and Supabase dashboard require owner sign-in. Do not
publish the unfinished buttons or claim provider activation. Rerun final focused
checks (including recovery-error discrimination), native lint and UI checks before
committing the slice as complete. No credentials were copied or provider settings
changed. Owner requested shutdown at a user-input/allowance boundary; checkpoint
was saved at that boundary. Android release remains held.

25 October: login icons and explicit authentication Home exits are verified locally. See latest PROGRESS. Provider activation remains unfinished. Owner requested tighter verification and objected to allowance consumption; no further checks at this handoff.


## Navigation and legacy retirement - 25 October 2026

Legacy issues/PRs/phase milestones and Product Roadmap project 2 are closed with
owner approval; issue #20 now describes remaining GM2 readiness. Do not reopen
or recreate old tasks from imported roadmaps. Single practice Home with optional
sign-in remains the product; historical guest storage names preserve data only.
Web main/practice/auth navigation now uses shared browser history, with local
Back/Forward/draft evidence. Native auth Back follows prior pages. See newest
PROGRESS for verification and rollout boundaries. Do not claim these source
changes deployed or installed on the phone automatically.


## Simplification checkpoint - 25 October 2026

Owner requested wrap-up near 15% five-hour allowance. See the latest PROGRESS
entry: source changes are incomplete and must not be deployed. Finish account
round adoption/resume, safe attachment and navigation/migration checks before
rollout. New workplace content is an unpublished candidate. No full suites at
this checkpoint; preserve the Android publication hold.

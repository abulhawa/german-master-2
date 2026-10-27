# German Master 2.0 current checkpoint

## Bounded readiness fixes and provider setup - 27 October 2026

Owner requested finishing readiness and resuming Google/Microsoft activation.
The previous local-check failures below are superseded by these fresh results:
root `npm test` passes 199 backend + 458 web + 20 HTTP tests; generated/type checks
and production build pass. Offline-shell browser acceptance and the accessibility
browser audit pass after fixing the stale Account selector, repeated navigation
inside the offline test polling callback, and the web Home link's 44px target.
Native full suite: 212 tests, zero failures/errors, one existing skip. LintDebug,
assembleDebug and learnerPreview builds pass offline. Restored deterministic
starter fixtures and current explicit-exit/five-question assertions; duplicate
Grammar selectors now distinguish the topic row from the reference button.

A real native submission defect was found and fixed: nanosecond clock timestamps
violated the strict GuestAttempt contract. Native temporary-practice answers now
use canonical millisecond timestamps, with a high-resolution clock regression.
This matches web Date ISO serialization; no grading/content policy changed.
Automated renders are test artifacts, not renewed owner UX/TalkBack acceptance.

Provider setup is in progress under explicit owner configuration approval. The
existing Google web client and Microsoft GermanVerbMaster registration were found;
both had callbacks for older implementations. GM2 callbacks are now saved in both
registrations, Google is enabled in the clean Supabase project using the
owner-entered replacement secret, and exact production/preview Android return
URLs are saved. Microsoft secret entry and actual both-client provider handoffs
remain pending. Application social gates stay disabled until accepted handoffs;
no credential value is recorded in tracked evidence. See dated readiness report.

Phone is ADB-authorized. The updated preview was replacement-installed without
data clearing; the first build used local-fixture configuration and is not live
Auth acceptance. A second preview was built with the existing production public
configuration. The production-configured preview was then replacement-installed successfully.
Device functional acceptance remains in progress; auto-lock was observed. No store publication, push, deployment or release tag in this slice.

Next: finish owner credential handoff, prove Google/Microsoft sign-in/cancellation
and native round retention, then enable provider UI on both clients in the same
slice. Preserve round-only consent/account isolation and complete remaining real
lifecycle/export/rollback evidence before a candidate tag. Android publication
hold remains. Do not infer hosted CI success from these local checks.

## Release readiness reassessment - 27 October 2026

New owner request resumes bounded readiness work. **Not release-ready; M2 remains
active.** See [dated assessment](docs/operations/release-readiness-2026-10-27.md)
for the completion matrix, hosted CI failures, verification and exact next action.
This section supersedes stale deployment/evidence statements below.

Read-only Vercel inspection: production is READY at `dd8c873`, deployment
`dpl_959ot7XCCsrCYknBdKEZrqfVqnKx`; earlier not-deployed wording is stale.
No deployment, push, tag, content/database change or store operation in this slice.

Fixed demonstrated native unfinished-round loss on Settings to Home: only completed
rounds are cleared by non-practice navigation. Pinned lifecycle fixtures compare
exact round/draft/position; web equivalent retention regression passes. Native
access/practice 20/20, lintDebug and assembleDebug pass offline. This is Robolectric
and build evidence; physical device is ADB-unauthorized. Broader hosted native
functional failures remain open; do not equate this focused result with acceptance.

Fresh checks: generated/web/backend TypeScript and production build pass; backend
199/199 and web 457/457 pass; updated guest suite 14/14 passes. Separate HTTP run
passes 20/20, but full root npm test reproduces an offline cold-host failure
(19/20 HTTP). It remains unresolved. Hosted browser offline-shell Account timeout
and 32px Home-link accessibility target also remain open. Guardrail manifest and
runbook reconciled to current finite scope; passing guardrails is not readiness.

Next: resolve offline/browser failures, reconcile remaining native functional
failures, then complete real lifecycle/privacy/export/account-isolation acceptance
and compatible rollback verification on the exact candidate. No release tag until
candidate gates pass. Cross-device unfinished-draft resume remains unavailable;
Work/social stay disabled. Android owner UX/publication hold remains.


## Current priority — stabilize the core, 26 October 2026

Owner adopted the [finite stabilization plan](docs/operations/stabilize-release-2026-10-26.md).
This section supersedes the historical priorities below. Keep the architecture;
fix whole-round saving, consent/account isolation and native Continue. Work content
and Google/Microsoft remain implemented but hidden behind disabled release gates;
activation is no longer a prerequisite for the core web release. Android Play
approval remains separate. M2 remains the active milestone; no gates are declared
closed by this implementation. No new features, redesign or broad audit.

Implemented: additive v2 whole-round adoption pins existing published revisions,
grades answers authoritatively and records skips in one transaction. Zero-answer
drafts, position, partial word order and current feedback transfer into durable
account practice. Identical retries are safe; changed payloads and another account
cannot adopt the same round. Existing unfinished account rounds are preserved.
Web consent now carries a frozen snapshot, round id, initiating-tab token and bound
subject. Another tab's temporary work cannot become the automatically saved round.
Native Continue retains its session; exercises lacking starter suggestions use
the shared input renderer. A newly signed-in account does not inherit an empty
Practice route from another account.

Limits: this is a local stabilization change, not a release candidate or deployment.
Adopted drafts resume from durable storage on the same client. Restoring an
unfinished adopted round on another device is not implemented; do not promise
cross-device draft resume. Existing confirmed progress synchronization is separate.
Real signup/cancellation, disconnected/restart/sync, export artifact and second
account/device acceptance remain open. No live provider, production or content
settings changed; no database migration/reset or Android publication. Astra's
audit and immutable-payload finding are preserved in the stabilization plan;
implementation continued with Luna/high at owner request.

Verification: final web TypeScript and generated contract/design/grammar checks
pass; production learner build passes. The initial full web run passed 437 and
failed 20 old-flow assertions. Those tests were updated to the approved temporary
practice, explicit exits and five-question preferences; final combined affected
suite passes 101/101. Focused API adoption tests pass 16/16;
Android main and test Kotlin compile. Final backend/HTTP and native runtime results
are recorded below before handoff. No physical device acceptance is claimed.

Final broad checks: backend 198 passed / 1 failed (historical timezone/content
bounding test); HTTP integration 11 passed / 9 failed (Event constructor/test-realm
and inherited route/resume expectations, plus one offline enabled-state failure).
Navigation now dispatches window.Event to fix the demonstrated test-realm mismatch;
the HTTP suite has not been repeated after that correction. These failures are not
declared harmless or cleared. Native focused runtime run completed 10 tests with
5 failures; it exposed a typed-input fallback defect, corrected to shared text
fields, and stale fixed-starter/scroll assertions. Final compilation results below
qualify this correction. Debug assembly/lint and fresh device acceptance remain
unverified for this slice. Final native compileDebugKotlin passes after the typed
fallback and default-General gate corrections; test Kotlin also compiles. Three
targeted runtime tests still fail on scroll-query assumptions; runtime acceptance
is not claimed. These limits prevent calling the change release-ready.

Content: model-assisted source spot review of the bundled general B1/B2 prompts,
accepted answers and all 11 article MCQs found no demonstrated two-answer MCQ
defect in that reviewed set. This is not independent human review or proof of the
current live inventory. The known unpublished Work ambiguity stays deferred with
the disabled feature; no immutable published revision was edited.

Exact next action: finish the bounded lifecycle acceptance against the committed
candidate, including cross-device draft behavior as an explicit limitation or
critical defect according to the promised release behavior. Only reproducible
critical failures, privacy/data-integrity problems and serious content errors block
the candidate; lesser issues go after release. Tag only after candidate gates pass.
Web release and the later small real-learner pilot are separate from Android Play
approval. Owner now prioritizes job applications; stop discretionary app work at
this checkpoint.

## Historical checkpoints

### Additional bounded stabilization — 26 October 2026

Owner explicitly resumed work using the remaining allowance. Fixed a real catalog
regression: changing level/context hid previously practised targets even though
they remain eligible for reinforcement. Catalog and runtime selection now retain
those existing targets; new targets remain bounded by preferences. Existing rounds,
immutable revisions and historical timezone evidence remain unchanged.

Verification: focused backend profile/adoption suites 20/20 pass; web/backend
TypeScript pass. HTTP rerun after window.Event fixed three failures (14/20 pass).
The remaining six content journeys assumed remount always went Home; updated them
to accept actual restored Practice navigation and await background reads. The
content integration rerun passes 7/7, retaining exact frozen-attempt replay and
one-evidence assertions. No full combined HTTP/backend rerun after these changes.

Android lintDebug and assembleDebug pass offline. Updated native access tests for
explicit temporary-practice leave choices, current topic labels and actual auth
return routes. One run reached 9/10, but the final run passed 8/10: randomized
answer selection/draft preservation remains unstable, and another native access
assertion remains unresolved. Do not call native runtime acceptance complete or
weaken draft assertions. Final next action is to reproduce these failures with a
deterministic selected exercise and check draft persistence before/after recreation.
No release, installation, live account/content operation or provider change.

Remaining release limits above still apply, especially cross-device draft resume,
live lifecycle/export/account isolation and owner Android UX approval. Work and
social capabilities stay disabled. Stop at the saved allowance checkpoint.

Updated 25 October 2026. See the [current backlog reassessment](docs/operations/backlog-reassessment-2026-10-25.md); dated entries below preserve implementation history. Recovery tracking: [issue #20](https://github.com/abulhawa/german-master-2/issues/20). Earlier checkpoints are preserved in [history](docs/operations/history/progress-through-2026-10-23.md); their paths are relative to repository root.

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

## Pixel installation — 24 October 2026

Owner requested installing the aligned Android app on their Pixel. Built the
learnerPreview variant from 12e7230 with the clean replacement project, canonical
production API and the public publishable key from the deployed web artifact.
Verified configuration without printing the key. Offline preview assembly passed.
ADB replacement install succeeded on the connected Pixel 10 Pro for the existing
preview package, preserving its data and leaving the separate store app intact.
No uninstall, data clear, owner signing-key access or Play upload occurred.

Launched German Master Preview successfully (cold launch, app process running).
Device UI hierarchy confirms direct Practise German Home, retained existing
completion state (Repeat the five questions), and Progress / Downloads / Account
without Sign in / Create account on Home. This closes install/basic launch
verification only; no complete native account lifecycle or owner UX acceptance
is claimed. The Android publication hold remains. Exact next action: owner tests
the installed preview; address demonstrated user-facing blockers on both clients.

## Navigation release blocker — 24 October 2026

Owner rejected the installed preview's missing app navigation. The earlier
practice-access patch preserved a stripped-down local host instead of the
blueprint's navigation shell; source flow alignment was not adequate UI parity.

Corrected both clients: persistent Home / Progress / Topics bottom navigation
(narrow web / Android), desktop web rail, active destination indicators, Account
in the header/rail, and Downloads within Account. Account-required pages keep
the main navigation. Topics exposes the existing B1 five-question foundations
pack without sign-in; it does not expose the full authenticated catalog or add
content. Practice remains focused, with safe Close and draft restoration.
Android shares its actual navigation component between local and signed-in
shells; Home and Topics use the same calmer card hierarchy across clients.

Verification: root generated/web/backend type checks, production build, 71
focused web regressions and 14 native UI/provider/shell regressions pass. Android
debug and learnerPreview assembly and lint pass (zero errors). Local built-bundle
browser checks at 1280px/390px pass navigation, public Topics, account prompts,
auth cancellation, draft restoration, grading and summary; no overflow/page errors.
A root npm test rerun stalled during backend tests and was stopped; do not claim
a new local full-suite pass. Hosted CI will provide the complete offline run.

The Pixel disconnected during the build. The new production-configured preview
APK is built, but replacement installation failed because no ADB device was
available. The owner was asked to reconnect/unlock the Pixel. Do not claim the
navigation fix is installed or device-accepted until it is actually verified.
Exact next action: deploy/verify web through main and check hosted CI; reconnect
the Pixel, replace-install the preview without data clearing, and verify visible
navigation on the device. Android publication/UX approval remains held.

Web rollout result: navigation commit 7cd7f47 deployed READY as
`dpl_Gj2kWehyV3WKB7tNUaq3QktPhNHA`. Live public-site checks at 1280px and
390px passed persistent navigation, Topics, contextual account requirements,
return from auth, retained draft, grading and completion. Web client/HTTP and
web build/browser CI jobs passed; backend CI was still running at this update.
Android CI exposed a stale synthetic production configuration still naming the
retired project. Updated the CI fixture to the clean project and added an
explicit retired-project rejection check; the production guard is retained.
Local positive/negative configuration checks already passed. Corrected only
line endings in the shared-shell call site; no additional runtime behavior.
The Pixel remains absent from ADB after the owner's reconnect acknowledgement;
replacement installation/device navigation verification remains pending.

## Navigation device verification completed — 24 October 2026

Owner reconnected/unlocked the Pixel 10 Pro. Replacement install of the new
production-configured preview succeeded without uninstalling or clearing data.
Physical-device UI checks passed Home / Topics / Progress navigation, public
Topics without signup, the contextual Progress account requirement while keeping
all three tabs visible, and returning Home with the existing unfinished session
still available. Left the app open on Home. These checks did not submit answers,
create an account, alter saved credentials or claim a complete native lifecycle.

A focused device visual check exposed white system-bar gutters in dark mode.
Both native hosts now draw the scaffold behind safe insets and use the shared
navigation bar's standard system insets. Final debug/preview builds, 14 focused
native tests and lint pass; the final preview was installed and the same device
navigation checks passed again. Dark system bars were visually verified. The
existing stopped screenshot campaign and accepted TalkBack scope were not
reopened; only this reported navigation defect received focused visual QA.

Hosted Web checks for 7cd7f47 passed all jobs, including backend checks, web/HTTP
integration and browser/build verification. The corrected Android CI run at
d2e3552 was still building its unsigned release-check artifact at this update;
no full hosted native pass is claimed yet. Exact next action: inspect that hosted
result and address demonstrated user-facing release blockers on both clients.
Owner UX approval and Android store publication remain separate and held.

## Owner rejects overall UI — 24 October 2026

Owner rejects the overall hard-reset UI, beyond the earlier navigation defects,
and requests a contemporary, suitable design without supplying a reference app.
Treat this as renewed visual/interaction design work within the existing feature
freeze. Passing behavior checks and earlier focused device checks do not satisfy
owner UX acceptance.

Inspected the current live signed-out Home and Topics visually. Findings include
repeated session labels/actions, weak content hierarchy, excessive empty space,
and Topics presented as a plain list inside an oversized card. Created a private,
interactive design proposal covering responsive Home/navigation, Topics, account
gates and a sample choice/feedback interaction. This is a proposal, not integrated
web/Android code or a new content pack. Browser preview verified Home-to-practice,
selection, disabled/enabled Check, and incorrect feedback. No native runtime,
production change, renewed accessibility acceptance or complete redesign is claimed.

Next action: use owner feedback on the concrete visual proposal to refine the
direction, then implement the agreed design across web and native Android in the
same slice, preserving account isolation, drafts and existing practice semantics.
The Android publication hold remains.

## Approved direction implemented mobile first — 24 October 2026

Owner accepted the concrete proposal and requested autonomous mobile-first
implementation without a phone. Implemented native Android and aligned mobile
web in the same slice. See [mobile design](docs/product/mobile-design-2026-10-24.md).
Shared generated colours, Compose typography/shapes, compact headers, calmer Home
cards, topic rows, answer choices, question progress and feedback now follow the
approved direction. Sign-in and contextual account gates use the same treatment.
Account-backed Progress has a useful empty state and the existing practice action;
destination changes reset scroll. Drafts, grading, content revisions, account
isolation and explicit attachment are preserved. M2 remains the active milestone.

Verification: offline root dependency reinstall, generated/web/backend checks,
production learner/API build and full root test run passed (198 backend, 434 web,
20 HTTP tests). After final web layout refinements, checks/build and 71 mounted
guest/account journey regressions passed again. Native final focused run passed
35 tests across seven suites, including 11 native render cases. Debug and
learnerPreview assembly and lint passed (zero errors, 19 existing warnings).
Rendered light/dark, narrow German, large text, practice, Topics, authentication
and fixture-backed Home/Progress were visually inspected. A discovered native
scroll-retention defect and web CSS spacing conflict were corrected.

Local browser verification passed the guest practice journey at 390px, German
Home at 320px and desktop layout at 1280px, with no horizontal overflow. These are
local real-component checks, not new production or live-account acceptance.
The final learnerPreview APK was separately assembled with the existing clean
production project/public key/API origin and passed the release-configuration
guard. No private credential was used. No emulator image/device was available;
no physical install, live native Auth or renewed TalkBack acceptance is claimed.
No push, deployment or store publication occurred.

Exact next action: review the built mobile preview with the owner; install it on
an available device without clearing data, complete native/live account acceptance,
then address demonstrated issues before requesting Android release approval.
Prior export/disconnect/second-account gates remain open. Keep the approved visual
direction and both clients aligned; do not restart broad design analysis.

## Mobile redesign installed — 24 October 2026

Owner requested installation on the connected Pixel 10 Pro. ADB replacement
installation of the production-configured learnerPreview APK built from 708ba9b
succeeded, without uninstalling or clearing data. The separate store package was
not replaced. Explicit launch succeeded and the preview process is running.
Device UI hierarchy confirms the new Home headline, Home / Progress / Topics
navigation and an available practice action. Left the preview open on Home.
This verifies installation/basic launch only; no new answers, account lifecycle,
owner UX acceptance or store release is claimed. Android publication remains held.

## Settings and appearance access correction — 24 October 2026

Owner reported that the redesigned app still felt restrictive because settings
and theme controls were missing. Replaced the ambiguous secondary header icon
with a visible Settings label on web/native. Language and System / Light / Dark
controls are available before sign-in; only account-backed operations remain
gated. The Settings account section explains syncing instead of claiming the
whole page needs an account. Native theme changes also update system-bar icon
contrast and feedback colours. Local theme preferences survive recreation; native
signed-out language now persists. Device appearance is separate from account
identity/practice storage; draft and explicit-attachment schemas are unchanged.

Validation: root generated/web/backend checks, production build and all 435 web
tests pass. Seventy-two focused mounted web regressions include theme persistence
and retained practice. Eleven focused native access/shell tests pass, including
theme changes across recreation with a saved draft; explicit light/dark native
render checks also pass. Debug assembly and lint pass (zero errors, 22 warnings,
including KTX suggestions and the now-unused prior account-label resource).
An initial lint process crashed internally; the stable-source rerun passed.
Local 390px browser checks confirm visible Settings, account-free controls and a
saved dark theme after reload. Production-configured preview assembly and its
configuration guard pass.

Replacement installation on the owner's Pixel succeeded without uninstalling or
clearing data. After owner unlock, device hierarchy showed the running preview
and visible Settings label. The phone auto-locked again during interaction;
do not claim a completed physical theme-toggle or native live-account journey.
No store release, web deployment, push or backend configuration change occurred.
Exact next action: owner tries Settings in the installed preview; address concrete
issues while keeping web/native aligned and the Android publication hold intact.

## Quick grammar reference restored — 24 October 2026

Owner explicitly requests the old reference menu in GM2 on both clients, overriding
the feature freeze for this bounded restoration. Restored a global Grammar entry
in the shared shell and focused practice, without sign-in. Both implementations
offer the same topic menu, content search, topic return, tables, notes, expandable
rules/examples/common mistakes and safe dismissal over the current draft.
See [reference behaviour](docs/product/grammar-reference.md).

Canonical content is `content/reference/grammar-reference.json`: 14 menu topics,
16 original tables and 151 examples. The preserved original Android source and
the imported source match. Every reference field is preserved and compared in a
JVM regression. A detected extraction encoding error was corrected before
acceptance. Web bundles this JSON; Android copies the same file into its assets.
No backend/API, published exercise revision, grading, mastery or account storage
changes. Reference lookup works offline; German reference prose is retained with
English/German interface labels. M2 remains the sole active milestone.

Validation: root generated/web/backend checks and production build pass. Full
root run passed 198 backend, 438 web and 20 HTTP tests. A subsequent account-backed
reference regression passed with 50 related mounted tests (51 total). Native
reference/content/access/shell/render tests pass across final runs, including
exact legacy-content equality, both language menus and unchanged drafts. Debug
assembly and lint pass (zero errors, 22 warnings). Native render inspection caught
and corrected cramped German header/close controls; final 320dp dark menu and
393dp adjective table renders were inspected. Local 390px browser checks pass
modal containment, table scrolling, Escape/focus return and retained selection.

The production-configured learnerPreview APK is built for owner review. The
phone was absent from ADB during this slice, so no replacement install or physical
reference journey is claimed. Web source is aligned and built but not deployed.
This unrolled-out state remains a release/acceptance gap; do not describe current
production and the installed phone app as already containing this restoration.
No push, production deployment, store upload, schema/content reset or live AI call
occurred. Exact next action: install the preview when the phone reconnects and
review the reference; obtain the applicable rollout decision for web. Android
store publication and outstanding account-lifecycle gates remain held.

## Grammar preview installed — 25 October 2026

Owner reconnected the Pixel and requested the pending installation/status check.
Replacement installation of the production-configured grammar-reference preview
built from bd73a70 succeeded without uninstalling or clearing data. Explicit
launch succeeded; device hierarchy confirmed the preview and its new Grammar
entry. The phone auto-locked during the subsequent menu interaction, so no
completed physical reference-browsing journey is claimed. Native/browser tests
and render evidence remain as recorded above. Web source is aligned and tested,
but production deployment is still outstanding; no web rollout or store release
occurred. Next: owner review of the installed reference and the applicable web
rollout decision. Android publication hold remains.


## Google and Microsoft sign-in work in progress — 25 October 2026

Owner requests Google and Outlook/Microsoft signup/login alongside existing email,
and authorizes reuse of existing provider apps. This supersedes the earlier OAuth
feature deferral for this bounded slice. Added both choices to web and native
sign-in/signup, retaining email/password and explicit guest attachment. Native
social auth uses a separate PKCE client, encrypted expiring verifier outside backup,
strict per-application code callbacks, and existing verified learner binding.
Email confirmation/reset retain their existing flow. Debug/preview callbacks are
routed to the matching host; no signing or application identity change.

Live read-only evidence: legacy Supabase has Google enabled, Azure disabled;
clean replacement has both disabled and email enabled. Existing Google Cloud
project GermanVerbMaster Auth contains a Supabase Google OAuth web client, with
its legacy callback. It needs the clean project's callback added while retaining
existing entries. Microsoft Entra and Supabase dashboard require owner sign-in.
No provider configuration, secret transfer, deployment, push or store release
was performed. Do not expose these buttons in production until setup/acceptance.

Validation so far: root generated/web/backend type checks passed before the last
OAuth-error refinement. Latest native debug/learnerPreview assembly passed;
PKCE callback validation (1 test) and native access/social UI (8 tests) passed.
The additional older NativeEntry suite failed five guest assertions; investigate
without claiming regression origin. Lint found a missing explicit exported flag
in the debug overlay; corrected but not rerun. Web provider suite passed 16/17;
it exposed OAuth cancellation being mistaken for password recovery. Recovery
intent discrimination was corrected but needs rerun, including recovery tests.
Full backend/root tests stalled and were stopped; no fresh full-suite pass.
No final visual/browser/device or live social-login acceptance is claimed.

Owner requests PC shutdown whenever user input or allowance is required. Saved
this incomplete slice before shutdown because provider-dashboard sign-in is
needed. Next: rerun focused web/recovery/native checks and lint, inspect remaining
native failures, verify responsive UI, then reuse Google app and inspect Microsoft
registration after owner sign-in. Provider changes and live account acceptance
remain unfinished. M2 and Android publication hold remain unchanged.

## Login icons and Home exits - 25 October 2026

Added recognizable Google/Microsoft vector icons on web and native Android.
Authentication/signup, confirmation resend, reset, recovery states and email-change
pages expose Home; native reauthentication exits restore the account host. Existing
safe practice close/restore and explicit guest attachment remain intact. Standing
navigation expectations are recorded in AGENTS.md.

Validation: 39 focused web tests, root type/generated checks and production build
pass. Native final focused run passed 12 tests; debug assembly and lint pass
(0 errors, 23 warnings). Two native light/dark/narrow renders were inspected; local
browser desktop/mobile sign-in icons and Home/reset navigation were checked using
mock Auth. Full backend run stalled; broader web run exposed a db-push test failure
before being stopped. No fresh full-suite pass or device/live-provider claim.
No provider activation, push, deployment or store release performed in this slice.
Prior social-provider setup and acceptance remain unfinished. Owner objected to
excessive verification/allowance consumption; stop further checks at this handoff.
Next: provider setup only when resumed; do not publish unfinished social login.

## Updated login preview installed - 25 October 2026

Owner requested installation of the latest Google/Microsoft login UI changes.
Built learnerPreview from 0d3119a with existing clean production public Auth/API
configuration; configuration guard passed. ADB replacement installation on the
connected Pixel 10 Pro succeeded without uninstalling or clearing app data.
Explicit preview launch succeeded and its process was confirmed running.
This is installation/launch evidence only. Provider activation and real social
login remain unfinished; no store app replacement, store publication or web
rollout occurred. Next: review installed UI; complete provider setup when resumed.


## Backlog reassessment - 25 October 2026

Reviewed one monorepo issue/no open PRs, legacy web's 21 issues/seven PRs/five
phase milestones, and legacy Android's empty open issue/PR inventory. All items
and GM-001 through GM-024 have proposed dispositions in
[the reassessment](docs/operations/backlog-reassessment-2026-10-25.md).
Nine overlapping epic pairs and old verb-first/commercial trajectories should be
retired or consolidated. README's obsolete 503/cutover work order was corrected.
No GitHub items, milestones, providers, permissions or deployments were changed;
no tests/builds run. Board-only drafts are unassessed because read:project was
unavailable and the connector has no Project tool. M2 remains active. Next: review
the proposed closures and issue #20 rewrite; preserve outstanding privacy/offline
acceptance rather than reopening completed bootstrap or creating 24 new issues.


## Legacy backlog retired and browser navigation repaired - 25 October 2026

Owner approved cleanup and reaffirmed a single practice product, without a
separate guest/welcome/Try mode. Optional sign-in and existing local practice
storage are preserved. Closed legacy web issues 3,4,5,6,7,8,9,11,13,14,16,18,19,20,
21,22,24,26,27,67,77 as not planned; closed PRs 64,202-207 without merging and phase
milestones 1-5. Verified no open legacy issues/PRs remain. Browser access resolved
the CLI Project-scope limitation: reviewed the old nine-backlog/13-Done-card
Product Roadmap project and closed it; settings now offer Re-open this project.
No original repository source/history or branches were deleted. Monorepo issue
20 was rewritten to remaining release-readiness work and stays open. Imported
legacy roadmap/sync documents now warn against recreating retired tasks.

Web main pages, practice and authentication now share one hash/history route
source. Navigation pushes actual browser entries; Back/Forward restores screens,
and reloading practice restores its saved answer/feedback. Question progress is
not browser history. One real Home entry is established without trapping Back at
the site boundary. OAuth/recovery parameters are left for Auth; explicit Home
cleans recovery state. Account isolation and captured email-change binding remain.
Native already had screen-aware main-page Back; auth Back now follows previous
account pages, while Home exits directly. No guest selector was introduced.

Evidence: 85 focused web journey/history tests passed; final account-binding
provider rerun passed 24. Root generated/type checks and production build passed.
Local mocked-provider browser checks confirmed practice -> Settings -> Back with
selected answer, Forward to Settings, and signup -> Back to sign-in. These are
local behavior checks, not live social login. Final native access/history run passed 10 tests; debug assembly and lint passed. No production deployment,
push, phone replacement install or store release is part of this slice.
Next: roll out the verified navigation change under applicable authorization;
finish provider configuration and remaining issue-20 acceptance. M2 and Android
publication hold are unchanged.


## Web logo Home link — 25 October 2026

Web signed-out/signed-in rail logos and the sign-in wordmark now link Home via
the existing navigation handlers, preserving practice state. Links retain the
existing appearance and provide keyboard focus. This is a web navigation
convention; native Home/close behavior is unchanged.
Validation: one focused existing draft-preservation test passed; diff whitespace
check passed. Full suites deferred to CI at owner request. No deployment or push.
Next: roll out with the pending web changes under applicable authorization.


## Simplified practice work in progress — 25 October 2026

Owner approved the simplification plan, then requested minimal implementation and
an immediate wrap-up near 15% remaining five-hour allowance. This is an incomplete
local implementation checkpoint, NOT release-ready or deployed. M2 remains active.

Implemented source: temporary signed-out browser/session and native/process
storage; explicit leave/save/discard choices; durable opt-in authentication
handoff; B1/B2 and general/work preferences; five-question new rounds; topic
selection from a shared bundle. Account preference/session contracts accept an
optional learningContext (absent means general). Backend persists that preference
in existing revisioned profile-request JSON and filters the selected catalog;
no database migration or live database operation was performed. Existing account
rounds/downloads retain their saved requests.

Added 60 original workplace exercises (30 per level), five contexts, hash-bound
AI editorial review and deterministic rubric validation; not independent human
review or proficiency certification. Shared bundle uses existing production
revisions for general practice and the new candidate identities for work.
Unpublished additive SQL/catalog is prepared; production release configuration
is unchanged. Candidate workplace attempts cannot attach to production until
separately authorized content activation. No live AI service calls.

Evidence: both TypeScript projects checked successfully; two new focused web
exit/consent tests passed after correcting the Close destination; final native
compileDebugKotlin passed. Content preparation/validation passed in isolated
PGlite. No full suite, native UI/unit suite, assembly, lint, browser/device journey,
real signup/provider acceptance, deployment, push or store release is claimed.

Release blockers / exact next work:
- Finish preserving and resuming an UNFINISHED round inside the account after
  signup. Existing attachment saves answered attempts only; temporary ungraded
  draft/position is retained separately, not converted into a resumable account
  round. Do not claim the full save/resume plan complete.
- Reconcile web/native attachment success/retry/partial-result UX and subject
  binding; native rejects unavailable revisions but lacks the polished recovery
  screen. Legacy native state lacks earlier per-question attempt records; do not
  invent those when offering its one-time save/discard choice.
- Verify browser Back/Forward/boundary handling, legacy migration, authentication
  cancellation/confirmation/recovery, process recreation and close warnings.
  Review modal focus and repeated legacy prompts; no parity acceptance yet.
- Update focused tests that deliberately assume the former fixed starter,
  persistent signed-out work and 15-question preferences; add native regressions.
- Validate selection/profile persistence and activation/replay against local
  fixtures. Review workplace ambiguity (including passive distractors), editorial
  hashes and final shared bundle before any content rollout. Simplify remaining
  fixed-B1/foundations copy and Progress presentation.

Next: complete these bounded correctness gaps, run affected checks only, prepare
activation/rollout instructions, and obtain applicable production/content rollout
authorization. Android publication hold and unfinished provider activation remain.


## Backlog narrowed to learner needs - 25 October 2026

Reassessed the old backlog against the approved simplification. Issue #20 now
keeps concrete learner-facing saving/resume, simple learning choices, content,
auth, offline/privacy and UX acceptance. Other work is explicitly Done/maintenance,
Superseded, Not planned or Deferred; minimal telemetry is deferred. Existing
reliability/privacy gates remain bounded supporting obligations. Legacy issue/PR/
board closures remain intact. No implementation, tests, deployment or publication
in this documentation/triage slice. Next implementation action remains completing
the unfinished account-round adoption/save flow recorded above.

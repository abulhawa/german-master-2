# Web tab-return navigation investigation — 9 October 2026

Branch: `fix/web-tab-return-navigation`, based on clean `813c779`. No deployment, push, merge, content publication, production data write or Android change.

## Runtime cause

The owner reproduced the reset after disconnecting browser control and switching tabs manually. The owner's Edge tab was inspected and its Nouns and plurals card opened; controlled inspection alone did not reproduce the reset. A separate headed Chromium run used Playwright against public deployed assets at `https://germanmaster.qortxai.com`, with synthetic auth and contract API responses fulfilled locally. This is runtime execution of the deployed application and real visibility changes, not a real-account sign-in or source-only diagnosis.

On return, the sequence was `visibilitychange: hidden` → `visible` → Supabase `SIGNED_IN` for the existing session → account generation replacement → new learner component instances → Home. In the first captured trace, visibility changed at 556/634 ms, followed by remounts at about 671 ms. The detail heading disappeared and became “A little practice. Lasting progress.” The document time origin stayed identical, there was one document request, and pathname remained `/`. A later repeat again changed generation 1 → 2 and returned Home. Internal navigation is React state, so the unchanged URL does not preserve it across remounts.

The app cleared `account` on every positive auth event, unmounting `LearnerJourney`. A second SDK subscriber also invalidated the provider. Rebinding allocated a fresh generation, which additionally changed `AccountLearnerJourney`'s key. Neither layer distinguished recovery of an existing session from an actual identity/session change. This is why a tab return lost the selected topic even without a new credential entry during the visit.

Playwright normally enables Chromium focus emulation on its own CDP session. Disabling it from another session did not reliably undo that override. The working harness launches a dedicated browser/profile and connects with `noDefaults: true`; it asserts actual hidden/visible events. Background waits use timer polling because animation-frame polling pauses in hidden tabs. The owner browser session was not exported or copied.

## Alternative causes and authentication limits

| Runtime case | Result |
| --- | --- |
| Deployed assets, synthetic existing session, no controlling worker | Detail resets; generation changes; no document reload |
| Deployed assets, same fixtures, controlling `sw.js` after an intentional setup reload | Same reset; no additional document navigation on tab return |
| Deployed assets, clean guest profile without credentials | Guest practice and typed draft remain; only `INITIAL_SESSION` observed |
| Fixed product build, verified session | Topic remains; generation 1 retained; existing component IDs retained |
| Fixed product build, online verification fails on return | Topic remains; generation retained; local recovery mode shown; delivery stays blocked |
| Fixed product build, clean guest | Practice and draft remain |

Both worker-controlled and uncontrolled deployed cases fail, excluding a worker update as the cause of this reproduction. No `controllerchange` accompanied the reset. Auth/API requests in the harness are local fixtures; the observed repeated user/API reads are not claims about the owner's production network or credentials. A completely credential-free guest reset was not reproduced. The owner's report that sign-in is unnecessary is retained as evidence; a saved/recovered session differs from a clean guest profile. No general claim that every possible unauthenticated reset is ruled out is made.

## Fix and regressions

Auth-event data is a hint for retaining local UI, never authorization. The provider retains the same binding only for matching subject and auth-session UUID (or an owned local binding awaiting its first matching verified session). Every bind still checks issuer/audience/role/expiry/session UUID and online verified identity. Changed subject/session, missing session, sign-out and password recovery invalidate old authority. Same-session recovery, rotated tokens and user updates re-verify without clearing the mounted view or the guest-attachment decision. Failed verification blocks online delivery while retaining local navigation and drafts. Existing stale-operation guards remain.

Unit regressions assert detail retention while verification is deferred and after completion, repeated positive events, genuinely rotated tokens, failed verification with blocked delivery, immediate invalidation for different subject/session or sign-out, and rejection of stale sign-in completion. Cold owned storage can upgrade to verified access without remounting. ADR 029 now qualifies its earlier blanket auth-event removal rule. This preserves blueprint interruption recovery and the web UI/accessibility guidance; no visual redesign, mastery policy or backend authorization change.

`apps/web/scripts/verify-tab-return.mjs` asserts real visibility events, the rendered detail or guest draft, unchanged document identity/navigation count, stable account generation and retained learner component IDs. Its React hook records only names, instance numbers, generation and navigation labels; it does not log credentials, user UUIDs or answer payloads. Default mode serves the built product entirely through local fixtures at a synthetic HTTPS origin. A real worker precache cannot be populated by that routed-static mode; `--controlled` is therefore restricted to the explicitly authorized live diagnostic. Fixed-build worker-controlled acceptance is not claimed.

Reproduce locally after an offline install, using synthetic public configuration and an installed Chromium executable when the bundled executable is unavailable:

```powershell
$env:VITE_V2_AUTH_PROJECT = 'zgmyrpzwgtydwlzponih'
$env:VITE_V2_AUTH_PUBLISHABLE_KEY = 'sb_publishable_synthetic-tab-regression'
$env:VITE_V2_API_ORIGIN = 'https://api.example'
# Optional: set GM_BROWSER_EXECUTABLE to an installed Chromium executable.
npm run build:learner-product
node apps/web/scripts/verify-tab-return.mjs
node apps/web/scripts/verify-tab-return.mjs --verification-failure
node apps/web/scripts/verify-tab-return.mjs --guest
```

`--live --expect-reset`, `--live --controlled --expect-reset` and `--live --guest` are separate opt-in diagnostics against public deployed assets, with all auth/API responses still synthetic and local. Do not include live diagnostics in normal checks. No production practice/session/account writes are made.

## Verification and handoff

Node 22 offline root `npm ci --offline --ignore-scripts`, generated/web/backend checks, full root tests (193 backend, 408 web, 20 real local HTTP), root web/API/server build and separate learner product build pass. Final strengthened rotated-token/provider regressions pass 18 tests across two files. Chromium fixed-build topic, failed-verification and guest checks pass; deployed reset and guest comparisons pass in their stated modes. Ignored diagnostic outputs retain the detailed traces. The bundled Chromium executables failed to launch on this host; installed Chrome provided the real Chromium runs. No Android checks were required for this web-only change; no Groq/live AI calls.

M1 remains active with its existing design/manual accessibility and hosted-verification gates. Next: owner review of this branch and authorized hosted verification. Deployment and merge remain outside this request.

Reference: the [Supabase auth-event documentation](https://supabase.com/docs/reference/javascript/auth-onauthstatechange) describes repeated `SIGNED_IN` notifications on tab refocus. Installed SDK and Playwright sources were checked alongside runtime evidence. The changelog Markdown endpoint could not be fetched by the browser tool; the breaking-change index was checked instead.

## 10 October corrective cold-start investigation

PR #13 was merged and deployed under subsequent owner authorization. The first deployed ordinary tab-return check passed, but its worker-controlled reload failed before reaching the topic view. The owner then supplied the Home screenshot with three independent preference/catalog/progress failures. This exposed a readiness transition missed by the original tests, rather than a service-worker-caused navigation reset.

The deployed build was reproduced in headed Chromium with a saved subject and an 800 ms synthetic delay on the first verified-user response. Local access mounted first; profile, catalog and target reads were blocked before any `/v2/` HTTP request. Online verification completed and removed the local-access notice, but the binding and API reference stayed identical, so the learner's `[api]` loading effect never ran again. All three failure controls remained; practice stayed unavailable. This synthetic network delay is a controlled runtime condition, not a measured production request duration.

The correction renews the API when local/verified readiness changes, retaining the account generation and mounted detail. Already-verified same-session background checks no longer temporarily turn delivery off; each request still obtains an online verified credential and now also checks its session UUID against the captured session. Failure blocks delivery; changed identities/session UUIDs invalidate immediately. A deferred-verification UI regression checks that initially blocked reads retry and selected detail survives. Provider tests cover concurrent verified delivery and a silent same-subject session change.

The browser harness adds `--cold --load-only` for positive cold-start acceptance and `--live --cold --expect-load-failure` for explicitly authorized old-build diagnosis. Positive acceptance requires profile/catalog/targets requests, enabled practice and no alert or preference/topic reload controls. The local corrected product passes those assertions and the ordinary real hidden/visible tab-return check. The `--cold` combined tab-switch run encountered a browser visibility timeout on this host; cold data loading and real tab-switch acceptance are recorded as separate runs, not a successful combined run. Full checks and final corrective deployment results belong in the newest checkpoint.

Final correction: PR #14 head `86e3859` passed all hosted checks, including offline shell acceptance, and merged as `086e9ae` at 00:17 Europe/Berlin on 10 October. Production `dpl_8TKhmvpxo7NGJBq6suYh4xGxWkDV` is READY for that merge; canonical HTML changed to `index-Cn1Bd8Nj.js`. Full local tests passed 193 backend, 411 web and 20 HTTP cases, with 21 focused provider/UI regressions and both builds/type checks passing.

On the corrected canonical site, `--live --cold --load-only` passes with three verified learner reads, and `--live --controlled --load-only` passes with seven reads across initial load/reload. Both enable practice and remove preference/topic retry controls and alerts without manual clicks. A separate `--live --controlled` run passes actual hidden/visible return, retaining “German in everyday work”, the account generation and existing component IDs under the controlling production worker. No extra document navigation occurs during tab return. These checks execute public deployed client assets with synthetic local services, not a real production-account/backend journey. No production user writes occur.

The first load-only assertion checked the practice button immediately after catalog arrived, before concurrent profile/progress work completed. It was refined to await practice readiness, then rerun successfully in all three stated production cases. Detailed trace files are ignored/private. The final helper refinement and evidence commit stay local. Post-merge Web, accessibility, safety and main analysis checks subsequently passed, separately from the green reviewed PR head. M1 design/manual accessibility and other milestone gates remain open.

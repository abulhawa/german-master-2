# M1 acceptance recheck — 9 October 2026

M1 remains active. Fetched origin before changing the checkout. Local main was clean at `e6facd2`, matching origin/main; created a local tracking branch for draft [PR #11](https://github.com/abulhawa/german-master-2/pull/11), head `bce5cc3`. No local work was discarded. No push, merge, deployment, publication, production data change or Android release occurred.

## Hosted state

At the inspected head, Web and Android verify fail. Web fails `content-review.test.ts` because mutating an approved target invalidates its hash before the structural assertion runs. Android fails `NativePracticeUiTest.kt:66` when entering text into an offscreen field. Accessibility, repository safety and CodeQL pass. Vercel deployment `dpl_E18YCtyayRKbsCRHT3jeaCUmWSTd` is READY and metadata matches the PR head. Its preview redirects this browser to Vercel login; hosted visual acceptance is not established. Protection was not weakened and no share link was created.

## Repairs and local verification

- Structural-validation test explicitly marks its mutated fixture pending; the separate obsolete-approval-hash regression remains intact.
- Native typed-fixture test scrolls both fields into view before entering text. No production control behavior changed.
- Candidate preview normalizes CRLF in SQL comparison so a Windows checkout can validate the same generated SQL. Regeneration produced no semantic artifact changes; manifest remains `0f6fe969595ca116346c29c67b6d881fa82862222c5f164fc0773256b229b676`.
- Node 22.23.3: root dependency install, generated/type checks, full tests and web/API build pass. Backend: 29 files/193 tests; web: 93 files/398 tests; authoritative HTTP: five files/20 tests.
- Offline Android: 43 suites/165 tests, zero failures/errors; debug and learner-preview assembly and both lint tasks pass. Lint has zero errors, 17 debug/18 preview warnings. Local SDK path was supplied through the environment after the first invocation reported no SDK location.
- Existing converted-content tests cover 100 revision-2 renderings, all 30 B2 targets through native authoritative HTTP/draft restart/completion, and representative B2 web authoritative journeys. Hash-bound AI approvals validate for all 60 draft targets. Independent human German review is no longer a required content gate under the recorded owner policy.

Logs: ignored `.local/m1-20261009-{install,check,test,build,android,a11y}.log`.

## Physical Android and web observations

Authorized Pixel 10 Pro, Android 17/API 37. Installed the newly assembled isolated `.preview` APK beside the original application; routed only the local fixture API. Preserved the existing preview cache by renaming it before the temporary session and restored it afterward. Temporary acceptance cache remains separately preserved on the device.

Native B2 passive-perfect gap choices render four options; selecting `worden` and Check receives confirmed Correct feedback and the authored explanation. Continue reaches the next B2 question. At font scale 2.0 and density 560 (about 309dp wide), feedback wraps and Continue remains reachable by scrolling; configuration recreation retains the confirmed practice, resumed through Continue practice. This is physical runtime evidence, not JVM simulation. The preview Home places substantial account/privacy content before practice, requiring several swipes at enlarged settings; final design acceptance must assess this.

TalkBack was installed and enabled, bound with spoken/haptic feedback and touch exploration. After its onboarding and notification prompt were dismissed, a screenshot captured the TalkBack focus outline on question position. A complete gesture/keyboard practice journey, spoken labels/selected states, feedback announcements and German pronunciation were not verified. ADB-injected navigation is not a substitute for manual auditory acceptance. No full TalkBack pass is claimed.

Restored font scale 1.0, density 420 with no override, no enabled accessibility service and accessibility_enabled=0; verified these values. Original preview cache restored, temporary port reversal removed, preview stopped. Notification permission was not granted.

Web: local unpublished B2 passive-present answer `wird` obtains confirmed feedback; next passive-past question accepts keyboard Space/Enter submission. Feedback receives focus. At 320px, measured scroll width is 305px and visual feedback wraps. Existing accessibility harness passes at 320×800, including 200% learner text, keyboard journey, focus, names, targets and token contrast. These automated checks use the starter fixture; they do not certify all candidate screens or manual assistive technology. Screenshots are saved privately in `.local/m1-phone-large.png`, `.local/m1-talkback.png` and the chat's output directory.

## Exact remaining M1 gates

**Owner acceptance update:** TalkBack acceptance is **completed for now**, as explicitly requested on 9 October. Further TalkBack testing/improvements are deferred; the earlier incomplete-journey notes below remain evidence limits, not a current TalkBack completion blocker. UIAutomator inspection repeatedly interrupted the listening experience according to the owner's report; automated inspection is not proof of uninterrupted speech. The testing loop stopped, accessibility returned to the original disabled state and preview stopped. No original learner draft was changed in this follow-up.

The follow-up fixes native prompt focus/scroll visibility on question changes, with a next-prompt focus regression. All 165 offline Android tests, both builds and lint pass. A newly built physical preview exposes the first prompt as focused. Remaining current gates are hosted verification of this subsequent native change and final both-client design/web accessibility acceptance; production approval stays separate.

Update after fresh fetch on 9 October: PR head `d9715d0` now contains the repairs. All Web/Android/accessibility/safety/CodeQL Actions and Vercel statuses pass. [Web run](https://github.com/abulhawa/german-master-2/actions/runs/37898138261) and [Android run](https://github.com/abulhawa/german-master-2/actions/runs/37898138259) close item 1 below. The earlier hosted-state and unpushed observations describe the initial recheck; items 2 and 3 remain open. PR remains draft.

1. Hosted Web/Android checks must pass for the repaired commit. Repairs are local and unpushed; hosted results still describe `bce5cc3`.
2. Complete physical Android accessibility core journey: TalkBack spoken output/reading order/selection/feedback/focus, ordering and matching alternatives, keyboard/insets, and usable large-text/display-size navigation. The phone is available; no missing-device blocker remains.
3. Final both-client design/accessibility acceptance, including manual web screen-reader review. Current local visuals and automated reflow checks are partial evidence. Hosted preview visual review needs an existing authorized browser session or equivalent scoped access.

Content conversion and hash-bound AI editorial validation are complete locally. Merge, production deployment/content publication and Android release approval remain separate from M1 acceptance; none was requested or performed.

# Replacement account acceptance - 24 October 2026

This follows [issue #20](https://github.com/abulhawa/german-master-2/issues/20) under owner authorization for an isolated Vercel test deployment and owner-controlled Outlook identity. It is not a production-cutover or Android-release decision.

## Deployment and access

Initial PR #21-only preview `dpl_7SP8xR3vbd75YsoaVQLQ2nSwj5U4` reached READY but signed-in browser API requests failed before reaching application logs. The provider used fetch credentials omit, excluding the same-origin Vercel protection cookie. Source and protected-runtime behaviour established this boundary; no application profile had been created then.

Commit `cc20b2c` retains same-origin hosting authentication while keeping application bearer authentication mandatory. Fetch still excludes cookies across origins, redirects remain errors and subject/session verification is unchanged. Provider regression and privacy HTTP recovery checks pass. The current combined recovery branch is pushed; GitHub PRs #21/#22 remain unmerged.

Final deployment `dpl_GkJvnxzNVb3QpbZ3A93PSbGAd5GK` at `b67e4c0` is READY, replacing the cc20b2c preview at the same stable alias. Its [stable preview alias](https://german-master-v2-git-recovery-issue-2-c7c627-abulhawas-projects.vercel.app/) is the test entry point. Seven branch-only preview variables select replacement-verification and the clean project; only the backend and Auth verifier credentials are supplied. Both identity-deletion switches are disabled, and the admin secret is absent. Vercel protection remains enabled. CLI access needs the full deployment URL; path-only checks initially returned the protection redirect. Application profile/catalog deny unauthenticated access with 401 after protection authentication.

The replacement now permits exact root/recovery callbacks for the initial unique URL and the stable recovery alias. Its existing production callback and Site URL remain. SMTP was already saved/enabled and was left unchanged. Production deployment and all ten production environment entries were verified unchanged after isolated setup.

## Observed account flow

| Scenario | Evidence and limit |
| --- | --- |
| Registration/confirmation | Owner entered credentials and confirmed email; read-only hosted counts show one confirmed Auth user. Passwords were not requested in chat or inspected |
| Password recovery | One app recovery request accepted; owner performed the email/password handoff and later returned to authenticated Home. Agent did not capture the password-update HTTP response independently |
| Profile and catalog | Authenticated Home loads seven topics; profile persists; published catalog spans the existing B1/B2 targets |
| Online practice | One correct plural and one deliberately incorrect plural were submitted to the real API; backend counts rose to two attempts/evidence records |
| Reload and tab ownership | Saved answer feedback survived reload. A second test tab acquired the lock during reload; closing the redundant tab restored the main tab without clearing work or duplicating the answer |
| Partial completion | Summary showed 2/15 answered and one correct; server confirmed one partial-completion receipt |
| Progress | Owned target review check/date appeared, with zero mastered targets. Synthetic test submissions are not learning/pilot evidence |
| Downloaded practice | Two sessions were allocated atomically. Initial UI falsely showed no availability; after reload it showed two. One downloaded answer was evaluated locally, persisted across reload, completed partially, then synced with server confirmation and zero queued events |
| Hosted final counts so far | One confirmed user/profile, five allocated sessions, three attempts/evidence records and two partial-completion receipts after the final fresh-pack check. Two current reserve sessions and the earlier saved practice remain available |
| Export | App reported Export downloaded. Browser download capture timed out and browser policy blocked internal download-list access; file inspection is not verified |
| Sign-out | Sync-and-sign-out returned to guest entry, retaining remote test progress. Fresh password login passed; saved review checks, needs-practice target and preferences returned |

## Follow-up corrections

- Shared backend feedback copy still used Needs correction / Korrektur nötig; align it with the established Not quite / Noch nicht ganz contract. The correction is presentation-only.
- OfflineDesk cached availability and never refreshed at issuance/expiry boundaries. A slightly future issuedAt or a status result arriving after the boundary could leave Start disabled until reload. A local-only guarded refresh and distinct not-yet-valid message address this. Two fake-time regressions cover normal issuance/expiry and a delayed pre-issuance status resolving after issuance. Engine canStartPreparedPack, expiry, hash checks, account ownership and pending writes are unchanged. No automatic network retry is introduced.

## Vercel agent setup

Followed the owner-requested [official setup guide](https://vercel.com/get-started.md). CLI 65.2.0 is installed user-wide and authenticated; the existing Vercel plugin supplies guidance/tools. Added the shared official MCP URL through Codex's user configuration, preserving unrelated entries and configuring write confirmation. Owner completed its separate OAuth consent. After refresh, the new shared server's own documentation search and authenticated list-teams both succeed. No standalone duplicate skill pack, new Vercel project or billing change was needed.

## Remaining acceptance

Actual exported-file validation, full disconnected/reconnect behaviour, a second independent account, real Android sign-in/practice/offline and separately authorized deletion remain open. Downloaded/local evaluation while connected does not prove browser network-disconnection acceptance. Native access to a protected preview also needs a reviewed authentication path; do not disable protection to make that test pass. No TalkBack gate was reopened. Google/Microsoft sign-in remains future work.

Final combined verification at b67e4c0 passes generated/web/backend type checks, 198 backend / 431 web / 20 HTTP tests and production learner/API build. The two timer regressions and 62 focused copy/journey tests pass; staged secret scan has no findings. The browser loaded the matching b67e4c0 asset bundle. A fresh download showed two available sessions without reload and retained Open saved session 1. Final signed-in 390px override did not apply to this claimed browser tab; default-viewport Home was inspected/captured, but no new mobile viewport acceptance is inferred. Private captures and runtime files stay ignored; public evidence contains no private identity, credentials or workstation paths.

Production was rechecked after final preview verification: the latest production deployment remains dpl_5iYh7e2NDy5KENpEvRsxozGUhByd at a9f0c3ae. No production cutover, retirement, learner deletion, schema/content mutation, store release, paid usage or live AI inference occurred. Owner-approved account/practice test writes and replacement callback additions are the external test changes.

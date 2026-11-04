# First reviewed enrichment wave — production receipt

Owner explicitly instructed "no staging, directly to production" on 4 November
2026. No other service was paused; no Supabase project/billing change occurred.

PR #30 merged checked head `9bf363b0` as main `14b2ae0b9d28711b83e6fa256c79229b84f7aaf8`.
All activation-head hosted checks passed, including backend, client/HTTP, builds,
browser, accessibility, repository safety, native and release guardrails. Local
native verification passed 12 candidate/access tests, lintDebug and assembleDebug.

Production Supabase target: clean project `sqgjsmiaprsuilcjmaav`. The additive
transaction imported 11 targets / 22 questions and retained all 125 prior members.
Before commit it compared SHA-256 over every old/new immutable runtime row against
the independently generated expected rows, checked the old release manifest,
and confirmed the old rows remained unchanged. The exact transaction was first
executed in isolated PGlite. Post-import production row digest matched:
`806ca273e7b134d5e98c9b2c4f5e1cf7bb1386578f566c7982c368911d3680c7`.

New published release `90000000-0000-4000-8000-000000000001` contains 147 members;
manifest `41fc881972547fe0cda6dcb87da06b36349d8072a0cc79313c0fe5cbe27357dc`.
Original five-member and 125-member releases remain published. No learner rows,
existing revisions, policies, credentials or schema were changed by the import.

Vercel production deployment `dpl_CMUP8gvk3MJ3rwaTZGEC6jvTsJud` at the merge commit
is READY and serves `https://germanmaster.qortxai.com`. Fresh isolated Chromium
contexts verified the new Everyday vocabulary topic on both B1 (12 questions)
and B2 (10 questions), the exact release pin, selected-answer restoration after
reload, a correct accepted-answer grade, feedback restoration after reload, and
Continue advancing. Homepage returned 200; unauthenticated catalog/profile 401.
These were temporary signed-out rounds; no account answers or existing user tabs
were touched. This is not new authenticated saving or physical native acceptance.

The same shared bundle is active in Android source and the debug APK was built;
no phone installation or Play publication occurred. Android Play remains held.
Previous production deployment is `dpl_BqZFcnYf9dic4biNaVPeDMN48wcG` at `27fdd13f`.
Rollback must retain new release/revisions for pinned saved rounds. Prefer
restoring the previous runtime selector while retaining the compatible bundle;
an old asset rollback is not certified for temporary new-question guest rounds.

The 22 questions are the first wave, not the total reviewed/approved content.
The next release bounds selection on both clients independently of library size,
then integrates the larger hash-verified reviewed pool with material corrections
or exclusions. Independent editorial review remains untouched.

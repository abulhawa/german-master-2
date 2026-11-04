# Reviewed dataset production receipt — 4 November 2026

## Delivered dataset

Owner authorizes reviewed = editorially approved, direct production without
staging, and explicitly asks to focus on dataset deployment. PR #31 checked head
`55d828eb` merged as `56e38fdb957d6129b3de3b4112d7378b2064a2d7`.

All 78 completed batches / 2,588 reviewed questions are accounted for:

* 2,466 additional questions / 1,233 targets imported in this rollout;
* 22 reviewed questions already imported in wave 1;
* 100 questions in 50 whole targets excluded for unresolved material defects.

The new release therefore includes 2,488 reviewed questions plus the 125 previous
questions: **2,613 actual rows in `gm.exercise_revision`**, independently counted
after publication. Ninety-one additional corrections have exact semantic recheck
evidence; original source flags, answer identities and review branch remain intact.
Fifty-four explicit Work questions keep their gate. General pools contain 1,283
B1 / 1,276 B2 questions. Vocabulary kinds and POS topics are explicit; three
connector grammar objectives retain grammar kind. No CEFR/rights certification
or human-review claim is made.

## Import evidence

Target: existing clean Supabase project `sqgjsmiaprsuilcjmaav`, Frankfurt. No other
project/service was paused, restored or deleted; no billing/role/schema changes.
Old account history is retained. Existing learner rows were not modified.

The exact import was first executed locally using PGlite. Twenty-five bounded
transactions inserted immutable new entities while old app selection stayed live.
Each transaction held the release advisory lock, verified the old release manifest
and prior-row SHA-256 digest, and verified its full new rows before commit. The
final transaction published release/membership only after all entities were complete
and verified both old and complete new runtime rows. Post-commit verification matched.
Old five-, 125- and 147-member releases and all their rows remain intact.

* Release: `91000000-0000-4000-8000-000000000001` (2,613 members).
* Runtime manifest: `e1a05398d099fcd0f7a5a2ebaa73bf8bc7e554cebfc0e16e2b84a1ca7b467722`.
* Actual full-row digest: `c82930e92c68d458c64ad9672e1786a672eef01e8dddb6cc0f9376784764cb44`.
* Seed SHA-256: `6367bc4a40609df4925a17f7c10816f759f0226650dab20d1497cdcc8b00c85d`.
* Shared offline SHA-256: `5862635e177c339569a0397078f478541e536fa6c9aa5e6ca281460bcda8d119`.

## Checks and production delivery

All hosted checks pass at the activation head: backend/typecheck, full client/HTTP,
production/preview builds and Chromium offline acceptance, accessibility, repository
safety, native and release guardrails. Web run 41835478340 is successful. Local
backend 201 tests passed; all 466 client tests passed across the full run plus the
corrected traversal timeout rerun; 20 HTTP tests passed. Native 15 tests pass,
including grading every one of the 2,466 new questions, bounded/frozen state and
existing access flows; lintDebug and assembleDebug pass offline. No gate was
removed. The 125 bound remains per saved round, independent of library size.

Vercel production `dpl_2qQWY8WbH5hXvLRdXDf5TzGo9Gq9` at the merge commit is READY
and assigned the existing production domains including `https://germanmaster.qortxai.com`.
Fresh isolated Chromium contexts checked the actual production site on B1 and B2:

* Adjectives topic is shown once despite paged packs.
* A 125-question round starts from the B1 350-question / B2 362-question topic pool.
* The round pins the exact new release and contains the expected candidate exercise.
* Offline reload preserves the selected answer; accepted answer grades correct.
* Further offline reload preserves feedback; Continue advances to index 1.
* Homepage returns 200; unauthenticated catalog/profile return 401.

No page errors occurred. A deployment-scoped 10-minute error/fatal log-count query
returned no groups. These are bounded signed-out production checks, not a fresh
authenticated saving journey or physical native acceptance. No existing user tab,
account answer or profile was touched. Android uses the same source bundle and
bounded selection with local acceptance, but no phone installation or Play upload
occurred; the owner Play hold remains.

## Rollback and continuation

Previous functional production is wave 1 deployment `dpl_CMUP8gvk3MJ3rwaTZGEC6jvTsJud`
at main `14b2ae0b`, release `90000000-0000-4000-8000-000000000001`. Prefer restoring
the prior runtime selector while retaining new immutable revisions and the
compatible current bundle so pinned new rounds remain readable. Do not delete new
revisions/releases or claim an old asset bundle is safe for temporary new-question
rounds. Local acceptance proves new pinned-round readback with the old selector.

The frozen candidate/evidence remain preparation artifacts; this receipt records
live publication. Continue independent review after 0078 on its sole-writer branch.
The excluded material-defect targets need validated corrections before a future
additive wave. No redesign, staging, archive deletion or paid change is needed.

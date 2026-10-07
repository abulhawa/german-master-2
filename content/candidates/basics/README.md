# Basic practice integration

This candidate addresses the empty B2 catalog and the tiny B1 starter. It adds
30 B1 targets / 60 existing ChatGPT-prepared exercises and 30 B2 targets / 60
original Codex-authored exercises. The existing published five B1 targets and
revisions are retained, giving 65 targets / 125 exercises in total.

Topics: nouns/plurals, cases/adjective endings, verbs/prepositions, sentence
structure, passive/conditionals and German at work. The existing B1 drafts cover
all five supported exercise forms; the new B2 scaffold uses controlled cloze
answers. B2 is a pack selection for B2 learners, not a proficiency assessment or
a claim that every individual form is exclusive to that level.

Every addition includes English/German instructions, hints and explanations,
accepted-answer rubrics, distinct context/variant identities and provenance.
AI editorial assistance is recorded honestly; independent review is pending.
No live AI, Groq or external content service is required for practice or tests.

## Reproduce and review

From the repository root, run `npm run content:prepare-basics`. The command uses
an isolated in-memory PGlite database, installs the existing starter, validates
the combined drafts and emits `catalog.json`, `seed.sql` and `REVIEW.md` here.
It never reads credentials, connects to production or changes its configuration.

Edit `content/drafts/initial-30.json` and `content/drafts/b2-basics.json` directly
for corrections, then regenerate. Do not rerun the B1 authoring script over the
ChatGPT edits. The B2 authoring script refuses to overwrite an existing file.
Independent reviewer approvals must include the exact content hash. A future
separate ChatGPT editorial pass can use the workbook and source JSON to check
German naturalness, alternatives, distractors, hint leakage and explanations;
it must not mark itself as an independent human reviewer.

`seed.sql` inserts unpublished targets, pending immutable revisions and a draft
release. Its manifest matches those exact statuses. It references the current
published revisions without modifying their payloads, membership or provenance.
It intentionally fails if repeated rather than silently hiding ID collisions.

## Activation boundary

The owner authorized publication on 7 October 2026. The active production
configuration now references the separately generated published basic release.
The artifacts in this candidate directory remain unpublished review copies.
Never install this pending draft SQL in production and then
try to update revision approvals: revisions are immutable. After independent
review or explicit owner authorization to publish with that review pending,
prepare the final release INSERTs and recompute the manifest for the actual
publication statuses and honest authorization provenance before installation.
For this release, the content-bound authorization is recorded in
`content/production/basics-authorization.json`. Running
`npm run content:prepare-basics -- content/production/basics-authorization.json`
reproduces `content/production/basics-catalog.json` and
`db/seed/production-basics.sql`, without connecting to production or switching
the active configuration. Changed content requires a matching new authorization;
published corrections additionally require new immutable revision identities.

Activation must install and verify the final additive release before deploying
its matching runtime configuration. Keep the prior published release available
for existing sessions, prepared packs and guest attachment. Check B1 and B2
catalog counts, new practice, answer persistence and old-session replay. No
learner reset, history migration, legacy-data bulk import or app identity change
is part of this content work.

## Verification

`services/api/src/basic-content.test.ts` executes the generated SQL and checks
the manifest against actual joined rows, old revision preservation, immutable
guards and draft refusal. A separately labelled local activation simulation
checks both levels' 15-question sessions, distinct targets, server answer
recording, duplicate receipts and pinned old-session replay. It is local
engineering evidence, not content publication or independent German approval.

## Low-typing candidate refresh — 7 October 2026

The unpublished candidate now includes 10 B1 low-typing targets as revision 2 `gap_choice` exercises: five adjective-ending targets and five irregular present-tense verb targets (20 variants total). The prior published basic release is not modified. Candidate manifest: `2d13318c15966d0ccfb74eb102201c777c89f4f5ec33718ad219a14e0bf475eb`. Independent German review remains pending.

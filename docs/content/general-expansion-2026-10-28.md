# General German enrichment — 28 October 2026

Owner requests removing the forced five-question ending and enriching the question
database. These are related but separate changes: continuous practice needs useful
question selection and enough varied content, not a larger visible batch counter.

## Prepared pilot

`content/drafts/general-expansion.json` contains 20 original AI-authored questions:
10 B1 and 10 B2, two contexts for each of ten proposed learning targets. Topics:
perfect with haben/sein, accusative reflexives, possessive articles, seit with the
dative, je/desto, weder/noch, sowohl/als auch, statt/zu and lassen with an infinitive.
All use the existing choice-per-gap format supported by both clients, bilingual
instructions/explanations and a completed sentence. Target and exercise identities
use a new namespace; existing published revisions are untouched.

All ten editorial reviews are **pending**, and `publicationApproved` is false.
This is an authoring pilot, not a certified B1/B2 assessment, independent review,
or published expansion. No live database, release configuration or client bundle
has changed. The existing 60 unpublished workplace questions remain a separate
candidate; they are not general-practice enrichment or part of the published count.

Run from the repository root:

```powershell
node node_modules/tsx/dist/cli.mjs services/api/scripts/review-general-expansion.ts
```

This applies the existing draft validator to the pilot alongside the established
B1/B2 drafts (the validator requires at least 30 targets and multiple formats).
It checks identity linkage, distinct contexts, accepted-answer grading and review
hash rules, rejects repeated prompts against the existing drafts, and regenerates
`content/drafts/GENERAL-EXPANSION-REVIEW.md`. No database connection is used.
Structural checks do not establish that distractors, explanations or level labels
are educationally sound. The author script refuses to overwrite an existing draft.

## Next content work

1. Reuse the preserved legacy material before scaling new authorship. Read-only
   inspection of the original web repository on 28 October found 4,754 nonempty
   JSONL records under `data/pos/`: 2,806 nouns, 1,029 verbs, 614 adjectives,
   210 adverbs, 40 prepositions, 39 conjunctions, 8 particles and 8 pronouns.
   `data/wortschatz/b2-beruf.csv` contains 1,663 parsed rows; level-specific verb
   lists also remain under `db/verb-lists/`. These are source-record counts,
   not unique words, reviewed questions or extra published exercises. Sources
   overlap and contain known translation/format issues; an `approved` flag in a
   legacy file is not GM2 editorial approval. Preserve provenance and check reuse
   rights, normalize/deduplicate and review a small sample before conversion.
   Do not modify the preserved original repositories or import user/account data.
2. Review this small pilot question by question: natural German, one defensible
   offered answer, useful distractors, non-answer-revealing hints, explanation,
   level fit and genuinely distinct contexts. Record AI review honestly and bind
   any approval to the final content hash. Revise generic hints before approval.
3. Check target identity against the production catalog so new variants of an
   existing concept reuse its target rather than splitting learning evidence.
   Include existing starter and workplace targets in that editorial comparison.
4. Extend coverage with reviewed examples of mistakes and everyday contexts;
   prioritize useful variation and underrepresented objectives over raw counts.
   ChatGPT can author and assist review without adding paid inference to practice.
5. Prepare additive candidate SQL/catalog and the matching offline bundle through
   the existing immutable-release pipeline. Verify in a clean local fixture
   database, then test selection, grading and rendering on web and Android.
6. Present the exact reviewed candidate and obtain publication authorization before
   writing to production. Existing sessions retain their pinned revisions.

## Local databases

The two stopped Supabase Postgres containers from the 8 October preservation work
were isolated restore rehearsals, not the hosted production database or a complete
local Supabase service. With owner authorization they were removed on 28 October,
after all 112 files in the private backup manifest matched their saved SHA-256
hashes and the saved database/Storage restore-verification flags were confirmed.
External backup files and the Docker image were retained. Private recovery paths,
account data and credentials are excluded from this document.

Future content validation should use clean disposable fixtures/PGlite, as the
existing content scripts do, rather than reusing recovered account databases.

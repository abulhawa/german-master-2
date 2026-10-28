# Batch output contract

One planned batch has at most 20 source records and at most 40 new questions.
Use `node tools/content/enrichment.mjs start legacy-0001` once to create its
catalog and report. Resume existing files on later runs. Never regenerate the
frozen plan or change UUID allocations to work around a validation error.

## Catalog

Save `batches/legacy-NNNN.catalog.json` with exactly the same draft envelope and
target/variant structure as `content/drafts/general-expansion.json`:

```json
{"schemaVersion":1,"status":"agent-authored-draft","publicationApproved":false,"targets":[]}
```

Each authored seed produces one target with two variants. Required target fields:
`id`, bilingual `title` and `description`, precise `objective`, `category`, `level`,
`provenance`, `review` and `variants`. Review stays
`{"status":"pending","reviewer":null,"date":null,"notes":"...","reviewedHash":null}`.
Record the actual AI editorial pass in the report; do not fabricate approval.

For a new objective use the input's `proposedTargetId`. For an existing objective,
reuse its target ID and copy its title, description, objective, category and level
unchanged from the existing draft catalog. If that mapping is unclear, defer it
with the proposed match; do not duplicate or redefine the target. New exercise IDs
always use the input's two `proposedExerciseIds`, revision 1. Existing revisions
are immutable. Stable variant/context/transfer keys can use
`legacy-NNNN-L<source-line>-v1` and `-v2`; they must be globally unique.

Each variant contains `variantKey`, `contextKey`, `transferKey`, `exercise`,
`rubric` and `ambiguityNotes`. Use distinct non-null transfer/context keys for both
variants, without claiming that editorial context variation proves transfer.
Exercise formats: `choice`, `gap_choice`, `word_order`, `matching`, schemaVersion 1.
Read `docs/content/practice-formats.md` and the versioned examples under
`contracts/v2/examples/` for exact shapes. Do not introduce typed-answer formats.

Every exercise needs a German prompt, bilingual instruction and useful hint;
the rubric needs `normalizationVersion: "de-nfc-trim-v1"`, complete
`acceptedAnswers`, a concise bilingual explanation and `completedAnswer`.
The latter is `{"schemaVersion":1,"parts":[{"text":"…","emphasis":false}]}`.
For matching/ordering, use a natural complete representation of the accepted
mapping/sentence rather than pretending a single gap was filled. Keep answers
out of prompts and instructions; hints should support reasoning, not reveal them.

For choice formats, test every offered option in the exact context. Avoid two
defensible answers, implausible distractors, clues from answer length and repeated
correct-answer positions. For ordering, constrain the task explicitly and include
all accepted natural orders. Author genuinely different contexts, not punctuation
changes, subject-name swaps or paraphrases that repeat the same cue.

## Source report

Save `batches/legacy-NNNN.report.json` alongside the catalog. `start` provides all
rows. Preserve each `sourceId`/`sourceHash`. Fields:

- `schemaVersion: 1`, `batchId`, `state` (`draft` or `ready-for-review`).
- `catalogSha256`: SHA-256 of the exact saved catalog after CRLF-to-LF conversion.
- `aiReviewer`: actual AI/editor identity; do not guess an unavailable model name.
- `reviewedAt`: actual ISO date; `reviewNotes`: checks performed and unresolved issues.
- `rows`: all planned source rows, each with `sourceId`, `sourceHash`, `decision`,
  `targetIds`, `notes`, `corrections`, `rightsBasis`.

Decisions: `authored` (exactly one target and two variants); `duplicate` (explain
the known concept/source and reference existing target if relevant); `deferred`
(accuracy, rights, ambiguity or mapping unresolved); `out-of-scope` (explain).
Use `pending` only while `state` is `draft`, with no target IDs. Do not count a
deferred source as authored or generate filler to meet a quantity goal.

An interrupted run can save a valid authored subset with remaining rows pending.
A ready batch has no pending rows and a recorded question-by-question AI review.
Compute the hash after the final catalog edit. Example Node command:

```sh
node --input-type=module -e "import fs from 'node:fs';import crypto from 'node:crypto';const p='content/enrichment/batches/legacy-0001.catalog.json';console.log(crypto.createHash('sha256').update(fs.readFileSync(p,'utf8').replaceAll('\r\n','\n')).digest('hex'));"
node node_modules/tsx/dist/cli.mjs services/api/scripts/validate-enrichment.ts
```

Validation checks all saved batches together with the existing draft corpus,
retaining its minimum-size/multi-format rules. It catches source drift, missing
dispositions, duplicate identities/prompts, malformed exercise/rubric linkage and
false publication flags. It cannot establish sound German or semantic uniqueness.
Do not weaken or edit validation to accommodate a bad question; correct/defer it.

## Save and hand off

Commit only the batch's catalog/report/input. Suggested message:
`content: enrich legacy-NNNN with <N> reviewed draft questions` (AI review remains
clearly labelled, and target approval stays pending). Push the content branch;
fetch/read back the remote files and confirm the commit. If tooling creates a PR,
target `content/enrichment-workbench`. Do not merge or target main.

Return: batch ID, source dispositions, question count, corrections/deferred issues,
validation result, commit/PR URL and next batch/resume action. If checks or GitHub
writes could not run, say exactly what remains; never claim a save from a draft
in chat. CI validates content-only artifacts without production credentials.

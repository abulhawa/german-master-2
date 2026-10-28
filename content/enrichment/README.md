# GM2 question-enrichment workbench

Owner request, 28 October 2026: prepare legacy references and a resumable batch
workflow that ChatGPT web can save directly to GitHub. This branch prepares
**unpublished content**, not application behavior or a production database import.

Repository: [abulhawa/german-master-2](https://github.com/abulhawa/german-master-2).
Working branch: `content/enrichment-workbench`. Do not commit enrichment to `main`:
main automatically deploys the application. All legacy references are already in
this monorepo; no local paths, database archives, credentials or second checkout
are required. Read [AGENTS.md](AGENTS.md), [the prompt](CHATGPT-PROMPT.md),
[source references](SOURCES.md) and [the batch contract](BATCH-CONTRACT.md).

## What is prepared

- `SOURCE-MANIFEST.json`: 15 commit-pinned source files and portable SHA-256 hashes.
- `plan.json`: 3,452 B1/B2 seed records split into 175 batches of at most 20 source
  records each, interleaving word types. These are not 3,452 distinct reviewed
  concepts or a promised number of questions. Pronouns have no B1/B2-labelled
  rows in this snapshot; lower-level material remains reference-only.
- `inputs/legacy-0001.json`: first ready-to-read input with source records, row
  hashes and allocated UUIDs. Later inputs are generated from the frozen plan.
- `batches/`: catalogs and per-source reports. Empty initially; no completed
  enrichment batch or editorial approval is implied by this preparation.
- `tools/content/enrichment.mjs`: verify sources, find/resume a batch and create
  its initial files. `services/api/scripts/validate-enrichment.ts`: validate
  content, grading conformance, provenance links, identities and saved progress.
- A narrowly scoped GitHub Actions validation workflow. Its checks are structural,
  not proof of good German, licensing, learning outcomes or release acceptance.

## Start or resume one batch

Run at the repository root (Node 24 and existing root dependencies):

```sh
node tools/content/enrichment.mjs next
node tools/content/enrichment.mjs start legacy-0001
node node_modules/tsx/dist/cli.mjs services/api/scripts/validate-enrichment.ts
```

`next` identifies the first incomplete batch, using Git-tracked reports rather
than chat memory. `start` refuses to overwrite existing work; on a resumed batch,
read and edit its existing files instead. `show legacy-0002` prints a later input.
The initial source plan must not be regenerated after work begins.

For each source record, author at most one precise learning target with exactly
two new exercise variants, or explain why it was duplicated, deferred or outside
scope. A batch therefore produces **at most 40 questions**, often fewer. Quality
and meaningful variation take precedence over filling a quota. A source lemma can
be revisited under a different objective in a later separately planned expansion.

Save a structurally valid partial catalog with `state: "draft"` and unfinished
rows marked `pending` when interrupted. It will be resumed before another batch.
Once every source has a defensible disposition and AI editorial review is
recorded, set `state: "ready-for-review"`; target reviews still stay `pending`
and publication remains false. Recompute the report's `catalogSha256` after any
catalog edit, using UTF-8 text with CRLF changed to LF. Run validation, commit and
push the batch, then verify the remote files/commit. Never invent a passing check.

Do one batch per run. On the next run, use the same prompt and say **Continue the
next enrichment batch**. Do not run multiple writers against the same branch.
If working through PRs, finish/reconcile the previous batch before claiming its
sources are available for another writer.

## GitHub saving from ChatGPT web

A prompt cannot grant repository access. First verify that the selected ChatGPT
session has tools capable of writing files/commits to this repository. If it does,
use those tools on the content branch, preserving remote changes. If it only has
read access, use a GitHub-connected Codex Cloud task in ChatGPT web and select
this branch; do not claim the files were saved through a read-only connection.
[Official cloud instructions](https://learn.chatgpt.com/docs/cloud) describe
repository-connected work and reviewing/committing changes or opening a PR.
Availability and permissions depend on the user's actual account and environment.
No API key or paid model API is required by this repository workflow.

If the environment uses its own working branch, base it on
`content/enrichment-workbench` and open a PR **into that branch**, never main.
The user should see a commit/PR URL and check status after each batch. Do not
merge or deploy. A saved draft in GitHub is separate from content publication.

## After batches are saved

Review the batch diffs, corrections, deferred records, duplicate mapping and
editorial judgments. Only then prepare the additive immutable release and shared
offline bundle, test both clients and obtain publication authorization. The
current client save contract is bounded at 125 questions; a future larger bundle
requires compatible queueing rather than simply packing all new questions into
one saved session. This workbench does not change that runtime contract.

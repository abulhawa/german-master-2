# Basic practice release — 7 October 2026

The owner explicitly approved “Publish the basics now” after reviewing the
scope of the 125-exercise B1/B2 catalog and its pending independent German
review. This follows the AI editorial basis of the original starter without
claiming independent human approval. Source review records remain pending.

The release adds the existing ChatGPT-prepared 30 B1 targets / 60 exercises and
30 Codex-authored B2 targets / 60 exercises. It retains the original five B1
targets/exercises unchanged: 65 targets, 125 immutable revisions in total.
English/German instructions, hints and explanations accompany every addition.
The B2 pack scaffolds passive voice, conditionals, connectors, infinitives,
relative clauses, cases, adjectives, verb/preposition pairs and work vocabulary.

Legacy vocabulary was inspected but not bulk imported. Semantic and malformed
translation rows require corrections before reuse. These additions use original
controlled contexts, not unverified copies of legacy example sentences.
No live AI or Groq requests were made; normal practice has no AI dependency.

## Artifacts and installation

- `content/production/starter-catalog.json` retains the prior configuration.
- `content/production/basics-authorization.json` binds owner authorization to
  content hash `b9311c540876d9201d28eadec7ff065a167f42bcbdd42b0d3c12a79f9fa2164d`.
- `content/production/basics-catalog.json` is the published release snapshot.
- `content/production/catalog.json` is the active API configuration.
- `db/seed/production-basics.sql` contains the additive transaction. Never
  repeat it against a database where it is already installed.
- `content/candidates/basics/` retains the separate draft SQL/configuration and
  editorial workbook for review and a future ChatGPT editorial pass.

Release: `50000000-0000-4000-8000-000000000001`.
Manifest: `691c7e1c3dae87d770c3eb36cb19305dd330cb3427ba7f5f4917883f1fa32640`.

Installed the new release in `german-master-v2-production` before switching
the deployed configuration. Queried all 125 actual revision/target/topic rows:
their manifest matches the configuration, every accepted answer grades correct,
and the original five revisions still produce the prior starter manifest.
No learner records, permissions, schemas, policies, old revisions or membership
were updated or deleted. The old release remains published for pinned sessions,
prepared packs and guest attachment. Guest-first local starter content remains
the existing five-question pack; this expansion supplies account practice.

## Verification and limits

Local PGlite tests execute the generated SQL, enforce draft refusal and immutable
revisions, verify manifest conformance, allocate separate 15-question B1/B2
sessions, record answers and duplicate receipts, and replay old pinned sessions.
Independent German review remains open; B2 pack selection is not a proficiency
certification. Publication does not close product milestones or Android release
gates. Deployment and integrated-browser acceptance are recorded in PROGRESS.md.

The content commit `17686ff` automatically deployed as
`dpl_6mosqevefzxdhQS2QLCHA4nurPym`. Integrated-browser acceptance on the same
production artifact's isolated `german-master-v2.vercel.app` origin confirms
B2 practice is enabled, an actual 15-question session loads and its hint renders.
The custom-domain origin had an existing cross-tab practice lock; the user's
tab was left untouched. No test answers or preference changes were submitted.

Hosted verification exposed a pre-existing timestamp truncation defect in the
web client. Commit `f2d1419` preserves milliseconds so a quick answer cannot be
mistaken for a pre-session answer solely because its timestamp was rounded
down. The real HTTP regression now answers at the exact subsecond issuance
instant and checks one qualifying success after lost-response/duplicate replay.
No evidence policy, server grader or existing frozen request is changed.
Its automatic deployment `dpl_ApE99jb6zL4PNxKXBgQ8y5fAwUsY` is READY.
Hosted [Web checks](https://github.com/abulhawa/german-master-2/actions/runs/37678733138)
pass at this code commit, including all tests, normal/preview/product builds and
offline cold-launch/late-sync acceptance. Web accessibility and repository
safety also pass. Independent German review remains a separate open gate.

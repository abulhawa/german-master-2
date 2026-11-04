# Full eligible reviewed enrichment — wave 2

**Published 4 November 2026** via PR #31, main `56e38fdb`. Production now has
2,613 question revisions. See [live receipt](../../../docs/operations/enrichment-wave-2-production-2026-11-04.md)
for verified database/deployment evidence and limits. Frozen files below remain
the exact derivation inputs/outputs, not a mutable live-status manifest.

Pinned review commit `71064b6bde57f1b2cf88d36885521545cd9f4003` covers all 78
completed batches / 2,588 questions. Evidence exports bind original byte hashes,
source commits, findings, overlays and rechecks without editing authoring or the
single-writer review branch. Historical pending/publication flags remain unchanged.
Normal generation/tests read these frozen files offline; the fixed index hash in
`prepare-reviewed-expansion.ts` rejects changed evidence.

The derived candidate adds 1,233 targets / **2,466 questions** to wave 1. Together
with 22 already live this accounts for 2,488 of the reviewed questions. Fifty
whole targets / 100 questions are excluded because at least one variant has an
unresolved material defect. Every target decision and question disposition is in
`manifest.json`; no incomplete review is admitted. Ninety-one material corrections
have exact proposal-hash-bound semantic rechecks and unchanged answer identities.
Optional/style suggestions are left as reviewed. A sound sibling is also excluded
when the objective's required second variant remains materially defective.

Topic mapping follows the pinned lexical source POS. Vocabulary objectives are
lexical; the three explicit connectors grammar objectives retain grammar kind.
Explicit `work_vocabulary` keeps Work context: 27 targets / 54 questions remain
behind the existing Work gate. Other lexical objectives use General German;
mentioning a colleague in an example does not itself imply Work. Gender fields
from legacy source rows are not imported, including the known Augenblick seed
defect; reviewed exercise/target content is retained. No CEFR/rights certification
or human-review claim is made.

The new immutable release has 2,613 members: all previous 147 plus the 2,466 new
questions. Previous releases/revisions and learner data are retained. Offline
packs are individually at most 125 questions; General pools contain B1 1,283 /
B2 1,276 questions. Both clients shuffle the complete eligible pool and freeze at
most 125 for a new round. Explicit review reuses the frozen question set; topic
selection can start another bounded set. Topic navigation deduplicates pack pages.
The 125 limit applies to saved rounds, not publication or the content library.

The larger offline bundle is approximately 6.1 MB JSON and the compressed product
JavaScript bundle approximately 714 kB. The existing service-worker asset budget
is explicitly 12 MiB so the actual 3.9 MB JavaScript asset remains precached for
offline launch; no API cache, contract bound, paid service or billing change is
introduced.

Generate with `npx tsx services/api/scripts/prepare-reviewed-expansion.ts`.
Original canonical validation remains unchanged, with initial-30 as format/minimum
context only. `enrichment-expansion.test.ts` installs/replays the additive SQL,
compares immutable manifests, grades all new accepted answers and saves 125-question
rounds that remain readable with the old runtime selector. Native tests grade all
2,466 new answers and round-trip bounded saved state using the exact shared bundle.
Production status belongs in the deployment receipt/PROGRESS, not this preparation
manifest. Owner explicitly authorizes direct production without staging; Android
Play remains held and other Supabase projects must remain untouched.

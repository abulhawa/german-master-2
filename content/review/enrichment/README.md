# Frozen enrichment review checkpoint

Owner requests takeover by the original German Master Enrichment ChatGPT chat.
Read [CHATGPT-TAKEOVER.md](CHATGPT-TAKEOVER.md): 676 reviewed through 0022, with
85 inactive proposals and 61 exact-hash-bound rechecks. The full finished corpus
has 5,682 questions at b7a6507; 5,006 remain to review overall. Finish the existing
frozen queue, then use a separately pinned strictly validated extension. The
49-batch queue is only the initial slice. Local reviewer is stopped.

The Enrichment review verification GitHub workflow checks source, artifacts and
in-memory proposals on this branch; it does not publish or deploy. The snapshot
export helper supports Windows junctions and Linux symlinks for CI. Confirm
actual hosted workflow success rather than claiming a local command was run in
a GitHub-only ChatGPT environment.

Owner-authorized AI-assisted review and preparatory integration only. Snapshot:
`671e9d19f4f5a61d7916ed96b3b41433b768fd39`. Review branch starts at current
main `27fdd13f858503ddf24f7f1b393d7a70b0da5de4`, preserving Android fixes.
Source inputs remain immutable Git objects, imported into a detached isolated
workspace for inspection. Recover them with `git worktree add --detach <scratch>
671e9d19f4f5a61d7916ed96b3b41433b768fd39`; never chase authoring head.

Run `python content/review/enrichment/audit.py` from any directory. It reads only
pinned Git objects, audits LF catalog/source hashes and JSON source-row hashes,
report states/dispositions, selected rubric linkage, identities and exact duplicate
prompts/objectives within this snapshot. Inventory: 49 ready batches, 768 targets,
1536 questions; draft 0050 excluded. All 15 source-file hashes match. No exact
within-snapshot identity/key/prompt/objective duplicates found. This is not the
canonical TypeScript validator, semantic review or cross-corpus duplicate audit.
Topic/context mappings are inventoried as null where absent, not invented.

Queue key is batch ID plus exact LF-normalized catalog SHA-256. Preserve existing
unchanged queue entries when rerunning. `coverage.json` and the per-batch findings
are the current coverage evidence; the original 12-question pilot is superseded.
A complete review requires the exact set of source variant keys and assessments
of every offered option. `python content/review/enrichment/validate.py --sync-queue`
checks source hashes, coverage, immutable identities and coherent inactive patch
preconditions before saving queue progress. Run it without the switch to verify
an already saved checkpoint. Review completion does not grant candidate approval.
Proposed overlays stay inactive and need editorial recheck. Findings preserve
original exercise IDs; never change original source artifacts.

The hosted reviewer lacked allowance telemetry and paused after the original
pilot. On 29 October the owner authorized substantial supervised continuation;
the desktop coordinator has live account telemetry. The study-reserve threshold
is waived for this session. This does not establish telemetry in the hosted chat
or restart its schedule. Never use reset credits, purchases, paid API fallback or
billing changes. Refresh live telemetry before substantial dispatch.

## Repeatable structural evidence

`validate-snapshot.py --snapshot <full-commit> --dependency-root <existing-checkout>`
exports only tracked content and validator source into ignored scratch storage,
uses existing local dependencies, and runs the unchanged canonical TypeScript
validator. Dependency workspace package sources must match the pinned snapshot.
No install, network request or database is involved. Saved `structural-*.json`
reports cover both the frozen 49-batch editorial queue and a separate 137-batch
authoring snapshot. The latter does not expand or silently mark the editorial queue.
`compare-snapshots.py <earlier-full-commit> <later-full-commit>` compares those
validated inventories without changing the queue. All 49 frozen completed
catalogs and author reports are unchanged in the 137-batch snapshot, so findings
remain bound to the same inputs while the other task authors later batches.

`review-packet.py <batch-id>` prints compact JSONL from the fixed Git snapshot:
full exercise/rubric fields, target metadata and lexical source records, omitting
only repeated author self-assessment prose. Source/author approval flags remain
untrusted claims. This reduces reading overhead without generating judgments.

`quality-signals.py --snapshot <full-commit>` checks every saved completion against
its selected option where a single gap permits exact reconstruction, normalized
duplicate choices, a few encoding markers and explicit separable-infinitive error
patterns. These are triage signals, not a language proof. `offline-capacity.ts`
checks all questions as in-memory sessions of at most 125 and confirms rejection
at 126. Its level-only pages have no topic/context assignment and are not an
installable bundle. Run TypeScript probes with the existing root tsx runner.

`validate-overlays.ts` applies exact-hash-bound proposed corrections only in
memory, preserves accepted option identities and checks the resulting content
with the unchanged canonical validator. `overlay-validation.json` binds results
to exact overlay hashes. No corrected original catalogs, runtime bundles or SQL
are written by that command.

`provenance-signals.py --snapshot <full-commit>` finds literal linked-source
example overlap after whitespace normalization (case/punctuation retained;
at least five words and 25 characters). The 137-batch snapshot has three matches,
documented with exact source hashes. This does not establish copying, ownership
or infringement, and a missing match does not prove originality. The earlier
worker's claimed exact match in batch 0010 was a misreading and is corrected in
the saved finding; its common near-phrase overlap is advisory only.

Current saved continuation coverage is 606 variants through batch 0020, with 79
inactive correction proposals. The owner subsequently waived both the weekly
reserve and five-hour-headroom operating thresholds for this session and directed
continuation from 0012. Review is resumed with one worker and durable slices.
Do not consume reset credits, purchase capacity, use paid fallback or change
billing; actual service availability still applies. Current coverage remains
authoritative in coverage.json rather than this historical slice count.

`correction-rechecks.json` records coordinator semantic proposal rechecks and
scoped sentence-capitalization rechecks, all AI-assisted and inactive. Each
entry binds the exact sorted-key JSON proposal SHA-256 and source catalog hash.
`validate.py` rejects stale hashes or an `ai-rechecked-inactive` label without its
matching evidence. This does not grant target, candidate or publication approval.
Overlay fields `proposedInstruction`, `proposedHint` and `proposedExplanation`
require both languages and are applied by the in-memory canonical validation
probe. Task directions stay out of completed German answers.

Use one writer. A hosted continuation must check GitHub ref and current execution
state before writing, use expected-head fast-forward protection, and stop on overlap.
If reliable single-execution ownership cannot be established, do not write. Quiet
while blocked/unchanged. No app builds, device suites, merge, deployment, publication,
live database operation or Android release. Disable the job schedule when all 49
batches have honest dispositions. Candidate manifest lists integration blockers.

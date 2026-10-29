# Frozen enrichment review checkpoint

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

Use one writer. A hosted continuation must check GitHub ref and current execution
state before writing, use expected-head fast-forward protection, and stop on overlap.
If reliable single-execution ownership cannot be established, do not write. Quiet
while blocked/unchanged. No app builds, device suites, merge, deployment, publication,
live database operation or Android release. Disable the job schedule when all 49
batches have honest dispositions. Candidate manifest lists integration blockers.

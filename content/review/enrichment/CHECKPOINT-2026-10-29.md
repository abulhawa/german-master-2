# Editorial review checkpoint, 29 October 2026

## Latest continuation: legacy-0042

All 34 source variants of 0042 received question-by-question and four-choice editorial review. Serious grammar defect: the original L296-v1 completed sentence has **zu eindämmen** where the separable-verb zu-infinitive is **einzudämmen**. An inactive, not-yet-rechecked overlay changes the task stem to use modal **möchte ... eindämmen**, preserving the answer ID and one gap. No source, client, published-content or canonical-validator changes.

Latest totals: **42 of 49** original batches complete; **1,318 / 1,536** in frozen queue, **1,318 / 5,682** full corpus, **4,364 remaining** (218 first-queue plus 4,146 extension), **166 inactive proposed corrections, 119 exact-hash rechecked, 47 awaiting recheck**. Next batch **0043**. The older checkpoint figures below are historical.



## Latest continuation: legacy-0041

Reviewed all 36 variants in batch 0041, checking the four real alternatives and the bilingual teaching text. Added three inactive corrections pending recheck: improve the idiom `hohe Bedienbarkeit` to `gute Bedienbarkeit`, scope `Bedingung` to hiring rather than merely filing an application, and improve the construction `Beihilfe zu ihren Behandlungskosten`. No original inputs, app, client or validator were changed. Exact result and CI evidence are stored in the corresponding batch artifact and overlay-validation report.

At this continuation: 41 complete first-queue batches, **1,284/1,536** first-queue variants and **1,284/5,682** overall, **252** first-queue questions plus 4,146 later remain (**4,398** total); **165** inactive proposals, **119** exact-hash rechecked, **46** still awaiting recheck. Next batch: **0042**. The initial values later in this checkpoint refer to the previous 0040 commit.



Sole-writer check at start: both German Master automation schedules disabled; no open review PR; branch head `473a7cb88de418efae019c918b68af275eb7ef30`. Explicit owner continuation allows this review-only slice. All changes made with expected-head fast-forward protection. Historical handoff counts in README/PROGRESS/CHATGPT-TAKEOVER are superseded by `coverage.json`.

## Completed in this checkpoint

- Independently reviewed all 32 questions in `legacy-0040` (16 lexical targets), with actual choice-by-choice reasoning, complete-answer grammar, EN/DE hints/explanations, source and provisional level commentary.
- One serious competing-answer risk: `legacy-0040-L274-v1` could describe an advertising/subscription-funded nonprofit while marking `gemeinnützige` as wrong. Added a narrowly constrained **inactive**, un-rechecked prompt/hint/explanation proposal explicitly establishing owner profit intent. Do not activate before exact-hash AI recheck and further editorial acceptance.
- Flagged optional precision about intensity vs effort (`L256-v1`), innovations that need an established first-use cue (`L254-v2`), the universal-sounding image license claim (`L274-v2`), and technically discrete per-second sampling (`L280-v1`). These are not evidence of release acceptance.
- Verified source `adjectives.jsonl` lines 254–281 against the authored targets. Source gloss `kommerziell` is rendered 'commercially' (English adverb) whereas the target teaches adjective 'commercial'; this should be corrected in future mapped lexical metadata without rewriting the immutable source.

## Durable state

- Original frozen 49-batch queue: **40 complete**, **1,248/1,536 reviewed**, **288** remaining in **0041–0049**.
- Whole fixed corpus: **1,248/5,682** reviewed; **4,434** remain including 4,146 in 0050–0175.
- **162 inactive proposals**, **119 existing exact-hash AI rechecks**, **43 awaiting recheck** (including new L274-v1 proposal); none published.
- Extension inventory/strict queue for 0050–0175 is not yet created. Main, authoring branch, application, canonical validators, live database and release remain untouched. All candidate/publication approvals remain false.

## Next action

Review `legacy-0041` question-by-question against its exact frozen catalog SHA and synchronize `queue.json`, `coverage.json`, candidate overlay list and any precise correction proposal. After 0049 establish a separate hash-pinned and nonoverlapping strict queue for final snapshot `b7a6507e135895b1ebb671b73538a9a6d3742304`. The deferred corrections need genuine semantic rechecks; do not auto-promote status by counts. Validate full artifact/overlay/source via the unchanged hosted verification workflow on the review branch. Any workflow result must be checked before calling it passing.

# Remaining enrichment corpus: review continuation index

Created on 30 October 2026 from the pinned, already canonically validated final snapshot `b7a6507e135895b1ebb671b73538a9a6d3742304`.

The original 49-batch review is complete: 1,536/1,536 questions. This separate index includes exactly 126 non-overlapping ready batches `legacy-0050` to `legacy-0175` with catalog/report SHA-256 values copied from the committed structural report. It starts **0/4,146** reviewed. No extension variants have yet received the separate question-level review.

The index and hash metadata are prepared, **and its read-only validator and dedicated CI step have been added**. Do not interpret the metadata index alone as strict coverage validation, editorial approval, linguistic acceptance, or publication readiness. Before marking any entry complete, verify each catalog's bytes against its listed SHA-256 and the report hash at the exact pinned Git commit, validate the exact set of variants/options and identities, then save a reviewed findings artifact with explicit dispositions. Keep any corrections inactive until exact proposal recheck. Preserve the original queue and avoid double-counting its 49 batches.

Resume at `legacy-0050` on `content/enrichment-review` after verifying single-writer ownership and remote head. Do not modify authored inputs, app code, canonical validators, main, live DB, content release or deployments. No candidate or publication approval has been granted.

## Updated validation protocol (30 October 2026)

`validate-extension.py` independently verifies the exact hashes of all 126 pinned author catalogs and reports, all 4,146 variant identities, exact option labels, each saved review and inactive correction's unchanged accepted option. Its GitHub Actions step is scoped to `content/enrichment-review` and cannot activate or publish content. Inactive extension corrections live in `extension-proposals/` to avoid mixing them with the first49 overlay validator. Current extension review is through 0052 (104 questions) and resumes at 0053. Verify the latest Actions conclusion before relying on this gate.


## Continuation checkpoint: 30 October 2026

At pinned final snapshot `b7a6507e135895b1ebb671b73538a9a6d3742304`, **five extension batches 0050–0054 are completed**, containing 24+40+40+40+40 = **184 independently assessed questions**. Total across both queues **1,536 + 184 = 1,720 / 5,682 (30.27%)**, with **3,962 questions pending** (121 extension batches, beginning with **0055**). This checkpoint is derived from the exact committed reviewed variant lists, not inferred from batch names alone.

The 0051-L364-v1 cold-exposure premise has one **inactive, un-rechecked** proposed correction in `extension-proposals/legacy-0051.overlay.json` that retains the scored word `erkälten` and instead asks about close contact with sick people. Original frozen queue retains **171 inactive proposals, 119 rechecked, 52 pending recheck**. Extension proposal count is **1**, pending separate exact-hash recheck; hence grand total is **172 inactive proposals** and **53 needing recheck**. The other reviewed batches had no blocking answer errors. Minor nonblocking editorial notes are in each findings file.

Review-only validator `validate-extension.py` and review CI step check exact source catalog/report hashes, option-by-option assessments, 126-batch coverage and inactive extension corrections. Original review and canonical validators remain unchanged. Check the newest GitHub Actions run, not the checkpoint, to know whether CI passed for the current head.

Single active writer; fast-forward/expected-head commits only. No scheduled tasks created; no source/authoring, app, live DB, integration, main, deployment or publication changes. Content/candidate approvals remain false. **Next first incomplete: legacy-0055.**


## Latest resume point: legacy-0056 (30 October 2026)

Since the earlier checkpoint, independently reviewed and committed all 40 original variant/options in **legacy-0055**, with bilingual guidance, full German answers, source provenance, grammar and lexical meaning. Small nonblocking note: square tiles described solely as 20 cm long and wide are conventionally rectangular but strictly need right-angle information to prove mathematical square. No blocking answer/key defect identified, and no new proposal added.

**Current: first49 1,536 reviewed + extension 224/4,146 reviewed (six batches 0050–0055) = 1,760/5,682 questions, 30.97% of complete corpus. Remaining 3,922 questions, 120 extension batches.** First incomplete **0056**, hash from extension-queue.json. Original queue 171 inactive correction proposals, 119 exact-hash rechecks and 52 pending; extension one inactive, unrechecked proposal; total 172 inactive corrections, 53 pending AI recheck. No change to source, app, canonical validators, main, release, candidate flags or database; review-only CI now validates source hashes and exact saved coverage of both queues. Continue one writer, expected-head fast-forward, per-question substantive assessment. This section supersedes earlier counts.

## Authoritative checkpoint: 30 October 2026, resume legacy-0062

These numbers supersede earlier checkpoints. The original 49 batches contain 1,536 reviewed questions. Extension batches 0050-0061 contain 434 reviewed questions across 12 completed batches. Total 1,970 / 5,682 reviewed (34.67%); 3,712 questions and 114 extension batches remain. First incomplete: legacy-0062.

This manual run independently checked all 210 actual question variants in batches 0056 (34), 0057 (34), 0058 (34), 0059 (32), 0060 (40), and 0061 (36), including each true option, completed German, syntax, bilingual feedback, original targets and source.

Three inactive extension corrections are staged in extension-proposals for 0051 (common-cold causality), 0060 (freigenommen versus freigehalten option ambiguity), and 0061 (dialogue-free versus truly silent film). None are activated or rechecked. Original frozen review has 171 inactive proposals (119 rechecked, 52 awaiting recheck). Combined 174 inactive proposals, 55 awaiting editorial recheck. Original source accepted option identities are unchanged.

Review-only validation checks the frozen 126-batch sources, full question identities and exact reviewed coverage, with proposals pinned and inactive. Do not change original validators, authored sources, app code, main, DB, deployment, publication or scheduled tasks. Confirm latest CI conclusion on the actual branch head before claiming success. Always verify sole writer and use expected-head fast-forward protection.

## Latest continuation: 30 October 2026, resume legacy-0063

This section supersedes all earlier counts. Source authoring snapshot remains b7a6507e135895b1ebb671b73538a9a6d3742304. First 49 batches independently reviewed 1,536 questions. Extension batches 0050-0062 reviewed 468 questions in 13 completed batches, leaving 3,678 unreviewed questions in 113 extension batches, starting with legacy-0063. Entire published-input corpus is 5,682 questions and 2,004 (35.27%) have completed independent question-level AI editorial review. Save and reconcile queue and coverage counters on each commit.

Manual continuation 30 October 2026 completed legacy-0056 34 questions, 0057 34, 0058 34, 0059 32, 0060 40, 0061 36, 0062 34, totaling 244 actual question-level AI editorial passes with all four presented choices and their semantic fit, original accepted option, full German, EN/DE hint and explanation, source, objective and provisional CEFR. Findings saved in corresponding legacy-XXXX.findings.json files. Unedited original input catalogs remain pinned.

Extension correction proposals are separately stored in extension-proposals/ for 0051 cold causation, 0060 ambiguous freigehalten vs freigenommen, and 0061 silent versus dialogue-free film. All three are unapproved, inactive and need an independent correction recheck. Original review has 171 inactive proposals (119 rechecked, 52 un-rechecked). Combined total 174 inactive proposals, 55 pending recheck. Batch 0062 includes nonblocking notes on Bürger (citizen vs resident) and the old vocational label Bürokaufmann; no source patch or approval for these.

Single writer expected-head fast-forward GitHub commits only; 126-batch exact hash and question coverage validator in review CI. Check latest GitHub Actions result. No source authoring, app code, main branch, canon validators, live DB, deployment, publication or scheduled-task changes were made. Continue with earliest incomplete legacy-0063.

## Latest checkpoint: 31 October 2026, resume legacy-0070

This section supersedes all prior numeric checkpoints. Authored snapshot b7a6507e135895b1ebb671b73538a9a6d3742304 stays fixed. First-49 original review has 1,536 independently assessed questions. Extension 0050-0069 has 734 individually assessed questions in 20 completed batches. Thus full corpus is 2,270 / 5,682 reviewed (39.95%), with 3,412 questions and 106 extension batches remaining. First incomplete batch is legacy-0070.

This manual continuation completed 0063 (38), 0064 (40), 0065 (34), 0066 (38), 0067 (40), 0068 (38), 0069 (38): 266 question-by-question AI editorial reviews. Every assessment records all four actual offered choices, accepted German completion and grammar, bilingual hints/explanations, source and provisional level. Findings are saved as legacy-XXXX.findings.json; authored catalogs and canonical validators untouched.

Four separate inactive extension proposals exist in extension-proposals for 0051 cold-causality wording, 0060 freigehalten versus freigenommen answer ambiguity, 0061 a dialogue-free film that is not necessarily silent, and 0069 'die Felswand klettern' revised to 'an der Felswand klettern' without changing the accepted option. All proposals active:false and candidateReady:false, pending independent editorial recheck. Original 49 queue has 171 inactive proposals, 119 rechecked and 52 awaiting recheck. Combined totals: 175 inactive proposals, 56 needing recheck.

Review-only CI checks exact pinned source hashes and strict question coverage of the 126 extension batches as well as the original frozen review. Check the newest GitHub Actions run before claiming success on the latest head; there is no editor approval for publication. Preserve single active writer and expected-head nonforced branch updates. No change to main, authoring, app, live DB, release, deployment, original validators, or scheduled tasks.

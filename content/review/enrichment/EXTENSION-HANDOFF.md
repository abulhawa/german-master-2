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

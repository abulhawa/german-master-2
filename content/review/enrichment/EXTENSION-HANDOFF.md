# Remaining enrichment corpus: review continuation index

Created on 30 October 2026 from the pinned, already canonically validated final snapshot `b7a6507e135895b1ebb671b73538a9a6d3742304`.

The original 49-batch review is complete: 1,536/1,536 questions. This separate index includes exactly 126 non-overlapping ready batches `legacy-0050` to `legacy-0175` with catalog/report SHA-256 values copied from the committed structural report. It starts **0/4,146** reviewed. No extension variants have yet received the separate question-level review.

The index and hash metadata are prepared, **and its read-only validator and dedicated CI step have been added**. Do not interpret the metadata index alone as strict coverage validation, editorial approval, linguistic acceptance, or publication readiness. Before marking any entry complete, verify each catalog's bytes against its listed SHA-256 and the report hash at the exact pinned Git commit, validate the exact set of variants/options and identities, then save a reviewed findings artifact with explicit dispositions. Keep any corrections inactive until exact proposal recheck. Preserve the original queue and avoid double-counting its 49 batches.

Resume at `legacy-0050` on `content/enrichment-review` after verifying single-writer ownership and remote head. Do not modify authored inputs, app code, canonical validators, main, live DB, content release or deployments. No candidate or publication approval has been granted.

## Updated validation protocol (30 October 2026)

`validate-extension.py` independently verifies the exact hashes of all 126 pinned author catalogs and reports, all 4,146 variant identities, exact option labels, each saved review and inactive correction's unchanged accepted option. Its GitHub Actions step is scoped to `content/enrichment-review` and cannot activate or publish content. Inactive extension corrections live in `extension-proposals/` to avoid mixing them with the first49 overlay validator. Current extension review is through 0052 (104 questions) and resumes at 0053. Verify the latest Actions conclusion before relying on this gate.

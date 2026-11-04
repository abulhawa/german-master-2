# Enrichment history-scan diagnosis

PR #30 run 41819728961 scanned 1,434 commits and reported 8,072 findings. Main's
last passing run 40744180227 scanned 1,086 commits. The policy-only PR diff did
not introduce credential text. Reproducing the unchanged pinned Gitleaks 8.30.1
scan locally produced the same 8,072 findings. Every finding was classified from
its exact Git file/commit/line without printing any credential:

* 8,004 `variantKey` values matching `legacy-NNNN-LN-v1` or `-v2`;
* 68 `contextKey` values matching `legacy-NNNN-LN-scenario-1` or `-2`;
* all were the `generic-api-key` rule, inside enrichment content/review JSON.

The added `.gitleaks.toml` inherits every default rule. Only that generic rule
receives an AND exception requiring an enrichment JSON path and a complete line
containing the exact public identity format. The exception also covers the frozen
wave-1 evidence/manifest exports, which preserve the same identities. No rule,
commit, file tree or history range is disabled; scanner version/checksum and
workflow command remain unchanged. Arbitrary values in the same key fields are
still scanned. The history scan with this configuration reports zero findings.

A local fixture in an allowed content path tested both legitimate identity
formats and two unrelated high-entropy dummy values (`api_key` and `variantKey`).
The identity values were ignored and both dummy credential-shaped values were
detected, confirming that the exception does not blanket-ignore those files or
field names. Redacted scanner reports and probe files remain ignored local
artifacts. Hosted results must be checked on the actual pushed PR commit.

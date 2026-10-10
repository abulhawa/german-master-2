# Consultant fixes — 10 October 2026

Owner instruction: implement and commit each slice without running tests, builds, lint or browser/device verification. All implementation below is **unverified**. M1 stays active; later milestone work remains queued. No publication, deployment, push or release is included.

PR #16 at `dd23fed` and its PR #15 baseline are integrated locally, preserving the prior cold-start evidence commit. No GitHub merge was performed.

| # | Fix | Status | Commit | Deferred verification |
| --- | --- | --- | --- | --- |
| 1 | History-aware variant selection; prefer missing qualifying contexts, then least-presented variants; frozen replay preserved | Implemented, unverified | `f0a9554` | Multi-session mastery reachability, prepared packs, SQL adapters, replay |
| 2 | Authored applied contexts, two transfer identities per grammar target, and structural coverage gate | Implemented, unverified | This slice | Content review, transfer reachability, immutable releases |
| 3 | Align all authored assessments with their objectives | Pending | — | German editorial review and client exercise previews |
| 4 | Independent progress freshness | Pending | — | Failed refresh followed by local edits and navigation |
| 5 | Bilingual objectives and examples | Pending | — | Catalog generation and both-client detail screens |
| 6 | Structured completed-sentence feedback | Pending | — | Contract conformance, both clients, old-content fallback |
| 7 | Password recovery and confirmation resend | Pending | — | Recovery links, expired links, identity isolation, delivery |
| 8 | Native learner terminology | Pending | — | English/German native journeys and recovery states |

## Implementation notes

- Selection policy becomes `mixed-selection-v2`. Existing sessions keep their pinned questions and version. Presentation history includes issued sessions/packs so unanswered or reserved variants do not monopolize selection.
- Regression tests and all execution verification are deliberately deferred, not passed or waived for release.

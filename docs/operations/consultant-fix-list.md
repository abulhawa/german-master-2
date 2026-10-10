# Consultant fixes — 10 October 2026

Owner instruction: implement and commit each slice without running tests, builds, lint or browser/device verification. All implementation below is **unverified**. M1 stays active; later milestone work remains queued. No publication, deployment, push or release is included.

PR #16 at `dd23fed` and its PR #15 baseline are integrated locally, preserving the prior cold-start evidence commit. No GitHub merge was performed.

| # | Fix | Status | Commit | Deferred verification |
| --- | --- | --- | --- | --- |
| 1 | History-aware variant selection; prefer missing qualifying contexts, then least-presented variants; frozen replay preserved | Implemented, unverified | `f0a9554` | Multi-session mastery reachability, prepared packs, SQL adapters, replay |
| 2 | Authored applied contexts, two transfer identities per grammar target, and structural coverage gate | Implemented, unverified | `b9f4833` | Content review, transfer reachability, immutable releases |
| 3 | Audited 60 objectives; rewrote connector/collocation choices, removed ordering-rule and title leakage | Implemented, unverified | `6af2218` | German editorial review and client exercise previews |
| 4 | Dedicated progress freshness, retained across local operations and cleared only by successful snapshot refresh | Implemented, unverified | `239128d` | Failed refresh followed by local edits and navigation |
| 5 | Authored bilingual descriptions and examples for 60 targets; candidate builder uses descriptions | Implemented, unverified | This slice | Catalog generation and both-client detail screens |
| 6 | Structured completed-sentence feedback | Pending | — | Contract conformance, both clients, old-content fallback |
| 7 | Password recovery and confirmation resend | Pending | — | Recovery links, expired links, identity isolation, delivery |
| 8 | Native learner terminology | Pending | — | English/German native journeys and recovery states |

## Implementation notes

- Selection policy becomes `mixed-selection-v2`. Existing sessions keep their pinned questions and version. Presentation history includes issued sessions/packs so unanswered or reserved variants do not monopolize selection.
- Regression tests and all execution verification are deliberately deferred, not passed or waived for release.

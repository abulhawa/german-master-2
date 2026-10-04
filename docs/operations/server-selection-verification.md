# Server-selected session verification

4 October 2026. Local fixture-only continuation from `4085fa5`.

Implemented `mixed-selection-v1` in `packages/learning-engine/src/selection.ts` and wired it into `FoundationStore.createSession`. [ADR 006](../adr/006_server_selected_sessions.md) records allocation, bounded urgency, manageable opening, reinforcement fallback and catalog limitations.

## Evidence

- Pure synthetic catalog tests verify 8/5/2 allocation, pool reallocation, input-order independence, exact deadline eligibility, capped backlog urgency, stable ties, one question per target and interleaving.
- PGlite tests create genuine accepted correct/incorrect histories for one learner. Another learner remains new. Before next-day eligibility, new material wins; at the saved Berlin midnight UTC deadline, the failed target wins a one-question request. A three-question request opens with a due retention question, then the weakness and new material. Cached `isDue` remains false in storage; selection updates no projection or sync change.
- Concurrent same-request session creation returns one allocation. Clock changes and target retirement do not alter replay. Fresh requests exclude retired targets, incompatible capabilities cannot fill quotas, and unavailable requests leave no session. Responses omit solutions; questions save immutable assessment/reinforcement roles and session selection version.
- Existing grading, ownership, exposure, rollback and receipt-policy regressions run with adaptive question order. Test answer fixtures locate questions by exercise identity. The assistance test prepares two eligible assessments before receiving either result, proving a pinned assessment remains an assessment after another session advances its deadline. Early extra practice receives grading without independent evidence. Filesystem restart verification now also replays the original session request.

## Local checks

Root `npm ci`, `npm run check`, `npm test` and `npm run build` passed with portable Node 22.23.3/npm 10.9.9. Backend: six files / 57 tests. Web: 78 files / 301 tests. A targeted nine-test API rerun passed after adding session restart replay; final backend checks/tests and launcher build were repeated after preserving the stored release ID on replay.

Dependency installation reports the existing 12 audit findings (six moderate/six high); dependency changes are outside this slice. Browserslist reports stale data. No new dependencies were introduced. Android, browser and device checks were not rerun: no client rendering, native code, generated transport or tokens changed. Existing HTTP tests exercise the session route; selection-specific contrasting-history tests use the store transaction boundary. No native/runtime journey acceptance is claimed.

Staged diff and full-history Gitleaks 8.30.1 scans passed with zero findings; the final history scan covered 777 commits at `7f4a2ec`. Both applicable hosted workflows passed at this exact implementation commit: [Web checks](https://github.com/abulhawa/german-master-2/actions/runs/37191977392) and [Repository safety](https://github.com/abulhawa/german-master-2/actions/runs/37191977396). Android CI did not trigger for this server-only change.

## Remaining gates

This is a five-draft-target local demonstration, with one variant per target. It does not prove reviewed content diversity, profile-level/topic filtering, seeded variant variation, production release selection, production authenticated identity or independent network PostgreSQL connections/roles. PGlite tests prove one embedded adapter. M0/M1 acceptance and M2 production gates remain open. Next: versioned owned target/sync snapshots with current due eligibility and cursor semantics, then complete learner navigation and shorter-session handling.

No live AI/Groq inference, production mutation, content publication, deployment or store release occurred.

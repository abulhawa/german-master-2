# Consultant fixes — 10 October 2026

Owner instruction: implement and commit each slice without running tests, builds, lint or browser/device verification. All implementation below is **unverified**. M1 stays active; later milestone work remains queued. No publication, deployment, push or release is included.

PR #16 at `dd23fed` and its PR #15 baseline are integrated locally, preserving the prior cold-start evidence commit. No GitHub merge was performed.

| # | Fix | Status | Commit | Deferred verification |
| --- | --- | --- | --- | --- |
| 1 | History-aware variant selection; prefer missing qualifying contexts, then least-presented variants; frozen replay preserved | Implemented, unverified | `f0a9554` | Multi-session mastery reachability, prepared packs, SQL adapters, replay |
| 2 | Authored applied contexts, two transfer identities per grammar target, and structural coverage gate | Implemented, unverified | `b9f4833` | Content review, transfer reachability, immutable releases |
| 3 | Audited 60 objectives; rewrote connector/collocation choices, removed ordering-rule and title leakage | Implemented, unverified | `6af2218` | German editorial review and client exercise previews |
| 4 | Dedicated progress freshness, retained across local operations and cleared only by successful snapshot refresh | Implemented, unverified | `239128d` | Failed refresh followed by local edits and navigation |
| 5 | Authored bilingual descriptions and examples for 60 targets; candidate builder uses descriptions | Implemented, unverified | `7785e36` | Catalog generation and both-client detail screens |
| 6 | Optional versioned rubric/evaluation feedback parts, generated TS/Kotlin contracts, web/native rendering and offline propagation | Implemented, unverified | `956c90a` | Contract conformance, both clients, old-content fallback |
| 7 | Localized recovery/resend forms, PKCE callback screen, verified-subject password update, expiry/error states | Implemented, unverified | `2cad943` | Recovery links, expired links, identity isolation, delivery |
| 8 | Native progress, practice, privacy and provider copy aligned; shared bilingual terminology guide | Implemented, unverified | `e69b4f3` | English/German native journeys and recovery states |

## Implementation notes

- Selection policy becomes `mixed-selection-v2`. Existing sessions keep their pinned questions and version. Presentation history includes issued sessions/packs so unanswered or reserved variants do not monopolize selection.
- Regression tests and all execution verification are deliberately deferred, not passed or waived for release.

- Fix 6 adds optional `completedAnswer` to v2 evaluations/offline rubrics, with schema version 1 and text/emphasis parts. Solutions remain in rubrics and feedback, not online question payloads. Older content keeps its existing answer display. Kotlin optional fields default to null; wire omission is accepted, explicit null is rejected by shape parsing.
- Contract code generation and unpublished candidate artifact generation were performed as implementation steps. The candidate writer used `--write-only`, skipping candidate SQL execution. No tests, builds, lint, browser or device verification were run. Production catalogs and SQL remain untouched. Source approvals are pending.

- Fix 7 provider redirect allowlisting and delivered-email behavior remain unverified. Consumed recovery links require a fresh email after reload. See `account-recovery.md`. No live emails or auth writes were made.

## Deferred verification handoff

All eight implementation slices are committed locally on `fix/consultant-review`. They are not acceptance passes.

1. Run generated-contract checks, TypeScript checks, backend/web/HTTP tests, web builds and Android unit/lint/debug assembly. Update regression expectations for intentional policy, content-revision and copy changes; do not weaken behavioral assertions.
2. Add multi-session selection/retention integration cases with two contexts, history from prepared packs, repetition, skips, failed answers and frozen replay. Exercise real PostgreSQL query behavior and query cost.
3. Review all 60 changed draft targets, answer alternatives, descriptions, transfer adequacy and structured feedback. Regenerate the historical draft workbook, then record fresh AI editorial approvals by hash. Candidate generation does not constitute editorial approval.
4. Exercise stale progress after failed refresh followed by local saves, navigation, reload and successful sync.
5. Exercise optional feedback parsing/serialization, old-content fallback, web online/offline/guest rendering and native feedback at enlarged text sizes.
6. Exercise recovery-event ordering, failed PKCE, consumed links, account switches during password update, provider policy errors and delivered-email recovery/resend. Check provider redirect allowlisting separately before deployment. No provider settings were changed.
7. Complete the existing M1 design/manual web accessibility acceptance. Keep the owner-accepted TalkBack scope closed. Later milestone gates remain queued.

Commit sequence: local WIP integration `111c9ee`; fixes `f0a9554`, `b9f4833`, `6af2218`, `239128d`, `7785e36`, `956c90a`, `2cad943`, `e69b4f3`.

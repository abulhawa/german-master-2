# German Master 2.0 Executive Summary

**3 October 2026 · Recommended direction following source inspection**

German Master should become the practice companion for B1–B2 learners who keep making the same German mistakes. Its promise is simple: find what needs practice, give a useful mixed session, and verify that improvement lasts.

Build one coherent experience across web and Android: Home, Practice, Progress, and Topics. A ten-minute session mixes vocabulary, verb forms, articles/plurals, cases/prepositions, adjective endings, word order, and controlled sentence completion. Feedback explains each correction. Progress shows Needs practice, Improving, and Mastered. Full courses, social features, speech assessment, AI chat, and unrestricted essay grading stay outside the initial release.

**Use one monorepo, with React web and native Kotlin Android apps.** Retain their useful foundations. Share API contracts, exercise schemas, content, design tokens and test fixtures. Generate TypeScript and Kotlin API models; keep client UI native. The backend is a separately owned product service inside the repo and is the only authority for confirmed grading, mastery and scheduling.

The inspection found a concrete reason to change the boundary: web uses task/submission APIs, while Android reads task tables and writes practice history directly through Supabase. Sharing tables has not produced one shared learning system. Both clients should move to a versioned product API while retaining the same environment identity provider.

**Offline support means prepared sessions and durable answers.** Clients download a session with pinned exercise revisions, provide provisional feedback offline, and sync immutable attempt IDs. The backend regrades and reconciles evidence. Clients do not merge mastery scores. Interrupted sessions survive restart, and switching accounts cannot attach one person's answers to another.

**Reset into a new environment.** There are no learner histories to migrate. Preserve both original repos, database/content exports, provenance, and signing recovery before restructuring. Audit and selectively reuse reviewed content. Use new local cache namespaces; verify update behavior for old test installations. Preserve Android application identity and signing continuity unless a separate store listing is intentionally selected.

The proposed delivery sequence is:

1. Preserve the legacy baseline and verify builds, deployment, database access, and Android store identity.
2. Establish the monorepo, shared contracts/tokens, target-centered database, and initial reviewed content.
3. Prove backend grading, adaptive selection, mastery, idempotency and evidence replay.
4. Deliver a complete web journey, then native Android parity.
5. Verify offline, cross-device consistency, privacy actions and release recovery.
6. Run a small usability and delayed-retention pilot before public release.

Allow approximately **13–19 weeks as an initial planning range**, then re-estimate after the baseline audit. Content review and offline consistency are the likely critical path. The pilot includes delayed-retention follow-up after its first two weeks. Milestone exit evidence takes precedence over calendar targets.

Measure **retained resolution of previously weak targets**, not time spent or streaks. The initial mastery policy requires multiple unassisted successes across dates and contexts; it is a proposed operational rule that needs pilot calibration. Same-day repetition and hints cannot manufacture mastery.

The next execution slice is preservation, baseline verification, monorepo rehearsal, shared contracts/design tokens, a fresh schema, and 30 reviewed targets. It ends with both clients rendering the same sample exercise contracts. Production cutover follows only after the full release gates.

Source inspection covered web revision `f1ccc88113d6f636b080117b11af0e24c5fb9a87` and Android revision `3d09b26b1becf5cba6d3b06788e4b72c1623a265`. Runtime tests, deployed database policies and store status remain M0 verification work. Neither repository nor any database was changed during this analysis.

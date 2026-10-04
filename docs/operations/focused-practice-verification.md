# Local catalog and focused practice verification

4 October 2026. Changes started at clean `2857ba7`; the prior web/safety workflows were confirmed successful at `55cfbd1` using GitHub CLI.

## Implementation

- V2 solution-free catalog and target/topic focused request contracts generate TypeScript/Zod and native Kotlin transport models. Shared `catalog.json` is used by both clients' tests. Metadata is authored as an agent-reviewed draft; independent German-language review remains pending.
- Authenticated `/v2/catalog` reads release-linked database targets and topic titles; explicit unpublished status, no-store, strict query rejection and no learner data. Focused `/v2/sessions` filters by target or database skill/topic relation before the same authoritative policy. Replay and conflicting payloads share the mixed-session owner namespace.
- Development-only `/renovation` adds Topics, topic detail and target detail from Progress. Focused creation, draft, frozen attempt, feedback and confirmed summary use the existing fixture persistence flow. Old mixed records remain readable. Unfinished practice is preserved when browsing another target.

## Local evidence

- Root `npm ci` passed after stopping stale project API/Vite processes holding a native module. The first install failed with Windows EPERM; no files or unrelated processes were deleted. Existing audit output: 12 vulnerabilities (6 moderate, 6 high).
- Root generation/type check and web/API/server build passed. Web: 79 files / 315 tests passed on the corrected rerun. Backend: eight files / 66 tests passed. New tests cover authenticated solution-free metadata, scope and capability filters, unknown scopes/count rollback, concurrent owned replay, changed-focus/mixed conflicts, reinforcement before due, assessment at due, retirement/replay and strict HTTP transport.
- Android offline `testDebugUnitTest lintDebug assembleDebug` passed: 26 suites / 100 tests, zero failures/errors/skips; lint zero errors / 16 existing warnings. New contract test round-trips metadata and focused polymorphic transport and rejects an extra rubric field. No device/emulator or native product journey verification is claimed.
- Browser over local API: keyboard topic activation and heading focus; target detail; actual one-question target-focused session; saved draft restored after reload; acknowledged feedback, summary 1/1, Learning and saved deadline in Progress. Completed all five forms in a topic-focused session with summary 5/5; the repeated noun target stayed at one qualifying check. English/German and light/dark target detail were inspected at 320px; no horizontal overflow (document width 305 at viewport 320), visible controls at least 48px. Navigation padding was corrected after an initial word wrap. Browser error log was empty. The original `127.0.0.1` preview storage was preserved; browser tests used the separate `localhost` origin.
- Screenshot evidence is ignored locally at `.local/focused-target-detail.jpg`; it is illustrative fixture data, not learner acceptance.

Hosted results are recorded in `PROGRESS.md` after completion. An initial new test failed due to an incorrect expected language-control label; the label was corrected before the rerun. No full screen-reader, zoom, real-device or learner-usability acceptance is claimed in this slice.

## Remaining boundaries

The catalog is five unpublished single-variant drafts. Published catalog pagination/eligibility, review, target history/explanatory examples, profile setup/level filtering, seeded variants, production authentication/database, durable offline/multi-tab reconciliation and native 2.0 parity remain open. M0/M1 remain in progress; M2/M3 remain in progress. Zero AI/Groq calls, production mutation, deployment, content publication or store release.

See [ADR 009](../adr/009_local_catalog_focus.md) for scope and intentional legacy design differences.

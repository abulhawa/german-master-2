# Native Topics and focus verification

4 October 2026. Debug learner preview only; production and release navigation are unchanged.

Topics opens catalog topic detail, which links to target detail and focused Practice. Progress also opens target detail. Details display bilingual metadata, level, availability, confirmed learner state, qualifying checks, saved due flag and server deadlines in the profile timezone. Shared freshness text labels saved snapshots. Catalog zero/unavailable states are explicit. Targets request one question; topics request up to five available questions. The API remains responsible for selection, grading, reinforcement and retained evidence.

## Evidence

NativeFocusTest verifies AtomicFile restart after lost focused-session response, identical UUID/focus/count/capabilities on retry, saved draft restoration, exclusion of another focus, zero availability, pending preferences, bounded topic count and failed pre-send persistence with zero network calls. Existing native Practice tests continue to cover mixed drafts, frozen answers/exposures and acknowledged counters.

NativeTopicsUiTest navigates Topics → topic → target → focused Practice under Robolectric, writes a draft, closes, attempts another focus, resumes the original session and checks no second creation. A German detail test checks confirmed mastered state with a due flag, three qualifying checks, Europe/Berlin date display and disabled zero-availability practice. LearnerHttpTest verifies exact generated FocusedSessionRequest serialization through the owned no-store local HTTP endpoint against an ephemeral mock server.

Root generated contract/token checks passed. Final Android test/assembly/lint totals are recorded in PROGRESS.md. Web/backend/contracts were unchanged; their suites were not repeated. Zero AI/Groq calls, production mutation, content publication, deployment or store upload.

## Reproduction and limitations

Follow [native shell setup](native-shell-verification.md) to launch the local API and debug LearnerPreviewActivity. Complete B1 setup, open Topics, select a topic, start its session or open a target and start one-question practice. Close with a draft, return to Topics and resume saved work; another focus remains disabled until completion or confirmed discard. Open a confirmed target from Progress and verify saved counts/deadlines. B2 in the five-target fixture exposes zero availability.

Robolectric and repository restart tests are not device visual, process-kill, TalkBack or font-scale acceptance. Real native HTTP against the authoritative API remains unverified. Catalog data may be stale after a failed refresh; the server can reject a frozen request without replacing it. This is an unpublished shared local fixture, not production account partitioning, cross-process coordination or offline reconciliation. M0/M1 review/preservation and full M4 acceptance remain open.

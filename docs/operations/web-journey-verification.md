# Local web journey verification

4 October 2026. This is an isolated development demonstration, not a deployment or learner acceptance report.

## Reproduce

From the root, use Node 22 and run `npm ci`, `npm run check`, `npm test`, `npm run build`. In separate terminals run `npm run dev:api` and `npm run dev:web`, then open `http://127.0.0.1:5000/renovation`. The launcher uses only an in-memory local PostgreSQL fixture, binds loopback and reads no production credentials. Use one browser tab. Home explicitly offers five questions instead of fifteen.

Answer the five forms, inspect confirmed feedback, continue to summary, then open Progress. Close practice and resume; reload after entering a draft, viewing a hint, after submission failure, or on feedback. Browser restart reloads the same saved record; stopping the fixture server resets its database and can make saved sessions/cursors unusable. The session discard control explains its local pending-data removal. Restart with fresh local preview storage only when intentionally abandoning the fixture demonstration.

## Local evidence

- Root dependency installation passed using portable Node 22.23.3/npm 10.9.9. Existing audit warnings: 12 vulnerabilities (6 moderate, 6 high); no dependency changes or audit fix in this slice.
- Root generation/type checks and web/API/server builds passed. The development-only route remains isolated from inherited auth, queue mounting and service-worker registration.
- Root backend suite: 7 files / 61 tests passed. Root web suite: 79 files / 312 tests passed. Eleven new journey/read/persistence tests plus the existing foundation tests cover all forms, server-owned outcomes, permanent rejection, snapshot consistency, per-page cursor persistence, partial sync failure, corrupt/denied storage, save-before-send, hint/draft restoration, lost session response and lost attempt response with identical payloads after reload. Earlier test-helper failures were fixed; only the final passing counts establish acceptance.
- Browser verification used agent-browser 0.27.0 against the live loopback web/API. Completed all five actual server-selected questions and confirmed all five answers over HTTP; summary showed `5 / 5`, and Progress showed five Learning targets with one qualifying check each and the next review date. Browser page error collection was empty. Correct answers were fixture examples, not content-review evidence.
- Reloaded an entered noun draft, resumed its existing session and restored its text. Subsequent question navigation focused the next heading; Enter/Space activated answers/continuation. Automated component tests additionally proved frozen submission replay and feedback focus. One initial submission was activated through an observed DOM button after the CLI pointer did not activate a control below the viewport; this is not claimed as an entirely keyboard-only browser run.
- Inspected desktop dark Home and narrow light Progress screenshots. At 320px viewport, document scroll width was 305px (scrollbar accounted for), with visible controls at least 48px. English/German selection and persisted theme worked. German Progress at 640px with 200% CSS zoom had 625px document scroll width, without horizontal overflow. CSS zoom is a reflow simulation, not browser zoom or screen-reader acceptance. Practice hides settings/navigation so the prompt is prominent. Full TalkBack/screen-reader and learner usability review remain open.
- Final React checklist reviewed: lazy development route, isolated native semantic controls, no conditional hooks, bounded fixture storage with schema validation, explicit async lock, stable persisted retry IDs, server-only learning decisions and semantic tokens. Affected primitives retain existing static/backend preview tests. No Android or generated contract changes occurred; Android/device checks were not rerun.

## Boundaries and next work

The foundation and authoritative engine remain implemented locally. This starts the web vertical slice; it does not complete M3 or any other milestone. The five original drafts lack independent approval. No production auth/network database, offline grading, multi-tab coordination, authenticated storage partitions, general catalog labels, Topics/focused selection, content publication, deployment, store release or live AI/Groq calls were performed. No production endpoint was used by the journey.

Versioned, reviewed server catalog metadata and target-focused session selection are the next implementation slice. Keep the local Home/Practice/summary/Progress flow and its retry/reconciliation evidence intact while replacing fixture-only presentation labels and adding genuine target detail and Topics behavior.

Decision: [ADR 008](../adr/008_local_web_journey.md). Hosted results are recorded in the checkpoint once their exact implementation commit has completed.

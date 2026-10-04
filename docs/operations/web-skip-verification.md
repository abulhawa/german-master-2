# Web Skip verification

4 October 2026. Local fixture evidence only.

## Scope

`/renovation` saves frozen skip events before HTTP, retries exact event IDs after response loss/reload and advances only on linked accepted/duplicate acknowledgment. Drafts remain until acknowledgment. Attempts and skips cannot both be submitted for the current question through the UI. Rejection or persistence failure preserves the saved question. Old v1 records gain additive zero/null defaults. Summary separates graded, skipped and server-correct counts; completion refreshes server-confirmed targets.

## Checks

- Root `npm ci`, generated/type checks, `npm test` and web/API/server build passed. The full test run passed backend nine files / 70 tests and web 79 files / 325 tests. Two further journey tests cover acknowledgment-save failure followed by duplicate replay and blocking Skip during pending grading; final targeted journey verification passed 26 tests. Final root check passed after skip-specific rejection copy and import cleanup.
- Tests cover frozen skip/draft reload, duplicate replay, storage failure before HTTP, semantic rejection without advancement, mixed counts and confirmed refresh, legacy record compatibility and acknowledgment linkage rejection. The backend's existing exposure tests cover first-terminal submission, replay/conflict, rollback, completion and no retention gain; no backend policy changed.
- Agent-browser 0.27.0 ran against loopback Vite/API with embedded PostgreSQL. A real accepted skip response was deliberately lost in the browser fetch wrapper. Reload preserved the unfinished draft and pending event; keyboard Space retried it and advanced exactly once. A subsequent cloze answer was graded correct by the server. Completion displayed one graded answer, four skips and one correct answer.
- The refreshed target snapshot showed four skipped targets still `new`, each with one exposure and zero qualifying checks. The graded target was `learning`, with one qualifying check. Duplicate skip replay did not double-count exposure. Progress navigation displayed the confirmed targets.
- Inspected desktop and 320px screenshots, English dark and German light practice. No horizontal overflow at 320px; measured action heights were 50px. Heading focus followed acknowledged advancement. Browser page error collection was empty. DOM evaluation was used for some interactions because agent-browser reference clicks did not activate setup; keyboard Space activation was verified for Retry skip. This is not full screen-reader, device or usability acceptance.

## Limits

No contracts/native changes; Android checks were not repeated. Existing twelve dependency audit findings remain (six moderate/six high). Fixture auth, ephemeral server data and single-tab browser storage do not establish production identity, coordinated durable outboxes or offline reconciliation. Content remains five unpublished drafts; M0/M1 stay open and M2/M3 remain in progress. No deployment, database reset, publication, store release or AI/Groq calls occurred.

Next: start native learner parity with a debug-only product shell, owned profile/setup and Home/confirmed Progress using the existing v2 contracts. Preserve Android application/signing identity and foundation previews; record unavailable device/runtime checks accurately.

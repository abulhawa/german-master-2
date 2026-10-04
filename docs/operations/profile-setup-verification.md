# Owned profile and setup verification

4 October 2026. Local fixture evidence only; no production migration, publication, store release or AI inference.

## Implemented scope

- Migration 004, strict generated TypeScript/Zod and Kotlin preferences/profile/request contracts, owned authenticated profile reads/writes and revision/request replay rules. Setup preferences include locale, IANA timezone, B1/B2 and five/fifteen-question preference.
- Profile-level eligibility bounds new questions while preserving already practised targets and exact mixed/focused session replay. The owned five-target B1 draft catalog reports zero new availability for fresh B2 preferences.
- Development-only `/renovation` setup, later editing, exact pending request persistence/retry, explicit refresh after conflicts/invalid input, honest smaller sessions and review-date formatting in the saved timezone. Existing practice storage remains compatible.

## Local checks

- Root `npm ci` passed with Node 22.23.3/npm 10.9.9. Existing dependency audit reports 12 vulnerabilities (six moderate/six high); no dependency replacement is claimed.
- Root generated/type checks passed. Backend nine files / 70 tests passed. Tests cover strict authenticated HTTP, owner separation, invalid timezone/level/extra fields, revision contention, changed replay conflicts, response replay after subsequent edits and filesystem restart, late-write rollback, B2 new-content limits, preserved session replay, unchanged historical evidence reduction and timezone at next ingestion.
- Final root check/test/build passed: backend nine files / 70 tests, web 79 files / 319 tests, web/API/server builds. Final catalog/start-gate review was followed by another passing type check, 18 journey tests, nine profile/focused backend tests and root build. Journey tests cover setup gating, B2 availability, storage-write failures, lost-response reload with frozen payload and unchanged session draft, and explicit current-profile reload to correct an invalid frozen request.
- Android offline unit tests: 26 suites / 101 tests, zero failures/errors/skips. `lintDebug` and `assembleDebug` passed; lint reports zero errors and the same 16 warnings. Kotlin profile/request round trips, strict extra-field rejection and invalid level/session-length checks passed. No native learner setup UI is claimed.

## Browser evidence

Agent-browser 0.27.0 against the live isolated loopback web/API:

- New fixture opens setup with heading focus. Saved B2/UTC preferences lead to zero available questions and disabled new-practice action. Editing to B1 and German enables the actual five-question session; saved locale, level and timezone survive page reload.
- Invalid timezone freezes the pending request and reports failure; explicit reload restores the current UTC preference and enables editing. Keyboard Space activates save, edit, cancel and practice controls. Page error collection is empty; the deliberate invalid request produces its expected HTTP 400.
- Inspected desktop setup and 320px German light/dark setup screenshots. No horizontal overflow; measured inputs/selects at 48px and buttons at 50px. Heading focus returns to Home after closing setup. Inspected a 200% CSS-zoom simulation at 640px (equivalent content width 320px); no horizontal overflow or clipped controls. This is not operating-system font scaling or full WCAG/screen-reader acceptance.
- Created a five-question B1 session over real local HTTP, typed the noun answer, reloaded, resumed the unchanged draft and received authoritative feedback. Existing full five-form completion remains covered by previous browser evidence and the journey regression suite.

## Limits and next work

Fixture authentication, one-tab browser storage and embedded PostgreSQL do not establish production account security, durable offline coordination or independent network connections. Profile changes are not emitted through the target-only sync contract; clients reread profiles on load. Saved profile locale is reapplied on reload; quick language/theme controls are local preview overrides. The server launcher is ephemeral. All content remains unreviewed local B1 drafts; thirty-target/diversity and independent language review gates remain open. M0/M1 remain open and M2/M3 remain in progress. Next implement web skip with durable exposure retries and distinct summary counts, preserving authority and frozen requests.

## Hosted confirmation

Implementation `4799d61` is pushed to main. All three applicable workflows passed at that exact commit: [Web checks](https://github.com/abulhawa/german-master-2/actions/runs/37201865023), [Android checks](https://github.com/abulhawa/german-master-2/actions/runs/37201865007) and [Repository safety](https://github.com/abulhawa/german-master-2/actions/runs/37201864999). Web logs confirm 319 passing tests. Local staged and full-history Gitleaks scans passed with zero findings; the latter scanned 785 commits. Final evidence/checkpoint edits are documentation-only and are committed/pushed at handoff.

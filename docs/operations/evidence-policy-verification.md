# Retained-evidence policy verification

Date: 4 October 2026. Started at clean `798899c` on `main`, matching remote main. Rechecked GitHub's prior backend-session results: web, Android and repository safety all passed at `4877d22`; documentation safety passed at `798899c`.

## Implemented and tested

`packages/learning-engine/src/evidence.ts` exports the pure `retained-evidence-v1` reducer and typed internal evidence/projection models. It uses an injected clock, saved event timezones and server receipt ordering. ADR 004 records concrete transitions, timing checks, editorial transfer requirements, interval semantics and the distinction between practice due dates and retention eligibility.

The 18 new policy tests cover:

- Empty history, exposure/skips, assisted correctness/incorrectness and reinforcement without independent mastery gains.
- The 1/3/7/14/30 calendar-day ladder, same-day repetition, premature assessment, four-check/14-day gate, lexical context diversity and explicit concept transfer diversity.
- Each current foundation target remaining below Mastered when tested repeatedly with its single draft variant/context.
- First failure, two later recovery occasions, repeated failure, mastery/lapse/recovery cycles and retained transitions.
- Deterministic replay from shuffled inputs, immutable inputs, scope/identity/version/metadata rejection, future receipt rejection and malformed/implausible answer timing.
- Late successes predating a later independent failure; scheduling anchored to receipt rather than a stale client answer time.
- Assistance requesting an earlier independent practice review without shortening the retention gate or postponing overdue work.
- Berlin spring/autumn DST, New York local-day boundaries, timezone changes and Apia's historically skipped calendar day.
- Elapsed time changing due status without demoting mastery; 30 synthetic histories containing only assisted, skipped and reinforcement events failing to manufacture mastery.

These are synthetic evidence fixtures. Editorial identities are trusted server metadata, and the tests do not establish language-content approval or measured retention.

## Local verification

Node 22.23.3 / npm 10.9.9, from repository root:

- `npm ci` passed. Two initial EPERM failures came from existing preview processes holding LightningCSS and esbuild binaries. Stopped the identified project Vite process and its project esbuild binary holder, then repeated installation successfully. The stale web preview was left stopped; no product runtime acceptance is claimed. The unchanged lockfile's audit reports 6 moderate and 6 high vulnerabilities; dependency remediation was not part of this engine slice.
- `npm run check` passed: generated contract/token guards, inherited web TypeScript and engine/API TypeScript.
- `npm test` passed: backend 3 files / 37 tests (19 existing, 18 new); web 78 files / 301 tests with two workers.
- `npm run build` passed: web/PWA, inherited API/server and isolated API launcher. Existing Browserslist freshness notices remain.
- Gitleaks source, staged-change and full-history scans passed with zero findings (773 commits at implementation head).

No client, contract generator, database migration or API endpoint changed. Android/device/browser checks were not rerun for this pure server-engine addition; previous native/browser evidence remains scoped to the prior session slice. Tests used local mocks and fixtures with no production database or Groq credential. Zero AI inference calls and zero cached inference results used. No production reset, deployment, content publication or store release occurred.

Implementation `a520eab` is committed and pushed. Hosted [Web checks 37160726354](https://github.com/abulhawa/german-master-2/actions/runs/37160726354) passed install, generation/type checks, backend 3 files / 37 tests, web 78 files / 301 tests and build at that exact commit. Hosted [Repository safety 37160726305](https://github.com/abulhawa/german-master-2/actions/runs/37160726305) also passed. Android was outside the changed-path trigger; no new native CI result is claimed. The final evidence/checkpoint update is documentation-only.

## Next action

Integrate the reducer with accepted-event persistence. Add pinned editorial variant/context/transfer classification and saved profile timezone at ingestion, then commit attempts/evaluations, target projection/schedule and sync delta atomically. Prove replay/rebuild, rollback, acknowledgment idempotency and competing updates using the isolated PostgreSQL adapter. Introduce explicit skip/exposure transport contracts with their endpoint, then demonstrate server selection from differing learner histories. The local one-variant draft catalog must remain unable to satisfy transfer/mastery gates. Production identity/network PostgreSQL and content review remain open.

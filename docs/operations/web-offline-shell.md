# Isolated web offline shell evidence

5 October 2026. Started clean on `main` at `ee56767`. That exact commit passed [Web](https://github.com/abulhawa/german-master-2/actions/runs/37304572528), [Android](https://github.com/abulhawa/german-master-2/actions/runs/37304572433) and [Repository safety](https://github.com/abulhawa/german-master-2/actions/runs/37304572371). These results predate the shell changes.

## Reproduction

From the repository root, using Node 22:

```powershell
npm ci
npm run build:learner-preview
npx playwright install chromium
npm run test:offline-shell
```

The harness starts a fresh in-memory fixture API on 5011 and the preview on 5010; occupied ports fail rather than reusing a possibly unrelated service. Both are closed afterwards. It uses a new temporary Chromium profile. `GM_PLAYWRIGHT_MODULE` and `GM_CHROMIUM_EXECUTABLE` can identify an existing runtime/browser; normally the pinned dev dependency resolves automatically. `--existing-servers` is only for explicitly prepared fixture servers. No production credentials or configuration are needed. Browser installation is tooling setup, not a live API test dependency.

For manual preview, start `dev:api` with `GM_DEMO_PORT=5011` and run `npm run preview:learner`. Open `http://127.0.0.1:5010/learner-preview/`. Keep this on its own origin; do not mount it behind the legacy root worker. Wait for the shell-saved status and download a reserve while connected before expecting offline launch. Closing all preview clients lets a waiting shell update activate safely.

## Actual local evidence

The agent-browser CLI was unavailable. The bundled Playwright harness initially found no matching installed browser; explicitly selecting the installed Chromium also failed with `spawn UNKNOWN`. A PowerShell browser launch was rejected as `blocked by policy`; no permission override or bypass was used. The supported CUA in-app browser was then used.

With its fixture profile configured and a validated two-session reserve downloaded, both agent-created loopback servers were stopped. The preview tab was closed and a new tab launched at the same URL. The real learner loaded from the worker and exposed two reserved sessions while HTTP-dependent reads showed their existing errors. Practice consumed one slot. A hint and `Berufe` draft survived another tab close/new-tab launch. Provisional correct feedback survived reload. Four Skips and End session saved a full 1-graded/4-Skip summary with six pending writes. Captured JavaScript error logs were empty. The original server had deliberately in-memory storage, so this manual run does not claim late sync after server restart; the harness keeps the same API alive while its browser network is disabled.

This verifies fresh-document shell launch with the origin/API unavailable, not full browser/OS restart, airplane mode, cache-eviction recovery or production release accessibility. The stronger Chromium harness is checked in and added to CI but has not completed locally or on a new hosted commit. Its failure is reported, not suppressed. The old full-suite component/HTTP offline tests remain complementary evidence.

Verification: offline root npm ci on Node 22.23.3/npm 10.9.9 passed; root generated/type checks passed; root tests passed backend 13 files/128 tests, web 82/355, HTTP 2/7. Root web/API/server build and separate shell build passed. Android code is unchanged, so native suites were not repeated. The shell uses existing foundation tokens/controls with localized status; no layout/design replacement was made. The stale EN/DE connection-only notice was corrected.

Adding pinned dev-only Playwright changed only its package entries in the lockfile. A live registry audit reported 12 existing dependency findings (6 moderate/6 high), none in Playwright; the earlier offline audit's zero was not current advisory evidence. No unrelated dependency upgrades or audit suppressions were applied. Assess applicability before release.

Local shell implementation and fresh-tab/server-unavailable behavior are established. No full M0–M6 gate closes. Full-process/network-emulation acceptance remains pending, followed by whole-client write coordination, account boundaries, two-device/auth faults, privacy and recovery. External content/design, staging and M0 resources remain as recorded in milestone gates. Zero Groq/AI calls and no production mutation, deployment, publication, destructive reset, signing or store upload.

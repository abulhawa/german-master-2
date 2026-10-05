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

This local run verifies fresh-document shell launch with the origin/API unavailable. The stronger harness subsequently passed in [hosted Web CI](https://github.com/abulhawa/german-master-2/actions/runs/37307188448) at `b9e2d3b` using Chromium 151.0.7922.34: full browser close/restart with network disabled, assisted draft and feedback recovery, full completion and late sync against the same server, waiting update without reload, preservation of unrelated storage and a non-owned same-scope precache, asset-only scope and 320px/no-overflow checks. [Android](https://github.com/abulhawa/german-master-2/actions/runs/37307188510), [Repository safety](https://github.com/abulhawa/german-master-2/actions/runs/37307188417) and CodeQL also passed. No OS restart, cache-eviction recovery or full production release accessibility acceptance is claimed.

The generated-worker review identified that Workbox incompatible-cache cleanup matches the registration scope rather than our namespace. It is disabled; ordinary precache activation still removes obsolete entries from the owned cache. The harness changes only its generated worker file to install a waiting update and restores it during teardown. An unrelated same-scope precache must survive activation.

Verification: offline root npm ci on Node 22.23.3/npm 10.9.9 passed; root generated/type checks passed; root tests passed backend 13 files/128 tests, web 82/355, HTTP 2/7. Root web/API/server build and separate shell build passed. Android code is unchanged, so native suites were not repeated. The shell uses existing foundation tokens/controls with localized status; no layout/design replacement was made. The stale EN/DE connection-only notice was corrected.

Adding pinned dev-only Playwright changed only its package entries in the lockfile. A live registry audit reported 12 existing dependency findings (6 moderate/6 high), none in Playwright; the earlier offline audit's zero was not current advisory evidence. No unrelated dependency upgrades or audit suppressions were applied. Assess applicability before release.

Local fixture shell implementation and hosted full-process/network-emulation acceptance are established. No full M0–M6 gate closes. Whole-client write coordination, account boundaries, native/physical-device/account-switch faults, privacy and recovery remain. External content/design, staging and M0 resources remain as recorded in milestone gates. Zero Groq/AI calls and no production mutation, deployment operation, publication, destructive reset, signing or store upload. The changes are in draft [PR #1](https://github.com/abulhawa/german-master-2/pull/1); main remains at verified `ee56767`.

## Two-client reconciliation continuation

An additional actual HTTP/PGlite test uses two separate web IndexedDB databases/device IDs for the same fixture learner. Each prepares and completes its own one-question offline session against the same target. Device A loses its server-accepted response. Authentication rejection is then injected at the real server, returning HTTP 401 for both clients; their frozen payloads and unsaved receipts remain unchanged, with one accepted evidence row. After reopening A's database and restoring authentication, B delivers first and A explicitly replays its original answer as duplicate. Exactly two evidence rows remain; both clients pull/reset to identical confirmed targets with one qualifying same-day check, state Learning and no Mastered credit. IDs and request bytes remain frozen throughout. This is simulated two-store/network/auth-fault integration, not physical devices or real account expiry/switching.

All eight HTTP integration cases pass locally. The older response-loss component test exposed a timing failure when a retry was clicked before its control was enabled, or six real acknowledgments exceeded the default one-second assertion window. It now waits for the enabled control and uses a bounded five-second receipt wait, keeping the exact receipt/evidence assertions. No production behavior changed and no assertion was removed.

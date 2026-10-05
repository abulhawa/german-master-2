# ADR 023: Isolated local web offline shell

Accepted 5 October 2026 for the fixture learner only. Implements the shell portion of blueprint section 19 and follows ADR 022.

The explicit `build:learner-preview` command builds a standalone learner entry into `apps/web/dist/learner-preview`. The normal build, release entry and deployment configuration stay separate. The preview uses its own loopback origin (port 5010), `/learner-preview/` worker scope and `german-master-v2-local-shell` Workbox cache namespace. It imports the actual learner, contracts and deterministic grader, without the legacy app, legacy public packs, manifest or external font requests. Foundation tokens and controls retain their existing accessibility behavior.

Only generated HTML, JavaScript and CSS enter the precache. Navigation fallback accepts the preview root and its index, never API, auth or arbitrary paths. APIs remain network-only; validated owned reserves, pinned started sessions and frozen writes stay in the existing v2 fixture stores. Cleanup applies to this precache namespace, not unrelated origin storage. Separate-origin operation is required: a legacy root-scope worker on a shared origin could otherwise control this entry.

Worker updates wait for the existing clients to close. There is no automatic reload, `skipWaiting` or `clientsClaim` during practice. The localized status reports installation readiness/failure; browser storage eviction can still remove cached assets. This is not an installable production PWA or production identity/account partitioning.

The Chromium acceptance harness uses a fresh persistent browser profile, closes the entire browser between launches, disables network, resumes an assisted draft and provisional feedback, saves a full end, and reconnects to the same isolated API for explicit ordered delivery. It also checks the worker scope, asset-only cache and unrelated-storage preservation. The workflow installs the pinned Playwright browser and runs it independently of ordinary fixture tests.

Local supported-browser evidence establishes fresh-tab cold launch and reload with both loopback services stopped, retained draft/assistance/feedback, and full end with six pending events. Full browser-process restart/network-emulation/late-sync harness execution remains unverified on this host because process launch was blocked. Do not infer that the new hosted gate passed from the earlier green workflows. See the operations record for reproduction and limits.

No migrations, production connection, publishing, signing or deployment operations are introduced. Consolidate development migrations before establishing the first real staging/production database.

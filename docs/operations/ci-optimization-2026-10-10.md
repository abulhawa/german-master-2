# CI optimization, PR #18 — 10 October 2026

## Scope and approval boundary

This optimization is included in PR #18. It does not publish course content, change Supabase, deploy to Vercel, merge the PR, or release Android. Node.js 24 has already been migrated in the same PR. CI checks must continue to report success or failure of all existing coverage and release guardrails.

## Measurements: most recent completed run and first split

Measured from the earliest and latest timestamp of hosted job logs, including setup, post-actions and cleanup. Wall clock is measured across jobs; runner-time is the sum of job durations. Figures are approximate, not billing invoices.

| Metric | Baseline at `1bcebee` | First split at `b2cf190` |
|---|---:|---:|
| Web wall clock | 282 s (4m42s) | 259 s (4m19s) |
| Web summed job runtime | 282 s | 300 s |
| Android wall clock | 601 s (10m01s) | 478 s (7m58s) |
| Android summed job runtime | 601 s | 833 s |
| Acceptance | All workflows passed | All workflows passed |

The initial split met the Android under-eight-minute goal, but increased Android runner time by roughly 39%. Web time improved only modestly. The second web refinement in this PR separates backend tests (previously ~96 s) from client and HTTP tests (previously ~72 s and ~47 s), with separate runner setup; its actual cost and elapsed time must be measured from the final commit's completed workflows. Keep the split only if its speed/cost tradeoff is useful.

## Cache control and inventory

The Android workflow continues using only `gradle/actions/setup-gradle@v4` for Gradle Home. The release job is always cache read-only. The debug job is read-only on PRs and automatic pushes; only a manually dispatched run **on main** may write refreshed Gradle entries, and only in that debug job. Caches therefore cannot grow from ordinary Android CI Gradle writes. This does not prevent other unrelated workflow caches (e.g., npm) from changing.

The read-only GitHub Actions cache API reported **119 entries and 5,962,952,780 bytes** (about **5.96 decimal GB** or **5.55 GiB**) at the time of the first successful split. This is total repository Actions cache usage, not Gradle cache usage, and is below the cited 10 GB storage threshold. No keys were deleted or altered, and a repository-wide list of individual keys was not available through the GitHub connector. Before any future cleanup, inspect the inventory via the authorized GitHub UI's Actions caches page or `gh cache list -R abulhawa/german-master-2 --limit 200 --sort size_in_bytes --json id,key,ref,sizeInBytes,lastAccessedAt` and delete only confirmed superseded entries.

The aggregate Android gate prints the total cache bytes and entry count through a read-only token if GitHub makes this endpoint accessible. The workflow tolerates an unavailable measurement endpoint but never hides a failed verification gate.

## Required-check preservation and scheduling

Both web and Android keep a job called `verify`. That gate depends on every new substantive job and fails if any dependency fails. Web verifies TypeScript, backend tests, browser/client tests, HTTP tests, all three distinct build configurations and Playwright offline acceptance. Android verifies unchanged negative configuration guards, production-origin rejection, an unsigned release bundle, native tests, both debug and learner-preview assemblies and lint.

PR concurrency cancels superseded runs of the same PR. Main pushes and manual runs use run-specific identities so one main-branch commit cannot cancel or evict another commit's queued check. The Gitleaks scan retains full Git history and checksum pinning. The browser setup still uses exactly Chromium; no large additional browser or Gradle cache was introduced.

## Not claimed

- No observed future cache-growth stability yet; compare daily or weekly inventory measurements.
- No proof of a two-minute web workflow or a further Android speedup until final workflows complete.
- No verification that branch protection is configured to require `verify` (repository branch-protection API is inaccessible); the job name and dependency behavior are preserved.
- No production email-flow, Android device/store, learner editorial or design acceptance is inferred from CI success.

For a PR review, compare wall time and summed runner time, check all five hosted workflow conclusions, and preserve the existing merge/deployment boundary.

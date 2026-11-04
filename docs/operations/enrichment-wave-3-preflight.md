# Wave 3: remaining reviewed enrichment, read-only preflight

This is the **preparation stage**, not a candidate release or a deployment.
No edits are made to the independent review branch, the immutable authoring
snapshots, the installed production catalog, the database, or the Android app.

## Fixed baselines

- Live wave 2 already contains **2,488** reviewed enrichment questions plus
  125 existing questions, released as `91000000-0000-4000-8000-000000000001`.
- Full independent review of **5,682 / 5,682** questions is complete at review
  SHA `0f8d39ef501c10062b574a9d5c1483f75b3b04b3` and its exact commit
  passed the enrichment review verification workflow.
- Earlier wave-2 selection was pinned to 78 reviewed batches and excludes
  100 questions in 50 two-variant targets for material defects.
- The next wave considers all 175 batches, including newly completed
  0079–0175, and re-evaluates prior exclusions against the now-pinned rechecks.
- The review scheduler may continue adding recheck evidence. This preflight
  intentionally freezes a known-good commit; do not silently follow a moving head.

## Run

From repository root with full Git history available:

```sh
node tools/enrichment-wave-3-preflight.mjs --report wave-3-readiness.json
```

The script uses local Git objects only, pins both immutable source snapshots,
verifies every catalog SHA-256 against the exact source queue, checks all 175
completed findings and every actual option assessment, and reconciles all
5,682 questions against the currently published target identities. It admits
unchanged nonblocking reviews and exact semantic-rechecked material proposals
only. Any other material defect excludes its *entire two-variant target*.
It performs provisional POS/topic/context checks, flags possible title collisions
as advisories, and writes an auditable per-target readiness report.

The GitHub preflight workflow performs the same read-only audit and uploads the
JSON report as a short-lived CI artifact. An audit passing means the counts and
eligibility decisions are mechanically verified; it does **not** mean language
correctness, licensing, new bundle identity, DB import, production rollout, or
client lifecycle acceptance have been demonstrated.

## Verified frozen candidate, 4 November 2026

The exact pinned preflight and candidate generation completed in GitHub Actions
run [41923258028](https://github.com/abulhawa/german-master-2/actions/runs/41923258028).
The read-only preflight, pinned workspace install, candidate build, additive
PGlite installation/replay, previous-release preservation, all newly accepted
answer grading and bounded 125-question saved-round adoption passed. The
workflows committed the generated output on this **release branch only**
at `0ddb07fe70af3902949326d0ae6cff818c35957f`. It did not deploy.

| Frozen result | Count |
|---|---:|
| Independently reviewed source questions | 5,682 |
| Reviewed questions already published in wave 2 | 2,488 |
| Newly eligible and generated | **3,006** (1,503 targets) |
| Excluded for unresolved material defects | **188** (94 targets) |
| Exact rechecked corrections applied in this new candidate | 8 |
| Previously excluded targets now eligible | 5 |
| Old release revisions preserved | 2,613 |
| New release total revisions | **5,619** |
| General B1 question pool | 2,721 |
| General B2 question pool | 2,844 |
| Maximum frozen saved round | 125 |

Frozen inactive candidate identity:
`92000000-0000-4000-8000-000000000001`.

The 54 previously gated Work questions remain outside General practice. The
candidate SQL was installed only in local PGlite as a test, not in Supabase.
No new candidate is active in `content/production/catalog.json` or
`content/guest/practice.json`, and no production deployment or Android Play
publication has occurred.

### Remaining deployment blockers

The catalog and offline bundle are **prepared and mechanically verified**, but
there is no production rollout authorization implicit in CI. The next slice
must activate these exact frozen files in an integration commit, test actual
web/native offline lifecycle (including larger asset size), validate native
packaging, build and check a non-destructive import plan against the exact
existing production membership and content digests, and then execute the
owner-authorized direct production rollout with post-deploy smoke tests and
rollback retention. Hold back the 188 excluded questions until rechecked.

## Next implementation gate

1. Treat the generated report as the freeze input for a **new additive immutable
   release**, not as a live selector. Apply verified corrections in the new
   derived candidate only; never modify original source or deployed revisions.
2. Build the next catalog/SQL/shared web-and-native offline bundle, preserving
   all 2,613 previous members and existing saved-round identity. Use the existing
   bounded maximum of 125 questions per round, with equivalent topic/Work gates.
3. Test exact immutable-row/digest import and rollback locally, all newly accepted
   answers, large-topic bounded sessions, guest/authenticated resume/isolation,
   offline/restart/retry/Continue in web and native, then hosted CI.
4. Under the existing owner direction, deploy directly to the *same* clean
   production project only after release checks pass. Preserve older releases
   and user data; do not pause any other project. Android Play upload remains
   explicitly held.

Never reuse the wave-2 release ID for changed membership, change previously
published exercise payloads, or treat an unrechecked material correction as approved.

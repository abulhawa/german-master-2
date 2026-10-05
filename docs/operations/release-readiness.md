# German Master 2.0 release-readiness guardrails

This runbook adds a deliberately non-deploying release check for German Master 2.0. It records immutable release inputs and fails on accidental changes to known safety invariants. It does **not** authorize or perform a deployment, database change, credential retrieval, signing operation, Play upload, content publication, or production cutover.

## What the workflow verifies

The `Release readiness` workflow runs `tools/release/readiness.mjs` on relevant pull requests and on manual dispatch. The script verifies:

- Android application identity remains `com.germanverbmaster.android`.
- Android version metadata can be parsed. The current source version is recorded, but store upload remains blocked until the **live highest published versionCode** is known and the new code is higher.
- The dedicated staging project remains `german-master-v2-staging` / `zgmyrpzwgtydwlzponih` in Frankfurt `eu-central-1`.
- The staging plan still forbids legacy-data import, Data API exposure of the learning schema, and implicit production cutover.
- Common secret-bearing files remain ignored.
- SHA-256 fingerprints are recorded for the clean v2 database baseline, both reviewed server-role policy files and the optional private identity-deletion subsystem setup.
- The current web auto-deploy setting is recorded as evidence, not interpreted as v2 release approval.

The workflow uploads only the generated JSON manifest as a short-lived GitHub Actions artifact. The manifest contains repository identifiers, hashes and release metadata only. It intentionally contains no credentials.

## Gates that remain external

A green readiness check means the repository has not violated these guardrails. It is not a release approval. Before a staged or public release, evidence is still required for:

1. the live Play published maximum versionCode and signing/key custody;
2. approved staging credentials and application of only the clean v2 baseline plus reviewed role SQL;
3. independent network connections, least-privilege/advisor checks, contention and representative load;
4. independent German content review, design approval and accessibility acceptance;
5. private off-machine backup plus a full isolated restore rehearsal;
6. explicit deployment, Play upload and production cutover approval.

The legacy web configuration currently enables main-branch deployment. German Master 2.0 release routing must be reviewed separately; this workflow does not promote or modify it.

## Using the manifest during a release review

Run the workflow on the exact candidate commit. Retain the manifest with the release evidence and compare its hashes to the reviewed database/policy files before schema application. Record the live Play maximum and candidate versionCode separately. If any immutable input changes after approval, rerun review rather than reusing the earlier evidence.

Rollback follows the blueprint: redeploy the last compatible API/web artifacts, halt Android rollout and ship a higher-versionCode corrective build when necessary. Do not assume Play permits downgrade. Once real users exist, use forward-compatible database changes rather than destructive rollback.

## Current milestone effect

This closes no full milestone by itself. It advances GM-021 and M0/M6 release evidence by making provenance and approval boundaries machine-checkable. Live restore, signing continuity, deployment inventory, staging smoke and rollback rehearsal remain required.

# German Master 2.0 release readiness

The [26 October finite stabilization checklist](stabilize-release-2026-10-26.md)
is the current release scope. See [the latest assessment](release-readiness-2026-10-27.md)
for dated evidence and unresolved gates. M2 remains active.

## Repository guardrails

The `Release readiness` workflow runs `tools/release/readiness.mjs` on relevant
pull requests and manual dispatch. It verifies application identity, parseable
Android version metadata, the configured clean replacement plan, disabled legacy
import/Data API exposure, ignored secret-file patterns, and hashes of the clean
baseline and role policies. It records main auto-deployment configuration.

Run `node tools/release/readiness.mjs --output <private-evidence-path>` against the
exact candidate commit. A passing manifest proves repository guardrails only;
it does not inspect deployed settings, live users, current content or lifecycle
behavior. Its external gates are reminders, not live status. Retain its commit
and hashes with the release evidence. Never include credentials.

## Candidate completion

- Whole-round save/resume, round-specific consent/account isolation and native
  Continue must pass bounded client-specific acceptance.
- General B1/B2 content needs bounded serious-error review. Work practice and
  Google/Microsoft stay disabled; email remains available.
- Verify signup/cancellation, save/resume, disconnect/reconnect, restart, sync,
  inspected export, logout/account switching and concurrency. Fixture results
  and physical/browser acceptance must be distinguished.
- Record cross-device unfinished-draft resume as unavailable unless implemented
  and verified; confirmed progress sync is a separate behavior.
- Freeze/tag only after candidate gates pass. Record exact compatible web/API
  artifacts, environment bindings and rollback steps before rollout.
- Web deployment requires its applicable explicit authorization. Android Play
  publication separately requires owner UX approval and a versionCode above the
  live published maximum. Signing remains owner-held.
- Run the small real-learner pilot after the stable web release. It is not
  replaceable by model-assisted review or fixtures.

TalkBack retains its accepted scope. Off-machine backup is not an owner-required
M0 gate; retain private recovery artifacts and required restore evidence.
Previously published general content does not require republication merely to
run this readiness check. Do not enable account deletion until its separate
requirements and authorization are satisfied.

## Rollback preparation

Identify and retain the last known compatible deployed web/API artifacts and
verify their environment bindings before promotion. Redeploy those compatible
artifacts if the candidate fails smoke checks. Preserve learner data; use
forward-compatible database changes. Halt an Android rollout and use an
owner-signed higher-versionCode corrective build where needed; Play downgrade
must not be assumed. This document performs no rollback or deployment.

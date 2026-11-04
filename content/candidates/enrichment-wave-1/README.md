# Reviewed enrichment wave 1 — inactive candidate

Owner policy: completed AI editorial review is editorial approval. This candidate
adds 11 everyday noun objectives / 22 questions from completed batch 0001: 17
unchanged questions and five corrections with exact proposal-hash-bound semantic
rechecks. Optional suggestions remain unapplied. Other batch targets are outside
this wave; unresolved material defects are excluded rather than published.

`manifest.json` records the exact source/review commits, catalog/findings and
unchanged canonical-validator hashes, per-question decisions, correction evidence,
new immutable release identity, prior manifest and output hashes. Frozen byte
exports in `evidence/` retain source `review.status: pending`; the derived manifest
records editorial approval. The source authoring and independent review branches
are not modified. The generator verifies the fixed evidence-index hash and every
input hash; ordinary checks need no Git network access or deep checkout.

Run from the repository root:

```
npx tsx services/api/scripts/prepare-reviewed-enrichment.ts
npx vitest run --config vitest.backend.config.ts services/api/src/enrichment-content.test.ts
```

The unchanged canonical validator uses the existing initial-30 catalog as its
minimum-target/multi-format context. Only the explicitly selected enrichment
targets enter the new release; validation context is never reimported. The new
targets are explicitly lexical, General German, Everyday vocabulary, rather than
falling back from `vocabulary` to grammar or inferring Work from a scenario.

The SQL retains all 125 previous published members and adds 22 new revisions.
`importEnrichment` installs inside a transaction, verifies the resulting manifest
before commit, accepts an exact installed replay, and rejects an ID/hash collision.
Do not run the raw seed twice: use the checked importer. No UPDATE/DELETE/schema
or learner-data changes are generated. Rollback restores the prior runtime
catalog and prior shared offline bundle; retain the new release and revisions so
already saved rounds can still be read. The acceptance test proves this locally.

`offline.json` is a derived replacement for the shared web/native `practice.json`,
not an active bundle. General pools contain 77 B1 / 70 B2 questions. Both clients
concatenate eligible packs; all General pack headers therefore use the additive
release containing the old and new questions. Whole-pool adoption, draft/replay,
grading and the 126-question rejection are tested. Existing Work packs remain
unchanged and gated. This wave avoids client paging changes; larger waves still
require bounded selection on both clients.

## Remaining release gates

Hosted safety, accessibility, dependency and backend checks pass at `e7cff367`.
The hosted browser job fails at `verify-offline-shell.mjs:61`, waiting for the
retired `Offline practice` Settings summary. This script is unchanged from main;
align it with the current approved flow while retaining offline/draft/late-sync
assertions. It remains a release blocker; no gate was removed or bypassed.

No production database import, active catalog/bundle replacement, deployment or
Android upload occurred. On 4 November the connector reports the old GM2 target
`zgmyrpzwgtydwlzponih` INACTIVE and clean production replacement
`sqgjsmiaprsuilcjmaav` ACTIVE_HEALTHY. No separate active staging target is present.
Do not use production as an isolated staging database or create a paid/third target.

Next: establish an isolated existing staging target, validate its baseline and
previous manifest, import through the checked importer, activate both derived
catalog and shared bundle together in staging, then prove real web/native offline
practice, account round saving, grade/replay, old-session restoration and rollback.
Only after those gates and hosted CI pass may this wave be merged/imported and
deployed to production under the standing owner authorization. Android Play
publication remains on owner hold. Local PGlite checks are not live staging or
native device acceptance.

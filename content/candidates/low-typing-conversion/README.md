# Low-typing conversion: release 4

**Status:** prepared and validated, awaiting additive production import and exact-head activation.

The current wave-3 library inherited 103 older revision-1 exercises requiring typed input. All are replaced with choice/gap-choice exercises in the new **93000000-0000-4000-8000-000000000001** immutable release, without changing the 5,619-question library size or old release membership.

| Old type | New type | Count |
| --- | --- | ---: |
| `short_answer` | `choice` | 11 |
| `cloze` | `choice` | 10 |
| `cloze` | `gap_choice` | 71 |
| `multi_slot` | `gap_choice` | 11 |
| **Total converted** | | **103** |

`draft.json` records the proposals, identity, original revision, new payload, rubric, authored source and reviewer evidence. One hundred replacements reuse already approved GPT-6 AI editorial drafts. Three original starter exercises were rechecked 5 November 2026 for acceptance and distractor correctness. In the preposition example, both *ins* and *in das* are correct.

Fifty-nine replacement prompts differ from their old written-production versions. This is an intentional **recognition rather than unrestricted writing** learning mode and must not be represented as equal evidence of unrestricted recall. Previously saved attempts and old release memberships must remain unchanged.

## Reproduce

From a Node 24 checkout with dependencies installed:

```sh
npx tsx services/api/scripts/prepare-low-typing-release.ts
npx vitest run --config vitest.backend.config.ts services/api/src/low-typing-conversion.test.ts services/api/src/low-typing-release.test.ts
```

The generator installs the previous five releases into **disposable PGlite**, validates the frozen source manifest, stages the exact immutable new revision SQL, and checks the resulting full manifest and every accepted answer. It writes:

- `catalog.json`, `seed.sql`, `manifest.json`, `offline.json` in this directory.
- `content/production/catalog.json` and `content/guest/practice.json` as corresponding shared web/native runtime input.

The previously published seed and candidate files remain unchanged. The additive release uses the same 5,619 exercise identities, 103 newer revisions and the original 5,516 unaffected revision memberships. The existing revisions and previous release remain available for pinned rounds and retries.

## Activation

**Do not merge the new runtime selector before inserting and verifying the additive 9300 release in the existing clean Supabase database.** Import only after confirming the live 9200 manifest equals `manifest.json.previousManifestHash`, the old release count is 5,619, and exactly 103 historical typed members exist. The checked SQL only inserts new revisions, evidence identities, and a complete new release; no UPDATE/DELETE of historical or learner data. Re-query both release counts and the new zero-typing count. Merge the same checked head, verify Vercel production, then complete signed-out browser acceptance of B1/B2 typed-to-choice examples, saved drafts, feedback and continuation.

Android uses `content/guest/practice.json` as its shared offline source, but the Play release remains blocked until separate owner approval. This content release does not authorize Android Play publication.

# Low-typing conversion: legacy published exercises

**Status:** draft candidate; not a release, not deployed, and not approved for publication.

The current wave-3 library inherits 103 older revision-1 exercises requiring typed input. The wave-1 shared offline bundle identifies these same exercises, without downloading or changing production data. The other new enrichment questions were authored with choice and gap-choice interactions.

| Previously published type | New draft type | Questions |
| --- | --- | ---: |
| `short_answer` | `choice` | 11 |
| `cloze` | `choice` | 10 |
| `cloze` | `gap_choice` | 71 |
| `multi_slot` | `gap_choice` | 11 |
| **Total** | | **103** |

`draft.json` contains exactly 103 candidate replacements. Each keeps its original exercise ID and target ID and increments the **immutable revision**. It includes the exact proposed exercise payload and grading rubric, source linkage, historical type/revision, and provenance.

Of the 103, **100** reuse already AI-reviewed, approved authoring revisions from `content/drafts/initial-30.json` and `content/drafts/b2-basics.json`. Some intentionally rephrase or change the tested grammatical objective, so they must not be treated as a lossless visual conversion. The other **three** use existing starter choice suggestions and accepted answers but need final editorial rechecking. In the preposition example, both *ins* and *in das* are accepted.

## Verification and release boundary

`services/api/src/low-typing-conversion.test.ts` asserts exact coverage in the frozen published bundles, authoring/review linkage, no changes to old revision identities, correct grading of every accepted new answer, and solution-free selection formats.

No production SQL, release selector, historical question row, published catalog, web component or Android component is changed by this draft. The existing starter's suggestion renderer already presents choices in the guest UI, but legacy account/offline releases still carry the older typed exercise revisions.

**Next:** independently recheck the three starter conversions and any material objective changes; prepare a *new additive content release* that inserts these 103 revisions and replaces only their membership in a new release manifest. Rebuild the full shared web/Android offline catalog using the same pinned revision identities. Verify all accepted answers, release hash, previous-release replay, pinned unfinished rounds, 125-question freezing, both clients and CI. Activate only after explicit publication/merge authorization and production checks. Leave the editorial review branch to its sole writer.

Selection questions measure recognition; they are not a substitute for free writing. A future optional writing mode may retain the old typed revisions without making typing mandatory.

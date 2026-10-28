# Legacy learning-material references

The original web repository is [abulhawa/german-master](https://github.com/abulhawa/german-master).
Its learning files are already imported, with history, in the active GM2 monorepo.
Use the GM2 snapshot pinned in `SOURCE-MANIFEST.json`:
`e6376123e086fc358075462f253f5688a59a7b30`. The manifest links directly to every
source file at that commit and hashes its LF-normalized text. Working-tree source
changes cause validation to stop; do not silently rebase the source plan.

| Source in GM2 | Records | Planned B1/B2 seeds |
| --- | ---: | ---: |
| `apps/web/data/pos/nouns.jsonl` | 2,806 | 2,117 |
| `apps/web/data/pos/verbs.jsonl` | 1,029 | 740 |
| `apps/web/data/pos/adjectives.jsonl` | 614 | 429 |
| `apps/web/data/pos/adverbs.jsonl` | 210 | 119 |
| `apps/web/data/pos/prepositions.jsonl` | 40 | 16 |
| `apps/web/data/pos/conjunctions.jsonl` | 39 | 27 |
| `apps/web/data/pos/particles.jsonl` | 8 | 4 |
| `apps/web/data/pos/pronouns.jsonl` | 8 | 0 |
| **Total JSONL records** | **4,754** | **3,452** |

`apps/web/data/wortschatz/b2-beruf.csv` additionally contains 1,663 parsed rows;
`apps/web/db/verb-lists/{a1,a2,b1,b2,c1,c2}.ts` contains level-specific verb
references. These overlap other material. They are auxiliary references, not
additional question counts or separate initial batches. External source lists
under `apps/web/docs/external/` are not blanket-approved for reuse.

## Treat old fields as claims to review

An inherited `approved: true` or level label is not current editorial approval or
CEFR certification. Known problems include malformed translations, missing or
misclassified forms, questionable level placement and awkward examples. There can
also be conflicts between `examples`, `example_de`, `example_en` and word-type
metadata. Report conflicts; do not silently select a convenient field or infer
that the database representation made the content correct.

Use lemmas and grammar facts as seeds. Write original prompts, contexts,
distractors and explanations. Track the exact source row/hash and explain any
correction. Check provenance and reuse rights; do not copy third-party example
sentences, translations or definitions merely because they exist in the old repo.
Defer records whose accuracy or permitted reuse cannot be established. A
`rightsBasis` note documents the author's assessment; validation cannot certify it.

Existing GM2 material to check before creating new objectives:

- `content/production/catalog.json`: active target identities and topics.
- `content/drafts/initial-30.json`, `b2-basics.json`, `workplace.json` and
  `general-expansion.json`: authored objectives, exercise identities and rubrics.
- `content/guest/practice.json`: current shared bundled exercise inventory.
- `content/reference/grammar-reference.json`: existing reference explanations.

Reuse an existing target ID when the objective is genuinely the same; create new
exercise IDs for new contexts. Never edit a published revision or claim a new
target just because a sentence or noun changed unless the assessable objective
itself is distinct. The batch validator checks identity/metadata consistency;
semantic duplicate detection still needs editorial judgment.

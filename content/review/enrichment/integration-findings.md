# Enrichment integration findings, 29 October 2026

This is preparatory review on `content/enrichment-review`. Nothing here activates
content, edits the authoring workbench, changes application behavior, or authorizes
publication. Per-question findings and inactive overlays remain AI-assisted work.

## Verified structural baselines

The unchanged canonical TypeScript validator passes for both pinned snapshots:

| Snapshot | Completed batches | Targets | Questions | B1 questions | B2 questions |
|---|---:|---:|---:|---:|---:|
| `671e9d19f4f5a61d7916ed96b3b41433b768fd39` | 49 | 768 | 1,536 | 720 | 816 |
| `206ed84bd7f36332f589e9a6e0c7bfc4671c6161` | 137 | 2,238 | 4,476 | 2,196 | 2,280 |

The first snapshot contains an empty unfinished batch 0050; it is excluded from
the editorial queue. The second snapshot contains no partial batch. Structural
reports bind every catalog/report hash and the unchanged validator source hash.
They include the existing four draft corpora as validation context. They do not
query hosted data, approve language, certify CEFR placement or establish rights.

Deterministic triage of all 4,476 questions found no normalized duplicate options,
no checked encoding markers, no unfilled stored completions and no single-gap
key/completed-answer discrepancies. The explicit separable-infinitive pattern
check found `zu abmildern` in `legacy-0002-L20-v2`; its inactive correction changes
the prompt and all options together, retaining the answer identity. This small
pattern list cannot rule out other grammatical errors.

## Mapping must precede a runtime candidate

All 2,238 new targets currently use `category: vocabulary`, without explicit
runtime topic/context mapping. `services/api/src/basic-content.ts` has no default
topic mapping for that category and throws `Unmapped content category: vocabulary`.
Its existing lexical/grammar classification treats only `plural` and
`work_vocabulary` as lexical. Adding a topic fallback alone would therefore
misclassify the new lexical objectives as grammar. These are source-inspected
integration limitations, not an observed production failure.

Prepare explicit metadata for each accepted objective: topic, general/work
context, lexical/grammar kind, retained target identity, exact exercise revision,
variant/context/transfer identity and source provenance. Avoid placing everything
in Work merely because an example mentions a colleague; Work remains gated.
Use a separately identified additive immutable release and preserve all existing
release memberships/revisions. The basic builder's default release identity is
already used; it is not an enrichment release allocation.

## Identity triage

Normalized title matching alone produces false positives:

- `legacy-0078`, target `80000000-0000-4000-8000-000000007810`, teaches the verb
  **nutzen** (use). `legacy-0133`, target
  `80000000-0000-4000-8000-000000013311`, teaches the noun **Nutzen** (benefit).
  Their lowercase titles collide, but their explicit objectives, word classes and
  prompts differ. Keep this as a disambiguated title match, not an automatic merge.
- `legacy-0001` target `80000000-0000-4000-8000-000000000100` explicitly tests
  nominalized **das Abarbeiten**. `legacy-0002` target
  `80000000-0000-4000-8000-000000000200` tests the verb **abarbeiten**. They share
  lexical meaning but exercise different forms. Preserve both pending a deliberate
  objective-mapping decision; do not silently deduplicate by lemma.
- The exact objective/title collision **Genitive after während / Genitiv nach
  während** is between existing draft targets `40000000-0000-4000-8000-000000000017`
  and `60000000-0000-4000-8000-000000000125`. It predates the enrichment and is
  not evidence that a new enrichment target duplicates another one.

This triage resolves these string-match candidates only. Semantic identity review
against the complete prior corpus remains open.

## Offline capacity and parity

`offline-capacity.ts` checked every question against the current shared
`SessionSchema`: 4,476 questions fit into 37 temporary level-only pages of at most
125 questions. A 126-question payload is rejected for both levels. Existing IDs
remain unique across the pages. These pages are in-memory shape probes, not
installable packs or issued sessions, and have no topic/context assignment.

The catalog can be much larger than one saved round. A future shared bundle must
select/page within the learner's actual level, topic and context; preserve frozen
retry payloads and unfinished work; and supply equivalent web/Android behavior.
The capacity proof does not verify those lifecycle requirements. Do not stuff the
whole catalog into one saved/adopted round or increase the bound as a shortcut.

Source inspection makes the concrete integration gap clear: web `begin()` in
`apps/web/client/src/renovation/guest-starter.tsx` concatenates all eligible pack
questions and calls `SessionSchema.parse` on that one session. Native `start()` in
`apps/android/app/src/main/java/com/germanverbmaster/android/learner/NativeGuestEntry.kt`
also concatenates all eligible pack questions into a single saved session. Merely
splitting a larger bundle into more pack files would therefore not solve the
saved-session bound; the entry/queue selection must change on both clients.
No oversized production bundle was installed to demonstrate a runtime failure.

## Review quality controls and next action

An initial worker generated incomplete coverage and reused positional option
rationales across sibling variants. Those defects were caught before commit.
Exact key-set validation now rejects missing, extra or duplicate review entries,
and a fresh substantive review revisits the affected batches. Matching counts and
option labels are necessary bookkeeping, not proof that the semantic judgment is
sound. Keep concise prompt-specific reasons and distinguish real competing answers
from far-fetched hypothetical readings or optional style changes.

Use `coverage.json` for the latest editorial count and per-batch findings for the
actual judgments. Resume the first pending batch in the frozen 49-batch queue.
Save a meaningful bounded review slice, validate the queue and inactive overlays,
and push only the review branch. Keep candidate-ready/publication flags false
until the unresolved language and integration issues have been addressed.

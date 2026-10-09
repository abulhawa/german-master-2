# Initial content review workspace

## Current editorial policy — 9 October 2026

The owner removed the separate independent human German-review requirement for M1. ChatGPT GPT-6 is the editor for the initial B1/B2 practice candidate. All 60 target review records identify GPT-6 as an **AI** reviewer, include the review date/checklist and bind approval to the exact content SHA-256. Modifying any exercise invalidates that approval. This is an AI editorial decision, not external certification, CEFR assessment or learner validation. The passages below describe earlier checkpoints; any references to an outstanding mandatory independent reviewer are superseded. The five historical published starter revisions remain immutable. Production publishing still requires separate authorization.

`initial-30.json` contains 30 original, agent-authored B1 draft targets with two distinct prompts/contexts each: noun plurals, article/case selection after prepositions, adjective endings, present verb forms and subordinate word order. All five initial exercise forms are represented. These are foundational objectives used by intermediate learners; B2 breadth and the approximately 120-target pilot catalog remain unfinished.

Run `npm run content:review` from the repository root to validate and regenerate [the German-language review workbook](REVIEW.md). It shows every solution-free exercise, revision, accepted answer, explanation, ambiguity note and provenance together for editorial review. The root backend tests exercise validation and reject malformed linkage, rubric answers, identities, context reuse, incomplete approval records, publication flags and leaked solutions.

The independent reviewer edits each target's review record in `initial-30.json`: status (`pending`, `changes-requested`, `approved`), reviewer identity, ISO date, checklist findings and `reviewedHash` copied from the workbook's content SHA-256. Approval needs a named reviewer, date, nonempty notes and matching hash. Content edits invalidate old approval; set the record back to pending before revalidation. Structural validation cannot verify the reviewer is independent or that a grammatical judgment is correct. Record uncertainty as changes requested; include all acceptable alternatives explicitly in the rubric, then rerun validation. Check grammar, naturalness, objective/level, distractors, hint leakage, variants, alternatives and provenance. Context differences alone do not establish retained transfer.

`author-initial.mjs` records original draft authorship. Do not rerun it over reviewer edits: it regenerates pending drafts. Published corrections must use new immutable revisions; this workspace has no publisher. Even all approved records leave `publicationApproved: false`. No content is installed into the API or either learner. The original five-target fixture and its immutable IDs remain untouched.

## Editorial refinement pass — 7 October 2026

ChatGPT completed the first editorial refinement of all 30 targets / 60 variants. The pass replaces template-like prompts with more natural B1 contexts, uses learner-facing target names, makes hints and explanations rule-specific, and strengthens variation across the two contexts for each target. Target IDs, exercise IDs, revision numbers, grading forms and publication boundaries are preserved. Accepted answers were rechecked while editing; the word-order variants now use more natural clauses while retaining a single explicitly instructed ordering.

This is still agent-authored draft content. All 30 review records remain pending, zero targets are independently approved, and `publicationApproved` remains false. `REVIEW.md` has been regenerated from the refined catalog with fresh content hashes. An independent German reviewer must still check grammar, naturalness, level, ambiguity, alternatives, distractors, hint leakage and explanations before any approval or publication.

## Low-typing adjective pilot — 7 October 2026

The five B1 adjective-ending targets are now prepared as immutable revision 2 exercises using `gap_choice` instead of typed cloze answers. Each of the 10 variants offers authored weak-ending distractors (`-e`, `-en`, `-er`, `-es`, `-em`) in deliberately varied authored orders, preserves the existing target and exercise identity, and keeps the original bilingual rule explanation and context. The learning objective now explicitly measures choosing the ending rather than unrestricted written production.

Revision 1 remains the published historical content and is not changed by this workspace edit. The new revisions remain agent-authored, pending independent German review and unpublished. `REVIEW.md` is regenerated from the revision 2 source so reviewer hashes and displayed rubrics match the draft.

## Low-typing verb pilot — 7 October 2026

The five B1 irregular present-tense targets (`fahren`, `lesen`, `geben`, `nehmen`, `sprechen`) are now prepared as immutable revision 2 `gap_choice` exercises instead of typed `multi_slot` answers. All 10 variants retain two explicit subject slots, use plausible finite-form distractors in varied authored orders, and preserve the bilingual rule explanations. The objective wording now reflects choosing the correct forms rather than unrestricted production. Revision 1 remains historical published content; revision 2 remains pending independent review and unpublished.

## Low-typing plural conversion — 8 October 2026

All 10 B1 plural targets are prepared as immutable revision 2 `choice` exercises (20 variants). Each question uses four noun-form options built from the correct plural plus realistic learner errors such as missing umlaut, wrong plural ending, dative-plural `-n`, singular carry-over or `-s` overgeneralization where appropriate. Correct-answer positions vary in authored order because clients do not shuffle at runtime. The objective now explicitly measures choosing the standard plural rather than unrestricted written production. Revision 1 remains historical published content; revision 2 remains unpublished and pending independent German review.


## B2 low-typing conversion — 9 October 2026

All 30 B2 draft targets (60 variants) now use authored revision 2 `gap_choice` exercises instead of typed cloze input. Original exercise/target identifiers and context pairs are preserved, with four distinct options in varied authored order. Existing bilingual explanations remain. B2 level labels describe the learning pack, not a CEFR proficiency assessment. These are AI-authored review drafts, not independently verified German content. The synchronized candidate remains unpublished, and all earlier published revisions remain unchanged.

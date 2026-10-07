# Low-typing practice authoring

The 7 October 2026 owner decision makes selection the default for new practice. This slice supplies working formats on web and Android, shared contracts, grading, draft persistence and offline handling. It does not convert or publish the live catalog. Examples are agent-authored engineering illustrations, pending independent German editorial review, not a production content release.

## Choose the format from the learning objective

| Task | Format | Authoring requirements |
| --- | --- | --- |
| Article, inflection, meaning or best sentence | `choice@1` | One correct selection; plausible, reviewed distractors; avoid clues from length or position. |
| Word order or sentence construction | `word_order@1` | Frozen word tokens with distinct IDs, including repeated words; explicitly accepted complete orders. |
| Complete one or several gaps | `gap_choice@1` | One to six labelled gaps; two to six options per gap; accepted complete selections. |
| Match collocations, meanings or related forms | `matching@1` | Two to eight items per side; equal-sized lists; one-to-one mapping with complete accepted pair sets. |

Do not invent distractors from the accepted answer at runtime. Write and review the prompt, options, hint, rationale and alternatives together. Present options and words in authored order; runtime shuffling is not part of this slice. Matching does not support extra unused distractors or many-to-one mappings. Ordering uses every token exactly once, with no decoy tokens.

## Shared exercise and answer shape

All exercises carry `type`, `schemaVersion: 1`, stable exercise/target UUIDs, revision, German prompt and bilingual instruction/hint. Rubrics remain server-only except inside an intentionally downloaded grading pack. Session responses expose no solution.

Gap choices add `slots: [{ id, label, options: [{ id, text }] }]`. Answers use:

```json
{"type":"gap_choice","selections":[{"slotId":"wir","optionId":"b"},{"slotId":"du","optionId":"c"}]}
```

Matching adds `left` and `right` option arrays. Answers use:

```json
{"type":"matching","pairs":[{"leftId":"decision","rightId":"make"},{"leftId":"question","rightId":"ask"},{"leftId":"responsibility","rightId":"take"}]}
```

Options are identified within their gap; the same option ID may appear in different gaps. Matching IDs are scoped to their side. Slot, option, token and side IDs must be unique in their respective lists. Visible word text can repeat. Pair/selection array order has no grading significance; token order does. Distinct valid linguistic alternatives belong in `acceptedAnswers`, not in client normalization or automatic correction.

Partial selections and pairs may be stored as drafts, including empty lists. They cannot be submitted: linkage validation requires every gap or every item on both matching sides exactly once. Unknown IDs and duplicate assignments are malformed submissions, not incorrect answers. A complete but linguistically wrong selection is graded incorrect. Both clients keep Check explicit and allow revision before submission. Picking a matching right item replaces any existing pair using either selected item; Remove unpairs it.

## Reusable controls and previews

Web: `foundation/exercise-input.tsx` owns the reusable renderer; `foundation/controls.tsx` owns shared buttons/cards. Guest, account, downloaded-practice and backend-preview flows use these directly, without importing the gallery's sample catalog. Controls use semantic tokens, named radio groups/buttons and keyboard alternatives; ordering supports adding, individual removal, left/right moves and reset. Focus stays within the relevant interaction after a word or pair button disappears. Matching lists stack on narrow screens.

Android: `LowTypingInput` is reused by the native practice and static preview. The repository owns durable answer/order state; Check remains outside the renderer. Labels use English/German resources according to the practice locale. Matching lists stack at small available widths or enlarged system text. Native controls are buttons with selected semantics and at least 48dp height; dragging is unnecessary. Language splitting is disabled so both resource languages remain available when the in-app practice locale changes offline, following [Android bundle guidance](https://developer.android.com/guide/app-bundle/configure-base#handling_language_changes). A focused Robolectric/Compose test completes and unpairs matching at font scale 2.0 in a 320dp column; this does not substitute for device magnification/TalkBack acceptance.

Run `npm run dev:client --workspace=apps/web -- --port 5110 --strictPort`, then open `/foundation?formats=1`. Add `&text=200` to inspect controls with a 32px base learner font. The gallery is development-only and inspects prepared answers; it neither submits attempts nor claims confirmed grading. The original five-format gallery remains available at `/foundation` for unconverted content.

The Android debug `FoundationPreviewActivity` accepts boolean intent extra `formats=true`. It packages the solution-free four-question session only. Rubrics, offline pack and conformance cases stay in test resources; they are not included as preview grading assets. No device installation or store upload is required by the foundation.

Canonical samples are `contracts/v2/examples/practice-formats-session.json`, `practice-formats-rubrics.json`, and `practice-formats-pack.json`. The ordering example contains two separate “sie” tokens and two accepted token orders. The sample pack uses two distinct sessions and a canonical SHA-256 manifest. Never use these sample IDs/provenance as published editorial approval.

## Content conversion next

Inventory each current question by objective; choose a format using the table, author distractors/pairs, supply complete accepted answers and bilingual feedback, then preview both clients and complete German editorial review. Create immutable revisions and a reviewed release rather than changing solutions in place. Recognition questions must not claim to measure unrestricted written production or become invented transfer evidence.

No learner-data migration or compatibility shim was added. The clean SQL baseline and disposable local fixture schema accept the new types. An already-created database is not silently altered by this change; provisioning/resetting a clean deployment and publishing reviewed content remain separately authorized operations. The current live catalog continues using its authored formats until that later content slice.

## Verification evidence (7 October 2026)

Root checks/builds and the full 188 backend / 398 web / 13 HTTP tests pass. Final focused renderer/grading checks pass after refinements. Android's full 161-test unit suite, lint and debug assembly pass; a subsequent three-test control run includes completing/unpairing matching with simulated font scale 2.0 in a 320dp column. Browser acceptance covers all four formats at 320px with enlarged learner/answer text in both themes, keyboard interactions and focus recovery. Physical device/TalkBack/system-magnification testing remains pending because no device/emulator was attached. The saved matching screenshot is `.local/practice-formats/matching-desktop.jpg`; detailed handoff is in root `PROGRESS.md`.

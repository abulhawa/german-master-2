# Learner experience audit: 10 October 2026

Status: **implementation branch**, not production acceptance. This review was performed from current source and connected repository evidence, not from an interactive browser session. Maintain the existing M1 and publication gates.

## Mandate

Review as a Head of Product Experience, combining first-time B1/B2 learner, language-learning designer, UX content designer and accessibility reviewer perspectives. Evaluate every surface by whether it helps a learner decide **what to do, why, and what changed**. Technical architecture may be important to engineers, but it should not become ordinary learner copy.

References: [2.0 blueprint](blueprint.md), [learner screen UX](learner-screens-ux.md), [guest UX](guest-practice-ux.md), and [web design guidelines](../../apps/web/docs/ui-ux-guidelines.md).

## Findings and changes

| Area | Evidence / visitor impact | Remedy in this branch | Priority |
| --- | --- | --- | --- |
| Progress | Heading “Server-confirmed progress”, “Refresh confirmed progress” and permanent “Last confirmed snapshot” notice framed studying as backend operations. | Use “Your progress” and “Refresh progress”; render the saved-progress notice only after refresh failure. Keep authoritative data behavior unchanged. | P1 |
| Progress & Home | Labels “learning targets”, “Improving targets” and snapshot language needlessly expose the system's terminology. | Use skills and learner-oriented explanations; preserve precise underlying status counts. | P1 |
| Account & sync | Technical “local server”, “preview”, “Web Locks” and “confirmation” strings in recoverable states made the product feel unfinished. | Rewrite everyday account, practice and error copy with actions that learners understand. Preserve retry safety and explicit consequences of discarding work. | P1 |
| German localization | German learner copy mixed formal **Sie/Ihr** with informal **du/dein**. | Standardize learner-interface prose on **du** outside immutable exercise content. | P1 |
| Loading & failures | Long implementation details competed with recovery actions. | Simplify unavailable, retry, saved-work and completion states; avoid promising that unsynced answers are confirmed. | P1 |
| Practice data | Labels had to distinguish current draft, pending submission, saved progress and a completed session. | Preserve all existing engine and ownership semantics; make states more intelligible in English and German. | P0 safeguard |
| Navigation and cards | The current Home → Practice → Progress / Topics navigation follows the accepted reduced IA. PR #15 already implemented history navigation and initial guided guest answers. | Do not reopen a major redesign without actual learner evidence. | Preserve |
| Visual design | Actual narrow viewport, zoom, contrast, focus, touch, tab switching, animated feedback and reduced motion were not directly observable via this source audit. | **Acceptance pending:** browser-based checks, not source review by assertion. | P1 |

## Acceptance scenarios required before release

1. Unauthenticated learner: guest starter → check feedback → finish → explicit guest attachment offer. No auth coercion.
2. Signed-in learner: Home → choose topic → target → practice → saved summary → Progress. Include correct, incorrect, hint and skip.
3. No progress, improving, mastered, due-review, failed refresh and persisted stale-progress states. Verify no false success language or spurious stale warning.
4. Close, reload, browser Back/Forward, tab hidden/visible, lost/recovered connection and saved work.
5. Device width 320 px through desktop, 200% text, keyboard-only journey, focus visibility, light/dark, English/German wording.
6. Android: verify parity only on a real device/emulator as a separate release acceptance; a web copy pass alone does not certify native behavior.

## Boundaries

No source changes to exercise revisions, learner state contracts, backend, schema, grading, content publication, Android binary, or production environment are requested in this branch. Do not claim a browser or device pass without execution evidence.

## Next actions

Run hosted web/Android checks, inspect an actual browser on the isolated preview at narrow and wide viewports, correct any regressions, then decide on merging and production deployment in their separately authorized scope.

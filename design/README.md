# Foundation design bindings

`tokens.json` implements the blueprint/mockup semantic palette, shared spacing, 48px/dp control minimum and 720px/dp practice column. `generate.mjs` emits scoped CSS and native Kotlin bindings, and validates text/action/control/focus contrast pairs. Run generation/checks through the root foundation scripts.

The first reusable components are PracticeCard, AnswerField, choice input, word-order input, and action buttons, with explicit unanswered, filled, disabled, hint and answer-inspection states. Web supports system/light/dark selection and English/German instructions. Android follows the system theme and locale, with a German exercise-instruction toggle. Web continuation moves focus to the prompt; native focus, TalkBack, large-font/device checks and the full component inventory remain acceptance work.

The 2.0 preview intentionally adopts the blueprint's bounded practice column, larger prompt, 48px controls and inline hint disclosure. These supersede the inherited web guideline's unbounded card, 44px baseline and tooltip-only explanations for this isolated preview. There are no progress metrics or outcome colors because grading and learner state are not implemented. Native widgets use Material 3 with semantic colors; layouts need not be pixel-identical.

The web CSS is scoped to avoid changing legacy pages. Essential input boundaries use `controlBorder`; decorative borders are not relied on for control contrast. The preview has no motion or overlays. Keyboard order, text labels and radio groups use native HTML semantics; word ordering uses buttons and an ordered text list, so dragging is never required.

Automated contrast checks cover token pairs, not every rendered state. Browser checks at 320px verify reflow, target heights and light/dark text. Device, assistive-technology and full 2.0 accessibility acceptance remain open.

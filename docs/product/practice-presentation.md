# One practice experience across clients

Owner direction: 23 October 2026. Apply to web and native Android, guest and
signed-in practice. Authentication selects where work is stored and synced;
it must not introduce a different interaction or technical feedback vocabulary.

## Presentation contract

- Render one input method for an exercise's presentation data. Where answer
  choices are supplied, show selectable choices with a visible selected state;
  omit the duplicate free-text field and any instruction telling users to type.
  Actual free-text exercises without choices retain their labelled input.
- Shared starter modes: plural choice, article choice, preposition choice,
  word-order chips and per-slot verb choices. Store their existing versioned
  answer shapes; choosing a radio must not change grading or exercise identity.
- Check stays explicit and disabled for incomplete answers. Selection never
  submits or advances. Multi-slot choices preserve other fields and partial work.
- Feedback is Correct / Not quite (Richtig / Noch nicht ganz), the accepted form,
  a concise explanation and Continue. Show the learner's answer for an incorrect
  comparison; avoid repeating an already correct answer. Completed guest controls
  and a previously revealed hint do not remain as duplicate feedback content.
- Do not add locally, checked locally or Assisted answer to outcomes. Do not
  repeat account saving instructions beneath every result. Meaningful storage,
  offline and retry states remain contextual; errors must not be hidden.
- Hint usage, provisional receipts and confirmed mastery remain internal evidence
  with their existing policies. Plain correctness wording is not a mastery claim.
- Do not automatically assign local guest work to a newly signed-in account.
  Preserve existing identity boundaries, explicit transfer and retry-safe writes.

This owner direction intentionally simplifies earlier blueprint/help-copy wording
that put assisted/provisional qualifiers on each answer. It supersedes those
presentation suggestions while preserving the underlying grading/evidence rules.

## Implemented scope and evidence

The 23 October correction aligns starter choices and answer outcomes, not every
screen or backend integration. Web uses shared ExerciseInput and PracticeFeedback
across its learner paths; Android shares its low-typing renderer and signed-in/
offline feedback, with guest suggestion controls aligned to the same semantics.
Separate guest/account persistence adapters and screen hosts still exist.

- Browser: all five starter types completed correctly at desktop (1280px) and
  mobile (390px) widths; choice-only input, plain feedback, hint evidence and
  reload restoration verified. Local isolated guest harness; no account network
  request or production deployment. Physical Android evidence is in PROGRESS.md.
- Offline root dependency install, generated/type checks, 194 backend tests,
  426 web tests, 20 HTTP integration tests and learner-product build pass.
- Native focused tests and debug/preview builds are recorded in PROGRESS.md.
  Existing account lifecycle and accepted TalkBack scope were not reopened.

The bundled starter and published catalog remain distinct content inventories.
This does not claim that every published exercise or authenticated device journey
has received a new manual review. Continue the broader dynamic UI audit under the
existing release hold; do not equate this contract with completed release approval.

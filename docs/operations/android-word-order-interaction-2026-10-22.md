# Android word-order interaction - 22 October 2026

The owner prioritized drag-and-drop and layout continuity. This corrects the
previous fixed-distance, long-press approximation; it does not claim that the
prior screenshot review verified gestures.

## Behaviour

- Match web interaction roles: tap an available bank word to append, directly
  drag an answer chip onto another answer chip to place it there, tap an answer
  chip to remove, and explicitly Check. No long-press delay.
- Actual laid-out chip rectangles determine the target, including wrapped rows
  and words of different widths. The dragged chip follows the pointer with a
  raised appearance, and the target receives an outline. Other words retain
  their positions until drop; the existing slot remains during the gesture.
- Commit order only on a valid drop. Cancelling or releasing outside a different
  chip restores the original visual state without altering the durable draft.
- The answer area reserves height and scrolls for overflow; compact bank slots
  stay mounted and used words are disabled. Adding/removing words does not
  shift the bank, Reset or Check vertically. Selected bank placeholders are
  excluded from semantics, avoiding duplicate accessible answer controls.
- Keep stable token IDs, duplicate-word identity, read-only locking, explicit
  submission, existing accessibility move/remove actions and caller-owned
  persistence. No grading, exercise-revision or mastery-policy change.

## Verification

- Final offline checks pass: 14 tests (5 low-typing controls, 3 practice UI,
  6 entry), zero failures/errors/skips; lintDebug zero errors / 18 warnings;
  assembleDebug and assembleLearnerPreview pass. One existing next-question
  focus assertion failed once and passed unchanged on the final rerun. This
  transient is retained as an evidence limit; no new TalkBack acceptance claim.
- Gesture tests exercise a direct drag to a measured target, cancellation,
  read-only gesture rejection, tap removal and unchanged Reset position. The
  existing mixed-form journey verifies caller draft persistence and submission.
- Installed by replacement-in-place on the connected Pixel preview, preserving
  the original app and data. Selected all six starter words, dragged the first
  onto the last across rows, and verified six retained tokens in the new order.
  Reversed the drag, removed exactly one word by tap and verified incomplete
  Check disabled. Force-stop/relaunch restored the five-word draft.
- Recorded the actual drag and inspected before/mid-gesture/after physical
  frames: the dragged chip moves while neighbouring slots stay in place until
  drop. The recording and XML/geometry evidence remain private outside checkout;
  the ignored word-order output-path record locates them.
- Physical geometry: Check text bounds stay [425,1973,656,2026] and Reset text
  bounds stay [95,1795,285,1848] with an empty answer, all six selected and after
  drop. Check retains the same position after a tap removal. These coordinates
  establish this Pixel configuration only, not every device/font scale.
- Full suites, every viewport/font configuration and the authenticated physical
  flow were not repeated. Both guest and signed-in callers use the same control.
  No real account request, live AI call, content publication or store release.

## Limits

Only visible answer chips are drop targets. Scroll an overflowing answer area
before dragging to another visible chip; edge-triggered drag auto-scroll is
not implemented. Bank selection remains tap-to-append, matching the web's
available-word buttons. The bank's stable disabled placeholders intentionally
avoid the shrinking web bank layout. This is a native presentation refinement.

Android release remains blocked pending explicit owner UX acceptance. The
accepted TalkBack scope is unchanged; gesture tests do not reopen that gate.

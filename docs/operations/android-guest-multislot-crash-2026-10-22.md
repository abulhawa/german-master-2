# Guest verb-choice crash - 22 October 2026

The owner reported a Pixel crash after tapping the du verb choice in the fifth
starter exercise. The preview crash log confirms IllegalArgumentException:
Invalid AnswerMultiSlot.values, raised in NativeGuestEntry while constructing
the answer on selection, before the local store write.

The old handler constructed a multi-slot answer from only the fields selected
so far. The versioned answer contract requires at least two fields. Selecting
either first field therefore produced an invalid one-field object. The
signed-in typed input already represents all fields, with empty strings for
unfinished fields; the guest handler now uses the same representation and
canonical exercise slot order.

Each tap replaces only its field. Other selections survive, unanswered fields
remain empty, and Check stays disabled until all fields are nonblank. The
existing store persists partial drafts without changing grading or contract
validation. The crash did not write the invalid answer; earlier saved guest
work is preserved. No reset, migration or data clear is needed.

Regression coverage exercises du-first selection, partial-store restoration,
ihr-first selection, changing the first choice and successful local grading.
Final verification and physical reproduction are recorded in root PROGRESS.md.
Android release remains blocked pending explicit owner UX acceptance.

## Verification

- Final offline run: 13 tests (8 NativeEntryTest and 5 LowTypingInputTest),
  zero failures/errors/skips; lintDebug passes with zero errors / 18 warnings;
  debug and learner-preview assembly pass. The initial test selectors matched
  both identical arbeitet buttons; scoped selection was corrected before the
  passing run. The broader practice suite again hit the previously recorded
  next-question focus assertion; this is not claimed resolved by this crash fix.
- Replaced the preview on the connected Pixel without uninstalling or clearing
  data. On the owner's saved fifth question, tapping arbeitest leaves the app
  running and Check disabled. Force-stop/relaunch preserves that partial draft.
  Selecting ihr arbeitet enables Check; explicit Check produces Correct local
  feedback. Private XML/physical captures preserve this evidence.
- No contract weakening, signing/version change, production request, deployment,
  content publication or store release. Android release hold remains active.

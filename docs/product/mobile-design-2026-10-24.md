# Mobile visual direction

Owner accepted the interactive proposal on 24 October 2026 and requested mobile
implementation first, without a connected phone. This replaces the previous
large coloured session panels and repeated session headings. Existing product
scope, content, grading and storage rules remain the baseline.

- Use quiet neutral surfaces, an indigo action colour, restrained borders and
  clear type hierarchy. Generate the palette for web and Compose from
  `design/tokens.json`. Support light/dark appearance and text scaling.
- Home has a compact brand/language/account header, one headline, one session
  card and one obvious Start/Continue action. Keep session metadata together.
  Topics remains optional exploration of real available content.
- Use readable topic rows, generous answer targets, a selected radio state,
  question progress, an explicit Check action and a distinct correction panel.
  Feedback retains the accepted answer and explanation, with one Continue action.
- Keep Home / Progress / Topics navigation on non-practice pages. Practice has
  Close, question count and safe account access. Preserve drafts and explicit
  account attachment. Do not add account promotions to practice or completion.
- Account-dependent pages retain contextual authentication. Empty account-backed
  Progress explains the next step and offers the existing practice action.
  Native destination changes reset page scroll instead of opening halfway down.

Native UI uses shared `StudyHeader`, `StudyPanel`, `StudyPrimary`, `StudyTopicRow`
and `ChoiceOption` components. Both local and account-backed practice consume the
same theme and feedback presentation. Mobile web uses the generated palette and
the same hierarchy, with native layout conventions retained on Android.

This intentionally supersedes the legacy optional analytics/sidebar guidance.
The current blueprint's focused practice and three primary destinations remain.
No new navigation destination, content pack or learning policy was introduced.

## Verification boundary

Focused Robolectric Compose tests cover navigation, draft persistence, cancelled
auth, system Back, exercise selection, locked results, session completion,
topics/focused practice and account switching. Native rendering evidence is under
the ignored `apps/android/app/build/outputs/mobile-design/` directory: light/dark
Home, narrow German Home, 1.6x text, selected answer, incorrect feedback, Topics,
account gate, sign-in and fixture-backed Home/Progress. These are rendered native
components, not physical-device screenshots or live account acceptance.

Local web browser checks cover 390px practice/navigation, draft reload, correct
and incorrect feedback, completion without promotions, German at 320px and a
1280px layout/overflow check. The harness uses the real guest components with
local storage; authentication is not submitted. Mounted tests cover the existing
account-backed flows separately. React review checked stable list keys, native
accessible controls, labelled progress, unchanged hook order and no added data
fetches or storage schema changes.

The production-configured learnerPreview APK uses the existing recovered public
configuration. Configuration validation and assembly do not prove live Auth.
Android publication and final owner UX acceptance remain held. No production
deployment is part of this design implementation.

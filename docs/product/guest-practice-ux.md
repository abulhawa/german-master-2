# Guest practice UX handoff

Status: implementation-ready product/design specification  
Prepared: 6 October 2026  
Baseline: [German Master 2.0 blueprint](blueprint.md)

This document completes the ChatGPT guest-practice design deliverable from
[plan-of-work.md](plan-of-work.md). It specifies S01 welcome/setup, the local
guest starter journey, the post-practice save prompt, authenticated attachment
copy, and relevant error/empty states for web and Android.

It does not authorize content publication, production deployment, or account
data attachment. Guest work is local/provisional until the learner explicitly
chooses to attach eligible attempts after authentication.

## 1. Product decision

A first-time learner should reach a real German exercise without creating an
account.

The flow is:

1. Choose interface language.
2. Choose approximate level.
3. Choose normal session length.
4. Start a bundled reviewed starter session as a guest.
5. Complete, partially complete, or leave the session with local durable state.
6. At summary/Home, offer account creation or sign-in to save eligible local
   attempts and use confirmed progress across devices.
7. After authentication, show exactly how many eligible local attempts can be
   attached and ask for explicit confirmation.
8. Attach only after confirmation. Repeated attachment must be idempotent.

Do not put authentication in front of the first useful exercise. Do not imply
that guest results are confirmed progress or mastery.

## 2. Information hierarchy

### Welcome/setup, S01

One card/column, no product dashboard.

**Brand**
German Master

**Heading**
- EN: Practise what needs attention.
- DE: Übe, was noch Aufmerksamkeit braucht.

**Supporting copy**
- EN: Short mixed sessions help you find recurring mistakes and make the right form more reliable.
- DE: Kurze gemischte Übungen helfen dir, wiederkehrende Fehler zu erkennen und die richtige Form sicherer zu machen.

The setup is one compact flow with three selections. On narrow screens these
may appear as three stacked sections; on desktop they remain in one centered
card.

### Step A, interface language

Label:
- EN: Interface language
- DE: Sprache der Oberfläche

Options:
- English
- Deutsch

Helper:
- EN: You can change this later.
- DE: Du kannst das später ändern.

The selected interface language takes effect immediately, including the rest of
setup. Exercise content remains German.

### Step B, level

Label:
- EN: Your current German level
- DE: Dein aktuelles Deutschniveau

Options:
- B1
- B2
- Not sure / Nicht sicher

Helper:
- EN: This only guides the starter mix. It is not a level test.
- DE: Das bestimmt nur die Auswahl der ersten Übungen. Es ist kein Einstufungstest.

If "Not sure" is selected, use the conservative B1-B2 starter mix defined by
content. Never label the learner with a certified level.

### Step C, usual session

Label:
- EN: Usual session
- DE: Übliche Übungslänge

Options:
- 10 questions / 10 Fragen
- 15 questions / 15 Fragen
- 20 questions / 20 Fragen

Default: 15 questions.

Helper:
- EN: About 10 minutes for 15 questions. No timer.
- DE: Etwa 10 Minuten für 15 Fragen. Ohne Zeitdruck.

### Primary action

- EN: Try German Master
- DE: German Master ausprobieren

Secondary action:
- EN: I already have an account
- DE: Ich habe bereits ein Konto

The secondary action opens authentication. It must not suggest that an account
is required to try the product.

## 3. First-use behavior

On primary action:

- Save setup preferences locally before session creation.
- Load the bundled reviewed starter pack.
- Create a local guest session using the chosen count when enough reviewed
  exercises exist.
- If fewer exercises are available, transparently start the shorter session.
- Persist the session, current question, draft, assistance state, completed
  answers, skips, and provisional evaluations locally.
- No guest attempt creates server-confirmed mastery.
- A browser refresh, process restart, or normal back/forward navigation must not
  silently discard completed guest work.

If the starter pack cannot be opened, keep setup selections and show the failure
state below.

## 4. Starter launch states

### Loading

Heading:
- EN: Preparing your starter practice
- DE: Deine ersten Übungen werden vorbereitet

Body:
- EN: We’re getting a short mixed session ready.
- DE: Wir stellen eine kurze gemischte Übung für dich zusammen.

Do not show invented counts before the actual session is known.

### Shorter than requested

Inline notice at the first question:
- EN: Shorter session: {n} reviewed questions are available.
- DE: Kürzere Übung: {n} geprüfte Fragen sind verfügbar.

Do not block start.

### Starter unavailable

Heading:
- EN: Starter practice isn’t available
- DE: Die ersten Übungen sind gerade nicht verfügbar

Body:
- EN: Your choices are saved. Try again when the starter pack is available.
- DE: Deine Auswahl ist gespeichert. Versuche es erneut, sobald das Übungspaket verfügbar ist.

Primary:
- EN: Try again
- DE: Erneut versuchen

Secondary:
- EN: Change setup
- DE: Auswahl ändern

If authentication is available, a tertiary text action may say:
- EN: Sign in instead
- DE: Stattdessen anmelden

## 5. Guest practice chrome

Guest practice uses the same S03/S04 focused practice UI as authenticated
practice. Do not introduce a visually separate "demo" exercise engine.

Add one quiet status line near the question count:

- EN: Guest practice · saved on this device
- DE: Gastübung · auf diesem Gerät gespeichert

This text is not a warning and must not compete with the prompt.

For provisional feedback, use:

Correct:
- EN: Looks correct locally
- DE: Sieht lokal richtig aus

Incorrect:
- EN: Not quite
- DE: Noch nicht ganz

Supporting status:
- EN: This result is saved on this device. Sign in later to save eligible attempts to your account.
- DE: Dieses Ergebnis ist auf diesem Gerät gespeichert. Melde dich später an, um geeignete Versuche in deinem Konto zu speichern.

Never use "confirmed", "mastered", "synced", or an equivalent server-authority
claim for guest evidence.

## 6. Leaving a guest session

Close follows the normal partial-session behavior.

Dialog title:
- EN: Leave this session?
- DE: Diese Übung verlassen?

Body:
- EN: Your answers and draft stay on this device.
- DE: Deine Antworten und dein Entwurf bleiben auf diesem Gerät.

Actions:
- EN: Save and return Home
- DE: Speichern und zur Startseite

- EN: Keep practising
- DE: Weiter üben

If product behavior supports explicit early completion, the separate
"End session" action remains governed by the existing completion contract. The
guest copy must still describe the result as local/provisional.

## 7. Guest summary, S05

### Heading

Completed:
- EN: Starter session complete
- DE: Erste Übung abgeschlossen

Partial:
- EN: Session saved
- DE: Übung gespeichert

### Summary line

- EN: {graded} answered · {skipped} skipped
- DE: {graded} beantwortet · {skipped} übersprungen

If a local correct count is shown:
- EN: {correct} look correct locally
- DE: {correct} sehen lokal richtig aus

Do not display mastery percentages, "level gained", streaks, or confirmed
progress.

### Learning message

- EN: This is a starting point. Reliable progress comes from later unassisted checks.
- DE: Das ist ein Anfang. Verlässlicher Fortschritt zeigt sich bei späteren Übungen ohne Hilfe.

### Save card

Show when there is at least one eligible local attempt.

Heading:
- EN: Keep your progress
- DE: Fortschritt behalten

Body:
- EN: Create an account or sign in to save this practice and use confirmed progress across devices.
- DE: Erstelle ein Konto oder melde dich an, um diese Übungen zu speichern und bestätigten Fortschritt auf mehreren Geräten zu nutzen.

Primary:
- EN: Create account
- DE: Konto erstellen

Secondary:
- EN: Sign in
- DE: Anmelden

Quiet text action:
- EN: Not now
- DE: Nicht jetzt

"Not now" returns to guest Home and preserves local guest history. Do not nag
again during the same completed session.

If there are no eligible attempts, omit the save card and do not claim that
anything can be imported.

## 8. Guest Home after first practice

The guest Home is a lightweight continuation state, not the full confirmed
Progress dashboard.

Primary card:
- EN heading: Continue practising
- DE heading: Weiter üben

If a partial session exists:
- EN action: Continue session
- DE action: Übung fortsetzen

Otherwise:
- EN action: Start practice
- DE action: Übung starten

Status:
- EN: Your guest practice is saved on this device.
- DE: Deine Gastübungen sind auf diesem Gerät gespeichert.

Account callout, non-blocking:
- EN heading: Save across devices
- DE heading: Auf mehreren Geräten speichern

- EN body: Sign in when you want to attach eligible guest attempts and build confirmed progress.
- DE body: Melde dich an, wenn du geeignete Gastversuche übernehmen und bestätigten Fortschritt aufbauen möchtest.

Actions:
- EN: Create account · Sign in
- DE: Konto erstellen · Anmelden

Progress navigation while signed out may open a guest explanation rather than
a fake confirmed progress view:

- EN: Confirmed Progress starts after you save eligible practice to an account.
- DE: Bestätigter Fortschritt beginnt, nachdem du geeignete Übungen in einem Konto gespeichert hast.

## 9. Authentication entry from guest practice

Authentication may be opened from setup, summary, Home, or an account action.
The auth form itself should keep the current guest state untouched.

Above authentication when eligible guest data exists:

- EN: Your guest practice will stay on this device while you sign in.
- DE: Deine Gastübungen bleiben auf diesem Gerät, während du dich anmeldest.

Cancel:
- EN: Back to guest practice
- DE: Zurück zur Gastübung

Authentication failure, expiration, or user cancellation must not erase the
guest session or eligible attempts.

## 10. Explicit attachment after authentication

Successful authentication does not itself attach guest history.

When eligible attempts exist, show a dedicated confirmation sheet/dialog before
server attachment.

Title:
- EN: Save your guest practice?
- DE: Gastübungen speichern?

Count body:
- EN singular: 1 eligible attempt is saved on this device.
- EN plural: {n} eligible attempts are saved on this device.
- DE singular: 1 geeigneter Versuch ist auf diesem Gerät gespeichert.
- DE plural: {n} geeignete Versuche sind auf diesem Gerät gespeichert.

Explanation:
- EN: We’ll validate the exercise versions before adding them to this account. Practice that can’t be validated will stay on this device.
- DE: Wir prüfen zuerst die Übungsversionen, bevor wir sie diesem Konto hinzufügen. Übungen, die nicht geprüft werden können, bleiben auf diesem Gerät.

Primary:
- EN: Save {n} attempts
- DE: {n} Versuche speichern

Secondary:
- EN: Not now
- DE: Nicht jetzt

Never preselect or auto-trigger the primary action.

## 11. Attachment states

### In progress

- EN: Saving guest practice…
- DE: Gastübungen werden gespeichert…

Block duplicate submission but do not block navigating away if durable
retry-safe state is already stored.

### Complete

Heading:
- EN: Guest practice saved
- DE: Gastübungen gespeichert

Body when all attached:
- EN: {n} attempts were added to this account. Progress will use the server-confirmed results.
- DE: {n} Versuche wurden diesem Konto hinzugefügt. Der Fortschritt verwendet die vom Server bestätigten Ergebnisse.

Primary:
- EN: View Progress
- DE: Fortschritt ansehen

Secondary:
- EN: Continue practising
- DE: Weiter üben

The local source records may be marked attached only after durable server
acknowledgement.

### Partial validation

Heading:
- EN: Some practice couldn’t be saved
- DE: Einige Übungen konnten nicht gespeichert werden

Body:
- EN: {saved} attempts were added. {remaining} stay on this device because their exercise versions could not be validated.
- DE: {saved} Versuche wurden hinzugefügt. {remaining} bleiben auf diesem Gerät, weil ihre Übungsversionen nicht geprüft werden konnten.

Actions:
- EN: Continue
- DE: Weiter

Optional:
- EN: Review details
- DE: Details ansehen

Do not turn validation rejection into a false incorrect answer.

### Temporary failure

Heading:
- EN: Guest practice wasn’t saved
- DE: Gastübungen wurden nicht gespeichert

Body:
- EN: Your attempts are still on this device. You can retry safely.
- DE: Deine Versuche sind weiterhin auf diesem Gerät gespeichert. Du kannst es sicher erneut versuchen.

Primary:
- EN: Try again
- DE: Erneut versuchen

Secondary:
- EN: Later
- DE: Später

### Already attached / replay

Treat an idempotent replay as success. Do not show an error just because the
same attachment request has already completed.

## 12. Account mismatch and shared-device safety

The product must not assume guest history belongs to whichever account signs in
next.

The explicit attachment dialog is required every time unattached guest evidence
is about to be associated with an account.

If the learner signs out before attaching, local guest evidence remains
unattached.

If a different account signs in later, the same explicit confirmation is
required. The copy may add:

- EN: Only save these attempts if they are yours.
- DE: Speichere diese Versuche nur, wenn sie von dir sind.

A "Discard guest practice" control belongs in Account/Privacy or guest Home,
behind confirmation:

Title:
- EN: Delete guest practice from this device?
- DE: Gastübungen von diesem Gerät löschen?

Body:
- EN: This removes local guest sessions and attempts that have not been saved to an account. This can’t be undone.
- DE: Dadurch werden lokale Gastübungen und Versuche gelöscht, die noch nicht in einem Konto gespeichert wurden. Das kann nicht rückgängig gemacht werden.

Destructive:
- EN: Delete from device
- DE: Vom Gerät löschen

Cancel:
- EN: Cancel
- DE: Abbrechen

## 13. Responsive behavior

### Web desktop

- Center setup in a readable column, approximately 640–720 px.
- Keep existing compact site identity, but no learner navigation is needed
  before setup is complete.
- Practice remains outside the normal navigation rail.
- Attachment confirmation may be a modal dialog if focus trapping, keyboard
  escape, and return focus are correct.

### Narrow web

- Single-column setup.
- Selection controls fill available width.
- Keep the primary action above the fold when practical, without reducing text
  size.
- Do not use side-by-side level/session controls if labels wrap awkwardly.

### Android

- Native Material controls and standard system back behavior.
- Setup may be a single scrollable screen rather than a carousel.
- Do not introduce a forced onboarding pager unless usability testing shows it
  is needed.
- Authentication cancellation returns to the same durable guest state.
- Attachment confirmation is a native dialog/bottom sheet with accessible
  heading and explicit actions.

## 14. Accessibility requirements

- All selectable cards/options expose role, label, selected state, and at least
  a 48 px/dp target.
- Language change must move focus predictably and not reset the other choices.
- The setup can be completed with keyboard only on web and TalkBack on Android.
- Status changes such as local save, attachment success, or temporary failure
  use a polite live-region/accessibility announcement where appropriate.
- Do not use color alone for selected, provisional, success, or failure states.
- 200% web text zoom and large Android font scale must reflow without hiding the
  primary action.
- Error messages are associated with the action/state that caused them.
- Modal/dialog focus returns to the invoking control after dismissal.

## 15. Analytics and privacy boundaries

Useful product events may record the flow stage and anonymous/local session
state, but must not contain answer text unless the established privacy model
explicitly permits it.

Suggested event names:

- guest_setup_completed
- guest_session_started
- guest_session_completed
- guest_session_left_partial
- guest_auth_started
- guest_attach_prompt_shown
- guest_attach_confirmed
- guest_attach_succeeded
- guest_attach_partially_validated
- guest_attach_failed

Do not use analytics delivery as a prerequisite for continuing practice.

## 16. Implementation acceptance checklist

This handoff is satisfied in a client when all applicable items pass:

- Signed-out first visit can reach a starter exercise without authentication.
- Language, level, and session preference persist locally.
- Guest practice uses the same exercise renderer/feedback model as normal
  practice.
- Guest results are labelled local/provisional and never create confirmed
  mastery before attachment.
- Refresh/process restart preserves guest session, draft, completed answers, and
  skips.
- Leaving practice preserves the partial session.
- Summary offers account creation/sign-in only after useful practice and remains
  dismissible.
- Successful sign-in does not silently attach guest history.
- Attachment dialog shows the eligible attempt count and requires explicit
  confirmation.
- Server revision validation happens before ownership is assigned.
- Repeating an attachment request is safe and does not duplicate evidence.
- Temporary attachment failure preserves local attempts and supports retry.
- Partial validation distinguishes attached and remaining local attempts.
- Signing into a different account still requires explicit attachment consent.
- Guest history can be deleted locally through an explicit destructive
  confirmation.
- Web keyboard/screen-reader behavior and Android TalkBack/font-scale behavior
  meet the existing accessibility gate.

## 17. Deliberate non-features

Do not add these to the guest slice:

- mandatory signup before practice
- countdown timers
- streaks, XP, trophies, or "level up" claims
- client-confirmed mastery
- automatic guest-history import after login
- AI chat or generated feedback
- a separate simplified demo exercise engine
- cross-device transfer of an unfinished guest session
- pressure copy such as "Don't lose your progress"

The intended experience is simple: try real practice first, keep it safely on
the device, and let the learner explicitly save valid evidence to an account
when they decide the product is useful.

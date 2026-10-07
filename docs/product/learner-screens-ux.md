# Learner screens UX handoff

Status: implementation-ready product/design specification  
Prepared: 7 October 2026  
Baseline: [German Master 2.0 blueprint](blueprint.md)  
Related: [guest practice UX](guest-practice-ux.md), [visual concepts](../design/mockups/README.md)

This document completes the broader ChatGPT learner-screen handoff from
[plan-of-work.md](plan-of-work.md). It specifies the shared learner experience
for Home, Practice, Session summary, Progress, Target detail, Topics and Topic
detail on web and native Android.

It is a product/design handoff, not implementation evidence. It does not
authorize content publication, deployment, account attachment or release.

## 1. Product-level decisions

The learner experience has three persistent destinations:

1. Home
2. Progress
3. Topics

Practice is a focused flow outside the normal navigation shell.

Account, sync/download and privacy actions are secondary surfaces. They must not
compete with the primary study path.

The default daily path remains:

Home → Start/Continue practice → Question → Check → Feedback → Continue →
Summary → Home or Progress.

The interface should answer three questions without forcing the learner to
interpret analytics:

- What should I do now?
- Why am I seeing this?
- What has become more reliable over time?

Avoid gamification, streak pressure, large dashboard metrics, mastery
probabilities, countdowns and AI-chat framing.

## 2. Shared shell and navigation

### Wide web, 1024 px and above

Use a compact left rail.

Top:
- German Master wordmark

Primary navigation:
- Home / Startseite
- Progress / Fortschritt
- Topics / Themen

Bottom/secondary:
- Account / Konto

The selected destination has a visible text label and state beyond color.
Do not add History, Analytics, Writing or Vocabulary as primary destinations.

Main content uses a centered readable region. Home and Progress may use a wider
content width than Practice; Practice remains approximately 720 px maximum.

### Medium and narrow web

Below the wide breakpoint, use a compact top bar and bottom navigation with the
same three destinations: Home, Progress, Topics.

Do not collapse the primary navigation into a hamburger for ordinary learner
use.

### Android

Use Material 3 bottom navigation with exactly:
- Startseite
- Fortschritt
- Themen

Practice replaces the navigation shell until the session ends or the learner
closes it.

System back from Practice follows the partial-session flow rather than silently
discarding work.

## 3. S02 Home

### Purpose

Give one obvious next action and a small amount of trustworthy context.

### Layout order

1. Page heading
2. Primary practice card
3. Needs-practice / retention summary
4. Recently improving targets, when available
5. Quiet sync/account status only when relevant

### Default heading

EN: **Ready to practise?**  
DE: **Bereit zum Üben?**

Supporting line:
- EN: We’ll mix the targets that need attention with useful review.
- DE: Wir mischen Themen, die Aufmerksamkeit brauchen, mit sinnvoller Wiederholung.

### Primary card, due work exists

Title:
- EN: Practice what needs attention
- DE: Übe, was Aufmerksamkeit braucht

Reason line examples:
- EN: 4 targets need practice · 2 retention checks
- DE: 4 Themen brauchen Übung · 2 Wiederholungen sind fällig

Primary:
- EN: Start practice
- DE: Übung starten

If a durable partial session exists:
- EN: Continue session
- DE: Übung fortsetzen

Secondary text:
- EN: About {n} questions
- DE: Etwa {n} Fragen

Do not show a countdown estimate.

### No confirmed evidence yet

Title:
- EN: Let’s find what to practise
- DE: Finden wir heraus, was du üben solltest

Body:
- EN: Start a mixed session and German Master will begin identifying recurring weak points.
- DE: Starte eine gemischte Übung. German Master erkennt mit der Zeit wiederkehrende Unsicherheiten.

Primary:
- EN: Start practice
- DE: Übung starten

Do not show empty mastery percentages.

### No due targets

Title:
- EN: Nothing urgent right now
- DE: Im Moment ist nichts dringend

Body:
- EN: You can practise something new or choose a topic.
- DE: Du kannst etwas Neues üben oder ein Thema auswählen.

Primary:
- EN: Try something new
- DE: Etwas Neues üben

Secondary:
- EN: Choose a topic
- DE: Thema auswählen

### Pending sync overlay

Keep Home usable.

Status:
- EN: {n} answers are saved on this device and waiting to sync.
- DE: {n} Antworten sind auf diesem Gerät gespeichert und warten auf die Synchronisierung.

Action:
- EN: Retry sync
- DE: Synchronisierung erneut versuchen

Never replace Start/Continue practice with a blocking sync screen if local
practice can continue safely.

### API unavailable

If a prepared local session exists:
- EN: You’re offline. Your prepared practice is still available.
- DE: Du bist offline. Deine vorbereitete Übung ist weiterhin verfügbar.

Primary:
- EN: Continue offline
- DE: Offline weiterüben

If no prepared content exists:
- EN: Practice can’t be prepared right now.
- DE: Im Moment kann keine Übung vorbereitet werden.

Action:
- EN: Try again
- DE: Erneut versuchen

## 4. S03 Practice question

### Focused header

Left:
- Close / Schließen

Center or right:
- Question {current} of {total}
- Frage {current} von {total}

Optional quiet status:
- Saved / Gespeichert
- Saved on this device / Auf diesem Gerät gespeichert

No main navigation rail or bottom navigation while answering.

### Content order

1. Target/topic context, only if useful
2. German prompt
3. Supporting context or gloss
4. Answer control
5. Optional character helpers
6. Hint/Show answer disclosure
7. Skip
8. Primary Check action

### Typed answer

Label:
- EN: Your answer
- DE: Deine Antwort

Primary:
- EN: Check
- DE: Prüfen

Character helpers:
ä · ö · ü · ß

They insert characters at the current caret and do not replace the normal
keyboard.

Enter submits only while the answer state is valid and no IME composition is
active.

### Choice question

Selection does not submit.

Primary remains:
- EN: Check
- DE: Prüfen

### Word order

Use clearly focusable tokens with move-left/move-right actions. Drag-and-drop
may be offered, but keyboard and TalkBack alternatives are required.

Instruction:
- EN: Put the words in the correct order.
- DE: Bringe die Wörter in die richtige Reihenfolge.

### Empty state

Check remains disabled.

Do not display an error before the learner attempts submission.

### Invalid input

Use a specific inline message.

Examples:
- EN: Complete every blank before checking.
- DE: Fülle alle Lücken aus, bevor du prüfst.

- EN: Use each word once.
- DE: Verwende jedes Wort genau einmal.

Do not use generic “Invalid answer”.

### Saving/submitting

Lock the submitted answer for that question.

Primary label:
- EN: Checking…
- DE: Wird geprüft…

If server confirmation is unavailable but the local attempt is durably stored,
continue into explicitly provisional feedback.

### Durable-save failure

Do not show feedback as if the answer were saved.

Heading:
- EN: Your answer wasn’t saved
- DE: Deine Antwort wurde nicht gespeichert

Body:
- EN: Your draft is still here. Try again before continuing.
- DE: Dein Entwurf ist noch da. Versuche es erneut, bevor du weitermachst.

Action:
- EN: Try again
- DE: Erneut versuchen

### Hint and Show answer

Actions:
- EN: Hint / Show answer
- DE: Hinweis / Antwort zeigen

Assisted attempts remain visibly assisted in feedback and do not create
independent mastery evidence.

### Skip

Action:
- EN: Skip
- DE: Überspringen

Skip records exposure without a correct/incorrect grade.

## 5. S04 Practice feedback

### Required hierarchy

1. Outcome label and icon
2. Learner answer
3. Accepted answer, if useful
4. One concise explanation
5. Assistance/provisional status when relevant
6. Report-a-problem action
7. Continue

Do not auto-advance.

### Correct

Heading:
- EN: Correct
- DE: Richtig

Example explanation:
- EN: “Mit” takes the dative here.
- DE: „Mit“ steht hier mit dem Dativ.

### Incorrect

Heading:
- EN: Not quite
- DE: Noch nicht ganz

Labels:
- EN: Your answer / Accepted answer
- DE: Deine Antwort / Richtige Antwort

Explanation should teach the tested distinction rather than merely restating
the answer.

### Assisted

Status:
- EN: Answered with help
- DE: Mit Hilfe beantwortet

Supporting copy:
- EN: This helps you practise, but it won’t count as an independent mastery check.
- DE: Das hilft beim Üben, zählt aber nicht als selbstständige Überprüfung.

### Skipped

Heading:
- EN: Skipped
- DE: Übersprungen

Body:
- EN: We’ll keep this target available for later practice.
- DE: Dieses Thema bleibt für spätere Übungen verfügbar.

### Provisional/offline

Status:
- EN: Saved on this device
- DE: Auf diesem Gerät gespeichert

Body:
- EN: The result will be confirmed after sync.
- DE: Das Ergebnis wird nach der Synchronisierung bestätigt.

Do not use “mastered” from provisional evidence.

### Disputed/content problem

Action:
- EN: Report a problem
- DE: Problem melden

After submission:
- EN: Thanks. This exercise was sent for review.
- DE: Danke. Diese Übung wurde zur Prüfung gemeldet.

Reporting does not change the displayed grade automatically.

### Primary action

- EN: Continue
- DE: Weiter

Keyboard shortcut for Continue must not be triggered by the same Enter press
that submitted the answer.

## 6. S05 Session summary

### Complete session

Heading:
- EN: Session complete
- DE: Übung abgeschlossen

Summary:
- EN: {answered} answered · {skipped} skipped
- DE: {answered} beantwortet · {skipped} übersprungen

If useful:
- EN: {targets} targets practised
- DE: {targets} Themen geübt

Learning message:
- EN: Today’s answers are evidence. Reliable progress comes from later unassisted checks.
- DE: Deine heutigen Antworten sind Hinweise. Verlässlicher Fortschritt zeigt sich bei späteren Übungen ohne Hilfe.

Primary:
- EN: Back to Home
- DE: Zur Startseite

Secondary:
- EN: View Progress
- DE: Fortschritt ansehen

### Partial session

Heading:
- EN: Session saved
- DE: Übung gespeichert

Body:
- EN: Your completed answers and current draft are saved. You can continue later.
- DE: Deine beantworteten Aufgaben und dein aktueller Entwurf sind gespeichert. Du kannst später weitermachen.

Primary:
- EN: Back to Home
- DE: Zur Startseite

Secondary:
- EN: Continue practising
- DE: Weiter üben

### Pending sync

Status:
- EN: Some results are still waiting to sync.
- DE: Einige Ergebnisse warten noch auf die Synchronisierung.

Do not hide the local summary.

### Confirmed

Status:
- EN: Progress updated
- DE: Fortschritt aktualisiert

This means the authoritative backend accepted the relevant evidence. It does
not mean every practised target is mastered.

## 7. S06 Progress

### Purpose

Show learning state by target, not a single score.

### Page heading

- EN: Progress
- DE: Fortschritt

Supporting copy:
- EN: See what needs practice, what is improving, and what has become reliable.
- DE: Sieh, was noch Übung braucht, was sich verbessert und was inzwischen zuverlässig sitzt.

### Group order

1. Needs practice / Braucht Übung
2. Improving / Wird sicherer
3. Mastered / Sicher

A mastered target may separately show:
- EN: Retention check due
- DE: Wiederholung fällig

Do not move due mastered targets back into “Needs practice” only because a
retention check is due.

### Target row/card

Show:
- Human-readable target name
- State
- Short reason
- Optional due/retention marker
- Chevron/open action

Reason examples:
- EN: Missed recently
- DE: Kürzlich falsch beantwortet

- EN: Correct in recent unassisted checks
- DE: In den letzten selbstständigen Übungen richtig

- EN: Time for a retention check
- DE: Zeit für eine Wiederholung

Avoid confidence percentages unless the product later has a reviewed reason to
expose them.

### No evidence

Heading:
- EN: No confirmed progress yet
- DE: Noch kein bestätigter Fortschritt

Body:
- EN: Complete a few practice questions and your target progress will appear here.
- DE: Beantworte einige Übungen. Danach erscheint hier dein Fortschritt nach Themen.

Primary:
- EN: Start practice
- DE: Übung starten

### Pending overlay

Banner/status:
- EN: {n} local results are waiting to sync. Confirmed progress may change afterward.
- DE: {n} lokale Ergebnisse warten auf die Synchronisierung. Der bestätigte Fortschritt kann sich danach ändern.

### Stale snapshot

Status:
- EN: Showing your last confirmed progress from {date}.
- DE: Angezeigt wird dein zuletzt bestätigter Fortschritt vom {date}.

Action:
- EN: Refresh
- DE: Aktualisieren

Keep the last confirmed snapshot visible instead of replacing it with a blank
error page.

## 8. S07 Target detail

### Purpose

Explain what the target means, why its state is what it is, and provide a direct
path to focused practice.

### Layout

1. Back
2. Target name
3. State badge
4. “What this tests”
5. Short rule/explanation
6. Evidence timeline or recent checks
7. Next-step reason
8. Practise this target

### Copy labels

- EN: What this tests
- DE: Was hier geübt wird

- EN: Why this status?
- DE: Warum dieser Status?

- EN: Recent checks
- DE: Letzte Überprüfungen

Primary:
- EN: Practise this target
- DE: Dieses Thema üben

### State-specific next step

Needs practice:
- EN: This target was missed recently. A short focused session can reinforce it.
- DE: Dieses Thema wurde kürzlich falsch beantwortet. Eine kurze gezielte Übung kann es festigen.

Improving:
- EN: Recent results are better. More unassisted checks are needed before it is considered reliable.
- DE: Die letzten Ergebnisse sind besser. Weitere selbstständige Überprüfungen sind nötig, bevor das Thema als sicher gilt.

Mastered:
- EN: This target has passed the required spaced checks.
- DE: Dieses Thema hat die erforderlichen zeitlich verteilten Überprüfungen bestanden.

Retention due:
- EN: It was reliable before. A retention check is due now.
- DE: Das Thema war zuvor sicher. Jetzt ist eine Wiederholung fällig.

### Evidence display

Use simple dated entries such as:
- Correct, unassisted
- Incorrect
- Assisted
- Skipped
- Retention check

Do not expose raw internal scores, sequence IDs or algorithm thresholds in the
learner UI.

## 9. S08 Topics

### Purpose

Let the learner voluntarily focus without turning Topics into a mandatory
course tree.

### Page heading

- EN: Topics
- DE: Themen

Supporting copy:
- EN: Choose an area when you want focused practice. Mixed practice remains the best default.
- DE: Wähle ein Gebiet, wenn du gezielt üben möchtest. Gemischte Übungen bleiben der Standard.

### Search

Placeholder:
- EN: Search topics
- DE: Themen suchen

Topic cards may show:
- Topic name
- B1/B2 relevance
- Number of reviewed targets/exercises when truthful
- Availability/offline status

Avoid lock icons that imply a linear syllabus.

### Empty search

- EN: No topics match “{query}”.
- DE: Keine Themen passen zu „{query}“.

Action:
- EN: Clear search
- DE: Suche löschen

### No reviewed exercises

Heading:
- EN: No reviewed practice yet
- DE: Noch keine geprüften Übungen

Body:
- EN: This topic exists, but reviewed practice is not available yet.
- DE: Dieses Thema ist vorhanden, aber geprüfte Übungen sind noch nicht verfügbar.

Do not substitute unpublished draft content.

## 10. S09 Topic detail

### Layout

1. Topic name
2. Level relevance
3. Short explanation
4. Two or three reviewed examples, when available
5. Targets included
6. Focused-practice action
7. Download/offline status when applicable

Primary:
- EN: Practise this topic
- DE: Dieses Thema üben

### Supported

Use a concise explanation. The page should help the learner understand what is
included, not become a long grammar chapter.

### Content unavailable

Heading:
- EN: Practice isn’t available for this topic yet
- DE: Für dieses Thema sind noch keine Übungen verfügbar

Body:
- EN: You can return to mixed practice or choose another topic.
- DE: Du kannst zur gemischten Übung zurückkehren oder ein anderes Thema wählen.

Actions:
- EN: Start mixed practice / Choose another topic
- DE: Gemischte Übung starten / Anderes Thema wählen

### Downloaded

Status:
- EN: Available offline
- DE: Offline verfügbar

Do not imply that all personalized practice is available offline merely because
the topic pack is downloaded.

## 11. Close and early-end behavior

Close from Practice opens a decision surface.

Title:
- EN: Leave this session?
- DE: Diese Übung verlassen?

Body:
- EN: Your completed answers and current draft are saved.
- DE: Deine beantworteten Aufgaben und dein aktueller Entwurf sind gespeichert.

Primary:
- EN: End session
- DE: Übung beenden

Secondary:
- EN: Keep practising
- DE: Weiter üben

Ending produces a partial summary rather than discarding the session.

If durable local save has failed, the UI must say so and must not promise that
the draft is preserved.

## 12. Responsive rules

### Wide web

- Compact left rail.
- Home/Progress content may use a two-column supporting layout, but the primary
  practice action remains visually dominant.
- Practice uses one centered column, approximately 720 px maximum.
- Feedback expands below the answer instead of creating a side panel.

### Narrow web, below 640 px

- Single content column.
- Bottom navigation for Home, Progress, Topics.
- Full-width primary buttons when useful.
- Progress groups stack.
- Target rows wrap naturally; badges must not cause horizontal scrolling.
- Practice header remains compact and sticky only if it does not obscure zoomed
  content.
- With the on-screen keyboard open, keep the active answer and primary action
  reachable without hiding the prompt context.

### Android

- Respect keyboard and system-bar insets.
- Use standard Material dialogs/sheets.
- Do not force tablet layouts to imitate phone widths; adapt by window size.
- Large font scale may move actions below the fold. Scrolling is acceptable;
  clipping is not.
- Native behavior may differ visually from web while preserving information
  hierarchy and outcomes.

## 13. Loading, empty and error pattern

### Loading

Use neutral skeletons or a simple progress indicator.

Do not display invented counts, target states or percentages.

### Empty

Every empty state explains:
1. what is empty,
2. why that can be normal,
3. what the learner can do next.

### Recoverable error

Structure:
- Plain-language heading
- One-sentence consequence
- Retry action
- Safe fallback when available

Example:
- EN heading: Progress couldn’t be refreshed
- DE heading: Fortschritt konnte nicht aktualisiert werden
- EN body: Your last confirmed progress is still shown below.
- DE body: Dein zuletzt bestätigter Fortschritt wird weiterhin unten angezeigt.
- Action: Retry / Erneut versuchen

### Authentication expiry during practice

Preserve local work.

Copy:
- EN: Sign in again to sync. Your current practice is still saved on this device.
- DE: Melde dich erneut an, um zu synchronisieren. Deine aktuelle Übung bleibt auf diesem Gerät gespeichert.

Do not force an auth modal over the active answer unless the learner explicitly
chooses to sync.

## 14. Localization conventions

Use stable locale keys and keep English and German structurally equivalent.

German product terminology:

| English | German |
| --- | --- |
| Home | Startseite |
| Practice | Übung |
| Progress | Fortschritt |
| Topics | Themen |
| Needs practice | Braucht Übung |
| Improving | Wird sicherer |
| Mastered | Sicher |
| Retention check | Wiederholung |
| Start practice | Übung starten |
| Continue session | Übung fortsetzen |
| Check | Prüfen |
| Continue | Weiter |
| Skip | Überspringen |
| Hint | Hinweis |
| Show answer | Antwort zeigen |
| Saved on this device | Auf diesem Gerät gespeichert |
| Confirmed progress | Bestätigter Fortschritt |

Use “du” consistently in German learner copy.

Exercise text remains German. Instructional UI follows the selected interface
language.

## 15. Accessibility acceptance

For web:
- Complete Home → Practice → Summary → Progress → Target detail by keyboard.
- Maintain visible focus at 200% zoom.
- Reflow at 320 CSS px without horizontal scrolling for ordinary text/content.
- Do not rely on color alone for state.
- Preserve focus when Check changes question state.
- Move focus deliberately to the next prompt only after Continue.
- Announce result and save/sync status without re-reading the full screen.

For Android:
- Complete the same journey with TalkBack.
- Verify large font scaling and system display scaling.
- Provide labelled movement alternatives for word-order interactions.
- Ensure bottom navigation labels remain understandable when text expands.
- Keep minimum 48 dp interaction targets.

For both:
- German exercise content is tagged/pronounced as German.
- Instructional controls use the selected UI locale.
- Outcome icons have accompanying text.
- Loading does not steal focus repeatedly.

## 16. Relationship to guest practice

[guest-practice-ux.md](guest-practice-ux.md) remains authoritative for:
- S01 first-use setup
- guest-local/provisional labels
- post-practice account prompts
- explicit guest-to-account attachment confirmation
- partial validation and attachment failure states
- shared-device consent wording

This document owns the broader signed-in learner screen structure and the common
Practice/Progress/Topics behaviors.

Where both documents apply, the guest document changes authority wording only.
For example, authenticated confirmed feedback may say “Correct”; guest feedback
uses the specified local/provisional wording.

## 17. Implementation handoff checklist

Codex integration can treat this deliverable as complete when:

- Home has one dominant Start/Continue action.
- Web and Android use exactly Home, Progress and Topics as learner navigation.
- Practice removes normal navigation and preserves partial work on Close.
- Typed, choice, word-order and controlled-completion questions follow the same
  Check → Feedback → Continue rhythm.
- Blank/invalid/saving/durable-save-failure states are explicit.
- Feedback distinguishes correct, incorrect, assisted, skipped, disputed and
  provisional outcomes.
- Session summary distinguishes complete, partial, pending and confirmed.
- Progress uses Needs practice, Improving and Mastered without a single mastery
  percentage.
- Retention due remains distinct from “Needs practice”.
- Target detail explains the target, status reason, evidence and next action.
- Topics provide optional focus rather than a locked curriculum.
- Narrow web reflows cleanly and Android adapts to keyboard/font/window changes.
- English/German copy is localized from stable keys rather than inline mixed
  language.
- Last confirmed data remains visible through recoverable refresh failures.
- Accessibility checks cover keyboard, focus, zoom/reflow, TalkBack and large
  fonts.

## 18. Deliberate non-features

Do not add to this learner-screen implementation:

- streaks, XP, trophies or leaderboards
- countdown timers
- generic “overall mastery” percentages
- a standalone analytics dashboard
- mandatory topic progression
- separate Vocabulary/Writing learner modes
- auto-advance after checking
- answer correctness based on response speed
- blocking sync dialogs for retry-safe local work
- unpublished draft exercises as empty-state filler
- AI-chat controls in the core practice flow

The intended product remains a calm practice companion: one clear next action,
transparent evidence, concise correction, and reliable progress over time.

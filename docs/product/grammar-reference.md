# Quick grammar reference

Owner request, 24 October 2026: restore the old app's quick grammar menu in GM2,
keeping web and native Android aligned. This is the explicitly authorized
exception to the current feature freeze, not a new learning mode.

## Entry and behaviour

- Grammar / Grammatik is a secondary, visibly labelled control in the app shell
  and focused practice. Home / Progress / Topics remain the primary destinations.
- Opening it shows the topic menu without sign-in, network access or session
  allocation. Search matches both language titles and the complete German content.
- Choose a topic to read its tables, notes, rule, examples, common mistakes and
  additional sections. Rule starts expanded; optional detail starts collapsed.
  The three adjective-declension tables remain grouped together.
- All topics returns to the menu and retains the query. Close, Escape on web and
  Android system Back dismiss the overlay. The underlying question, answer,
  feedback, draft and ownership remain unchanged. Lookup never submits, advances
  or records mastery.
- Interface controls are English/German. The original German reference text is
  preserved, with a short language note in the menu. This is not a claim that the
  entire reference has been translated into English.
- Wide declension tables scroll within their own region; long reference tables
  use labelled facts. Narrow screens keep a reachable close control. The Android
  implementation is native Compose; web uses an accessible modal dialog.

## One content input

Both clients consume `content/reference/grammar-reference.json`: 14 topics,
16 tables and 151 existing examples. Web bundles the JSON in its application
artifact; Android copies that exact file into its assets. The preserved original
repository's source matches the imported monorepo copy. See the content README
for the repository-relative provenance and version.

Structure validation is part of root `check:generated`; a native JVM regression
compares every table and field to the existing source. No exercise identities,
rubrics, backend content releases or account data are changed.

## Evidence and boundaries

Mounted web and native tests cover guest lookup, search/no results, topic return,
German controls and closing over an unchanged practice draft. Account-backed
tests verify the same reference entry and draft protection. Browser checks cover
390px layout, confined table scrolling, Escape and focus return. Focused native
renders cover the menu, adjective tables and German dark mode at 320dp.

These do not replace physical-device acceptance or the accepted TalkBack scope.
The Android publication hold and separate production deployment boundary remain.

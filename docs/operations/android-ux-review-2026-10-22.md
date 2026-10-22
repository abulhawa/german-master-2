# Android component-purpose review - 22 October 2026

Status: first UX correction verified locally and on Pixel; release hold remains active.

The owner resumed UX work and authorized real Pixel screenshots. Review each
component by purpose, timing, action result, and preservation of work. This is
an AI-assisted source/interaction review, not independent human expert approval.

| Screen/component | Purpose and expected behaviour | Decision |
| --- | --- | --- |
| Guest entry: Start | Begin without authentication | Keep as the filled primary action |
| Guest entry: language | Change interface language | Keep on entry as a secondary text action |
| Guest entry: local-storage notice | Explain where guest work remains | Keep one short account/storage explanation outside questions |
| Guest entry: account links | Optional registration, sign-in and recovery | Keep outside active questions; no automatic transfer promise |
| Guest question: introduction/account promotion | Repeats entry material and competes with answering | Remove during answering and feedback |
| Guest question: Pause/system Back | Return to entry while retaining question, draft and feedback | Add explicit Pause/Continue; no reset or submission |
| Guest question: Check/Continue | Submit explicitly, then advance explicitly | Filled primary actions; retain disabled incomplete Check |
| Guest question: hint/Skip | Optional assistance or move past a question | Keep secondary; persist assistance and draft changes |
| Guest completion: result and retry | Show local results and offer a fresh starter | Keep local-result qualification; restore optional account actions |
| Account form: fields/recovery instructions | Collect required input and explain email/browser handoff | Retain required instructions; shorten repeated account promotion |
| Signed-in Home: start/topics/status | Start or resume mixed practice, optionally choose a focus | Retain existing primary start and outlined topic choices; no redesign in this slice |
| Signed-in question: Your selection | Repeats the visible selection control state | Remove extra heading |
| Signed-in question: Hint | Optional assistance, not a competing primary action | Use secondary text action; disable after revealing |
| Signed-in practice: system Back | Match visible Close and preserve pending work | Route through the same close decision; block while busy |
| Close dialog: pause versus end | Pause retains resumable work; End records completion and shows summary | Rename misleading Save action to Pause and return Home |
| Correct feedback: Your answer | Repeats a correct accepted answer | Omit duplicate line; retain accepted answer and explanation |
| Incorrect feedback: Your answer | Compare mistake with correction | Retain |
| Offline feedback: sync paragraph | Repeats offline banner and unsynced outcome label | Remove duplicate paragraph; retain provisional label and screen sync status |
| Topics/Progress | Discover focus and inspect evidence | Preserve current behaviour; no new acceptance claimed |
| Account/downloads/privacy | Explicit settings, sync and irreversible actions | Preserve existing controls and confirmations; no real requests in review |

The feedback simplification intentionally omits the blueprint's learner-answer
line for correct answers only. Incorrect-answer comparison, accepted answers,
assisted status and provisional status remain. No grading policy changed.

Guest history attachment remains unimplemented. Account copy states that earlier
guest results remain on this device. No screenshot batch is restarted implicitly.
A bounded real-device capture is authorized for this review; private artifacts
must stay outside the public checkout. Engineering checks and captures do not
lift the owner's release hold or reopen the accepted TalkBack scope.

## Verification and remaining review

- Final offline run: 11 tests across NativeEntryTest (6), NativePracticeUiTest (3)
  and NativeTopicsUiTest (2), zero failures/errors/skips. lintDebug passes with
  zero errors and 18 warnings; assembleDebug and assembleLearnerPreview pass.
  Full Android/web/backend suites were not repeated. Earlier overlapping Gradle
  runs hit a Windows runtime-jar lock; the older test worker was stopped and the
  final runs passed serially with two workers.
- Replaced only the preview package on the connected Pixel without uninstalling
  or clearing data. Original application preserved. Saved question-three draft
  survived replacement, and explicit Check produced local feedback without
  advancing. Pause and system Back returned to guest entry; resume retained
  work. Registration, sign-in and reset forms opened without submitting any
  real account/email request. English and German entry captures were retained.
- Private physical captures include before/after feedback, question/draft,
  pause/entry and account forms. The final compact header was installed and
  captured after the last refinement. Account-form captures predate only that
  guest-header refinement; their implementation did not change afterward.
- Real-device signed-in Home/Topics/Progress/download/privacy journeys were not
  reproduced in this slice. Signed-in feedback and Back have Compose fixture
  evidence, not a new authenticated physical-device acceptance claim.
- Device review also exposed existing raw slot IDs (such as preposition) and a
  German slot label in English guest feedback. Follow up on localized slot
  presentation without editing immutable exercise revisions or grading data.
- Next: finish the component-purpose audit with signed-in device journeys and
  account Back-navigation checks, address the slot-label inconsistency, then
  present the resulting UX for explicit owner acceptance. No release approval
  is inferred. M2 remains active; TalkBack acceptance remains unchanged.

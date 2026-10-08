# Converted B1 candidate: local client acceptance

8 October 2026. Candidate manifest: `2417bd644424ce5ac84aadd15b473ff723e0c801d7cf69d4e783fd51f30456f7`.

The three converted families can now be practised through the real web/native clients against a disposable authoritative API. This closes local engineering preview of the 20 converted targets/40 revision-2 exercises. Independent German review and physical-device accessibility acceptance remain pending; no content was published.

## Reproduce the candidate preview

From the repository root, with installed dependencies:

```powershell
npm run content:preview-basics
```

The host binds `127.0.0.1:5001` and creates a fresh in-memory PGlite database. It checks the committed candidate SQL/catalog against the generator before starting. Because the service intentionally refuses unpublished releases, the host simulates activation inside that disposable database using the existing content builder. Authored exercise payloads, rubric answers and revision identities stay intact. Synthetic local activation is not independent editorial approval. It accepts only the public `foundation-local-demo` fixture token; it loads no credentials, database URLs or environment files and writes no generated content files. Restart discards the local database.

For web, start a second terminal:

```powershell
npm run dev:client --workspace=apps/web -- --port 5110 --strictPort
```

Open `http://127.0.0.1:5110/renovation`, save B1 preferences, and choose a converted target through Topics. Plurals use four-option MCQs; adjective endings use one-gap choices; irregular present-tense verbs use two-gap choices. Check stays explicit. Review the accepted answer and explanation, Continue, then Confirm session completion. The local setup copy now describes the loaded draft catalog without the obsolete five-question/B1-only assumption.

The existing debug Android `LearnerPreviewActivity` uses the same loopback fixture API. On an attached test device, route its port with `adb reverse tcp:5001 tcp:5001`, then open the isolated learner preview. No device was attached during this slice; device UI/TalkBack/process-death acceptance is not claimed.

## Automated client evidence

- `apps/web/client/src/renovation/basic-content.integration.test.tsx`: all 40 authored variants are selected through the reusable renderer without text inputs. Partial two-gap drafts survive remount and cannot submit. For a representative target in each family, the actual owned learner component uses real loopback HTTP, restores a saved draft, loses an accepted response, remounts and explicitly retries the identical frozen request. Each yields one evidence event, correct confirmed feedback and a full one-question completion receipt.
- `BasicCandidatePreviewTest`: all 40 variants are selected through Compose controls with simulated font scale 2.0 in a 320dp column, with visible controls at least 48dp high and native provisional grading. Real native repository/HTTP tests practise all 20 converted targets, preserve partial/complete drafts in AtomicFile through repository restart, replay one lost accepted response and obtain completion receipts. Exactly 20 evidence events are recorded. These are JVM/Robolectric checks, not physical-device measurements.
- Both paths start from the same candidate-checking preview helper. JVM rubric inspection uses stdin commands; HTTP session responses remain solution-free. Preview scripts are outside the production host/client imports.

## Browser evidence

The integrated browser completed plural `der Antrag`, masculine-nominative adjective endings and present-tense `fahren` through target-focused practice, server grading and explicit completion. Plural selection worked with the keyboard; checking moved focus to feedback and Continue moved it to the summary.

At a 320px viewport, `fahren` restored a selected first gap after page reload and Continue practice. Check remained disabled until the second gap was selected. Confirmed feedback and summary remained readable; measured DOM scroll width was 305px within the 320px viewport. No captured browser console errors. Screenshots: `.local/basic-preview/verb-320.png` and `.local/basic-preview/confirmed-summary-320.png`. The temporary viewport was reset afterward. This does not establish complete web assistive-technology or 200% text-zoom acceptance.

## Required checks

Final checks use repository Node 22.23.3 and local offline fixtures. Root offline install, generated/type checks, full root tests and web/API build pass: backend 29 files/191 tests, web 93 files/398 tests, and real HTTP five files/17 tests. Full Android debug unit tests pass 43 suites/164 tests with zero failures/errors/skips; lint and debug assembly pass, with zero lint errors and 17 existing warnings. No production/AI service calls or device installation were needed.

## Next content slice

B1 preposition/article targets already use authored choices and the B1 ordering topic already uses word tokens; do not reconvert them. Convert the five B2 verb/preposition targets (10 typed cloze variants) to authored gap choices with plausible distractors and bilingual feedback. Preserve immutable revisions, regenerate the unpublished candidate/workbooks, and run the same client checks. Keep independent German sign-off separate from engineering acceptance.

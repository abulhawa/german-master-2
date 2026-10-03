# German Master 2.0

## Product Design Architecture and Delivery Blueprint

**Version 1.0 · 3 October 2026 · Recommended baseline for implementation**

German Master helps intermediate learners find recurring weaknesses in German, practise them in short mixed sessions, and retain what they learn. This document defines the product and the system needed to deliver that promise on web and Android. It replaces the earlier verb-centred direction for new development; it does not change either existing repository or any database.

**Architecture recommendation:** consolidate development into one monorepo, retain React web and native Kotlin Android clients, and give a single backend ownership of content publication, grading, mastery, scheduling, and synchronization. Share contracts, fixtures, tokens, and content assets across languages. Share TypeScript implementation where it fits; generate Kotlin contracts rather than attempting to import TypeScript into Android.

The inspection is of source code at the revisions recorded below. Deployment settings, database policies, production content quality, store status, and actual runtime behavior require verification during M0. Product targets, algorithm thresholds, designs, and schedules below are proposals, not measured results or existing capabilities.

## 1 Executive Summary

The product is for B1–B2 learners, particularly adults living or working in Germany, who already study German but repeatedly make the same errors. The central experience is a useful ten-minute session spanning vocabulary, verbs, articles, plurals, cases, prepositions, adjective endings, and sentence structure. Each exercise supplies evidence about a precise learning target. The application remembers that evidence and schedules further practice.

Build four learner areas: Home, Practice, Progress, and Topics. Account and settings are secondary. Keep import and long-form writing out of the initial release. Move useful existing grammar and vocabulary material into one practice system instead of preserving separate mini-apps.

Use one repository because content, schema, engine, and client changes need coordinated review. Native Android remains valuable: its Compose, Room, WorkManager, authentication, and test foundations should be adapted selectively. The web React/Vite stack should also remain. A rewrite into a new cross-platform UI framework would add a second transformation without resolving the main problem.

The backend becomes the only authority for confirmed learner state. Both clients use the same versioned API. Android stops using raw database tables as its application contract. Offline practice runs from downloaded sessions, writes a durable local outbox, and shows provisional results until the backend confirms them. Clients never merge mastery scores.

The reset uses a new database environment, a new content baseline, and new client storage namespaces. There is no learner-history migration. Archive the old code and content before cutover; retain the Android application identity and signing continuity unless a separate store listing is intentionally chosen.

Deliver in gated phases: preserve and audit; define contracts and content; prove the learning engine; ship a complete web vertical slice; bring Android to behavioral parity; prove offline and cross-device consistency; run a small learning pilot; release. Judge success by retained improvement on previously weak targets, supported by session usefulness and return behavior.

## 2 Inspection Evidence and Existing System

### Repositories inspected

| Repository | Local path | Inspected revision | Inspection state |
|---|---|---|---|
| Web and backend | `C:/Projects/german-master` | `f1ccc88113d6f636b080117b11af0e24c5fb9a87` | Existing checkout; clean at inspection |
| Native Android | `C:/Projects/GermanVerbMaster-Android` | `3d09b26b1becf5cba6d3b06788e4b72c1623a265` | Cloned for this analysis; source preserved |

Remote repositories: [web](https://github.com/abulhawa/german-master) and [Android](https://github.com/abulhawa/GermanVerbMaster-Android).

| Area | Source evidence | Implication for 2.0 |
|---|---|---|
| Web stack | `package.json`: React 19, Vite, TypeScript, Wouter, TanStack Query, Radix, Tailwind, Dexie | Keep the stack; restructure by product capability |
| Backend | `server/routes/tasks/handlers.ts`, `db/schema.ts`: Express, Drizzle, Postgres; task and submission APIs | A useful foundation for one authoritative API |
| Hosting | `vercel.json`: static output and API rewrite; main deployment enabled | Hosting configuration exists; deployment health unverified |
| Content | `data/pos`, enrichment snapshots; `shared/task-registry.ts`; lexeme and inflection entities | Reuse reviewed lexical material and provenance; generalize beyond lexemes |
| Web learner state | `client/src/lib/practice-progress.ts`; `pages/home/use-practice-session.ts` | Local counters and client queue/shuffle logic exist; replace confirmed state ownership |
| Web navigation | `client/src/App.tsx`: home, writing, vocabulary, progress, history, analytics, admin, UI testbed | Consolidate learner navigation; isolate content operations |
| Android stack | `app/build.gradle.kts`: Compose Material 3, Room, Hilt, Ktor, WorkManager, Supabase Auth/PostgREST | Retain native UI, local storage, background sync, and authentication foundations |
| Android data boundary | `data/remote/SupabaseTaskApi.kt`, `SupabaseHistoryApi.kt` | Reads `task_specs`, upserts `practice_history` directly; replace with product API |
| Android practice | `GetNextTaskUseCase.kt`, `TaskRepository.kt`, `SubmitAnswerUseCase.kt` | Local batch selection and client result storage; route confirmed evaluation through backend |
| Android navigation | `navigation/Screen.kt`: Home, B2Practice, Wortschatz, Analytics, History, Auth, WordDetail | Fold B2 grammar and word drills into the unified engine |
| Android local schema | `data/local/db/AppDatabase.kt`: Room version 17; five content/history entities | Local schema is a cache/outbox concern, independent from server relational schema |
| Android release | application ID `com.germanverbmaster.android`; version code 29; Play publisher configured for alpha | Preserve identity/signing; confirm actual store track and published version during M0 |
| Quality foundations | Web Vitest and pg-mem; Android unit, DAO, Robolectric and screenshot tests | Reuse valuable fixtures and testing approach; add cross-client conformance |
| Quality gaps | Neither inspected tree contains `.github/workflows`; Android release lint is non-blocking | Establish CI; make relevant lint and contract checks release gates |

The shared database currently provides structural overlap, not a shared learning engine. Web submission accepts a result field and includes server leniency handling; Android submits client-computed results through PostgREST. This is a boundary to redesign, not evidence that either client is insecure in production. Live grants, RLS, authorization, and deployed behavior were not inspected.

The web README contains assumptions that differ from source configuration: it describes different offline delivery approaches and build/migration steps, while `vercel.json` only installs and builds. Treat executable configuration and verified behavior as evidence; do not inherit documentation claims as guarantees.

### Reuse disposition

| Asset or feature | Disposition | Reason and condition |
|---|---|---|
| React and Compose foundations | Keep selectively | Avoid framework churn; rebuild navigation and practice composition |
| Lexemes, inflections, approved examples | Keep after audit | Valuable content; validate correctness, rights, provenance and stable IDs |
| Registry and normalizers | Adapt | Useful concepts; require typed exercise variants and language conformance fixtures |
| Dexie, Room and WorkManager patterns | Adapt | Durable caching/outbox remains essential; replace payload and conflict rules |
| Authentication integration | Adapt | One identity provider; verify web and Android same-user behavior |
| Separate writing/B2/vocabulary modes | Simplify | Integrate bounded exercises into Practice and topic filtering |
| History and analytics dashboards | Simplify | Progress drilldown and internal analysis; remove from primary navigation |
| Content admin/enrichment | Keep as internal tooling | Add editorial publishing controls and separate access |
| Translation downloads, countdowns, streak emphasis | Defer or remove from MVP | Do not advance the core weakness loop sufficiently |
| Legacy tables and compatibility shims | Archive | Fresh baseline avoids permanent transitional complexity |

## 3 Vision Positioning and Scope

**Vision:** make recurring German mistakes visible and help learners replace them with reliable habits.

**Promise:** “Practise your weak points until they become reliable.” Use supportive language in the interface: “Needs practice” rather than describing the learner as weak.

**Positioning:** a focused practice companion alongside classes, work, reading, and conversation. The distinguishing hypothesis is precise weakness tracking and retained improvement across varied exercises. Competitive differentiation is a hypothesis to validate, not a claim established by market research.

MVP covers B1–B2 vocabulary meaning, verb forms, noun gender/plurals, cases after prepositions, adjective endings, word order, and short controlled sentence completion. These subjects share one session engine. Launch a small reviewed set across all subjects rather than a large unchecked catalog.

Non-goals: full beginner courses; lesson trees; social feeds; rankings; streak pressure; unrestricted AI chat; certified exam scoring; unrestricted essay grading; speech assessment; automatic mastery based on time spent; iOS support in this reset. Do not add paid AI inference to the daily core loop. Monetization and pricing remain a later decision after usefulness is demonstrated.

## 4 Target Learner and Jobs to Be Done

Primary learner: an adult around B1–B2, using German in Germany, with fragmented knowledge and limited study time. Level selection is self-reported and guides content; it is not a proficiency certification. MVP instructional UI is English or German; exercise text remains German, with English glosses where relevant. Keep all UI copy localized and do not infer native language from location.

| Situation | Job | Desired outcome | Design consequence |
|---|---|---|---|
| Repeating mistakes despite attending classes | Identify which patterns need attention | Specific targets, not a generic percentage | Link mistakes to concepts and individual items |
| Ten minutes between activities | Practise without configuring a lesson | Useful mixed session within a few taps | Default session ready on Home |
| Unsure why an answer was wrong | Understand the correction quickly | One rule and one example | Feedback explains the tested distinction |
| Switching between phone and laptop | Continue with consistent progress | Same confirmed weaknesses and history | One identity and backend state |
| Commuting without connectivity | Finish prepared practice | No lost answers | Downloadable session and durable outbox |
| Returning after a break | Restart without feeling punished | Manageable priorities | Cap session length; no overdue debt messaging |

Validation before public release: observe 5–8 intended learners completing onboarding, one mixed session, and a Progress drilldown; then run a two-week practice pilot with delayed follow-up in weeks three and four. The mastery gate and subsequent seven-day retention check cannot both mature during the first two weeks. Recruit learners with diverse access needs. Record confusion, perceived relevance, correction disputes, and later unassisted recall; do not treat the small pilot as statistical proof.

## 5 Product Principles and Decision Rules

1. One obvious next action. Home prioritizes Start Practice or Continue Practice.
2. Evidence before claims. A correct answer is evidence; durable mastery needs spaced unassisted checks.
3. Explain the next step. Show short reasons such as “You missed this recently” or “Time for a retention check.”
4. Calm correction. Feedback teaches, avoids shame, and gives explicit control over moving on.
5. One product across devices. Outcomes and learning rules match; native interaction conventions can differ.
6. Offline honesty. Provisional answers and confirmed progress remain distinguishable.
7. Content quality first. Reviewed explanations and accepted variants outweigh catalog size.
8. Protect the session. Avoid banners, promotions, dashboards, and background updates that interrupt practice.

A feature enters the backlog only if it improves selection, answer quality, feedback, retention, content quality, accessibility, or reliability. Every proposal names its target learner problem and success measure.

## 6 Information Architecture

```text
German Master
├── Home
│   ├── Start or continue practice
│   └── Today and recently improving
├── Practice (focused session, outside the main navigation shell)
│   ├── Question → Check → Feedback → Continue
│   └── Session summary
├── Progress
│   ├── Needs practice / Improving / Mastered
│   └── Target detail → explanation → practise target
├── Topics
│   └── Topic detail → focused practice / explanation
└── Account and settings (secondary entry)
    ├── Sign in / profile / language / theme
    ├── Downloads and sync status
    └── Privacy / export / delete account

Internal content workspace (web only, separate authorization)
└── Draft → review → preview → publish → retire
```

Mobile and narrow web: three main navigation destinations, Home, Progress, Topics. Starting practice opens a focused screen. Desktop: compact navigation rail with the same destinations. Practice shows a small header with Close and question count; do not retain a large sidebar or analytics panel. Topics provide optional focus, not a mandatory syllabus.

Home count means distinct targets currently eligible for a weakness or due-retention question, counted once. Separate “Needs practice” from “Retention checks” when combining them would mislead. With no evidence show “Let’s find what to practise”; with no due targets offer “Try something new” and voluntary topic practice.

## 7 Main Journeys and Acceptance Behavior

**First visit:** choose interface language, self-reported level, and five- or ten-minute preference. Offer a short mixed starter session immediately. Present login as the way to save and use progress across devices. The starter can run from a bundled reviewed pack as a local guest; it creates no server-confirmed mastery until an account is established and attempts are attached explicitly.

**Daily practice:** Home → Start → prepared mixed session → answer → feedback → Continue → summary. Default ten minutes means an estimated 15 questions, adjustable to 10 or 20; time is never a countdown or grading signal. Stop after the planned question count. If content is insufficient, offer a shorter session transparently.

**Mistake to improvement:** wrong answer → highlight the tested difference → explain one rule → offer optional detail → include a different exercise for the same target after at least three other questions → schedule later unassisted recall. The immediate repeat is reinforcement, not mastery evidence.

**Focused practice:** Topics or Progress target → Practise this → short target-focused session using the same grading and scheduling rules. Voluntary extra practice cannot inflate distinct-target weekly mastery.

**Interrupt and resume:** persist current question, draft answer, answered questions, and outbox before leaving. Resume locally after process/browser restart. After a completed answer, back navigation never submits again. A second device starts its own session; MVP does not promise live cross-device transfer of a partially completed session.

**Offline:** download when online → practise with local feedback → show “Saved on this device” → reconnect → sync → show confirmed results. First launch without bundled/cached content offers the starter pack only; personalized sessions require prior preparation.

**Cross-device:** authenticate to the same account → refresh authoritative Progress → generate a session from confirmed state. Two simultaneous sessions can contain overlapping targets; their events are deduplicated and reconciled by the backend.

**Guest to account:** show the number of local attempts to save; ask whether to attach them to the signed-in account. Server validates the pack/exercise revisions and assigns ownership. Do not silently attach another person's device history. Repeated attachment is idempotent.

## 8 Screen Inventory and State Requirements

| ID | Screen | Main content/action | Required states |
|---|---|---|---|
| S01 | Welcome and setup | Level, language, session preference; start | New, saved preference, offline starter |
| S02 | Home | Ready targets, Start/Continue, modest progress | New, due, no due, pending sync, API unavailable |
| S03 | Practice question | Prompt, input, optional hint, Check | Empty, draft, invalid, submitting, queued |
| S04 | Practice feedback | Outcome, accepted answer, explanation, Continue | Correct, incorrect, assisted, skipped, disputed, provisional |
| S05 | Session summary | Completed questions, targets worked on, next step | Complete, partial, pending sync, confirmed |
| S06 | Progress | Three state groups, recent retained gains | No evidence, populated, pending overlay, stale snapshot |
| S07 | Target detail | What is being tested, evidence, rule, practise | Learning, needs practice, improving, mastered, due |
| S08 | Topics | Clear topics and level relevance | Empty search, available, no reviewed exercises |
| S09 | Topic detail | Explanation, examples, focused session | Supported, content unavailable, downloaded |
| S10 | Account and auth | Sign in, save guest practice, settings | Signed out, signed in, canceled, expired, error |
| S11 | Downloads and sync | Prepared sessions, pending attempts, retry | Downloading, ready, insufficient space, auth required, failed |
| S12 | Privacy and account actions | Export, sign out, account deletion | Confirmation, queued request, completed, error |
| S13 | Content workspace | Edit, preview both clients, review, publish | Draft, validation error, review, published, retired |

S01–S12 are shared product behavior implemented natively per client. S13 is internal web tooling. No standalone learner analytics, word history, or writing screen is required in MVP. History belongs inside target detail and optional Progress drilldown.

## 9 Practice Interaction Specification

Use a small state machine: **loading → answering → saving → feedback → next → summary**. Drafts persist during answering. Check is disabled for blank input; validate malformed input inline. A submitted answer locks for that question, preventing double taps. If local durable save fails, explain that the answer is not saved and keep the draft; do not show a false success.

Typed answers: Enter checks; once feedback is visible, an explicit Continue button moves on. Keyboard shortcuts ignore active composition and do not cause the same Enter press to skip feedback. Keep the answer in view when the mobile keyboard opens. Provide optional ä, ö, ü, ß insertion controls without replacing the keyboard.

Choice questions: select then Check; selection alone does not submit. Word order uses move-left/move-right controls and keyboard actions as well as optional drag. Controlled sentence completion has labelled blanks and a clear reading order. Do not use broad free translation until accepted-answer policies are reviewed.

Feedback format: outcome label → learner answer versus accepted answer → brief explanation → Continue. Correct examples: “Correct. ‘Mit’ takes the dative.” Incorrect examples: “Use ‘dem neuen Kollegen’. After ‘mit’, use the dative.” Always retain meaning and context. Do not mark noun capitalization optional by default; tolerance is exercise-specific. Never normalize away umlaut distinctions globally. Normalize Unicode, surrounding spaces, and permitted punctuation through a versioned policy.

Hints and Show answer are available without penalty language. Mark these attempts assisted; they do not count as independent mastery evidence. Skip moves on and records exposure without a correct/incorrect grade. “Report a problem” attaches the exercise revision and optional comment to internal review, without silently changing the result. A disputed exercise can be excluded from scheduling until editorial review.

Do not auto-advance after a correct answer. Do not use color alone. Do not count response time as correctness: it varies with accessibility, interruptions, and keyboard usage. On Close, offer End session or Keep practising; locally retain the partial session. Completion records are retry-safe.

Loading: show neutral skeletons without invented counts. Failed session creation: Retry plus cached session option. Rate limiting: respect retry information while retaining the draft. Expired auth: keep local answers and offer sign-in. Pending sync must not produce repeated blocking dialogs.

## 10 Visual Design System

**Direction:** a calm study workspace with clear typography, generous answer space, and restrained blue accents. Use German Master as a wordmark; an optional small open-loop motif can suggest improvement. Avoid national flags, trophy imagery, and red-heavy mistake screens. Final logo artwork is a later design task.

### Token baseline

| Semantic token | Light | Dark | Use |
|---|---|---|---|
| background | `#F6F7FB` | `#101826` | App canvas |
| surface | `#FFFFFF` | `#192437` | Cards, sheets, answer area |
| text primary | `#172033` | `#F2F5FA` | Prompt and body |
| text secondary | `#526078` | `#B2BED0` | Supporting text |
| primary | `#2457C5` | `#8FB2FF` | Main actions; dark primary uses dark foreground |
| on primary | `#FFFFFF` | `#101826` | Button text |
| success text | `#176447` | `#82D7B2` | Correct state label with icon |
| attention text | `#854D0E` | `#F3CC84` | Needs practice and pending state |
| error text | `#A62B36` | `#FFA4AC` | Incorrect state and validation |
| decorative border | `#D7DEEA` | `#3C4A61` | Nonessential separators only |

These are proposed starting tokens. Validate contrast in actual state combinations before acceptance. Essential control boundaries and focus outlines must meet their contrast requirements; do not assume decorative borders suffice. All consumers use semantic tokens, not inline color literals. Focus uses a 2px contrasting outline with an offset and survives dark mode/high contrast.

Type: platform system sans for MVP, with complete German glyph support. Web body 16px, line height 1.5; mobile body 16sp with scalable text. Prompt 24–28px/sp; page title 28–32; section title 20; labels 14. Allow prompts to wrap naturally. Numbers are supporting information, not the visual center.

Spacing: 4, 8, 12, 16, 24, 32, 48. Cards use 16–24 internal padding, 12–16 radius, and a subtle border rather than heavy shadows. Controls are at least 48dp on Android and a 48px design baseline on web. Use a readable practice column around 720px maximum on wide screens; use full available width inside that column and within narrow layouts. This intentionally supersedes the old guideline against constraining any practice card width once 2.0 is adopted.

Layout: narrow web below 640px, medium 640–1023px, wide 1024px and above; these are layout proposals, not device detection. Android adapts to window size rather than assuming every device is a phone. Honor safe areas, keyboard insets, and system bars. Progress cards stack at narrow widths. Feedback expands vertically instead of obscuring input.

Components: AppShell, PageHeader, Button, TextField, ChoiceGroup, OrderEditor, PracticeCard, FeedbackPanel, HintDisclosure, TargetStateBadge, ProgressGroup, EmptyState, SyncStatus, Dialog, Sheet, Toast. Document default, focus, selected, loading, disabled, error, offline and dark states. Radix wrappers remain web-specific; Compose components map the same tokens to Material 3 roles. Shared behavior does not require pixel-identical native controls.

Motion: brief 120–200ms transitions for selection and panel appearance; reduced-motion mode removes movement. Avoid celebratory animations and full-screen transitions between questions. Icons need text for outcomes and accessible names for actions. Explanations use a disclosure or sheet accessible by touch and keyboard, never tooltip-only content.

## 11 Accessibility and Localization

Adopt WCAG 2.2 AA as the web acceptance target and equivalent native testing with TalkBack, large fonts, contrast, and focus behavior. The [W3C standard](https://www.w3.org/TR/WCAG22/) defines the web requirements; native acceptance also needs platform-specific manual checks.

Required checks: complete keyboard journey; visible and unobscured focus; text contrast at least 4.5:1 for normal text and 3:1 for large text; necessary non-text visual information at least 3:1; 200% text zoom and reflow at a 320 CSS-pixel viewport; no color-only result communication; accessible alternatives to dragging. The 48px/dp product target exceeds the web minimum target size, but spacing and exceptions still require review.

Announce outcome and save state without re-reading the whole screen. Preserve focus after checking and move it deliberately to the next prompt on Continue. Expose question position and input labels. Screen readers should hear word-order token positions and available movement actions. Test German pronunciation language tags for exercise text and the UI locale for instructions. Audio is optional, with text alternatives; no mandatory listening in MVP.

English and German UI strings live in locale resources with stable keys. Format dates in the selected locale and calculate “today” using the learner's saved IANA timezone. Dark and system themes are supported. No mixed instructional language except German exercise content. Longer German copy must not clip buttons or badges.

## 12 Learning Model and Mastery Rules

### Separate the things being learned from the questions

A **skill** is a concept such as dative after “mit” or subordinate-clause verb position. A **learning target** is an assessable unit: noun gender for “Beruf”, the past form of “gehen”, or application of “mit + dative” across contexts. A lexeme may support several targets; grammar targets need no lexeme. An **exercise revision** is one immutable way to assess one primary target. Secondary skill tags support analysis but must not automatically create independent mastery gains.

An **attempt** records an answer to a session question and its conditions. A **mastery snapshot** is a derived view of accepted evidence for a learner and target. A **review schedule** says when another informative assessment is due. Topic/category progress aggregates target evidence; it is not a separate score directly incremented by each answer.

### Learner states

| State | Meaning | Suggested transition |
|---|---|---|
| New | No graded independent evidence | First valid unassisted attempt → Learning or Needs practice |
| Learning | Some evidence, too little for confidence | Spaced successes → Improving; repeated misses → Needs practice |
| Needs practice | Recent evidence of difficulty or a retention lapse | Two later unassisted successes on distinct occasions → Improving |
| Improving | Multiple successes, retention still being verified | Meets retained-evidence gate → Mastered |
| Mastered | Meets the proposed retention gate | Unassisted retention failure → Needs practice |

“Due” is a scheduling flag, not a learner state. A mastered target can be due for a retention check while remaining mastered. A mere passage of time cannot turn “mastered” into “wrong”. Recently missed and not yet reassessed targets appear first; neutral New/Learning states live in target details rather than crowding the three Progress groups.

### Initial deterministic policy

Use an explicit versioned rule engine first. Do not claim a probability-of-recall model without calibration. The server keeps qualifying success dates, recent independent failures, last practice, state, and due date. Correct unassisted spaced reviews advance a provisional interval ladder of 1, 3, 7, 14, and 30 days. A wrong answer schedules a next-day review and may add an in-session reinforcement; an assisted answer schedules another independent check without advancing the ladder. Skips add exposure only.

For MVP, Mastered requires at least four unassisted correct checks on distinct local dates, spanning at least 14 days, including two exercise variants or contexts, and no later independent failure. Repeated same-day answers cannot satisfy the gate. For concept targets, the variants must test transfer, not merely reorder the same words. This is a proposed operational rule to validate; it does not prove fluency or general proficiency.

Store the policy version with the snapshot and attempt evaluation. On policy changes, replay evidence or rebuild snapshots under a named version; never silently reinterpret an old outcome. Preserve evidence independently of projections. Wrong answers must not erase the fact that a target was previously mastered; retain transitions for honest analysis.

### Session selection

For a default 15-question session, allocate approximately 8 to recent weaknesses/overdue reviews, 5 to other due retention checks, and 2 to new targets. Reallocate missing pools rather than inventing questions. Start with a manageable question; avoid five difficult items in a row. Prioritize overdue targets within a bounded urgency score so an old backlog does not dominate every session.

The backend ranks by review eligibility, recent failure, time since informative evidence, level fit, and content availability. Stable target IDs break ties; a stored seed can vary exercise choice reproducibly. Avoid testing the same target twice until three intervening questions, unless there is insufficient content. Normally cap a target at two questions per session. Diversify exercise forms and subjects when eligible material permits. Manual topic focus changes the pool, not the mastery policy.

Use the profile level only to bound new content. Do not infer B2 certification from exercise accuracy. Cap reinforcement and never allow infinite failure loops. Prefer failure-specific correction and later transfer assessment over repeated memorization of one prompt.

## 13 Grading and Evidence Integrity

MVP grading is deterministic per exercise revision: accepted answer forms, permitted normalization, typed slots, alternatives, and rubric metadata are versioned with content. Online session payloads omit solutions; the answer response returns evaluation and explanation. Offline packs include a local evaluation payload because immediate feedback requires it. This learning product does not claim cheat-proof testing.

Server grading is authoritative for both online and offline attempts. Never trust a client “correct” flag. Store submitted answer, assistance events, timestamps, exercise revision, evaluation policy, and server outcome. Local feedback is provisional. If the confirmed outcome differs, show a calm correction in sync detail and update Progress after reconciliation; do not change a question underneath the learner mid-session.

Controlled sentence tasks accept explicitly reviewed alternatives. Ambiguous prompts are quarantined. AI may propose content or assist internal review later, but model output cannot publish exercises or certify mastery automatically. No inference calls are needed for this documentation or normal builds and tests.

## 14 Content Model and Editorial Operations

Content hierarchy: topic → skill → target → exercise revisions. Lexical resources, examples, explanations and assets are linked independently. Use a lightweight skill tree with one parent per skill initially; prerequisites are separate explicit edges validated against cycles. B1/B2 and work-related labels are tags, not separate duplicated catalogs.

Each target has stable ID, title, kind, skill, optional lexical sense, level tag, learning objective, explanation and status. Each exercise has type, primary target, prompt schema, solution schema, accepted variants, hint, rationale, difficulty, editorial provenance and immutable revision. Template-generated instances require frozen IDs/seeds and must reproduce exactly for grading.

Exercise variants in MVP: typed short answer; selected choice; labelled cloze; word ordering; controlled multi-slot completion. Implement subject content through these forms. Each renderer declares supported schema versions. A session excludes incompatible exercises based on client capabilities; unsupported data must never crash a session or silently disappear.

Editorial flow: imported draft → automated validation → German-language review → exercise preview → published content release. Validation checks schema, accepted-answer completeness, target linkage, missing explanations, duplicate prompts, incorrect POS normalization, broken assets and provenance. Review incorrect distractors and meaningful alternative forms manually. A source's “approved” flag is evidence to audit, not automatic 2.0 approval.

Store source URL, creator/attribution, license identifier, acquisition date, derivation notes, and checksums. The old enrichment pipeline includes external providers and attribution assumptions. Review rights and obligations before republishing; verify authoritative license texts if legal interpretation is needed. Do not assume permission from a cached file's existence. Withhold questionable material pending review.

Publish immutable content releases containing compatible exercise revisions and a manifest/checksum. Pin sessions and offline packs to a release. Retire by tombstone and retain historical grading definitions. Corrections create a revision and identify affected attempts; if a rubric was wrong, regrade affected evidence and rebuild projections with an audit record. Meaningful changes to a learning target create a new target ID rather than inheriting unrelated mastery.

Initial quality scope: about 120 targets, at least two reviewed variants each, distributed across the seven subject groups. This is a planning target; audit actual inventory and reviewer capacity before committing. Avoid claiming catalog coverage or correctness until the review completes.

## 15 Proposed Database and Domain Model

Use one Postgres database per environment. Keep content and learning tables in a private application schema; the product API accesses them through a least-privilege server role. The identity provider owns authentication identities. Database schema is not a client API, and Room/IndexedDB schemas are not replicas of its table structure.

| Entity | Main fields and keys | Relationship or constraint |
|---|---|---|
| learner_profile | user UUID PK, locale, timezone, level, session preference | User identity; no duplicate auth password store |
| skill | UUID PK, parent ID, slug, title | Unique slug; cycle validation |
| skill_prerequisite | skill ID, prerequisite ID | Composite PK; validated acyclic graph |
| learning_target | UUID PK, skill ID, kind, objective, lexical sense ID?, status | Grammar permitted without lexical link |
| lexeme and lexical_sense | UUIDs, lemma, POS, gloss, language | Sense distinguishes meanings; preserve stable import map |
| inflection | UUID, lexeme ID, form, features | Uniqueness by lexical identity and features |
| exercise | UUID PK, target ID, active status | Stable logical identity |
| exercise_revision | exercise ID, revision, type, payload, rubric, policy version | Composite PK; immutable after publication |
| content_source | UUID, locator, license, attribution, checksum | Provenance of each sourced asset |
| content_attribution | resource ID/type, source ID, derivation | Many sources per resource |
| content_release | UUID, version, manifest hash, published timestamp | Immutable published set |
| content_release_exercise | release ID, exercise ID, revision | Pins exact exercise revision |
| device | UUID, user ID, platform, last seen | Identifier is not an authentication credential |
| practice_session | UUID, user ID, mode, engine version, release ID, status | Owns deterministic question allocation |
| session_question | UUID, session ID, exercise ID/revision, position, purpose | Unique session/position; purpose includes reinforcement |
| attempt | UUID, user ID, question ID, device ID, answer, assistance, received sequence | Unique user/attempt ID and question first submission |
| attempt_evaluation | attempt ID, evaluator version, grade, reason, evaluated at | Latest effective evaluation plus audit history |
| learner_target_state | user ID, target ID, status, evidence summary, projection version | Composite PK; derived and rebuildable |
| review_schedule | user ID, target ID, due_at, interval step, policy version | Composite PK; server-owned |
| mastery_transition | UUID, user ID, target ID, from/to, evidence references | Deduplicate transition by projection generation |
| sync_change | sequence, user ID, object kind, object ID, operation | Stable server cursor and tombstones |
| content_report | UUID, exercise/revision, user ID?, category, comment, status | Internal moderation workflow |

Use UUIDs for domain identities and client attempt IDs; use a server sequence for ordering synchronization changes. Store UTC instants and the learner timezone used for qualifying local dates. Validate enum values, JSON payload versions, nonnegative durations, and relationship ownership at ingestion. JSONB contains typed exercise/rubric variants; relational columns carry identity, ownership, versioning and query predicates.

Indexes: `(user_id, due_at)` for due selection; `(user_id, target_id)` for state; `(user_id, received_sequence)` for history replay; `(user_id, sequence)` for sync; target/type/status indexes for eligible content. Resolve competing updates inside a transaction with per-user/target locking or optimistic revision retries. Insert attempt, evaluation, snapshot/schedule update and sync event atomically. Sessions created concurrently can overlap, but cannot cause lost projection updates.

Referential policy: retire content instead of cascading deletion into historical attempts. Profile deletion invokes explicit deletion/anonymization policy for learner data and logs; do not let content ownership delete shared catalog resources. User export contains owned attempts, confirmed state and preferences, with schema version. Specify retention and deletion windows before public release with an appropriate privacy review; this blueprint is not a legal determination.

Conceptual relationship flow:

```text
Skill → LearningTarget ← LexicalSense (optional)
                  ↓
              Exercise → ExerciseRevision ← ContentRelease
                              ↓
Learner → Session → SessionQuestion → Attempt → Evaluation
   ↓                                           ↓
TargetState + ReviewSchedule ← versioned evidence reducer
```

## 16 Repository Strategy and Architecture Decision

### ADR 001 One monorepo with native clients

Status: recommended for adoption at M0 exit. Context: one product, two languages, coordinated content and learning changes, no user migration burden, and existing viable client foundations.

| Option | Coordination | Sharing | Operational cost | Verdict |
|---|---|---|---|---|
| Keep two repos and put backend in web | Requires cross-repo release discipline | Duplicated models and token updates likely | Low initial change, recurring drift | Do not retain as target |
| Keep two clients plus backend/contracts repo | Explicit ownership, versioned artifacts | Good if package publishing is disciplined | Three repos and multiple release dependencies | Appropriate later for independent teams |
| Monorepo with React and Kotlin | Atomic contract/client review; independent artifacts | Contracts, tokens, fixtures, content; TS libraries where useful | One root with two build ecosystems | Recommended |
| Rebuild both UIs with one cross-platform framework | Potential UI sharing | Broad but requires replacing current clients | Highest reset scope; native behavior must be rebuilt | Reject for this phase |

Monorepo is a coordination decision, not a requirement that every component be shared. Preserve native Android. Do not introduce Kotlin Multiplatform just to run the server algorithm on the phone: prepared offline sessions make duplicate scheduling unnecessary. Reconsider repo split only when ownership, security access or independent release teams create a clear benefit.

### Target layout

```text
german-master/
├── apps/
│   ├── web/                 React learner client
│   ├── android/             Gradle root and native application
│   └── content-admin/       Small internal web workspace
├── services/
│   └── api/                 Express application and deployable adapter
├── packages/
│   ├── learning-engine/     Pure TypeScript grading/state/selection policy
│   ├── api-client-ts/       Generated TypeScript DTOs and client
│   ├── web-ui/              React primitives and token bindings
│   └── content-tools/       Import validation and release building
├── contracts/
│   ├── openapi.yaml
│   ├── exercise-schemas/
│   ├── fixtures/            Valid/invalid requests and grading examples
│   └── generated/kotlin/    Generated module consumed by Android
├── design/
│   ├── tokens.json
│   ├── component-specs/
│   └── generated/           CSS variables and Compose theme values
├── content/                 Reviewed sources and release manifests
├── db/                      Drizzle model and one SQL migration history
├── ops/                     Environment and release runbooks
├── docs/                    Product baseline, ADRs, UX and backlog
└── .github/workflows/       Separate jobs and deployment gates
```

Use npm workspaces for TypeScript and the Android Gradle wrapper for Kotlin. Start without a universal task orchestrator; GitHub Actions can invoke each toolchain. Add build caching/orchestration only if measured CI cost warrants it. Large licensed datasets, generated packs and binaries belong in artifact storage with manifests, not routine source commits. Commit lockfiles and generator versions.

### Ownership

The product lead owns scope and success criteria; UX lead owns interaction/token specifications; content lead owns correctness and publication; platform lead owns schema, API, engine and compatibility; web/mobile leads own their client and local storage. One person may hold several roles, but each change needs the appropriate review perspective. Use CODEOWNERS to route reviews as a team grows, with one accountable owner per area. The backend is a product service, not a private part of the website.

## 17 API Boundaries and Contract Management

Use a versioned HTTPS JSON API under `/v2`. Describe DTOs, exercise discriminators, errors and authentication in OpenAPI 3.1, with JSON Schema for exercise payloads. The [OpenAPI specification](https://spec.openapis.org/oas/v3.1.0.html) supports a language-independent API definition. Generated models are transport types; clients map them to UI/local domain models.

| Endpoint | Responsibility | Key behavior |
|---|---|---|
| GET/PATCH `/v2/me` | Profile and preferences | Only authenticated subject; field-level validation |
| GET `/v2/home` | Ready counts and next action | Consistent snapshot version and generated timestamp |
| POST `/v2/sessions` | Select and pin questions | Mode, duration preference, capabilities; idempotent creation |
| GET `/v2/sessions/{id}` | Resume owned server session | Pinned revisions; ownership checked |
| POST `/v2/attempts:batch` | Evaluate and commit attempts | Per-item acknowledgments and idempotency |
| POST `/v2/sessions/{id}/complete` | Mark full/partial completion | Idempotent; never substitutes for accepted attempts |
| GET `/v2/progress` | Confirmed state groups | Pagination, snapshot version, pending handled locally |
| GET `/v2/targets/{id}` | Target evidence and explanation | Own learner evidence plus published content |
| GET `/v2/topics` | Published topics and eligibility | No unpublished catalog exposure |
| POST `/v2/offline-packs` | Prepare owned downloadable sessions | Manifest, rubric versions, checksums, validity metadata |
| GET `/v2/sync?cursor=...` | Ordered learner deltas | Opaque server cursor, tombstones and pagination |
| POST `/v2/content-reports` | Correction report | Exercise revision and category; rate limited |
| POST `/v2/guest-attempts:claim` | Explicit attach after login | Validated starter content; replay-safe claim ID |
| GET `/v2/me/export`; DELETE `/v2/me` | Privacy actions | Reauthentication where appropriate; explicit status |

Admin APIs are separate under `/internal`, with an operator role and audit trail. No shared admin token in learner builds. Authentication can call Supabase directly; all learning data goes through the product API. Verify server tokens using the provider's supported verification mechanism and derive ownership from the verified subject. Never trust a user ID sent in a request body as authority.

Attempt request fields: attemptId, sessionQuestionId, exerciseRevision, deviceId, answer payload, hint/reveal events, answeredAt, client sequence, grading-policy version used locally and offline-pack ID where relevant. Grade is returned, not accepted as authoritative input. Batch maximum proposed at 50 attempts and bounded payload size; validate per item, transactionally commit each accepted item, return accepted/duplicate/rejected statuses and structured reasons. A retry of the same attempt returns its original acknowledgment; reuse with a different payload yields conflict.

Errors contain code, safe message, request ID, retryability, and optional retry-after metadata. Use 400 for malformed data, 401 for expired/invalid auth, 403 for unauthorized access, 409 for idempotency/capability conflict, and 429 for limiting. Never leak database errors. Log request IDs and policy versions without raw private answers by default.

Contract changes: update schema and fixtures → regenerate both SDKs → verify no generated diff remains → exercise both clients in CI → deploy compatible server → release clients. Additive fields are optional for older clients; enum/renderer additions require declared capabilities. Breaking changes get a new API version or negotiated payload version. Maintain compatibility with the released Android app through its announced sunset; propose at least 90 days after replacement availability, extended if adoption/store review requires. Dates are policy proposals, not current commitments.

## 18 Client and Backend Responsibilities

| Concern | Backend | Web | Android |
|---|---|---|---|
| Identity | Verify subject and authorize resources | Browser auth flow and session handling | Native credential/auth flow and secure token storage |
| Session selection | Rank and pin exercises | Request/display prepared session | Request/display prepared session |
| Grading | Confirm deterministic result | Online result or provisional offline feedback | Same contract; local offline evaluator |
| Mastery and schedule | Sole confirmed writer | Cached snapshot and pending indicators | Cached snapshot and pending indicators |
| Local persistence | Accept durable event IDs | IndexedDB session/outbox; PWA shell | Room session/outbox; DataStore preferences |
| Sync | Idempotency, ordering, projections, tombstones | Foreground/online flush; background optional | WorkManager and foreground reconciliation |
| Design | Publish shared specifications | React wrappers and responsive layout | Compose components and adaptive layout |
| Content | Validate, publish, version | Render supported types | Render supported types |

The learning-engine package is pure TypeScript with clock and randomness supplied explicitly, so policies are reproducible. The server imports it. Web may use the grading subset for downloaded content. Android implements the minimal deterministic offline grading policy in Kotlin and must pass the same fixtures. Scheduling and confirmed mastery never run independently on either client. Introducing portable algorithm execution is unnecessary until fixtures reveal an unmanageable grading surface.

## 19 Offline Synchronization and Conflict Rules

Prepare a small rolling reserve, initially two sessions plus their explanations. Download payloads over an authenticated API, check manifests, and commit an entire pack locally before declaring it ready. Pack expiry controls whether a new offline session can start; already started sessions remain uploadable with their pinned revisions. Proposed new-start validity is seven days, subject to pilot experience.

The Android design uses repositories with local and network data sources, consistent with [Android's offline architecture guidance](https://developer.android.com/topic/architecture/data-layer/offline-first). UI reads local state; network reconciliation updates that state. Web uses the same product behavior with IndexedDB and a service worker for app assets. Cached API responses alone are not an offline session contract.

Write answer event and session position in one local transaction. Outbox entries move pending → in-flight → acknowledged, with in-flight recovered after restart. Delete only after persisting the server acknowledgment. Retry with the same attempt ID after network timeout; server idempotency makes unknown outcomes safe to reconcile. Exponential backoff with jitter and Retry-After for API sync is allowed; this is independent of Groq's no-ambiguous-retry guidance because the product endpoint is expressly idempotent.

Sync pushes attempts, then pulls confirmed deltas using a server cursor. Never advance the pull cursor until all returned changes are locally committed. Expired cursors trigger a full confirmed snapshot while preserving pending events. Include deletions/tombstones. Download/update content atomically; do not replace a revision needed by an active session or pending attempt.

**Conflict policy:** immutable attempts merge by identity, not timestamp. First accepted answer for a session question is graded; edits after feedback are new practice questions, not changes to evidence. Different devices create distinct sessions and events. Same-target events are evaluated in deterministic server receipt-sequence order for the online projection. Independently successful repetitions on the same qualifying day count once; reinforcement never advances retained mastery. Two questions answered before either result was known can both be retained but do not double-count the date gate.

Store answeredAt for educational timing, but compare against server-issued session/pack time and server receive time. Quarantine impossible/future times; missing or implausible timing may retain the answer while excluding it from spaced-retention qualification. Late arrivals rebuild that target's evidence summary under the same ordering policy; client clocks cannot move authoritative due dates arbitrarily. Effective local date is derived from validated timing and timezone, with policy metadata recorded.

If multiple devices have already prepared sessions, later sync cannot retrospectively make those selections adaptive. Show consistent confirmed Progress after sync and refresh future packs. Settings may use server revision/last accepted write; attempts never use last-write-wins. Account changes partition local storage by subject and prevent one user's outbox from uploading as another. Sign-out offers sync now or explicit local removal; do not discard pending work silently. Account deletion revokes ability to upload abandoned events.

Privacy: cache only necessary learner data; use app-private native storage and supported secure credential storage. On web, acknowledge that browser storage is not secure hardware storage and shared devices need explicit sign-out clearing. Do not store raw bearer tokens in the exercise outbox. Downloaded solutions are an intentional learning aid, not protected examination material.

## 20 Environments CI and Release Flow

Environments: local, CI disposable, staging, production. Each has separate database/auth/storage configuration and no production credentials in developer/test jobs. PR previews use fixtures or isolated staging resources, never production tables. Pin runtime/toolchain versions after M0 build verification rather than assuming the current manifests build on every machine.

Retain the existing hosting provider for the first thin API/web deployment if its measured execution limits fit. Place the API behind a stable product endpoint and isolate provider adapters. The reset does not depend on changing host or migrating to Next.js. Current hosting configuration is a starting point, not a guarantee. Check current provider limits before committing pack sizes, long-running jobs or paid services.

Database migrations have one owner and one ordered history in `db`. Use reviewed generated SQL through the chosen Drizzle workflow; do not also maintain an independently authored Supabase migration history. Provision and access-policy definitions must be versioned. Content publishing and migrations are explicit release steps, not side effects of every web build or incoming task request.

CI required on PRs: schema lint; generated-contract/token cleanliness; TypeScript type checking; engine/contract tests; web component tests; production build with safe fixtures; Android unit tests, lint and debug assembly; content validation; secret scanning. Run changed-area jobs where practical, but changes to contracts/content/tokens fan out to both clients. Nightly or release candidates add real Postgres integration, emulator scenarios, accessibility and screenshot review. Shared branch protection lists required checks clearly.

Release flow:

1. Merge a reviewed change with all required checks and documented compatibility.
2. Build immutable artifacts tied to commit, API/content/policy versions, and checksums.
3. Apply compatible database changes to staging once; deploy API, publish staging content, then web.
4. Distribute an Android internal-test build against staging; verify auth, session, grading, offline, and cross-device outcomes.
5. Approve release evidence; apply production compatible migrations and deploy API before dependent clients.
6. Publish content, promote web, then distribute signed Android AAB through internal/closed testing and a controlled production rollout when appropriate.
7. Observe sync rejection, grading disputes, error rates and crash reports; pause rollout on regression.

Web/API and Android have independent artifact versions and release cadence. Use tags such as `api-v2.0.0`, `web-v2.0.0`, and `android-v2.0.0`; one root version does not force simultaneous store release. Android versionCode must exceed the highest published code, not merely the inspected code 29. Keep signing material outside source and available only in protected release jobs.

Rollback: redeploy last compatible API/web artifacts; halt Android rollout and issue a higher-code corrective build. Do not assume a store downgrade is possible. Use expand/contract migrations once users exist; avoid destructive database rollback. Content releases can be retired and sessions pinned to still-supported prior releases. Rehearse restoring database backups in staging before production cutover.

## 21 Authentication Data Access and Operational Baseline

Continue with Supabase Auth/Postgres unless M0 reveals a concrete blocker. Both clients authenticate against the same environment identity provider. Keep the learning schema private with no learner PostgREST table grants. Exposed tables must have suitable RLS; server routes still authorize every resource. [Supabase's RLS guidance](https://supabase.com/docs/guides/database/postgres/row-level-security) distinguishes row policies from table access. Never ship service credentials to either client.

Server queries bind the verified subject, not a caller-supplied user ID. Internal roles come from controlled authorization data, not editable user metadata. Use separate content-publishing privileges. Validate request bodies, rate-limit creation/upload/report endpoints and enforce payload caps. Correlation IDs support incident investigation without logging submitted answers or emails by default.

At release record: environment owner, backup/restore owner, signing recovery location, content rollback steps, API compatibility policy, incident contact and error-budget thresholds. This is a small product; avoid introducing distributed microservices, event brokers or a data warehouse before load/ownership justifies them.

## 22 Analytics and Success Metrics

North star: **retained resolution of previously weak targets per active learner per week**. Count a distinct target that previously entered Needs practice, passed the operational mastery gate, and later passed an unassisted check at least seven days after that transition. The metric is delayed and cohort-based; show provisional “newly mastered” separately. Count a target once per resolution cycle and distinguish later relapse. This prevents same-session repetition from looking like learning.

| Measure | Definition | Initial use |
|---|---|---|
| Activation | First completed session with at least 10 graded questions, within 24 hours of setup | Find onboarding friction; shorter valid sessions reported separately |
| Session completion | Completed planned sessions / started sessions | Pilot target ≥70%; proposed, not measured |
| Perceived usefulness | Post-session optional relevant/useful response | Pilot target ≥80% useful among respondents; report response bias |
| Retention check pass | Unassisted correct delayed checks / eligible delayed checks | Track by target, level and exercise type |
| D7 return | Activated learners practising on days 6–8 after activation | Directional pilot signal, no asserted benchmark |
| Correction dispute | Reported grading problems / graded attempts | Investigate by exercise revision, not just aggregate |
| Sync integrity | Accepted unique attempt IDs compared with durable local submitted IDs | Release target zero loss/duplicate projection effects in test scenarios |
| API responsiveness | Session creation p95 under representative staged load | Proposed target <1 second excluding network; load profile defined in M2 |
| Android reliability | Crash-free sessions and offline completion | Set public numeric target after representative pilot data |

Events: setup_completed, session_started, question_presented, attempt_saved_local, attempt_accepted, session_completed/ended, hint_used, content_reported, pack_downloaded, sync_failed/recovered and mastery_transition. Authoritative grading and transition analytics are emitted by the backend; client events describe interaction and delivery. Deduplicate by event ID. Include versions, platform, coarse subject and pseudonymous learner ID. Do not send raw answers, emails or full sentences to analytics.

During the no-user stage, fixtures prove correctness, not user value. In the pilot, report denominator, sample size, missing follow-up and cohort timing. A high immediate accuracy rate can mean questions are too easy; retained transfer on different contexts is the meaningful signal. Decide analytics vendor and consent/data retention requirements before public rollout; avoid unnecessary tracking by default.

## 23 Roadmap Milestones and Exit Gates

Indicative plan for a small team or one developer with part-time design/content review: roughly 13–19 weeks, with content review and offline reliability likely on the critical path. These are planning ranges, not promised dates. Re-estimate after M0; do not trade exit gates for a calendar target. The pilot range includes the delayed-retention follow-up.

| Milestone | Planning range | Deliverables | Exit gate |
|---|---|---|---|
| M0 Preserve and establish baseline | 3–5 working days | Tags/bundles, content/DB export, deployment inventory, builds, repo decision | Restore rehearsal works; inspected and deployed versions distinguished |
| M1 Contracts design and content foundation | 1–2 weeks | Monorepo skeleton, tokens, journeys, API schemas, initial DB, 30 reviewed targets | Both clients parse all initial variants; documented design approved; content validation passes |
| M2 Authoritative learning engine | 2–3 weeks | Grading, evidence reducer, sessions, attempt API, deterministic fixtures | Different learner histories yield appropriate different queues; idempotency and replay pass |
| M3 Complete web vertical slice | 2–3 weeks | Onboarding, Home, Practice, summary, Progress, Topics, auth | A learner completes and resumes a mixed session with correct confirmed progress |
| M4 Android parity | 2–3 weeks | Native screens, API replacement, Room/outbox, token bindings | Same fixture history produces same confirmed outcomes; accessibility core journey passes |
| M5 Offline cross-device and operations | 2–3 weeks | Prepared packs, reconciliation, privacy actions, CI/release runbooks | Airplane-mode/restart/two-device/expired-auth scenarios lose no attempts |
| M6 Pilot and public readiness | 3–4 weeks | About 120 reviewed targets, learner pilot and follow-up, reliability fixes, store/release assets | No unresolved critical defects; feedback and delayed-retention evidence reviewed |
| M7 Targeted expansion | After usefulness gate | Add/import material, broader contexts, better calibration | Each expansion demonstrates usefulness without weakening the core loop |

Dependencies: stable exercise schemas precede renderers; target/content quality precedes meaningful engine evaluation; backend vertical slice precedes production Android API integration; offline semantics precede release. Android shell/token work can overlap web, but not at the cost of implementing a competing engine. Content review begins at M1 and runs throughout.

MVP delivery includes both clients and the verified offline subset. M3 is an internal web reference release, not permission to leave Android behind. Do not add import, speech or full writing before M6 exit.

## 24 Epics and Starter Backlog

Backlog hierarchy: milestone → epic → user story → implementation task. Label client, domain, priority, risk, dependency and verification type. Stories include learner outcome, acceptance examples, empty/error/offline behavior and measurement impact. State flow: Proposed → Ready → In progress → Review → Verified → Done. Limit concurrent work to one product vertical slice plus one content/quality stream.

| ID | Epic / story | Priority | Depends on | Acceptance evidence |
|---|---|---|---|---|
| GM-001 | Preserve code and content baseline | P0 | None | Both repos restore from bundles; exports have checksums |
| GM-002 | Audit deployment and store identity | P0 | None | Environment map; signed app/update continuity verified |
| GM-003 | Create monorepo without changing behavior | P0 | 001 | History/source maps, build paths and independent jobs work |
| GM-004 | Define API and exercise contracts | P0 | 003 | TS/Kotlin consume valid fixtures and reject invalid variants |
| GM-005 | Generate semantic tokens for both clients | P0 | 003 | Light/dark components show same semantic roles |
| GM-006 | Build target model and new DB baseline | P0 | 004 | Blank database installs; constraints and ownership tested |
| GM-007 | Review initial 30 targets and variants | P0 | 004 | Editorial checklist and source attribution recorded |
| GM-008 | Grade deterministic exercise variants | P0 | 004,007 | Server/web/Kotlin fixtures agree on boundaries and assistance |
| GM-009 | Reduce attempts into mastery and schedule | P0 | 006,008 | Same-day repetition, lapse, spaced gates and replay covered |
| GM-010 | Create adaptive mixed sessions | P0 | 009 | Contrasting histories produce different justified sessions |
| GM-011 | Commit attempts idempotently | P0 | 006,008 | Timeout retry returns same result; projection updated once |
| GM-012 | Implement web setup to summary journey | P0 | 005,010,011 | Keyboard and narrow viewport complete/resume session |
| GM-013 | Implement Progress and target detail | P0 | 009,012 | Counts match API; learner understands next action |
| GM-014 | Implement Topics with focused practice | P1 | 010,012 | Same engine policy used; no duplicated mastery |
| GM-015 | Replace Android table access with API | P0 | 004,011 | Learning traffic uses v2; raw-table API calls removed |
| GM-016 | Implement native core screens | P0 | 005,015 | TalkBack, large font and keyboard-inset journey verified |
| GM-017 | Prepare and cache offline sessions | P0 | 010,012,016 | Manifest commit atomic; unavailable pack handled honestly |
| GM-018 | Reconcile durable outboxes across devices | P0 | 011,017 | Restart, retry, auth switch, late events, duplicate questions tested |
| GM-019 | Provide export deletion and sign-out behavior | P0 | 006,018 | Ownership isolation; no pending data attached to wrong account |
| GM-020 | Instrument useful learning and reliability | P1 | 009,018 | Deduped event catalog; no raw private content in telemetry |
| GM-021 | Build protected release pipeline | P0 | 003,004 | Staging promotion and rollback rehearsal recorded |
| GM-022 | Expand reviewed pilot content | P0 | 007 | Coverage and two-variant quality targets reviewed |
| GM-023 | Run learning and usability pilot | P0 | 018,021,022 | Findings, denominators, delayed checks and fixes documented |
| GM-024 | Add personal material ingestion | P2 | M6 exit | Draft import review, provenance and safe publication specified |

Example story GM-018: “As a commuter, I can finish downloaded practice and later see the same confirmed progress on my laptop.” Accept when 15 local answers survive a force-stop, repeated upload produces 15 accepted unique events, same-day repetitions do not inflate mastery, and both clients show the reconciled target state. Authentication expiry must retain pending attempts without leaking them to another account.

Definition of Ready: named target problem; in-scope milestone; owner; agreed contract/design dependency; concrete acceptance examples; content available; no unresolved decision that blocks implementation. P0 means required for the release, P1 improves the planned product but can be reduced if justified, P2 is explicitly deferred. Security/data-loss/accessibility blockers override feature priority.

## 25 Definition of Done and Testing Strategy

A story is Done when its acceptance behavior works on each affected client; content and contracts are versioned; relevant automated checks pass; failure/offline/accessibility states are reviewed; documentation is updated; telemetry is safe and meaningful; deployment compatibility is known; and no critical defect or data-loss path remains. Screenshots alone do not establish learning correctness, and unit tests alone do not establish usability.

| Layer | Required verification | Scope |
|---|---|---|
| Learning engine | Deterministic fixtures and property/invariant tests | Same evidence → same state; assisted/repeated attempts cannot manufacture mastery |
| Grading conformance | Shared Unicode, case, umlaut, alternate-answer and ordering fixtures | Server, web offline and Kotlin offline agree |
| API/auth | Integration and negative ownership tests | Wrong-user sessions, forged body IDs, expired auth, payload caps |
| Database | Real Postgres transactional tests | Constraint behavior, concurrent inserts, replay, migration install; pg-mem alone insufficient |
| Client behavior | Web component and Android ViewModel tests | Submission locks, draft restore, unknown variants, provisional results |
| Persistence | IndexedDB and Room tests | Atomic save, restart recovery, account partitioning, cursor commit |
| End to end | Browser plus emulator/device scenarios | Setup → session → feedback → progress; same account on both clients |
| Offline/sync | Fault injection | Lost response, 429, airplane mode, restart, stale pack, late arrivals, partial batch |
| Accessibility | Automated web checks plus manual keyboard/TalkBack | Focus, live announcements, reflow, large font, drag alternatives |
| Content | Schema validation and German review | Rubrics, examples, transfer variants, attribution |
| Release | Staging smoke, signed release build and restore drill | Environment isolation, auth redirects, compatibility, recovery |

Performance tests use a representative seeded staging dataset and documented device/network/load conditions. Fix meaningful regressions; do not claim speed targets from a development laptop. Offline learning paths and routine tests use fixed local content, no live AI services. Source inspection performed for this blueprint did not execute the old application test suites or builds; M0 establishes that baseline.

Release blocking criteria: lost attempts, ownership leak, inconsistent confirmed grading, inaccessible core journey, invalid migration, unrecoverable startup crash, or a content error affecting a material practice set. Store/assets/theme defects can also block when they prevent normal use. A pilot issue log tracks remaining minor defects and owner/target release.

## 26 Hard Reset and Repository Restructuring Runbook

The no-user constraint removes historical learner migration work, not the need to preserve content, store identity, backups and a rollback path. No destructive action has been executed as part of this analysis.

### Preserve before restructuring

1. Confirm the deployed web/API commit, database project, Android published version and signing identity. Record clean/dirty state of both checkouts; preserve any uncommitted work separately.
2. Create immutable legacy tags and Git bundles for both repositories. Export the existing database, approved content, provenance/enrichment snapshots, manifests and deployment configuration. Secrets remain in approved secret storage, outside the documentation and repo.
3. Restore bundles and the database export into disposable locations. Compare content counts/checksums and representative records. Archive only after this test succeeds.

### Build the target without disturbing originals

4. Create a sibling staging checkout, for example `C:/Projects/german-master-2`, rather than moving folders inside the current checkout immediately.
5. Start from the web repository history and import Android under `apps/android` with a history-preserving subtree merge. First rehearse on temporary clones; verify authors/commits and path mapping. Keep the full originals and bundles regardless of history strategy.
6. Move web, server, shared utilities and tooling into the target layout in dedicated mechanical commits. Adjust build paths and imports before changing product behavior. Keep one TS lockfile/workspace and the Android wrapper/version catalog.
7. Replace instructions with explicit ownership, checks and secrets rules. Add README/bootstrap and CI. Legacy docs move into an archive folder in the target repo; mark this blueprint as the active baseline once adopted. Do not overwrite synced ChatGPT `sources/` files.
8. Keep legacy apps runnable from their tags while the new implementation develops. Avoid carrying compatibility branches into the new schema merely to run old clients against it.

### Reset data and cut over

9. Provision isolated 2.0 staging database/auth/storage. Apply fresh schema and reviewed content only; no learner/history data import. Preserve a mapping of legacy content IDs to new targets for audit, not automatic mastery inheritance.
10. Add new local namespaces: web `german-master-v2` IndexedDB/cache names, Android a new Room baseline or explicit legacy-cache reset. Test upgrade from an old installed build even without real users. Remove old service-worker behavior without deleting unrelated origin storage. Explain the reset to any test installations.
11. Provision a separate 2.0 production environment. Verify auth redirects, API URL, permissions, pack manifests and staging/production isolation. Rehearse rollback before switching traffic.
12. Switch web routing and publish the Android update after release gates. Old clients must fail gracefully or show an update notice; they must never write incompatible data into the new system. Preserve applicationId/signing continuity and increase versionCode beyond the published maximum.
13. Disable legacy writes and old deployment automation after cutover. Observe the new system, then archive the old Android GitHub repo only when its history, issues, release assets and signing recovery are recorded. Retain the web remote as the eventual monorepo remote to minimize URL churn, replacing its main branch through a reviewed cutover PR.

Do not run the old `db:reset`, blindly reseed production, force-push history, delete repos, or clear all browser storage as an implementation shortcut. The cleanest reset is a new environment with a controlled cutover. Future real users will require deliberate migrations and compatibility, even though this reset does not.

## 27 Risks Open Decisions and Decision Register

| Risk | Likelihood / impact | Mitigation | Owner / gate |
|---|---|---|---|
| Weakness tracking overclaims learning | Medium / high | Spaced transfer checks; visible operational definition; pilot calibration | Product + learning / M6 |
| Content correctness/rights block release | High / high | Small reviewed baseline; provenance; quarantine uncertain sources | Content / M1–M6 |
| Offline outcomes diverge | Medium / high | Shared grading fixtures; provisional feedback; server authority | Platform + clients / M5 |
| Late concurrent evidence inflates mastery | Medium / high | Unique IDs, date qualification, receipt ordering, replay tests | Platform / M2–M5 |
| Reset scope overwhelms small team | High / high | Vertical slices; defer import, speech, essay grading and framework changes | Product / every milestone |
| Native store update/signing interruption | Medium / high | Verify existing identity and published code early; protect recovery assets | Mobile / M0 |
| Legacy database grants remain exposed | Unknown / high | Inspect live policies/grants in M0; new private schema and ownership tests | Platform / M0–M2 |
| Generated SDK drift | Medium / medium | Pin generator; regenerate in CI; compatibility fixtures | Platform / M1 onward |
| Hosting limits unsuitable for pack jobs | Unknown / medium | Measure workloads; background publishing outside request path | Platform / M2 |
| Metrics reward repetition | Medium / high | Distinct target/date rules; delayed retained-resolution metric | Product / M2–M6 |

| Decision | Recommended default | Resolve by | Evidence needed |
|---|---|---|---|
| Repository strategy | Monorepo, React and native Kotlin | M0 exit | Build/path/history rehearsal |
| Backend owner | Dedicated service in monorepo | M1 | Contract and deployment boundaries |
| Identity/database provider | Retain Supabase, new environment | M0 | Live configuration, access review, resource fit |
| Mastery policy | Explicit interval ladder and retained-evidence gate | M2 initial, M6 calibrate | Simulation, pilot delayed recall |
| Interface languages | English and German | M1 | Learner validation and content reviewer capacity |
| Guest mode | Local starter; explicit attach on login | M1 | Account switch and ownership acceptance examples |
| Android listing | Preserve application identity | M0 | Store access and signing continuity |
| Content inventory | About 120 targets/two variants by pilot | M1 re-estimate | Approved content audit and review throughput |
| Analytics vendor/retention | Minimal first-party event collection initially | Before M6 | Privacy requirements and actual reporting need |
| Import and monetization | Deferred | After M6 | Demonstrated usefulness and learner demand |

These are recommendations pending adoption, not approvals already given by a fictional team. Keep ADRs with status Proposed, Accepted, Superseded. Record the decision, rationale, owner, date, evidence and consequences. Implementation changes update this baseline rather than accumulating competing roadmaps.

## 28 Source of Truth Governance and Next Execution Slice

Store the adopted blueprint in `docs/product/blueprint.md`, with linked design specs, API schema, database migrations, content releases, ADRs and milestone backlog. Code and schema define executable behavior; the blueprint defines intended behavior and acceptance. Any mismatch is a tracked defect or an explicit decision change. The project owner approves product scope; technical owners review feasibility; content and UX reviewers verify their areas.

The first implementation slice is GM-001 through GM-007: preserve legacy assets, establish builds/store/environment evidence, rehearse monorepo import, define shared contracts/tokens, create a new target-centered schema and review 30 targets. Its demonstration is both clients rendering the same sample exercises from one contract, with no production cutover. The next slice grades one complete session through the backend before expanding UI or content breadth.

## 29 Reference Index

Source evidence uses the repo revisions in Section 2. Key web files: `package.json`, `client/src/App.tsx`, `client/src/lib/practice-progress.ts`, `client/src/pages/home/use-practice-session.ts`, `shared/task-registry.ts`, `db/schema.ts`, `server/routes/tasks/handlers.ts`, `server/routes/tasks/schemas.ts`, `docs/adr/012-client-practice-state-migration.md`, `docs/ui-ux-guidelines.md`, `README.md`, `vercel.json`. Key Android files: `app/build.gradle.kts`, `navigation/Screen.kt`, `domain/usecase/GetNextTaskUseCase.kt`, `domain/usecase/SubmitAnswerUseCase.kt`, `data/repository/TaskRepository.kt`, `data/repository/PracticeRepository.kt`, `data/remote/SupabaseTaskApi.kt`, `data/remote/SupabaseHistoryApi.kt`, `data/local/db/AppDatabase.kt`. Android source paths are relative to `app/src/main/java/com/germanverbmaster/android/` unless specified otherwise.

Product context: the referenced “Refocus German App” conversation, particularly the B1–B2 recurring-mistake direction and the explicit web/Android shared-database hard reset with no users.

External technical references were checked on 3 October 2026: [Android offline architecture](https://developer.android.com/topic/architecture/data-layer/offline-first), [OpenAPI 3.1](https://spec.openapis.org/oas/v3.1.0.html), [WCAG 2.2](https://www.w3.org/TR/WCAG22/), and [Supabase row-level security](https://supabase.com/docs/guides/database/postgres/row-level-security). These support technical constraints; they do not validate this product's learning policy, schedule, market positioning, or actual runtime quality.

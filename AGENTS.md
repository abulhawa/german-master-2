# German Master 2.0 agent instructions

## Stabilize and release — owner direction (26 October 2026)

- Retain the architecture. Complete the three core defects: whole unfinished-round
  save/resume, round-specific consent/account isolation, and non-destructive native
  Continue. Do not start another renovation or expand the feature backlog.
- Hide unavailable workplace practice and unverified Google/Microsoft sign-in behind
  disabled release gates, retaining their implementation. Their activation is not a
  prerequisite for the core release. Email authentication remains available.
- Review the existing B1/B2 content for serious learning errors and run bounded
  lifecycle acceptance. After the core fixes, only reproducible critical failures,
  privacy/data-integrity defects and serious learning-content errors block the
  candidate. Other findings belong in the post-release backlog.
- Establish one stable web release independently of Android Play publication, then
  a small real-learner pilot. Keep shared behavior aligned in implementation; the
  Android owner approval hold remains. Do not add streaks or redesign for the pilot.
- Follow docs/operations/stabilize-release-2026-10-26.md for the finite checklist.
- Use a less expensive implementation worker; preserve Astra's audit/design
  findings. No further Astra work is requested.

- Use `docs/product/blueprint.md` as the product baseline; read sections relevant to the current slice and the applicable ADRs before changing behavior. `docs/adr/002-monorepo-bootstrap.md` records the accepted repository decision and transitional layout.
- This is the active monorepo foundation. Do not modify, archive or delete the two original repositories as part of routine work here.
- Search for scoped instructions case-insensitively. Instructions inside `apps/web` and `apps/android` still apply to their imported code. Root safety rules apply throughout this repository.
- Use PowerShell-compatible commands on Windows. Read or write secrets only in memory when strictly needed. Never print credentials, including commands suggested by historical setup documentation. Never commit `.env`, `local.properties`, signing keys or service-account credentials.
- Routine builds and tests must use local fixtures and mocks. Do not connect tests to production, run database-reset scripts, publish content, deploy, or upload store releases without authorization for that operation.
- Root npm workspace owns the TypeScript lockfile and inherited dependency overrides. Run `npm ci`, `npm run check`, `npm test` and the appropriate build from the repository root for web changes. Keep the Android wrapper and version catalog inside `apps/android`.
- For Android changes, run relevant unit tests, lint and debug assembly where the toolchain is available. Report unavailable checks precisely. Use emulators/devices for behavior that needs them; do not describe source inspection as runtime verification.
- New shared APIs use versioned contracts. Confirmed grading, mastery and scheduling belong to the future backend service. Do not add independent client mastery policies.
- UI/UX changes must satisfy the 2.0 blueprint and accessibility requirements. Existing web guidance applies to legacy code until explicitly superseded; record intentional differences in review descriptions.
- Owner clarification (3 October 2026): this is a hard reset. Agents may use current dependencies, replace dependencies, and change the design within the renovation scope. Treat the blueprint as the product baseline, not a requirement to preserve legacy libraries or visual design; document material departures and verify affected behavior. Application identity, signing continuity, preservation and release authorization boundaries still apply.
- Preserve application identity, signing continuity, provenance and immutable exercise revision semantics.
- Do not use live AI services in normal checks. Groq quota and credential rules from the user's global guidance remain applicable.
- Include a proposed commit message when reporting code changes. Distinguish completed bootstrap work from the unimplemented product reset.

## Unified practice presentation

- Owner direction (23 October 2026): web and Android, guest and signed-in practice must follow `docs/product/practice-presentation.md`. Authentication changes saving/sync, not exercise interactions or feedback vocabulary.
- When choices are supplied, do not also show a textbox or typing instructions. Use plain Correct / Not quite feedback; omit locally and Assisted answer labels while retaining hint usage, provisional receipts and mastery policies internally.

## Continuing the renovation

- Owner decision (20 October 2026): Android release is BLOCKED pending a renewed UX review and explicit owner approval. The owner rejects the current Android experience, specifically excessive help/explanatory text and unexpected behaviour. Stop the screenshot review for now; preserve partial artifacts and unfinished tooling. Do not treat prior design acceptance, passing tests, a debug install or screenshot generation as release approval. Do not publish/upload/promote an Android release until the owner explicitly lifts this hold. This does not reopen the accepted TalkBack scope.

- Owner decision (9 October 2026): TalkBack acceptance is completed for now based on the available evidence. Stop further TalkBack testing and defer improvements until needed. Record this as owner acceptance of the current scope, not a claim that a complete uninterrupted spoken/gesture journey passed. Do not reopen this M1 gate without a new owner request or a demonstrated critical defect. Final design/web accessibility acceptance and production release approval remain separate.

- Never put personal machine paths or private backup locations in tracked files, commits, or public reports. Keep those details in ignored/private recovery manifests; public evidence uses repository-relative paths or generic placeholders.


- Owner clarification (8 October 2026): backups may remain on this laptop; an off-machine destination is not an M0 completion requirement. Store private recovery artifacts outside the public checkout. Keep the existing legacy and v2 Supabase projects separate; no paid plan or third project is requested. Full recovery verification remains required.


- Owner clarification (8 October 2026): Android signing keys remain owner-held and builds are signed by the owner in Android Studio. Agent work concerns application identity and versionCode/versionName using the preserved sister project as reference; do not request or inspect signing keys or require agent certificate/custody verification to close M0. Source version numbers are not proof of a live Play maximum.


- Owner preference (8 October 2026): close M0–M6 sequentially. Keep only one milestone active; use the latest checkpoint for the current milestone. Preserve partial work in later milestones as queued; do not advance them or weaken exit gates when the active milestone is blocked. Reconcile current summaries and report exact missing resources.


- Owner preference (8 October 2026): always commit completed, verified work before handoff. This authorizes local commits by default; push, deployment, publication and release boundaries remain governed by the owner's applicable authorization.

When the user says "continue", "pick up where we stopped", or otherwise asks to resume German Master 2.0, treat it as an implementation request. Read root `PROGRESS.md`, then `docs/operations/continuation.md`. Inspect the actual checkout and relevant recent commits, reconcile the checkpoint, and carry out the next unfinished implementation slice. Do not restart the product analysis, ask the user to repeat the roadmap, or stop after proposing a plan.

Preserve ongoing work and complete the current slice before starting unrelated features. Update `PROGRESS.md` with changes, evidence, blockers and the exact next action before every handoff. Use the repo files as durable context; prior chat history is optional. Only advance milestone status when its exit gates are met. Keep production cutover, database resets and store releases within their separately authorized scope.

## Practice access — owner direction (24 October 2026)

- Release blockers only, prioritizing user-facing issues. New features remain frozen.
- Remove the separate welcome/Try/guest entry path. Open directly to practice Home; signing in is optional for practice.
- Account-dependent pages explain that an account is required and offer Sign in / Create account, with a return to practice. Do not interrupt practice with signup promotions.
- Preserve local drafts, explicit attachment and account isolation. Apply the product direction to both clients; Android release approval remains separate.

## Web and Android parity — standing owner direction (24 October 2026)

- Always keep web and native Android user-facing behavior in sync. Product-flow,
  interaction and feedback changes must be implemented and verified for both
  clients in the same work slice, not left as an optional later follow-up.
- Track any unavoidable parity gap explicitly as a release blocker; never claim
  parity based on shared source/contracts alone. Report client-specific evidence.
- This does not lift the Android release hold or authorize store publication.

## Grammar reference — owner direction (24 October 2026)

- Owner explicitly requests restoring the old quick grammar-reference menu in
  GM2.0 on both clients. This bounded restoration is authorized despite the
  general feature freeze.
- Make the reference available offline and without sign-in, including safe access
  during practice. Preserve the current draft, feedback and session position.
- Web and Android must use the same reference content and offer equivalent topic
  navigation, lookup, explanations and examples. Publication boundaries remain.


## Navigation and sign-in presentation - owner direction (25 October 2026)

- Provider sign-in buttons include recognizable service icons and readable names
  on both web and native Android.
- Every learner page must offer an obvious route Home. Authentication, signup,
  confirmation, recovery and error pages must not become dead ends. Focused
  practice uses the existing safe close/save path, preserving draft and position;
  dialogs may dismiss to their underlying page. Returning Home never silently
  discards practice or attaches guest work to an account.


## Single practice product - owner confirmation (25 October 2026)

- There is no separate guest mode or welcome/Try entry. Practice opens at Home
  without requiring sign-in; accounts add saving/sync. Existing internal storage
  namespaces may retain historical names to preserve data.
- Browser Back/Forward follows actual main-page, practice and authentication
  navigation. Preserve drafts/feedback/session position; never trap Back at Home.
- Legacy issues/PRs/milestones and the old Phase 0-4 Product Roadmap board were
  retired with owner authorization. Do not reopen or recreate them from imported
  roadmaps; use the current reassessment and monorepo issue #20.


## Temporary practice and simple learning choices - owner direction (25 October 2026)

- The owner-approved simplification plan supersedes persistent signed-out resume
  and the previous prohibition on contextual save/signup prompts. Use one practice
  UI; unsigned work is temporary. Leaving unfinished practice requires an explicit
  save/sign-in, discard, or keep-practising choice. Never silently discard it.
- Saving/signup from that explicit action authorizes automatic transfer of that
  round only. Ordinary sign-in must not attach unrelated work. Preserve temporary
  work through failed/cancelled authentication and prevent cross-account retries.
- Keep B1/B2 and General German/German for work preferences, topic selection under
  Topics, and five-question new rounds; avoid legacy word-type switches.
- This implementation is WIP; use PROGRESS for blockers and rollout boundaries.

## Simple Settings — owner direction (28 October 2026)

- Apply the same grouped Settings on web and Android: visible account identity,
  language/appearance, and practice preferences. Keep technical explanations out
  of the normal learner flow.
- Remove learner-facing manual sync, data-saving, export and download controls.
  Keep internal persistence, account isolation and unfinished-practice protection;
  this does not authorize silent loss of work.
- Do not show the Grammar reference button inside Settings on either client.
- Owner adopted Astra's screenshot recommendations: compact, consistent Account,
  Language/appearance and Practice preferences groups. Sign out opens a compact
  Cancel / Sign out confirmation, not an expanding settings drawer. Keep deletion
  visually secondary.
- Language choices must explicitly say English / Deutsch rather than EN/DE
  toggles. Account preference selections persist immediately; no separate Save
  preferences button in the authenticated learner flow. Retain frozen retries on
  failure and existing account isolation.
- Android system Back dismisses settings dialogs first, returns nested settings
  to Settings, then Settings to Home. It must not exit the app from Settings or
  silently discard unfinished practice.
- Retain a clear request to delete the account and related learning data on both
  clients. The established verified-ownership support request is distinct from
  automatic identity deletion; do not claim a request was submitted merely by
  opening its instructions or enable unverified destructive automation.

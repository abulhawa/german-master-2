# German Master 2.0 plan of work

Agreed with the owner on 6 October 2026. Product baseline:
[blueprint.md](blueprint.md). Current implementation and release evidence:
[../../PROGRESS.md](../../PROGRESS.md).

## Working approach

Prioritize completing the product. Keep required verification focused on each
finished implementation slice; avoid repeating audits or acceptance runs
without a new change, failure or unresolved concern. Save broad validation and
polish for the complete product. Fix defects that block implementation or risk
user data as they arise. Existing security, accessibility, immutable content,
identity and release-authorization requirements continue to apply.

Use the established blueprint and existing drafts; do not restart the roadmap.
This plan assigns work, rather than claiming any feature or milestone complete.

## Division of work

ChatGPT prepares concrete content and design deliverables. Codex integrates
them into the actual repository and working application. "Codex work" means
work requiring this checkout and its development tools in our workflow; it is
not a claim that ChatGPT cannot write code or that Codex cannot author content.

| Work area | ChatGPT deliverables | Codex implementation |
| --- | --- | --- |
| Guest practice | Welcome copy, onboarding flow, signup/save prompts and screen specifications | Local guest starter session, provisional grading, storage and explicit, replay-safe transfer to an authenticated account |
| Learner screens | Home, Practice and Progress mockups; layout decisions; English/German wording; empty, loading and error states | Responsive web screens, native Android screens, navigation and accessible interactions |
| German content | Refine existing exercises and variants; accepted answers, hints, explanations, rubrics and topic sequences | Convert to repository format, validate contracts, integrate immutable revisions and publication tooling |
| Account features | Signup, confirmation, recovery, sign-out and deletion wording and flows | Authentication, email-provider integration, password recovery and account cleanup |
| Product completion | Help text, onboarding guidance and remaining product copy | Backend/client integration, offline behavior, focused verification, builds, commits and authorized releases |

ChatGPT's German review is editorial assistance. It does not replace the
independent human approval required by the applicable content/release gates.
Content preparation does not authorize publication. Credentials and external
account setup use protected channels; never include secrets in handoff files.

## Immediate work streams

1. **ChatGPT: content and learner design.** Refine the existing 30-target,
   60-variant draft and prepare concrete learner-screen designs based on the
   blueprint. Return usable files, screen specifications and copy.
2. **Codex: guest practice.** Implement "Try German Master" without the current
   sign-in wall. Keep guest evidence local/provisional. Offer explicit saving
   after sign-in, with server revision validation, ownership isolation and
   replay-safe attachment. Do not create client-owned confirmed mastery.
3. **Codex: integration.** Integrate the prepared learner designs and content,
   then finish remaining account and offline/client features within the
   established scope. Track the actual remaining work in PROGRESS.md.
4. **Completion pass.** Once the product features are implemented, perform
   broader end-to-end validation and improvements, recording real evidence
   and retaining outstanding human/external release gates.

SMTP configuration remains necessary for public signup and delivered-email
confirmation. It can proceed when credentials/access are safely available and
does not block guest-practice implementation. The dependency remediation is
already pushed; deployment status remains a separate checkpoint item.

## ChatGPT handoff inputs and outputs

Supply these existing sources through an available file/connector workflow;
local paths alone do not give a separate ChatGPT chat access:

- `docs/product/blueprint.md`
- Relevant files under `docs/design/` and the established design tokens
- `content/drafts/initial-30.json`
- `content/drafts/README.md` and `content/drafts/REVIEW.md`
- `contracts/v2/schema.json` when preparing structured exercise data
- Current learner screenshots when preparing screen designs

Content output should preserve existing target/revision identifiers, clearly
mark proposed changes and review status, and include accepted answers,
explanations, hints and rubric constraints in the existing schema. Codex will
assign new immutable revisions where needed during integration.

Design output should cover screen structure, navigation, primary actions,
responsive behavior, English/German copy and relevant empty/error/loading
states. Identify material departures from the blueprint explicitly.

No new chat, delegation, deployment or publication is created by saving this
plan. The next implementation slice is guest practice; progress and exact
handoff actions belong in PROGRESS.md.

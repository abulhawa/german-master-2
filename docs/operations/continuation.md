# Continuing the German Master renovation

## Owner workflow

Open `<repository>` in a new Codex chat and say:

> Continue the German Master renovation from where we stopped.

If the chat starts outside the repo, use:

> Continue German Master 2.0 in <repository>. Read AGENTS.md and PROGRESS.md, then implement the next unfinished slice.

The repository contains the durable state. A new chat need not have access to the old conversation to understand the product, current implementation or next action.

## Current shared priority — 9 October 2026

Commit `813c779` is pushed to `main`; Web, Android, accessibility, safety, CodeQL and Vercel statuses passed for that exact revision. Android Studio versionCode/versionName remains owner-managed. PR #12 holds the subsequent v2-only native launch/release-configuration change, including Android Studio public configuration and a production-only release API origin. It remains a review branch until the latest-head checks pass; no release is authorized by that change.

Latest design implementation: Android now uses the web-inspired blue-panel Home and both clients align practice with the revised low-typing S04 concepts. Local builds, tests and Pixel/light/dark/English/German/enlarged-text evidence are recorded in [design alignment](../design/study-alignment-2026-10-09.md) and the newest checkpoint. Next: authorized hosted verification and final owner design/manual web accessibility acceptance. Do not revert Home to the older October 3 concept or recreate completed low-typing controls.

Latest owner decision: **TalkBack acceptance is completed for now**. Further TalkBack testing/improvements are deferred; do not reopen it as a blocker. The native next-question focus fix passes full local Android checks and needs hosted verification after an authorized push. Finish final both-client design/web accessibility acceptance before closing M1. Earlier TalkBack pending references below describe historical evidence limits and are superseded by this accepted scope.

Owner decision: close M0–M6 one by one. M0 is complete with [verified private recovery evidence](m0-closure.md). M1 is the only active milestone; M2–M5 are queued with partial work preserved, and M6 is not started. Do not open another milestone merely because the active milestone has an external blocker.

All 30 B2 draft targets / 60 variants now use authored single-slot gap choices at revision 2 in the unpublished candidate. Root and Android suites, converted-content client tests and hash-bound GPT-6 AI editorial validation pass locally; all hosted Actions and Vercel checks pass at PR #11 head `d9715d0`. A connected physical phone has partial B2/large-text runtime evidence. Exact next action: finish physical TalkBack/keyboard and manual web screen-reader acceptance, and record final both-client design acceptance. See [9 October acceptance evidence](m1-acceptance-2026-10-09.md). Publication remains separately authorized.

The completed B1 preview at `f6b4087` covers 20 targets/40 revision-2 exercises. The owner waived mandatory independent human German review on 9 October; ChatGPT GPT-6 editorial review is the content gate. Real physical-device Android acceptance remains open. B1 prepositions already use MCQs and B1 ordering already uses word tokens; do not recreate that conversion. See [candidate preview evidence](low-typing-candidate-preview.md) and [current milestone gates](milestone-gates.md).

The owner accepts laptop backups and personally holds/signs Android keys in Android Studio. Source identity/version is `com.germanverbmaster.android`, 29 / 0.2.08; the owner checks the live Play maximum before release. Exact recovery locations and machine paths stay in ignored/private manifests. The existing legacy and renovated Supabase projects remain separate and unchanged; no third project is needed.

This is a hard reset with no existing learners to migrate. Preserve identity/signing, provenance, immutable revisions and operational recovery. The blueprint defines exit criteria; the current gate table and newest PROGRESS.md checkpoint define the active work. Older entries are historical. Release, publication and production mutation boundaries remain unchanged.

## Agent workflow

1. Read root `AGENTS.md` and `PROGRESS.md`; inspect scoped instructions for the files to change.
2. Verify the checkout identity, branch, local changes and recent commits. Check the remote state when relevant. Do not automatically pull over dirty work or discard anything to match the checkpoint.
3. Reconcile progress against the actual code and verification records. Skip completed work and correct stale claims. Read the blueprint sections and ADRs relevant to the current slice.
4. Briefly tell the owner where the project stands and what the next slice will deliver, then implement it. Routine choices should use the accepted product baseline; ask only for a missing decision or permission that genuinely blocks dependent work.
5. Run appropriate checks. Keep normal tests offline and production isolated. An external blocker does not justify abandoning independent useful work.
6. Finish one coherent, reviewable slice before broadening scope. For complex work, maintain a short slice plan with progress, discoveries and remaining actions so interrupted work can resume.
7. Update `PROGRESS.md` before handoff, including partially completed work. Commit/push verified changes when authorized, then report the concrete outcome, checks, material limitations and next slice.

The owner wants implementation on a continuation request, not repeated planning or a menu of next actions. The roadmap remains the destination; the checkpoint is the route from the current code.

## Checkpoint accuracy

Use Done only when the acceptance evidence exists. Distinguish implemented, tested locally, passed hosted CI, deployed and validated with learners. The source-of-truth blueprint is not evidence that its features have been implemented. Never claim a milestone is complete because only its easiest task was finished.

Retain partially completed work with clear reproduction steps and the exact next action. Replace stale next-action text after each slice. Small commits and durable files provide continuity even if a chat ends unexpectedly.

## Scope and approval boundaries

Resume ordinary code, tests, documentation, local isolated restructuring and repair within the renovation scope. Preserve the two originals and the public monorepo's secret hygiene. Live production cutover, destructive database operations, store publishing, billing changes and external messages still require authorization appropriate to that action. An approval boundary should stop only dependent work; continue independent implementation where possible.

## References

This workflow uses repository instructions and a living progress file, consistent with [OpenAI's AGENTS.md guidance](https://learn.chatgpt.com/docs/agent-configuration/agents-md) and [its guidance for durable execution plans](https://developers.openai.com/cookbook/articles/codex_exec_plans). The saved files supply context; they do not guarantee that an unrelated chat on another machine can discover this repository without being pointed to it.

# Continuing the German Master renovation

## Owner workflow

Open `C:/Projects/german-master-2` in a new Codex chat and say:

> Continue the German Master renovation from where we stopped.

If the chat starts outside the repo, use:

> Continue German Master 2.0 in C:\Projects\german-master-2. Read AGENTS.md and PROGRESS.md, then implement the next unfinished slice.

The repository contains the durable state. A new chat need not have access to the old conversation to understand the product, current implementation or next action.

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

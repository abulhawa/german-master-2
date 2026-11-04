# Enrichment release: reviewed equals editorially approved

## Direct production authorization — 4 November 2026

Owner explicitly instructs: "no staging, directly to production". This supersedes
the staging requirement for the reviewed enrichment rollout and cancels the pending
proposal to pause another service. Use the existing clean production project and
Vercel project only. Retain local/hosted checks, exact immutable-row import
verification, production smoke and additive rollback preservation. No paid
project, billing change, unrelated service interruption or Android Play release
is authorized. Live production results belong in the release receipt/checkpoint;
the frozen candidate manifest remains preparation evidence, not a live-status feed.

Owner direction, 4 November 2026: **a completed, verified GPT-6 question-by-question editorial review is sufficient editorial approval for the exact reviewed content.** Do not require a second independent human review or an additional owner approval ceremony for each batch. This supersedes older prose requiring separate human editorial approval. It does not change historical evidence, misrepresent the reviewer as human, or certify CEFR/rights.

The owner also authorized **incremental integration and deployment of reviewed content**, with editorial review continuing on `content/enrichment-review`. The review writer owns that branch exclusively. The integration writer owns this separate release branch. Never cherry-pick incomplete review-branch state onto `main`.

## Interpretation of approved

* A source question is **editorially approved** when its exact catalog SHA-256 is pinned, the corresponding findings record covers that question and all offered options, its batch is `complete`, the saved review validators pass, and there is no unresolved **material** defect in the version to ship.
* `no-blocker-found` findings and optional/nonblocking precision or style suggestions are approved **as reviewed**. Optional suggestions must not delay publication or be silently applied.
* Material-error findings (ambiguity, wrong key, incorrect grammar/explanation, misleading guidance or demonstrable source-fact error) approve the *review finding*, not the defective original text. Ship the exact reviewed correction when its immutable preconditions and recheck evidence pass; otherwise exclude that question/target from the release candidate. Never publish a known bad item just to make counts larger.
* An already rechecked and structurally verified correction can be accepted as part of the release without another human sign-off. Unrechecked corrections with a material defect remain excluded until rechecked. Source authoring artifacts stay immutable.
* User approval is for the **AI editorial-review standard**, not a false claim of human review, language certification, copyright clearance or deployment/test success.
* The word `approved` in an integration manifest must identify this owner-approved editorial standard and cite evidence (review commit, source commit/hash, question or target identity, applied correction hash when present). Preserve the original `review.status: pending` in frozen source inputs; record release approval only in the *derived* candidate.

## Release workstreams

1. **Independent editorial review** continues from the last verified queue position on `content/enrichment-review`, with a single writer and expected-head protection. The reviewer does not integrate, merge or deploy.
2. **Integration and deployment** use a separate branch based on current `main`. In each wave, choose only completed, hash-verified reviewed questions; apply eligible rechecked corrections without changing source catalogs; skip unresolved material defects; assign accurate topic, general/work and lexical/grammar metadata. Prepare a new immutable additive release, retain prior revisions and learner progress, and prepare the same bounded offline content behavior for web and Android.
3. Validate the candidate against the unchanged canonical content/grading rules, revision/manifest identity, safe repeatable database import, 125-question saved-session bounds, and guest/authenticated practice. Run production-equivalent smoke and rollback checks. Only then merge/deploy that wave. **Never conflate editorial approval with technical release readiness.**

## First wave

Prioritize the original 49 reviewed batches (1,536 question assessments) rather than waiting for all 175. Continue admitting later extension batches only after their own review artifacts and validations are complete. At the checkpoint inspected on 4 November, extension batches 0050–0078 add 1,052 reviewed question assessments; candidate inclusion remains selective and counts will change as review progresses.

Known technical work: `category: vocabulary` is not a valid mapping in the existing basic builder; lexical targets require explicit kind and topic/context instead of a fallback to grammar. Both clients currently concatenate eligible guest packs into a session and must use bounded selection/paging before any large shared offline bundle is enabled. Do not bypass either issue by reusing a published release ID or increasing the 125-question cap.

## Release decision

**Editorial review completion automatically grants editorial approval under this owner's policy.** No separate human editorial gate. The only remaining publication gates are resolving known material defects for the exact version selected, mechanical correctness/metadata/identity, functional and data-safety checks, and a reversible deployment. Keep other app issues, cosmetic changes, redesigns, and unfinished review batches outside this scope. This file is policy and preparation, **not** proof that enrichment has been integrated or deployed.

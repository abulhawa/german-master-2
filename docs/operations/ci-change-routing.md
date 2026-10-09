# CI change routing

Application workflows use ordered GitHub `paths` filters for both pull requests and pushes to main. Manual dispatch remains available for a full run of any workflow.

| Changed inputs | Automatic application checks |
| --- | --- |
| Root docs, mockups, Markdown/MDX, web/Android documentation, Android store listing | None |
| Web application, tests, build configuration | Web; accessibility for its learner fixture inputs |
| Android application, tests, Gradle configuration | Android |
| Contracts, generated design tokens, learning packages, API services, content, database | Web, Android and web accessibility |
| Root package manifest/lockfile or npm configuration | Web, Android and web accessibility |
| A workflow file | That workflow |
| Release guardrail inputs, including `docs/operations/v2-staging-plan.json` | Release readiness on pull requests |

The negative documentation patterns must follow the positive patterns. A mixed code-and-documentation change still runs the checks for the code. Test fixtures and app assets remain included; only documentation directories and Android store-listing assets are excluded, not all images or JSON files. Shared inputs intentionally fan out because the Android HTTP checks exercise the API and learning engine.

Repository safety continues scanning every change: secrets can appear in documentation too. GitHub's separately configured CodeQL default setup is not controlled by these workflow files. Vercel deployments are also separate from GitHub Actions test routing.

On 9 October 2026, read-only GitHub inspection reported main as unprotected and no repository rulesets. If required checks are introduced, do not require a workflow that may be skipped by these path filters: GitHub leaves such required checks pending. Use an always-running routing/check job with conditional test jobs before enabling that requirement. See [GitHub's path-filter documentation](https://docs.github.com/en/actions/reference/workflows-and-actions/workflow-syntax#onpushpull_requestpull_request_targetpathspaths-ignore).

Local verification parses the workflows and exercises documentation-only, mixed, per-client, shared-input and workflow-change cases. Hosted behavior is verified after an authorized push; local checks do not claim a hosted run.

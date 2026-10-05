# Dedicated German Master v2 staging preparation

The owner selected the new project **german-master-v2-staging**, **ali's Org** (`qyivvafnnfycqnbexhfq`), **Frankfurt / eu-central-1**. On 5 October 2026 the read-only Supabase cost quote was **0 per month**. Nothing has been created or deployed. [Prepared configuration](v2-staging-plan.json) contains no credentials and deliberately has no project reference.

## Concrete approval boundaries

The next external action is creation of this one new project. Organization and region selection is not creation approval. Obtain explicit creation/cost approval, then use Supabase cost confirmation and creation tools. Verify the returned project name, organization, region and ready status before saving its non-secret reference. Never link the legacy project, branch from it, copy its keys or read/mutate its learner data.

Project creation does not authorize schema application, credential provisioning, deployment, content publication or production cutover. Prepare and separately review each concrete operation. Keep secrets in the approved environment/credential channel, never chat, source, Git or logs. Publishable client keys and server-only database/service secrets have different scopes; never embed elevated keys in either client.

## First database

Use [clean baseline 1](../../db/baseline/v2.sql) for an empty new database. Do not apply development migrations 001–010 to staging. The baseline is deliberately non-idempotent: an existing `gm` schema must cause failure, not replacement. It creates no auth identities, learner rows, catalog publication or fixture seeds. The baseline adapter initialization does not import draft content. The local launcher still creates disposable fixture schemas through the development chain.

Local PGlite verifies column/constraint/trigger parity, empty catalog, RLS/default access denial and owned pack → Skip → partial completion → export → deletion/replay with shared revision immutability. This is not Supabase advisor, multi-connection PostgreSQL or live identity evidence. Docker and Supabase CLI are unavailable on this host. Before applying the baseline, add and review scoped backend roles/policies, test independent connections and ownership enforcement, and run advisors against the approved isolated environment. The `gm` schema must remain outside the Data API, with no `anon`/`authenticated` client grants. [Supabase Data API security guidance](https://supabase.com/docs/guides/api/securing-your-api).

## Auth integration to implement

Use the new project's supported token verification (`getClaims` or online `getUser`) to derive the subject, checking the intended issuer/project and audience. Never accept fixture credentials, the subject precondition header or user-editable metadata as authentication. Resolve the subject and identity generation before mounting the existing partitioned client stores. Refresh/expiry/account replacement must invalidate captured transports without silently attaching another subject's work. [Token verification](https://supabase.com/docs/reference/javascript/auth-getclaims), [online user verification](https://supabase.com/docs/reference/javascript/auth-getuser).

For sensitive writes/deletion, verify the token's `session_id` against the current owned auth session; JWT signature/expiry alone does not prove that a session still exists after sign-out. Implement and verify reauthentication, revocation and owned auth-identity deletion separately from learner-data tombstones. Do not enable real deletion until retention, backup/log handling and recovery have been reviewed. [Supabase session lifecycle](https://supabase.com/docs/guides/auth/sessions).

Next independent implementation: the network PostgreSQL adapter and scoped role policies, followed by both clients' verified auth provider bindings. Real account lifecycle acceptance and deployment remain gated by the new environment and the separate approvals above. The current fixture resume buttons explicitly resume the same public local learner and do not claim production sign-in.

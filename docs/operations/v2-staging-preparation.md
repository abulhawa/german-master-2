# Dedicated German Master v2 staging preparation

The owner selected the new project **german-master-v2-staging**, **ali's Org** (`qyivvafnnfycqnbexhfq`), **Frankfurt / eu-central-1**. On 5 October 2026 the read-only Supabase cost quote was **0 per month**. After explicit owner approval, this project was created on 5 October 2026 and returned ACTIVE_HEALTHY with project reference `zgmyrpzwgtydwlzponih`. [Prepared configuration](v2-staging-plan.json) contains only non-secret identifiers. No credentials were retrieved, schema applied or application deployed.

## Concrete approval boundaries

Project creation/cost approval was granted and executed using Supabase cost confirmation and creation tools. The returned name, organization, Frankfurt region and healthy status matched the approved operation. Further external operations require their separate approvals. Never link the legacy project, branch from it, copy its keys or read/mutate its learner data.

Project creation does not authorize schema application, credential provisioning, deployment, content publication or production cutover. Prepare and separately review each concrete operation. Keep secrets in the approved environment/credential channel, never chat, source, Git or logs. Publishable client keys and server-only database/service secrets have different scopes; never embed elevated keys in either client.

## First database

Use [clean baseline 1](../../db/baseline/v2.sql) for an empty new database. Do not apply development migrations 001–010 to staging. The baseline is deliberately non-idempotent: an existing `gm` schema must cause failure, not replacement. It creates no auth identities, learner rows, catalog publication or fixture seeds. The baseline adapter initialization does not import draft content. The local launcher still creates disposable fixture schemas through the development chain.

Local PGlite verifies column/constraint/trigger parity, empty catalog, RLS/default access denial and owned pack → Skip → partial completion → export → deletion/replay with shared revision immutability. This is not Supabase advisor, multi-connection PostgreSQL or live identity evidence. Docker and Supabase CLI are unavailable on this host. Before applying the baseline, add and review scoped backend roles/policies, test independent connections and ownership enforcement, and run advisors against the approved isolated environment. The `gm` schema must remain outside the Data API, with no `anon`/`authenticated` client grants. [Supabase Data API security guidance](https://supabase.com/docs/guides/api/securing-your-api).

## Auth integration to implement

Use the new project's supported token verification (`getClaims` or online `getUser`) to derive the subject, checking the intended issuer/project and audience. Never accept fixture credentials, the subject precondition header or user-editable metadata as authentication. Resolve the subject and identity generation before mounting the existing partitioned client stores. Refresh/expiry/account replacement must invalidate captured transports without silently attaching another subject's work. [Token verification](https://supabase.com/docs/reference/javascript/auth-getclaims), [online user verification](https://supabase.com/docs/reference/javascript/auth-getuser).

For sensitive writes/deletion, verify the token's `session_id` against the current owned auth session; JWT signature/expiry alone does not prove that a session still exists after sign-out. Implement and verify reauthentication, revocation and owned auth-identity deletion separately from learner-data tombstones. Do not enable real deletion until retention, backup/log handling and recovery have been reviewed. [Supabase session lifecycle](https://supabase.com/docs/guides/auth/sessions).

The network PostgreSQL adapter, scoped role policies and server auth verifier are now implemented locally. Next independent implementation: both clients' verified auth provider bindings and reviewed-catalog runtime routing. Real account lifecycle acceptance and deployment remain gated by the new environment and the separate approvals above. The current fixture resume buttons explicitly resume the same public local learner and do not claim production sign-in.

## Prepared backend integration — 5 October 2026

Network adapter, request-scoped server composition, scoped learning SQL and online server auth verification are now implemented. See [ADR 028](../adr/028_network_backend_and_verified_auth.md). The adapter requires verified TLS and an existing baseline; it cannot initialize fixtures. Both clients remain unchanged public fixtures, and reviewed-catalog runtime selection is not yet integrated.

Concrete external operations still requiring separate approval:

1. Provision server-only login credentials inheriting `gm_backend`, a separate auth-verifier login inheriting `gm_auth_verifier`, and the new project's publishable auth key through an approved secret channel. Retrieve the actual connection endpoints and CA certificate; never infer a pooler host or place credentials in source/chat. Verify that neither login owns schema objects, bypasses RLS or inherits elevated roles.
2. Inspect only the isolated project's schema/roles and confirm the current `auth.sessions` id/user_id/not_after columns and privilege behavior. Apply `db/baseline/v2.sql`, then `backend-access.sql` and `auth-session-access.sql` in one reviewed transaction after confirming the `gm` schema and roles are absent. Fail on existing objects; do not drop or reset anything. Leave `gm` outside the Data API. No catalog or learner rows are imported.
3. Run advisors and actual independent-connection ownership/content denial, concurrent write/replay, rollback/expiry and representative load acceptance. Isolated PGlite and adapter mocks are not substitutes for these checks. Deployment remains a separate operation.

`createSupabaseAuthenticate` uses the publishable key and a mandatory `currentAuthSession` callback using the separate auth-verifier database. `createNetworkApi` obtains a fresh subject-scoped learning store after authentication, exports no credential-bearing configuration and starts no listener. Real learner deletion is intentionally disabled there pending host-auth revocation/deletion, reauthentication and retention/recovery integration. No external operation above was executed.

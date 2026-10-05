# First isolated staging installation review

6 October 2026. Target: `german-master-v2-staging`, `zgmyrpzwgtydwlzponih`,
organization `qyivvafnnfycqnbexhfq`, Frankfurt `eu-central-1`.

Read-only connector evidence confirms ACTIVE_HEALTHY, PostgreSQL 17.11,
zero `gm`/`gm_privacy` schemas and zero of the four proposed private roles.
`auth.users.id`, `auth.sessions.id/user_id` are UUIDs;
`auth.sessions.not_after` is timestamptz. Both auth tables enable RLS.
The security advisor returns no notices for the current empty project;
this does not verify the proposed installed schema.

## Proposed operation requiring owner approval

Apply the already consolidated clean baseline and four private subsystem/access
files below together in one transaction. Recheck target identity and absence
immediately before applying. Existing schemas or named roles must fail;
never replace/drop objects or use development migrations.

1. `db/baseline/v2.sql`
2. `db/baseline/backend-access.sql`
3. `db/baseline/auth-session-access.sql`
4. `db/baseline/identity-deletion.sql`
5. `db/baseline/identity-verifier-access.sql`

Create private `gm` and `gm_privacy` structures, NOLOGIN non-bypass roles,
subject policies and column-limited server-only auth read policies. No auth
identity or learner/catalog data is created. Keep both private schemas outside
the Data API. The optional privacy subsystem is part of this initial baseline,
not an upgrade of an existing v2 environment.

After the transaction, read metadata/grants/policies, confirm empty private
schemas and run security/performance advisors. This approval covers only
that installation and its read-only verification. Separately provisioned login
credentials, publishable/admin keys, live lifecycle fixtures, catalog
publication, deployment and release still need their existing authorization.

The SQL files have local PGlite policy/ownership/role acceptance. Actual
independent-connection contention, representative load, live provider/storage
identity deletion, retention and backup/restore remain open. Login credentials
must stay in an approved secret channel, never source, chat or tool output.

Owner approved this exact installation on 6 October 2026. It was executed successfully as `v2_clean_initial_baseline` using a single locally validated atomic DO block. No development migration chain was applied.

## SQL provenance

| File | SHA-256 |
|---|---|
| auth-session-access.sql | a710601e25d382d564c4e2e12d9c5a4dfb5928fc195d3edebb280b98e610acb3 |
| backend-access.sql | 5c4847b7621ed06572649a28964184e7114af5f90f00a1da18869777009c30ab |
| identity-deletion.sql | b93a07b44ca3aa9d23fc116013c50d1a2e3ba3b045bc6e2d9797e7eb11fd8826 |
| identity-verifier-access.sql | 999c25c93e88e89b9e3d6461b7741737b3b6a3072cd914ab0a0a2a13ff864923 |
| v2.sql | 19f4b6747bead6815b46c07a8f5ce4fd835cc172f64939aa5388f3b6368572f4 |

## Actual post-install evidence

Metadata verifies baseline 1, 29 private tables all with RLS, four NOLOGIN,
non-superuser/non-bypass roles and no anon/authenticated schema usage.
Auth verifiers have their allowed columns without table-wide SELECT. Targets,
releases, learners and deletion jobs remain empty. No credentials, login
accounts, auth identities or catalog publication were created.

Advisors are not clean: one security warning for
[mutable function search_path](https://supabase.com/docs/guides/database/database-linter?lint=0011_function_search_path_mutable)
on `gm.reject_mutation`, 19 performance warnings for
[per-row owner-policy settings](https://supabase.com/docs/guides/database/database-linter?lint=0003_auth_rls_initplan),
and eight [unindexed foreign keys](https://supabase.com/docs/guides/database/database-linter?lint=0001_unindexed_foreign_keys).
Unused-index notices are expected in this empty database; do not remove indexes
because they have no traffic yet. Prepare the minimal corrective SQL and obtain
separate authorization before another live schema mutation. Representative
load and independent-connection/provider acceptance remain open.

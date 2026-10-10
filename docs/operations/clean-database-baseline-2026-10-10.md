# Clean database replacement preparation — 10 October 2026

The owner requested one initial schema, a fresh Supabase project, no learner-data
migration or compatibility layer, and verification before a separately controlled
production cutover. This supersedes the earlier decision that no third project
was requested. The chosen organization is the existing organization; its current
plan is Free. No paid usage or plan change is authorized.

## Fresh read-only inventory

The current v2 production project returned these counts:

| Relation | Rows |
| --- | ---: |
| auth.users | 2 |
| gm.learner_profile | 2 |
| gm.practice_session | 2 |
| gm.attempt | 2 |
| gm.learner_target_state | 2 |
| gm.accepted_evidence | 2 |

The owner explicitly confirmed that both accounts and their progress are
disposable development/test data. These counts establish that the environment
is not empty; they do not independently establish account purpose. No account
or record was removed. Recheck counts immediately before cutover and resolve
any newly accumulated data before proceeding.

## Prepared SQL

`db/0001_initial_schema.sql` combines the current clean product schema, immutable
triggers, indexes, fixed function search path, backend ownership policies,
privacy worker, restricted Auth verifier grants/policies and private invoker
views. It includes no content, learner rows, login credentials or provider
secrets. Supabase must provide its managed Auth schema first. Apply only to a
fresh project, never to the existing production project.

Local PGlite acceptance uses synthetic provider tables to check installation,
empty learner/content tables, baseline version, absence of the development
ledger, RLS, restricted non-login roles, invoker views and denial of backend
content writes. This is local SQL evidence, not hosted Supabase or either
client's complete lifecycle acceptance.

The development migration runner and split baseline inputs remain in place
pending the full consolidation conversion. The new file is prepared replacement
SQL; the repository migration consolidation is not complete. Do not claim a
single authoritative runtime path while these duplicate paths remain.

## Provisioning blocker and exact continuation

The connector's advertised `get_cost` call returned UNAVAILABLE:
`MCP tool get_cost was not returned by tools/list`. Its project-creation contract
requires this cost check and confirmation before creation. No replacement
project was created and no cost was confirmed. Restore that capability or use
an owner-selected provisioning path; do not pause/delete existing projects to
make room or enable billing as a workaround.

1. Complete consolidation of fixture initialization, tests and release tooling
   onto the initial schema; remove development upgrades and duplicate SQL.
2. Obtain the replacement project's actual cost/quota eligibility, then create
   it in the selected organization without a paid plan change.
3. Apply the baseline as the sole application migration; verify catalogs,
   functions, indexes, policies, grants, advisors and restricted real connections.
4. Provision separate scoped server logins privately. Configure Auth confirmation,
   SMTP, callback allowlists and environment secrets for isolated verification.
   Do not copy production credentials into tracked artifacts.
5. Explicitly establish the reviewed content release required for practice;
   schema installation alone supplies no exercises. Keep content publication
   authorization explicit.
6. Verify registration/confirmation, login, recovery, practice, persistence,
   offline synchronization, export and deletion on web and Android against the
   replacement. Record actual device/browser and provider-delivery evidence.
7. Present the verified cutover target, configuration changes and rollback
   procedure for separate production approval. Retire the old project only
   after successful cutover verification and explicit retirement authorization.

M2 remains the only active milestone. No production cutover, project retirement,
Auth configuration mutation, content publication, deployment or store release
was performed in this preparation.

## Hosted installation follow-up

The owner created `german-master-v2-clean` (`sqgjsmiaprsuilcjmaav`) in Frankfurt
in the selected Free organization. It is ACTIVE_HEALTHY. Before installation,
Auth users were zero, application schemas absent and the migration ledger absent.
Applied the exact prepared initial SQL once. Supabase's ledger contains one
entry: `20261010172945`, named `0001_initial_schema`; the application baseline
is 1. The prior provisioning blocker is resolved through owner dashboard action.

Hosted inspection verifies 29 application tables, all with RLS, 29 policies,
61 indexes, 19 immutable triggers, three private invoker Auth views, four
restricted NOLOGIN parent roles, fixed empty mutation-function search path and
no learner-role schema access. Auth users, learner profiles and targets remain
zero. Security advisor returned no findings.

Performance follow-ups: 19 [RLS initialization-plan warnings](https://supabase.com/docs/guides/database/database-linter?lint=0003_auth_rls_initplan),
eight [unindexed foreign keys](https://supabase.com/docs/guides/database/database-linter?lint=0001_unindexed_foreign_keys),
and 16 [unused indexes](https://supabase.com/docs/guides/database/database-linter?lint=0005_unused_index).
Unused-index notices on an empty database do not justify removing indexes.
These findings remain to assess alongside the active M2 load/query-cost work.

Scoped server login provisioning, Auth/SMTP/callback setup, replacement API/client
configuration, content setup and complete both-client lifecycle acceptance remain
unfinished. The existing production project remains unchanged. This installation
is not production cutover or readiness acceptance.
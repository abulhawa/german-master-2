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
## Consolidation and scoped runtime verification

The repository now uses only `db/0001_initial_schema.sql` for the v2 schema.
Development migrations, split SQL and the standalone remediation were removed.
Fixtures install the provider-independent section, seed unpublished foundation
content and evidence identities atomically, and reopen baseline 1 without
replaying seeds. Development schemas fail with a recreate-disposable-fixture
instruction. Role/Auth/privacy tests and release tooling read the same source.
Legacy imported SQL remains preserved provenance rather than a v2 migration path.

Four separate restricted server logins were generated on the replacement. Their
passwords were transferred encrypted into ignored private storage; server key
configuration was likewise transferred privately. No credentials are tracked.
Independent TLS connections passed for learning, Auth verification, privacy and
identity observation. A rollback-only learning probe verified own-profile access,
cross-subject hiding and denial of schema creation. The connector itself could
not assume the backend role; restricted real connections supplied this evidence.
The unsuccessful connector probe rolled back and left zero learner profiles.

Server-key read-only Auth administration returns HTTP 200 with zero users.
Provider settings confirm email confirmation required, email provider enabled
and signup enabled. Replacement URL configuration still has the default local
site URL and no explicit redirect allowlist; it is not production-ready.

The API supports explicit `replacement-verification` mode bound to the new
project. Its compiled loopback runtime connects and returns HTTP 401 for an
unauthenticated profile request. This is composition/auth-denial evidence,
not signed-in or practice lifecycle acceptance. Web accepts the explicitly
selected replacement project. Android debug configuration accepts it, while
release configuration retains the current production project and canonical
origin. No deployed production configuration or release was switched.

SMTP preparation found that the existing production sender uses Resend at
`smtp.resend.com:465`, with a 60-second per-user interval. Its stored password
is hidden and cannot be recovered from the dashboard. Matching non-secret
replacement fields were prepared, unsaved. Password entry/submission requires
owner handoff under browser credential rules. No SMTP settings changed on
production, no new email key created and no real test email sent.

Remaining work: owner SMTP credential handoff; exact isolated callback/API origin
setup; replacement content setup with explicit publication authorization;
signed-in registration/recovery/practice/offline/export/deletion journeys on both
clients; independent-connection contention/load and advisor remediation. The
replacement remains empty. Cutover and retirement remain separate controlled
steps. M2 remains active, with no later milestone advanced.

Verification follow-up: root offline dependency installation, generated/type
checks and production learner/API builds pass. Full backend: 194 tests; full
Node 24 web: 423 tests plus three new focused configuration tests; HTTP: 20.
Staged secret scan reports no findings. Android lint/debug assembly pass with
replacement public config and a non-routable verification origin. The full native
unit run passed 167/168; one obsolete revision bound rejected authored B1 revision
5. Its exact authored id/revision lookup remains, and the corrected real-HTTP/
AtomicFile restart test passes on targeted rerun. This does not claim a fresh
168-test aggregate run or native device/live backend lifecycle acceptance.
The native production-release guard rejects replacement-project configuration.

The owner reports creating a new Resend sending-only key restricted to the
verified sender domain and saving replacement SMTP. Verify persistence next;
no delivered confirmation/recovery email is yet claimed. Existing keys remain.

Reload verification returned custom SMTP disabled despite the owner's completion
report. Saving did not persist. The replacement non-secret SMTP form was restored;
owner must enter the new Resend key into Password and submit Save changes. The
stored production SMTP secret was not retrieved or changed. No SMTP delivery
acceptance is claimed. All completed code is committed before this handoff.

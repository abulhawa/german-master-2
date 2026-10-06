# Actual staging role metadata evidence

6 October 2026, dedicated project `zgmyrpzwgtydwlzponih` only. Collected through
read-only connector SQL. No credentials, login creation, schema/data mutation,
auth identities or content publication were involved.

All four roles (`gm_backend`, `gm_auth_verifier`, `gm_identity_verifier`,
`gm_privacy_worker`) are NOLOGIN, non-superuser, non-BYPASSRLS and cannot create
databases/roles or replicate. They own no relations. No role has CREATE on the
two private schemas; anon/authenticated have neither USAGE nor CREATE there.

| Role | Actual catalog privilege result |
|---|---|
| Backend | USAGE on gm only; no auth/private-worker schema access; no exercise revision INSERT/UPDATE/DELETE; no deletion-tombstone DELETE |
| Session verifier | SELECT only id/user_id/not_after on auth.sessions; no table-wide SELECT, session secret-column read, auth writes or gm usage |
| Identity verifier | SELECT only auth.users.id and auth.sessions.user_id; no table-wide user/session SELECT, password read, user DELETE or gm usage |
| Privacy worker | USAGE on gm_privacy only; column UPDATE on completed_at/recovery_until/recovery_hash; no table-wide UPDATE or subject UPDATE, learning/auth schema usage |

These are actual effective privilege checks (`has_schema_privilege`,
`has_table_privilege`, `has_column_privilege`) and catalog grant inspection.
They do not prove row ownership against populated data or successful requests
over independently authenticated backend connections.

## Runtime blocker observed

Four read-only transactions attempted `SET LOCAL ROLE` for the four private
roles. All failed with PostgreSQL 42501 permission denied to set role; no role
query executed. Follow-up membership inspection explains this without a code
change: connector current_user is `postgres`; for each private role its
membership has ADMIN TRUE, INHERIT FALSE, SET FALSE. There are no other members
of these roles. No membership/grant was changed to bypass this boundary.

Runtime role denial, owned A/B row isolation, independent-connection contention,
TLS/login behavior and representative load remain open. Use separately approved
dedicated scoped connections, not this administrative metadata result as a
substitute. Any login/credential/grant provisioning remains a separate approved
operation under [account acceptance](staging-account-acceptance.md).

This closes the installed-role metadata inspection subset of M2 least-privilege
evidence. It does not close the full least-privilege runtime requirement or M2.

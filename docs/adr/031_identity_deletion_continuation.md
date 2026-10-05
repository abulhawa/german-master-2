# ADR 031: Durable identity deletion preparation

Accepted for local implementation, 5 October 2026. No schema application, auth
mutation, credential retrieval, deployment or release is authorized here.

Owned learning deletion and authentication identity deletion are different
outcomes. Keep the existing `/v2/me` fixture receipt and tombstone semantics.
Configured clients and the network DELETE route stay disabled until the complete
host protocol and provider administrator are integrated and verified.

`IdentityDeletionService` persists a private job before external identity work.
Only fresh server-verified authentication for the captured subject can create a
job. The password adapter creates a new nonpersistent SDK client per proof,
performs password sign-in, then uses online user/current-session verification.
It closes that temporary session. Failed cleanup fails authorization. Neither
JWT issue time, refresh, client timestamps nor editable metadata proves fresh
authentication. Password and tokens are never persisted or returned.

The client must freeze a request UUID and a cryptographically random 32-byte
base64url recovery capability in its durable deletion marker before delivery.
The server stores only its SHA-256 hash. Different IDs/capabilities conflict
instead of replacing a job. Capability recovery returns only version, request
ID, pending/completed status and completion time; it cannot create or run a job.
The HTTPS host must carry capabilities in bounded request bodies, avoid
logging credentials/capabilities, impose rate limits, enforce origin/subject
checks and preserve explicit retry semantics.

The opt-in host now accepts versioned POST bodies at
`/v2/me/identity-deletion:begin` and `/v2/me/identity-deletion:status`.
Begin requires the existing verified session, a matching `X-Learner-Subject`,
explicit `delete_identity` confirmation and transient fresh password proof.
Status needs only the frozen request/capability; it never invokes a worker.
Both routes reject query parameters, use no-store responses and enforce an 8 KiB
body bound. Schema limits cap email/password/capability lengths in generated
TypeScript and Kotlin. Pending responses omit completion time; completed
responses require an identity-specific completion time.

The host uses a bounded 4096-entry socket-peer limit of ten requests per minute
(including failed requests), ignoring untrusted forwarded headers, and returns
429 with Retry-After. A proxy may share a peer and therefore share this limit;
multi-host operation still requires a shared upstream limit. Server body/header
timeouts are 15/10 seconds. This is not a provider-operation cancellation or
distributed worker lease. All unexpected errors return generic messages.

`serializedDeletionWorker` is a separate private callable, never a public HTTP
route. It serializes explicit deliveries in one process, stops an invocation on
failure and allows a subsequent explicit continuation. A recovery capability
cannot enqueue work. The composition host must deliver admitted jobs explicitly;
there is no automatic scheduler or complete operational dispatcher yet.
`createNetworkApi` enables the routes only with an explicitly supplied service;
default composition and legacy owned-data DELETE remain disabled. No client
control, credential lookup, schema installation or deployment is enabled.

An explicit private worker first commits existing owned learning removal and
its upload tombstone. It then observes provider identity/session state, uses
pre-delete subject-based revocation where supported, hard-deletes the identity
if still present, and observes again.
Only verified absence of both identity and sessions can persist a completed
receipt. Provider errors/timeouts leave a pending job and stop the invocation.
An explicit continuation observes state before another removal; accepted
deletion with a lost response can therefore complete without repeating removal.
The host must serialize worker invocations; multi-worker leases and live
provider administration are not implemented or claimed. No background retry
scheduler or public deletion control is enabled by this preparation.

The private `gm_privacy` subsystem has a distinct non-login, non-owner worker
role, no Data API/client grants, and no learning/auth privileges. Subject-scoped
learning removal retains its existing connection; provider administration is a
separate injected capability. The concrete Supabase adapter uses hard admin
deletion (`deleteUser(subject,false)`) and a separate column-limited read-only
identity/session verifier. It never interprets an admin error/timeout as
absence. Its hard deletion must remove sessions; remaining sessions fail
completion. Existing online user/session checks and the learning tombstone
deny issued JWTs even though their signatures can remain valid until expiry.
No learner JWT is persisted for a restart-time global sign-out. The adapter
accepts an explicitly supplied dedicated staging server secret only and never
retrieves one. Actual Supabase session/refresh-token cascade and storage-owned
object behavior need approved staging acceptance before enablement.
`identity-deletion.sql` and `identity-verifier-access.sql` are clean initial
setup, not an upgrade chain or applied schema. Their hashes join provenance.

Supabase auth tables have RLS enabled. Both verifier setups therefore pair
column-limited grants with explicit server-only read policies. The identity
observer checks applicable constant-true read visibility and rejects restrictive
policies; filtered rows are never proof of absence. This also fixes the earlier
session-verifier setup, whose grants alone would fail live authentication.
No policy is granted to a client role, and neither verifier gets auth writes.

Local engineering default: completed recovery capabilities expire after a fixed
30 days, with an explicit maintenance method removing only their hashes.
Reads/replays do not renew this deadline. Pending jobs remain recoverable;
minimal job identifiers/completion and owned-data tombstones are retained.
This is not an approved public retention/privacy policy. Logs, backups, auth
storage/provider behavior, retention durations and recovery review remain open.
Restores must preserve/reapply tombstones and pending/completed deletion jobs
before accepting uploads; restoring old learning/auth data cannot resurrect an
account. An actual backup/restore rehearsal remains required by M0/M5.

Evidence: actual PGlite job persistence, existing transactional learning
deletion, receipt-loss restart, provider failure/session remainder, fixed
recovery expiry and private-role checks; isolated SDK/password verifier mocks
and hard-delete composition against a minimal local auth/session cascade.
No live provider, actual process death, public HTTP/client protocol or full
milestone gate is verified by this slice.

Current provider APIs were checked against [password sign-in](https://supabase.com/docs/reference/javascript/auth-signinwithpassword)
and [admin hard deletion](https://supabase.com/docs/reference/javascript/auth-admin-deleteuser).
The markdown changelog index could not be read by the web tool; no successful
changelog or live auth verification is claimed.
RLS behavior and hard-delete cascades were checked against the official
[auth RLS migration](https://github.com/supabase/auth/blob/master/migrations/20240612123726_enable_rls_update_grants.up.sql)
and [user-management documentation source](https://github.com/supabase/supabase/blob/master/apps/docs/content/guides/auth/managing-user-data.mdx).

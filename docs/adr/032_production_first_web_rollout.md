# ADR 032: Production-first web rollout

Accepted by the owner on 6 October 2026 during live environment preparation.

Repurpose the empty v2 Supabase project `zgmyrpzwgtydwlzponih` as
`german-master-v2-production`. No v2 real users or production learner data need
migration. Retain the private schemas, four restricted parent roles, ownership
policies and verified TLS. Reclassify the newly provisioned login names as
`gm_app_backend`, `gm_app_auth`, `gm_app_privacy` and `gm_app_identity`.

Use the separate Vercel project `german-master-v2` for the real production web
artifact and same-origin `/v2` API. The legacy project stays deployed for
reference and rollback. Publish on the Vercel-provided URL first. Transfer the
existing custom domains only after registration, verified sign-in, practice,
confirmed persistence/progress, logout/login recovery, owned export, complete
account deletion and basic role/RLS acceptance pass on that deployment.

The owner explicitly removed independent German approval, exhaustive
accessibility, Android publication and full milestone completion as automatic
web cutover prerequisites. A demonstrated content, security, accessibility or
data-loss defect can still block release. Keep these gates open and accurately
record evidence rather than redefining them as completed.

The five-form engineering release is retired and excluded from the deployed
catalog. A distinct immutable five-target B1 starter release uses original
renovation examples and agent editorial/rubric checks; independent German
review remains pending. This is small real starter content, not the 30-target
reviewed or 120-target pilot catalog. Owner authorization to ship this initial
release is not represented as independent content review.

Public deletion uses fresh password verification, an owned learning tombstone,
provider hard deletion and separate authoritative identity/session absence
checks. Admitted deletion delivery is coordinated across Vercel instances by a
transaction advisory lock on the request UUID. No public worker endpoint,
fixture bearer, disposable-email authorization exception, RLS bypass or
disabled TLS verification is introduced.

Actual connection acceptance found the managed auth schema USAGE grant was
silently ineffective under the connector's non-owner postgres role. Use private
SECURITY INVOKER views under `gm_auth`; PostgreSQL checks the caller's existing
base column grants and RLS, while schema lookup requires only the private view
schema. No SECURITY DEFINER function, auth-owner connection or RLS bypass is
introduced. Observer code refuses altered invoker options or filtered base
policies. See `db/baseline/auth-verifier-views.sql` and PostgreSQL 17 CREATE VIEW.

Production self-service email registration must retain email confirmation.
The default Supabase sender's organization-only delivery is insufficient for
public signup. A safely configured SMTP provider and delivery acceptance are
required; administrator-provisioned test accounts cannot substitute for that
gate. No paid provider, private/personal mailbox or production domain move is
authorized as a workaround for failed registration.

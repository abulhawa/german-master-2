# German Master 2.0 production deployment

Owner strategy changed to production-first on 6 October 2026. This document and
ADR 032 supersede the earlier separate-staging approval prerequisites. Existing
historical staging evidence remains valid for the named project at that time.

## Architecture and isolation

- Vercel project: `german-master-v2`, ID `prj_YO8nVz0rVOWWRMoFLtsiUgHiZ9s6`.
- Root `vercel.json` deploys only the v2 learner artifact, with a Frankfurt Node
  22 function and same-origin versioned `/v2` routes. It does not deploy the
  imported legacy web/API entry points.
- Supabase: `german-master-v2-production`, ref `zgmyrpzwgtydwlzponih`, Frankfurt,
  PostgreSQL 17.11. This is the former empty `german-master-v2-staging` resource,
  renamed in place, not copied from legacy production.
- Learning, session verification, identity verification and private deletion
  each use independent restricted logins. No schema ownership, superuser,
  BYPASSRLS, role/database creation or auth SQL mutation privileges.
- Pooler endpoint comes from this project's dashboard. TLS verifies the
  official Supabase CA in `services/api/supabase-ca.pem`; the certificate is
  public trust material, not a private signing key or API credential.
- Managed `auth` schema ownership prevents the connector's `postgres` role
  from delegating schema USAGE: the GRANT reports success but the ACL remains
  unchanged. Private `gm_auth` SECURITY INVOKER views expose only the already
  granted session/identity columns, enforce the caller's existing Auth RLS and
  have no client, learning-role or worker grants. Runtime observers verify
  invoker options and unfiltered base-table policies before crediting absence.
  Actual restricted connection reads pass without broad auth schema access.
- Only the publishable Auth key enters the browser. Database URLs and provider
  admin capability are sensitive Vercel server environment variables. No
  credential is stored in source, Git, client caches or public evidence.

## Configuration

`GM_ENVIRONMENT=production`, `VITE_V2_AUTH_PROJECT`,
`VITE_V2_AUTH_PUBLISHABLE_KEY`, `GM_DATABASE_URL`, `GM_AUTH_DATABASE_URL`,
`GM_PRIVACY_DATABASE_URL`, `GM_IDENTITY_DATABASE_URL`, `GM_SUPABASE_SECRET_KEY`,
`GM_IDENTITY_DELETION=enabled` and `VITE_V2_IDENTITY_DELETION=enabled` are explicit
production settings. Same-origin hosting derives the API origin from the
current browser origin; no obsolete staging hostname is embedded. Preview
deployments must not receive production database/admin credentials.

The configured account host supports registration and sign-in. Registration
never binds an unconfirmed account or retains its password. Provider email
confirmation stays enabled. The UI uses the existing semantic tokens, bilingual
account copy, labelled fields, native form submission and existing account
binding/isolation behavior. ADR 032 records the intentional v2 design and
release-policy departure from the legacy imported guidelines.

## Content and cleanup

Engineering release `20000000-0000-4000-8000-000000000002` and its five targets
are retired, with immutable revision provenance retained. These are not served
by the product catalog. Product starter release
`30000000-0000-4000-8000-000000000002` has five B1 targets, one revision each:
noun plural, dative article, destination preposition, subordinate word order and
two present-tense verb slots. Catalog/hash and reproducible insert SQL live in
`content/production/catalog.json` and `db/seed/production-starter.sql`.
Independent German review is pending; no pilot readiness claim follows.

No staging identity or lifecycle learner data existed when reclassification
began. Disposable acceptance accounts must be hard-deleted through the actual
product protocol after checks. Verify every owned table and Auth identity,
sessions/refresh tokens/storage, retaining only designed deletion tombstones
and minimal private receipt jobs. Never remove a tombstone to make acceptance
pass. Logs/backups and full restore/retention review remain separate evidence.

## Current acceptance boundary

Actual independent network connections passed for all four restricted logins
with certificate verification. Installed role membership inspection confirms
only the intended parent role, INHERIT TRUE/SET FALSE, with no elevated flags.
Security advisor returns no findings. Runtime row isolation and hosted learner
journey acceptance are pending and must be appended with exact results.

Ordinary signup actually creates an unconfirmed disposable identity; email
delivery and a complete self-service confirmation journey are not yet proven.
Custom SMTP is unconfigured and email confirmation is enabled. Obtain SMTP/Resend credentials through a
protected channel, never chat. Configure production URL/redirects, verify
delivery to disposable authorized inboxes and complete ordinary registration.
Administrator-generated confirmation is useful engineering acceptance but
does not close public email delivery/registration. The owner has now completed
the custom-domain cutover before that acceptance finished; this is recorded as
an intentional production-first risk, not as evidence that email delivery passed.

## Cutover and rollback

Cutover is complete. `germanmaster.qortxai.com` is verified on
`german-master-v2`; `gvm.qortxai.com` is a verified 307 redirect to the primary
domain. Deployment `dpl_GyZaLwy2pEio4zvkXKdVj4QZyfFt` from commit `6d692ca`
is READY and serves the public domain. The legacy Vercel project retains no
custom domains and remains available only through its Vercel-provided URLs.

The public HTML no longer carries the pre-release `noindex,nofollow` directive,
and signup confirmation now requests a redirect to the configured current app
origin. HTTPS and primary/secondary domain routing were verified after cutover.
Public SMTP delivery and a full self-service confirmation journey remain open.

Rollback is a reverse domain assignment to the retained legacy project, not
deletion of either database or application.

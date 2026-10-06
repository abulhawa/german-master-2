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

Self-service registration is currently blocked by unconfigured custom SMTP;
email confirmation is enabled. Obtain SMTP/Resend credentials through a
protected channel, never chat. Configure production URL/redirects, verify
delivery to disposable authorized inboxes and complete ordinary registration.
Administrator-generated confirmation is useful engineering acceptance but
does not close public email delivery/registration. Custom-domain transfer is
conditional on the complete minimum acceptance and has not happened.

## Cutover and rollback

Before transfer, record the READY v2 deployment/commit and legacy production
deployment `dpl_AuPHoBMVHU6M6bQZ2qE4Xce67K2M` in legacy project
`prj_sr2SF6UGsRB6U8dpwukQEuJsPBaY`. Reassign `germanmaster.qortxai.com` and the
`gvm.qortxai.com` redirect deliberately using Vercel; do not delete DNS first.
Keep legacy deployed on its Vercel URL. Verify the custom-domain web/Auth/API
journey after transfer. Rollback is a reverse domain assignment to the retained
legacy project, not deletion of either database or application. No domain move
may be inferred from a successful build or an owner-approved future cutover.

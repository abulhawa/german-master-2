# PR #12 Android v2-only release path audit

## Decision boundary

This document audits source/CI merge readiness, not Android Play release readiness. The owner keeps Android Studio versionCode/versionName, signing and Play rollout. TalkBack is owner-accepted for now and is not reopened here. Production write/publication, merge and Play upload are separate operations.

## Reviewed risks and controls

- Entry point: MainActivity always launches the v2 host and never opens the imported legacy navigation. The legacy WorkManager SyncWorker now returns success without touching legacy data; the imported Supabase Hilt binding fails closed. Preserve the original repositories and existing installed application data.
- Configuration: only public project reference, publishable Auth key and API origin enter BuildConfig. Legacy Supabase endpoint/key and Google OAuth values are empty, never read from older local.properties fields. The ignored `apps/android/local.properties` supports the `gm.v2.*` properties for Android Studio; environment variable overrides remain possible for CI. Do not commit local.properties or signing credentials.
- Production origin: release tasks require the known production Supabase project ref, a syntactically shaped public Auth key and `https://germanmaster.qortxai.com` (optional trailing slash). Debug/isolated preview can use other valid HTTPS origins. This is a structural check, not a live Auth check.
- Owner signing: CI builds an **unsigned** production-configured optimized AAB using a synthetic public key and no private signing files. The owner signs the final release in Android Studio. No actual signed bundle or Play installation/update acceptance is claimed.
- CI: negative tests reject missing configuration and an arbitrary HTTPS host; a positive synthetic build checks guard wiring plus `bundleRelease`, and debug/preview unit tests/lint remain required.

## Merge evidence requirements

1. Latest-head Android CI passes negative configuration checks, optimized unsigned release AAB, debug/preview compilation and tests, and lint.
2. Repository safety, release-readiness guardrails and CodeQL pass on the same head.
3. PR diff contains no credentials or unapproved application identity/version changes; original app and caches are not touched.

## Separate Play release blockers

- Final signed AAB created by owner from the reviewed source, with same applicationId and signing/upload continuity and a versionCode above the live Play maximum.
- Production-confirmed Android account/content journey on a physical phone; offline, restart, expired Auth, account switching and replay/recovery acceptance.
- Publication and live verification of the reviewed converted revision-2 B1/B2 content, under an explicit separate authorization.
- Owner final visual design acceptance, remaining M1/M2-M6 milestone/pilot gates, refreshed store listing/screenshots, and controlled Play internal test/rollback plan.
- The existing imported Android data is preserved, not migrated or cleared here. Privacy/retention cleanup and real upgrade-in-place verification remain distinct release checks.

No database connections, content publication, Play upload, signing or deployment are performed by this PR.

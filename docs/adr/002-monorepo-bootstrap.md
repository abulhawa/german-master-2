# ADR 002 Create a separate private monorepo

Status: Accepted for repository creation on 3 October 2026 following the project owner's request.

## Decision

Create `abulhawa/german-master-2` as a separate private repository, with local checkout `C:/Projects/german-master-2`. Import both original histories without squashing, under `apps/web` and `apps/android`. Preserve the original repos and remote branches.

This supersedes the blueprint's eventual recommendation to reuse the web remote. The owner requested a new repo, and a separate private remote preserves the public web/private Android visibility boundary. The rest of the product and architecture recommendations remain the planning baseline.

## Transitional layout

The existing web project is full stack and relies on relative paths across client, server, schema, shared utilities, scripts and content. Import it intact as the `apps/web` workspace first. Root npm commands invoke that workspace. Preserve its pinned dependencies in one root lockfile and carry its dependency overrides into the root manifest, where npm workspace resolution applies them.

The Android project remains a native Gradle root under `apps/android`. Importing it does not change its application ID, versionCode, signing configuration, authentication or database access behavior. Those behaviors are redesigned through later vertical slices, not by a mechanical folder move.

The imported legacy backend is still coupled to the web workspace. This is explicitly transitional, not the final API boundary. No 2.0 mastery, database, contract or UI behavior is claimed by the bootstrap.

## Next restructuring steps

1. Establish and triage inherited build/test failures using safe local fixtures.
2. Define the first working v2 exercise/API contracts and conformance fixtures under `contracts`.
3. Introduce the pure learning-engine package and the separately deployable `services/api` with a working thin vertical slice.
4. Move shared database ownership to root `db` with one reviewed migration history for the new environment. Preserve the old schema inside the legacy baseline until it is no longer needed.
5. Extract reviewed content/import tooling and generated semantic design tokens when their implementation is used by both clients.
6. Move web to the new API and replace Android direct PostgREST learning operations. Keep Supabase Auth as the common identity provider unless the environment audit identifies a blocker.
7. Separate or extract the internal content workspace when its first reviewed publishing workflow exists.

Do not replace working modules with empty placeholders merely to match the target directory diagram. Add package boundaries with their first implementation and verify affected behavior.

## Preservation and verification

`legacy-web-baseline` points to `f1ccc88113d6f636b080117b11af0e24c5fb9a87`; `legacy-android-baseline` points to `3d09b26b1becf5cba6d3b06788e4b72c1623a265`. Each baseline is an ancestor of the new main branch. The imported file trees matched the originals before bootstrap configuration edits. Both originals remain clean.

Release infrastructure is intentionally limited to checks. No production deployment linkage, database reset, GitHub repository archival, release upload or signing credential migration is part of this step.

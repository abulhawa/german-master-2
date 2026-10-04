# ADR 018: Local read cache retention

Accepted for the isolated fixture, 4 October 2026.

Frozen target pages expire at a fixed deadline, seven days after snapshot creation by default. Tests inject the clock and page lifetime independently of sync cursor lifetime. Every page in a chain shares its deadline. At `now >= expires_at`, owned page reads return HTTP 400 `invalid_cursor`, also used for unknown, foreign and wrong-kind cursors. Access does not renew a deadline or rebind a page to newer state. Active payloads remain immutable through ingestion and cleanup.

Migration 006 gives undated legacy pages one bounded grace period from upgrade time; repeated initialization does not extend it. New inserts require an explicit deadline. Cursor and page update guards remain, but delete guards are removed for these transport caches only. This local privileged adapter is not a production least-privilege design.

Fresh snapshots and successful sync reads prune expired transport rows in their transaction. Explicit `cleanupReads()` supports maintenance when traffic stops. Cleanup deletes only expired pages and cursors, across fixture owners, using one injected clock instant. A failed read or delete rolls back its transaction. Idle stores retain expired rows until maintenance or successful traffic resumes; this is a time retention window, not a hard byte quota under unlimited request volume.

Accepted evidence, sync changes, projections, sessions, immutable exercise revisions and idempotency records are never pruned. An active page can refer to an already expired/deleted sync cursor; its payload remains exact and the next pull requires a fresh snapshot as in ADR 017.

The web snapshot loader commits only after all pages validate. An expired continuation page fails that refresh, preserving the previous confirmed snapshot/cursor and pending writes. Explicit retry starts a new cursorless snapshot; no automatic retry loop is introduced. Native full reset reconciliation remains open. No production migration, scheduler, retention guarantee or release is implied.

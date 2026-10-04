# ADR 017: Local sync cursor expiry

Accepted for the isolated fixture, 4 October 2026.

Sync positions have an immutable expiry instant. The local default is seven days from issuance; tests inject both clock and lifetime. A cursor is invalid at `now >= expires_at`. Reads return the existing HTTP 400 `invalid_cursor` response for expired, unknown, foreign and wrong-kind cursors. Polling does not extend lifetime. A new snapshot or cursorless pull may issue a new UUID at the same sequence after expiry, without reviving the old UUID. Learner locking serializes issuance with ingestion. Each read captures one clock instant.

Migration 005 removes uniqueness of owner/sequence and adds an expiry index. Existing records without trustworthy issuance times are expired rather than assigned invented ages. Migration checks are idempotent; evidence, schedules, attempts and immutable target pages are untouched. Snapshot pagination still returns the exact original payload after clock passage, ingestion and restart. A snapshot completed after its sync cursor expires can be committed, but its next pull requires another full snapshot. No page is silently rebound to a later watermark.

The web's existing ADR 016 recovery replaces confirmed targets and cursor only after complete snapshot validation and successful local persistence. Pending session, attempt, exposure and profile writes remain independent. HTTP integration verifies expiry at the millisecond boundary, frozen pagination with concurrent ingestion, fresh cursor recovery and exact pending-attempt duplicate replay.

This is logical expiry, not physical cleanup: append-only cursor/page records and sync evidence remain retained. Bounded storage cleanup, snapshot page expiry, production retention policy, account partitions and native reset reconciliation remain separate work. Seven days is a local fixture choice, not an accepted production guarantee. No production migration or release occurs.

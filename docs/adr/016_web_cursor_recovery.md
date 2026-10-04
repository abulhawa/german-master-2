# ADR 016: Local web sync cursor recovery

Accepted for the isolated fixture, 4 October 2026.

The web transport recognizes the existing local API's HTTP 400 `invalid_cursor` response only on `/v2/sync`, and raises a distinct reset error. Authentication, network, malformed responses and other HTTP failures do not initiate recovery. The server currently retains cursors indefinitely; this prepares the client for missing/reset cursor records without introducing a production expiry policy.

Pull recovery fetches a fresh target snapshot, validates the existing pagination watermark and timestamp invariants, then commits all confirmed targets and the replacement cursor together. It replaces the target set, so absent historical entries are removed. No partial snapshot is committed. Failed pagination or failed local persistence retains the last successful delta page and its cursor; the next explicit refresh retries from that position. There are no automatic retry loops.

The existing exclusive fixture ownership and journey commit merge preserve current practice, drafts, assistance, frozen session/attempt/exposure requests and counters. Profile requests remain in their separate existing record. Recovery does not send or clear pending writes. The normal refresh still follows pulls with a fresh snapshot to refresh time-sensitive due flags.

No schema, content, learner policy, production identity or UI flow changes. Production cursor retention/expiry, account partitions, native coordination and offline reconciliation remain open.

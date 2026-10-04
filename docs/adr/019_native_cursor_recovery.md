# ADR 019: Native local sync cursor recovery

Accepted for the debug local fixture, 4 October 2026.

Add a nullable sync cursor to the version-one AtomicFile learner cache. Existing records without it load unchanged and obtain a full snapshot on refresh. Confirmed targets and their cursor are saved together. The debug HTTP transport validates generated v2 sync responses; only a strict HTTP 400 ApiError with code `invalid_cursor` on the sync endpoint raises the dedicated reset signal. Authentication, malformed responses, ordinary network failures and target-page errors do not initiate reset recovery.

Refresh obtains profile/catalog, pulls from the saved cursor if present, then refreshes the full snapshot for authoritative time-sensitive due flags, matching the local web refresh behavior. Each validated delta page merges target upserts without replacing a newer lastSequence with older evidence and durably commits its targets/cursor before requesting another page. Repeating continuation cursors fail without looping. The current v2 contract supports upserts; no new deletion/tombstone contract is introduced.

A reset obtains every fresh snapshot page and validates consistent watermark/timestamp, unique targets and non-repeating pagination. Only a complete snapshot replaces the confirmed target set and cursor, including removal of targets absent from the new snapshot. Failed pagination or local save preserves the last committed data/cursor, including any preceding successful delta page. Target-page expiry fails the refresh; the next explicit refresh or activity restart retries from persisted state. No automatic retry or write flush is added.

Recovery copies the current cache while replacing only confirmed reads. Pending profile/session/attempt/Skip payloads, drafts, token order, assistance, position and summary counters remain intact. Repository mutex and existing UI busy state preserve activity-level serialization; cross-process coordination, production account partitions and complete offline reconciliation remain separate gates.

No release navigation, signing, application identity, content, generated contracts or learning policy changes. Repository restart tests and JVM loopback HTTP transport tests do not establish real native device/process-kill/TalkBack/font-scale acceptance.

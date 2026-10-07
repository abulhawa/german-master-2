# ADR 033 Low-typing practice foundation

Status: Accepted implementation of the owner's 7 October 2026 direction.

Use single-answer MCQ, complete sentence ordering, per-gap choices and one-to-one pair matching as the authoring foundation on web and native Android. Extend v2 with negotiated `gap_choice@1` and `matching@1` capabilities; regenerate both language contracts from the schema. Keep grading authoritative on the service and provisional offline grading aligned with the shared conformance corpus. No new mastery, retention or scheduling policy is introduced.

Use stable IDs, authored presentation order and explicit Check. Partial drafts are persistable; complete answers are validated against the pinned exercise. Matching permits reassignment and unpairing; ordering permits repeated visible words, individual removal and accessible moves. No drag dependency or new UI library is needed. Every flow consumes the same platform renderer.

The app is a hard reset with no existing learners to migrate. Update clean schema definitions without adding database upgrades or client-data migration paths. Keep existing typed controls temporarily because catalog conversion/publication is explicitly deferred; their presence is not a compatibility requirement. Preserve package identity, signing continuity, source provenance and immutable editorial revisions.

Local engineering examples and downloaded-pack fixtures demonstrate the new formats without expanding live content. Content review must distinguish recognition from written production; format changes alone do not establish new transfer identities. Details and next steps: `docs/content/practice-formats.md`.

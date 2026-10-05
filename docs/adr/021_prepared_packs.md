# ADR 021: Local prepared reserve and integrity

Accepted for the isolated fixture, 5 October 2026.

POST /v2/packs accepts the existing v2 session request contract. The backend allocates two owned sessions in one transaction with pinned exercise revisions, deduplicated versioned rubrics and explanations. Ordinary session responses remain solution-free. A pack intentionally includes solutions as a learning aid; unpublished fixture content remains unpublished.

The pack ID is the request ID in its own owned namespace. Migration 009 stores immutable request/response and owned session membership. Replays return the entire original pack including deadline; changed requests conflict. Failure rolls back both allocations. Prepared sessions may overlap because no new learning has occurred between their offline selections.

SHA-256 covers UTF-8 canonical JSON of every field except contentHash: recursively sorted property names, original array order, normal JSON strings and numbers. All contracted keys are fixed ASCII identifiers. This checksum detects corruption and partial replacement; it is not a signature or an authentication mechanism. Authentication and ownership remain API responsibilities.

Both client transports check the complete hash, supported evaluator/normalization versions, release/session/question/revision/rubric linkage and accepted-answer structure before return. TypeScript also validates rubrics with the existing pure grader. Seven-day expiry gates a new start only (issuedAt <= now < expiresAt); started work remains uploadable at pinned revisions after expiry. No client scheduling or mastery policy is introduced.

Native prepareReserve freezes a request before downloading, validates the entire result, and atomically saves the reserve and clears that request while preserving current practice and other writes. AtomicFile reads validate cached pack integrity; corruption remains on disk and blocks loading. Existing cache records remain readable. This is local fixture groundwork: no offline learner UI/start adoption, IndexedDB reserve, provisional Kotlin grader, coordinated outbox or production account partition is claimed.

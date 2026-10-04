# Foundation API demonstration

Run from the repository root in two terminals:

```powershell
npm run dev:api
npm run dev:web
```

Open `http://127.0.0.1:5000/foundation?backend=1`. `/foundation` still provides the static renderer inspection. Backend mode creates five fresh owned questions, submits answers and displays confirmed server feedback, then a summary. It does not calculate mastery. The Vite development proxy forwards `/v2` to the isolated API on loopback port 5001.

For the isolated learner journey, open `http://127.0.0.1:5000/renovation`: Home → explicitly shorter five-question practice → confirmed summary → Progress. The server supplies selection, grading and retained-evidence state. The web preview saves drafts, immutable pending answers, acknowledgments and confirmed read cursors in a versioned fixture-only localStorage record across reloads. Use one tab; this is connected practice, not offline grading or production account storage. The server still resets on restart. See [journey evidence](../../docs/operations/web-journey-verification.md) and [ADR 008](../../docs/adr/008_local_web_journey.md).

Android debug demonstration on an isolated emulator/device:

```powershell
adb reverse tcp:5001 tcp:5001
adb shell am start -n com.germanverbmaster.android/.foundation.FoundationPreviewActivity --ez backend true
```

The debug network-security configuration permits HTTP only to `127.0.0.1`. Backend transport/UI, fixture authentication and strings are debug-only. This activity shares the inherited Application, so use a device without production configuration. No device verification is claimed by the local Robolectric tests.

`src/server.ts` accepts an injected authenticator that supplies a verified UUID subject. It has no default authenticator. `src/local.ts` explicitly uses a **public fixture token**, `foundation-local-demo`, maps it to a fixture learner, binds only to loopback and initializes an in-memory PGlite PostgreSQL database. This is a local demonstration launcher, not a production authentication system or deployable product configuration. No production environment variables or database URLs are read. `GM_DEMO_PORT` optionally changes its loopback port for build smoke checks. Data resets on restart; client pending submissions are held only in memory. Durable outboxes and production privacy/security work remain open.

Implemented draft endpoints:

- `POST /v2/sessions`: server-owned mixed allocation from persisted target states and current UTC deadlines, strict capability negotiation, exact requested question count or explicit insufficient-content error, immutable release/revision/assessment-role pinning, subject-scoped replay/conflict by request ID, solution-free payload. See [ADR 006](../../docs/adr/006_server_selected_sessions.md) for the small-catalog policy and reinforcement fallback.
- `POST /v2/attempts:batch`: 1–50 typed attempts, 128 KiB body bound, subject from authentication, linkage validation, assistance recording, pure server evaluation, first submission and idempotency. Valid items commit independently in arrival order. Invalid transport envelopes return 400; semantic linkage failures return per-item rejections. Raw payload replay compares object keys canonically but preserves array order, answer text and assistance. Retries return original evaluation/sequence as `duplicate`. Attempt/evaluation/device insertion, accepted evidence, target projection, review schedule, sync change and complete-session marking commit atomically per item. No client grade is accepted.

`npm run check:backend` and `npm run test:backend` check engine/API independently. Root check/test/build include them. `npm run build --workspace=services/api` bundles the first local launcher to `services/api/dist/local.js`; `node services/api/dist/local.js` runs it with installed external Zod/PGlite and the repository migration file. It is a repository-relative build, not a self-contained deployment artifact.

- `POST /v2/exposures:batch`: 1–50 strict typed events with the same body/auth bounds. `exposure` records only exposure and leaves the question open; `skip` closes it without grading. Subject-scoped event replay/conflict and first-terminal-submission validation share the answer transaction boundary. No skip UI is implemented yet.

`retained-evidence-v1` runs only on the server. Immutable evidence pins editorial variant/context/transfer identities, evaluation version, session issuance and the profile timezone saved at ingestion. Grammar targets map to the policy's concept gate. Each ingest rebuilds that target in receipt order and saves state/schedule plus an immutable sync change. A learner-profile row lock serializes competing writes. The embedded adapter still cannot prove independent network-connection contention. Rebuild is read-only and derives current `isDue` from the injected clock; stored/synced snapshots record `isDue` as of ingestion and consumers must refresh due eligibility. See [ADR 005](../../docs/adr/005_transactional_evidence.md).

Remaining boundaries: verified production identity; network PostgreSQL transactions/locking and roles; profile-level/topic filtering and reviewed catalog diversity; explicit partial completion; skip UI and standalone reveal commands; public sync cursors, durable outboxes and reconciliation; content approval/publication. No production changes or AI calls occur in this slice.
# Confirmed read transport

`GET /v2/targets?limit=50` returns a frozen confirmed snapshot. Follow its `nextPageCursor` via `cursor` until empty, then persist the complete snapshot and its `syncCursor`. `GET /v2/sync?cursor=...&limit=50` pulls owned ingestion-time upserts; apply changes before saving `nextCursor`. Continue while `hasMore`, then reuse that position to poll. Omit the sync cursor to read from the beginning. Limits are 1–100; authentication is the same as session writes.

Fresh target reads refresh due flags using UTC deadlines; sync retains the originally ingested flags. See [ADR 007](../../docs/adr/007_owned_target_sync_transport.md) for replay, sequence and local retention limits. These endpoints expose confirmed summaries only; durable client outboxes and reconciliation are still unimplemented.

## Local catalog and focused practice

`GET /v2/catalog` returns authenticated, solution-free bilingual metadata for the five unpublished local targets. Its explicit `unpublished_local_draft` status is not publication approval. No query parameters are supported. `/renovation` uses it for Topics, topic detail and confirmed target detail.

`POST /v2/sessions` also accepts the existing fields plus `focus: { type: "target" | "topic", id: "UUID" }`. Filtering happens before authoritative selection. Counts remain exact; unavailable scopes/capabilities return `insufficient_content`. Focus is part of request replay/conflict identity. The demonstration offers one question per target or up to five per topic; non-due extra practice remains reinforcement. Saved focused requests resume across reload without replacing unfinished mixed practice. See [ADR 009](../../docs/adr/009_local_catalog_focus.md).

## Owned profile/setup

`GET /v2/profile` reads the authenticated fixture's saved preferences and revision. `POST /v2/profile` accepts `apiVersion`, a stable UUID `requestId`, `expectedRevision`, and `preferences` containing `locale` (`en`/`de`), an IANA `timezone`, `level` (`B1`/`B2`), and `sessionQuestionCount` (5/15). Exact retries return the original response; altered retries or stale revisions conflict. Read again for current preferences after a replay. Both endpoints are no-store, with no query parameters.

`/renovation` now begins with setup and offers later editing. The B1-only local draft catalog explicitly reports no new questions for fresh B2 profiles. Targets already practised remain eligible for review; existing session requests replay unchanged. Profile timezone changes apply at subsequent event ingestion; saved event timezones and historical evidence stay unchanged. The five-question catalog can provide less than the preferred fifteen. Pending setup writes use a separate versioned public-fixture storage key and exact retries; this is not a production account partition or offline outbox. See [ADR 010](../../docs/adr/010_owned_profile_setup.md).

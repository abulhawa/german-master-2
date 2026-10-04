# Shared foundation contracts

`v2/schema.json` is the transport source of truth (JSON Schema 2020-12). `v2/openapi.json` describes the draft session/attempt/exposure API boundary, implemented by the isolated local foundation service under `services/api`. `/v2` versions the API; `schemaVersion: 1` versions each of the five exercise payload forms. Exercise IDs and positive revisions identify immutable content; questions pin them within a content release.

Run `npm run generate:foundation` from the repository root after schema/token edits. The checked-in generator v1 produces Zod transport types and Kotlin serialization models. `npm run check:generated` detects drift; both CI jobs run it. The generator fails on unsupported schema keywords instead of silently ignoring them. Generated files are not hand-edited.

Objects are closed and all fields required. Integers use the same signed 32-bit range in both languages. Timestamps are canonical UTC seconds (`YYYY-MM-DDTHH:mm:ssZ`) or three fractional digits (`YYYY-MM-DDTHH:mm:ss.SSSZ`); offsets and impossible dates are rejected. UUID validation checks hexadecimal layout. Kotlin runs generated structural validation before decoding to reject quoted numbers and serializer coercion. Unknown API versions, exercise versions, forms and fields fail explicitly; session negotiation must exclude unsupported forms before selection.

`examples/session.json` contains five solution-free exercise revisions. The browser imports it through the contracts workspace; Gradle copies only this file into assets. Test resources contain the same acceptance/rejection corpus, typed attempts and accepted/duplicate/rejected acknowledgment examples. Editorial solutions live separately in `content/foundation`; they are not copied into Android assets or online sessions.

`Attempt` contains no learner ID or grade. The service derives ownership from its injected authenticator, validates question/revision/answer linkage and assistance, and rejects a reused attempt ID with changed payload. The local launcher uses only public fixture authentication; verified production identity remains open. Each accepted batch item commits independently; duplicate acknowledgments retain their original evaluation. `Evaluation.assisted` explicitly reports whether hint/reveal events accompanied the submission. Client clocks and sequences are evidence, not server ordering authority. The current server sequence is bounded to the draft integer range; move to decimal-string cursors before scaling beyond it.

This contract covers online attempts and confirmed target/sync reads. Offline-pack IDs, local policy metadata, explicit partial session completion, guest claiming, durable client cursor commits and the remaining blueprint endpoints must be added and tested before those flows are enabled. Backend preview modes submit answers and display confirmed evaluations without local grading or mastery. Static inspection modes remain available. See `services/api/README.md` for setup and limitations.

## Demonstration

- Web: root `npm run dev:web`, then `/foundation`. The route is development-only; production still opens the inherited client. The preview lazy-loads independently of legacy auth/server modules.
- Android: build/install the debug APK, then `adb shell am start -n com.germanverbmaster.android/.foundation.FoundationPreviewActivity`. The activity is declared only in the debug manifest. Existing application identity and release navigation are preserved. The inherited Application still owns background workers, so use an isolated device without production configuration when demonstrating.
- Automated: web rendering tests interact with every input form; Kotlin tests decode/round-trip the shared fixtures; a Robolectric Compose test renders every prompt. These do not establish device/TalkBack acceptance.

## Exposure ingestion

`ExposureEvent` and `POST /v2/exposures:batch` accept explicit `skip` or `exposure` dispositions with UUID event/question/device identities, revision and canonical UTC occurrence time. They accept neither grades nor timezone/editorial/mastery claims. Exposure is nonterminal; skip completes that question without an evaluation. Answers and skips compete for the first terminal submission. Changed event payloads conflict; identical retries return the original exposure sequence without another sync change. `examples/exposure-conformance.json` is checked by TypeScript and Kotlin tests. The previews do not yet offer skip controls.

Attempt acknowledgments retain the original attempt receipt sequence for compatibility. Exposure acknowledgments use the accepted-evidence receipt sequence. Neither is a sync cursor; `/v2/sync` uses separate opaque owned cursors over `sync_change.sequence`.

## Confirmed reads

`TargetPage` provides confirmed summaries at one `generatedAt` instant with frozen `nextPageCursor` pagination and a `syncCursor` watermark. `SyncPage` provides ordered ingestion-time `upsert` summaries and a reusable `nextCursor`; client pending work is absent. `schedule` has zero or one UTC review deadline, and `lastSequence` is accepted-evidence order. Shared `target-page.json` and `sync-page.json` fixtures are decoded in TypeScript and Kotlin. See [ADR 007](../docs/adr/007_owned_target_sync_transport.md) for complete cursor, due-refresh and local storage semantics.

Owned setup transport adds `ProfilePreferences`, `LearnerProfile` and `ProfileRequest`, generated in both languages. The server additionally validates IANA timezone names at ingestion. Profile writes use stable request IDs and expected revisions; replay returns the original response, so clients reread current state afterward. See [ADR 010](../docs/adr/010_owned_profile_setup.md).

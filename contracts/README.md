# Shared foundation contracts

`v2/schema.json` is the transport source of truth (JSON Schema 2020-12). `v2/openapi.json` describes the first session/attempt API boundary; it is a draft contract, not an implemented service. `/v2` versions the API; `schemaVersion: 1` versions each of the five exercise payload forms. Exercise IDs and positive revisions identify immutable content; questions pin them within a content release.

Run `npm run generate:foundation` from the repository root after schema/token edits. The checked-in generator v1 produces Zod transport types and Kotlin serialization models. `npm run check:generated` detects drift; both CI jobs run it. The generator fails on unsupported schema keywords instead of silently ignoring them. Generated files are not hand-edited.

Objects are closed and all fields required. Integers use the same signed 32-bit range in both languages. Timestamps are canonical UTC seconds (`YYYY-MM-DDTHH:mm:ssZ`); fractional seconds and offsets require a future contract change. UUID validation checks hexadecimal layout. Kotlin runs generated structural validation before decoding to reject quoted numbers and serializer coercion. Unknown API versions, exercise versions, forms and fields fail explicitly; session negotiation must exclude unsupported forms before selection.

`examples/session.json` contains five solution-free exercise revisions. The browser imports it through the contracts workspace; Gradle copies only this file into assets. Test resources contain the same acceptance/rejection corpus, typed attempts and accepted/duplicate/rejected acknowledgment examples. Editorial solutions live separately in `content/foundation`; they are not copied into Android assets or online sessions.

`Attempt` contains no learner ID or grade. The future service derives ownership from verified authentication, validates question/revision/answer linkage and assistance, and rejects a reused attempt ID with changed payload. Each accepted batch item commits independently; duplicate acknowledgments retain their original evaluation. Client clocks and sequences are evidence, not server ordering authority. The current server sequence is bounded to the draft integer range; move to decimal-string cursors before scaling beyond it.

This first contract covers online attempts. Offline-pack IDs, local policy metadata, skip/exposure commands, session completion, guest claiming, cursor sync and the remaining blueprint endpoints must be added and tested before those flows are enabled. Clients currently prepare answers only: no grading, mastery, scheduling, persistence or API calls are implemented by the previews.

## Demonstration

- Web: root `npm run dev:web`, then `/foundation`. The route is development-only; production still opens the inherited client. The preview lazy-loads independently of legacy auth/server modules.
- Android: build/install the debug APK, then `adb shell am start -n com.germanverbmaster.android/.foundation.FoundationPreviewActivity`. The activity is declared only in the debug manifest. Existing application identity and release navigation are preserved. The inherited Application still owns background workers, so use an isolated device without production configuration when demonstrating.
- Automated: web rendering tests interact with every input form; Kotlin tests decode/round-trip the shared fixtures; a Robolectric Compose test renders every prompt. These do not establish device/TalkBack acceptance.

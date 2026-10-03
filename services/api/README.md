# Foundation API demonstration

Run from the repository root in two terminals:

```powershell
npm run dev:api
npm run dev:web
```

Open `http://127.0.0.1:5000/foundation?backend=1`. `/foundation` still provides the static renderer inspection. Backend mode creates five fresh owned questions, submits answers and displays confirmed server feedback, then a summary. It does not calculate mastery. The Vite development proxy forwards `/v2` to the isolated API on loopback port 5001.

Android debug demonstration on an isolated emulator/device:

```powershell
adb reverse tcp:5001 tcp:5001
adb shell am start -n com.germanverbmaster.android/.foundation.FoundationPreviewActivity --ez backend true
```

The debug network-security configuration permits HTTP only to `127.0.0.1`. Backend transport/UI, fixture authentication and strings are debug-only. This activity shares the inherited Application, so use a device without production configuration. No device verification is claimed by the local Robolectric tests.

`src/server.ts` accepts an injected authenticator that supplies a verified UUID subject. It has no default authenticator. `src/local.ts` explicitly uses a **public fixture token**, `foundation-local-demo`, maps it to a fixture learner, binds only to loopback and initializes an in-memory PGlite PostgreSQL database. This is a local demonstration launcher, not a production authentication system or deployable product configuration. No production environment variables or database URLs are read. `GM_DEMO_PORT` optionally changes its loopback port for build smoke checks. Data resets on restart; client pending submissions are held only in memory. Durable outboxes and production privacy/security work remain open.

Implemented draft endpoints:

- `POST /v2/sessions`: strict capability negotiation, exact requested question count or explicit insufficient-content error, immutable release/revision pinning, subject-scoped replay/conflict by request ID, solution-free payload.
- `POST /v2/attempts:batch`: 1–50 typed attempts, 128 KiB body bound, subject from authentication, linkage validation, assistance recording, pure server evaluation, first submission and idempotency. Valid items commit independently in arrival order. Invalid transport envelopes return 400; semantic linkage failures return per-item rejections. Raw payload replay compares object keys canonically but preserves array order, answer text and assistance. Retries return original evaluation/sequence as `duplicate`. Attempt/evaluation/device insertion and complete-session marking commit atomically per item. No client grade is accepted.

`npm run check:backend` and `npm run test:backend` check engine/API independently. Root check/test/build include them. `npm run build --workspace=services/api` bundles the first local launcher to `services/api/dist/local.js`; `node services/api/dist/local.js` runs it with installed external Zod/PGlite and the repository migration file. It is a repository-relative build, not a self-contained deployment artifact.

Remaining boundaries: verified production identity; network PostgreSQL transactions/locking and roles; dynamic/adaptive selection; evidence reducer and scheduling; explicit partial completion; skip/exposure/reveal commands; durable outboxes and sync; content approval/publication. No production changes, AI calls or mastery projections occur in this slice.

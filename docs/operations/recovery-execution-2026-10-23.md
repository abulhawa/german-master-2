# GM2.0 recovery execution plan (23 October 2026)

Tracking: [Issue #20](https://github.com/abulhawa/german-master-2/issues/20).

## Recovery decision and deployment boundary

**Keep** the shared contracts, grading, content, Postgres schema, offline outbox, and native/web foundations. **Do not** reset Git history or create a new database. Production currently serves a functioning *local five-question guest demo* but `/v2/catalog` and `/v2/profile` return HTTP 503. Fix operational continuity first; simplify the learner experience second.

At the checkpoint, `main` was `a9f0c3ae`, and its Vercel production deployment was READY. The production environment and API still bound to the inactive old project `zgmyrpzwgtydwlzponih`. The new clean project `sqgjsmiaprsuilcjmaav` was restored and verified ACTIVE_HEALTHY, with baseline 1, 29 RLS-enabled tables, zero users, 65 published targets (35 B1, 30 B2), 125 exercises, and 2 published content releases (5 starter and 125 full catalog links). Security advisors reported no findings. Preserve older Auth users/history in the old project until explicitly retired.

### Gate A. Configuration and isolated account service (FIRST)

1. Keep the live site on its current deployment. Test the runtime's explicit project resolver and scoped database username checks in CI. The resolver must reject unknown projects, missing mode, and old-project replacement verification; it must *not* silently fall back to the old project.
2. In the replacement dashboard, verify required confirmation emails, branded SMTP delivery, site URL and redirect allowlist, and email recovery. No credential values go in source, logs, comments, or chat.
3. Configure a **nonproduction** authenticated runtime against the replacement. Its environment must be `GM_ENVIRONMENT=replacement-verification`, `VITE_V2_AUTH_PROJECT=sqgjsmiaprsuilcjmaav`, and its database URLs must use the four restricted `gm_app_*` login roles for that project over the verified Frankfurt TLS pooler. Use the new project's publishable and secret keys, not the old project's. Make sure production secrets cannot leak into previews.
4. Test the complete API lifecycle and user session with real replacement Auth: an unauthenticated `GET /v2/catalog` should give **401**, not **503**; authenticated profile, catalog and session requests should succeed. Test signup/confirmation, password recovery, progress persistence across logins, offline replay, privacy and deletion, and browser/Android parity. Use disposable test accounts in the new project only.
5. Record check results and any data changes. Never infer availability from Vercel's READY status or from a local guest demo.

### Gate B. Separately approved production cutover

1. Capture the exact deployment SHA, Vercel env names and rollback settings. No old project deletion or unrelated DNS change.
2. In **one coordinated deployment**, set the clean project consistently for `VITE_V2_AUTH_PROJECT`, `VITE_V2_AUTH_PUBLISHABLE_KEY`, all four `GM_*_DATABASE_URL` scoped credentials, and `GM_SUPABASE_SECRET_KEY`; keep `GM_ENVIRONMENT=production`. Verify that the project ID embedded in each pooler username matches the selected project.
3. Deploy a reviewed, CI-green build only after verified isolated lifecycle and explicit cutover approval. Confirm real `/v2` authenticated and unauthenticated behavior, monitor runtime errors and rollback immediately on failure. Restore the recorded prior config/deployment as needed. Note: reverting to the currently **inactive** old database is *not* a working service rollback until it is operational.
4. Keep the old project retained until new-site evidence is accepted, then obtain separate retirement approval.

### Gate C. Bounded learner-facing simplification

- One journey: Start (guest or signed in) → B1/B2/topic → question → explicit Check → concise correction → Next → useful summary and suitable next step.
- Remove duplicate signup prompts, decorative status messages and fake setup controls that do not change the questions; maintain a single optional account call to action outside answering.
- Web Home: Start/Resume; Topics: optional focus; Progress: actionable targets based only on actual evidence. No unsupported diagnostic claims, streak pressure or mastery shortcuts.
- Web and Android share answer semantics and feedback, without forcing identical navigation widgets. Preserve signing, identity boundary, partial drafts, and durable offline outboxes.
- Verify real signed-in browser and physical Android paths; the owner-approved Android release hold remains.

### Gate D. Verification and pilot

- Run focused and full backend/web/Android checks as appropriate, including responsive layout and accepted accessibility scope.
- Short usability pilot with real B1/B2 learners; prioritize task completion, correction clarity and repeat usefulness over feature counts.
- Keep a one-page current-state log; treat `PROGRESS.md` as historical evidence, not a reliable live status source.

## Current execution status

- **Verified:** clean hosted schema and published catalog; recovery issue recorded; local five-question guest journey; production API 503.
- **Implemented in recovery branch:** runtime project binding is explicit and restricted, with contract tests.
- **Unverified:** replacement SMTP/redirect and real signed-in lifecycle; safe final production connection credentials; production cutover; broad Android UX.
- **No authorization in this runbook:** deletion, database reset, production environment mutation, deployment or Play Store release.

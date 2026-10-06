# Confirmed-user lifecycle acceptance — 6 October 2026

## Environment and scope

Actual public host: `https://germanmaster.qortxai.com`. Vercel project
`prj_YO8nVz0rVOWWRMoFLtsiUgHiZ9s6`; READY production deployment
`dpl_GyZaLwy2pEio4zvkXKdVj4QZyfFt`, source commit `6d692ca`.
Supabase project `zgmyrpzwgtydwlzponih`. This verifies the deployed product;
dependency-remediation commit `394efa0` is pushed but is not this deployment.

The earlier disposable identities A/B were already absent from Auth. Created a
fresh disposable identity for this explicitly requested test, using ordinary
signup and an administrator-generated signup token verified through Auth.
Passwords, tokens, deletion recovery capabilities and server credentials stayed
in memory. No existing learner or preserved Android/loopback session was reset.

**Confirmation limitation:** signup created an unconfirmed identity without a
session. Administrator-assisted token confirmation established a confirmed
identity. Email delivery, inbox access, clicking a delivered link and the full
self-service registration journey remain unproven; custom SMTP is still the
documented blocker. This result does not close that gate.

## Actual results

| Boundary | Result and evidence |
| --- | --- |
| Confirmed identity and sign-in | Auth confirmation succeeded; browser sign-in reached onboarding on the public domain. An independent password sign-in confirmed the identity and initially empty learner export. |
| Preferences | Browser saved English, B1, Europe/Berlin and five questions; Home displayed the saved five-question preference. |
| Practice | Browser completed short answer, choice, cloze, word order and multi-slot exercises. Each received server-confirmed Correct feedback. No hints or Skip were used. |
| Completion | Explicit confirmation returned a full completion receipt for session `dc2ac212-4cdb-4634-9548-e69a79fa54c1`: five planned/graded/correct, zero skipped. |
| Persistence and Progress | Owned server export contains one session, five attempts, five evaluations, five target states and one completion. Browser Progress displays five Learning targets, each with one qualifying check and the next review on 7 October. No retained-mastery claim follows. |
| Logout/login | Browser's sync-and-sign-out returned to sign-in. Same-account sign-in recovered the five-question preference and all five Progress entries. Independent authenticated exports before/after have identical attempt IDs, target state/schedules and completion receipts. |
| Browser cleanup | After recovery verification, the product's explicit local-removal sign-out removed this disposable account's browser work and returned to sign-in. |
| Account deletion | Hosted `/v2/me/identity-deletion:begin` with fresh password proof and explicit `delete_identity` confirmation returned HTTP 200 / `identity_deleted`. Session-free recovery returned the same receipt, request `735d9180-42e1-4e42-96d5-53f6bf32f4d5`. |
| Authoritative removal | Actual independent restricted identity observer reports no Auth identity or active sessions. Subject-scoped learning reads report zero rows in all 18 owned learner tables, retaining one deletion tombstone. Old bearer access returns 401; provider password login and browser login are rejected. |

Additional read-only connector counts for this exact disposable subject are
zero in `auth.users`, `auth.sessions`, `auth.identities`, `auth.refresh_tokens`
and owned `storage.objects`. Storage was not uploaded during this run. Temporary
credential-transfer and acceptance processes were stopped after cleanup.

Deletion was exercised through the actual hosted product API. This run does
not claim browser deletion-dialog/recovery-marker acceptance or native account
deletion acceptance. Browser cleanup used local-removal sign-out before the
server deletion. Broader retention/log/backups, load, cross-device and milestone
gates remain open.

## Reproduction and evidence hygiene

Ignored local evidence: `.local/confirmed-lifecycle-evidence-20261006.json` and
`.local/production-deletion-lifecycle-20261006.json`. These contain sanitized
results, counts, IDs and receipts, without passwords, access/refresh tokens,
server credentials or recovery capabilities. The account is deleted; do not
attempt to reuse its credentials.

Two harness issues were resolved without product changes: browser password
inspection returned a redaction marker, so a temporary memory-only clipboard
transfer supplied the actual disposable credential and cleared the clipboard;
the initial API deletion harness omitted the required confirmation literal and
was correctly rejected with 400. The corrected request passed. Neither failure
is counted as a product lifecycle defect.

Next release action: configure safely supplied SMTP, verify delivery and
self-service confirmation on the public domain, then verify the browser
deletion controls and recovery/local cleanup within that ordinary account
journey. Deploy and independently verify the dependency updates under the
existing release workflow before claiming them live.

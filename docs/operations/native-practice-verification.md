# Native Practice verification

4 October 2026. Debug learner preview only; no production or release changes.

Home starts or continues a stable server-selected session. All five forms save drafts, including partial slots and one-token order. Assistance is saved before hint disclosure. Check and Skip freeze IDs/question/revision/device/timestamp before HTTP. Failed writes block transmission. Accepted/duplicate acknowledgments save feedback or Skip advancement with separate counters. Close retains work; confirmed discard removes the local session. Completion shows graded/skipped/server-correct counts and refreshes confirmed targets.

## Offline evidence

NativePracticeTest covers AtomicFile restart with drafts/assistance/frozen answers/feedback/summary, lost session response replay, failed freeze with zero network writes, failed answer/Skip acknowledgment saves, exact retry, pending operation exclusion, incomplete drafts and mixed counts. NativePracticeUiTest completes all five forms under Robolectric, checks partial-order/slot persistence, assistance, five-versus-fifteen copy, server feedback and a four-graded/one-skipped summary. LearnerHttpTest exercises owned session/attempt/exposure routes and serialized bodies with fixture authorization/no-store and strict decoding against an ephemeral HTTP server.

Root contract/token generation guards passed. Full offline Android results are in PROGRESS.md. An initial UI test used incorrect multi-slot fixture labels; corrected to the actual du/ihr fixture and rerun. Zero AI/Groq calls, production mutations, resets, deployments, content publication or store uploads.

## Reproduction and limits

Use the existing local API launcher and adb reverse tcp:5001 tcp:5001, then open LearnerPreviewActivity as described in [native shell verification](native-shell-verification.md). Complete B1 setup, Start practice, close/reopen to resume, submit or Skip and reach summary. Server restart resets the fixture; explicitly discard an unusable saved session before starting another.

Repository restart tests do not establish actual Android force-stop behavior. UI verification uses Robolectric. Real native HTTP against the authoritative API, TalkBack, hardware keyboard, large fonts, visual themes and device process-kill remain open. Production account partitions, cross-process outbox coordination, downloaded offline practice, reconciliation and native Topics/focus remain open. Web/backend/contracts were unchanged; their suites were not repeated.

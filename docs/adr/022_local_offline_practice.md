# ADR 022: Durable local offline practice and ordered delivery

Accepted for the isolated learner fixtures, 5 October 2026. Builds on ADR 021; this is not production identity or a complete M5 exit.

Web uses a new `german-master-v2-local-fixture` IndexedDB database. Download requests freeze before HTTP; a validated whole pack replaces the reserve and clears the request in one transaction. Integrity validation and crypto finish outside transactions. Web Locks retain the existing single-owner learner boundary, and transaction comparisons reject stale writes across database connections. Existing localStorage practice/profile/report requests remain intact.

Each start consumes one of two sessions and persists its full pinned practice atomically. Expiry prevents new starts only. Started sessions retain their own complete pack when the reserve refreshes, and remain available after expiry. Android persists consumed slots, active practice and completed offline sessions together in its existing fixture AtomicFile. Completed offline sessions can return Home without dropping queued writes, allowing the second reserved session to start.

The shared TypeScript grader and minimal Kotlin evaluator provide provisional feedback; neither reduces confirmed mastery nor schedules practice. The 36-case versioned fixture compares complete evaluations or rejection codes on both runtimes. Kotlin explicitly reproduces ECMAScript trim characters: generic Kotlin trim would mishandle BOM/control separators. All five forms, alternatives, NFC, whitespace, case, punctuation, assistance, slot identity and token ordering are covered.

Answer/Skip state changes, provisional feedback and frozen queued writes commit together. Completion follows all prior question events. Explicit sync delivers one event at a time, saving each receipt before continuing. Network failure, 429/auth failure, response loss and receipt-save failure stop delivery. Retry reuses original IDs/payloads; no timeout retry loop is introduced. Returned permanent rejections remain saved and block later events for review. Provisional feedback and current prompts are never replaced by server acknowledgments; differences appear separately in sync detail. Only authoritative reads refresh confirmed Progress.

This outbox coordinates answers, Skips and completion per started offline session. Existing profile/report/session-request mechanisms remain separate. Production subject partitioning, guest attachment, expired-auth/account-switch behavior, a coordinator spanning all write kinds, privacy actions, retention/recovery and two-device acceptance are still required. History is retained rather than silently pruned; storage failure blocks new saves while preserving committed work.

The web learner remains a development preview with no v2 offline app-shell/service-worker cold-launch guarantee. Native changes remain in debug/learnerPreview paths apart from the pure grader. Release routing/auth, reviewed content, device/accessibility acceptance and publication/cutover authorization remain separate.

No database migrations are added. Development migrations remain useful for fixture initialization; consolidate them into a clean v2 baseline before the first real staging/production database, as instructed by the owner. None currently has a claimed operational compatibility requirement.

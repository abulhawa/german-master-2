# ADR 014: Native Topics and focused practice

Accepted for the debug local fixture demonstration, 4 October 2026.

Add Topics, topic detail and target detail to LearnerPreviewActivity using existing solution-free v2 catalog contracts. Progress links to target detail. Show bilingual metadata, level, availability and joined server-confirmed state/checks/deadlines in the saved profile timezone. Shared snapshot freshness copy applies to details; due flags never change learner state. Unavailable metadata or zero availability is explicit. No explanations or examples are invented for the unpublished catalog.

Persist optional PracticeFocus alongside the existing mixed SessionRequest in NativePractice. Older AtomicFile records default to null focus. Before HTTP, freeze the request UUID, capabilities, bounded question count and focus together. Serialize the existing FocusedSessionRequest at the transport boundary; retry without a new focus uses the saved request. Target practice requests one question; topic practice requests up to five available questions. Server selection, evaluation and scheduling stay authoritative.

Saved practice, including creation awaiting acknowledgment, blocks any new focus. Resume continues its original mode and draft; existing confirmed discard is the only UI path to remove it. Pending preferences block new sessions but allow saved work to resume. Failures preserve frozen requests, confirmed snapshots and drafts. Navigation, localized copy, semantic headings and 48dp actions reuse the debug shell. Production account partitioning, cross-process coordination, offline reconciliation and device/accessibility acceptance remain open. Foundation previews, legacy release navigation and signing identity are preserved.

# ADR 011: Retry-safe web Skip

Accepted for the isolated local preview, 4 October 2026.

Use the existing v2 exposure contract for Skip in `/renovation`. Skip records exposure and completes the server-owned question without grading or advancing retention. Do not generate an answer or show correct/incorrect feedback for it. Advance only after an accepted or duplicate acknowledgment linked to the saved event ID.

Before HTTP, save a frozen event containing its UUID, question ID, immutable revision, fixture device ID, `skip` disposition and timestamp in the same journey record as the draft and position. Once pending, lock answer editing, hints and answer submission; retry the same event after failures/reload. An answer pending confirmation blocks Skip. Rejection preserves the event/draft and does not advance; fixture reset recovery continues to require explicit session discard. If saving the acknowledgment fails, the saved pending event remains available for duplicate replay.

Persist acknowledged advancement, draft clearing and the skipped count together. Summary renders graded answers, skipped questions and server-correct answers separately. Refresh confirmed target/sync data at completion through the existing atomic read flow; refresh failure retains prior confirmed data. Skip never adds a qualifying success on the client.

Keep the v1 fixture storage key and additive schema defaults (`pendingExposure: null`, `skippedCount: 0`) so older sessions, drafts and pending answers remain readable. This connected single-tab localStorage demonstration does not establish multi-tab coordination, production subject partitions or offline reconciliation.

The new secondary action uses the existing semantic button primitive, 50px measured controls, visible keyboard focus and English/German copy. ADR 008's intentional blueprint reset differences from legacy navigation, metrics and Radix/CVA guidance remain applicable. No dependencies, transport contracts, Android behavior or production routes change.

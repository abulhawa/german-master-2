# ADR 020: Frozen local session completion

Accepted for isolated learner previews, 5 October 2026.

POST /v2/sessions/{sessionId}/complete accepts a v2 request ID and full/partial intent. The backend locks the learner and derives counts from owned accepted rows. Full requires an answer or Skip for every question. Completion never substitutes for grading, mastery or scheduling evidence.

Migration 008 stores one immutable receipt per owned session and request ID. Replay returns original timestamp/counts; changed intent, request ID or session conflicts. Receipt and terminal status commit together. Partial end rejects new writes; accepted writes replay before the terminal check. Existing automatic completed status after the final answer remains; the explicit receipt confirms the learner action separately.

Clients freeze before sending and save receipts before dismissal/replacement. Storage failures and response loss preserve explicit retries across restart. Resolve pending answers/Skips before ending; coordinated offline delivery remains M5. Partial drafts/assistance remain saved until explicit dismissal/replacement. Summaries exclude untouched targets.

Close offers End session and Keep practising. Save and return Home preserves interruption and browsing while writes are pending. Web uses an inline confirmation with heading focus, semantic tokens and 48px controls; native uses AlertDialog. This follows the 2.0 blueprint over legacy sidebar/analytics guidance. Production routing/auth, signing and content publication are unchanged.

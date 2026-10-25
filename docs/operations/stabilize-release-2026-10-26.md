# Stabilize the core release — 26 October 2026

Owner-approved scope. This supersedes earlier requirements to activate social
providers or publish workplace content before the core release. Keep the current
architecture and existing accepted evidence; no new renovation or broad audit.

## Finite completion checklist

1. Fix whole-round save/resume, including zero-answer and partial drafts, feedback,
   round-only consent, retries, cancellation and account isolation. Native Continue
   must preserve its active session. Do not replace existing unfinished account work.
2. Hide unavailable Work choices and unverified social login behind disabled gates.
   Preserve their code and content drafts; retain email signup/sign-in.
3. Review the current general B1/B2 exercise bundle for ambiguity and serious errors.
   Distinguish model-assisted source review from live inventory and learner review.
4. Run bounded production-equivalent acceptance: signup, practice, save, resume,
   offline/reconnect, restart, sync, inspected export, logout/account switching,
   concurrency and authentication cancellation. Record client-specific evidence.
5. Freeze a passing candidate at an exact commit, record known limitations and usable
   rollback steps, and tag only when the candidate gates pass. Web deployment and
   Android Play publication remain separately authorized operations.
6. After the stable web release, run a small real-learner pilot: can visitors start
   unaided, understand answers/progress, save/leave/return and want another session?
   Do not substitute model or fixture feedback for participant evidence. No streak
   feature is required; evaluate any existing metric only if it is actually shown.

After the three core fixes, only reproducible critical failures, privacy/data
integrity defects and serious content errors block the candidate. Record lesser
issues for after release. Provider activation, unpublished workplace content,
analytics, redesign and Android store approval do not block the stable web release.
The Android hold and accepted TalkBack scope remain intact.

## Preserved audit findings

Astra/high reviewed revision 686deb1 without modifying it. It found that answered
attempt import was not whole-round saving; web consent stored an unused round id
while reading a different tab's draft; native Continue generated a fresh round;
Work content was unpublished; social providers lacked accepted real handoffs.
It recommended retaining the architecture. These are preserved design inputs,
not proof that runtime reproduction or a fix passed.

Additional handoff: practice_session.request_payload is immutable. An adoption
snapshot may use that existing field, but mutable resume state must not be written
there. Preserve ordered published revision identities and authoritative grading.

## Current evidence

Implementation and verification are recorded in the latest PROGRESS.md entry.
No release tag, rollout, live lifecycle pass or real-learner pilot is implied by
this checklist.

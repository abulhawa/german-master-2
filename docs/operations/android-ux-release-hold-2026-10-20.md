# Android UX issues and owner release hold

Status: **RELEASE BLOCKED. Review paused by owner on 20 October 2026.**

## Owner feedback

- The owner is not satisfied with the Android experience overall.
- There is too much help/explanatory text. Review the amount, placement and
  necessity of this copy so the main practice action is clear.
- Behaviour and presentation did not meet expectations. Specific additional
  behaviours have not yet been enumerated; do not invent a detailed defect list
  or treat this concern as resolved by copy changes alone.
- Practice must be available without signing in, as on web. Accounts are for
  online saving and cross-device sync. Registration and password recovery must
  be discoverable. These controls were recently implemented, but implementation
  and engineering checks are not owner acceptance of the resulting experience.
- Stop screenshot work for now, retain the issues, and continue later.

## Release boundary

No Android store upload, publication or promotion until the owner explicitly
lifts this hold after reviewing the revised UX. Preserve identity, signing and
existing user data. Do not infer approval from tests, debug installation,
screenshots, previous design acceptance or general permission to continue coding.

This is a tracked owner release decision enforced through project instructions;
no external Play Console switch or CI publishing mechanism was changed. The
accepted TalkBack scope is not reopened. M2 remains the only active milestone.

## Preserved work and evidence limits

Partial physical Pixel captures are retained locally outside the public checkout.
The ignored local output-path record identifies their directory. They include
account entry and the current guest/post-answer screen; no real email request
was submitted. A complete screenshot inventory and visual review were not achieved.

The opt-in rendered-capture tooling and its test-only dependency/configuration
edits remain unfinished and uncommitted. Pilot failures involved test runner
configuration, resource qualifier ordering, unsupported direct image capture and
an offline dependency version. Corrections were underway when work was stopped.
Do not describe these as passing or use incomplete/empty files as evidence.

Known implementation context: native guest drafts/results are local and are not
automatically transferred to an account; signed-in practice uses account sync.
This limitation must be made clear during review, without adding more technical
explanatory text to the main learner flow.

## Resume order

1. Wait for the owner to resume; do not restart screenshot capture automatically.
2. Review the main guest and signed-in journeys against web and owner expectations.
3. Agree concrete UX issues, simplify copy and actions, and verify the revised flow.
4. Resume an agreed screenshot scope only when useful; retain clear source labels
   for physical-device versus fixture-based renders.
5. Obtain explicit owner acceptance before lifting the Android release hold.

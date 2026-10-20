# Shared guest starter

`starter.json` is Android's bundled copy of the existing web guest starter in
`apps/web/client/src/renovation/guest-starter.tsx`: the same five questions,
immutable exercise/release identities, suggestions and provisional rubrics.
It introduces no new authored content or publication. Guest feedback is local;
it never establishes confirmed account progress or mastery.

Regenerate with `node tools/generate-native-guest.mjs` after an intentional web
starter change. `npm run check:generated` rejects drift. Private account packs
and authenticated outboxes do not use this guest asset.

Android persists its guest draft, position and local result counts separately
from account records. Creating/signing into an account does not silently attach
guest history. Native attachment of earlier guest answers is not implemented by
this entry-screen change; signed-in practice uses the existing account sync flow.

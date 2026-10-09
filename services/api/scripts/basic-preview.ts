import { PGlite } from '@electric-sql/pglite';
import { readFileSync } from 'node:fs';
import { buildBasicCandidate, basicContentHash } from '../src/basic-content';
import { FoundationStore } from '../src/store';
import { runtimeMembers, RuntimeCatalogSchema } from '../src/runtime-catalog';

const read = (path: string) => readFileSync(new URL(path, import.meta.url), 'utf8');

/** Local-only activation of the exact candidate, never a production connection or publication. */
export async function basicPreview() {
  const db = new PGlite();
  try {
    await new FoundationStore(db).initialize();
    await db.exec(read('../../../db/seed/production-starter.sql'));
    const b1 = JSON.parse(read('../../../content/drafts/initial-30.json'));
    const b2 = JSON.parse(read('../../../content/drafts/b2-basics.json'));
    const input = { ...b1, targets: [...b1.targets, ...b2.targets] };
    const previous = RuntimeCatalogSchema.parse(JSON.parse(read('../../../content/production/starter-catalog.json')));
    const old = await runtimeMembers(db, previous.releaseId);
    const draft = buildBasicCandidate(input, previous, old);
    if (JSON.stringify(draft.config) !== JSON.stringify(JSON.parse(read('../../../content/candidates/basics/catalog.json')))
      || draft.sql !== read('../../../content/candidates/basics/seed.sql').replace(/\r\n/g, '\n')) throw Error('Regenerate the candidate before previewing');
    // The service deliberately refuses draft releases. Simulate activation only in this new
    // in-memory database, preserving the authored payloads/rubrics and pending external review.
    const activated = buildBasicCandidate(input, previous, old, {
      contentHash: basicContentHash(input, previous), authorizedBy: 'Disposable local preview',
      authorizedAt: '2026-10-08T12:00:00Z', authorization: 'Synthetic local-only activation; no publication approval.',
      independentReview: 'pending',
    });
    await db.exec(activated.sql);
    return { db, store: new FoundationStore(db, undefined, undefined, undefined, activated.config),
      members: activated.members, candidateManifest: draft.config.manifestHash };
  } catch (error) { await db.close(); throw error; }
}

import { expect, it } from 'vitest';
import { PGlite } from '@electric-sql/pglite';
import { randomUUID } from 'node:crypto';
import { FoundationStore } from './store';
import { runtimeManifestHash, runtimeMembers, type RuntimeCatalog } from './runtime-catalog';
import sample from '../../../contracts/v2/examples/session.json';
import { foundationCatalog } from './catalog';
import type { SessionRequest } from '@german-master/contracts';

async function installSynthetic(db: PGlite, review = 'approved'): Promise<RuntimeCatalog> {
  const releaseId = randomUUID(), topic = randomUUID(), skill = randomUUID(), target = randomUUID(), exercise = randomUUID();
  const title = {en:'Synthetic reviewed test target',de:'Synthetisches Testlernziel'};
  const targets = [{id:target,topicId:topic,title,description:title,level:'B1' as const}];
  await db.query('INSERT INTO gm.topic VALUES($1,$2)',[topic,title]);
  await db.query('INSERT INTO gm.skill VALUES($1,$2,$3,NULL)',[skill,topic,'Synthetic test skill']);
  await db.query("INSERT INTO gm.learning_target VALUES($1,$2,'lexical','B1','Synthetic test objective','published')",[target,skill]);
  await db.query('INSERT INTO gm.exercise VALUES($1,$2)',[exercise,target]);
  const payload = {...sample.questions[0].exercise,id:exercise,targetId:target};
  const rubric = foundationCatalog().rubrics.get(`${sample.questions[0].exercise.id}@1`)!;
  await db.query('INSERT INTO gm.exercise_revision VALUES($1,1,$2,$3,$4,$5,$6,$7)',[exercise,payload.type,payload,rubric,'de-nfc-trim-v1','Synthetic local test, not independent approval',review]);
  const identity = (await db.query<Record<string,unknown>>('SELECT * FROM gm.revision_evidence_identity WHERE exercise_id=$1',[sample.questions[0].exercise.id])).rows[0];
  identity.exercise_id = exercise;
  const keys = Object.keys(identity);
  await db.query(`INSERT INTO gm.revision_evidence_identity (${keys.join(',')}) VALUES (${keys.map((_,i)=>`$${i+1}`).join(',')})`,Object.values(identity));
  await db.query("INSERT INTO gm.content_release VALUES($1,'published',$2,'2026-10-05T12:00:00Z')",[releaseId,'0'.repeat(64)]);
  await db.query('INSERT INTO gm.content_release_exercise VALUES($1,$2,1)',[releaseId,exercise]);
  const members = await runtimeMembers(db,releaseId);
  const manifestHash = runtimeManifestHash(releaseId,targets,members);
  await db.query('UPDATE gm.content_release SET manifest_hash=$2 WHERE id=$1',[releaseId,manifestHash]);
  return {releaseId,targets,manifestHash};
}
const request = (): SessionRequest => ({apiVersion:'v2',requestId:randomUUID(),questionCount:1,capabilities:['short_answer@1']});
it('routes catalog, sessions, packs and snapshots to configured content; retired release replay stays pinned',async()=> {
  const db = new PGlite();
  try {
    await new FoundationStore(db).initialize();
    const config = await installSynthetic(db); const owner = randomUUID();
    const store = new FoundationStore(db,undefined,undefined,undefined,config);
    const catalog = await store.catalog(owner);
    expect(catalog.status).toBe('published'); expect(catalog.targets.map(t=>t.id)).toEqual(config.targets.map(t=>t.id));
    expect(catalog.contentReleaseId).toBe(config.releaseId);
    const input = request(); const first = await store.createSession(owner,input);
    expect(first.contentReleaseId).toBe(config.releaseId); expect(first.questions[0].exercise.targetId).toBe(config.targets[0].id);
    const packInput = request(); const pack = await store.preparePack(owner,packInput);
    expect(pack.contentReleaseId).toBe(config.releaseId); expect(pack.sessions.every(s=>s.contentReleaseId===config.releaseId)).toBe(true);
    expect((await store.targets(owner)).targets.map(t=>t.targetId)).toEqual([config.targets[0].id]);
    await db.query("UPDATE gm.content_release SET status='retired' WHERE id=$1",[config.releaseId]);
    const replacement = await installSynthetic(db); const next = new FoundationStore(db,undefined,undefined,undefined,replacement);
    expect(await next.createSession(owner,input)).toEqual(first); expect(await next.preparePack(owner,packInput)).toEqual(pack);
    expect((await next.createSession(owner,request())).contentReleaseId).toBe(replacement.releaseId);
    await expect(store.createSession(owner,request())).rejects.toMatchObject({code:'content_unavailable'});
  } finally {await db.close();}
});
it('refuses unpublished, unreviewed, mismatched and missing catalogs without allocating practice',async()=> {
  const db = new PGlite();
  try {
    await new FoundationStore(db).initialize(); const config = await installSynthetic(db); const owner = randomUUID();
    const changed = {...config,targets:config.targets.map(t=>({...t,title:{en:'Changed',de:'Geändert'}}))};
    const pending = await installSynthetic(db,'pending');
    for(const value of [changed,pending,{...config,releaseId:randomUUID()}]) {
      const store = new FoundationStore(db,undefined,undefined,undefined,value);
      await expect(store.catalog()).rejects.toMatchObject({code:'content_unavailable'});
      await expect(store.preparePack(owner,request())).rejects.toMatchObject({code:'content_unavailable'});
    }
    await db.query("UPDATE gm.content_release SET status='draft' WHERE id=$1",[config.releaseId]);
    await expect(new FoundationStore(db,undefined,undefined,undefined,config).createSession(owner,request())).rejects.toMatchObject({code:'content_unavailable'});
    expect((await db.query('SELECT * FROM gm.practice_session')).rows).toHaveLength(0);
  } finally {await db.close();}
});

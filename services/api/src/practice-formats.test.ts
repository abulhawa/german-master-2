import { afterAll, beforeAll, expect, it } from 'vitest';
import { PGlite } from '@electric-sql/pglite';
import { randomUUID } from 'node:crypto';
import type { AddressInfo } from 'node:net';
import { SessionSchema, AttemptBatchResponseSchema, type Attempt } from '@german-master/contracts';
import source from '../../../contracts/v2/examples/practice-formats-session.json';
import rubrics from '../../../contracts/v2/examples/practice-formats-rubrics.json';
import { validatePreparedPack } from '@german-master/learning-engine';
import { FoundationStore } from './store';
import { createApi } from './server';
import { foundationCatalog } from './catalog';

const user = randomUUID();
let db: PGlite, store: FoundationStore, server: ReturnType<typeof createApi>, origin: string;
const input = () => ({apiVersion:'v2' as const,requestId:randomUUID(),questionCount:2,capabilities:['gap_choice@1','matching@1'] as ('gap_choice@1'|'matching@1')[]});
const post = async (path:string,body:unknown) => {
  const response = await fetch(origin+path,{method:'POST',headers:{'Content-Type':'application/json',Authorization:`Bearer ${user}`},body:JSON.stringify(body)});
  return {status:response.status,body:await response.json()};
};
beforeAll(async () => {
  db = new PGlite(); store = new FoundationStore(db,()=>new Date('2026-10-07T12:00:00Z')); await store.initialize();
  // Add unpublished engineering examples to this disposable fixture only.
  const release = foundationCatalog().session.contentReleaseId;
  for (const [index,q] of SessionSchema.parse(source).questions.entries()) {
    await db.query('INSERT INTO gm.exercise VALUES ($1,$2)',[q.exercise.id,q.exercise.targetId]);
    await db.query('INSERT INTO gm.exercise_revision VALUES ($1,$2,$3,$4,$5,$6,$7,$8)',
      [q.exercise.id,1,q.exercise.type,q.exercise,rubrics[index],'de-nfc-trim-v1','Local low-typing engineering sample','agent_reviewed_draft']);
    await db.query('INSERT INTO gm.content_release_exercise VALUES ($1,$2,$3)',[release,q.exercise.id,1]);
    const original = foundationCatalog().session.questions.find(o=>o.exercise.targetId === q.exercise.targetId)!;
    await db.query('INSERT INTO gm.revision_evidence_identity SELECT $1,$2,variant_key || $3,context_key || $3,transfer_key FROM gm.revision_evidence_identity WHERE exercise_id=$4 AND revision=$2',
      [q.exercise.id,1,`-formats-${index}`,original.exercise.id]);
  }
  server = createApi(store,async req=>req.headers.authorization === `Bearer ${user}` ? user : null);
  await new Promise<void>(resolve=>server.listen(0,'127.0.0.1',resolve));
  origin = `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
});
afterAll(async () => {if(server) await new Promise<void>(resolve=>server.close(()=>resolve()));if(db) await db.close();});

it('selects compatible questions and records exactly one event on frozen HTTP replay', async () => {
  const result = await post('/v2/sessions',input()); expect(result.status).toBe(200);
  const session = SessionSchema.parse(result.body);
  expect(session.questions.map(q=>q.exercise.type).sort()).toEqual(['gap_choice','matching']);
  expect(JSON.stringify(session)).not.toContain('acceptedAnswers');
  for (const [index,q] of session.questions.entries()) {
    const rubric = rubrics.find(r=>r.exerciseId === q.exercise.id)!;
    const attempt: Attempt = {attemptId:randomUUID(),sessionQuestionId:q.id,exerciseRevision:1,deviceId:randomUUID(),
      answer:rubric.acceptedAnswers[0] as Attempt['answer'],assistance:[],answeredAt:"2026-10-07T12:00:00Z",clientSequence:index};
    const incomplete: Attempt = {...attempt,attemptId:randomUUID(),answer:q.exercise.type === 'gap_choice'
      ? {type:'gap_choice',selections:[]} : {type:'matching',pairs:[]}};
    const rejected = await post('/v2/attempts:batch',{apiVersion:'v2',attempts:[incomplete]});
    expect(rejected.status).toBe(200);
    expect(AttemptBatchResponseSchema.parse(rejected.body).acknowledgments[0].status).toBe('rejected');
    const first = await post('/v2/attempts:batch',{apiVersion:'v2',attempts:[attempt]});expect(first.status).toBe(200);
    const again = await post('/v2/attempts:batch',{apiVersion:'v2',attempts:[attempt]});
    const accepted = AttemptBatchResponseSchema.parse(first.body).acknowledgments[0];
    const duplicate = AttemptBatchResponseSchema.parse(again.body).acknowledgments[0];
    expect(accepted.status).toBe('accepted');expect(duplicate.status).toBe('duplicate');
    if(accepted.status === 'rejected' || duplicate.status === 'rejected') throw Error('unexpected rejection');
    expect(duplicate.evaluation).toEqual(accepted.evaluation);
  }
  expect((await db.query<{count:number}>('SELECT count(*)::int AS count FROM gm.accepted_evidence WHERE user_id=$1',[user])).rows[0].count).toBe(2);
});
it('prepares complete verifiable offline packs for the new capabilities', async () => {
  const pack = await store.preparePack(randomUUID(),input());
  expect(await validatePreparedPack(pack)).toEqual(pack);
  expect(pack.sessions.every(s=>s.questions.every(q=>['gap_choice','matching'].includes(q.exercise.type)))).toBe(true);
});

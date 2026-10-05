import { afterAll, beforeAll, expect, it } from 'vitest';
import { PGlite } from '@electric-sql/pglite';
import { randomUUID, createHash } from 'node:crypto';
import { FoundationStore, canonical } from './store';
import { canStartPreparedPack, grade, validatePreparedPack } from '@german-master/learning-engine';
import { type SessionRequest, type PreparedPack } from '@german-master/contracts';

const user='00000000-0000-4000-8000-000000000010';
const other='00000000-0000-4000-8000-000000000011';
const request=():SessionRequest=>({apiVersion:'v2',requestId:randomUUID(),questionCount:5,capabilities:['short_answer@1','choice@1','cloze@1','word_order@1','multi_slot@1']});
let now=new Date('2026-10-05T10:00:00Z'); let db:PGlite; let store:FoundationStore;
beforeAll(async()=>{db=new PGlite();store=new FoundationStore(db,()=>now);await store.initialize();});
afterAll(async()=>{await db.close();});
const rehash=(pack:PreparedPack)=>{const {contentHash,...payload}=pack;return {...payload,contentHash:createHash('sha256').update(canonical(payload)).digest('hex')};};

it('allocates two owned pinned sessions and verifies hash/rubrics for all five forms',async()=>{
  const input=request(); const pack=await store.preparePack(user,input);
  expect(await validatePreparedPack(pack)).toEqual(pack);
  expect(pack.sessions).toHaveLength(2); expect(pack.rubrics).toHaveLength(5);
  expect(new Set(pack.sessions.flatMap(s=>s.questions.map(q=>q.id))).size).toBe(10);
  for(const session of pack.sessions) for(const q of session.questions) {
    const rubric=pack.rubrics.find(r=>r.exerciseId===q.exercise.id && r.exerciseRevision===q.exercise.revision)!;
    expect(grade(q.exercise,rubric,rubric.acceptedAnswers[0],[]).outcome).toBe('correct');
  }
  expect((await db.query('SELECT * FROM gm.accepted_evidence')).rows).toHaveLength(0);
  expect(await store.preparePack(user,input)).toEqual(pack);
  await store.initialize(); expect(await store.preparePack(user,input)).toEqual(pack);
  await expect(store.preparePack(user,{...input,questionCount:4})).rejects.toMatchObject({code:'pack_conflict'});
  const foreign=await store.preparePack(other,input);
  expect(foreign.sessions[0].id).not.toBe(pack.sessions[0].id);
});

it('expiry blocks new starts at its boundary but retains replay and late accepted writes',async()=>{
  const input=request();const pack=await store.preparePack(user,input);
  expect(canStartPreparedPack(pack,new Date(pack.issuedAt))).toBe(true);
  expect(canStartPreparedPack(pack,new Date(Date.parse(pack.expiresAt)-1))).toBe(true);
  now=new Date(pack.expiresAt);
  expect(canStartPreparedPack(pack,now)).toBe(false);
  expect(canStartPreparedPack(pack,new Date(Date.parse(pack.issuedAt)-1))).toBe(false);
  expect(await store.preparePack(user,input)).toEqual(pack);
  const q=pack.sessions[0].questions[0];const rubric=pack.rubrics.find(r=>r.exerciseId===q.exercise.id)!;
  const attempt={attemptId:randomUUID(),sessionQuestionId:q.id,exerciseRevision:q.exercise.revision,deviceId:randomUUID(),clientSequence:0,answer:rubric.acceptedAnswers[0],assistance:[],answeredAt:pack.issuedAt};
  expect((await store.submit(user,attempt,randomUUID())).status).toBe('accepted');
  expect((await store.submit(user,attempt,randomUUID())).status).toBe('duplicate');
});

it('rejects corrupted downloads, incompatible versions and hashed linkage/answer errors',async()=>{
  const pack=await store.preparePack(user,request());
  await expect(validatePreparedPack({...pack,expiresAt:pack.issuedAt})).rejects.toThrow();
  await expect(validatePreparedPack({...pack,evaluatorVersion:'unknown'})).rejects.toThrow();
  await expect(validatePreparedPack({...pack,sessions:[pack.sessions[0],pack.sessions[0]]})).rejects.toThrow();
  await expect(validatePreparedPack(rehash({...pack,sessions:[pack.sessions[0],pack.sessions[0]]}))).rejects.toThrow('Pack session mismatch');
  await expect(validatePreparedPack(rehash({...pack,rubrics:pack.rubrics.slice(1)}))).rejects.toThrow('Missing pack rubric');
  await expect(validatePreparedPack(rehash({...pack,rubrics:[...pack.rubrics,pack.rubrics[0]]}))).rejects.toThrow('Duplicate pack rubric');
  const corrupt=structuredClone(pack);corrupt.rubrics[0].acceptedAnswers=[{type:'choice',optionId:'unknown'}];
  await expect(validatePreparedPack(rehash(corrupt))).rejects.toThrow();
});

it('rolls back both allocations if pack persistence fails',async()=>{
  const before=await db.query('SELECT id FROM gm.practice_session');
  await db.exec('CREATE TRIGGER test_pack_failure BEFORE INSERT ON gm.prepared_pack FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation()');
  const input=request();
  try {await expect(store.preparePack(user,input)).rejects.toThrow();}
  finally {await db.exec('DROP TRIGGER test_pack_failure ON gm.prepared_pack');}
  expect((await db.query('SELECT id FROM gm.practice_session')).rows).toEqual(before.rows);
  const [a,b]=await Promise.all([store.preparePack(user,input),store.preparePack(user,input)]);
  expect(a).toEqual(b);
});

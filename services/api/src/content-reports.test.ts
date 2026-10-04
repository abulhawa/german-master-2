import { it, expect } from 'vitest';
import { PGlite } from '@electric-sql/pglite';
import { randomUUID } from 'node:crypto';
import type { AddressInfo } from 'node:net';
import { FoundationStore } from './store';
import { createApi } from './server';
import type { ContentReportRequest } from '@german-master/contracts';

it('records owned immutable revision reports over HTTP, safely replays and bounds new writes without learning effects',async()=>{
  const db=new PGlite();let now=new Date('2026-10-04T10:00:00Z');const store=new FoundationStore(db,()=>now);
  await store.initialize();await store.initialize();
  const user=randomUUID(),other=randomUUID();
  const session=await store.createSession(user,{apiVersion:'v2',requestId:randomUUID(),questionCount:5,capabilities:['short_answer@1','choice@1','cloze@1','multi_slot@1','word_order@1']});
  const question=session.questions[0];
  const server=createApi(store,async req=>[user,other].find(id=>req.headers.authorization===`Bearer ${id}`)??null);
  await new Promise<void>(resolve=>server.listen(0,'127.0.0.1',resolve));
  const post=async(input:unknown,owner: string=user)=>{
    const response=await fetch(`http://127.0.0.1:${(server.address() as AddressInfo).port}/v2/content-reports`,{method:'POST',headers:{'Content-Type':'application/json',Authorization:`Bearer ${owner}`},body:JSON.stringify(input)});
    return {status:response.status,body:await response.json()};
  };
  const request:ContentReportRequest={apiVersion:'v2',reportId:randomUUID(),sessionQuestionId:question.id,exerciseRevision:question.exercise.revision,category:'incorrect_answer'};
  try {
    expect((await post(request,'invalid')).status).toBe(401);
    expect((await post(request,other)).status).toBe(404);
    expect((await post({...request,exerciseRevision:99})).status).toBe(409);
    expect((await post({...request,category:'invalid'})).status).toBe(400);
    const accepted=await post(request);expect(accepted.status).toBe(200);
    expect(await post(request)).toEqual(accepted);
    expect((await post({...request,category:'other'})).status).toBe(409);
    for(let i=1;i<10;i++)expect((await post({...request,reportId:randomUUID()})).status).toBe(200);
    expect((await post({...request,reportId:randomUUID()})).status).toBe(429);
    expect(await post(request)).toEqual(accepted);
    expect((await db.query<{n:number}>('SELECT count(*)::int AS n FROM gm.content_report')).rows[0].n).toBe(10);
    expect((await db.query<{n:number}>('SELECT count(*)::int AS n FROM gm.attempt')).rows[0].n).toBe(0);
    expect((await db.query<{status:string}>('SELECT status FROM gm.practice_session')).rows[0].status).toBe('active');
    await expect(db.query('UPDATE gm.content_report SET category=$1',['other'])).rejects.toThrow('immutable');
    now=new Date(now.getTime()+3600000);
    expect((await post({...request,reportId:randomUUID()})).status).toBe(200);
  } finally {await new Promise<void>(resolve=>server.close(()=>resolve()));await db.close();}
});

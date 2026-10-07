import { expect, it } from 'vitest';
import { OfflineRubricSchema, type Answer } from '@german-master/contracts';
import packSource from '@german-master/contracts/examples/practice-formats-pack.json';
import { validatePreparedPack } from '@german-master/learning-engine';
import { WebReserve } from './reserve';
import { OfflineRepository } from './offline';
import { readyAnswer } from './storage';

it('restores incomplete new-format drafts and all four offline answers with frozen requests', async () => {
  const pack = await validatePreparedPack(packSource);
  const request = {apiVersion:'v2' as const,requestId:pack.packId,questionCount:4,capabilities:['choice@1','word_order@1','gap_choice@1','matching@1'] as const};
  const db = new WebReserve(`practice-formats-${crypto.randomUUID()}`);
  try {
    await db.freeze({...request,capabilities:[...request.capabilities]});
    await db.accept({...request,capabilities:[...request.capabilities]},pack);
    let repo = new OfflineRepository(db);
    const id = await repo.start(crypto.randomUUID(),new Date('2026-10-07T13:00:00Z'));
    for(const [index,q] of pack.sessions[0].questions.entries()) {
      const partial: Answer | null = q.exercise.type === 'gap_choice' ? {type:'gap_choice',selections:[{slotId:'wir',optionId:'b'}]}
        : q.exercise.type === 'matching' ? {type:'matching',pairs:[{leftId:'decision',rightId:'make'}]} : null;
      if(partial) {
        await repo.draft(id,partial);db.close();await db.open();repo = new OfflineRepository(db);
        expect((await repo.read(id)).practice.draft).toEqual(partial);
        expect(readyAnswer(q.exercise,partial)).toBeNull();
        await expect(repo.answer(id,partial)).rejects.toThrow();
      }
      const rubric = OfflineRubricSchema.parse(pack.rubrics.find(r=>r.exerciseId === q.exercise.id));
      const answer = rubric.acceptedAnswers[0];
      await repo.draft(id,answer);await repo.answer(id,answer);
      const before = (await repo.read(id)).practice;
      expect(before.feedback?.outcome).toBe('correct');
      db.close();await db.open();repo = new OfflineRepository(db);
      expect((await repo.read(id)).practice.events).toEqual(before.events);
      if(index < 3) await repo.next(id);
    }
    expect((await repo.read(id)).practice.events).toHaveLength(4);
  } finally { await db.delete(); }
});

import { describe, expect, it } from 'vitest';
import draft from '../../../content/drafts/initial-30.json';
import { validateDraftCatalog } from './content-review';

describe('initial editorial workspace', () => {
  it('validates 30 original targets, 60 variants and the intended low-typing forms without approving content', () => {
    const result = validateDraftCatalog(draft);
    expect([result.targets,result.variants,result.independentlyApproved]).toEqual([30,60,0]);
    expect(result.catalog.publicationApproved).toBe(false);
  });
  it.each(['linkage','duplicate','answer','context','approval','publication','solution'])('rejects broken %s boundaries', mode => {
    const data = structuredClone(draft);
    const target = data.targets[0];
    const variant = target.variants[0];
    if (mode === 'linkage') variant.exercise.targetId = data.targets[1].id;
    if (mode === 'duplicate') data.targets[1].id = target.id;
    if (mode === 'answer') Object.assign(variant.rubric.acceptedAnswers[0], {type:'choice',optionId:'unknown'});
    if (mode === 'context') target.variants[1].contextKey = variant.contextKey;
    if (mode === 'approval') target.review.status = 'approved';
    if (mode === 'publication') data.publicationApproved = true;
    if (mode === 'solution') Object.assign(variant.exercise,{acceptedAnswer:'leaked'});
    expect(() => validateDraftCatalog(data)).toThrow();
  });
  it('converts plural recognition to revision-2 choices with realistic distinct distractors', () => {
    const result = validateDraftCatalog(draft);
    const plurals = result.catalog.targets.filter(item => item.category === 'plural');
    expect(plurals).toHaveLength(10);
    const variants = plurals.flatMap(item => item.variants);
    expect(variants).toHaveLength(20);
    expect(variants.every(item => item.exercise.type === 'choice' && item.exercise.revision === 2)).toBe(true);
    for (const item of variants) {
      const answer = item.rubric.acceptedAnswers[0];
      if (item.exercise.type !== 'choice' || answer.type !== 'choice') throw Error('unexpected plural shape');
      expect(new Set(item.exercise.options.map(option => option.id)).size).toBe(4);
      expect(item.exercise.options.some(option => option.id === answer.optionId)).toBe(true);
    }
  });
  it('validates identities inside low-typing inputs', () => {
    const data = structuredClone(draft);
    const target = data.targets.find(item => item.category === 'adjective');
    expect(target).toBeDefined();
    const exercise = target!.variants[0].exercise as any;
    expect(exercise.type).toBe('gap_choice');
    exercise.slots[0].options[1].id = exercise.slots[0].options[0].id;
    expect(() => validateDraftCatalog(data)).toThrow('duplicate input identity');
  });
  it('invalidates sign-off after content changes', () => {
    const data=structuredClone(draft);
    const result=validateDraftCatalog(data);
    Object.assign(data.targets[0].review,{status:'approved',reviewer:'Test reviewer',date:'2026-10-04',notes:'Fixture approval only',reviewedHash:result.targetHashes.get(data.targets[0].id)});
    expect(validateDraftCatalog(data).independentlyApproved).toBe(1);
    data.targets[0].variants[0].exercise.prompt += ' Changed';
    expect(()=>validateDraftCatalog(data)).toThrow('matching content hash');
  });
});

import { describe, expect, it } from 'vitest';
import draft from '../../../content/drafts/initial-30.json';
import { validateDraftCatalog } from './content-review';

describe('initial editorial workspace', () => {
  it('validates 30 original targets, 60 variants and all initial forms without approving content', () => {
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
  it('invalidates sign-off after content changes', () => {
    const data=structuredClone(draft);
    const result=validateDraftCatalog(data);
    Object.assign(data.targets[0].review,{status:'approved',reviewer:'Test reviewer',date:'2026-10-04',notes:'Fixture approval only',reviewedHash:result.targetHashes.get(data.targets[0].id)});
    expect(validateDraftCatalog(data).independentlyApproved).toBe(1);
    data.targets[0].variants[0].exercise.prompt += ' Changed';
    expect(()=>validateDraftCatalog(data)).toThrow('matching content hash');
  });
});

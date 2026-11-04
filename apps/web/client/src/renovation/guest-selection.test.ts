import {it,expect} from 'vitest';
import {selectGuestQuestions} from './guest-selection';

it('samples across a larger library while freezing at most 125 distinct questions',()=>{
 const library=Array.from({length:400},(_,i)=>({id:i}));
 const before=JSON.stringify(library);
 const selected=selectGuestQuestions(library,()=>0);
 expect(selected).toHaveLength(125);
 expect(new Set(selected.map(q=>q.id)).size).toBe(125);
 expect(selected.some(q=>q.id>=125)).toBe(true);
 expect(JSON.stringify(library)).toBe(before);
 expect(selectGuestQuestions(library.slice(0,77))).toHaveLength(77);
 expect(selectGuestQuestions([])).toEqual([]);
});

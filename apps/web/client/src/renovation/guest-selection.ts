/** Bound one saved round, not the published library. Sample across the full eligible pool. */
export function selectGuestQuestions<T>(eligible:readonly T[],random:()=>number=Math.random):T[] {
  const shuffled=[...eligible];
  for(let i=shuffled.length-1;i>0;i--){const j=Math.floor(random()*(i+1));[shuffled[i],shuffled[j]]=[shuffled[j],shuffled[i]];}
  return shuffled.slice(0,125);
}

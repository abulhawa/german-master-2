import type { GuestRoundAdoptionResponse } from '@german-master/contracts';
import { sessionRequest } from '../foundation/api';
import { readJourney, saveJourney, type JourneyStorage } from './storage';

/** Called under the account's writer lock. Replaying a save cannot rewind it. */
export function installAdoptedRound(storage:JourneyStorage, saved:GuestRoundAdoptionResponse) {
  const state=readJourney(storage), prior=state.practice;
  if(prior?.session?.id===saved.session.id)return;
  if(prior && !prior.completionReceipt && (!prior.session || prior.index<prior.session.questions.length))
    throw Error('Finish the saved account round before saving another round');
  saveJourney(storage,{...state,practice:{
    request:{...sessionRequest(),requestId:saved.session.id,questionCount:saved.session.questions.length},
    session:saved.session,index:saved.index,
    draft:saved.draft ?? (saved.order?{type:'word_order',tokenIds:saved.order}:null),
    assisted:saved.assisted,pending:null,evaluation:saved.evaluation ?? null,
    rejected:false,confirmedCount:saved.confirmedCount,correctCount:saved.correctCount,
    skippedCount:saved.skippedCount,pendingExposure:null,
  }});
}

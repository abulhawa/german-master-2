import {afterEach,expect,it,vi} from 'vitest';
import {cleanup,fireEvent,render,screen} from '@testing-library/react';
import {GuestStarterJourney,TEMPORARY_KEY,SAVE_INTENT_KEY,authorizeGuestSave,hasGuestSaveIntent,buildGuestRoundAdoptionRequest,clearGuestPractice} from './guest-starter';
import {installAdoptedRound} from './adopted-round';
import {emptyJourney,readJourney,saveJourney} from './storage';
import {releaseCapabilities} from './release-capabilities';

const subject='00000000-0000-4000-8000-000000000020';
afterEach(()=>{cleanup();localStorage.clear();sessionStorage.clear();window.history.replaceState(null,'','/');});
function start(){render(<GuestStarterJourney onAuth={vi.fn()}/>);fireEvent.click(screen.getByRole('button',{name:'Start practice'}));return JSON.parse(sessionStorage.getItem(TEMPORARY_KEY)!);}

it('binds zero-answer save to the frozen round and initiating tab, rejecting another account',()=>{
 const original=start();authorizeGuestSave();
 const owner=sessionStorage.getItem('german-master-v2:save-practice-owner')!;
 const intent=localStorage.getItem(SAVE_INTENT_KEY);
 // A second tab owns unrelated temporary work and has no initiating-tab token.
 sessionStorage.clear();
 sessionStorage.setItem(TEMPORARY_KEY,JSON.stringify({...original,session:{...original.session,id:crypto.randomUUID()}}));
 expect(hasGuestSaveIntent()).toBe(false);
 expect(buildGuestRoundAdoptionRequest(subject)).toBeNull();
 clearGuestPractice();expect(localStorage.getItem(SAVE_INTENT_KEY)).toBe(intent);
 // Return to the consenting tab. The other draft never becomes the save input.
 sessionStorage.setItem('german-master-v2:save-practice-owner',owner);
 sessionStorage.setItem(TEMPORARY_KEY,JSON.stringify({...original,index:1}));
 const request=buildGuestRoundAdoptionRequest(subject)!;
 expect(request.requestId).toBe(original.session.id);
 expect(request.index).toBe(0);expect(request.attempts).toEqual([]);
 expect(request.questions).toHaveLength(5);
 expect(()=>buildGuestRoundAdoptionRequest('00000000-0000-4000-8000-000000000021')).toThrow(/another account/);
});

it('installs an unanswered draft durably, never rewinds replay or overwrites another active round',()=>{
 const guest=start(),storage=localStorage;
 const saved={apiVersion:'v2' as const,session:guest.session,index:0,draft:{type:'word_order' as const,tokenIds:['first']},order:['first'],assisted:true,confirmedCount:0,correctCount:0,skippedCount:0};
 saveJourney(storage,emptyJourney());
 installAdoptedRound(storage,saved);
 expect(readJourney(storage).practice).toMatchObject({session:guest.session,index:0,draft:saved.draft,assisted:true});
 const progressed=readJourney(storage);progressed.practice!.index=1;saveJourney(storage,progressed);
 installAdoptedRound(storage,saved);expect(readJourney(storage).practice!.index).toBe(1);
 expect(()=>installAdoptedRound(storage,{...saved,session:{...saved.session,id:crypto.randomUUID()}})).toThrow(/Finish/);
 expect(readJourney(storage).practice!.session!.id).toBe(saved.session.id);
});

it('hides unavailable workplace selection while preserving B1/B2 choices',()=>{
 expect(releaseCapabilities.workplacePractice).toBe(false);
 render(<GuestStarterJourney onAuth={vi.fn()}/>);
 fireEvent.click(screen.getByRole('button',{name:'Settings',exact:true}));
 expect(screen.queryByRole('option',{name:'German for work'})).toBeNull();
 expect(screen.getByRole('combobox',{name:'Level'})).toBeInTheDocument();
});

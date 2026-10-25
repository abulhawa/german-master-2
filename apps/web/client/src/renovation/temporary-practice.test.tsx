import {afterEach,expect,it,vi} from 'vitest';
import {cleanup,fireEvent,render,screen} from '@testing-library/react';
import {GuestStarterJourney,TEMPORARY_KEY,SAVE_INTENT_KEY} from './guest-starter';
afterEach(()=>{cleanup();localStorage.clear();sessionStorage.clear();window.history.replaceState(null,'','/');});
it('keeps a temporary round on cancel and discards only after an explicit choice',()=>{
 render(<GuestStarterJourney onAuth={vi.fn()}/>);
 fireEvent.click(screen.getByRole('button',{name:'Start practice'}));
 const round=JSON.parse(sessionStorage.getItem(TEMPORARY_KEY)!);
 expect(round.session.questions).toHaveLength(5);
 fireEvent.click(screen.getByRole('button',{name:'Account',exact:true}));
 expect(screen.getByRole('dialog')).toBeInTheDocument();
 fireEvent.click(screen.getByRole('button',{name:'Keep practising'}));
 expect(JSON.parse(sessionStorage.getItem(TEMPORARY_KEY)!).session.id).toBe(round.session.id);
 fireEvent.click(screen.getByRole('button',{name:'Close practice'}));
 fireEvent.click(screen.getByRole('button',{name:'Leave without saving'}));
 expect(sessionStorage.getItem(TEMPORARY_KEY)).toBeNull();
 expect(screen.getByRole('button',{name:'Start practice'})).toBeInTheDocument();
});
it('persists signup handoff only after the save action',()=>{
 const auth=vi.fn();render(<GuestStarterJourney onAuth={auth}/>);
 fireEvent.click(screen.getByRole('button',{name:'Start practice'}));
 expect(localStorage.getItem(SAVE_INTENT_KEY)).toBeNull();
 fireEvent.click(screen.getByRole('button',{name:'Close practice'}));
 fireEvent.click(screen.getByRole('button',{name:'Create account'}));
 expect(auth).toHaveBeenCalledWith('register');
 expect(localStorage.getItem(SAVE_INTENT_KEY)).not.toBeNull();
});

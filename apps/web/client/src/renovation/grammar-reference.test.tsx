import {afterEach,expect,it,vi} from 'vitest';
import {cleanup,fireEvent,render,screen,within} from '@testing-library/react';
import {GrammarReference} from './grammar-reference';
import {GuestStarterJourney,TEMPORARY_KEY} from './guest-starter';
import reference from '../../../../../content/reference/grammar-reference.json';

vi.mock('../../../../../content/guest/practice.json',async()=>{
 const {default:starter}=await import('../../../../../content/guest/starter.json');
 return {default:{packs:[{level:'B1',learningContext:'general',topicId:'30000000-0000-4000-8000-000000000600',title:{en:'Grammar foundations',de:'Grammatik-Grundlagen'},...starter}]}};
});
afterEach(()=>{cleanup();localStorage.clear();sessionStorage.clear();window.history.replaceState(null,"","/");vi.restoreAllMocks();});
function modalSupport() {
  if(!HTMLDialogElement.prototype.showModal) Object.defineProperty(HTMLDialogElement.prototype,'showModal',{configurable:true,writable:true,value:function(){}});
  if(!HTMLDialogElement.prototype.close) Object.defineProperty(HTMLDialogElement.prototype,'close',{configurable:true,writable:true,value:function(){}});
  vi.spyOn(HTMLDialogElement.prototype,'showModal').mockImplementation(function(this:HTMLDialogElement){this.setAttribute('open','');});
  vi.spyOn(HTMLDialogElement.prototype,'close').mockImplementation(function(this:HTMLDialogElement){this.removeAttribute('open');this.dispatchEvent(new Event('close'));});
}
it('exposes the complete legacy menu, grouped adjective tables, rules and examples without an account',()=>{
  modalSupport();render(<GrammarReference/>);
  fireEvent.click(screen.getByRole('button',{name:'Grammar',exact:true}));
  const dialog=within(screen.getByRole('dialog',{name:'Grammar reference'}));
  for(const topic of reference.topics) expect(dialog.getByRole('button',{name:topic.title.en,exact:true})).toBeEnabled();
  fireEvent.click(dialog.getByRole('button',{name:'Adjective endings',exact:true}));
  for(const table of reference.topics[0].tables) expect(dialog.getByRole('heading',{name:table.title})).toBeInTheDocument();
  expect(dialog.getAllByRole('table')).toHaveLength(3);
  expect(dialog.getAllByText('Examples')).toHaveLength(3);
  fireEvent.click(dialog.getAllByText('Examples')[0]);
  expect(dialog.getByText('M+Nom: Der neue Kollege ist nett.')).toBeVisible();
  fireEvent.click(dialog.getByRole('button',{name:'All topics'}));
  expect(dialog.getByRole('searchbox',{name:'Search grammar'})).toBeInTheDocument();
});
it('supports search, an empty result and German navigation',()=>{
  modalSupport();render(<GrammarReference locale="de"/>);
  fireEvent.click(screen.getByRole('button',{name:'Grammatik',exact:true}));
  const dialog=within(screen.getByRole('dialog',{name:'Grammatik-Referenz'}));
  fireEvent.change(dialog.getByRole('searchbox'),{target:{value:'relativ'}});
  expect(dialog.getByRole('button',{name:'Relativpronomen',exact:true})).toBeEnabled();
  expect(dialog.queryByRole('button',{name:'Passiv',exact:true})).toBeNull();
  fireEvent.change(dialog.getByRole('searchbox'),{target:{value:'zzzznomatch'}});
  expect(dialog.getByRole('status')).toHaveTextContent('Keine passenden Themen.');
  fireEvent.click(dialog.getByRole('button',{name:'Referenz schließen'}));
  expect(screen.queryByRole('dialog')).toBeNull();
});
it('opens and closes during practice without changing the saved draft or grading it',()=>{
  vi.spyOn(Math,'random').mockReturnValue(0.99999);
  modalSupport();const auth=vi.fn();render(<GuestStarterJourney onAuth={auth}/>);
  fireEvent.click(screen.getByRole('button',{name:'Start practice'}));
  fireEvent.click(screen.getByRole('radio',{name:'Berufe'}));
  const saved=sessionStorage.getItem(TEMPORARY_KEY);
  fireEvent.click(screen.getByRole('button',{name:'Grammar',exact:true}));
  const dialog=within(screen.getByRole('dialog',{name:'Grammar reference'}));
  fireEvent.click(dialog.getByRole('button',{name:'Relative pronouns',exact:true}));
  fireEvent.click(dialog.getByRole('button',{name:'Close reference'}));
  expect(screen.getByRole('radio',{name:'Berufe'})).toBeChecked();
  expect(sessionStorage.getItem(TEMPORARY_KEY)).toBe(saved);
  expect(auth).not.toHaveBeenCalled();
});

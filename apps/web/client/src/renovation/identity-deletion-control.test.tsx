import {afterEach,it,expect,vi} from 'vitest';
import {cleanup,fireEvent,render,screen} from '@testing-library/react';
import {IdentityDeletionControl} from './identity-deletion-control';
afterEach(cleanup);
it('requires explicit proof, clears the password on failure, and gives status recovery no proof',async()=>{
 const deliver=vi.fn(async()=>{throw Error('offline');});
 const {rerender}=render(<IdentityDeletionControl locale="en" marker={null} blocked={false} deliver={deliver}/>);
 fireEvent.click(screen.getByRole('button',{name:'Delete account…'}));
 fireEvent.change(screen.getByLabelText('Email'),{target:{value:'learner@example.com'}});
 fireEvent.change(screen.getByLabelText('Current password'),{target:{value:'transient'}});
 fireEvent.submit(screen.getByRole('button',{name:'Confirm account deletion'}).closest('form')!);
 await screen.findByRole('alert');expect(screen.getByLabelText('Current password')).toHaveValue('');
 expect(deliver).toHaveBeenCalledWith({email:'learner@example.com',password:'transient'});
 const marker={subject:'00000000-0000-4000-8000-000000000020',request:{apiVersion:'v2' as const,requestId:'00000000-0000-4000-8000-000000000021',recoveryCapability:'a'.repeat(43)},receipt:null,complete:false};
 rerender(<IdentityDeletionControl locale="en" marker={marker} blocked={false} deliver={deliver}/>);
 fireEvent.click(screen.getByRole('button',{name:'Check saved deletion status'}));
 await screen.findByRole('alert');expect(deliver).toHaveBeenLastCalledWith(undefined);
});

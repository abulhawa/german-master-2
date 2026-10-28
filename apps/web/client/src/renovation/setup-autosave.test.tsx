import {afterEach,expect,it,vi} from 'vitest';
import {cleanup,fireEvent,render,screen,waitFor} from '@testing-library/react';
import {ProfileSetup,PROFILE_PENDING_KEY} from './setup';
import {localLearnerApi} from './api';
import type {LearnerProfile} from '@german-master/contracts';

afterEach(()=>{cleanup();localStorage.clear();});
const profile:LearnerProfile={apiVersion:'v2',revision:1,setupCompleted:true,preferences:{locale:'en',timezone:'UTC',level:'B1',sessionQuestionCount:5,learningContext:'general'}};
it('saves a selected account preference immediately without a save button',async()=>{
  const current={...profile,revision:2,preferences:{...profile.preferences,level:'B2' as const}};
  const api={...localLearnerApi(),saveProfile:vi.fn(async()=>current),profile:vi.fn(async()=>current)};
  const saved=vi.fn();
  render(<ProfileSetup authenticated profile={profile} api={api} storage={localStorage} onSaved={saved} onReload={api.profile} onPreviewLocale={()=>{}}/>);
  expect(screen.queryByRole('button',{name:'Save preferences'})).toBeNull();
  fireEvent.change(screen.getByLabelText('Practice level'),{target:{value:'B2'}});
  await waitFor(()=>expect(saved).toHaveBeenCalledWith(current));
  expect(api.saveProfile).toHaveBeenCalledTimes(1);
  expect(localStorage.getItem(PROFILE_PENDING_KEY)).toBe('');
});
it('preserves a failed automatic write and retries the exact request after reopening',async()=>{
  const api={...localLearnerApi(),saveProfile:vi.fn().mockRejectedValueOnce(Error('lost')).mockResolvedValue(profile),profile:vi.fn(async()=>profile)};
  const props={authenticated:true,profile,api,storage:localStorage,onSaved:vi.fn(),onReload:api.profile,onPreviewLocale:()=>{}};
  const view=render(<ProfileSetup {...props}/>);
  fireEvent.change(screen.getByLabelText('Practice level'),{target:{value:'B2'}});
  await screen.findByRole('alert');
  const request=JSON.parse(localStorage.getItem(PROFILE_PENDING_KEY)!);
  view.unmount();render(<ProfileSetup {...props}/>);
  fireEvent.click(screen.getByRole('button',{name:'Retry'}));
  await waitFor(()=>expect(api.saveProfile).toHaveBeenCalledTimes(2));
  expect(api.saveProfile.mock.calls[1][0]).toEqual(request);
});

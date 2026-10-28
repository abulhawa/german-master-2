import { afterEach, expect, it, vi } from 'vitest';
import { render, screen, fireEvent, waitFor, cleanup } from '@testing-library/react';
import { PrivacyExport, PrivacySignOut } from './privacy';
import { localLearnerApi } from './api';
import { AccountBinding } from './account';
import type { LearnerExport } from '@german-master/contracts';
const subject='00000000-0000-4000-8000-000000000010';
const data:LearnerExport={apiVersion:'v2',schemaVersion:'learner-export-v1',subject,generatedAt:'2026-10-05T12:00:00Z',
  profile:{apiVersion:'v2',revision:0,setupCompleted:false,preferences:{locale:'en',timezone:'UTC',level:'B1',sessionQuestionCount:5}},
  sessions:[],attempts:[],evaluations:[],exposures:[],targets:[],reports:[],completions:[]};
afterEach(()=>{cleanup();vi.restoreAllMocks();vi.unstubAllGlobals();});
it('signs out an account without asking the learner to manage local data',async()=>{
  Object.defineProperty(HTMLDialogElement.prototype,'showModal',{configurable:true,value:function(this:HTMLDialogElement){this.setAttribute('open','');}});
  Object.defineProperty(HTMLDialogElement.prototype,'close',{configurable:true,value:function(this:HTMLDialogElement){this.removeAttribute('open');}});
  const leave=vi.fn(async()=>{});
  render(<PrivacySignOut locale="en" authenticated blocked={false} signedOut={false} complete={false} leave={leave} resume={()=>{}}/>);
  expect(screen.getAllByRole('button')).toHaveLength(1);
  fireEvent.click(screen.getByRole('button',{name:'Sign out',exact:true}));
  expect(leave).not.toHaveBeenCalled();
  expect(screen.getByRole('dialog',{name:'Sign out?'})).toBeVisible();
  fireEvent.click(screen.getByRole('button',{name:'Cancel',exact:true}));
  expect(leave).not.toHaveBeenCalled();
  fireEvent.click(screen.getByRole('button',{name:'Sign out',exact:true}));
  fireEvent.click(screen.getByRole('dialog',{name:'Sign out?'}).querySelector('.gm-settings-dialog-actions button:last-child')!);
  await waitFor(()=>expect(leave).toHaveBeenCalledExactlyOnceWith(false));
});
it('uses explicit export choices, blocks duplicate clicks and preserves retry after failure',async()=>{
  const create=vi.fn(()=> 'blob:fixture');vi.stubGlobal('URL',Object.assign(URL,{createObjectURL:create,revokeObjectURL:vi.fn()}));
  vi.spyOn(HTMLAnchorElement.prototype,'click').mockImplementation(()=>{});
  let finish!:(value:LearnerExport)=>void;
  const exportData=vi.fn(()=>new Promise<LearnerExport>(resolve=>{finish=resolve;}));
  render(<PrivacyExport locale="en" blocked={false} exportData={exportData}/>);
  fireEvent.click(screen.getByRole('button',{name:'Sync my work and download data'}));
  fireEvent.click(screen.getByRole('button',{name:'Download my learning data'}));
  expect(exportData).toHaveBeenCalledExactlyOnceWith(true);
  finish(data);await waitFor(()=>expect(screen.getByRole('status')).toHaveTextContent('Export downloaded'));
  expect(create).toHaveBeenCalledOnce();
  exportData.mockRejectedValueOnce(Error('offline'));
  fireEvent.click(screen.getByRole('button',{name:'Download my learning data'}));
  await waitFor(()=>expect(screen.getByRole('alert')).toHaveTextContent('Saved work remains'));
  expect(exportData).toHaveBeenLastCalledWith(false);
});
it('rejects foreign export and identity expiry during export JSON reading',async()=>{
  let active:{subject:string;generation:number}|null={subject,generation:0};
  const account=new AccountBinding(active,()=>active);
  vi.stubGlobal('fetch',vi.fn(async()=>({ok:true,json:async()=>({...data,subject:'00000000-0000-4000-8000-000000000099'})})));
  await expect(localLearnerApi(account).exportLearner!()).rejects.toThrow('account mismatch');
  vi.stubGlobal('fetch',vi.fn(async()=>({ok:true,json:async()=>{active=null;return data;}})));
  await expect(localLearnerApi(account).exportLearner!()).rejects.toThrow('Sign in');
});

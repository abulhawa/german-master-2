import { useEffect, useSyncExternalStore } from 'react';

export type LearnerView = 'home'|'practice'|'progress'|'topics'|'topic'|'target'|'account'|'downloads'|'sign-in'|'register'|'reset'|'resend'|'email-change';
export type LearnerRoute = {view:LearnerView;selectedId:string|null};
const changed='gm-navigation';
const pattern=/^#\/learn\/(home|practice|progress|topics|topic|target|account|downloads|sign-in|register|reset|resend|email-change)(?:\/([0-9a-f-]{36}))?$/;
export function readLearnerRoute():LearnerRoute {
  const match=pattern.exec(window.location.hash);
  if(!match||(['topic','target'].includes(match[1])&&!match[2]))return {view:'home',selectedId:null};
  return {view:match[1] as LearnerView,selectedId:match[2]??null};
}
function notify(){window.dispatchEvent(new Event(changed));}
export function navigateLearner(view:LearnerView,selectedId?:string|null,replace=false) {
  const hash=`#/learn/${view}${['topic','target'].includes(view)&&selectedId?`/${selectedId}`:''}`;
  if(window.location.hash===hash)return;
  const depth=Number.isSafeInteger(window.history.state?.gmDepth)?window.history.state.gmDepth:0;
  window.history[replace?'replaceState':'pushState']({germanMaster:true,gmDepth:depth+(replace?0:1)},'',hash);
  notify();
}
export function backInLearner(fallback:LearnerView='home') {
  if(window.history.state?.germanMaster && window.history.state.gmDepth>0)window.history.back();
  else navigateLearner(fallback,null,true);
}
function subscribe(listener:()=>void) {
  for(const event of ['popstate','hashchange',changed])window.addEventListener(event,listener);
  return ()=>{for(const event of ['popstate','hashchange',changed])window.removeEventListener(event,listener);};
}
export function useLearnerRoute() {
  useSyncExternalStore(subscribe,()=>window.location.hash,()=> '');
  useEffect(()=>{
    // One real Home entry, not a back-button trap. Leave provider/recovery fragments for Auth.
    if(!window.location.hash) {
      window.history.replaceState({germanMaster:true,gmDepth:0},'','#/learn/home');notify();
    }
  },[]);
  return readLearnerRoute();
}

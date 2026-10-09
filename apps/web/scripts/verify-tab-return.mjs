// Offline by default. --live explicitly inspects public deployed assets, while
// every auth/API request is fulfilled locally; no production account is used.
import assert from 'node:assert/strict';
import { readFile, mkdtemp } from 'node:fs/promises';
import { spawn } from 'node:child_process';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { chromium } from 'playwright';

const live = process.argv.includes('--live');
const expectReset = process.argv.includes('--expect-reset');
const guest = process.argv.includes('--guest');
const controlled = process.argv.includes('--controlled');
const failedVerification = process.argv.includes('--verification-failure');
if(controlled&&!live)throw Error('--controlled requires the explicitly authorized live diagnostic; routed static fixtures cannot populate a real worker precache.');
const origin = live ? 'https://germanmaster.qortxai.com' : 'https://learner.example';
const project = 'zgmyrpzwgtydwlzponih';
const subject = '00000000-0000-4000-8000-000000000020';
const example = async name => JSON.parse(await readFile(new URL(`../../../contracts/v2/examples/${name}.json`, import.meta.url), 'utf8'));
const catalog = await example('catalog');
const targets = await example('target-page');
const profile = {apiVersion:'v2',revision:1,setupCompleted:true,preferences:{locale:'en',timezone:'Europe/Berlin',level:'B1',sessionQuestionCount:15}};
const expires = Math.floor(Date.now()/1000) + 3600;
const token = `e30.${Buffer.from(JSON.stringify({iss:`https://${project}.supabase.co/auth/v1`,aud:'authenticated',role:'authenticated',sub:subject,session_id:subject,exp:expires})).toString('base64url')}.c2ln`;
const user = {id:subject,is_anonymous:false,aud:'authenticated',role:'authenticated'};
const session = {user,access_token:token,refresh_token:'synthetic-refresh',expires_at:expires,expires_in:3600,token_type:'bearer'};
// Playwright's normal connection forces focus emulation on its CDP session.
// A second session cannot undo that override. Connect without default overrides
// to a dedicated browser/profile so switching tabs produces real visibility.
const executable = process.env.GM_BROWSER_EXECUTABLE ?? chromium.executablePath();
const browserProfile = await mkdtemp(join(tmpdir(),'gm-tab-return-'));
const processHandle = spawn(executable,['--remote-debugging-port=0',`--user-data-dir=${browserProfile}`,'--no-first-run','--no-default-browser-check','about:blank'],{stdio:['ignore','ignore','pipe'],windowsHide:true});
const endpoint = await new Promise((resolve,reject)=>{
  processHandle.once('error',reject);
  processHandle.stderr.on('data',chunk=>{const match=chunk.toString().match(/DevTools listening on (ws:\/\/\S+)/);if(match)resolve(match[1]);});
  processHandle.once('exit',()=>reject(Error('Browser exited before CDP connection')));
});
const browser = await chromium.connectOverCDP(endpoint,{noDefaults:true});
try {
  const context = browser.contexts()[0];
  // Routing and service workers are independently inspected below. Auth/API
  // fixtures must never escape into a service-worker fetch or live network.
  const requests = [], errors = [];let rejectVerification=false;
  await context.route('**/*', async route => {
    const url = new URL(route.request().url());
    if (url.hostname.endsWith('.supabase.co')) {
      assert.equal(url.hostname, `${project}.supabase.co`);
      assert.equal(url.pathname, '/auth/v1/user');
      if(rejectVerification)return route.fulfill({status:503,json:{message:'Offline verification fixture'}});
      return route.fulfill({json:user});
    }
    if (url.pathname.startsWith('/v2/')) {
      assert.equal(route.request().method(), 'GET');
      const response = {'/v2/profile':profile,'/v2/catalog':catalog,'/v2/targets':targets,
        '/v2/sync':{apiVersion:'v2',changes:[],nextCursor:targets.syncCursor,hasMore:false}}[url.pathname];
      assert.ok(response, `Unexpected API route ${url.pathname}`);
      return route.fulfill({json:response});
    }
    assert.equal(url.origin, origin, 'Unexpected network destination');
    if(!live){
      const path=url.pathname==='/'?'index.html':url.pathname.slice(1);
      assert.ok(!path.includes('..'));
      const body=await readFile(new URL(`../dist/learner-product/${path}`,import.meta.url));
      const contentType=path.endsWith('.js')?'application/javascript':path.endsWith('.css')?'text/css':path.endsWith('.html')?'text/html':path.endsWith('.svg')?'image/svg+xml':'application/octet-stream';
      return route.fulfill({body,contentType});
    }
    return route.continue();
  });
  await context.addInitScript(({project,session,guest}) => {
    if(!guest)localStorage.setItem(`gm-v2-auth-${project}`,JSON.stringify(session));
    const events = window.__tabTrace = [];
    const log = (kind,data={}) => events.push({kind,time:Math.round(performance.now()),...data});
    document.addEventListener('visibilitychange',()=>log('visibility',{state:document.visibilityState}));
    window.addEventListener('pageshow',event=>log('pageshow',{persisted:event.persisted}));
    window.addEventListener('pagehide',()=>log('pagehide'));
    navigator.serviceWorker.addEventListener('controllerchange',()=>log('controllerchange'));
    const clients = new WeakSet(), ids = new WeakMap(); let next = 0;
    window.__REACT_DEVTOOLS_GLOBAL_HOOK__ = {supportsFiber:true,inject:()=>1,onCommitFiberUnmount:()=>{},
      onCommitFiberRoot(_id,root) {
        function visit(fiber) {
          if(!fiber)return;
          const host=fiber.memoizedProps?.host;
          if(host && !clients.has(host.client)) {
            clients.add(host.client);
            host.client.auth.onAuthStateChange((event,session)=>log('auth',{event,hasSession:!!session}));
          }
          const account=fiber.memoizedProps?.account;
          if(account && typeof fiber.type==='function') {
            if(!ids.has(fiber)){ids.set(fiber,++next);if(fiber.alternate)ids.set(fiber.alternate,next);}
            const views=[];let hook=fiber.memoizedState;
            while(hook){if(['home','practice','progress','topics','topic','target','account'].includes(hook.memoizedState))views.push(hook.memoizedState);hook=hook.next;}
            log('component',{name:fiber.type.name,id:ids.get(fiber),generation:account.identity.generation,views});
          }
          visit(fiber.child);visit(fiber.sibling);
        }
        visit(root.current);
      }};
  },{project,session,guest});
  const page=await context.newPage();
  page.on('request',r=>requests.push({path:new URL(r.url()).pathname,type:r.resourceType()}));
  page.on('pageerror',e=>errors.push(e.message));
  await page.goto(origin);
  if(controlled){await page.evaluate(()=>Promise.race([navigator.serviceWorker.ready,new Promise((_,reject)=>setTimeout(()=>reject(Error('Worker registration unavailable')),15000))]));await page.reload();await page.waitForFunction(()=>!!navigator.serviceWorker.controller);}
  let heading;
  if(guest){
    await page.getByRole('button',{name:'Try German Master',exact:true}).click();
    const answer=page.getByLabel('Your answer');await answer.fill('retained guest draft');
    heading=answer;
  }else{
  await page.getByRole('button',{name:'Browse topics',exact:true}).waitFor();
  // Use the visible Home topic card, regardless of localized ordinal decoration.
  const homeCards=page.getByRole('button').filter({hasText:catalog.topics[0].title.en});
  await homeCards.first().click();
  heading=page.getByRole('heading',{name:catalog.topics[0].title.en,exact:true});
  await heading.waitFor();
  }
  // Let the dedicated headed browser settle before measuring tab transitions.
  await page.waitForTimeout(1000);await page.bringToFront();
  const before=await page.evaluate(()=>({timeOrigin:performance.timeOrigin,trace:window.__tabTrace.length}));
  rejectVerification=failedVerification;
  const away=await context.newPage();await away.goto('data:text/html,<title>Other tab</title>');await away.bringToFront();
  // requestAnimationFrame polling stops in background tabs.
  await page.waitForFunction(()=>document.visibilityState==='hidden',null,{polling:100,timeout:5000}).catch(async error=>{
    console.log('Visibility diagnostic',await page.evaluate(()=>({state:document.visibilityState,events:window.__tabTrace.filter(x=>x.kind==='visibility')})),await away.evaluate(()=>document.visibilityState));throw error;
  });
  await page.bringToFront();
  await page.waitForFunction(()=>document.visibilityState==='visible');
  if(!guest)await page.waitForFunction(start=>window.__tabTrace.slice(start).some(x=>x.kind==='auth'&&x.event==='SIGNED_IN'),before.trace);
  // Allow the asynchronous verified binding and React ownership lease to settle.
  await page.waitForTimeout(800);
  const after=await page.evaluate(()=>({timeOrigin:performance.timeOrigin,trace:window.__tabTrace,
    sw:{controller:navigator.serviceWorker.controller?.scriptURL??null},heading:document.querySelector('h1')?.textContent,
    navigation:performance.getEntriesByType('navigation').map(x=>({type:x.type,start:x.startTime})),
    url:location.pathname,historyLength:history.length}));
  assert.equal(before.timeOrigin,after.timeOrigin,'Document reloaded');
  assert.equal(requests.filter(r=>r.type==='document').length,controlled?2:1);
  assert.deepEqual(errors,[]);
  assert.equal(await heading.isVisible(),!expectReset);
  if(guest)assert.equal(await heading.inputValue(),'retained guest draft');
  const transitions=after.trace.slice(before.trace).filter(x=>x.kind==='visibility').map(x=>x.state);
  assert.deepEqual(transitions,['hidden','visible']);
  const beforeGenerations=after.trace.slice(0,before.trace).filter(x=>x.kind==='component').map(x=>x.generation);
  const afterGenerations=after.trace.slice(before.trace).filter(x=>x.kind==='component').map(x=>x.generation);
  if(!guest&&!expectReset)assert.ok(afterGenerations.every(x=>x===beforeGenerations.at(-1)),'Account generation changed');
  if(!guest&&!expectReset){
    const ids=new Set(after.trace.slice(0,before.trace).filter(x=>x.kind==='component').map(x=>x.id));
    assert.ok(after.trace.slice(before.trace).filter(x=>x.kind==='component').every(x=>ids.has(x.id)),'Learner component remounted');
  }
  if(controlled)assert.ok(after.sw.controller);
  console.log(JSON.stringify({mode:live?'deployed assets with synthetic local services':'offline product build',expectReset,requests,after},null,2));
} finally {await browser.close();processHandle.kill();}

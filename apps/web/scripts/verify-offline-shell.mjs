// Run against the explicit isolated build + fresh local API, never production.
import assert from 'node:assert/strict';
import { mkdtemp, readFile, writeFile } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { pathToFileURL } from 'node:url';
import { fileURLToPath } from 'node:url';
import { spawn } from 'node:child_process';
import { createConnection } from 'node:net';
import { preview } from 'vite';

const { chromium } = await import(process.env.GM_PLAYWRIGHT_MODULE
  ? pathToFileURL(process.env.GM_PLAYWRIGHT_MODULE).href : 'playwright');
const url = 'http://127.0.0.1:5010/learner-preview/';
const profile = await mkdtemp(join(tmpdir(), 'gm-v2-shell-'));
const errors = [];
let context;
let previewServer;
let apiProcess;
const workerPath = new URL('../dist/learner-preview/sw.js', import.meta.url);
let originalWorker;
async function assertUnused(port) {
  await new Promise((resolve, reject) => {
    const socket = createConnection({ host: '127.0.0.1', port });
    socket.once('connect', () => { socket.destroy(); reject(Error(`Fixture port ${port} already in use`)); });
    socket.once('error', error => error.code === 'ECONNREFUSED' ? resolve() : reject(error));
  });
}
async function startServers() {
  await assertUnused(5010); await assertUnused(5011);
  apiProcess = spawn(process.execPath, ['--import', 'tsx', fileURLToPath(new URL('../../../services/api/src/local.ts', import.meta.url))], {
    cwd: fileURLToPath(new URL('../../../', import.meta.url)),
    env: { ...process.env, GM_DEMO_PORT: '5011' }, stdio: ['ignore', 'ignore', 'inherit'],
  });
  let ready = false;
  for (let i = 0; i < 100; i++) {
    if (apiProcess.exitCode !== null) throw Error('Fresh fixture API failed to start');
    try { await fetch('http://127.0.0.1:5011/v2/profile'); ready = true; break; } catch { /* Startup only; no writes/retries. */ }
    await new Promise(resolve => setTimeout(resolve, 100));
  }
  if (!ready) throw Error('Fresh fixture API startup timed out');
  previewServer = await preview({ configFile: fileURLToPath(new URL('../vite.learner-preview.config.ts', import.meta.url)) });
}
async function launch(offline) {
  context = await chromium.launchPersistentContext(profile, { headless: true,
    executablePath: process.env.GM_CHROMIUM_EXECUTABLE, viewport: { width: 320, height: 800 } });
  await context.setOffline(offline);
  const page = context.pages()[0];
  page.on('pageerror', error => errors.push(error.message));
  await page.goto(url);
  return page;
}
const button = (page, name) => page.getByRole('button', { name, exact: true });
try {
  if (!process.argv.includes('--existing-servers')) await startServers();
  let page = await launch(false);
  await page.getByText('Local preview saved for offline launch.', { exact: true }).waitFor();
  await button(page, 'Save preferences').click();
  await button(page, 'Download two sessions').click();
  await page.getByText(/^Sessions available to start\s*:\s*2$/).waitFor();
  await page.evaluate(async () => {
    localStorage.setItem('unrelated-shell-test', 'preserve');
    const cache = await caches.open('unrelated-shell-test');
    await cache.put('/sentinel', new Response('preserve'));
    // Workbox's broad cleanup would wrongly delete this non-owned precache.
    const other = await caches.open(`unrelated-precache-${location.origin}/learner-preview/`);
    await other.put('/precache-sentinel', new Response('preserve'));
  });
  // Entire browser closes before going offline: no live document/module cache.
  await context.close();
  page = await launch(true);
  await page.getByText(/^Sessions available to start\s*:\s*2$/).waitFor();
  await button(page, 'Start downloaded practice').click();
  await button(page, 'Hint').click();
  const input = page.locator('.gm-offline-answer input').first();
  await input.fill('Berufe');
  await page.waitForFunction(async () => {
    const db = await new Promise((resolve, reject) => {
      const request = indexedDB.open('german-master-v2-local-fixture');
      request.onsuccess = () => resolve(request.result); request.onerror = () => reject(request.error);
    });
    try {
      const practices = await new Promise((resolve, reject) => {
        const request = db.transaction('practices').objectStore('practices').getAll();
        request.onsuccess = () => resolve(request.result); request.onerror = () => reject(request.error);
      });
      return practices.some(p => p.assisted && p.draft?.text === 'Berufe');
    } finally { db.close(); }
  });
  await context.close();
  page = await launch(true);
  await button(page, 'Open saved session 1').click();
  await page.locator('.gm-offline-answer input').first().waitFor();
  assert.equal(await page.locator('.gm-offline-answer input').first().inputValue(), 'Berufe');
  await button(page, 'Check answer').click();
  await button(page, 'Continue').waitFor();
  await page.reload();
  await button(page, 'Open saved session 1').click();
  await button(page, 'Continue').click();
  for (let i = 0; i < 4; i++) {
    await button(page, 'Skip').click();
    if (i < 3) await page.getByText(new RegExp(`^Question ${i + 3}\\s*/\\s*5$`)).waitFor();
  }
  await button(page, 'End session').click();
  await button(page, 'Sync saved work (6)').waitFor();
  await context.setOffline(false);
  originalWorker = await readFile(workerPath, 'utf8');
  await writeFile(workerPath, originalWorker + '\n// Isolated acceptance update\n');
  await page.evaluate(async () => { await (await navigator.serviceWorker.getRegistration()).update(); });
  await page.waitForFunction(async () => !!(await navigator.serviceWorker.getRegistration()).waiting);
  // Installing a new worker must not reload or change the current summary.
  await button(page, 'Sync saved work (6)').waitFor();
  await context.close();
  page = await launch(true);
  await button(page, 'Open saved session 1').click();
  await button(page, 'Sync saved work (6)').waitFor();
  const before = await page.evaluate(async () => {
    const keys = await caches.keys();
    const entries = [];
    for (const key of keys.filter(k => k.startsWith('german-master-v2-local-shell'))) {
      for (const request of await (await caches.open(key)).keys()) entries.push(new URL(request.url).pathname);
    }
    return { keys, entries, sentinel: await (await (await caches.open('unrelated-shell-test')).match('/sentinel')).text(),
      precacheSentinel: await (await (await caches.open(`unrelated-precache-${location.origin}/learner-preview/`)).match('/precache-sentinel')).text(),
      localSentinel: localStorage.getItem('unrelated-shell-test'),
      scope: (await navigator.serviceWorker.getRegistration()).scope,
      overflow: document.documentElement.scrollWidth > innerWidth };
  });
  assert.equal(before.scope, url);
  assert.equal(before.sentinel, 'preserve'); assert.equal(before.localSentinel, 'preserve');
  assert.equal(before.precacheSentinel, 'preserve');
  assert.equal(before.overflow, false);
  assert.ok(before.entries.length >= 4);
  assert.ok(before.entries.every(path => path.startsWith('/learner-preview/') && !path.includes('/v2/')));
  await page.evaluate(async () => {
    try { await fetch('/v2/profile'); throw Error('Offline API unexpectedly cached'); }
    catch (error) { if (error.message === 'Offline API unexpectedly cached') throw error; }
  });
  await context.setOffline(false);
  await button(page, 'Sync saved work (6)').click();
  await button(page, 'Sync saved work (0)').waitFor();
  assert.deepEqual(errors, []);
  console.log(JSON.stringify({ result: 'passed', browser: await context.browser().version(),
    evidence: ['browser restart offline', 'assisted draft retained', 'feedback retained on reload',
      'offline completion and late sync', 'waiting update without reload', 'scoped asset-only cache',
      'unrelated precache/storage retained across activation', '320px reflow'], ...before }, null, 2));
} finally {
  await context?.close();
  if (originalWorker !== undefined) await writeFile(workerPath, originalWorker);
  await new Promise(resolve => previewServer ? previewServer.httpServer.close(resolve) : resolve());
  apiProcess?.kill();
}

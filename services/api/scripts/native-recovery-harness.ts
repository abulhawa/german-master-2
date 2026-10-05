// Disposable authoritative service for JVM integration. Controls use stdin, never HTTP.
import { PGlite } from '@electric-sql/pglite';
import { createInterface } from 'node:readline';
import type { AddressInfo } from 'node:net';
import { FoundationStore } from '../src/store';
import { createApi } from '../src/server';
import {readFile} from 'node:fs/promises';
import {IdentityDeletionService} from '../src/identity-deletion';
import {identityDeletionHttp,serializedDeletionWorker} from '../src/identity-deletion-http';

const db = new PGlite();
let now = Date.parse('2026-10-04T12:00:00Z');
let expirePage = false;
const calls: string[] = [];
const store = new FoundationStore(db, () => new Date(now), 1000, 2000);
await store.initialize();
await db.exec(await readFile(new URL('../../../db/baseline/identity-deletion.sql',import.meta.url),'utf8'));
let identityExists=true;
const identityService=new IdentityDeletionService(db,()=>store,{inspect:async()=>({exists:identityExists,activeSessions:identityExists}),remove:async()=>{identityExists=false;}},
  async proof=>(proof as {password:string}).password==='fresh-test-proof'?{subject:'00000000-0000-4000-8000-000000000010',authenticatedAt:Date.now()}:null);
const authenticate = async (request:import('node:http').IncomingMessage) => {
  calls.push(`${request.method} ${request.url}`);
  if (!identityExists || request.headers.authorization !== 'Bearer foundation-local-demo') return null;
  const url = new URL(request.url!, 'http://localhost');
  if (url.pathname === '/v2/targets') {
    if (url.searchParams.has('cursor') && expirePage) {
      expirePage = false;
      now += 2000;
      await store.cleanupReads();
    }
  }
  return '00000000-0000-4000-8000-000000000010';
};
const server = createApi(store, authenticate,undefined,identityDeletionHttp(identityService,authenticate,undefined,serializedDeletionWorker(identityService)));
// Force pagination before the real route parser runs, without changing the shipped API.
server.prependListener('request', request => {
  const url = new URL(request.url!, 'http://localhost');
  if (url.pathname === '/v2/targets' && !url.searchParams.has('cursor')) request.url = '/v2/targets?limit=1';
});
await new Promise<void>(resolve => server.listen(0, '127.0.0.1', resolve));
console.log(JSON.stringify({ port: (server.address() as AddressInfo).port }));
for await (const line of createInterface({ input: process.stdin })) {
  const command = JSON.parse(line);
  if (command.action === 'expire') { now += 1000; expirePage = true; }
  else if (command.action === 'expire-pack') { now += 8 * 24 * 60 * 60 * 1000; }
  else if (command.action === 'stats') {
    const evidence = (await db.query('SELECT id FROM gm.accepted_evidence')).rows.length;
    const pages = (await db.query('SELECT id FROM gm.target_page')).rows.length;
    console.log(JSON.stringify({ evidence, pages, calls }));
    continue;
  } else if (command.action === 'close') break;
  else throw Error('Unknown harness command');
  console.log('{}');
}
await new Promise<void>(resolve => server.close(() => resolve()));
await db.close();

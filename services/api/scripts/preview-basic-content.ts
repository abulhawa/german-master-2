import { createInterface } from 'node:readline';
import type { AddressInfo } from 'node:net';
import { createApi } from '../src/server';
import { basicPreview } from './basic-preview';

const stdio = process.argv.includes('--stdio');
const portIndex = process.argv.indexOf('--port');
const port = portIndex < 0 ? (stdio ? 0 : 5001) : Number(process.argv[portIndex + 1]);
if (!Number.isInteger(port) || port < (stdio ? 0 : 1) || port > 65535) throw Error('Invalid preview port');
const { db, store, members, candidateManifest } = await basicPreview();
const subject = '00000000-0000-4000-8000-000000000010';
const server = createApi(store, async request => request.headers.authorization === 'Bearer foundation-local-demo' ? subject : null);
await new Promise<void>(resolve => server.listen(port, '127.0.0.1', resolve));
const actualPort = (server.address() as AddressInfo).port;
let closed = false;
async function close() {
  if (closed) return; closed = true;
  await new Promise<void>(resolve => server.close(() => resolve())); await db.close();
}
for (const signal of ['SIGINT', 'SIGTERM'] as const) process.on(signal, () => { void close(); });
if (stdio) {
  console.log(JSON.stringify({ port: actualPort, candidateManifest }));
  try {
    for await (const line of createInterface({ input: process.stdin })) {
      const command = JSON.parse(line);
      if (command.action === 'close') break;
      if (command.action === 'answers') console.log(JSON.stringify({ variants: members.filter(m => m.revision === 2)
        .map(m => ({ exercise: m.payload, rubric: { ...(m.rubric as object), exerciseId: m.exercise_id, exerciseRevision: m.revision } })) }));
      else if (command.action === 'stats') console.log(JSON.stringify({ evidence: (await db.query('SELECT id FROM gm.accepted_evidence')).rows.length }));
      else throw Error('Unknown preview command');
    }
  } finally { await close(); }
} else console.log(`Unpublished basic candidate preview: http://127.0.0.1:${actualPort}; in-memory only, resets on restart; manifest ${candidateManifest}`);

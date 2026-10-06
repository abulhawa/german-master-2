import type { IncomingMessage, ServerResponse } from 'node:http';
import { readFileSync } from 'node:fs';
import { PostgresDatabase } from './postgres';
import { createNetworkApi } from './network-api';
import { createSupabaseAuthenticate, currentAuthSession } from './supabase-auth';
import { FoundationStore } from './store';
import { IdentityDeletionService } from './identity-deletion';
import { createPasswordDeletionReauthentication } from './deletion-reauthentication';
import { createSupabaseIdentityDeletionProvider } from './supabase-identity-deletion';
import catalog from '../../../content/production/catalog.json';
import { RuntimeCatalogSchema } from './runtime-catalog';

const project = 'zgmyrpzwgtydwlzponih';
function required(name: string) {
  const value = process.env[name];
  if (!value) throw Error(`Missing production configuration: ${name}`);
  return value;
}
function connect(name: string) {
  const connectionString = required(name);
  const url = new URL(connectionString);
  const roles: Record<string,string> = {GM_DATABASE_URL:'gm_app_backend',GM_AUTH_DATABASE_URL:'gm_app_auth',GM_PRIVACY_DATABASE_URL:'gm_app_privacy',GM_IDENTITY_DATABASE_URL:'gm_app_identity'};
  if (url.hostname !== 'aws-1-eu-central-1.pooler.supabase.com' || decodeURIComponent(url.username) !== `${roles[name]}.${project}`)
    throw Error('Dedicated production database connection required');
  const ca = readFileSync(new URL('../supabase-ca.pem', import.meta.url), 'utf8');
  return PostgresDatabase.connect({ connectionString, ssl: { rejectUnauthorized: true, ca }, max: 2 });
}
async function compose() {
  if (required('GM_ENVIRONMENT') !== 'production' || required('VITE_V2_AUTH_PROJECT') !== project)
    throw Error('Dedicated production environment required');
  const runtimeCatalog = RuntimeCatalogSchema.parse(catalog);
  const learning = connect('GM_DATABASE_URL');
  const verifier = connect('GM_AUTH_DATABASE_URL');
  const sessionExists = currentAuthSession(verifier, true);
  const authenticate = createSupabaseAuthenticate(project, required('VITE_V2_AUTH_PUBLISHABLE_KEY'), sessionExists);
  // Provider removal is separate from owned learning deletion and requires
  // freshly verified password proof plus authoritative identity/session absence.
  let deletion: IdentityDeletionService | undefined;
  let deliver: ((requestId:string)=>Promise<unknown>) | undefined;
  if (process.env.GM_IDENTITY_DELETION === 'enabled') {
    const worker = connect('GM_PRIVACY_DATABASE_URL');
    const workerLock = connect('GM_PRIVACY_DATABASE_URL');
    const observer = connect('GM_IDENTITY_DATABASE_URL');
    const provider = createSupabaseIdentityDeletionProvider(project, required('GM_SUPABASE_SECRET_KEY'), observer, true);
    const reauthenticate = createPasswordDeletionReauthentication(project, required('VITE_V2_AUTH_PUBLISHABLE_KEY'), sessionExists);
    deletion = new IdentityDeletionService(worker,
      subject => new FoundationStore(learning.forSubject(subject), undefined, undefined, undefined, runtimeCatalog), provider, reauthenticate);
    const service = deletion;
    // A transaction lock coordinates admitted jobs across serverless instances.
    // The service uses a separate pool so lock waiters cannot exhaust its pool.
    deliver = requestId => workerLock.transaction(async tx => {
      await tx.query('SELECT pg_advisory_xact_lock(1735202,hashtext($1))',[requestId]);
      return service.continue(requestId);
    });
  }
  return createNetworkApi(learning, authenticate, runtimeCatalog, undefined, deletion, deliver);
}
let server: ReturnType<typeof compose> | undefined;
export async function productionHandler(request: IncomingMessage, response: ServerResponse) {
  try {
    const url = new URL(request.url ?? '/', 'https://germanmaster.invalid');
    const path = url.searchParams.get('gm_path');
    if (path !== null) {
      url.searchParams.delete('gm_path');
      request.url = `/v2/${path}${url.search}`;
    }
    server ??= compose();
    const api = await server;
    await new Promise<void>(resolve => {
      response.once('finish', resolve);
      response.once('close', resolve);
      api.emit('request', request, response);
    });
  } catch {
    server = undefined;
    response.statusCode = 503;
    response.setHeader('Content-Type', 'application/json');
    response.setHeader('Cache-Control', 'no-store');
    response.end(JSON.stringify({code:'service_unavailable',message:'Account service is unavailable.'}));
  }
}

import {FoundationStore,ApiFailure} from './store';
import {createApi,type Authenticate} from './server';
import {PostgresDatabase} from './postgres';
import type {RuntimeCatalog} from './runtime-catalog';

/** Explicit server composition; never reads environment secrets or starts a listener. */
export async function createNetworkApi(db:PostgresDatabase,authenticate:Authenticate,catalog?:RuntimeCatalog,webOrigin?:string) {
  // Runtime startup is read-only. An absent/unknown baseline refuses startup.
  await new FoundationStore(db).initialize();
  return createApi(subject=>new FoundationStore(db.forSubject(subject),undefined,undefined,undefined,catalog),async request=> {
    const subject=await authenticate(request);
    if(!subject) return null;
    // Service tombstones are not auth-identity deletion. Keep real deletion closed
    // until reauthentication/revocation, retention and recovery are integrated.
    if(request.method==='DELETE') throw new ApiFailure('deletion_not_enabled',403);
    return subject;
  },webOrigin);
}

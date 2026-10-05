import {FoundationStore,ApiFailure} from './store';
import {createApi,type Authenticate} from './server';
import {PostgresDatabase} from './postgres';

/** Explicit server composition; never reads environment secrets or starts a listener. */
export async function createNetworkApi(db:PostgresDatabase,authenticate:Authenticate) {
  // Runtime startup is read-only. An absent/unknown baseline refuses startup.
  await new FoundationStore(db).initialize();
  return createApi(subject=>new FoundationStore(db.forSubject(subject)),async request=> {
    const subject=await authenticate(request);
    if(!subject) return null;
    // Service tombstones are not auth-identity deletion. Keep real deletion closed
    // until reauthentication/revocation, retention and recovery are integrated.
    if(request.method==='DELETE') throw new ApiFailure('deletion_not_enabled',403);
    return subject;
  });
}

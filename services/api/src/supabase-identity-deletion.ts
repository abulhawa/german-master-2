import {createClient} from '@supabase/supabase-js';
import type {SqlTransaction} from './database';
import type {IdentityDeletionProvider} from './identity-deletion';
import {ApiFailure} from './store';

const UUID=/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
type Admin={deleteUser(subject:string,softDelete:boolean):Promise<{error:unknown}>};

// Absence is authoritative only when RLS is inactive or an applicable constant
// true SELECT policy guarantees visibility and no restrictive policy filters it.
// Policy catalog checks use no user data; missing/changed setup fails closed.
function visible(relation:'auth.users'|'auth.sessions') {
  const oid=`(SELECT c.oid FROM pg_class c JOIN pg_namespace n ON n.oid=c.relnamespace WHERE n.nspname='auth' AND c.relname='${relation.split('.')[1]}')`;
  const applicable=`(0=ANY(p.polroles) OR EXISTS(SELECT 1 FROM unnest(p.polroles) role_id WHERE role_id<>0 AND pg_has_role(current_user,role_id,'member')))`;
  return `(NOT row_security_active(${oid}) OR (
    EXISTS(SELECT 1 FROM pg_policy p WHERE p.polrelid=${oid}
      AND p.polpermissive AND p.polcmd IN ('r','*') AND pg_get_expr(p.polqual,p.polrelid)='true' AND ${applicable})
    AND NOT EXISTS(SELECT 1 FROM pg_policy p WHERE p.polrelid=${oid}
      AND NOT p.polpermissive AND p.polcmd IN ('r','*') AND ${applicable})
  ))`;
}

/** Dedicated read-only gm_identity_verifier connection. Do not infer absence
 * from getUserById error strings/statuses; an outage must remain pending. */
export function identityPresence(db:Pick<SqlTransaction,'query'>, privateViews=false) {
  return async(subject:string)=>{
    if(!UUID.test(subject)) throw new ApiFailure('invalid_request',400);
    const result=await db.query<{exists:boolean;active_sessions:boolean;unfiltered:boolean}>(`SELECT
      ${visible('auth.users')} AND ${visible('auth.sessions')}
      ${privateViews?`AND (SELECT count(*)=2 FROM pg_class c JOIN pg_namespace n ON n.oid=c.relnamespace
        WHERE n.nspname='gm_auth' AND c.relname IN ('identity_subject','identity_session') AND c.reloptions @> ARRAY['security_invoker=true'])`:''} AS unfiltered,
      EXISTS(SELECT 1 FROM ${privateViews?'gm_auth.identity_subject':'auth.users'} WHERE id=$1) AS exists,
      EXISTS(SELECT 1 FROM ${privateViews?'gm_auth.identity_session':'auth.sessions'} WHERE user_id=$1) AS active_sessions`,[subject]);
    const row=result.rows[0];
    if(result.rows.length!==1 || row?.unfiltered!==true || typeof row.exists!=='boolean' || typeof row.active_sessions!=='boolean')
      throw new ApiFailure('identity_provider_unavailable',503);
    // Count even expired sessions: completion requires their removal, not just
    // a temporarily unusable token. Do not return any identity/token material.
    return {exists:row.exists,activeSessions:row.active_sessions};
  };
}

/** Supabase hard deletion removes the identity and its sessions. Existing JWTs
 * remain cryptographically valid until expiry, so learning tombstones and the
 * existing online user/current-session verifier remain mandatory. No learner
 * token is persisted to perform global signOut after a process restart. */
export function supabaseIdentityDeletionProvider(admin:Admin,
  observe:ReturnType<typeof identityPresence>):IdentityDeletionProvider {
  return {
    inspect:async subject=>{
      try {return await observe(subject);}catch {throw new ApiFailure('identity_provider_unavailable',503);}
    },
    remove:async subject=>{
      if(!UUID.test(subject)) throw new ApiFailure('invalid_request',400);
      try {
        const result=await admin.deleteUser(subject,false);
        if(result.error) throw new ApiFailure('identity_provider_unavailable',503);
      }catch {throw new ApiFailure('identity_provider_unavailable',503);}
    },
  };
}

/** Explicit dedicated v2 composition only; never retrieves a credential,
 * starts a worker/listener, exposes the secret to clients or changes auth. */
export function createSupabaseIdentityDeletionProvider(projectRef:string,secretKey:string,
  db:Pick<SqlTransaction,'query'>, privateViews=false):IdentityDeletionProvider {
  if(!['zgmyrpzwgtydwlzponih','sqgjsmiaprsuilcjmaav'].includes(projectRef) || !secretKey.startsWith('sb_secret_'))
    throw Error('Dedicated v2 project and server secret key required');
  const client=createClient(`https://${projectRef}.supabase.co`,secretKey,{
    auth:{persistSession:false,autoRefreshToken:false,detectSessionInUrl:false},
    global:{fetch:(url,options)=>fetch(url,{...options,signal:AbortSignal.timeout(10000),redirect:'error'})},
  });
  return supabaseIdentityDeletionProvider(client.auth.admin,identityPresence(db,privateViews));
}

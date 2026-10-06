import {createClient} from '@supabase/supabase-js';
import type {Authenticate} from './server';
import type {SqlTransaction} from './database';

const uuid=/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
type VerifiedUser = {id:string; is_anonymous?:boolean};
export interface AuthVerifier {
  getUser(token:string):Promise<{data:{user:VerifiedUser|null};error:unknown}>;
}

/** Use a separate gm_auth_verifier connection, never the learning role. */
export function currentAuthSession(db:Pick<SqlTransaction,'query'>, privateView=false) {
  return async(subject:string,sessionId:string):Promise<boolean>=> {
    if(!uuid.test(subject) || !uuid.test(sessionId)) return false;
    const result=await db.query<{active:boolean}>(`SELECT EXISTS (
      SELECT 1 FROM ${privateView?'gm_auth.session_identity':'auth.sessions'} WHERE id=$1 AND user_id=$2
      AND (not_after IS NULL OR not_after>statement_timestamp())
      ${privateView?`AND EXISTS(SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid=c.relnamespace
        WHERE n.nspname='gm_auth' AND c.relname='session_identity' AND c.reloptions @> ARRAY['security_invoker=true'])`:''}
    ) AS active`,[sessionId,subject]);
    return result.rows.length===1 && result.rows[0].active===true;
  };
}

/** Online verification plus current session existence; decoded JWT alone grants nothing. */
export function verifiedSupabaseAuth(projectRef:string, verifier:AuthVerifier,
  sessionExists:(subject:string,sessionId:string)=>Promise<boolean>, now=()=>Date.now()):Authenticate {
  if(!/^[a-z]{20}$/.test(projectRef)) throw Error('Invalid auth project reference');
  const issuer=`https://${projectRef}.supabase.co/auth/v1`;
  return async request=> {
    const header=request.headers.authorization;
    if(typeof header!=='string' || header.length>16384 || !/^Bearer [A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+$/.test(header)) return null;
    const token=header.slice(7);
    try {
      const claims=JSON.parse(Buffer.from(token.split('.')[1],'base64url').toString('utf8'));
      if(claims.iss!==issuer || claims.aud!=='authenticated' || claims.role!=='authenticated'
        || typeof claims.sub!=='string' || !uuid.test(claims.sub)
        || typeof claims.session_id!=='string' || !uuid.test(claims.session_id)
        || !Number.isSafeInteger(claims.exp) || claims.exp*1000<=now()) return null;
      const {data,error}=await verifier.getUser(token);
      if(error || !data.user || data.user.id!==claims.sub || data.user.is_anonymous===true) return null;
      if(!await sessionExists(data.user.id,claims.session_id)) return null;
      if(claims.exp*1000<=now()) return null;
      return data.user.id;
    } catch {return null;}
  };
}

/** No persisted server session, service-role key, fixture token or automatic refresh. */
export function createSupabaseAuthenticate(projectRef:string,publishableKey:string,
  sessionExists:(subject:string,sessionId:string)=>Promise<boolean>):Authenticate {
  if(!/^[a-z]{20}$/.test(projectRef)) throw Error('Invalid auth project reference');
  if(!publishableKey.startsWith('sb_publishable_')) throw Error('Publishable auth key required');
  const client=createClient(`https://${projectRef}.supabase.co`,publishableKey,{
    auth:{persistSession:false,autoRefreshToken:false,detectSessionInUrl:false},
    global:{fetch:(url,options)=>fetch(url,{...options,signal:AbortSignal.timeout(10000)})},
  });
  return verifiedSupabaseAuth(projectRef,client.auth,sessionExists);
}

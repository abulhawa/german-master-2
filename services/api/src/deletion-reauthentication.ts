import {createClient} from '@supabase/supabase-js';
import type {IncomingMessage} from 'node:http';
import {verifiedSupabaseAuth,type AuthVerifier} from './supabase-auth';
import type {VerifyDeletionReauthentication} from './identity-deletion';

export interface PasswordReauthenticationClient extends AuthVerifier {
  signInWithPassword(credentials:{email:string;password:string}):Promise<{
    data:{session:{access_token:string}|null};error:unknown;
  }>;
  signOut(options:{scope:'local'}):Promise<{error:unknown}>;
}

/** A NEW isolated SDK client per proof. Password and temporary tokens live only
 * in this request; no storage, shared SDK session, refresh or logging. The host
 * still authenticates its existing subject and begin() compares that subject. */
export function passwordDeletionReauthentication(projectRef:string,
  client:()=>PasswordReauthenticationClient,sessionExists:(subject:string,sessionId:string)=>Promise<boolean>,
  clock=()=>Date.now()):VerifyDeletionReauthentication {
  // Validate project before accepting credentials or constructing any client.
  verifiedSupabaseAuth(projectRef,{getUser:async()=>({data:{user:null},error:null})},sessionExists,clock);
  return async proof=>{
    if(!proof || typeof proof!=='object' || Array.isArray(proof)) return null;
    const input=proof as Record<string,unknown>;
    if(Object.keys(input).length!==2 || typeof input.email!=='string' || typeof input.password!=='string'
      || input.email.length>320 || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(input.email)
      || !input.password.length || input.password.length>4096) return null;
    const auth=client();const authenticatedAt=clock();let subject:string|null=null;let cleanupFailed=false;
    try {
      const response=await auth.signInWithPassword({email:input.email,password:input.password});
      if(!response.error && response.data.session) {
        const authenticate=verifiedSupabaseAuth(projectRef,auth,sessionExists,clock);
        subject=await authenticate({headers:{authorization:`Bearer ${response.data.session.access_token}`}} as IncomingMessage);
      }
    }catch {subject=null;}
    finally {
      // Close only this temporary verification session. A failed cleanup cannot
      // authorize deletion; never return provider errors containing credentials.
      try {cleanupFailed=!!(await auth.signOut({scope:'local'})).error;}catch{cleanupFailed=true;}
    }
    return subject && !cleanupFailed?{subject,authenticatedAt}:null;
  };
}

export function createPasswordDeletionReauthentication(projectRef:string,publishableKey:string,
  sessionExists:(subject:string,sessionId:string)=>Promise<boolean>):VerifyDeletionReauthentication {
  if(!/^[a-z]{20}$/.test(projectRef) || !publishableKey.startsWith('sb_publishable_'))
    throw Error('Dedicated project and publishable key required');
  return passwordDeletionReauthentication(projectRef,()=>createClient(`https://${projectRef}.supabase.co`,publishableKey,{
    auth:{persistSession:false,autoRefreshToken:false,detectSessionInUrl:false},
    global:{fetch:(url,options)=>fetch(url,{...options,signal:AbortSignal.timeout(10000),cache:'no-store',redirect:'error'})},
  }).auth,sessionExists);
}

import type {IncomingMessage, ServerResponse} from 'node:http';
import {IdentityDeletionBeginSchema, IdentityDeletionRecoverySchema, IdentityDeletionResponseSchema} from '@german-master/contracts';
import type {Authenticate} from './server';
import {ApiFailure} from './store';
import type {IdentityDeletionService} from './identity-deletion';

/** Explicit private-worker delivery. One process/one worker only; never retries
 * automatically, and a failed invocation does not poison subsequent delivery. */
export function serializedDeletionWorker(service:IdentityDeletionService) {
  let tail:Promise<unknown>=Promise.resolve();
  return (requestId:string)=> {
    const result=tail.then(()=>service.continue(requestId));
    tail=result.catch(()=>undefined);
    return result;
  };
}

/** Opt-in host protocol. Recovery is read-only and needs no surviving session.
 * No public worker route: possession of a recovery capability cannot run deletion.
 * Rate limits are process-local, bounded, keyed by socket peer (never forwarded
 * headers); multi-host deployment must supply an upstream shared limit as well. */
export function identityDeletionHttp(service:IdentityDeletionService,authenticate:Authenticate,
  clock=()=>Date.now()) {
  const peers=new Map<string,{start:number;count:number}>();
  return async (request:IncomingMessage,response:ServerResponse):Promise<boolean>=> {
    const path=request.url;
    if(path!=='/v2/me/identity-deletion:begin' && path!=='/v2/me/identity-deletion:status') return false;
    if(request.method!=='POST') throw new ApiFailure('not_found',404);
    const now=clock(),peer=request.socket.remoteAddress??'unknown';
    for(const [key,value] of peers) if(now-value.start>=60_000) peers.delete(key);
    const rate=peers.get(peer)??{start:now,count:0};
    if(rate.count>=10 || (!peers.has(peer) && peers.size>=4096)) {
      response.setHeader('Retry-After','60');throw new ApiFailure('rate_limited',429);
    }
    rate.count++;peers.set(peer,rate);
    if(request.headers['content-type']?.split(';')[0].trim()!=='application/json') throw new ApiFailure('json_required',415);
    const chunks:Buffer[]=[];let size=0;
    for await(const chunk of request) {
      size+=chunk.length;
      if(size>8192) throw new ApiFailure('payload_too_large',413);
      chunks.push(chunk);
    }
    let input:unknown;
    try {input=JSON.parse(Buffer.concat(chunks).toString('utf8'));}
    catch {throw new ApiFailure('invalid_json',400);}
    let receipt;
    if(path.endsWith(':begin')) {
      const parsed=IdentityDeletionBeginSchema.safeParse(input);
      if(!parsed.success || parsed.data.proof.email.length>320 || parsed.data.proof.password.length>4096) throw new ApiFailure('invalid_request',400);
      const subject=await authenticate(request);
      if(!subject) throw new ApiFailure('authentication_required',401);
      const expected=request.headers['x-learner-subject'];
      if(typeof expected!=='string' || expected.toLowerCase()!==subject.toLowerCase()) throw new ApiFailure('account_changed',409);
      const data=parsed.data;
      receipt=await service.begin(subject,data.requestId,data.recoveryCapability,data.proof);
    } else {
      const parsed=IdentityDeletionRecoverySchema.safeParse(input);
      if(!parsed.success) throw new ApiFailure('invalid_request',400);
      receipt=await service.recover(parsed.data.requestId,parsed.data.recoveryCapability);
    }
    response.end(JSON.stringify(IdentityDeletionResponseSchema.parse(receipt.status==='pending'
      ? {apiVersion:receipt.apiVersion,requestId:receipt.requestId,status:receipt.status}
      : receipt)));
    return true;
  };
}

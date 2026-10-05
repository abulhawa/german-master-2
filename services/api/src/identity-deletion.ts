import {createHash, timingSafeEqual} from 'node:crypto';
import type {SqlDatabase} from './database';
import {ApiFailure, type FoundationStore} from './store';

const UUID=/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
const CAPABILITY=/^[A-Za-z0-9_-]{43}$/;
const FRESH_FOR=5*60*1000;
const RECEIPT_FOR=30*24*60*60*1000;
type Job={subject:string;request_id:string;recovery_hash:string|null;requested_at:Date;completed_at:Date|null;recovery_until:Date|null};
export type IdentityDeletionReceipt={apiVersion:'v2';requestId:string;status:'pending'|'identity_deleted';completedAt:string|null};
/** This adapter must obtain fresh server-verified password/OTP authentication,
 * not JWT iat, a refresh, user metadata, or a client-supplied timestamp. */
export type VerifyDeletionReauthentication=(proof:unknown)=>Promise<{subject:string;authenticatedAt:number}|null>;
export interface IdentityDeletionProvider {
  /** Online authoritative observation; failures throw, never imply absence. */
  inspect(subject:string):Promise<{exists:boolean;activeSessions:boolean}>;
  /** Optional pre-delete revocation when the provider supports subject-based
   * revocation without persisting a learner token. Hard deletion must otherwise
   * remove sessions, with authoritative absence checked before completion. */
  revokeAll?(subject:string):Promise<void>;
  /** Hard delete; no soft-delete success or treating arbitrary errors as 404. */
  remove(subject:string):Promise<void>;
}
const hash=(value:string)=>createHash('sha256').update(value).digest('hex');
const matches=(saved:string|null,value:string)=>saved!==null && timingSafeEqual(Buffer.from(saved,'hex'),Buffer.from(hash(value),'hex'));

/** Durable server orchestration only. No listener, credential lookup or automatic
 * retries. Existing configured-client deletion remains disabled until host wiring. */
export class IdentityDeletionService {
  constructor(private db:SqlDatabase,private learning:(subject:string)=>Pick<FoundationStore,'deleteLearner'>,
    private provider:IdentityDeletionProvider,private reauthenticate:VerifyDeletionReauthentication,
    private clock=()=>Date.now()) {}

  private async job(requestId:string) {
    if(!UUID.test(requestId)) throw new ApiFailure('invalid_request',400);
    return (await this.db.query<Job>('SELECT * FROM gm_privacy.identity_deletion WHERE request_id=$1',[requestId])).rows[0];
  }
  private receipt(job:Job):IdentityDeletionReceipt {
    return {apiVersion:'v2',requestId:job.request_id,status:job.completed_at?'identity_deleted':'pending',
      completedAt:job.completed_at?new Date(job.completed_at).toISOString():null};
  }
  /** Client generates and durably freezes 32 random bytes (base64url) before
   * request delivery. Only its hash is stored. Same request+capability can replay. */
  async begin(subject:string,requestId:string,recoveryCapability:string,proof:unknown) {
    if(!UUID.test(subject) || !UUID.test(requestId) || !CAPABILITY.test(recoveryCapability)
      || Buffer.from(recoveryCapability,'base64url').toString('base64url')!==recoveryCapability)
      throw new ApiFailure('invalid_request',400);
    subject=subject.toLowerCase();requestId=requestId.toLowerCase();
    // Recovery never authorizes a new deletion; fresh proof is always required.
    const verified=await this.reauthenticate(proof);const now=this.clock();
    if(!verified || verified.subject.toLowerCase()!==subject || !Number.isSafeInteger(verified.authenticatedAt)
      || verified.authenticatedAt>now || now-verified.authenticatedAt>=FRESH_FOR)
      throw new ApiFailure('fresh_authentication_required',401);
    return this.db.transaction(async tx=> {
      await tx.query(`INSERT INTO gm_privacy.identity_deletion
        (subject,request_id,recovery_hash,requested_at) VALUES ($1,$2,$3,$4) ON CONFLICT DO NOTHING`,
      [subject,requestId,hash(recoveryCapability),new Date(now).toISOString()]);
      const job=(await tx.query<Job>('SELECT * FROM gm_privacy.identity_deletion WHERE subject=$1 FOR UPDATE',[subject])).rows[0];
      if(!job || job.request_id!==requestId || !matches(job.recovery_hash,recoveryCapability))
        throw new ApiFailure('deletion_conflict',409);
      return this.receipt(job);
    });
  }
  /** Capability permits status only, with no subject/profile/password in receipt.
   * No account session is needed after provider deletion; unknown/wrong/expired
   * capabilities have the same result. Host must rate limit and never log it. */
  async recover(requestId:string,recoveryCapability:string) {
    if(!CAPABILITY.test(recoveryCapability)) throw new ApiFailure('receipt_unavailable',404);
    const job=await this.job(requestId);
    if(!job || !matches(job.recovery_hash,recoveryCapability)
      || (job.recovery_until && this.clock()>=new Date(job.recovery_until).getTime()))
      throw new ApiFailure('receipt_unavailable',404);
    return this.receipt(job);
  }
  /** Private explicit worker operation. No capability or learner endpoint can
   * call this directly. A lost provider response stops; a later explicit run
   * observes identity/session state before deciding whether to send again. */
  async continue(requestId:string) {
    const job=await this.job(requestId);
    if(!job) throw new ApiFailure('deletion_not_found',404);
    if(job.completed_at) return this.receipt(job);
    // Learning tombstone and data removal commit before ANY identity mutation.
    await this.learning(job.subject).deleteLearner(job.subject,{apiVersion:'v2',requestId:job.request_id,confirmation:'delete_owned_data'});
    const before=await this.provider.inspect(job.subject);
    if(before.activeSessions && this.provider.revokeAll) await this.provider.revokeAll(job.subject);
    if(before.exists) await this.provider.remove(job.subject);
    const after=await this.provider.inspect(job.subject);
    if(after.exists || after.activeSessions) throw new ApiFailure('identity_deletion_pending',503);
    const now=this.clock();
    await this.db.query(`UPDATE gm_privacy.identity_deletion SET completed_at=$2,recovery_until=$3
      WHERE request_id=$1 AND completed_at IS NULL`,[requestId,new Date(now).toISOString(),new Date(now+RECEIPT_FOR).toISOString()]);
    return this.receipt((await this.job(requestId))!);
  }
  /** Fixed receipt deadline, never renewed by reads/replays. Pending jobs remain
   * recoverable; tombstones remain to reject abandoned uploads/backup restores. */
  async expireRecoveryCapabilities() {
    await this.db.query(`UPDATE gm_privacy.identity_deletion SET recovery_hash=NULL
      WHERE recovery_until<=$1 AND recovery_hash IS NOT NULL`,[new Date(this.clock()).toISOString()]);
  }
}

import { z } from 'zod';
import { IdentityDeletionProofSchema, IdentityDeletionRecoverySchema, IdentityDeletionResponseSchema, type IdentityDeletionProof, type IdentityDeletionResponse } from '@german-master/contracts';
import type { AccountBinding } from './account';
import type { JourneyStorage } from './storage';

export const IDENTITY_DELETION_KEY = 'german-master-v2:identity-deletion-v1';
const MarkerSchema = z.strictObject({subject:z.string().uuid(),request:IdentityDeletionRecoverySchema,receipt:IdentityDeletionResponseSchema.nullable(),complete:z.boolean()});
export type IdentityDeletionMarker = z.infer<typeof MarkerSchema>;
export interface IdentityDeletionTransport {
  begin(request:IdentityDeletionMarker['request'],proof:IdentityDeletionProof):Promise<IdentityDeletionResponse>;
  recover(request:IdentityDeletionMarker['request']):Promise<IdentityDeletionResponse>;
}
/** Subject storage is independent of provider session lifetime. Passwords are never saved. */
export class LearnerIdentityDeletion {
  constructor(private account:AccountBinding,private storage:JourneyStorage) {}
  read():IdentityDeletionMarker|null {
    const raw=this.storage.getItem(IDENTITY_DELETION_KEY);if(!raw)return null;
    const value=MarkerSchema.parse(JSON.parse(raw));
    if(value.subject!==this.account.identity.subject || value.receipt && value.receipt.requestId!==value.request.requestId || value.complete && value.receipt?.status!=='identity_deleted') throw Error('Identity deletion marker mismatch');
    return value;
  }
  assertActive() {if(this.read())throw Error('Identity deletion pending or completed');}
  private save(value:IdentityDeletionMarker) {this.storage.setItem(IDENTITY_DELETION_KEY,JSON.stringify(MarkerSchema.parse(value)));}
  async deliver(transport:IdentityDeletionTransport,cleanup:()=>Promise<void>,proof?:IdentityDeletionProof,frozen:()=>void=()=>{},finish:()=>Promise<void>=async()=>{}) {
    if(proof)proof=IdentityDeletionProofSchema.parse(proof);
    let value=this.read();
    if(!value) {
      this.account.assertCurrent();if(!proof)throw Error('Fresh authentication required');
      const bytes=crypto.getRandomValues(new Uint8Array(32));
      const recoveryCapability=btoa(String.fromCharCode(...bytes)).replace(/\+/g,'-').replace(/\//g,'_').replace(/=+$/,'');
      value={subject:this.account.identity.subject,request:{apiVersion:'v2',requestId:crypto.randomUUID(),recoveryCapability},receipt:null,complete:false};
      this.save(value);
    }
    frozen();
    if(value.complete){await finish();return;}
    if(value.receipt?.status!=='identity_deleted') {
      // Recover first after ambiguity/restart; a capability never admits or dispatches work.
      const receipt=IdentityDeletionResponseSchema.parse(proof ? await transport.begin(value.request,proof) : await transport.recover(value.request));
      if(receipt.requestId!==value.request.requestId)throw Error('Identity deletion receipt mismatch');
      value={...value,receipt};this.save(value);
    }
    if(value.receipt?.status==='identity_deleted') {
      await cleanup(); // Durable identity receipt precedes every destructive local operation.
      this.save({...value,complete:true});
      await finish();
    }
  }
}

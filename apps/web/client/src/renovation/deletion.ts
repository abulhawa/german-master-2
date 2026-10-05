import { z } from 'zod';
import { PrivacyDeleteRequestSchema, PrivacyDeleteReceiptSchema } from '@german-master/contracts';
import type { AccountBinding } from './account';
import type { JourneyStorage } from './storage';
import type { LearnerApi } from './api';

export const DELETION_KEY = 'german-master-v2:local-fixture:deletion-v1';
const DeletionSchema = z.strictObject({ subject: z.string().uuid(), request: PrivacyDeleteRequestSchema, receipt: PrivacyDeleteReceiptSchema.nullable(), complete:z.boolean().default(false) });
export type Deletion = z.infer<typeof DeletionSchema>;

/** Keep a terminal marker even after cleanup: the fixture identity must never resume uploads. */
export class LearnerDeletion {
  constructor(private readonly account: AccountBinding, private readonly storage: JourneyStorage) {}
  read(): Deletion | null {
    const raw = this.storage.getItem(DELETION_KEY);
    if (!raw) return null;
    const value = DeletionSchema.parse(JSON.parse(raw));
    if (value.subject !== this.account.identity.subject || value.complete && !value.receipt || value.receipt && (value.receipt.subject.toLowerCase() !== value.subject || value.receipt.requestId !== value.request.requestId)) throw Error('Deletion ownership mismatch');
    return value;
  }
  assertActive() { if (this.read()) throw Error('Learner deletion pending or completed'); }
  private save(value: Deletion) { this.storage.setItem(DELETION_KEY, JSON.stringify(DeletionSchema.parse(value))); }
  async deliver(api: LearnerApi, cleanup: () => Promise<void>, frozen: () => void = () => {}): Promise<void> {
    this.account.assertCurrent();
    let value = this.read();
    if (!value) {
      value = {subject:this.account.identity.subject,request:{apiVersion:'v2',requestId:crypto.randomUUID(),confirmation:'delete_owned_data'},receipt:null,complete:false};
      this.save(value); // Persist before the first byte goes to the server.
    }
    frozen();
    if (!value.receipt) {
      if (!api.deleteLearner) throw Error('Deletion unavailable');
      const receipt = PrivacyDeleteReceiptSchema.parse(await api.deleteLearner(value.request));
      this.account.assertCurrent();
      if (receipt.subject.toLowerCase() !== value.subject || receipt.requestId !== value.request.requestId) throw Error('Deletion receipt mismatch');
      value = {...value,receipt}; this.save(value); // Receipt before any local removal.
    }
    this.account.assertCurrent();
    if(!value.complete) {
      await cleanup(); // Failure is resumable from the durable confirmed receipt.
      this.save({...value,complete:true});
    }
  }
}

/** Wrap supplied and real APIs so every operation uses the same durable barrier. */
export function deletionGuard(api: LearnerApi, deletion: {assertActive():void}): LearnerApi {
  return new Proxy(api, { get(target, key) {
    const value = Reflect.get(target,key);
    if (typeof value !== 'function' || key === 'deleteLearner') return value;
    return async (...args: unknown[]) => { deletion.assertActive(); const result = await value.apply(target,args); deletion.assertActive(); return result; };
  } });
}

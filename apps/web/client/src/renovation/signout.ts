import { z } from 'zod';
import type { AccountBinding } from './account';
import type { JourneyStorage } from './storage';

export const SIGNOUT_KEY = 'german-master-v2:local-fixture:signout-v1';
const SignOutSchema = z.strictObject({subject:z.string().uuid(),remove:z.boolean(),complete:z.boolean()});
export class LearnerSignOut {
  constructor(private readonly account: AccountBinding,private readonly storage: JourneyStorage) {}
  read() {
    const raw=this.storage.getItem(SIGNOUT_KEY);if(!raw) return null;
    const value=SignOutSchema.parse(JSON.parse(raw));
    if(value.subject!==this.account.identity.subject) throw Error('Sign-out ownership mismatch');return value;
  }
  assertActive() { if(this.read()) throw Error('Learner signed out'); }
  async finish(remove:boolean,sync:()=>Promise<void>,cleanup:()=>Promise<void>,frozen:()=>void) {
    this.account.assertCurrent();let value=this.read();
    if(!value) {
      if(!remove) { await sync();this.account.assertCurrent(); }
      value={subject:this.account.identity.subject,remove,complete:false};
      this.storage.setItem(SIGNOUT_KEY,JSON.stringify(value));
    }
    frozen();
    if(value.complete) return;
    this.account.assertCurrent();if(value.remove) await cleanup();
    this.storage.setItem(SIGNOUT_KEY,JSON.stringify({...value,complete:true}));
  }
  resume() {
    this.account.assertCurrent();if(!this.read()?.complete) throw Error('Finish sign-out first');
    this.storage.setItem(SIGNOUT_KEY,'');
  }
}

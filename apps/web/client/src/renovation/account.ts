import type { JourneyStorage } from './storage';
import { WebReserve, RESERVE_DATABASE } from './reserve';
import { OWNER_LOCK } from './ownership';

export const FIXTURE_SUBJECT = '00000000-0000-4000-8000-000000000010';
export type LearnerIdentity = { subject: string; generation: number };

/** Captures an authenticated identity, never a bearer token. A new sign-in generation
 * invalidates old transports even when the same subject signs back in. */
export class AccountBinding {
  readonly identity: Readonly<LearnerIdentity>;
  constructor(identity: LearnerIdentity, private readonly current: () => LearnerIdentity | null) {
    if (!/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(identity.subject)
      || !Number.isSafeInteger(identity.generation) || identity.generation < 0) throw Error('Invalid learner identity');
    this.identity = Object.freeze({ ...identity, subject: identity.subject.toLowerCase() });
  }
  assertCurrent() {
    const active = this.current();
    if (!active || active.subject.toLowerCase() !== this.identity.subject || active.generation !== this.identity.generation)
      throw Error('Sign in to the saved learner account before syncing');
  }
  // The original namespace belongs exclusively to the established public primary fixture.
  private suffix() { return this.identity.subject === FIXTURE_SUBJECT ? '' : `:subject:${this.identity.subject}`; }
  storage(base: JourneyStorage): JourneyStorage {
    return { getItem: key => base.getItem(key + this.suffix()), setItem: (key, value) => base.setItem(key + this.suffix(), value) };
  }
  reserve() { return new WebReserve(RESERVE_DATABASE + this.suffix()); }
  get ownerLock() { return OWNER_LOCK + this.suffix(); }
}
const fixtureIdentity: LearnerIdentity = { subject: FIXTURE_SUBJECT, generation: 0 };
export const fixtureAccount = new AccountBinding(fixtureIdentity, () => fixtureIdentity);

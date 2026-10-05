import Dexie, { type Table } from 'dexie';
import { z } from 'zod';
import { SessionRequestSchema, type SessionRequest, type PreparedPack } from '@german-master/contracts';
import { validatePreparedPack, packCanonical, canStartPreparedPack } from '@german-master/learning-engine';

export const RESERVE_DATABASE = 'german-master-v2-local-fixture';
const RecordSchema = z.strictObject({
  id: z.literal('reserve'), version: z.literal(1),
  pending: SessionRequestSchema.nullable(), pack: z.unknown().nullable(),
  consumed: z.array(z.string().uuid()).default([]),
});
type ReserveRecord = z.infer<typeof RecordSchema>;
export type Reserve = { pending: SessionRequest | null; pack: PreparedPack | null };

/** Separate fixture namespace. Production account ownership is not inferred from this cache. */
export class WebReserve extends Dexie {
  private records!: Table<ReserveRecord, string>;
  constructor(name = RESERVE_DATABASE) {
    super(name);
    this.version(1).stores({ records: 'id' });
    this.version(2).stores({ practices: 'id' });
    this.records = this.table('records');
  }
  private async record(): Promise<ReserveRecord> {
    const raw = await this.records.get('reserve');
    return raw ? RecordSchema.parse(raw) : { id: 'reserve', version: 1, pending: null, pack: null, consumed: [] };
  }
  async read(): Promise<Reserve> {
    const record = await this.record();
    return { pending: record.pending, pack: record.pack === null ? null : await validatePreparedPack(record.pack) };
  }
  async freeze(input: SessionRequest): Promise<SessionRequest> {
    const request = SessionRequestSchema.parse(input);
    // Validate existing content before mutation; retain corrupt bytes for explicit recovery.
    await this.read();
    return this.transaction('rw', this.records, async () => {
      const record = await this.record();
      if (record.pending) return record.pending;
      await this.records.put({ ...record, pending: request });
      return request;
    });
  }
  async accept(request: SessionRequest, input: unknown): Promise<PreparedPack> {
    // Crypto awaits must complete before opening an IndexedDB transaction.
    const pack = await validatePreparedPack(input);
    if (pack.packId !== request.requestId) throw Error('Pack request mismatch');
    return this.transaction('rw', this.records, async () => {
      const record = await this.record();
      if (!record.pending || packCanonical(record.pending) !== packCanonical(request)) {
        // Concurrent identical replay can finish after the first atomic commit.
        if (!record.pending && packCanonical(record.pack) === packCanonical(pack)) return pack;
        throw Error('Reserve request changed');
      }
      await this.records.put({ ...record, pending: null, pack, consumed: [] });
      return pack;
    });
  }
  /** One explicit call; response loss/save failure leaves the original request for retry. */
  async prepare(input: SessionRequest, download: (request: SessionRequest) => Promise<PreparedPack>): Promise<PreparedPack> {
    const frozen = await this.freeze(input);
    return this.accept(frozen, await download(frozen));
  }
  async status(now: Date): Promise<{ reserve: Reserve; available: number }> {
    const record = await this.record();
    const reserve = { pending: record.pending, pack: record.pack === null ? null : await validatePreparedPack(record.pack) };
    return { reserve, available: reserve.pack && canStartPreparedPack(reserve.pack, now)
      ? reserve.pack.sessions.filter(s => !record.consumed.includes(s.id)).length : 0 };
  }
}

export const browserReserve = new WebReserve();

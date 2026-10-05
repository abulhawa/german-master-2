import { Pool, types, type PoolClient, type PoolConfig } from 'pg';
import type { SqlDatabase, SqlTransaction } from './database';

/** Contracts use safe JSON integer sequences, while pg defaults int8 to strings. */
export function safeSequence(value:string):number {
  const parsed=Number(value);
  if(!Number.isSafeInteger(parsed)) throw Error('Database sequence exceeds the supported contract range');
  return parsed;
}

function connection(client: PoolClient): SqlTransaction {
  return {
    async query<T>(sql: string, values?: unknown[]) {
      const result = await client.query(sql, values);
      return { rows: result.rows as T[] };
    },
    async exec(sql) { await client.query(sql); },
  };
}

/** Server-only adapter. No automatic retry: a lost commit response is ambiguous. */
export class PostgresDatabase implements SqlDatabase {
  readonly baselineOnly = true;
  constructor(private readonly pool: Pick<Pool, 'connect' | 'end'>) {}

  static connect(config: PoolConfig): PostgresDatabase {
    // Reject URL SSL switches that can override certificate verification in pg.
    if (config.connectionString) {
      const url = new URL(config.connectionString);
      if ([...url.searchParams.keys()].some(key => key.startsWith('ssl')))
        throw Error('Configure database TLS separately from the connection URL');
    }
    if (!config.ssl || typeof config.ssl !== 'object' || config.ssl.rejectUnauthorized !== true)
      throw Error('Verified database TLS is required');
    return new PostgresDatabase(new Pool({max: 4, connectionTimeoutMillis: 10000,
      idleTimeoutMillis: 30000, ...config,
      types:{getTypeParser:(oid,format)=>oid===20 && format!=='binary'
        ? safeSequence : types.getTypeParser(oid,format)},
    }));
  }

  async query<T = Record<string, unknown>>(sql: string, values?: unknown[]) {
    const client = await this.pool.connect();
    try { return await connection(client).query<T>(sql, values); }
    finally { client.release(); }
  }

  async exec(sql: string) { await this.query(sql); }

  /** Construct only after server authentication; never from a request body/header. */
  forSubject(subject: string): SqlDatabase {
    if (!/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(subject))
      throw Error('Invalid verified subject');
    const scoped = <T>(work: (tx: SqlTransaction) => Promise<T>) => this.transaction(async tx => {
      await tx.query("SELECT set_config('gm.subject', $1, true)", [subject]);
      return work(tx);
    });
    return {
      baselineOnly: true,
      query: <T>(sql: string, values?: unknown[]) => scoped(tx => tx.query<T>(sql, values)),
      exec: (sql: string) => scoped(tx => tx.exec(sql)),
      transaction: scoped,
    };
  }

  async transaction<T>(work: (tx: SqlTransaction) => Promise<T>): Promise<T> {
    const client = await this.pool.connect();
    let discard = false;
    try {
      await client.query('BEGIN');
      const result = await work(connection(client));
      await client.query('COMMIT');
      return result;
    } catch (error) {
      try { await client.query('ROLLBACK'); } catch { discard = true; }
      throw error;
    } finally { client.release(discard); }
  }

  async close() { await this.pool.end(); }
}

/** The engine needs parameterized SQL and a single connection per transaction. */
export interface SqlTransaction {
  query<T = Record<string, unknown>>(sql: string, values?: unknown[]): Promise<{rows: T[]}>;
  exec(sql: string): Promise<unknown>;
}
export interface SqlDatabase extends SqlTransaction {
  /** Network stores must never initialize a fixture or run development upgrades. */
  readonly baselineOnly?: boolean;
  transaction<T>(work: (tx: SqlTransaction) => Promise<T>): Promise<T>;
}

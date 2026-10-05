export const OWNER_LOCK = "german-master-v2:local-fixture:owner-v1";

// The browser owns lifetime/recovery. No expiring localStorage lease can safely
// steal ownership from a suspended tab that still has an HTTP request in flight.
export class FixtureOwner {
  private active = 0;
  private closing = false;
  private release!: () => void;
  readonly closed = new Promise<void>(resolve => { this.release = resolve; });
  private tail: Promise<unknown> = Promise.resolve();

  async run<T>(work: () => Promise<T>): Promise<T> {
    if (this.closing) throw Error("Fixture owner closed");
    this.active++;
    const result = this.tail.then(work);
    this.tail = result.catch(() => {});
    try { return await result; }
    finally { this.active--; this.finish(); }
  }
  close() { this.closing = true; this.finish(); }
  private finish() { if (this.closing && this.active === 0) this.release(); }
}

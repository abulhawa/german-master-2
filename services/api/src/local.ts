import { PGlite } from "@electric-sql/pglite";
import { FoundationStore } from "./store";
import { createApi } from "./server";

// Deliberately in-memory and loopback-only. This token is a public fixture, not a credential.
// No production auth, environment credentials, database URLs or published content are loaded.
const db = new PGlite();
const store = new FoundationStore(db);
await store.initialize();
const server = createApi(store, async request => request.headers.authorization === "Bearer foundation-local-demo"
  ? "00000000-0000-4000-8000-000000000010" : null);
const port = Number(process.env.GM_DEMO_PORT ?? 5001);
if (!Number.isInteger(port) || port < 1 || port > 65535) throw Error("Invalid GM_DEMO_PORT");
server.listen(port, "127.0.0.1", () => console.log(`Draft foundation API: http://127.0.0.1:${port} (in-memory PostgreSQL; resets on restart)`));
for (const signal of ["SIGINT", "SIGTERM"] as const) process.on(signal, () => server.close(() => { void db.close(); }));

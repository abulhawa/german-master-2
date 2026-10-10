import { afterEach, expect, it, vi } from "vitest";
import { cleanup, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { PGlite } from "@electric-sql/pglite";
import { randomUUID } from "node:crypto";
import type { AddressInfo } from "node:net";
import { FoundationStore } from "../../../../../services/api/src/store";
import { createApi } from "../../../../../services/api/src/server";
import { localLearnerApi } from "./api";
import { OwnedLearnerJourney } from "./journey";
import { emptyJourney, readJourney, saveJourney, snapshot } from "./storage";
import { prepareAttempt } from "../foundation/api";

const profileKey = "german-master-v2:local-fixture:profile-request-v1";
const nativeFetch = globalThis.fetch;
afterEach(() => { cleanup(); localStorage.clear(); vi.unstubAllGlobals(); });

it.each(["attempt", "skip", "profile"].flatMap(kind =>
  ["response-loss", "expired-page"].map(failure => ({ kind, failure }))))(
  "refreshes through real HTTP expiry without losing pending $kind on $failure", async ({ kind, failure }) => {
  const db = new PGlite();
  let now = Date.parse("2026-10-04T12:00:00Z");
  const store = new FoundationStore(db, () => new Date(now), 1000, 1000);
  const owner = randomUUID();
  await store.initialize();
  const server = createApi(store, async request => request.headers.authorization === "Bearer foundation-local-demo" ? owner : null);
  await new Promise<void>(resolve => server.listen(0, "127.0.0.1", resolve));
  const base = `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
  const reads: string[] = [], writes: string[] = [];
  const syncStatuses: number[] = [];
  const pageStatuses: number[] = [];
  let failPage = false;
  // Only loopback addressing, page size, response loss and server time/cleanup are injected.
  // Transport parsing, reset recognition, component refresh and storage are real.
  vi.stubGlobal("fetch", async (input: string, init?: RequestInit) => {
    const url = new URL(input, base);
    if (init?.method === "POST") writes.push(url.pathname);
    else reads.push(url.pathname + url.search);
    if (url.pathname === "/v2/targets") {
      url.searchParams.set("limit", "1");
      if (failPage && url.searchParams.has("cursor")) {
        if (failure === "response-loss") throw Error("Injected page response loss");
        // Expire the real frozen chain at its exact deadline, then delete it.
        // The continuation still reaches HTTP and must fail without an auto retry.
        now += 1000;
        await store.cleanupReads();
        expect((await db.query("SELECT * FROM gm.target_page")).rows).toHaveLength(0);
      }
    }
    const response = await nativeFetch(url, init);
    if (url.pathname === "/v2/sync") syncStatuses.push(response.status);
    if (url.pathname === "/v2/targets") pageStatuses.push(response.status);
    return response;
  });
  try {
    const api = localLearnerApi();
    const profile = await api.profile();
    const preferences = { ...profile.preferences, locale: "en" as const };
    await api.saveProfile({ apiVersion: "v2", requestId: randomUUID(), expectedRevision: profile.revision, preferences });
    const request = { apiVersion: "v2" as const, requestId: randomUUID(), questionCount: 1, capabilities: ["short_answer@1"] };
    const session = await api.createSession(request);
    const state = emptyJourney();
    state.confirmed = await snapshot(api);
    const pending = prepareAttempt(session, 0, { type: "short_answer", text: "frozen answer" }, true, state.deviceId);
    pending.answeredAt = new Date(now).toISOString();
    const event = { eventId: randomUUID(), deviceId: state.deviceId, sessionQuestionId: session.questions[0].id,
      exerciseRevision: session.questions[0].exercise.revision, disposition: "skip" as const, occurredAt: pending.answeredAt };
    state.practice = { request, session, index: 0, draft: pending.answer, assisted: true,
      pending: kind === "attempt" ? pending : null, pendingExposure: kind === "skip" ? event : null,
      evaluation: null, rejected: false, confirmedCount: 0, correctCount: 0, skippedCount: 0 };
    saveJourney(localStorage, state);
    const currentProfile = await api.profile();
    const frozenProfile = JSON.stringify({ apiVersion: "v2", requestId: randomUUID(), expectedRevision: currentProfile.revision, preferences });
    if (kind === "profile") localStorage.setItem(profileKey, frozenProfile);
    // Server accepted the write, but its acknowledgment never reached local storage.
    if (kind === "attempt") expect((await api.submit(pending)).status).toBe("accepted");
    if (kind === "skip") expect((await api.expose(event)).status).toBe("accepted");
    if (kind === "profile") await api.saveProfile(JSON.parse(frozenProfile));
    const before = readJourney(localStorage);
    writes.length = 0; reads.length = 0; pageStatuses.length = 0;
    now += 1000; failPage = true;
    render(<OwnedLearnerJourney api={api} />);
    await screen.findByText("Your progress could not be updated. Previously saved results are still available, if any.");
    expect(readJourney(localStorage)).toEqual(before);
    expect(writes).toEqual([]);
    expect(reads.some(path => path.startsWith("/v2/sync?"))).toBe(true);
    expect(syncStatuses).toEqual([400]);
    if (failure === "expired-page") expect(pageStatuses).toEqual([200, 400]);
    expect(reads.filter(path => path.startsWith("/v2/targets") && !path.includes("cursor="))).toHaveLength(1);
    failPage = false;
    // Pending profile setup has no refresh button; remount retries its saved cursor.
    // Other cases exercise an explicit retry in the same mounted component.
    if (kind === "profile") { cleanup(); render(<OwnedLearnerJourney api={api} />); }
    else fireEvent.click(screen.getByText("Refresh progress"));
    await waitFor(() => expect(readJourney(localStorage).confirmed!.cursor).not.toBe(before.confirmed!.cursor));
    await waitFor(() => expect(screen.queryByText("Your progress could not be updated. Previously saved results are still available, if any.")).toBeNull());
    expect(readJourney(localStorage).practice).toEqual(before.practice);
    expect(readJourney(localStorage).confirmed!.targets).toHaveLength(5);
    expect(readJourney(localStorage).confirmed!.targets.reduce((total, target) => total + target.exposureCount, 0)).toBe(kind === "profile" ? 0 : 1);
    expect(writes).toEqual([]);
    // Existing refresh pulls recovered state, then reads a fresh full snapshot.
    await waitFor(() => expect(pageStatuses.filter(status => status === 200)).toHaveLength(11));
    expect(reads.filter(path => path.startsWith("/v2/targets") && !path.includes("cursor="))).toHaveLength(3);
    expect(readJourney(localStorage).confirmed!.generatedAt).toBe(new Date(now).toISOString());
    if (kind === "profile") {
      expect(localStorage.getItem(profileKey)).toBe(frozenProfile);
      const revision = (await api.profile()).revision;
      await api.saveProfile(JSON.parse(localStorage.getItem(profileKey)!));
      expect((await api.profile()).revision).toBe(revision);
    }
    else {
      await waitFor(() => expect(screen.getByText("Refresh progress")).not.toBeDisabled());
      const recoveredCursor = readJourney(localStorage).confirmed!.cursor;
      fireEvent.click(screen.getByText("Refresh progress"));
      await waitFor(() => expect(reads.some(path => path.startsWith("/v2/sync?") && path.includes(recoveredCursor))).toBe(true));
      expect((await (kind === "attempt" ? api.submit(pending) : api.expose(event))).status).toBe("duplicate");
      expect((await db.query("SELECT * FROM gm.accepted_evidence WHERE user_id=$1", [owner])).rows).toHaveLength(1);
    }
  } finally { cleanup(); await new Promise<void>(resolve => server.close(() => resolve())); await db.close(); }
}, 30000);

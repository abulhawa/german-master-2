import { afterEach, expect, it, vi } from "vitest";
import { cleanup, fireEvent, render, waitFor, within } from "@testing-library/react";
import LearnerJourney from "./journey";
import { FixtureOwner, OWNER_LOCK } from "./ownership";
import { readJourney } from "./storage";
import type { LearnerApi } from "./api";
import sample from "@german-master/contracts/examples/session.json";
import targets from "@german-master/contracts/examples/target-page.json";
import catalog from "@german-master/contracts/examples/catalog.json";
import acknowledgment from "@german-master/contracts/examples/attempt-response.json";
import { PROFILE_PENDING_KEY } from "./setup";

// FIFO exclusive scheduler represents separate tabs sharing the browser lock.
function locks() {
  let tail = Promise.resolve();
  return { request: vi.fn((name: string, options: { signal: AbortSignal }, work: () => Promise<void>) => {
    expect(name).toBe(OWNER_LOCK);
    const result = tail.then(async () => { if (!options.signal.aborted) await work(); });
    tail = result.catch(() => {});
    return result;
  }) };
}
function fixture(): LearnerApi {
  return {
    profile: vi.fn(async () => ({ apiVersion: "v2", revision: 1, setupCompleted: true, preferences: { locale: "en", timezone: "Europe/Berlin", level: "B1", sessionQuestionCount: 15 } })),
    catalog: vi.fn(async () => catalog), targets: vi.fn(async () => targets),
    sync: vi.fn(async cursor => ({ apiVersion: "v2", changes: [], nextCursor: cursor, hasMore: false })),
    createSession: vi.fn(async () => sample),
    expose: vi.fn(async event => ({ eventId: event.eventId, status: "duplicate", serverSequence: 1 })),
    submit: vi.fn(), saveProfile: vi.fn(), createFocusedSession: vi.fn(),
  } as LearnerApi;
}
afterEach(() => { cleanup(); localStorage.clear(); vi.restoreAllMocks(); vi.unstubAllGlobals(); });
it("waits for an in-flight operation before relinquishing ownership", async () => {
  const owner = new FixtureOwner(); let finish!: () => void;
  const work = owner.run(() => new Promise<void>(resolve => { finish = resolve; }));
  const released = vi.fn(); void owner.closed.then(released);
  owner.close(); await Promise.resolve(); expect(released).not.toHaveBeenCalled();
  await expect(owner.run(async () => {})).rejects.toThrow("closed");
  finish(); await work; await owner.closed; expect(released).toHaveBeenCalledOnce();
});
it("blocks a second tab offline and reloads the exact frozen skip after acknowledgment-save failure", async () => {
  vi.stubGlobal("navigator", { locks: locks() });
  const api = fixture(); let fail = false; let answer!: () => void;
  const storage = { getItem: (key: string) => localStorage.getItem(key), setItem: (key: string, value: string) => {
    if (fail) throw Error("disk full"); localStorage.setItem(key, value);
  } };
  api.expose = vi.fn(async event => { await new Promise<void>(resolve => { answer = resolve; }); fail = true; return { eventId: event.eventId, status: "accepted", serverSequence: 1 }; });
  const first = render(<LearnerJourney api={api} storage={storage} />);
  const a = within(first.container);
  await waitFor(() => expect(a.getByText("Start short practice")).toBeEnabled());
  fireEvent.click(a.getByText("Start short practice")); await a.findByLabelText("Your answer");
  fireEvent.change(a.getByLabelText("Your answer"), { target: { value: "offline draft" } });
  fireEvent.click(a.getByText("Skip"));
  await waitFor(() => expect(api.expose).toHaveBeenCalledOnce());
  const frozen = readJourney(localStorage).practice!.pendingExposure;
  const reads = vi.mocked(api.profile).mock.calls.length;
  const second = render(<LearnerJourney api={api} storage={storage} />);
  const b = within(second.container);
  expect(b.getByRole("status")).toHaveTextContent("another tab");
  expect(b.queryByText("Skip")).toBeNull(); expect(api.profile).toHaveBeenCalledTimes(reads);
  first.unmount(); await Promise.resolve();
  expect(b.getByRole("status")).toHaveTextContent("another tab");
  answer();
  await waitFor(() => expect(b.queryByText("another tab", { exact: false })).toBeNull());
  expect(readJourney(localStorage).practice!.pendingExposure).toEqual(frozen);
  fail = false;
  api.expose = vi.fn(async event => ({ eventId: event.eventId, status: "duplicate", serverSequence: 1 }));
  fireEvent.click(await b.findByText("Continue practice")); await b.findByLabelText("Your answer");
  expect(b.getByLabelText("Your answer")).toHaveValue("offline draft");
  fireEvent.click(b.getByText("Retry skip"));
  await waitFor(() => expect(readJourney(localStorage).practice!.index).toBe(1));
  expect(api.expose).toHaveBeenCalledExactlyOnceWith(frozen);
  expect(readJourney(localStorage).practice!.skippedCount).toBe(1);
});
it("fails closed without browser coordination, preserving storage and sending no HTTP", () => {
  vi.stubGlobal("navigator", {});
  const api = fixture(); const before = localStorage.length;
  const view = render(<LearnerJourney api={api} />);
  expect(within(view.container).getByRole("alert")).toHaveTextContent("Web Locks");
  expect(api.profile).not.toHaveBeenCalled(); expect(localStorage.length).toBe(before);
});
it("hands off a lost-response attempt without generating another session or answer ID", async () => {
  vi.stubGlobal("navigator", { locks: locks() });
  const api = fixture(); api.submit = vi.fn().mockRejectedValue(Error("offline"));
  const first = render(<LearnerJourney api={api} />); const a = within(first.container);
  await waitFor(() => expect(a.getByText("Start short practice")).toBeEnabled());
  fireEvent.click(a.getByText("Start short practice")); await a.findByLabelText("Your answer");
  fireEvent.change(a.getByLabelText("Your answer"), { target: { value: "Berufe" } });
  fireEvent.click(a.getByText("Check answer")); await a.findByRole("alert");
  const saved = readJourney(localStorage).practice!;
  const second = render(<LearnerJourney api={api} />); const b = within(second.container);
  expect(b.getByRole("status")).toHaveTextContent("another tab");
  first.unmount();
  fireEvent.click(await b.findByText("Continue practice")); await b.findByLabelText("Your answer");
  api.submit = vi.fn(async attempt => ({ ...acknowledgment.acknowledgments[0], attemptId: attempt.attemptId })) as LearnerApi["submit"];
  fireEvent.click(b.getByText("Retry")); await b.findByText("Continue");
  expect(api.submit).toHaveBeenCalledExactlyOnceWith(saved.pending);
  expect(api.createSession).toHaveBeenCalledOnce();
  expect(readJourney(localStorage).practice).toMatchObject({ request: saved.request, confirmedCount: 1 });
});
it("preserves the frozen profile request when an offline owner hands over to another tab", async () => {
  vi.stubGlobal("navigator", { locks: locks() });
  const api = fixture(); api.saveProfile = vi.fn().mockRejectedValue(Error("offline"));
  const first = render(<LearnerJourney api={api} />); const a = within(first.container);
  await waitFor(() => expect(a.getByText("Practice preferences")).toBeEnabled());
  fireEvent.click(a.getByText("Practice preferences"));
  await a.findByText("Save preferences");
  fireEvent.change(a.getByLabelText("Timezone (IANA name)"), { target: { value: "UTC" } });
  fireEvent.click(a.getByText("Save preferences")); await a.findByRole("alert");
  const frozen = JSON.parse(localStorage.getItem(PROFILE_PENDING_KEY)!);
  const second = render(<LearnerJourney api={api} />); const b = within(second.container);
  expect(b.getByRole("status")).toHaveTextContent("another tab");
  expect(api.saveProfile).toHaveBeenCalledOnce(); first.unmount();
  await b.findByText("Retry");
  expect(b.getByLabelText("Timezone (IANA name)")).toHaveValue("UTC");
  api.saveProfile = vi.fn(async request => ({ apiVersion: "v2", revision: 2, setupCompleted: true, preferences: request.preferences }));
  fireEvent.click(b.getByText("Retry"));
  await waitFor(() => expect(localStorage.getItem(PROFILE_PENDING_KEY)).toBe(""));
  expect(api.saveProfile).toHaveBeenCalledExactlyOnceWith(frozen);
});


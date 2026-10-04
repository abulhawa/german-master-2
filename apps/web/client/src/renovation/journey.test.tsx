import { afterEach, describe, expect, it, vi } from "vitest";
import { cleanup, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { SessionSchema, AttemptBatchResponseSchema, TargetPageSchema } from "@german-master/contracts";
import sample from "@german-master/contracts/examples/session.json";
import acknowledgment from "@german-master/contracts/examples/attempt-response.json";
import targetPage from "@german-master/contracts/examples/target-page.json";
import catalog from "@german-master/contracts/examples/catalog.json";
import { PROFILE_PENDING_KEY } from "./setup";
import LearnerJourney from "./journey";
import { localLearnerApi, type LearnerApi } from "./api";
import { emptyJourney, readJourney, saveJourney, snapshot, pull, STORAGE_KEY } from "./storage";

const session = SessionSchema.parse(sample);
const ack = AttemptBatchResponseSchema.parse(acknowledgment).acknowledgments[0];
const page = TargetPageSchema.parse(targetPage);
function apiFixture(): LearnerApi {
  return { expose: vi.fn(async event => ({ eventId: event.eventId, status: "accepted" as const, serverSequence: 1 })), profile: vi.fn(async () => ({ apiVersion: "v2", revision: 1, setupCompleted: true, preferences: { locale: "en", timezone: "Europe/Berlin", level: "B1", sessionQuestionCount: 15 } })), saveProfile: vi.fn(async request => ({ apiVersion: "v2", revision: 2, setupCompleted: true, preferences: request.preferences })), catalog: vi.fn(async () => catalog as Awaited<ReturnType<LearnerApi["catalog"]>>), createFocusedSession: vi.fn(async () => ({ ...session, questions: [session.questions[0]] })), createSession: vi.fn(async () => session), submit: vi.fn(async input => ({ ...ack, attemptId: input.attemptId })),
    targets: vi.fn(async () => page), sync: vi.fn(async cursor => ({ apiVersion: "v2", changes: [], nextCursor: cursor, hasMore: false })) };
}
afterEach(() => { cleanup(); localStorage.clear(); vi.unstubAllGlobals(); });
it("retries a frozen skip after a lost response and reload, preserving its draft", async () => {
  const api = apiFixture();
  api.expose = vi.fn().mockRejectedValueOnce(Error("lost response")).mockImplementation(async event => ({ eventId: event.eventId, status: "duplicate", serverSequence: 1 }));
  await start(api);
  fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "unfinished" } });
  fireEvent.click(screen.getByText("Skip"));
  await screen.findByRole("alert");
  const saved = readJourney(localStorage).practice!;
  expect(saved).toMatchObject({ index: 0, draft: { text: "unfinished" }, skippedCount: 0 });
  expect(saved.pendingExposure).toMatchObject({ disposition: "skip", sessionQuestionId: session.questions[0].id, exerciseRevision: session.questions[0].exercise.revision });
  expect(screen.getByLabelText("Your answer")).toBeDisabled();
  expect(screen.queryByText("Check answer")).not.toBeInTheDocument();
  cleanup(); await start(api);
  expect(screen.getByLabelText("Your answer")).toHaveValue("unfinished");
  fireEvent.click(screen.getByText("Retry skip"));
  await waitFor(() => expect(readJourney(localStorage).practice!.index).toBe(1));
  expect(api.expose).toHaveBeenLastCalledWith(saved.pendingExposure);
  expect(readJourney(localStorage).practice).toMatchObject({ skippedCount: 1, confirmedCount: 0, correctCount: 0, pendingExposure: null, draft: null });
  expect(screen.getByRole("heading")).toHaveFocus();
  expect(api.submit).not.toHaveBeenCalled();
});
it("blocks skip before HTTP when storage fails", async () => {
  const api = apiFixture(); let fail = false;
  const storage = { getItem: (key: string) => localStorage.getItem(key), setItem: (key: string, value: string) => { if (fail) throw Error("disk full"); localStorage.setItem(key, value); } };
  render(<LearnerJourney api={api} storage={storage} />);
  await waitFor(() => expect(screen.getByText("Start short practice")).toBeEnabled());
  fireEvent.click(screen.getByText("Start short practice"));
  await screen.findByLabelText("Your answer");
  fail = true; fireEvent.click(screen.getByText("Skip"));
  expect(await screen.findByRole("alert")).toHaveTextContent("Could not save practice");
  expect(api.expose).not.toHaveBeenCalled();
  expect(readJourney(localStorage).practice!.index).toBe(0);
});
it("keeps rejected skips and drafts without grade or advancement", async () => {
  const api = apiFixture();
  api.expose = vi.fn(async event => ({ eventId: event.eventId, status: "rejected", error: { code: "question_completed", message: "Already completed", requestId: crypto.randomUUID(), retryable: false } }));
  await start(api); fireEvent.click(screen.getByText("Skip"));
  await waitFor(() => expect(readJourney(localStorage).practice!.rejected).toBe(true));
  expect(readJourney(localStorage).practice).toMatchObject({ index: 0, skippedCount: 0, evaluation: null });
  expect(screen.getByText("Retry skip")).toBeDisabled();
});
it("replays skip when saving its server acknowledgment fails", async () => {
  const api = apiFixture(); let fail = false;
  const storage = { getItem: (key: string) => localStorage.getItem(key), setItem: (key: string, value: string) => { if (fail) throw Error("disk full"); localStorage.setItem(key, value); } };
  api.expose = vi.fn(async event => { fail = true; return { eventId: event.eventId, status: "accepted", serverSequence: 1 }; });
  render(<LearnerJourney api={api} storage={storage} />);
  await waitFor(() => expect(screen.getByText("Start short practice")).toBeEnabled());
  fireEvent.click(screen.getByText("Start short practice")); await screen.findByLabelText("Your answer");
  fireEvent.click(screen.getByText("Skip"));
  await screen.findByRole("alert");
  const event = readJourney(localStorage).practice!.pendingExposure;
  expect(readJourney(localStorage).practice).toMatchObject({ index: 0, skippedCount: 0 });
  fail = false; cleanup();
  api.expose = vi.fn(async input => ({ eventId: input.eventId, status: "duplicate", serverSequence: 1 }));
  await start(api); fireEvent.click(screen.getByText("Retry skip"));
  await waitFor(() => expect(readJourney(localStorage).practice!.index).toBe(1));
  expect(api.expose).toHaveBeenCalledWith(event);
  expect(readJourney(localStorage).practice!.skippedCount).toBe(1);
});
it("prevents skip while an answer awaits confirmation", async () => {
  const api = apiFixture(); api.submit = vi.fn().mockRejectedValue(Error("lost response"));
  await start(api); fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "Berufe" } });
  fireEvent.click(screen.getByText("Check answer")); await screen.findByRole("alert");
  expect(screen.queryByText("Skip")).not.toBeInTheDocument();
  expect(api.expose).not.toHaveBeenCalled();
});
it("counts graded and skipped questions separately and refreshes confirmed targets", async () => {
  const api = apiFixture(); await start(api);
  fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "Berufe" } });
  fireEvent.click(screen.getByText("Check answer"));
  fireEvent.click(await screen.findByText("Continue"));
  const reads = vi.mocked(api.targets).mock.calls.length;
  for (let index = 1; index < session.questions.length; index++) {
    fireEvent.click(screen.getByText("Skip"));
    await waitFor(() => expect(readJourney(localStorage).practice!.index).toBe(index + 1));
  }
  expect(screen.getByText("Questions skipped: 4")).toBeInTheDocument();
  expect(screen.getByRole("status")).toHaveTextContent("Answers confirmed: 1 / 5");
  expect(screen.getByText("Correct answers: 1")).toBeInTheDocument();
  await waitFor(() => expect(vi.mocked(api.targets).mock.calls.length).toBeGreaterThan(reads));
});
it("reads pre-skip saved sessions without losing pending attempts", () => {
  const saved = emptyJourney();
  const practice = { request: { apiVersion: "v2", requestId: crypto.randomUUID(), questionCount: 5, capabilities: ["short_answer@1"] }, session, index: 0,
    draft: null, assisted: false, pending: null, evaluation: null, rejected: false, confirmedCount: 0, correctCount: 0 };
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ ...saved, practice }));
  expect(readJourney(localStorage).practice).toEqual({ ...practice, pendingExposure: null, skippedCount: 0 });
});
it("validates exposure acknowledgment linkage before accepting a response", async () => {
  const event = { eventId: crypto.randomUUID(), sessionQuestionId: session.questions[0].id, exerciseRevision: 1, deviceId: crypto.randomUUID(), disposition: "skip" as const, occurredAt: new Date().toISOString() };
  const fetchMock = vi.fn(async (_path: string, _options: RequestInit) => ({ ok: true, json: async () => ({ apiVersion: "v2", acknowledgments: [{ eventId: crypto.randomUUID(), status: "accepted", serverSequence: 1 }] }) }));
  vi.stubGlobal("fetch", fetchMock);
  await expect(localLearnerApi().expose(event)).rejects.toThrow("linkage mismatch");
  expect(JSON.parse(fetchMock.mock.calls[0][1].body as string)).toEqual({ apiVersion: "v2", events: [event] });
});
async function start(api = apiFixture()) {
  render(<LearnerJourney api={api} />);
  await waitFor(() => expect(screen.getByText(/Start short practice|Continue practice/)).toBeEnabled());
  fireEvent.click(screen.getByText(/Start short practice|Continue practice/));
  await waitFor(() => expect(screen.getByText("Discard this preview session")).toBeEnabled());
  return api;
}
it("requires setup, confirms preferences and reports unavailable B2 drafts", async () => {
  const api = apiFixture();
  let profile: Awaited<ReturnType<LearnerApi["profile"]>> = { apiVersion: "v2", revision: 0, setupCompleted: false,
    preferences: { locale: "en", timezone: "Europe/Berlin", level: "B1", sessionQuestionCount: 15 } };
  api.profile = vi.fn(async () => profile);
  api.saveProfile = vi.fn(async input => { profile = { ...profile, revision: 1, setupCompleted: true, preferences: input.preferences }; return profile; });
  api.catalog = vi.fn(async () => ({ ...catalog, targets: catalog.targets.map(t => ({ ...t, availableQuestionCount: profile.preferences.level === "B2" ? 0 : 1 })) } as Awaited<ReturnType<LearnerApi["catalog"]>>));
  render(<LearnerJourney api={api} />);
  await waitFor(() => expect(screen.getByRole("heading", { name: "Set up your practice" })).toHaveFocus());
  expect(screen.queryByText("Start short practice")).not.toBeInTheDocument();
  fireEvent.change(screen.getByLabelText("Practice level"), { target: { value: "B2" } });
  fireEvent.change(screen.getByLabelText("Timezone (IANA name)"), { target: { value: "UTC" } });
  fireEvent.click(screen.getByText("Save preferences"));
  await screen.findByText(/No new questions are available/);
  expect(screen.getByText("Start short practice")).toBeDisabled();
  expect(api.createSession).not.toHaveBeenCalled();
  fireEvent.click(screen.getByText("Practice preferences"));
  fireEvent.change(screen.getByLabelText("Practice level"), { target: { value: "B1" } });
  fireEvent.click(screen.getByText("Save preferences"));
  await waitFor(() => expect(screen.getByText("Start short practice")).toBeEnabled());
});
it("retries frozen setup payload after lost response and reload without overwriting practice", async () => {
  const api = apiFixture();
  const saved = emptyJourney(); saved.practice = { request: { apiVersion: "v2", requestId: crypto.randomUUID(), questionCount: 5,
    capabilities: ["short_answer@1"] }, session, index: 0, draft: { type: "short_answer", text: "draft" }, assisted: false, pending: null, evaluation: null,
    rejected: false, pendingExposure: null, skippedCount: 0, confirmedCount: 0, correctCount: 0 };
  saveJourney(localStorage, saved);
  let profile = await api.profile();
  api.profile = vi.fn(async () => profile);
  api.saveProfile = vi.fn().mockRejectedValueOnce(Error("lost response")).mockImplementation(async input => {
    profile = { ...profile, revision: 2, preferences: input.preferences }; return profile;
  });
  render(<LearnerJourney api={api} />);
  await waitFor(() => expect(screen.getByText("Practice preferences")).toBeEnabled());
  fireEvent.click(screen.getByText("Practice preferences"));
  fireEvent.change(screen.getByLabelText("Timezone (IANA name)"), { target: { value: "Asia/Tokyo" } });
  fireEvent.click(screen.getByText("Save preferences"));
  await screen.findByRole("alert");
  const request = JSON.parse(localStorage.getItem(PROFILE_PENDING_KEY)!);
  cleanup(); render(<LearnerJourney api={api} />);
  await screen.findByText("Retry");
  expect(screen.getByLabelText("Timezone (IANA name)")).toBeDisabled();
  fireEvent.click(screen.getByText("Retry"));
  await screen.findByRole("heading", { name: /Your next practice|find what to practise/ });
  expect(api.saveProfile).toHaveBeenLastCalledWith(request);
  expect(readJourney(localStorage).practice).toEqual(saved.practice);
  expect(localStorage.getItem(PROFILE_PENDING_KEY)).toBe("");
});
it("blocks setup writes when local pending storage fails", async () => {
  const api = apiFixture(); api.profile = vi.fn(async () => ({ ...(await apiFixture().profile()), setupCompleted: false }));
  const storage = { getItem: () => null, setItem: (key: string) => { if (key === PROFILE_PENDING_KEY) throw Error("disk full"); } };
  render(<LearnerJourney api={api} storage={storage} />);
  fireEvent.click(await screen.findByText("Save preferences"));
  expect(await screen.findByRole("alert")).toHaveTextContent("Preferences could not be confirmed");
  expect(api.saveProfile).not.toHaveBeenCalled();
});

it("reloads current preferences to correct an invalid frozen setup request", async () => {
  const api = apiFixture();
  const request = { apiVersion: "v2", requestId: crypto.randomUUID(), expectedRevision: 1,
    preferences: { locale: "en", timezone: "Invalid/Zone", level: "B1", sessionQuestionCount: 15 } };
  localStorage.setItem(PROFILE_PENDING_KEY, JSON.stringify(request));
  render(<LearnerJourney api={api} />);
  expect(await screen.findByText("Retry")).toBeInTheDocument();
  expect(screen.getByLabelText("Timezone (IANA name)")).toBeDisabled();
  fireEvent.click(screen.getByText("Reload current preferences"));
  await waitFor(() => expect(screen.getByLabelText("Timezone (IANA name)")).toBeEnabled());
  expect(screen.getByLabelText("Timezone (IANA name)")).toHaveValue("Europe/Berlin");
  expect(localStorage.getItem(PROFILE_PENDING_KEY)).toBe("");
  expect(api.saveProfile).not.toHaveBeenCalled();
});

describe("isolated learner journey", () => {
  it("opens Topics and target detail, then recovers the exact focused request after reload", async () => {
    const api = apiFixture();
    api.createFocusedSession = vi.fn().mockRejectedValueOnce(Error("lost response")).mockResolvedValue({ ...session, questions: [session.questions[0]] });
    render(<LearnerJourney api={api} />);
    fireEvent.click(screen.getByRole("button", { name: "Topics" }));
    fireEvent.click(await screen.findByRole("button", { name: "German in everyday work" }));
    fireEvent.click(screen.getByRole("button", { name: "Plural of Beruf" }));
    expect(screen.getByRole("heading")).toHaveFocus();
    expect(screen.getByText("Available questions: 1")).toBeInTheDocument();
    await waitFor(() => expect(screen.getByRole("button", { name: "Practise this" })).toBeEnabled());
    fireEvent.click(screen.getByRole("button", { name: "Practise this" }));
    await screen.findByRole("alert");
    const saved = readJourney(localStorage).practice!.request;
    expect(saved).toMatchObject({ questionCount: 1, focus: { type: "target", id: catalog.targets[0].id } });
    cleanup(); await start(api);
    expect(api.createFocusedSession).toHaveBeenLastCalledWith(saved);
    expect(api.createSession).not.toHaveBeenCalled();
    fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "Berufe" } });
    fireEvent.click(screen.getByText("Check answer"));
    fireEvent.click(await screen.findByText("Continue"));
    expect(screen.getByRole("status")).toHaveTextContent("Answers confirmed: 1 / 1");
  });
  it("starts topic focus and preserves unfinished practice when browsing another target", async () => {
    const api = apiFixture();
    render(<LearnerJourney api={api} />);
    fireEvent.click(screen.getByRole("button", { name: "Topics" }));
    fireEvent.click(await screen.findByRole("button", { name: "German in everyday work" }));
    await waitFor(() => expect(screen.getByRole("button", { name: "Practise this" })).toBeEnabled());
    fireEvent.click(screen.getByRole("button", { name: "Practise this" }));
    await screen.findByLabelText("Your answer");
    expect(api.createFocusedSession).toHaveBeenCalledWith(expect.objectContaining({ questionCount: 5, focus: { type: "topic", id: catalog.topics[0].id } }));
    const saved = readJourney(localStorage).practice!.request;
    fireEvent.click(screen.getByText("Close practice"));
    fireEvent.click(screen.getByRole("button", { name: "Topics" }));
    fireEvent.click(screen.getByRole("button", { name: "German in everyday work" }));
    fireEvent.click(screen.getByRole("button", { name: "Dative after mit" }));
    expect(screen.queryByRole("button", { name: "Practise this" })).not.toBeInTheDocument();
    expect(readJourney(localStorage).practice!.request).toEqual(saved);
    fireEvent.click(screen.getByRole("button", { name: "Continue practice" }));
    expect(api.createFocusedSession).toHaveBeenCalledTimes(1);
  });
  it("shows catalog failures with retry and renders German metadata", async () => {
    const api = apiFixture(); api.catalog = vi.fn().mockRejectedValueOnce(Error("offline")).mockResolvedValue(catalog);
    render(<LearnerJourney api={api} />);
    fireEvent.click(screen.getByRole("button", { name: "Topics" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("Topic information is unavailable");
    fireEvent.click(screen.getByRole("button", { name: "Reload topics" }));
    await screen.findByRole("button", { name: "German in everyday work" });
    fireEvent.change(screen.getByLabelText("Interface language"), { target: { value: "de" } });
    expect(screen.getByRole("button", { name: "Deutsch im Arbeitsalltag" })).toBeInTheDocument();
  });
  it("reuses a saved session request after an ambiguous creation failure and reload", async () => {
    const api = apiFixture();
    api.createSession = vi.fn().mockRejectedValueOnce(Error("lost session response")).mockResolvedValue(session);
    render(<LearnerJourney api={api} />);
    await waitFor(() => expect(screen.getByText("Start short practice")).toBeEnabled());
    fireEvent.click(screen.getByText("Start short practice"));
    await screen.findByRole("alert");
    const request = readJourney(localStorage).practice!.request;
    cleanup(); await start(api);
    expect(api.createSession).toHaveBeenLastCalledWith(request);
    expect(api.createSession).toHaveBeenCalledTimes(2);
  });
  it("resumes a saved draft, then retries a lost response with the exact same payload across reload", async () => {
    const api = apiFixture();
    api.submit = vi.fn().mockRejectedValueOnce(Error("lost response")).mockImplementation(async input => ({ ...ack, attemptId: input.attemptId }));
    await start(api);
    fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "berufe" } });
    cleanup();
    await start(api);
    expect(screen.getByLabelText("Your answer")).toHaveValue("berufe");
    expect(api.createSession).toHaveBeenCalledTimes(1);
    fireEvent.click(screen.getByText("Check answer"));
    await screen.findByRole("alert");
    const pending = readJourney(localStorage).practice!.pending;
    cleanup();
    await start(api);
    expect(screen.getByLabelText("Your answer")).toBeDisabled();
    fireEvent.click(screen.getByText("Retry"));
    await screen.findByText("Continue");
    expect(api.submit).toHaveBeenLastCalledWith(pending);
    expect(screen.getByRole("status")).toHaveTextContent("Correct");
    expect(readJourney(localStorage).practice!.confirmedCount).toBe(1);
    cleanup();
    await start(api);
    expect(screen.getByText("Continue")).toBeInTheDocument();
    expect(api.submit).toHaveBeenCalledTimes(2);
  });
  it("completes all five forms and refreshes server Progress without inventing mastery", async () => {
    const api = await start();
    for (let i = 0; i < 5; i++) {
      if (i === 0) fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "Berufe" } });
      if (i === 1) fireEvent.click(screen.getByLabelText("dem"));
      if (i === 2) fireEvent.change(screen.getByLabelText("Präposition"), { target: { value: "ins" } });
      if (i === 3) for (const word of ["weil", "ich", "heute", "im", "Büro", "arbeite"]) fireEvent.click(screen.getByRole("button", { name: word }));
      if (i === 4) for (const [label, value] of [["du", "arbeitest"], ["ihr", "arbeitet"]]) fireEvent.change(screen.getByLabelText(label), { target: { value } });
      fireEvent.click(screen.getByText("Check answer"));
      await screen.findByText("Continue");
      fireEvent.click(screen.getByText("Continue"));
    }
    expect(screen.getByRole("status")).toHaveTextContent("Answers confirmed: 5 / 5");
    await waitFor(() => expect(api.sync).toHaveBeenCalled());
    await waitFor(() => expect(screen.getByText("Discard this preview session")).toBeEnabled());
    fireEvent.click(screen.getByText("Progress"));
    expect(screen.getByText("Plural of Beruf")).toBeInTheDocument();
    expect(screen.getByText("Needs practice")).toBeInTheDocument();
    expect(screen.queryByText("Mastered")).not.toBeInTheDocument();
    expect(screen.getByRole("heading")).toHaveFocus();
  });
  it("persists partial slot drafts, token order and hint assistance", async () => {
    const state = emptyJourney();
    state.practice = { request: { apiVersion: "v2", requestId: crypto.randomUUID(), questionCount: 5, capabilities: ["multi_slot@1"] }, session,
      index: 4, draft: null, pending: null, evaluation: null, assisted: false, rejected: false, correctCount: 4, confirmedCount: 4 };
    saveJourney(localStorage, state);
    await start();
    fireEvent.change(screen.getByLabelText("du"), { target: { value: "arbeitest" } });
    fireEvent.click(screen.getByText("Hint"));
    cleanup();
    await start();
    expect(screen.getByLabelText("du")).toHaveValue("arbeitest");
    expect(screen.getByText("Check answer")).toBeDisabled();
    expect(readJourney(localStorage).practice!.assisted).toBe(true);
    const next = readJourney(localStorage); next.practice!.index = 3; next.practice!.draft = { type: "word_order", tokenIds: [session.questions[3].exercise.type === "word_order" ? session.questions[3].exercise.tokens[0].id : ""] };
    saveJourney(localStorage, next);
    cleanup(); await start();
    expect(document.querySelectorAll(".gm-order li")).toHaveLength(1);
  });
  it("does not send an answer when persisting the frozen attempt fails", async () => {
    const api = await start();
    fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "Berufe" } });
    const spy = vi.spyOn(Storage.prototype, "setItem").mockImplementation(() => { throw Error("quota"); });
    fireEvent.click(screen.getByText("Check answer"));
    expect(await screen.findByRole("alert")).toHaveTextContent("New submissions are blocked");
    expect(api.submit).not.toHaveBeenCalled(); spy.mockRestore();
  });
  it("preserves a corrupt record and blocks mutation", () => {
    localStorage.setItem(STORAGE_KEY, "corrupt");
    const api = apiFixture(); render(<LearnerJourney api={api} />);
    expect(screen.getByRole("alert")).toHaveTextContent("preserved");
    expect(localStorage.getItem(STORAGE_KEY)).toBe("corrupt"); expect(api.targets).not.toHaveBeenCalled();
  });
  it("handles denied browser storage access without mounting the practice flow", () => {
    const api = apiFixture();
    render(<LearnerJourney api={api} storage={{ getItem() { throw Error("denied"); }, setItem() { throw Error("denied"); } }} />);
    expect(screen.getByRole("alert")).toHaveTextContent("could not be read");
    expect(api.createSession).not.toHaveBeenCalled();
  });
  it("keeps confirmed data after refresh failure and permanent rejection never becomes feedback", async () => {
    const api = await start();
    api.submit = vi.fn(async input => ({ status: "rejected", attemptId: input.attemptId, error: { code: "question_already_answered", message: "Rejected", requestId: crypto.randomUUID(), retryable: false } }));
    fireEvent.change(screen.getByLabelText("Your answer"), { target: { value: "Berufe" } });
    fireEvent.click(screen.getByText("Check answer"));
    await screen.findByRole("alert"); expect(screen.queryByText("Continue")).not.toBeInTheDocument();
    api.targets = vi.fn().mockRejectedValue(Error("offline"));
    fireEvent.click(screen.getByText("Close practice")); fireEvent.click(screen.getByText("Progress"));
    await screen.findByRole("alert"); expect(screen.getByText("Plural of Beruf")).toBeInTheDocument();
  });
});
describe("confirmed read reconciliation", () => {
  it("uses no-store authenticated reads and validates transport", async () => {
    const fetch = vi.fn().mockResolvedValue({ ok: true, json: async () => page }); vi.stubGlobal("fetch", fetch);
    await localLearnerApi().targets("cursor value");
    expect(fetch).toHaveBeenCalledWith("/v2/targets?limit=50&cursor=cursor%20value", expect.objectContaining({ cache: "no-store" }));
    fetch.mockResolvedValue({ ok: true, json: async () => ({ apiVersion: "bad" }) });
    await expect(localLearnerApi().targets()).rejects.toThrow();
  });
  it("never commits a partial snapshot or a mismatched watermark", async () => {
    const api = apiFixture(); api.targets = vi.fn().mockResolvedValueOnce({ ...page, nextPageCursor: "next" }).mockRejectedValueOnce(Error("offline"));
    await expect(snapshot(api)).rejects.toThrow("offline");
    api.targets = vi.fn().mockResolvedValueOnce({ ...page, nextPageCursor: "next" }).mockResolvedValueOnce({ ...page, generatedAt: "2026-10-05T00:00:00Z" });
    await expect(snapshot(api)).rejects.toThrow("Snapshot changed");
  });
  it("saves target upserts and polling cursor together, and retains a committed first page after a later failure", async () => {
    const api = apiFixture(); const initial = await snapshot(api);
    const cursor = crypto.randomUUID();
    api.sync = vi.fn().mockResolvedValueOnce({ apiVersion: "v2", changes: [{ sequence: 2, operation: "upsert", target: { ...page.targets[0], lastSequence: 2, state: "learning" } }], nextCursor: cursor, hasMore: true }).mockRejectedValueOnce(Error("offline"));
    const state = emptyJourney(); state.confirmed = initial; saveJourney(localStorage, state);
    await expect(pull(api, initial, confirmed => saveJourney(localStorage, { ...state, confirmed }))).rejects.toThrow("offline");
    const recovered = readJourney(localStorage).confirmed!;
    expect(recovered.cursor).toBe(cursor); expect(recovered.targets[0].state).toBe("learning");
    api.sync = vi.fn(async position => ({ apiVersion: "v2", changes: [], nextCursor: position, hasMore: false }));
    await pull(api, recovered, () => {}); expect(api.sync).toHaveBeenCalledWith(cursor);
  });
});

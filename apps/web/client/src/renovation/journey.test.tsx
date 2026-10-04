import { afterEach, describe, expect, it, vi } from "vitest";
import { cleanup, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { SessionSchema, AttemptBatchResponseSchema, TargetPageSchema } from "@german-master/contracts";
import sample from "@german-master/contracts/examples/session.json";
import acknowledgment from "@german-master/contracts/examples/attempt-response.json";
import targetPage from "@german-master/contracts/examples/target-page.json";
import catalog from "@german-master/contracts/examples/catalog.json";
import LearnerJourney from "./journey";
import { localLearnerApi, type LearnerApi } from "./api";
import { emptyJourney, readJourney, saveJourney, snapshot, pull, STORAGE_KEY } from "./storage";

const session = SessionSchema.parse(sample);
const ack = AttemptBatchResponseSchema.parse(acknowledgment).acknowledgments[0];
const page = TargetPageSchema.parse(targetPage);
function apiFixture(): LearnerApi {
  return { catalog: vi.fn(async () => catalog as Awaited<ReturnType<LearnerApi["catalog"]>>), createFocusedSession: vi.fn(async () => ({ ...session, questions: [session.questions[0]] })), createSession: vi.fn(async () => session), submit: vi.fn(async input => ({ ...ack, attemptId: input.attemptId })),
    targets: vi.fn(async () => page), sync: vi.fn(async cursor => ({ apiVersion: "v2", changes: [], nextCursor: cursor, hasMore: false })) };
}
afterEach(() => { cleanup(); localStorage.clear(); vi.unstubAllGlobals(); });
async function start(api = apiFixture()) {
  render(<LearnerJourney api={api} />);
  await waitFor(() => expect(screen.getByText(/Start short practice|Continue practice/)).toBeEnabled());
  fireEvent.click(screen.getByText(/Start short practice|Continue practice/));
  await waitFor(() => expect(screen.getByText("Discard this preview session")).toBeEnabled());
  return api;
}
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

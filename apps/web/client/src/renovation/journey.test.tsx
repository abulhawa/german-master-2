import { afterEach, describe, expect, it, vi } from "vitest";
import { cleanup, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { SessionSchema, AttemptBatchResponseSchema, TargetPageSchema } from "@german-master/contracts";
import sample from "@german-master/contracts/examples/session.json";
import formatSample from "@german-master/contracts/examples/practice-formats-session.json";
import acknowledgment from "@german-master/contracts/examples/attempt-response.json";
import targetPage from "@german-master/contracts/examples/target-page.json";
import catalog from "@german-master/contracts/examples/catalog.json";
import { PROFILE_PENDING_KEY } from "./setup";
import { sessionRequest, prepareAttempt } from "../foundation/api";
import { OwnedLearnerJourney as LearnerJourney } from "./journey";
import { localLearnerApi, SyncCursorReset, type LearnerApi } from "./api";
import { emptyJourney, readJourney, saveJourney, snapshot, pull, STORAGE_KEY } from "./storage";
import { AccountBinding, FIXTURE_SUBJECT } from './account';
import { WebReserve } from './reserve';

it('deletion requires confirmation, survives remount and removes only owned local work after a receipt',async()=> {
  const api=apiFixture();let lose=true;
  api.deleteLearner=vi.fn(async request=> {if(lose) throw Error('lost');return {apiVersion:'v2',requestId:request.requestId,subject:FIXTURE_SUBJECT,status:'deleted',deletedAt:'2026-10-05T12:00:00Z'};});
  const db=new WebReserve('deletion-ui');const foreign=new AccountBinding({subject:'00000000-0000-4000-8000-000000000099',generation:0},()=>null).storage(localStorage);
  foreign.setItem(STORAGE_KEY,'foreign preserved');await db.table('records').put({id:'marker',value:'owned'});
  const rendered=render(<LearnerJourney api={api} reserve={db}/>);
  fireEvent.click(screen.getByRole('button', {name:'Account', exact:true}));
  await waitFor(()=>expect(screen.getByText('Delete learner data…')).toBeEnabled());
  fireEvent.click(screen.getByText('Delete learner data…'));expect(api.deleteLearner).not.toHaveBeenCalled();
  fireEvent.click(screen.getByText('Keep my data'));expect(api.deleteLearner).not.toHaveBeenCalled();
  fireEvent.click(screen.getByText('Delete learner data…'));fireEvent.click(screen.getByText('Confirm deletion of learner data'));
  await screen.findByText('Retry saved deletion');expect(screen.queryByText('Start short practice')).toBeNull();
  expect(await db.table('records').get('marker')).toBeDefined();
  const reads=vi.mocked(api.profile).mock.calls.length;rendered.unmount();render(<LearnerJourney api={api} reserve={db}/>);
  expect(vi.mocked(api.profile).mock.calls).toHaveLength(reads);lose=false;
  fireEvent.click(screen.getByText('Retry saved deletion'));
  await waitFor(()=>expect(localStorage.getItem(STORAGE_KEY)).toBe(''));
  expect(await db.table('records').count()).toBe(0);expect(foreign.getItem(STORAGE_KEY)).toBe('foreign preserved');
  expect(vi.mocked(api.deleteLearner).mock.calls[0]).toEqual(vi.mocked(api.deleteLearner).mock.calls[1]);
  cleanup();await db.delete();
});

const session = SessionSchema.parse(sample);
const ack = AttemptBatchResponseSchema.parse(acknowledgment).acknowledgments[0];
const page = TargetPageSchema.parse(targetPage);
function apiFixture(): LearnerApi {
  return { expose: vi.fn(async event => ({ eventId: event.eventId, status: "accepted" as const, serverSequence: 1 })), profile: vi.fn(async () => ({ apiVersion: "v2", revision: 1, setupCompleted: true, preferences: { locale: "en", timezone: "Europe/Berlin", level: "B1", sessionQuestionCount: 15 } })), saveProfile: vi.fn(async request => ({ apiVersion: "v2", revision: 2, setupCompleted: true, preferences: request.preferences })), catalog: vi.fn(async () => catalog as Awaited<ReturnType<LearnerApi["catalog"]>>), createFocusedSession: vi.fn(async () => ({ ...session, questions: [session.questions[0]] })), createSession: vi.fn(async () => session), submit: vi.fn(async input => ({ ...ack, attemptId: input.attemptId })),
    targets: vi.fn(async () => page), sync: vi.fn(async cursor => ({ apiVersion: "v2", changes: [], nextCursor: cursor, hasMore: false })) };
}
afterEach(() => { cleanup(); localStorage.clear(); window.history.replaceState(null, "", window.location.pathname); vi.unstubAllGlobals(); });
it('retains selected choices while saving and after feedback, then focuses the next prompt', async () => {
  const formats = SessionSchema.parse(formatSample);
  const api = apiFixture();
  api.createSession = vi.fn(async () => formats);
  let release!: (value: Awaited<ReturnType<LearnerApi['submit']>>) => void;
  api.submit = vi.fn(() => new Promise(resolve => { release = resolve; }));
  render(<LearnerJourney api={api} />);
  await waitFor(() => expect(screen.getByRole('button', {name:'Start short practice'})).toBeEnabled());
  fireEvent.click(screen.getByRole('button', {name:'Start short practice'}));
  const selected = await screen.findByRole('radio', {name:'den'});
  fireEvent.click(selected);
  fireEvent.click(screen.getByRole('button', {name:'Check answer'}));
  await waitFor(() => expect(api.submit).toHaveBeenCalledOnce());
  expect(selected).toBeChecked(); expect(selected).toBeDisabled();
  const attempt = vi.mocked(api.submit).mock.calls[0][0];
  release({attemptId: attempt.attemptId, status: 'accepted', serverSequence: 1,
    evaluation: {outcome: 'incorrect', policyVersion: 'test', explanation: {en:'Use the dative.',de:'Verwende den Dativ.'},
      acceptedAnswer: {type:'choice', optionId:'dem'}, assisted: false}});
  await screen.findByText('Use the dative.');
  expect(selected).toBeChecked(); expect(selected).toBeDisabled();
  expect(screen.queryByRole('button', {name:'Check answer'})).toBeNull();
  fireEvent.click(screen.getByRole('button', {name:'Continue', exact:true}));
  await waitFor(() => expect(screen.getByRole('heading', {name:formats.questions[1].exercise.prompt})).toHaveFocus());
});
describe('Home screen handoff', () => {
  it('keeps account controls behind Account and preserves a draft across navigation', async () => {
    const api = apiFixture();
    render(<LearnerJourney api={api} />);
    await waitFor(() => expect(screen.getByRole('button', {name:'Start short practice'})).toBeEnabled());
    expect(screen.queryByText('Sync saved work and sign out')).toBeNull();
    expect(screen.queryByLabelText('Theme')).toBeNull();
    fireEvent.click(screen.getByRole('button', {name:'Start short practice'}));
    fireEvent.change(await screen.findByLabelText('Your answer'), {target:{value:'preserved draft'}});
    expect(screen.queryByRole('navigation')).toBeNull();
    fireEvent.click(screen.getByRole('button', {name:'Close practice'}));
    fireEvent.click(screen.getByRole('button', {name:'Save and return Home'}));
    fireEvent.click(screen.getByRole('button', {name:'Account', exact:true}));
    expect(await screen.findByRole('heading', {name:'Account', exact:true})).toHaveFocus();
    expect(screen.getByLabelText('Theme')).toBeInTheDocument();
    expect(screen.getByText('Sync saved work and sign out')).not.toBeVisible();
    fireEvent.click(screen.getByText('Sign out'));
    expect(screen.getByText('Sync saved work and sign out')).toBeVisible();
    fireEvent.click(screen.getByRole('button', {name:'Home', exact:true}));
    fireEvent.click(screen.getByRole('button', {name:'Continue practice'}));
    expect(await screen.findByLabelText('Your answer')).toHaveValue('preserved draft');
    expect(api.createSession).toHaveBeenCalledTimes(1);
  });
  it('offers discovery without presenting new targets as confirmed evidence', async () => {
    const api = apiFixture();
    api.targets = vi.fn(async () => ({ ...page, targets: [] }));
    render(<LearnerJourney api={api} />);
    await screen.findByRole('heading', { name: 'Let’s find what to practise' });
    expect(screen.getByRole('button', { name: 'Start short practice' })).toBeEnabled();
    expect(screen.queryByText(/Needs practice: 0/)).toBeNull();
  });
  it('counts overlapping due and needs-practice targets once', async () => {
    const api = apiFixture();
    api.targets = vi.fn(async () => ({ ...page, targets: [
      { ...page.targets[0], isDue: true },
      { ...page.targets[0], targetId: '00000000-0000-4000-8000-000000000201', state: 'improving' as const, isDue: true },
    ] }));
    render(<LearnerJourney api={api} />);
    await screen.findByText('Needs practice: 1 · Retention checks: 1');
    expect(screen.getByRole('heading', { name: 'Practice what needs attention' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'Getting stronger' })).toBeInTheDocument();
  });
  it('offers voluntary practice and topic navigation when confirmed work is not due', async () => {
    const api = apiFixture();
    api.targets = vi.fn(async () => ({ ...page, targets: [{ ...page.targets[0], state: 'improving' as const, isDue: false }] }));
    render(<LearnerJourney api={api} />);
    await screen.findByRole('heading', { name: 'Nothing urgent right now' });
    await waitFor(() => expect(screen.getByRole('button', { name: 'Try something new' })).toBeEnabled());
    fireEvent.click(screen.getByRole('button', { name: 'Choose a topic' }));
    await screen.findByRole('heading', { name: 'Topics' });
    expect(api.createSession).not.toHaveBeenCalled();
    fireEvent.click(screen.getByRole('button', { name: 'Home', exact: true }));
    fireEvent.click(screen.getByRole('button', { name: 'Try something new' }));
    await screen.findByLabelText('Your answer');
    fireEvent.click(screen.getByRole('button', { name: 'Close practice' }));
    fireEvent.click(screen.getByRole('button', { name: 'Save and return Home' }));
    expect(screen.getByRole('button', { name: 'Continue practice' })).toBeEnabled();
    fireEvent.click(screen.getByRole('button', { name: 'Continue practice' }));
    await screen.findByLabelText('Your answer');
    expect(api.createSession).toHaveBeenCalledTimes(1);
  });
  it('does not claim no due work when the confirmed snapshot is unavailable', async () => {
    const api = apiFixture();
    api.targets = vi.fn(async () => { throw Error('offline'); });
    render(<LearnerJourney api={api} />);
    await screen.findByText('Your progress could not be loaded. You can still choose a topic or try again.');
    expect(screen.queryByText('Nothing urgent right now')).toBeNull();
    expect(screen.queryByText('Let’s find what to practise')).toBeNull();
    expect(screen.getByRole('button', { name: 'Refresh progress' })).toBeEnabled();
  });
});
it('sign-out choices require explicit removal and resume retained work only for the same local learner',async()=> {
  const api=apiFixture();const db=new WebReserve('signout-ui');
  const rendered=render(<LearnerJourney api={api} reserve={db}/>);
  fireEvent.click(screen.getByRole('button', {name:'Account', exact:true}));
  fireEvent.click(screen.getByText('Sign out'));
  await waitFor(()=>expect(screen.getByText('Sync saved work and sign out')).toBeEnabled());
  fireEvent.click(screen.getByText('Remove local work and sign out…'));
  fireEvent.click(screen.getByText('Keep my local work'));
  fireEvent.click(screen.getByText('Sync saved work and sign out'));
  await screen.findByText('Resume the same local fixture learner');
  expect(screen.queryByText('Start short practice')).toBeNull();
  rendered.unmount();const remount=render(<LearnerJourney api={api} reserve={db}/>);
  fireEvent.click(screen.getByText('Resume the same local fixture learner'));
  fireEvent.click(await screen.findByRole('button', {name:'Account', exact:true}));
  fireEvent.click(screen.getByText('Sign out'));
  await waitFor(()=>expect(screen.getByText('Remove local work and sign out…')).toBeEnabled());
  localStorage.setItem(PROFILE_PENDING_KEY,'explicitly removed pending work');
  fireEvent.click(screen.getByText('Remove local work and sign out…'));fireEvent.click(screen.getByText('Confirm local removal and sign out'));
  await screen.findByText('Resume the same local fixture learner');
  expect(localStorage.getItem(PROFILE_PENDING_KEY)).toBe('');expect(api.saveProfile).not.toHaveBeenCalled();
  remount.unmount();await db.delete();
});
it("Close preserves assisted drafts; partial completion retries the frozen request after restart", async () => {
  const api = apiFixture();
  api.complete = vi.fn(async (sessionId, request) => ({apiVersion:'v2',requestId:request.requestId,sessionId,mode:request.mode,plannedCount:5,gradedCount:0,skippedCount:0,correctCount:0,completedAt:'2026-10-05T10:00:00Z'}));
  vi.mocked(api.complete).mockRejectedValueOnce(Error('response lost'));
  const rendered = render(<LearnerJourney api={api} />);
  await waitFor(() => expect(screen.getByText('Start short practice')).toBeEnabled());
  fireEvent.click(screen.getByText('Start short practice'));
  fireEvent.change(await screen.findByLabelText('Your answer'), {target:{value:'saved draft'}});
  fireEvent.click(screen.getByText('Hint'));
  const before = readJourney(localStorage).practice!;
  fireEvent.click(screen.getByText('Close practice'));
  expect(await screen.findByText('Keep practising')).toBeVisible();
  fireEvent.click(screen.getByText('Keep practising'));
  expect(await screen.findByLabelText('Your answer')).toHaveValue('saved draft');
  expect(api.complete).not.toHaveBeenCalled();
  fireEvent.click(screen.getByText('Close practice')); fireEvent.click(screen.getByText('End session'));
  await screen.findByText('Try saving session again');
  const frozen = readJourney(localStorage).practice!.completion!;
  expect(frozen.mode).toBe('partial');
  expect(readJourney(localStorage).practice).toMatchObject({draft:before.draft,assisted:before.assisted});
  rendered.unmount(); render(<LearnerJourney api={api} />);
  await waitFor(() => expect(screen.getByText('Start short practice')).toBeEnabled());
  fireEvent.click(screen.getByText('Start short practice'));
  fireEvent.click(await screen.findByText('Try saving session again'));
  await screen.findByText('Session saved to your progress.');
  expect(api.complete).toHaveBeenLastCalledWith(session.id,frozen);
  expect(readJourney(localStorage).practice!.draft).toEqual(before.draft);
  expect(api.submit).not.toHaveBeenCalled(); expect(api.expose).not.toHaveBeenCalled();
});
it("keeps the saved-progress warning after local practice saves until a successful refresh", async () => {
  const api=apiFixture();
  const confirmed=await snapshot(api);
  saveJourney(localStorage,{...emptyJourney(),confirmed});
  vi.mocked(api.targets).mockRejectedValueOnce(Error("offline"));
  render(<LearnerJourney api={api} />);
  await screen.findByText("Your progress could not be updated. Previously saved results are still available, if any.");
  fireEvent.click(await screen.findByRole("button",{name:"Start short practice"}));
  fireEvent.change(await screen.findByLabelText("Your answer"),{target:{value:"locally saved draft"}});
  fireEvent.click(screen.getByRole("button",{name:"Close practice"}));
  fireEvent.click(screen.getByRole("button",{name:"Save and return Home"}));
  fireEvent.click(screen.getByRole("button",{name:"Progress"}));
  expect(screen.getByText("Showing your last saved progress. Try refreshing when online.")).toBeVisible();
  fireEvent.click(screen.getByRole("button",{name:"Refresh progress"}));
  await waitFor(()=>expect(screen.queryByText("Showing your last saved progress. Try refreshing when online.")).toBeNull());
});
it("keeps a failed refresh visible when background profile loading finishes later", async () => {
  const api = apiFixture();
  const profile = await api.profile();
  let finishProfile!: (value: typeof profile) => void;
  api.profile = vi.fn(() => new Promise(resolve => { finishProfile = resolve; }));
  vi.mocked(api.targets).mockRejectedValueOnce(Error("Unavailable snapshot"));
  render(<LearnerJourney api={api} />);
  const message = "Your progress could not be updated. Previously saved results are still available, if any.";
  await screen.findByText(message);
  finishProfile(profile);
  await waitFor(() => expect(screen.getByText("Start short practice")).toBeEnabled());
  expect(screen.getByText(message)).toBeVisible();
  fireEvent.click(screen.getByText("Refresh progress"));
  await waitFor(() => expect(readJourney(localStorage).confirmed).not.toBeNull());
  expect(screen.queryByText(message)).toBeNull();
});
it.each(['freeze','receipt'])("completion %s save failure preserves practice and retries explicitly", async failure => {
  const api = apiFixture();
  let fail = false;
  const storage = {getItem:(key:string)=>localStorage.getItem(key),setItem:(key:string,value:string)=>{if(fail) throw Error('disk full'); localStorage.setItem(key,value);}};
  api.complete = vi.fn(async (sessionId,request)=>{
    if(failure === 'receipt') fail = true;
    return {apiVersion:'v2',requestId:request.requestId,sessionId,mode:request.mode,plannedCount:5,gradedCount:0,skippedCount:0,correctCount:0,completedAt:'2026-10-05T10:00:00Z'};
  });
  render(<LearnerJourney api={api} storage={storage} />);
  await waitFor(()=>expect(screen.getByText('Start short practice')).toBeEnabled());
  fireEvent.click(screen.getByText('Start short practice')); await screen.findByLabelText('Your answer');
  fireEvent.click(screen.getByText('Close practice'));
  if(failure === 'freeze') fail = true;
  fireEvent.click(screen.getByText('End session'));
  await screen.findByText(/Your answer could not be saved on this device/);
  expect(api.complete).toHaveBeenCalledTimes(failure === 'freeze' ? 0 : 1);
  expect(readJourney(localStorage).practice!.completionReceipt).toBeUndefined();
  fail = false;
  if(failure === 'receipt') {
    const frozen = readJourney(localStorage).practice!.completion;
    api.complete = vi.fn(async (sessionId,request)=>({apiVersion:'v2',requestId:request.requestId,sessionId,mode:request.mode,plannedCount:5,gradedCount:0,skippedCount:0,correctCount:0,completedAt:'2026-10-05T10:00:00Z'}));
    fireEvent.click(screen.getByText('Try saving session again'));
    await screen.findByText('Session saved to your progress.');
    expect(api.complete).toHaveBeenCalledWith(session.id,frozen);
  }
});
it("Enter checks typed answers once, ignores composition and keeps feedback for explicit continuation", async () => {
  const api = apiFixture();
  render(<LearnerJourney api={api} />);
  await waitFor(() => expect(screen.getByText("Start short practice")).toBeEnabled());
  fireEvent.click(await screen.findByText("Start short practice"));
  const input = await screen.findByLabelText("Your answer");
  fireEvent.keyDown(input, { key: "Enter" });
  expect(api.submit).not.toHaveBeenCalled();
  fireEvent.change(input, { target: { value: "Berufe" } });
  fireEvent.keyDown(input, { key: "Enter", isComposing: true });
  fireEvent.keyDown(input, { key: "Enter", repeat: true });
  fireEvent.keyDown(input, { key: "Enter", keyCode: 229 });
  expect(api.submit).not.toHaveBeenCalled();
  fireEvent.keyDown(input, { key: "Enter" });
  fireEvent.keyDown(input, { key: "Enter" });
  await screen.findByText("Continue");
  expect(api.submit).toHaveBeenCalledTimes(1);
  expect(readJourney(localStorage).practice?.index).toBe(0);
  fireEvent.keyDown(input, { key: "Enter" });
  expect(api.submit).toHaveBeenCalledTimes(1);
  expect(readJourney(localStorage).practice?.index).toBe(0);
});
it("recognizes only the server sync reset response", async () => {
  const fetch = vi.fn().mockResolvedValue({ ok: false, status: 400, json: async () => ({ code: "invalid_cursor" }) });
  vi.stubGlobal("fetch", fetch);
  await expect(localLearnerApi().sync("old")).rejects.toBeInstanceOf(SyncCursorReset);
  await expect(localLearnerApi().targets("old")).rejects.not.toBeInstanceOf(SyncCursorReset);
  fetch.mockResolvedValue({ ok: false, status: 401, json: async () => ({ code: "invalid_cursor" }) });
  await expect(localLearnerApi().sync("old")).rejects.not.toBeInstanceOf(SyncCursorReset);
});

it.each(["session", "attempt", "exposure"])("atomically recovers reset cursors while retaining pending %s and profile work", async kind => {
  const api = apiFixture();
  const state = emptyJourney(); state.confirmed = await snapshot(api);
  const request = sessionRequest();
  // Reuse validated journey records from the existing serializer for each pending operation.
  const practiceSession = kind === "session" ? null : session;
  state.practice = { request, session: practiceSession, index: 0, draft: { type: "short_answer", text: "saved draft" }, assisted: true, pending: null, pendingExposure: null, evaluation: null, rejected: false, confirmedCount: 0, correctCount: 0, skippedCount: 0 };
  if (kind === "attempt") state.practice.pending = prepareAttempt(session, 0, { type: "short_answer", text: "saved draft" }, true, state.deviceId);
  if (kind === "exposure") state.practice.pendingExposure = { eventId: crypto.randomUUID(), sessionQuestionId: session.questions[0].id, exerciseRevision: session.questions[0].exercise.revision, deviceId: state.deviceId, disposition: "skip", occurredAt: "2026-10-04T12:00:00Z" };
  saveJourney(localStorage, state);
  localStorage.setItem(PROFILE_PENDING_KEY, "frozen profile payload");
  const saved = readJourney(localStorage);
  api.sync = vi.fn().mockRejectedValue(new SyncCursorReset());
  api.targets = vi.fn().mockResolvedValue({ ...page, targets: [], syncCursor: "fresh" });
  await pull(api, saved.confirmed!, confirmed => saveJourney(localStorage, { ...readJourney(localStorage), confirmed }));
  expect(readJourney(localStorage)).toEqual({ ...saved, confirmed: { targets: [], cursor: "fresh", generatedAt: page.generatedAt } });
  expect(localStorage.getItem(PROFILE_PENDING_KEY)).toBe("frozen profile payload");
  api.sync = vi.fn(async cursor => ({ apiVersion: "v2", changes: [], nextCursor: cursor, hasMore: false }));
  await pull(api, readJourney(localStorage).confirmed!, () => {});
  expect(api.sync).toHaveBeenCalledWith("fresh");
});

it("retains the old snapshot when reset pagination or its atomic save fails", async () => {
  const api = apiFixture(); const initial = await snapshot(api); const commit = vi.fn();
  api.sync = vi.fn().mockRejectedValue(new SyncCursorReset());
  api.targets = vi.fn().mockResolvedValueOnce({ ...page, nextPageCursor: "next" }).mockRejectedValueOnce(Error("offline"));
  await expect(pull(api, initial, commit)).rejects.toThrow("offline"); expect(commit).not.toHaveBeenCalled();
  api.targets = vi.fn().mockResolvedValue(page);
  await expect(pull(api, initial, () => { throw Error("disk full"); })).rejects.toThrow("disk full");
  expect(api.sync).toHaveBeenLastCalledWith(initial.cursor);
  await pull(api, initial, commit); expect(commit).toHaveBeenCalledTimes(1);
});

it("retains an applied delta when a later page resets and recovery fails", async () => {
  const api = apiFixture(); const initial = await snapshot(api);
  api.sync = vi.fn().mockResolvedValueOnce({ apiVersion: "v2", changes: [{ sequence: 2, operation: "upsert", target: { ...page.targets[0], lastSequence: 2, state: "learning" } }], nextCursor: "applied", hasMore: true }).mockRejectedValueOnce(new SyncCursorReset());
  api.targets = vi.fn().mockRejectedValue(Error("offline"));
  let saved = initial;
  await expect(pull(api, initial, value => { saved = value; })).rejects.toThrow("offline");
  expect(saved.cursor).toBe("applied"); expect(saved.targets[0].state).toBe("learning");
  api.sync = vi.fn().mockRejectedValue(new SyncCursorReset()); api.targets = vi.fn().mockResolvedValue({ ...page, syncCursor: "reset" });
  await pull(api, saved, value => { saved = value; });
  expect(api.sync).toHaveBeenCalledWith("applied"); expect(saved.cursor).toBe("reset");
});
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
  expect(await screen.findByRole("alert")).toHaveTextContent("Your answer could not be saved");
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
  expect(screen.getByRole("status")).toHaveTextContent("Questions answered: 1 / 5");
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
  await waitFor(() => expect(screen.getByText("Discard unfinished session")).toBeEnabled());
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
  fireEvent.change(screen.getByLabelText("Timezone"), { target: { value: "UTC" } });
  fireEvent.click(screen.getByText("Save preferences"));
  await screen.findByText(/No questions are available at your selected level/);
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
  fireEvent.click(screen.getByRole("button", { name: "Account", exact: true }));
  await waitFor(() => expect(screen.getByText("Practice preferences")).toBeEnabled());
  fireEvent.click(screen.getByText("Practice preferences"));
  fireEvent.change(screen.getByLabelText("Timezone"), { target: { value: "Asia/Tokyo" } });
  fireEvent.click(screen.getByText("Save preferences"));
  await screen.findByRole("alert");
  const request = JSON.parse(localStorage.getItem(PROFILE_PENDING_KEY)!);
  cleanup(); render(<LearnerJourney api={api} />);
  await screen.findByText("Retry");
  expect(screen.getByLabelText("Timezone")).toBeDisabled();
  fireEvent.click(screen.getByText("Retry"));
  await screen.findByRole("heading", { name: 'A little practice. Lasting progress.' });
  expect(api.saveProfile).toHaveBeenLastCalledWith(request);
  expect(readJourney(localStorage).practice).toEqual(saved.practice);
  expect(localStorage.getItem(PROFILE_PENDING_KEY)).toBe("");
});
it("blocks setup writes when local pending storage fails", async () => {
  const api = apiFixture(); api.profile = vi.fn(async () => ({ ...(await apiFixture().profile()), setupCompleted: false }));
  const storage = { getItem: () => null, setItem: (key: string) => { if (key === PROFILE_PENDING_KEY) throw Error("disk full"); } };
  render(<LearnerJourney api={api} storage={storage} />);
  fireEvent.click(await screen.findByText("Save preferences"));
  expect(await screen.findByRole("alert")).toHaveTextContent("Your preferences could not be saved");
  expect(api.saveProfile).not.toHaveBeenCalled();
});

it("reloads current preferences to correct an invalid frozen setup request", async () => {
  const api = apiFixture();
  const request = { apiVersion: "v2", requestId: crypto.randomUUID(), expectedRevision: 1,
    preferences: { locale: "en", timezone: "Invalid/Zone", level: "B1", sessionQuestionCount: 15 } };
  localStorage.setItem(PROFILE_PENDING_KEY, JSON.stringify(request));
  render(<LearnerJourney api={api} />);
  expect(await screen.findByText("Retry")).toBeInTheDocument();
  expect(screen.getByLabelText("Timezone")).toBeDisabled();
  fireEvent.click(screen.getByText("Reload current preferences"));
  await waitFor(() => expect(screen.getByLabelText("Timezone")).toBeEnabled());
  expect(screen.getByLabelText("Timezone")).toHaveValue("Europe/Berlin");
  expect(localStorage.getItem(PROFILE_PENDING_KEY)).toBe("");
  expect(api.saveProfile).not.toHaveBeenCalled();
});

it("keeps topic detail in the URL and restores navigation on browser history or remount", async () => {
  const api = apiFixture();
  const mounted=render(<LearnerJourney api={api} />);
  fireEvent.click(screen.getByRole("button", { name: "Topics" }));
  fireEvent.click(await screen.findByRole("button", { name: "German in everyday work" }));
  expect(window.location.hash).toBe(`#/learn/topic/${catalog.topics[0].id}`);
  fireEvent.click(screen.getByRole("button", { name: "Plural of Beruf" }));
  expect(window.location.hash).toBe(`#/learn/target/${catalog.targets[0].id}`);
  mounted.unmount();
  render(<LearnerJourney api={api} />);
  expect(await screen.findByRole("heading", { name: "Plural of Beruf" })).toBeInTheDocument();
  window.history.replaceState(null,"","#/learn/topics");
  fireEvent.popState(window);
  expect(await screen.findByRole("heading", { name: "Topics" })).toBeInTheDocument();
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
    expect(screen.getByRole("status")).toHaveTextContent("Questions answered: 1 / 1");
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
    fireEvent.click(screen.getByText("Close practice")); fireEvent.click(screen.getByText("Save and return Home"));
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
    expect(await screen.findByRole("alert")).toHaveTextContent("Topics could not be loaded");
    fireEvent.click(screen.getByRole("button", { name: "Reload topics" }));
    await screen.findByRole("button", { name: "German in everyday work" });
    fireEvent.click(screen.getByRole("button", { name: "Account", exact: true }));
    fireEvent.change(screen.getByLabelText("Interface language"), { target: { value: "de" } });
    fireEvent.click(screen.getByRole("button", { name: "Themen", exact: true }));
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
    expect(screen.getByRole("status")).toHaveTextContent("Questions answered: 5 / 5");
    expect(screen.getByRole("heading", { name: "Targets covered" })).toBeInTheDocument();
    expect(screen.getByText("Word order after weil")).toBeInTheDocument();
    await waitFor(() => expect(api.sync).toHaveBeenCalled());
    await waitFor(() => expect(screen.getByText("Discard unfinished session")).toBeEnabled());
    fireEvent.click(screen.getByText("Progress"));
    expect(screen.getByText("Plural of Beruf")).toBeInTheDocument();
    expect(screen.getByText("Needs practice", { selector: ".gm-state-badge" })).toBeInTheDocument();
    expect(screen.getByText("Mastered").closest(".gm-progress-stat")).toHaveTextContent("0Mastered");
    expect(screen.queryByText("Mastered", { selector: ".gm-state-badge" })).not.toBeInTheDocument();
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
    expect(screen.getByRole("alert")).toHaveTextContent("was not deleted");
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
    fireEvent.click(screen.getByText("Close practice")); fireEvent.click(screen.getByText("Save and return Home")); fireEvent.click(screen.getByText("Progress"));
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

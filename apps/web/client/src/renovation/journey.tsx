import { useEffect, useRef, useState } from "react";
import type { Answer, Catalog, PracticeFocus, LearnerProfile } from "@german-master/contracts";
import { ExerciseInput, FoundationButton, PracticeCard } from "../foundation/preview";
import { prepareAttempt, answerText, sessionRequest } from "../foundation/api";
import { learnerCopy } from "./locales";
import { localLearnerApi, type LearnerApi } from "./api";
import { browserStorage, emptyJourney, readJourney, saveJourney, snapshot, pull, readyAnswer, type Journey, type JourneyStorage } from "./storage";

import { PROFILE_PENDING_KEY, ProfileSetup } from "./setup";
import { FixtureOwner, OWNER_LOCK } from "./ownership";
import { ContentReport } from './report';

const localApi = localLearnerApi();
type JourneyProps = { api?: LearnerApi; storage?: JourneyStorage };
export default function LearnerJourney(props: JourneyProps) {
  const [owner, setOwner] = useState<FixtureOwner | null>(null);
  const [failed, setFailed] = useState(false);
  useEffect(() => {
    const controller = new AbortController();
    let lease: FixtureOwner | null = null;
    try {
    if (!navigator.locks) { setFailed(true); return; }
    void navigator.locks.request(OWNER_LOCK, { signal: controller.signal }, async () => {
      if (controller.signal.aborted) return;
      lease = new FixtureOwner();
      setOwner(lease);
      await lease.closed;
    }).catch(() => { if (!controller.signal.aborted) setFailed(true); });
    } catch { setFailed(true); }
    return () => { controller.abort(); lease?.close(); };
  }, []);
  if (owner) return <OwnedLearnerJourney {...props} owner={owner} />;
  const locale = (() => { try { return readJourney(props.storage ?? browserStorage).locale; } catch { return "en"; } })();
  return <main className="gm-foundation" lang={locale}><div className="gm-column"><PracticeCard>
    <h1>{learnerCopy[locale].ownershipTitle}</h1>
    <p role={failed ? "alert" : "status"}>{learnerCopy[locale][failed ? "ownershipUnavailable" : "ownershipWaiting"]}</p>
  </PracticeCard></div></main>;
}

export function OwnedLearnerJourney({ api = localApi, storage = browserStorage, owner }: JourneyProps & { owner?: FixtureOwner }) {
  const [loaded] = useState(() => { try { return { state: readJourney(storage), damaged: false }; } catch { return { state: emptyJourney(), damaged: true }; } });
  const [state, setState] = useState(loaded.state);
  const current = useRef(state);
  const [view, setView] = useState<"home" | "practice" | "progress" | "topics" | "topic" | "target">("home");
  const [profile, setProfile] = useState<LearnerProfile | null>(null);
  const [profileFailed, setProfileFailed] = useState(false);
  const [editingSetup, setEditingSetup] = useState(() => { try { return !!storage.getItem(PROFILE_PENDING_KEY); } catch { return true; } });
  const [catalog, setCatalog] = useState<Catalog | null>(null);
  const [catalogFailed, setCatalogFailed] = useState(false);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const lock = useRef(false);
  const [error, setError] = useState<"storageError" | "unavailable" | "connectionError" | null>(null);
  const heading = useRef<HTMLHeadingElement>(null);
  const feedback = useRef<HTMLDivElement>(null);
  const c = learnerCopy[state.locale];
  const p = state.practice;
  const exercise = p?.session?.questions[p.index]?.exercise;
  const setup = editingSetup || (!!profile && !profile.setupCompleted);
  const availableCount = catalog?.targets.filter(t => t.availableQuestionCount > 0).length ?? 0;
  const complete = !!p?.session && p.index === p.session.questions.length;
  const canPractice = !!p && !complete || !!profile?.setupCompleted && !setup && availableCount > 0;

  function commit(next: Journey) {
    try { saveJourney(storage, next); } catch { setError("storageError"); throw Error("Storage unavailable"); }
    current.current = next; setState(next); setError(null);
  }
  function editPractice(update: Partial<NonNullable<Journey["practice"]>>) {
    if (current.current.practice) commit({ ...current.current, practice: { ...current.current.practice, ...update } });
  }
  async function run(work: () => Promise<void>, fallback: "unavailable" | "connectionError") {
    if (lock.current || loaded.damaged) return;
    lock.current = true; setBusy(true); setError(null);
    try { await (owner ? owner.run(work) : work()); } catch (e) { if (!(e instanceof Error && e.message === "Storage unavailable")) setError(fallback); }
    finally { lock.current = false; setBusy(false); }
  }
  async function refresh() {
    await run(async () => {
      const confirmed = current.current.confirmed;
      if (confirmed) await pull(api, confirmed, value => commit({ ...current.current, confirmed: value }));
      const fresh = await snapshot(api);
      commit({ ...current.current, confirmed: fresh });
    }, "unavailable");
  }
  async function loadCatalog() {
    setCatalogFailed(false);
    try { setCatalog(await api.catalog()); } catch { setCatalogFailed(true); }
  }
  async function loadProfile() {
    setProfileFailed(false);
    try { const work = async () => { const value = await api.profile(); commit({ ...current.current, locale: value.preferences.locale }); setProfile(value); }; await (owner ? owner.run(work) : work()); }
    catch { setProfileFailed(true); }
  }
  useEffect(() => { void refresh(); void loadCatalog(); void loadProfile(); }, [api]);
  useEffect(() => { if (!setup || view === "practice") heading.current?.focus(); }, [view, selectedId, p?.index, p?.session?.id, setup]);
  useEffect(() => { if (p?.evaluation) feedback.current?.focus(); }, [p?.evaluation]);

  async function start(focus?: PracticeFocus) {
    if (!canPractice) return;
    setView("practice");
    await run(async () => {
      let practice = current.current.practice;
      if (!practice || (practice.session && practice.index === practice.session.questions.length)) {
        const base = { ...sessionRequest(), questionCount: Math.min(profile?.preferences.sessionQuestionCount ?? 15, availableCount) };
        const available = focus?.type === "target" ? catalog?.targets.find(t => t.id === focus.id)?.availableQuestionCount
          : focus ? catalog?.targets.filter(t => t.topicId === focus.id && t.availableQuestionCount > 0).length : availableCount;
        if (!available) throw Error("No compatible content");
        practice = { request: focus ? { ...base, questionCount: Math.min(profile?.preferences.sessionQuestionCount ?? 15, available), focus } : base, session: null, index: 0, draft: null, assisted: false,
          pending: null, pendingExposure: null, skippedCount: 0, evaluation: null, rejected: false, confirmedCount: 0, correctCount: 0 };
        commit({ ...current.current, practice });
      }
      if (!practice.session) {
        const session = "focus" in practice.request ? await api.createFocusedSession(practice.request) : await api.createSession(practice.request);
        editPractice({ session });
      }
    }, "connectionError");
  }
  async function submit() {
    await run(async () => {
      const practice = current.current.practice;
      if (!practice?.session || practice.rejected || practice.evaluation || practice.pendingExposure) return;
      const question = practice.session.questions[practice.index];
      const answer = readyAnswer(question.exercise, practice.draft as Answer | null);
      if (!answer && !practice.pending) return;
      const attempt = practice.pending ?? prepareAttempt(practice.session, practice.index, answer!, practice.assisted, current.current.deviceId);
      editPractice({ pending: attempt }); // Save before any network write. Retry never changes this payload.
      const result = await api.submit(attempt);
      if (result.status === "rejected") editPractice({ rejected: true });
      else editPractice({ evaluation: result.evaluation, confirmedCount: practice.confirmedCount + 1,
        correctCount: practice.correctCount + (result.evaluation.outcome === "correct" ? 1 : 0) });
    }, "connectionError");
  }
  async function skip() {
    let completed = false;
    await run(async () => {
      const practice = current.current.practice;
      if (!practice?.session || practice.pending || practice.evaluation || practice.rejected) return;
      const question = practice.session.questions[practice.index];
      if (!question) return;
      const event = practice.pendingExposure ?? { eventId: crypto.randomUUID(), sessionQuestionId: question.id,
        exerciseRevision: question.exercise.revision, deviceId: current.current.deviceId, disposition: "skip" as const,
        occurredAt: new Date().toISOString() };
      editPractice({ pendingExposure: event });
      const result = await api.expose(event);
      if (result.eventId !== event.eventId) throw Error("Exposure acknowledgment linkage mismatch");
      if (result.status === "rejected") { editPractice({ rejected: true }); return; }
      editPractice({ index: practice.index + 1, draft: null, assisted: false, pendingExposure: null,
        skippedCount: practice.skippedCount + 1 });
      completed = practice.index + 1 === practice.session.questions.length;
    }, "connectionError");
    if (completed) await refresh();
  }
  function next() {
    if (!p?.evaluation) return;
    try { editPractice({ index: p.index + 1, draft: null, assisted: false, pending: null, evaluation: null, rejected: false });
      if (p.session && p.index + 1 === p.session.questions.length) void refresh();
    } catch { /* Error already surfaced; leave acknowledged question in place. */ }
  }
  const confirmed = state.confirmed;
  const needs = confirmed?.targets.filter(t => t.state === "needs_practice").length ?? 0;
  const due = confirmed?.targets.filter(t => t.isDue && t.state !== "needs_practice").length ?? 0;
  const hasEvidence = confirmed?.targets.some(t => t.lastSequence > 0);
  function preference(update: Partial<Journey>) { try { commit({ ...current.current, ...update }); } catch { /* Preserve prior state. */ } }

  const selectedTarget = catalog?.targets.find(t => t.id === selectedId);
  const selectedTopic = catalog?.topics.find(t => t.id === selectedId);
  const targetState = confirmed?.targets.find(t => t.targetId === selectedId);
  function showTarget(id: string) { setSelectedId(id); setView("target"); }
  function focusAction(focus: PracticeFocus, count: number) {
    return <><p>{c.available}: {count}</p><p>{c.focusedShort}</p>
      {p && !complete ? <><p>{c.resumeFirst}</p><FoundationButton disabled={busy || !canPractice} onClick={() => void start()}>{c.resume}</FoundationButton></>
        : <FoundationButton disabled={busy || count === 0} onClick={() => void start(focus)}>{c.practise}</FoundationButton>}</>;
  }

  return <main className="gm-foundation" data-theme={state.theme} lang={state.locale}>
    <div className="gm-column">
      <header className="gm-header"><strong>German Master</strong><span>{c.subtitle}</span></header>
      {!loaded.damaged && <ContentReport question={view === 'practice' && !complete ? p?.session?.questions[p.index] : undefined} locale={state.locale} storage={storage} send={api.report ? request => owner ? owner.run(() => api.report!(request)) : api.report!(request) : undefined} />}
      {view !== "practice" && !setup && <div className="gm-settings">
        <label>{c.language}<select value={state.locale} onChange={e => preference({ locale: e.target.value as Journey["locale"] })}><option value="en">English</option><option value="de">Deutsch</option></select></label>
        <label>{c.theme}<select value={state.theme} onChange={e => preference({ theme: e.target.value as Journey["theme"] })}><option value="system">{c.system}</option><option value="light">{c.light}</option><option value="dark">{c.dark}</option></select></label>
      </div>}
      {view !== "practice" && <p className="gm-notice">{c.notice}</p>}
      {view !== "practice" && !setup && <nav className="gm-navigation" aria-label={c.subtitle}>
        <FoundationButton className="gm-secondary" aria-current={view === "home" ? "page" : undefined} onClick={() => setView("home")}>{c.home}</FoundationButton>
        <FoundationButton className="gm-secondary" aria-current={view === "progress" ? "page" : undefined} onClick={() => { setView("progress"); void refresh(); }}>{c.progress}</FoundationButton>
        <FoundationButton className="gm-secondary" aria-current={view === "topics" || view === "topic" ? "page" : undefined} onClick={() => setView("topics")}>{c.topics}</FoundationButton>
      </nav>}
      {view !== "practice" && !setup && <FoundationButton className="gm-secondary" disabled={busy || !profile} onClick={() => setEditingSetup(true)}>{c.editSetup}</FoundationButton>}
      {profileFailed && <><p role="alert">{c.setupError}</p><FoundationButton onClick={() => void loadProfile()}>{c.reloadProfile}</FoundationButton></>}
      {!profile && !profileFailed && <p role="status">{c.profileLoading}</p>}
      {setup && profile && view !== "practice" && !loaded.damaged && <ProfileSetup key={profile.revision} profile={profile} api={api} storage={storage}
        owner={owner}
        onPreviewLocale={locale => preference({ locale })}
        onSaved={value => { preference({ locale: value.preferences.locale }); setProfile(value); setEditingSetup(false); setView("home"); void loadCatalog(); }}
        onReload={async () => { const value = await api.profile(); setProfile(value); preference({ locale: value.preferences.locale }); return value; }}
        onCancel={profile.setupCompleted ? () => setEditingSetup(false) : undefined} />}
      {setup && view !== "practice" && p && !complete && <FoundationButton disabled={busy || !canPractice} onClick={() => void start()}>{c.resume}</FoundationButton>}
      {loaded.damaged ? <p role="alert">{c.damaged}</p> : <>
        {error && <p role="alert">{c[error]}</p>}
        {view === "home" && !setup && <PracticeCard>
          <h1 ref={heading} tabIndex={-1}>{hasEvidence ? c.priorities : c.welcome}</h1>
          {confirmed && <p>{c.needs}: {needs} · {c.retention}: {due}</p>}
          <p>{c.draftLevel}</p><p>{c.requested}: {profile?.preferences.sessionQuestionCount ?? 15} · {c.available}: {availableCount}</p>{availableCount === 0 && <p>{c.noContent}</p>}
          <FoundationButton disabled={busy || !canPractice} onClick={() => void start()}>{p && !complete ? c.resume : c.start}</FoundationButton>
          <FoundationButton className="gm-secondary" disabled={busy} onClick={() => void refresh()}>{c.refresh}</FoundationButton>
          {confirmed && <p className="gm-meta">{c.stale}</p>}
        </PracticeCard>}
        {view === "practice" && <>
          <FoundationButton className="gm-secondary" onClick={() => setView("home")}>{c.close}</FoundationButton>
          <PracticeCard>
            {!p?.session ? <><h1 ref={heading} tabIndex={-1}>{c.loading}</h1><FoundationButton disabled={busy || !canPractice} onClick={() => void start()}>{c.retry}</FoundationButton></> : complete ? <>
              <h1 ref={heading} tabIndex={-1}>{c.complete}</h1><p role="status">{c.summary}: {p.confirmedCount} / {p.session.questions.length}</p>
              <p>{c.skippedCount}: {p.skippedCount}</p><p>{c.correctCount}: {p.correctCount}</p><p>{c.retentionNote}</p>
              <h2>{c.coveredTargets}</h2>
              <ul>{p.session.questions.filter((question, index, questions) => questions.findIndex(q => q.exercise.targetId === question.exercise.targetId) === index).map(question =>
                <li key={question.exercise.targetId}>{catalog?.targets.find(t => t.id === question.exercise.targetId)?.title[state.locale] ?? question.exercise.prompt}</li>)}</ul>
              <FoundationButton onClick={() => { setView("progress"); void refresh(); }}>{c.progress}</FoundationButton>
            </> : exercise ? <>
              <p className="gm-meta">{c.question} {p.index + 1} {c.of} {p.session.questions.length}</p>
              {!("focus" in p.request) && p.session.questions.length < (profile?.preferences.sessionQuestionCount ?? p.request.questionCount) && <p>{c.shorterSession}: {p.session.questions.length}</p>}
              <h1 ref={heading} tabIndex={-1} lang="de">{exercise.prompt}</h1><p>{exercise.instruction[state.locale]}</p>
              <fieldset className="gm-answer-group" disabled={!!p.pending || !!p.pendingExposure || busy} onKeyDown={event => {
                if (event.key !== "Enter" || event.nativeEvent.isComposing || event.nativeEvent.keyCode === 229 || event.repeat || !(event.target instanceof HTMLInputElement) || event.target.type !== "text") return;
                event.preventDefault();
                if (!busy && !p.pending && !p.pendingExposure && !p.evaluation && !p.rejected && readyAnswer(exercise, p.draft as Answer | null)) void submit();
              }}>
                <ExerciseInput key={`${p.session.id}-${p.index}`} exercise={exercise} locale={state.locale} initialAnswer={p.draft as Answer | null} onAnswer={() => {}} onDraft={draft => { try { editPractice({ draft }); return true; } catch { return false; } }} />
                <FoundationButton className="gm-secondary" disabled={p.assisted} onClick={() => { try { editPractice({ assisted: true }); } catch { /* Reveal only after assistance has been saved. */ } }}>{c.hint}</FoundationButton>
                {p.assisted && <p>{exercise.hint[state.locale]}</p>}
              </fieldset>
              {!p.evaluation && !p.pendingExposure && <FoundationButton disabled={busy || p.rejected || (!p.pending && !readyAnswer(exercise, p.draft as Answer | null)) || error === "storageError"} onClick={() => void submit()}>{busy ? c.sending : p.pending ? c.retry : c.submit}</FoundationButton>}
              {!p.evaluation && !p.pending && <FoundationButton className="gm-secondary" disabled={busy || p.rejected || error === "storageError"} onClick={() => void skip()}>{p.pendingExposure ? c.retrySkip : c.skip}</FoundationButton>}
              {p.pendingExposure && <p role="status">{c.skipPending}</p>}
              {p.pending && !p.evaluation && <p role="status">{c.pending}</p>}
              {p.rejected && <p role="alert">{p.pendingExposure ? c.skipRejected : c.rejected} {c.discardNote}</p>}
              {p.evaluation && p.pending && <div className="gm-feedback" ref={feedback} tabIndex={-1}>
                <p role="status">{p.evaluation.outcome === "correct" ? c.correct : c.incorrect}{p.evaluation.assisted ? ` · ${c.assisted}` : ""}</p>
                <p>{c.yourAnswer}: <span lang="de">{answerText(p.pending.answer, p.session, p.index)}</span></p>
                <p>{c.acceptedAnswer}: <span lang="de">{answerText(p.evaluation.acceptedAnswer, p.session, p.index)}</span></p>
                <p>{p.evaluation.explanation[state.locale]}</p><FoundationButton onClick={next}>{c.next}</FoundationButton>
              </div>}
            </> : null}
          </PracticeCard>
        </>}
        {view === "progress" && !setup && <PracticeCard>
          <h1 ref={heading} tabIndex={-1}>{c.confirmed}</h1><p>{c.explanation}</p>
          <FoundationButton className="gm-secondary" disabled={busy} onClick={() => void refresh()}>{c.refresh}</FoundationButton>
          {!confirmed ? <p>{c.empty}</p> : <><p className="gm-meta">{c.stale}</p><ul className="gm-targets">{confirmed.targets.map(t => <li key={t.targetId}>
            <FoundationButton className="gm-secondary" onClick={() => showTarget(t.targetId)}>{catalog?.targets.find(m => m.id === t.targetId)?.title[state.locale] ?? c.unknown}</FoundationButton><p>{c.states[t.state]}</p>
            {t.schedule[0] && <p>{t.isDue ? c.due : c.later}: <time dateTime={t.schedule[0].dueAt}>{new Date(t.schedule[0].dueAt).toLocaleDateString(state.locale, { timeZone: profile?.preferences.timezone ?? "Europe/Berlin" })}</time></p>}
            <details><summary>{c.checks}: {t.qualifyingCheckCount}</summary><p>{c.retentionNote}</p></details>
          </li>)}</ul></>}
          <FoundationButton disabled={busy || !canPractice} onClick={() => void start()}>{p && !complete ? c.resume : c.start}</FoundationButton>
        </PracticeCard>}
        {(view === "topics" || view === "topic" || view === "target") && !setup && <PracticeCard>
          <h1 ref={heading} tabIndex={-1}>{view === "topics" ? c.topics : view === "topic" ? selectedTopic?.title[state.locale] ?? c.topicDetail : selectedTarget?.title[state.locale] ?? c.detail}</h1>
          {catalogFailed && <p role="alert">{c.catalogUnavailable}</p>}
          {(!catalog || catalogFailed) && <FoundationButton disabled={busy} onClick={() => void loadCatalog()}>{c.retryCatalog}</FoundationButton>}
          {view === "topics" && catalog && <ul className="gm-targets">{catalog.topics.map(topic => <li key={topic.id}>
            <FoundationButton className="gm-secondary" onClick={() => { setSelectedId(topic.id); setView("topic"); }}>{topic.title[state.locale]}</FoundationButton>
            <p>{c.available}: {catalog.targets.filter(t => t.topicId === topic.id && t.availableQuestionCount > 0).length}</p>
          </li>)}</ul>}
          {view === "topic" && selectedTopic && catalog && <>
            {focusAction({ type: "topic", id: selectedTopic.id }, catalog.targets.filter(t => t.topicId === selectedTopic.id && t.availableQuestionCount > 0).length)}
            <ul className="gm-targets">{catalog.targets.filter(t => t.topicId === selectedTopic.id).map(t => <li key={t.id}>
              <FoundationButton className="gm-secondary" onClick={() => showTarget(t.id)}>{t.title[state.locale]}</FoundationButton><p>{t.level} · {t.description[state.locale]}</p>
            </li>)}</ul>
          </>}
          {view === "target" && selectedTarget && <>
            <p>{selectedTarget.level} · {selectedTarget.description[state.locale]}</p>
            {targetState ? <><p>{c.states[targetState.state]}</p><p>{c.checks}: {targetState.qualifyingCheckCount}</p>
              {targetState.schedule[0] && <p>{targetState.isDue ? c.due : c.later}: <time dateTime={targetState.schedule[0].dueAt}>{new Date(targetState.schedule[0].dueAt).toLocaleDateString(state.locale, { timeZone: profile?.preferences.timezone ?? "Europe/Berlin" })}</time></p>}
              <p className="gm-meta">{c.stale}</p></> : <p>{c.empty}</p>}
            <p>{c.retentionNote}</p>
            {focusAction({ type: "target", id: selectedTarget.id }, selectedTarget.availableQuestionCount)}
          </>}
          {view !== "topics" && <FoundationButton className="gm-secondary" onClick={() => setView("topics")}>{c.back}</FoundationButton>}
        </PracticeCard>}
        {p && <details><summary>{c.saved}</summary><p>{c.discardNote}</p><FoundationButton className="gm-secondary" disabled={busy} onClick={() => { preference({ practice: null }); setView("home"); }}>{c.discard}</FoundationButton></details>}
      </>}
    </div>
  </main>;
}

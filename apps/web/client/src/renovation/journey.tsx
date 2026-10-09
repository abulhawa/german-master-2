import { useEffect, useMemo, useRef, useState, type ReactNode } from "react";
import type { Answer, Catalog, PracticeFocus, LearnerProfile } from "@german-master/contracts";
import { PracticeFeedback } from "../foundation/practice-feedback";
import { ExerciseInput } from "../foundation/exercise-input";
import { FoundationButton, PracticeCard } from "../foundation/controls";
import { prepareAttempt, answerText, sessionRequest } from "../foundation/api";
import { learnerCopy, accountLearnerCopy } from "./locales";
import { homeCopy } from "./home-locales";
import { shellCopy } from './shell-locales';
import { providerCopy } from './provider-locales';
import './workspace.css';
import { StudyArt } from './study-art';
import { AppMark } from './app-mark';
import { Home, ChartNoAxesColumn, BookOpen, UserRound, ArrowRight, RefreshCw } from 'lucide-react';
import { localLearnerApi, type LearnerApi } from "./api";
import { browserStorage, emptyJourney, readJourney, saveJourney, snapshot, pull, readyAnswer, type Journey, type JourneyStorage } from "./storage";

import { PROFILE_PENDING_KEY, ProfileSetup } from "./setup";
import { FixtureOwner } from "./ownership";
import { ContentReport } from './report';
import { OfflineDesk } from './offline-desk';
import { syncSavedWork } from './sync';
import { browserReserve } from './reserve';
import { fixtureAccount, type AccountBinding } from './account';
import type { WebReserve } from './reserve';
import { PrivacyExport } from './privacy';
import { LearnerDeletion, deletionGuard } from './deletion';
import { PrivacyDeletion, PrivacySignOut } from './privacy';
import { LearnerSignOut } from './signout';
import { REPORT_KEY } from './report';
import { STORAGE_KEY } from './storage';
import { LearnerIdentityDeletion, type IdentityDeletionTransport } from './identity-deletion';
import { IdentityDeletionControl } from './identity-deletion-control';
import type { IdentityDeletionProof } from '@german-master/contracts';

const localApi = localLearnerApi(fixtureAccount);
type WorkspaceView = "home" | "practice" | "progress" | "topics" | "topic" | "target" | "account";
function readWorkspaceRoute(): { view: WorkspaceView; selectedId: string | null } {
  const match = /^#\/learn\/(home|practice|progress|topics|topic|target|account)(?:\/([0-9a-f-]{36}))?$/.exec(window.location.hash);
  if (!match || ((match[1] === "topic" || match[1] === "target") && !match[2])) return { view: "home", selectedId: null };
  return { view: match[1] as WorkspaceView, selectedId: match[2] ?? null };
}
type JourneyProps = { api?: LearnerApi; storage?: JourneyStorage; account?: AccountBinding; reserve?: WebReserve; revoke?: () => Promise<void>; authorizeResume?: () => void; authenticated?: boolean; identityDeletion?:IdentityDeletionTransport; clearDeletedIdentity?:()=>Promise<void>; forgetDeletedIdentity?:()=>Promise<void>; onReauthenticate?:()=>void; localAccess?:boolean };
export default function LearnerJourney(props: JourneyProps) {
  const account = props.account ?? fixtureAccount;
  return <AccountLearnerJourney key={`${account.identity.subject}:${account.identity.generation}`} {...props} account={account} />;
}
function AccountLearnerJourney(props: JourneyProps & { account: AccountBinding }) {
  const [owner, setOwner] = useState<FixtureOwner | null>(null);
  const [failed, setFailed] = useState(false);
  useEffect(() => {
    const controller = new AbortController();
    let lease: FixtureOwner | null = null;
    try {
    if (!navigator.locks) { setFailed(true); return; }
    void navigator.locks.request(props.account.ownerLock, { signal: controller.signal }, async () => {
      if (controller.signal.aborted) return;
      lease = new FixtureOwner();
      setOwner(lease);
      await lease.closed;
    }).catch(() => { if (!controller.signal.aborted) setFailed(true); });
    } catch { setFailed(true); }
    return () => { controller.abort(); lease?.close(); };
  }, []);
  if (owner) return <OwnedLearnerJourney {...props} owner={owner} />;
  const locale = (() => { try { return readJourney(props.storage ?? props.account.storage(browserStorage)).locale; } catch { return "en"; } })();
  return <main className="gm-foundation" lang={locale}><div className="gm-column"><PracticeCard>
    <h1>{learnerCopy[locale].ownershipTitle}</h1>
    <p role={failed ? "alert" : "status"}>{learnerCopy[locale][failed ? "ownershipUnavailable" : "ownershipWaiting"]}</p>
  </PracticeCard></div></main>;
}

export function OwnedLearnerJourney({ api: suppliedApi, storage: suppliedStorage, account = fixtureAccount, reserve, owner, revoke, authorizeResume, identityDeletion, clearDeletedIdentity,forgetDeletedIdentity, onReauthenticate, localAccess }: JourneyProps & { owner?: FixtureOwner }) {
  const storage = useMemo(() => suppliedStorage ?? account.storage(browserStorage), [suppliedStorage, account]);
  const db = useMemo(() => reserve ?? (account === fixtureAccount ? browserReserve : account.reserve()), [reserve, account]);
  useEffect(()=>()=>{
    if(reserve||db===browserReserve) return;
    if(owner) void owner.closed.then(()=>db.close());else db.close();
  },[db,reserve,owner]);
  const rawApi = useMemo(() => suppliedApi ?? localLearnerApi(account), [suppliedApi,account]);
  const deletion = useMemo(() => new LearnerDeletion(account,storage), [account,storage]);
  const signout = useMemo(()=>new LearnerSignOut(account,storage),[account,storage]);
  const identityMarker=useMemo(()=>new LearnerIdentityDeletion(account,storage),[account,storage]);
  const barrier = useMemo(()=>({assertActive:()=>{account.assertCurrent();identityMarker.assertActive();deletion.assertActive();signout.assertActive();}}),[account,deletion,signout,identityMarker]);
  const api = useMemo(() => deletionGuard(rawApi,barrier), [rawApi,barrier]);
  const activeOwner = useMemo(() => owner ? new Proxy(owner,{get(target,key) {
    if(key === 'run') return <T,>(work:()=>Promise<T>)=>target.run(async()=>{barrier.assertActive();const result=await work();barrier.assertActive();return result;});
    const value=Reflect.get(target,key);return typeof value === 'function'?value.bind(target):value;
  }}) : undefined,[owner,barrier]);
  const activeStorage = useMemo(() => ({getItem:storage.getItem.bind(storage),setItem:(key:string,value:string)=> { barrier.assertActive(); storage.setItem(key,value); }}), [storage,barrier]);
  const [,refresh] = useState(0);
  let saved; let signedOut; let identitySaved=null; let damaged = false;
  try { saved = deletion.read();signedOut=signout.read();identitySaved=identityMarker.read(); } catch { damaged = true; }
  const locale = (() => { try { return readJourney(storage).locale; } catch { return 'en' as const; } })();
  async function cleanup() {
      // Only this subject's database and three owned keys; never clear the origin.
      await db.transaction('rw',db.tables,async()=> { for (const table of db.tables) await table.clear(); });
      for (const key of [STORAGE_KEY,PROFILE_PENDING_KEY,REPORT_KEY]) storage.setItem(key,'');
  }
  async function remove() {
    const work = () => {signout.assertActive();return deletion.deliver(rawApi,cleanup, () => refresh(v=>v+1));};
    try { await (owner ? owner.run(work) : work()); }
    finally { refresh(v=>v+1); }
  }
  async function leave(removeLocal:boolean) {
    const work=()=>{identityMarker.assertActive();deletion.assertActive();return signout.finish(removeLocal,()=>syncSavedWork(api,activeStorage,db),cleanup,()=>refresh(v=>v+1),revoke);};
    try { await(owner?owner.run(work):work()); } finally { refresh(v=>v+1); }
  }
  async function deleteIdentity(proof?:IdentityDeletionProof) {
    const work=()=>{signout.assertActive();deletion.assertActive();return identityMarker.deliver(identityDeletion!,async()=>{await cleanup();await clearDeletedIdentity!();},proof,()=>refresh(v=>v+1),forgetDeletedIdentity);};
    try {if(!identityDeletion||!clearDeletedIdentity)throw Error('Identity deletion not configured');await(owner?owner.run(work):work());}finally{refresh(v=>v+1);}
  }
  const identityControl=identityDeletion&&clearDeletedIdentity?(currentLocale:'en'|'de',blocked:boolean)=><IdentityDeletionControl locale={currentLocale} marker={identitySaved} blocked={blocked||damaged} deliver={deleteIdentity}/>:undefined;
  if(identitySaved) return <main className="gm-foundation" lang={locale}><div className="gm-column">{identityControl?.(locale,false)??<p role="alert">{locale==='de'?'Kontolöschung gespeichert. Üben und Synchronisieren sind gesperrt.':'Account deletion saved. Practice and sync are blocked.'}</p>}</div></main>;
  if (saved || damaged) return <main className="gm-foundation" lang={locale}><div className="gm-column"><PrivacyDeletion locale={locale} pending={!!saved} confirmed={!!saved?.receipt} complete={!!saved?.complete} blocked={damaged} remove={remove} /></div></main>;
  if(signedOut) return <main className="gm-foundation" lang={locale}><div className="gm-column"><PrivacySignOut authenticated={!!revoke} locale={locale} blocked={false} signedOut complete={signedOut.complete} leave={leave} resume={()=>{authorizeResume?.();signout.resume();refresh(v=>v+1);}} /></div></main>;
  return <ActiveLearnerJourney onReauthenticate={onReauthenticate} localAccess={localAccess} identityControl={identityControl} authenticated={!!revoke} api={api} storage={activeStorage} account={account} reserve={db} owner={activeOwner} onDelete={remove} onSignOut={leave} />;
}
function ActiveLearnerJourney({ api: suppliedApi, storage: suppliedStorage, account = fixtureAccount, reserve, owner, onDelete, onSignOut, authenticated=false,identityControl,onReauthenticate,localAccess }: JourneyProps & { owner?: FixtureOwner; onDelete: () => Promise<void>; onSignOut:(remove:boolean)=>Promise<void>; identityControl?:(locale:'en'|'de',blocked:boolean)=>ReactNode }) {
  const api = useMemo(() => suppliedApi ?? (account === fixtureAccount ? localApi : localLearnerApi(account)), [suppliedApi, account]);
  const storage = useMemo(() => suppliedStorage ?? account.storage(browserStorage), [suppliedStorage, account]);
  const db = useMemo(() => reserve ?? (account === fixtureAccount ? browserReserve : account.reserve()), [reserve, account]);
  useEffect(() => () => {
    if (reserve || db === browserReserve) return;
    if (owner) void owner.closed.then(() => db.close()); else db.close();
  }, [db, owner, reserve]);
  const [loaded] = useState(() => { try { return { state: readJourney(storage), damaged: false }; } catch { return { state: emptyJourney(), damaged: true }; } });
  const [state, setState] = useState(loaded.state);
  const current = useRef(state);
  const [view, setView] = useState<WorkspaceView>(() => readWorkspaceRoute().view);
  const [profile, setProfile] = useState<LearnerProfile | null>(null);
  const [profileFailed, setProfileFailed] = useState(false);
  const [editingSetup, setEditingSetup] = useState(() => { try { return !!storage.getItem(PROFILE_PENDING_KEY); } catch { return true; } });
  const [catalog, setCatalog] = useState<Catalog | null>(null);
  const [catalogFailed, setCatalogFailed] = useState(false);
  const [selectedId, setSelectedId] = useState<string | null>(() => readWorkspaceRoute().selectedId);
  const [busy, setBusy] = useState(false);
  const [closing, setClosing] = useState(false);
  const [syncRevision, setSyncRevision] = useState(0);
  const lock = useRef(false);
  const [error, setError] = useState<"storageError" | "unavailable" | "connectionError" | null>(null);
  const heading = useRef<HTMLHeadingElement>(null);
  const feedback = useRef<HTMLDivElement>(null);
  const c = {...learnerCopy[state.locale],...(authenticated?accountLearnerCopy[state.locale]:{})};
  const s = shellCopy[state.locale];
  const p = state.practice;
  const exercise = p?.session?.questions[p.index]?.exercise;
  const setup = editingSetup || (!!profile && !profile.setupCompleted);
  const availableCount = catalog?.targets.filter(t => t.availableQuestionCount > 0).length ?? 0;
  const complete = !!p?.session && (p.index === p.session.questions.length || !!p.completion);
  const canPractice = !!p && !complete || !!profile?.setupCompleted && !setup && availableCount > 0;

  function commit(next: Journey, clearError = true) {
    try { saveJourney(storage, next); } catch { setError("storageError"); throw Error("Storage unavailable"); }
    current.current = next; setState(next); if (clearError) setError(null);
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
    // This background read must not dismiss an error from concurrent progress refresh.
    try { const work = async () => { const value = await api.profile(); commit({ ...current.current, locale: value.preferences.locale }, false); setProfile(value); }; await (owner ? owner.run(work) : work()); }
    catch { setProfileFailed(true); }
  }
  async function syncAllOwned() {
    try { account.assertCurrent(); await syncSavedWork(api, storage, db); account.assertCurrent(); }
    finally { const saved = readJourney(storage); current.current = saved; setState(saved); setSyncRevision(v => v + 1); }
    const value = await api.profile(); setProfile(value); setEditingSetup(false); await loadCatalog();
    const confirmed = await snapshot(api); commit({ ...current.current, confirmed });
  }
  useEffect(() => { void refresh(); void loadCatalog(); void loadProfile(); }, [api]);
  useEffect(() => {
    const restore = () => { const route = readWorkspaceRoute(); setView(route.view); setSelectedId(route.selectedId); };
    window.addEventListener("popstate", restore); window.addEventListener("hashchange", restore);
    return () => { window.removeEventListener("popstate", restore); window.removeEventListener("hashchange", restore); };
  }, []);
  useEffect(() => { if (!setup || view === "practice") heading.current?.focus(); }, [view, selectedId, p?.index, p?.session?.id, setup]);
  useEffect(() => { if (p?.evaluation) feedback.current?.focus(); }, [p?.evaluation]);
  useEffect(() => { if (view === 'practice') heading.current?.focus(); }, [closing, p?.completion, view]);

  async function start(focus?: PracticeFocus) {
    if (!canPractice) return;
    navigate("practice");
    await run(async () => {
      let practice = current.current.practice;
      if (practice?.completion && !practice.completionReceipt) return;
      if (!practice || practice.completionReceipt) {
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
  async function finish() {
    await run(async () => {
      const practice = current.current.practice;
      if (!practice?.session || (practice.pending && !practice.evaluation) || practice.pendingExposure || practice.rejected) return;
      if (practice.completionReceipt) return;
      const request = practice.completion ?? {apiVersion:'v2' as const, requestId:crypto.randomUUID(),
        mode: practice.confirmedCount + practice.skippedCount === practice.session.questions.length ? 'full' as const : 'partial' as const};
      editPractice({completion:request});
      setClosing(false);
      if (!api.complete) throw Error('Completion unavailable');
      const receipt = await api.complete(practice.session.id, request);
      if (receipt.sessionId !== practice.session.id || receipt.requestId !== request.requestId || receipt.mode !== request.mode || receipt.plannedCount !== practice.session.questions.length
        || receipt.gradedCount + receipt.skippedCount > receipt.plannedCount || receipt.correctCount > receipt.gradedCount
        || request.mode === 'full' && receipt.gradedCount + receipt.skippedCount !== receipt.plannedCount) throw Error('Completion mismatch');
      editPractice({completionReceipt:receipt});
    }, 'connectionError');
  }
  async function submit() {
    await run(async () => {
      const practice = current.current.practice;
      if (!practice?.session || practice.completion || practice.rejected || practice.evaluation || practice.pendingExposure) return;
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
      if (!practice?.session || practice.completion || practice.pending || practice.evaluation || practice.rejected) return;
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
  const h = homeCopy[state.locale];
  const nothingDue = !!confirmed && !!hasEvidence && needs + due === 0;
  const improving = confirmed?.targets.filter(t => t.state === 'improving') ?? [];
  function preference(update: Partial<Journey>) { try { commit({ ...current.current, ...update }); } catch { /* Preserve prior state. */ } }
  function navigate(nextView: WorkspaceView, nextId?: string) {
    const id = nextId ?? selectedId;
    if (nextId !== undefined) setSelectedId(nextId);
    setView(nextView);
    const route = `#/learn/${nextView}${(nextView === "topic" || nextView === "target") && id ? `/${id}` : ""}`;
    if (window.location.hash !== route) window.history.pushState({ germanMaster: true }, "", route);
    // Keyboard activation leaves focus on the triggering button in real browsers.
    // Refocus after the click/default-action turn so the new page is announced.
    setTimeout(() => heading.current?.focus(), 0);
  }

  const selectedTarget = catalog?.targets.find(t => t.id === selectedId);
  const selectedTopic = catalog?.topics.find(t => t.id === selectedId);
  const targetState = confirmed?.targets.find(t => t.targetId === selectedId);
  function showTarget(id: string) { navigate("target", id); }
  function focusAction(focus: PracticeFocus, count: number) {
    return <><p>{c.available}: {count}</p><p>{c.focusedShort}</p>
      {p && !complete ? <><p>{c.resumeFirst}</p><FoundationButton disabled={busy || !canPractice} onClick={() => void start()}>{c.resume}</FoundationButton></>
        : <FoundationButton disabled={busy || count === 0} onClick={() => void start(focus)}>{c.practise}</FoundationButton>}</>;
  }

  return <main className={`gm-foundation gm-workspace ${view === 'practice' ? 'gm-focused' : ''}`} data-theme={state.theme} lang={state.locale}>
    {view !== 'practice' && <aside className="gm-rail">
      <div className="gm-wordmark"><AppMark/><strong>German Master<span>.</span></strong></div>
      <nav className="gm-rail-navigation" aria-label={c.subtitle}>
        {([{id:'home',label:c.home,icon:Home},{id:'progress',label:c.progress,icon:ChartNoAxesColumn},{id:'topics',label:c.topics,icon:BookOpen}] as const).map(item => <FoundationButton key={item.id} className="gm-nav-item gm-secondary" aria-current={view === item.id || item.id === 'topics' && (view === 'topic' || view === 'target') ? 'page' : undefined} onClick={()=>{setEditingSetup(false);navigate(item.id);if(item.id==='progress')void refresh();}}><item.icon aria-hidden="true"/>{item.label}</FoundationButton>)}
      </nav>
      <FoundationButton className="gm-nav-item gm-account-link gm-secondary" aria-current={view === 'account' ? 'page' : undefined} onClick={()=>{setEditingSetup(false);navigate('account');}}><UserRound aria-hidden="true"/>{s.account}</FoundationButton>
    </aside>}
    <div className="gm-column gm-workspace-content">
      {view === 'practice' ? <header className="gm-practice-header"><strong>German Master</strong>
        {p?.session && !complete && <span className="gm-meta">{c.question} {p.index + 1} {c.of} {p.session.questions.length}</span>}
        <FoundationButton className="gm-secondary" disabled={busy} onClick={() => { if (complete || !p?.session) navigate("home"); else setClosing(true); }}>{c.close}</FoundationButton>
      </header> : <header className="gm-workspace-header"><span>{view === 'home' ? s.homeLabel : s.workspace}</span>{confirmed && <span className="gm-status"><span className="gm-status-dot" aria-hidden="true"/>{s.confirmed}</span>}</header>}
      {view === 'account' && !setup && <>
        <div className="gm-page-heading"><h1 ref={heading} tabIndex={-1}>{s.account}</h1><p>{s.accountIntro}</p></div>
        <PracticeCard><h2>{s.appearance}</h2><div className="gm-settings">
          <label>{c.language}<select value={state.locale} onChange={e => preference({ locale: e.target.value as Journey['locale'] })}><option value="en">English</option><option value="de">Deutsch</option></select></label>
          <label>{c.theme}<select value={state.theme} onChange={e => preference({ theme: e.target.value as Journey['theme'] })}><option value="system">{c.system}</option><option value="light">{c.light}</option><option value="dark">{c.dark}</option></select></label>
        </div><FoundationButton className="gm-secondary" disabled={busy || !profile} onClick={() => setEditingSetup(true)}>{c.editSetup}</FoundationButton>
        {onReauthenticate && !localAccess && <FoundationButton className="gm-secondary" onClick={onReauthenticate}>{providerCopy[state.locale].reauth}</FoundationButton>}
        </PracticeCard>
      </>}
      {localAccess && onReauthenticate && <div className="gm-connection-note"><p role="status">{providerCopy[state.locale].local}</p><FoundationButton className="gm-secondary" onClick={onReauthenticate}>{providerCopy[state.locale].reauth}</FoundationButton></div>}
      {profileFailed && <><p role="alert">{c.setupError}</p><FoundationButton onClick={() => void loadProfile()}>{c.reloadProfile}</FoundationButton></>}
      {!profile && !profileFailed && <p role="status">{c.profileLoading}</p>}
      {setup && profile && view !== "practice" && !loaded.damaged && <ProfileSetup key={`${profile.revision}:${syncRevision}`} profile={profile} api={api} storage={storage}
        owner={owner} authenticated={authenticated}
        onPreviewLocale={locale => preference({ locale })}
        onSaved={value => { preference({ locale: value.preferences.locale }); setProfile(value); setEditingSetup(false); navigate("home"); void loadCatalog(); }}
        onReload={async () => { const value = await api.profile(); setProfile(value); preference({ locale: value.preferences.locale }); return value; }}
        onCancel={profile.setupCompleted ? () => setEditingSetup(false) : undefined} />}
      {setup && view !== "practice" && p && !complete && <FoundationButton disabled={busy || !canPractice} onClick={() => void start()}>{c.resume}</FoundationButton>}
      {loaded.damaged ? <p role="alert">{c.damaged}</p> : <>
        {error && <p role="alert">{c[error]}</p>}
        {view === "home" && !setup && <>
          <div className="gm-page-heading gm-home-heading"><p className="gm-eyebrow">{h.heading}</p><h1 ref={heading} tabIndex={-1}>{s.tagline}</h1></div>
          <div className="gm-session-hero"><div className="gm-session-copy">
            <p className="gm-kicker">{s.learn} · {profile?.preferences.level ?? 'B1'}</p>
            <h2>{!confirmed ? c.priorities : !hasEvidence ? h.discover : nothingDue ? h.quiet : h.attention}</h2>
            {!confirmed ? <p>{h.snapshotUnavailable}</p> : !hasEvidence ? <p>{h.discoverBody}</p> : nothingDue ? <p>{h.quietBody}</p> : <p>{c.needs}: {needs} · {c.retention}: {due}</p>}
            <FoundationButton className="gm-start-action" disabled={busy || !canPractice} onClick={()=>void start()}>{p && !complete ? c.resume : nothingDue ? c.fresh : c.start}<ArrowRight aria-hidden="true"/></FoundationButton>
            {catalogFailed && <><p role="alert">{c.catalogUnavailable}</p><FoundationButton className="gm-secondary" disabled={busy} onClick={()=>void loadCatalog()}>{c.retryCatalog}</FoundationButton></>}
            {!catalogFailed && catalog && availableCount === 0 && <div className="gm-empty-content"><p>{s.noQuestions}</p><FoundationButton className="gm-secondary" disabled={busy || !profile} onClick={()=>setEditingSetup(true)}>{c.editSetup}</FoundationButton></div>}
          </div><StudyArt/></div>
          {catalog && catalog.topics.length > 0 && <section className="gm-explore-section"><div className="gm-section-heading"><div><h2>{s.explore}</h2><p>{s.exploreNote}</p></div><FoundationButton className="gm-text-button gm-secondary" onClick={()=>navigate('topics')}>{nothingDue ? h.chooseTopic : s.browseTopics}<ArrowRight aria-hidden="true"/></FoundationButton></div>
            <div className="gm-topic-grid">{catalog.topics.map((topic,index)=><FoundationButton key={topic.id} className="gm-topic-tile gm-secondary" onClick={()=>{navigate('topic', topic.id);}}><span className="gm-tile-number">{String(index+1).padStart(2,'0')}</span><BookOpen aria-hidden="true"/><span className="gm-tile-title">{topic.title[state.locale]}</span><span className="gm-tile-note">{catalog.targets.filter(t=>t.topicId===topic.id).length} {s.targetCount}</span><ArrowRight className="gm-tile-arrow" aria-hidden="true"/></FoundationButton>)}</div>
          </section>}
        </>}
        {view === 'home' && !setup && confirmed && confirmed.targets.some(t => t.state === 'needs_practice' || t.isDue) && <PracticeCard>
          <h2>{s.nextFocus}</h2><p className="gm-meta">{s.nextFocusNote}</p>
          <ul className="gm-targets">{confirmed.targets.filter(t => t.state === 'needs_practice' || t.isDue).slice(0, 3).map(target => <li key={target.targetId}>
            <FoundationButton className="gm-secondary gm-focus-link" onClick={()=>showTarget(target.targetId)}><span>{catalog?.targets.find(t=>t.id===target.targetId)?.title[state.locale] ?? c.unknown}</span><ArrowRight aria-hidden="true"/></FoundationButton>
            <p>{target.state === 'needs_practice' ? c.needs : c.retention}</p>
          </li>)}</ul>
        </PracticeCard>}
        {view === 'home' && !setup && improving.length > 0 && <PracticeCard>
          <h2>{h.improving}</h2><p>{h.improvingNote}</p>
          <ul className="gm-targets">{improving.slice(0, 3).map(target => <li key={target.targetId}>
            <FoundationButton className="gm-secondary" onClick={() => showTarget(target.targetId)}>{catalog?.targets.find(t => t.id === target.targetId)?.title[state.locale] ?? c.unknown}</FoundationButton>
          </li>)}</ul>
        </PracticeCard>}
        {view === 'home' && !setup && <div className="gm-home-footer"><FoundationButton className="gm-text-button gm-secondary" disabled={busy} onClick={()=>void refresh()}><RefreshCw aria-hidden="true"/>{c.refresh}</FoundationButton></div>}
        {view === 'account' && <details className="gm-account-disclosure"><summary>{s.offlineTools}</summary><PracticeCard><p className="gm-notice">{c.notice}</p><FoundationButton disabled={busy} onClick={()=>void run(syncAllOwned,'connectionError')}>{c.syncAll}</FoundationButton></PracticeCard>
        { api.preparePack && <OfflineDesk db={db} refreshRevision={syncRevision} syncAll={syncAllOwned} api={api} locale={state.locale}
          deviceId={state.deviceId} questionCount={Math.min(profile?.preferences.sessionQuestionCount ?? 15, availableCount)}
          blocked={busy || !!p && !complete} owner={owner} catalog={catalog} onSynced={() => void refresh()} />}
</details>}
        {view === 'account' && <details className="gm-account-disclosure"><summary>{s.signOut}</summary><PrivacySignOut authenticated={authenticated} locale={state.locale} blocked={busy || loaded.damaged} signedOut={false} complete={false} leave={onSignOut} resume={()=>{}} /></details>}
         {view === 'account' && <details className="gm-account-disclosure"><summary>{s.accountTools}</summary>
        { api.deleteLearner && <PrivacyDeletion locale={state.locale} pending={false} confirmed={false} blocked={busy || loaded.damaged} remove={onDelete} />}
                { identityControl?.(state.locale,busy||loaded.damaged)}
        { api.exportLearner && <PrivacyExport locale={state.locale} blocked={busy || loaded.damaged} exportData={async sync => {
          if (lock.current) throw Error('Learner operation in progress');
          lock.current=true;setBusy(true);
          try {
            const work=async()=>{ account.assertCurrent(); if(sync) await syncAllOwned(); const data=await api.exportLearner!(); account.assertCurrent(); return data; };
            return await (owner ? owner.run(work) : work());
          } finally { lock.current=false;setBusy(false); }
        }} />}
</details>}
        {view === "practice" && <>
          {closing ? <PracticeCard><h1 ref={heading} tabIndex={-1}>{c.endQuestion}</h1><p>{c.endNote}</p>
            {((p?.pending && !p.evaluation) || p?.pendingExposure || p?.rejected) && <p role="status">{c.resolvePending}</p>}
            <FoundationButton disabled={busy || !!(p?.pending && !p.evaluation) || !!p?.pendingExposure || !!p?.rejected} onClick={() => void finish()}>{c.endSession}</FoundationButton>
            <FoundationButton className="gm-secondary" onClick={() => { setClosing(false); heading.current?.focus(); }}>{c.keepPractising}</FoundationButton>
            <FoundationButton className="gm-secondary" onClick={() => { setClosing(false); navigate("home"); }}>{c.saveAndLeave}</FoundationButton>
          </PracticeCard> : <PracticeCard>
            {!p?.session ? <><h1 ref={heading} tabIndex={-1}>{c.loading}</h1><FoundationButton disabled={busy || !canPractice} onClick={() => void start()}>{c.retry}</FoundationButton></> : complete ? <>
              <h1 ref={heading} tabIndex={-1}>{p.completion?.mode === 'partial' ? c.partialSummary : c.complete}</h1><p role="status">{c.summary}: {p.completionReceipt?.gradedCount ?? p.confirmedCount} / {p.session.questions.length}</p>
              <p>{p.completionReceipt ? c.completionConfirmed : c.completionPending}</p>
              {!p.completionReceipt && <FoundationButton disabled={busy} onClick={() => void finish()}>{p.completion ? c.retryCompletion : c.confirmCompletion}</FoundationButton>}
              <p>{c.skippedCount}: {p.completionReceipt?.skippedCount ?? p.skippedCount}</p><p>{c.correctCount}: {p.completionReceipt?.correctCount ?? p.correctCount}</p><p>{c.retentionNote}</p>
              <h2>{c.coveredTargets}</h2>
              <ul>{p.session.questions.slice(0, p.index + (p.evaluation ? 1 : 0)).filter((question, index, questions) => questions.findIndex(q => q.exercise.targetId === question.exercise.targetId) === index).map(question =>
                <li key={question.exercise.targetId}>{catalog?.targets.find(t => t.id === question.exercise.targetId)?.title[state.locale] ?? question.exercise.prompt}</li>)}</ul>
              <FoundationButton onClick={() => { navigate("progress"); void refresh(); }}>{c.progress}</FoundationButton>
            </> : exercise ? <>
              {catalog?.targets.find(t => t.id === exercise.targetId)?.title[state.locale] && <p className="gm-practice-target">{catalog.targets.find(t => t.id === exercise.targetId)!.title[state.locale]}</p>}
              {!("focus" in p.request) && p.session.questions.length < (profile?.preferences.sessionQuestionCount ?? p.request.questionCount) && <p>{c.shorterSession}: {p.session.questions.length}</p>}
              <p>{exercise.instruction[state.locale]}</p><h1 ref={heading} tabIndex={-1} lang="de">{exercise.prompt}</h1>
              <fieldset className="gm-answer-group" disabled={!!p.pending || !!p.pendingExposure || !!p.evaluation || p.rejected || busy} onKeyDown={event => {
                if (event.key !== "Enter" || event.nativeEvent.isComposing || event.nativeEvent.keyCode === 229 || event.repeat || !(event.target instanceof HTMLInputElement) || event.target.type !== "text") return;
                event.preventDefault();
                if (!busy && !p.pending && !p.pendingExposure && !p.evaluation && !p.rejected && readyAnswer(exercise, p.draft as Answer | null)) void submit();
              }}>
                <ExerciseInput key={`${p.session.id}-${p.index}`} exercise={exercise} locale={state.locale} initialAnswer={p.draft as Answer | null} onAnswer={() => {}} onDraft={draft => { try { editPractice({ draft }); return true; } catch { return false; } }} />
                {!p.evaluation && <FoundationButton className="gm-secondary" disabled={p.assisted} onClick={() => { try { editPractice({ assisted: true }); } catch { /* Reveal only after assistance has been saved. */ } }}>{c.hint}</FoundationButton>}
                {p.assisted && <p>{exercise.hint[state.locale]}</p>}
              </fieldset>
              {!p.evaluation && !p.pendingExposure && <FoundationButton disabled={busy || p.rejected || (!p.pending && !readyAnswer(exercise, p.draft as Answer | null)) || error === "storageError"} onClick={() => void submit()}>{busy ? c.sending : p.pending ? c.retry : c.submit}</FoundationButton>}
              {!p.evaluation && !p.pending && <FoundationButton className="gm-secondary" disabled={busy || p.rejected || error === "storageError"} onClick={() => void skip()}>{p.pendingExposure ? c.retrySkip : c.skip}</FoundationButton>}
              {p.pendingExposure && <p role="status">{c.skipPending}</p>}
              {p.pending && !p.evaluation && <p role="status">{c.pending}</p>}
              {p.rejected && <p role="alert">{p.pendingExposure ? c.skipRejected : c.rejected} {c.discardNote}</p>}
              {p.evaluation && p.pending && <PracticeFeedback regionRef={feedback} correct={p.evaluation.outcome === "correct"}
                outcome={`${p.evaluation.outcome === "correct" ? c.correct : c.incorrect}${p.evaluation.assisted ? ` · ${c.assisted}` : ""}`}
                answer={answerText(p.pending.answer, p.session, p.index)} accepted={answerText(p.evaluation.acceptedAnswer, p.session, p.index)}
                answerLabel={c.yourAnswer} acceptedLabel={c.acceptedAnswer} explanation={p.evaluation.explanation[state.locale]}
                nextLabel={c.next} onNext={next} disabled={busy} />}
            </> : null}
          </PracticeCard>}
        </>}
      {!loaded.damaged && <ContentReport key={syncRevision} owner={owner} question={view === 'practice' && !complete ? p?.session?.questions[p.index] : undefined} locale={state.locale} storage={storage} send={api.report} />}
        {view === "progress" && !setup && <>
          <div className="gm-page-heading"><h1 ref={heading} tabIndex={-1}>{c.confirmed}</h1><p>{c.explanation}</p></div>
          <PracticeCard>
          <FoundationButton className="gm-secondary" disabled={busy} onClick={() => void refresh()}>{c.refresh}</FoundationButton>
          {!confirmed ? <p>{c.empty}</p> : <><p className="gm-meta">{c.stale}</p>{confirmed.targets.length === 0 && <p>{s.emptyBody}</p>}<ul className="gm-targets">{confirmed.targets.map(t => <li key={t.targetId}>
            <FoundationButton className="gm-secondary" onClick={() => showTarget(t.targetId)}>{catalog?.targets.find(m => m.id === t.targetId)?.title[state.locale] ?? c.unknown}</FoundationButton><p className="gm-state-badge" data-state={t.state}>{c.states[t.state]}</p>
            {t.schedule[0] && <p>{t.isDue ? c.due : c.later}: <time dateTime={t.schedule[0].dueAt}>{new Date(t.schedule[0].dueAt).toLocaleDateString(state.locale, { timeZone: profile?.preferences.timezone ?? "Europe/Berlin" })}</time></p>}
            <details><summary>{c.checks}: {t.qualifyingCheckCount}</summary><p>{c.retentionNote}</p></details>
          </li>)}</ul></>}
          <FoundationButton disabled={busy || !canPractice} onClick={() => void start()}>{p && !complete ? c.resume : c.start}</FoundationButton>
        </PracticeCard></>}
        {(view === "topics" || view === "topic" || view === "target") && !setup && <>
          <div className="gm-page-heading"><h1 ref={heading} tabIndex={-1}>{view === "topics" ? c.topics : view === "topic" ? selectedTopic?.title[state.locale] ?? c.topicDetail : selectedTarget?.title[state.locale] ?? c.detail}</h1>{view === 'topics' && <p>{s.topicsIntro}</p>}</div>
          <PracticeCard>
          {catalogFailed && <p role="alert">{c.catalogUnavailable}</p>}
          {(!catalog || catalogFailed) && <FoundationButton disabled={busy} onClick={() => void loadCatalog()}>{c.retryCatalog}</FoundationButton>}
          {view === "topics" && catalog && <ul className="gm-targets gm-collection">{catalog.topics.map(topic => <li key={topic.id}>
            <FoundationButton className="gm-secondary" onClick={() => { navigate("topic", topic.id); }}>{topic.title[state.locale]}</FoundationButton>
            <p>{c.available}: {catalog.targets.filter(t => t.topicId === topic.id).reduce((sum,t) => sum + t.availableQuestionCount, 0)}</p>
          </li>)}</ul>}
          {view === "topic" && selectedTopic && catalog && <><p className="gm-topic-description">{s.topicDetailIntro}</p>
            {focusAction({ type: "topic", id: selectedTopic.id }, catalog.targets.filter(t => t.topicId === selectedTopic.id).reduce((sum,t) => sum + t.availableQuestionCount, 0))}
            <ul className="gm-targets gm-collection">{catalog.targets.filter(t => t.topicId === selectedTopic.id).map(t => <li key={t.id}>
              <FoundationButton className="gm-secondary" onClick={() => showTarget(t.id)}>{t.title[state.locale]}</FoundationButton><p>{t.level} · {t.description[state.locale]}</p>
            </li>)}</ul>
          </>}
          {view === "target" && selectedTarget && <><p className="gm-topic-description">{s.targetDetailIntro}</p>
            <p>{selectedTarget.level} · {selectedTarget.description[state.locale]}</p>
            {targetState ? <><p>{c.states[targetState.state]}</p><p>{c.checks}: {targetState.qualifyingCheckCount}</p>
              {targetState.schedule[0] && <p>{targetState.isDue ? c.due : c.later}: <time dateTime={targetState.schedule[0].dueAt}>{new Date(targetState.schedule[0].dueAt).toLocaleDateString(state.locale, { timeZone: profile?.preferences.timezone ?? "Europe/Berlin" })}</time></p>}
              <p className="gm-meta">{c.stale}</p></> : <p>{c.empty}</p>}
            <p>{c.retentionNote}</p>
            {focusAction({ type: "target", id: selectedTarget.id }, selectedTarget.availableQuestionCount)}
          </>}
          {view !== "topics" && <FoundationButton className="gm-secondary" onClick={() => view === "target" && selectedTarget ? navigate("topic",selectedTarget.topicId) : navigate("topics")}>{view === "target" ? s.backToTargets : c.back}</FoundationButton>}
        </PracticeCard></>}
        {p && <details><summary>{c.saved}</summary><p>{c.discardNote}</p><FoundationButton className="gm-secondary" disabled={busy || !!p.completion && !p.completionReceipt} onClick={() => { preference({ practice: null }); navigate("home"); }}>{c.discard}</FoundationButton></details>}
      </>}
    </div>
  </main>;
}

import { useEffect, useRef, useState } from 'react';
import { type Answer, type Catalog } from '@german-master/contracts';
import { PracticeFeedback } from "../foundation/practice-feedback";
import { ExerciseInput } from "../foundation/exercise-input";
import { FoundationButton, PracticeCard } from "../foundation/controls";
import { sessionRequest, answerText } from '../foundation/api';
import { browserReserve, type WebReserve } from './reserve';
import { OfflineRepository } from './offline';
import { readyAnswer } from './storage';
import { type LearnerApi } from './api';
import { type FixtureOwner } from './ownership';

const copy = {
  en: { title: 'Downloaded practice', download: 'Download two sessions', retry: 'Retry saved download', start: 'Start downloaded practice',
    available: 'Sessions available to start', pending: 'Download request saved. Retry when connected.',
    error: 'Could not complete this action. Saved work stays on this device. Retry explicitly when ready.',
    saved: 'Saved on this device. Feedback is provisional until synced.', submit: 'Check answer', next: 'Continue', hint: 'Hint', skip: 'Skip',
    sync: 'Sync saved work', back: 'Save and return', end: 'End session', keep: 'Keep practising', close: 'Close practice',
    endQuestion: 'End this session?', question: 'Question', correct: 'Correct', incorrect: 'Not quite',
    resume: 'Open saved session', summary: 'Session saved', graded: 'Answers saved', skipped: 'Skipped',
    confirmed: 'Server confirmed', correction: 'Server correction', empty: 'Download while connected to prepare practice for later.',
    note: 'Only confirmed results update Progress. Retention needs later independent checks.',
    syncing: 'Saving…', old: 'Download expired or consumed. Started sessions remain saved.',
    rejected: 'A saved event was not accepted. It remains on this device for review; further sync has stopped.',
    provisionalCorrect: 'Provisional correct', covered: 'Targets covered',
    locallyCorrect: 'Looks correct locally', locallyIncorrect: 'Not quite — checked locally', yourAnswer: 'Your answer', accepted: 'Accepted answer',
  },
  de: { title: 'Heruntergeladene Übungen', download: 'Zwei Sitzungen herunterladen', retry: 'Gespeicherten Download wiederholen', start: 'Heruntergeladene Übungen starten',
    available: 'Sitzungen zum Starten verfügbar', pending: 'Download-Anfrage gespeichert. Bei Verbindung erneut versuchen.',
    error: 'Die Aktion konnte nicht abgeschlossen werden. Gespeicherte Arbeit bleibt auf diesem Gerät. Bei Bedarf erneut versuchen.',
    saved: 'Auf diesem Gerät gespeichert. Rückmeldungen sind bis zur Synchronisierung vorläufig.', submit: 'Antwort prüfen', next: 'Weiter', hint: 'Hinweis', skip: 'Überspringen',
    sync: 'Gespeicherte Arbeit synchronisieren', back: 'Speichern und zurück', end: 'Sitzung beenden', keep: 'Weiterüben', close: 'Übungen schließen',
    endQuestion: 'Diese Sitzung beenden?', question: 'Aufgabe', correct: 'Richtig', incorrect: 'Noch nicht ganz',
    resume: 'Gespeicherte Sitzung öffnen', summary: 'Sitzung gespeichert', graded: 'Gespeicherte Antworten', skipped: 'Übersprungen',
    confirmed: 'Vom Server bestätigt', correction: 'Serverkorrektur', empty: 'Bei Verbindung herunterladen, um später zu üben.',
    note: 'Nur bestätigte Ergebnisse aktualisieren den Fortschritt. Behalten erfordert spätere unabhängige Prüfungen.',
    syncing: 'Wird gespeichert…', old: 'Download abgelaufen oder verbraucht. Gestartete Sitzungen bleiben gespeichert.',
    rejected: 'Ein gespeichertes Ereignis wurde nicht akzeptiert. Es bleibt zur Prüfung auf diesem Gerät. Die Synchronisierung wurde angehalten.',
    provisionalCorrect: 'Vorläufig richtig', covered: 'Behandelte Lernziele',
    locallyCorrect: 'Lokal richtig', locallyIncorrect: 'Noch nicht ganz — lokal geprüft', yourAnswer: 'Deine Antwort', accepted: 'Akzeptierte Antwort',
  },
};

export function OfflineDesk({ api, locale, deviceId, questionCount, blocked, owner, onSynced, catalog, db = browserReserve, syncAll, refreshRevision }: {
  api: LearnerApi; locale: 'en' | 'de'; deviceId: string; questionCount: number; blocked: boolean;
  owner?: FixtureOwner; onSynced?: () => void; catalog?: Catalog | null; db?: WebReserve;
  syncAll?: () => Promise<void>; refreshRevision?: number;
}) {
  const [repo] = useState(() => new OfflineRepository(db));
  const [reserve, setReserve] = useState<Awaited<ReturnType<WebReserve['status']>> | null>(null);
  const [ids, setIds] = useState<string[]>([]);
  const [active, setActive] = useState<Awaited<ReturnType<OfflineRepository['read']>> | null>(null);
  const [error, setError] = useState(false);
  const [busy, setBusy] = useState(false);
  const [savingDraft, setSavingDraft] = useState(0);
  const [closing, setClosing] = useState(false);
  const [inputKey, setInputKey] = useState(0);
  const working = useRef(false);
  const draftWrites = useRef(Promise.resolve());
  const heading = useRef<HTMLHeadingElement>(null);
  const feedbackRegion = useRef<HTMLDivElement>(null);
  const c = copy[locale];
  async function reload(id?: string) {
    setReserve(await db.status(new Date())); setIds(await repo.list());
    if (id) setActive(await repo.read(id));
  }
  useEffect(() => { let alive = true; void db.status(new Date()).then(async value => {
    const list = await repo.list(); if (alive) { setReserve(value); setIds(list); }
  }).catch(() => { if (alive) setError(true); }); return () => { alive = false; }; }, [db, repo]);
  useEffect(() => {
    if (refreshRevision === undefined) return;
    // Re-read receipts without replacing the active session or its provisional prompt.
    void reload(active?.practice.id).catch(() => setError(true));
  }, [refreshRevision]);
  useEffect(() => {
    if (active?.practice.feedback && !closing) feedbackRegion.current?.focus(); else heading.current?.focus();
  }, [active?.practice.id, active?.practice.index, active?.practice.feedback, closing]);
  async function run(work: () => Promise<void>) {
    if (working.current) return;
    working.current = true; setBusy(true); setError(false);
    try { await draftWrites.current; await (owner ? owner.run(work) : work()); }
    catch { setError(true); setInputKey(v => v + 1); try { await reload(active?.practice.id); } catch { /* Preserve unreadable storage. */ } }
    finally { working.current = false; setBusy(false); }
  }
  function draft(answer: Answer | null) {
    if (!active || working.current) return false;
    const id = active.practice.id;
    // Serialize rapid keystrokes. A failed save rolls the input back to committed state.
    setSavingDraft(v => v + 1);
    const save = async () => {
      try { await repo.draft(id, answer); setActive(await repo.read(id)); }
      catch { setError(true); setInputKey(v => v + 1); }
      finally { setSavingDraft(v => v - 1); }
    };
    draftWrites.current = draftWrites.current.then(() => owner ? owner.run(save) : save());
  }
  const practice = active?.practice;
  const question = active?.session.questions[practice?.index ?? 0];
  const feedback = practice?.feedback;
  const summary = !!practice && (practice.ended || !question);
  const pending = practice?.events.filter(e => !e.receipt || 'status' in e.receipt && e.receipt.status === 'rejected').length ?? 0;
  function check() {
    if (!practice || !question) return;
    void run(async () => {
      const saved = await repo.read(practice.id);
      const answer = readyAnswer(question.exercise, saved.practice.draft as Answer | null);
      if (!answer) return;
      await repo.answer(practice.id, answer); await reload(practice.id);
    });
  }
  return <PracticeCard>
    <h2 ref={heading} tabIndex={-1}>{summary ? c.summary : c.title}</h2>
    {error && <p role="alert">{c.error}</p>}
    {(busy || savingDraft > 0) && <p role="status">{c.syncing}</p>}
    {!reserve && <FoundationButton disabled={busy} onClick={() => void run(() => reload())}>{c.retry}</FoundationButton>}
    {!active ? <>
      <p>{c.available}: {reserve?.available ?? 0}</p>
      <p>{reserve?.reserve.pending ? c.pending : reserve?.reserve.pack && !reserve.available ? c.old : c.empty}</p>
      <FoundationButton disabled={busy || !reserve || !api.preparePack || questionCount < 1 && !reserve.reserve.pending}
        onClick={() => void run(async () => { await db.prepare(reserve?.reserve.pending ?? { ...sessionRequest(), questionCount }, request => api.preparePack!(request)); await reload(); })}>
        {reserve?.reserve.pending ? c.retry : c.download}
      </FoundationButton>
      <FoundationButton disabled={busy || blocked || !reserve?.available}
        onClick={() => void run(async () => { const id = await repo.start(deviceId, new Date()); await reload(id); })}>{c.start}</FoundationButton>
      {ids.map((id, i) => <FoundationButton key={id} disabled={busy}
        onClick={() => void run(async () => { await reload(id); })}>{c.resume} {i + 1}</FoundationButton>)}
    </> : <>
      {savingDraft === 0 && <p role="status">{c.saved}</p>}
      {practice!.events.some(e => e.receipt && 'status' in e.receipt && e.receipt.status === 'rejected') && <p role="alert">{c.rejected}</p>}
      {closing ? <><h3>{c.endQuestion}</h3>
        <FoundationButton disabled={busy} onClick={() => void run(async () => { await repo.end(practice!.id); setClosing(false); await reload(practice!.id); })}>{c.end}</FoundationButton>
        <FoundationButton disabled={busy} onClick={() => setClosing(false)}>{c.keep}</FoundationButton>
      </> : summary ? <>
        <p>{c.graded}: {practice!.events.filter(e => e.kind === 'attempt').length} · {c.skipped}: {practice!.events.filter(e => e.kind === 'skip').length}</p>
        <p>{c.provisionalCorrect}: {practice!.events.filter(e => e.kind === 'attempt' && e.provisional.outcome === 'correct').length}</p>
        <h3>{c.covered}</h3><ul>{active.session.questions.filter(q => practice!.events.some(e => e.kind !== 'completion' && e.request.sessionQuestionId === q.id))
          .filter((q, index, values) => values.findIndex(v => v.exercise.targetId === q.exercise.targetId) === index).map(q => <li key={q.exercise.targetId}>
            {catalog?.targets.find(t => t.id === q.exercise.targetId)?.title[locale] ?? q.exercise.prompt}
          </li>)}</ul>
        {practice!.events.map(e => e.kind === 'completion' && e.receipt ? <p key={e.request.requestId}>{c.confirmed}: {c.graded} {e.receipt.gradedCount} · {c.skipped} {e.receipt.skippedCount} · {c.correct} {e.receipt.correctCount}</p> : null)}
        <p>{c.note}</p>
        {!practice!.ended && <FoundationButton disabled={busy} onClick={() => void run(async () => { await repo.end(practice!.id); await reload(practice!.id); })}>{c.end}</FoundationButton>}
      </> : <>
        <header className="gm-practice-header"><strong>German Master</strong><span>{c.question} {practice!.index + 1} / {active.session.questions.length}</span>
          <FoundationButton className="gm-secondary" disabled={busy} onClick={() => setClosing(true)}>{c.close}</FoundationButton></header>
        <h3 lang="de">{question!.exercise.prompt}</h3>
        <p>{question!.exercise.instruction[locale]}</p>
        <fieldset className="gm-offline-answer gm-answer-group" disabled={busy || !!feedback} onKeyDown={event => {
          if (event.key !== 'Enter' || event.repeat || event.nativeEvent.isComposing || !(event.target instanceof HTMLInputElement)
            || event.target.type === 'radio' || !readyAnswer(question!.exercise, practice!.draft as Answer | null)) return;
          event.preventDefault(); check();
        }}>
          <ExerciseInput key={`${practice!.id}:${practice!.index}:${inputKey}`} exercise={question!.exercise} locale={locale}
            initialAnswer={practice!.draft as Answer | null} onAnswer={() => {}} onDraft={draft} />
          {!feedback && <FoundationButton disabled={!readyAnswer(question!.exercise, practice!.draft as Answer | null)} onClick={check}>{c.submit}</FoundationButton>}
          {!feedback && <FoundationButton onClick={() => void run(async () => { await repo.skip(practice!.id); await reload(practice!.id); })}>{c.skip}</FoundationButton>}
          {!feedback && question!.exercise.hint && <FoundationButton onClick={() => void run(async () => {
            const saved = await repo.read(practice!.id); await repo.draft(practice!.id, saved.practice.draft, true); await reload(practice!.id);
          })}>{c.hint}</FoundationButton>}
          {practice!.assisted && <p>{question!.exercise.hint?.[locale]}</p>}
        </fieldset>
        {feedback && <PracticeFeedback regionRef={feedbackRegion} correct={feedback.outcome === 'correct'}
          outcome={feedback.outcome === 'correct' ? c.locallyCorrect : c.locallyIncorrect}
          answer={answerText(practice!.draft as Answer, active.session, practice!.index)} accepted={answerText(feedback.acceptedAnswer, active.session, practice!.index)}
          answerLabel={c.yourAnswer} acceptedLabel={c.accepted}
          explanation={feedback.explanation[locale]} note={c.note} nextLabel={c.next} disabled={busy}
          onNext={() => void run(async () => { await repo.next(practice!.id); await reload(practice!.id); })} />}
      </>}
      <FoundationButton disabled={busy || pending === 0} onClick={() => void run(async () => {
        try { if (syncAll) await syncAll(); else { await repo.sync(practice!.id, api); onSynced?.(); } }
        finally { await reload(practice!.id); }
      })}>{c.sync} ({pending})</FoundationButton>
      {practice!.events.map((event, i) => event.kind === 'attempt' && event.receipt && event.receipt.status !== 'rejected'
        ? <p key={i}>{event.receipt.evaluation.outcome === event.provisional.outcome ? c.confirmed : c.correction}: {i + 1} —
          {event.receipt.evaluation.outcome === 'correct' ? c.correct : c.incorrect}. {event.receipt.evaluation.explanation[locale]}</p> : null)}
      <FoundationButton disabled={busy} onClick={() => void run(async () => { setActive(null); setClosing(false); await reload(); })}>{c.back}</FoundationButton>
    </>}
  </PracticeCard>;
}

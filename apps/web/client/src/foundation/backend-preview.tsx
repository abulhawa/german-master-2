import { useEffect, useRef, useState } from "react";
import type { Answer, Attempt, Evaluation, Session } from "@german-master/contracts";
import { ExerciseInput, FoundationButton, PracticeCard } from "./preview";
import { localFoundationApi, sessionRequest, prepareAttempt, answerText, type FoundationApi } from "./api";
import { backendCopy, type Locale } from "./locales";

const localApi = localFoundationApi();
export default function BackendPreview({ api = localApi }: { api?: FoundationApi }) {
  const [locale, setLocale] = useState<Locale>("en");
  const [theme, setTheme] = useState("system");
  const [session, setSession] = useState<Session | null>(null);
  const [index, setIndex] = useState(0);
  const [answer, setAnswer] = useState<Answer | null>(null);
  const [assisted, setAssisted] = useState(false);
  const [evaluation, setEvaluation] = useState<Evaluation | null>(null);
  const [pending, setPending] = useState<Attempt | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState(false);
  const [rejected, setRejected] = useState(false);
  const [request] = useState(sessionRequest);
  const [device] = useState(() => crypto.randomUUID());
  const heading = useRef<HTMLHeadingElement>(null);
  const feedback = useRef<HTMLDivElement>(null);
  const c = backendCopy[locale];

  async function load() {
    setBusy(true); setError(false);
    try { setSession(await api.createSession(request)); }
    catch { setError(true); }
    finally { setBusy(false); }
  }
  useEffect(() => { void load(); }, [api]);
  useEffect(() => { heading.current?.focus(); }, [index, session]);
  useEffect(() => { if (evaluation) feedback.current?.focus(); }, [evaluation]);

  async function submit() {
    if (!session || !answer || busy || rejected) return;
    const attempt = pending ?? prepareAttempt(session, index, answer, assisted, device);
    setPending(attempt); setBusy(true); setError(false);
    try {
      const result = await api.submit(attempt);
      if (result.status === "rejected") { setRejected(true); setError(true); }
      else setEvaluation(result.evaluation);
    } catch { setError(true); }
    finally { setBusy(false); }
  }

  const exercise = session?.questions[index]?.exercise;
  return <main className="gm-foundation" data-theme={theme} lang={locale}>
    <div className="gm-column">
      <header className="gm-header"><strong>German Master</strong><span>{c.subtitle}</span></header>
      <div className="gm-settings">
        <label>{c.language}<select value={locale} onChange={e => setLocale(e.target.value as Locale)}><option value="en">English</option><option value="de">Deutsch</option></select></label>
        <label>{c.theme}<select value={theme} onChange={e => setTheme(e.target.value)}><option value="system">{c.system}</option><option value="light">{c.light}</option><option value="dark">{c.dark}</option></select></label>
      </div>
      <p className="gm-notice">{c.notice}</p>
      <PracticeCard>
        {!session ? <><h1 ref={heading} tabIndex={-1}>{c.loading}</h1>{error && <p role="alert">{c.connectionError}</p>}<FoundationButton disabled={busy} onClick={() => void load()}>{c.retry}</FoundationButton></> :
          !exercise ? <><h1 ref={heading} tabIndex={-1}>{c.complete}</h1><p role="status">{c.summary}</p></> : <>
            <p className="gm-meta">{c.question} {index + 1} {c.of} {session.questions.length}</p>
            <h1 ref={heading} tabIndex={-1} lang="de">{exercise.prompt}</h1>
            <p>{exercise.instruction[locale]}</p>
            <fieldset className="gm-answer-group" disabled={!!pending || busy}>
              <ExerciseInput key={exercise.id} exercise={exercise} locale={locale} onAnswer={setAnswer} />
              <details key={`hint-${exercise.id}`} onToggle={e => { if (e.currentTarget.open) setAssisted(true); }}><summary>{c.hint}</summary><p>{exercise.hint[locale]}</p></details>
            </fieldset>
            {!evaluation && <FoundationButton disabled={!answer || busy || rejected} onClick={() => void submit()}>{busy ? c.sending : pending ? c.retry : c.submit}</FoundationButton>}
            {error && <p role="alert">{rejected ? c.rejected : c.connectionError}</p>}
            {evaluation && pending && <div className="gm-feedback" ref={feedback} tabIndex={-1}>
              <p role="status">{evaluation.outcome === "correct" ? c.correct : c.incorrect}{evaluation.assisted ? ` · ${c.assisted}` : ""}</p>
              <p>{c.yourAnswer}: <span lang="de">{answerText(pending.answer, session, index)}</span></p>
              <p>{c.acceptedAnswer}: <span lang="de">{answerText(evaluation.acceptedAnswer, session, index)}</span></p>
              <p>{evaluation.explanation[locale]}</p>
              <FoundationButton onClick={() => { setIndex(index + 1); setAnswer(null); setAssisted(false); setEvaluation(null); setPending(null); setError(false); }}>{c.next}</FoundationButton>
            </div>}
          </>}
      </PracticeCard>
    </div>
  </main>;
}

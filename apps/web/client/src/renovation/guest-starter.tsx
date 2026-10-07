import { useState } from "react";
import { Infinity as Loop } from 'lucide-react';
import './workspace.css';
import { StudyArt } from './study-art';
import { shellCopy } from './shell-locales';
import { z } from "zod";
import {
  AnswerSchema,
  EvaluationSchema,
  SessionSchema,
  type Answer,
  type Evaluation,
  type Exercise,
  type GuestAttachmentRequest,
} from "@german-master/contracts";
import { answerText } from "../foundation/api";
import { ExerciseInput, FoundationButton, PracticeCard } from "../foundation/preview";
import { readyAnswer } from "./storage";
import { grade, NORMALIZATION_VERSION, type Rubric } from "@german-master/learning-engine";

export const GUEST_STORAGE_KEY = "german-master-v2:guest-starter-v1";
export const GUEST_ATTACHED_KEY = "german-master-v2:guest-attached-v1";
export const GUEST_DEVICE_KEY = "german-master-v2:guest-device-v1";
type Locale = "en" | "de";
type AuthMode = "sign-in" | "register";

const starterSession = SessionSchema.parse({
  apiVersion: "v2",
  id: "30000000-0000-4000-8000-000000000001",
  contentReleaseId: "30000000-0000-4000-8000-000000000002",
  questions: [
    {
      id: "30000000-0000-4000-8000-000000000300",
      exercise: {
        type: "short_answer",
        schemaVersion: 1,
        id: "30000000-0000-4000-8000-000000000100",
        revision: 1,
        targetId: "30000000-0000-4000-8000-000000000200",
        prompt: "Wie lautet der Plural von „der Beruf“?",
        instruction: { en: "Type the plural.", de: "Schreiben Sie den Plural." },
        hint: { en: "Keep the noun capitalized.", de: "Schreiben Sie das Nomen groß." },
      },
    },
    {
      id: "30000000-0000-4000-8000-000000000301",
      exercise: {
        type: "choice",
        schemaVersion: 1,
        id: "30000000-0000-4000-8000-000000000101",
        revision: 1,
        targetId: "30000000-0000-4000-8000-000000000201",
        prompt: "Ich spreche mit ___ neuen Kollegen. (Ein Kollege.)",
        instruction: { en: "Choose the singular dative article.", de: "Wählen Sie den Artikel im Dativ Singular." },
        hint: { en: "„Mit“ takes the dative.", de: "„Mit“ verlangt den Dativ." },
        options: [{ id: "der", text: "der" }, { id: "den", text: "den" }, { id: "dem", text: "dem" }],
      },
    },
    {
      id: "30000000-0000-4000-8000-000000000302",
      exercise: {
        type: "cloze",
        schemaVersion: 1,
        id: "30000000-0000-4000-8000-000000000102",
        revision: 1,
        targetId: "30000000-0000-4000-8000-000000000202",
        prompt: "Ich fahre mit dem Bus ___ Büro.",
        instruction: { en: "Complete the preposition.", de: "Ergänzen Sie die Präposition." },
        hint: { en: "Think of the destination.", de: "Denken Sie an das Ziel." },
        slots: [{ id: "preposition", label: "Präposition" }],
      },
    },
    {
      id: "30000000-0000-4000-8000-000000000303",
      exercise: {
        type: "word_order",
        schemaVersion: 1,
        id: "30000000-0000-4000-8000-000000000103",
        revision: 1,
        targetId: "30000000-0000-4000-8000-000000000203",
        prompt: "Büro / weil / heute / ich / im / arbeite",
        instruction: { en: "Order the words. Put „heute“ immediately after „ich“.", de: "Ordnen Sie die Wörter. Setzen Sie „heute“ direkt nach „ich“." },
        hint: { en: "The conjugated verb comes last.", de: "Das konjugierte Verb steht am Ende." },
        tokens: [
          { id: "0", text: "Büro" }, { id: "1", text: "weil" }, { id: "2", text: "heute" },
          { id: "3", text: "ich" }, { id: "4", text: "im" }, { id: "5", text: "arbeite" },
        ],
      },
    },
    {
      id: "30000000-0000-4000-8000-000000000304",
      exercise: {
        type: "multi_slot",
        schemaVersion: 1,
        id: "30000000-0000-4000-8000-000000000104",
        revision: 1,
        targetId: "30000000-0000-4000-8000-000000000204",
        prompt: "Ergänzen Sie im Präsens: Du ___ früh. Ihr ___ früh.",
        instruction: { en: "Complete both forms of „arbeiten“.", de: "Ergänzen Sie beide Formen von „arbeiten“." },
        hint: { en: "Watch the endings after the stem „arbeit-“.", de: "Achten Sie auf die Endungen nach dem Stamm „arbeit-“." },
        slots: [{ id: "du", label: "du" }, { id: "ihr", label: "ihr" }],
      },
    },
  ],
});

const DraftSchema = z.union([
  AnswerSchema,
  z.strictObject({ type: z.literal("word_order"), tokenIds: z.array(z.string()) }),
]);
const GuestAttemptSchema = z.strictObject({
  attemptId: z.string().uuid(),
  questionId: z.string().uuid(),
  exerciseId: z.string().uuid(),
  exerciseRevision: z.number().int().min(1),
  answer: AnswerSchema,
  evaluation: EvaluationSchema,
  answeredAt: z.string(),
});
const GuestStateSchema = z.strictObject({
  version: z.literal(1),
  locale: z.enum(["en", "de"]),
  level: z.enum(["B1", "B2", "unsure"]),
  questionCount: z.union([z.literal(10), z.literal(15), z.literal(20)]),
  setupCompleted: z.boolean(),
  session: SessionSchema.nullable(),
  index: z.number().int().min(0),
  draft: DraftSchema.nullable(),
  assisted: z.boolean(),
  feedback: EvaluationSchema.nullable(),
  graded: z.number().int().min(0),
  skipped: z.number().int().min(0),
  correct: z.number().int().min(0),
  attempts: z.array(GuestAttemptSchema),
  savePromptDismissed: z.boolean(),
});
type GuestState = z.infer<typeof GuestStateSchema>;

function emptyGuestState(): GuestState {
  return {
    version: 1,
    locale: "en",
    level: "B1",
    questionCount: 15,
    setupCompleted: false,
    session: null,
    index: 0,
    draft: null,
    assisted: false,
    feedback: null,
    graded: 0,
    skipped: 0,
    correct: 0,
    attempts: [],
    savePromptDismissed: false,
  };
}

function loadGuestState(): { state: GuestState; damaged: boolean } {
  try {
    const raw = window.localStorage.getItem(GUEST_STORAGE_KEY);
    if (!raw) return { state: emptyGuestState(), damaged: false };
    const parsed = GuestStateSchema.safeParse(JSON.parse(raw));
    return parsed.success ? { state: parsed.data, damaged: false } : { state: emptyGuestState(), damaged: true };
  } catch {
    return { state: emptyGuestState(), damaged: true };
  }
}

export function guestAttemptCount(): number {
  try {
    const raw = window.localStorage.getItem(GUEST_STORAGE_KEY);
    if (!raw) return 0;
    const parsed = GuestStateSchema.safeParse(JSON.parse(raw));
    return parsed.success ? parsed.data.attempts.length : 0;
  } catch {
    return 0;
  }
}

function attachedGuestAttempts(): Record<string, string[]> {
  try {
    const raw=window.localStorage.getItem(GUEST_ATTACHED_KEY);
    if(!raw) return {};
    const value=JSON.parse(raw) as unknown;
    if(!value || typeof value!=="object" || Array.isArray(value)) return {};
    const result:Record<string,string[]>={};
    for(const [subject,ids] of Object.entries(value)) {
      if(!z.string().uuid().safeParse(subject).success || !Array.isArray(ids)) continue;
      const valid=ids.filter((id):id is string=>typeof id==="string"&&z.string().uuid().safeParse(id).success);
      result[subject]=[...new Set(valid)];
    }
    return result;
  } catch { return {}; }
}
function attachedGuestAttemptIds(subject?:string): Set<string> {
  const markers=attachedGuestAttempts();
  if(subject) return new Set(markers[subject]??[]);
  return new Set(Object.values(markers).flat());
}
function guestDeviceId(): string {
  const saved=window.localStorage.getItem(GUEST_DEVICE_KEY);
  if(saved&&z.string().uuid().safeParse(saved).success) return saved;
  const created=crypto.randomUUID();window.localStorage.setItem(GUEST_DEVICE_KEY,created);return created;
}
export function guestUnattachedAttemptCount(subject?:string): number {
  try {
    const raw=window.localStorage.getItem(GUEST_STORAGE_KEY);if(!raw)return 0;
    const parsed=GuestStateSchema.safeParse(JSON.parse(raw));if(!parsed.success)return 0;
    const attached=attachedGuestAttemptIds(subject);
    return parsed.data.attempts.filter(attempt=>!attached.has(attempt.attemptId)).length;
  } catch { return 0; }
}
export function buildGuestAttachmentRequest(subject:string): GuestAttachmentRequest|null {
  if(!z.string().uuid().safeParse(subject).success) throw Error("Invalid guest attachment subject");
  const raw=window.localStorage.getItem(GUEST_STORAGE_KEY);if(!raw)return null;
  const parsed=GuestStateSchema.safeParse(JSON.parse(raw));if(!parsed.success||!parsed.data.session)return null;
  const attached=attachedGuestAttemptIds(subject);
  const attempts=parsed.data.attempts.filter(attempt=>!attached.has(attempt.attemptId)).map((attempt,index)=>({
    attemptId:attempt.attemptId,contentReleaseId:parsed.data.session!.contentReleaseId,exerciseId:attempt.exerciseId,
    exerciseRevision:attempt.exerciseRevision,answer:attempt.answer,assistance:attempt.evaluation.assisted?["hint" as const]:[],
    answeredAt:attempt.answeredAt,clientSequence:index,
  }));
  if(!attempts.length)return null;
  return {apiVersion:"v2",requestId:crypto.randomUUID(),deviceId:guestDeviceId(),attempts};
}
export function markGuestAttemptsAttached(subject:string,attemptIds:string[]):void {
  if(!z.string().uuid().safeParse(subject).success) throw Error("Invalid guest attachment subject");
  const markers=attachedGuestAttempts(),next=new Set(markers[subject]??[]);
  for(const id of attemptIds)next.add(id);
  markers[subject]=[...next];
  window.localStorage.setItem(GUEST_ATTACHED_KEY,JSON.stringify(markers));
}

const starterRubrics = new Map<string, Rubric>([
  ["30000000-0000-4000-8000-000000000100@1", {
    normalizationVersion: NORMALIZATION_VERSION,
    acceptedAnswers: [{ type: "short_answer", text: "Berufe" }],
    explanation: { en: "The plural is „Berufe“; nouns retain capitalization.", de: "Der Plural lautet „Berufe“; Nomen werden großgeschrieben." },
  }],
  ["30000000-0000-4000-8000-000000000101@1", {
    normalizationVersion: NORMALIZATION_VERSION,
    acceptedAnswers: [{ type: "choice", optionId: "dem" }],
    explanation: { en: "„Mit“ takes the dative. For one colleague, use „dem neuen Kollegen“.", de: "„Mit“ verlangt den Dativ. Bei einem Kollegen heißt es „dem neuen Kollegen“." },
  }],
  ["30000000-0000-4000-8000-000000000102@1", {
    normalizationVersion: NORMALIZATION_VERSION,
    acceptedAnswers: [
      { type: "cloze", values: [{ slotId: "preposition", text: "ins" }] },
      { type: "cloze", values: [{ slotId: "preposition", text: "in das" }] },
    ],
    explanation: { en: "A destination with „das Büro“ uses „in das“, usually „ins“.", de: "Ein Ziel mit „das Büro“ verlangt „in das“, meist verkürzt zu „ins“." },
  }],
  ["30000000-0000-4000-8000-000000000103@1", {
    normalizationVersion: NORMALIZATION_VERSION,
    acceptedAnswers: [{ type: "word_order", tokenIds: ["1", "3", "2", "4", "0", "5"] }],
    explanation: { en: "After „weil“, the conjugated verb goes to the end.", de: "Nach „weil“ steht das konjugierte Verb am Ende." },
  }],
  ["30000000-0000-4000-8000-000000000104@1", {
    normalizationVersion: NORMALIZATION_VERSION,
    acceptedAnswers: [{ type: "multi_slot", values: [{ slotId: "du", text: "arbeitest" }, { slotId: "ihr", text: "arbeitet" }] }],
    explanation: { en: "„Arbeiten“ adds an e before -st and -t: arbeitest, arbeitet.", de: "Bei „arbeiten“ steht vor -st und -t ein e: arbeitest, arbeitet." },
  }],
]);

function localEvaluation(exercise: Exercise, answer: Answer, assisted: boolean): Evaluation {
  const rubric = starterRubrics.get(`${exercise.id}@${exercise.revision}`);
  if (!rubric) throw Error("Missing guest starter rubric");
  return grade(exercise, rubric, answer, assisted ? ["hint"] : []);
}

const copy = {
  en: {
    subtitle: "B1–B2 German practice",
    title: "Practise what needs attention.",
    intro: "Short mixed sessions help you find recurring mistakes and make the right form more reliable.",
    language: "Interface language",
    languageHelp: "You can change this later.",
    level: "Your current German level",
    unsure: "Not sure",
    levelHelp: "This only guides the starter mix. It is not a level test.",
    length: "Usual session",
    q10: "10 questions", q15: "15 questions", q20: "20 questions",
    lengthHelp: "About 10 minutes for 15 questions. No timer.",
    try: "Try German Master",
    existing: "I already have an account",
    resumeAccount: "Continue saved account",
    localStatus: "Guest practice · saved on this device",
    shorter: "Shorter session: 5 reviewed questions are available.",
    question: "Question", of: "of",
    hint: "Hint", check: "Check answer", skip: "Skip", next: "Continue",
    correct: "Looks correct locally", incorrect: "Not quite", assisted: "Assisted answer",
    yourAnswer: "Your answer", accepted: "Accepted answer",
    provisional: "This result is saved on this device. Sign in later to save eligible attempts to your account.",
    close: "Close practice", leaveTitle: "Leave this session?", leaveBody: "Your answers and draft stay on this device.",
    saveHome: "Save and return Home", keep: "Keep practising",
    homeTitle: "Continue practising", homeStatus: "Your guest practice is saved on this device.",
    continueSession: "Continue session", start: "Start practice",
    saveAcross: "Save across devices",
    saveAcrossBody: "Sign in when you want to attach eligible guest attempts and build confirmed progress.",
    create: "Create account", signIn: "Sign in",
    complete: "Starter session complete", saved: "Session saved",
    answered: "answered", skipped: "skipped", lookCorrect: "look correct locally",
    retention: "This is a starting point. Reliable progress comes from later unassisted checks.",
    keepProgress: "Keep your progress",
    keepProgressBody: "Create an account or sign in to save this practice and use confirmed progress across devices.",
    notNow: "Not now", finish: "Finish",
    setupAgain: "Change setup",
    storageError: "Guest practice could not be saved on this device. New answers are blocked until storage is available.",
    damaged: "Saved guest practice could not be read. It has been preserved and will not be overwritten.",
    reset: "Reset local guest practice",
    b2Note: "The current starter pack contains five reviewed B1 foundation questions.",
  },
  de: {
    subtitle: "Deutsch üben auf B1–B2",
    title: "Übe, was noch Aufmerksamkeit braucht.",
    intro: "Kurze gemischte Übungen helfen dir, wiederkehrende Fehler zu erkennen und die richtige Form sicherer zu machen.",
    language: "Sprache der Oberfläche",
    languageHelp: "Du kannst das später ändern.",
    level: "Dein aktuelles Deutschniveau",
    unsure: "Nicht sicher",
    levelHelp: "Das bestimmt nur die Auswahl der ersten Übungen. Es ist kein Einstufungstest.",
    length: "Übliche Übungslänge",
    q10: "10 Fragen", q15: "15 Fragen", q20: "20 Fragen",
    lengthHelp: "Etwa 10 Minuten für 15 Fragen. Ohne Zeitdruck.",
    try: "German Master ausprobieren",
    existing: "Ich habe bereits ein Konto",
    resumeAccount: "Gespeichertes Konto fortsetzen",
    localStatus: "Gastübung · auf diesem Gerät gespeichert",
    shorter: "Kürzere Übung: 5 geprüfte Fragen sind verfügbar.",
    question: "Frage", of: "von",
    hint: "Hinweis", check: "Antwort prüfen", skip: "Überspringen", next: "Weiter",
    correct: "Sieht lokal richtig aus", incorrect: "Noch nicht ganz", assisted: "Antwort mit Hilfe",
    yourAnswer: "Deine Antwort", accepted: "Akzeptierte Antwort",
    provisional: "Dieses Ergebnis ist auf diesem Gerät gespeichert. Melde dich später an, um geeignete Versuche in deinem Konto zu speichern.",
    close: "Übung schließen", leaveTitle: "Diese Übung verlassen?", leaveBody: "Deine Antworten und dein Entwurf bleiben auf diesem Gerät.",
    saveHome: "Speichern und zur Startseite", keep: "Weiter üben",
    homeTitle: "Weiter üben", homeStatus: "Deine Gastübungen sind auf diesem Gerät gespeichert.",
    continueSession: "Übung fortsetzen", start: "Übung starten",
    saveAcross: "Auf mehreren Geräten speichern",
    saveAcrossBody: "Melde dich an, wenn du geeignete Gastversuche übernehmen und bestätigten Fortschritt aufbauen möchtest.",
    create: "Konto erstellen", signIn: "Anmelden",
    complete: "Erste Übung abgeschlossen", saved: "Übung gespeichert",
    answered: "beantwortet", skipped: "übersprungen", lookCorrect: "sehen lokal richtig aus",
    retention: "Das ist ein Anfang. Verlässlicher Fortschritt zeigt sich bei späteren Übungen ohne Hilfe.",
    keepProgress: "Fortschritt behalten",
    keepProgressBody: "Erstelle ein Konto oder melde dich an, um diese Übungen zu speichern und bestätigten Fortschritt auf mehreren Geräten zu nutzen.",
    notNow: "Nicht jetzt", finish: "Abschließen",
    setupAgain: "Auswahl ändern",
    storageError: "Die Gastübung konnte auf diesem Gerät nicht gespeichert werden. Neue Antworten sind blockiert, bis Speicher verfügbar ist.",
    damaged: "Gespeicherte Gastübungen konnten nicht gelesen werden. Sie bleiben erhalten und werden nicht überschrieben.",
    reset: "Lokale Gastübungen zurücksetzen",
    b2Note: "Das aktuelle Startpaket enthält fünf geprüfte B1-Grundlagenfragen.",
  },
} as const;

export function GuestStarterJourney({
  onAuth,
  hasSavedAccount = false,
  onResumeSaved,
}: {
  onAuth: (mode: AuthMode) => void;
  hasSavedAccount?: boolean;
  onResumeSaved?: () => void;
}) {
  const [loaded] = useState(loadGuestState);
  const [state, setState] = useState(loaded.state);
  const [damaged, setDamaged] = useState(loaded.damaged);
  const [storageError, setStorageError] = useState(false);
  const [view, setView] = useState<"home" | "practice">("home");
  const [closing, setClosing] = useState(false);
  const c = copy[state.locale];
  const unattachedCount = guestUnattachedAttemptCount();
  const complete = !!state.session && state.index >= state.session.questions.length;
  const active = !!state.session && !complete;
  const question = active ? state.session!.questions[state.index] : null;

  function commit(next: GuestState) {
    if (damaged) return false;
    try {
      const parsed = GuestStateSchema.parse(next);
      window.localStorage.setItem(GUEST_STORAGE_KEY, JSON.stringify(parsed));
      setState(parsed);
      setStorageError(false);
      return true;
    } catch {
      setStorageError(true);
      return false;
    }
  }

  function resetDamaged() {
    const fresh = emptyGuestState();
    try {
      window.localStorage.setItem(GUEST_STORAGE_KEY, JSON.stringify(fresh));
      setState(fresh);
      setDamaged(false);
      setStorageError(false);
      setView("home");
    } catch {
      setStorageError(true);
    }
  }

  function begin() {
    if (damaged) return;
    const next = {
      ...state,
      setupCompleted: true,
      session: starterSession,
      index: 0,
      draft: null,
      assisted: false,
      feedback: null,
      graded: 0,
      skipped: 0,
      correct: 0,
      savePromptDismissed: false,
    };
    if (commit(next)) setView("practice");
  }

  function check() {
    if (!question || state.feedback || storageError) return;
    const answer = readyAnswer(question.exercise, state.draft as Answer | null);
    if (!answer) return;
    const evaluation = localEvaluation(question.exercise, answer, state.assisted);
    const attempt = {
      attemptId: crypto.randomUUID(),
      questionId: question.id,
      exerciseId: question.exercise.id,
      exerciseRevision: question.exercise.revision,
      answer,
      evaluation,
      answeredAt: new Date().toISOString(),
    };
    commit({
      ...state,
      feedback: evaluation,
      graded: state.graded + 1,
      correct: state.correct + (evaluation.outcome === "correct" ? 1 : 0),
      attempts: [...state.attempts, attempt],
    });
  }

  function next() {
    if (!state.feedback) return;
    commit({ ...state, index: state.index + 1, draft: null, assisted: false, feedback: null });
  }

  function skip() {
    if (!question || state.feedback || storageError) return;
    commit({ ...state, index: state.index + 1, draft: null, assisted: false, skipped: state.skipped + 1 });
  }

  if (damaged) return <main className="gm-foundation" lang={state.locale}><div className="gm-column"><PracticeCard>
    <h1>German Master</h1><p role="alert">{c.damaged}</p>
    <FoundationButton onClick={resetDamaged}>{c.reset}</FoundationButton>
  </PracticeCard></div></main>;

  if (!state.setupCompleted) return <main className="gm-foundation gm-welcome" lang={state.locale}><div className="gm-column">
    <header className="gm-header"><div className="gm-wordmark"><Loop aria-hidden="true"/><strong>German Master<span>.</span></strong></div>
      <label className="gm-language-switch">{c.language}<select aria-label={c.language} value={state.locale} onChange={e=>commit({...state,locale:e.target.value as Locale})}><option value="en">English</option><option value="de">Deutsch</option></select></label>
    </header>
    <div className="gm-welcome-layout"><div className="gm-welcome-copy">
      <p className="gm-kicker">{shellCopy[state.locale].guestLabel}</p>
      <h1>{c.title}</h1><p className="gm-welcome-intro">{c.intro}</p>
      {storageError && <p role="alert">{c.storageError}</p>}
      <FoundationButton className="gm-welcome-start" disabled={storageError} onClick={begin}>{c.try}<span aria-hidden="true">↗</span></FoundationButton>
      <p className="gm-meta">{shellCopy[state.locale].guestNote}</p>
      <FoundationButton className="gm-text-button gm-secondary" onClick={()=>onAuth('sign-in')}>{c.existing}</FoundationButton>
      {hasSavedAccount && onResumeSaved && <FoundationButton className="gm-secondary" onClick={onResumeSaved}>{c.resumeAccount}</FoundationButton>}
    </div><div className="gm-welcome-visual"><StudyArt/><div className="gm-visual-caption"><span>01 / Deutsch</span><p>{shellCopy[state.locale].session}</p></div></div></div>
  </div></main>;

  if (view === "home") return <main className="gm-foundation gm-welcome" lang={state.locale}><div className="gm-column">
    <header className="gm-header"><div className="gm-wordmark"><Loop aria-hidden="true"/><strong>German Master</strong></div><span>{c.subtitle}</span></header>
    <PracticeCard>
      <h1>{c.homeTitle}</h1><p role="status">{c.homeStatus}</p>
      {storageError && <p role="alert">{c.storageError}</p>}
      <FoundationButton disabled={storageError} onClick={() => active ? setView("practice") : begin()}>{active ? c.continueSession : c.start}</FoundationButton>
      <FoundationButton className="gm-secondary" disabled={active} onClick={() => commit({ ...state, setupCompleted: false, session: null, index: 0, draft: null, assisted: false, feedback: null })}>{c.setupAgain}</FoundationButton>
    </PracticeCard>
    {unattachedCount > 0 && <PracticeCard><h2>{c.saveAcross}</h2><p>{c.saveAcrossBody}</p>
      <FoundationButton onClick={() => onAuth("register")}>{c.create}</FoundationButton>
      <FoundationButton className="gm-secondary" onClick={() => onAuth("sign-in")}>{c.signIn}</FoundationButton>
    </PracticeCard>}
    {hasSavedAccount && onResumeSaved && <FoundationButton className="gm-secondary" onClick={onResumeSaved}>{c.resumeAccount}</FoundationButton>}
  </div></main>;

  return <main className="gm-foundation gm-workspace gm-focused" lang={state.locale}><div className="gm-column gm-workspace-content">
    <header className="gm-header"><strong>German Master</strong><span>{c.subtitle}</span></header>
    <p role="status" className="gm-notice">{c.localStatus}</p>
    <FoundationButton className="gm-secondary" onClick={() => complete ? setView("home") : setClosing(true)}>{c.close}</FoundationButton>
    {storageError && <p role="alert">{c.storageError}</p>}
    {closing ? <PracticeCard><h1>{c.leaveTitle}</h1><p>{c.leaveBody}</p>
      <FoundationButton onClick={() => { setClosing(false); setView("home"); }}>{c.saveHome}</FoundationButton>
      <FoundationButton className="gm-secondary" onClick={() => setClosing(false)}>{c.keep}</FoundationButton>
    </PracticeCard> : complete ? <PracticeCard>
      <h1>{state.index >= starterSession.questions.length ? c.complete : c.saved}</h1>
      <p role="status">{state.graded} {c.answered} · {state.skipped} {c.skipped}</p>
      <p>{state.correct} {c.lookCorrect}</p><p>{c.retention}</p>
      {unattachedCount > 0 && !state.savePromptDismissed && <div className="gm-feedback">
        <h2>{c.keepProgress}</h2><p>{c.keepProgressBody}</p>
        <FoundationButton onClick={() => onAuth("register")}>{c.create}</FoundationButton>
        <FoundationButton className="gm-secondary" onClick={() => onAuth("sign-in")}>{c.signIn}</FoundationButton>
        <FoundationButton className="gm-secondary" onClick={() => commit({ ...state, savePromptDismissed: true })}>{c.notNow}</FoundationButton>
      </div>}
      <FoundationButton className="gm-secondary" onClick={() => setView("home")}>{c.finish}</FoundationButton>
    </PracticeCard> : question ? <PracticeCard>
      <p className="gm-meta">{c.question} {state.index + 1} {c.of} {state.session!.questions.length}</p>
      {state.session!.questions.length < state.questionCount && <p className="gm-notice">{c.shorter}</p>}
      <h1 lang="de">{question.exercise.prompt}</h1><p>{question.exercise.instruction[state.locale]}</p>
      <fieldset className="gm-answer-group" disabled={!!state.feedback || storageError}>
        <ExerciseInput key={question.id} exercise={question.exercise} locale={state.locale} initialAnswer={state.draft as Answer | null}
          onAnswer={() => {}} onDraft={draft => commit({ ...state, draft })} />
        <FoundationButton className="gm-secondary" disabled={state.assisted || !!state.feedback || storageError}
          onClick={() => commit({ ...state, assisted: true })}>{c.hint}</FoundationButton>
        {state.assisted && <p>{question.exercise.hint[state.locale]}</p>}
      </fieldset>
      {!state.feedback && <><FoundationButton disabled={storageError || !readyAnswer(question.exercise, state.draft as Answer | null)} onClick={check}>{c.check}</FoundationButton>
        <FoundationButton className="gm-secondary" disabled={storageError} onClick={skip}>{c.skip}</FoundationButton></>}
      {state.feedback && <div className="gm-feedback" tabIndex={-1}>
        <p role="status">{state.feedback.outcome === "correct" ? c.correct : c.incorrect}{state.feedback.assisted ? ` · ${c.assisted}` : ""}</p>
        <p>{c.yourAnswer}: <span lang="de">{answerText(state.draft as Answer, state.session!, state.index)}</span></p>
        <p>{c.accepted}: <span lang="de">{answerText(state.feedback.acceptedAnswer, state.session!, state.index)}</span></p>
        <p>{state.feedback.explanation[state.locale]}</p><p className="gm-meta">{c.provisional}</p>
        <FoundationButton onClick={next}>{c.next}</FoundationButton>
      </div>}
    </PracticeCard> : null}
  </div></main>;
}

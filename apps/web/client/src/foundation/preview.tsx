import {
  useEffect,
  useRef,
  useState,
} from "react";
import {
  SessionSchema,
  type Answer,
} from "@german-master/contracts";
import formats from "@german-master/contracts/examples/practice-formats-session.json";
import sample from "@german-master/contracts/examples/session.json";
import "./foundation.css";

import { copy, type Locale } from "./locales";
const parsedSample = SessionSchema.safeParse(new URLSearchParams(window.location.search).has("formats") ? formats : sample);
export { FoundationButton } from "./controls";
import { FoundationButton } from "./controls";
export { PracticeCard } from "./controls";
import { PracticeCard } from "./controls";
export { ExerciseInput, AnswerField } from "./exercise-input";
import { ExerciseInput } from "./exercise-input";
export default function FoundationPreview() {
  const parsed = parsedSample;
  const [locale, setLocale] = useState<Locale>("en");
  const [theme, setTheme] = useState("system");
  const [index, setIndex] = useState(0);
  const [answer, setAnswer] = useState<Answer | null>(null);
  const [inspected, setInspected] = useState(false);
  const heading = useRef<HTMLHeadingElement>(null);
  useEffect(() => {
    heading.current?.focus();
  }, [index]);
  const c = copy[locale];
  if (!parsed.success)
    return (
      <main className="gm-foundation" role="alert">
        {c.unavailable}
      </main>
    );
  const session = parsed.data;
  const exercise = session.questions[index].exercise;
  return (
    <main className={`gm-foundation ${new URLSearchParams(window.location.search).get("text") === "200" ? "gm-format-large-text" : ""}`} data-theme={theme} lang={locale}>
      <div className="gm-column">
        <header className="gm-header">
          <strong>{c.title}</strong>
          <span>{c.subtitle}</span>
        </header>
        <div className="gm-settings">
          <label>
            {c.language}
            <select
              value={locale}
              onChange={(e) => setLocale(e.target.value as Locale)}
            >
              <option value="en">English</option>
              <option value="de">Deutsch</option>
            </select>
          </label>
          <label>
            {c.theme}
            <select value={theme} onChange={(e) => setTheme(e.target.value)}>
              <option value="system">{c.system}</option>
              <option value="light">{c.light}</option>
              <option value="dark">{c.dark}</option>
            </select>
          </label>
        </div>
        <p className="gm-notice">{c.notice}</p>
        <PracticeCard>
          <p className="gm-meta">
            {c.question} {index + 1} {c.of} {session.questions.length}
          </p>
          <h1 ref={heading} tabIndex={-1} lang="de">
            {exercise.prompt}
          </h1>
          <p>{exercise.instruction[locale]}</p>
          <ExerciseInput
            key={exercise.id}
            exercise={exercise}
            locale={locale}
            onAnswer={(v) => {
              setAnswer(v);
              setInspected(false);
            }}
          />
          <details key={`hint-${exercise.id}`}>
            <summary>{c.hint}</summary>
            <p>{exercise.hint[locale]}</p>
          </details>
          <FoundationButton
            disabled={!answer}
            onClick={() => setInspected(true)}
          >
            {c.inspect}
          </FoundationButton>
          {inspected && (
            <div className="gm-feedback">
              <p role="status">{c.ready}</p>
              <pre>{JSON.stringify(answer, null, 2)}</pre>
            </div>
          )}
          <FoundationButton
            className="gm-secondary"
            onClick={() => {
              setIndex((index + 1) % session.questions.length);
              setAnswer(null);
              setInspected(false);
            }}
          >
            {c.next}
          </FoundationButton>
        </PracticeCard>
      </div>
    </main>
  );
}

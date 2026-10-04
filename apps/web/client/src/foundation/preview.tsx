import {
  useEffect,
  useRef,
  useState,
  type ButtonHTMLAttributes,
  type ReactNode,
} from "react";
import {
  SessionSchema,
  type Exercise,
  type Answer,
} from "@german-master/contracts";
import sample from "@german-master/contracts/examples/session.json";
import "./foundation.css";

import { copy, type Locale } from "./locales";
const parsedSample = SessionSchema.safeParse(sample);
export function FoundationButton(
  props: ButtonHTMLAttributes<HTMLButtonElement>,
) {
  return <button {...props} className={`gm-button ${props.className ?? ""}`} />;
}
export function PracticeCard({ children }: { children: ReactNode }) {
  return <section className="gm-card">{children}</section>;
}
export function AnswerField({
  label,
  value,
  onChange,
  id,
}: {
  label: string;
  value: string;
  onChange: (v: string) => void;
  id: string;
}) {
  return (
    <div className="gm-field">
      <label htmlFor={id}>{label}</label>
      <input
        id={id}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        autoComplete="off"
        lang="de"
      />
    </div>
  );
}
export function ExerciseInput({
  exercise,
  locale,
  onAnswer,
  initialAnswer,
  onDraft,
}: {
  exercise: Exercise;
  locale: Locale;
  onAnswer: (answer: Answer | null) => void;
  initialAnswer?: Answer | null;
  onDraft?: (answer: Answer | null) => boolean | void;
}) {
  const [text, setText] = useState(() => initialAnswer?.type === "short_answer" ? initialAnswer.text : initialAnswer?.type === "choice" ? initialAnswer.optionId : "");
  const [values, setValues] = useState<Record<string, string>>(() => initialAnswer?.type === "cloze" || initialAnswer?.type === "multi_slot" ? Object.fromEntries(initialAnswer.values.map(v => [v.slotId, v.text])) : {});
  const [order, setOrder] = useState<string[]>(() => initialAnswer?.type === "word_order" ? initialAnswer.tokenIds : []);
  const c = copy[locale];
  function moveToken(index: number, direction: -1 | 1) {
    if (exercise.type !== "word_order") return;
    const destination = index + direction;
    if (destination < 0 || destination >= order.length) return;
    const next = [...order];
    [next[index], next[destination]] = [next[destination], next[index]];
    if (onDraft?.({ type: "word_order", tokenIds: next }) === false) return;
    setOrder(next);
    onAnswer(next.length === exercise.tokens.length ? { type: "word_order", tokenIds: next } : null);
  }
  if (exercise.type === "short_answer")
    return (
      <AnswerField
        id={`answer-${exercise.id}`}
        label={c.answer}
        value={text}
        onChange={(v) => {
          if (onDraft?.({ type: "short_answer", text: v }) === false) return;
          setText(v);
          onAnswer(v.trim() ? { type: exercise.type, text: v } : null);
        }}
      />
    );
  if (exercise.type === "choice")
    return (
      <fieldset className="gm-choices" lang="de">
        <legend>{c.answer}</legend>
        {exercise.options.map((o) => (
          <label key={o.id}>
            <input
              type="radio"
              name={exercise.id}
              value={o.id}
              checked={text === o.id}
              onChange={() => {
                if (onDraft?.({ type: "choice", optionId: o.id }) === false) return;
                setText(o.id);
                onAnswer({ type: "choice", optionId: o.id });
              }}
            />
            {o.text}
          </label>
        ))}
      </fieldset>
    );
  if (exercise.type === "cloze" || exercise.type === "multi_slot")
    return (
      <>
        {exercise.slots.map((slot) => (
          <AnswerField
            key={slot.id}
            id={`${exercise.id}-${slot.id}`}
            label={slot.label}
            value={values[slot.id] ?? ""}
            onChange={(v) => {
              const next = { ...values, [slot.id]: v };
              if (onDraft?.({ type: exercise.type, values: exercise.slots.map(s => ({ slotId: s.id, text: next[s.id] ?? "" })) }) === false) return;
              setValues(next);
              onAnswer(
                exercise.slots.every((s) => next[s.id]?.trim())
                  ? {
                      type: exercise.type,
                      values: exercise.slots.map((s) => ({
                        slotId: s.id,
                        text: next[s.id],
                      })),
                    }
                  : null,
              );
            }}
          />
        ))}
      </>
    );
  return (
    <div className="gm-order">
      <p>{c.answer}</p>
      <ol aria-live="polite">
        {order.map((id, index) => {
          const word = exercise.tokens.find(t => t.id === id)?.text;
          return <li key={id}>
            <span lang="de">{word}</span>
            <FoundationButton type="button" disabled={index === 0}
              aria-label={`${c.moveLeft}: ${word} (${index + 1})`} onClick={() => moveToken(index, -1)}>{c.moveLeft}</FoundationButton>
            <FoundationButton type="button" disabled={index === order.length - 1}
              aria-label={`${c.moveRight}: ${word} (${index + 1})`} onClick={() => moveToken(index, 1)}>{c.moveRight}</FoundationButton>
          </li>;
        })}
      </ol>
      <div lang="de">
        {exercise.tokens
          .filter((t) => !order.includes(t.id))
          .map((t) => (
            <FoundationButton
              key={t.id}
              type="button"
              onClick={() => {
                const next = [...order, t.id];
                if (onDraft?.({ type: "word_order", tokenIds: next }) === false) return;
                setOrder(next);
                onAnswer(
                  next.length === exercise.tokens.length
                    ? { type: "word_order", tokenIds: next }
                    : null,
                );
              }}
            >
              {t.text}
            </FoundationButton>
          ))}
      </div>
      <FoundationButton
        type="button"
        disabled={!order.length}
        onClick={() => {
          if (onDraft?.(null) === false) return;
          setOrder([]);
          onAnswer(null);
        }}
      >
        {c.reset}
      </FoundationButton>
    </div>
  );
}
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
    <main className="gm-foundation" data-theme={theme} lang={locale}>
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

import { useState, useRef, useEffect } from "react";
import type { Exercise, Answer } from "@german-master/contracts";
import { answerReady } from "@german-master/learning-engine";
import { FoundationButton } from "./controls";
import { copy, type Locale } from "./locales";

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
  suggestions,
}: {
  exercise: Exercise;
  locale: Locale;
  onAnswer: (answer: Answer | null) => void;
  initialAnswer?: Answer | null;
  onDraft?: (answer: Answer | null) => boolean | void;
  /** Optional tap choices for an otherwise free-text exercise; answer schema and grading stay unchanged. */
  suggestions?: Record<string, string[]>;
}) {
  const [text, setText] = useState(() => initialAnswer?.type === "short_answer" ? initialAnswer.text : initialAnswer?.type === "choice" ? initialAnswer.optionId : "");
  const [values, setValues] = useState<Record<string, string>>(() => initialAnswer?.type === "cloze" || initialAnswer?.type === "multi_slot" ? Object.fromEntries(initialAnswer.values.map(v => [v.slotId, v.text])) : {});
  const [order, setOrder] = useState<string[]>(() => initialAnswer?.type === "word_order" ? initialAnswer.tokenIds : []);
  const [interactive, setInteractive] = useState<Answer | null>(initialAnswer ?? null);
  const [activeLeft, setActiveLeft] = useState<string | null>(null);
  const bank = useRef<HTMLDivElement>(null);
  const selectedWords = useRef<HTMLOListElement>(null);
  const matchingLeft = useRef<HTMLDivElement>(null);
  const [focusDestination, setFocusDestination] = useState<{group: "bank" | "selected" | "matching"; id?: string} | null>(null);
  useEffect(() => {
    if (!focusDestination) return;
    const group = focusDestination.group === "matching" ? matchingLeft.current : focusDestination.group === "bank" ? bank.current : selectedWords.current;
    const buttons = Array.from(group?.querySelectorAll<HTMLButtonElement>("button:not(:disabled)") ?? []);
    (buttons.find(b => b.dataset.itemId === focusDestination.id) ?? buttons[0])?.focus();
  }, [focusDestination]);
  const c = copy[locale];
  function commit(next: Answer) {
    if (onDraft?.(next) === false) return false;
    setInteractive(next);
    onAnswer(answerReady(exercise, next));
    return true;
  }
  if (exercise.type === "gap_choice") return <div className="gm-gap-choices">
    {exercise.slots.map(slot => <fieldset className="gm-choices" key={slot.id}>
      <legend lang="de">{slot.label}</legend>
      {slot.options.map(option => <label key={option.id}>
        <input type="radio" name={`${exercise.id}-${slot.id}`} checked={interactive?.type === "gap_choice" && interactive.selections.some(v => v.slotId === slot.id && v.optionId === option.id)}
          onChange={() => commit({ type: "gap_choice", selections: [
            ...(interactive?.type === "gap_choice" ? interactive.selections.filter(v => v.slotId !== slot.id) : []),
            { slotId: slot.id, optionId: option.id }] })} />
        <span lang="de">{option.text}</span>
      </label>)}
    </fieldset>)}
  </div>;
  if (exercise.type === "matching") {
    const pairs = interactive?.type === "matching" ? interactive.pairs : [];
    return <div className="gm-matching">
      <p>{c.matchingInstruction}</p>
      <div className="gm-matching-columns">
        <div ref={matchingLeft} role="group" aria-label={c.leftItems}>
          <p className="gm-meta">{c.leftItems}</p>
          {exercise.left.map(item => <FoundationButton key={item.id} data-item-id={item.id} type="button" aria-pressed={activeLeft === item.id}
            onClick={() => setActiveLeft(activeLeft === item.id ? null : item.id)}><span lang="de">{item.text}</span></FoundationButton>)}
        </div>
        <div role="group" aria-label={c.rightItems}>
          <p className="gm-meta">{c.rightItems}</p>
          {exercise.right.map(item => <FoundationButton key={item.id} type="button" disabled={!activeLeft}
            onClick={() => {
              const next: Answer = { type: "matching", pairs: [...pairs.filter(p => p.leftId !== activeLeft && p.rightId !== item.id), { leftId: activeLeft!, rightId: item.id }] };
              if (!commit(next)) return;
              setFocusDestination({group: "matching", id: activeLeft!}); setActiveLeft(null);
            }}><span lang="de">{item.text}</span></FoundationButton>)}
        </div>
      </div>
      <ul aria-live="polite">{pairs.map(pair => <li key={pair.leftId}>
        <span lang="de">{exercise.left.find(i => i.id === pair.leftId)?.text} → {exercise.right.find(i => i.id === pair.rightId)?.text}</span>
        <FoundationButton type="button" aria-label={`${c.removePair}: ${exercise.left.find(i => i.id === pair.leftId)?.text}`}
          onClick={() => {
            if (commit({ type: "matching", pairs: pairs.filter(p => p.leftId !== pair.leftId) })) setFocusDestination({group: "matching", id: pair.leftId});
          }}>×</FoundationButton>
      </li>)}</ul>
    </div>;
  }

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
      <div className="gm-quick-entry">
        {suggestions?.answer && <fieldset className="gm-choices gm-quick-choices"><legend>{c.answer}</legend>
          {suggestions.answer.map(option => <label key={option}><input type="radio" name={`${exercise.id}-quick`} checked={text === option}
            onChange={() => { const answer: Answer = { type: "short_answer", text: option }; if (onDraft?.(answer) === false) return; setText(option); onAnswer(answer); }} />
            <span lang="de">{option}</span></label>)}
        </fieldset>}
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
      </div>
    );
  if (exercise.type === "choice")
    return (
      <fieldset className="gm-choices">
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
            <span lang="de">{o.text}</span>
          </label>
        ))}
      </fieldset>
    );
  if (exercise.type === "cloze" || exercise.type === "multi_slot")
    return (
      <>
        {exercise.slots.map((slot) => (
          <div key={slot.id} className="gm-quick-entry">
            {suggestions?.[slot.id] && <fieldset className="gm-choices gm-quick-choices"><legend lang="de">{slot.label}</legend>
              {suggestions[slot.id].map(option => <label key={option}><input type="radio" name={`${exercise.id}-${slot.id}-quick`} checked={values[slot.id] === option}
                onChange={() => { const next = { ...values, [slot.id]: option }; const answer: Answer = { type: exercise.type, values: exercise.slots.map(s => ({ slotId: s.id, text: next[s.id] ?? "" })) }; if (onDraft?.(answer) === false) return; setValues(next); onAnswer(exercise.slots.every(s => next[s.id]?.trim()) ? answer : null); }} />
                <span lang="de">{option}</span></label>)}
            </fieldset>}
            <AnswerField
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
          </div>
        ))}
      </>
    );
  return (
    <div className="gm-order">
      <p>{c.answer}</p>
      <ol ref={selectedWords} aria-live="polite">
        {order.map((id, index) => {
          const word = exercise.tokens.find(t => t.id === id)?.text;
          return <li key={id}>
            <span lang="de">{word}</span>
            <FoundationButton type="button" aria-label={`${c.remove}: ${word} (${index + 1})`} onClick={() => {
              const next = order.filter(t => t !== id);
              if (onDraft?.({ type: "word_order", tokenIds: next }) === false) return;
              setOrder(next); onAnswer(null); setFocusDestination({group: next.length ? "selected" : "bank"});
            }}>×</FoundationButton>
            <FoundationButton type="button" disabled={index === 0}
              aria-label={`${c.moveLeft}: ${word} (${index + 1})`} onClick={() => moveToken(index, -1)}>←</FoundationButton>
            <FoundationButton type="button" disabled={index === order.length - 1}
              aria-label={`${c.moveRight}: ${word} (${index + 1})`} onClick={() => moveToken(index, 1)}>→</FoundationButton>
          </li>;
        })}
      </ol>
      <div ref={bank} lang="de">
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
                setFocusDestination({group: next.length === exercise.tokens.length ? "selected" : "bank"});
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
          setFocusDestination({group: "bank"});
          onAnswer(null);
        }}
      >
        {c.reset}
      </FoundationButton>
    </div>
  );
}

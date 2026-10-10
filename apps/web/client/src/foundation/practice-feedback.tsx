import type { Ref } from 'react';
import type { CompletedAnswer } from '@german-master/contracts';
import { FoundationButton } from './controls';

/** Presentation only: grading, durable writes and progression remain with the caller. */
export function PracticeFeedback({ correct, outcome, answer, accepted, answerLabel, acceptedLabel, explanation,
  note, nextLabel, onNext, disabled = false, regionRef, completedAnswer }: {
  correct: boolean; outcome: string; answer: string; accepted: string; answerLabel: string; acceptedLabel: string;
  explanation: string; note?: string; nextLabel: string; onNext: () => void; disabled?: boolean; regionRef?: Ref<HTMLDivElement>;
  completedAnswer?: CompletedAnswer;
}) {
  return <>
    <div className="gm-feedback gm-practice-feedback" data-outcome={correct ? 'correct' : 'incorrect'} ref={regionRef} tabIndex={-1}>
      <p role="status"><span aria-hidden="true">{correct ? '✓' : 'ⓘ'}</span> {outcome}</p>
      <p>{answerLabel}: <strong lang="de">{answer}</strong></p>
      <p>{acceptedLabel}: {completedAnswer ? <span lang="de">{completedAnswer.parts.map((part, i) =>
        part.emphasis ? <strong key={i}>{part.text}</strong> : <span key={i}>{part.text}</span>)}</span>
        : <strong lang="de">{accepted}</strong>}</p>
      <p>{explanation}</p>
      {note && <p className="gm-meta">{note}</p>}
    </div>
    <FoundationButton className="gm-continue-action" disabled={disabled} onClick={onNext}>{nextLabel}</FoundationButton>
  </>;
}

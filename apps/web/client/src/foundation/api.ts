import { SessionSchema, AttemptBatchResponseSchema, type Answer, type Attempt, type Acknowledgment, type Session, type SessionRequest } from "@german-master/contracts";

export interface FoundationApi {
  createSession(request: SessionRequest): Promise<Session>;
  submit(attempt: Attempt): Promise<Acknowledgment>;
}

/** Public fixture auth is used only by the isolated loopback development preview. */
export function localFoundationApi(): FoundationApi {
  async function post(path: string, input: unknown) {
    const response = await fetch(path, { method: "POST", headers: {
      "Content-Type": "application/json", Authorization: "Bearer foundation-local-demo",
    }, body: JSON.stringify(input) });
    if (!response.ok) throw Error("Foundation request failed");
    return response.json();
  }
  return {
    async createSession(request) { return SessionSchema.parse(await post("/v2/sessions", request)); },
    async submit(attempt) {
      const parsed = AttemptBatchResponseSchema.parse(await post("/v2/attempts:batch", { apiVersion: "v2", attempts: [attempt] }));
      if (parsed.acknowledgments.length !== 1 || parsed.acknowledgments[0].attemptId !== attempt.attemptId)
        throw Error("Acknowledgment linkage mismatch");
      return parsed.acknowledgments[0];
    },
  };
}

export function sessionRequest(): SessionRequest {
  return { apiVersion: "v2", requestId: crypto.randomUUID(), questionCount: 5,
    capabilities: ["short_answer@1", "choice@1", "cloze@1", "word_order@1", "multi_slot@1"] };
}
export function prepareAttempt(session: Session, index: number, answer: Answer, assisted: boolean, deviceId: string): Attempt {
  return { attemptId: crypto.randomUUID(), sessionQuestionId: session.questions[index].id,
    exerciseRevision: session.questions[index].exercise.revision, deviceId, answer,
    assistance: assisted ? ["hint"] : [], answeredAt: new Date().toISOString().replace(/\.\d{3}Z$/, "Z"), clientSequence: index };
}

export function answerText(answer: Answer, session: Session, index: number): string {
  const exercise = session.questions[index].exercise;
  switch (answer.type) {
    case "short_answer": return answer.text;
    case "choice": return exercise.type === "choice" ? exercise.options.find(o => o.id === answer.optionId)?.text ?? answer.optionId : "";
    case "word_order": return exercise.type === "word_order" ? answer.tokenIds.map(id => exercise.tokens.find(t => t.id === id)?.text).join(" ") : "";
    default: return answer.values.map(v => {
      const label = exercise.type === "cloze" || exercise.type === "multi_slot"
        ? exercise.slots.find(s => s.id === v.slotId)?.label ?? v.slotId : v.slotId;
      return `${label}: ${v.text}`;
    }).join(" · ");
  }
}

import { z } from 'zod';
import { AnswerSchema, AttemptSchema, AcknowledgmentSchema, EvaluationSchema, ExposureEventSchema,
  ExposureAcknowledgmentSchema, SessionCompletionRequestSchema, SessionCompletionReceiptSchema,
  type Answer, type SessionCompletionRequest } from '@german-master/contracts';
import { grade, validatePreparedPack, canStartPreparedPack, packCanonical } from '@german-master/learning-engine';
import { prepareAttempt } from '../foundation/api';
import { WebReserve } from './reserve';
import { type LearnerApi } from './api';

const EventSchema = z.discriminatedUnion('kind', [
  z.object({ kind: z.literal('attempt'), request: AttemptSchema, provisional: EvaluationSchema, receipt: AcknowledgmentSchema.nullable() }),
  z.object({ kind: z.literal('skip'), request: ExposureEventSchema, receipt: ExposureAcknowledgmentSchema.nullable() }),
  z.object({ kind: z.literal('completion'), request: SessionCompletionRequestSchema, receipt: SessionCompletionReceiptSchema.nullable() }),
]);
const PracticeSchema = z.object({
  version: z.literal(1).default(1),
  id: z.string().uuid(), deviceId: z.string().uuid(), pack: z.unknown(), index: z.number().int().min(0),
  draft: z.union([AnswerSchema, z.object({ type: z.literal('word_order'), tokenIds: z.array(z.string()) })]).nullable(),
  assisted: z.boolean(), feedback: EvaluationSchema.nullable(), events: z.array(EventSchema).max(51), ended: z.boolean(),
});
export type OfflinePractice = z.infer<typeof PracticeSchema>;

/** Each practice pins its own complete pack, so reserve refresh cannot change started work. */
export class OfflineRepository {
  constructor(readonly db: WebReserve) {}
  async read(id: string) {
    const raw = await this.db.table('practices').get(id);
    if (!raw) throw Error('Offline practice unavailable');
    const practice = PracticeSchema.parse(raw);
    const pack = await validatePreparedPack(practice.pack);
    const session = pack.sessions.find(s => s.id === id);
    if (!session || practice.index > session.questions.length) throw Error('Offline session mismatch');
    const used = new Set<string>();
    for (const [index, event] of practice.events.entries()) {
      if (event.kind === 'completion') {
        if (!practice.ended || index !== practice.events.length - 1 || event.receipt &&
          (event.receipt.sessionId !== id || event.receipt.requestId !== event.request.requestId || event.receipt.mode !== event.request.mode))
          throw Error('Offline completion linkage mismatch');
        continue;
      }
      const q = session.questions.find(q => q.id === event.request.sessionQuestionId);
      if (!q || used.has(q.id) || q.exercise.revision !== event.request.exerciseRevision || event.request.deviceId !== practice.deviceId)
        throw Error('Offline event linkage mismatch');
      used.add(q.id);
      if (event.kind === 'attempt') {
        const rubric = pack.rubrics.find(r => r.exerciseId === q.exercise.id && r.exerciseRevision === q.exercise.revision)!;
        if (packCanonical(grade(q.exercise, rubric, event.request.answer, event.request.assistance)) !== packCanonical(event.provisional)
          || event.receipt && event.receipt.attemptId !== event.request.attemptId) throw Error('Offline evaluation linkage mismatch');
      } else if (event.receipt && event.receipt.eventId !== event.request.eventId) throw Error('Offline Skip linkage mismatch');
    }
    if (used.size !== practice.index + (practice.feedback ? 1 : 0)
      || practice.ended !== practice.events.some(e => e.kind === 'completion')) throw Error('Offline position mismatch');
    if (practice.feedback) {
      const current = practice.events.find(e => e.kind === 'attempt' && e.request.sessionQuestionId === session.questions[practice.index]?.id);
      if (!current || current.kind !== 'attempt' || packCanonical(current.provisional) !== packCanonical(practice.feedback))
        throw Error('Offline feedback mismatch');
    }
    return { practice, pack, session };
  }
  async list(): Promise<string[]> { return this.db.table('practices').toCollection().primaryKeys() as Promise<string[]>; }
  async start(deviceId: string, now: Date): Promise<string> {
    const { pack } = await this.db.read();
    if (!pack || !canStartPreparedPack(pack, now)) throw Error('No valid prepared session');
    return this.db.transaction('rw', this.db.table('records'), this.db.table('practices'), async () => {
      const record = await this.db.table('records').get('reserve');
      if (packCanonical(record.pack) !== packCanonical(pack)) throw Error('Reserve changed');
      const consumed: string[] = record.consumed ?? [];
      const session = pack.sessions.find(s => !consumed.includes(s.id));
      if (!session) throw Error('Reserve exhausted');
      const practice = PracticeSchema.parse({ version: 1, id: session.id, deviceId, pack, index: 0, draft: null,
        assisted: false, feedback: null, events: [], ended: false });
      await this.db.table('practices').add(practice);
      await this.db.table('records').put({ ...record, consumed: [...consumed, session.id] });
      return session.id;
    });
  }
  private async change(id: string, update: (value: Awaited<ReturnType<OfflineRepository['read']>>) => OfflinePractice) {
    const value = await this.read(id);
    const next = PracticeSchema.parse(update(value));
    return this.db.transaction('rw', this.db.table('practices'), async () => {
      const raw = await this.db.table('practices').get(id);
      if (packCanonical(PracticeSchema.parse(raw)) !== packCanonical(value.practice)) throw Error('Offline practice changed; retry');
      await this.db.table('practices').put(next);
    });
  }
  async draft(id: string, draft: OfflinePractice['draft'], assisted?: boolean) {
    await this.change(id, ({ practice }) => {
      if (practice.ended || practice.feedback) throw Error('Question already closed');
      return { ...practice, draft, assisted: assisted ?? practice.assisted };
    });
  }
  async answer(id: string, answer: Answer) {
    await this.change(id, ({ practice, pack, session }) => {
      if (practice.ended || practice.feedback) throw Error('Question already closed');
      const q = session.questions[practice.index];
      if (!q) throw Error('Question unavailable');
      const rubric = pack.rubrics.find(r => r.exerciseId === q.exercise.id && r.exerciseRevision === q.exercise.revision)!;
      const provisional = grade(q.exercise, rubric, answer, practice.assisted ? ['hint'] : []);
      const request = prepareAttempt(session, practice.index, answer, practice.assisted, practice.deviceId);
      return { ...practice, feedback: provisional, events: [...practice.events, { kind: 'attempt' as const, request, provisional, receipt: null }] };
    });
  }
  async next(id: string) {
    await this.change(id, ({ practice }) => {
      if (practice.ended || !practice.feedback) throw Error('No feedback to continue');
      return { ...practice, index: practice.index + 1, draft: null, assisted: false, feedback: null };
    });
  }
  async skip(id: string) {
    await this.change(id, ({ practice, session }) => {
      if (practice.ended || practice.feedback) throw Error('Question already closed');
      const q = session.questions[practice.index];
      if (!q) throw Error('Question unavailable');
      const request = { eventId: crypto.randomUUID(), sessionQuestionId: q.id, exerciseRevision: q.exercise.revision,
        deviceId: practice.deviceId, disposition: 'skip' as const, occurredAt: new Date().toISOString() };
      return { ...practice, index: practice.index + 1, draft: null, assisted: false,
        events: [...practice.events, { kind: 'skip' as const, request, receipt: null }] };
    });
  }
  async end(id: string) {
    await this.change(id, ({ practice, session }) => {
      if (practice.ended) return practice;
      const answered = practice.events.filter(e => e.kind !== 'completion').length;
      const request: SessionCompletionRequest = { apiVersion: 'v2', requestId: crypto.randomUUID(),
        mode: answered === session.questions.length ? 'full' : 'partial' };
      return { ...practice, ended: true, events: [...practice.events, { kind: 'completion' as const, request, receipt: null }] };
    });
  }
  /** Ordered single-event delivery. Stop on any network/storage/rejection failure; explicit retry only. */
  async sync(id: string, api: Pick<LearnerApi, 'submit' | 'expose' | 'complete'>) {
    for (;;) {
      const { practice, session } = await this.read(id);
      const index = practice.events.findIndex(e => !e.receipt || 'status' in e.receipt && e.receipt.status === 'rejected');
      if (index === -1) return;
      const event = practice.events[index];
      if (event.receipt && 'status' in event.receipt && event.receipt.status === 'rejected' && !event.receipt.error.retryable)
        throw Error('Rejected offline event requires review');
      let accepted: z.infer<typeof EventSchema>;
      if (event.kind === 'attempt') {
        const receipt = AcknowledgmentSchema.parse(await api.submit(event.request));
        if (receipt.attemptId !== event.request.attemptId) throw Error('Offline answer mismatch');
        accepted = { ...event, receipt };
      } else if (event.kind === 'skip') {
        const receipt = ExposureAcknowledgmentSchema.parse(await api.expose(event.request));
        if (receipt.eventId !== event.request.eventId) throw Error('Offline Skip mismatch');
        accepted = { ...event, receipt };
      } else {
        if (!api.complete) throw Error('Completion unavailable');
        const receipt = SessionCompletionReceiptSchema.parse(await api.complete(id, event.request));
        const graded = practice.events.filter(e => e.kind === 'attempt').length;
        const skipped = practice.events.filter(e => e.kind === 'skip').length;
        const correct = practice.events.filter(e => e.kind === 'attempt' && e.receipt && e.receipt.status !== 'rejected'
          && e.receipt.evaluation.outcome === 'correct').length;
        if (receipt.requestId !== event.request.requestId || receipt.sessionId !== id || receipt.mode !== event.request.mode
          || receipt.plannedCount !== session.questions.length || receipt.gradedCount !== graded || receipt.skippedCount !== skipped || receipt.correctCount !== correct)
          throw Error('Offline completion mismatch');
        accepted = { ...event, receipt };
      }
      // Merge into the latest local state; syncing never changes the visible prompt/provisional feedback.
      await this.change(id, ({ practice: latest }) => {
        if (packCanonical(latest.events[index].request) !== packCanonical(event.request)) throw Error('Offline event changed');
        const events = [...latest.events]; events[index] = accepted;
        return { ...latest, events };
      });
      if (accepted.receipt && 'status' in accepted.receipt && accepted.receipt.status === 'rejected') throw Error('Offline event not accepted');
    }
  }
}

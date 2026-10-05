import { AcknowledgmentSchema, ContentReportRequestSchema, ContentReportReceiptSchema, ExposureAcknowledgmentSchema,
  ProfileRequestSchema, SessionCompletionReceiptSchema, SessionSchema } from '@german-master/contracts';
import type { LearnerApi } from './api';
import { type JourneyStorage, readJourney, saveJourney } from './storage';
import { PROFILE_PENDING_KEY } from './setup';
import { REPORT_KEY } from './report';
import { OfflineRepository } from './offline';
import { type WebReserve } from './reserve';

/** Explicit dependency order, not client-clock order. Caller holds the fixture owner
 * through every receipt commit. Failure stops the entire pass; no new work is created. */
export async function syncSavedWork(api: LearnerApi, storage: JourneyStorage, db: WebReserve) {
  const rawProfile = storage.getItem(PROFILE_PENDING_KEY);
  if (rawProfile) {
    const request = ProfileRequestSchema.parse(JSON.parse(rawProfile));
    await api.saveProfile(request);
    await api.profile(); // Historical replay must not install old preferences.
    storage.setItem(PROFILE_PENDING_KEY, '');
  }
  function update(patch: Partial<NonNullable<ReturnType<typeof readJourney>['practice']>>) {
    const latest = readJourney(storage);
    if (!latest.practice) throw Error('Saved practice disappeared');
    saveJourney(storage, { ...latest, practice: { ...latest.practice, ...patch } });
  }
  let p = readJourney(storage).practice;
  if (p) {
    if (p.rejected) throw Error('Rejected event requires review');
    if (!p.session) {
      const session = SessionSchema.parse('focus' in p.request
        ? await api.createFocusedSession(p.request) : await api.createSession(p.request));
      update({ session });
      p = readJourney(storage).practice!;
    }
    if (p.pending && !p.evaluation) {
      const receipt = AcknowledgmentSchema.parse(await api.submit(p.pending));
      if (receipt.attemptId !== p.pending.attemptId) throw Error('Answer receipt mismatch');
      if (receipt.status === 'rejected') { update({ rejected: true }); throw Error('Saved answer rejected'); }
      update({ evaluation: receipt.evaluation, confirmedCount: p.confirmedCount + 1,
        correctCount: p.correctCount + (receipt.evaluation.outcome === 'correct' ? 1 : 0) });
    }
    p = readJourney(storage).practice!;
    if (p.pendingExposure) {
      const receipt = ExposureAcknowledgmentSchema.parse(await api.expose(p.pendingExposure));
      if (receipt.eventId !== p.pendingExposure.eventId) throw Error('Skip receipt mismatch');
      if (receipt.status === 'rejected') { update({ rejected: true }); throw Error('Saved Skip rejected'); }
      update({ index: p.index + 1, draft: null, assisted: false, pendingExposure: null, skippedCount: p.skippedCount + 1 });
    }
    p = readJourney(storage).practice!;
    if (p.completion && !p.completionReceipt) {
      if (!api.complete || !p.session || p.pending && !p.evaluation || p.pendingExposure) throw Error('Completion not ready');
      const receipt = SessionCompletionReceiptSchema.parse(await api.complete(p.session.id, p.completion));
      if (receipt.requestId !== p.completion.requestId || receipt.sessionId !== p.session.id || receipt.mode !== p.completion.mode
        || receipt.plannedCount !== p.session.questions.length || receipt.gradedCount !== p.confirmedCount
        || receipt.skippedCount !== p.skippedCount || receipt.correctCount !== p.correctCount) throw Error('Completion receipt mismatch');
      update({ completionReceipt: receipt });
    }
  }
  const offline = new OfflineRepository(db);
  for (const id of await offline.list()) await offline.sync(id, api);
  const rawReport = storage.getItem(REPORT_KEY);
  if (rawReport) {
    const record = JSON.parse(rawReport);
    if (record.version !== 1 || typeof record.recorded !== 'boolean') throw Error('Unreadable report');
    const request = ContentReportRequestSchema.parse(record.request);
    if (!record.recorded) {
      if (!api.report) throw Error('Reporting unavailable');
      const receipt = ContentReportReceiptSchema.parse(await api.report(request));
      if (receipt.reportId !== request.reportId) throw Error('Report receipt mismatch');
      storage.setItem(REPORT_KEY, JSON.stringify({ version: 1, request, recorded: true }));
    }
  }
  const reserve = await db.read();
  if (reserve.pending) {
    if (!api.preparePack) throw Error('Download unavailable');
    await db.prepare(reserve.pending, request => api.preparePack!(request));
  }
}

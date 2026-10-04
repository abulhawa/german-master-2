import type { ConfirmedTarget } from '@german-master/contracts';
import { EVIDENCE_POLICY_VERSION, type TargetSnapshot } from '@german-master/learning-engine';

/** Deliberately omit raw answers, learner identity and internal evidence history. */
export function confirmedTarget(targetId: string, snapshot: TargetSnapshot | null, lastSequence: number,
  now?: string): ConfirmedTarget {
  return { targetId, policyVersion: EVIDENCE_POLICY_VERSION, state: snapshot?.state ?? 'new',
    exposureCount: snapshot?.exposureCount ?? 0, qualifyingCheckCount: snapshot?.qualifyingChecks.length ?? 0,
    everMastered: snapshot?.everMastered ?? false, lapseCount: snapshot?.lapseCount ?? 0, lastSequence,
    schedule: snapshot?.schedule ? [{ dueAt: snapshot.schedule.dueAt,
      intervalStep: snapshot.schedule.intervalStep, intervalDays: snapshot.schedule.intervalDays }] : [],
    isDue: now === undefined ? snapshot?.isDue ?? false :
      !!snapshot?.schedule && Date.parse(snapshot.schedule.dueAt) <= Date.parse(now) };
}

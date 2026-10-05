import { PreparedPackSchema, type PreparedPack } from '@german-master/contracts';
import { grade } from './index';

/** Same canonical JSON as the local server. No credentials or learner state. */
export function packCanonical(value: unknown): string {
  if (Array.isArray(value)) return `[${value.map(packCanonical).join(',')}]`;
  if (value !== null && typeof value === 'object') return `{${Object.entries(value).sort(([a],[b])=>a.localeCompare(b))
    .map(([key,v])=>`${JSON.stringify(key)}:${packCanonical(v)}`).join(',')}}`;
  return JSON.stringify(value);
}

/** Verify the entire download before a cache can declare it ready. */
export async function validatePreparedPack(input: unknown): Promise<PreparedPack> {
  const pack = PreparedPackSchema.parse(input);
  if (!/^[0-9a-f]{64}$/.test(pack.contentHash) || Date.parse(pack.expiresAt) <= Date.parse(pack.issuedAt)) throw Error('Invalid pack manifest');
  const {contentHash, ...payload} = pack;
  const bytes = new TextEncoder().encode(packCanonical(payload));
  const digest = await crypto.subtle.digest('SHA-256',bytes);
  const hash = Array.from(new Uint8Array(digest),v=>v.toString(16).padStart(2,'0')).join('');
  if(hash !== contentHash) throw Error('Pack hash mismatch');
  const sessions = new Set<string>(); const questions = new Set<string>(); const needed = new Set<string>();
  const rubrics = new Map(pack.rubrics.map(r=>[`${r.exerciseId}@${r.exerciseRevision}`,r]));
  if(rubrics.size !== pack.rubrics.length) throw Error('Duplicate pack rubric');
  for(const session of pack.sessions) {
    if(sessions.has(session.id) || session.contentReleaseId !== pack.contentReleaseId) throw Error('Pack session mismatch');
    sessions.add(session.id);
    for(const question of session.questions) {
      if(questions.has(question.id)) throw Error('Duplicate pack question');
      questions.add(question.id);
      const key = `${question.exercise.id}@${question.exercise.revision}`; needed.add(key);
      const rubric = rubrics.get(key);
      if(!rubric) throw Error('Missing pack rubric');
      grade(question.exercise,rubric,rubric.acceptedAnswers[0],[]);
    }
  }
  if(needed.size !== rubrics.size) throw Error('Extraneous pack rubric');
  return pack;
}

/** Expiry gates new starts only. Pending work from a started session remains uploadable. */
export function canStartPreparedPack(pack: PreparedPack, now: Date): boolean {
  return Number.isFinite(now.getTime()) && now.getTime() >= Date.parse(pack.issuedAt) && now.getTime() < Date.parse(pack.expiresAt);
}

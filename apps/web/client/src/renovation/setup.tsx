import { useEffect, useRef, useState } from "react";
import { ProfileRequestSchema, type LearnerProfile, type ProfileRequest } from "@german-master/contracts";
import { FoundationButton, PracticeCard } from "../foundation/preview";
import { learnerCopy, accountLearnerCopy } from "./locales";
import type { LearnerApi } from "./api";
import type { JourneyStorage } from "./storage";
import type { FixtureOwner } from "./ownership";
import { shellCopy } from './shell-locales';

export const PROFILE_PENDING_KEY = "german-master-v2:local-fixture:profile-request-v1";
export function ProfileSetup({ profile, api, storage, owner, onSaved, onReload, onCancel, onPreviewLocale, authenticated=false }: {
  authenticated?: boolean;
  owner?: FixtureOwner;
  profile: LearnerProfile; api: LearnerApi; storage: JourneyStorage;
  onPreviewLocale: (locale: "en" | "de") => void; onSaved: (profile: LearnerProfile) => void; onReload: () => Promise<LearnerProfile>; onCancel?: () => void;
}) {
  const [loaded] = useState(() => {
    try { const raw = storage.getItem(PROFILE_PENDING_KEY); return { pending: raw ? ProfileRequestSchema.parse(JSON.parse(raw)) : null, damaged: false }; }
    catch { return { pending: null, damaged: true }; }
  });
  const [pending, setPending] = useState<ProfileRequest | null>(loaded.pending);
  const [preferences, setPreferences] = useState(loaded.pending?.preferences ?? profile.preferences);
  const [busy, setBusy] = useState(false);
  const lock = useRef(false);
  const [error, setError] = useState(false);
  const heading = useRef<HTMLHeadingElement>(null);
  const c = learnerCopy[preferences.locale];
  useEffect(() => { heading.current?.focus(); }, []);
  async function save() {
    if (lock.current || loaded.damaged) return;
    lock.current = true; setBusy(true); setError(false);
    try { await (owner ? owner.run(saveOwned) : saveOwned()); }
    catch { setError(true); }
    finally { lock.current = false; setBusy(false); }
  }
  async function saveOwned() {
    try {
      const request = pending ?? { apiVersion: "v2", requestId: crypto.randomUUID(), expectedRevision: profile.revision, preferences };
      storage.setItem(PROFILE_PENDING_KEY, JSON.stringify(ProfileRequestSchema.parse(request)));
      setPending(request);
      await api.saveProfile(request);
      // Replay can return a historical response. Read current state before showing confirmed preferences.
      const current = await api.profile();
      storage.setItem(PROFILE_PENDING_KEY, "");
      onSaved(current);
    } catch { setError(true); }
  }
  async function reload() {
    if (lock.current || loaded.damaged) return;
    lock.current = true; setBusy(true);
    try { await (owner ? owner.run(reloadOwned) : reloadOwned()); }
    catch { setError(true); }
    finally { lock.current = false; setBusy(false); }
  }
  async function reloadOwned() {
    try { const value = await onReload(); storage.setItem(PROFILE_PENDING_KEY, ""); setPending(null); setPreferences(value.preferences); setError(false); }
    catch { setError(true); }
  }
  return <div lang={preferences.locale}><PracticeCard>
    <h1 ref={heading} tabIndex={-1}>{c.setup}</h1>
    <p>{authenticated?accountLearnerCopy[preferences.locale].draftLevel:c.draftLevel}</p>
    {loaded.damaged ? <p role="alert">{c.damaged}</p> : <>
      <fieldset className="gm-answer-group" disabled={busy || !!pending}>
        <label>{c.language}<select value={preferences.locale} onChange={e => { const locale = e.target.value as "en" | "de"; setPreferences({ ...preferences, locale }); onPreviewLocale(locale); }}><option value="en">English</option><option value="de">Deutsch</option></select></label>
        <label>{c.level}<select value={preferences.level} onChange={e => setPreferences({ ...preferences, level: e.target.value as "B1" | "B2" })}><option>B1</option><option>B2</option></select></label>
        <details className="gm-advanced"><summary>{shellCopy[preferences.locale].advanced}</summary>
        <label className="gm-field">{c.timezone}<input value={preferences.timezone} onChange={e => setPreferences({ ...preferences, timezone: e.target.value })} aria-describedby="timezone-help" /></label>
        <p id="timezone-help">{c.timezoneNote}</p>
        <label>{c.length}<select value={preferences.sessionQuestionCount} onChange={e => setPreferences({ ...preferences, sessionQuestionCount: Number(e.target.value) as 5 | 15 })}><option value={5}>{c.short}</option><option value={15}>{c.standard}</option></select></label>
        </details>
      </fieldset>
      {error && <p role="alert">{c.setupError}</p>}
      <FoundationButton disabled={busy || !preferences.timezone.trim()} onClick={() => void save()}>{pending ? c.retry : c.saveSetup}</FoundationButton>
      {(pending || error) && <FoundationButton className="gm-secondary" disabled={busy} onClick={() => void reload()}>{c.reloadProfile}</FoundationButton>}
      {onCancel && !pending && <FoundationButton className="gm-secondary" disabled={busy} onClick={onCancel}>{c.cancelSetup}</FoundationButton>}
    </>}
  </PracticeCard></div>;
}

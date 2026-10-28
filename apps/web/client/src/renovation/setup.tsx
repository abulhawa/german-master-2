import { useEffect, useRef, useState } from "react";
import { ProfileRequestSchema, type LearnerProfile, type ProfileRequest } from "@german-master/contracts";
import { FoundationButton, PracticeCard } from "../foundation/controls";
import { learnerCopy, accountLearnerCopy } from "./locales";
import type { LearnerApi } from "./api";
import type { JourneyStorage } from "./storage";
import type { FixtureOwner } from "./ownership";
import { shellCopy } from './shell-locales';
import { releaseCapabilities } from './release-capabilities';

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
  const [preferences, setPreferences] = useState<LearnerProfile['preferences']>(() => {
    if (loaded.pending || profile.setupCompleted) {
      const saved=authenticated?profile.preferences:loaded.pending?.preferences ?? profile.preferences;
      return {...saved,learningContext:releaseCapabilities.workplacePractice ? saved.learningContext : 'general' as const};
    }
    // Profile defaults are not a learner timezone choice. Use the browser zone at first setup only.
    let timezone = profile.preferences.timezone;
    try { timezone = Intl.DateTimeFormat().resolvedOptions().timeZone || timezone; } catch { /* Keep server default. */ }
    return { ...profile.preferences, timezone, learningContext: releaseCapabilities.workplacePractice ? profile.preferences.learningContext : 'general' as const };
  });
  const [busy, setBusy] = useState(false);
  const lock = useRef(false);
  const [error, setError] = useState(false);
  const heading = useRef<HTMLHeadingElement>(null);
  const c = learnerCopy[preferences.locale];
  useEffect(() => { heading.current?.focus(); }, []);
  async function save(next = preferences) {
    if (lock.current || loaded.damaged) return;
    lock.current = true; setBusy(true); setError(false);
    try { await (owner ? owner.run(()=>saveOwned(next)) : saveOwned(next)); }
    catch { setError(true); }
    finally { lock.current = false; setBusy(false); }
  }
  async function saveOwned(next = preferences) {
    try {
      const request = pending ?? { apiVersion: "v2", requestId: crypto.randomUUID(), expectedRevision: profile.revision, preferences: {...next,sessionQuestionCount:5 as const} };
      storage.setItem(PROFILE_PENDING_KEY, JSON.stringify(ProfileRequestSchema.parse(request)));
      setPending(request);
      await api.saveProfile(request);
      // Replay can return a historical response. Read current state before showing confirmed preferences.
      const current = await api.profile();
      storage.setItem(PROFILE_PENDING_KEY, "");
      if(authenticated) {setPreferences(current.preferences);onPreviewLocale(current.preferences.locale);}
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
  useEffect(()=>{
    if(authenticated&&!profile.setupCompleted&&!loaded.pending&&!loaded.damaged)void save();
  },[]);
  return <div lang={preferences.locale}><PracticeCard>
    <h1 ref={heading} tabIndex={-1}>{authenticated?shellCopy[preferences.locale].practicePreferences:c.setup}</h1>
    {!authenticated&&<p>{c.draftLevel}</p>}
    {loaded.damaged ? <p role="alert">{c.damaged}</p> : <>
      <fieldset className="gm-answer-group" disabled={busy || !!pending}>
        <label>{c.language}<select value={preferences.locale} onChange={e => { const locale = e.target.value as "en" | "de"; if(authenticated)void save({...preferences,locale});else {setPreferences({ ...preferences, locale }); onPreviewLocale(locale);} }}><option value="en">English</option><option value="de">Deutsch</option></select></label>
        <label>{c.level}<select value={preferences.level} onChange={e => {const level=e.target.value as "B1" | "B2";if(authenticated)void save({...preferences,level});else setPreferences({ ...preferences, level });}}><option>B1</option><option>B2</option></select></label>
        {releaseCapabilities.workplacePractice && <label>{preferences.locale==='de'?'Übung':'Practice'}<select value={preferences.learningContext ?? 'general'} onChange={e=>setPreferences({...preferences,learningContext:e.target.value as 'general'|'work'})}><option value="general">{preferences.locale==='de'?'Allgemeines Deutsch':'General German'}</option><option value="work">{preferences.locale==='de'?'Deutsch im Beruf':'German for work'}</option></select></label>}

      </fieldset>
      {error && <p role="alert">{c.setupError}</p>}
      {(!authenticated||pending)&&<FoundationButton disabled={busy || !preferences.timezone.trim()} onClick={() => void save()}>{pending ? c.retry : c.saveSetup}</FoundationButton>}
      {(pending || error) && <FoundationButton className="gm-secondary" disabled={busy} onClick={() => void reload()}>{c.reloadProfile}</FoundationButton>}
      {onCancel && !pending && <FoundationButton className="gm-secondary" disabled={busy} onClick={onCancel}>{c.cancelSetup}</FoundationButton>}
    </>}
  </PracticeCard></div>;
}

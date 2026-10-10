import { useEffect, useRef, useState } from 'react';
import type { SupabaseClient } from '@supabase/supabase-js';
import { FoundationButton, PracticeCard } from '../foundation/controls';
import { AccountRecovery, type RecoveryState } from './account-recovery';
import { recoveryCopy } from './recovery-locales';

export function RecoveryEmail({ auth, locale, initialEmail, mode, back, backLabel }: {
  auth: SupabaseClient['auth']; locale: 'en' | 'de'; initialEmail: string;
  mode: 'reset' | 'resend'; back: () => void; backLabel?: string;
}) {
  const c = recoveryCopy[locale];
  const [email, setEmail] = useState(initialEmail);
  const [busy, setBusy] = useState(false), [sent, setSent] = useState(false), [failed, setFailed] = useState(false);
  const [cooldown, setCooldown] = useState(false);
  const lock = useRef(false);
  const heading = useRef<HTMLHeadingElement>(null);
  useEffect(() => { heading.current?.focus(); }, []);
  useEffect(() => { if (cooldown) { const id = setTimeout(() => setCooldown(false), 60_000); return () => clearTimeout(id); } }, [cooldown]);
  async function send() {
    if (lock.current || cooldown) return;
    lock.current = true; setBusy(true); setFailed(false); setSent(false);
    try {
      // Redirects are fixed to this frontend origin, never the API origin or an input URL.
      const redirect = new URL('/', window.location.origin);
      if (mode === 'reset') redirect.searchParams.set('auth', 'recovery');
      const { error } = mode === 'reset'
        ? await auth.resetPasswordForEmail(email.trim(), { redirectTo: redirect.href })
        : await auth.resend({ type: 'signup', email: email.trim(), options: { emailRedirectTo: redirect.href } });
      if (error) throw error;
      setSent(true);
    } catch { setFailed(true); }
    finally { lock.current = false; setBusy(false); setCooldown(true); }
  }
  return <PracticeCard>
    <h1 ref={heading} tabIndex={-1}>{mode === 'reset' ? c.resetTitle : c.resendTitle}</h1>
    <form className="gm-answer-group" onSubmit={event => { event.preventDefault(); void send(); }}>
      <label className="gm-field" htmlFor="recovery-email">{c.email}<input id="recovery-email" type="email" autoComplete="email" required disabled={busy} value={email} onChange={e => setEmail(e.target.value)} /></label>
      <button type="submit" className="gm-button" disabled={busy || cooldown}>{busy ? c.sending : c.send}</button>
    </form>
    {sent && <p role="status">{c.sent}</p>}{failed && <p role="alert">{c.failed}</p>}
    {cooldown && <p>{c.wait}</p>}<p>{c.savedWork}</p>
    <FoundationButton disabled={busy} onClick={back}>{backLabel ?? c.back}</FoundationButton>
  </PracticeCard>;
}

export function RecoveryPassword({ recovery, state, locale, finish }: {
  recovery: AccountRecovery; state: RecoveryState; locale: 'en' | 'de'; finish: () => void;
}) {
  const c = recoveryCopy[locale];
  const [password, setPassword] = useState(''), [repeat, setRepeat] = useState('');
  const [busy, setBusy] = useState(false), [error, setError] = useState<'failed' | 'mismatch' | null>(null);
  const lock = useRef(false);
  async function save() {
    if (lock.current) return;
    if (password !== repeat) { setError('mismatch'); return; }
    lock.current = true; setBusy(true); setError(null);
    try { await recovery.update(password); }
    catch { setError('failed'); }
    finally { lock.current = false; setBusy(false); setPassword(''); setRepeat(''); }
  }
  return <main className="gm-foundation gm-auth" lang={locale}><div className="gm-column"><PracticeCard>
    <h1>{c.resetTitle}</h1>
    {state.stage === 'waiting' && <p role="status">{c.waiting}</p>}
    {state.stage === 'invalid' && <p role="alert">{c.invalid}</p>}
    {state.stage === 'complete' && <p role="status">{c.complete}</p>}
    {state.stage === 'ready' && <form className="gm-answer-group" onSubmit={e => { e.preventDefault(); void save(); }}>
      <label className="gm-field" htmlFor="new-password">{c.newPassword}<input id="new-password" type="password" autoComplete="new-password" minLength={8} required disabled={busy} value={password} onChange={e => setPassword(e.target.value)} /></label>
      <label className="gm-field" htmlFor="repeat-password">{c.confirmPassword}<input id="repeat-password" type="password" autoComplete="new-password" minLength={8} required disabled={busy} value={repeat} onChange={e => setRepeat(e.target.value)} /></label>
      <button className="gm-button" type="submit" disabled={busy}>{busy ? c.saving : c.save}</button>
    </form>}
    {error && <p role="alert">{c[error]}</p>}<p>{c.savedWork}</p>
    {(state.stage === 'invalid' || state.stage === 'complete') && <FoundationButton onClick={finish}>{state.stage === 'complete' ? c.continue : c.requestAgain}</FoundationButton>}
  </PracticeCard></div></main>;
}

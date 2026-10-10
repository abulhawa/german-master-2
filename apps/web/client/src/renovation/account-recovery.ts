import type { AuthChangeEvent, Session, SupabaseClient } from '@supabase/supabase-js';

export type RecoveryState = { stage: 'none' | 'waiting' | 'ready' | 'invalid' | 'complete'; subject: string | null };

/** A recovery URL selects a screen, never an account. Only the SDK recovery event
 * supplies the subject; a verified user is required before updating a password. */
export class AccountRecovery {
  private state: RecoveryState;
  private listeners = new Set<() => void>();
  constructor(private readonly auth: SupabaseClient['auth']) {
    const url = new URL(window.location.href);
    const hash = new URLSearchParams(url.hash.slice(1));
    const intent = url.searchParams.get('auth') === 'recovery' || hash.get('type') === 'recovery';
    const failed = url.searchParams.has('error') || hash.has('error');
    this.state = { stage: failed ? 'invalid' : intent ? 'waiting' : 'none', subject: null };
  }
  getSnapshot = () => this.state;
  subscribe = (listener: () => void) => { this.listeners.add(listener); return () => { this.listeners.delete(listener); }; };
  private set(stage: RecoveryState['stage'], subject: string | null = this.state.subject) {
    this.state = { stage, subject }; this.listeners.forEach(listener => listener());
  }
  observe(event: AuthChangeEvent, session: Session | null) {
    if (event === 'PASSWORD_RECOVERY') {
      this.set(session ? 'ready' : 'invalid', session?.user.id ?? null);
    } else if (this.state.stage === 'waiting' && event === 'INITIAL_SESSION') {
      // A failed/expired PKCE exchange must not fall back to a previously signed-in account.
      this.set('invalid', null);
    } else if (this.state.stage === 'ready' && (!session || session.user.id !== this.state.subject)) {
      this.set('invalid', null);
    }
  }
  initializationFailed() { if (this.state.stage === 'waiting') this.set('invalid', null); }
  async update(password: string) {
    const subject = this.state.stage === 'ready' ? this.state.subject : null;
    if (!subject) throw Error('recovery_expired');
    const verified = await this.auth.getUser();
    if (verified.error || verified.data.user?.id !== subject || this.state.stage !== 'ready' || this.state.subject !== subject) {
      this.set('invalid', null); throw Error('recovery_expired');
    }
    const { error } = await this.auth.updateUser({ password });
    if (error) throw error;
    if (this.state.stage !== 'ready' || this.state.subject !== subject) throw Error('recovery_expired');
    this.set('complete', subject);
  }
  finish() {
    const url = new URL(window.location.href);
    for (const key of ['auth', 'code', 'error', 'error_code', 'error_description']) url.searchParams.delete(key);
    if (new URLSearchParams(url.hash.slice(1)).has('error')) url.hash = '';
    window.history.replaceState(window.history.state, '', url.pathname + url.search + url.hash);
    this.set('none', null);
  }
}

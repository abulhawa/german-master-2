import { useEffect, useState } from 'react';

export type Appearance = 'system' | 'light' | 'dark';
export const APPEARANCE_KEY = 'german-master-v2:appearance-v1';
const valid = (value: unknown): value is Appearance => value === 'system' || value === 'light' || value === 'dark';

/** Device preference, separate from account-owned practice and identity. */
export function useAppearance(fallback: Appearance = 'system') {
  const [theme, setTheme] = useState<Appearance>(() => {
    try { const value = localStorage.getItem(APPEARANCE_KEY); return valid(value) ? value : fallback; } catch { return fallback; }
  });
  const [failed, setFailed] = useState(false);
  useEffect(() => {
    const receive = (event: StorageEvent) => { if(event.key === APPEARANCE_KEY && valid(event.newValue)) setTheme(event.newValue); };
    const changed = () => { try { const value = localStorage.getItem(APPEARANCE_KEY); if(valid(value)) setTheme(value); } catch { /* retain current appearance */ } };
    window.addEventListener('storage', receive);
    window.addEventListener('gm-appearance', changed);
    return () => { window.removeEventListener('storage', receive); window.removeEventListener('gm-appearance', changed); };
  }, []);
  function choose(value: Appearance) {
    setTheme(value);
    try { localStorage.setItem(APPEARANCE_KEY, value); setFailed(false); window.dispatchEvent(new Event('gm-appearance')); } catch { setFailed(true); }
  }
  return { theme, choose, failed };
}

import { StrictMode, useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { registerSW } from 'virtual:pwa-register';
import LearnerJourney from '../client/src/renovation/journey';
import { readJourney, browserStorage } from '../client/src/renovation/storage';

const shellCopy = {
  en: { pending: 'Saving the local preview for offline launch…', ready: 'Local preview saved for offline launch.',
    failed: 'Offline launch is unavailable. Reopen this preview while connected to retry.' },
  de: { pending: 'Die lokale Vorschau wird für den Offline-Start gespeichert…', ready: 'Die lokale Vorschau ist für den Offline-Start gespeichert.',
    failed: 'Offline-Start nicht verfügbar. Öffne diese Vorschau mit Verbindung erneut, um es noch einmal zu versuchen.' },
};

function Preview() {
  const [status, setStatus] = useState<'pending' | 'ready' | 'failed'>('pending');
  const [locale, setLocale] = useState<'en' | 'de'>(() => {
    try { return readJourney(browserStorage).locale; } catch { return 'en'; }
  });
  useEffect(() => {
    // The learner owns locale selection; reflect its actual language outside its root.
    const observer = new MutationObserver(() => {
      const lang = document.querySelector('main[lang]')?.getAttribute('lang');
      if (lang === 'en' || lang === 'de') setLocale(lang);
    });
    observer.observe(document.getElementById('root')!, { subtree: true, attributes: true, attributeFilter: ['lang'], childList: true });
    return () => observer.disconnect();
  }, []);
  useEffect(() => {
    let alive = true;
    if (!('serviceWorker' in navigator)) { setStatus('failed'); return; }
    registerSW({ immediate: true,
      onOfflineReady: () => { if (alive) setStatus('ready'); },
      onRegisterError: () => { if (alive) setStatus('failed'); },
      onRegisteredSW: (_url, registration) => {
        if (!registration && alive) setStatus('failed');
        if (registration?.active && alive) setStatus('ready');
        const installing = registration?.installing;
        installing?.addEventListener('statechange', () => {
          if (alive && installing.state === 'redundant' && !registration?.active) setStatus('failed');
        });
      },
    });
    return () => { alive = false; };
  }, []);
  return <><div className="gm-foundation" lang={locale}><p role="status" className="gm-column">{shellCopy[locale][status]}</p></div><LearnerJourney /></>;
}

const root = document.getElementById('root');
if (!root) throw Error('Missing preview root');
createRoot(root).render(<StrictMode><Preview /></StrictMode>);

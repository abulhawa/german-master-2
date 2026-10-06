import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { registerSW } from 'virtual:pwa-register';
import { configuredLearnerHost } from '../client/src/renovation/configured-host';
import { ProviderLearnerJourney } from '../client/src/renovation/provider-journey';

// Dedicated v2 artifact: never falls back to legacy auth or a fixture bearer.
let configured: ReturnType<typeof configuredLearnerHost> | undefined;
try {
  configured=import.meta.hot?.data.configured ?? configuredLearnerHost(import.meta.env.VITE_V2_AUTH_PROJECT,import.meta.env.VITE_V2_AUTH_PUBLISHABLE_KEY,import.meta.env.VITE_V2_API_ORIGIN ?? window.location.origin);
  if(import.meta.hot) import.meta.hot.data.configured=configured;
} catch { /* Fail closed; saved learner storage is untouched. */ }
registerSW({immediate:true});
const root=document.getElementById('root');
if(!root) throw Error('Missing learner root');
const app=import.meta.hot?.data.app ?? createRoot(root);
if(import.meta.hot) import.meta.hot.data.app=app;
app.render(<StrictMode>{configured
  ? <ProviderLearnerJourney host={configured.host} origin={configured.origin} deletionEnabled={import.meta.env.VITE_V2_IDENTITY_DELETION === 'enabled'}/>
  : <main className="gm-foundation"><p role="alert">German Master account configuration is unavailable.</p></main>}</StrictMode>);
import.meta.hot?.dispose(()=>configured?.host.provider.invalidate());

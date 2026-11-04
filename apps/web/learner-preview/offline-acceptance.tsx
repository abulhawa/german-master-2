// Explicit local fixture only. Not an entry point in the production build.
import {createRoot} from 'react-dom/client';
import {registerSW} from 'virtual:pwa-register';
import '../client/src/foundation/foundation.css';
import '../client/src/renovation/workspace.css';
import '../client/src/renovation/mobile-design.css';
import {OfflineDesk} from '../client/src/renovation/offline-desk';
import {localLearnerApi} from '../client/src/renovation/api';
import {browserStorage,readJourney} from '../client/src/renovation/storage';

registerSW({immediate:true});
const root=document.getElementById('root');
if(!root)throw Error('Missing acceptance root');
createRoot(root).render(<main className="gm-foundation" lang="en">
  <h1>Isolated offline-engine acceptance</h1>
  <OfflineDesk api={localLearnerApi()} locale="en" deviceId={readJourney(browserStorage).deviceId}
    questionCount={5} blocked={false} />
</main>);

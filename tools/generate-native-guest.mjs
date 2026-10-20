import fs from 'node:fs';
import vm from 'node:vm';
import { fileURLToPath } from 'node:url';

// Preserve the existing reviewed web starter identities/rubrics in Android's bundled asset.
// Evaluate only repository-owned constant expressions, never inbound content.
const root = new URL('../', import.meta.url);
const source = fs.readFileSync(new URL('apps/web/client/src/renovation/guest-starter.tsx', root), 'utf8');
const sessionExpression = source.split('const starterSession = SessionSchema.parse(')[1]?.split('\n});')[0];
const suggestionsExpression = source.split('const starterSuggestions: Record<string, Record<string,string[]>> = ')[1]?.split('\n};')[0];
const rubricSource = source.split('const starterRubrics = ')[1];
if (!sessionExpression || !suggestionsExpression || !rubricSource) throw Error('Guest source layout changed; review extraction');
const session = vm.runInNewContext(`(${sessionExpression}\n})`, {}, { timeout: 1000 });
const suggestions = vm.runInNewContext(`(${suggestionsExpression}\n})`, {}, { timeout: 1000 });
const rows = vm.runInNewContext(rubricSource.slice(rubricSource.indexOf('([') + 1, rubricSource.indexOf('\n]);') + 2)
  .replaceAll('NORMALIZATION_VERSION', '"de-nfc-trim-v1"'), {}, { timeout: 1000 });
const rubrics = rows.map(([id, rubric]) => ({ exerciseId: id.split('@')[0], exerciseRevision: Number(id.split('@')[1]), ...rubric }));
const output = JSON.stringify({ session, suggestions, rubrics }, null, 2) + '\n';
const destination = new URL('content/guest/starter.json', root);
if (process.argv.includes('--check')) {
  if (fs.readFileSync(destination, 'utf8') !== output) throw Error('Native guest asset is stale; run tools/generate-native-guest.mjs');
} else {
  fs.mkdirSync(fileURLToPath(new URL('content/guest/', root)), { recursive: true });
  fs.writeFileSync(destination, output);
}
console.log('Native guest starter matches the web starter.');

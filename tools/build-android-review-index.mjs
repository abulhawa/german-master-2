import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';

const root = path.resolve(process.argv[2] ?? '');
if (!process.argv[2] || !fs.existsSync(root)) throw Error('Pass an existing private capture directory');
const walk = dir => fs.readdirSync(dir, { withFileTypes: true }).flatMap(e => e.isDirectory() ? walk(path.join(dir, e.name)) : [path.join(dir, e.name)]);
const esc = s => String(s).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c]);
const files = walk(root);
const shots = [];
const invalid = [];
for (const file of files.filter(f => f.endsWith('.png')).sort()) {
  const data = fs.readFileSync(file);
  const relative = path.relative(root, file).replaceAll('\\', '/');
  if (data.length < 24 || data.readUInt32BE(0) !== 0x89504e47) { invalid.push(relative); continue; }
  const stem = file.replace(/--(?:viewport|lower)\.png$/, '');
  const meta = fs.existsSync(stem + '.json') ? JSON.parse(fs.readFileSync(stem + '.json', 'utf8')) : {};
  const pixel = relative.startsWith('pixel-installed/');
  shots.push({ file: relative, source: pixel ? 'Physical Pixel · installed app' : 'Native rendered fixture · production UI components',
    scenario: meta.scenario ?? path.basename(file, '.png'), locale: meta.locale ?? 'device', theme: meta.theme ?? 'device', fontScale: meta.fontScale ?? 'device',
    view: file.includes('--lower') ? 'lower page' : 'viewport', width: data.readUInt32BE(16), height: data.readUInt32BE(20),
    sha256: crypto.createHash('sha256').update(data).digest('hex') });
}
const scenarios = [...new Set(shots.filter(s => !s.file.startsWith('pixel-installed/')).map(s => s.scenario))];
const manifest = { date: '2026-10-20', screenshots: shots.length, fixtureScenarios: scenarios.length, physicalPixelScreenshots: shots.filter(s => s.file.startsWith('pixel-installed/')).length, invalidImages: invalid, shots };
fs.writeFileSync(path.join(root, 'manifest.json'), JSON.stringify(manifest, null, 2) + '\n');
const columns = ['file', 'source', 'scenario', 'locale', 'theme', 'fontScale', 'view', 'width', 'height', 'sha256'];
fs.writeFileSync(path.join(root, 'coverage.csv'), [columns, ...shots.map(s => columns.map(k => s[k]))].map(row => row.map(v => '"' + String(v).replaceAll('"', '""') + '"').join(',')).join('\n') + '\n');
const cards = shots.map(s => `<article data-search="${esc([s.scenario,s.source,s.locale,s.theme,s.view].join(' ').toLowerCase())}"><a href="${encodeURI(s.file)}" target="_blank"><img src="${encodeURI(s.file)}" loading="lazy" alt="${esc(s.scenario+' '+s.view)}"></a><div><b>${esc(s.scenario)}</b><p>${esc(s.source)}</p><p>${esc(s.locale)} · ${esc(s.theme)} · ${esc(s.view)} · ${s.width}×${s.height}</p><a href="${encodeURI(s.file)}" download>Download PNG</a></div></article>`).join('\n');
fs.writeFileSync(path.join(root, 'index.html'), `<!doctype html><html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>GM2 Android review · 20 October 2026</title><style>*{box-sizing:border-box}body{margin:0;background:#edf1f6;color:#192434;font:16px system-ui}header{padding:28px;position:sticky;top:0;background:#fff;z-index:2;border-bottom:1px solid #ccd4df}h1{margin:0 0 10px;font-size:26px}p{margin:6px 0}input{padding:12px;width:min(100%,700px);margin-top:12px;border:1px solid #637389;border-radius:8px;font:inherit}main{padding:24px;display:grid;grid-template-columns:repeat(auto-fill,minmax(250px,1fr));gap:24px}article{border:1px solid #c7d0dc;border-radius:12px;background:#fff;overflow:hidden}article img{width:100%;height:510px;object-fit:contain;object-position:top;background:#dbe2ec}article div{padding:14px;overflow-wrap:anywhere}article p{font-size:13px;color:#4a5a70}a{color:#1558a5}article[hidden]{display:none}small{display:block;margin-top:8px}</style><header><h1>German Master 2.0 · Android review</h1><p>${shots.length} screenshots · ${scenarios.length} rendered scenarios · ${manifest.physicalPixelScreenshots} physical Pixel captures</p><p>Review inventory, not a claim that every account journey passed. Fixture captures use local data; Pixel captures show the installed app.</p><input id="search" aria-label="Filter screenshots" placeholder="Filter: word_order, incorrect, register, dark, de-light, lower…"><small><a href="coverage.csv">Coverage CSV</a> · <a href="manifest.json">Image manifest and hashes</a> · <a href="REVIEW-NOTES.md">Scope and limitations</a> · <span id="count">${shots.length} shown</span></small></header><main>${cards}</main><script>const q=document.querySelector('#search');q.addEventListener('input',()=>{let n=0;document.querySelectorAll('article').forEach(a=>{a.hidden=!q.value.toLowerCase().split(/\s+/).every(w=>a.dataset.search.includes(w));if(!a.hidden)n++});document.querySelector('#count').textContent=n+' shown'})</script></html>`);
console.log(JSON.stringify({ screenshots: shots.length, scenarios: scenarios.length, pixel: manifest.physicalPixelScreenshots, invalid: invalid.length }));

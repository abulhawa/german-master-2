"""Validate hash bindings, coverage and inactive overlay preconditions."""
import json,subprocess,hashlib
from pathlib import Path
p=Path(__file__).resolve().parent;r=p.parents[2]
def load(n):return json.loads((p/n).read_text())
q=load('queue.json'); inv=load('inventory.json');assert len(q['entries'])==len(inv['completed'])==49
assert len({e['key'] for e in q['entries']})==49
for e in q['entries']:assert e['key']==e['batchId']+':'+e['catalogSha256'] and not e['candidateReady']
f=load('legacy-0001.findings.json');assert len(f['reviewed'])==12
assert set(q['entries'][0]['reviewedVariantKeys'])=={e['variantKey'] for e in f['reviewed']}
for name in ['legacy-0001.findings.json','legacy-0001.overlay.json','legacy-0002.preflight.json']:
 a=load(name);raw=subprocess.check_output(['git','show',f"{q['snapshot']}:content/enrichment/batches/{a['batchId']}.catalog.json"],cwd=r)
 assert hashlib.sha256(raw.replace(b'\r\n',b'\n')).hexdigest()==a['catalogSha256']
 if 'patches' in a:
  c=json.loads(raw);vs={v['variantKey']:v for t in c['targets'] for v in t['variants']}
  assert a['active'] is False and a['candidateReady'] is False
  for patch in a['patches']:
   v=vs[patch['variantKey']];assert v['exercise']['id']==patch['exerciseId'] and v['exercise']['prompt']==patch['expectedPrompt']
   assert patch['proposedPrompt'].count('___')==1 and '___' not in patch['proposedCompletedAnswer']
assert not load('candidate-manifest.json')['active']
print('PASS: 49 queue keys, 12 covered variants, hash bindings and 2 inactive overlay preconditions')

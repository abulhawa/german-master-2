"""Frozen input audit. Read-only source; review output lives beside this program."""
import collections, functools, hashlib, json, subprocess
from pathlib import Path
ROOT=Path(__file__).resolve().parents[3]
OUT=Path(__file__).resolve().parent
PIN='671e9d19f4f5a61d7916ed96b3b41433b768fd39'
@functools.lru_cache(maxsize=None)
def raw(p): return subprocess.check_output(['git','show',f'{PIN}:{p}'],cwd=ROOT)
def sha(b): return hashlib.sha256(b.replace(b'\r\n',b'\n')).hexdigest()
def read(p): return json.loads(raw(p))
def save(p,v): (OUT/p).write_text(json.dumps(v,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
manifest=read('content/enrichment/SOURCE-MANIFEST.json')
sourceChecks=[dict(path=f['path'],expected=f['sha256'],actual=sha(raw(f['path']))) for f in manifest['files']]
assert all(x['expected']==x['actual'] for x in sourceChecks)
plan=read('content/enrichment/plan.json'); inventory=[]; excluded=[]; errors=[]
available=set(subprocess.check_output(['git','ls-tree','-r','--name-only',PIN,'content/enrichment/batches'],cwd=ROOT).decode().splitlines())
seen={k:{} for k in ['exerciseId','variantKey','contextKey','transferKey','prompt','objective']}; duplicates=[]
for b in plan['batches']:
 id=b['id']; base=f'content/enrichment/batches/{id}'
 if base+'.report.json' not in available:continue
 r=read(base+'.report.json')
 if r['state']!='ready-for-review': excluded.append({'batchId':id,'state':r['state']});continue
 c=read(base+'.catalog.json'); h=sha(raw(base+'.catalog.json')); assert h==r['catalogSha256']
 assert c['publicationApproved'] is False and c['status']=='agent-authored-draft'
 inp=read(f'content/enrichment/inputs/{id}.json'); by={x['sourceId']:x for x in inp['records']}
 assert len(r['rows'])==len(b['sourceLines']) and len(by)==len(r['rows'])
 for row in r['rows']:
  x=by[row['sourceId']]; actual=hashlib.sha256(json.dumps(x['record'],ensure_ascii=False,separators=(',',':')).encode()).hexdigest()
  assert actual==x['sourceHash']==row['sourceHash']
  line=int(row['sourceId'].split(':L')[1]);record=json.loads(raw(b['sourceFile']).decode().splitlines()[line-1]);assert record==x['record']
  assert row['decision']!='pending' and row['rightsBasis'].strip()
  if row['decision']=='authored':assert len(row['targetIds'])==1
 checks=collections.Counter();mapping=[]
 for t in c['targets']:
  assert t['review']['status']=='pending' and t['level'] in ['B1','B2'] and len(t['variants'])==2
  assert any(t['id'] in row['targetIds'] for row in r['rows'])
  mapping.append({'targetId':t['id'],'category':t['category'],'levelClaim':t['level'],'topic':t.get('topic'),'learningContext':t.get('learningContext'),'objective':t['objective']})
  entries=[('objective',t['objective'])]
  for v in t['variants']:
   e=v['exercise'];rub=v['rubric'];assert e['targetId']==t['id'] and e['revision']==1
   assert rub['normalizationVersion']=='de-nfc-trim-v1' and rub['acceptedAnswers']
   for field in ['instruction','hint']: assert all(e[field][lang].strip() for lang in ['en','de'])
   assert all(rub['explanation'][lang].strip() for lang in ['en','de'])
   assert rub['completedAnswer']['parts']
   if e['type']=='choice':
    ids={o['id'] for o in e['options']};assert all(a['type']=='choice' and a['optionId'] in ids for a in rub['acceptedAnswers'])
   elif e['type']=='gap_choice':
    slots={s['id']:{o['id'] for o in s['options']} for s in e['slots']}
    for a in rub['acceptedAnswers']:
     assert a['type']=='gap_choice' and {s['slotId'] for s in a['selections']}==set(slots)
     assert all(s['optionId'] in slots[s['slotId']] for s in a['selections'])
   else:checks['otherFormatNeedsCanonicalValidation']+=1
   checks[e['type']]+=1
   entries += [('exerciseId',e['id']),('prompt',e['prompt'].strip()),*[(k,v[k]) for k in ['variantKey','contextKey','transferKey']]]
  for kind,value in entries:
   if value in seen[kind]:duplicates.append({'kind':kind,'value':value,'firstBatch':seen[kind][value],'batchId':id})
   else:seen[kind][value]=id
 inventory.append({'batchId':id,'catalogSha256':h,'reportSha256':sha(raw(base+'.report.json')),'inputSha256':sha(raw(f'content/enrichment/inputs/{id}.json')),'authorState':r['state'],'targets':len(c['targets']),'questions':sum(len(t['variants']) for t in c['targets']),'sourceDecisions':dict(collections.Counter(row['decision'] for row in r['rows'])),'formats':dict(checks),'mapping':mapping})
save('inventory.json',{'snapshot':PIN,'sourceManifestChecks':sourceChecks,'completed':inventory,'excluded':excluded,'exactDuplicatesWithinSnapshot':duplicates,'limitations':['Canonical TypeScript rubric validator not run by this Python audit.','Semantic duplicates, prior objective mapping, CEFR and rights require editorial review.','Null topic/context are unresolved integration mapping, not inferred assignments.']})
old=json.loads((OUT/'queue.json').read_text()) if (OUT/'queue.json').exists() else {'entries':[]}
prior={x['key']:x for x in old['entries']}
queue=[]
for x in inventory:
 key=x['batchId']+':'+x['catalogSha256'];queue.append(prior.get(key,{'key':key,'batchId':x['batchId'],'catalogSha256':x['catalogSha256'],'state':'pending','reviewedVariantKeys':[],'candidateReady':False}))
save('queue.json',{'snapshot':PIN,'entries':queue})
print(json.dumps({'completedBatches':len(inventory),'questions':sum(x['questions'] for x in inventory),'targets':sum(x['targets'] for x in inventory),'excluded':excluded,'duplicates':len(duplicates),'sourceFilesVerified':len(sourceChecks)}))

"""Export pinned review evidence into a derived candidate; never write review/authoring branches."""
import hashlib,json,pathlib,subprocess
REVIEW='71064b6bde57f1b2cf88d36885521545cd9f4003'
OUT=pathlib.Path('content/candidates/enrichment-wave-2/evidence')
def raw(ref,path):return subprocess.check_output(['git','show',ref+':'+path]).replace(b'\r\n',b'\n')
def read(name):return json.loads(raw(REVIEW,'content/review/enrichment/'+name))
names=set(subprocess.check_output(['git','ls-tree','-r','--name-only',REVIEW,'content/review/enrichment']).decode().splitlines())
index={}
def save(name,ref,path):
 data=raw(ref,path);dest=OUT/name;dest.parent.mkdir(parents=True,exist_ok=True);dest.write_bytes(data)
 index[name]={'commit':ref,'path':path,'sha256':hashlib.sha256(data).hexdigest()}
for name in ['queue.json','extension-queue.json','coverage.json','extension-coverage.json','correction-rechecks.json']:
 save('review/'+name,REVIEW,'content/review/enrichment/'+name)
count=0
for queue in [read('queue.json'),read('extension-queue.json')]:
 for entry in queue['entries']:
  if entry['state']!='complete':continue
  batch=entry['batchId'];source=f'content/enrichment/batches/{batch}.catalog.json'
  data=raw(queue['snapshot'],source)
  assert hashlib.sha256(data).hexdigest()==entry['catalogSha256']
  save('source/'+batch+'.catalog.json',queue['snapshot'],source)
  save('review/'+batch+'.findings.json',REVIEW,'content/review/enrichment/'+batch+'.findings.json')
  for suffix in [batch+'.overlay.json','extension-proposals/'+batch+'.overlay.json']:
   path='content/review/enrichment/'+suffix
   if path in names:save('review/'+suffix,REVIEW,path)
  count+=1
blob=(json.dumps(index,sort_keys=True,indent=2)+'\n').encode();OUT.mkdir(parents=True,exist_ok=True);(OUT/'index.json').write_bytes(blob)
print(json.dumps({'batches':count,'evidenceFiles':len(index),'indexSha256':hashlib.sha256(blob).hexdigest()}))

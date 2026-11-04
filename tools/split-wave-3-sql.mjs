import {readFileSync,writeFileSync,mkdirSync,readdirSync} from 'node:fs';
import {createHash} from 'node:crypto';
const root='content/candidates/enrichment-wave-3';
const destination='ops/wave-3-sql-parts';
const sha256=text=>createHash('sha256').update(text.replaceAll('\r\n','\n')).digest('hex');
const sql=readFileSync(root+'/seed.sql','utf8').replaceAll('\r\n','\n');
const manifest=JSON.parse(readFileSync(root+'/manifest.json','utf8'));
if(sha256(sql)!==manifest.seedSha256) throw Error('Frozen seed SQL SHA mismatch');
if(manifest.releaseId!=='92000000-0000-4000-8000-000000000001'||
   manifest.previousReleaseId!=='91000000-0000-4000-8000-000000000001'||
   manifest.newQuestions!==3006||manifest.readOnlyCandidate!==true) throw Error('Unexpected candidate');
const lines=sql.split('\n'),first=lines.indexOf('BEGIN;'),last=lines.lastIndexOf('COMMIT;');
if(first<0||last<0||first>=last||lines.slice(0,first).some(x=>x.trim()&&!x.startsWith('--'))||
   lines.slice(last+1).some(x=>x.trim()))throw Error('Unexpected SQL transaction shell');
const statements=lines.slice(first+1,last);
if(statements.some(x=>!/^INSERT INTO gm\.[a-z_]+ \(.+\) VALUES \(.+\);$/.test(x)))throw Error('Seed has multiline or unrecognized SQL statements');
if(statements.some(x=>/\b(UPDATE|DELETE|TRUNCATE|DROP|ALTER)\b/i.test(x.match(/^INSERT INTO gm\.\w+/)?.[0]??''))) throw Error('Seed operation prohibited');
const oldHash='e1a05398d099fcd0f7a5a2ebaa73bf8bc7e554cebfc0e16e2b84a1ca7b467722';
const prefix=[
  'BEGIN;',
  "SET LOCAL statement_timeout = '90s';",
  "SELECT pg_advisory_xact_lock(hashtext('german-master-wave3-import'));",
  "DO $$BEGIN IF (SELECT count(*) FROM gm.content_release_exercise WHERE release_id='91000000-0000-4000-8000-000000000001') <> 2613 OR NOT EXISTS (SELECT 1 FROM gm.content_release WHERE id='91000000-0000-4000-8000-000000000001' AND manifest_hash='"+oldHash+"' AND status='published') THEN RAISE EXCEPTION 'Prior wave 2 release not exact'; END IF; END$$;"
];
mkdirSync(destination,{recursive:true});
if(readdirSync(destination).length!==0)throw Error('SQL parts directory must start empty');
const parts=[];let group=[],bytes=0;
const emit=()=>{
 if(!group.length)return;
 const number=parts.length+1;
 const name='part-'+String(number).padStart(3,'0')+'.sql';
 const body=[...prefix,...group,'COMMIT;', "SELECT 'wave3-part-"+String(number).padStart(3,'0')+"' AS applied;"].join('\n')+'\n';
 writeFileSync(destination+'/'+name,body,'utf8');
 parts.push({name,sha256:sha256(body),statements:group.length,first:group[0].slice(0,70),last:group.at(-1).slice(0,70),bytes:Buffer.byteLength(body)});
 group=[];bytes=0;
};
for(const stmt of statements){
 const n=Buffer.byteLength(stmt+'\n','utf8');
 if(group.length>0&&bytes+n>235000)emit();
 group.push(stmt);bytes+=n;
}
emit();
if(parts.reduce((n,p)=>n+p.statements,0)!==statements.length)throw Error('Incomplete output');
const result={schemaVersion:1,reviewCommit:manifest.reviewCommit,releaseId:manifest.releaseId,priorReleaseId:manifest.previousReleaseId,manifestHash:manifest.manifestHash,seedSha256:manifest.seedSha256,expectedPrevious:2613,expectedNew:3006,expectedTotal:5619,statements:statements.length,parts};
writeFileSync(destination+'/index.json',JSON.stringify(result,null,2)+'\n');
console.log(JSON.stringify({sqlSha256:result.seedSha256,statements:statements.length,parts:parts.length,totalBytes:parts.reduce((n,p)=>n+p.bytes,0)}));

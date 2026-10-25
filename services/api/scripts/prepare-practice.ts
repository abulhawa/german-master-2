import {readFileSync,writeFileSync,mkdirSync} from 'node:fs';
import {PGlite} from '@electric-sql/pglite';
import {FoundationStore} from '../src/store';
import {runtimeMembers,RuntimeCatalogSchema} from '../src/runtime-catalog';
import {buildBasicCandidate} from '../src/basic-content';
import {validateDraftCatalog} from '../../../packages/learning-engine/src/content-review';
import {SessionSchema} from '@german-master/contracts';
const read=(p:string)=>JSON.parse(readFileSync(new URL(p,import.meta.url),'utf8'));
const work=read('../../../content/drafts/workplace.json');
const reviewed=validateDraftCatalog(work);
const names=[['colleagues','Colleagues','Kollegen'],['schedules','Schedules','Dienstpläne'],['requests','Requests','Bitten'],['meetings','Meetings','Besprechungen'],['emails','Workplace emails','E-Mails im Beruf']];
const db=new PGlite();
try {
 await new FoundationStore(db).initialize();
 await db.exec(readFileSync(new URL('../../../db/seed/production-starter.sql',import.meta.url),'utf8'));
 await db.exec(readFileSync(new URL('../../../db/seed/production-basics.sql',import.meta.url),'utf8'));
 const previous=RuntimeCatalogSchema.parse(read('../../../content/production/basics-catalog.json'));
 const members=await runtimeMembers(db,previous.releaseId);
 const candidate=buildBasicCandidate(work,previous,members,undefined,{
  releaseId:'60000000-0000-4000-8000-000000000001',prefix:'60000000',learningContext:'work',
  topics:names.map(([key,en,de])=>({key,title:{en,de}})),
  topicForTarget:id=>names.findIndex(([key])=>work.targets.find((t:any)=>t.id===id).variants[0].contextKey.startsWith(`work-${key}-`))
 });
 const directory=new URL('../../../content/candidates/workplace/',import.meta.url);mkdirSync(directory,{recursive:true});
 writeFileSync(new URL('catalog.json',directory),JSON.stringify(candidate.config,null,2)+'\n');
 writeFileSync(new URL('seed.sql',directory),candidate.sql);
 const groups=new Map<string,any>();
 for(const m of candidate.members) {
  const meta=candidate.config.targets.find(t=>t.id===m.target_id)!;
  const context=meta.learningContext??'general';const key=`${m.level}:${context}:${m.topic_id}`;
  if(!groups.has(key))groups.set(key,{level:m.level,learningContext:context,topicId:m.topic_id,title:m.topic_title,
    session:{apiVersion:'v2',id:m.topic_id,contentReleaseId:context==='work'?candidate.config.releaseId:previous.releaseId,questions:[]},rubrics:[],suggestions:{}});
  const group=groups.get(key);group.session.questions.push({id:m.exercise_id,exercise:m.payload});
  group.rubrics.push({exerciseId:m.exercise_id,exerciseRevision:m.revision,...m.rubric as object});
 }
 // Existing short-answer starter suggestions are retained for those exact revisions.
 const starter=read('../../../content/guest/starter.json');
 for(const g of groups.values()){g.suggestions=starter.suggestions;SessionSchema.parse({...g.session,questions:g.session.questions.slice(0,50)});}
 writeFileSync(new URL('../../../content/guest/practice.json',import.meta.url),JSON.stringify({packs:[...groups.values()]},null,2)+'\n');
 console.log(`Prepared ${reviewed.variants} workplace exercises, ${reviewed.editoriallyApproved} AI-reviewed targets; shared bundle and unpublished SQL only.`);
} finally {await db.close();}

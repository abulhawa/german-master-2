import { validateDraftCatalog } from '../../../packages/learning-engine/src/content-review';
import { createHash } from 'node:crypto';
import { z } from 'zod';
import { runtimeManifestHash, RuntimeCatalogSchema, type RuntimeCatalog, type RuntimeMember } from './runtime-catalog';

const releaseId = '50000000-0000-4000-8000-000000000001';
const authorizationSchema=z.strictObject({contentHash:z.string().regex(/^[a-f0-9]{64}$/),
  authorizedBy:z.string().trim().min(1),authorizedAt:z.string().datetime({offset:true}),
  authorization:z.string().trim().min(1),independentReview:z.literal('pending')});
export type BasicPublicationAuthorization=z.infer<typeof authorizationSchema>;
export function basicContentHash(input:unknown,previous:RuntimeCatalog) {
  const {targetHashes}=validateDraftCatalog(input);
  return createHash('sha256').update(JSON.stringify({previousManifest:previous.manifestHash,
    targets:[...targetHashes].sort(([a],[b])=>a.localeCompare(b))})).digest('hex');
}
const id = (n:number) => `50000000-0000-4000-8000-${String(n).padStart(12,'0')}`;
const topics = [
  {key:'nouns',title:{en:'Nouns and plurals',de:'Nomen und Plural'}},
  {key:'cases',title:{en:'Cases and adjective endings',de:'Fälle und Adjektivendungen'}},
  {key:'verbs',title:{en:'Verbs and prepositions',de:'Verben und Präpositionen'}},
  {key:'sentences',title:{en:'Sentence structure',de:'Satzbau'}},
  {key:'advanced',title:{en:'Passive and conditionals',de:'Passiv und Konjunktiv'}},
  {key:'work',title:{en:'German at work',de:'Deutsch im Beruf'}},
];
function topicIndex(category:string) {
  if(category==='plural') return 0;
  if(['article','case','cases','adjective'].includes(category)) return 1;
  if(['verb','preposition','verb_preposition'].includes(category)) return 2;
  if(['word_order','word-order','connectors','infinitive','relative'].includes(category)) return 3;
  if(['passive','conditional'].includes(category)) return 4;
  if(category==='work_vocabulary') return 5;
  throw Error(`Unmapped content category: ${category}`);
}
const literal = (value:unknown) => value===null?'NULL':`'${(typeof value==='object'?JSON.stringify(value):String(value)).replaceAll("'","''")}'`;
function insert(table:string,columns:string[],values:unknown[]) {
  return `INSERT INTO gm.${table} (${columns.join(',')}) VALUES (${values.map(literal).join(',')});`;
}

/** Builds an additive draft or content-authorized release, with no connection side effects.
 * Existing revisions and release membership are referenced, never replaced. */
export function buildBasicCandidate(input:unknown, previous:RuntimeCatalog, previousMembers:RuntimeMember[], publication?:BasicPublicationAuthorization) {
  const {catalog, targets:targetCount, variants} = validateDraftCatalog(input);
  RuntimeCatalogSchema.parse(previous);
  if(publication) {
    authorizationSchema.parse(publication);
    if(publication.contentHash!==basicContentHash(input,previous)) throw Error('Publication authorization does not match content');
  }
  const targetStatus=publication?'published':'draft', reviewStatus=publication?'approved':'pending';
  if(previousMembers.some(m=>m.review_status!=='approved'||m.status!=='published') ||
    runtimeManifestHash(previous.releaseId,previous.targets,previousMembers)!==previous.manifestHash)
    throw Error('Previous release must match its published immutable manifest');
  const targetIds = new Set(previous.targets.map(t=>t.id));
  const exerciseIds = new Set(previousMembers.map(m=>m.exercise_id));
  const sql = [publication?'-- Owner-authorized additive basic release. GPT-6 AI editorial review is tracked in source; no independent human review required.':
    '-- Unpublished additive candidate. Run only in an isolated review database.',
    '-- No learner writes, UPDATE, DELETE or replacement of existing revisions.', 'BEGIN;'];
  topics.forEach((topic,i)=>sql.push(insert('topic',['id','title'],[id(600+i),topic.title])));
  const targets = [...previous.targets];
  const members = [...previousMembers].sort((a,b)=>a.exercise_id.localeCompare(b.exercise_id)||a.revision-b.revision);
  catalog.targets.forEach((target,n)=> {
    if(targetIds.has(target.id)) throw Error('Target collides with previous release');
    targetIds.add(target.id);
    const topic = topicIndex(target.category), topicId=id(600+topic), skillId=id(2000+n);
    targets.push({id:target.id,topicId,title:target.title,description:target.title,level:target.level});
    sql.push(insert('skill',['id','topic_id','title','parent_id'],[skillId,topicId,target.objective,null]),
      insert('learning_target',['id','skill_id','kind','level','objective','status'],[target.id,skillId,
        target.category==='plural'||target.category==='work_vocabulary'?'lexical':'grammar',target.level,target.objective,targetStatus]));
    target.variants.forEach(v=> {
      const e=v.exercise;
      if(exerciseIds.has(e.id)) throw Error('Exercise collides with previous release');
      exerciseIds.add(e.id);
      const provenance=target.provenance+(publication?` Owner-authorized basic release by ${publication.authorizedBy} at ${publication.authorizedAt}; GPT-6 AI editorial/rubric checks; independent human review not required by owner. Authorization: ${publication.authorization}`:'');
      const member:RuntimeMember = {exercise_id:e.id,revision:e.revision,target_id:target.id,topic_id:topicId,
        level:target.level,kind:target.category==='plural'||target.category==='work_vocabulary'?'lexical':'grammar',
        objective:target.objective,topic_title:topics[topic].title,status:targetStatus,review_status:reviewStatus,payload:e,
        rubric:v.rubric,normalization_version:v.rubric.normalizationVersion,provenance,
        variant_key:v.variantKey,context_key:v.contextKey,transfer_key:v.transferKey};
      members.push(member);
      sql.push(insert('exercise',['id','target_id'],[e.id,target.id]),
        insert('exercise_revision',['exercise_id','revision','type','payload','rubric','normalization_version','provenance','review_status'],
          [e.id,e.revision,e.type,e,v.rubric,v.rubric.normalizationVersion,provenance,reviewStatus]),
        insert('revision_evidence_identity',['exercise_id','revision','variant_key','context_key','transfer_key'],
          [e.id,e.revision,v.variantKey,v.contextKey,v.transferKey]));
    });
  });
  const manifestHash=runtimeManifestHash(releaseId,targets,members);
  const config=RuntimeCatalogSchema.parse({releaseId,manifestHash,targets});
  sql.push(insert('content_release',['id','status','manifest_hash','published_at'],[releaseId,targetStatus,manifestHash,publication?.authorizedAt??null]));
  members.forEach(m=>sql.push(insert('content_release_exercise',['release_id','exercise_id','revision'],[releaseId,m.exercise_id,m.revision])));
  sql.push('COMMIT;','');
  return {config,members,sql:sql.join('\n'),newTargets:targetCount,newVariants:variants};
}

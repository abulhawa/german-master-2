import { z } from 'zod';
import { CatalogTargetSchema } from '@german-master/contracts';
import { createHash } from 'node:crypto';
import type { SqlTransaction } from './database';

/** Supplied by reviewed release configuration, never inferred from fixture IDs.
 * The manifest hash also covers these learner-facing target descriptions. */
export const RuntimeCatalogSchema = z.strictObject({
  releaseId: z.string().uuid(),
  manifestHash: z.string().regex(/^[a-f0-9]{64}$/),
  targets: z.array(CatalogTargetSchema.omit({ availableQuestionCount: true })).min(1)
    .refine(values => new Set(values.map(v => v.id)).size === values.length, 'Duplicate target metadata'),
});
export type RuntimeCatalog = z.infer<typeof RuntimeCatalogSchema>;
export type RuntimeMember = { exercise_id:string;revision:number;target_id:string;topic_id:string;level:string;kind:string;objective:string;topic_title:unknown;status:string;review_status:string;payload:unknown;rubric:unknown;normalization_version:string;provenance:string;variant_key:string;context_key:string;transfer_key:string|null };
export async function runtimeMembers(db: Pick<SqlTransaction,'query'>, releaseId: string) {
  return (await db.query<RuntimeMember>(`SELECT r.exercise_id,r.revision,e.target_id,s.topic_id,t.level,t.kind,t.objective,tp.title AS topic_title,t.status,
    r.review_status,r.payload,r.rubric,r.normalization_version,r.provenance,i.variant_key,i.context_key,i.transfer_key
    FROM gm.content_release_exercise cr JOIN gm.exercise_revision r ON r.exercise_id=cr.exercise_id AND r.revision=cr.revision
    JOIN gm.exercise e ON e.id=r.exercise_id JOIN gm.learning_target t ON t.id=e.target_id JOIN gm.skill s ON s.id=t.skill_id
    JOIN gm.topic tp ON tp.id=s.topic_id JOIN gm.revision_evidence_identity i ON i.exercise_id=r.exercise_id AND i.revision=r.revision
    WHERE cr.release_id=$1`,[releaseId])).rows;
}
export function runtimeManifestHash(releaseId: string, targets: RuntimeCatalog['targets'], members: RuntimeMember[]) {
  function ordered(value: unknown): unknown {
    if(Array.isArray(value)) return value.map(ordered);
    if(value && typeof value === 'object') return Object.fromEntries(Object.entries(value).sort(([a],[b])=>a.localeCompare(b)).map(([k,v])=>[k,ordered(v)]));
    return value;
  }
  return createHash('sha256').update(JSON.stringify(ordered({releaseId,
    targets:[...targets].sort((a,b)=>a.id.localeCompare(b.id)),
    revisions:[...members].sort((a,b)=>a.exercise_id.localeCompare(b.exercise_id)||a.revision-b.revision),
  }))).digest('hex');
}

import type {SqlDatabase} from './database';
import {runtimeMembers,runtimeManifestHash,type RuntimeCatalog} from './runtime-catalog';

/** Exact replay succeeds; collisions fail closed. Validate before committing.
 * Rollback means restoring the previous runtime config, retaining revisions for saved sessions. */
export async function importEnrichment(db:SqlDatabase,config:RuntimeCatalog,sql:string) {
 const existing=await db.query<{manifest_hash:string}>('SELECT manifest_hash FROM gm.content_release WHERE id=$1',[config.releaseId]);
 if(existing.rows.length){
  if(existing.rows[0].manifest_hash!==config.manifestHash||runtimeManifestHash(config.releaseId,config.targets,await runtimeMembers(db,config.releaseId))!==config.manifestHash)
   throw Error('Existing enrichment release differs; never overwrite immutable content');
  return 'already-installed' as const;
 }
 if(!/^BEGIN;$/m.test(sql)||!sql.endsWith('COMMIT;\n'))throw Error('Expected generated transactional SQL');
 await db.transaction(async tx=>{
  await tx.exec(sql.replace(/^BEGIN;$/m,'').replace(/COMMIT;\n$/,''));
  if(runtimeManifestHash(config.releaseId,config.targets,await runtimeMembers(tx,config.releaseId))!==config.manifestHash)
   throw Error('Installed enrichment manifest mismatch');
 });
 return 'installed' as const;
}

import {initialSchemaSection} from './initial-schema';
import {it,expect,vi} from 'vitest';
import {PGlite} from '@electric-sql/pglite';
import {randomUUID,randomBytes} from 'node:crypto';
import {FoundationStore} from './store';
import {IdentityDeletionService,type IdentityDeletionProvider} from './identity-deletion';

async function fixture() {
  const db=new PGlite();const store=new FoundationStore(db);await store.initialize();
  await db.exec(await initialSchemaSection('identity-deletion.sql'));
  const subject=randomUUID(),other=randomUUID(),requestId=randomUUID(),capability=randomBytes(32).toString('base64url');
  await store.profile(subject);await store.profile(other);
  let now=Date.parse('2026-10-05T12:00:00Z');let exists=true,sessions=true;
  const provider:IdentityDeletionProvider={
    inspect:vi.fn(async()=>({exists,activeSessions:sessions})),
    revokeAll:vi.fn(async()=>{sessions=false;}),remove:vi.fn(async()=>{exists=false;}),
  };
  const verify=vi.fn(async()=>({subject,authenticatedAt:now}));
  const service=()=>new IdentityDeletionService(db,()=>store,provider,verify,()=>now);
  return {db,store,subject,other,requestId,capability,provider,verify,service,
    advance:(ms:number)=>{now+=ms;},setIdentity:(value:boolean)=>{exists=value;},
    setSessions:(value:boolean)=>{sessions=value;}};
}

it('requires fresh matching server proof before persisting anything or touching identities',async()=>{
  const f=await fixture();try {
    f.verify.mockResolvedValue({subject:f.other,authenticatedAt:Date.parse('2026-10-05T12:00:00Z')});
    await expect(f.service().begin(f.subject,f.requestId,f.capability,{})).rejects.toMatchObject({code:'fresh_authentication_required'});
    f.verify.mockResolvedValue({subject:f.subject,authenticatedAt:Date.parse('2026-10-05T12:00:00Z')});f.advance(5*60*1000);
    await expect(f.service().begin(f.subject,f.requestId,f.capability,{})).rejects.toMatchObject({code:'fresh_authentication_required'});
    expect((await f.db.query('SELECT * FROM gm_privacy.identity_deletion')).rows).toEqual([]);
    expect(f.provider.inspect).not.toHaveBeenCalled();expect((await f.store.profile(f.subject)).setupCompleted).toBe(false);
  }finally{await f.db.close();}
});

it('persists a pending job, isolates recovery and completes learning removal before identity mutation',async()=>{
  const f=await fixture();try {
    expect(await f.service().begin(f.subject,f.requestId,f.capability,{})).toMatchObject({status:'pending',completedAt:null});
    expect(await f.service().begin(f.subject,f.requestId,f.capability,{})).toMatchObject({status:'pending'});
    await expect(f.service().begin(f.subject,randomUUID(),f.capability,{})).rejects.toMatchObject({code:'deletion_conflict'});
    await expect(f.service().recover(f.requestId,randomBytes(32).toString('base64url'))).rejects.toMatchObject({code:'receipt_unavailable'});
    expect(JSON.stringify((await f.db.query('SELECT * FROM gm_privacy.identity_deletion')).rows)).not.toContain(f.capability);
    f.provider.remove=vi.fn(async()=>{
      await expect(f.store.profile(f.subject)).rejects.toMatchObject({code:'account_deleted'});
      expect((await f.store.profile(f.other)).setupCompleted).toBe(false);
      f.setIdentity(false);
    });
    const receipt=await f.service().continue(f.requestId);expect(receipt.status).toBe('identity_deleted');
    expect(receipt).not.toHaveProperty('subject');expect(f.provider.revokeAll).toHaveBeenCalledWith(f.subject);
    expect(await f.service().recover(f.requestId,f.capability)).toEqual(receipt);
    expect(await f.service().continue(f.requestId)).toEqual(receipt);expect(f.provider.remove).toHaveBeenCalledTimes(1);
    expect((await f.db.query('SELECT * FROM gm.exercise_revision')).rows).toHaveLength(5);
  }finally{await f.db.close();}
});

it('recovers deletion response loss after restart without falsely completing or repeating provider removal',async()=>{
  const f=await fixture();try {
    await f.service().begin(f.subject,f.requestId,f.capability,{});
    f.provider.remove=vi.fn(async()=>{f.setIdentity(false);throw Error('ambiguous timeout');});
    await expect(f.service().continue(f.requestId)).rejects.toThrow('ambiguous timeout');
    expect(await f.service().recover(f.requestId,f.capability)).toMatchObject({status:'pending'});
    await expect(f.store.profile(f.subject)).rejects.toMatchObject({code:'account_deleted'});
    expect((await f.service().continue(f.requestId)).status).toBe('identity_deleted');
    expect(f.provider.remove).toHaveBeenCalledTimes(1);
  }finally{await f.db.close();}
});

it('never interprets provider failures or remaining sessions as completed deletion',async()=>{
  const f=await fixture();try {
    await f.service().begin(f.subject,f.requestId,f.capability,{});
    f.provider.inspect=vi.fn(async()=>{throw Error('offline');});
    await expect(f.service().continue(f.requestId)).rejects.toThrow('offline');expect(f.provider.remove).not.toHaveBeenCalled();
    f.provider.inspect=vi.fn(async()=>({exists:false,activeSessions:true}));
    await expect(f.service().continue(f.requestId)).rejects.toMatchObject({code:'identity_deletion_pending'});
    expect(await f.service().recover(f.requestId,f.capability)).toMatchObject({status:'pending'});
  }finally{await f.db.close();}
});

it('blocks identity mutation on failed learning deletion and expires only completed recovery hashes',async()=>{
  const f=await fixture();try {
    await f.service().begin(f.subject,f.requestId,f.capability,{});
    await f.db.exec('CREATE TRIGGER fail_delete BEFORE DELETE ON gm.learner_profile FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation()');
    await expect(f.service().continue(f.requestId)).rejects.toThrow();expect(f.provider.inspect).not.toHaveBeenCalled();
    await f.db.exec('DROP TRIGGER fail_delete ON gm.learner_profile');
    f.advance(31*24*60*60*1000);await f.service().expireRecoveryCapabilities();
    expect(await f.service().recover(f.requestId,f.capability)).toMatchObject({status:'pending'});
    const receipt=await f.service().continue(f.requestId);
    f.advance(30*24*60*60*1000-1);expect(await f.service().recover(f.requestId,f.capability)).toEqual(receipt);
    f.advance(1);await expect(f.service().recover(f.requestId,f.capability)).rejects.toMatchObject({code:'receipt_unavailable'});
    await f.service().expireRecoveryCapabilities();await f.service().expireRecoveryCapabilities();
    expect((await f.db.query('SELECT recovery_hash FROM gm_privacy.identity_deletion')).rows).toEqual([{recovery_hash:null}]);
    expect(await f.service().continue(f.requestId)).toEqual(receipt);
    await expect(f.store.profile(f.subject)).rejects.toMatchObject({code:'account_deleted'});
  }finally{await f.db.close();}
});

it('keeps private job capabilities away from learning and client roles',async()=>{
  const f=await fixture();try {
    await f.db.exec(await initialSchemaSection('backend-access.sql'));
    await f.db.exec('CREATE ROLE privacy_client NOLOGIN');
    for(const role of ['gm_backend','privacy_client']) {
      expect((await f.db.query(`SELECT has_schema_privilege($1,'gm_privacy','USAGE') AS allowed`,[role])).rows).toEqual([{allowed:false}]);
    }
    await f.db.transaction(async tx=>{
      await tx.exec('SET LOCAL ROLE gm_privacy_worker');
      await tx.query(`INSERT INTO gm_privacy.identity_deletion(subject,request_id,recovery_hash,requested_at) VALUES($1,$2,$3,$4)`,
        [f.subject,f.requestId,'a'.repeat(64),'2026-10-05T12:00:00Z']);
      expect((await tx.query('SELECT * FROM gm_privacy.identity_deletion')).rows).toHaveLength(1);
    });
    await expect(f.db.transaction(async tx=>{await tx.exec('SET LOCAL ROLE gm_privacy_worker');await tx.query('SELECT * FROM gm.learner_profile');})).rejects.toThrow('permission denied');
    await expect(f.db.transaction(async tx=>{await tx.exec('SET LOCAL ROLE gm_privacy_worker');await tx.query('DELETE FROM gm_privacy.identity_deletion');})).rejects.toThrow('permission denied');
  }finally{await f.db.close();}
});

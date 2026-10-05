import {it,expect,vi} from 'vitest';
import {PGlite} from '@electric-sql/pglite';
import {readFile} from 'node:fs/promises';
import {randomUUID} from 'node:crypto';
import {identityPresence,supabaseIdentityDeletionProvider,createSupabaseIdentityDeletionProvider} from './supabase-identity-deletion';
import {IdentityDeletionService} from './identity-deletion';
import {FoundationStore} from './store';

it('reads only identity/session presence through a separate role and observes revocation after hard deletion',async()=>{
  const db=new PGlite();const subject=randomUUID(),requestId=randomUUID(),other=randomUUID();
  try {
    const store=new FoundationStore(db);await store.initialize();
    await db.exec(await readFile(new URL('../../../db/baseline/identity-deletion.sql',import.meta.url),'utf8'));
    await db.exec(`CREATE SCHEMA auth; CREATE TABLE auth.users(id uuid PRIMARY KEY,email text);
      CREATE TABLE auth.sessions(id uuid PRIMARY KEY,user_id uuid REFERENCES auth.users ON DELETE CASCADE,refresh_secret text)`);
    await db.exec('ALTER TABLE auth.users ENABLE ROW LEVEL SECURITY; ALTER TABLE auth.sessions ENABLE ROW LEVEL SECURITY');
    await db.exec(await readFile(new URL('../../../db/baseline/identity-verifier-access.sql',import.meta.url),'utf8'));
    await db.query('INSERT INTO auth.users VALUES($1,$2),($3,$4)',[subject,'synthetic@example.invalid',other,'other@example.invalid']);
    await db.query('INSERT INTO auth.sessions VALUES($1,$2,$3)',[randomUUID(),subject,'synthetic-secret']);
    const scoped={query:<T>(sql:string,values?:unknown[])=>db.transaction(async tx=>{
      await tx.exec('SET LOCAL ROLE gm_identity_verifier');return tx.query<T>(sql,values);
    })};
    const presence=identityPresence(scoped);
    expect(await presence(subject)).toEqual({exists:true,activeSessions:true});
    await expect(scoped.query('SELECT email FROM auth.users')).rejects.toThrow('permission denied');
    await expect(scoped.query('SELECT refresh_secret FROM auth.sessions')).rejects.toThrow('permission denied');
    await expect(scoped.query('DELETE FROM auth.sessions')).rejects.toThrow('permission denied');
    await expect(scoped.query('SELECT * FROM gm.learner_profile')).rejects.toThrow('permission denied');
    const deleteUser=vi.fn(async(id:string,soft:boolean)=>{
      expect(soft).toBe(false);await db.query('DELETE FROM auth.users WHERE id=$1',[id]);return {error:null};
    });
    const provider=supabaseIdentityDeletionProvider({deleteUser},presence);
    const service=new IdentityDeletionService(db,()=>store,provider,async()=>({subject,authenticatedAt:Date.now()}));
    const capability=Buffer.alloc(32,1).toString('base64url');
    await service.begin(subject,requestId,capability,{});expect((await service.continue(requestId)).status).toBe('identity_deleted');
    expect(await presence(subject)).toEqual({exists:false,activeSessions:false});
    expect(await presence(other)).toEqual({exists:true,activeSessions:false});
    expect(deleteUser).toHaveBeenCalledExactlyOnceWith(subject,false);
    await expect(store.profile(subject)).rejects.toMatchObject({code:'account_deleted'});
    expect(await service.recover(requestId,capability)).toMatchObject({status:'identity_deleted'});
    // RLS-hidden rows are not authoritative absence. Fail even if a policy
    // would hide an existing subject from this restricted verifier role.
    await db.exec('DROP POLICY gm_identity_users_read ON auth.users');
    await expect(presence(other)).rejects.toMatchObject({code:'identity_provider_unavailable'});
    await db.exec('CREATE POLICY gm_identity_users_read ON auth.users FOR SELECT TO gm_identity_verifier USING (true)');
    await db.exec('CREATE POLICY restrict_identity ON auth.users AS RESTRICTIVE FOR SELECT TO gm_identity_verifier USING (false)');
    await expect(presence(other)).rejects.toMatchObject({code:'identity_provider_unavailable'});
  }finally{await db.close();}
});
it('maps admin/observer failures to safe pending errors and refuses foreign/project/public key composition',async()=>{
  const subject=randomUUID();const observe=vi.fn(async()=>({exists:true,activeSessions:true}));
  for(const deleteUser of [async()=>({error:Error('sensitive-provider-error')}),async()=>{throw Error('ambiguous');}])
    await expect(supabaseIdentityDeletionProvider({deleteUser},observe).remove(subject)).rejects.toMatchObject({code:'identity_provider_unavailable'});
  await expect(supabaseIdentityDeletionProvider({deleteUser:async()=>({error:null})},async()=>{throw Error('offline');}).inspect(subject)).rejects.toMatchObject({code:'identity_provider_unavailable'});
  const query=vi.fn();
  expect(()=>createSupabaseIdentityDeletionProvider('aaaaaaaaaaaaaaaaaaaa','sb_secret_synthetic',{query})).toThrow('Dedicated');
  expect(()=>createSupabaseIdentityDeletionProvider('zgmyrpzwgtydwlzponih','sb_publishable_synthetic',{query})).toThrow('server secret');
  expect(query).not.toHaveBeenCalled();
});

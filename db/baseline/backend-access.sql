-- Separate reviewed first-environment operation; no credentials or login creation.
-- Apply only after v2.sql to an empty isolated environment with approval.
-- A separately provisioned server login must inherit gm_backend, never own gm.
CREATE ROLE gm_backend NOLOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION NOBYPASSRLS;
GRANT USAGE ON SCHEMA gm TO gm_backend;

DO $$
DECLARE relation record;
BEGIN
  FOR relation IN
    SELECT tablename FROM pg_tables WHERE schemaname='gm'
  LOOP
    IF EXISTS (SELECT 1 FROM information_schema.columns
      WHERE table_schema='gm' AND table_name=relation.tablename AND column_name='user_id') THEN
      EXECUTE format('GRANT SELECT, INSERT ON gm.%I TO gm_backend', relation.tablename);
      EXECUTE format('CREATE POLICY backend_owner ON gm.%I TO gm_backend USING
        (user_id = nullif(current_setting(''gm.subject'', true), '''')::uuid)
        WITH CHECK (user_id = nullif(current_setting(''gm.subject'', true), '''')::uuid)', relation.tablename);
      IF relation.tablename <> 'deleted_learner' THEN
        EXECUTE format('GRANT DELETE ON gm.%I TO gm_backend', relation.tablename);
      END IF;
      IF relation.tablename IN ('learner_profile','practice_session','learner_target_state','review_schedule') THEN
        EXECUTE format('GRANT UPDATE ON gm.%I TO gm_backend', relation.tablename);
      END IF;
    ELSE
      -- The learning server can read shared content but cannot publish or mutate it.
      EXECUTE format('GRANT SELECT ON gm.%I TO gm_backend', relation.tablename);
      EXECUTE format('CREATE POLICY backend_content_read ON gm.%I FOR SELECT TO gm_backend USING (true)', relation.tablename);
    END IF;
  END LOOP;
END;
$$;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA gm TO gm_backend;
-- No schema CREATE, table ownership, auth access or client-role grants.

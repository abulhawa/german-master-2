-- Authoritative German Master 2.0 initial schema for a fresh Supabase project only.
-- Supabase owns auth.users/auth.sessions; provision Auth before applying this file.
-- No learner/content data, login credentials, or provider secrets are included.

-- BEGIN v2.sql
-- Clean v2 baseline 1 for a NEW isolated database only.
-- No learner data, fixture catalog, upgrade backfills, credentials or auth-provider objects.
-- Privileged deletion machinery still needs production least-privilege/privacy review.
-- Isolated reset schema. Never apply to the legacy production database.
CREATE SCHEMA gm;
REVOKE ALL ON SCHEMA gm FROM PUBLIC;

CREATE TABLE gm.topic (id uuid PRIMARY KEY, title jsonb NOT NULL);
CREATE TABLE gm.skill (
  id uuid PRIMARY KEY, topic_id uuid NOT NULL REFERENCES gm.topic(id),
  title text NOT NULL, parent_id uuid REFERENCES gm.skill(id), CHECK (parent_id IS DISTINCT FROM id)
);
CREATE INDEX skill_topic_idx ON gm.skill(topic_id);
CREATE INDEX skill_parent_idx ON gm.skill(parent_id);
CREATE TABLE gm.learning_target (
  id uuid PRIMARY KEY, skill_id uuid NOT NULL REFERENCES gm.skill(id),
  kind text NOT NULL CHECK (kind IN ('lexical', 'grammar')), level text NOT NULL CHECK (level IN ('B1','B2')),
  objective text NOT NULL, status text NOT NULL CHECK (status IN ('draft','published','retired'))
);
CREATE INDEX target_skill_idx ON gm.learning_target(skill_id);
CREATE TABLE gm.exercise (id uuid PRIMARY KEY, target_id uuid NOT NULL REFERENCES gm.learning_target(id));
CREATE INDEX exercise_target_idx ON gm.exercise(target_id);
CREATE TABLE gm.exercise_revision (
  exercise_id uuid NOT NULL REFERENCES gm.exercise(id), revision integer NOT NULL CHECK (revision > 0),
  type text NOT NULL CHECK (type IN ('short_answer','choice','cloze','word_order','multi_slot','gap_choice','matching')),
  payload jsonb NOT NULL, rubric jsonb NOT NULL, normalization_version text NOT NULL,
  provenance text NOT NULL, review_status text NOT NULL,
  PRIMARY KEY (exercise_id, revision)
);
CREATE TABLE gm.content_release (
  id uuid PRIMARY KEY, status text NOT NULL CHECK (status IN ('draft','published','retired')),
  manifest_hash text NOT NULL, published_at timestamptz,
  CHECK (status <> 'published' OR published_at IS NOT NULL)
);
CREATE TABLE gm.content_release_exercise (
  release_id uuid NOT NULL REFERENCES gm.content_release(id), exercise_id uuid NOT NULL, revision integer NOT NULL,
  PRIMARY KEY (release_id, exercise_id, revision),
  FOREIGN KEY (exercise_id, revision) REFERENCES gm.exercise_revision(exercise_id, revision)
);
CREATE INDEX release_revision_idx ON gm.content_release_exercise(exercise_id, revision);
CREATE TABLE gm.learner_profile (
  user_id uuid PRIMARY KEY, locale text NOT NULL CHECK (locale IN ('en','de')), timezone text NOT NULL,
  level text NOT NULL DEFAULT 'B1' CHECK (level IN ('B1','B2')),
  session_question_count integer NOT NULL DEFAULT 15 CHECK (session_question_count IN (5,15)),
  revision integer NOT NULL DEFAULT 0 CHECK (revision >= 0),
  setup_completed boolean NOT NULL DEFAULT false
);
CREATE TABLE gm.device (
  user_id uuid NOT NULL REFERENCES gm.learner_profile(user_id), id uuid NOT NULL,
  PRIMARY KEY (user_id, id)
);
CREATE TABLE gm.practice_session (
  id uuid PRIMARY KEY, user_id uuid NOT NULL REFERENCES gm.learner_profile(user_id),
  request_id uuid NOT NULL, request_payload jsonb NOT NULL, release_id uuid NOT NULL REFERENCES gm.content_release(id),
  engine_version text NOT NULL, status text NOT NULL CHECK (status IN ('active','completed','ended')),
  issued_at timestamptz NOT NULL,
  UNIQUE (user_id, request_id), UNIQUE (user_id, id), UNIQUE (id, release_id)
);
CREATE INDEX session_release_idx ON gm.practice_session(release_id);
CREATE TABLE gm.session_question (
  id uuid PRIMARY KEY, user_id uuid NOT NULL, session_id uuid NOT NULL, release_id uuid NOT NULL,
  exercise_id uuid NOT NULL, revision integer NOT NULL, position integer NOT NULL CHECK (position >= 0),
  evidence_role text NOT NULL DEFAULT 'assessment' CHECK (evidence_role IN ('assessment','reinforcement')),
  CONSTRAINT question_report_revision UNIQUE (user_id,id,exercise_id,revision),
  UNIQUE (session_id, position), UNIQUE (user_id, id),
  FOREIGN KEY (user_id, session_id) REFERENCES gm.practice_session(user_id, id),
  FOREIGN KEY (session_id, release_id) REFERENCES gm.practice_session(id, release_id),
  FOREIGN KEY (release_id, exercise_id, revision) REFERENCES gm.content_release_exercise(release_id, exercise_id, revision)
);
CREATE INDEX question_release_revision_idx ON gm.session_question(release_id, exercise_id, revision);
CREATE TABLE gm.attempt (
  user_id uuid NOT NULL, id uuid NOT NULL, question_id uuid NOT NULL, device_id uuid NOT NULL,
  payload jsonb NOT NULL, received_sequence integer GENERATED ALWAYS AS IDENTITY UNIQUE,
  received_at timestamptz NOT NULL, PRIMARY KEY (user_id, id), UNIQUE (user_id, question_id),
  FOREIGN KEY (user_id, question_id) REFERENCES gm.session_question(user_id, id),
  FOREIGN KEY (user_id, device_id) REFERENCES gm.device(user_id, id)
);
CREATE INDEX attempt_device_idx ON gm.attempt(user_id, device_id);
CREATE INDEX attempt_history_idx ON gm.attempt(user_id, received_sequence);
CREATE TABLE gm.attempt_evaluation (
  user_id uuid NOT NULL, attempt_id uuid NOT NULL, evaluator_version text NOT NULL,
  evaluation jsonb NOT NULL, evaluated_at timestamptz NOT NULL,
  PRIMARY KEY (user_id, attempt_id, evaluator_version),
  FOREIGN KEY (user_id, attempt_id) REFERENCES gm.attempt(user_id, id)
);

-- Frozen revision definitions and evidence are append-only, including draft session fixtures.
CREATE FUNCTION gm.reject_mutation() RETURNS trigger LANGUAGE plpgsql SET search_path = '' AS $$
BEGIN
  IF TG_OP = 'DELETE' AND TG_TABLE_NAME IN (
    'practice_session','session_question','attempt','attempt_evaluation',
    'exposure_event','accepted_evidence','sync_change','profile_request',
    'content_report','session_completion','prepared_pack','prepared_pack_session'
  ) AND to_jsonb(OLD)->>'user_id' = current_setting('gm.privacy_subject', true) THEN
    RETURN OLD;
  END IF;
  RAISE EXCEPTION 'immutable row: create a new revision or audit entry';
END;
$$;
CREATE TRIGGER immutable_revision BEFORE UPDATE OR DELETE ON gm.exercise_revision FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_release_member BEFORE UPDATE OR DELETE ON gm.content_release_exercise FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_question BEFORE UPDATE OR DELETE ON gm.session_question FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_attempt BEFORE UPDATE OR DELETE ON gm.attempt FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_evaluation BEFORE UPDATE OR DELETE ON gm.attempt_evaluation FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();

CREATE TABLE gm.revision_evidence_identity (
  exercise_id uuid NOT NULL, revision integer NOT NULL,
  variant_key text NOT NULL CHECK (length(variant_key)>0),
  context_key text NOT NULL CHECK (length(context_key)>0), transfer_key text,
  PRIMARY KEY (exercise_id,revision),
  FOREIGN KEY (exercise_id,revision) REFERENCES gm.exercise_revision(exercise_id,revision)
);
CREATE TABLE gm.exposure_event (
  user_id uuid NOT NULL, id uuid NOT NULL, question_id uuid NOT NULL, device_id uuid NOT NULL,
  disposition text NOT NULL CHECK (disposition IN ('skip','exposure')), payload jsonb NOT NULL,
  PRIMARY KEY (user_id,id),
  FOREIGN KEY (user_id,question_id) REFERENCES gm.session_question(user_id,id),
  FOREIGN KEY (user_id,device_id) REFERENCES gm.device(user_id,id)
);
CREATE UNIQUE INDEX one_skip_per_question ON gm.exposure_event(user_id,question_id) WHERE disposition='skip';
CREATE TABLE gm.accepted_evidence (
  user_id uuid NOT NULL, id uuid NOT NULL, target_id uuid NOT NULL REFERENCES gm.learning_target(id),
  attempt_id uuid, exposure_id uuid, received_sequence integer GENERATED ALWAYS AS IDENTITY UNIQUE,
  policy_version text NOT NULL CHECK (policy_version='retained-evidence-v1'), payload jsonb NOT NULL,
  PRIMARY KEY (user_id,id),
  CHECK ((attempt_id IS NULL) <> (exposure_id IS NULL)),
  FOREIGN KEY (user_id,attempt_id) REFERENCES gm.attempt(user_id,id),
  FOREIGN KEY (user_id,exposure_id) REFERENCES gm.exposure_event(user_id,id),
  UNIQUE (user_id,attempt_id), UNIQUE (user_id,exposure_id)
);
CREATE INDEX evidence_replay_idx ON gm.accepted_evidence(user_id,target_id,received_sequence);
CREATE TABLE gm.learner_target_state (
  user_id uuid NOT NULL REFERENCES gm.learner_profile(user_id), target_id uuid NOT NULL REFERENCES gm.learning_target(id),
  policy_version text NOT NULL, last_sequence integer NOT NULL, snapshot jsonb NOT NULL,
  PRIMARY KEY (user_id,target_id)
);
CREATE TABLE gm.review_schedule (
  user_id uuid NOT NULL, target_id uuid NOT NULL, due_at timestamptz NOT NULL, schedule jsonb NOT NULL,
  PRIMARY KEY (user_id,target_id),
  FOREIGN KEY (user_id,target_id) REFERENCES gm.learner_target_state(user_id,target_id)
);
CREATE INDEX review_due_idx ON gm.review_schedule(user_id,due_at);
CREATE TABLE gm.sync_change (
  sequence integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  user_id uuid NOT NULL, target_id uuid NOT NULL, evidence_id uuid NOT NULL,
  operation text NOT NULL CHECK (operation='upsert'), payload jsonb NOT NULL,
  FOREIGN KEY (user_id,evidence_id) REFERENCES gm.accepted_evidence(user_id,id),
  UNIQUE (user_id,evidence_id)
);
CREATE INDEX sync_owner_cursor_idx ON gm.sync_change(user_id,sequence);
CREATE TRIGGER immutable_identity BEFORE UPDATE OR DELETE ON gm.revision_evidence_identity FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_exposure BEFORE UPDATE OR DELETE ON gm.exposure_event FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_evidence BEFORE UPDATE OR DELETE ON gm.accepted_evidence FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_sync BEFORE UPDATE OR DELETE ON gm.sync_change FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
-- Status can change, but historical educational identities/issuance cannot.
CREATE TRIGGER immutable_target_definition BEFORE UPDATE OF kind,objective,skill_id,level OR DELETE ON gm.learning_target FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_exercise BEFORE UPDATE OR DELETE ON gm.exercise FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_session_definition BEFORE UPDATE OF user_id,request_id,request_payload,release_id,engine_version,issued_at OR DELETE ON gm.practice_session FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();

-- Durable opaque read cursors, scoped to the authenticated subject.
CREATE TABLE gm.sync_cursor (
  id uuid PRIMARY KEY,
  user_id uuid NOT NULL REFERENCES gm.learner_profile(user_id),
  sequence integer NOT NULL CHECK (sequence >= 0),
  expires_at timestamptz NOT NULL
);
CREATE TABLE gm.target_page (
  id uuid PRIMARY KEY,
  user_id uuid NOT NULL REFERENCES gm.learner_profile(user_id),
  payload jsonb NOT NULL,
  expires_at timestamptz NOT NULL
);
CREATE TRIGGER immutable_sync_cursor BEFORE UPDATE ON gm.sync_cursor FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_target_page BEFORE UPDATE ON gm.target_page FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();

CREATE INDEX sync_cursor_position_expiry ON gm.sync_cursor (user_id,sequence,expires_at);
CREATE INDEX target_page_expiry ON gm.target_page (expires_at);
CREATE INDEX sync_cursor_expiry ON gm.sync_cursor (expires_at);

CREATE TABLE gm.profile_request (
  user_id uuid NOT NULL REFERENCES gm.learner_profile(user_id), request_id uuid NOT NULL,
  request jsonb NOT NULL, response jsonb NOT NULL, PRIMARY KEY (user_id,request_id)
);
CREATE TRIGGER immutable_profile_request BEFORE UPDATE OR DELETE ON gm.profile_request
  FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();

CREATE TABLE gm.content_report (
  user_id uuid NOT NULL, id uuid NOT NULL, question_id uuid NOT NULL,
  exercise_id uuid NOT NULL, revision integer NOT NULL,
  category text NOT NULL CHECK (category IN ('incorrect_answer','ambiguous_prompt','other')),
  payload jsonb NOT NULL, received_at timestamptz NOT NULL,
  PRIMARY KEY (user_id,id),
  FOREIGN KEY (user_id,question_id,exercise_id,revision) REFERENCES gm.session_question(user_id,id,exercise_id,revision),
  FOREIGN KEY (exercise_id,revision) REFERENCES gm.exercise_revision(exercise_id,revision)
);
CREATE INDEX report_owner_time ON gm.content_report(user_id,received_at);
CREATE TRIGGER immutable_report BEFORE UPDATE OR DELETE ON gm.content_report FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();

CREATE TABLE gm.session_completion (
  user_id uuid NOT NULL, id uuid NOT NULL, session_id uuid NOT NULL,
  payload jsonb NOT NULL, receipt jsonb NOT NULL,
  PRIMARY KEY (user_id,id), UNIQUE (user_id,session_id),
  FOREIGN KEY (user_id,session_id) REFERENCES gm.practice_session(user_id,id)
);
CREATE TRIGGER immutable_completion BEFORE UPDATE OR DELETE ON gm.session_completion
  FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();

CREATE TABLE gm.prepared_pack (
  user_id uuid NOT NULL REFERENCES gm.learner_profile(user_id), id uuid NOT NULL,
  payload jsonb NOT NULL, response jsonb NOT NULL,
  PRIMARY KEY (user_id,id)
);
CREATE TABLE gm.prepared_pack_session (
  user_id uuid NOT NULL, pack_id uuid NOT NULL, session_id uuid NOT NULL,
  PRIMARY KEY (user_id,pack_id,session_id), UNIQUE (user_id,session_id),
  FOREIGN KEY (user_id,pack_id) REFERENCES gm.prepared_pack(user_id,id),
  FOREIGN KEY (user_id,session_id) REFERENCES gm.practice_session(user_id,id)
);
CREATE TRIGGER immutable_pack BEFORE UPDATE OR DELETE ON gm.prepared_pack FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_pack_session BEFORE UPDATE OR DELETE ON gm.prepared_pack_session FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();

CREATE TABLE gm.deleted_learner (
  user_id uuid PRIMARY KEY, request_id uuid NOT NULL, deleted_at timestamptz NOT NULL
);

CREATE TABLE gm.schema_baseline (version integer PRIMARY KEY CHECK (version = 1));
INSERT INTO gm.schema_baseline VALUES (1);

-- The product API owns all learning access. No client role is granted schema access.
-- RLS has no policies here: accidental grants fail closed until scoped backend
-- policies and least-privilege roles are deliberately added and verified.
REVOKE ALL ON ALL TABLES IN SCHEMA gm FROM PUBLIC;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA gm FROM PUBLIC;
REVOKE ALL ON ALL FUNCTIONS IN SCHEMA gm FROM PUBLIC;
ALTER DEFAULT PRIVILEGES IN SCHEMA gm REVOKE EXECUTE ON FUNCTIONS FROM PUBLIC;
DO $$
DECLARE relation record;
BEGIN
  FOR relation IN SELECT tablename FROM pg_tables WHERE schemaname = 'gm' LOOP
    EXECUTE format('ALTER TABLE gm.%I ENABLE ROW LEVEL SECURITY', relation.tablename);
  END LOOP;
END;
$$;

-- END v2.sql

-- BEGIN backend-access.sql
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

-- END backend-access.sql

-- BEGIN auth-session-access.sql
-- Separate credential/schema approval; never grant this role to client roles.
-- Read only session identity and its maximum lifetime, never refresh tokens.
CREATE ROLE gm_auth_verifier NOLOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION NOBYPASSRLS;
GRANT USAGE ON SCHEMA auth TO gm_auth_verifier;
GRANT SELECT (id, user_id, not_after) ON auth.sessions TO gm_auth_verifier;
-- Supabase enables RLS on auth.sessions. A column grant alone sees no rows.
-- The server verifier may see only these non-secret session columns; it still
-- queries the online-verified subject AND session id before authorizing access.
CREATE POLICY gm_auth_session_read ON auth.sessions FOR SELECT TO gm_auth_verifier USING (true);
-- Provision a separate server login inheriting only this role. Do not grant it
-- gm_backend, schema ownership, auth writes or service-role privileges.

-- END auth-session-access.sql

-- BEGIN identity-deletion.sql
-- Optional private server subsystem, applied after v2.sql only with schema approval.
-- Never expose gm_privacy through the Data API or grant this role to clients.
CREATE SCHEMA gm_privacy;
REVOKE ALL ON SCHEMA gm_privacy FROM PUBLIC;
CREATE TABLE gm_privacy.identity_deletion (
  subject uuid PRIMARY KEY,
  request_id uuid NOT NULL UNIQUE,
  recovery_hash text CHECK (recovery_hash IS NULL OR recovery_hash ~ '^[0-9a-f]{64}$'),
  requested_at timestamptz NOT NULL,
  completed_at timestamptz,
  recovery_until timestamptz,
  CHECK ((completed_at IS NULL AND recovery_until IS NULL) OR
         (completed_at IS NOT NULL AND recovery_until > completed_at))
);
ALTER TABLE gm_privacy.identity_deletion ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON gm_privacy.identity_deletion FROM PUBLIC;
CREATE ROLE gm_privacy_worker NOLOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION NOBYPASSRLS;
GRANT USAGE ON SCHEMA gm_privacy TO gm_privacy_worker;
GRANT SELECT, INSERT ON gm_privacy.identity_deletion TO gm_privacy_worker;
GRANT UPDATE (completed_at, recovery_until, recovery_hash) ON gm_privacy.identity_deletion TO gm_privacy_worker;
CREATE POLICY privacy_worker ON gm_privacy.identity_deletion TO gm_privacy_worker USING (true) WITH CHECK (true);
-- No auth/learning/schema-owner privileges. Learning deletion uses its existing
-- subject-scoped connection; provider administration uses a separate adapter.

-- END identity-deletion.sql

-- BEGIN identity-verifier-access.sql
-- Separate reviewed first-environment operation. No auth writes or token reads.
-- Use a distinct server login; never grant this role to client/learning roles.
CREATE ROLE gm_identity_verifier NOLOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION NOBYPASSRLS;
GRANT USAGE ON SCHEMA auth TO gm_identity_verifier;
GRANT SELECT (id) ON auth.users TO gm_identity_verifier;
GRANT SELECT (user_id) ON auth.sessions TO gm_identity_verifier;
CREATE POLICY gm_identity_users_read ON auth.users FOR SELECT TO gm_identity_verifier USING (true);
CREATE POLICY gm_identity_sessions_read ON auth.sessions FOR SELECT TO gm_identity_verifier USING (true);
-- Hard deletion uses the separately isolated server Auth admin adapter, not SQL
-- DELETE or the learning connection. Presence queries return booleans only.

-- END identity-verifier-access.sql

-- BEGIN auth-verifier-views.sql
-- Managed Supabase auth schema USAGE cannot be delegated by its postgres role.
-- These private invoker views retain underlying column grants and Auth RLS.
-- No definer function, schema-owner access, token columns or Auth writes.
CREATE SCHEMA gm_auth;
REVOKE ALL ON SCHEMA gm_auth FROM PUBLIC;
CREATE VIEW gm_auth.session_identity WITH (security_invoker = true)
  AS SELECT id,user_id,not_after FROM auth.sessions;
CREATE VIEW gm_auth.identity_subject WITH (security_invoker = true)
  AS SELECT id FROM auth.users;
CREATE VIEW gm_auth.identity_session WITH (security_invoker = true)
  AS SELECT user_id FROM auth.sessions;
REVOKE ALL ON ALL TABLES IN SCHEMA gm_auth FROM PUBLIC;
GRANT USAGE ON SCHEMA gm_auth TO gm_auth_verifier,gm_identity_verifier;
GRANT SELECT ON gm_auth.session_identity TO gm_auth_verifier;
GRANT SELECT ON gm_auth.identity_subject,gm_auth.identity_session TO gm_identity_verifier;

-- END auth-verifier-views.sql

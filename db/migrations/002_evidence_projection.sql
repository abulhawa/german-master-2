-- Local adapter migration; no legacy production database is involved.
CREATE TABLE gm.schema_migration (version integer PRIMARY KEY);
INSERT INTO gm.schema_migration VALUES (1);
ALTER TABLE gm.practice_session ADD COLUMN issued_at timestamptz;
-- Historical issuance was not saved: bound conservatively by first receipt.
UPDATE gm.practice_session s SET issued_at=COALESCE(
  (SELECT min(a.received_at) FROM gm.attempt a JOIN gm.session_question q ON q.id=a.question_id WHERE q.session_id=s.id), now());
ALTER TABLE gm.practice_session ALTER COLUMN issued_at SET NOT NULL;
CREATE TABLE gm.revision_evidence_identity (
  exercise_id uuid NOT NULL, revision integer NOT NULL,
  variant_key text NOT NULL CHECK (length(variant_key)>0),
  context_key text NOT NULL CHECK (length(context_key)>0), transfer_key text,
  PRIMARY KEY (exercise_id,revision),
  FOREIGN KEY (exercise_id,revision) REFERENCES gm.exercise_revision(exercise_id,revision)
);
ALTER TABLE gm.session_question ADD COLUMN evidence_role text NOT NULL DEFAULT 'assessment'
  CHECK (evidence_role IN ('assessment','reinforcement'));
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

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
INSERT INTO gm.schema_migration VALUES (9);

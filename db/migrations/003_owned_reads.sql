-- Durable opaque read cursors, scoped to the authenticated subject.
CREATE TABLE gm.sync_cursor (
  id uuid PRIMARY KEY,
  user_id uuid NOT NULL REFERENCES gm.learner_profile(user_id),
  sequence integer NOT NULL CHECK (sequence >= 0),
  UNIQUE (user_id,sequence)
);
CREATE TABLE gm.target_page (
  id uuid PRIMARY KEY,
  user_id uuid NOT NULL REFERENCES gm.learner_profile(user_id),
  payload jsonb NOT NULL
);
CREATE TRIGGER immutable_sync_cursor BEFORE UPDATE OR DELETE ON gm.sync_cursor FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_target_page BEFORE UPDATE OR DELETE ON gm.target_page FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
INSERT INTO gm.schema_migration VALUES (3);

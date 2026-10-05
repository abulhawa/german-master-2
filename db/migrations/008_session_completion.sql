-- Completion receipts are operational records, never learning evidence.
ALTER TABLE gm.practice_session DROP CONSTRAINT practice_session_status_check;
ALTER TABLE gm.practice_session ADD CONSTRAINT practice_session_status_check CHECK (status IN ('active','completed','ended'));
CREATE TABLE gm.session_completion (
  user_id uuid NOT NULL, id uuid NOT NULL, session_id uuid NOT NULL,
  payload jsonb NOT NULL, receipt jsonb NOT NULL,
  PRIMARY KEY (user_id,id), UNIQUE (user_id,session_id),
  FOREIGN KEY (user_id,session_id) REFERENCES gm.practice_session(user_id,id)
);
CREATE TRIGGER immutable_completion BEFORE UPDATE OR DELETE ON gm.session_completion
  FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
INSERT INTO gm.schema_migration VALUES (8);

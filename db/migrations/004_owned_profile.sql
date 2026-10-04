-- Local owned setup. Existing fixture evidence and session requests remain untouched.
ALTER TABLE gm.learner_profile
  ADD COLUMN level text NOT NULL DEFAULT 'B1' CHECK (level IN ('B1','B2')),
  ADD COLUMN session_question_count integer NOT NULL DEFAULT 15 CHECK (session_question_count IN (5,15)),
  ADD COLUMN revision integer NOT NULL DEFAULT 0 CHECK (revision >= 0),
  ADD COLUMN setup_completed boolean NOT NULL DEFAULT false;
CREATE TABLE gm.profile_request (
  user_id uuid NOT NULL REFERENCES gm.learner_profile(user_id), request_id uuid NOT NULL,
  request jsonb NOT NULL, response jsonb NOT NULL, PRIMARY KEY (user_id,request_id)
);
CREATE TRIGGER immutable_profile_request BEFORE UPDATE OR DELETE ON gm.profile_request
  FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
INSERT INTO gm.schema_migration (version) VALUES (4);

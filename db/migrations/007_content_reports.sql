ALTER TABLE gm.session_question ADD CONSTRAINT question_report_revision UNIQUE (user_id,id,exercise_id,revision);
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
INSERT INTO gm.schema_migration VALUES (7);

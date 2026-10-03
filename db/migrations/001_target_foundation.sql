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
  type text NOT NULL CHECK (type IN ('short_answer','choice','cloze','word_order','multi_slot')),
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
CREATE TABLE gm.learner_profile (user_id uuid PRIMARY KEY, locale text NOT NULL CHECK (locale IN ('en','de')), timezone text NOT NULL);
CREATE TABLE gm.device (
  user_id uuid NOT NULL REFERENCES gm.learner_profile(user_id), id uuid NOT NULL,
  PRIMARY KEY (user_id, id)
);
CREATE TABLE gm.practice_session (
  id uuid PRIMARY KEY, user_id uuid NOT NULL REFERENCES gm.learner_profile(user_id),
  request_id uuid NOT NULL, request_payload jsonb NOT NULL, release_id uuid NOT NULL REFERENCES gm.content_release(id),
  engine_version text NOT NULL, status text NOT NULL CHECK (status IN ('active','completed')),
  UNIQUE (user_id, request_id), UNIQUE (user_id, id), UNIQUE (id, release_id)
);
CREATE INDEX session_release_idx ON gm.practice_session(release_id);
CREATE TABLE gm.session_question (
  id uuid PRIMARY KEY, user_id uuid NOT NULL, session_id uuid NOT NULL, release_id uuid NOT NULL,
  exercise_id uuid NOT NULL, revision integer NOT NULL, position integer NOT NULL CHECK (position >= 0),
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
CREATE FUNCTION gm.reject_mutation() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN RAISE EXCEPTION 'immutable row: create a new revision or audit entry'; END;
$$;
CREATE TRIGGER immutable_revision BEFORE UPDATE OR DELETE ON gm.exercise_revision FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_release_member BEFORE UPDATE OR DELETE ON gm.content_release_exercise FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_question BEFORE UPDATE OR DELETE ON gm.session_question FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_attempt BEFORE UPDATE OR DELETE ON gm.attempt FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_evaluation BEFORE UPDATE OR DELETE ON gm.attempt_evaluation FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();

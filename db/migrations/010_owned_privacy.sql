-- Disposable local adapter only. Consolidate before the first real environment.
CREATE TABLE gm.deleted_learner (
  user_id uuid PRIMARY KEY, request_id uuid NOT NULL, deleted_at timestamptz NOT NULL
);
-- The transaction-local subject permits only owned deletion, never updates or
-- catalog mutation. Ordinary evidence/revision immutability stays in force.
CREATE OR REPLACE FUNCTION gm.reject_mutation() RETURNS trigger LANGUAGE plpgsql AS $$
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
INSERT INTO gm.schema_migration VALUES (10);

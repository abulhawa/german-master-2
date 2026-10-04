-- Old cursors have no trustworthy issuance time; force a confirmed snapshot reset.
ALTER TABLE gm.sync_cursor ADD COLUMN expires_at timestamptz NOT NULL DEFAULT '-infinity';
ALTER TABLE gm.sync_cursor DROP CONSTRAINT sync_cursor_user_id_sequence_key;
CREATE INDEX sync_cursor_position_expiry ON gm.sync_cursor (user_id,sequence,expires_at);
INSERT INTO gm.schema_migration VALUES (5);

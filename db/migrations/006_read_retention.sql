-- Transport caches may be deleted; their payloads remain immutable while retained.
ALTER TABLE gm.target_page ADD COLUMN expires_at timestamptz NOT NULL DEFAULT 'infinity';
CREATE INDEX target_page_expiry ON gm.target_page (expires_at);
CREATE INDEX sync_cursor_expiry ON gm.sync_cursor (expires_at);
DROP TRIGGER immutable_sync_cursor ON gm.sync_cursor;
DROP TRIGGER immutable_target_page ON gm.target_page;
CREATE TRIGGER immutable_sync_cursor BEFORE UPDATE ON gm.sync_cursor FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
CREATE TRIGGER immutable_target_page BEFORE UPDATE ON gm.target_page FOR EACH ROW EXECUTE FUNCTION gm.reject_mutation();
INSERT INTO gm.schema_migration VALUES (6);

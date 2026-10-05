-- Optional private server subsystem, applied after v2.sql only with schema approval.
-- Never expose gm_privacy through the Data API or grant this role to clients.
CREATE SCHEMA gm_privacy;
REVOKE ALL ON SCHEMA gm_privacy FROM PUBLIC;
CREATE TABLE gm_privacy.identity_deletion (
  subject uuid PRIMARY KEY,
  request_id uuid NOT NULL UNIQUE,
  recovery_hash text CHECK (recovery_hash IS NULL OR recovery_hash ~ '^[0-9a-f]{64}$'),
  requested_at timestamptz NOT NULL,
  completed_at timestamptz,
  recovery_until timestamptz,
  CHECK ((completed_at IS NULL AND recovery_until IS NULL) OR
         (completed_at IS NOT NULL AND recovery_until > completed_at))
);
ALTER TABLE gm_privacy.identity_deletion ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON gm_privacy.identity_deletion FROM PUBLIC;
CREATE ROLE gm_privacy_worker NOLOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION NOBYPASSRLS;
GRANT USAGE ON SCHEMA gm_privacy TO gm_privacy_worker;
GRANT SELECT, INSERT ON gm_privacy.identity_deletion TO gm_privacy_worker;
GRANT UPDATE (completed_at, recovery_until, recovery_hash) ON gm_privacy.identity_deletion TO gm_privacy_worker;
CREATE POLICY privacy_worker ON gm_privacy.identity_deletion TO gm_privacy_worker USING (true) WITH CHECK (true);
-- No auth/learning/schema-owner privileges. Learning deletion uses its existing
-- subject-scoped connection; provider administration uses a separate adapter.

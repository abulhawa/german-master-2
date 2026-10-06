-- Managed Supabase auth schema USAGE cannot be delegated by its postgres role.
-- These private invoker views retain underlying column grants and Auth RLS.
-- No definer function, schema-owner access, token columns or Auth writes.
CREATE SCHEMA gm_auth;
REVOKE ALL ON SCHEMA gm_auth FROM PUBLIC;
CREATE VIEW gm_auth.session_identity WITH (security_invoker = true)
  AS SELECT id,user_id,not_after FROM auth.sessions;
CREATE VIEW gm_auth.identity_subject WITH (security_invoker = true)
  AS SELECT id FROM auth.users;
CREATE VIEW gm_auth.identity_session WITH (security_invoker = true)
  AS SELECT user_id FROM auth.sessions;
REVOKE ALL ON ALL TABLES IN SCHEMA gm_auth FROM PUBLIC;
GRANT USAGE ON SCHEMA gm_auth TO gm_auth_verifier,gm_identity_verifier;
GRANT SELECT ON gm_auth.session_identity TO gm_auth_verifier;
GRANT SELECT ON gm_auth.identity_subject,gm_auth.identity_session TO gm_identity_verifier;

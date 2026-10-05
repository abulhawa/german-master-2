-- Separate credential/schema approval; never grant this role to client roles.
-- Read only session identity and its maximum lifetime, never refresh tokens.
CREATE ROLE gm_auth_verifier NOLOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION NOBYPASSRLS;
GRANT USAGE ON SCHEMA auth TO gm_auth_verifier;
GRANT SELECT (id, user_id, not_after) ON auth.sessions TO gm_auth_verifier;
-- Supabase enables RLS on auth.sessions. A column grant alone sees no rows.
-- The server verifier may see only these non-secret session columns; it still
-- queries the online-verified subject AND session id before authorizing access.
CREATE POLICY gm_auth_session_read ON auth.sessions FOR SELECT TO gm_auth_verifier USING (true);
-- Provision a separate server login inheriting only this role. Do not grant it
-- gm_backend, schema ownership, auth writes or service-role privileges.

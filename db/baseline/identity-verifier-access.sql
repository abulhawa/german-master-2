-- Separate reviewed first-environment operation. No auth writes or token reads.
-- Use a distinct server login; never grant this role to client/learning roles.
CREATE ROLE gm_identity_verifier NOLOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION NOBYPASSRLS;
GRANT USAGE ON SCHEMA auth TO gm_identity_verifier;
GRANT SELECT (id) ON auth.users TO gm_identity_verifier;
GRANT SELECT (user_id) ON auth.sessions TO gm_identity_verifier;
CREATE POLICY gm_identity_users_read ON auth.users FOR SELECT TO gm_identity_verifier USING (true);
CREATE POLICY gm_identity_sessions_read ON auth.sessions FOR SELECT TO gm_identity_verifier USING (true);
-- Hard deletion uses the separately isolated server Auth admin adapter, not SQL
-- DELETE or the learning connection. Presence queries return booleans only.

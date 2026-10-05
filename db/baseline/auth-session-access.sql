-- Separate credential/schema approval; never grant this role to client roles.
-- Read only session identity and its maximum lifetime, never refresh tokens.
CREATE ROLE gm_auth_verifier NOLOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION NOBYPASSRLS;
GRANT USAGE ON SCHEMA auth TO gm_auth_verifier;
GRANT SELECT (id, user_id, not_after) ON auth.sessions TO gm_auth_verifier;
-- Provision a separate server login inheriting only this role. Do not grant it
-- gm_backend, schema ownership, auth writes or service-role privileges.

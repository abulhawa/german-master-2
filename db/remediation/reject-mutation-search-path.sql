-- Dedicated v2 staging only; separate owner approval required before execution.
-- Preserve the installed function body, ownership, privileges and trigger bindings.
BEGIN;
ALTER FUNCTION gm.reject_mutation() SET search_path = '';
COMMIT;

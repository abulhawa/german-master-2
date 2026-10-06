# Staging advisor correction review

6 October 2026. Target only `german-master-v2-staging`,
`zgmyrpzwgtydwlzponih`. Owner approved and applied on 6 October 2026 as
`v2_reject_mutation_fixed_search_path`.

## Actual approved application evidence

Reconfirmed the dedicated staging project name, ID, Frankfurt region,
ACTIVE_HEALTHY status and PostgreSQL 17.11 before execution. Installed function
body matched the reviewed baseline; configuration was null. Applied exactly the
transaction below through the migration connector, which returned success.
Post-application metadata confirms `search_path=""`, SECURITY INVOKER,
unchanged function body, owner OID 16388, ACL `{postgres=X/postgres}` and
19 trigger bindings. Security advisor now returns zero findings. Performance
advisor remains at 19 initplan WARNs, eight unindexed-FK INFOs and 16
unused-index INFOs. No live learner/auth fixture or credential was used.

This closes the real staging search-path security subrequirement. It does not
close M2 load/contention/catalog/identity acceptance or any full milestone.
The following review records the approved operation and prior warning.

Read-only inspection confirms `gm.reject_mutation()` is PL/pgSQL,
SECURITY INVOKER, with null `proconfig`. Security advisor still reports one
[mutable search-path warning](https://supabase.com/docs/guides/database/database-linter?lint=0011_function_search_path_mutable).
Its body uses built-ins and trigger variables, with no application object lookup.
[Current advisor documentation](https://supabase.com/docs/guides/observability/advisors?queryGroups=lint&lint=0011_function_search_path_mutable)
recommends an empty function search path. The changelog index was retrieved;
no search-path entry was found. Documentation lookup used the connector.

## Exact proposed operation

After explicit owner approval, recheck project identity and the function body
against `db/baseline/v2.sql`, then execute only
[the correction](../../db/remediation/reject-mutation-search-path.sql):

```sql
BEGIN;
ALTER FUNCTION gm.reject_mutation() SET search_path = '';
COMMIT;
```

This changes one function configuration, preserving its body, owner, privileges
and trigger bindings. No role/login/account/data/catalog/routing change is
included. Existing initial-install approval does not authorize this correction.
Re-read `proconfig`/`prosecdef` and rerun security and performance advisors after
an approved application; expect this security warning to disappear, without
claiming that performance/load or auth acceptance is complete.

The fresh baseline now includes the same setting. The historical installation
hash in `staging-initial-install-review.md` remains accurate and unchanged.
New baseline SHA-256: `fd91765ddf0b4530f33c312b269539699b8d6cd7cce40414a2eb7332e5ed4dc7`.
Correction SHA-256: `ff294ff1a63e7cd9280971fbb667dbc08d0f434667192f8a0ad7640557c0d982`.

## Local evidence and outstanding real evidence

PGlite verifies fresh installation and remediation of the previously mutable
definition; configuration, unchanged body/owner/ACL, immutable revision
rejection, owned pack/Skip/completion/export/deletion/replay and shared-content
preservation pass with a hostile caller path and shadow built-ins. Focused
baseline/backend-access/identity-deletion suites: three files, ten tests passed.
This is local SQL evidence, not a live correction or independent-connection test.

Actual read-only staging performance advisor: 19 `auth_rls_initplan` WARNs,
eight unindexed-FK INFOs and 16 unused-index INFOs. Track policy evaluation and
FK plans with representative approved staging load; do not remove unused indexes
on the basis of an empty database. These findings are outside this minimal SQL.

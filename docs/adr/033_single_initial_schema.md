# ADR 033: One initial schema for the replacement database

Accepted under the owner's 10 October 2026 clean database request.

`db/0001_initial_schema.sql` replaces development migrations 001–010, split baseline SQL and standalone function remediation. This supersedes fixture-history retention in ADR 027. Original imported legacy code/schema remains provenance; it is not part of the v2 initial schema.

Local fixture initialization and role/Auth tests select sections from the same file. Provider stand-ins exist only in local tests. Hosted installation applies the complete file once, with managed Auth supplied by Supabase. Content and credential provisioning remain separate. Schema installation must not implicitly publish content or create server login passwords.

No saved development schema upgrade, learner-history backfill or legacy schema compatibility is provided. Fixture schema and draft content/evidence identities are created atomically. Existing baseline-1 databases reopen without seeding; unknown/development schemas fail with an explicit recreate-fixture instruction. Network repositories continue to validate only, never initialize or seed.

The hosted replacement was installed from this exact schema with one Supabase ledger entry. Future changes are ordinary reviewed incremental migrations. Performance advisor findings require evaluation and do not establish workload acceptance. Any baseline revision during preparation requires deliberate hosted reconciliation; changing a source file alone cannot claim hosted parity.

Production cutover and old-project retirement remain separately controlled.

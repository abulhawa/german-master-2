# German Master 2.0 database

`0001_initial_schema.sql` is the sole initial application schema. It contains tables, constraints, indexes, immutable triggers, RLS, restricted parent roles, privacy storage and private Auth verifier views. Apply the complete file once to a fresh Supabase project with managed Auth already present. It contains no content, learner data, login credentials, SMTP settings or provider secrets.

The replacement project is recorded in `docs/operations/replacement-database-plan.json`. Installing the schema does not authorize a production connection switch, content publication or retirement.

Local PGlite fixtures use `initialSchemaSection` to select provider-independent sections of this same SQL source. Fixture initialization creates the learning schema and unpublished foundation content atomically. Reopening baseline 1 does not replay seeds. Saved development schemas are unsupported: recreate only disposable local fixture databases. There is no upgrade/backfill runner or development migration ledger. Network initialization only validates baseline 1; it never installs schema or seeds content.

Future schema changes use reviewed incremental migrations after this baseline. Content releases remain explicit operations under `seed/`, separate from schema installation. Credentials and project Auth settings are provisioned privately.

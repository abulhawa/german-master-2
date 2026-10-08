# M0 environment and preservation inventory

Checked 3 October 2026 during the first foundation slice. No deployment, database reset, content publication, billing change or store release was performed. Original repositories remain preserved.

## Legacy deployment

Connected Vercel inventory identifies the `german-master` project linked to `abulhawa/german-master`, separate from this monorepo. Its latest listed READY production deployment is `dpl_AuPHoBMVHU6M6bQZ2qE4Xce67K2M`, at original `main` commit `30b2c9e21ba643264d113711cb30494e86a8cc6c`. Newer READY entries are dependency previews, not production. The reported production SHA is on a lineage outside the imported local head history. GitHub confirms it exists (13 September 2026); it was fetched into an isolated bare recovery repository, passed full integrity checks and was saved as ignored `.local/backups/legacy-deployment.bundle`. Bundle verification confirms complete reachable history for the exact deployed ref. A separate bare clone restored the bundle and passed full integrity checks. Neither original checkout was modified.

Deployment URL: [immutable production deployment](https://german-master-5tdl4ml5e-abulhawas-projects.vercel.app). Read-only Vercel `list_deployments` supplied identity/state/revision. Public fetch was unavailable. The project-detail connector has incompatible `projectId` versus `idOrName` validation requirements; two attempts could not retrieve settings. Production aliases, environment parity and live API health remain unverified. READY is platform evidence, not a successful learner journey or database recovery.

## Database and content

Connected Supabase inventory exposed one healthy PostgreSQL 17 project. Its public-table inventory matches the legacy application. The needed legacy database connection was read only in memory from the preserved web configuration. No credential was copied into this monorepo or printed. A direct read-only repeatable-read transaction exported all seven public base tables plus column, constraint and RLS-policy catalogs into ignored `.local/backups/legacy-public-data.json` (20,712,043 bytes). No rows appear in committed evidence.

| Table | Snapshot rows |
|---|---:|
| inflections | 10,499 |
| lexemes | 4,754 |
| practice_history | 1,261 |
| practice_log | 245 |
| task_specs | 9,786 |
| task_sync_state | 1 |
| words | 4,754 |

Every table's rows were recovered into an isolated pg-mem JSONB archive and matched the original SHA-256 payload digest. Local evidence with per-table hashes is `.local/backups/legacy-data-evidence.json`. This is exact public-row preservation and JSON archive recovery, **not a full PostgreSQL schema/application restore**. Keep these files private: they include legacy practice records. No production writes occurred.

A manifest hashes 21 tracked content/Play metadata files under `apps/web/data` and `apps/android/app/src/main/play`; its SHA-256 is `b7718f397ec07feab113b0aaf488adf2b158ca9da290fa216915ad966b01720b`. The local manifest is `.local/backups/source-content-manifest.json`. These sources are also covered by the verified Git recovery bundles. This inventory is not content correctness/rights approval.

Remaining gates: no local `pg_dump`, `psql`, Docker or PostgreSQL restore server was available. Obtain a full managed/native export and prove an isolated PostgreSQL restore of schema, functions, extensions, privileges and policies. Separately preserve auth users and Storage objects/metadata as applicable. Verify managed backup retention and an off-machine private recovery destination. Local row snapshots and source bundles do not establish these gates.

## Android store/signing continuity

The baseline tag and current source agree: namespace/application ID `com.germanverbmaster.android`, versionCode 29, versionName `0.2.08`. Release signing configuration still loads local properties; application identity and signing configuration are unchanged. The new preview activity is debug-only.

No local properties or Play service-account file was present in either Android checkout; no release certificate/keystore configuration was available to compare. The public Play listing fetch was unavailable, and no authenticated Play Console connector was available. Published track/version, upload certificate, Play app-signing certificate, key custody/recovery and signing continuity remain blocked on store records/credentials. Debug signing is not release-signing evidence. No device/emulator was attached; no release build/upload or device behavior is claimed.

## Remaining M0 exit gates

Deployment source identity and accessible public-row preservation now have evidence. M0 stays in progress until live deployment/API settings, full private database/content recovery and published Android signing/store records have been verified. Continue independent foundation work in isolated environments; never establish a baseline with a reset or an unauthorized production cutover.

## Recheck — 8 October 2026

M0 is the sole active milestone under the owner's sequential closure decision. Local read-only checks at `31f310f` establish:

- All three recovery bundles (`.local/legacy-web.bundle`, `.local/legacy-android.bundle`, `.local/backups/legacy-deployment.bundle`) pass `git bundle verify`. This is fresh bundle verification; the full clone/fsck restore rehearsal remains the historical 3 October evidence.
- All 21 current tracked content/Play metadata files match the private source-content manifest hashes. The private public-row snapshot SHA-256 matches its saved evidence. No row contents or credentials were printed.
- `pg_dump`, `pg_restore`, `psql` and Docker are absent from PATH; the standard `C:/Program Files/PostgreSQL` location was not found. This is a bounded tool check, not a claim that no executable exists anywhere on the machine.
- The known `.local/backups` folder contains the public-row JSON snapshot/evidence, source-content manifest and deployed-source Git bundle; no full PostgreSQL backup is present there. No off-machine destination has been identified in this check.
- Release local.properties is absent at the checked monorepo root/app and original Android root paths. No authoritative Play records were available to this local check. Source version 29 is not proof of the maximum published code.

Full isolated database/Auth/Storage recovery, protected off-machine recovery and Play signing/version custody remain open. Asked the owner for the private backup destination and Play records/access route; credentials and private keys must not be supplied in chat. Next: resolve those resources and provision local isolated restore tooling, then perform the full recovery rehearsal. No live provider access, production mutation, upload or release occurred in this recheck. Stay within M0 while blocked.

## Owner signing clarification — 8 October 2026

The owner confirms holding the signing keys and signing builds personally in Android Studio. Agent certificate/key-custody inspection is removed from the M0 task scope; the owner retains signing continuity and release responsibility. No signing secret is needed in this repository or chat.

Read-only inspection of `<preserved-android-repository>/app/build.gradle.kts` confirms application ID `com.germanverbmaster.android`, versionCode `29`, versionName `0.2.08`. The original checkout is clean and was not changed. These are source numbers, not a fresh Play Console maximum-version check. Future release code must exceed the then-current Play maximum, checked by the owner before upload.

Remaining M0 task: complete protected source/content/database preservation and isolated recovery verification, with a private off-machine copy. Backup covers original source histories, authored content, relevant database state and the renovation source snapshot; signing keys remain under the owner's existing process. No learner-history migration is being added.

## Laptop backup and project separation — 8 October 2026

Owner accepts backups on this laptop; off-machine storage is no longer an M0 exit requirement. Signing remains owner-managed. Full database recovery verification is still required.

Saved `the private laptop recovery directory` outside the checkout: three legacy recovery bundles, existing private public-row snapshot/evidence, content manifest and a fresh all-ref renovation bundle at `dc73f5d`. Seven recovery files plus a private SHA-256 manifest. All copied-file hashes match; all four bundles verify. The renovation bundle was cloned into a new bare repository, passed full fsck and restored the exact main commit. This set does not contain a full PostgreSQL dump, Auth/Storage export or signing keys. Private snapshot contents were not printed or committed. Later commits require a new dated snapshot.

Read-only Supabase inventory confirms both `Ali's Project` (`kagsgjzijfgvtvkylczl`, legacy) and `german-master-v2-production` (`zgmyrpzwgtydwlzponih`, renovation) are ACTIVE_HEALTHY in the same organization. No third project is needed or created. [Current pricing](https://supabase.com/pricing) allows two active Free projects, with 500 MB database size per project and no automatic backups. Inventory does not establish the actual organization billing plan.

Next: obtain full database exports for legacy and v2 without production writes and verify isolated PostgreSQL recovery, including applicable Auth/Storage scope. Keep the two live databases separate and unchanged. Local restore tooling availability remains the previously recorded blocker; project separation is verified, not a database restore.

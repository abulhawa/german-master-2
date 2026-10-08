# M0 preservation and baseline closure

Closed on 8 October 2026 under the owner's clarified baseline scope: backups may remain on the laptop; Android signing keys and Android Studio signing remain owner-managed. The agent verifies application identity and source version metadata. The owner checks the live Play maximum before a future release. These decisions supersede earlier off-machine and agent key-custody prerequisites.

## Recovery evidence

Private backups are outside the public checkout, with directory access restricted to the owner, SYSTEM and administrators. The ignored `.local/backups/m0-private-location.json` points to the private recovery set. Public documentation contains no personal machine paths, credentials, account records, Storage filenames or backup payloads.

| Recovered item | Verified result |
| --- | --- |
| Legacy web, Android and deployed-source histories | Fresh bare clones and full Git integrity checks pass; both baseline trees match; preserved deployed commit is recoverable. Original repositories remain clean and unchanged. |
| Tracked legacy content and Play metadata | All 21 source-manifest SHA-256 values match. |
| Legacy PostgreSQL | A read-only repeatable-read transaction exports a shared snapshot for the native custom-format archive and catalog/data fingerprints. Fresh isolated native restore passes; all 48 table row counts and sorted canonical-JSON data digests match. |
| Renovated PostgreSQL | Read-only connector capture preserves schema/catalog and table data. Final metadata capture verifies unchanged fingerprints before reusing table payloads; bigint sequence bounds are read as text to preserve precision. The rebuilt database matches, is exported as a native PostgreSQL archive, and passes an independent fresh native restore and comparison. All 68 table fingerprints match. |
| Auth | All captured Auth tables restore with matching row digests, including four legacy users and one renovated-project user. No account records are public. |
| Storage | Legacy bucket/object metadata restores with the database. All 25 objects, 50,016,080 bytes total, are downloaded, source-verified and recovered into a fresh local destination with matching SHA-256 values. The renovated project has no buckets or objects. |

Both restores run in separate local containers with networking disabled and no published ports, using the official `supabase/postgres:17.6.1.011` image, digest `sha256:6291866b0f14119ba3c2e60231b9d893705b1d23b18607fd0738287cfeec8b39`. Live source versions are PostgreSQL 17.6 (legacy) and 17.11 (renovation); the actual logical restore compatibility is verified rather than inferred from version numbers.

| Source catalog comparison | Legacy | Renovation |
| --- | ---: | ---: |
| Schemas | 11 | 13 |
| Source roles / memberships | 15 / 21 | 23 / 36 |
| Extensions | 5 | 5 |
| Relations / sequences | 63 / 8 | 83 / 5 |
| Functions | 103 | 103 |
| Constraints / indexes | 154 / 105 | 242 / 104 |
| Triggers / policies | 8 / 12 | 27 / 32 |
| Default-privilege records / event triggers | 19 / 7 | 24 / 6 |
| Publications | 1 | 1 |

All comparisons pass, including table/column grants, ownership, role attributes, RLS/forced RLS, invoker-view options, function definitions, sequence state and publication definitions. The isolated recovery administrator is the local grantor; comparisons normalize that grantor identity. Extension bootstrap ownership is infrastructure-specific; extension name/schema/version and member-object ownership/grants match. Temporary local superuser ownership adapters are reverted to captured role attributes. Native dumps require the saved source privilege overlay because platform initial grants and extension-member ownership are not fully reproduced by a stock PostgreSQL bootstrap. Recovery scripts, overlays, captures and detailed comparison reports are retained privately with the archives.

Vault tables are empty in both captures. The renovated capture has no foreign tables, subscriptions, large objects or custom aggregates requiring a separate exporter. No database role passwords, Vercel secrets or signing keys are copied into the recovery set. Reconnecting services after a future live restore requires separately approved credential/provider configuration; this rehearsal establishes database and content recovery, not a new Auth/Storage service deployment or production rollback acceptance.

| Native archive | Bytes | SHA-256 |
| --- | ---: | --- |
| `legacy-full-consistent.dump` | 3,089,254 | `7058c953ae4243091f2e1e012b5968e28edfb54186fd36aa887c51c1be016a54` |
| `v2-full-verified.dump` | 495,618 | `e940a16808f50243093dd6ba2fecd7b9116c261788616214b6d450de50e00618` |

## Inspected and deployed baseline

The source baseline application ID is `com.germanverbmaster.android`, versionCode `29`, versionName `0.2.08`, matching the preserved sister project. This is a source baseline, not a Play Console maximum-version claim. Signing and the eventual higher release code remain the owner's release responsibility.

Read-only provider inventory confirms separate healthy legacy and renovated Supabase projects; no project was created, renamed, reset or migrated. The latest READY renovated production deployment is `dpl_21JAfzLXp6nbvca6aMWf8ZJAnpLx`, Git commit `f6b4087afacf39e76d768fef3b9ed83f7399075b`. Both custom domains remain verified on the v2 project. The canonical web returns HTTP 200 and unauthenticated `/v2/catalog` returns the expected HTTP 401. This identifies the deployed baseline and route; it does not replace M3 account/accessibility acceptance. The current local closure changes are documentation only and are not pushed or deployed. Existing application build/test evidence at `f6b4087` remains applicable to the unchanged application code.

## Closure and next milestone

M0 is complete: preserved histories/content, recoverable private database/Storage backups, isolated restore evidence, accepted repository baseline, distinct deployed/source identities and owner-managed signing/version boundaries are recorded. M1 is now the only active milestone. Resume the five B2 verb/preposition targets (10 typed variants), then finish remaining conversion, independent German review and design/accessibility acceptance. M2–M5 remain queued with partial work preserved; M6 has not started.

No production writes, hosted restores, destructive resets, content publication, deployments, store uploads, billing changes, external messages or AI/Groq calls occurred. Local recovery containers and temporary transfer/proxy processes are stopped after verification. Later operational/pilot milestones retain their own acceptance gates.

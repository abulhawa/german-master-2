# Legacy recovery rehearsal

Verified locally on 3 October 2026. This is source/history recovery evidence; it does not establish recovery of a live database, deployment or store release.

Both ignored bundles were cloned into disposable bare repositories using `git clone --bare`. `git fsck --full` completed successfully for each restored repository. The restored baseline tag trees exactly matched the same tags in the active monorepo:

| Bundle | Restored baseline | Tree SHA |
|---|---|---|
| `.local/legacy-web.bundle` | `legacy-web-baseline` | `7b1060039d3cd4f576654773a6c9ebe541e6c186` |
| `.local/legacy-android.bundle` | `legacy-android-baseline` | `9e3ae96565a99bea4007707d603b61b7c66fb21a` |

Reproduce from the root, selecting fresh destination directories if these already exist:

```powershell
git clone --bare .local/legacy-web.bundle .local/restore-web.git
git clone --bare .local/legacy-android.bundle .local/restore-android.git
git --git-dir=.local/restore-web.git fsck --full
git --git-dir=.local/restore-android.git fsck --full
git --git-dir=.local/restore-web.git rev-parse 'legacy-web-baseline^{tree}'
git --git-dir=.local/restore-android.git rev-parse 'legacy-android-baseline^{tree}'
git rev-parse 'legacy-web-baseline^{tree}' 'legacy-android-baseline^{tree}'
```

The bundles and restored repositories are local ignored recovery artifacts. Their presence on this workstation does not establish an off-machine backup. Git preserves tracked content and schema history; it does not capture live database rows, ignored secrets, signing keys or release assets.

Remaining M0 evidence: identify the deployed web/API revision, verify a non-destructive database/content export and isolated restore, verify the published Android version and signing continuity, and record content provenance. Those checks remain unverified; no production access, reset, deployment or store upload was performed during this rehearsal.

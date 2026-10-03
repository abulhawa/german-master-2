# German Master 2.0 agent instructions

- Read `docs/product/blueprint.md` and relevant ADRs before changing product behavior. `docs/adr/002-monorepo-bootstrap.md` records the accepted repository decision and transitional layout.
- This is the active monorepo foundation. Do not modify, archive or delete the two original repositories as part of routine work here.
- Search for scoped instructions case-insensitively. Instructions inside `apps/web` and `apps/android` still apply to their imported code. Root safety rules apply throughout this repository.
- Use PowerShell-compatible commands on Windows. Read or write secrets only in memory when strictly needed. Never print credentials, including commands suggested by historical setup documentation. Never commit `.env`, `local.properties`, signing keys or service-account credentials.
- Routine builds and tests must use local fixtures and mocks. Do not connect tests to production, run database-reset scripts, publish content, deploy, or upload store releases without authorization for that operation.
- Root npm workspace owns the TypeScript lockfile and inherited dependency overrides. Run `npm ci`, `npm run check`, `npm test` and the appropriate build from the repository root for web changes. Keep the Android wrapper and version catalog inside `apps/android`.
- For Android changes, run relevant unit tests, lint and debug assembly where the toolchain is available. Report unavailable checks precisely. Use emulators/devices for behavior that needs them; do not describe source inspection as runtime verification.
- New shared APIs use versioned contracts. Confirmed grading, mastery and scheduling belong to the future backend service. Do not add independent client mastery policies.
- UI/UX changes must satisfy the 2.0 blueprint and accessibility requirements. Existing web guidance applies to legacy code until explicitly superseded; record intentional differences in review descriptions.
- Preserve application identity, signing continuity, provenance and immutable exercise revision semantics.
- Do not use live AI services in normal checks. Groq quota and credential rules from the user's global guidance remain applicable.
- Include a proposed commit message when reporting code changes. Distinguish completed bootstrap work from the unimplemented product reset.

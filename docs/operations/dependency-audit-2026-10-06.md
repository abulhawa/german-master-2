# npm audit remediation — 6 October 2026

The root workspace audit initially reported 16 affected packages: one critical,
seven high and eight moderate. The final root `npm audit` reports zero findings
across production and development dependencies; no advisory is suppressed.

Direct upgrades are csv-parse 7.0.3, nanoid 5.1.16 and Vitest 4.1.11. The lockfile
also updates the affected baseline-browser-mapping, brace-expansion,
browserslist, fast-uri, ip-address, moment, proxy-addr, qs, source-map-js,
undici and nested nanoid dependencies, plus required dependency companions.

Typography 0.5.19 pins an affected selector parser. A root override scoped to
that package selects postcss-selector-parser 7.1.6 without the audit tool's
suggested typography downgrade. The generated web CSS/build and full tests
validate the affected code paths, including CSV loading. Application behavior
and shared contracts are unchanged.

Node remains the repository's portable 22.23.3. npm 10.9.9 failed during audit
fix and Vitest resolution with an internal `edgesOut` error. An isolated,
ignored npm 11 installation resolved the lockfile; npm 10 then successfully
installed and audited it. No npm runner or tool binary enters Git. The Windows
esbuild file lock was released by stopping only the verified compiler helper,
preserving the in-memory acceptance process and its disposable credentials.

Local evidence: clean root `npm ci --ignore-scripts --no-audit`, root generated
and TypeScript checks, root web/API build, and final audit pass. Full test
counts are recorded in PROGRESS.md after completion. These are local checks,
not evidence that the dependency updates are deployed.

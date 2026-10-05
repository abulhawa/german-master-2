# Live release inventory

Evidence captured on 5 October 2026 for the German Master 2.0 renovation.

This is a read-only inventory of the currently deployed web release. It does not authorize or perform a v2 deployment, domain change, environment-variable change, production cutover, rollback, signing action, store upload or data/schema mutation.

## Current Vercel project

- Team: `abulhawa's projects` (`team_c7iYKCXXF2s3AP8bnH5TWL4X`)
- Project: `german-master` (`prj_sr2SF6UGsRB6U8dpwukQEuJsPBaY`)
- Framework: Vite
- Node: 22.x
- Git integration: GitHub `abulhawa/german-master`
- The project is **not** linked to the v2 monorepo `abulhawa/german-master-2`.

The production project therefore remains part of the preserved legacy release path. The dedicated v2 learner artifact in the monorepo is not the live production route.

## Current production deployment

The latest READY production deployment reported by Vercel is:

- Deployment: `dpl_AuPHoBMVHU6M6bQZ2qE4Xce67K2M`
- Git branch: `main`
- Git repository: `abulhawa/german-master`
- Git commit: `30b2c9e21ba643264d113711cb30494e86a8cc6c`
- Commit message: `Update and secure npm dependencies`
- Rollback candidate: yes

A newer READY deployment exists in project history with no production target; it is not evidence of production cutover.

## Production domains

Verified production custom domains:

- `germanmaster.qortxai.com` — canonical production domain
- `gvm.qortxai.com` — verified redirect to `germanmaster.qortxai.com` using HTTP 307

Read-only fetch evidence on 5 October 2026:

- `https://germanmaster.qortxai.com/` returned HTTP 200 from Vercel.
- Its HTML identifies the product as German Master and serves the existing legacy Vite application.
- `https://germanmaster.qortxai.com/privacy` returned HTTP 200 and the existing German Master Android privacy policy.
- `https://gvm.qortxai.com/` returned HTTP 307 to the canonical domain.

## Release-routing conclusion

This evidence narrows the release gate:

1. The currently deployed production surface is identified and healthy.
2. Its exact Vercel project, Git source repository and production commit are known.
3. The canonical and redirect domains are known and verified.
4. German Master 2.0 is **not** currently routed to production.
5. A future v2 cutover must be explicit. It must not be inferred from building the v2 artifact or merging code into `german-master-2`.

## What this does not close

This inventory does not close M0 or M3 by itself. Remaining evidence/actions include:

- define and approve the v2 Vercel project/release routing or an explicit migration of the existing project;
- verify required staging/production public configuration without exposing secrets;
- deploy only after separate owner authorization;
- verify the deployed v2 revision and API/environment parity;
- retain a tested rollback target and rollback procedure;
- complete the remaining account, reviewed-content and acceptance gates.

The production deployment identified above is a known rollback reference for the legacy web surface, but no rollback operation was performed.

## Safety boundary

No Vercel project, deployment, domain, environment variable, Git integration, protection setting or alias was changed while collecting this evidence.

# Deepmerge: local security-patched dependency

This directory is a narrowly scoped vendored copy of upstream `deepmerge` v4.3.1 (MIT) with two guards added in `mergeObject` to reject own `__proto__` keys while cloning both target and source. This prevents a parsed JSON `__proto__` property from altering the returned object's prototype (CVE-2026-93753 / GHSA-gcqx-w48m-w2h4).

It is used only via the root npm override for `@rollup/plugin-node-resolve`, which is pulled in by `vite-plugin-pwa -> workbox-build` during builds. It is not an application runtime dependency. Do not publish this fork; replace with a fixed upstream version or a reviewed new PWA build chain when available.

Upstream source: https://github.com/TehShrike/deepmerge/tree/v4.3.1
Original authors and MIT license are preserved in LICENSE. The changed version string is strictly internal; **4.3.2-gm.0 is not an upstream release**.

Verify with `npm run security:deepmerge` at repository root after `npm ci`, and run the existing offline-shell browser acceptance tests. Changes to this copy require a focused regression test.

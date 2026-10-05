import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { VitePWA } from 'vite-plugin-pwa';
import { fileURLToPath } from 'node:url';

const fromHere = (relative: string) => fileURLToPath(new URL(relative, import.meta.url));

// Explicit local fixture build. Never selected by the release build/deployment.
export default defineConfig({
  root: fromHere('./learner-preview'),
  base: '/learner-preview/',
  publicDir: false,
  plugins: [react(), VitePWA({
    scope: '/learner-preview/',
    registerType: 'prompt',
    injectRegister: false,
    manifest: false,
    workbox: {
      cacheId: 'german-master-v2-local-shell',
      globPatterns: ['**/*.{js,css,html}'],
      navigateFallback: '/learner-preview/index.html',
      navigateFallbackAllowlist: [/^\/learner-preview\/(?:index\.html)?$/],
      cleanupOutdatedCaches: true,
      // No API/runtime caching. Owned packs belong in validated IndexedDB.
      runtimeCaching: [],
      skipWaiting: false,
      clientsClaim: false,
    },
  })],
  resolve: { alias: { '@': fromHere('./client/src') } },
  build: { outDir: fromHere('./dist/learner-preview'), emptyOutDir: true },
  preview: {
    host: '127.0.0.1', port: 5010, strictPort: true,
    proxy: { '/v2': { target: 'http://127.0.0.1:5011' } },
  },
});

/// <reference lib="webworker" />
import { setCacheNameDetails } from 'workbox-core';
import { matchPrecache, precacheAndRoute } from 'workbox-precaching';
import { registerRoute } from 'workbox-routing';
import { NetworkFirst } from 'workbox-strategies';

declare const self: ServiceWorkerGlobalScope & { __WB_MANIFEST: Array<string | { url: string; revision?: string | null }> };

setCacheNameDetails({ prefix: 'german-master-v2-product-shell' });

// Activate the new network-first shell without navigating open documents.
// Drafts, account storage and unrelated caches are never removed here.
self.addEventListener('install', event => event.waitUntil(self.skipWaiting()));

const navigation = new NetworkFirst({
  cacheName: 'german-master-v2-product-navigation',
  networkTimeoutSeconds: 3,
});
// Register ahead of precache routes: online refresh must obtain current HTML.
registerRoute(
  ({ request, url }) => request.mode === 'navigate' && url.origin === self.location.origin
    && !/^\/(?:api|v2)(?:\/|$)/.test(url.pathname),
  async context => {
    try { return await navigation.handle(context); }
    catch { return await matchPrecache('/index.html') ?? Response.error(); }
  },
);
precacheAndRoute(self.__WB_MANIFEST);

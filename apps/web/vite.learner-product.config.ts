import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { VitePWA } from 'vite-plugin-pwa';
import { fileURLToPath } from 'node:url';

const fromHere=(relative:string)=>fileURLToPath(new URL(relative,import.meta.url));
// Separate artifact; deployment selection and cutover still require authorization.
export default defineConfig({
  root:fromHere('./learner-product'),base:'/',publicDir:false,
  plugins:[react(),VitePWA({scope:'/',registerType:'prompt',injectRegister:false,manifest:false,
    workbox:{cacheId:'german-master-v2-product-shell',globPatterns:['**/*.{js,css,html}'],
      navigateFallback:'/index.html',navigateFallbackDenylist:[/^\/v2(?:\/|$)/,/^\/api(?:\/|$)/],
      cleanupOutdatedCaches:false,runtimeCaching:[],skipWaiting:false,clientsClaim:false}})],
  resolve:{alias:{'@':fromHere('./client/src')}},
  build:{outDir:fromHere('./dist/learner-product'),emptyOutDir:true},
  preview:{host:'127.0.0.1',port:5012,strictPort:true},
});

import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";

// No client asset rewriting: the real server reads migrations through file URLs.
export default defineConfig({
  plugins: [react()],
  test: {
    environment: "node", maxWorkers: 1, testTimeout: 30000, hookTimeout: 30000,
    setupFiles: ["apps/web/vitest.setup.ts"],
    include: ["apps/web/client/src/**/*.integration.test.tsx"],
  },
});

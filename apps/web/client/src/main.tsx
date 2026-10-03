import { StrictMode, Suspense, lazy } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { registerSW } from "virtual:pwa-register";

const FoundationPreview = lazy(() => import("./foundation/preview"));
const BackendPreview = lazy(() => import("./foundation/backend-preview"));
const App = lazy(() => import("./App"));

const foundation = import.meta.env.DEV && window.location.pathname === "/foundation";
if (!foundation) registerSW({ immediate: true });

const rootElement = document.getElementById("root");

if (!rootElement) {
  throw new Error("Root element with id 'root' was not found in the document.");
}

createRoot(rootElement).render(
  <StrictMode>
    <Suspense fallback={null}>
      {foundation ? (window.location.search === "?backend=1" ? <BackendPreview /> : <FoundationPreview />) : <App />}
    </Suspense>
  </StrictMode>,
);

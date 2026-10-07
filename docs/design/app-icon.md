# German Master app icon

The website and Android launcher now share a white, rounded lowercase “ü” on the existing primary blue. The geometric mark is authored in `design/app-icon.svg`; it uses no font dependency or AI-generated image. `tools/generate-app-icons.mjs` renders the committed PNG/WebP assets using Sharp (an authoring-only dependency, supplied as an optional module entry path). Normal builds use the checked-in assets.

Web assets include SVG/64px PNG favicons, a 180px Apple touch icon, 192/512px install icons and a full-background 512px maskable icon. Both legacy and v2 entry points reference them, and the v2 wordmark displays the same mark; the dedicated v2 build now actually includes its public assets and manifest. Versioned favicon URLs distinguish the icon from previously cached files. The manifest ID/start URL preserves the root app identity.

Android includes regenerated five-density normal/round raster icons, a vector adaptive foreground, the blue background and the same alpha-only shape for themed monochrome icons. The adaptive foreground is scaled into the central safe region. The source store-listing icon is updated; no store upload or release occurred. Package ID, version/signing configuration and learner data are unchanged.

Verification includes source-asset visual inspection, production-manifest PNG dimensions, Android unit tests/lint/debug and learner-preview assemblies. This does not establish physical launcher rendering or an installed app update.

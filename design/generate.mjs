import fs from "node:fs";
import path from "node:path";
const root = path.resolve(import.meta.dirname, "..");
const t = JSON.parse(
  fs.readFileSync(path.join(root, "design/tokens.json"), "utf8"),
);
const luminance = (hex) => {
  const c=hex.slice(1).match(/../g).map(v=>parseInt(v,16)/255).map(v=>v<=0.04045?v/12.92:((v+0.055)/1.055)**2.4);
  return c[0]*0.2126+c[1]*0.7152+c[2]*0.0722;
};
for(const theme of [t.light,t.dark]) {
  for(const [fg,bg,min] of [['text','surface',4.5],['secondary','background',4.5],['primary','onPrimary',4.5],['controlBorder','surface',3],['primary','surface',3],['success','surface',4.5],['attention','surface',4.5],['error','surface',4.5]]) {
    const a=luminance(theme[fg]), b=luminance(theme[bg]);
    const contrast=(Math.max(a,b)+0.05)/(Math.min(a,b)+0.05);
    if(contrast<min) throw Error(`Contrast failure: ${fg}/${bg} = ${contrast}`);
  }
}
const vars = (o) =>
  Object.entries(o)
    .map(([k, v]) => `  --gm-${k}: ${v};`)
    .join("\n");
const css = `/* Generated from design/tokens.json. */\n.gm-foundation {\n${vars(t.light)}\n${t.spacing.map((v) => `  --gm-space-${v}: ${v}px;`).join("\n")}\n  --gm-control-min: ${t.controlMin}px;\n  --gm-practice-max: ${t.practiceMax}px;\n  --gm-radius: ${t.radius}px;\n}\n@media (prefers-color-scheme: dark) { .gm-foundation {\n${vars(t.dark)}\n} }\n.gm-foundation[data-theme="dark"] {\n${vars(t.dark)}\n}\n.gm-foundation[data-theme="light"] {\n${vars(t.light)}\n}\n`;
const colors = (o) =>
  Object.entries(o)
    .map(([k, v]) => `        ${k} = Color(0xFF${v.slice(1)}),`)
    .join("\n");
const kt = `// Generated from design/tokens.json.\npackage com.germanverbmaster.android.foundation\n\nimport androidx.compose.ui.graphics.Color\n\ndata class FoundationColors(\n${Object.keys(
  t.light,
)
  .map((k) => `    val ${k}: Color`)
  .join(
    ",\n",
  )}\n)\nobject FoundationTokens {\n    const val controlMin = ${t.controlMin}\n    const val practiceMax = ${t.practiceMax}\n    const val radius = ${t.radius}\n    val spacing = listOf(${t.spacing.join(", ")})\n    val light = FoundationColors(\n${colors(t.light)}\n    )\n    val dark = FoundationColors(\n${colors(t.dark)}\n    )\n}\n`;
for (const [f, s] of [
  ["design/generated/tokens.css", css],
  [
    "apps/android/app/src/main/java/com/germanverbmaster/android/foundation/Tokens.kt",
    kt,
  ],
]) {
  const p = path.join(root, f);
  if (process.argv.includes("--check")) {
    if (!fs.existsSync(p) || fs.readFileSync(p, "utf8") !== s)
      throw Error("Token drift: " + f);
  } else {
    fs.mkdirSync(path.dirname(p), { recursive: true });
    fs.writeFileSync(p, s);
  }
}
console.log("Design token generation verified.");

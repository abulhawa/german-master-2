import { createHash } from "node:crypto";
import { execFileSync } from "node:child_process";
import { mkdir, readFile, writeFile } from "node:fs/promises";
import { dirname, resolve } from "node:path";

const root = process.cwd();
const outputFlag = process.argv.indexOf("--output");
const outputPath =
  outputFlag >= 0 && process.argv[outputFlag + 1]
    ? resolve(root, process.argv[outputFlag + 1])
    : null;

const requiredFiles = {
  androidGradle: "apps/android/app/build.gradle",
  webVercel: "apps/web/vercel.json",
  stagingPlan: "docs/operations/v2-staging-plan.json",
  baseline: "db/baseline/v2.sql",
  backendAccess: "db/baseline/backend-access.sql",
  authAccess: "db/baseline/auth-session-access.sql",
  gitignore: ".gitignore",
};

const failures = [];
function requireInvariant(condition, message) {
  if (!condition) failures.push(message);
}

async function text(path) {
  return readFile(resolve(root, path), "utf8");
}

async function sha256(path) {
  const bytes = await readFile(resolve(root, path));
  return createHash("sha256").update(bytes).digest("hex");
}

function capture(source, expression, label) {
  const match = source.match(expression);
  requireInvariant(Boolean(match), `Could not read ${label}`);
  return match?.[1] ?? null;
}

const [
  androidGradle,
  webVercelText,
  stagingPlanText,
  gitignore,
] = await Promise.all([
  text(requiredFiles.androidGradle),
  text(requiredFiles.webVercel),
  text(requiredFiles.stagingPlan),
  text(requiredFiles.gitignore),
]);

const webVercel = JSON.parse(webVercelText);
const stagingPlan = JSON.parse(stagingPlanText);

const applicationId = capture(
  androidGradle,
  /applicationId\s*=\s*"([^"]+)"/,
  "Android applicationId",
);
const versionCodeRaw = capture(
  androidGradle,
  /versionCode\s*=\s*(\d+)/,
  "Android versionCode",
);
const versionName = capture(
  androidGradle,
  /versionName\s*=\s*"([^"]+)"/,
  "Android versionName",
);
const versionCode = versionCodeRaw ? Number(versionCodeRaw) : null;

requireInvariant(
  applicationId === "com.germanverbmaster.android",
  "Android applicationId must preserve com.germanverbmaster.android",
);
requireInvariant(
  Number.isInteger(versionCode) && versionCode > 0,
  "Android versionCode must be a positive integer",
);
requireInvariant(
  stagingPlan.projectName === "german-master-v2-staging",
  "Staging project name changed unexpectedly",
);
requireInvariant(
  stagingPlan.projectRef === "zgmyrpzwgtydwlzponih",
  "Staging project reference changed unexpectedly",
);
requireInvariant(
  stagingPlan.region === "eu-central-1",
  "Staging region must remain Frankfurt/eu-central-1 unless separately reviewed",
);
requireInvariant(
  stagingPlan.exposeLearningSchemaToDataApi === false,
  "The learning schema must remain outside the Supabase Data API",
);
requireInvariant(
  stagingPlan.importLegacyData === false,
  "Legacy learner data import must remain disabled for the v2 reset",
);
requireInvariant(
  stagingPlan.productionCutover === false,
  "Release-readiness checks must not imply production cutover approval",
);

for (const ignored of [
  ".env",
  "local.properties",
  "play-service-account.json",
  "*.jks",
  "*.keystore",
]) {
  requireInvariant(
    gitignore.split(/\r?\n/).includes(ignored),
    `Secret-bearing file pattern is not ignored: ${ignored}`,
  );
}

const hashes = {};
for (const [name, path] of Object.entries({
  cleanBaseline: requiredFiles.baseline,
  backendRolePolicy: requiredFiles.backendAccess,
  authVerifierPolicy: requiredFiles.authAccess,
  stagingPlan: requiredFiles.stagingPlan,
})) {
  hashes[name] = { path, sha256: await sha256(path) };
}

let commit = process.env.GITHUB_SHA ?? null;
if (!commit) {
  try {
    commit = execFileSync("git", ["rev-parse", "HEAD"], {
      cwd: root,
      encoding: "utf8",
      stdio: ["ignore", "pipe", "ignore"],
    }).trim();
  } catch {
    commit = "unknown";
  }
}

const manifest = {
  schemaVersion: 1,
  status: failures.length === 0 ? "guardrails-passed" : "guardrails-failed",
  source: {
    commit,
    ref: process.env.GITHUB_REF ?? null,
  },
  android: {
    applicationId,
    versionCode,
    versionName,
    inspectedLegacyVersionCode: 29,
    publishedMaximumVersionCode: null,
    publishedMaximumGate:
      "unverified-live-evidence-required-before-store-upload",
  },
  staging: {
    projectName: stagingPlan.projectName,
    projectRef: stagingPlan.projectRef,
    region: stagingPlan.region,
    schemaStatus: stagingPlan.status,
    learningSchemaExposedToDataApi: stagingPlan.exposeLearningSchemaToDataApi,
    importsLegacyData: stagingPlan.importLegacyData,
    productionCutoverApproved: stagingPlan.productionCutover,
  },
  web: {
    legacyMainAutoDeploy:
      webVercel?.git?.deploymentEnabled?.main === true,
    releaseRoutingGate:
      "separate-staging-and-production-routing-approval-required",
  },
  immutableInputs: hashes,
  unresolvedExternalGates: [
    "live Play published maximum versionCode and signing/key custody",
    "approved staging credentials and clean schema application",
    "live staging connection, advisors, contention and load acceptance",
    "independent German content and design review",
    "private off-machine backup and full restore rehearsal",
    "deployment, store upload and production cutover approval",
  ],
  failures,
};

const rendered = JSON.stringify(manifest, null, 2) + "\n";
process.stdout.write(rendered);

if (outputPath) {
  await mkdir(dirname(outputPath), { recursive: true });
  await writeFile(outputPath, rendered, "utf8");
}

if (failures.length > 0) {
  process.exitCode = 1;
}

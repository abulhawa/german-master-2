$ErrorActionPreference = 'Stop'

# Supply public v2 configuration in this process's environment. Normal offline
# checks may build an unconfigured fixture; that artifact must not be installed
# as the learner's authenticated preview.
foreach ($name in @('GM_V2_AUTH_PROJECT', 'GM_V2_AUTH_PUBLISHABLE_KEY', 'GM_V2_API_ORIGIN')) {
    if ([string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($name))) {
        throw "Missing public preview configuration: $name"
    }
}
$androidRoot = Join-Path $PSScriptRoot '../../apps/android'
Push-Location $androidRoot
try {
    & .\gradlew.bat :app:verifyV2ReleaseConfiguration :app:assembleLearnerPreview --offline --no-daemon --max-workers=2
    if ($LASTEXITCODE -ne 0) { throw 'Configured preview build failed.' }
    Copy-Item -LiteralPath 'app/build/outputs/apk/learnerPreview/app-learnerPreview.apk' `
        -Destination 'app/build/outputs/apk/learnerPreview/app-learnerPreview-configured.apk'
    Write-Output 'Configured learner preview built. Use app-learnerPreview-configured.apk for replacement installation.'
} finally {
    Pop-Location
}

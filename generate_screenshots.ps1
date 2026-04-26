# PowerShell script to generate Play Store screenshots
Write-Host "Starting automated screenshot generation..." -ForegroundColor Cyan

# Run the Gradle task
.\gradlew :app:generatePlayStoreScreenshots

if ($LASTEXITCODE -eq 0) {
    Write-Host "Screenshots generated successfully!" -ForegroundColor Green
    Write-Host "Location: play-store-listing/" -ForegroundColor Gray
} else {
    Write-Host "Failed to generate screenshots. Check the logs above." -ForegroundColor Red
}

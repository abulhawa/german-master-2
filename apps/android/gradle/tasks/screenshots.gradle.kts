tasks.register<Test>("generatePlayStoreScreenshots") {
    group = "publishing"
    description = "Generates and saves screenshots for the Play Store using Roborazzi"
    
    // Run only StoreScreenshotTest
    filter {
        includeTestsMatching("*StoreScreenshotTest*")
    }

    // Enable Roborazzi recording
    systemProperty("roborazzi.test.record", "true")
    
    // Ensure it runs even if tests are up to date
    outputs.upToDateWhen { false }
}

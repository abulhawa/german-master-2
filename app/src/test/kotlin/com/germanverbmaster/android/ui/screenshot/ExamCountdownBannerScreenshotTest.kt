package com.germanverbmaster.android.ui.screenshot

import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.germanverbmaster.android.ui.components.ExamCountdownBanner
import com.germanverbmaster.android.ui.theme.GermanVerbMasterTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ExamCountdownBannerScreenshotTest {
    // Record: .\gradlew :app:testDebugUnitTest --tests "*ScreenshotTest" -Droborazzi.test.record=true
    // Verify: .\gradlew :app:testDebugUnitTest --tests "*ScreenshotTest" -Droborazzi.test.verify=true

    @get:Rule
    val roborazziRule = RoborazziRule()

    @Test
    fun exam_countdown_banner_light() {
        captureRoboImage(
            filePath = "src/test/screenshots/exam_countdown_banner_light.png",
            content = {
                GermanVerbMasterTheme(darkTheme = false) {
                    ExamCountdownBanner(examDate = LocalDate.now().plusDays(30))
                }
            },
        )
    }

    @Test
    fun exam_countdown_banner_dark() {
        captureRoboImage(
            filePath = "src/test/screenshots/exam_countdown_banner_dark.png",
            content = {
                GermanVerbMasterTheme(darkTheme = true) {
                    ExamCountdownBanner(examDate = LocalDate.now().plusDays(30))
                }
            },
        )
    }
}

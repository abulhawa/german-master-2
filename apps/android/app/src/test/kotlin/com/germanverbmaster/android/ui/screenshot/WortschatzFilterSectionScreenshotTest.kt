package com.germanverbmaster.android.ui.screenshot

import com.germanverbmaster.android.ui.theme.GermanVerbMasterTheme
import com.germanverbmaster.android.ui.wortschatz.FilterSection
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class WortschatzFilterSectionScreenshotTest {
    @get:Rule
    val roborazziRule = RoborazziRule()

    @Test
    fun filter_section_header_with_word_count_light() {
        captureRoboImage(
            filePath = "src/test/screenshots/wortschatz_filter_section_header_light.png",
            content = {
                GermanVerbMasterTheme(darkTheme = false) {
                    FilterSection(
                        selectedLevels = setOf("B1"),
                        onLevelToggle = {},
                        selectedPosSet = setOf("N"),
                        onPosToggle = {},
                        posOptions = listOf("Alle", "N", "V"),
                        wordCount = 1668,
                        onDismiss = {},
                    )
                }
            },
        )
    }
}

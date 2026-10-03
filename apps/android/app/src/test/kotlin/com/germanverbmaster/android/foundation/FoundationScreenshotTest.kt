package com.germanverbmaster.android.foundation

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
@Config(sdk=[35])
class FoundationScreenshotTest {
    @get:Rule val roborazziRule = RoborazziRule()
    @Test fun sharedSessionNativePreview() {
        val session=ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("session.json")).readText())
        captureRoboImage(filePath="build/outputs/foundation/preview.png", content={ FoundationTheme { FoundationPreview(session) } })
    }
}

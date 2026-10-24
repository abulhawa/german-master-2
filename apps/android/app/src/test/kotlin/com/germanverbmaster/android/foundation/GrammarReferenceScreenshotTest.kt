package com.germanverbmaster.android.foundation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.germanverbmaster.android.learner.NativeGuestEntry
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk=[35],qualifiers="w393dp-h852dp-notnight-420dpi")
class GrammarReferenceScreenshotTest {
    @get:Rule val compose=createComposeRule()
    @get:Rule val folder=TemporaryFolder()
    private fun open(german:Boolean=false) {
        compose.setContent {FoundationTheme {NativeGuestEntry(File(folder.root,"practice"),german,{}, {})}}
        compose.onNodeWithText(if(german)"Grammatik" else "Grammar",substring=false).performScrollTo().performClick()
        compose.waitForIdle()
    }
    private fun capture(name:String) {compose.onAllNodes(isRoot()).onLast().captureRoboImage("build/outputs/grammar-reference/$name.png")}
    @Test fun menu() {open();capture("menu")}
    @Test fun adjectiveTable() {
        open();compose.onNodeWithText("Adjective endings",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("Adjektivdeklination: Bestimmter Artikel").assertExists()
        compose.waitForIdle();capture("adjectives")
    }
    @Test @Config(qualifiers="w320dp-h720dp-night-320dpi")
    fun narrowGermanMenu() {open(true);capture("menu-german-dark-320")}
}

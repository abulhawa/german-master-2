package com.germanverbmaster.android.foundation

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import android.content.res.Configuration
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.germanverbmaster.android.foundation.contract.*
import com.germanverbmaster.android.learner.nativeAnswerReady
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LowTypingInputTest {
    @get:Rule val compose = createComposeRule()
    private val session = ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("practice-formats-session.json")).readText())
    @Test fun dragUsesMeasuredDropTargetWithoutRemovingTokenOrMovingControls() {
        val exercise = ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("session.json")).readText()).questions[3].exercise as ExerciseWordOrder
        val ids = exercise.tokens.take(3).map { it.id }
        var order by mutableStateOf(ids)
        compose.setContent { FoundationTheme { Column { LowTypingInput(exercise,null,order,onDraft={},onOrder={order=it}) } } }
        val first = exercise.tokens.first { it.id == ids[0] }.text
        val last = exercise.tokens.first { it.id == ids[2] }.text
        val start = compose.onNodeWithContentDescription("1. $first").fetchSemanticsNode().boundsInRoot
        val end = compose.onNodeWithContentDescription("3. $last").fetchSemanticsNode().boundsInRoot
        val resetY = compose.onNodeWithText("Reset order").fetchSemanticsNode().boundsInRoot.top
        compose.onNodeWithContentDescription("1. $first").performTouchInput {
            swipe(center, end.center - start.topLeft, 600)
        }
        compose.runOnIdle { assertEquals(listOf(ids[1],ids[2],ids[0]),order) }
        assertEquals(resetY,compose.onNodeWithText("Reset order").fetchSemanticsNode().boundsInRoot.top,0.1f)
        compose.onNodeWithContentDescription("3. $first").performClick()
        compose.runOnIdle { assertEquals(listOf(ids[1],ids[2]),order) }
        assertEquals(resetY,compose.onNodeWithText("Reset order").fetchSemanticsNode().boundsInRoot.top,0.1f)
    }
    @Test fun cancelledDragAndReadOnlyGesturesDoNotChangeOrder() {
        val exercise = ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("session.json")).readText()).questions[3].exercise as ExerciseWordOrder
        val ids = exercise.tokens.take(3).map { it.id }
        var order by mutableStateOf(ids)
        var locked by mutableStateOf(false)
        compose.setContent { FoundationTheme { Column { LowTypingInput(exercise,null,order,onDraft={},onOrder={order=it},readOnly=locked) } } }
        val label = "1. ${exercise.tokens.first {it.id==ids[0]}.text}"
        compose.onNodeWithContentDescription(label).performTouchInput {
            down(center); moveBy(androidx.compose.ui.geometry.Offset(100f,0f)); cancel()
        }
        compose.runOnIdle { assertEquals(ids,order);locked=true }
        compose.onNodeWithContentDescription(label).performTouchInput { swipe(center,center + androidx.compose.ui.geometry.Offset(150f,0f),600) }
        compose.runOnIdle { assertEquals(ids,order) }
    }
    @Test fun gapChoicesCanBeChangedWithoutTyping() {
        val exercise = session.questions[2].exercise
        var draft by mutableStateOf<Answer?>(null)
        compose.setContent { FoundationTheme { Column(Modifier.verticalScroll(rememberScrollState())) { LowTypingInput(exercise,draft,onDraft = {draft=it}) } } }
        compose.onNodeWithText("arbeiten").performClick()
        assertFalse(nativeAnswerReady(exercise,draft))
        compose.onNodeWithText("gehst").performClick()
        assertTrue(nativeAnswerReady(exercise,draft))
        compose.onNodeWithText("geht").performClick()
        assertEquals("a",(draft as AnswerGapChoice).selections.first {it.slotId == "du"}.optionId)
    }
    @Test fun matchingSupportsReassignmentAndRemovingPairs() {
        val exercise = session.questions[3].exercise
        var draft by mutableStateOf<Answer?>(null)
        compose.setContent { FoundationTheme { Column(Modifier.verticalScroll(rememberScrollState())) { LowTypingInput(exercise,draft,onDraft = {draft=it}) } } }
        fun pair(left:String,right:String) { compose.onNodeWithText(left).performScrollTo().performClick();compose.onNodeWithText(right).performScrollTo().performClick() }
        pair("eine Entscheidung","treffen");pair("eine Frage","stellen");pair("Verantwortung","übernehmen")
        assertTrue(nativeAnswerReady(exercise,draft))
        pair("eine Frage","treffen")
        assertFalse(nativeAnswerReady(exercise,draft))
        compose.onNodeWithText("Remove: eine Frage → treffen").performScrollTo().performClick()
        assertEquals(listOf(MatchPair("responsibility","take")),(draft as AnswerMatching).pairs)
    }
    @Test fun matchingRemainsUsableAtDoubleFontScaleInANarrowColumn() {
        val exercise = session.questions[3].exercise
        var draft by mutableStateOf<Answer?>(null)
        compose.setContent {
            val density = LocalDensity.current
            val config = Configuration(LocalConfiguration.current).apply { fontScale = 2f }
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f), LocalConfiguration provides config) {
                FoundationTheme {
                    Column(Modifier.width(320.dp).verticalScroll(rememberScrollState())) {
                        LowTypingInput(exercise,draft,onDraft = {draft=it})
                    }
                }
            }
        }
        fun pair(left:String,right:String) {
            compose.onNodeWithText(left).performScrollTo().assertIsDisplayed().performClick()
            compose.onNodeWithText(right).performScrollTo().assertIsDisplayed().assertHeightIsAtLeast(48.dp).performClick()
        }
        pair("eine Entscheidung","treffen");pair("eine Frage","stellen");pair("Verantwortung","übernehmen")
        assertTrue(nativeAnswerReady(exercise,draft))
        compose.onNodeWithText("Remove: Verantwortung → übernehmen").performScrollTo().assertIsDisplayed().performClick()
        assertFalse(nativeAnswerReady(exercise,draft))
    }
}

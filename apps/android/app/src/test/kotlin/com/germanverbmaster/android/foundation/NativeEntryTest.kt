package com.germanverbmaster.android.foundation

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.foundation.contract.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[35])
class NativeEntryTest {
    @get:Rule val compose=createComposeRule()
    @Test fun guestStartsWithoutAuthAndRestoresDraft() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val path=File(context.cacheDir,"guest-${java.util.UUID.randomUUID()}")
        var requested:String?=null
        compose.setContent {FoundationTheme {NativeGuestEntry(path,false,{}, {requested=it})}}
        compose.onNodeWithText("Start practising").performScrollTo().performClick()
        compose.onNodeWithText("Berufe").performScrollTo().performClick()
        compose.onNodeWithText("Create account").assertDoesNotExist()
        compose.onNodeWithText("Practise German. No account needed.").assertDoesNotExist()
        assertNull(requested)
        assertEquals(AnswerShortAnswer("Berufe"),NativeGuestStore(path).read().draft)
        compose.onNodeWithText("Check answer").performScrollTo().performClick()
        assertEquals("correct",NativeGuestStore(path).read().feedback?.outcome)
        compose.onNodeWithText("Continue").performScrollTo().performClick()
        assertEquals(1,NativeGuestStore(path).read().index)
    }
    @Test fun pausePreservesDraftAndResumeReturnsToQuestion() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val path=File(context.cacheDir,"guest-${java.util.UUID.randomUUID()}")
        compose.setContent {FoundationTheme {NativeGuestEntry(path,false,{}, {})}}
        compose.onNodeWithText("Start practising").performScrollTo().performClick()
        compose.onNodeWithText("Berufe").performScrollTo().performClick()
        compose.onNodeWithText("Pause").performScrollTo().performClick()
        compose.onNodeWithText("Create account").assertExists()
        assertEquals(AnswerShortAnswer("Berufe"),NativeGuestStore(path).read().draft)
        compose.onNodeWithText("Continue practice").performScrollTo().performClick()
        compose.onNodeWithText("Question 1 of 5").assertExists()
        compose.onNodeWithText("Check answer").assertIsEnabled()
    }
    @Test fun completionRestoresAccountActionsWithoutDiscardingResults() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val path=File(context.cacheDir,"guest-${java.util.UUID.randomUUID()}")
        NativeGuestStore(path).write(NativeGuestState(started=true,index=5,graded=4,correct=3))
        var requested:String?=null
        compose.setContent {FoundationTheme {NativeGuestEntry(path,false,{}, {requested=it})}}
        compose.onNodeWithText("Starter complete: 3 of 4 correct locally.").assertExists()
        compose.onNodeWithText("Create account").performScrollTo().performClick()
        assertEquals("register",requested)
        assertEquals(3,NativeGuestStore(path).read().correct)
    }
    @Test fun guestMultiSlotFirstChoiceSurvivesRestartAndRequiresBothSlots() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val path=File(context.cacheDir,"guest-${java.util.UUID.randomUUID()}")
        NativeGuestStore(path).write(NativeGuestState(started=true,index=4))
        var generation by mutableStateOf(0)
        compose.setContent {key(generation) {FoundationTheme {NativeGuestEntry(path,false,{}, {})}}}
        compose.onNodeWithText("arbeitest",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("Check answer").assertIsNotEnabled()
        val partial=NativeGuestStore(path).read().draft as AnswerMultiSlot
        assertEquals(listOf(SlotValue("du","arbeitest"),SlotValue("ihr","")),partial.values)
        compose.runOnIdle {generation++}
        compose.onNodeWithText("Check answer").assertIsNotEnabled()
        assertEquals(partial,NativeGuestStore(path).read().draft)
        compose.onAllNodesWithText("arbeitet",substring=false).onLast().performScrollTo().performClick()
        compose.onNodeWithText("Check answer").assertIsEnabled().performScrollTo().performClick()
        assertEquals("correct",NativeGuestStore(path).read().feedback?.outcome)
    }
    @Test fun guestMultiSlotCanStartWithSecondSlotAndChangeFirstChoice() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val path=File(context.cacheDir,"guest-${java.util.UUID.randomUUID()}")
        NativeGuestStore(path).write(NativeGuestState(started=true,index=4))
        compose.setContent {FoundationTheme {NativeGuestEntry(path,false,{}, {})}}
        compose.onAllNodesWithText("arbeitet",substring=false).onLast().performScrollTo().performClick()
        compose.onNodeWithText("Check answer").assertIsNotEnabled()
        compose.onNodeWithText("arbeits",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("arbeitest",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("Check answer").assertIsEnabled()
        assertEquals(listOf(SlotValue("du","arbeitest"),SlotValue("ihr","arbeitet")),(NativeGuestStore(path).read().draft as AnswerMultiSlot).values)
    }
    @Test fun resetNeedsOnlyEmailAndUsesExplicitSubmit() {
        var submitted:List<String>?=null
        compose.setContent {FoundationTheme {NativeAccountEntry("reset",false,{}, {}, {a,e,p->submitted=listOf(a,e,p)})}}
        compose.onNodeWithText("Password").assertDoesNotExist()
        compose.onNodeWithText("Email").performTextInput("learner@example.test")
        assertNull(submitted)
        compose.onNodeWithText("Send reset email").performScrollTo().performClick()
        compose.waitForIdle()
        assertEquals(listOf("reset","learner@example.test",""),submitted)
        compose.onNodeWithText("Send reset email").assertIsNotEnabled()
    }
    @Test fun registrationRequiresMatchingPasswordsAndClearsThem() {
        var submitted:List<String>?=null
        compose.setContent {FoundationTheme {NativeAccountEntry("register",false,{}, {}, {a,e,p->submitted=listOf(a,e,p)})}}
        compose.onNodeWithText("Email").performTextInput("learner@example.test")
        compose.onNodeWithText("Password").performTextInput("synthetic-password")
        compose.onAllNodesWithText("Create account").filter(hasClickAction()).onFirst().assertIsNotEnabled()
        compose.onNodeWithText("Confirm password").performTextInput("synthetic-password")
        compose.onAllNodesWithText("Create account").filter(hasClickAction()).onFirst().performScrollTo().performClick()
        compose.waitForIdle()
        assertEquals(listOf("register","learner@example.test","synthetic-password"),submitted)
        compose.onNodeWithText("Password").assert(SemanticsMatcher.expectValue(
            androidx.compose.ui.semantics.SemanticsProperties.EditableText,androidx.compose.ui.text.AnnotatedString("")))
    }
    @Test fun damagedGuestRecordIsPreserved() {
        val path=File.createTempFile("gm-guest", ".json")
        path.writeText("damaged")
        assertTrue(runCatching {NativeGuestStore(path).read()}.isFailure)
        assertEquals("damaged",path.readText())
        path.delete()
    }
}

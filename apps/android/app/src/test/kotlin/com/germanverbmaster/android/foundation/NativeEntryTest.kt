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
    private fun seed(path:File,index:Int=0,graded:Int=0,correct:Int=0) {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val starter=ContractReader.json.decodeFromString<GuestStarterContent>(context.assets.open("starter.json").bufferedReader().use {it.readText()})
        NativeGuestStore(path).write(NativeGuestState(started=true,index=index,graded=graded,correct=correct,session=starter.session))
    }
    @Test fun highResolutionClockProducesAValidGuestAttemptTimestamp() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val starter=ContractReader.json.decodeFromString<GuestStarterContent>(context.assets.open("starter.json").bufferedReader().use {it.readText()})
        val exercise=starter.session.questions.first().exercise
        val timestamp=nativeGuestAnsweredAt(java.time.Instant.parse("2026-10-27T08:10:20.123456789Z"))
        val attempt=GuestAttempt(java.util.UUID.randomUUID().toString(),starter.session.contentReleaseId,exercise.id,exercise.revision,AnswerShortAnswer("Berufe"),emptyList(),timestamp,0)
        assertEquals("2026-10-27T08:10:20.123Z",attempt.answeredAt)
    }
    @Test fun guestStartsWithoutAuthAndRestoresDraft() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val path=File(context.cacheDir,"guest-${java.util.UUID.randomUUID()}")
        seed(path)
        var requested:String?=null
        compose.setContent {FoundationTheme {NativeGuestEntry(path,false,{}, {requested=it})}}
        compose.onNodeWithText("Continue session").performScrollTo().performClick()
        compose.onNodeWithText("Berufe").performScrollTo().performClick()
        compose.onNodeWithText("Create account").assertDoesNotExist()
        compose.onNodeWithText("Practise German. No account needed.").assertDoesNotExist()
        assertNull(requested)
        compose.onNodeWithText("Berufe").assertIsSelected()
        assertEquals(AnswerShortAnswer("Berufe"),NativeGuestStore(path).read().draft)
        compose.onNodeWithText("Check answer").performScrollTo().performClick()
        assertEquals("correct",NativeGuestStore(path).read().feedback?.outcome)
        compose.onNodeWithText("Continue").performScrollTo().performClick()
        assertEquals(1,NativeGuestStore(path).read().index)
    }
    @Test fun freshGuestPracticeUsesTheFullEligiblePoolPastTheOldFiveItemBoundary() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val path=File(context.cacheDir,"guest-${java.util.UUID.randomUUID()}")
        compose.setContent {FoundationTheme {NativeGuestEntry(path,false,{}, {})}}
        compose.onNodeWithText("Start practice").performScrollTo().performClick()
        assertEquals(65,NativeGuestStore(path).read().session!!.questions.size)
        compose.onNodeWithText("Question 1").assertExists()
        repeat(5) { compose.onNodeWithText("Skip").performScrollTo().performClick() }
        assertEquals(5,NativeGuestStore(path).read().index)
        compose.onNodeWithText("Question 6").assertExists()
        compose.onNodeWithText("You've practised the available questions").assertDoesNotExist()
    }
    @Test fun pausePreservesDraftAndResumeReturnsToQuestion() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val path=File(context.cacheDir,"guest-${java.util.UUID.randomUUID()}")
        seed(path)
        compose.setContent {FoundationTheme {NativeGuestEntry(path,false,{}, {})}}
        compose.onNodeWithText("Continue session").performScrollTo().performClick()
        compose.onNodeWithText("Berufe").performScrollTo().performClick()
        compose.onNodeWithText("Close practice").performScrollTo().performClick()
        compose.onNodeWithText("Create account").assertExists()
        assertEquals(AnswerShortAnswer("Berufe"),NativeGuestStore(path).read().draft)
        compose.onNodeWithText("Keep practising").performClick()
        compose.onNodeWithText("Question 1").assertExists()
        compose.onNodeWithText("Check answer").assertIsEnabled()
    }
    @Test fun completionKeepsResultsUntilExplicitFinishWithoutAccountPromotion() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val path=File(context.cacheDir,"guest-${java.util.UUID.randomUUID()}")
        seed(path,index=4,graded=4,correct=3)
        compose.setContent {FoundationTheme {NativeGuestEntry(path,false,{}, {})}}
        compose.onNodeWithText("Continue session").performScrollTo().performClick()
        compose.onNodeWithText("Skip").performScrollTo().performClick()
        compose.onNodeWithText("You've practised the available questions").assertExists()
        compose.onNodeWithText("4 answered · 1 skipped").assertExists()
        compose.onNodeWithText("Create account").assertDoesNotExist()
        assertEquals(3,NativeGuestStore(path).read().correct)
        compose.onNodeWithText("Finish").performScrollTo().performClick()
        assertFalse(NativeGuestStore(path).read().started)
    }
    @Test fun guestMultiSlotFirstChoiceSurvivesRestartAndRequiresBothSlots() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val path=File(context.cacheDir,"guest-${java.util.UUID.randomUUID()}")
        seed(path,index=4)
        var generation by mutableStateOf(0)
        compose.setContent {key(generation) {FoundationTheme {NativeGuestEntry(path,false,{}, {})}}}
        compose.onNodeWithText("Continue session").performScrollTo().performClick()
        compose.onNodeWithText("arbeitest",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("Check answer").assertIsNotEnabled()
        val partial=NativeGuestStore(path).read().draft as AnswerMultiSlot
        assertEquals(listOf(SlotValue("du","arbeitest"),SlotValue("ihr","")),partial.values)
        compose.runOnIdle {generation++}
        compose.onNodeWithText("Continue session").performScrollTo().performClick()
        compose.onNodeWithText("Check answer").assertIsNotEnabled()
        assertEquals(partial,NativeGuestStore(path).read().draft)
        compose.onAllNodesWithText("arbeitet",substring=false).onLast().performScrollTo().performClick()
        compose.onNodeWithText("Check answer").assertIsEnabled().performScrollTo().performClick()
        assertEquals("correct",NativeGuestStore(path).read().feedback?.outcome)
    }
    @Test fun guestMultiSlotCanStartWithSecondSlotAndChangeFirstChoice() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val path=File(context.cacheDir,"guest-${java.util.UUID.randomUUID()}")
        seed(path,index=4)
        compose.setContent {FoundationTheme {NativeGuestEntry(path,false,{}, {})}}
        compose.onNodeWithText("Continue session").performScrollTo().performClick()
        compose.onAllNodesWithText("arbeitet",substring=false).onLast().performScrollTo().performClick()
        compose.onNodeWithText("Check answer").assertIsNotEnabled()
        compose.onNodeWithText("arbeits",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("arbeitest",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("Check answer").assertIsEnabled()
        assertEquals(listOf(SlotValue("du","arbeitest"),SlotValue("ihr","arbeitet")),(NativeGuestStore(path).read().draft as AnswerMultiSlot).values)
    }
    @Test fun resetNeedsOnlyEmailAndUsesExplicitSubmit() {
        var submitted:List<String>?=null
        compose.setContent {FoundationTheme {NativeAccountEntry("reset",false,{}, {}, submit={a,e,p->submitted=listOf(a,e,p)})}}
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
        compose.setContent {FoundationTheme {NativeAccountEntry("register",false,{}, {}, submit={a,e,p->submitted=listOf(a,e,p)})}}
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

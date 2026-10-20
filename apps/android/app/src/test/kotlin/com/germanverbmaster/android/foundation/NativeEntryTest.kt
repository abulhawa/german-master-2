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
        assertNull(requested)
        assertEquals(AnswerShortAnswer("Berufe"),NativeGuestStore(path).read().draft)
        compose.onNodeWithText("Check answer").performScrollTo().performClick()
        assertEquals("correct",NativeGuestStore(path).read().feedback?.outcome)
        compose.onNodeWithText("Continue").performScrollTo().performClick()
        assertEquals(1,NativeGuestStore(path).read().index)
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

package com.germanverbmaster.android.foundation

import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.germanverbmaster.android.foundation.contract.AnswerShortAnswer
import com.germanverbmaster.android.learner.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NativePracticeAccessTest {
    @get:Rule val compose = createComposeRule()
    @get:Rule val folder = TemporaryFolder()
    private fun path() = File(folder.root, "practice")

    @Test fun practiceHomeHasNoSignInGateAndAccountPagesExplainTheRequirement() {
        val auth = mutableListOf<String>()
        compose.setContent { FoundationTheme { NativeGuestEntry(path(),false,{},auth::add) } }
        compose.onNodeWithText("A little German.\nA little more confidence.").assertExists()
        compose.onNodeWithText("Start practice").assertIsEnabled()
        compose.onNodeWithText("Sign in").assertDoesNotExist()
        compose.onNodeWithText("Create account").assertDoesNotExist()
        for(page in listOf("Progress","Downloads","Account")) {
            if(page=="Downloads") compose.onNode((hasText("Account") or hasContentDescription("Account")) and hasClickAction()).performClick()
            compose.onNode((hasText(page) or hasContentDescription(page)) and hasClickAction()).performClick()
            compose.onNodeWithText("This page requires an account. Sign in or create an account to continue.").assertExists()
            compose.onNodeWithText("Sign in").assertIsEnabled()
            compose.onNodeWithText("Create account").assertIsEnabled()
            compose.onNodeWithText("Back to practice").performScrollTo().performClick()
            compose.onNodeWithText("Start practice").assertExists()
        }
        assertTrue(auth.isEmpty())
        compose.onNode((hasText("Account") or hasContentDescription("Account")) and hasClickAction()).performClick()
        compose.onNodeWithText("Create account").performScrollTo().performClick()
        assertEquals(listOf("register"),auth)
    }

    @Test fun cancelingAuthenticationAndClosingPracticeRetainTheSelectedAnswer() {
        val file=path()
        val mode=mutableStateOf<String?>(null)
        compose.setContent { FoundationTheme {
            val current=mode.value
            if(current==null) NativeGuestEntry(file,false,{}, {mode.value=it})
            else NativeAccountEntry(current,false,{}, {mode.value=it}) {_,_,_->error("No network expected")}
        } }
        compose.onNodeWithText("Start practice").performScrollTo().performClick()
        compose.onNodeWithText("Berufe").performScrollTo().performClick()
        compose.onNode((hasText("Account") or hasContentDescription("Account")) and hasClickAction()).performClick()
        compose.onNodeWithText("Sign in").performClick()
        compose.onNodeWithText("Back to practice").performScrollTo().performClick()
        compose.onNodeWithText("Continue session").performScrollTo().performClick()
        assertEquals(AnswerShortAnswer("Berufe"),NativeGuestStore(file).read().draft)
        compose.onNodeWithText("Close practice").performScrollTo().performClick()
        compose.onNodeWithText("Save and return Home").performScrollTo().performClick()
        compose.onNodeWithText("Continue session").performScrollTo().performClick()
        assertEquals(AnswerShortAnswer("Berufe"),NativeGuestStore(file).read().draft)
        compose.onNodeWithText("Check answer").performScrollTo().performClick()
        compose.onNodeWithText("Correct").assertExists()
    }

    @Test fun systemBackLeavesAccountPagesAndAuthenticationWithoutDiscardingPractice() {
        val file=path()
        val mode=mutableStateOf<String?>(null)
        lateinit var back:OnBackPressedDispatcher
        compose.setContent { FoundationTheme {
            back=checkNotNull(LocalOnBackPressedDispatcherOwner.current).onBackPressedDispatcher
            val current=mode.value
            if(current==null) NativeGuestEntry(file,false,{}, {mode.value=it})
            else NativeAccountEntry(current,false,{}, {mode.value=it}) {_,_,_->error("No network expected")}
        } }
        compose.onNodeWithText("Start practice").performScrollTo().performClick()
        compose.onNodeWithText("Berufe").performScrollTo().performClick()
        compose.onNode((hasText("Account") or hasContentDescription("Account")) and hasClickAction()).performClick()
        compose.runOnIdle { back.onBackPressed() }
        compose.onNodeWithText("Check answer").assertExists()
        compose.onNode((hasText("Account") or hasContentDescription("Account")) and hasClickAction()).performClick()
        compose.onNodeWithText("Sign in").performClick()
        compose.runOnIdle { back.onBackPressed() }
        compose.onNodeWithText("Continue session").performScrollTo().performClick()
        assertEquals(AnswerShortAnswer("Berufe"),NativeGuestStore(file).read().draft)
        compose.runOnIdle { back.onBackPressed() }
        compose.onNodeWithText("Leave this session?").assertExists()
        compose.onNodeWithText("Keep practising").performScrollTo().performClick()
        compose.onNodeWithText("Check answer").assertExists()
    }

    @Test fun finishingPracticeDoesNotPromoteAnAccountAndCanRepeat() {
        compose.setContent { FoundationTheme { NativeGuestEntry(path(),false,{}, {}) } }
        compose.onNodeWithText("Start practice").performScrollTo().performClick()
        repeat(5) { compose.onNodeWithText("Skip").performScrollTo().performClick() }
        compose.onNodeWithText("Session complete").assertExists()
        compose.onNodeWithText("0 answered · 5 skipped").assertExists()
        compose.onNodeWithText("Sign in").assertDoesNotExist()
        compose.onNodeWithText("Create account").assertDoesNotExist()
        compose.onNodeWithText("Finish").performScrollTo().performClick()
        compose.onNodeWithText("Repeat the five questions").performScrollTo().performClick()
        compose.onNodeWithText("Question 1 of 5").assertExists()
    }

    @Test fun primaryNavigationRemainsAvailableAndTopicsNeverRequiresAnAccount() {
        compose.setContent { FoundationTheme { NativeGuestEntry(path(),false,{}, {}) } }
        val selected=SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.Selected,true)
        compose.onNode(hasText("Home") and selected).assertIsDisplayed()
        compose.onNode(hasText("Topics") and hasClickAction()).performClick()
        compose.onNode(hasText("Topics") and selected).assertIsDisplayed()
        compose.onNodeWithText("Grammar foundations").assertExists()
        compose.onNodeWithText("Word order").assertExists()
        compose.onNodeWithText("Sign in").assertDoesNotExist()
        compose.onNode(hasText("Progress") and hasClickAction()).performClick()
        compose.onNodeWithText("This page requires an account. Sign in or create an account to continue.").assertExists()
        compose.onNode(hasText("Home") and hasClickAction()).assertIsDisplayed().performClick()
        compose.onNodeWithText("Start practice").performScrollTo().performClick()
        compose.onNode(hasText("Home") and hasClickAction()).assertDoesNotExist()
        compose.onNodeWithText("Close practice").performScrollTo().performClick()
        compose.onNodeWithText("Save and return Home").performScrollTo().performClick()
        compose.onNode(hasText("Home") and selected).assertIsDisplayed()
    }

    @Test fun accountRequirementAndAuthOptionsAreLocalized() {
        compose.setContent { FoundationTheme { NativeGuestEntry(path(),true,{}, {}) } }
        compose.onNodeWithText("Ein wenig Deutsch.\nEin Stück sicherer.").assertExists()
        compose.onNode(hasText("Fortschritt") and hasClickAction()).performClick()
        compose.onNodeWithText("Für diese Seite brauchst du ein Konto. Melde dich an oder erstelle ein Konto, um fortzufahren.").assertExists()
        compose.onNodeWithText("Anmelden").assertIsEnabled()
        compose.onNodeWithText("Konto erstellen").assertIsEnabled()
    }
}

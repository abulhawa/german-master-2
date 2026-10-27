package com.germanverbmaster.android.foundation

import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.germanverbmaster.android.foundation.contract.*
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

    /** Pin a published choice so lifecycle assertions do not depend on shuffled content. */
    private fun seedDraftRound(file:File) {
        val context=androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val bundle=ContractReader.json.decodeFromString<NativeGuestBundle>(context.assets.open("practice.json").bufferedReader().use {it.readText()})
        val session=bundle.packs.first {pack -> pack.session.questions.any {it.exercise is ExerciseChoice}}.session
        val question=session.questions.first {it.exercise is ExerciseChoice}
        NativeGuestStore(file).write(NativeGuestState(started=true,session=session.copy(questions=listOf(question))))
    }

    private fun enterAnyDraft(file:File) {
        val guest=NativeGuestStore(file).read();val exercise=requireNotNull(guest.session).questions[guest.index].exercise
        when(exercise) {
            is ExerciseChoice -> compose.onNodeWithText(exercise.options.first().text).performClick()
            is ExerciseGapChoice -> compose.onNodeWithText(exercise.slots.first().options.first().text).performClick()
            is ExerciseWordOrder -> compose.onNodeWithText(exercise.tokens.first().text).performClick()
            is ExerciseMatching -> {compose.onNodeWithText(exercise.left.first().text).performClick();compose.onNodeWithText(exercise.right.first().text).performClick()}
            is ExerciseShortAnswer,is ExerciseCloze,is ExerciseMultiSlot -> {
                val context=androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
                val bundle=ContractReader.json.decodeFromString<NativeGuestBundle>(context.assets.open("practice.json").bufferedReader().use {it.readText()})
                val starter=ContractReader.json.decodeFromString<GuestStarterContent>(context.assets.open("starter.json").bufferedReader().use {it.readText()})
                val options=(starter.suggestions+bundle.packs.flatMap {it.suggestions.entries}.associate {it.toPair()})[exercise.id]
                if(options!=null) compose.onNodeWithText(options.values.first().first()).performClick()
                else compose.onAllNodes(hasSetTextAction()).onFirst().performTextInput("draft")
            }
            else -> error("Unsupported fixture exercise")
        }
    }
    private fun assertDraftPreserved(file:File) {
        val guest=NativeGuestStore(file).read()
        assertTrue("The saved answer or word order must survive",guest.draft!=null || guest.order.isNotEmpty())
    }

    @Test fun settingsChangesThemeWithoutAnAccountAndSurvivesRecreationWithDraftIntact() {
        val context=androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val prefs=context.getSharedPreferences("gm-v2-appearance",android.content.Context.MODE_PRIVATE)
        val previous=prefs.getString("theme","system")
        prefs.edit().putString("theme","system").commit()
        val file=path(); seedDraftRound(file); val generation=mutableStateOf(0); val auth=mutableListOf<String>()
        try {
            compose.setContent { androidx.compose.runtime.key(generation.value) { FoundationTheme {
                NativeGuestEntry(file,false,{},auth::add)
            } } }
            compose.onNode(hasText("Settings") and hasClickAction()).performClick()
            compose.onNodeWithText("Dark").performScrollTo().performClick()
            assertEquals("dark",prefs.getString("theme",null))
            compose.onNode(hasText("Home") and hasClickAction()).performClick()
            compose.onNodeWithText("Continue session").performScrollTo().performClick()
            val originalSession=requireNotNull(NativeGuestStore(file).read().session).id
            enterAnyDraft(file)
            val expected=NativeGuestStore(file).read()
            assertDraftPreserved(file)
            compose.runOnIdle {generation.value++}
            compose.onNodeWithText("Continue session").performScrollTo().performClick()
            assertEquals(originalSession,NativeGuestStore(file).read().session?.id)
            assertDraftPreserved(file)
            assertEquals(expected.session,NativeGuestStore(file).read().session)
            assertEquals(expected.index,NativeGuestStore(file).read().index)
            assertEquals(expected.draft,NativeGuestStore(file).read().draft)
            assertEquals(expected.order,NativeGuestStore(file).read().order)
            assertEquals("dark",prefs.getString("theme",null))
            assertTrue(auth.isEmpty())
        } finally { prefs.edit().putString("theme",previous).commit() }
    }

    @Test fun practiceHomeHasNoSignInGateAndAccountPagesExplainTheRequirement() {
        val auth = mutableListOf<String>()
        compose.setContent { FoundationTheme { NativeGuestEntry(path(),false,{},auth::add) } }
        compose.onNodeWithText("A little German.\nA little more confidence.").assertExists()
        compose.onNodeWithText("Start practice").assertIsEnabled()
        compose.onNodeWithText("Sign in").assertDoesNotExist()
        compose.onNodeWithText("Create account").assertDoesNotExist()
        for(page in listOf("Progress","Downloads","Settings")) {
            if(page=="Downloads") compose.onNode((hasText("Account") or hasText("Settings")) and hasClickAction()).performClick()
            val entry=compose.onNode((hasText(page) or hasContentDescription(page)) and hasClickAction())
            if(page=="Downloads") entry.performScrollTo().performClick() else entry.performClick()
            compose.onNodeWithText(if(page=="Settings") "Sign in to save and sync practice across devices." else "This page requires an account. Sign in or create an account to continue.").assertExists()
            compose.onNodeWithText("Sign in").assertIsEnabled()
            compose.onNodeWithText("Create account").assertIsEnabled()
            compose.onNodeWithText("Back to practice").performScrollTo().performClick()
            compose.onNodeWithText("Start practice").assertExists()
        }
        assertTrue(auth.isEmpty())
        compose.onNode((hasText("Account") or hasText("Settings")) and hasClickAction()).performClick()
        compose.onNodeWithText("Create account").performScrollTo().performClick()
        assertEquals(listOf("register"),auth)
    }

    @Test fun cancelingAuthenticationAndClosingPracticeRetainTheSelectedAnswer() {
        val file=path(); seedDraftRound(file)
        val mode=mutableStateOf<String?>(null)
        compose.setContent { FoundationTheme {
            val current=mode.value
            if(current==null) NativeGuestEntry(file,false,{}, {mode.value=it})
            else NativeAccountEntry(current,false,{}, {mode.value=it}) {_,_,_->error("No network expected")}
        } }
        compose.onNodeWithText("Continue session").performScrollTo().performClick()
        enterAnyDraft(file)
        val expected=NativeGuestStore(file).read()
        assertDraftPreserved(file)
        compose.onNode((hasText("Account") or hasText("Settings")) and hasClickAction()).performClick()
        compose.onNodeWithText("Sign in").performClick()
        compose.onNodeWithText("Home").performClick()
        if(compose.onAllNodesWithText("Continue session").fetchSemanticsNodes().isNotEmpty()) compose.onNodeWithText("Continue session").performScrollTo().performClick()
        assertDraftPreserved(file)
        compose.onNodeWithText("Close practice").performScrollTo().performClick()
        compose.onNodeWithText("Keep practising").performClick()
        assertDraftPreserved(file)
        assertEquals(expected.session,NativeGuestStore(file).read().session)
        assertEquals(expected.index,NativeGuestStore(file).read().index)
        assertEquals(expected.draft,NativeGuestStore(file).read().draft)
        assertEquals(expected.order,NativeGuestStore(file).read().order)
    }

    @Test fun systemBackLeavesAccountPagesAndAuthenticationWithoutDiscardingPractice() {
        val file=path(); seedDraftRound(file)
        val mode=mutableStateOf<String?>(null)
        lateinit var back:OnBackPressedDispatcher
        compose.setContent { FoundationTheme {
            back=checkNotNull(LocalOnBackPressedDispatcherOwner.current).onBackPressedDispatcher
            val current=mode.value
            if(current==null) NativeGuestEntry(file,false,{}, {mode.value=it})
            else NativeAccountEntry(current,false,{}, {mode.value=it}) {_,_,_->error("No network expected")}
        } }
        compose.onNodeWithText("Continue session").performScrollTo().performClick()
        enterAnyDraft(file)
        val expected=NativeGuestStore(file).read()
        assertDraftPreserved(file)
        compose.onNode((hasText("Account") or hasText("Settings")) and hasClickAction()).performClick()
        compose.runOnIdle { back.onBackPressed() }
        compose.onNodeWithText("Check answer").assertExists()
        compose.onNode((hasText("Account") or hasText("Settings")) and hasClickAction()).performClick()
        compose.onNodeWithText("Sign in").performClick()
        compose.runOnIdle { back.onBackPressed() }
        if(compose.onAllNodesWithText("Continue session").fetchSemanticsNodes().isNotEmpty()) compose.onNodeWithText("Continue session").performScrollTo().performClick()
        assertDraftPreserved(file)
        assertEquals(expected.session,NativeGuestStore(file).read().session)
        assertEquals(expected.index,NativeGuestStore(file).read().index)
        assertEquals(expected.draft,NativeGuestStore(file).read().draft)
        assertEquals(expected.order,NativeGuestStore(file).read().order)
        compose.runOnIdle { back.onBackPressed() }
        compose.onNodeWithText("Save your progress?").assertExists()
        compose.onNodeWithText("Keep practising").performClick()
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
        compose.onNodeWithText("Start practice").performScrollTo().performClick()
        compose.onNodeWithText("Question 1 of 5").assertExists()
    }

    @Test fun primaryNavigationRemainsAvailableAndTopicsNeverRequiresAnAccount() {
        compose.setContent { FoundationTheme { NativeGuestEntry(path(),false,{}, {}) } }
        val selected=SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.Selected,true)
        compose.onNode(hasText("Home") and selected).assertIsDisplayed()
        compose.onNode(hasText("Topics") and hasClickAction()).performClick()
        compose.onNode(hasText("Topics") and selected).assertIsDisplayed()
        compose.onNodeWithText("Nouns and plurals").assertExists()
        compose.onNodeWithText("Nouns and plurals").assertIsEnabled()
        compose.onNodeWithText("Sign in").assertDoesNotExist()
        compose.onNode(hasText("Progress") and hasClickAction()).performClick()
        compose.onNodeWithText("This page requires an account. Sign in or create an account to continue.").assertExists()
        compose.onNode(hasText("Home") and hasClickAction()).assertIsDisplayed().performClick()
        compose.onNodeWithText("Start practice").performScrollTo().performClick()
        compose.onNode(hasText("Home") and hasClickAction()).assertDoesNotExist()
        compose.onNodeWithText("Close practice").performScrollTo().performClick()
        compose.onNodeWithText("Leave without saving").performClick()
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
    @Test fun configuredSocialOptionsKeepEmailAndDispatchEachProvider() {
        val selected=mutableListOf<String>()
        val mode=mutableStateOf("sign-in")
        compose.setContent { FoundationTheme {
            NativeAccountEntry(mode.value,false,{}, {if(it!=null)mode.value=it},socialSignIn={selected.add(it)}) {_,_,_->error("Email must not be submitted")}
        } }
        compose.onNodeWithText("Email").assertExists()
        compose.onNodeWithText("Continue with Google").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Continue with Microsoft").performScrollTo().performClick()
        compose.waitForIdle()
        assertEquals(listOf("google","azure"),selected)
        compose.runOnIdle {mode.value="reset"}
        compose.onNodeWithText("Continue with Google").assertDoesNotExist()
        compose.onNodeWithText("Continue with Microsoft").assertDoesNotExist()
    }

    @Test fun homeExitIsAvailableOnEachAccountPage() {
        val mode=mutableStateOf("sign-in")
        var returned=false
        compose.setContent { FoundationTheme {
            NativeAccountEntry(mode.value,false,{}, {if(it==null)returned=true},socialSignIn={}) {_,_,_->error("No network expected")}
        } }
        listOf("sign-in","register","reset").forEach { page ->
            compose.runOnIdle {mode.value=page;returned=false}
            compose.onNodeWithText("Home").performClick()
            compose.runOnIdle {assertTrue(returned)}
        }
    }

    @Test fun authenticationBackUsesPreviousPageAndHomeRemainsAnExplicitExit() {
        val mode=mutableStateOf("register")
        var home=false
        lateinit var dispatcher:OnBackPressedDispatcher
        compose.setContent { FoundationTheme {
            dispatcher=checkNotNull(LocalOnBackPressedDispatcherOwner.current).onBackPressedDispatcher
            NativeAccountEntry(mode.value,false,{}, {home=it==null},back={mode.value="sign-in"}) {_,_,_->error("No network expected")}
        } }
        compose.runOnIdle {dispatcher.onBackPressed()}
        compose.onNode(hasText("Sign in") and hasClickAction()).assertExists()
        compose.onNodeWithText("Home").performClick()
        compose.runOnIdle {assertTrue(home)}
    }

}

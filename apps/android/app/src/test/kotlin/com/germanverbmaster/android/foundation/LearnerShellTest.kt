package com.germanverbmaster.android.foundation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.runtime.mutableStateOf
import com.germanverbmaster.android.foundation.contract.*
import com.germanverbmaster.android.learner.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LearnerShellTest {
    @get:Rule val compose = createComposeRule()
    @Test fun accountLanguageAndLevelSaveAsSoonAsAChoiceIsSelected() {
        val repo=repository(true,authenticated=true,saveWorks=true)
        compose.setContent {FoundationTheme {LearnerShell(repo)}}
        compose.onNode(hasText("Settings") and hasClickAction()).performClick()
        compose.onNode(hasText("Language") and hasClickAction()).performScrollTo().performClick()
        compose.onNode(hasText("Deutsch") and hasClickAction()).performClick()
        compose.waitUntil(10000) {repo.state.profile?.preferences?.locale=="de"}
        compose.onNodeWithText("Einstellungen speichern").assertDoesNotExist()
        compose.onNode(hasText("Übungsniveau") and hasClickAction()).performScrollTo().performClick()
        compose.onNode(hasText("B2") and hasClickAction()).performClick()
        compose.waitUntil(10000) {repo.state.profile?.preferences?.level=="B2"}
    }
    @Test fun systemBackReturnsFromPreferencesToSettingsThenHome() {
        val repo=repository(true)
        lateinit var back:androidx.activity.OnBackPressedDispatcher
        compose.setContent {FoundationTheme {
            back=checkNotNull(androidx.activity.compose.LocalOnBackPressedDispatcherOwner.current).onBackPressedDispatcher
            LearnerShell(repo)
        }}
        compose.onNode(hasText("Settings") and hasClickAction()).performClick()
        compose.onNode(hasText("Practice level") and hasClickAction()).performScrollTo().performClick()
        compose.runOnIdle {back.onBackPressed()}
        compose.onNodeWithText("Account").assertExists()
        compose.runOnIdle {back.onBackPressed()}
        compose.onNodeWithText("A little practice. Lasting progress.").assertExists()
    }
    @Test fun languageUsesExplicitChoicesAndSignOutRequiresConfirmation() {
        val german=mutableStateOf(false)
        val repo=repository(true,authenticated=true)
        compose.setContent {FoundationTheme {androidx.compose.foundation.layout.Column {
            StudyLanguageRow(german.value) {german.value=!german.value}
            NativePrivacySignOut(repo,false,false) {}
        }}}
        compose.onNode(hasText("Language") and hasClickAction()).performClick()
        compose.onNode(hasText("Deutsch") and hasClickAction()).performClick()
        compose.runOnIdle {org.junit.Assert.assertTrue(german.value)}
        compose.onNode(hasText("Sign out") and hasClickAction()).performClick()
        compose.onNodeWithText("Sign out?").assertExists()
        org.junit.Assert.assertFalse(repo.state.signedOut)
        compose.onNodeWithText("Cancel").performClick()
        compose.onNodeWithText("Sign out?").assertDoesNotExist()
        org.junit.Assert.assertFalse(repo.state.signedOut)
    }
    @Test fun settingsDisplaysCurrentAccountEmailAndHidesItForLocalAccess() {
        val repo = repository(true)
        val local = mutableStateOf(false)
        compose.setContent { FoundationTheme { LearnerShell(repo, localOnly = local.value, accountEmail = { "learner@example.test" }) } }
        compose.onNode(hasText("Settings") and hasClickAction()).performScrollTo().performClick()
        compose.onNodeWithText("Account").performScrollTo().assertExists()
        compose.onNodeWithText("learner@example.test").assertExists()
        compose.onNodeWithText("Grammar").assertDoesNotExist()
        compose.onNodeWithText("Sync all saved work").assertDoesNotExist()
        compose.onNodeWithText("Download my learning data").assertDoesNotExist()
        compose.onNodeWithText("Request account deletion").assertExists()
        compose.runOnIdle { local.value = true }
        compose.onNodeWithText("learner@example.test").assertDoesNotExist()
        compose.onNodeWithText("Reconnect your account to view your email.").assertExists()
    }
    private val id = "00000000-0000-4000-8000-000000000001"
    private fun repository(completed: Boolean, pending: Boolean = false, blockRefresh: Boolean = false, authenticated:Boolean=false,saveWorks:Boolean=false): LearnerRepository {
        val prefs = ProfilePreferences("en", "UTC", "B1", 5)
        var profile = LearnerProfile("v2", 0, completed, prefs)
        val targets = listOf(ConfirmedTarget(id, "retained-evidence-v1", "needs_practice", 1, 0, false, 0, 1, emptyList(), true))
        val api = object : LearnerApi {
            override suspend fun profile() = if (blockRefresh) kotlinx.coroutines.CompletableDeferred<LearnerProfile>().await() else profile
            override suspend fun sync(cursor: String) = SyncPage("v2", emptyList(), cursor, false)
            override suspend fun save(request: ProfileRequest):LearnerProfile {if(!saveWorks)error("offline");profile=profile.copy(revision=request.expectedRevision+1,preferences=request.preferences);return profile}
            override suspend fun catalog() = Catalog("v2", id, "unpublished_local_draft", emptyList(), emptyList())
            override suspend fun targets(cursor: String) = TargetPage("v2", "2026-10-04T10:00:00Z", targets, "", id)
        }
        val store = object : LearnerStore {
            var cache = LearnerCache(profile = profile, pending = if (pending) ProfileRequest("v2", id, 0, prefs) else null, targets = targets)
            override fun read() = cache
            override fun write(value: LearnerCache) { cache = value }
        }
        return LearnerRepository(api, store, revoke=if(authenticated) ({}) else null)
    }
    @Test fun replacingAccountClearsPreviousLearnersUiWhileTheNewRefreshIsStillPending() {
        val a = repository(true,true); val b = repository(true,blockRefresh=true)
        val active = mutableStateOf(a)
        compose.setContent { FoundationTheme { LearnerShell(active.value) } }
        compose.waitUntil(10000) { compose.onAllNodesWithText("Retry saved preferences").fetchSemanticsNodes().isNotEmpty() }
        compose.runOnIdle { active.value = b }
        compose.onNodeWithText("Retry saved preferences").assertDoesNotExist()
        compose.runOnIdle { active.value = a }
        compose.waitUntil(10000) { compose.onAllNodesWithText("Retry saved preferences").fetchSemanticsNodes().isNotEmpty() }
    }
    @Test fun homeDoesNotDoubleCountDueWeaknessAndProgressUsesServerState() {
        val repo = repository(true)
        compose.setContent { FoundationTheme { LearnerShell(repo) } }
        compose.waitUntil(10000) { compose.onAllNodesWithText("Needs practice: 1").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Retention checks: 0").assertExists()
        compose.onNodeWithText("No questions available for these preferences.").assertExists()
        compose.onNodeWithText("Progress").performClick()
        compose.onNodeWithText("Needs practice (1)").performScrollTo().assertExists()
        compose.onNodeWithText("Review checks: 0").performScrollTo().assertExists()
    }
    @Test fun pendingSetupBlocksEditingAndExposesExplicitRecovery() {
        val repo = repository(false, true)
        compose.setContent { FoundationTheme { LearnerShell(repo) } }
        compose.waitUntil(10000) { compose.onAllNodesWithText("Save preferences").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Save preferences").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Retry saved preferences").performScrollTo().assertExists()
        compose.onNodeWithText("Reload current preferences").performScrollTo().performClick()
        compose.waitUntil(10000) { repo.state.pending == null }
        compose.onNodeWithText("Save preferences").performScrollTo().assertIsEnabled()
    }
    @Test fun homeKeepsSettingsAndSyncBehindAccountAndKeepsStartDisabledWithoutContent() {
        val repo = repository(true)
        compose.setContent { FoundationTheme { LearnerShell(repo) } }
        compose.waitUntil(10000) { repo.state.catalog != null }
        compose.onNodeWithText("A little practice. Lasting progress.").assertExists()
        compose.onNodeWithText("Start practice").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Sync all saved work").assertDoesNotExist()
        compose.onNodeWithText("Downloaded practice").assertDoesNotExist()
        compose.onNode(hasText("Settings") and hasClickAction()).performScrollTo().performClick()
        compose.onNodeWithText("Downloaded practice").assertDoesNotExist()
        compose.onNodeWithText("Sync all saved work").assertDoesNotExist()
        compose.onNodeWithText("Request account deletion").assertExists()
        compose.onNode(hasText("Practice level") and hasClickAction()).performScrollTo().performClick()
        compose.onNodeWithText("Save preferences").performScrollTo().assertIsEnabled()
        compose.onNodeWithText("Back to Home").performScrollTo().performClick()
        compose.onNodeWithText("A little practice. Lasting progress.").assertExists()
    }
    @Test fun accountBackedHomeCanReadReferenceEvenWithoutAvailablePractice() {
        val repo=repository(true)
        compose.setContent {FoundationTheme {LearnerShell(repo)}}
        compose.waitUntil(10000) {repo.state.catalog!=null}
        compose.onNodeWithText("Grammar",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("Search grammar").performTextInput("relativ")
        compose.onNodeWithText("Relative pronouns",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("Relativpronomen",substring=false).assertExists()
        compose.onNodeWithContentDescription("Close reference").performClick()
        compose.onNodeWithText("Start practice").performScrollTo().assertIsNotEnabled()
    }
}

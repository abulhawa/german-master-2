package com.germanverbmaster.android.foundation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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
    private val id = "00000000-0000-4000-8000-000000000001"
    private fun repository(completed: Boolean, pending: Boolean = false): LearnerRepository {
        val prefs = ProfilePreferences("en", "UTC", "B1", 5)
        val profile = LearnerProfile("v2", 0, completed, prefs)
        val targets = listOf(ConfirmedTarget(id, "retained-evidence-v1", "needs_practice", 1, 0, false, 0, 1, emptyList(), true))
        val api = object : LearnerApi {
            override suspend fun profile() = profile
            override suspend fun sync(cursor: String) = SyncPage("v2", emptyList(), cursor, false)
            override suspend fun save(request: ProfileRequest) = error("offline")
            override suspend fun catalog() = Catalog("v2", id, "unpublished_local_draft", emptyList(), emptyList())
            override suspend fun targets(cursor: String) = TargetPage("v2", "2026-10-04T10:00:00Z", targets, "", id)
        }
        val store = object : LearnerStore {
            var cache = LearnerCache(profile = profile, pending = if (pending) ProfileRequest("v2", id, 0, prefs) else null, targets = targets)
            override fun read() = cache
            override fun write(value: LearnerCache) { cache = value }
        }
        return LearnerRepository(api, store)
    }
    @Test fun homeDoesNotDoubleCountDueWeaknessAndProgressUsesServerState() {
        val repo = repository(true)
        compose.setContent { FoundationTheme { LearnerShell(repo) } }
        compose.waitUntil(10000) { compose.onAllNodesWithText("Needs practice: 1").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Retention checks: 0").assertExists()
        compose.onNodeWithText("No questions available for these preferences.").assertExists()
        compose.onNodeWithText("Progress").performScrollTo().performClick()
        compose.onNodeWithText("Needs practice (1)").performScrollTo().assertExists()
        compose.onNodeWithText("Qualifying checks: 0").performScrollTo().assertExists()
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
}

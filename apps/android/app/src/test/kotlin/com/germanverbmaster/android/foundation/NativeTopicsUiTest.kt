package com.germanverbmaster.android.foundation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.foundation.contract.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NativeTopicsUiTest {
    @get:Rule val compose = createComposeRule()
    @Test fun topicsTargetDetailAndFocusedPracticePreserveSavedSession() {
        val session = ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("session.json")).readText())
        val topic = "00000000-0000-4000-8000-000000000001"
        val target = "00000000-0000-4000-8000-000000000002"
        val requests = mutableListOf<FocusedSessionRequest>()
        val catalog = Catalog("v2", session.contentReleaseId, "unpublished_local_draft", listOf(CatalogTopic(topic, LocalizedText("Grammar", "Grammatik"))), listOf(CatalogTarget(target, topic, LocalizedText("Nouns", "Nomen"), LocalizedText("Noun description", "Nomenbeschreibung"), "B1", 1)))
        val api = object : LearnerApi {
            override suspend fun sync(cursor: String) = SyncPage("v2", emptyList(), cursor, false)
            override suspend fun profile() = LearnerProfile("v2", 0, true, ProfilePreferences("en", "UTC", "B1", 15))
            override suspend fun save(request: ProfileRequest) = profile()
            override suspend fun catalog() = catalog
            override suspend fun targets(cursor: String) = TargetPage("v2", "2026-10-04T10:00:00Z", emptyList(), "", topic)
            override suspend fun session(request: FocusedSessionRequest): Session { requests.add(request); return session.copy(questions = session.questions.take(1)) }
        }
        val store = object : LearnerStore {
            var cache = LearnerCache()
            override fun read() = cache
            override fun write(value: LearnerCache) { cache = value }
        }
        val repo = LearnerRepository(api, store)
        compose.setContent { FoundationTheme { LearnerShell(repo) } }
        compose.waitUntil(10000) { repo.state.profile != null }
        compose.waitForIdle()
        compose.onNodeWithText("Topics", substring = false).performScrollTo().performClick()
        compose.onNodeWithText("Grammar").performScrollTo().performClick()
        compose.onNodeWithText("Nouns · B1").performScrollTo().performClick()
        compose.onNodeWithText("Noun description").assertExists()
        compose.onNodeWithText("No confirmed practice yet.").assertExists()
        compose.onNodeWithText("Practise this (1)").performScrollTo().performClick()
        compose.waitUntil(10000) { repo.state.practice?.session != null }
        compose.waitForIdle()
        assertEquals(TargetFocus(target), requests.single().focus)
        compose.onNodeWithText("Answer", substring = false).performTextInput("saved draft")
        compose.onNodeWithText("Close practice").performScrollTo().performClick()
        compose.onNodeWithText("Topics", substring = false).performScrollTo().performClick()
        compose.onNodeWithText("Grammar").performScrollTo().performClick()
        compose.onNodeWithText("Practise this (1)").assertIsNotEnabled()
        compose.onNodeWithText("Continue practice").performScrollTo().performClick()
        compose.waitForIdle()
        assertEquals(1, requests.size)
        assertEquals(AnswerShortAnswer("saved draft"), repo.state.practice!!.draft)
        compose.onNodeWithText("saved draft").assertExists()
    }
    @Test fun germanUnavailableTargetShowsConfirmedCountsAndSavedTimezone() {
        val id = "00000000-0000-4000-8000-000000000001"
        val title = LocalizedText("Grammar", "Grammatik")
        val cache = LearnerCache(
            profile = LearnerProfile("v2", 0, true, ProfilePreferences("de", "Europe/Berlin", "B2", 5)),
            catalog = Catalog("v2", id, "unpublished_local_draft", listOf(CatalogTopic(id, title)), listOf(CatalogTarget(id, id, title, title, "B1", 0))),
            targets = listOf(ConfirmedTarget(id, "retained-evidence-v1", "mastered", 8, 3, true, 0, 8, listOf(TargetSchedule("2026-10-04T10:00:00Z", 1, 3)), true)))
        compose.setContent { FoundationTheme { NativeTopicsView(cache, "target", id, true, false, { _, _ -> }, {}, { error("Unavailable focus must not start") }) } }
        compose.onNodeWithText("Bestätigter Stand: Beherrscht").assertExists()
        compose.onNodeWithText("Qualifizierte Prüfungen: 3").assertExists()
        compose.onNodeWithText("Wiederholung: 2026-10-04 12:00 (Europe/Berlin)").assertExists()
        compose.onNodeWithText("Im gespeicherten Datenstand fällig").assertExists()
        compose.onNodeWithText("Für diese Einstellungen sind keine Fragen verfügbar.").assertExists()
        compose.onNodeWithText("Gezielt üben (1)").assertIsNotEnabled()
    }
}

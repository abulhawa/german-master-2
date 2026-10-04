package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.nio.file.Files

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NativeFocusTest {
    private val session = ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("session.json")).readText())
    private val topic = "00000000-0000-4000-8000-000000000001"
    private val target = "00000000-0000-4000-8000-000000000002"
    private val catalog = Catalog("v2", session.contentReleaseId, "unpublished_local_draft", listOf(CatalogTopic(topic, LocalizedText("Grammar", "Grammatik"))), listOf(CatalogTarget(target, topic, LocalizedText("Target", "Lernziel"), LocalizedText("Description", "Beschreibung"), "B1", 1)))
    private inner class Api : LearnerApi {
        override suspend fun sync(cursor: String) = SyncPage("v2", emptyList(), cursor, false)
        val requests = mutableListOf<FocusedSessionRequest>()
        var lose = false
        override suspend fun session(request: FocusedSessionRequest): Session {
            requests.add(request)
            if (lose) { lose = false; error("lost response") }
            return session.copy(questions = session.questions.take(request.questionCount))
        }
        override suspend fun profile() = LearnerProfile("v2", 0, true, ProfilePreferences("en", "UTC", "B1", 15))
        override suspend fun save(request: ProfileRequest) = profile()
        override suspend fun catalog() = catalog
        override suspend fun targets(cursor: String) = TargetPage("v2", "2026-10-04T10:00:00Z", emptyList(), "", topic)
    }
    @Test fun focusedLostResponseRestartsWithSamePayloadAndPreservesDraft() = runBlocking {
        val api = Api()
        val store = AtomicLearnerStore(File(Files.createTempDirectory("focus").toFile(), "cache.json"))
        var repo = LearnerRepository(api, store); repo.refresh()
        api.lose = true
        assertTrue(runCatching { repo.startPractice(TargetFocus(target)) }.isFailure)
        val frozen = repo.state.practice
        repo = LearnerRepository(api, store)
        assertTrue(runCatching { repo.startPractice(TopicFocus(topic)) }.isFailure)
        assertEquals(frozen, repo.state.practice)
        repo.startPractice()
        assertEquals(api.requests[0], api.requests[1]); assertEquals(1, api.requests[1].questionCount)
        assertEquals(TargetFocus(target), api.requests[1].focus)
        repo.draft(AnswerShortAnswer("draft"))
        repo = LearnerRepository(api, store)
        assertEquals(AnswerShortAnswer("draft"), repo.state.practice!!.draft)
        repo.startPractice(); assertEquals(2, api.requests.size)
        assertTrue(runCatching { repo.startPractice(TopicFocus(topic)) }.isFailure)
        assertEquals(AnswerShortAnswer("draft"), repo.state.practice!!.draft)
    }
    @Test fun availabilityAndPendingPreferencesBlockNewFocusWhileTopicCountIsBounded() = runBlocking {
        val api = Api()
        val store = AtomicLearnerStore(File(Files.createTempDirectory("focus-gates").toFile(), "cache.json"))
        store.write(LearnerCache(profile = api.profile(), catalog = catalog.copy(targets = catalog.targets.map { it.copy(availableQuestionCount = 0) })))
        var repo = LearnerRepository(api, store)
        assertTrue(runCatching { repo.startPractice(TargetFocus(target)) }.isFailure)
        assertTrue(api.requests.isEmpty()); assertNull(repo.state.practice)
        store.write(store.read().copy(catalog = catalog, pending = ProfileRequest("v2", topic, 0, api.profile().preferences)))
        repo = LearnerRepository(api, store)
        assertTrue(runCatching { repo.startPractice(TopicFocus(topic)) }.isFailure)
        assertTrue(api.requests.isEmpty())
        store.write(store.read().copy(pending = null, catalog = catalog.copy(targets = catalog.targets.map { it.copy(availableQuestionCount = 8) })))
        repo = LearnerRepository(api, store); repo.startPractice(TopicFocus(topic))
        assertEquals(8, api.requests.single().questionCount)
        assertEquals(TopicFocus(topic), repo.state.practice!!.focus)
    }
    @Test fun failedFocusFreezePreventsNetworkAndLeavesSnapshotUntouched() = runBlocking {
        val api = Api()
        val original = LearnerCache(profile = api.profile(), catalog = catalog)
        val store = object : LearnerStore { override fun read() = original; override fun write(value: LearnerCache) = error("disk full") }
        val repo = LearnerRepository(api, store)
        assertTrue(runCatching { repo.startPractice(TargetFocus(target)) }.isFailure)
        assertEquals(original, repo.state); assertTrue(api.requests.isEmpty())
    }
}

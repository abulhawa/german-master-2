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
class NativePracticeTest {
    private val session = ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("session.json")).readText())
    private val answers = ContractReader.attempts(requireNotNull(javaClass.classLoader?.getResource("attempt-batch.json")).readText()).attempts
    private class Store(var cache: LearnerCache) : LearnerStore {
        var fail = false
        override fun read() = cache
        override fun write(value: LearnerCache) { check(!fail); cache = value }
    }
    private inner class Api : LearnerApi {
        override suspend fun sync(cursor: String) = SyncPage("v2", emptyList(), cursor, false)
        val attempts = mutableListOf<Attempt>()
        val exposures = mutableListOf<ExposureEvent>()
        val requests = mutableListOf<SessionRequest>()
        var lose = false
        var after: () -> Unit = {}
        override suspend fun session(request: SessionRequest): Session { requests.add(request); if(lose) { lose = false; error("response lost") }; return session }
        override suspend fun submit(attempt: Attempt): Acknowledgment {
            attempts.add(attempt); after()
            if(lose) { lose = false; error("response lost") }
            return AttemptDuplicate(attempt.attemptId, Evaluation("correct", "test", LocalizedText("Server", "Server"), attempt.answer, attempt.assistance.isNotEmpty()), 1)
        }
        override suspend fun expose(event: ExposureEvent): ExposureAcknowledgment { exposures.add(event); after(); if(lose) { lose = false; error("response lost") }; return ExposureDuplicate(event.eventId, 1) }
        override suspend fun profile() = LearnerProfile("v2", 0, true, ProfilePreferences("en", "UTC", "B1", 15))
        override suspend fun save(request: ProfileRequest) = profile()
        override suspend fun catalog() = error("unused")
        override suspend fun targets(cursor: String) = error("unused")
    }
    private suspend fun cache(api: Api) = LearnerCache(profile = api.profile())
    @Test fun restartPreservesDraftAssistanceFrozenAnswerFeedbackAndCounts() = runBlocking {
        val api = Api(); val file = File(Files.createTempDirectory("practice").toFile(), "cache.json")
        val store = AtomicLearnerStore(file); store.write(cache(api))
        var repo = LearnerRepository(api, store); repo.startPractice(); repo.draft(answers[0].answer); repo.hint()
        repo = LearnerRepository(api, store)
        assertEquals(answers[0].answer, repo.state.practice!!.draft)
        api.lose = true; assertTrue(runCatching { repo.answer() }.isFailure)
        val frozen = repo.state.practice!!.pending
        assertTrue(runCatching { repo.skip() }.isFailure)
        repo = LearnerRepository(api, store); repo.answer()
        assertEquals(frozen, api.attempts.last()); assertEquals(1, repo.state.practice!!.graded)
        assertTrue(repo.state.practice!!.evaluation!!.assisted)
        repo = LearnerRepository(api, store); assertTrue(runCatching { repo.answer() }.isFailure)
        repo.continuePractice(); assertEquals(1, repo.state.practice!!.index); assertNull(repo.state.practice!!.draft)
        repeat(4) { repo.skip() }
        val summary = LearnerRepository(api, store).state.practice!!
        assertEquals(5, summary.index); assertEquals(1, summary.graded); assertEquals(1, summary.correct); assertEquals(4, summary.skipped)
    }
    @Test fun failedFreezeBlocksAllNetworkWrites() = runBlocking {
        val api = Api(); val store = Store(cache(api)); val repo = LearnerRepository(api, store)
        store.fail = true; assertTrue(runCatching { repo.startPractice() }.isFailure); assertTrue(api.requests.isEmpty())
        store.fail = false; repo.startPractice(); repo.draft(answers[0].answer)
        store.fail = true
        assertTrue(runCatching { repo.answer() }.isFailure); assertTrue(api.attempts.isEmpty())
        assertTrue(runCatching { repo.skip() }.isFailure); assertTrue(api.exposures.isEmpty())
        assertNull(repo.state.practice!!.pending); assertNull(repo.state.practice!!.exposure)
    }
    @Test fun lostSessionResponseReusesSavedRequest() = runBlocking {
        val api = Api(); val store = Store(cache(api)); var repo = LearnerRepository(api, store)
        api.lose = true; assertTrue(runCatching { repo.startPractice() }.isFailure)
        repo = LearnerRepository(api, store); repo.startPractice()
        assertEquals(api.requests[0], api.requests[1]); assertEquals(15, api.requests[1].questionCount)
    }
    @Test fun skipAcknowledgmentSaveFailureRetriesExactEventWithoutAdvancing() = runBlocking {
        val api = Api(); val store = Store(cache(api)); var repo = LearnerRepository(api, store)
        repo.startPractice(); repo.draft(answers[0].answer)
        api.after = { store.fail = true }; assertTrue(runCatching { repo.skip() }.isFailure)
        assertEquals(0, repo.state.practice!!.index); assertEquals(0, repo.state.practice!!.skipped)
        assertTrue(runCatching { repo.answer() }.isFailure); assertTrue(runCatching { repo.draft(null) }.isFailure)
        store.fail = false; api.after = {}; repo = LearnerRepository(api, store); repo.skip()
        assertEquals(api.exposures[0], api.exposures[1]); assertEquals(1, repo.state.practice!!.index)
        assertEquals(1, repo.state.practice!!.skipped); assertEquals(0, repo.state.practice!!.correct)
    }
    @Test fun answerAcknowledgmentSaveFailureDoesNotDoubleCount() = runBlocking {
        val api = Api(); val store = Store(cache(api)); val repo = LearnerRepository(api, store)
        repo.startPractice(); repo.draft(answers[0].answer)
        api.after = { store.fail = true }; assertTrue(runCatching { repo.answer() }.isFailure)
        assertEquals(0, repo.state.practice!!.graded); assertNull(repo.state.practice!!.evaluation)
        store.fail = false; api.after = {}; repo.answer()
        assertEquals(api.attempts[0], api.attempts[1]); assertEquals(1, repo.state.practice!!.graded)
    }
    @Test fun incompleteDraftsRemainSavedButCannotSubmit() = runBlocking {
        val api = Api(); val store = Store(cache(api)); val repo = LearnerRepository(api, store)
        repo.startPractice(); repo.draft(AnswerShortAnswer(""))
        assertTrue(runCatching { repo.answer() }.isFailure); assertTrue(api.attempts.isEmpty())
        assertEquals(AnswerShortAnswer(""), LearnerRepository(api, store).state.practice!!.draft)
    }
}

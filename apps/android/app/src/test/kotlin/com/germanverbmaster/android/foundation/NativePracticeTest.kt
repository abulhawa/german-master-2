package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
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
        val reports = mutableListOf<ContentReportRequest>()
        override suspend fun report(request: ContentReportRequest): ContentReportReceipt { reports.add(request); after(); if(lose) {lose = false; error("response lost")}; return ContentReportReceipt("v2",request.reportId,"recorded") }
        val exposures = mutableListOf<ExposureEvent>()
        val requests = mutableListOf<SessionRequest>()
        val completions = mutableListOf<SessionCompletionRequest>()
        override suspend fun complete(sessionId: String, request: SessionCompletionRequest): SessionCompletionReceipt {
            completions.add(request); after(); if(lose) { lose = false; error("response lost") }
            return SessionCompletionReceipt("v2",request.requestId,sessionId,request.mode,5,0,0,0,"2026-10-05T10:00:00Z")
        }
        var lose = false
        var after: () -> Unit = {}
        override suspend fun session(request: SessionRequest): Session { requests.add(request); if(lose) { lose = false; error("response lost") }; return session.copy(questions = session.questions.take(request.questionCount)) }
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
    private val catalog = ContractReader.json.decodeFromString<Catalog>(requireNotNull(javaClass.classLoader?.getResource("catalog.json")).readText())
    private suspend fun cache(api: Api) = LearnerCache(profile = api.profile(), catalog = catalog)
    @Test fun wholeClientSyncCommitsOnlineAnswerBeforeReportAndStopsOnReceiptSaveFailure() = runBlocking {
        val api = Api(); val store = Store(cache(api)); var repo = LearnerRepository(api,store)
        repo.startPractice(); repo.draft(answers[0].answer); repo.hint()
        api.lose = true; assertTrue(runCatching {repo.answer()}.isFailure)
        val pending = repo.state.practice!!.pending
        val report = ContentReportRequest("v2",java.util.UUID.randomUUID().toString(),session.questions[0].id,session.questions[0].exercise.revision,"other")
        store.cache = repo.state.copy(contentReport = report); repo = LearnerRepository(api,store)
        api.after = {store.fail = true}
        assertTrue(runCatching {repo.syncSavedWork()}.isFailure)
        assertTrue(api.reports.isEmpty()); assertNull(repo.state.practice!!.evaluation)
        store.fail = false; api.after = {}; repo = LearnerRepository(api,store)
        repo.syncSavedWork()
        assertEquals(listOf(pending,pending,pending),api.attempts)
        assertEquals(listOf(report),api.reports); assertTrue(repo.state.reportRecorded)
        assertEquals(1,repo.state.practice!!.graded); assertTrue(repo.state.practice!!.assisted)
        repo.syncSavedWork(); assertEquals(3,api.attempts.size); assertEquals(1,api.reports.size)
    }
    @Test fun partialCompletionPreservesDraftAndFrozenRetryAcrossRestartAndSaveFailure() = runBlocking {
        val api = Api(); val store = Store(cache(api)); var repo = LearnerRepository(api,store)
        repo.startPractice(); repo.draft(answers[0].answer); repo.hint()
        val draft = repo.state.practice!!.draft
        api.lose = true; assertTrue(runCatching {repo.finishPractice()}.isFailure)
        val frozen = repo.state.practice!!.completion!!
        assertEquals("partial",frozen.mode)
        assertTrue(runCatching {repo.discardPractice()}.isFailure)
        assertTrue(runCatching {repo.answer()}.isFailure)
        repo = LearnerRepository(api,store)
        api.after = {store.fail = true}; assertTrue(runCatching {repo.finishPractice()}.isFailure)
        assertNull(repo.state.practice!!.completionReceipt)
        store.fail = false; api.after = {}; repo = LearnerRepository(api,store); repo.finishPractice()
        assertEquals(listOf(frozen,frozen,frozen),api.completions)
        assertNotNull(repo.state.practice!!.completionReceipt)
        assertEquals(draft,repo.state.practice!!.draft); assertTrue(repo.state.practice!!.assisted)
        repo.finishPractice(); assertEquals(3,api.completions.size)
    }
    @Test fun reportRetrySurvivesRestartAndAcknowledgmentSaveFailureWithoutChangingPractice() = runBlocking {
        val api = Api(); val file = File(Files.createTempDirectory("report").toFile(), "cache.json")
        val store = AtomicLearnerStore(file); store.write(cache(api))
        var repo = LearnerRepository(api, store); repo.startPractice(); repo.draft(answers[0].answer); repo.hint()
        val practice = repo.state.practice
        api.lose = true; assertTrue(runCatching { repo.reportProblem("ambiguous_prompt") }.isFailure)
        val frozen = repo.state.contentReport
        repo = LearnerRepository(api,store); assertEquals(practice,repo.state.practice)
        repo.reportProblem("other")
        assertEquals(frozen,api.reports[1]); assertTrue(repo.state.reportRecorded); assertEquals(practice,repo.state.practice)
        val memory = Store(repo.state.copy(contentReport = null, reportRecorded = false)); repo = LearnerRepository(api,memory)
        api.after = {memory.fail = true}; assertTrue(runCatching {repo.reportProblem("other")}.isFailure)
        assertFalse(repo.state.reportRecorded)
        val second = repo.state.contentReport
        memory.fail = false; api.after = {}; repo = LearnerRepository(api,memory); repo.reportProblem("incorrect_answer")
        assertEquals(second,api.reports.last()); assertEquals(practice,repo.state.practice)
    }
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
        assertEquals(api.requests[0], api.requests[1]); assertEquals(5, api.requests[1].questionCount)
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
    @Test fun mixedAvailabilityBoundsNewRequestsAndNeverRewritesAnUnacknowledgedRequest() = runBlocking {
        val api = Api(); val store = Store(cache(api).copy(catalog = catalog.copy(targets = catalog.targets.take(3))))
        var repo = LearnerRepository(api, store)
        api.lose = true
        assertTrue(runCatching { repo.startPractice() }.isFailure)
        assertEquals(3, api.requests.single().questionCount)
        val savedRequest = repo.state.practice!!.request
        store.cache = store.cache.copy(catalog = catalog.copy(targets = emptyList()))
        repo = LearnerRepository(api, store); repo.startPractice()
        assertEquals(savedRequest, api.requests.last())
        assertEquals(3, repo.state.practice!!.session!!.questions.size)
        repo.discardPractice()
        assertTrue(runCatching { repo.startPractice() }.isFailure)
        assertNull(repo.state.practice)
        assertEquals(2, api.requests.size)
    }
}

package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.*
import com.germanverbmaster.android.learner.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LowTypingPracticeTest {
    private fun resource(name: String) = requireNotNull(javaClass.classLoader?.getResource(name)).readText()
    private val session = ContractReader.session(resource("practice-formats-session.json"))
    private val rubrics = ContractReader.json.decodeFromString<List<OfflineRubric>>(resource("practice-formats-rubrics.json"))
    private val profile = LearnerProfile("v2", 0, true, ProfilePreferences("en", "UTC", "B1", 15))
    private class Store(var cache: LearnerCache) : LearnerStore {
        var fail = false
        override fun read() = ContractReader.json.decodeFromString<LearnerCache>(ContractReader.json.encodeToString(cache))
        override fun write(value: LearnerCache) { check(!fail); cache = value }
    }
    private inner class Api : LearnerApi {
        val attempts = mutableListOf<Attempt>()
        var lose = true
        override suspend fun sync(cursor: String) = error("unused")
        override suspend fun profile() = profile
        override suspend fun save(request: ProfileRequest) = profile
        override suspend fun catalog() = error("unused")
        override suspend fun targets(cursor: String) = error("unused")
        override suspend fun session(request: SessionRequest) = session
        override suspend fun submit(attempt: Attempt): Acknowledgment {
            attempts.add(attempt)
            if(lose) { lose = false; error("response lost") }
            val q = session.questions.first { it.id == attempt.sessionQuestionId }
            val rubric = rubrics.first { it.exerciseId == q.exercise.id }
            val evaluation = OfflineGrader.grade(q.exercise, rubric,
                ContractReader.json.encodeToJsonElement(Answer.serializer(),attempt.answer),attempt.assistance)
            return AttemptDuplicate(attempt.attemptId,evaluation,1)
        }
    }
    @Test fun newFormatsRestorePartialDraftAndFrozenSubmissionAcrossRestart() = runBlocking {
        for(index in listOf(2,3)) {
            val partial: Answer = if(index == 2) AnswerGapChoice(listOf(GapSelection("wir","b")))
                else AnswerMatching(listOf(MatchPair("decision","make")))
            val store = Store(LearnerCache(profile = profile, practice = NativePractice(foundationSessionRequest(),session = session,index = index, skipped = index)))
            val api = Api(); var repo = LearnerRepository(api,store)
            repo.draft(partial)
            assertFalse(nativeAnswerReady(session.questions[index].exercise, partial))
            assertTrue(runCatching { repo.answer() }.isFailure); assertTrue(api.attempts.isEmpty())
            repo = LearnerRepository(api,store); assertEquals(partial,repo.state.practice!!.draft)
            store.fail = true
            assertTrue(runCatching { repo.draft(rubrics[index].acceptedAnswers[0]) }.isFailure)
            assertEquals(partial,repo.state.practice!!.draft)
            store.fail = false; repo.draft(rubrics[index].acceptedAnswers[0])
            assertTrue(runCatching { repo.answer() }.isFailure)
            val frozen = repo.state.practice!!.pending
            repo = LearnerRepository(api,store); repo.answer()
            assertEquals(listOf(frozen,frozen),api.attempts)
            assertEquals("correct",repo.state.practice!!.evaluation!!.outcome)
            assertEquals(1,repo.state.practice!!.graded)
        }
    }
    @Test fun allFourFormatsGradeOfflineAndRetainFrozenOutboxAfterRestart() = runBlocking {
        val pack = PreparedPackReader.read(resource("practice-formats-pack.json"))
        val store = Store(LearnerCache(profile = profile, preparedPack = pack))
        val api = Api(); var repo = LearnerRepository(api,store)
        repo.startOffline(Instant.parse("2026-10-07T13:00:00Z"))
        for(index in 0..3) {
            val answer = rubrics[index].acceptedAnswers[0]
            if(answer is AnswerWordOrder) repo.order(answer.tokenIds) else repo.draft(answer)
            repo.answer()
            assertEquals("correct",repo.state.practice!!.evaluation!!.outcome)
            val frozen = repo.state.practice!!.outbox
            repo = LearnerRepository(api,store)
            assertEquals(frozen,repo.state.practice!!.outbox)
            if(index < 3) repo.continuePractice()
        }
        assertEquals(4,repo.state.practice!!.outbox.size)
        assertTrue(api.attempts.isEmpty())
    }
}

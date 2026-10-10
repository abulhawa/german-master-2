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
import kotlinx.coroutines.runBlocking
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NativePracticeUiTest {
    @get:Rule val compose = createComposeRule()
    @Test fun offlineChoiceFeedbackStaysProvisionalAndLocked() = runBlocking {
        val pack = PreparedPackReader.read(requireNotNull(javaClass.classLoader?.getResource("practice-formats-pack.json")).readText())
        val profile = LearnerProfile("v2", 0, true, ProfilePreferences("en", "UTC", "B1", 5))
        val api = object : LearnerApi {
            override suspend fun profile() = profile
            override suspend fun save(request: ProfileRequest) = profile
            override suspend fun targets(cursor: String) = error("offline")
            override suspend fun sync(cursor: String) = error("offline")
            override suspend fun catalog() = error("offline")
        }
        val store = object : LearnerStore {
            var cache = LearnerCache(profile = profile, preparedPack = pack)
            override fun read() = cache
            override fun write(value: LearnerCache) { cache = value }
        }
        val repo = LearnerRepository(api, store)
        repo.startOffline(Instant.parse(pack.issuedAt).plusSeconds(1))
        val exercise = repo.state.practice!!.question.exercise as ExerciseChoice
        val rubric = pack.rubrics.single { it.exerciseId == exercise.id && it.exerciseRevision == exercise.revision }
        repo.draft(rubric.acceptedAnswers.first())
        repo.answer()
        compose.setContent { FoundationTheme {
            androidx.compose.foundation.layout.Column {
                NativePracticeView(repo.state.practice!!, false, false, {}, repo, { it() }, {}, {})
            }
        } }
        compose.onNodeWithText("✓ Looks correct — not synced yet").assertExists()
        compose.onNodeWithText("This answer is saved on this device. Your progress will update after it syncs.").assertExists()
        val answer = rubric.acceptedAnswers.first() as AnswerChoice
        compose.onNodeWithText(exercise.options.single { it.id == answer.optionId }.text).assertIsSelected().assertIsNotEnabled()
        assertEquals(1, repo.state.practice!!.outbox.size)
        assertNull(repo.state.practice!!.outbox.single().attemptReceipt)
    }
    @Test fun checkedChoiceRemainsSelectedAndLockedUntilContinue() {
        val sample = ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("practice-formats-session.json")).readText())
        val session = sample.copy(questions = sample.questions.take(1))
        val exercise = session.questions.single().exercise as ExerciseChoice
        val profile = LearnerProfile("v2", 0, true, ProfilePreferences("en", "UTC", "B1", 5))
        val api = object : LearnerApi {
            override suspend fun profile() = profile
            override suspend fun sync(cursor: String) = SyncPage("v2", emptyList(), cursor, false)
            override suspend fun save(request: ProfileRequest) = profile
            override suspend fun catalog() = Catalog("v2", session.contentReleaseId, "unpublished_local_draft", emptyList(), emptyList())
            override suspend fun targets(cursor: String) = TargetPage("v2", "2026-10-09T10:00:00Z", emptyList(), "", session.id)
            override suspend fun submit(attempt: Attempt): Acknowledgment = AttemptAcknowledgment(attempt.attemptId,
                Evaluation("incorrect", "test", LocalizedText("Use the dative.", "Verwende den Dativ."), AnswerChoice(exercise.options.last().id), false), 1)
        }
        val store = object : LearnerStore {
            var cache = LearnerCache(profile = profile, practice = NativePractice(foundationSessionRequest().copy(questionCount = 5), session = session))
            override fun read() = cache
            override fun write(value: LearnerCache) { cache = value }
        }
        val repo = LearnerRepository(api, store)
        compose.setContent { FoundationTheme { LearnerShell(repo) } }
        compose.onNodeWithText("Continue practice").performScrollTo().performClick()
        val chosen = exercise.options.first()
        compose.onNodeWithText(chosen.text).performScrollTo().performClick()
        compose.onNodeWithText("Check", substring = false).performScrollTo().performClick()
        compose.waitUntil(10000) { repo.state.practice?.evaluation != null }
        compose.onNodeWithText(chosen.text).assertIsSelected().assertIsNotEnabled()
        exercise.options.drop(1).forEach { compose.onNodeWithText(it.text).assertIsNotEnabled() }
        compose.onNodeWithText("Use the dative.").performScrollTo().assertExists()
        compose.onNodeWithText("Check", substring = false).assertDoesNotExist()
        assertEquals(AnswerChoice(chosen.id), repo.state.practice!!.draft)
        assertEquals(repo.state.practice, LearnerRepository(api, store).state.practice)
    }
    @Test fun fiveFormsPersistDraftsThenShowFeedbackAndMixedSummary() {
        val session = ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("session.json")).readText())
        val answers = ContractReader.attempts(requireNotNull(javaClass.classLoader?.getResource("attempt-batch.json")).readText()).attempts
        val attempts = mutableListOf<Attempt>()
        val api = object : LearnerApi {
            override suspend fun sync(cursor: String) = SyncPage("v2", emptyList(), cursor, false)
            override suspend fun profile() = LearnerProfile("v2", 0, true, ProfilePreferences("en", "UTC", "B1", 15))
            override suspend fun save(request: ProfileRequest) = profile()
            override suspend fun catalog() = Catalog("v2", session.contentReleaseId, "unpublished_local_draft", emptyList(), emptyList())
            override suspend fun targets(cursor: String) = TargetPage("v2", "2026-10-04T10:00:00Z", emptyList(), "", session.id)
            override suspend fun session(request: SessionRequest) = session
            override suspend fun submit(attempt: Attempt): Acknowledgment { attempts.add(attempt); return AttemptAcknowledgment(attempt.attemptId, Evaluation("correct", "test", LocalizedText("Server feedback", "Serverfeedback"), answers[attempt.clientSequence].answer, attempt.assistance.isNotEmpty()), 1) }
            override suspend fun expose(event: ExposureEvent): ExposureAcknowledgment = ExposureAccepted(event.eventId, 1)
        }
        val store = object : LearnerStore {
            var cache = LearnerCache(profile = LearnerProfile("v2", 0, true, ProfilePreferences("en", "UTC", "B1", 15)), practice = NativePractice(foundationSessionRequest().copy(questionCount = 15), session = session))
            override fun read() = cache
            override fun write(value: LearnerCache) { cache = value }
        }
        val repo = LearnerRepository(api, store)
        compose.setContent { FoundationTheme { LearnerShell(repo) } }
        compose.waitForIdle()
        compose.onNodeWithText("Continue practice").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Shorter session: 5 questions available.").assertExists()
        for(index in 0..4) {
            when(index) {
                0 -> { compose.onNodeWithText("Hint").performScrollTo().performClick(); compose.onNodeWithText("Answer", substring = false).performTextInput("Berufe") }
                1 -> compose.onNodeWithText("dem", substring = false).performScrollTo().performClick()
                2 -> compose.onNodeWithText("Präposition").performTextInput("in das")
                3 -> {
                    (session.questions[3].exercise as ExerciseWordOrder).tokens.first().let { token ->
                        compose.onNodeWithText(token.text, substring = false).performScrollTo().performClick()
                        assertEquals(listOf(token.id), repo.state.practice!!.order)
                        assertEquals(repo.state.practice, LearnerRepository(api, store).state.practice)
                    }
                    compose.onNodeWithText("Reset order").performScrollTo().performClick()
                    (answers[3].answer as AnswerWordOrder).tokenIds.forEach { id -> compose.onNodeWithText((session.questions[3].exercise as ExerciseWordOrder).tokens.first { it.id == id }.text, substring = false).performScrollTo().performClick() }
                    val ordered = (answers[3].answer as AnswerWordOrder).tokenIds
                    val firstWord = (session.questions[3].exercise as ExerciseWordOrder).tokens.first { it.id == ordered[0] }.text
                    compose.onNodeWithText("Move left: $firstWord (1)").assertIsNotEnabled()
                    compose.onNodeWithText("Move right: $firstWord (1)").performScrollTo().performClick()
                    assertEquals(listOf(ordered[1], ordered[0]) + ordered.drop(2), repo.state.practice!!.order)
                    assertEquals(repo.state.practice, LearnerRepository(api, store).state.practice)
                    compose.onNodeWithText("Move left: $firstWord (2)").performScrollTo().performClick()
                    assertEquals(ordered, (repo.state.practice!!.draft as AnswerWordOrder).tokenIds)
                }
                4 -> {
                    compose.onNodeWithText("du").performScrollTo().performTextInput("arbeitest")
                    compose.onNodeWithText("Check", substring = false).assertIsNotEnabled()
                    assertEquals(repo.state.practice, LearnerRepository(api, store).state.practice)
                    compose.onNodeWithText("ihr").performScrollTo().performTextInput("arbeitet")
                }
            }
            if(index == 1) compose.onNodeWithText("Skip", substring = false).performScrollTo().performClick()
            else {
                if (index == 0) compose.onNodeWithText("Answer", substring = false).performImeAction()
                else compose.onNodeWithText("Check", substring = false).performScrollTo().performClick()
                compose.waitForIdle()
                compose.onNodeWithText("Server feedback").assertExists()
                compose.onNodeWithText("Continue", substring = false).performScrollTo().performClick()
            }
            compose.waitForIdle()
            if (index < 4) compose.onNodeWithText(session.questions[index + 1].exercise.prompt).assertIsFocused()
        }
        compose.onNodeWithText("Practice summary").assertExists()
        compose.onNodeWithText("Graded: 4 · Skipped: 1 · Correct: 4").assertExists()
        compose.onNodeWithText("Skills practised").assertExists()
        compose.onNodeWithText("View Progress").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Your progress").assertExists()
        assertTrue(attempts[0].assistance.contains("hint"))
    }
}


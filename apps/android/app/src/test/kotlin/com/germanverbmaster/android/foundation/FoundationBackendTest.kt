package com.germanverbmaster.android.foundation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.germanverbmaster.android.foundation.contract.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[35])
class FoundationBackendTest {
    @get:Rule val compose = createComposeRule()
    private fun resource(name: String) = requireNotNull(javaClass.classLoader?.getResource(name)).readText()

    @Test fun retryPreservesSubmissionAndDisplaysServerOutcome() {
        val session = ContractReader.session(resource("session.json"))
        val attempts = mutableListOf<Attempt>()
        val api = object : FoundationApi {
            override suspend fun createSession(request: SessionRequest) = session
            override suspend fun submit(attempt: Attempt): Acknowledgment {
                attempts.add(attempt)
                if(attempts.size==1) error("Simulated timeout after send")
                // UI must show the server outcome even if typed text differs from the accepted answer.
                return AttemptDuplicate(attempt.attemptId, Evaluation("correct","deterministic-v1/de-nfc-trim-v1",
                    LocalizedText("Server explanation","Servererklärung"), AnswerShortAnswer("Berufe"),false),1)
            }
        }
        compose.setContent { FoundationTheme { FoundationBackendPreview(api) } }
        compose.waitForIdle()
        compose.onNodeWithText("Your answer").performTextInput("berufe")
        compose.onNodeWithText("Check answer").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Retry").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Correct").assertExists()
        compose.onNodeWithText("Accepted answer: Berufe").assertExists()
        assertEquals(2, attempts.size)
        assertEquals(attempts[0],attempts[1])
        compose.onNodeWithText("Continue").performScrollTo().performClick()
        compose.onNodeWithText("Check answer").assertIsNotEnabled()
    }

    @Test fun confirmsAllFiveFormsAndRecordsHintAssistance() {
        val session = ContractReader.session(resource("session.json"))
        val accepted = ContractReader.attempts(resource("attempt-batch.json")).attempts
        val submitted = mutableListOf<Attempt>()
        val api = object : FoundationApi {
            override suspend fun createSession(request: SessionRequest) = session
            override suspend fun submit(attempt: Attempt): Acknowledgment {
                submitted.add(attempt)
                return AttemptAcknowledgment(attempt.attemptId,Evaluation("correct","deterministic-v1/de-nfc-trim-v1",
                    LocalizedText("Server explanation","Servererklärung"),accepted[attempt.clientSequence].answer,attempt.assistance.isNotEmpty()),submitted.size)
            }
        }
        compose.setContent { FoundationTheme { FoundationBackendPreview(api) } }
        compose.waitForIdle()
        for(index in 0..4) {
            when(index) {
                0 -> { compose.onNodeWithText("Hint").performScrollTo().performClick(); compose.onNodeWithText("Your answer").performTextInput("Berufe") }
                1 -> compose.onNodeWithText("dem").performScrollTo().performClick()
                2 -> compose.onNodeWithText("Präposition").performTextInput("in das")
                3 -> listOf("weil","ich","heute","im","Büro","arbeite").forEach { compose.onNodeWithText(it).performScrollTo().performClick() }
                4 -> { compose.onNodeWithText("du").performTextInput("arbeitest"); compose.onNodeWithText("ihr").performTextInput("arbeitet") }
            }
            compose.onNodeWithText("Check answer").performScrollTo().performClick()
            compose.waitForIdle()
            compose.onNodeWithText("Continue").performScrollTo().performClick()
        }
        compose.onNodeWithText("Session complete").assertExists()
        assertEquals(listOf("hint"),submitted[0].assistance)
        assertTrue(submitted.drop(1).all { it.assistance.isEmpty() })
        assertEquals(session.questions.map { it.id },submitted.map { it.sessionQuestionId })
    }
}

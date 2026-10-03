package com.germanverbmaster.android.foundation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.germanverbmaster.android.foundation.contract.Answer
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[35])
class FoundationRenderTest {
    @get:Rule val compose = createComposeRule()
    @Test fun rendersEverySharedExerciseForm() {
        val session=ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("session.json")).readText())
        compose.setContent { FoundationTheme { FoundationPreview(session) } }
        session.questions.forEach { question ->
            compose.onNodeWithText(question.exercise.prompt).assertExists()
            compose.onNodeWithText("Inspect answer").assertIsNotEnabled()
            compose.onNodeWithText("Next exercise").performScrollTo().performClick()
        }
    }
}

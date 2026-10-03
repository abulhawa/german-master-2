package com.germanverbmaster.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.domain.model.GrammarTable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class GrammarBottomSheetLayoutTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun grammarBottomSheetContent_fillsAvailableHeight() {
        composeRule.setContent {
            MaterialTheme {
                Box(Modifier.requiredSize(width = 320.dp, height = 640.dp)) {
                    GrammarBottomSheetContent(
                        currentPage = -1,
                        totalPages = 7,
                        onPrev = {},
                        modifier = Modifier.testTag(SHEET_CONTENT_TAG),
                        content = {
                            Box(Modifier.fillMaxSize())
                        }
                    )
                }
            }
        }

        composeRule
            .onNodeWithTag(SHEET_CONTENT_TAG)
            .assertHeightIsEqualTo(640.dp)
    }

    @Test
    fun grammarTableCard_showsRuleAndExpandsExamples() {
        composeRule.setContent {
            MaterialTheme {
                GrammarTableCard(
                    table = GrammarTable(
                        title = "Test",
                        note = "Note",
                        headers = listOf("A", "B"),
                        rows = listOf(listOf("a", "b")),
                        examples = listOf("Ein Beispiel."),
                        rule = "Eine klare Regel.",
                        mistakes = listOf("Ein typischer Fehler.")
                    )
                )
            }
        }

        composeRule.onNodeWithText("Regel").assertIsDisplayed()
        composeRule.onNodeWithText("• Eine klare Regel.").assertIsDisplayed()

        composeRule.onNodeWithText("Beispiele").performClick()
        composeRule.onNodeWithText("• Ein Beispiel.").assertIsDisplayed()

        composeRule.onNodeWithText("Typische Fehler").performClick()
        composeRule.onNodeWithText("• Ein typischer Fehler.").assertIsDisplayed()
    }

    private companion object {
        const val SHEET_CONTENT_TAG = "grammar_sheet_content"
    }
}

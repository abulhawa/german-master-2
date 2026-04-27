package com.germanverbmaster.android.ui.auth

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AccountBrandHeaderTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun accountBrandHeader_showsAppNameAndIcon() {
        composeRule.setContent {
            MaterialTheme {
                AccountBrandHeader()
            }
        }

        composeRule.onNodeWithText("German Master").assertIsDisplayed()
        composeRule.onNodeWithText("Account").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("German Master icon").assertIsDisplayed()
    }
}

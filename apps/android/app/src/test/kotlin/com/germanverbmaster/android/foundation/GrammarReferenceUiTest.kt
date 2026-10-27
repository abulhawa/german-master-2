package com.germanverbmaster.android.foundation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.foundation.contract.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[35])
class GrammarReferenceUiTest {
    @get:Rule val compose=createComposeRule()
    @get:Rule val folder=TemporaryFolder()
    @Test fun referenceCanBeSearchedAndClosedWithoutChangingPractice() {
        val path=File(folder.root,"practice");val auth=mutableListOf<String>()
        val context=androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val starter=ContractReader.json.decodeFromString<GuestStarterContent>(context.assets.open("starter.json").bufferedReader().use {it.readText()})
        NativeGuestStore(path).write(NativeGuestState(started=true,session=starter.session))
        compose.setContent {FoundationTheme {NativeGuestEntry(path,false,{},auth::add)}}
        compose.onNodeWithText("Continue session").performScrollTo().performClick()
        compose.onNodeWithText("Berufe").performScrollTo().performClick()
        val before=NativeGuestStore(path).read()
        compose.onNodeWithText("Grammar",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("Search grammar").performTextInput("relativ")
        compose.onNodeWithText("Relative pronouns",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("Relativpronomen",substring=false).assertExists()
        compose.onNodeWithText("All topics").performScrollTo().performClick()
        compose.onNodeWithText("Search grammar").performTextClearance()
        compose.onNodeWithText("Search grammar").performTextInput("zzzznomatch")
        compose.onNodeWithText("No matching topics.").assertExists()
        compose.onNodeWithContentDescription("Close reference").performClick()
        compose.onNodeWithText("Berufe").assertIsSelected()
        assertEquals(before,NativeGuestStore(path).read());assertTrue(auth.isEmpty())
    }
    @Test fun germanReferenceIsAvailableFromHomeWithoutSigningIn() {
        compose.setContent {FoundationTheme {NativeGuestEntry(File(folder.root,"german"),true,{}, {})}}
        compose.onNodeWithText("Grammatik",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("Grammatik-Referenz").assertExists()
        compose.onNodeWithText("Adjektivdeklination",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("Adjektivdeklination: Bestimmter Artikel").assertExists()
        compose.onNodeWithContentDescription("Referenz schließen").performClick()
        compose.onNodeWithText("Übung starten").assertExists()
    }
}

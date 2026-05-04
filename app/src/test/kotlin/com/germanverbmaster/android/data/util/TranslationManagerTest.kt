package com.germanverbmaster.android.data.util

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TranslationManagerTest {

    private lateinit var translationManager: TranslationManager
    private val deToTarget: Translator = mockk()
    private val targetToDe: Translator = mockk()

    @Before
    fun setup() {
        mockkStatic(Translation::class)
        every { Translation.getClient(any()) } returnsMany listOf(deToTarget, targetToDe)
        
        translationManager = TranslationManager()
    }

    @After
    fun tearDown() {
        unmockkStatic(Translation::class)
    }

    private fun <T> mockTask(result: T): Task<T> = Tasks.forResult(result)

    @Test
    fun `verifyWithRoundTrip accepts cognates when back-translation matches`() = runTest {
        val word = "Nation"
        val lang = TranslateLanguage.ENGLISH
        
        // German -> English: "Nation" (Cognate)
        every { deToTarget.translate(word) } returns mockTask("Nation")
        // English -> German: "Nation" (Confirmed)
        every { targetToDe.translate("Nation") } returns mockTask("Nation")

        val result = translationManager.verifyWithRoundTrip(word, lang)

        assertTrue("Should be Success for cognate", result is TranslationManager.TranslationResult.Success)
        if (result is TranslationManager.TranslationResult.Success) {
            assertTrue(result.translation == "Nation")
        }
    }

    @Test
    fun `verifyWithRoundTrip rejects unknown words when translation matches but back-translation fails`() = runTest {
        val word = "Asdfghjkl"
        val lang = TranslateLanguage.ENGLISH
        
        // German -> English: "Asdfghjkl" (ML Kit fallback for unknown)
        every { deToTarget.translate(word) } returns mockTask("Asdfghjkl")
        // English -> German: something different or empty
        every { targetToDe.translate("Asdfghjkl") } returns mockTask("Unknown")

        val result = translationManager.verifyWithRoundTrip(word, lang)

        assertTrue("Should be Error for truly unknown word", result is TranslationManager.TranslationResult.Error)
        if (result is TranslationManager.TranslationResult.Error) {
            assertTrue(result.message.contains("unbekannt"))
        }
    }

    @Test
    fun `verifyWithRoundTrip succeeds for normal translations`() = runTest {
        val word = "Haus"
        val lang = TranslateLanguage.ENGLISH
        
        every { deToTarget.translate(word) } returns mockTask("House")
        every { targetToDe.translate("House") } returns mockTask("Haus")

        val result = translationManager.verifyWithRoundTrip(word, lang)

        assertTrue("Should be Success for house -> Haus", result is TranslationManager.TranslationResult.Success)
        if (result is TranslationManager.TranslationResult.Success) {
            assertTrue(result.translation == "House")
        }
    }
}

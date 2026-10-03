package com.germanverbmaster.android.data.util

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TranslationManagerTest {

    private lateinit var translationManager: TranslationManager
    private val deToTarget: Translator = mockk()

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        
        mockkStatic(Translation::class)
        every { Translation.getClient(any()) } returns deToTarget
        
        translationManager = TranslationManager()
    }

    @After
    fun tearDown() {
        unmockkStatic(Translation::class)
        unmockkStatic(Log::class)
    }

    private fun <T> mockTask(result: T): Task<T> = Tasks.forResult(result)

    @Test
    fun `translateDirect returns Success for non-empty result`() = runTest {
        val word = "Haus"
        val lang = TranslateLanguage.ENGLISH
        
        coEvery { deToTarget.translate(word) } returns mockTask("House")

        val result = translationManager.translateDirect(word, lang)

        assertTrue("Should be Success", result is TranslationManager.TranslationResult.Success)
        if (result is TranslationManager.TranslationResult.Success) {
            assertEquals("House", result.translation)
        }
    }

    @Test
    fun `translateDirect returns Error for empty result`() = runTest {
        val word = "Unknown"
        val lang = TranslateLanguage.ARABIC
        
        coEvery { deToTarget.translate(word) } returns mockTask("")

        val result = translationManager.translateDirect(word, lang)

        assertTrue("Should be Error", result is TranslationManager.TranslationResult.Error)
    }

    @Test
    fun `translateDirect returns Error on exception`() = runTest {
        val word = "Haus"
        val lang = TranslateLanguage.ENGLISH
        
        every { deToTarget.translate(word) } returns Tasks.forException(RuntimeException("Network Error"))

        val result = translationManager.translateDirect(word, lang)

        assertTrue("Should be Error on exception", result is TranslationManager.TranslationResult.Error)
        if (result is TranslationManager.TranslationResult.Error) {
            assertEquals("Network Error", result.message)
        }
    }
}

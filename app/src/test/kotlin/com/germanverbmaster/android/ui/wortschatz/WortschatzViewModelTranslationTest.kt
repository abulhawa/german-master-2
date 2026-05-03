package com.germanverbmaster.android.ui.wortschatz

import android.content.Context
import android.util.Log
import app.cash.turbine.test
import com.germanverbmaster.android.data.local.AppPreferences
import com.germanverbmaster.android.data.local.dao.DrillStats
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.data.util.ModelDownloadManager
import com.germanverbmaster.android.data.util.TranslationManager
import com.germanverbmaster.android.domain.usecase.SubmitAnswerUseCase
import com.germanverbmaster.android.domain.usecase.SyncDataUseCase
import com.germanverbmaster.android.domain.usecase.SyncHistoryUseCase
import com.germanverbmaster.android.speech.TextToSpeechHelper
import com.google.mlkit.nl.translate.TranslateLanguage
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WortschatzViewModelTranslationTest {

    private val repo: WordRepository = mockk(relaxed = true)
    private val practiceRepo: PracticeRepository = mockk(relaxed = true)
    private val submitAnswerUseCase: SubmitAnswerUseCase = mockk(relaxed = true)
    private val syncDataUseCase: SyncDataUseCase = mockk(relaxed = true)
    private val syncHistoryUseCase: SyncHistoryUseCase = mockk(relaxed = true)
    private val prefs: AppPreferences = mockk(relaxed = true)
    private val tts: TextToSpeechHelper = mockk(relaxed = true)
    private val translationManager: TranslationManager = mockk(relaxed = true)
    private val modelDownloadManager: ModelDownloadManager = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private val mockWords = listOf(
        WordEntity(id = 1, lemma = "machen", pos = "V", level = "A1", english = "to do"),
        WordEntity(id = 2, lemma = "Haus", pos = "N", level = "A1", english = "house")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        mockkStatic(TranslateLanguage::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { TranslateLanguage.getAllLanguages() } returns listOf(TranslateLanguage.ENGLISH, TranslateLanguage.GERMAN)
        
        every { prefs.kiTargetLanguage } returns flowOf(TranslateLanguage.ENGLISH)
        every { repo.observeAll() } returns flowOf(mockWords)
        every { repo.observeDistinctPos() } returns flowOf(listOf("V", "N"))
        every { practiceRepo.observeCorrectTaskIds("vocabulary_drill") } returns flowOf(emptySet())
        every { practiceRepo.observeStats(any<List<String>>(), any<List<String>>()) } returns flowOf(DrillStats(0, 0))
        
        every { modelDownloadManager.isDownloading } returns MutableStateFlow(false)
        every { modelDownloadManager.error } returns MutableStateFlow(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
        unmockkStatic(TranslateLanguage::class)
    }

    @Test
    fun `advancing card resets translation and increments selection key`() = runTest {
        val viewModel = WortschatzViewModel(
            repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase, 
            prefs, tts, translationManager, modelDownloadManager, context
        )

        viewModel.state.test {
            // Skip initial states until loaded
            var state = awaitItem()
            while (state.isLoading || state.drillQueue.isEmpty()) {
                state = awaitItem()
            }

            val initialKey = state.selectionKey
            assertNull(state.aiTranslation)

            // Request translation
            viewModel.requestAiTranslation()
            state = awaitItem()
            while (state.aiTranslation == null) {
                state = awaitItem()
            }
            assertEquals("Success", (state.aiTranslation as TranslationManager.TranslationResult.Success).translation)

            // Mark correct (advances)
            viewModel.markCorrect()
            state = awaitItem()
            while (state.drillIndex == 0) {
                state = awaitItem()
            }

            assertEquals(1, state.drillIndex)
            assertNull(state.aiTranslation) // Should be reset
            assertEquals(initialKey + 1, state.selectionKey) // Should be incremented
        }
    }
}

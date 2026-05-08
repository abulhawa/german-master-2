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
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
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
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WortschatzViewModelTest {

    private val repo: WordRepository = mockk()
    private val practiceRepo: PracticeRepository = mockk()
    private val submitAnswerUseCase: SubmitAnswerUseCase = mockk()
    private val syncDataUseCase: SyncDataUseCase = mockk()
    private val syncHistoryUseCase: SyncHistoryUseCase = mockk()
    private val prefs: AppPreferences = mockk()
    private val tts: TextToSpeechHelper = mockk(relaxed = true)
    private val context: Context = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val mockWords = listOf(
        WordEntity(id = 1, lemma = "machen", pos = "V", level = "A1", english = "to do"),
        WordEntity(id = 2, lemma = "Haus", pos = "N", level = "A1", english = "house"),
        WordEntity(id = 3, lemma = "groß", pos = "Adj", level = "A1", english = "big")
    )

    private val translationManager: TranslationManager = mockk()
    private val modelDownloadManager: ModelDownloadManager = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        
        coEvery { prefs.getWortschatzLastSync() } returns 0L
        coEvery { prefs.getDrillIndex() } returns 0
        coEvery { prefs.getDrillCorrect() } returns 0
        coEvery { prefs.getDrillWrong() } returns 0
        coEvery { prefs.getDrillSeed() } returns null
        coEvery { prefs.setDrillSeed(any()) } returns mockk()
        coEvery { prefs.setWortschatzLastSync(any()) } returns mockk()
        coEvery { prefs.setDrillIndex(any()) } returns mockk()
        coEvery { prefs.setDrillCorrect(any()) } returns mockk()
        coEvery { prefs.setDrillWrong(any()) } returns mockk()
        coEvery { prefs.getWortschatzDatasetVersion() } returns null
        coEvery { prefs.setWortschatzDatasetVersion(any()) } returns mockk()
        coEvery { prefs.getWortschatzLevels() } returns setOf("B2 Beruf")
        coEvery { prefs.getWortschatzPos() } returns emptySet()
        coEvery { prefs.setWortschatzLevels(any()) } returns mockk()
        coEvery { prefs.setWortschatzPos(any()) } returns mockk()
        every { prefs.kiTargetLanguage } returns flowOf(TranslateLanguage.ENGLISH)

        every { repo.observeAll(any()) } returns flowOf(mockWords)
        every { repo.observeByLevels(any(), any()) } returns flowOf(mockWords)
        every { repo.observeByPosTypes(any(), any()) } returns flowOf(mockWords)
        every { repo.observeByLevelsAndPos(any(), any(), any()) } returns flowOf(mockWords)
        every { repo.observeDistinctPos() } returns flowOf(listOf("V", "N", "Adj"))
        every { practiceRepo.observeCorrectTaskIds("vocabulary_drill") } returns flowOf(emptySet())
        every { practiceRepo.observeStats(any<List<String>>(), any<List<String>>(), any()) } returns flowOf(DrillStats(0, 0))
        
        coEvery { repo.fetchDatasetVersion() } returns null
        coEvery { repo.needsSync() } returns false
        coEvery { repo.sync(any()) } returns Unit
        coEvery { submitAnswerUseCase(any(), any(), any(), any()) } returns Unit
        coEvery { syncDataUseCase() } returns Unit
        coEvery { syncHistoryUseCase() } returns Unit

        coEvery { modelDownloadManager.isModelDownloaded(any()) } returns true
        every { modelDownloadManager.isDownloading } returns MutableStateFlow(false)
        every { modelDownloadManager.error } returns MutableStateFlow(null)
        every { prefs.isAiAutoTranslateEnabled } returns flowOf(false)
        
        coEvery { translationManager.translateDirect(any(), any()) } returns TranslationManager.TranslationResult.Success("Success")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `selectTab to DRILL does not reset queue if already built`() = runTest {
        val viewModel = WortschatzViewModel(
            repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase, 
            prefs, tts, translationManager, modelDownloadManager, context
        )
        
        viewModel.state.test {
            // Initial load
            var state = awaitItem()
            while (state.isLoading || state.drillQueue.isEmpty()) {
                state = awaitItem()
            }
            
            assertEquals(WortschatzTab.DRILL, state.tab)
            val firstQueue = state.drillQueue
            assertNotEquals(0, firstQueue.size)

            // Switch to LIST
            viewModel.selectTab(WortschatzTab.LIST)
            state = awaitItem()
            while (state.tab != WortschatzTab.LIST) {
                state = awaitItem()
            }
            assertEquals(WortschatzTab.LIST, state.tab)

            // Switch back to DRILL
            viewModel.selectTab(WortschatzTab.DRILL)
            state = awaitItem()
            while (state.tab != WortschatzTab.DRILL) {
                state = awaitItem()
            }
            assertEquals(WortschatzTab.DRILL, state.tab)
            assertEquals(firstQueue, state.drillQueue) // Queue should be same
        }
    }

    @Test
    fun `mastery count is correctly calculated from masteredIds`() = runTest {
        val masteredFlow = MutableStateFlow(emptySet<String>())
        every { practiceRepo.observeCorrectTaskIds("vocabulary_drill") } returns masteredFlow
        
        val viewModel = WortschatzViewModel(
            repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase, 
            prefs, tts, translationManager, modelDownloadManager, context
        )
        
        viewModel.state.test {
            var state = awaitItem()
            while (state.isLoading || state.listCards.isEmpty()) {
                state = awaitItem()
            }
            
            assertEquals(0, state.masteredCount)

            masteredFlow.value = setOf("word_1", "word_3")
            
            state = awaitItem()
            while (state.masteredIds.isEmpty()) {
                state = awaitItem()
            }

            assertEquals(2, state.masteredCount)
            assertEquals(2f/3f, state.masteryProgress, 0.01f)
        }
    }

    @Test
    fun `default B2 Beruf selection filters words by b2 beruf collection`() = runTest {
        WortschatzViewModel(
            repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase,
            prefs, tts, translationManager, modelDownloadManager, context
        )

        testDispatcher.scheduler.advanceUntilIdle()

        verify(atLeast = 1) { repo.observeAll("b2_beruf") }
        verify(atLeast = 1) { practiceRepo.observeStats(any<List<String>>(), any<List<String>>(), "b2_beruf") }
    }

    @Test
    fun `markCorrect records b2 beruf collection metadata when b2 beruf filter is active`() = runTest {
        val resultSlot = slot<com.germanverbmaster.android.domain.model.PracticeResult>()
        coEvery { submitAnswerUseCase(capture(resultSlot), any(), any(), any()) } returns Unit

        val viewModel = WortschatzViewModel(
            repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase,
            prefs, tts, translationManager, modelDownloadManager, context
        )

        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.drillCurrent != null)

        viewModel.markCorrect()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf("b2_beruf"), resultSlot.captured.collections)
    }

    @Test
    fun `triggerSync skips network sync within 24h when dataset version unchanged`() = runTest {
        val now = System.currentTimeMillis()
        coEvery { prefs.getWortschatzLastSync() } returns now
        coEvery { prefs.getWortschatzDatasetVersion() } returns "dataset-v1"
        coEvery { repo.fetchDatasetVersion() } returns "dataset-v1"

        WortschatzViewModel(
            repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase,
            prefs, tts, translationManager, modelDownloadManager, context
        )

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { repo.sync(any()) }
    }

    @Test
    fun `triggerSync does not skip recent sync when local words need sync`() = runTest {
        val now = System.currentTimeMillis()
        coEvery { prefs.getWortschatzLastSync() } returns now
        coEvery { prefs.getWortschatzDatasetVersion() } returns "dataset-v1"
        coEvery { repo.fetchDatasetVersion() } returns "dataset-v1"
        coEvery { repo.needsSync() } returns true

        WortschatzViewModel(
            repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase,
            prefs, tts, translationManager, modelDownloadManager, context
        )

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(atLeast = 1) { repo.sync(forceFullRefresh = true) }
    }

    @Test
    fun `triggerSync forces full word sync when dataset version changes`() = runTest {
        val now = System.currentTimeMillis()
        coEvery { prefs.getWortschatzLastSync() } returns now
        coEvery { prefs.getWortschatzDatasetVersion() } returns "dataset-v1"
        coEvery { repo.fetchDatasetVersion() } returns "dataset-v2"

        WortschatzViewModel(
            repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase,
            prefs, tts, translationManager, modelDownloadManager, context
        )

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(atLeast = 1) { repo.sync(forceFullRefresh = true) }
        coVerify(atLeast = 1) { prefs.setWortschatzDatasetVersion("dataset-v2") }
    }

    @Test
    fun `toggleLevel B2 Beruf clears other levels`() = runTest {
        val viewModel = WortschatzViewModel(
            repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase,
            prefs, tts, translationManager, modelDownloadManager, context
        )

        viewModel.toggleLevel("A1")
        viewModel.toggleLevel("A2")
        
        viewModel.toggleLevel("B2 Beruf")
        assertEquals(setOf("B2 Beruf"), viewModel.state.value.selectedLevels)
    }

    @Test
    fun `toggleLevel A1 removes B2 Beruf`() = runTest {
        val viewModel = WortschatzViewModel(
            repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase,
            prefs, tts, translationManager, modelDownloadManager, context
        )

        // Initial is B2 Beruf (mocked in setup)
        assertTrue(viewModel.state.value.selectedLevels.contains("B2 Beruf"))

        viewModel.toggleLevel("A1")
        assertEquals(setOf("A1"), viewModel.state.value.selectedLevels)
    }

    @Test
    fun `togglePos Alle clears pos filters`() = runTest {
        val viewModel = WortschatzViewModel(
            repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase,
            prefs, tts, translationManager, modelDownloadManager, context
        )

        viewModel.togglePos("V")
        viewModel.togglePos("N")
        assertNotEquals(0, viewModel.state.value.selectedPosSet.size)

        viewModel.togglePos("Alle")
        assertEquals(0, viewModel.state.value.selectedPosSet.size)
    }
}

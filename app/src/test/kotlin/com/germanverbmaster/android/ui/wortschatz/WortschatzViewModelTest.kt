package com.germanverbmaster.android.ui.wortschatz

import android.util.Log
import app.cash.turbine.test
import com.germanverbmaster.android.data.local.AppPreferences
import com.germanverbmaster.android.data.local.dao.DrillStats
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.domain.usecase.SubmitAnswerUseCase
import com.germanverbmaster.android.domain.usecase.SyncDataUseCase
import com.germanverbmaster.android.domain.usecase.SyncHistoryUseCase
import io.mockk.coEvery
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
import org.junit.Assert.assertNotEquals
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
    private val testDispatcher = StandardTestDispatcher()

    private val mockWords = listOf(
        WordEntity(id = 1, lemma = "machen", pos = "V", level = "A1", english = "to do"),
        WordEntity(id = 2, lemma = "Haus", pos = "N", level = "A1", english = "house"),
        WordEntity(id = 3, lemma = "groß", pos = "Adj", level = "A1", english = "big")
    )

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

        every { repo.observeAll() } returns flowOf(mockWords)
        every { repo.observeByLevels(any()) } returns flowOf(mockWords)
        every { repo.observeByPosTypes(any()) } returns flowOf(mockWords)
        every { repo.observeByLevelsAndPos(any(), any()) } returns flowOf(mockWords)
        every { repo.observeDistinctPos() } returns flowOf(listOf("V", "N", "Adj"))
        every { practiceRepo.observeCorrectTaskIds("vocabulary_drill") } returns flowOf(emptySet())
        every { practiceRepo.observeStats(any<List<String>>(), any<List<String>>()) } returns flowOf(DrillStats(0, 0))
        
        coEvery { repo.needsSync() } returns false
        coEvery { repo.upsertBundledB2BerufWordsIfAvailable() } returns 0
        coEvery { syncDataUseCase() } returns Unit
        coEvery { syncHistoryUseCase() } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `selectTab to DRILL does not reset queue if already built`() = runTest {
        val viewModel = WortschatzViewModel(repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase, prefs)
        
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
        
        val viewModel = WortschatzViewModel(repo, practiceRepo, submitAnswerUseCase, syncDataUseCase, syncHistoryUseCase, prefs)
        
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
}

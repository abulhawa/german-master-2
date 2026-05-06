package com.germanverbmaster.android.ui.history

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.germanverbmaster.android.data.local.entity.LexemeEntity
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.repository.LexemeRepository
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.domain.usecase.SyncHistoryUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnswerHistoryViewModelTest {

    private val practiceRepository: PracticeRepository = mockk()
    private val wordRepository: WordRepository = mockk()
    private val lexemeRepository: LexemeRepository = mockk()
    private val syncHistoryUseCase: SyncHistoryUseCase = mockk()
    private val prefs: com.germanverbmaster.android.data.local.AppPreferences = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val mockAttempts = listOf(
        PracticeHistoryEntity(
            localId = 1,
            taskId = "t1",
            lexemeId = "l1",
            lemma = "machen",
            pos = "V",
            taskType = "conjugation",
            result = "correct",
            submittedAnswer = "mache",
            correctAnswer = "mache",
            responseMs = 1000,
            submittedAt = "2023-10-01T10:00:00Z",
        ),
        PracticeHistoryEntity(
            localId = 2,
            taskId = "t2",
            lexemeId = "l2",
            lemma = "Haus",
            pos = "N",
            taskType = "translation",
            result = "incorrect",
            submittedAnswer = "Hous",
            correctAnswer = "Haus",
            responseMs = 2000,
            submittedAt = "2023-10-01T11:00:00Z",
        ),
    )

    private val attemptsFlow = MutableStateFlow(mockAttempts)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { practiceRepository.observeRecent(500) } returns attemptsFlow
        every { practiceRepository.observeDistinctPos() } returns MutableStateFlow(listOf("V", "N", "Adj"))
        coEvery { lexemeRepository.getByIds(any()) } returns emptyList()
        coEvery { lexemeRepository.getById(any()) } returns null
        coEvery { syncHistoryUseCase() } returns Unit
        
        coEvery { prefs.getHistoryPos() } returns emptySet()
        coEvery { prefs.getHistoryLevels() } returns emptySet()
        coEvery { prefs.getHistoryLatest() } returns false
        coEvery { prefs.setHistoryPos(any()) } returns mockk()
        coEvery { prefs.setHistoryLevels(any()) } returns mockk()
        coEvery { prefs.setHistoryLatest(any()) } returns mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state reflects all attempts from repository`() = runTest {
        val viewModel = AnswerHistoryViewModel(
            practiceRepository,
            wordRepository,
            lexemeRepository,
            syncHistoryUseCase,
            prefs,
            SavedStateHandle(),
        )

        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals(true, initialState.isLoading)

            advanceUntilIdle()
            val loadedState = awaitItem()
            assertEquals(false, loadedState.isLoading)
            assertEquals(2, loadedState.attempts.size)
            assertEquals(null, loadedState.filterResult)
            assertEquals(emptySet<String>(), loadedState.filterPosSet)
        }
    }

    @Test
    fun `filtering by result correctly filters attempts`() = runTest {
        val viewModel = AnswerHistoryViewModel(
            practiceRepository,
            wordRepository,
            lexemeRepository,
            syncHistoryUseCase,
            prefs,
            SavedStateHandle(),
        )

        viewModel.state.test {
            awaitItem()
            advanceUntilIdle()
            awaitItem()

            viewModel.setFilterResult("correct")

            advanceUntilIdle()
            val filteredState = awaitItem()
            assertEquals("correct", filteredState.filterResult)
            assertEquals(1, filteredState.attempts.size)
            assertEquals("machen", filteredState.attempts[0].lemma)
        }
    }

    @Test
    fun `filtering by POS correctly filters attempts`() = runTest {
        val viewModel = AnswerHistoryViewModel(
            practiceRepository,
            wordRepository,
            lexemeRepository,
            syncHistoryUseCase,
            prefs,
            SavedStateHandle(),
        )

        viewModel.state.test {
            awaitItem()
            advanceUntilIdle()
            awaitItem()

            viewModel.togglePos("N")

            advanceUntilIdle()
            val filteredState = awaitItem()
            assertEquals(setOf("N"), filteredState.filterPosSet)
            assertEquals(1, filteredState.attempts.size)
            assertEquals("Haus", filteredState.attempts[0].lemma)
        }
    }

    @Test
    fun `initial result from SavedStateHandle is applied`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("result" to "incorrect"))
        val viewModel = AnswerHistoryViewModel(
            practiceRepository,
            wordRepository,
            lexemeRepository,
            syncHistoryUseCase,
            prefs,
            savedStateHandle,
        )

        viewModel.state.test {
            awaitItem()
            advanceUntilIdle()
            val loadedState = awaitItem()
            assertEquals("incorrect", loadedState.filterResult)
            assertEquals(1, loadedState.attempts.size)
            assertEquals("Haus", loadedState.attempts[0].lemma)
        }
    }

    @Test
    fun `blank remote lemma is hydrated from local lexemes`() = runTest {
        attemptsFlow.value = listOf(
            PracticeHistoryEntity(
                localId = 3,
                taskId = "t3",
                lexemeId = "lex-3",
                lemma = "",
                pos = "V",
                taskType = "conjugation",
                result = "correct",
                responseMs = 900,
                submittedAt = "2023-10-01T12:00:00Z",
            ),
        )
        coEvery { lexemeRepository.getByIds(listOf("lex-3")) } returns listOf(
            LexemeEntity(
                id = "lex-3",
                lemma = "gehen",
                pos = "V",
                cefrLevel = "B1",
            ),
        )

        val viewModel = AnswerHistoryViewModel(
            practiceRepository,
            wordRepository,
            lexemeRepository,
            syncHistoryUseCase,
            prefs,
            SavedStateHandle(),
        )

        viewModel.state.test {
            awaitItem()
            advanceUntilIdle()
            val loadedState = awaitItem()
            assertEquals("gehen", loadedState.attempts[0].lemma)
            assertEquals("B1", loadedState.attempts[0].cefrLevel)
        }
    }

    @Test
    fun `word lookup falls back to lexeme hydration when lemma is blank`() = runTest {
        val attempt = PracticeHistoryEntity(
            localId = 4,
            taskId = "t4",
            lexemeId = "lex-4",
            lemma = "",
            pos = "N",
            taskType = "translation",
            result = "incorrect",
            responseMs = 700,
            submittedAt = "2023-10-01T13:00:00Z",
        )
        coEvery { lexemeRepository.getById("lex-4") } returns LexemeEntity(
            id = "lex-4",
            lemma = "Haus",
            pos = "N",
        )
        coEvery { wordRepository.findIdByLemmaAndPos("Haus", "N") } returns 41

        val viewModel = AnswerHistoryViewModel(
            practiceRepository,
            wordRepository,
            lexemeRepository,
            syncHistoryUseCase,
            prefs,
            SavedStateHandle(),
        )

        assertEquals(41, viewModel.getWordIdForHistory(attempt))
    }
}

package com.germanverbmaster.android.ui.history

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.data.repository.WordRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
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
            submittedAt = "2023-10-01T10:00:00Z"
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
            submittedAt = "2023-10-01T11:00:00Z"
        )
    )

    private val attemptsFlow = MutableStateFlow(mockAttempts)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { practiceRepository.observeRecent(200) } returns attemptsFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state reflects all attempts from repository`() = runTest {
        val viewModel = AnswerHistoryViewModel(practiceRepository, wordRepository, SavedStateHandle())

        viewModel.state.test {
            // Initial state (isLoading = true)
            val initialState = awaitItem()
            assertEquals(true, initialState.isLoading)

            // State after repository data is emitted
            val loadedState = awaitItem()
            assertEquals(false, loadedState.isLoading)
            assertEquals(2, loadedState.attempts.size)
            assertEquals(null, loadedState.filterResult)
            assertEquals(null, loadedState.filterPos)
        }
    }

    @Test
    fun `filtering by result correctly filters attempts`() = runTest {
        val viewModel = AnswerHistoryViewModel(practiceRepository, wordRepository, SavedStateHandle())

        viewModel.state.test {
            awaitItem() // Skip loading
            awaitItem() // Skip initial loaded state

            viewModel.setFilterResult("correct")

            val filteredState = awaitItem()
            assertEquals("correct", filteredState.filterResult)
            assertEquals(1, filteredState.attempts.size)
            assertEquals("machen", filteredState.attempts[0].lemma)
        }
    }

    @Test
    fun `filtering by POS correctly filters attempts`() = runTest {
        val viewModel = AnswerHistoryViewModel(practiceRepository, wordRepository, SavedStateHandle())

        viewModel.state.test {
            awaitItem() // Skip loading
            awaitItem() // Skip initial loaded state

            viewModel.setFilterPos("N")

            val filteredState = awaitItem()
            assertEquals("N", filteredState.filterPos)
            assertEquals(1, filteredState.attempts.size)
            assertEquals("Haus", filteredState.attempts[0].lemma)
        }
    }

    @Test
    fun `initial result from SavedStateHandle is applied`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("result" to "incorrect"))
        val viewModel = AnswerHistoryViewModel(practiceRepository, wordRepository, savedStateHandle)

        viewModel.state.test {
            awaitItem() // Skip loading
            
            val loadedState = awaitItem()
            assertEquals("incorrect", loadedState.filterResult)
            assertEquals(1, loadedState.attempts.size)
            assertEquals("Haus", loadedState.attempts[0].lemma)
        }
    }
}

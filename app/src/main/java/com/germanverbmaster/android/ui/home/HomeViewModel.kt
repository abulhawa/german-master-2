package com.germanverbmaster.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.domain.model.PracticeMode
import com.germanverbmaster.android.domain.model.PracticeResult
import com.germanverbmaster.android.domain.model.SessionStats
import com.germanverbmaster.android.domain.model.TaskCard
import com.germanverbmaster.android.domain.usecase.GetNextTaskUseCase
import com.germanverbmaster.android.domain.usecase.SubmitAnswerUseCase
import com.germanverbmaster.android.domain.usecase.SyncDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val isOffline: Boolean = false,
    val mode: PracticeMode = PracticeMode.ALL,
    val cefrLevel: String? = null,  // null = no level filter; set explicitly by user
    val b2ExamMode: Boolean = false,
    val queue: List<TaskCard> = emptyList(),
    val currentTask: TaskCard? = null,
    val stats: SessionStats = SessionStats(),
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNextTask: GetNextTaskUseCase,
    private val submitAnswer: SubmitAnswerUseCase,
    private val syncData: SyncDataUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        syncAndLoad()
    }

    private fun syncAndLoad() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        
        // 1. Load local data immediately so the app is usable offline/instantly
        loadNextBatch()
        _state.update { it.copy(isLoading = false) }

        // 2. Run sync in the background
        _state.update { it.copy(isSyncing = true) }
        try {
            syncData()
            // 3. Refresh if sync brought in new/updated tasks
            loadNextBatch()
        } catch (e: Exception) {
            e.printStackTrace()
            _state.update { it.copy(isOffline = true) }
        } finally {
            _state.update { it.copy(isSyncing = false) }
        }
    }

    fun refresh() {
        syncAndLoad()
    }

    private suspend fun loadNextBatch() {
        val effectiveMode = if (_state.value.b2ExamMode) PracticeMode.B2_EXAM else _state.value.mode
        val tasks = getNextTask(
            mode = effectiveMode,
            cefrLevel = if (_state.value.b2ExamMode) null else _state.value.cefrLevel,
        )
        _state.update { it.copy(queue = tasks, currentTask = tasks.firstOrNull()) }
    }

    fun setMode(mode: PracticeMode) {
        _state.update { it.copy(mode = mode) }
        viewModelScope.launch { loadNextBatch() }
    }

    fun setCefrLevel(level: String?) {
        _state.update { it.copy(cefrLevel = level) }
        viewModelScope.launch { loadNextBatch() }
    }

    fun toggleB2ExamMode() {
        _state.update { it.copy(b2ExamMode = !it.b2ExamMode) }
        viewModelScope.launch { loadNextBatch() }
    }

    fun submitResult(task: TaskCard, isCorrect: Boolean, submitted: String, correct: String, responseMs: Int) {
        viewModelScope.launch {
            submitAnswer(
                result = PracticeResult(
                    taskId    = task.taskId,
                    lexemeId  = task.lexemeId,
                    pos       = task.pos,
                    taskType  = task.taskType,
                    renderer  = task.renderer,
                    result    = if (isCorrect) "correct" else "incorrect",
                    responseMs = responseMs,
                    cefrLevel = task.cefrLevel,
                ),
                lemma = task.lemma,
                submitted = submitted,
                correct = correct
            )
            val newStats = if (isCorrect)
                _state.value.stats.copy(correct = _state.value.stats.correct + 1)
            else
                _state.value.stats.copy(incorrect = _state.value.stats.incorrect + 1)

            val remaining = _state.value.queue.drop(1)
            val next = remaining.firstOrNull()
            _state.update { it.copy(stats = newStats, queue = remaining, currentTask = next) }
            if (remaining.size < 5) viewModelScope.launch { loadNextBatch() }
        }
    }

    fun skip() {
        val remaining = _state.value.queue.drop(1)
        _state.update {
            it.copy(
                queue = remaining,
                currentTask = remaining.firstOrNull(),
                stats = it.stats.copy(skipped = it.stats.skipped + 1),
            )
        }
    }
}

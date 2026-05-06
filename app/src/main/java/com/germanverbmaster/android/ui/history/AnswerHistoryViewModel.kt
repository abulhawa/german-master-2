package com.germanverbmaster.android.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.data.local.AppPreferences
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.repository.LexemeRepository
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.ui.wortschatz.canonicalPos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val attempts: List<PracticeHistoryEntity> = emptyList(),
    val filterResult: String? = null, // "correct", "incorrect", null
    val filterPosSet: Set<String> = emptySet(),
    val filterLevelSet: Set<String> = emptySet(),
    val posOptions: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val showLatestOnly: Boolean = false,
    val attemptCounts: Map<String, Int> = emptyMap()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnswerHistoryViewModel @Inject constructor(
    private val practiceRepository: PracticeRepository,
    private val wordRepository: WordRepository,
    private val lexemeRepository: LexemeRepository,
    private val syncHistoryUseCase: com.germanverbmaster.android.domain.usecase.SyncHistoryUseCase,
    private val prefs: AppPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialResult: String? = savedStateHandle["result"]
    private val _filterResult = MutableStateFlow(initialResult)
    private val _filterPosSet = MutableStateFlow<Set<String>>(emptySet())
    private val _filterLevelSet = MutableStateFlow<Set<String>>(emptySet())
    private val _showLatestOnly = MutableStateFlow(false)
    private val _rawPosOptions = MutableStateFlow<List<String>>(emptyList())

    init {
        viewModelScope.launch {
            _filterPosSet.value = prefs.getHistoryPos()
            _filterLevelSet.value = prefs.getHistoryLevels()
            _showLatestOnly.value = prefs.getHistoryLatest()

            try {
                syncHistoryUseCase()
            } catch (e: Exception) {
                android.util.Log.e("AnswerHistoryViewModel", "Failed to sync history", e)
            }
        }
        observePosOptions()
    }

    private fun observePosOptions() {
        viewModelScope.launch {
            this@AnswerHistoryViewModel.practiceRepository.observeDistinctPos().collect { posList ->
                _rawPosOptions.value = posList
            }
        }
    }

    private data class FilterParams(
        val result: String?,
        val posSet: Set<String>,
        val levelSet: Set<String>,
        val latestOnly: Boolean
    )

    private val _filterParams = combine(
        _filterResult, _filterPosSet, _filterLevelSet, _showLatestOnly
    ) { result, pos, level, latest ->
        FilterParams(result, pos, level, latest)
    }

    val state: StateFlow<HistoryUiState> = combine(
        practiceRepository.observeRecent(500),
        _filterParams,
        _rawPosOptions
    ) { attempts, filters, rawOptions ->
        // Normalize the UI options
        val displayOptions = listOf("Alle") + rawOptions.map { canonicalPos(it) }.distinct()

        // 1. First hydrate basic lemma/level if missing so we can filter by them
        val hydrated = hydrateAttempts(attempts)

        // 2. Calculate attempt counts per lexemeId (before de-duplication)
        val attemptCounts = hydrated.groupBy { it.lexemeId }.mapValues { it.value.size }

        // 3. Optional De-duplication: Only keep the latest attempt per word
        val processed = if (filters.latestOnly) {
            hydrated.groupBy { it.lexemeId }
                .map { (_, group) -> group.maxBy { it.submittedAt } }
                .sortedByDescending { it.submittedAt }
        } else {
            hydrated
        }

        // 4. Apply POS and Level filters for count calculations
        val categoryFiltered = processed.filter {
            (filters.posSet.isEmpty() || filters.posSet.contains(canonicalPos(it.pos))) &&
            (filters.levelSet.isEmpty() || filters.levelSet.contains(it.cefrLevel))
        }

        val correctCount = categoryFiltered.count { it.result == "correct" }
        val incorrectCount = categoryFiltered.count { it.result == "incorrect" }

        // 5. Final filter for display (Correct/Incorrect toggle)
        val filtered = if (filters.result == null) categoryFiltered else categoryFiltered.filter { it.result == filters.result }

        HistoryUiState(
            attempts = filtered,
            filterResult = filters.result,
            filterPosSet = filters.posSet,
            filterLevelSet = filters.levelSet,
            posOptions = displayOptions,
            isLoading = false,
            correctCount = correctCount,
            incorrectCount = incorrectCount,
            showLatestOnly = filters.latestOnly,
            attemptCounts = attemptCounts
        )
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    fun toggleLatestOnly() {
        _showLatestOnly.update { current ->
            val next = !current
            viewModelScope.launch { prefs.setHistoryLatest(next) }
            next
        }
    }

    fun setFilterResult(result: String?) {
        _filterResult.value = result
    }

    fun togglePos(pos: String) {
        _filterPosSet.update { current ->
            val next = if (pos == "Alle") {
                emptySet()
            } else {
                if (current.contains(pos)) current - pos else current + pos
            }
            viewModelScope.launch { prefs.setHistoryPos(next) }
            next
        }
    }

    fun toggleLevel(level: String) {
        _filterLevelSet.update { current ->
            val next = if (level == "Alle") {
                emptySet()
            } else {
                if (current.contains(level)) current - level else current + level
            }
            viewModelScope.launch { prefs.setHistoryLevels(next) }
            next
        }
    }

    suspend fun getWordIdForHistory(attempt: PracticeHistoryEntity): Int? {
        if (attempt.lexemeId.startsWith("word_")) {
            return attempt.lexemeId.removePrefix("word_").toIntOrNull()
        }

        val lemma = attempt.lemma.ifBlank {
            lexemeRepository.getById(attempt.lexemeId)?.lemma.orEmpty()
        }
        if (lemma.isBlank()) return null

        return wordRepository.findIdByLemmaAndPos(lemma, attempt.pos)
    }

    private suspend fun hydrateAttempts(attempts: List<PracticeHistoryEntity>): List<PracticeHistoryEntity> {
        val idsToHydrate = attempts
            .filter { it.lemma.isBlank() || it.cefrLevel == null }
            .map { it.lexemeId }
            .distinct()
        if (idsToHydrate.isEmpty()) return attempts

        val lexemesById = lexemeRepository
            .getByIds(idsToHydrate)
            .associateBy { it.id }

        return attempts.map { attempt ->
            val lexeme = lexemesById[attempt.lexemeId] ?: return@map attempt
            attempt.copy(
                lemma = attempt.lemma.ifBlank { lexeme.lemma },
                cefrLevel = attempt.cefrLevel ?: lexeme.cefrLevel,
            )
        }
    }
}

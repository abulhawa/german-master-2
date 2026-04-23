package com.germanverbmaster.android.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.repository.LexemeRepository
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.data.repository.WordRepository
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
    val posOptions: List<String> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnswerHistoryViewModel @Inject constructor(
    private val practiceRepository: PracticeRepository,
    private val wordRepository: WordRepository,
    private val lexemeRepository: LexemeRepository,
    private val syncHistoryUseCase: com.germanverbmaster.android.domain.usecase.SyncHistoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialResult: String? = savedStateHandle["result"]
    private val _filterResult = MutableStateFlow(initialResult)
    private val _filterPosSet = MutableStateFlow<Set<String>>(emptySet())
    private val _posOptions = MutableStateFlow<List<String>>(emptyList())

    init {
        viewModelScope.launch {
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
            practiceRepository.observeDistinctPos().collect { posList ->
                _posOptions.value = listOf("Alle") + posList
            }
        }
    }

    val state: StateFlow<HistoryUiState> = combine(
        practiceRepository.observeRecent(200),
        _filterResult,
        _filterPosSet,
        _posOptions
    ) { attempts, result, posSet, posOptions ->
        val filtered = attempts.filter {
            (result == null || it.result == result) &&
            (posSet.isEmpty() || posSet.contains(it.pos))
        }
        HistoryUiState(
            attempts = hydrateAttempts(filtered),
            filterResult = result,
            filterPosSet = posSet,
            posOptions = posOptions,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    fun setFilterResult(result: String?) {
        _filterResult.value = result
    }

    fun togglePos(pos: String) {
        _filterPosSet.update { current ->
            if (pos == "Alle") {
                emptySet()
            } else {
                if (current.contains(pos)) current - pos else current + pos
            }
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

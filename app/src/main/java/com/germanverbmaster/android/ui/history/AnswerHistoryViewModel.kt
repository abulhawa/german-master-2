package com.germanverbmaster.android.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HistoryUiState(
    val attempts: List<PracticeHistoryEntity> = emptyList(),
    val filterResult: String? = null, // "correct", "incorrect", null
    val filterPos: String? = null,    // "V", "N", "Adj", null
    val isLoading: Boolean = true
)

@HiltViewModel
class AnswerHistoryViewModel @Inject constructor(
    private val practiceRepository: PracticeRepository,
    private val wordRepository: WordRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialResult: String? = savedStateHandle["result"]
    private val _filterResult = MutableStateFlow<String?>(initialResult)
    private val _filterPos = MutableStateFlow<String?>(null)

    val state: StateFlow<HistoryUiState> = combine(
        practiceRepository.observeRecent(200),
        _filterResult,
        _filterPos
    ) { attempts, result, pos ->
        val filtered = attempts.filter {
            (result == null || it.result == result) &&
            (pos == null || it.pos == pos)
        }
        HistoryUiState(
            attempts = filtered,
            filterResult = result,
            filterPos = pos,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    fun setFilterResult(result: String?) {
        _filterResult.value = result
    }

    fun setFilterPos(pos: String?) {
        _filterPos.value = pos
    }

    suspend fun getWordIdForHistory(attempt: PracticeHistoryEntity): Int? {
        // Case 1: Direct "word_ID" format from Wortschatz drill
        if (attempt.lexemeId.startsWith("word_")) {
            return attempt.lexemeId.removePrefix("word_").toIntOrNull()
        }
        // Case 2: Lexeme ID from main practice — lookup by lemma and pos
        return wordRepository.findIdByLemmaAndPos(attempt.lemma, attempt.pos)
    }
}

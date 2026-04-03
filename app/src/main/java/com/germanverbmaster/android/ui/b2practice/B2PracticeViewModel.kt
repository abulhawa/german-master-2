package com.germanverbmaster.android.ui.b2practice

import androidx.lifecycle.ViewModel
import com.germanverbmaster.android.domain.model.B2Card
import com.germanverbmaster.android.domain.model.B2Category
import com.germanverbmaster.android.domain.model.CardMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class B2PracticeUiState(
    val category: B2Category = B2Category.ALL,
    val mode: CardMode = CardMode.DE_TO_EN,
    val shuffle: Boolean = true,
    val queue: List<B2Card> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val correct: Int = 0,
    val wrong: Int = 0,
    val skipped: Int = 0,
    val sessionDone: Boolean = false,
) {
    val current: B2Card? get() = queue.getOrNull(currentIndex)
    val progress: Float get() = if (queue.isEmpty()) 0f else currentIndex.toFloat() / queue.size
    val accuracy: Float get() {
        val total = correct + wrong
        return if (total == 0) 0f else correct.toFloat() / total * 100f
    }
}

@HiltViewModel
class B2PracticeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(B2PracticeUiState())
    val state: StateFlow<B2PracticeUiState> = _state.asStateFlow()

    init { buildQueue() }

    fun setCategory(category: B2Category) {
        _state.update { it.copy(category = category) }
        buildQueue()
    }

    fun setMode(mode: CardMode) {
        _state.update { it.copy(mode = mode, isFlipped = false) }
    }

    fun toggleShuffle() {
        _state.update { it.copy(shuffle = !it.shuffle) }
        buildQueue()
    }

    fun flip() {
        _state.update { it.copy(isFlipped = true) }
    }

    fun markCorrect() {
        _state.update { it.copy(correct = it.correct + 1) }
        advance()
    }

    fun markWrong() {
        _state.update { it.copy(wrong = it.wrong + 1) }
        advance()
    }

    fun skip() {
        _state.update { it.copy(skipped = it.skipped + 1) }
        advance()
    }

    fun restart() {
        _state.update { it.copy(correct = 0, wrong = 0, skipped = 0, sessionDone = false) }
        buildQueue()
    }

    private fun advance() {
        val next = _state.value.currentIndex + 1
        _state.update {
            it.copy(
                currentIndex = next,
                isFlipped = false,
                sessionDone = next >= it.queue.size,
            )
        }
    }

    private fun buildQueue() {
        val cards = B2ContentData.cardsForCategory(_state.value.category)
        val ordered = if (_state.value.shuffle) cards.shuffled() else cards
        _state.update {
            it.copy(
                queue = ordered,
                currentIndex = 0,
                isFlipped = false,
                sessionDone = false,
            )
        }
    }
}

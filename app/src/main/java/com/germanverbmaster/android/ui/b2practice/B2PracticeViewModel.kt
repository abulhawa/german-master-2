package com.germanverbmaster.android.ui.b2practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.domain.model.B2Card
import com.germanverbmaster.android.domain.model.B2Category
import com.germanverbmaster.android.domain.model.CardMode
import com.germanverbmaster.android.domain.model.PracticeResult
import com.germanverbmaster.android.domain.usecase.SubmitAnswerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
class B2PracticeViewModel @Inject constructor(
    private val submitAnswerUseCase: SubmitAnswerUseCase,
) : ViewModel() {

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
        val card = _state.value.current ?: return
        recordResult(card, "correct")
        _state.update { it.copy(correct = it.correct + 1) }
        advance()
    }

    fun markWrong() {
        val card = _state.value.current ?: return
        recordResult(card, "incorrect")
        _state.update { it.copy(wrong = it.wrong + 1) }
        advance()
    }

    fun skip() {
        _state.update { it.copy(skipped = it.skipped + 1) }
        advance()
    }

    private fun recordResult(card: B2Card, result: String) {
        viewModelScope.launch {
            submitAnswerUseCase(
                result = PracticeResult(
                    taskId = card.id,
                    lexemeId = card.id,
                    pos = when(card.category) {
                        B2Category.VERBEN_PRAEP -> "V"
                        B2Category.NOMEN_VERB -> "N"
                        else -> "Misc"
                    },
                    taskType = "b2_practice_${card.category.name.lowercase()}",
                    renderer = "b2_card",
                    result = result,
                    responseMs = 0,
                    cefrLevel = "B2"
                ),
                lemma = card.front,
                submitted = if (result == "correct") card.back else "",
                correct = card.back
            )
        }
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

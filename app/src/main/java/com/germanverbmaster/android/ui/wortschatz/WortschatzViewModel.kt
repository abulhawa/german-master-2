package com.germanverbmaster.android.ui.wortschatz

import androidx.lifecycle.ViewModel
import com.germanverbmaster.android.domain.model.B2Card
import com.germanverbmaster.android.domain.model.B2Category
import com.germanverbmaster.android.ui.b2practice.B2ContentData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ─── Topic filter ─────────────────────────────────────────────────────────────

private val WORTSCHATZ_TOPICS = listOf(
    "Alle",
    "Telefonieren",
    "Bewerbung",
    "Arbeitsalltag",
    "Formell",
)

// ─── Screen modes ─────────────────────────────────────────────────────────────

enum class WortschatzTab(val label: String) {
    LIST("Wortliste"),
    DRILL("Schnell-Drill"),
}

// ─── UI state ─────────────────────────────────────────────────────────────────

data class WortschatzUiState(
    val tab: WortschatzTab = WortschatzTab.LIST,
    val selectedTopic: String = "Alle",
    val topics: List<String> = WORTSCHATZ_TOPICS,

    // List-mode
    val listCards: List<B2Card> = emptyList(),

    // Drill-mode
    val drillQueue: List<B2Card> = emptyList(),
    val drillIndex: Int = 0,
    val drillFlipped: Boolean = false,
    val drillCorrect: Int = 0,
    val drillWrong: Int = 0,
    val drillDone: Boolean = false,
) {
    val drillCurrent: B2Card? get() = drillQueue.getOrNull(drillIndex)
    val drillProgress: Float get() =
        if (drillQueue.isEmpty()) 0f else drillIndex.toFloat() / drillQueue.size
    val drillAccuracy: Float get() {
        val total = drillCorrect + drillWrong
        return if (total == 0) 0f else drillCorrect.toFloat() / total * 100f
    }
}

// ─── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class WortschatzViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(WortschatzUiState())
    val state: StateFlow<WortschatzUiState> = _state.asStateFlow()

    init { refresh() }

    fun selectTab(tab: WortschatzTab) {
        _state.update { it.copy(tab = tab) }
        if (tab == WortschatzTab.DRILL) buildDrill()
    }

    fun selectTopic(topic: String) {
        _state.update { it.copy(selectedTopic = topic) }
        refresh()
    }

    fun flip() = _state.update { it.copy(drillFlipped = true) }

    fun markCorrect() {
        _state.update { it.copy(drillCorrect = it.drillCorrect + 1) }
        advance()
    }

    fun markWrong() {
        _state.update { it.copy(drillWrong = it.drillWrong + 1) }
        advance()
    }

    fun skip() = advance()

    fun restartDrill() {
        _state.update { it.copy(drillCorrect = 0, drillWrong = 0, drillDone = false) }
        buildDrill()
    }

    // ─── private ──────────────────────────────────────────────────────────────

    private fun refresh() {
        val filtered = filteredCards()
        _state.update { it.copy(listCards = filtered) }
        if (_state.value.tab == WortschatzTab.DRILL) buildDrill()
    }

    private fun buildDrill() {
        val cards = filteredCards().shuffled()
        _state.update {
            it.copy(
                drillQueue = cards,
                drillIndex = 0,
                drillFlipped = false,
                drillDone = false,
            )
        }
    }

    private fun advance() {
        val next = _state.value.drillIndex + 1
        _state.update {
            it.copy(
                drillIndex = next,
                drillFlipped = false,
                drillDone = next >= it.drillQueue.size,
            )
        }
    }

    private fun filteredCards(): List<B2Card> {
        val all = B2ContentData.cardsForCategory(B2Category.WORTSCHATZ)
        val topic = _state.value.selectedTopic
        return if (topic == "Alle") all else all.filter { it.topic == topic }
    }
}

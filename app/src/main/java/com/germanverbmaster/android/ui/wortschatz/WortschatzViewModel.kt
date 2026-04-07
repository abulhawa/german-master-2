package com.germanverbmaster.android.ui.wortschatz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── Filter options ───────────────────────────────────────────────────────────

val LEVEL_FILTERS = listOf("Alle", "A1", "A2", "B1", "B2", "C1")
val POS_FILTERS   = listOf("Alle", "V", "N", "Adj")
val POS_LABELS    = mapOf("Alle" to "Alle", "V" to "Verben", "N" to "Nomen", "Adj" to "Adjektive")

// ─── Screen modes ─────────────────────────────────────────────────────────────

enum class WortschatzTab(val label: String) {
    LIST("Wortliste"),
    DRILL("Schnell-Drill"),
}

// ─── UI state ─────────────────────────────────────────────────────────────────

data class WortschatzUiState(
    val tab: WortschatzTab = WortschatzTab.LIST,

    // Filters
    val selectedLevel: String = "B2",
    val selectedPos: String = "Alle",

    // Loading
    val isLoading: Boolean = true,
    val syncError: String? = null,

    // List-mode
    val listCards: List<WordEntity> = emptyList(),

    // Drill-mode
    val drillQueue: List<WordEntity> = emptyList(),
    val drillIndex: Int = 0,
    val drillFlipped: Boolean = false,
    val drillCorrect: Int = 0,
    val drillWrong: Int = 0,
    val drillDone: Boolean = false,
) {
    val drillCurrent: WordEntity? get() = drillQueue.getOrNull(drillIndex)
    val drillProgress: Float get() =
        if (drillQueue.isEmpty()) 0f else drillIndex.toFloat() / drillQueue.size
    val drillAccuracy: Float get() {
        val total = drillCorrect + drillWrong
        return if (total == 0) 0f else drillCorrect.toFloat() / total * 100f
    }
}

// ─── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class WortschatzViewModel @Inject constructor(
    private val repo: WordRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(WortschatzUiState())
    val state: StateFlow<WortschatzUiState> = _state.asStateFlow()

    init {
        // Trigger a background sync if the words table is empty
        viewModelScope.launch {
            if (repo.needsSync()) {
                try {
                    repo.sync()
                } catch (e: Exception) {
                    _state.update { it.copy(syncError = "Sync fehlgeschlagen: ${e.message}") }
                }
            }
        }
        observeWords()
    }

    fun selectTab(tab: WortschatzTab) {
        _state.update { it.copy(tab = tab) }
        if (tab == WortschatzTab.DRILL) buildDrill()
    }

    fun selectLevel(level: String) {
        _state.update { it.copy(selectedLevel = level) }
        observeWords()
    }

    fun selectPos(pos: String) {
        _state.update { it.copy(selectedPos = pos) }
        observeWords()
    }

    fun flip()        = _state.update { it.copy(drillFlipped = true) }
    fun markCorrect() { _state.update { it.copy(drillCorrect = it.drillCorrect + 1) }; advance() }
    fun markWrong()   { _state.update { it.copy(drillWrong   = it.drillWrong   + 1) }; advance() }
    fun skip()        = advance()

    fun restartDrill() {
        _state.update { it.copy(drillCorrect = 0, drillWrong = 0, drillDone = false) }
        buildDrill()
    }

    // ─── private ──────────────────────────────────────────────────────────────

    private var observeJob: kotlinx.coroutines.Job? = null

    private fun observeWords() {
        observeJob?.cancel()
        val level = _state.value.selectedLevel
        val pos   = _state.value.selectedPos

        val flow = when {
            level == "Alle" && pos == "Alle" -> repo.observeAll()
            level == "Alle"                  -> repo.observeByPos(pos)
            pos   == "Alle"                  -> repo.observeByLevel(level)
            else                             -> repo.observeByLevelAndPos(level, pos)
        }

        observeJob = viewModelScope.launch {
            flow.catch { e ->
                _state.update { it.copy(isLoading = false, syncError = e.message) }
            }.collect { words ->
                _state.update { s ->
                    s.copy(
                        isLoading = false,
                        listCards = words,
                        // Rebuild drill queue live if user hasn't started yet
                        drillQueue = if (s.drillDone || s.drillIndex == 0) words.shuffled()
                                     else s.drillQueue,
                    )
                }
            }
        }
    }

    private fun buildDrill() {
        val shuffled = _state.value.listCards.shuffled()
        _state.update {
            it.copy(
                drillQueue  = shuffled,
                drillIndex  = 0,
                drillFlipped = false,
                drillDone   = false,
            )
        }
    }

    private fun advance() {
        val next = _state.value.drillIndex + 1
        _state.update {
            it.copy(
                drillIndex   = next,
                drillFlipped = false,
                drillDone    = next >= it.drillQueue.size,
            )
        }
    }
}

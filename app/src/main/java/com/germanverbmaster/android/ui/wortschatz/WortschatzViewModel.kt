package com.germanverbmaster.android.ui.wortschatz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.domain.model.PracticeResult
import com.germanverbmaster.android.domain.usecase.SubmitAnswerUseCase
import com.germanverbmaster.android.domain.usecase.SyncDataUseCase
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
val POS_LABELS    = mapOf(
    "Alle" to "Alle",
    "V"    to "Verben",
    "N"    to "Nomen",
    "Adj"  to "Adjektive",
    "Adv"  to "Adverbien",
    "Prep" to "Präpositionen",
    "Conj" to "Konjunktionen",
    "Pron" to "Pronomen",
    "Int"  to "Interjektionen",
    "Art"  to "Artikel",
    "Num"  to "Numerale"
)

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
    val posOptions: List<String> = listOf("Alle", "V", "N", "Adj"),

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
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val syncDataUseCase: SyncDataUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(WortschatzUiState())
    val state: StateFlow<WortschatzUiState> = _state.asStateFlow()

    init {
        triggerSync()
        observeWords()
        observePosFilters()
    }

    fun triggerSync() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, syncError = null) }
            try {
                if (repo.needsSync()) repo.sync()
                syncDataUseCase()
                Log.d("WortschatzViewModel", "Sync completed successfully")
            } catch (e: Exception) {
                Log.e("WortschatzViewModel", "Sync failed", e)
                _state.update { it.copy(syncError = "Sync fehlgeschlagen: ${e.message}") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
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

    fun flip()        = _state.update { it.copy(drillFlipped = !it.drillFlipped) }

    fun markCorrect() {
        val word = _state.value.drillCurrent ?: return
        recordResult(word, "correct")
        _state.update { it.copy(drillCorrect = it.drillCorrect + 1) }
        advance()
    }

    fun markWrong() {
        val word = _state.value.drillCurrent ?: return
        recordResult(word, "incorrect")
        _state.update { it.copy(drillWrong = it.drillWrong + 1) }
        advance()
    }

    fun skip() = advance()

    private fun recordResult(word: WordEntity, result: String) {
        viewModelScope.launch {
            submitAnswerUseCase(
                result = PracticeResult(
                    taskId = "word_${word.id}",
                    lexemeId = "word_${word.id}",
                    pos = word.pos,
                    taskType = "vocabulary_drill",
                    renderer = "word_card",
                    result = result,
                    responseMs = 0, // Timing not yet tracked in this UI
                    cefrLevel = word.level
                ),
                lemma = word.lemma,
                submitted = if (result == "correct") word.lemma else "",
                correct = word.lemma
            )
        }
    }

    fun restartDrill() {
        _state.update { it.copy(drillCorrect = 0, drillWrong = 0, drillDone = false) }
        buildDrill()
    }

    // ─── private ──────────────────────────────────────────────────────────────

    private var observeJob: kotlinx.coroutines.Job? = null

    private fun observePosFilters() {
        viewModelScope.launch {
            repo.observeDistinctPos().collect { posList ->
                val filters = listOf("Alle") + posList
                _state.update { it.copy(posOptions = filters) }
            }
        }
    }

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

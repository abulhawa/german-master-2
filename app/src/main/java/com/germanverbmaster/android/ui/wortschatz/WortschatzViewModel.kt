package com.germanverbmaster.android.ui.wortschatz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.repository.SyncPreferences
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

val LEVEL_FILTERS = listOf("B2 Beruf", "Alle", "A1", "A2", "B1", "B2", "C1")
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
    DRILL("Schnell-Drill"),
    LIST("Wortliste"),
}

// ─── UI state ─────────────────────────────────────────────────────────────────

data class WortschatzUiState(
    val tab: WortschatzTab = WortschatzTab.DRILL,

    // Filters
    val selectedLevels: Set<String> = setOf("B2 Beruf"),
    val selectedPosSet: Set<String> = emptySet(),
    val posOptions: List<String> = listOf("Alle", "V", "N", "Adj"),
    val searchQuery: String = "",

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
    private val prefs: SyncPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(WortschatzUiState())
    val state: StateFlow<WortschatzUiState> = _state.asStateFlow()

    init {
        triggerSync(force = false)
        observeWords()
        observePosFilters()
    }

    fun triggerSync(force: Boolean) {
        viewModelScope.launch {
            val lastSync = prefs.getWortschatzLastSync()
            val now = System.currentTimeMillis()
            val twentyFourHours = 24 * 60 * 60 * 1000L

            if (!force && (now - lastSync) < twentyFourHours) {
                Log.d("WortschatzViewModel", "Skipping sync, last sync was less than 24h ago")
                return@launch
            }

            _state.update { it.copy(isLoading = true, syncError = null) }
            try {
                val shouldRunRemoteWordSync = repo.needsSync()
                repo.upsertBundledB2BerufWordsIfAvailable()
                if (shouldRunRemoteWordSync) repo.sync()
                syncDataUseCase()
                prefs.setWortschatzLastSync(now)
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

    fun toggleLevel(level: String) {
        _state.update { s ->
            val next = if (level == "Alle") {
                emptySet()
            } else {
                if (s.selectedLevels.contains(level)) s.selectedLevels - level else s.selectedLevels + level
            }
            s.copy(selectedLevels = next)
        }
        observeWords(forceReset = true)
    }

    fun togglePos(pos: String) {
        _state.update { s ->
            val next = if (pos == "Alle") {
                emptySet()
            } else {
                if (s.selectedPosSet.contains(pos)) s.selectedPosSet - pos else s.selectedPosSet + pos
            }
            s.copy(selectedPosSet = next)
        }
        observeWords(forceReset = true)
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
        observeWords(forceReset = false) // No need to reset drill for search, but wait, usually search is for the list
    }

    fun flip()        = _state.update { it.copy(drillFlipped = !it.drillFlipped) }

    fun markCorrect() {
        val word = _state.value.drillCurrent ?: return
        recordResult(word, "correct")
        val nextCorrect = _state.value.drillCorrect + 1
        _state.update { it.copy(drillCorrect = nextCorrect) }
        viewModelScope.launch { prefs.setDrillCorrect(nextCorrect) }
        advance()
    }

    fun markWrong() {
        val word = _state.value.drillCurrent ?: return
        recordResult(word, "incorrect")
        val nextWrong = _state.value.drillWrong + 1
        _state.update { it.copy(drillWrong = nextWrong) }
        viewModelScope.launch { prefs.setDrillWrong(nextWrong) }
        advance()
    }

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
        viewModelScope.launch {
            prefs.setDrillCorrect(0)
            prefs.setDrillWrong(0)
            prefs.setDrillIndex(0)
        }
        buildDrill(0)
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

    private fun observeWords(forceReset: Boolean = false) {
        observeJob?.cancel()
        _state.update { it.copy(isLoading = true) }
        val levels = _state.value.selectedLevels.toList()
        val posList = _state.value.selectedPosSet.toList()

        val flow = when {
            levels.isEmpty() && posList.isEmpty() -> repo.observeAll()
            levels.isEmpty()                      -> repo.observeByPosTypes(posList)
            posList.isEmpty()                     -> repo.observeByLevels(levels)
            else                                  -> repo.observeByLevelsAndPos(levels, posList)
        }

        observeJob = viewModelScope.launch {
            flow.catch { e ->
                _state.update { it.copy(isLoading = false, syncError = e.message) }
            }.collect { words ->
                val query = _state.value.searchQuery.trim().lowercase()
                val filteredWords = if (query.isEmpty()) {
                    words
                } else {
                    words.filter { word ->
                        word.lemma.lowercase().contains(query) ||
                                (word.english?.lowercase()?.contains(query) ?: false)
                    }
                }

                val savedIndex = if (forceReset) 0 else prefs.getDrillIndex()
                val savedCorrect = if (forceReset) 0 else prefs.getDrillCorrect()
                val savedWrong = if (forceReset) 0 else prefs.getDrillWrong()
                
                if (forceReset) {
                    viewModelScope.launch {
                        prefs.setDrillIndex(0)
                        prefs.setDrillCorrect(0)
                        prefs.setDrillWrong(0)
                    }
                }

                _state.update { s ->
                    val isFirstLoad = s.drillQueue.isEmpty()
                    
                    // Force reset if filters changed or if session was already done.
                    val shouldReset = forceReset || s.drillDone
                    
                    val queue = if (shouldReset || isFirstLoad) filteredWords.shuffled() else s.drillQueue
                    
                    // If first load or forced reset, use saved stats (0 if forced). Otherwise, follow current state or reset if finished.
                    val finalIndex = if (isFirstLoad || forceReset) {
                        if (savedIndex < queue.size) savedIndex else 0
                    } else if (shouldReset) {
                        0
                    } else {
                        s.drillIndex
                    }
                    
                    val finalCorrect = if (isFirstLoad || forceReset) savedCorrect else if (shouldReset) 0 else s.drillCorrect
                    val finalWrong = if (isFirstLoad || forceReset) savedWrong else if (shouldReset) 0 else s.drillWrong

                    s.copy(
                        isLoading = false,
                        listCards = filteredWords,
                        drillQueue = queue,
                        drillIndex = finalIndex,
                        drillCorrect = finalCorrect,
                        drillWrong = finalWrong,
                        drillDone = finalIndex >= queue.size && queue.isNotEmpty(),
                        drillFlipped = false
                    )
                }
            }
        }
    }

    private fun buildDrill(startIndex: Int = 0) {
        val shuffled = _state.value.listCards.shuffled()
        _state.update {
            it.copy(
                drillQueue  = shuffled,
                drillIndex  = startIndex,
                drillFlipped = false,
                drillDone   = startIndex >= shuffled.size && shuffled.isNotEmpty(),
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
        viewModelScope.launch { prefs.setDrillIndex(next) }
    }
}

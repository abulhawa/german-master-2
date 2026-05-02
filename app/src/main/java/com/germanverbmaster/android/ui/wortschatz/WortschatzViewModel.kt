package com.germanverbmaster.android.ui.wortschatz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.data.local.AppPreferences
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.domain.model.PracticeResult
import com.germanverbmaster.android.domain.usecase.SubmitAnswerUseCase
import com.germanverbmaster.android.domain.usecase.SyncDataUseCase
import com.germanverbmaster.android.domain.usecase.SyncHistoryUseCase
import com.germanverbmaster.android.speech.TextToSpeechHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

// ─── Filter options ───────────────────────────────────────────────────────────

val LEVEL_FILTERS = listOf("B2 Beruf", "Alle", "A1", "A2", "B1", "B2")
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

fun canonicalPos(raw: String): String {
    val upper = raw.trim().uppercase()
    return when (upper) {
        "V", "VERB" -> "V"
        "N", "NOMEN" -> "N"
        "ADJ", "ADJEKTIV" -> "Adj"
        "ADV", "ADVERB" -> "Adv"
        "PREP", "PRÄP", "PRÄPOSITION", "PRÄPOSITIONEN" -> "Prep"
        "CONJ", "KONJ", "KONJUNKTION" -> "Conj"
        "PRON", "PRONOMEN" -> "Pron"
        "INT", "INTERJEKTION" -> "Int"
        "ART", "ARTIKEL" -> "Art"
        "NUM", "NUMERALE" -> "Num"
        else -> raw
    }
}

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
    val historicalCorrect: Int = 0,
    val historicalWrong: Int = 0,
    val drillDone: Boolean = false,
    val masteredIds: Set<String> = emptySet(),
) {
    val drillCurrent: WordEntity? get() = drillQueue.getOrNull(drillIndex)

    // Progress is based on overall mastery in the current selection
    val masteryProgress: Float get() =
        if (listCards.isEmpty()) 0f else masteredCount.toFloat() / listCards.size

    val masteredCount: Int get() = listCards.count { masteredIds.contains("word_${it.id}") }
}

// ─── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class WortschatzViewModel @Inject constructor(
    private val repo: WordRepository,
    private val practiceRepo: PracticeRepository,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val syncDataUseCase: SyncDataUseCase,
    private val syncHistoryUseCase: SyncHistoryUseCase,
    private val prefs: AppPreferences,
    private val tts: TextToSpeechHelper,
) : ViewModel() {

    private val _state = MutableStateFlow(WortschatzUiState())
    val state: StateFlow<WortschatzUiState> = _state.asStateFlow()

    private var rawPosValues: List<String> = emptyList()

    init {
        triggerSync(force = false)
        observeWords()
        observePosFilters()
        observeMastery()
        observeHistoricalStats()
    }

    fun speak(text: String) {
        tts.speak(text)
    }

    private fun observeMastery() {
        viewModelScope.launch {
            practiceRepo.observeCorrectTaskIds("vocabulary_drill").collect { ids ->
                _state.update { it.copy(masteredIds = ids) }
            }
        }
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
                syncHistoryUseCase()
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
        // Only build drill if it's empty, to avoid resetting progress on tab switch
        if (tab == WortschatzTab.DRILL && _state.value.drillQueue.isEmpty()) {
            buildDrill()
        }
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
        // Force reset drill if we are currently in DRILL tab, otherwise just update list
        val shouldReset = _state.value.tab == WortschatzTab.DRILL
        observeWords(forceReset = shouldReset)
        observeHistoricalStats()
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
        val shouldReset = _state.value.tab == WortschatzTab.DRILL
        observeWords(forceReset = shouldReset)
        observeHistoricalStats()
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
        val shouldReset = _state.value.tab == WortschatzTab.DRILL
        observeWords(forceReset = shouldReset)
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
    private var statsJob: kotlinx.coroutines.Job? = null

    private fun observeHistoricalStats() {
        statsJob?.cancel()
        val levels = _state.value.selectedLevels.toList()
        val posList = _state.value.selectedPosSet.flatMap { selected ->
            rawPosValues.filter { canonicalPos(it) == selected }
        }

        statsJob = viewModelScope.launch {
            practiceRepo.observeStats(levels, posList).collect { stats ->
                _state.update {
                    it.copy(
                        historicalCorrect = stats.correct,
                        historicalWrong = stats.wrong
                    )
                }
            }
        }
    }

    private fun observePosFilters() {
        viewModelScope.launch {
            repo.observeDistinctPos().collect { posList ->
                rawPosValues = posList
                val filters = listOf("Alle") + posList.map { canonicalPos(it) }.distinct()
                _state.update { it.copy(posOptions = filters) }
                observeHistoricalStats()
            }
        }
    }

    private fun observeWords(forceReset: Boolean = false) {
        observeJob?.cancel()
        _state.update { it.copy(isLoading = true) }
        val levels = _state.value.selectedLevels.toList()
        
        // Expand canonical POS keys back to all matching raw values from DB
        val posList = _state.value.selectedPosSet.flatMap { selected ->
            rawPosValues.filter { canonicalPos(it) == selected }
        }

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
                
                var seed = prefs.getDrillSeed()
                if (seed == null || forceReset) {
                    seed = System.currentTimeMillis()
                    viewModelScope.launch { prefs.setDrillSeed(seed) }
                }

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
                    
                    val queue = if (shouldReset || isFirstLoad) {
                        filteredWords.shuffled(Random(seed))
                    } else {
                        s.drillQueue
                    }
                    
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
        viewModelScope.launch {
            val seed = System.currentTimeMillis()
            prefs.setDrillSeed(seed)
            val shuffled = _state.value.listCards.shuffled(Random(seed))
            _state.update {
                it.copy(
                    drillQueue = shuffled,
                    drillIndex = startIndex,
                    drillFlipped = false,
                    drillDone = startIndex >= shuffled.size && shuffled.isNotEmpty(),
                )
            }
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

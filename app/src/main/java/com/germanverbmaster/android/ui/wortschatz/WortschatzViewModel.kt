package com.germanverbmaster.android.ui.wortschatz

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.germanverbmaster.android.data.local.AppPreferences
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.remote.worker.SyncWorker
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.data.util.ModelDownloadManager
import com.germanverbmaster.android.data.util.PosNormalizer
import com.germanverbmaster.android.data.util.TranslationManager
import com.germanverbmaster.android.domain.model.PracticeResult
import com.germanverbmaster.android.domain.usecase.SubmitAnswerUseCase
import com.germanverbmaster.android.domain.usecase.SyncDataUseCase
import com.germanverbmaster.android.domain.usecase.SyncHistoryUseCase
import com.germanverbmaster.android.speech.TextToSpeechHelper
import com.germanverbmaster.android.ui.components.languageNameMap
import com.google.mlkit.nl.translate.TranslateLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

// ─── Filter options ───────────────────────────────────────────────────────────

val LEVEL_FILTERS = listOf("Alle", "A1", "A2", "B1", "B2", "B2 Beruf")
val POS_LABELS    = mapOf(
    "Alle" to "Alle",
    "V"    to "Verb",
    "N"    to "Nomen",
    "Adj"  to "Adjektiv",
    "Adv"  to "Adverb",
    "Präp" to "Präposition",
    "Konj" to "Konjunktion",
    "Pron" to "Pronomen",
    "Int"  to "Interjektion",
    "Art"  to "Artikel",
    "Num"  to "Numerale",
    "Part" to "Partikel"
)

fun canonicalPos(raw: String): String = PosNormalizer.normalize(raw)

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

    // Translation & Selection
    val aiTranslation: TranslationManager.TranslationResult? = null,
    val aiExampleTranslation: TranslationManager.TranslationResult? = null,
    val selectionTranslation: TranslationManager.TranslationResult? = null,
    val selectionKey: Int = 0,
    val isModelDownloaded: Boolean = false,
    val isAiAutoTranslateEnabled: Boolean = false,
    val downloadedLanguageCodes: Set<String> = emptySet(),
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
    private val translationManager: TranslationManager,
    private val modelDownloadManager: ModelDownloadManager,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(WortschatzUiState())
    val state: StateFlow<WortschatzUiState> = _state.asStateFlow()

    private var rawPosValues: List<String> = emptyList()

    val targetLanguage = prefs.kiTargetLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TranslateLanguage.ENGLISH
    )

    val isDownloading = modelDownloadManager.isDownloading
    val downloadError = modelDownloadManager.error

    init {
        // Initial setup for non-filter-dependent observations
        triggerSync(force = false)
        observeMastery()
        observeAutoTranslate()
        observeCurrentWordForAutoTranslate()

        viewModelScope.launch {
            targetLanguage.collect { checkModelStatus() }
        }
        refreshDownloadedLanguages()

        // Reactive filter setup
        viewModelScope.launch {
            // 1. Load saved filter state
            val savedLevels = prefs.getWortschatzLevels()
            val savedPos = prefs.getWortschatzPos()
            _state.update { it.copy(selectedLevels = savedLevels, selectedPosSet = savedPos) }

            // 2. Observe POS types from DB to handle canonical mapping
            repo.observeDistinctPos().collect { posList ->
                rawPosValues = posList
                val filters = listOf("Alle") + posList.map { canonicalPos(it) }.distinct()
                _state.update { it.copy(posOptions = filters) }

                // Re-start observations that depend on rawPosValues mapping
                observeWords()
                observeHistoricalStats()
            }
        }
    }

    private fun observeCurrentWordForAutoTranslate() {
        viewModelScope.launch {
            _state.collect { s ->
                val currentWord = s.drillCurrent
                if (currentWord != null && s.aiTranslation == null && s.isAiAutoTranslateEnabled && s.isModelDownloaded) {
                    requestAiTranslation()
                }
            }
        }
    }

    private fun observeAutoTranslate() {
        viewModelScope.launch {
            prefs.isAiAutoTranslateEnabled.collect { enabled ->
                _state.update { it.copy(isAiAutoTranslateEnabled = enabled) }
            }
        }
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
            val localDatasetVersion = prefs.getWortschatzDatasetVersion()
            val remoteDatasetVersion = repo.fetchDatasetVersion()
            val datasetVersionChanged = remoteDatasetVersion != null &&
                remoteDatasetVersion != localDatasetVersion
            val localWordsNeedSync = repo.needsSync()

            if (!force && !datasetVersionChanged && !localWordsNeedSync && (now - lastSync) < twentyFourHours) {
                Log.d("WortschatzViewModel", "Skipping sync, last sync was less than 24h ago")
                return@launch
            }

            _state.update { it.copy(isLoading = true, syncError = null) }
            try {
                repo.sync(forceFullRefresh = force || datasetVersionChanged || localWordsNeedSync)
                syncDataUseCase()
                syncHistoryUseCase()
                prefs.setWortschatzLastSync(now)

                if (remoteDatasetVersion != null && remoteDatasetVersion != localDatasetVersion) {
                    prefs.setWortschatzDatasetVersion(remoteDatasetVersion)
                }

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
            } else if (level == "B2 Beruf") {
                if (s.selectedLevels.contains(level)) s.selectedLevels - level else setOf(level)
            } else {
                val withoutB2Beruf = s.selectedLevels - "B2 Beruf"
                if (withoutB2Beruf.contains(level)) withoutB2Beruf - level else withoutB2Beruf + level
            }
            viewModelScope.launch { prefs.setWortschatzLevels(next) }
            s.copy(selectedLevels = next)
        }
        observeWords(forceReset = true)
        observeHistoricalStats()
    }

    fun togglePos(pos: String) {
        _state.update { s ->
            val next = if (pos == "Alle") {
                emptySet()
            } else {
                if (s.selectedPosSet.contains(pos)) s.selectedPosSet - pos else s.selectedPosSet + pos
            }
            viewModelScope.launch { prefs.setWortschatzPos(next) }
            s.copy(selectedPosSet = next)
        }
        observeWords(forceReset = true)
        observeHistoricalStats()
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
        val shouldReset = _state.value.tab == WortschatzTab.DRILL
        observeWords(forceReset = shouldReset)
    }

    fun flip() {
        _state.update { s ->
            s.copy(drillFlipped = !s.drillFlipped)
        }
    }

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
        val selectedCollections = if (_state.value.selectedLevels.contains("B2 Beruf")) {
            listOf("b2_beruf")
        } else {
            emptyList()
        }
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
                    cefrLevel = word.level,
                    collections = selectedCollections,
                ),
                lemma = word.lemma,
                submitted = if (result == "correct") word.lemma else "",
                correct = word.lemma
            )
        }
    }

    fun restartDrill() {
        _state.update { it.copy(drillCorrect = 0, drillWrong = 0, drillDone = false, aiExampleTranslation = null) }
        viewModelScope.launch {
            prefs.setDrillCorrect(0)
            prefs.setDrillWrong(0)
            prefs.setDrillIndex(0)
        }
        buildDrill(0)
    }

    fun onExitDrill() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueue(syncRequest)
        Log.d("WortschatzViewModel", "Enqueued background sync on drill exit")
    }

    // ─── AI Translation Logic ───────────────────────────────────────────────

    private fun checkModelStatus() {
        viewModelScope.launch {
            val de = modelDownloadManager.isModelDownloaded(TranslateLanguage.GERMAN)
            val target = modelDownloadManager.isModelDownloaded(targetLanguage.value)
            _state.update { it.copy(isModelDownloaded = de && target) }
            refreshDownloadedLanguages()
        }
    }

    fun refreshDownloadedLanguages() {
        viewModelScope.launch {
            val allCodes = TranslateLanguage.getAllLanguages()
            val downloaded = mutableSetOf<String>()
            for (code in allCodes) {
                if (modelDownloadManager.isModelDownloaded(code)) {
                    downloaded.add(code)
                }
            }
            _state.update { it.copy(downloadedLanguageCodes = downloaded) }
        }
    }

    fun setTargetLanguage(langCode: String) {
        viewModelScope.launch {
            prefs.setKiTargetLanguage(langCode)
            _state.update { it.copy(aiTranslation = null, aiExampleTranslation = null) }
        }
    }

    fun setAiAutoTranslateEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setAiAutoTranslateEnabled(enabled)
        }
    }

    fun downloadModels(allowMobileData: Boolean) {
        viewModelScope.launch {
            modelDownloadManager.downloadModels(targetLanguage.value, allowMobileData)
            checkModelStatus()
        }
    }

    fun deleteLanguageModel(langCode: String) {
        viewModelScope.launch {
            modelDownloadManager.deleteModels(langCode)
            checkModelStatus()
            refreshDownloadedLanguages()
        }
    }

    fun translateSelectedText(text: String) {
        viewModelScope.launch {
            if (!_state.value.isModelDownloaded) {
                checkModelStatus()
                if (!_state.value.isModelDownloaded) return@launch
            }

            val lang = targetLanguage.value
            val result = translationManager.verifyWithRoundTrip(
                germanText = text,
                targetLang = lang
            )
            _state.update { it.copy(selectionTranslation = result) }
        }
    }

    fun clearSelectionTranslation() {
        _state.update { it.copy(selectionTranslation = null) }
    }

    fun clearSelection() {
        _state.update { it.copy(selectionKey = it.selectionKey + 1) }
    }

    fun requestAiTranslation() {
        val currentWord = _state.value.drillCurrent ?: return
        viewModelScope.launch {
            if (!_state.value.isModelDownloaded) {
                checkModelStatus()
                if (!_state.value.isModelDownloaded) return@launch
            }

            val lang = targetLanguage.value
            val langName = languageNameMap[lang] ?: lang

            // 1. Use lemma directly from database
            val translationInput = currentWord.lemma

            // 2. Direct Translation - No context, no tricks
            val result = translationManager.translateDirect(
                germanText = translationInput,
                targetLang = lang
            )
            
            _state.update { it.copy(aiTranslation = result) }

            currentWord.exampleDe?.let { example ->
                val exampleRes = translationManager.translateDirect(
                    germanText = example,
                    targetLang = lang
                )
                _state.update { it.copy(aiExampleTranslation = exampleRes) }
            }
        }
    }

    // ─── private ──────────────────────────────────────────────────────────────

    private var observeJob: kotlinx.coroutines.Job? = null
    private var statsJob: kotlinx.coroutines.Job? = null

    private fun observeHistoricalStats() {
        statsJob?.cancel()
        val selectedLevels = _state.value.selectedLevels
        val isB2BerufSelected = selectedLevels.contains("B2 Beruf")
        val collection = if (isB2BerufSelected) "b2_beruf" else null
        val levels = (selectedLevels - "B2 Beruf").toList()

        val posList = _state.value.selectedPosSet.flatMap { selected ->
            rawPosValues.filter { canonicalPos(it) == selected }
        }

        statsJob = viewModelScope.launch {
            practiceRepo.observeStats(levels, posList, collection).collect { stats ->
                _state.update {
                    it.copy(
                        historicalCorrect = stats.correct,
                        historicalWrong = stats.wrong
                    )
                }
            }
        }
    }


    private fun observeWords(forceReset: Boolean = false) {
        observeJob?.cancel()
        _state.update { it.copy(isLoading = true) }
        val selectedLevels = _state.value.selectedLevels
        
        val isB2BerufSelected = selectedLevels.contains("B2 Beruf")
        val levels = (selectedLevels - "B2 Beruf").toList()
        val collection = if (isB2BerufSelected) "b2_beruf" else null
        
        // Expand canonical POS keys back to all matching raw values from DB
        val isPosFilterActive = _state.value.selectedPosSet.isNotEmpty()
        val posList = _state.value.selectedPosSet.flatMap { selected ->
            rawPosValues.filter { canonicalPos(it) == selected }
        }

        val flow = when {
            levels.isEmpty() && !isPosFilterActive -> repo.observeAll(collection)
            levels.isEmpty()                       -> repo.observeByPosTypes(posList, collection)
            !isPosFilterActive                     -> repo.observeByLevels(levels, collection)
            else                                   -> repo.observeByLevelsAndPos(levels, posList, collection)
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
                            drillFlipped = false,
                            aiTranslation = null,
                            aiExampleTranslation = null,
                            selectionKey = s.selectionKey + 1
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
                    aiTranslation = null,
                    aiExampleTranslation = null,
                    selectionKey = it.selectionKey + 1
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
                aiTranslation = null,
                aiExampleTranslation = null,
                selectionKey = it.selectionKey + 1
            )
        }
        viewModelScope.launch { prefs.setDrillIndex(next) }
    }
}

package com.germanverbmaster.android.ui.worddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.BuildConfig
import com.germanverbmaster.android.data.local.AppPreferences
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.data.util.ModelDownloadManager
import com.germanverbmaster.android.data.util.TranslationManager
import com.germanverbmaster.android.speech.TextToSpeechHelper
import com.germanverbmaster.android.ui.components.ContextualTranslationResult
import com.google.mlkit.nl.translate.TranslateLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WordRepository,
    private val tts: TextToSpeechHelper,
    private val translationManager: TranslationManager,
    private val modelDownloadManager: ModelDownloadManager,
    private val prefs: AppPreferences,
) : ViewModel() {

    private val wordId: Int = checkNotNull(savedStateHandle["wordId"])

    val word: StateFlow<WordEntity?> = repository.observeById(wordId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _aiTranslation = MutableStateFlow<TranslationManager.TranslationResult?>(null)
    val aiTranslation = _aiTranslation.asStateFlow()

    private val _aiExampleTranslation = MutableStateFlow<TranslationManager.TranslationResult?>(null)
    val aiExampleTranslation = _aiExampleTranslation.asStateFlow()

    private val _selectionTranslation = MutableStateFlow<TranslationManager.TranslationResult?>(null)
    val selectionTranslation = _selectionTranslation.asStateFlow()

    private val _contextualSelectionTranslation = MutableStateFlow<ContextualTranslationResult?>(null)
    val contextualSelectionTranslation = _contextualSelectionTranslation.asStateFlow()

    private val _selectionKey = MutableStateFlow(0)
    val selectionKey = _selectionKey.asStateFlow()

    private val _isModelDownloaded = MutableStateFlow(false)
    val isModelDownloaded = _isModelDownloaded.asStateFlow()

    private val _downloadedLanguageCodes = MutableStateFlow<Set<String>>(emptySet())
    val downloadedLanguageCodes = _downloadedLanguageCodes.asStateFlow()

    val targetLanguage = prefs.kiTargetLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TranslateLanguage.ENGLISH
    )

    val isDownloading = modelDownloadManager.isDownloading
    val downloadError = modelDownloadManager.error

    val isDebug = BuildConfig.DEBUG

    init {
        viewModelScope.launch {
            targetLanguage.collect { checkModelStatus() }
        }
        refreshDownloadedLanguages()
    }

    private fun checkModelStatus() {
        viewModelScope.launch {
            val de = modelDownloadManager.isModelDownloaded(TranslateLanguage.GERMAN)
            val target = modelDownloadManager.isModelDownloaded(targetLanguage.value)
            _isModelDownloaded.value = de && target
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
            _downloadedLanguageCodes.value = downloaded
        }
    }

    fun setTargetLanguage(langCode: String) {
        viewModelScope.launch {
            prefs.setKiTargetLanguage(langCode)
            _aiTranslation.value = null
            _aiExampleTranslation.value = null
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
            if (!_isModelDownloaded.value) {
                checkModelStatus()
                if (!_isModelDownloaded.value) return@launch
            }

            val lang = targetLanguage.value
            val result = translationManager.verifyWithRoundTrip(
                germanText = text,
                targetLang = lang
            )
            _selectionTranslation.value = result
        }
    }

    fun setContextualSelectionTranslation(result: ContextualTranslationResult?) {
        _contextualSelectionTranslation.value = result
    }

    fun clearSelectionTranslation() {
        _selectionTranslation.value = null
        _contextualSelectionTranslation.value = null
    }

    fun clearSelection() {
        _selectionKey.value += 1
    }

    fun speak(text: String) {
        tts.speak(text)
    }

    fun getTranslationManager(): TranslationManager = translationManager

    fun requestAiTranslation(displayText: String) {
        val currentWord = word.value ?: return
        viewModelScope.launch {
            if (!_isModelDownloaded.value) {
                checkModelStatus()
                if (!_isModelDownloaded.value) return@launch
            }

            val lang = targetLanguage.value

            // 1. Prepare word for translation (Use the exact text shown in UI headline)
            val translationInput = displayText

            // 2. Translate Word (Lemma) with Context using symbol-based prompt
            val contextPrompt = if (!currentWord.exampleDe.isNullOrBlank()) {
                "[[ $translationInput ]] || { ${currentWord.exampleDe} }"
            } else {
                translationInput
            }

            _aiTranslation.value = translationManager.verifyWithRoundTrip(
                germanText = contextPrompt,
                targetLang = lang,
                originalLemma = currentWord.lemma
            )

            currentWord.exampleDe?.let { example ->
                _aiExampleTranslation.value = translationManager.verifyWithRoundTrip(
                    germanText = example,
                    targetLang = lang
                )
            }
        }
    }

    fun updateDatabaseWithAi() {
        // Disabled per user request
    }
}

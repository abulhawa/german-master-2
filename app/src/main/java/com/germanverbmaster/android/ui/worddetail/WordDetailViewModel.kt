package com.germanverbmaster.android.ui.worddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.BuildConfig
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.data.util.ModelDownloadManager
import com.germanverbmaster.android.data.util.TranslationManager
import com.germanverbmaster.android.speech.TextToSpeechHelper
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

    private val _isModelDownloaded = MutableStateFlow(false)
    val isModelDownloaded = _isModelDownloaded.asStateFlow()

    val isDownloading = modelDownloadManager.isDownloading
    val downloadError = modelDownloadManager.error

    val isDebug = BuildConfig.DEBUG

    init {
        checkModelStatus()
    }

    private fun checkModelStatus() {
        viewModelScope.launch {
            val de = modelDownloadManager.isModelDownloaded(TranslateLanguage.GERMAN)
            val en = modelDownloadManager.isModelDownloaded(TranslateLanguage.ENGLISH)
            _isModelDownloaded.value = de && en
        }
    }

    fun downloadModels(allowMobileData: Boolean) {
        viewModelScope.launch {
            modelDownloadManager.downloadModels(allowMobileData)
            checkModelStatus()
        }
    }

    fun speak(text: String) {
        tts.speak(text)
    }

    fun requestAiTranslation() {
        val currentWord = word.value ?: return
        viewModelScope.launch {
            // Check models again just in case
            if (!_isModelDownloaded.value) {
                checkModelStatus()
                if (!_isModelDownloaded.value) return@launch
            }

            // 1. Prepare word for translation (add "sich" for verbs)
            val isVerb = currentWord.pos.uppercase().startsWith("V")
            val translationInput = if (isVerb && !currentWord.lemma.startsWith("sich", ignoreCase = true)) {
                "sich ${currentWord.lemma}"
            } else {
                currentWord.lemma
            }

            // 2. Translate Word (Lemma)
            _aiTranslation.value = translationManager.verifyWithRoundTrip(
                germanText = translationInput,
                originalLemma = currentWord.lemma
            )

            // 3. Translate Example (if exists)
            currentWord.exampleDe?.let { example ->
                _aiExampleTranslation.value = translationManager.verifyWithRoundTrip(example)
            }
        }
    }

    fun updateDatabaseWithAi() {
        // Disabled per user request
    }
}

package com.germanverbmaster.android.ui.worddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.BuildConfig
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.data.util.TranslationManager
import com.germanverbmaster.android.speech.TextToSpeechHelper
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

    private val _isUpdatingDb = MutableStateFlow(false)
    val isUpdatingDb = _isUpdatingDb.asStateFlow()

    val isDebug = BuildConfig.DEBUG

    fun speak(text: String) {
        tts.speak(text)
    }

    fun requestAiTranslation() {
        val currentWord = word.value ?: return
        viewModelScope.launch {
            _aiTranslation.value = translationManager.verifyWithRoundTrip(currentWord.lemma)
        }
    }

    fun updateDatabaseWithAi() {
        val currentWord = word.value ?: return
        val result = aiTranslation.value as? TranslationManager.TranslationResult.Success ?: return
        
        if (!isDebug) return

        viewModelScope.launch {
            _isUpdatingDb.value = true
            try {
                repository.updateWord(currentWord.copy(english = result.translation))
                _aiTranslation.value = null // Clear suggestion after update
            } catch (e: Exception) {
                // Log error
            } finally {
                _isUpdatingDb.value = false
            }
        }
    }
}

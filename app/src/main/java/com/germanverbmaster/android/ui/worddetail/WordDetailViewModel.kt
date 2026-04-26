package com.germanverbmaster.android.ui.worddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.speech.TextToSpeechHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WordDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: WordRepository,
    private val tts: TextToSpeechHelper,
) : ViewModel() {

    private val wordId: Int = checkNotNull(savedStateHandle["wordId"])

    val word: StateFlow<WordEntity?> = repository.observeById(wordId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun speak(text: String) {
        tts.speak(text)
    }
}

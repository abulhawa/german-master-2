package com.germanverbmaster.android.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null

    init {
        initializeTts()
    }

    private fun initializeTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.GERMANY)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "German language is not supported or missing data")
                } else {
                    isInitialized = true
                    pendingText?.let {
                        speak(it)
                        pendingText = null
                    }
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
    }

    fun speak(text: String) {
        if (text.isBlank()) return

        if (isInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "GVM_TTS_ID")
        } else {
            pendingText = text
            if (tts == null) {
                initializeTts()
            }
        }
    }

    fun stop() {
        tts?.stop()
    }

    /**
     * Should only be called when the application is actually being destroyed
     * if managed as a Singleton.
     */
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}

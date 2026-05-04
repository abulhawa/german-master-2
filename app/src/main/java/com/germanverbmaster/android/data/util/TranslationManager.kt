package com.germanverbmaster.android.data.util

import android.util.Log
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslationManager @Inject constructor() {

    private var currentTargetLanguage: String = TranslateLanguage.ENGLISH
    private var deToTargetTranslator: Translator? = null

    private fun getTranslator(targetLang: String): Translator {
        if (targetLang == currentTargetLanguage && deToTargetTranslator != null) {
            return deToTargetTranslator!!
        }

        // Close old translator
        deToTargetTranslator?.close()

        currentTargetLanguage = targetLang
        
        val deToTargetOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.GERMAN)
            .setTargetLanguage(targetLang)
            .build()

        deToTargetTranslator = Translation.getClient(deToTargetOptions)

        return deToTargetTranslator!!
    }

    /**
     * Performs a direct translation from DE to Target.
     * No cleaning, no verification, no tricks.
     */
    suspend fun translateDirect(
        germanText: String, 
        targetLang: String
    ): TranslationResult {
        return try {
            val translator = getTranslator(targetLang)
            val result = translator.translate(germanText).await()
            if (result.isNotBlank()) {
                TranslationResult.Success(result.trim())
            } else {
                TranslationResult.Error("Keine Übersetzung gefunden")
            }
        } catch (e: Exception) {
            Log.e("TranslationManager", "Error translating", e)
            TranslationResult.Error(e.message ?: "Fehler")
        }
    }

    /**
     * Deprecated: Redirecting to direct translation.
     */
    suspend fun verifyWithRoundTrip(
        germanText: String, 
        targetLang: String,
        originalLemma: String? = null
    ): TranslationResult {
        return translateDirect(germanText, targetLang)
    }

    sealed class TranslationResult {
        data class Success(val translation: String) : TranslationResult()
        data class LowConfidence(val translation: String, val backTranslation: String) : TranslationResult()
        data class Error(val message: String) : TranslationResult()
    }
}

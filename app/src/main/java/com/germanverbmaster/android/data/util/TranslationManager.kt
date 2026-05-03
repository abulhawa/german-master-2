package com.germanverbmaster.android.data.util

import android.util.Log
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslationManager @Inject constructor() {

    private val deEnOptions = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.GERMAN)
        .setTargetLanguage(TranslateLanguage.ENGLISH)
        .build()

    private val enDeOptions = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.GERMAN)
        .build()

    private val deEnTranslator = Translation.getClient(deEnOptions)
    private val enDeTranslator = Translation.getClient(enDeOptions)

    suspend fun translateDeToEn(text: String): String? {
        if (text.isBlank()) return null
        return try {
            deEnTranslator.translate(text).await()
        } catch (e: Exception) {
            Log.e("TranslationManager", "Error translating DE to EN", e)
            null
        }
    }

    suspend fun translateEnToDe(text: String): String? {
        if (text.isBlank()) return null
        return try {
            enDeTranslator.translate(text).await()
        } catch (e: Exception) {
            Log.e("TranslationManager", "Error translating EN to DE", e)
            null
        }
    }

    /**
     * Verifies a translation using the "Round-Trip" method.
     * Translates DE -> EN, then EN -> DE.
     * Returns the EN translation if the DE -> EN -> DE result matches the original.
     */
    suspend fun verifyWithRoundTrip(germanText: String, originalLemma: String? = null): TranslationResult {
        val english = translateDeToEn(germanText) ?: return TranslationResult.Error("Translation failed")
        val backToGerman = translateEnToDe(english) ?: return TranslationResult.Error("Verification failed")

        // Use originalLemma for comparison if provided (e.g. for reflexive verbs)
        val comparisonText = originalLemma ?: germanText

        return if (backToGerman.contains(comparisonText, ignoreCase = true) || 
            comparisonText.contains(backToGerman, ignoreCase = true)) {
            TranslationResult.Success(english)
        } else {
            TranslationResult.LowConfidence(english, backToGerman)
        }
    }

    private suspend fun ensureModelDownloaded(isDeToEn: Boolean) {
        // Models are now managed by ModelDownloadManager
    }

    sealed class TranslationResult {
        data class Success(val translation: String) : TranslationResult()
        data class LowConfidence(val translation: String, val backTranslation: String) : TranslationResult()
        data class Error(val message: String) : TranslationResult()
    }
}

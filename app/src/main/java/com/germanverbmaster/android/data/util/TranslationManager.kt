package com.germanverbmaster.android.data.util

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
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
            ensureModelDownloaded(isDeToEn = true)
            deEnTranslator.translate(text).await()
        } catch (e: Exception) {
            Log.e("TranslationManager", "Error translating DE to EN", e)
            null
        }
    }

    suspend fun translateEnToDe(text: String): String? {
        if (text.isBlank()) return null
        return try {
            ensureModelDownloaded(isDeToEn = false)
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
    suspend fun verifyWithRoundTrip(germanText: String): TranslationResult {
        val english = translateDeToEn(germanText) ?: return TranslationResult.Error("Translation failed")
        val backToGerman = translateEnToDe(english) ?: return TranslationResult.Error("Verification failed")

        return if (backToGerman.equals(germanText, ignoreCase = true)) {
            TranslationResult.Success(english)
        } else {
            TranslationResult.LowConfidence(english, backToGerman)
        }
    }

    private suspend fun ensureModelDownloaded(isDeToEn: Boolean) {
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        if (isDeToEn) {
            deEnTranslator.downloadModelIfNeeded(conditions).await()
        } else {
            enDeTranslator.downloadModelIfNeeded(conditions).await()
        }
    }

    sealed class TranslationResult {
        data class Success(val translation: String) : TranslationResult()
        data class LowConfidence(val translation: String, val backTranslation: String) : TranslationResult()
        data class Error(val message: String) : TranslationResult()
    }
}

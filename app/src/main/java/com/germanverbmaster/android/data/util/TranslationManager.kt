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
    private var targetToDeTranslator: Translator? = null

    private fun getTranslators(targetLang: String): Pair<Translator, Translator> {
        if (targetLang == currentTargetLanguage && deToTargetTranslator != null && targetToDeTranslator != null) {
            return Pair(deToTargetTranslator!!, targetToDeTranslator!!)
        }

        // Close old translators
        deToTargetTranslator?.close()
        targetToDeTranslator?.close()

        currentTargetLanguage = targetLang
        
        val deToTargetOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.GERMAN)
            .setTargetLanguage(targetLang)
            .build()

        val targetToDeOptions = TranslatorOptions.Builder()
            .setSourceLanguage(targetLang)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build()

        deToTargetTranslator = Translation.getClient(deToTargetOptions)
        targetToDeTranslator = Translation.getClient(targetToDeOptions)

        return Pair(deToTargetTranslator!!, targetToDeTranslator!!)
    }

    suspend fun translateDeToTarget(text: String, targetLang: String): String? {
        if (text.isBlank()) return null
        return try {
            val (translator, _) = getTranslators(targetLang)
            translator.translate(text).await()
        } catch (e: Exception) {
            Log.e("TranslationManager", "Error translating DE to $targetLang", e)
            null
        }
    }

    suspend fun translateTargetToDe(text: String, targetLang: String): String? {
        if (text.isBlank()) return null
        return try {
            val (_, translator) = getTranslators(targetLang)
            translator.translate(text).await()
        } catch (e: Exception) {
            Log.e("TranslationManager", "Error translating $targetLang to DE", e)
            null
        }
    }

    /**
     * Verifies a translation using the "Round-Trip" method.
     * Translates DE -> Target, then Target -> DE.
     */
    suspend fun verifyWithRoundTrip(
        germanText: String, 
        targetLang: String,
        originalLemma: String? = null
    ): TranslationResult {
        val translation = translateDeToTarget(germanText, targetLang) 
            ?: return TranslationResult.Error("Translation failed")
            
        val backToGerman = translateTargetToDe(translation, targetLang) 
            ?: return TranslationResult.Error("Verification failed")

        val comparisonText = originalLemma ?: germanText

        // Relaxed comparison: check if the core words exist in the back-translation
        // We'll normalize both strings and check for keyword overlap
        val normalizedBack = backToGerman.lowercase()
        val normalizedOriginal = comparisonText.lowercase()
        
        val isSimilar = normalizedBack == normalizedOriginal || 
                        normalizedBack.contains(normalizedOriginal) || 
                        normalizedOriginal.contains(normalizedBack)

        return if (isSimilar) {
            TranslationResult.Success(translation)
        } else {
            TranslationResult.LowConfidence(translation, backToGerman)
        }
    }

    sealed class TranslationResult {
        data class Success(val translation: String) : TranslationResult()
        data class LowConfidence(val translation: String, val backTranslation: String) : TranslationResult()
        data class Error(val message: String) : TranslationResult()
    }
}

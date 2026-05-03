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
        val translationRaw = translateDeToTarget(germanText, targetLang) 
            ?: return TranslationResult.Error("Translation failed")

        // Clean up translation: If KI translated our prompt format, try to extract the core word.
        // We look for patterns like "Word: [result]" or "[result] (Context: ...)"
        val cleanedTranslation = cleanTranslationResult(translationRaw)
            
        val backToGermanRaw = translateTargetToDe(cleanedTranslation, targetLang) 
            ?: return TranslationResult.Error("Verification failed")

        val backToGerman = cleanTranslationResult(backToGermanRaw)

        val comparisonText = originalLemma ?: germanText

        // Relaxed comparison: check if the core words exist in the back-translation
        val normalizedBack = backToGerman.lowercase()
        val normalizedOriginal = comparisonText.lowercase()
        
        val isSimilar = normalizedBack == normalizedOriginal || 
                        normalizedBack.contains(normalizedOriginal) || 
                        normalizedOriginal.contains(normalizedBack)

        return if (isSimilar) {
            TranslationResult.Success(cleanedTranslation)
        } else {
            TranslationResult.LowConfidence(cleanedTranslation, backToGerman)
        }
    }

    /**
     * Extracts the core translated word if the KI included the prompt structure in its response.
     */
    private fun cleanTranslationResult(raw: String): String {
        var text = raw.trim()
        
        // Handle "Word: [Result] (Context: ...)" pattern
        // The KI might translate the labels too, so we look for colons and parentheses.
        
        // 1. Remove anything after a '(' if it likely contains "Context" or similar
        val parenIndex = text.indexOf('(')
        if (parenIndex != -1) {
            text = text.substring(0, parenIndex).trim()
        }
        
        // 2. Remove anything before a ':' if it likely contains "Word" or similar
        val colonIndex = text.indexOf(':')
        if (colonIndex != -1 && colonIndex < text.length / 2) { // Colon should be near the start
            text = text.substring(colonIndex + 1).trim()
        }

        // 3. Remove leading/trailing quotes or punctuation often added by KI
        text = text.removeSurrounding("\"").removeSurrounding("'").trim()
        
        return text.ifBlank { raw }
    }

    sealed class TranslationResult {
        data class Success(val translation: String) : TranslationResult()
        data class LowConfidence(val translation: String, val backTranslation: String) : TranslationResult()
        data class Error(val message: String) : TranslationResult()
    }
}

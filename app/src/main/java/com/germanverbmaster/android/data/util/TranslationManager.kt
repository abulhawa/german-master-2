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

        // Check if the translation is identical to the source (common fallback for unknown words in ML Kit)
        if (cleanedTranslation.equals(comparisonText, ignoreCase = true) && 
            targetLang != TranslateLanguage.GERMAN && 
            comparisonText.length > 2) {
            
            // Allow "self-translation" if the back-translation also matches (it's a cognate)
            if (!backToGerman.equals(comparisonText, ignoreCase = true)) {
                return TranslationResult.Error("Keine Übersetzung gefunden (Wort unbekannt)")
            }
        }

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
     * New format: [[ word ]] || { context }
     */
    private fun cleanTranslationResult(raw: String): String {
        var text = raw.trim()
        
        // 1. Handle [[ ]] brackets
        if (text.contains("[[") && text.contains("]]")) {
            val start = text.indexOf("[[") + 2
            val end = text.indexOf("]]", start)
            if (end != -1) {
                text = text.substring(start, end).trim()
            }
        }
        
        // 2. Remove anything after a '||' or '{' if it likely contains context
        val pipeIndex = text.indexOf("||")
        if (pipeIndex != -1) {
            text = text.substring(0, pipeIndex).trim()
        }
        
        val braceIndex = text.indexOf('{')
        if (braceIndex != -1) {
            text = text.substring(0, braceIndex).trim()
        }

        // 3. Keep existing smart parenthesis handling (for reflexive verbs)
        if (text.startsWith("(") && text.endsWith(")")) {
             // Leave it
        } else {
            val parenIndex = text.indexOf('(')
            if (parenIndex != -1) {
                text = text.substring(0, parenIndex).trim()
            }
        }
        
        // 4. Handle "Word:" or "Wort:" leakage if it still happens
        val colonIndex = text.indexOf(':')
        if (colonIndex != -1 && colonIndex < text.length / 2) {
            text = text.substring(colonIndex + 1).trim()
        }

        // 5. Cleanup quotes
        text = text.removeSurrounding("\"").removeSurrounding("'").trim()
        
        return text.ifBlank { raw }
    }

    sealed class TranslationResult {
        data class Success(val translation: String) : TranslationResult()
        data class LowConfidence(val translation: String, val backTranslation: String) : TranslationResult()
        data class Error(val message: String) : TranslationResult()
    }
}

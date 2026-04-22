package com.germanverbmaster.android.domain.usecase

import android.util.Log
import com.germanverbmaster.android.data.local.entity.TaskSpecEntity
import com.germanverbmaster.android.data.repository.TaskRepository
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.domain.model.PracticeMode
import com.germanverbmaster.android.domain.model.TaskCard
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import javax.inject.Inject

class GetNextTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val wordRepository: WordRepository,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend operator fun invoke(
        mode: PracticeMode,
        cefrLevel: String? = null,
        batchSize: Int = 20,
    ): List<TaskCard> {
        val entities = when (mode) {
            PracticeMode.VERBS      -> taskRepository.fetchBatch("V", cefrLevel, batchSize)
            PracticeMode.NOUNS      -> taskRepository.fetchBatch("N", cefrLevel, batchSize)
            PracticeMode.ADJECTIVES -> taskRepository.fetchBatch("Adj", cefrLevel, batchSize)
            PracticeMode.ALL        -> taskRepository.fetchBatch(null, cefrLevel, batchSize)
        }
        Log.d("GetNextTaskUseCase", "fetchBatch returned ${entities.size} entities (mode=$mode, cefrLevel=$cefrLevel)")
        val cards = entities.mapNotNull { entity ->
            val card = entity.toTaskCard()
            if (card == null) {
                Log.w("GetNextTaskUseCase", "toTaskCard() returned null for task id=${entity.id} pos=${entity.pos} prompt=${entity.promptJson.take(120)}")
            }
            card
        }

        // Fetch translations for each card
        val cardsWithTranslation = cards.map { card ->
            val translation = wordRepository.findTranslationByLemmaAndPos(card.lemma, card.pos)
            card.copy(translation = translation)
        }

        Log.d("GetNextTaskUseCase", "Mapped ${cardsWithTranslation.size}/${entities.size} entities to TaskCards")
        return cardsWithTranslation
    }

    private fun TaskSpecEntity.toTaskCard(): TaskCard? = runCatching {
        val promptMap = parseJsonToMap(promptJson)
        val solutionMap = parseJsonToMap(solutionJson)
        val lemma = promptMap["lemma"] ?: promptMap["word"] ?: return@runCatching null
        TaskCard(
            taskId    = id,
            lexemeId  = lexemeId,
            lemma     = lemma,
            pos       = pos,
            taskType  = taskType,
            renderer  = renderer,
            cefrLevel = cefrLevel,
            prompt    = promptMap,
            solution  = solutionMap,
        )
    }.getOrNull()

    /**
     * Converts a flat-or-nested JSON object to Map<String, String>.
     * Primitives are unwrapped to their string value.
     * Nested objects/arrays are kept as their JSON string representation
     * so callers (renderers) can parse them further if needed.
     * Previously this called jsonPrimitive.content on ALL values, which threw
     * on any nested object/array and silently returned emptyMap() via runCatching.
     */
    private fun parseJsonToMap(raw: String): Map<String, String> = runCatching {
        val obj = json.decodeFromString<JsonObject>(raw)
        obj.mapValues { (_, v) ->
            when (v) {
                is JsonNull      -> ""                 // null → empty string
                is JsonPrimitive -> v.content          // unwrap string/number/bool
                is JsonObject    -> v.toString()       // nested object → JSON string
                is JsonArray     -> v.toString()       // array → JSON string
            }
        }
    }.onFailure { e ->
        Log.e("GetNextTaskUseCase", "parseJsonToMap failed for: ${raw.take(200)}", e)
    }.getOrDefault(emptyMap())
}

package com.germanverbmaster.android.domain.usecase

import com.germanverbmaster.android.data.local.entity.TaskSpecEntity
import com.germanverbmaster.android.data.repository.TaskRepository
import com.germanverbmaster.android.domain.model.PracticeMode
import com.germanverbmaster.android.domain.model.TaskCard
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class GetNextTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend operator fun invoke(
        mode: PracticeMode,
        cefrLevel: String? = null,
        batchSize: Int = 20,
    ): List<TaskCard> {
        val entities = when (mode) {
            PracticeMode.B2_EXAM  -> taskRepository.fetchB2Batch(batchSize)
            PracticeMode.VERBS    -> taskRepository.fetchBatch("V", cefrLevel, batchSize)
            PracticeMode.NOUNS    -> taskRepository.fetchBatch("N", cefrLevel, batchSize)
            PracticeMode.ADJECTIVES -> taskRepository.fetchBatch("Adj", cefrLevel, batchSize)
            PracticeMode.ALL      -> taskRepository.fetchBatch(null, cefrLevel, batchSize)
        }
        return entities.mapNotNull { it.toTaskCard() }
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

    private fun parseJsonToMap(raw: String): Map<String, String> = runCatching {
        val obj = json.decodeFromString<JsonObject>(raw)
        obj.entries.associate { (k, v) -> k to v.jsonPrimitive.content }
    }.getOrDefault(emptyMap())
}

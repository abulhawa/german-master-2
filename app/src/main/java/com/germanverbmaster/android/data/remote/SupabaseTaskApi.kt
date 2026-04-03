package com.germanverbmaster.android.data.remote

import com.germanverbmaster.android.data.local.entity.TaskSpecEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

@Serializable
data class RemoteTaskSpec(
    val id: String,
    @SerialName("lexeme_id") val lexemeId: String,
    val pos: String,
    @SerialName("task_type") val taskType: String,
    val renderer: String,
    val prompt: JsonObject,
    val solution: JsonObject,
    val hints: JsonArray? = null,
    val metadata: JsonObject? = null,
    val revision: Int = 1,
    @SerialName("updated_at") val updatedAt: String = "",
)

class SupabaseTaskApi @Inject constructor(
    private val client: SupabaseClient,
) {
    suspend fun fetchAll(): List<RemoteTaskSpec> =
        client.postgrest["task_specs"]
            .select()
            .decodeList<RemoteTaskSpec>()

    suspend fun fetchUpdatedSince(since: String): List<RemoteTaskSpec> =
        client.postgrest["task_specs"]
            .select { filter { gt("updated_at", since) } }
            .decodeList<RemoteTaskSpec>()

    /** Extract CEFR level from task metadata or linked lexeme metadata */
    fun RemoteTaskSpec.toEntity(cefrLevel: String?): TaskSpecEntity = TaskSpecEntity(
        id = id,
        lexemeId = lexemeId,
        pos = pos,
        taskType = taskType,
        renderer = renderer,
        promptJson = prompt.toString(),
        solutionJson = solution.toString(),
        hintsJson = hints?.toString(),
        metadataJson = metadata?.toString(),
        cefrLevel = cefrLevel ?: metadata?.get("level")?.toString()?.trim('"'),
        revision = revision,
        updatedAt = updatedAt,
    )
}

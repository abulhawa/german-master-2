package com.germanverbmaster.android.data.remote

import android.util.Log
import com.germanverbmaster.android.data.local.entity.TaskSpecEntity
import com.germanverbmaster.android.data.util.PosNormalizer
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
    val collections: List<String> = emptyList(),
    val revision: Int = 1,
    @SerialName("updated_at") val updatedAt: String = "",
)

class SupabaseTaskApi @Inject constructor(
    private val client: SupabaseClient,
) {
    suspend fun fetchAll(): List<RemoteTaskSpec> {
        val pageSize = 1000
        val all = mutableListOf<RemoteTaskSpec>()
        var from = 0
        Log.d("SupabaseTaskApi", "Fetching all task_specs...")
        try {
            while (true) {
                val page = client.postgrest["task_specs"]
                    .select {
                        range(from.toLong(), (from + pageSize - 1).toLong())
                        order("id", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                    }
                    .decodeList<RemoteTaskSpec>()
                all.addAll(page)
                Log.d("SupabaseTaskApi", "Fetched page from=$from, got ${page.size} rows, total=${all.size}")
                if (page.size < pageSize) break
                from += pageSize
            }
        } catch (e: Exception) {
            Log.e("SupabaseTaskApi", "Error fetching task_specs", e)
            throw e
        }
        return all
    }

    suspend fun fetchUpdatedSince(since: String): List<RemoteTaskSpec> {
        val pageSize = 1000
        val all = mutableListOf<RemoteTaskSpec>()
        var from = 0
        while (true) {
            val page = client.postgrest["task_specs"]
                .select {
                    filter { gt("updated_at", since) }
                    range(from.toLong(), (from + pageSize - 1).toLong())
                    order("id", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
                .decodeList<RemoteTaskSpec>()
            all.addAll(page)
            if (page.size < pageSize) break
            from += pageSize
        }
        return all
    }

    /** Extract CEFR level from task metadata or linked lexeme metadata */
    fun RemoteTaskSpec.toEntity(cefrLevel: String?): TaskSpecEntity = TaskSpecEntity(
        id = id.trim(),
        lexemeId = lexemeId.trim(),
        pos = PosNormalizer.normalize(pos),
        taskType = taskType.trim(),
        renderer = renderer.trim(),
        promptJson = prompt.toString(),
        solutionJson = solution.toString(),
        hintsJson = hints?.toString(),
        metadataJson = metadata?.toString(),
        collectionsJson = Json.encodeToString(collections),
        cefrLevel = cefrLevel ?: metadata?.get("level")?.toString()?.trim('"'),
        revision = revision,
        updatedAt = updatedAt.trim(),
    )
}

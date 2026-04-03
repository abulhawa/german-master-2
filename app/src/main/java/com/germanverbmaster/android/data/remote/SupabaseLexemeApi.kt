package com.germanverbmaster.android.data.remote

import com.germanverbmaster.android.data.local.entity.LexemeEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

@Serializable
data class RemoteLexeme(
    val id: String,
    val lemma: String,
    val language: String = "de",
    val pos: String,
    val gender: String? = null,
    val metadata: JsonObject? = null,
    @SerialName("frequency_rank") val frequencyRank: Int? = null,
    @SerialName("source_ids") val sourceIds: List<String> = emptyList(),
    @SerialName("updated_at") val updatedAt: String = "",
)

class SupabaseLexemeApi @Inject constructor(
    private val client: SupabaseClient,
) {
    /** Full fetch on first launch */
    suspend fun fetchAll(): List<RemoteLexeme> =
        client.postgrest["lexemes"]
            .select()
            .decodeList<RemoteLexeme>()

    /** Incremental sync — only rows updated after lastSyncedAt */
    suspend fun fetchUpdatedSince(since: String): List<RemoteLexeme> =
        client.postgrest["lexemes"]
            .select { filter { gt("updated_at", since) } }
            .decodeList<RemoteLexeme>()

    fun RemoteLexeme.toEntity() = LexemeEntity(
        id = id,
        lemma = lemma,
        language = language,
        pos = pos,
        gender = gender,
        metadataJson = metadata?.toString() ?: "{}",
        frequencyRank = frequencyRank,
        sourceIdsJson = sourceIds.toString(),
        updatedAt = updatedAt,
    )
}

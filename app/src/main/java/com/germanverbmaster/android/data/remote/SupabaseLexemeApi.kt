package com.germanverbmaster.android.data.remote

import com.germanverbmaster.android.data.local.entity.LexemeEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive
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

    fun RemoteLexeme.toEntity(): LexemeEntity {
        val metadata = metadata ?: JsonObject(emptyMap())
        
        val isApproved = metadata["approved"]?.jsonPrimitive?.booleanOrNull ?: false
        
        // Completeness per POS:
        // Verb: must have praeteritum, partizip_ii, perfekt
        // Noun: must have gender, plural
        // Adjective: must have comparative, superlative (or 'keine Steigerung')
        val isComplete = when (pos) {
            "V" -> {
                metadata.containsKey("praeteritum") && 
                metadata.containsKey("partizip_ii") && 
                metadata.containsKey("perfekt")
            }
            "N" -> {
                gender != null && metadata.containsKey("plural")
            }
            "Adj" -> {
                (metadata.containsKey("comparative") && metadata.containsKey("superlative")) ||
                metadata["no_comparison"]?.jsonPrimitive?.booleanOrNull == true
            }
            else -> false
        }

        return LexemeEntity(
            id = id,
            lemma = lemma,
            language = language,
            pos = pos,
            gender = gender,
            metadataJson = metadata.toString(),
            frequencyRank = frequencyRank,
            sourceIdsJson = sourceIds.toString(),
            updatedAt = updatedAt,
            isApproved = isApproved,
            isComplete = isComplete
        )
    }
}

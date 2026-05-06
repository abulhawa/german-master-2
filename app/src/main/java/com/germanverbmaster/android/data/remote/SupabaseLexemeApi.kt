package com.germanverbmaster.android.data.remote

import android.util.Log
import com.germanverbmaster.android.data.local.entity.LexemeEntity
import com.germanverbmaster.android.data.util.PosNormalizer
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
    @SerialName("is_approved") val isApproved: Boolean? = null,
    @SerialName("is_complete") val isComplete: Boolean? = null,
    @SerialName("frequency_rank") val frequencyRank: Int? = null,
    @SerialName("source_ids") val sourceIds: List<String> = emptyList(),
    val collections: List<String> = emptyList(),
    @SerialName("updated_at") val updatedAt: String = "",
)

class SupabaseLexemeApi @Inject constructor(
    private val client: SupabaseClient,
) {
    /** Full fetch on first launch — paginates through all rows in batches of 1000 */
    suspend fun fetchAll(): List<RemoteLexeme> {
        val pageSize = 1000
        val all = mutableListOf<RemoteLexeme>()
        var from = 0
        Log.d("SupabaseLexemeApi", "Fetching all lexemes... URL: ${client.supabaseUrl}")
        try {
            while (true) {
                val page = client.postgrest["lexemes"]
                    .select {
                        range(from.toLong(), (from + pageSize - 1).toLong())
                        order("id", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                    }
                    .decodeList<RemoteLexeme>()
                all.addAll(page)
                Log.d("SupabaseLexemeApi", "Fetched page from=$from, got ${page.size} rows, total=${all.size}")
                if (page.size < pageSize) break
                from += pageSize
            }
        } catch (e: PostgrestRestException) {
            Log.e("SupabaseLexemeApi", "Postgrest Error: ${e.error} (Status: ${e.statusCode})", e)
            Log.e("SupabaseLexemeApi", "Postgrest Hint: ${e.hint}")
            throw e
        } catch (e: Exception) {
            Log.e("SupabaseLexemeApi", "General Error fetching lexemes", e)
            throw e
        }
        return all
    }

    /** Incremental sync — only rows updated after lastSyncedAt */
    suspend fun fetchUpdatedSince(since: String): List<RemoteLexeme> {
        val pageSize = 1000
        val all = mutableListOf<RemoteLexeme>()
        var from = 0
        while (true) {
            val page = client.postgrest["lexemes"]
                .select {
                    filter { gt("updated_at", since) }
                    range(from.toLong(), (from + pageSize - 1).toLong())
                    order("id", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
                .decodeList<RemoteLexeme>()
            all.addAll(page)
            if (page.size < pageSize) break
            from += pageSize
        }
        return all
    }

    fun RemoteLexeme.toEntity(): LexemeEntity {
        val metadataMap = metadata ?: JsonObject(emptyMap())
        
        fun getBool(key: String): Boolean? {
            val element = metadataMap[key] ?: return null
            val content = element.jsonPrimitive.content.trim()
            return content.equals("true", ignoreCase = true) || 
                   content == "1" || 
                   element.jsonPrimitive.booleanOrNull == true
        }

        // Check top-level first, then various metadata keys
        val approved = isApproved 
            ?: getBool("approved") 
            ?: getBool("is_approved") 
            ?: true // Default to true so data is visible by default
        
        val posNormalized = PosNormalizer.normalize(pos)
        // Default to true if not specified to avoid hiding data
        val complete = isComplete ?: true

        // Extract CEFR level from metadata — try common key variants
        val cefrLevel = metadataMap["level"]?.jsonPrimitive?.content?.trim()
            ?: metadataMap["cefr_level"]?.jsonPrimitive?.content?.trim()
            ?: metadataMap["cefrLevel"]?.jsonPrimitive?.content?.trim()
            ?: metadataMap["cefr"]?.jsonPrimitive?.content?.trim()

        return LexemeEntity(
            id = id.trim(),
            lemma = lemma.trim(),
            language = language.trim(),
            pos = posNormalized,
            gender = gender?.trim(),
            metadataJson = metadataMap.toString(),
            cefrLevel = cefrLevel,
            frequencyRank = frequencyRank,
            sourceIdsJson = Json.encodeToString(sourceIds),
            collectionsJson = Json.encodeToString(collections),
            updatedAt = updatedAt.trim(),
            isApproved = approved,
            isComplete = complete,
        )
    }
}

package com.germanverbmaster.android.data.remote

import android.util.Log
import com.germanverbmaster.android.data.local.entity.WordEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
data class RemoteWord(
    val id: Int,
    val lemma: String,
    val pos: String,
    val level: String? = null,
    val english: String? = null,
    @SerialName("example_de") val exampleDe: String? = null,
    @SerialName("example_en") val exampleEn: String? = null,
    val gender: String? = null,
    val plural: String? = null,
    val separable: Boolean? = null,
    val aux: String? = null,
    val praeteritum: String? = null,
    @SerialName("partizip_ii") val partizipIi: String? = null,
    val comparative: String? = null,
    val superlative: String? = null,
    val collections: List<String> = emptyList(),
    @SerialName("updated_at") val updatedAt: String = "",
)

class SupabaseWordsApi @Inject constructor(
    private val client: SupabaseClient,
) {
    private companion object {
        const val TAG = "SupabaseWordsApi"
        const val DATASET_VERSION_HEADER = "X-Wortschatz-Dataset-Version"
        const val DEFAULT_BOOTSTRAP_LIMIT = 200L
    }

    suspend fun fetchDatasetVersionHeader(): String? {
        return try {
            val response = client.postgrest["words"].select(columns = Columns.list("id")) {
                head = true
                filter { filterNot("english", FilterOperator.IS, null) }
                limit(1)
            }
            response.headers[DATASET_VERSION_HEADER]
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch dataset version header", e)
            null
        }
    }

    /** Fast-path fetch for first launch so B2 Beruf drill can render early. */
    suspend fun fetchBootstrapB2Beruf(limit: Long = DEFAULT_BOOTSTRAP_LIMIT): List<RemoteWord> {
        return try {
            client.postgrest["words"]
                .select {
                    filter {
                        filterNot("english", FilterOperator.IS, null)
                        contains("collections", listOf("b2_beruf"))
                    }
                    range(0, (limit - 1).coerceAtLeast(0))
                    order("id", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
                .decodeList<RemoteWord>()
        } catch (e: Exception) {
            Log.w(TAG, "Bootstrap fetch for b2_beruf failed; continuing with full sync", e)
            emptyList()
        }
    }

    /** Paginated full fetch of the words table */
    suspend fun fetchAll(): List<RemoteWord> {
        val pageSize = 1000
        val all = mutableListOf<RemoteWord>()
        var from = 0
        Log.d(TAG, "Fetching all words... URL: ${client.supabaseUrl}")
        try {
            while (true) {
                val page = client.postgrest["words"]
                    .select {
                        filter { filterNot("english", FilterOperator.IS, null) }
                        range(from.toLong(), (from + pageSize - 1).toLong())
                        order("id", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                    }
                    .decodeList<RemoteWord>()
                all.addAll(page)
                Log.d(TAG, "Page from=$from got ${page.size}, total=${all.size}")
                if (page.size < pageSize) break
                from += pageSize
            }
        } catch (e: PostgrestRestException) {
            Log.e(TAG, "Postgrest Error: ${e.error} (Status: ${e.statusCode})", e)
            Log.e(TAG, "Postgrest Hint: ${e.hint}")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "General Error fetching words", e)
            throw e
        }
        return all
    }

    /** Incremental sync — only rows updated after since */
    suspend fun fetchUpdatedSince(since: String): List<RemoteWord> {
        val pageSize = 1000
        val all = mutableListOf<RemoteWord>()
        var from = 0
        while (true) {
            val page = client.postgrest["words"]
                .select {
                    filter {
                        filterNot("english", FilterOperator.IS, null)
                        gt("updated_at", since)
                    }
                    range(from.toLong(), (from + pageSize - 1).toLong())
                    order("id", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
                .decodeList<RemoteWord>()
            all.addAll(page)
            if (page.size < pageSize) break
            from += pageSize
        }
        return all
    }

    fun RemoteWord.toEntity() = WordEntity(
        id = id,
        lemma = lemma.trim(),
        pos = pos.trim(),
        level = level?.trim(),
        english = english?.trim(),
        exampleDe = exampleDe?.trim(),
        exampleEn = exampleEn?.trim(),
        gender = gender?.trim(),
        plural = plural?.trim(),
        separable = separable,
        aux = aux?.trim(),
        praeteritum = praeteritum?.trim(),
        partizip2 = partizipIi?.trim(),
        comparative = comparative?.trim(),
        superlative = superlative?.trim(),
        collectionsJson = Json.encodeToString(collections),
        updatedAt = updatedAt.trim(),
    )
}

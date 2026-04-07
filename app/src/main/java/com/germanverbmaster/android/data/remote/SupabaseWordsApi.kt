package com.germanverbmaster.android.data.remote

import android.util.Log
import com.germanverbmaster.android.data.local.entity.WordEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
    @SerialName("updated_at") val updatedAt: String = "",
)

class SupabaseWordsApi @Inject constructor(
    private val client: SupabaseClient,
) {
    /** Paginated full fetch of the words table */
    suspend fun fetchAll(): List<RemoteWord> {
        val pageSize = 1000
        val all = mutableListOf<RemoteWord>()
        var from = 0
        Log.d("SupabaseWordsApi", "Fetching all words...")
        while (true) {
            val page = client.postgrest["words"]
                .select {
                    filter { filterNot("english", FilterOperator.IS, null) }
                    range(from.toLong(), (from + pageSize - 1).toLong())
                    order("id", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
                .decodeList<RemoteWord>()
            all.addAll(page)
            Log.d("SupabaseWordsApi", "Page from=$from got ${page.size}, total=${all.size}")
            if (page.size < pageSize) break
            from += pageSize
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
        updatedAt = updatedAt.trim(),
    )
}

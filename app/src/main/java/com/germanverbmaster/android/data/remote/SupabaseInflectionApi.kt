package com.germanverbmaster.android.data.remote

import android.util.Log
import com.germanverbmaster.android.data.local.entity.InflectionEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

@Serializable
data class RemoteInflection(
    val id: String,
    @SerialName("lexeme_id") val lexemeId: String,
    val form: String,
    val features: JsonObject,
    @SerialName("audio_asset") val audioAsset: String? = null,
)

class SupabaseInflectionApi @Inject constructor(
    private val client: SupabaseClient,
) {
    suspend fun fetchAll(): List<RemoteInflection> {
        val pageSize = 1000
        val all = mutableListOf<RemoteInflection>()
        var from = 0
        Log.d("SupabaseInflectionApi", "Fetching all inflections...")
        try {
            while (true) {
                val page = client.postgrest["inflections"]
                    .select {
                        range(from.toLong(), (from + pageSize - 1).toLong())
                        order("id", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                    }
                    .decodeList<RemoteInflection>()
                all.addAll(page)
                Log.d("SupabaseInflectionApi", "Fetched page from=$from, got ${page.size} rows, total=${all.size}")
                if (page.size < pageSize) break
                from += pageSize
            }
        } catch (e: io.github.jan.supabase.postgrest.exception.PostgrestRestException) {
            Log.e("SupabaseInflectionApi", "Postgrest Error: ${e.error} (Status: ${e.statusCode})", e)
            Log.e("SupabaseInflectionApi", "Postgrest Hint: ${e.hint}")
            throw e
        } catch (e: Exception) {
            Log.e("SupabaseInflectionApi", "General Error fetching inflections", e)
            throw e
        }
        return all
    }

    fun RemoteInflection.toEntity() = InflectionEntity(
        id = id.trim(),
        lexemeId = lexemeId.trim(),
        form = form.trim(),
        featuresJson = features.toString(),
        audioAsset = audioAsset?.trim(),
    )
}

package com.germanverbmaster.android.data.remote

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
    suspend fun fetchAll(): List<RemoteInflection> =
        client.postgrest["inflections"]
            .select()
            .decodeList<RemoteInflection>()

    fun RemoteInflection.toEntity() = InflectionEntity(
        id = id,
        lexemeId = lexemeId,
        form = form,
        featuresJson = features.toString(),
        audioAsset = audioAsset,
    )
}

package com.germanverbmaster.android.data.remote

import android.util.Log
import com.germanverbmaster.android.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject

@Serializable
data class RemoteHistory(
    @SerialName("id") val remoteId: Long? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("task_id") val taskId: String,
    @SerialName("lexeme_id") val lexemeId: String,
    val lemma: String,
    val pos: String,
    @SerialName("task_type") val taskType: String,
    val renderer: String,
    @SerialName("device_id") val deviceId: String,
    val result: String,
    @SerialName("submitted_answer") val submittedAnswer: String,
    @SerialName("correct_answer") val correctAnswer: String,
    @SerialName("response_ms") val responseMs: Int,
    @SerialName("cefr_level") val cefrLevel: String? = null,
    @SerialName("hints_used") val hintsUsed: Boolean,
    @SerialName("submitted_at") val submittedAt: String,
)

internal val PRACTICE_HISTORY_SELECT_COLUMNS = Columns.list(
    "id",
    "user_id",
    "task_id",
    "lexeme_id",
    "lemma",
    "pos",
    "task_type",
    "renderer",
    "device_id",
    "result",
    "submitted_answer",
    "correct_answer",
    "response_ms",
    "cefr_level",
    "hints_used",
    "submitted_at",
)

private val remoteHistoryJson = Json {
    encodeDefaults = false
    explicitNulls = false
}

internal fun serializeRemoteHistory(entries: List<RemoteHistory>): JsonArray =
    remoteHistoryJson.encodeToJsonElement(entries).jsonArray

class SupabaseHistoryApi @Inject constructor(
    private val client: SupabaseClient,
) {
    private companion object {
        const val TAG = "SupabaseHistoryApi"
    }

    suspend fun upsert(entries: List<RemoteHistory>) {
        if (entries.isEmpty()) return
        val payload = serializeRemoteHistory(entries)
        Log.d(TAG, "Upserting ${entries.size} history records to user_practice_history")
        if (BuildConfig.DEBUG) {
            val columns = payload.flatMap { it.jsonObject.keys }.distinct().sorted()
            Log.d(TAG, "Outgoing user_practice_history columns=$columns")
        }
        try {
            client.postgrest["user_practice_history"].upsert(payload)
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting history", e)
            throw e
        }
    }

    suspend fun fetchUpdatedSince(since: String, userId: String): List<RemoteHistory> {
        val pageSize = 1000
        val all = mutableListOf<RemoteHistory>()
        var from = 0
        Log.d(TAG, "Fetching history since $since for user $userId from user_practice_history")
        try {
            while (true) {
                val page = client.postgrest["user_practice_history"]
                    .select(columns = PRACTICE_HISTORY_SELECT_COLUMNS) {
                        filter { 
                            eq("user_id", userId)
                            gt("submitted_at", since)
                        }
                        range(from.toLong(), (from + pageSize - 1).toLong())
                        order("submitted_at", Order.ASCENDING)
                    }
                    .decodeList<RemoteHistory>()
                all.addAll(page)
                Log.d(TAG, "Fetched ${page.size} history records, total ${all.size}")
                if (page.size < pageSize) break
                from += pageSize
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching history", e)
            throw e
        }
        return all
    }
}

package com.germanverbmaster.android.data.remote

import android.util.Log
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
data class RemoteHistory(
    @SerialName("id") val remoteId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("task_id") val taskId: String,
    @SerialName("lexeme_id") val lexemeId: String,
    val lemma: String,
    val pos: String,
    @SerialName("task_type") val taskType: String,
    val renderer: String,
    val result: String,
    @SerialName("submitted_answer") val submittedAnswer: String,
    @SerialName("correct_answer") val correctAnswer: String,
    @SerialName("response_ms") val responseMs: Int,
    @SerialName("cefr_level") val cefrLevel: String? = null,
    @SerialName("hints_used") val hintsUsed: Boolean,
    @SerialName("submitted_at") val submittedAt: String,
)

class SupabaseHistoryApi @Inject constructor(
    private val client: SupabaseClient,
) {
    suspend fun upsert(entries: List<RemoteHistory>) {
        if (entries.isEmpty()) return
        Log.d("SupabaseHistoryApi", "Upserting ${entries.size} history records")
        try {
            client.postgrest["practice_history"].upsert(entries)
        } catch (e: Exception) {
            Log.e("SupabaseHistoryApi", "Error upserting history", e)
            throw e
        }
    }

    suspend fun fetchUpdatedSince(since: String, userId: String): List<RemoteHistory> {
        val pageSize = 1000
        val all = mutableListOf<RemoteHistory>()
        var from = 0
        Log.d("SupabaseHistoryApi", "Fetching history since $since for user $userId")
        try {
            while (true) {
                val page = client.postgrest["practice_history"]
                    .select {
                        filter { 
                            eq("user_id", userId)
                            gt("submitted_at", since)
                        }
                        range(from.toLong(), (from + pageSize - 1).toLong())
                        order("submitted_at", Order.ASCENDING)
                    }
                    .decodeList<RemoteHistory>()
                all.addAll(page)
                Log.d("SupabaseHistoryApi", "Fetched ${page.size} history records, total ${all.size}")
                if (page.size < pageSize) break
                from += pageSize
            }
        } catch (e: Exception) {
            Log.e("SupabaseHistoryApi", "Error fetching history", e)
            throw e
        }
        return all
    }

    fun RemoteHistory.toEntity(): PracticeHistoryEntity = PracticeHistoryEntity(
        remoteId = remoteId,
        userId = userId,
        taskId = taskId,
        lexemeId = lexemeId,
        lemma = lemma,
        pos = pos,
        taskType = taskType,
        renderer = renderer,
        result = result,
        submittedAnswer = submittedAnswer,
        correctAnswer = correctAnswer,
        responseMs = responseMs,
        cefrLevel = cefrLevel,
        hintsUsed = hintsUsed,
        submittedAt = submittedAt,
        synced = true
    )

    fun PracticeHistoryEntity.toRemote(userId: String): RemoteHistory = RemoteHistory(
        remoteId = remoteId,
        userId = userId,
        taskId = taskId,
        lexemeId = lexemeId,
        lemma = lemma,
        pos = pos,
        taskType = taskType,
        renderer = renderer,
        result = result,
        submittedAnswer = submittedAnswer,
        correctAnswer = correctAnswer,
        responseMs = responseMs,
        cefrLevel = cefrLevel,
        hintsUsed = hintsUsed,
        submittedAt = submittedAt
    )
}

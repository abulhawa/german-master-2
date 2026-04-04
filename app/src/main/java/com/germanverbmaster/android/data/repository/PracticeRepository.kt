package com.germanverbmaster.android.data.repository

import com.germanverbmaster.android.data.local.dao.DailyAccuracy
import com.germanverbmaster.android.data.local.dao.PracticeHistoryDao
import com.germanverbmaster.android.data.local.dao.TaskTypeStat
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class RemotePracticeHistory(
    val task_id: String,
    val lexeme_id: String,
    val pos: String,
    val task_type: String,
    val result: String,
    val response_ms: Int,
    val cefr_level: String?,
    val hints_used: Boolean,
    val submitted_at: String,
    val device_id: String = "android",
)

@Singleton
class PracticeRepository @Inject constructor(
    private val dao: PracticeHistoryDao,
    private val client: SupabaseClient,
) {
    fun observeRecent(limit: Int = 100): Flow<List<PracticeHistoryEntity>> =
        dao.observeRecent(limit)

    suspend fun record(entry: PracticeHistoryEntity): Long =
        dao.insert(entry)

    /** Push unsynced rows to Supabase. Called by WorkManager periodically. */
    suspend fun flushToSupabase() {
        val pending = dao.unsynced()
        if (pending.isEmpty()) return
        try {
            val remote = pending.map {
                RemotePracticeHistory(
                    task_id = it.taskId,
                    lexeme_id = it.lexemeId,
                    pos = it.pos,
                    task_type = it.taskType,
                    result = it.result,
                    response_ms = it.responseMs,
                    cefr_level = it.cefrLevel,
                    hints_used = it.hintsUsed,
                    submitted_at = it.submittedAt,
                )
            }
            client.postgrest["practice_history"].insert(remote)
            dao.markSynced(pending.map { it.localId })
        } catch (_: Exception) {
            // Will retry on next WorkManager run
        }
    }

    suspend fun accuracyToday(): Pair<Float, Int> {
        val since = Instant.now().minusSeconds(86400).toString()
        val result = dao.statsSince(since)
        return Pair(result?.accuracy ?: 0f, result?.total ?: 0)
    }

    fun observeTaskTypeStats(): Flow<List<TaskTypeStat>> = dao.observeTaskTypeStats()

    suspend fun getDailyAccuracy(since: String): List<DailyAccuracy> = dao.getDailyAccuracy(since)
}

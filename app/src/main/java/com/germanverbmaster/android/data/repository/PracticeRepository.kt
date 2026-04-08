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
    val id: Int? = null,
    val task_id: String,
    val lexeme_id: String,
    val pos: String,
    val task_type: String,
    val renderer: String,
    val result: String,
    val response_ms: Int,
    val cefr_level: String?,
    val hints_used: Boolean,
    val submitted_at: String,
    val device_id: String = "android",
    val user_id: String? = null,
)

@Singleton
class PracticeRepository @Inject constructor(
    private val dao: PracticeHistoryDao,
    private val client: SupabaseClient,
    private val prefs: SyncPreferences,
    private val authRepository: AuthRepository,
) {
    fun observeRecent(limit: Int = 100): Flow<List<PracticeHistoryEntity>> =
        dao.observeRecent(limit)

    suspend fun record(entry: PracticeHistoryEntity): Long =
        dao.insert(entry)

    /** Pull history from Supabase. */
    suspend fun fetchFromSupabase() {
        try {
            val authenticatedUserId = authRepository.currentUserId
            val anonUserId = prefs.getUserId()
            val since = prefs.getPracticeLastSync()

            val remote = client.postgrest["practice_history"]
                .select {
                    filter {
                        if (authenticatedUserId != null) {
                            // If logged in, prioritize the actual user ID
                            eq("user_id", authenticatedUserId)
                        } else {
                            // Otherwise, fallback to the anonymous device ID
                            eq("device_id", anonUserId)
                        }
                        
                        if (since != null) {
                            gt("submitted_at", since)
                        }
                    }
                }
                .decodeList<RemotePracticeHistory>()
            
            if (remote.isNotEmpty()) {
                val entities = remote.map {
                    PracticeHistoryEntity(
                        taskId = it.task_id,
                        lexemeId = it.lexeme_id,
                        pos = it.pos,
                        taskType = it.task_type,
                        renderer = it.renderer,
                        result = it.result,
                        responseMs = it.response_ms,
                        cefrLevel = it.cefr_level,
                        hintsUsed = it.hints_used,
                        submittedAt = it.submitted_at,
                        synced = true
                    )
                }
                dao.insertIgnore(entities)
                
                // Update last sync time
                val latest = remote.maxOf { it.submitted_at }
                prefs.setPracticeLastSync(latest)
            }
        } catch (e: Exception) {
            android.util.Log.e("PracticeRepository", "Failed to fetch history", e)
        }
    }

    /** Push unsynced rows to Supabase. Called by WorkManager periodically. */
    suspend fun flushToSupabase() {
        val pending = dao.unsynced()
        if (pending.isEmpty()) return
        try {
            val authenticatedUserId = authRepository.currentUserId
            val anonUserId = prefs.getUserId()
            
            val remote = pending.map {
                RemotePracticeHistory(
                    task_id = it.taskId,
                    lexeme_id = it.lexemeId,
                    pos = it.pos,
                    task_type = it.taskType,
                    renderer = it.renderer,
                    result = it.result,
                    response_ms = it.responseMs,
                    cefr_level = it.cefrLevel,
                    hints_used = it.hintsUsed,
                    submitted_at = it.submittedAt,
                    device_id = anonUserId,
                    user_id = authenticatedUserId // This links the data to their Google account
                )
            }
            client.postgrest["practice_history"].insert(remote)
            dao.markSynced(pending.map { it.localId })
        } catch (e: Exception) {
            android.util.Log.e("PracticeRepository", "Failed to flush history", e)
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

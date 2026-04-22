package com.germanverbmaster.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeHistoryDao {

    @Insert
    suspend fun insert(entry: PracticeHistoryEntity): Long

    @Insert(onConflict = androidx.room.OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(entries: List<PracticeHistoryEntity>)

    @Query("SELECT COUNT(*) FROM practice_history")
    suspend fun count(): Int

    @Update
    suspend fun update(entry: PracticeHistoryEntity)

    @Query("SELECT * FROM practice_history ORDER BY submittedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 100): Flow<List<PracticeHistoryEntity>>

    @Query("SELECT * FROM practice_history WHERE synced = 0 LIMIT 50")
    suspend fun unsynced(): List<PracticeHistoryEntity>

    @Query("UPDATE practice_history SET synced = 1 WHERE localId IN (:ids)")
    suspend fun markSynced(ids: List<Int>)

    @Query(
        """
        SELECT
          SUM(CASE WHEN result = 'correct' THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as accuracy,
          COUNT(*) as total
        FROM practice_history
        WHERE submittedAt >= :since
        """,
    )
    suspend fun statsSince(since: String): AccuracyResult?

    @Query(
        """
        SELECT taskType, 
               SUM(CASE WHEN result = 'correct' THEN 1 ELSE 0 END) as correctCount,
               COUNT(*) as totalCount
        FROM practice_history
        GROUP BY taskType
        """,
    )
    fun observeTaskTypeStats(): Flow<List<TaskTypeStat>>

    @Query(
        """
        SELECT DATE(submittedAt) as date,
               SUM(CASE WHEN result = 'correct' THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as accuracy
        FROM practice_history
        WHERE submittedAt >= :since
        GROUP BY DATE(submittedAt)
        ORDER BY date ASC
        """,
    )
    suspend fun getDailyAccuracy(since: String): List<DailyAccuracy>

    @Query("SELECT DISTINCT taskId FROM practice_history WHERE result = 'correct'")
    fun observeCorrectTaskIds(): Flow<List<String>>
}

data class AccuracyResult(val accuracy: Float, val total: Int)

data class TaskTypeStat(
    val taskType: String,
    val correctCount: Int,
    val totalCount: Int,
)

data class DailyAccuracy(
    val date: String,
    val accuracy: Float,
)

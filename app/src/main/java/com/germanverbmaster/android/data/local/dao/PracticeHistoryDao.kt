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

    @Update
    suspend fun update(entry: PracticeHistoryEntity)

    @Query("SELECT * FROM practice_history ORDER BY submittedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 100): Flow<List<PracticeHistoryEntity>>

    @Query("SELECT * FROM practice_history WHERE synced = 0 LIMIT 50")
    suspend fun unsynced(): List<PracticeHistoryEntity>

    @Query("UPDATE practice_history SET synced = 1 WHERE localId IN (:ids)")
    suspend fun markSynced(ids: List<Int>)

    @Query("""
        SELECT
          SUM(CASE WHEN result = 'correct' THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as accuracy,
          COUNT(*) as total
        FROM practice_history
        WHERE submittedAt >= :since
    """)
    suspend fun accuracySince(since: String): AccuracyResult?
}

data class AccuracyResult(val accuracy: Float, val total: Int)

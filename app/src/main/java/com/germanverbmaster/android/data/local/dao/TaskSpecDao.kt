package com.germanverbmaster.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.germanverbmaster.android.data.local.entity.TaskSpecEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskSpecDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tasks: List<TaskSpecEntity>)

    // Word acceptance rule: only show approved+complete tasks (cefrLevel not null = complete)
    @Query("""
        SELECT * FROM task_specs
        WHERE (:pos IS NULL OR pos = :pos)
          AND (:cefrLevel IS NULL OR cefrLevel = :cefrLevel)
          AND cefrLevel IS NOT NULL
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun fetchBatch(
        pos: String? = null,
        cefrLevel: String? = null,
        limit: Int = 20,
    ): List<TaskSpecEntity>

    // B2 exam mode: B1+B2 across all task types
    @Query("""
        SELECT * FROM task_specs
        WHERE cefrLevel IN ('B1','B2')
          AND cefrLevel IS NOT NULL
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun fetchB2Batch(limit: Int = 20): List<TaskSpecEntity>

    @Query("SELECT COUNT(*) FROM task_specs")
    suspend fun count(): Int

    @Query("SELECT MAX(updatedAt) FROM task_specs")
    suspend fun latestUpdatedAt(): String?
}

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

    // Word acceptance rule: only show approved+complete tasks
    @Query("""
        SELECT ts.* FROM task_specs ts
        INNER JOIN lexemes l ON ts.lexemeId = l.id
        WHERE (:pos IS NULL OR ts.pos = :pos)
          AND (:cefrLevel IS NULL OR ts.cefrLevel = :cefrLevel)
          AND l.isApproved = 1
          AND l.isComplete = 1
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun fetchBatch(
        pos: String? = null,
        cefrLevel: String? = null,
        limit: Int = 20,
    ): List<TaskSpecEntity>

    // B2 exam mode: B1+B2 across all task types, must be approved and complete
    @Query("""
        SELECT ts.* FROM task_specs ts
        INNER JOIN lexemes l ON ts.lexemeId = l.id
        WHERE ts.cefrLevel IN ('B1','B2')
          AND l.isApproved = 1
          AND l.isComplete = 1
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun fetchB2Batch(limit: Int = 20): List<TaskSpecEntity>

    @Query("SELECT COUNT(*) FROM task_specs")
    suspend fun count(): Int

    @Query("SELECT MAX(updatedAt) FROM task_specs")
    suspend fun latestUpdatedAt(): String?
}

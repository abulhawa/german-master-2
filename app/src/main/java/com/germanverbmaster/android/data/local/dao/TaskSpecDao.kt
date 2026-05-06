package com.germanverbmaster.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.germanverbmaster.android.data.local.entity.TaskSpecEntity

@Dao
interface TaskSpecDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tasks: List<TaskSpecEntity>)

    // Word acceptance rule: only show approved+complete tasks
    @Query("""
        SELECT ts.* FROM task_specs ts
        INNER JOIN lexemes l ON ts.lexemeId = l.id
        WHERE (:allPos = 1 OR UPPER(TRIM(ts.pos)) IN (:posVariants) OR UPPER(TRIM(l.pos)) IN (:posVariants))
          AND (:cefrLevel IS NULL OR ts.cefrLevel = :cefrLevel)
          AND (:collection IS NULL OR ts.collectionsJson LIKE '%"' || :collection || '"%')
          AND ts.taskType IN ('conjugate_form', 'noun_case_declension', 'adj_ending')
          AND l.isApproved = 1
          AND l.isComplete = 1
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun fetchBatch(
        posVariants: List<String>,
        allPos: Boolean,
        cefrLevel: String? = null,
        collection: String? = null,
        limit: Int = 20,
    ): List<TaskSpecEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM task_specs WHERE id = :id)")
    suspend fun exists(id: String): Boolean

    @Query(
        """
        SELECT id FROM task_specs
        WHERE lexemeId = :lexemeId
        ORDER BY
          CASE
            WHEN taskType = 'vocabulary_drill' THEN 0
            WHEN UPPER(TRIM(:pos)) IN ('V', 'VERB') AND taskType = 'conjugate_form' THEN 0
            WHEN UPPER(TRIM(:pos)) IN ('N', 'NOMEN') AND taskType = 'noun_case_declension' THEN 0
            WHEN UPPER(TRIM(:pos)) IN ('ADJ', 'ADJEKTIV') AND taskType = 'adj_ending' THEN 0
            ELSE 1
          END,
          CASE taskType
            WHEN 'vocabulary_drill' THEN 0
            WHEN 'conjugate_form' THEN 1
            WHEN 'noun_case_declension' THEN 2
            WHEN 'adj_ending' THEN 3
            ELSE 4
          END,
          id ASC
        LIMIT 1
        """,
    )
    suspend fun findHistoryAnchorTaskId(lexemeId: String, pos: String): String?


    @Query("SELECT COUNT(*) FROM task_specs")
    suspend fun count(): Int

    @Query("SELECT MAX(updatedAt) FROM task_specs")
    suspend fun latestUpdatedAt(): String?

    @Query("DELETE FROM task_specs")
    suspend fun deleteAll()
}

package com.germanverbmaster.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.germanverbmaster.android.data.local.entity.LexemeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LexemeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(lexemes: List<LexemeEntity>)

    @Query("SELECT * FROM lexemes WHERE pos = :pos")
    fun observeByPos(pos: String): Flow<List<LexemeEntity>>

    @Query("SELECT * FROM lexemes WHERE id = :id")
    suspend fun getById(id: String): LexemeEntity?

    @Query("SELECT * FROM lexemes WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<LexemeEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM lexemes WHERE id = :id)")
    suspend fun exists(id: String): Boolean

    @Query(
        """
        SELECT id FROM lexemes
        WHERE LOWER(TRIM(lemma)) = LOWER(TRIM(:lemma))
          AND (
            LOWER(TRIM(pos)) = LOWER(TRIM(:pos))
            OR pos LIKE :pos || '%'
            OR :pos LIKE pos || '%'
          )
        ORDER BY
          CASE WHEN LOWER(TRIM(pos)) = LOWER(TRIM(:pos)) THEN 0 ELSE 1 END,
          id ASC
        LIMIT 1
        """,
    )
    suspend fun findIdByLemmaAndPos(lemma: String, pos: String): String?

    @Query("DELETE FROM lexemes")
    suspend fun deleteAll()

    @Query("SELECT id FROM lexemes")
    suspend fun getAllIds(): List<String>

    @Query("SELECT COUNT(*) FROM lexemes")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM lexemes WHERE isApproved = 1 AND isComplete = 1")
    suspend fun countApprovedAndComplete(): Int

    @Query("SELECT COUNT(*) FROM lexemes WHERE isApproved = 1")
    suspend fun countApproved(): Int

    @Query("SELECT COUNT(*) FROM lexemes WHERE isComplete = 1")
    suspend fun countComplete(): Int

    @Query("SELECT MAX(updatedAt) FROM lexemes")
    suspend fun latestUpdatedAt(): String?

    @Query("SELECT id, cefrLevel FROM lexemes")
    suspend fun getAllLevels(): List<LexemeIdLevel>
}

data class LexemeIdLevel(
    val id: String,
    val cefrLevel: String?
)

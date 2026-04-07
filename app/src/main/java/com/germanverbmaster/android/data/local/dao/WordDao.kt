package com.germanverbmaster.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.germanverbmaster.android.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(words: List<WordEntity>)

    /** All words that have a translation — used for Wortschatz drill */
    @Query("SELECT * FROM words WHERE english IS NOT NULL ORDER BY lemma ASC")
    fun observeAll(): Flow<List<WordEntity>>

    /** Filtered by CEFR level */
    @Query("SELECT * FROM words WHERE english IS NOT NULL AND level = :level ORDER BY lemma ASC")
    fun observeByLevel(level: String): Flow<List<WordEntity>>

    /** Filtered by POS */
    @Query("SELECT * FROM words WHERE english IS NOT NULL AND pos = :pos ORDER BY lemma ASC")
    fun observeByPos(pos: String): Flow<List<WordEntity>>

    /** Filtered by level + pos */
    @Query("SELECT * FROM words WHERE english IS NOT NULL AND level = :level AND pos = :pos ORDER BY lemma ASC")
    fun observeByLevelAndPos(level: String, pos: String): Flow<List<WordEntity>>

    @Query("SELECT DISTINCT pos FROM words WHERE english IS NOT NULL ORDER BY pos ASC")
    fun observeDistinctPos(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM words")
    suspend fun count(): Int

    @Query("SELECT MAX(updatedAt) FROM words")
    suspend fun latestUpdatedAt(): String?

    @Query("DELETE FROM words")
    suspend fun deleteAll()
}

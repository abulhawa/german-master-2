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
    suspend fun upsert(word: WordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(words: List<WordEntity>)

    /** All words that have a translation — used for Wortschatz drill */
    @Query("""
        SELECT * FROM words
        WHERE english IS NOT NULL
        AND (:collection IS NULL OR collectionsJson LIKE '%"' || :collection || '"%')
        ORDER BY lemma ASC
    """)
    fun observeAll(collection: String? = null): Flow<List<WordEntity>>

    /** Filtered by CEFR levels */
    @Query("""
        SELECT * FROM words 
        WHERE english IS NOT NULL 
        AND level IN (:levels) 
        AND (:collection IS NULL OR collectionsJson LIKE '%"' || :collection || '"%')
        ORDER BY lemma ASC
    """)
    fun observeByLevels(levels: List<String>, collection: String? = null): Flow<List<WordEntity>>

    /** Filtered by POS types */
    @Query("""
        SELECT * FROM words 
        WHERE english IS NOT NULL 
        AND pos IN (:posTypes) 
        AND (:collection IS NULL OR collectionsJson LIKE '%"' || :collection || '"%')
        ORDER BY lemma ASC
    """)
    fun observeByPosTypes(posTypes: List<String>, collection: String? = null): Flow<List<WordEntity>>

    /** Filtered by levels + POS types */
    @Query("""
        SELECT * FROM words 
        WHERE english IS NOT NULL 
        AND level IN (:levels) 
        AND pos IN (:posTypes) 
        AND (:collection IS NULL OR collectionsJson LIKE '%"' || :collection || '"%')
        ORDER BY lemma ASC
    """)
    fun observeByLevelsAndPos(levels: List<String>, posTypes: List<String>, collection: String? = null): Flow<List<WordEntity>>

    @Query("SELECT DISTINCT pos FROM words WHERE english IS NOT NULL ORDER BY pos ASC")
    fun observeDistinctPos(): Flow<List<String>>

    @Query("SELECT * FROM words WHERE id = :id")
    fun observeById(id: Int): Flow<WordEntity?>

    /**
     * Finds a word ID by lemma and POS. 
     * Uses case-insensitive matching and handles potential POS mismatches (e.g. 'V' vs 'Verb').
     */
    @Query("""
        SELECT id FROM words 
        WHERE LOWER(TRIM(lemma)) = LOWER(TRIM(:lemma)) 
        AND (
            LOWER(TRIM(pos)) = LOWER(TRIM(:pos)) 
            OR pos LIKE :pos || '%' 
            OR :pos LIKE pos || '%'
        )
        LIMIT 1
    """)
    suspend fun findIdByLemmaAndPos(lemma: String, pos: String): Int?

    @Query("""
        SELECT english FROM words 
        WHERE LOWER(TRIM(lemma)) = LOWER(TRIM(:lemma)) 
        AND (
            LOWER(TRIM(pos)) = LOWER(TRIM(:pos)) 
            OR pos LIKE :pos || '%' 
            OR :pos LIKE pos || '%'
        )
        LIMIT 1
    """)
    suspend fun findTranslationByLemmaAndPos(lemma: String, pos: String): String?

    /** Fallback search by lemma only */
    @Query("SELECT id FROM words WHERE LOWER(TRIM(lemma)) = LOWER(TRIM(:lemma)) LIMIT 1")
    suspend fun findIdByLemma(lemma: String): Int?

    @Query("SELECT COUNT(*) FROM words")
    suspend fun count(): Int

    @Query("""
        SELECT COUNT(*) FROM words
        WHERE english IS NOT NULL
        AND (level IS NULL OR level != :excludedLevel)
    """)
    suspend fun countByLevelExcluding(excludedLevel: String): Int

    @Query("SELECT MAX(updatedAt) FROM words")
    suspend fun latestUpdatedAt(): String?

    @Query("DELETE FROM words")
    suspend fun deleteAll()
}

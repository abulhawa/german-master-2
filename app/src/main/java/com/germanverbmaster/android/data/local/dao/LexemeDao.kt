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

    @Query("SELECT COUNT(*) FROM lexemes")
    suspend fun count(): Int

    @Query("SELECT MAX(updatedAt) FROM lexemes")
    suspend fun latestUpdatedAt(): String?
}

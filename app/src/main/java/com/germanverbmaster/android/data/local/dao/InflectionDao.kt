package com.germanverbmaster.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.germanverbmaster.android.data.local.entity.InflectionEntity

@Dao
interface InflectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(inflections: List<InflectionEntity>)

    @Query("SELECT * FROM inflections WHERE lexemeId = :lexemeId")
    suspend fun getByLexemeId(lexemeId: String): List<InflectionEntity>

    @Query("SELECT COUNT(*) FROM inflections")
    suspend fun count(): Int

    @Query("DELETE FROM inflections")
    suspend fun deleteAll()
}

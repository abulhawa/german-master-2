package com.germanverbmaster.android.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.germanverbmaster.android.data.local.dao.InflectionDao
import com.germanverbmaster.android.data.local.dao.LexemeDao
import com.germanverbmaster.android.data.local.dao.PracticeHistoryDao
import com.germanverbmaster.android.data.local.dao.TaskSpecDao
import com.germanverbmaster.android.data.local.entity.InflectionEntity
import com.germanverbmaster.android.data.local.entity.LexemeEntity
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.local.entity.TaskSpecEntity

@Database(
    entities = [
        LexemeEntity::class,
        InflectionEntity::class,
        TaskSpecEntity::class,
        PracticeHistoryEntity::class,
    ],
    version = 4,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lexemeDao(): LexemeDao
    abstract fun taskSpecDao(): TaskSpecDao
    abstract fun practiceHistoryDao(): PracticeHistoryDao
    abstract fun inflectionDao(): InflectionDao
}

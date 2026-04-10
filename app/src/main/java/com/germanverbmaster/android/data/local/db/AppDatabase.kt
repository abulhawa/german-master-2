package com.germanverbmaster.android.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.germanverbmaster.android.data.local.dao.InflectionDao
import com.germanverbmaster.android.data.local.dao.LexemeDao
import com.germanverbmaster.android.data.local.dao.PracticeHistoryDao
import com.germanverbmaster.android.data.local.dao.TaskSpecDao
import com.germanverbmaster.android.data.local.dao.WordDao
import com.germanverbmaster.android.data.local.entity.InflectionEntity
import com.germanverbmaster.android.data.local.entity.LexemeEntity
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.local.entity.TaskSpecEntity
import com.germanverbmaster.android.data.local.entity.WordEntity

@Database(
    entities = [
        LexemeEntity::class,
        InflectionEntity::class,
        TaskSpecEntity::class,
        PracticeHistoryEntity::class,
        WordEntity::class,
    ],
    version = 10,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lexemeDao(): LexemeDao
    abstract fun taskSpecDao(): TaskSpecDao
    abstract fun practiceHistoryDao(): PracticeHistoryDao
    abstract fun inflectionDao(): InflectionDao
    abstract fun wordDao(): WordDao
}

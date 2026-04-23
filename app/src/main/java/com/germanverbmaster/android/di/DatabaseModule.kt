package com.germanverbmaster.android.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.germanverbmaster.android.data.local.dao.InflectionDao
import com.germanverbmaster.android.data.local.dao.LexemeDao
import com.germanverbmaster.android.data.local.dao.PracticeHistoryDao
import com.germanverbmaster.android.data.local.dao.TaskSpecDao
import com.germanverbmaster.android.data.local.dao.WordDao
import com.germanverbmaster.android.data.local.db.AppDatabase
import com.germanverbmaster.android.data.repository.SyncPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_11_14 = object : Migration(11, 14) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 1. Wipe local history to force a fresh, normalized sync from the server
            db.execSQL("DELETE FROM practice_history")
            
            // 2. Drop intermediate indices if they exist from local development versions
            db.execSQL("DROP INDEX IF EXISTS `index_practice_history_userId_taskId_submittedAt` ")
            
            // 3. Create the final, robust unique index including lexemeId
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_practice_history_userId_taskId_lexemeId_submittedAt` ON `practice_history` (`userId`, `taskId`, `lexemeId`, `submittedAt`)")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        prefs: SyncPreferences,
    ): AppDatabase {
        val db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "german_verb_master.db"
            )
            .addMigrations(MIGRATION_11_14)
            .fallbackToDestructiveMigration(true)
            .build()
            
        // One-time clear of last sync time to force a full re-download of normalized records
        // after the migration wiped the table.
        kotlinx.coroutines.MainScope().launch {
            prefs.clearHistoryLastSync()
        }
        
        return db
    }

    @Provides
    fun provideLexemeDao(db: AppDatabase): LexemeDao = db.lexemeDao()

    @Provides
    fun provideTaskSpecDao(db: AppDatabase): TaskSpecDao = db.taskSpecDao()

    @Provides
    fun providePracticeHistoryDao(db: AppDatabase): PracticeHistoryDao = db.practiceHistoryDao()

    @Provides
    fun provideInflectionDao(db: AppDatabase): InflectionDao = db.inflectionDao()

    @Provides
    fun provideWordDao(db: AppDatabase): WordDao = db.wordDao()
}

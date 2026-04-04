package com.germanverbmaster.android.di

import android.content.Context
import androidx.room.Room
import com.germanverbmaster.android.data.local.dao.InflectionDao
import com.germanverbmaster.android.data.local.dao.LexemeDao
import com.germanverbmaster.android.data.local.dao.PracticeHistoryDao
import com.germanverbmaster.android.data.local.dao.TaskSpecDao
import com.germanverbmaster.android.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "german_verb_master.db"
            ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    fun provideLexemeDao(db: AppDatabase): LexemeDao = db.lexemeDao()

    @Provides
    fun provideTaskSpecDao(db: AppDatabase): TaskSpecDao = db.taskSpecDao()

    @Provides
    fun providePracticeHistoryDao(db: AppDatabase): PracticeHistoryDao = db.practiceHistoryDao()

    @Provides
    fun provideInflectionDao(db: AppDatabase): InflectionDao = db.inflectionDao()
}

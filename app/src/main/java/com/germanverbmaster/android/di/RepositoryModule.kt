package com.germanverbmaster.android.di

import com.germanverbmaster.android.data.local.dao.InflectionDao
import com.germanverbmaster.android.data.local.dao.LexemeDao
import com.germanverbmaster.android.data.local.dao.PracticeHistoryDao
import com.germanverbmaster.android.data.local.dao.TaskSpecDao
import com.germanverbmaster.android.data.local.dao.WordDao
import com.germanverbmaster.android.data.remote.SupabaseInflectionApi
import com.germanverbmaster.android.data.remote.SupabaseLexemeApi
import com.germanverbmaster.android.data.remote.SupabaseTaskApi
import com.germanverbmaster.android.data.remote.SupabaseWordsApi
import com.germanverbmaster.android.data.repository.InflectionRepository
import com.germanverbmaster.android.data.repository.LexemeRepository
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.data.repository.SyncPreferences
import com.germanverbmaster.android.data.repository.TaskRepository
import com.germanverbmaster.android.data.repository.WordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideLexemeRepository(
        dao: LexemeDao,
        api: SupabaseLexemeApi,
        prefs: SyncPreferences
    ): LexemeRepository = LexemeRepository(dao, api, prefs)

    @Provides
    @Singleton
    fun provideTaskRepository(
        dao: TaskSpecDao,
        api: SupabaseTaskApi,
        prefs: SyncPreferences
    ): TaskRepository = TaskRepository(dao, api, prefs)

    @Provides
    @Singleton
    fun providePracticeRepository(
        dao: PracticeHistoryDao
    ): PracticeRepository = PracticeRepository(dao)

    @Provides
    @Singleton
    fun provideInflectionRepository(
        dao: InflectionDao,
        api: SupabaseInflectionApi,
        prefs: SyncPreferences
    ): InflectionRepository = InflectionRepository(dao, api, prefs)

    @Provides
    @Singleton
    fun provideWordRepository(
        dao: WordDao,
        api: SupabaseWordsApi,
        prefs: SyncPreferences
    ): WordRepository = WordRepository(dao, api, prefs)
}

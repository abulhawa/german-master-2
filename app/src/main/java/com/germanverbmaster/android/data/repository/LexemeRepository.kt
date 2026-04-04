package com.germanverbmaster.android.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.germanverbmaster.android.data.local.dao.LexemeDao
import com.germanverbmaster.android.data.remote.SupabaseLexemeApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "sync_prefs")
private val LEXEME_LAST_SYNC = stringPreferencesKey("lexeme_last_sync")
private val TASK_LAST_SYNC   = stringPreferencesKey("task_last_sync")

@Singleton
class SyncPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun getLexemeLastSync(): String? =
        context.dataStore.data.first()[LEXEME_LAST_SYNC]

    suspend fun setLexemeLastSync(value: String) =
        context.dataStore.edit { it[LEXEME_LAST_SYNC] = value }

    suspend fun getTaskLastSync(): String? =
        context.dataStore.data.first()[TASK_LAST_SYNC]

    suspend fun setTaskLastSync(value: String) =
        context.dataStore.edit { it[TASK_LAST_SYNC] = value }
}

@Singleton
class LexemeRepository @Inject constructor(
    private val dao: LexemeDao,
    private val api: SupabaseLexemeApi,
    private val prefs: SyncPreferences,
) {
    /** Returns true if local DB is empty (first launch) */
    suspend fun needsFullSync(): Boolean = dao.count() == 0

    /**
     * Sync from Supabase.
     * On first launch: fetch everything.
     * On subsequent launches: fetch only rows updated since last sync.
     */
    suspend fun sync() {
        val since = prefs.getLexemeLastSync()
        val remote = if (since == null) api.fetchAll() else api.fetchUpdatedSince(since)
        if (remote.isEmpty()) return
        
        // Use the extension function from SupabaseLexemeApi
        val entities = remote.map { with(api) { it.toEntity() } }
        dao.upsertAll(entities)

        val latest = remote.maxOf { it.updatedAt }
        prefs.setLexemeLastSync(latest)
    }

    fun observeByPos(pos: String) = dao.observeByPos(pos)
}

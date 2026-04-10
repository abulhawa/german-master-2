package com.germanverbmaster.android.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.germanverbmaster.android.data.local.dao.LexemeDao
import com.germanverbmaster.android.data.remote.RemoteLexeme
import com.germanverbmaster.android.data.remote.SupabaseLexemeApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "sync_prefs")
private val LEXEME_LAST_SYNC = stringPreferencesKey("lexeme_last_sync")
private val TASK_LAST_SYNC   = stringPreferencesKey("task_last_sync")
private val INFLECTION_LAST_SYNC = stringPreferencesKey("inflection_last_sync")

@Singleton
class SyncPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    suspend fun getLexemeLastSync(): String? =
        context.dataStore.data.first()[LEXEME_LAST_SYNC]

    suspend fun setLexemeLastSync(value: String) =
        context.dataStore.edit { it[LEXEME_LAST_SYNC] = value }

    suspend fun getTaskLastSync(): String? =
        context.dataStore.data.first()[TASK_LAST_SYNC]

    suspend fun setTaskLastSync(value: String) =
        context.dataStore.edit { it[TASK_LAST_SYNC] = value }

    suspend fun getInflectionLastSync(): String? =
        context.dataStore.data.first()[INFLECTION_LAST_SYNC]

    suspend fun setInflectionLastSync(value: String) =
        context.dataStore.edit { it[INFLECTION_LAST_SYNC] = value }
}

@Singleton
class LexemeRepository @Inject constructor(
    private val dao: LexemeDao,
    private val api: SupabaseLexemeApi,
    private val prefs: SyncPreferences,
) {
    suspend fun deleteAll() = dao.deleteAll()

    /** Returns true if local DB has fewer than a healthy threshold (e.g. 500) */
    suspend fun needsFullSync(): Boolean = dao.count() < 500

    suspend fun getAllIds(): List<String> = dao.getAllIds()

    suspend fun countApprovedAndComplete(): Int = dao.countApprovedAndComplete()

    suspend fun fetchRemote(): List<RemoteLexeme>? {
        val since = prefs.getLexemeLastSync()
        val remote = if (since == null || needsFullSync()) api.fetchAll() else api.fetchUpdatedSince(since)
        return if (remote.isEmpty()) null else remote
    }

    suspend fun saveToLocal(remote: List<RemoteLexeme>) {
        if (remote.isEmpty()) return
        val entities = remote.map { with(api) { it.toEntity() } }
        dao.upsertAll(entities)
    }

    /**
     * Legacy sync from Supabase.
     */
    suspend fun sync() {
        val remote = fetchRemote()
        if (remote != null) {
            saveToLocal(remote)
        }
    }
}

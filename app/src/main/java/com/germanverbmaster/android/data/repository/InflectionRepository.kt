package com.germanverbmaster.android.data.repository

import com.germanverbmaster.android.data.local.dao.InflectionDao
import com.germanverbmaster.android.data.remote.RemoteInflection
import com.germanverbmaster.android.data.remote.SupabaseInflectionApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InflectionRepository @Inject constructor(
    private val dao: InflectionDao,
    val api: SupabaseInflectionApi,
    private val prefs: SyncPreferences,
) {
    /** Returns true if local DB has fewer than a healthy threshold (e.g. 500) */
    suspend fun needsFullSync(): Boolean = dao.count() < 500

    suspend fun fetchRemote(): List<RemoteInflection>? {
        val since = prefs.getInflectionLastSync()
        val remote = if (since == null || needsFullSync()) api.fetchAll() else api.fetchUpdatedSince(since)
        return if (remote.isEmpty()) null else remote
    }

    suspend fun saveToLocal(remote: List<RemoteInflection>) {
        if (remote.isEmpty()) return
        
        val entities = remote.map { with(api) { it.toEntity() } }
        dao.upsertAll(entities)
    }

    suspend fun sync() {
        val remote = fetchRemote()
        if (remote != null) {
            saveToLocal(remote)
        }
    }
}

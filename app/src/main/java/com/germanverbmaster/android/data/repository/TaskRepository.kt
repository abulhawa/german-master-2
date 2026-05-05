package com.germanverbmaster.android.data.repository

import com.germanverbmaster.android.data.local.AppPreferences
import com.germanverbmaster.android.data.local.dao.TaskSpecDao
import com.germanverbmaster.android.data.local.entity.TaskSpecEntity
import com.germanverbmaster.android.data.remote.RemoteTaskSpec
import com.germanverbmaster.android.data.remote.SupabaseTaskApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    val dao: TaskSpecDao,
    val api: SupabaseTaskApi,
    private val prefs: AppPreferences,
) {
    suspend fun exists(id: String): Boolean = dao.exists(id)

    suspend fun findHistoryAnchorTaskId(lexemeId: String, pos: String): String? =
        dao.findHistoryAnchorTaskId(lexemeId, pos)

    /** Returns true if local DB is empty */
    suspend fun needsFullSync(): Boolean = dao.count() == 0

    suspend fun fetchRemote(): List<RemoteTaskSpec>? {
        val since = prefs.getTaskLastSync()
        val remote = if (since == null || needsFullSync()) api.fetchAll() else api.fetchUpdatedSince(since)
        return if (remote.isEmpty()) null else remote
    }

    suspend fun saveToLocal(remote: List<RemoteTaskSpec>) {
        if (remote.isEmpty()) return
        
        // Use extension function within context of api
        val entities = remote.map { with(api) { it.toEntity(cefrLevel = null) } }
        dao.upsertAll(entities)
    }

    suspend fun sync() {
        val remote = fetchRemote()
        if (remote != null) {
            saveToLocal(remote)
        }
    }

    suspend fun fetchBatch(
        pos: String? = null,
        cefrLevel: String? = null,
        collection: String? = null,
        limit: Int = 20,
    ): List<TaskSpecEntity> = dao.fetchBatch(pos, cefrLevel, collection, limit)


    suspend fun count(): Int = dao.count()
}

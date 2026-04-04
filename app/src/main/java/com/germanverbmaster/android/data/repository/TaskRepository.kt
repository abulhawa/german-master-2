package com.germanverbmaster.android.data.repository

import com.germanverbmaster.android.data.local.dao.TaskSpecDao
import com.germanverbmaster.android.data.local.entity.TaskSpecEntity
import com.germanverbmaster.android.data.remote.SupabaseTaskApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val dao: TaskSpecDao,
    private val api: SupabaseTaskApi,
    private val prefs: SyncPreferences,
) {
    suspend fun sync() {
        val since = prefs.getTaskLastSync()
        val remote = if (since == null) api.fetchAll() else api.fetchUpdatedSince(since)
        if (remote.isEmpty()) return
        
        // Use extension function within context of api
        val entities = remote.map { with(api) { it.toEntity(cefrLevel = null) } }
        dao.upsertAll(entities)

        val latest = remote.maxOf { it.updatedAt }
        prefs.setTaskLastSync(latest)
    }

    suspend fun fetchBatch(
        pos: String? = null,
        cefrLevel: String? = null,
        limit: Int = 20,
    ): List<TaskSpecEntity> = dao.fetchBatch(pos, cefrLevel, limit)

    suspend fun fetchB2Batch(limit: Int = 20): List<TaskSpecEntity> =
        dao.fetchB2Batch(limit)

    suspend fun count(): Int = dao.count()
}

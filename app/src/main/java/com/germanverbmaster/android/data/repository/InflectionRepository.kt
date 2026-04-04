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
) {
    suspend fun fetchRemote(): List<RemoteInflection>? {
        val remote = api.fetchAll()
        return if (remote.isEmpty()) null else remote
    }

    suspend fun saveEntities(entities: List<com.germanverbmaster.android.data.local.entity.InflectionEntity>) {
        if (entities.isEmpty()) return
        dao.upsertAll(entities)
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

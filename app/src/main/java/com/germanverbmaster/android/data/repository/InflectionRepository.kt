package com.germanverbmaster.android.data.repository

import com.germanverbmaster.android.data.local.dao.InflectionDao
import com.germanverbmaster.android.data.remote.SupabaseInflectionApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InflectionRepository @Inject constructor(
    private val dao: InflectionDao,
    private val api: SupabaseInflectionApi,
) {
    suspend fun sync() {
        val remote = api.fetchAll()
        if (remote.isEmpty()) return
        
        // Using an extension property or a direct conversion
        // In this case, we'll use a local map for simplicity or fix the API to return entities
        val entities = remote.map { r ->
            com.germanverbmaster.android.data.local.entity.InflectionEntity(
                id = r.id,
                lexemeId = r.lexemeId,
                form = r.form,
                featuresJson = r.features.toString(),
                audioAsset = r.audioAsset
            )
        }
        dao.upsertAll(entities)
    }
}

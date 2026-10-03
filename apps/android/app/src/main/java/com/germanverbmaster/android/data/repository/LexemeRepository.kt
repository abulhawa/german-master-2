package com.germanverbmaster.android.data.repository

import com.germanverbmaster.android.data.local.AppPreferences
import com.germanverbmaster.android.data.local.dao.LexemeDao
import com.germanverbmaster.android.data.local.entity.LexemeEntity
import com.germanverbmaster.android.data.remote.RemoteLexeme
import com.germanverbmaster.android.data.remote.SupabaseLexemeApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LexemeRepository @Inject constructor(
    private val dao: LexemeDao,
    private val api: SupabaseLexemeApi,
    private val prefs: AppPreferences,
) {
    suspend fun exists(id: String): Boolean = dao.exists(id)

    suspend fun getById(id: String): LexemeEntity? = dao.getById(id)

    suspend fun getByIds(ids: List<String>): List<LexemeEntity> =
        if (ids.isEmpty()) emptyList() else dao.getByIds(ids)

    suspend fun findIdByLemmaAndPos(lemma: String, pos: String): String? =
        dao.findIdByLemmaAndPos(lemma, pos)

    suspend fun deleteAll() = dao.deleteAll()

    /** Returns true if local DB has fewer than a healthy threshold (e.g. 500) */
    suspend fun needsFullSync(): Boolean = dao.count() < 500

    suspend fun getAllIds(): List<String> = dao.getAllIds()

    suspend fun countApprovedAndComplete(): Int = dao.countApprovedAndComplete()

    suspend fun fetchRemote(): List<RemoteLexeme>? {
        val since = prefs.getLexemeLastSync()
        val remote = if ((since == null) || needsFullSync()) api.fetchAll() else api.fetchUpdatedSince(since)
        return remote.ifEmpty { null }
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
        fetchRemote()?.let {
            saveToLocal(it)
        }
    }
}

package com.germanverbmaster.android.data.repository

import com.germanverbmaster.android.data.local.dao.WordDao
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.remote.SupabaseWordsApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepository @Inject constructor(
    private val dao: WordDao,
    private val api: SupabaseWordsApi,
    private val prefs: SyncPreferences,
) {
    fun observeAll(): Flow<List<WordEntity>> = dao.observeAll()

    fun observeByLevel(level: String): Flow<List<WordEntity>> = dao.observeByLevel(level)

    fun observeByPos(pos: String): Flow<List<WordEntity>> = dao.observeByPos(pos)

    fun observeByLevelAndPos(level: String, pos: String): Flow<List<WordEntity>> =
        dao.observeByLevelAndPos(level, pos)

    fun observeById(id: Int): Flow<WordEntity?> = dao.observeById(id)

    suspend fun findIdByLemmaAndPos(lemma: String, pos: String): Int? =
        dao.findIdByLemmaAndPos(lemma, pos)

    fun observeDistinctPos(): Flow<List<String>> = dao.observeDistinctPos()

    suspend fun needsSync(): Boolean = dao.count() < 100

    suspend fun sync() {
        val since = prefs.getLexemeLastSync() // reuse same pref key — close enough for words
        val remote = if (since == null || needsSync()) api.fetchAll()
                     else api.fetchUpdatedSince(since)
        if (remote.isNotEmpty()) {
            val entities = remote.map { with(api) { it.toEntity() } }
            dao.upsertAll(entities)
        }
    }
}

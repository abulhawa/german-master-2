package com.germanverbmaster.android.data.repository

import com.germanverbmaster.android.data.local.AppPreferences
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
    private val prefs: AppPreferences,
) {
    private companion object {
        const val MIN_NON_BUNDLED_WORDS_FOR_HEALTHY_DB = 100
    }

    fun observeAll(collection: String? = null): Flow<List<WordEntity>> = dao.observeAll(collection)

    fun observeByLevels(levels: List<String>, collection: String? = null): Flow<List<WordEntity>> = 
        dao.observeByLevels(levels, collection)

    fun observeByPosTypes(posTypes: List<String>, collection: String? = null): Flow<List<WordEntity>> = 
        dao.observeByPosTypes(posTypes, collection)

    fun observeByLevelsAndPos(levels: List<String>, posTypes: List<String>, collection: String? = null): Flow<List<WordEntity>> =
        dao.observeByLevelsAndPos(levels, posTypes, collection)

    fun observeById(id: Int): Flow<WordEntity?> = dao.observeById(id)

    suspend fun findIdByLemmaAndPos(lemma: String, pos: String): Int? =
        dao.findIdByLemmaAndPos(lemma, pos)

    suspend fun findTranslationByLemmaAndPos(lemma: String, pos: String): String? =
        dao.findTranslationByLemmaAndPos(lemma, pos)

    fun observeDistinctPos(): Flow<List<String>> = dao.observeDistinctPos()

    suspend fun needsSync(): Boolean =
        dao.count() < MIN_NON_BUNDLED_WORDS_FOR_HEALTHY_DB

    suspend fun fetchDatasetVersion(): String? =
        api.fetchDatasetVersionHeader()

    suspend fun sync(forceFullRefresh: Boolean = false) {
        val since = prefs.getLexemeLastSync()
        val localCount = dao.count()
        val requiresFullRefresh =
            forceFullRefresh || (since == null) || (localCount < MIN_NON_BUNDLED_WORDS_FOR_HEALTHY_DB)

        if (requiresFullRefresh) {
            if (localCount == 0) {
                val bootstrap = api.fetchBootstrapB2Beruf()
                if (bootstrap.isNotEmpty()) {
                    val bootstrapEntities = bootstrap.map { with(api) { it.toEntity() } }
                    dao.upsertAll(bootstrapEntities)
                }
            }

            val remote = api.fetchAll()
            if (remote.isNotEmpty()) {
                val entities = remote.map { with(api) { it.toEntity() } }
                dao.upsertAll(entities)
            }
            return
        }

        val remote = api.fetchUpdatedSince(checkNotNull(since))
        if (remote.isNotEmpty()) {
            val entities = remote.map { with(api) { it.toEntity() } }
            dao.upsertAll(entities)
        }
    }

    suspend fun updateWord(word: WordEntity) {
        dao.upsert(word)
    }
}

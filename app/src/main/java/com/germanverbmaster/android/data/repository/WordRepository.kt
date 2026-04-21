package com.germanverbmaster.android.data.repository

import android.content.Context
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
    private val context: Context,
) {
    private companion object {
        const val BUNDLED_WORDLIST_ASSET = "b2_wortliste.csv"
        const val BUNDLED_WORDLIST_VERSION = "bundled_b2_beruf_2026_04_21"
        const val BUNDLED_BERUF_LEVEL = "B2 Beruf"
    }

    fun observeAll(): Flow<List<WordEntity>> = dao.observeAll()

    fun observeByLevels(levels: List<String>): Flow<List<WordEntity>> = dao.observeByLevels(levels)

    fun observeByPosTypes(posTypes: List<String>): Flow<List<WordEntity>> = dao.observeByPosTypes(posTypes)

    fun observeByLevelsAndPos(levels: List<String>, posTypes: List<String>): Flow<List<WordEntity>> =
        dao.observeByLevelsAndPos(levels, posTypes)

    fun observeById(id: Int): Flow<WordEntity?> = dao.observeById(id)

    suspend fun findIdByLemmaAndPos(lemma: String, pos: String): Int? =
        dao.findIdByLemmaAndPos(lemma, pos)

    suspend fun findTranslationByLemmaAndPos(lemma: String, pos: String): String? =
        dao.findTranslationByLemmaAndPos(lemma, pos)

    fun observeDistinctPos(): Flow<List<String>> = dao.observeDistinctPos()

    suspend fun needsSync(): Boolean = dao.count() < 100

    suspend fun upsertBundledB2BerufWordsIfAvailable(): Int {
        val csvText = runCatching {
            context.assets.open(BUNDLED_WORDLIST_ASSET).bufferedReader(Charsets.UTF_8).use { it.readText() }
        }.getOrNull() ?: return 0

        val entities = BundledWordsCsvParser.parse(
            csvText = csvText,
            level = BUNDLED_BERUF_LEVEL,
            versionTag = BUNDLED_WORDLIST_VERSION,
        )
        if (entities.isEmpty()) return 0

        dao.upsertAll(entities)
        return entities.size
    }

    suspend fun sync() {
        val since = prefs.getLexemeLastSync()
        val remote = if (since == null || needsSync()) api.fetchAll()
        else api.fetchUpdatedSince(since)
        if (remote.isNotEmpty()) {
            val entities = remote.map { with(api) { it.toEntity() } }
            dao.upsertAll(entities)
        }
    }
}

package com.germanverbmaster.android.data.repository

import android.content.Context
import com.germanverbmaster.android.data.local.dao.WordDao
import com.germanverbmaster.android.data.remote.SupabaseWordsApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WordRepositoryTest {

    private val dao: WordDao = mockk()
    private val api: SupabaseWordsApi = mockk()
    private val prefs: SyncPreferences = mockk()
    private val context: Context = mockk(relaxed = true)
    private lateinit var repository: WordRepository

    @Before
    fun setup() {
        repository = WordRepository(dao, api, prefs, context)
    }

    @Test
    fun `needsSync is true when only bundled level words exist`() = runBlocking {
        coEvery { dao.countByLevelExcluding("B2 Beruf") } returns 0

        val result = repository.needsSync()

        assertTrue(result)
    }

    @Test
    fun `needsSync is false when enough non bundled words exist`() = runBlocking {
        coEvery { dao.countByLevelExcluding("B2 Beruf") } returns 250

        val result = repository.needsSync()

        assertFalse(result)
    }

    @Test
    fun `sync fetches all words when non bundled coverage is below threshold even if since exists`() = runBlocking {
        coEvery { prefs.getLexemeLastSync() } returns "2026-04-21T00:00:00Z"
        coEvery { dao.countByLevelExcluding("B2 Beruf") } returns 0
        coEvery { api.fetchAll() } returns emptyList()

        repository.sync()

        coVerify(exactly = 1) { api.fetchAll() }
        coVerify(exactly = 0) { api.fetchUpdatedSince(any()) }
    }
}

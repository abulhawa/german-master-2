package com.germanverbmaster.android.data.repository

import com.germanverbmaster.android.data.local.dao.LexemeDao
import com.germanverbmaster.android.data.local.entity.LexemeEntity
import com.germanverbmaster.android.data.remote.RemoteLexeme
import com.germanverbmaster.android.data.remote.SupabaseLexemeApi
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class LexemeRepositoryTest {

    private val dao: LexemeDao = mockk()
    private val api: SupabaseLexemeApi = mockk()
    private val prefs: SyncPreferences = mockk()
    private lateinit var repository: LexemeRepository

    @Before
    fun setup() {
        repository = LexemeRepository(dao, api, prefs)
    }

    @Test
    fun `saveToLocal maps remote lexemes to entities and calls dao upsert`() = runBlocking {
        val remoteLexeme = RemoteLexeme(id = "1", lemma = "machen", pos = "V")
        val entity = LexemeEntity(id = "1", lemma = "machen", pos = "V")

        // Mock the mapping logic inside the API
        every { with(api) { any<RemoteLexeme>().toEntity() } } returns entity
        coEvery { dao.upsertAll(any()) } just Runs

        repository.saveToLocal(listOf(remoteLexeme))

        coVerify { dao.upsertAll(listOf(entity)) }
    }

    @Test
    fun `saveToLocal does nothing if list is empty`() = runBlocking {
        repository.saveToLocal(emptyList())
        coVerify(exactly = 0) { dao.upsertAll(any()) }
    }
}

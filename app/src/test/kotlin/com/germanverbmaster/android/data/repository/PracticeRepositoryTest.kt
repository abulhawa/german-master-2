package com.germanverbmaster.android.data.repository

import com.germanverbmaster.android.data.local.dao.AccuracyResult
import com.germanverbmaster.android.data.local.dao.PracticeHistoryDao
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PracticeRepositoryTest {

    private val dao: PracticeHistoryDao = mockk()
    private val repository = PracticeRepository(dao)

    @Test
    fun `record calls dao insert`() = runBlocking {
        val entry = mockk<PracticeHistoryEntity>()
        coEvery { dao.insert(entry) } returns 1L

        val result = repository.record(entry)

        assertEquals(1L, result)
        coVerify { dao.insert(entry) }
    }

    @Test
    fun `accuracyToday returns zeros when stats are null`() = runBlocking {
        coEvery { dao.statsSince(any()) } returns null

        val (accuracy, total) = repository.accuracyToday()

        assertEquals(0f, accuracy)
        assertEquals(0, total)
    }

    @Test
    fun `accuracyToday returns mapped values from dao`() = runBlocking {
        val stats = AccuracyResult(accuracy = 85.5f, total = 20)
        coEvery { dao.statsSince(any()) } returns stats

        val (accuracy, total) = repository.accuracyToday()

        assertEquals(85.5f, accuracy)
        assertEquals(20, total)
    }
}

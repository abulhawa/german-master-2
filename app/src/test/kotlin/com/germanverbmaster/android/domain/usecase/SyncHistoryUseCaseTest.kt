package com.germanverbmaster.android.domain.usecase

import com.germanverbmaster.android.data.local.dao.PracticeHistoryDao
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.remote.RemoteHistory
import com.germanverbmaster.android.data.remote.SupabaseHistoryApi
import com.germanverbmaster.android.data.repository.AuthRepository
import com.germanverbmaster.android.data.repository.SyncPreferences
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SyncHistoryUseCaseTest {

    private val historyDao: PracticeHistoryDao = mockk()
    private val historyApi: SupabaseHistoryApi = mockk()
    private val authRepository: AuthRepository = mockk()
    private val prefs: SyncPreferences = mockk()

    private lateinit var syncHistoryUseCase: SyncHistoryUseCase

    @Before
    fun setup() {
        syncHistoryUseCase = SyncHistoryUseCase(
            historyDao,
            historyApi,
            authRepository,
            prefs
        )

        // Mock Android Log
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
    }

    @Test
    fun `when not logged in, should skip sync`() = runTest {
        coEvery { authRepository.currentUserId } returns null

        syncHistoryUseCase()

        coVerify(exactly = 0) { historyDao.unsyncedForUser(any()) }
        coVerify(exactly = 0) { historyApi.fetchUpdatedSince(any(), any()) }
    }

    @Test
    fun `should upload unsynced records and download new ones`() = runTest {
        val userId = "user123"
        val lastSync = "2023-10-01T10:00:00Z"
        val unsyncedEntity = PracticeHistoryEntity(
            localId = 1,
            taskId = "t1",
            lexemeId = "l1",
            pos = "V",
            taskType = "drill",
            result = "correct",
            responseMs = 100,
            submittedAt = "2023-10-01T11:00:00Z",
            synced = false,
            userId = userId
        )
        val remoteHistory = RemoteHistory(
            remoteId = "r2",
            userId = userId,
            taskId = "t2",
            lexemeId = "l2",
            lemma = "lemma2",
            pos = "N",
            taskType = "drill",
            renderer = "default",
            result = "correct",
            submittedAnswer = "a",
            correctAnswer = "a",
            responseMs = 200,
            submittedAt = "2023-10-01T12:00:00Z",
            hintsUsed = false
        )

        coEvery { authRepository.currentUserId } returns userId
        coEvery { historyDao.unsyncedForUser(userId) } returns listOf(unsyncedEntity)
        
        // Mock toRemote conversion
        val remoteFromLocal = mockk<RemoteHistory>()
        every { with(historyApi) { unsyncedEntity.toRemote(userId) } } returns remoteFromLocal
        
        coEvery { historyApi.upsert(listOf(remoteFromLocal)) } just Runs
        coEvery { historyDao.markSynced(listOf(1)) } just Runs
        
        coEvery { prefs.getHistoryLastSync() } returns lastSync
        coEvery { historyApi.fetchUpdatedSince(lastSync, userId) } returns listOf(remoteHistory)
        
        // Mock toEntity conversion
        val entityFromRemote = mockk<PracticeHistoryEntity>()
        every { with(historyApi) { remoteHistory.toEntity() } } returns entityFromRemote
        
        coEvery { historyDao.upsertAll(listOf(entityFromRemote)) } just Runs
        coEvery { prefs.setHistoryLastSync("2023-10-01T12:00:00Z") } just Runs

        syncHistoryUseCase()

        coVerify { historyApi.upsert(any()) }
        coVerify { historyDao.markSynced(any()) }
        coVerify { historyDao.upsertAll(any()) }
        coVerify { prefs.setHistoryLastSync("2023-10-01T12:00:00Z") }
    }
}

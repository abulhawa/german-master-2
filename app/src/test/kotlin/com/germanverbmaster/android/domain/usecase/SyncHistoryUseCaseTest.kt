package com.germanverbmaster.android.domain.usecase

import com.germanverbmaster.android.data.local.dao.PracticeHistoryDao
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.remote.RemoteHistory
import com.germanverbmaster.android.data.remote.SupabaseHistoryApi
import com.germanverbmaster.android.data.repository.AuthRepository
import com.germanverbmaster.android.data.repository.SyncPreferences
import com.germanverbmaster.android.data.sync.HistorySyncMapper
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coJustRun
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
    private val historySyncMapper: HistorySyncMapper = mockk()
    private val authRepository: AuthRepository = mockk()
    private val prefs: SyncPreferences = mockk()

    private lateinit var syncHistoryUseCase: SyncHistoryUseCase

    @Before
    fun setup() {
        syncHistoryUseCase = SyncHistoryUseCase(
            historyDao,
            historyApi,
            historySyncMapper,
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
        every { authRepository.currentUserId } returns null

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
            lemma = "lemma1",
            pos = "V",
            taskType = "drill",
            result = "correct",
            responseMs = 100,
            submittedAt = "2023-10-01T11:00:00Z",
            synced = false,
            userId = userId
        )
        val remoteHistory = RemoteHistory(
            remoteId = 2,
            userId = userId,
            taskId = "t2",
            lexemeId = "l2",
            lemma = "lemma2",
            pos = "N",
            taskType = "drill",
            renderer = "default",
            deviceId = "device-1",
            result = "correct",
            submittedAnswer = "a",
            correctAnswer = "a",
            responseMs = 200,
            submittedAt = "2023-10-01T12:00:00Z",
            hintsUsed = false
        )

        every { authRepository.currentUserId } returns userId
        coEvery { historyDao.unsyncedForUser(userId) } returns listOf(unsyncedEntity)

        val remoteFromLocal = RemoteHistory(
            remoteId = null,
            userId = userId,
            taskId = "t1",
            lexemeId = "l1",
            lemma = "lemma1",
            pos = "V",
            taskType = "drill",
            renderer = "default",
            deviceId = "device-1",
            result = "correct",
            submittedAnswer = "a",
            correctAnswer = "a",
            responseMs = 100,
            hintsUsed = false,
            submittedAt = "2023-10-01T11:00:00Z",
        )
        coEvery { historySyncMapper.toRemote(unsyncedEntity, userId) } returns remoteFromLocal
        coEvery { historyApi.upsert(listOf(remoteFromLocal)) } just Runs
        coEvery { historyDao.markSynced(listOf(1), userId) } just Runs

        coEvery { prefs.getHistoryLastSync() } returns lastSync
        coEvery { historyApi.fetchUpdatedSince(lastSync, userId) } returns listOf(remoteHistory)

        val entityFromRemote = mockk<PracticeHistoryEntity>()
        coEvery { historySyncMapper.toLocalEntity(remoteHistory) } returns entityFromRemote
        coEvery { historyDao.upsertAll(listOf(entityFromRemote)) } just Runs
        coJustRun { prefs.setHistoryLastSync("2023-10-01T12:00:00Z") }

        syncHistoryUseCase()

        coVerify { historyApi.upsert(any()) }
        coVerify { historyDao.markSynced(any(), userId) }
        coVerify { historyDao.upsertAll(any()) }
        coVerify { prefs.setHistoryLastSync("2023-10-01T12:00:00Z") }
    }

    @Test
    fun `should upload anonymous records and associate them with user`() = runTest {
        val userId = "user123"
        val anonymousRecord = PracticeHistoryEntity(
            localId = 5,
            taskId = "t5",
            lexemeId = "l5",
            lemma = "lemma5",
            pos = "V",
            taskType = "drill",
            result = "correct",
            responseMs = 100,
            submittedAt = "2023-10-01T11:00:00Z",
            synced = false,
            userId = null
        )

        every { authRepository.currentUserId } returns userId
        coEvery { historyDao.unsyncedForUser(userId) } returns listOf(anonymousRecord)

        val remoteFromLocal = RemoteHistory(
            remoteId = null,
            userId = userId,
            taskId = "t5",
            lexemeId = "l5",
            lemma = "lemma5",
            pos = "V",
            taskType = "drill",
            renderer = "default",
            deviceId = "device-1",
            result = "correct",
            submittedAnswer = "a",
            correctAnswer = "a",
            responseMs = 100,
            hintsUsed = false,
            submittedAt = "2023-10-01T11:00:00Z",
        )
        coEvery { historySyncMapper.toRemote(anonymousRecord, userId) } returns remoteFromLocal
        coEvery { historyApi.upsert(listOf(remoteFromLocal)) } just Runs
        coEvery { historyDao.markSynced(listOf(5), userId) } just Runs

        coEvery { prefs.getHistoryLastSync() } returns null
        coEvery { historyApi.fetchUpdatedSince(any(), userId) } returns emptyList()

        syncHistoryUseCase()

        coVerify { historyApi.upsert(listOf(remoteFromLocal)) }
        coVerify { historyDao.markSynced(listOf(5), userId) }
    }

    @Test
    fun `should skip downloaded rows that match the just-uploaded batch`() = runTest {
        val userId = "user123"
        val lastSync = "2023-10-01T10:00:00Z"
        val uploadedEntity = PracticeHistoryEntity(
            localId = 9,
            taskId = "t9",
            lexemeId = "l9",
            lemma = "lemma9",
            pos = "V",
            taskType = "drill",
            result = "correct",
            responseMs = 150,
            hintsUsed = true,
            submittedAt = "2023-10-01T11:00:00Z",
            synced = false,
            userId = null,
        )
        val matchingRemote = RemoteHistory(
            remoteId = 12,
            userId = userId,
            taskId = uploadedEntity.taskId,
            lexemeId = uploadedEntity.lexemeId,
            lemma = "lemma9",
            pos = uploadedEntity.pos,
            taskType = uploadedEntity.taskType,
            renderer = "default",
            deviceId = "device-1",
            result = uploadedEntity.result,
            submittedAnswer = "a",
            correctAnswer = "a",
            responseMs = uploadedEntity.responseMs,
            hintsUsed = uploadedEntity.hintsUsed,
            submittedAt = uploadedEntity.submittedAt,
        )

        every { authRepository.currentUserId } returns userId
        coEvery { historyDao.unsyncedForUser(userId) } returns listOf(uploadedEntity)

        val remoteFromLocal = mockk<RemoteHistory>()
        every { remoteFromLocal.userId } returns userId
        every { remoteFromLocal.taskId } returns uploadedEntity.taskId
        every { remoteFromLocal.lexemeId } returns uploadedEntity.lexemeId
        every { remoteFromLocal.lemma } returns "lemma9"
        every { remoteFromLocal.pos } returns uploadedEntity.pos
        every { remoteFromLocal.taskType } returns uploadedEntity.taskType
        every { remoteFromLocal.renderer } returns "default"
        every { remoteFromLocal.result } returns uploadedEntity.result
        every { remoteFromLocal.submittedAnswer } returns "a"
        every { remoteFromLocal.correctAnswer } returns "a"
        every { remoteFromLocal.responseMs } returns uploadedEntity.responseMs
        every { remoteFromLocal.hintsUsed } returns uploadedEntity.hintsUsed
        every { remoteFromLocal.submittedAt } returns uploadedEntity.submittedAt
        coEvery { historySyncMapper.toRemote(uploadedEntity, userId) } returns remoteFromLocal

        coEvery { historyApi.upsert(listOf(remoteFromLocal)) } just Runs
        coEvery { historyDao.markSynced(listOf(9), userId) } just Runs
        coEvery { prefs.getHistoryLastSync() } returns lastSync
        coEvery { historyApi.fetchUpdatedSince(lastSync, userId) } returns listOf(matchingRemote)
        coJustRun { prefs.setHistoryLastSync(uploadedEntity.submittedAt) }

        syncHistoryUseCase()

        coVerify(exactly = 0) { historyDao.upsertAll(any()) }
        coVerify { prefs.setHistoryLastSync(uploadedEntity.submittedAt) }
    }

    @Test
    fun `should not upload unsupported local-only history rows`() = runTest {
        val userId = "user123"

        val blockedEntity = PracticeHistoryEntity(
            localId = 3,
            taskId = "word_3",
            lexemeId = "word_3",
            lemma = "unmapped",
            pos = "V",
            taskType = "vocabulary_drill",
            renderer = "word_card",
            result = "correct",
            responseMs = 10,
            submittedAt = "2023-10-01T11:00:00Z",
            synced = false,
            userId = null,
        )

        every { authRepository.currentUserId } returns userId
        coEvery { historyDao.unsyncedForUser(userId) } returns listOf(blockedEntity)
        coEvery { historySyncMapper.toRemote(blockedEntity, userId) } returns null
        coEvery { prefs.getHistoryLastSync() } returns null
        coEvery { historyApi.fetchUpdatedSince(any(), userId) } returns emptyList()

        syncHistoryUseCase()

        coVerify(exactly = 0) { historyApi.upsert(any()) }
        coVerify(exactly = 0) { historyDao.markSynced(any(), any()) }
    }
}

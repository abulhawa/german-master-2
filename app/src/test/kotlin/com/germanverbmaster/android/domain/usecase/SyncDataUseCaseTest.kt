package com.germanverbmaster.android.domain.usecase

import androidx.room.withTransaction
import com.germanverbmaster.android.data.local.dao.LexemeDao
import com.germanverbmaster.android.data.local.dao.TaskSpecDao
import com.germanverbmaster.android.data.local.db.AppDatabase
import com.germanverbmaster.android.data.remote.RemoteLexeme
import com.germanverbmaster.android.data.repository.InflectionRepository
import com.germanverbmaster.android.data.repository.LexemeRepository
import com.germanverbmaster.android.data.repository.SyncPreferences
import com.germanverbmaster.android.data.repository.TaskRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SyncDataUseCaseTest {

    private val lexemeRepository: LexemeRepository = mockk()
    private val taskRepository: TaskRepository = mockk()
    private val inflectionRepository: InflectionRepository = mockk()
    private val database: AppDatabase = mockk()
    private val prefs: SyncPreferences = mockk()
    
    private val lexemeDao: LexemeDao = mockk()
    private val taskSpecDao: TaskSpecDao = mockk()

    private lateinit var syncDataUseCase: SyncDataUseCase

    @Before
    fun setup() {
        syncDataUseCase = SyncDataUseCase(
            lexemeRepository,
            taskRepository,
            inflectionRepository,
            database,
            prefs
        )

        // Mock Android Log
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any<String>(), any<String>()) } returns 0
        every { android.util.Log.e(any<String>(), any<String>()) } returns 0
        every { android.util.Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { android.util.Log.w(any<String>(), any<String>()) } returns 0

        // Mock Room transaction
        mockkStatic("androidx.room.RoomDatabaseKt")
        val transactionLambda = slot<suspend () -> Any>()
        coEvery { database.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        every { database.lexemeDao() } returns lexemeDao
        every { database.taskSpecDao() } returns taskSpecDao
    }

    @Test
    fun `sync completes successfully and updates prefs`() = runTest {
        // Arrange
        val remoteLexemes = listOf(
            RemoteLexeme(
                id = "l1",
                lemma = "machen",
                pos = "V",
                updatedAt = "2023-10-01"
            )
        )
        coEvery { lexemeRepository.fetchRemote() } returns remoteLexemes
        coEvery { inflectionRepository.fetchRemote() } returns emptyList()
        coEvery { taskRepository.fetchRemote() } returns emptyList()
        
        coEvery { lexemeRepository.saveToLocal(any()) } just Runs
        coEvery { inflectionRepository.saveToLocal(any()) } just Runs
        coEvery { lexemeRepository.getAllIds() } returns emptyList()
        coEvery { lexemeRepository.countApprovedAndComplete() } returns 0
        coEvery { lexemeDao.countApproved() } returns 0
        coEvery { lexemeDao.countComplete() } returns 0
        coEvery { lexemeDao.getAllLevels() } returns emptyList()
        coEvery { taskSpecDao.upsertAll(any()) } just Runs
        coEvery { inflectionRepository.saveToLocal(any()) } just Runs
        
        coEvery { prefs.setLexemeLastSync(any()) } returns mockk()
        coEvery { prefs.setTaskLastSync(any()) } returns mockk()
        coEvery { prefs.setTaskLastSync(any()) } returns mockk()

        // Act
        syncDataUseCase.invoke()

        // Assert
        coVerify { lexemeRepository.saveToLocal(remoteLexemes) }
        coVerify { prefs.setLexemeLastSync("2023-10-01") }
    }

    @Test(expected = Exception::class)
    fun `sync throws exception when remote fetch fails`() = runTest {
        // Arrange
        coEvery { lexemeRepository.fetchRemote() } throws Exception("Network error")

        // Act
        syncDataUseCase.invoke()
    }
}

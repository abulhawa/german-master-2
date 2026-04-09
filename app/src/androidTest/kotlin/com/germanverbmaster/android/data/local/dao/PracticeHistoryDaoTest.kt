package com.germanverbmaster.android.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.germanverbmaster.android.data.local.db.AppDatabase
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PracticeHistoryDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: PracticeHistoryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.practiceHistoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndObserveRecent() = runBlocking {
        val entry = PracticeHistoryEntity(
            taskId = "t1",
            lexemeId = "l1",
            pos = "V",
            taskType = "type",
            result = "correct",
            responseMs = 1000,
            submittedAt = "2023-10-01T10:00:00Z"
        )

        dao.insert(entry)

        val recent = dao.observeRecent(10).first()
        assertEquals(1, recent.size)
        assertEquals("correct", recent[0].result)
    }

    @Test
    fun markSynced() = runBlocking {
        val entries = listOf(
            PracticeHistoryEntity(taskId = "t1", lexemeId = "l1", pos = "V", taskType = "t", result = "c", responseMs = 1, submittedAt = "a", synced = false),
            PracticeHistoryEntity(taskId = "t2", lexemeId = "l2", pos = "V", taskType = "t", result = "c", responseMs = 1, submittedAt = "b", synced = false)
        )
        dao.insertIgnore(entries)
        
        val unsyncedBefore = dao.unsynced()
        assertEquals(2, unsyncedBefore.size)
        
        val ids = unsyncedBefore.map { it.localId }
        dao.markSynced(ids)
        
        val unsyncedAfter = dao.unsynced()
        assertEquals(0, unsyncedAfter.size)
    }
}

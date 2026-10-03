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
            PracticeHistoryEntity(taskId = "t1", lexemeId = "l1", pos = "V", taskType = "t", result = "c", responseMs = 1, submittedAt = "a", synced = false, userId = "user1"),
            PracticeHistoryEntity(taskId = "t2", lexemeId = "l2", pos = "V", taskType = "t", result = "c", responseMs = 1, submittedAt = "b", synced = false, userId = "user1")
        )
        dao.insertIgnore(entries)
        
        val unsyncedBefore = dao.unsyncedForUser("user1")
        assertEquals(2, unsyncedBefore.size)
        
        val ids = unsyncedBefore.map { it.localId }
        dao.markSynced(ids, "user1")
        
        val unsyncedAfter = dao.unsyncedForUser("user1")
        assertEquals(0, unsyncedAfter.size)
    }

    @Test
    fun unsyncedForUserIncludesAllEligibleLocalAndUserRows() = runBlocking {
        val entries = (1..75).map { index ->
            PracticeHistoryEntity(
                taskId = "t$index",
                lexemeId = "l$index",
                pos = "V",
                taskType = "vocabulary_drill",
                result = "correct",
                responseMs = 1,
                submittedAt = "2023-10-01T10:00:00.${index.toString().padStart(3, '0')}Z",
                synced = false,
                userId = if (index % 2 == 0) "user1" else null,
            )
        } + PracticeHistoryEntity(
            taskId = "other-user",
            lexemeId = "other-user",
            pos = "V",
            taskType = "vocabulary_drill",
            result = "correct",
            responseMs = 1,
            submittedAt = "2023-10-01T12:00:00Z",
            synced = false,
            userId = "user2",
        ) + PracticeHistoryEntity(
            taskId = "already-synced",
            lexemeId = "already-synced",
            pos = "V",
            taskType = "vocabulary_drill",
            result = "correct",
            responseMs = 1,
            submittedAt = "2023-10-01T12:01:00Z",
            synced = true,
            userId = null,
        )

        dao.insertIgnore(entries)

        val unsynced = dao.unsyncedForUser("user1")

        assertEquals(75, unsynced.size)
        assertEquals("t1", unsynced.first().taskId)
        assertEquals("t75", unsynced.last().taskId)
    }

    @Test
    fun observeTaskTypeStats() = runBlocking {
        val entries = listOf(
            PracticeHistoryEntity(taskId = "t1", lexemeId = "l1", pos = "V", taskType = "verb_drill", result = "correct", responseMs = 1, submittedAt = "2023-10-01T10:00:00Z"),
            PracticeHistoryEntity(taskId = "t2", lexemeId = "l1", pos = "V", taskType = "verb_drill", result = "incorrect", responseMs = 1, submittedAt = "2023-10-01T10:01:00Z"),
            PracticeHistoryEntity(taskId = "t3", lexemeId = "l2", pos = "N", taskType = "noun_drill", result = "correct", responseMs = 1, submittedAt = "2023-10-01T10:02:00Z")
        )
        dao.insertIgnore(entries)

        val stats = dao.observeTaskTypeStats().first().sortedBy { it.taskType }
        assertEquals(2, stats.size)
        
        assertEquals("noun_drill", stats[0].taskType)
        assertEquals(1, stats[0].correctCount)
        assertEquals(1, stats[0].totalCount)

        assertEquals("verb_drill", stats[1].taskType)
        assertEquals(1, stats[1].correctCount)
        assertEquals(2, stats[1].totalCount)
    }

    @Test
    fun getDailyAccuracy() = runBlocking {
        val entries = listOf(
            PracticeHistoryEntity(taskId = "t1", lexemeId = "l1", pos = "V", taskType = "t", result = "correct", responseMs = 1, submittedAt = "2023-10-01T10:00:00Z"),
            PracticeHistoryEntity(taskId = "t2", lexemeId = "l1", pos = "V", taskType = "t", result = "incorrect", responseMs = 1, submittedAt = "2023-10-01T22:00:00Z"),
            PracticeHistoryEntity(taskId = "t3", lexemeId = "l1", pos = "V", taskType = "t", result = "correct", responseMs = 1, submittedAt = "2023-10-02T10:00:00Z")
        )
        dao.insertIgnore(entries)

        val daily = dao.getDailyAccuracy("2023-10-01T00:00:00Z")
        assertEquals(2, daily.size)
        
        assertEquals("2023-10-01", daily[0].date)
        assertEquals(50f, daily[0].accuracy)

        assertEquals("2023-10-02", daily[1].date)
        assertEquals(100f, daily[1].accuracy)
    }

    @Test
    fun uniqueConstraintViolation() = runBlocking {
        val entry1 = PracticeHistoryEntity(
            taskId = "t1",
            lexemeId = "l1",
            pos = "V",
            taskType = "type",
            result = "correct",
            responseMs = 1000,
            submittedAt = "2023-10-01T10:00:00Z",
            userId = "user1"
        )
        val entry2 = entry1.copy(result = "incorrect") // Same task, same time, same user, but different result

        dao.insert(entry1)
        
        // Attempting to insert a duplicate with insertIgnore should do nothing
        dao.insertIgnore(listOf(entry2))
        
        var all = dao.allForUser("user1")
        assertEquals(1, all.size)
        assertEquals("correct", all[0].result)

        // Attempting to upsert should replace the existing one
        dao.upsertAll(listOf(entry2))
        all = dao.allForUser("user1")
        assertEquals(1, all.size)
        assertEquals("incorrect", all[0].result)
    }

    @Test
    fun deleteDuplicates() = runBlocking {
        // We need to bypass the unique constraint to insert duplicates for testing deleteDuplicates
        // But since we have the constraint now, we can only test it if we had duplicates BEFORE the constraint.
        // Or we can test that it doesn't delete non-duplicates.
        
        val entries = listOf(
            PracticeHistoryEntity(taskId = "t1", lexemeId = "l1", pos = "V", taskType = "t", result = "c", responseMs = 1, submittedAt = "a", userId = "u1"),
            PracticeHistoryEntity(taskId = "t2", lexemeId = "l2", pos = "V", taskType = "t", result = "c", responseMs = 1, submittedAt = "b", userId = "u1")
        )
        dao.upsertAll(entries)
        
        dao.deleteDuplicates()
        
        val all = dao.allForUser("u1")
        assertEquals(2, all.size)
    }
}

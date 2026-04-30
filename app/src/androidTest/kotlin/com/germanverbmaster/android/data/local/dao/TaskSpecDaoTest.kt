package com.germanverbmaster.android.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.germanverbmaster.android.data.local.db.AppDatabase
import com.germanverbmaster.android.data.local.entity.LexemeEntity
import com.germanverbmaster.android.data.local.entity.TaskSpecEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskSpecDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var taskSpecDao: TaskSpecDao
    private lateinit var lexemeDao: LexemeDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        taskSpecDao = database.taskSpecDao()
        lexemeDao = database.lexemeDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun fetchBatch_respectsApprovedAndComplete() = runBlocking {
        // 1. Setup Lexemes
        val lexemes = listOf(
            LexemeEntity(id = "l1", lemma = "approved", pos = "V", isApproved = true, isComplete = true),
            LexemeEntity(id = "l2", lemma = "not_approved", pos = "V", isApproved = false, isComplete = true),
            LexemeEntity(id = "l3", lemma = "not_complete", pos = "V", isApproved = true, isComplete = false)
        )
        lexemeDao.upsertAll(lexemes)

        // 2. Setup Tasks
        val tasks = listOf(
            TaskSpecEntity(id = "t1", lexemeId = "l1", pos = "V", taskType = "type", renderer = "r", promptJson = "{}", solutionJson = "{}"),
            TaskSpecEntity(id = "t2", lexemeId = "l2", pos = "V", taskType = "type", renderer = "r", promptJson = "{}", solutionJson = "{}"),
            TaskSpecEntity(id = "t3", lexemeId = "l3", pos = "V", taskType = "type", renderer = "r", promptJson = "{}", solutionJson = "{}")
        )
        taskSpecDao.upsertAll(tasks)

        // 3. Act: Fetch batch
        val batch = taskSpecDao.fetchBatch(pos = "V", limit = 10)

        // 4. Assert: Only the task linked to the approved+complete lexeme should be returned
        assertEquals(1, batch.size)
        assertEquals("t1", batch[0].id)
    }

    @Test
    fun fetchBatch_filtersByPosAndLevel() = runBlocking {
        val lexeme = LexemeEntity(id = "l1", lemma = "word", pos = "N", isApproved = true, isComplete = true)
        lexemeDao.upsertAll(listOf(lexeme))

        val tasks = listOf(
            TaskSpecEntity(id = "t1", lexemeId = "l1", pos = "N", cefrLevel = "A1", taskType = "type", renderer = "r", promptJson = "{}", solutionJson = "{}"),
            TaskSpecEntity(id = "t2", lexemeId = "l1", pos = "V", cefrLevel = "A1", taskType = "type", renderer = "r", promptJson = "{}", solutionJson = "{}"),
            TaskSpecEntity(id = "t3", lexemeId = "l1", pos = "N", cefrLevel = "A2", taskType = "type", renderer = "r", promptJson = "{}", solutionJson = "{}")
        )
        taskSpecDao.upsertAll(tasks)

        // Filter by N and A1
        val batch = taskSpecDao.fetchBatch(pos = "N", cefrLevel = "A1", limit = 10)
        assertEquals(1, batch.size)
        assertEquals("t1", batch[0].id)
    }

    @Test
    fun findHistoryAnchorTaskId_prefersVocabularyDrill() = runBlocking {
        val lexeme = LexemeEntity(id = "l1", lemma = "Wort", pos = "N", isApproved = true, isComplete = true)
        lexemeDao.upsertAll(listOf(lexeme))

        val tasks = listOf(
            TaskSpecEntity(
                id = "task:noun",
                lexemeId = "l1",
                pos = "N",
                taskType = "noun_case_declension",
                renderer = "case_grid",
                promptJson = "{}",
                solutionJson = "{}",
            ),
            TaskSpecEntity(
                id = "task:vocab",
                lexemeId = "l1",
                pos = "N",
                taskType = "vocabulary_drill",
                renderer = "word_card",
                promptJson = "{}",
                solutionJson = "{}",
            ),
        )
        taskSpecDao.upsertAll(tasks)

        val anchorTaskId = taskSpecDao.findHistoryAnchorTaskId("l1", "N")

        assertEquals("task:vocab", anchorTaskId)
    }
}

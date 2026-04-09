package com.germanverbmaster.android.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.germanverbmaster.android.data.local.db.AppDatabase
import com.germanverbmaster.android.data.local.entity.LexemeEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LexemeDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var lexemeDao: LexemeDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        lexemeDao = database.lexemeDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertAndObserveByPos() = runBlocking {
        val lexemes = listOf(
            LexemeEntity(id = "1", lemma = "machen", pos = "V"),
            LexemeEntity(id = "2", lemma = "Haus", pos = "N"),
            LexemeEntity(id = "3", lemma = "gehen", pos = "V")
        )

        lexemeDao.upsertAll(lexemes)

        val verbs = lexemeDao.observeByPos("V").first()
        assertEquals(2, verbs.size)
        assertEquals(true, verbs.any { it.lemma == "machen" })
        assertEquals(true, verbs.any { it.lemma == "gehen" })
    }

    @Test
    fun countApprovedAndComplete() = runBlocking {
        val lexemes = listOf(
            LexemeEntity(id = "1", lemma = "a", pos = "V", isApproved = true, isComplete = true),
            LexemeEntity(id = "2", lemma = "b", pos = "V", isApproved = true, isComplete = false),
            LexemeEntity(id = "3", lemma = "c", pos = "V", isApproved = false, isComplete = true),
            LexemeEntity(id = "4", lemma = "d", pos = "V", isApproved = false, isComplete = false)
        )

        lexemeDao.upsertAll(lexemes)

        assertEquals(1, lexemeDao.countApprovedAndComplete())
        assertEquals(2, lexemeDao.countApproved())
        assertEquals(2, lexemeDao.countComplete())
    }

    @Test
    fun deleteAll() = runBlocking {
        val lexemes = listOf(LexemeEntity(id = "1", lemma = "a", pos = "V"))
        lexemeDao.upsertAll(lexemes)
        assertEquals(1, lexemeDao.count())

        lexemeDao.deleteAll()
        assertEquals(0, lexemeDao.count())
    }
}

package com.germanverbmaster.android.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.germanverbmaster.android.data.local.db.AppDatabase
import com.germanverbmaster.android.data.local.entity.WordEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var wordDao: WordDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        wordDao = database.wordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertAndObserveAll() = runBlocking {
        val words = listOf(
            WordEntity(id = 1, lemma = "Haus", pos = "N", level = "A1", english = "house"),
            WordEntity(id = 2, lemma = "gehen", pos = "V", level = "A1", english = null), // Should be filtered out by many queries
            WordEntity(id = 3, lemma = "Auto", pos = "N", level = "A2", english = "car")
        )

        wordDao.upsertAll(words)

        val allWords = wordDao.observeAll().first()
        assertEquals(2, allWords.size) // Only Haus and Auto have english translation
        assertEquals("Auto", allWords[0].lemma) // Ordered by lemma
        assertEquals("Haus", allWords[1].lemma)
    }

    @Test
    fun observeByLevel() = runBlocking {
        val words = listOf(
            WordEntity(id = 1, lemma = "Haus", pos = "N", level = "A1", english = "house"),
            WordEntity(id = 2, lemma = "Auto", pos = "N", level = "A2", english = "car")
        )

        wordDao.upsertAll(words)

        val a1Words = wordDao.observeByLevel("A1").first()
        assertEquals(1, a1Words.size)
        assertEquals("Haus", a1Words[0].lemma)
    }

    @Test
    fun observeByPos() = runBlocking {
        val words = listOf(
            WordEntity(id = 1, lemma = "Haus", pos = "N", level = "A1", english = "house"),
            WordEntity(id = 2, lemma = "schnell", pos = "Adj", level = "A1", english = "fast")
        )

        wordDao.upsertAll(words)

        val nouns = wordDao.observeByPos("N").first()
        assertEquals(1, nouns.size)
        assertEquals("Haus", nouns[0].lemma)
    }

    @Test
    fun observeDistinctPos() = runBlocking {
        val words = listOf(
            WordEntity(id = 1, lemma = "Haus", pos = "N", level = "A1", english = "house"),
            WordEntity(id = 2, lemma = "Auto", pos = "N", level = "A1", english = "car"),
            WordEntity(id = 3, lemma = "gehen", pos = "V", level = "A1", english = "to go")
        )

        wordDao.upsertAll(words)

        val positions = wordDao.observeDistinctPos().first()
        assertEquals(2, positions.size)
        assertEquals(listOf("N", "V"), positions)
    }
}

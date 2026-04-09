package com.germanverbmaster.android.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.germanverbmaster.android.data.local.db.AppDatabase
import com.germanverbmaster.android.data.local.entity.InflectionEntity
import com.germanverbmaster.android.data.local.entity.LexemeEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InflectionDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: InflectionDao
    private lateinit var lexemeDao: LexemeDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.inflectionDao()
        lexemeDao = database.lexemeDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertAndGetByLexemeId() = runBlocking {
        // Must insert lexeme first due to foreign key
        val lexeme = LexemeEntity(id = "l1", lemma = "machen", pos = "V")
        lexemeDao.upsertAll(listOf(lexeme))

        val inflections = listOf(
            InflectionEntity(id = "i1", lexemeId = "l1", form = "mache", featuresJson = "{}"),
            InflectionEntity(id = "i2", lexemeId = "l1", form = "machst", featuresJson = "{}")
        )

        dao.upsertAll(inflections)

        val result = dao.getByLexemeId("l1")
        assertEquals(2, result.size)
    }

    @Test
    fun deleteAll() = runBlocking {
        val lexeme = LexemeEntity(id = "l1", lemma = "machen", pos = "V")
        lexemeDao.upsertAll(listOf(lexeme))
        dao.upsertAll(listOf(InflectionEntity(id = "i1", lexemeId = "l1", form = "f", featuresJson = "{}")))

        dao.deleteAll()
        assertEquals(0, dao.getByLexemeId("l1").size)
    }
}

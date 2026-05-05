package com.germanverbmaster.android.di

import android.database.Cursor
import androidx.sqlite.db.SupportSQLiteDatabase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class DatabaseModuleTest {

    private fun tableInfoCursor(vararg columns: String): Cursor {
        val cursor = mockk<Cursor>()
        var index = -1
        every { cursor.getColumnIndex("name") } returns 0
        every { cursor.moveToNext() } answers {
            index += 1
            index < columns.size
        }
        every { cursor.getString(0) } answers { columns[index] }
        every { cursor.close() } returns Unit
        return cursor
    }

    @Test
    fun `migration 15 to 17 adds missing collection columns and clears migrated tables`() {
        val db = mockk<SupportSQLiteDatabase>(relaxed = true)
        every { db.query("PRAGMA table_info(`lexemes`)") } answers { tableInfoCursor("id", "lemma") }
        every { db.query("PRAGMA table_info(`task_specs`)") } answers { tableInfoCursor("id", "lexemeId") }
        every { db.query("PRAGMA table_info(`words`)") } answers { tableInfoCursor("id", "lemma") }
        every { db.query("PRAGMA table_info(`practice_history`)") } answers { tableInfoCursor("localId", "taskId") }

        DatabaseModule.MIGRATION_15_17.migrate(db)

        verify(exactly = 1) { db.execSQL("ALTER TABLE lexemes ADD COLUMN collectionsJson TEXT NOT NULL DEFAULT '[]'") }
        verify(exactly = 1) { db.execSQL("ALTER TABLE task_specs ADD COLUMN collectionsJson TEXT NOT NULL DEFAULT '[]'") }
        verify(exactly = 1) { db.execSQL("ALTER TABLE words ADD COLUMN collectionsJson TEXT NOT NULL DEFAULT '[]'") }
        verify(exactly = 1) { db.execSQL("ALTER TABLE practice_history ADD COLUMN collectionsJson TEXT") }
        verify(exactly = 1) { db.execSQL("DELETE FROM words") }
        verify(exactly = 1) { db.execSQL("DELETE FROM lexemes") }
        verify(exactly = 1) { db.execSQL("DELETE FROM task_specs") }
        verify(exactly = 1) { db.execSQL("DELETE FROM practice_history") }
    }

    @Test
    fun `migration 15 to 17 skips add column statements when columns already exist`() {
        val db = mockk<SupportSQLiteDatabase>(relaxed = true)
        every { db.query("PRAGMA table_info(`lexemes`)") } answers { tableInfoCursor("id", "collectionsJson") }
        every { db.query("PRAGMA table_info(`task_specs`)") } answers { tableInfoCursor("id", "collectionsJson") }
        every { db.query("PRAGMA table_info(`words`)") } answers { tableInfoCursor("id", "collectionsJson") }
        every { db.query("PRAGMA table_info(`practice_history`)") } answers { tableInfoCursor("localId", "collectionsJson") }

        DatabaseModule.MIGRATION_15_17.migrate(db)

        verify(exactly = 0) { db.execSQL("ALTER TABLE lexemes ADD COLUMN collectionsJson TEXT NOT NULL DEFAULT '[]'") }
        verify(exactly = 0) { db.execSQL("ALTER TABLE task_specs ADD COLUMN collectionsJson TEXT NOT NULL DEFAULT '[]'") }
        verify(exactly = 0) { db.execSQL("ALTER TABLE words ADD COLUMN collectionsJson TEXT NOT NULL DEFAULT '[]'") }
        verify(exactly = 0) { db.execSQL("ALTER TABLE practice_history ADD COLUMN collectionsJson TEXT") }

        verify(exactly = 1) { db.execSQL("DELETE FROM words") }
        verify(exactly = 1) { db.execSQL("DELETE FROM lexemes") }
        verify(exactly = 1) { db.execSQL("DELETE FROM task_specs") }
        verify(exactly = 1) { db.execSQL("DELETE FROM practice_history") }
    }
}

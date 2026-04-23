package com.germanverbmaster.android.data.sync

import com.germanverbmaster.android.data.local.entity.LexemeEntity
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.remote.HistorySyncDeviceIdProvider
import com.germanverbmaster.android.data.remote.RemoteHistory
import com.germanverbmaster.android.data.repository.LexemeRepository
import com.germanverbmaster.android.data.repository.TaskRepository
import com.germanverbmaster.android.data.repository.WordRepository
import io.mockk.every
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HistorySyncMapperTest {

    private val lexemeRepository: LexemeRepository = mockk()
    private val taskRepository: TaskRepository = mockk()
    private val wordRepository: WordRepository = mockk()
    private val deviceIdProvider: HistorySyncDeviceIdProvider = mockk {
        every { get() } returns "device-123"
    }

    private val mapper = HistorySyncMapper(
        lexemeRepository = lexemeRepository,
        taskRepository = taskRepository,
        wordRepository = wordRepository,
        deviceIdProvider = deviceIdProvider,
    )

    @Before
    fun setup() {
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(android.util.Log::class)
    }

    @Test
    fun `toRemote keeps remote-backed ids and parses nullable remote id`() = runTest {
        val entity = PracticeHistoryEntity(
            taskId = "task-1",
            lexemeId = "lex-1",
            lemma = "machen",
            pos = "V",
            taskType = "conjugate_form",
            renderer = "conjugate_form",
            result = "correct",
            submittedAnswer = "mache",
            correctAnswer = "mache",
            responseMs = 320,
            cefrLevel = "A1",
            hintsUsed = true,
            submittedAt = "2026-04-23T10:15:30Z",
            remoteId = "42",
        )

        coEvery { taskRepository.exists("task-1") } returns true
        coEvery { lexemeRepository.exists("lex-1") } returns true

        val remote = mapper.toRemote(entity, "user-1")

        requireNotNull(remote)
        assertEquals(42L, remote.remoteId)
        assertEquals("task-1", remote.taskId)
        assertEquals("lex-1", remote.lexemeId)
        assertEquals("machen", remote.lemma)
        assertEquals("V", remote.pos)
        assertEquals("mache", remote.submittedAnswer)
        assertEquals("mache", remote.correctAnswer)
        assertEquals("A1", remote.cefrLevel)
        assertEquals("device-123", remote.deviceId)
    }

    @Test
    fun `toRemote resolves Wortschatz rows to real remote identities`() = runTest {
        val entity = PracticeHistoryEntity(
            localId = 8,
            taskId = "word_12",
            lexemeId = "word_12",
            lemma = "machen",
            pos = "V",
            taskType = "vocabulary_drill",
            renderer = "word_card",
            result = "correct",
            submittedAnswer = "machen",
            correctAnswer = "machen",
            responseMs = 150,
            cefrLevel = "A1",
            submittedAt = "2026-04-23T10:15:30Z",
            remoteId = "guest-row",
        )

        coEvery { taskRepository.exists("word_12") } returns false
        coEvery { lexemeRepository.exists("word_12") } returns false
        coEvery { lexemeRepository.findIdByLemmaAndPos("machen", "V") } returns "lex-42"
        coEvery { taskRepository.findHistoryAnchorTaskId("lex-42", "V") } returns "task-42"

        val remote = mapper.toRemote(entity, "user-1")

        requireNotNull(remote)
        assertNull(remote.remoteId)
        assertEquals("task-42", remote.taskId)
        assertEquals("lex-42", remote.lexemeId)
        assertEquals("machen", remote.lemma)
        assertEquals("V", remote.pos)
        assertEquals("machen", remote.submittedAnswer)
        assertEquals("machen", remote.correctAnswer)
        assertEquals("A1", remote.cefrLevel)
        assertEquals("vocabulary_drill", remote.taskType)
        assertEquals("word_card", remote.renderer)
        assertEquals("device-123", remote.deviceId)
    }

    @Test
    fun `toRemote returns null when Wortschatz row cannot be resolved`() = runTest {
        val entity = PracticeHistoryEntity(
            taskId = "word_12",
            lexemeId = "word_12",
            lemma = "machen",
            pos = "V",
            taskType = "vocabulary_drill",
            renderer = "word_card",
            result = "correct",
            responseMs = 150,
            submittedAt = "2026-04-23T10:15:30Z",
        )

        coEvery { taskRepository.exists("word_12") } returns false
        coEvery { lexemeRepository.exists("word_12") } returns false
        coEvery { lexemeRepository.findIdByLemmaAndPos("machen", "V") } returns null

        val remote = mapper.toRemote(entity, "user-1")

        assertNull(remote)
    }

    @Test
    fun `toLocalEntity maps remote Wortschatz rows back to local word ids when possible`() = runTest {
        val remote = RemoteHistory(
            remoteId = 7,
            userId = "user-1",
            taskId = "task-42",
            lexemeId = "lex-42",
            lemma = "machen",
            pos = "V",
            taskType = "vocabulary_drill",
            renderer = "word_card",
            deviceId = "device-abc",
            result = "incorrect",
            submittedAnswer = "mache",
            correctAnswer = "machen",
            responseMs = 180,
            cefrLevel = "A1",
            hintsUsed = false,
            submittedAt = "2026-04-23T11:00:00Z",
        )

        coEvery { lexemeRepository.getById("lex-42") } returns LexemeEntity(
            id = "lex-42",
            lemma = "machen",
            pos = "V",
            cefrLevel = "A1",
        )
        coEvery { wordRepository.findIdByLemmaAndPos("machen", "V") } returns 12

        val entity = mapper.toLocalEntity(remote)

        assertEquals("7", entity.remoteId)
        assertEquals("word_12", entity.taskId)
        assertEquals("word_12", entity.lexemeId)
        assertEquals("machen", entity.lemma)
        assertEquals("A1", entity.cefrLevel)
        assertEquals("word_card", entity.renderer)
    }

    @Test
    fun `toLocalEntity keeps real ids when no local word match exists`() = runTest {
        val remote = RemoteHistory(
            remoteId = 9,
            userId = "user-1",
            taskId = "task-99",
            lexemeId = "lex-99",
            lemma = "Haus",
            pos = "N",
            taskType = "vocabulary_drill",
            renderer = "word_card",
            deviceId = "device-abc",
            result = "correct",
            submittedAnswer = "Haus",
            correctAnswer = "Haus",
            responseMs = 110,
            cefrLevel = "A1",
            hintsUsed = true,
            submittedAt = "2026-04-23T11:05:00Z",
        )

        coEvery { lexemeRepository.getById("lex-99") } returns LexemeEntity(
            id = "lex-99",
            lemma = "Haus",
            pos = "N",
            cefrLevel = "A1",
        )
        coEvery { wordRepository.findIdByLemmaAndPos("Haus", "N") } returns null

        val entity = mapper.toLocalEntity(remote)

        assertEquals("task-99", entity.taskId)
        assertEquals("lex-99", entity.lexemeId)
        assertEquals("Haus", entity.lemma)
        assertEquals("A1", entity.cefrLevel)
    }
}

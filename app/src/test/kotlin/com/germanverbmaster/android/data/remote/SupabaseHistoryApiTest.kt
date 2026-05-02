package com.germanverbmaster.android.data.remote

import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Test

class SupabaseHistoryApiTest {

    @Test
    fun `remote history payload uses all fields for practice_history`() {
        val remote = RemoteHistory(
            remoteId = 42,
            userId = "user-1",
            taskId = "task-1",
            lexemeId = "lex-1",
            lemma = "machen",
            pos = "V",
            taskType = "vocabulary_drill",
            renderer = "word_card",
            deviceId = "device-123",
            result = "correct",
            submittedAnswer = "machen",
            correctAnswer = "machen",
            responseMs = 320,
            cefrLevel = "A1",
            hintsUsed = true,
            submittedAt = "2026-04-23T10:15:30Z",
        )

        val payload = serializeRemoteHistory(listOf(remote)).single().jsonObject

        assertEquals(
            setOf(
                "id",
                "user_id",
                "task_id",
                "lexeme_id",
                "lemma",
                "pos",
                "task_type",
                "renderer",
                "device_id",
                "result",
                "submitted_answer",
                "correct_answer",
                "response_ms",
                "cefr_level",
                "hints_used",
                "submitted_at",
            ),
            payload.keys,
        )
    }

    @Test
    fun `remote history select columns match the practice_history schema`() {
        assertEquals(
            "id,user_id,task_id,lexeme_id,lemma,pos,task_type,renderer,device_id,result,submitted_answer,correct_answer,response_ms,cefr_level,hints_used,submitted_at",
            PRACTICE_HISTORY_SELECT_COLUMNS.value,
        )
    }
}

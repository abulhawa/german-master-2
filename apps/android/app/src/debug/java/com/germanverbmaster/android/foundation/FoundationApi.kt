package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URI
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

interface FoundationApi {
    suspend fun createSession(request: SessionRequest): Session
    suspend fun submit(attempt: Attempt): Acknowledgment
}

/** Debug-only public fixture authentication. Use adb reverse tcp:5001 tcp:5001. */
class LocalFoundationApi : FoundationApi {
    private suspend fun post(path: String, body: String): String = withContext(Dispatchers.IO) {
        val connection = URI("http://127.0.0.1:5001$path").toURL().openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Bearer foundation-local-demo")
            connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            check(connection.responseCode == 200) { "Foundation request failed" }
            connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally { connection.disconnect() }
    }
    override suspend fun createSession(request: SessionRequest): Session =
        ContractReader.session(post("/v2/sessions", ContractReader.json.encodeToString(SessionRequest.serializer(), request)))

    override suspend fun submit(attempt: Attempt): Acknowledgment {
        val response = ContractReader.acknowledgments(post("/v2/attempts:batch",
            ContractReader.json.encodeToString(AttemptBatch.serializer(), AttemptBatch("v2", listOf(attempt)))))
        check(response.acknowledgments.size == 1 && response.acknowledgments.single().attemptId == attempt.attemptId) { "Acknowledgment linkage mismatch" }
        return response.acknowledgments.single()
    }
}

fun foundationSessionRequest() = SessionRequest("v2", UUID.randomUUID().toString(), 5,
    listOf("short_answer@1", "choice@1", "cloze@1", "word_order@1", "multi_slot@1"))

fun foundationAttempt(session: Session, index: Int, answer: Answer, assisted: Boolean, deviceId: String) = Attempt(
    UUID.randomUUID().toString(), session.questions[index].id, session.questions[index].exercise.revision,
    deviceId, answer, if (assisted) listOf("hint") else emptyList(), Instant.now().truncatedTo(ChronoUnit.SECONDS).toString(), index
)

fun foundationAnswerText(answer: Answer, exercise: Exercise): String = when (answer) {
    is AnswerShortAnswer -> answer.text
    is AnswerChoice -> (exercise as? ExerciseChoice)?.options?.find { it.id == answer.optionId }?.text.orEmpty()
    is AnswerWordOrder -> answer.tokenIds.joinToString(" ") { id -> (exercise as? ExerciseWordOrder)?.tokens?.find { it.id == id }?.text.orEmpty() }
    is AnswerCloze -> answer.values.joinToString(" · ") { "${(exercise as? ExerciseCloze)?.slots?.find { slot -> slot.id == it.slotId }?.label ?: it.slotId}: ${it.text}" }
    is AnswerMultiSlot -> answer.values.joinToString(" · ") { "${(exercise as? ExerciseMultiSlot)?.slots?.find { slot -> slot.id == it.slotId }?.label ?: it.slotId}: ${it.text}" }
}

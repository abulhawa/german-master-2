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

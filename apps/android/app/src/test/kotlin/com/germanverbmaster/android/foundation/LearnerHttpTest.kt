package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.*
import com.germanverbmaster.android.learner.LocalLearnerApi
import com.germanverbmaster.android.learner.SyncCursorReset
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import org.junit.Assert.*
import org.junit.Test
import java.net.InetSocketAddress

class LearnerHttpTest {
    @Test fun localTransportUsesOwnedV2RoutesAndStrictResponses() = runBlocking {
        val id = "00000000-0000-4000-8000-000000000001"
        val profile = LearnerProfile("v2", 0, false, ProfilePreferences("en", "UTC", "B1", 5))
        val write = ProfileRequest("v2", id, 0, profile.preferences)
        val session = ContractReader.session(requireNotNull(javaClass.classLoader?.getResource("session.json")).readText())
        val attempt = ContractReader.attempts(requireNotNull(javaClass.classLoader?.getResource("attempt-batch.json")).readText()).attempts[0]
        val event = ExposureEvent(id, session.questions[0].id, session.questions[0].exercise.revision, id, "skip", "2026-10-04T10:00:00Z")
        val evaluation = Evaluation("correct", "test", LocalizedText("Server", "Server"), attempt.answer, false)
        val calls = mutableListOf<String>()
        val bodies = mutableListOf<String>()
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/v2/") { exchange ->
            calls.add(exchange.requestMethod + " " + exchange.requestURI.toString())
            bodies.add(exchange.requestBody.bufferedReader().use { it.readText() })
            assertEquals("Bearer foundation-local-demo", exchange.requestHeaders.getFirst("Authorization"))
            assertEquals("no-store", exchange.requestHeaders.getFirst("Cache-Control"))
            val response = when(exchange.requestURI.path) {
                "/v2/sessions" -> ContractReader.json.encodeToString(session)
                "/v2/attempts:batch" -> ContractReader.json.encodeToString(AttemptBatchResponse("v2", listOf(AttemptDuplicate(attempt.attemptId, evaluation, 1))))
                "/v2/exposures:batch" -> ContractReader.json.encodeToString(ExposureBatchResponse("v2", listOf(ExposureDuplicate(event.eventId, 1))))
                "/v2/profile" -> ContractReader.json.encodeToString(profile)
                "/v2/targets" -> ContractReader.json.encodeToString(TargetPage("v2", "2026-10-04T10:00:00Z", emptyList(), "", id))
                "/v2/sync" -> ContractReader.json.encodeToString(SyncPage("v2", emptyList(), id, false))
                else -> ContractReader.json.encodeToString(Catalog("v2", id, "unpublished_local_draft", emptyList(), emptyList()))
            }.toByteArray()
            exchange.sendResponseHeaders(200, response.size.toLong())
            exchange.responseBody.use { it.write(response) }
            exchange.close()
        }
        server.start()
        try {
            val api = LocalLearnerApi(server.address.port)
            assertEquals(profile, api.profile())
            assertEquals(profile, api.save(write))
            api.targets(id); api.catalog()
            assertEquals(listOf("GET /v2/profile", "POST /v2/profile", "GET /v2/targets?cursor=$id", "GET /v2/catalog"), calls)
            assertEquals(ContractReader.json.encodeToString(write), bodies[1])
            val request = foundationSessionRequest()
            assertEquals(session, api.session(request))
            assertEquals(attempt.attemptId, api.submit(attempt).attemptId)
            assertEquals(event.eventId, api.expose(event).eventId)
            assertEquals(listOf("POST /v2/sessions", "POST /v2/attempts:batch", "POST /v2/exposures:batch"), calls.drop(4))
            assertEquals(ContractReader.json.encodeToString(request), bodies[4])
            assertEquals(ContractReader.json.encodeToString(AttemptBatch("v2", listOf(attempt))), bodies[5])
            assertEquals(ContractReader.json.encodeToString(ExposureBatch("v2", listOf(event))), bodies[6])
            val focused = FocusedSessionRequest("v2", id, 1, request.capabilities, TargetFocus(id))
            assertEquals(session, api.session(focused))
            assertEquals("POST /v2/sessions", calls.last())
            assertEquals(ContractReader.json.encodeToString(focused), bodies.last())
            assertEquals(SyncPage("v2", emptyList(), id, false), api.sync(id))
            assertEquals("GET /v2/sync?cursor=$id", calls.last())
        } finally { server.stop(0) }
    }

    @Test fun onlyStrictInvalidCursorOnSyncRequestsSnapshotReset() = runBlocking {
        val id = "00000000-0000-4000-8000-000000000001"
        var status = 400
        var body = ContractReader.json.encodeToString(ApiError("invalid_cursor", "Unavailable", id, false))
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/v2/") { exchange ->
            val bytes = body.toByteArray()
            exchange.sendResponseHeaders(status, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
            exchange.close()
        }
        server.start()
        try {
            val api = LocalLearnerApi(server.address.port)
            assertTrue(runCatching { api.sync(id) }.exceptionOrNull() is SyncCursorReset)
            assertFalse(runCatching { api.targets(id) }.exceptionOrNull() is SyncCursorReset)
            assertFalse(runCatching { api.profile() }.exceptionOrNull() is SyncCursorReset)
            for (otherStatus in listOf(401, 403, 500)) {
                status = otherStatus
                assertFalse(runCatching { api.sync(id) }.exceptionOrNull() is SyncCursorReset)
            }
            status = 400
            for (invalid in listOf("not json", "{\"code\":\"invalid_cursor\"}",
                ContractReader.json.encodeToString(ApiError("invalid_request", "Unavailable", id, false)))) {
                body = invalid
                val error = runCatching { api.sync(id) }.exceptionOrNull()
                assertNotNull(error); assertFalse(error is SyncCursorReset)
            }
            status = 200; body = "{\"apiVersion\":\"v2\"}"
            assertTrue(runCatching { api.sync(id) }.isFailure)
        } finally { server.stop(0) }
    }
}

package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.*
import com.germanverbmaster.android.learner.LocalLearnerApi
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
        val calls = mutableListOf<String>()
        val bodies = mutableListOf<String>()
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/v2/") { exchange ->
            calls.add(exchange.requestMethod + " " + exchange.requestURI.toString())
            bodies.add(exchange.requestBody.bufferedReader().use { it.readText() })
            assertEquals("Bearer foundation-local-demo", exchange.requestHeaders.getFirst("Authorization"))
            assertEquals("no-store", exchange.requestHeaders.getFirst("Cache-Control"))
            val response = when(exchange.requestURI.path) {
                "/v2/profile" -> ContractReader.json.encodeToString(profile)
                "/v2/targets" -> ContractReader.json.encodeToString(TargetPage("v2", "2026-10-04T10:00:00Z", emptyList(), "", id))
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
        } finally { server.stop(0) }
    }
}

package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test

class ContractTest {
    private fun resource(name: String) = requireNotNull(javaClass.classLoader?.getResource(name)).readText()
    @Test fun sharedExposureAcceptanceCorpus() {
        val corpus = ContractReader.json.parseToJsonElement(resource("exposure-conformance.json")).jsonArray
        corpus.forEach { value ->
            val c = value.jsonObject
            val result = runCatching {
                val batch = c.getValue("batch")
                ContractShape.checkExposureBatch(batch)
                val decoded = ContractReader.json.decodeFromString<ExposureBatch>(batch.toString())
                assertEquals(decoded, ContractReader.json.decodeFromString<ExposureBatch>(ContractReader.json.encodeToString(decoded)))
            }
            assertEquals(c.getValue("name").jsonPrimitive.content, c.getValue("valid").jsonPrimitive.boolean, result.isSuccess)
        }
    }
    @Test fun sharedAcceptanceCorpus() {
        val corpus=ContractReader.json.parseToJsonElement(resource("conformance.json")).jsonArray
        corpus.forEach { value ->
            val c=value.jsonObject
            val result=runCatching { ContractReader.session(c.getValue("session").toString()) }
            assertEquals(c.getValue("name").jsonPrimitive.content,c.getValue("valid").jsonPrimitive.boolean,result.isSuccess)
        }
    }
    @Test fun acknowledgmentsRoundTrip() {
        val response=ContractReader.acknowledgments(resource("attempt-response.json"))
        assertEquals(3,response.acknowledgments.size)
        assertEquals(response,ContractReader.acknowledgments(ContractReader.json.encodeToString(response)))
    }
    @Test fun everyAnswerVariantAndSessionRoundTrip() {
        val batch=ContractReader.attempts(resource("attempt-batch.json"))
        assertEquals(5,batch.attempts.size)
        batch.attempts.forEach { assertEquals(it.answer,ContractReader.json.decodeFromString<Answer>(ContractReader.json.encodeToString<Answer>(it.answer))) }
        val session=ContractReader.session(resource("session.json"))
        assertEquals(session,ContractReader.session(ContractReader.json.encodeToString(session)))
    }
}

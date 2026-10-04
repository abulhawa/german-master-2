package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test

class ContractTest {
    private fun resource(name: String) = requireNotNull(javaClass.classLoader?.getResource(name)).readText()
    @Test fun confirmedReadContractsRoundTrip() {
        val raw = ContractReader.json.parseToJsonElement(resource("target-page.json"))
        ContractShape.checkTargetPage(raw)
        val page = ContractReader.json.decodeFromString<TargetPage>(raw.toString())
        assertEquals(page, ContractReader.json.decodeFromString<TargetPage>(ContractReader.json.encodeToString(page)))
        val syncRaw = ContractReader.json.parseToJsonElement(resource("sync-page.json"))
        ContractShape.checkSyncPage(syncRaw)
        val sync = ContractReader.json.decodeFromString<SyncPage>(syncRaw.toString())
        assertEquals(page.targets.first(), sync.changes.first().target)
        assertEquals(sync, ContractReader.json.decodeFromString<SyncPage>(ContractReader.json.encodeToString(sync)))
        listOf("2026-02-30T12:00:00Z", "2026-10-04T12:00:00+02:00", "tomorrow").forEach { instant ->
            val changed = JsonObject(raw.jsonObject + ("generatedAt" to JsonPrimitive(instant)))
            assertTrue(runCatching { ContractReader.json.decodeFromString<TargetPage>(changed.toString()) }.isFailure)
        }
        val extra = JsonObject(raw.jsonObject + ("pending" to JsonPrimitive(true)))
        assertTrue(runCatching { ContractShape.checkTargetPage(extra) }.isFailure)
        val due = JsonObject(raw.jsonObject + ("targets" to JsonArray(listOf(JsonObject(raw.jsonObject.getValue("targets").jsonArray.first().jsonObject + ("state" to JsonPrimitive("due")))))))
        assertTrue(runCatching { ContractReader.json.decodeFromString<TargetPage>(due.toString()) }.isFailure)
    }
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

package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test

class OfflineGraderTest {
    @Test fun sharedGradingConformance() {
        val source = requireNotNull(javaClass.classLoader?.getResource("offline-grading.json")).readText()
        val fixture = ContractReader.json.parseToJsonElement(source).jsonObject
        assertEquals(1, fixture.getValue("fixtureVersion").jsonPrimitive.int)
        val cases = fixture.getValue("cases").jsonArray
        assertTrue(cases.size >= 30)
        cases.forEach { element ->
            val item = element.jsonObject
            val name = item.getValue("name").jsonPrimitive.content
            val exercise = ContractReader.json.decodeFromJsonElement(Exercise.serializer(), item.getValue("exercise"))
            val rubric = ContractReader.json.decodeFromJsonElement(OfflineRubric.serializer(), item.getValue("rubric"))
            val assistance = item.getValue("assistance").jsonArray.map { it.jsonPrimitive.content }
            val expected = item.getValue("expected").jsonObject
            if ("error" in expected) {
                try {
                    OfflineGrader.grade(exercise, rubric, item.getValue("answer"), assistance)
                    fail("$name should reject")
                } catch (failure: OfflineGradingFailure) {
                    assertEquals(name, expected.getValue("error").jsonPrimitive.content, failure.code)
                }
            } else {
                val evaluation = OfflineGrader.grade(exercise, rubric, item.getValue("answer"), assistance)
                assertEquals(name, expected, ContractReader.json.encodeToJsonElement(Evaluation.serializer(), evaluation))
            }
        }
    }
    @Test fun normalizationRequiresKnownPolicy() {
        try { OfflineGrader.normalize("answer", "unknown"); fail("Unknown normalization accepted") }
        catch (failure: OfflineGradingFailure) { assertEquals("unsupported_policy", failure.code) }
    }
}

package com.germanverbmaster.android.data.remote

import io.mockk.mockk
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

class SupabaseLexemeApiTest {

    private val api = SupabaseLexemeApi(mockk())

    @Test
    fun `toEntity maps RemoteLexeme to LexemeEntity correctly`() {
        val remote = RemoteLexeme(
            id = "l1",
            lemma = "machen",
            pos = "V",
            isApproved = true,
            isComplete = true,
            updatedAt = "2023-10-01",
            metadata = JsonObject(mapOf("level" to JsonPrimitive("A1")))
        )

        with(api) {
            val entity = remote.toEntity()
            assertEquals("l1", entity.id)
            assertEquals("machen", entity.lemma)
            assertEquals("V", entity.pos)
            assertEquals(true, entity.isApproved)
            assertEquals(true, entity.isComplete)
            assertEquals("A1", entity.cefrLevel)
            assertEquals("2023-10-01", entity.updatedAt)
        }
    }

    @Test
    fun `toEntity extracts CEFR level from metadata variants`() {
        val metadataVariants = listOf(
            JsonObject(mapOf("level" to JsonPrimitive("A1"))),
            JsonObject(mapOf("cefr_level" to JsonPrimitive("B2"))),
            JsonObject(mapOf("cefr" to JsonPrimitive("B1")))
        )
        val expectedLevels = listOf("A1", "B2", "B1")

        with(api) {
            metadataVariants.forEachIndexed { index, metadata ->
                val remote = RemoteLexeme(id = "id", lemma = "l", pos = "V", metadata = metadata)
                assertEquals(expectedLevels[index], remote.toEntity().cefrLevel)
            }
        }
    }

    @Test
    fun `toEntity handles boolean heuristics in metadata`() {
        // Test case where isApproved/isComplete are null but present in metadata as strings/bools
        val metadata = JsonObject(mapOf(
            "approved" to JsonPrimitive("false")
        ))
        
        val remote = RemoteLexeme(
            id = "id", 
            lemma = "l", 
            pos = "V", 
            isApproved = null, 
            isComplete = null, 
            metadata = metadata
        )

        with(api) {
            val entity = remote.toEntity()
            assertEquals(false, entity.isApproved)
            assertEquals(true, entity.isComplete) // Defaults to true if null in RemoteLexeme
        }
    }
}

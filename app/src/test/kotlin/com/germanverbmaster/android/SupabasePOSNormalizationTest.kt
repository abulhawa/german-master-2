package com.germanverbmaster.android

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.junit.Test
import java.io.File
import java.util.Properties

class SupabasePOSNormalizationTest {

    private val posMapping = mapOf(
        "noun" to "N",
        "verb" to "V",
        "adjective" to "Adj",
        "adverb" to "Adv",
        "preposition" to "Prep",
        "conjunction" to "Conj",
        "pronoun" to "Pron",
        "particle" to "Part",
        "NOMEN" to "N",
        "VERB" to "V",
        "ADJEKTIV" to "Adj",
        "PrΣp" to "Prep",
        "Konj" to "Conj"
    )

    private val sourceIdMapping = mapOf(
        "pos_jsonl:nouns" to "pos_jsonl:N",
        "pos_jsonl:verbs" to "pos_jsonl:V",
        "pos_jsonl:adjectives" to "pos_jsonl:Adj",
        "pos_jsonl:adverbs" to "pos_jsonl:Adv"
    )

    @Test
    fun executeNormalization() = runBlocking {
        val localProps = Properties().apply {
            val f = File("../local.properties")
            if (f.exists()) f.inputStream().use { load(it) }
        }
        val supabaseUrl = localProps.getProperty("supabase.url")
        val serviceRoleKey = localProps.getProperty("supabase.service.role")

        val supabase = createSupabaseClient(supabaseUrl, serviceRoleKey) {
            install(Postgrest)
        }

        val tables = listOf("lexemes", "words", "task_specs", "practice_history")

        for (tableName in tables) {
            println("\n--- Processing Table: $tableName ---")
            try {
                // 1. Normalize 'pos' column
                for ((oldValue, newValue) in posMapping) {
                    try {
                        val result = supabase.from(tableName).update(
                            mapOf("pos" to newValue)
                        ) {
                            filter { eq("pos", oldValue) }
                        }
                        println("  Updated 'pos': $oldValue -> $newValue")
                    } catch (e: Exception) {
                        if (e.message?.contains("column") == true && e.message?.contains("does not exist") == true) {
                            // Column might not exist in this table, skip
                        } else {
                            println("  Error updating 'pos' ($oldValue): ${e.message}")
                        }
                    }
                }

                // 2. Specialized normalization for lexemes.source_ids
                if (tableName == "lexemes") {
                    println("  Checking source_ids for normalization...")
                    val lexemesWithSourceIds = supabase.from("lexemes").select {
                        range(0, 1000) // Doing it in batches if needed, but let's see
                    }.decodeList<JsonObject>()
                    
                    var updateCount = 0
                    for (lexeme in lexemesWithSourceIds) {
                        val id = lexeme["id"]?.jsonPrimitive?.content ?: continue
                        val sourceIds = lexeme["source_ids"]?.jsonArray ?: continue
                        
                        val newSourceIds = sourceIds.map { element ->
                            val content = element.jsonPrimitive.content
                            sourceIdMapping[content] ?: content
                        }
                        
                        if (newSourceIds.toString() != sourceIds.toString()) {
                            supabase.from("lexemes").update(
                                mapOf("source_ids" to newSourceIds)
                            ) {
                                filter { eq("id", id) }
                            }
                            updateCount++
                        }
                    }
                    println("  Updated source_ids for $updateCount rows.")
                }

            } catch (e: Exception) {
                println("  Critical error on table $tableName: ${e.message}")
            }
        }
    }
}

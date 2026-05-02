package com.germanverbmaster.android

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test
import java.io.File
import java.util.Properties

class SupabasePOSNormalizationTest {

    // For the 'words' table (shorthand format)
    private val shorthandPosMapping = mapOf(
        "noun" to "N",
        "verb" to "V",
        "adjective" to "Adj",
        "adverb" to "Adv",
        "preposition" to "Präp",
        "conjunction" to "Konj",
        "pronoun" to "Pron",
        "determiner" to "Det",
        "particle" to "Part",
        "interjection" to "Interj",
        "numeral" to "Num"
    )

    // For 'lexemes', 'task_specs', 'practice_history' (canonical format)
    private val canonicalPosMapping = mapOf(
        "v" to "verb",
        "n" to "noun",
        "adj" to "adjective",
        "adv" to "adverb",
        "prep" to "preposition",
        "konj" to "conjunction",
        "pron" to "pronoun",
        "det" to "determiner",
        "part" to "particle",
        "int" to "interjection",
        "num" to "numeral",
        "V" to "verb",
        "N" to "noun",
        "Adj" to "adjective",
        "Adv" to "adverb",
        "Präp" to "preposition",
        "Konj" to "conjunction",
        "Pron" to "pronoun",
        "Det" to "determiner",
        "Part" to "particle",
        "Interj" to "interjection",
        "Num" to "numeral"
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

        if (supabaseUrl.isNullOrBlank() || serviceRoleKey.isNullOrBlank()) {
            println("Skipping normalization: supabase.url or supabase.service.role missing in local.properties")
            return@runBlocking
        }

        val supabase = createSupabaseClient(supabaseUrl, serviceRoleKey) {
            install(Postgrest)
        }

        val tables = listOf("lexemes", "words", "task_specs", "practice_history")

        for (tableName in tables) {
            println("\n--- Processing Table: $tableName ---")
            val mapping = if (tableName == "words") shorthandPosMapping else canonicalPosMapping
            
            try {
                // 1. Normalize 'pos' column
                for ((oldValue, newValue) in mapping) {
                    try {
                        supabase.from(tableName).update(
                            mapOf("pos" to newValue)
                        ) {
                            filter { eq("pos", oldValue) }
                        }
                        println("  Updated '$tableName.pos': $oldValue -> $newValue")
                    } catch (e: Exception) {
                        if (e.message?.contains("column") == true && e.message?.contains("does not exist") == true) {
                            // Column might not exist in this table, skip
                        } else {
                            println("  Error updating '$tableName.pos' ($oldValue): ${e.message}")
                        }
                    }
                }

                // 2. Specialized normalization for lexemes.source_ids
                if (tableName == "lexemes") {
                    println("  Checking source_ids for normalization...")
                    val lexemesWithSourceIds = supabase.from("lexemes").select {
                        range(0, 1000)
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

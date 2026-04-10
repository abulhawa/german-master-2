package com.germanverbmaster.android.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    indices = [Index(value = ["pos"]), Index(value = ["level"])],
)
data class WordEntity(
    @PrimaryKey val id: Int,
    val lemma: String,
    val pos: String,            // "V" | "N" | "Adj"
    val level: String?,         // "A1" | "A2" | "B1" | "B2" | "C1"
    val english: String?,
    val exampleDe: String? = null,
    val exampleEn: String? = null,
    val gender: String? = null,        // "m" | "f" | "n" — for nouns
    val plural: String? = null,        // for nouns
    val separable: Boolean? = null,    // for verbs
    val aux: String? = null,           // for verbs: "haben", "sein", "haben / sein"
    val praeteritum: String? = null,   // for verbs
    val partizip2: String? = null,     // for verbs
    val perfekt: String? = null,       // for verbs
    val praesensIch: String? = null,   // for verbs
    val praesensEr: String? = null,    // for verbs
    val comparative: String? = null,   // for adjectives
    val superlative: String? = null,   // for adjectives
    val updatedAt: String = "",
)

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
    val exampleDe: String?,
    val exampleEn: String?,
    val gender: String?,        // "m" | "f" | "n" — for nouns
    val updatedAt: String = "",
)

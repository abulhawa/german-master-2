package com.germanverbmaster.android.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lexemes",
    indices = [Index(value = ["lemma", "pos"], unique = true)]
)
data class LexemeEntity(
    @PrimaryKey val id: String,
    val lemma: String,
    val language: String = "de",
    val pos: String,           // "V" | "N" | "Adj"
    val gender: String? = null,
    val metadataJson: String = "{}",   // serialized JSON
    val frequencyRank: Int? = null,
    val sourceIdsJson: String = "[]",  // serialized JSON array
    val updatedAt: String = "",
)

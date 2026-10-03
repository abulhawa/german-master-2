package com.germanverbmaster.android.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lexemes",
    indices = [Index(value = ["lemma", "pos"])] // Removed unique = true
)
data class LexemeEntity(
    @PrimaryKey val id: String,
    val lemma: String,
    val language: String = "de",
    val pos: String,           // "V" | "N" | "Adj"
    val gender: String? = null,
    val metadataJson: String = "{}",
    val cefrLevel: String? = null,
    val frequencyRank: Int? = null,
    val sourceIdsJson: String = "[]",
    val collectionsJson: String = "[]",
    val updatedAt: String = "",
    val isApproved: Boolean = false,
    val isComplete: Boolean = false
)

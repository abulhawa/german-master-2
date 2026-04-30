package com.germanverbmaster.android.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// task_type values include "vocabulary_drill", "conjugate_form", "noun_case_declension", "adj_ending"
@Entity(
    tableName = "task_specs",
    foreignKeys = [ForeignKey(
        entity = LexemeEntity::class,
        parentColumns = ["id"],
        childColumns = ["lexemeId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("lexemeId"), Index("pos"), Index("taskType")],
)
data class TaskSpecEntity(
    @PrimaryKey val id: String,
    val lexemeId: String,
    val pos: String,
    val taskType: String,
    val renderer: String,
    val promptJson: String,    // serialized JSON
    val solutionJson: String,  // serialized JSON
    val hintsJson: String? = null,
    val metadataJson: String? = null,
    val cefrLevel: String? = null,  // extracted from lexeme metadata for fast filtering
    val revision: Int = 1,
    val updatedAt: String = "",
)

package com.germanverbmaster.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practice_history")
data class PracticeHistoryEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val taskId: String,
    val lexemeId: String,
    val lemma: String = "",    // Added to avoid complex joins for history list
    val pos: String,           // "V" | "N" | "Adj"
    val taskType: String,
    val renderer: String = "default", // Matches the renderer in task_specs
    val result: String,        // "correct" | "incorrect"
    val submittedAnswer: String = "",
    val correctAnswer: String = "",
    val responseMs: Int,
    val cefrLevel: String? = null,
    val hintsUsed: Boolean = false,
    val submittedAt: String,
    val synced: Boolean = false,
)

package com.germanverbmaster.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practice_history")
data class PracticeHistoryEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val taskId: String,
    val lexemeId: String,
    val pos: String,
    val taskType: String,
    val result: String,        // "correct" | "incorrect"
    val responseMs: Int,
    val cefrLevel: String? = null,
    val hintsUsed: Boolean = false,
    val submittedAt: String,
    val synced: Boolean = false,   // false until successfully written to Supabase
)

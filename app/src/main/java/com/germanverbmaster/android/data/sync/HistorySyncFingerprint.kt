package com.germanverbmaster.android.data.sync

data class HistorySyncFingerprint(
    val userId: String,
    val taskId: String,
    val lexemeId: String,
    val taskType: String,
    val result: String,
    val submittedAt: String,
)

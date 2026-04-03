package com.germanverbmaster.android.domain.model

/** Clean domain model passed to ViewModels — decoupled from Room entities */
data class TaskCard(
    val taskId: String,
    val lexemeId: String,
    val lemma: String,
    val pos: String,           // "V" | "N" | "Adj"
    val taskType: String,      // "conjugate_form" | "noun_case_declension" | "adj_ending"
    val renderer: String,
    val cefrLevel: String?,
    val prompt: Map<String, String>,    // parsed from JSON
    val solution: Map<String, String>,  // parsed from JSON
    val hints: List<String> = emptyList(),
)

data class PracticeResult(
    val taskId: String,
    val lexemeId: String,
    val pos: String,
    val taskType: String,
    val result: String,         // "correct" | "incorrect"
    val responseMs: Int,
    val cefrLevel: String?,
    val hintsUsed: Boolean = false,
)

enum class PracticeMode { ALL, VERBS, NOUNS, ADJECTIVES, B2_EXAM }

data class SessionStats(
    val correct: Int = 0,
    val incorrect: Int = 0,
    val skipped: Int = 0,
) {
    val total get() = correct + incorrect
    val accuracy get() = if (total == 0) 0f else correct.toFloat() / total * 100f
}

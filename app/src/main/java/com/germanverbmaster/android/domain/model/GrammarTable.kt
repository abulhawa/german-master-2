package com.germanverbmaster.android.domain.model

/** Represents a structured grammar reference table */
data class GrammarTable(
    val title: String,
    val note: String,
    val headers: List<String>,
    val rows: List<List<String>>,
    val examples: List<String> = emptyList(),
    val rule: String = "",
    val mistakes: List<String> = emptyList(),
    val extraSections: List<GrammarSection> = emptyList(),
    val coverageItems: List<String> = emptyList(),
    val useListLayout: Boolean = false
)

/** Optional focused subsection inside a grammar reference card. */
data class GrammarSection(
    val title: String,
    val items: List<String>
)

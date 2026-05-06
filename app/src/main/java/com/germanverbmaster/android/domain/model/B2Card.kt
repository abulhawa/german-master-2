package com.germanverbmaster.android.domain.model

/** Represents one B2 flashcard — static content, not from Supabase */
data class B2Card(
    val id: String,
    val category: B2Category,
    val front: String,
    val back: String,
    val preposition: String? = null,   // for Verben+Präp
    val example: String,
    val topic: String? = null,         // sub-topic label
)

enum class B2Category(val label: String) {
    ALL("Alle"),
    VERBEN_PRAEP("Verben + Präp"),
    NOMEN_VERB("Nomen-Verb"),
    REDEMITTEL("Redemittel"),
}

enum class CardMode(val label: String) {
    DE_TO_EN("DE → EN"),
    EN_TO_DE("EN → DE"),
    EXAMPLE("Beispielsatz"),
}

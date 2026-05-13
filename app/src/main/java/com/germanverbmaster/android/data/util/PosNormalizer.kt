package com.germanverbmaster.android.data.util

object PosNormalizer {
    /**
     * Normalizes raw POS strings to the compact POS tags accepted by the
     * shared Supabase schema used by the web and Android apps.
     */
    fun normalize(raw: String): String {
        val trimmed = raw.trim()
        return when (trimmed.lowercase()) {
            "v", "verb", "verbs", "verben" -> "V"
            "n", "noun", "nouns", "nomen", "substantiv", "subst", "propn", "propernoun" -> "N"
            "adj", "adjective", "adjectives", "adjektiv", "num", "numeral", "numerale", "zahlwort" -> "Adj"
            "adv", "adverb" -> "Adv"
            "prep", "adp", "preposition", "praep", "praeposition", "pr\u00e4p", "pr\u00e4position" -> "Pr\u00e4p"
            "conj", "konj", "conjunction", "konjunktion", "cconj", "sconj" -> "Konj"
            "pron", "pronoun", "pronomen", "det", "determiner", "art", "article", "artikel" -> "Pron"
            "part", "particle", "partikel", "int", "intj", "interj", "interjection", "interjektion" -> "Part"
            else -> trimmed
        }
    }
}

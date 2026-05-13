package com.germanverbmaster.android.data.sync

object PartOfSpeechMapper {
    /**
     * Maps Android and legacy POS values to the compact tags accepted by Supabase history tables.
     */
    fun toCanonical(pos: String): String {
        return when (pos.trim().lowercase()) {
            "v", "verb", "verbs", "verben" -> "V"
            "n", "noun", "nouns", "nomen", "substantiv", "subst", "propn", "propernoun" -> "N"
            "adj", "adjective", "adjectives", "adjektiv", "num", "numeral", "numerale", "zahlwort" -> "Adj"
            "adv", "adverb" -> "Adv"
            "pron", "pronoun", "pronomen", "det", "determiner", "art", "article", "artikel" -> "Pron"
            "prep", "preposition", "präp", "prÃ¤p", "praep", "praeposition", "präposition", "prÃ¤position", "adp" -> "Präp"
            "konj", "conjunction", "conj", "konjunktion", "cconj", "sconj" -> "Konj"
            "part", "particle", "partikel", "int", "interj", "interjection", "interjektion" -> "Part"
            else -> pos.trim()
        }
    }
}

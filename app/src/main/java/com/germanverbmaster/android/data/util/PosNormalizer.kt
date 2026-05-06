package com.germanverbmaster.android.data.util

object PosNormalizer {
    /**
     * Normalizes raw POS strings to compact internal tags:
     * V, N, Adj, Adv, Präp, Konj, Pron, Part, Int, Art, Num
     */
    fun normalize(raw: String): String {
        val upper = raw.trim().uppercase()
        return when (upper) {
            "V", "VERB" -> "V"
            "N", "NOMEN", "NOUN" -> "N"
            "ADJ", "ADJEKTIV", "ADJECTIVE" -> "Adj"
            "ADV", "ADVERB" -> "Adv"
            "PREP", "PRÄP", "PRÄPOSITION", "PRÄPOSITIONEN", "PREPOSITION", "PRAEP" -> "Präp"
            "CONJ", "KONJ", "KONJUNKTION", "CONJUNCTION" -> "Konj"
            "PRON", "PRONOMEN", "PRONOUN" -> "Pron"
            "PART", "PARTIKEL", "PARTICLE" -> "Part"
            "INT", "INTERJEKTION", "INTERJECTION" -> "Int"
            "ART", "ARTIKEL", "DETERMINER", "DET" -> "Art"
            "NUM", "NUMERALE", "NUMERAL" -> "Num"
            else -> raw
        }
    }
}

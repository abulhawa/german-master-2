package com.germanverbmaster.android.data.sync

object PartOfSpeechMapper {
    /**
     * Maps internal Android shorthand POS codes to the canonical names expected by the server.
     * The server enforces a check constraint:
     * ('verb', 'noun', 'adjective', 'adverb', 'pronoun', 'determiner', 'preposition', 'conjunction', 'numeral', 'particle', 'interjection')
     */
    fun toCanonical(pos: String): String {
        return when (pos.lowercase()) {
            "v", "verb" -> "verb"
            "n", "noun" -> "noun"
            "adj", "adjective" -> "adjective"
            "adv", "adverb" -> "adverb"
            "pron", "pronoun" -> "pronoun"
            "det", "determiner", "art" -> "determiner"
            "prep", "preposition", "präp", "praep" -> "preposition"
            "konj", "conjunction", "conj" -> "conjunction"
            "num", "numeral" -> "numeral"
            "part", "particle" -> "particle"
            "int", "interj", "interjection" -> "interjection"
            else -> pos.lowercase()
        }
    }
}

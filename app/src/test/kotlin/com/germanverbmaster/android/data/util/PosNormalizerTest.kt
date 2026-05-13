package com.germanverbmaster.android.data.util

import org.junit.Assert.assertEquals
import org.junit.Test

class PosNormalizerTest {

    @Test
    fun `normalize only emits POS values accepted by shared Supabase schema`() {
        val expected = setOf("Pr\u00e4p", "Pron", "V", "Adv", "N", "Konj", "Adj", "Part")
        val rawValues = listOf(
            "verb",
            "noun",
            "adjective",
            "adverb",
            "preposition",
            "conjunction",
            "pronoun",
            "determiner",
            "article",
            "numeral",
            "interjection",
            "particle",
        )

        val normalized = rawValues.map(PosNormalizer::normalize).toSet()

        check(normalized.all { it in expected }) {
            "Unexpected POS values emitted: ${normalized - expected}"
        }
    }

    @Test
    fun `normalize collapses categories removed from the shared schema`() {
        assertEquals("Pron", PosNormalizer.normalize("Art"))
        assertEquals("Pron", PosNormalizer.normalize("Det"))
        assertEquals("Adj", PosNormalizer.normalize("Num"))
        assertEquals("Part", PosNormalizer.normalize("Int"))
        assertEquals("Part", PosNormalizer.normalize("Interj"))
    }
}

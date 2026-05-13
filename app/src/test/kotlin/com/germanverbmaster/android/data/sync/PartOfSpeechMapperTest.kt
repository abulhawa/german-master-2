package com.germanverbmaster.android.data.sync

import org.junit.Assert.assertEquals
import org.junit.Test

class PartOfSpeechMapperTest {

    @Test
    fun `toCanonical maps legacy names to Supabase compact POS tags`() {
        assertEquals("V", PartOfSpeechMapper.toCanonical("verb"))
        assertEquals("N", PartOfSpeechMapper.toCanonical("noun"))
        assertEquals("Adj", PartOfSpeechMapper.toCanonical("adjective"))
        assertEquals("Adv", PartOfSpeechMapper.toCanonical("adverb"))
        assertEquals("Pron", PartOfSpeechMapper.toCanonical("determiner"))
        assertEquals("Präp", PartOfSpeechMapper.toCanonical("preposition"))
        assertEquals("Konj", PartOfSpeechMapper.toCanonical("conjunction"))
        assertEquals("Adj", PartOfSpeechMapper.toCanonical("numeral"))
        assertEquals("Part", PartOfSpeechMapper.toCanonical("interjection"))
    }

    @Test
    fun `toCanonical keeps compact POS tags accepted by Supabase`() {
        assertEquals("V", PartOfSpeechMapper.toCanonical("V"))
        assertEquals("N", PartOfSpeechMapper.toCanonical("N"))
        assertEquals("Adj", PartOfSpeechMapper.toCanonical("Adj"))
        assertEquals("Präp", PartOfSpeechMapper.toCanonical("Präp"))
    }
}

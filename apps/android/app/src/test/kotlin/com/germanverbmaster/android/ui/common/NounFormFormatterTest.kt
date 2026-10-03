package com.germanverbmaster.android.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test

class NounFormFormatterTest {

    @Test
    fun `present expands slash-in nouns for display and keeps tts singular`() {
        val presentation = NounFormFormatter.present(
            lemma = "Lehrer/in",
            gender = "der/die",
            plural = "-/-nen",
        )

        assertEquals("der/die Lehrer/in", presentation.singularDisplay)
        assertEquals("-, nen", presentation.pluralDisplay)
        assertEquals("der Lehrer", presentation.speakText)
        assertEquals("der/die", presentation.genderDisplay)
    }

    @Test
    fun `present handles uppercase or spaced slash-in notation`() {
        val presentation = NounFormFormatter.present(
            lemma = "Schüler /In",
            gender = "der/die",
            plural = "-/-nen",
        )

        assertEquals("der/die Schüler/in", presentation.singularDisplay)
        assertEquals("-, nen", presentation.pluralDisplay)
        assertEquals("der Schüler", presentation.speakText)
    }

    @Test
    fun `present keeps compact plural marker for regular nouns and skips plural in tts`() {
        val presentation = NounFormFormatter.present(
            lemma = "Antrag",
            gender = "m",
            plural = "-e",
        )

        assertEquals("der Antrag", presentation.singularDisplay)
        assertEquals("-e", presentation.pluralDisplay)
        assertEquals("der Antrag", presentation.speakText)
        assertEquals("der", presentation.genderDisplay)
    }

    @Test
    fun `present converts full plural to ending when possible`() {
        val presentation = NounFormFormatter.present(
            lemma = "Personal",
            gender = "das",
            plural = "Personale",
        )

        assertEquals("das Personal", presentation.singularDisplay)
        assertEquals("-e", presentation.pluralDisplay)
        assertEquals("das Personal", presentation.speakText)
    }

    @Test
    fun `present uses base lemma when plural marker means unchanged`() {
        val presentation = NounFormFormatter.present(
            lemma = "Ratgeber",
            gender = "der",
            plural = "-",
        )

        assertEquals("der Ratgeber", presentation.singularDisplay)
        assertEquals("-", presentation.pluralDisplay)
        assertEquals("der Ratgeber", presentation.speakText)
    }
}

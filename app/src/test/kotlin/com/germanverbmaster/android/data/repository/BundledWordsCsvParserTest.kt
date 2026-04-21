package com.germanverbmaster.android.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BundledWordsCsvParserTest {

    @Test
    fun `parse maps rows with examples and noun metadata`() {
        val csv = """
            Article/Prefix,Word,English Translation,Example Sentence,English Translation (Sentence),POS
            der,"Antrag, -e",application,Ich stelle heute einen Antrag.,I am submitting an application today.,N
            ,arbeitslos,unemployed,"Nach der Kuendigung war er, leider, arbeitslos.","After the dismissal, he was unfortunately unemployed.",ADJ
        """.trimIndent()

        val parsed = BundledWordsCsvParser.parse(
            csvText = csv,
            level = "B2 Beruf",
            versionTag = "test-version",
        )

        assertEquals(2, parsed.size)

        val noun = parsed[0]
        assertEquals(2_000_001, noun.id)
        assertEquals("Antrag", noun.lemma)
        assertEquals("N", noun.pos)
        assertEquals("m", noun.gender)
        assertEquals("-e", noun.plural)
        assertEquals("B2 Beruf", noun.level)
        assertEquals("Ich stelle heute einen Antrag.", noun.exampleDe)
        assertEquals("I am submitting an application today.", noun.exampleEn)
        assertEquals("test-version", noun.updatedAt)

        val adjective = parsed[1]
        assertEquals("Adj", adjective.pos)
        assertNull(adjective.gender)
        assertNull(adjective.plural)
    }

    @Test
    fun `parse tolerates extra commas in english translation and normalizes pos variants`() {
        val csv = """
            Article/Prefix,Word,English Translation,Example Sentence,English Translation (Sentence),POS
            ,sich herausstellen,turn out, prove,"Es stellte sich heraus, dass die Aussage stimmt.",It turned out that the statement is correct.,VERB
            ,ohne,,Dieses Wort hat keine Uebersetzung,This row should be skipped,V
            ,Analysen,analyses,Wir erstellen taeglich Analysen.,We create analyses every day.,NOUN
        """.trimIndent()

        val parsed = BundledWordsCsvParser.parse(
            csvText = csv,
            level = "B2 Beruf",
            versionTag = "test-version",
        )

        assertEquals(2, parsed.size)
        assertEquals("V", parsed[0].pos)
        assertEquals("turn out,prove", parsed[0].english)
        assertEquals("N", parsed[1].pos)
    }
}

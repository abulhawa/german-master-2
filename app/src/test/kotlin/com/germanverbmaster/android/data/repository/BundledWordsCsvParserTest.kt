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

    @Test
    fun `parse keeps duplicate lemma-pos rows when examples differ`() {
        val csv = """
            Article/Prefix,Word,English Translation,Example Sentence,English Translation (Sentence),POS
            die,"Bescheinigung, -en",certificate(s),Bitte reichen Sie die Bescheinigung ein.,Please submit the certificate.,N
            die,"Bescheinigung, -en",certificate(s),Fuer diesen Kurs erhalten Sie eine Bescheinigung.,You will receive a certificate for this course.,N
            die,"Bescheinigung, -en",certificate(s),Bitte reichen Sie die Bescheinigung ein.,Please submit the certificate.,N
        """.trimIndent()

        val parsed = BundledWordsCsvParser.parse(
            csvText = csv,
            level = "B2 Beruf",
            versionTag = "test-version",
        )

        assertEquals(2, parsed.size)
        assertEquals("Bitte reichen Sie die Bescheinigung ein.", parsed[0].exampleDe)
        assertEquals("Fuer diesen Kurs erhalten Sie eine Bescheinigung.", parsed[1].exampleDe)
    }

    @Test
    fun `parse preserves multiple noun articles from article prefix`() {
        val csv = """
            Article/Prefix,Word,English Translation,Example Sentence,English Translation (Sentence),POS
            der/die,"Entwickler/in, -/-nen",developer,Die Entwickler arbeiten im Team.,Developers work in teams.,N
            der/das,"Fakt, -en",fact,Das ist ein wichtiger Fakt.,That is an important fact.,N
        """.trimIndent()

        val parsed = BundledWordsCsvParser.parse(
            csvText = csv,
            level = "B2 Beruf",
            versionTag = "test-version",
        )

        assertEquals(2, parsed.size)
        assertEquals("der/die", parsed[0].gender)
        assertEquals("der/das", parsed[1].gender)
    }
}

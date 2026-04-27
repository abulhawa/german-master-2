package com.germanverbmaster.android.ui.b2practice

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class B2GrammarContentTest {
    @Test
    fun grammarTables_includeRequestedB2EnrichmentTopics() {
        val titles = B2ContentData.grammarTables.map { it.title }

        assertTrue(titles.contains("Adjektivdeklination: Ohne Artikel"))
        assertTrue(titles.contains("Infinitiv mit zu / ohne zu"))
        assertTrue(titles.contains("Partizip I & II als Adjektive"))
        assertTrue(titles.contains("Nominalisierung"))
        assertTrue(titles.contains("Subjektive Bedeutung der Modalverben"))
        assertTrue(titles.contains("Rektion von Adjektiven"))
        assertTrue(titles.contains("Wortstellung im Mittelfeld"))
    }

    @Test
    fun grammarTables_haveRuleExampleMistakeContent() {
        B2ContentData.grammarTables.forEach { table ->
            assertTrue("${table.title} should have a rule", table.rule.isNotBlank())
            assertTrue("${table.title} should have examples", table.examples.isNotEmpty())
            assertTrue("${table.title} should have mistake notes", table.mistakes.isNotEmpty())
        }
    }

    @Test
    fun b2Content_hasLocalizedTypoFixes() {
        val allText = buildString {
            B2ContentData.allCards.forEach { card ->
                appendLine(card.front)
                appendLine(card.back)
                appendLine(card.preposition.orEmpty())
                appendLine(card.example)
                appendLine(card.topic.orEmpty())
            }
            B2ContentData.grammarTables.forEach { table ->
                appendLine(table.title)
                appendLine(table.note)
                appendLine(table.rule)
                table.rows.flatten().forEach(::appendLine)
                table.examples.forEach(::appendLine)
                table.mistakes.forEach(::appendLine)
                table.extraSections.forEach { section ->
                    appendLine(section.title)
                    section.items.forEach(::appendLine)
                }
            }
        }

        assertFalse(allText.contains("Rückschrift"))
        assertFalse(allText.contains("bezüglich / betreff,"))
        assertFalse(allText.contains("Ich wäre gerne arbeitsfähig."))
        assertTrue(allText.contains("Rücksicht auf Kollegen"))
        assertTrue(allText.contains("bezüglich / betreffend"))
    }
}

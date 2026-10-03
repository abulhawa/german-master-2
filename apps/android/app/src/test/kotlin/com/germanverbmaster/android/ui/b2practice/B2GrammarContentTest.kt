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
        assertTrue(titles.contains("werden, worden, geworden & Passiversatzformen"))
    }

    @Test
    fun grammarTables_haveRuleExampleMistakeContent() {
        B2ContentData.grammarTables.forEach { table ->
            assertTrue("${table.title} should have a rule", table.rule.isNotBlank())
            assertTrue("${table.title} should have examples", table.examples.isNotEmpty())
            assertTrue("${table.title} should have mistake notes", table.mistakes.isNotEmpty())
            assertTrue("${table.title} should declare coverage items", table.coverageItems.isNotEmpty())
        }
    }

    @Test
    fun grammarTables_coverTheirDeclaredCoverageItems() {
        B2ContentData.grammarTables.forEach { table ->
            val searchableContent = buildString {
                table.headers.forEach(::appendLine)
                table.rows.flatten().forEach(::appendLine)
                table.examples.forEach(::appendLine)
                appendLine(table.rule)
                table.mistakes.forEach(::appendLine)
                table.extraSections.forEach { section ->
                    appendLine(section.title)
                    section.items.forEach(::appendLine)
                }
            }

            table.coverageItems.forEach { item ->
                assertTrue(
                    "${table.title} should represent coverage item $item",
                    searchableContent.contains(item)
                )
            }
        }
    }

    @Test
    fun grammarTables_haveEnoughLearnerFacingExamplesForCoverage() {
        B2ContentData.grammarTables.forEach { table ->
            val learnerFacingExampleCount = table.examples.size +
                table.extraSections.sumOf { it.items.size }

            assertTrue(
                "${table.title} should have enough examples for its coverage items",
                learnerFacingExampleCount >= table.coverageItems.size
            )
        }
    }

    @Test
    fun adjectiveDeclensionTables_coverEveryGenderAndCaseCombination() {
        val requiredLabels = listOf(
            "M+Nom", "M+Akk", "M+Dat", "M+Gen",
            "F+Nom", "F+Akk", "F+Dat", "F+Gen",
            "N+Nom", "N+Akk", "N+Dat", "N+Gen",
            "Pl+Nom", "Pl+Akk", "Pl+Dat", "Pl+Gen"
        )

        B2ContentData.grammarTables
            .filter { it.title.startsWith("Adjektivdeklination") }
            .forEach { table ->
                requiredLabels.forEach { label ->
                    assertTrue(
                        "${table.title} should include an example for $label",
                        table.examples.any { it.startsWith("$label:") }
                    )
                }
            }
    }

    @Test
    fun connectorTable_coversB2BerufConnectorFunctions() {
        val table = B2ContentData.grammarTables.first { it.title == "Satzverbindungen (Konnektoren)" }
        val allText = buildString {
            table.rows.flatten().forEach(::appendLine)
            table.examples.forEach(::appendLine)
            table.mistakes.forEach(::appendLine)
            table.extraSections.forEach { section ->
                appendLine(section.title)
                section.items.forEach(::appendLine)
            }
        }
        val requiredConnectors = listOf(
            "aber", "denn", "sondern",
            "weil", "da", "zumal",
            "obwohl", "auch wenn", "während",
            "wenn", "falls", "sofern",
            "bevor", "nachdem", "sobald", "seitdem", "bis",
            "damit", "sodass", "um ... zu",
            "deshalb", "deswegen", "daher", "darum", "folglich", "somit",
            "trotzdem", "dennoch", "allerdings", "jedoch",
            "außerdem", "zudem", "darüber hinaus",
            "zuerst", "anschließend", "danach", "inzwischen", "schließlich",
            "wegen", "aufgrund", "trotz", "infolge",
            "je ... desto", "entweder ... oder", "weder ... noch",
            "sowohl ... als auch", "nicht nur ... sondern auch",
            "andernfalls"
        )

        requiredConnectors.forEach { connector ->
            assertTrue(
                "Connector table should include $connector",
                allText.contains(connector)
            )
        }
    }

    @Test
    fun passiveAndWerdenTables_coverCommonConfusionPoints() {
        val passiveText = grammarTableText("Passiv (Vorgangspassiv)")
        val werdenText = grammarTableText("werden, worden, geworden & Passiversatzformen")

        listOf(
            "Vorgangspassiv",
            "Zustandspassiv",
            "Futur",
            "werden + Partizip II",
            "werden + Infinitiv",
            "sein + Partizip II",
            "worden",
            "geworden",
            "sich lassen + Infinitiv",
            "sein + zu + Infinitiv",
            "-bar/-lich",
            "Passiversatz"
        ).forEach { item ->
            assertTrue(
                "Passive/werden content should include $item",
                "$passiveText\n$werdenText".contains(item)
            )
        }
    }

    @Test
    fun lassenPassiversatzExamples_useVariedConjugations() {
        val werdenText = grammarTableText("werden, worden, geworden & Passiversatzformen")

        listOf(
            "lässt sich",
            "lassen sich",
            "ließ sich",
            "ließen sich",
            "hat sich",
            "lassen.",
            "müssen sich"
        ).forEach { form ->
            assertTrue(
                "Lassen passiversatz examples should include $form",
                werdenText.contains(form)
            )
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

    private fun grammarTableText(title: String): String {
        val table = B2ContentData.grammarTables.first { it.title == title }
        return buildString {
            appendLine(table.title)
            appendLine(table.note)
            appendLine(table.rule)
            table.headers.forEach(::appendLine)
            table.rows.flatten().forEach(::appendLine)
            table.examples.forEach(::appendLine)
            table.mistakes.forEach(::appendLine)
            table.extraSections.forEach { section ->
                appendLine(section.title)
                section.items.forEach(::appendLine)
            }
            table.coverageItems.forEach(::appendLine)
        }
    }
}

package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.learner.*
import com.germanverbmaster.android.ui.b2practice.B2ContentData
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class GrammarReferenceContentTest {
    @Test fun sharedReferencePreservesEveryLegacyTableRuleAndExample() {
        val data=ContractReader.json.decodeFromString<GrammarReferenceData>(File(System.getProperty("gm.repoRoot"),"content/reference/grammar-reference.json").readText())
        assertEquals(14,data.topics.size)
        assertEquals(3,data.topics.first().tables.size)
        val expected=B2ContentData.grammarTables.map {t->GrammarReferenceTable(t.title,t.note,t.headers,t.rows,t.examples,t.rule,t.mistakes,t.extraSections.map {GrammarReferenceSection(it.title,it.items)},t.coverageItems,t.useListLayout)}
        assertEquals(expected,data.topics.flatMap {it.tables})
    }
}

package com.germanverbmaster.android.learner

import android.content.Context
import kotlinx.serialization.Serializable
import com.germanverbmaster.android.foundation.ContractReader

@Serializable data class GrammarReferenceText(val en:String,val de:String)
@Serializable data class GrammarReferenceSection(val title:String,val items:List<String>)
@Serializable data class GrammarReferenceTable(
    val title:String,val note:String,val headers:List<String>,val rows:List<List<String>>,
    val examples:List<String> = emptyList(),val rule:String = "",val mistakes:List<String> = emptyList(),
    val extraSections:List<GrammarReferenceSection> = emptyList(),val coverageItems:List<String> = emptyList(),val useListLayout:Boolean=false
)
@Serializable data class GrammarReferenceTopic(val id:String,val title:GrammarReferenceText,val tables:List<GrammarReferenceTable>)
@Serializable data class GrammarReferenceData(val schemaVersion:Int,val contentLanguage:String,val topics:List<GrammarReferenceTopic>)
internal fun grammarSearchText(topic:GrammarReferenceTopic):String =
    (listOf(topic.title.en,topic.title.de)+topic.tables.flatMap {t->listOf(t.title,t.note)+t.headers+t.rows.flatten()+t.examples+listOf(t.rule)+t.mistakes+t.extraSections.flatMap {listOf(it.title)+it.items}+t.coverageItems})
        .joinToString("\n").lowercase(java.util.Locale.GERMAN)
internal fun readGrammarReference(context:Context):GrammarReferenceData =
    ContractReader.json.decodeFromString<GrammarReferenceData>(context.assets.open("grammar-reference.json").bufferedReader().use {it.readText()})
        .also {require(it.schemaVersion==1 && it.contentLanguage=="de")}

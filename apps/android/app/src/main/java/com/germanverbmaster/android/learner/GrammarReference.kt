package com.germanverbmaster.android.learner

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp

/** Bundled read-only reference, independent of authentication and practice state. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GrammarReferenceButton(german:Boolean,enabled:Boolean=true) {
    val context=LocalContext.current
    val data=remember(context) {readGrammarReference(context)}
    var open by rememberSaveable {mutableStateOf(false)}
    var selected by rememberSaveable {mutableStateOf<String?>(null)}
    var query by rememberSaveable {mutableStateOf("")}
    fun label(en:String,de:String)=if(german)de else en
    OutlinedButton(onClick={selected=null;query="";open=true},enabled=enabled,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {
        Icon(Icons.AutoMirrored.Outlined.MenuBook,contentDescription=null,modifier=Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp));Text(label("Grammar","Grammatik"))
    }
    if(open) ModalBottomSheet(onDismissRequest={open=false},sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),
        containerColor=MaterialTheme.colorScheme.background,modifier=Modifier.fillMaxHeight(0.94f)) {
        Row(Modifier.fillMaxWidth().padding(horizontal=20.dp),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
            Text(label("Grammar reference","Grammatik-Referenz"),style=MaterialTheme.typography.titleMedium,modifier=Modifier.weight(1f).semantics {heading()})
            IconButton(onClick={open=false}){Icon(Icons.Outlined.Close,contentDescription=label("Close reference","Referenz schließen"))}
        }
        HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant)
        val topic=data.topics.find {it.id==selected}
        key(selected) {
            LazyColumn(Modifier.fillMaxWidth().weight(1f),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
                if(topic==null) {
                    item {Text(label("Grammar notes and examples are in German.","Grammatikhinweise und Beispiele sind auf Deutsch."),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}
                    item {OutlinedTextField(query,{query=it},label={Text(label("Search grammar","Grammatik suchen"))},singleLine=true,modifier=Modifier.fillMaxWidth())}
                    val needle=query.trim().lowercase(java.util.Locale.GERMAN)
                    val results=data.topics.filter {grammarSearchText(it).contains(needle)}
                    if(results.isEmpty()) item {Text(label("No matching topics.","Keine passenden Themen."))}
                    items(results,key={it.id}) {entry-> StudyTopicRow(if(german)entry.title.de else entry.title.en,action={selected=entry.id})}
                } else {
                    item {TextButton(onClick={selected=null}){Text(label("All topics","Alle Themen"))}}
                    item {StudyPageTitle(if(german)topic.title.de else topic.title.en)}
                    items(topic.tables,key={it.title}) {table->GrammarReferenceCard(table,german)}
                }
            }
        }
    }
}

@Composable
private fun GrammarReferenceCard(table:GrammarReferenceTable,german:Boolean) {
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Text(table.title,style=MaterialTheme.typography.titleLarge,modifier=Modifier.semantics {heading()})
        Text(table.note,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
        if(table.useListLayout || table.headers.size<4) table.rows.forEach {row->
            Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {row.forEachIndexed {i,cell->
                Text(table.headers[i],style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
                Text(cell,style=MaterialTheme.typography.bodyLarge)
            }}
            HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant)
        } else Column(Modifier.horizontalScroll(rememberScrollState())) {
            Row {table.headers.forEach {header->Text(header,style=MaterialTheme.typography.labelLarge,modifier=Modifier.width(112.dp).padding(10.dp))}}
            table.rows.forEach {row->
                HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant)
                Row {row.forEach {cell->Text(cell,modifier=Modifier.width(112.dp).padding(10.dp))}}
            }
        }
        if(table.rule.isNotBlank()) GrammarReferenceDisclosure(if(german)"Regel" else "Rule",listOf(table.rule),german,true)
        if(table.examples.isNotEmpty()) GrammarReferenceDisclosure(if(german)"Beispiele" else "Examples",table.examples,german)
        if(table.mistakes.isNotEmpty()) GrammarReferenceDisclosure(if(german)"Typische Fehler" else "Common mistakes",table.mistakes,german)
        table.extraSections.forEach {section->GrammarReferenceDisclosure(section.title,section.items,german)}
    }
}

@Composable
private fun GrammarReferenceDisclosure(title:String,items:List<String>,german:Boolean,initiallyExpanded:Boolean=false) {
    var expanded by rememberSaveable(title) {mutableStateOf(initiallyExpanded)}
    TextButton(onClick={expanded=!expanded},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp).semantics {
        stateDescription=if(german) {if(expanded) "Ausgeklappt" else "Eingeklappt"} else {if(expanded) "Expanded" else "Collapsed"}
    }) {Text(title,Modifier.weight(1f));Text(if(expanded) "−" else "+")}
    if(expanded) items.forEach {Text(it,style=MaterialTheme.typography.bodyMedium)}
}

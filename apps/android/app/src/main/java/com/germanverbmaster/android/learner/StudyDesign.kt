package com.germanverbmaster.android.learner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Shared presentation only. Persistence and session transitions stay with their hosts. */
@Composable
internal fun StudyHeader(german: Boolean, account: () -> Unit, enabled: Boolean = true, showGrammar: Boolean = true) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(color = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(10.dp)) {
            Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) { Text("ü", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        }
        Text("German Master", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        TextButton(onClick = account, enabled = enabled) {
            Icon(Icons.Outlined.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(if(german) "Einstellungen" else "Settings")
        }
    }
    if(showGrammar) GrammarReferenceButton(german,enabled)
}

@Composable
internal fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, Modifier.padding(bottom=8.dp).semantics { heading() }, style=MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
internal fun SettingsRow(title: String, detail: String? = null, enabled: Boolean = true, action: () -> Unit) {
    TextButton(onClick=action, enabled=enabled, modifier=Modifier.fillMaxWidth().heightIn(min=48.dp),
        colors=ButtonDefaults.textButtonColors(contentColor=MaterialTheme.colorScheme.onSurface)) {
        Column(Modifier.weight(1f), verticalArrangement=Arrangement.spacedBy(2.dp)) {
            Text(title, Modifier.fillMaxWidth(), style=MaterialTheme.typography.bodyLarge)
            detail?.let { Text(it, Modifier.fillMaxWidth(), style=MaterialTheme.typography.bodyMedium, color=MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

@Composable
internal fun StudyLanguageRow(german: Boolean, enabled: Boolean = true, language: () -> Unit) {
    var choosing by remember { mutableStateOf(false) }
    SettingsRow(if(german) "Sprache" else "Language", if(german) "Deutsch" else "English", enabled) { choosing=true }
    if(choosing) AlertDialog(onDismissRequest={choosing=false}, title={Text(if(german) "Sprache" else "Language")},
        text={Column {
            listOf(false to "English", true to "Deutsch").forEach { (choice,label) ->
                com.germanverbmaster.android.foundation.ChoiceOption(label,german==choice,enabled) {
                    choosing=false; if(choice!=german) language()
                }
            }
        }}, confirmButton={TextButton(onClick={choosing=false}) {Text(if(german) "Abbrechen" else "Cancel")}})
}

@Composable
internal fun PracticeLevelSetting(level: String, german: Boolean, enabled: Boolean, choose: (String) -> Unit) {
    var choosing by remember {mutableStateOf(false)}
    SettingsRow(if(german) "Übungsniveau" else "Practice level",level,enabled) {choosing=true}
    if(choosing) AlertDialog(onDismissRequest={choosing=false},title={Text(if(german) "Übungsniveau" else "Practice level")},
        text={Column {listOf("B1","B2").forEach {value->
            com.germanverbmaster.android.foundation.ChoiceOption(value,level==value,enabled) {choosing=false;if(value!=level)choose(value)}
        }}}, confirmButton={TextButton(onClick={choosing=false}) {Text(if(german) "Abbrechen" else "Cancel")}})
}

@Composable
internal fun StudyAppearanceSettings(german: Boolean, language: () -> Unit, enabled: Boolean = true) {
    val (appearance, choose) = com.germanverbmaster.android.foundation.rememberAppearance()
    var choosing by remember { mutableStateOf(false) }
    val choices=listOf("system" to if(german) "Systemeinstellung" else "System", "light" to if(german) "Hell" else "Light", "dark" to if(german) "Dunkel" else "Dark")
    SettingsGroup(if(german) "Sprache und Darstellung" else "Language and appearance") {
        StudyLanguageRow(german,enabled,language)
        SettingsRow(if(german) "Design" else "Theme", choices.first {it.first==appearance}.second, enabled) {choosing=true}
    }
    if(choosing) AlertDialog(onDismissRequest={choosing=false}, title={Text(if(german) "Design" else "Theme")},
        text={Column {choices.forEach { (value,label) ->
            com.germanverbmaster.android.foundation.ChoiceOption(label,appearance==value,enabled) {choose(value);choosing=false}
        }}}, confirmButton={TextButton(onClick={choosing=false}){Text(if(german) "Abbrechen" else "Cancel")}})
}

@Composable
internal fun StudyEyebrow(label: String) {
    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
internal fun StudyPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
    }
}

@Composable
internal fun StudyPrimary(label: String, enabled: Boolean = true, action: () -> Unit) {
    Button(onClick = action, enabled = enabled, shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp), modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
        Text(label, Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
    }
}

@Composable
internal fun StudyTopicRow(title: String, detail: String? = null, action: (() -> Unit)? = null, enabled: Boolean = true) {
    val body: @Composable RowScope.() -> Unit = {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            detail?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
    if (action == null) Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) { body() }
    else TextButton(onClick = action, enabled = enabled, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(vertical = 14.dp, horizontal = 0.dp), modifier = Modifier.fillMaxWidth()) {
        body()
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
internal fun StudyPageTitle(title: String) {
    Text(title, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.semantics { heading() })
}

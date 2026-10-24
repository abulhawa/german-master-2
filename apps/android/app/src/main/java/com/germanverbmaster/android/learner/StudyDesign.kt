package com.germanverbmaster.android.learner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.R

/** Shared presentation only. Persistence and session transitions stay with their hosts. */
@Composable
internal fun StudyHeader(german: Boolean, account: () -> Unit, language: (() -> Unit)? = null, enabled: Boolean = true) {
    val c = studyCopy(german)
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(color = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(10.dp)) {
            Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) { Text("ü", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        }
        Text("German Master", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        language?.let { TextButton(onClick = it, enabled = enabled, modifier = Modifier.semantics { contentDescription = if(german) "Sprache der Oberfläche" else "Interface language" }) { Text(if (german) "DE" else "EN") } }
        IconButton(onClick = account, enabled = enabled) { Icon(Icons.Outlined.AccountCircle, contentDescription = c(R.string.study_account)) }
    }
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

package com.germanverbmaster.android.ui.worddetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.util.TranslationManager
import com.germanverbmaster.android.ui.common.NounFormFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(
    onBack: () -> Unit,
    viewModel: WordDetailViewModel = hiltViewModel()
) {
    val word by viewModel.word.collectAsStateWithLifecycle()
    val aiTranslation by viewModel.aiTranslation.collectAsStateWithLifecycle()
    val isUpdatingDb by viewModel.isUpdatingDb.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wortdetails") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val currentWord = word
            if (currentWord == null) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
            } else {
                WordDetailContent(
                    word = currentWord,
                    onSpeak = { viewModel.speak(it) },
                    aiTranslation = aiTranslation,
                    isUpdatingDb = isUpdatingDb,
                    isDebug = viewModel.isDebug,
                    onRefreshAi = { viewModel.requestAiTranslation() },
                    onUpdateDb = { viewModel.updateDatabaseWithAi() }
                )
            }
        }
    }
}

@Composable
private fun WordDetailContent(
    word: WordEntity,
    onSpeak: (String) -> Unit,
    aiTranslation: TranslationManager.TranslationResult?,
    isUpdatingDb: Boolean,
    isDebug: Boolean,
    onRefreshAi: () -> Unit,
    onUpdateDb: () -> Unit
) {
    val nounPresentation = remember(word) {
        if (NounFormFormatter.isNoun(word.pos)) {
            NounFormFormatter.present(
                lemma = word.lemma,
                gender = word.gender,
                plural = word.plural,
            )
        } else {
            null
        }
    }
    val headlineText = nounPresentation?.singularDisplay ?: word.lemma
    val headlineSpeak = nounPresentation?.speakText ?: word.lemma
    val genderDisplay = nounPresentation?.genderDisplay ?: word.gender

    // Lemma and Audio
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = headlineText,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = { onSpeak(headlineSpeak) }) {
            Icon(
                Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Sprechen",
                modifier = Modifier.size(32.dp)
            )
        }
    }

    // Translation
    word.english?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
        )
    }

    Spacer(Modifier.height(16.dp))

    // AI Translation Section
    AiTranslationBox(
        currentTranslation = word.english,
        result = aiTranslation,
        isUpdatingDb = isUpdatingDb,
        isDebug = isDebug,
        onRefresh = onRefreshAi,
        onUpdateDb = onUpdateDb
    )

    Spacer(Modifier.height(24.dp))

    // Badges Row
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        word.level?.let {
            InfoBadge(label = "Level", value = it, color = MaterialTheme.colorScheme.primaryContainer)
        }
        InfoBadge(label = "Kategorie", value = word.pos, color = MaterialTheme.colorScheme.secondaryContainer)
        genderDisplay?.let {
            InfoBadge(label = "Genus", value = it, color = MaterialTheme.colorScheme.tertiaryContainer)
        }
    }

    Spacer(Modifier.height(24.dp))
    HorizontalDivider()
    Spacer(Modifier.height(24.dp))

    // Grammatical Info
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (word.pos.uppercase()) {
            "V", "VERB" -> {
                DetailSection(title = "Hilfsverb", content = word.aux ?: "Keine Angabe")
                DetailSection(title = "Präteritum", content = word.praeteritum ?: "Keine Angabe")
                DetailSection(title = "Partizip II", content = word.partizip2 ?: "Keine Angabe")
                if (!word.praesensEr.isNullOrBlank()) {
                    DetailSection(title = "Präsens (er/sie/es)", content = word.praesensEr)
                }
            }
            "N", "NOMEN" -> {
                DetailSection(title = "Plural", content = nounPresentation?.pluralDisplay ?: "Keine Angabe")
            }
            "ADJ", "ADJEKTIV" -> {
                DetailSection(title = "Komparativ", content = word.comparative ?: "Keine Angabe")
                DetailSection(title = "Superlativ", content = word.superlative ?: "Keine Angabe")
            }
        }
    }

    // Example Section
    if (!word.exampleDe.isNullOrBlank()) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Beispiel",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = word.exampleDe,
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onSpeak(word.exampleDe) }) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Sprechen")
                    }
                }
                word.exampleEn?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoBadge(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DetailSection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(content, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AiTranslationBox(
    currentTranslation: String?,
    result: TranslationManager.TranslationResult?,
    isUpdatingDb: Boolean,
    isDebug: Boolean,
    onRefresh: () -> Unit,
    onUpdateDb: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "KI Übersetzung",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onRefresh, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "KI Refresh", modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            when (result) {
                null -> {
                    Text(
                        "Klicken Sie auf das Icon oben, um eine KI-Übersetzung anzufordern.",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic
                    )
                }
                is TranslationManager.TranslationResult.Success -> {
                    Column {
                        Text(
                            text = result.translation,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (isDebug && result.translation != currentTranslation) {
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = onUpdateDb,
                                enabled = !isUpdatingDb,
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = ButtonDefaults.TextButtonContentPadding
                            ) {
                                if (isUpdatingDb) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Datenbank aktualisieren")
                                }
                            }
                        }
                    }
                }
                is TranslationManager.TranslationResult.LowConfidence -> {
                    Column {
                        Text(
                            "Niedrige Konfidenz:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = result.translation,
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                        Text(
                            "Rückübersetzung: ${result.backTranslation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                is TranslationManager.TranslationResult.Error -> {
                    Text(
                        result.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

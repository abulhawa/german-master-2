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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.speech.TextToSpeechHelper
import com.germanverbmaster.android.ui.common.NounFormFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(
    onBack: () -> Unit,
    viewModel: WordDetailViewModel = hiltViewModel()
) {
    val word by viewModel.word.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val ttsHelper = remember { TextToSpeechHelper(context) }

    DisposableEffect(Unit) {
        onDispose { ttsHelper.shutdown() }
    }

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
                WordDetailContent(currentWord, onSpeak = { ttsHelper.speak(it) })
            }
        }
    }
}

@Composable
private fun WordDetailContent(word: WordEntity, onSpeak: (String) -> Unit) {
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

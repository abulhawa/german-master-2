package com.germanverbmaster.android.ui.worddetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.util.TranslationManager
import com.germanverbmaster.android.ui.common.NounFormFormatter
import com.google.mlkit.nl.translate.TranslateLanguage

private val languageNameMap = mapOf(
    "af" to "Afrikaans", "sq" to "Albanisch", "ar" to "Arabisch", "be" to "Belarussisch",
    "bn" to "Bengalisch", "bg" to "Bulgarisch", "ca" to "Katalanisch", "zh" to "Chinesisch",
    "hr" to "Kroatisch", "cs" to "Tschechisch", "da" to "Dänisch", "nl" to "Niederländisch",
    "en" to "Englisch", "eo" to "Esperanto", "et" to "Estnisch", "fi" to "Finnisch",
    "fr" to "Französisch", "gl" to "Galicisch", "ka" to "Georgisch", "de" to "Deutsch",
    "el" to "Griechisch", "gu" to "Gujarati", "ht" to "Haitianisch", "he" to "Hebräisch",
    "hi" to "Hindi", "hu" to "Ungarisch", "is" to "Isländisch", "id" to "Indonesisch",
    "ga" to "Irisch", "it" to "Italienisch", "ja" to "Japanisch", "kn" to "Kannada",
    "ko" to "Koreanisch", "lv" to "Lettisch", "lt" to "Litauisch", "mk" to "Mazedonisch",
    "ms" to "Malaiisch", "mt" to "Maltesisch", "mr" to "Marathi", "no" to "Norwegisch",
    "fa" to "Persisch", "pl" to "Polnisch", "pt" to "Portugiesisch", "ro" to "Rumänisch",
    "ru" to "Russisch", "sk" to "Slowakisch", "sl" to "Slowenisch", "es" to "Spanisch",
    "sw" to "Swahili", "sv" to "Schwedisch", "tl" to "Tagalog", "ta" to "Tamil",
    "te" to "Telugu", "th" to "Thailändisch", "tr" to "Türkisch", "uk" to "Ukrainisch",
    "ur" to "Urdu", "vi" to "Vietnamesisch", "cy" to "Walisisch"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(
    onBack: () -> Unit,
    viewModel: WordDetailViewModel = hiltViewModel()
) {
    val word by viewModel.word.collectAsStateWithLifecycle()
    val aiTranslation by viewModel.aiTranslation.collectAsStateWithLifecycle()
    val aiExampleTranslation by viewModel.aiExampleTranslation.collectAsStateWithLifecycle()
    val isModelDownloaded by viewModel.isModelDownloaded.collectAsStateWithLifecycle()
    val isDownloading by viewModel.isDownloading.collectAsStateWithLifecycle()
    val downloadError by viewModel.downloadError.collectAsStateWithLifecycle()
    val targetLanguage by viewModel.targetLanguage.collectAsStateWithLifecycle()
    val downloadedCodes by viewModel.downloadedLanguageCodes.collectAsStateWithLifecycle()

    var showDownloadDialog by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }

    val allLanguages = remember(downloadedCodes) {
        TranslateLanguage.getAllLanguages().map { 
            it to (languageNameMap[it] ?: it)
        }.sortedWith(compareByDescending<Pair<String, String>> { downloadedCodes.contains(it.first) }.thenBy { it.second })
    }

    if (showDownloadDialog) {
        DownloadPermissionDialog(
            targetLanguageName = languageNameMap[targetLanguage] ?: targetLanguage,
            onConfirm = { allowMobile ->
                viewModel.downloadModels(allowMobile)
                showDownloadDialog = false
            },
            onDismiss = { showDownloadDialog = false }
        )
    }

    if (showLanguagePicker) {
        LanguagePickerDialog(
            languages = allLanguages,
            currentLanguageCode = targetLanguage,
            downloadedCodes = downloadedCodes,
            onLanguageSelected = { code ->
                viewModel.setTargetLanguage(code)
                showLanguagePicker = false
            },
            onDeleteLanguage = { code ->
                viewModel.deleteLanguageModel(code)
            },
            onDismiss = { showLanguagePicker = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wortdetails") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    IconButton(onClick = { showLanguagePicker = true }) {
                        Icon(Icons.Default.Translate, contentDescription = "Sprache wählen")
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
                val nounPresentation = remember(currentWord) {
                    if (NounFormFormatter.isNoun(currentWord.pos)) {
                        NounFormFormatter.present(
                            lemma = currentWord.lemma,
                            gender = currentWord.gender,
                            plural = currentWord.plural,
                        )
                    } else {
                        null
                    }
                }
                val headlineText = nounPresentation?.singularDisplay ?: currentWord.lemma

                WordDetailContent(
                    word = currentWord,
                    onSpeak = { viewModel.speak(it) },
                    aiTranslation = aiTranslation,
                    aiExampleTranslation = aiExampleTranslation,
                    isModelDownloaded = isModelDownloaded,
                    isDownloading = isDownloading,
                    downloadError = downloadError,
                    targetLanguageName = languageNameMap[targetLanguage] ?: targetLanguage,
                    onRefreshAi = { 
                        if (isModelDownloaded) {
                            viewModel.requestAiTranslation(headlineText)
                        } else {
                            showDownloadDialog = true
                        }
                    }
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
    aiExampleTranslation: TranslationManager.TranslationResult?,
    isModelDownloaded: Boolean,
    isDownloading: Boolean,
    downloadError: String?,
    targetLanguageName: String,
    onRefreshAi: () -> Unit,
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
        wordResult = aiTranslation,
        isModelDownloaded = isModelDownloaded,
        isDownloading = isDownloading,
        downloadError = downloadError,
        targetLanguageName = targetLanguageName,
        onRefresh = onRefreshAi,
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

                // KI Example Translation
                aiExampleTranslation?.let { result ->
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.height(8.dp))
                    
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "KI Übersetzung ($targetLanguageName)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        when (result) {
                            is TranslationManager.TranslationResult.Success -> {
                                Text(
                                    text = result.translation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            is TranslationManager.TranslationResult.LowConfidence -> {
                                Text(
                                    text = result.translation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Surface(
                                    shape = MaterialTheme.shapes.extraSmall,
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    Text(
                                        "Die KI ist sich bei dieser Übersetzung nicht zu 100% sicher. Bitte prüfen.",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                            is TranslationManager.TranslationResult.Error -> {
                                Text(result.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        
                        Text(
                            "powered by Google",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
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
    wordResult: TranslationManager.TranslationResult?,
    isModelDownloaded: Boolean,
    isDownloading: Boolean,
    downloadError: String?,
    targetLanguageName: String,
    onRefresh: () -> Unit,
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
                        "KI Übersetzung ($targetLanguageName)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (isDownloading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    IconButton(onClick = onRefresh, modifier = Modifier.size(24.dp)) {
                        val icon = if (isModelDownloaded) Icons.Default.AutoAwesome else Icons.Default.CloudDownload
                        Icon(icon, contentDescription = "KI Refresh", modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (downloadError != null) {
                Text(
                    "Download-Fehler: $downloadError",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(8.dp))
            }

            if (!isModelDownloaded && !isDownloading) {
                Text(
                    "KI-Modelle für $targetLanguageName müssen heruntergeladen werden (ca. 60MB). Klicken Sie auf das Cloud-Icon.",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
            } else if (isDownloading) {
                Text(
                    "Modelle für $targetLanguageName werden heruntergeladen...",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
            } else if (wordResult == null) {
                Text(
                    "Klicken Sie auf das Icon oben, um eine KI-Übersetzung anzufordern.",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    wordResult.let {
                        TranslationResultView(title = "Wort (basierend auf Beispielsatz)", result = it)
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                Text(
                    "powered by Google",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun DownloadPermissionDialog(
    targetLanguageName: String,
    onConfirm: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var allowMobileData by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "KI-Modelle herunterladen",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Für die KI-Übersetzung nach $targetLanguageName müssen Sprachmodelle heruntergeladen werden (ca. 60MB).",
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = allowMobileData,
                        onCheckedChange = { allowMobileData = it }
                    )
                    Text("Auch über mobile Daten herunterladen")
                }
                
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Abbrechen")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onConfirm(allowMobileData) }) {
                        Text("Download")
                    }
                }
            }
        }
    }
}

@Composable
private fun TranslationResultView(title: String, result: TranslationManager.TranslationResult) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        when (result) {
            is TranslationManager.TranslationResult.Success -> {
                Text(
                    text = result.translation,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            is TranslationManager.TranslationResult.LowConfidence -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = result.translation,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Die KI ist sich bei dieser Übersetzung nicht zu 100% sicher. Bitte prüfen.",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
            is TranslationManager.TranslationResult.Error -> {
                Text(result.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerDialog(
    languages: List<Pair<String, String>>,
    currentLanguageCode: String,
    downloadedCodes: Set<String>,
    onLanguageSelected: (String) -> Unit,
    onDeleteLanguage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredLanguages = remember(searchQuery, languages) {
        languages.filter { 
            it.second.contains(searchQuery, ignoreCase = true) || 
            it.first.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Zielsprache wählen",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Suchen...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredLanguages) { (code, name) ->
                        val isDownloaded = downloadedCodes.contains(code)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLanguageSelected(code) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (code == currentLanguageCode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isDownloaded) {
                                    Text(
                                        "Bereit für offline",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            if (isDownloaded && code != TranslateLanguage.GERMAN && code != TranslateLanguage.ENGLISH) {
                                IconButton(onClick = { onDeleteLanguage(code) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Löschen",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            if (code == currentLanguageCode) {
                                Text("✓", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Schließen")
                }
            }
        }
    }
}

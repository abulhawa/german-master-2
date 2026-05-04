package com.germanverbmaster.android.ui.worddetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.util.TranslationManager
import com.germanverbmaster.android.ui.common.NounFormFormatter
import com.germanverbmaster.android.ui.components.AiTranslationBox
import com.germanverbmaster.android.ui.components.ContextualTranslationResult
import com.germanverbmaster.android.ui.components.DownloadPermissionDialog
import com.germanverbmaster.android.ui.components.LanguagePickerDialog
import com.germanverbmaster.android.ui.components.SelectionTranslationDialog
import com.germanverbmaster.android.ui.components.TappableSentenceText
import com.germanverbmaster.android.ui.components.TranslatingSelectionContainer
import com.germanverbmaster.android.ui.components.languageNameMap
import com.google.mlkit.nl.translate.TranslateLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(
    onBack: () -> Unit,
    onShowGrammar: () -> Unit,
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
    val selectionTranslation by viewModel.selectionTranslation.collectAsStateWithLifecycle()
    val contextualSelectionTranslation by viewModel.contextualSelectionTranslation.collectAsStateWithLifecycle()
    val selectionKey by viewModel.selectionKey.collectAsStateWithLifecycle()

    var showDownloadDialog by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }

    // Logic: Back deselects first, then navigates
    BackHandler {
        onBack()
    }

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

    SelectionTranslationDialog(
        result = selectionTranslation,
        contextualResult = contextualSelectionTranslation,
        onDismiss = viewModel::clearSelectionTranslation
    )

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
                    IconButton(onClick = onShowGrammar) {
                        Text(
                            text = "G",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showLanguagePicker = true }) {
                        Icon(Icons.Default.Translate, contentDescription = "Sprache wählen")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { viewModel.clearSelection() })
                }
        ) {
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
                        translationManager = viewModel.getTranslationManager(),
                        aiTranslation = aiTranslation,
                        aiExampleTranslation = aiExampleTranslation,
                        isModelDownloaded = isModelDownloaded,
                        isDownloading = isDownloading,
                        downloadError = downloadError,
                        targetLanguageName = languageNameMap[targetLanguage] ?: targetLanguage,
                        targetLanguageCode = targetLanguage,
                        selectionKey = selectionKey,
                        onRefreshAi = { 
                            if (isModelDownloaded) {
                                viewModel.requestAiTranslation(headlineText)
                            } else {
                                showDownloadDialog = true
                            }
                        },
                        onTranslateSelection = viewModel::setContextualSelectionTranslation,
                        onLegacyTranslate = viewModel::translateSelectedText
                    )
                }
            }
        }
    }
}

@Composable
private fun WordDetailContent(
    word: WordEntity,
    onSpeak: (String) -> Unit,
    translationManager: TranslationManager,
    aiTranslation: TranslationManager.TranslationResult?,
    aiExampleTranslation: TranslationManager.TranslationResult?,
    isModelDownloaded: Boolean,
    isDownloading: Boolean,
    downloadError: String?,
    targetLanguageName: String,
    targetLanguageCode: String,
    selectionKey: Int,
    onRefreshAi: () -> Unit,
    onTranslateSelection: (ContextualTranslationResult) -> Unit,
    onLegacyTranslate: (String) -> Unit,
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
        key(selectionKey) {
            TranslatingSelectionContainer(
                onTranslate = onLegacyTranslate,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = headlineText,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )
            }
        }
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
        key(selectionKey) {
            TranslatingSelectionContainer(onTranslate = onLegacyTranslate) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    // AI Translation Section
    AiTranslationBox(
        wordResult = aiTranslation,
        isModelDownloaded = isModelDownloaded,
        isDownloading = isDownloading,
        downloadError = downloadError,
        targetLanguageName = targetLanguageName,
        selectionKey = selectionKey,
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
        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Beispiel",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            TextButton(
                onClick = {
                    val url = "https://translate.google.com/?sl=de&tl=$targetLanguageCode&text=${java.net.URLEncoder.encode(word.exampleDe, "UTF-8")}&op=translate"
                    uriHandler.openUri(url)
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Google Web", style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.height(8.dp))
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    key(selectionKey) {
                        TappableSentenceText(
                            text = word.exampleDe,
                            translationManager = translationManager,
                            targetLang = targetLanguageCode,
                            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                            modifier = Modifier.weight(1f),
                            onResult = onTranslateSelection
                        )
                    }
                    IconButton(onClick = { onSpeak(word.exampleDe) }) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Sprechen")
                    }
                }
                word.exampleEn?.let {
                    Spacer(Modifier.height(4.dp))
                    key(selectionKey) {
                        TranslatingSelectionContainer(onTranslate = onLegacyTranslate) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
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
                        
                        key(selectionKey) {
                            TranslatingSelectionContainer(onTranslate = onLegacyTranslate) {
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
                                    }
                                    is TranslationManager.TranslationResult.Error -> {
                                        Text(result.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
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
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        color = color,
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DetailSection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(content, style = MaterialTheme.typography.bodyLarge)
    }
}

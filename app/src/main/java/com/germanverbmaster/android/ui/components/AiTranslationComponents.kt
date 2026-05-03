package com.germanverbmaster.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import com.germanverbmaster.android.data.util.TranslationManager
import com.google.mlkit.nl.translate.TranslateLanguage

val languageNameMap = mapOf(
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

@Composable
fun AiTranslationBox(
    wordResult: TranslationManager.TranslationResult?,
    isModelDownloaded: Boolean,
    isDownloading: Boolean,
    downloadError: String?,
    targetLanguageName: String,
    selectionKey: Int,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                        key(selectionKey) {
                            TranslationResultView(title = "Wort (basierend auf Beispielsatz)", result = it)
                        }
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
fun TranslationResultView(title: String, result: TranslationManager.TranslationResult) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)

        SelectionContainer {
            when (result) {
                is TranslationManager.TranslationResult.Success -> {
                    Text(
                        text = result.translation,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                is TranslationManager.TranslationResult.LowConfidence -> {
                    Text(
                        text = result.translation,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                is TranslationManager.TranslationResult.Error -> {
                    Text(result.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (result is TranslationManager.TranslationResult.LowConfidence) {
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

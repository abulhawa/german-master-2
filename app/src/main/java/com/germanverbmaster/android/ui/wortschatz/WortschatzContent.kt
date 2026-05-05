package com.germanverbmaster.android.ui.wortschatz

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.data.util.TranslationManager
import com.germanverbmaster.android.ui.components.AiTranslationBox
import com.germanverbmaster.android.ui.components.DownloadPermissionDialog
import com.germanverbmaster.android.ui.components.ExamCountdownBanner
import com.germanverbmaster.android.ui.components.LanguagePickerDialog
import com.germanverbmaster.android.ui.components.SelectionTranslationDialog
import com.germanverbmaster.android.ui.components.ShimmerItem
import com.germanverbmaster.android.ui.components.TranslatingSelectionContainer
import com.germanverbmaster.android.ui.components.languageNameMap
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WortschatzScreenContent(
    state: WortschatzUiState,
    targetLanguage: String,
    isDownloading: Boolean,
    downloadError: String?,
    onTriggerSync: () -> Unit,
    onSelectTab: (WortschatzTab) -> Unit,
    onUpdateSearchQuery: (String) -> Unit,
    onToggleLevel: (String) -> Unit,
    onTogglePos: (String) -> Unit,
    onNavigateToHistory: (String) -> Unit,
    onNavigateToWordDetail: (Int) -> Unit,
    onShowGrammar: () -> Unit,
    onFlip: () -> Unit,
    onMarkCorrect: () -> Unit,
    onMarkWrong: () -> Unit,
    onRestartDrill: () -> Unit,
    onExitDrill: () -> Unit,
    onSpeak: (String) -> Unit,
    onRefreshAi: () -> Unit,
    onTranslateSelection: (String) -> Unit,
    selectionTranslation: TranslationManager.TranslationResult?,
    onClearSelectionTranslation: () -> Unit,
    onSetTargetLanguage: (String) -> Unit,
    onDownloadModels: (Boolean) -> Unit,
    onDeleteLanguageModel: (String) -> Unit,
    onToggleAutoTranslate: (Boolean) -> Unit,
    onClearSelection: () -> Unit,
) {
    val showFilterSheet = remember { mutableStateOf(false) }
    var showDownloadDialog by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }

    if (showFilterSheet.value) {
        FilterBottomSheet(
            selectedLevels = state.selectedLevels,
            onLevelToggle = onToggleLevel,
            selectedPosSet = state.selectedPosSet,
            onPosToggle = onTogglePos,
            posOptions = state.posOptions,
            wordCount = if (state.isLoading) null else state.listCards.size,
            onDismiss = { showFilterSheet.value = false }
        )
    }

    if (showDownloadDialog) {
        DownloadPermissionDialog(
            targetLanguageName = languageNameMap[targetLanguage] ?: targetLanguage,
            onConfirm = { allowMobile ->
                onDownloadModels(allowMobile)
                showDownloadDialog = false
            },
            onDismiss = { showDownloadDialog = false }
        )
    }

    if (showLanguagePicker) {
        val downloadedCodes = state.downloadedLanguageCodes
        val allLanguages = remember(downloadedCodes) {
            TranslateLanguage.getAllLanguages().map { 
                it to (languageNameMap[it] ?: it)
            }.sortedWith(compareByDescending<Pair<String, String>> { downloadedCodes.contains(it.first) }.thenBy { it.second })
        }
        LanguagePickerDialog(
            languages = allLanguages,
            currentLanguageCode = targetLanguage,
            downloadedCodes = downloadedCodes,
            isAutoTranslateEnabled = state.isAiAutoTranslateEnabled,
            onLanguageSelected = { code ->
                onSetTargetLanguage(code)
                showLanguagePicker = false
            },
            onDeleteLanguage = { code ->
                onDeleteLanguageModel(code)
            },
            onToggleAutoTranslate = onToggleAutoTranslate,
            onDismiss = { showLanguagePicker = false }
        )
    }

    SelectionTranslationDialog(
        result = selectionTranslation,
        onDismiss = onClearSelectionTranslation
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClearSelection() })
            }
    ) {
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = onTriggerSync,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
            ) {
                ExamCountdownBanner(
                    examDate = LocalDate.of(2026, 4, 30),
                    modifier = Modifier.padding(top = 12.dp)
                )

                Spacer(Modifier.height(12.dp))

                // Mode Selector
                PrimaryTabRow(
                    selectedTabIndex = state.tab.ordinal,
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    divider = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    WortschatzTab.entries.forEach { tab ->
                        Tab(
                            selected = state.tab == tab,
                            onClick = { onSelectTab(tab) },
                            text = { Text(tab.label, style = MaterialTheme.typography.titleSmall) }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Search and Filter Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (state.searchQuery.isEmpty()) {
                                    Text(
                                        "Wort suchen...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                                BasicTextField(
                                    value = state.searchQuery,
                                    onValueChange = onUpdateSearchQuery,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onUpdateSearchQuery("") },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Löschen",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    val activeFilters = state.selectedLevels.size + state.selectedPosSet.size
                    BadgedBox(
                        badge = {
                            if (activeFilters > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(activeFilters.toString())
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = { showFilterSheet.value = true },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (activeFilters > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.5f
                                    ),
                                    RoundedCornerShape(24.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter",
                                tint = if (activeFilters > 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        onClick = { showLanguagePicker = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Sprache wählen",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                when {
                    state.isLoading && state.listCards.isEmpty() -> {
                        if (state.tab == WortschatzTab.DRILL) {
                            DrillSkeleton()
                        } else {
                            WortschatzSkeleton()
                        }
                    }

                    else -> when (state.tab) {
                        WortschatzTab.LIST -> WordListContent(
                            state.listCards,
                            onSpeak = onSpeak,
                            onWordClick = onNavigateToWordDetail
                        )

                        WortschatzTab.DRILL -> {
                            DisposableEffect(Unit) {
                                onDispose { onExitDrill() }
                            }
                            DrillContent(
                                state = state,
                                targetLanguage = targetLanguage,
                                isDownloading = isDownloading,
                                downloadError = downloadError,
                                onNavigateToHistory = onNavigateToHistory,
                                onSpeak = onSpeak,
                                onFlip = onFlip,
                                onMarkCorrect = onMarkCorrect,
                                onMarkWrong = onMarkWrong,
                                onRestartDrill = onRestartDrill,
                                onRefreshAi = onRefreshAi,
                                onTranslateSelection = onTranslateSelection,
                                onRequestDownload = { showDownloadDialog = true }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Word List ────────────────────────────────────────────────────────────────

@Composable
private fun WordListContent(
    cards: List<WordEntity>,
    onSpeak: (String) -> Unit,
    onWordClick: (Int) -> Unit
) {
    if (cards.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Keine Wörter für diese Filter.", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    val sortedGroups = remember(cards) {
        val grouped = cards.groupBy { it.pos }
        val posOrder = listOf("V", "N", "Adj", "Adv", "Prep", "Conj", "Pron", "Art", "Num", "Int")
        grouped.entries.sortedBy {
            val idx = posOrder.indexOf(it.key)
            if (idx == -1) 99 else idx
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        sortedGroups.forEach { (pos, words) ->
            item(key = "header_$pos") {
                Text(
                    text = POS_LABELS[pos] ?: pos,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                )
            }

            itemsIndexed(words, key = { _, word -> word.id }) { _, word ->
                WordRow(
                    word = word,
                    onSpeak = { onSpeak(word.lemma) },
                    onClick = { onWordClick(word.id) }
                )
            }
        }
    }
}

@Composable
private fun WordRow(
    word: WordEntity,
    onSpeak: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val article = if (isNoun(word.pos)) genderArticle(word.gender) else ""
                    if (article.isNotBlank()) {
                        Text(
                            text = article,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        text = word.lemma,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                word.english?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onSpeak) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Sprechen",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    selectedLevels: Set<String>,
    onLevelToggle: (String) -> Unit,
    selectedPosSet: Set<String>,
    onPosToggle: (String) -> Unit,
    posOptions: List<String>,
    wordCount: Int?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        FilterSection(
            selectedLevels = selectedLevels,
            onLevelToggle = onLevelToggle,
            selectedPosSet = selectedPosSet,
            onPosToggle = onPosToggle,
            posOptions = posOptions,
            wordCount = wordCount,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun FilterSection(
    selectedLevels: Set<String>,
    onLevelToggle: (String) -> Unit,
    selectedPosSet: Set<String>,
    onPosToggle: (String) -> Unit,
    posOptions: List<String>,
    wordCount: Int?,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Filter",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            wordCount?.let {
                Text(
                    "$it Wörter gefunden",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("Lernstufen", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val row1 = LEVEL_FILTERS.take(3)
            row1.forEach { level ->
                FilterChip(
                    selected = (level == "Alle" && selectedLevels.isEmpty()) || selectedLevels.contains(level),
                    onClick = { onLevelToggle(level) },
                    label = { Text(level) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val row2 = LEVEL_FILTERS.drop(3)
            row2.forEach { level ->
                FilterChip(
                    selected = selectedLevels.contains(level),
                    onClick = { onLevelToggle(level) },
                    label = { Text(level) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("Wortarten", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            posOptions.chunked(3).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { pos ->
                        FilterChip(
                            selected = (pos == "Alle" && selectedPosSet.isEmpty()) || selectedPosSet.contains(pos),
                            onClick = { onPosToggle(pos) },
                            label = { Text(POS_LABELS[pos] ?: pos) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowOptions.size < 3) {
                        repeat(3 - rowOptions.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Anwenden")
        }
    }
}

private fun isNoun(pos: String): Boolean {
    val p = canonicalPos(pos).uppercase()
    return p == "N" || p == "NOMEN"
}

private fun genderArticle(gender: String?): String {
    return when (gender?.lowercase()?.trim()) {
        "m", "maskulin" -> "der"
        "f", "feminin" -> "die"
        "n", "neutrum" -> "das"
        else -> ""
    }
}

@Composable
private fun DrillContent(
    state: WortschatzUiState,
    targetLanguage: String,
    isDownloading: Boolean,
    downloadError: String?,
    onNavigateToHistory: (String) -> Unit,
    onSpeak: (String) -> Unit,
    onFlip: () -> Unit,
    onMarkCorrect: () -> Unit,
    onMarkWrong: () -> Unit,
    onRestartDrill: () -> Unit,
    onRefreshAi: () -> Unit,
    onTranslateSelection: (String) -> Unit,
    onRequestDownload: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DrillStatChip(
                label = "✓ ${state.historicalCorrect}",
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToHistory("correct") }
            )
            DrillStatChip(
                label = "✗ ${state.historicalWrong}",
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToHistory("incorrect") }
            )
        }

        LinearProgressIndicator(
            progress = { state.masteryProgress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Gesamtfortschritt",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
            Text(
                "${state.masteredCount} / ${state.listCards.size} Wörter",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }

        Spacer(Modifier.height(8.dp))

        if (state.drillDone) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DrillDoneCard(
                    correct = state.historicalCorrect,
                    wrong = state.historicalWrong,
                    onRestart = onRestartDrill,
                )
            }
        } else if (state.drillQueue.isEmpty() && !state.isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Keine Wörter für diese Filter gefunden.\nWähle andere Filter oder suche etwas anderes.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            state.drillCurrent?.let { card ->
                val (displayFront, speakFront, pluralDisplay) = remember(card) {
                    val lemma = card.lemma
                    if (isNoun(card.pos)) {
                        val article = genderArticle(card.gender)
                        val singularWithArticle = if (article.isNotBlank()) "$article $lemma" else lemma
                        val plural = card.plural?.trim()?.takeIf { it.isNotEmpty() }
                        Triple(singularWithArticle, singularWithArticle, plural)
                    } else {
                        Triple(lemma, lemma, null)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        DrillFlipCard(
                            card = card,
                            displayFront = displayFront,
                            speakFront = speakFront,
                            pluralDisplay = pluralDisplay,
                            isFlipped = state.drillFlipped,
                            aiTranslation = state.aiTranslation,
                            aiExampleTranslation = state.aiExampleTranslation,
                            isModelDownloaded = state.isModelDownloaded,
                            isDownloading = isDownloading,
                            downloadError = downloadError,
                            targetLanguageName = languageNameMap[targetLanguage] ?: targetLanguage,
                            selectionKey = state.selectionKey,
                            onFlip = onFlip,
                            onSpeak = onSpeak,
                            onMarkCorrect = onMarkCorrect,
                            onMarkWrong = onMarkWrong,
                            onRefreshAi = onRefreshAi,
                            onTranslateSelection = onTranslateSelection,
                            onRequestDownload = onRequestDownload,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    // Bottom controls - constant height to prevent jumping
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, top = 12.dp)
                            .height(110.dp), // Fixed height for buttons
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        if (state.drillFlipped) {
                            OutlinedButton(
                                onClick = onFlip,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                            ) { Text("Zurück zur Frage") }

                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Button(
                                    onClick = onMarkWrong,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                ) { Text("✗ Falsch") }

                                Button(
                                    onClick = onMarkCorrect,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                ) { Text("✓ Richtig") }
                            }
                        } else {
                            Button(
                                onClick = onFlip,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Antwort zeigen", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrillFlipCard(
    card: WordEntity,
    displayFront: String,
    speakFront: String,
    pluralDisplay: String?,
    isFlipped: Boolean,
    aiTranslation: TranslationManager.TranslationResult?,
    aiExampleTranslation: TranslationManager.TranslationResult?,
    isModelDownloaded: Boolean,
    isDownloading: Boolean,
    downloadError: String?,
    targetLanguageName: String,
    selectionKey: Int,
    onFlip: () -> Unit,
    onSpeak: (String) -> Unit,
    onMarkCorrect: () -> Unit,
    onMarkWrong: () -> Unit,
    onRefreshAi: () -> Unit,
    onTranslateSelection: (String) -> Unit,
    onRequestDownload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var dragX by remember(card) { mutableFloatStateOf(0f) }

    val rotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "drill_flip",
    )

    Box(
        modifier = modifier
            .pointerInput(card, isFlipped) {
                if (!isFlipped) return@pointerInput
                val velocityTracker = VelocityTracker()

                detectHorizontalDragGestures(
                    onDragStart = { velocityTracker.resetTracking() },
                    onDragEnd = {
                        val velocity = velocityTracker.calculateVelocity().x
                        val threshold = size.width / 4f
                        val velocityThreshold = 1000f

                        scope.launch {
                            val targetX = if (dragX > threshold || velocity > velocityThreshold) {
                                size.width.toFloat() * 1.5f
                            } else if (dragX < -threshold || velocity < -velocityThreshold) {
                                -size.width.toFloat() * 1.5f
                            } else {
                                0f
                            }

                            animate(initialValue = dragX, targetValue = targetX, animationSpec = tween(300)) { value, _ ->
                                dragX = value
                            }

                            if (targetX > 0) onMarkCorrect()
                            else if (targetX < 0) onMarkWrong()
                        }
                    },
                    onDragCancel = {
                        scope.launch {
                            animate(initialValue = dragX, targetValue = 0f, animationSpec = tween(300)) { value, _ ->
                                dragX = value
                            }
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        dragX += dragAmount
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                    }
                )
            }
            .then(if (!isFlipped) Modifier.clickable { onFlip() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // Wrapper for the card that actually moves
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(dragX.roundToInt(), 0) }
                .graphicsLayer {
                    rotationZ = (dragX / 20f).coerceIn(-15f, 15f)
                    transformOrigin = TransformOrigin(0.5f, 1.2f) // Pivot below card for wiper effect
                    alpha = (1f - (abs(dragX) / 800f)).coerceAtLeast(0.6f)
                },
            contentAlignment = Alignment.Center
        ) {
            if (rotationY <= 90f) {
                DrillCardFace(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { this.rotationY = rotationY },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            card.level?.let { level ->
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                ) {
                                    Text(
                                        level,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
                                }
                            }
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                            ) {
                                Text(
                                    card.pos,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                        IconButton(onClick = { onSpeak(speakFront) }) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Sprechen")
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = displayFront,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                    pluralDisplay?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Tippen zum Aufdecken",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    )
                }
            }

            if (rotationY > 90f) {
                DrillCardFace(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { this.rotationY = rotationY - 180f },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        key(selectionKey) {
                            TranslatingSelectionContainer(
                                onTranslate = onTranslateSelection,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = card.english ?: "",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        card.exampleDe?.let { ex ->
                            Spacer(Modifier.height(12.dp))
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        key(selectionKey) {
                                            TranslatingSelectionContainer(
                                                onTranslate = onTranslateSelection,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = ex,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontStyle = FontStyle.Italic,
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = { onSpeak(ex) },
                                            modifier = Modifier.offset(y = (-4).dp)
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Sprechen")
                                        }
                                    }
                                    card.exampleEn?.let { en ->
                                        Spacer(Modifier.height(4.dp))
                                        key(selectionKey) {
                                            TranslatingSelectionContainer(onTranslate = onTranslateSelection) {
                                                Text(
                                                    text = en,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
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
                                                TranslatingSelectionContainer(onTranslate = onTranslateSelection) {
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
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        AiTranslationBox(
                            wordResult = aiTranslation,
                            isModelDownloaded = isModelDownloaded,
                            isDownloading = isDownloading,
                            downloadError = downloadError,
                            targetLanguageName = targetLanguageName,
                            selectionKey = selectionKey,
                            onRefresh = {
                                if (isModelDownloaded) {
                                    onRefreshAi()
                                } else {
                                    onRequestDownload()
                                }
                            }
                        )
                    }
                }
            }
        }

        if (rotationY > 90f && abs(dragX) > 40) {
            val alpha = ((abs(dragX) - 40) / 160f).coerceIn(0f, 1f)
            val isCorrect = dragX > 0

            Box(
                modifier = Modifier.matchParentSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = (if (isCorrect) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer).copy(
                        alpha = alpha * 0.85f
                    ),
                    border = BorderStroke(
                        4.dp,
                        (if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error).copy(
                            alpha = alpha
                        )
                    ),
                    modifier = Modifier.graphicsLayer {
                        scaleX = 0.5f + (alpha * 0.7f) // Starts at 0.5 and grows to 1.2
                        scaleY = 0.5f + (alpha * 0.7f)
                    }
                ) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(16.dp),
                        tint = if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun DrillCardFace(
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .heightIn(min = 360.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Composable
private fun DrillStatChip(label: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color,
        modifier = modifier
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WortschatzSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        repeat(6) {
            ShimmerItem(
                height = 72.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun DrillSkeleton() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(360.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun DrillDoneCard(correct: Int, wrong: Int, onRestart: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(32.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Drill Beendet!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Richtig", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "$correct",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Falsch", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "$wrong",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Nochmal Starten")
            }
        }
    }
}

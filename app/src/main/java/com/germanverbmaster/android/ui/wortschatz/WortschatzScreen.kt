package com.germanverbmaster.android.ui.wortschatz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.speech.TextToSpeechHelper
import com.germanverbmaster.android.ui.components.ExamCountdownBanner
import com.germanverbmaster.android.ui.components.ShimmerItem
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WortschatzScreen(
    viewModel: WortschatzViewModel = hiltViewModel(),
    onNavigateToHistory: (String) -> Unit,
    onNavigateToWordDetail: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val ttsHelper = remember { TextToSpeechHelper(context) }

    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.triggerSync(force = true) },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
        ) {
            ExamCountdownBanner(
                examDate = LocalDate.of(2026, 4, 30),
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            )

            // Tab row: Wortliste | Schnell-Drill
            PrimaryTabRow(
                selectedTabIndex = WortschatzTab.entries.indexOf(state.tab),
                modifier = Modifier.height(40.dp)
            ) {
                WortschatzTab.entries.forEach { tab ->
                    Tab(
                        selected = state.tab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Search Bar (Compact)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (state.searchQuery.isEmpty()) {
                            Text(
                                text = "Suchen (Deutsch oder Englisch)…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                        BasicTextField(
                            value = state.searchQuery,
                            onValueChange = viewModel::updateSearchQuery,
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.updateSearchQuery("") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Löschen",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(2.dp))

            FilterSection(
                selectedLevels = state.selectedLevels,
                onLevelToggle = viewModel::toggleLevel,
                selectedPosSet = state.selectedPosSet,
                onPosToggle = viewModel::togglePos,
                posOptions = state.posOptions,
                wordCount = if (state.isLoading) null else state.listCards.size,
            )

            // Sync error banner removed

            when {
                state.isLoading -> {
                    if (state.tab == WortschatzTab.DRILL) {
                        DrillSkeleton()
                    } else {
                        WortschatzSkeleton()
                    }
                }

                else -> when (state.tab) {
                    WortschatzTab.LIST -> WordListContent(
                        state.listCards,
                        onSpeak = { ttsHelper.speak(it) },
                        onWordClick = onNavigateToWordDetail
                    )

                    WortschatzTab.DRILL -> DrillContent(
                        state,
                        viewModel,
                        onNavigateToHistory,
                        onSpeak = { ttsHelper.speak(it) })
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

    // Group by POS for readability - optimized with remember to avoid re-calculating on every recompose
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
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        sortedGroups.forEach { (pos, groupCards) ->
            item(key = "header_$pos") {
                Text(
                    text = POS_LABELS[pos] ?: pos,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                )
            }

            // Optimization: Use itemsIndexed instead of a nested Column inside a single item.
            // This allows LazyColumn to only compose and draw the words actually visible on screen,
            // which is essential for performance with large lists (like the 1800 nouns).
            itemsIndexed(
                items = groupCards,
                key = { _, card -> card.id }
            ) { index, card ->
                val isFirst = index == 0
                val isLast = index == groupCards.lastIndex

                // Maintain the "Card" look by rounding only the top of the first item
                // and the bottom of the last item in the group.
                val shape = when {
                    isFirst && isLast -> RoundedCornerShape(12.dp)
                    isFirst -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    isLast -> RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    else -> RectangleShape
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = shape,
                    tonalElevation = 2.dp,
                    // Subtle shadow on outer edges
                    shadowElevation = if (isFirst || isLast) 1.dp else 0.dp
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        WordRow(
                            card = card,
                            onSpeak = onSpeak,
                            onClick = { onWordClick(card.id) }
                        )
                        if (!isLast) {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WordRow(
    card: WordEntity,
    onSpeak: (String) -> Unit,
    onClick: () -> Unit
) {
    val (displayText, pluralText, speakText) = remember(card) {
        if (isNoun(card.pos)) {
            val article = genderArticle(card.gender)
            val singularWithArticle = if (article.isNotBlank()) "$article ${card.lemma}" else card.lemma
            Triple(
                singularWithArticle,
                card.plural?.trim()?.takeIf { it.isNotEmpty() },
                singularWithArticle,
            )
        } else {
            Triple(card.lemma, null, card.lemma)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                IconButton(onClick = { onSpeak(speakText) }, modifier = Modifier.padding(start = 4.dp)) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Sprechen",
                        modifier = Modifier.padding(4.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }
            pluralText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 2.dp),
                )
            }
            card.exampleDe?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onSpeak(it) }, modifier = Modifier.padding(start = 4.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Sprechen",
                            modifier = Modifier.padding(4.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
            card.exampleEn?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                )
            }
        }
        Spacer(Modifier.padding(horizontal = 8.dp))
        Text(
            text = card.english ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.9f),
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
    wordCount: Int? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                val activeFilters = selectedLevels.size + selectedPosSet.size
                Text(
                    text = if (activeFilters > 0) "Filter ($activeFilters)" else "Filter",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                wordCount?.let {
                    Text(
                        text = "$it Wörter",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.End,
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                // Level filter
                Text("Niveau", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(bottom = 8.dp),
                ) {
                    items(LEVEL_FILTERS) { level ->
                        FilterChip(
                            selected = if (level == "Alle") selectedLevels.isEmpty() else selectedLevels.contains(level),
                            onClick = { onLevelToggle(level) },
                            label = { Text(level, style = MaterialTheme.typography.labelSmall) },
                        )
                    }
                }

                // POS filter
                Text("Wortart", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(bottom = 8.dp),
                ) {
                    items(posOptions) { pos ->
                        FilterChip(
                            selected = if (pos == "Alle") selectedPosSet.isEmpty() else selectedPosSet.contains(pos),
                            onClick = { onPosToggle(pos) },
                            label = { Text(POS_LABELS[pos] ?: pos, style = MaterialTheme.typography.labelSmall) },
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            }
        }
    }
}

// ─── Drill ────────────────────────────────────────────────────────────────────

private fun isNoun(pos: String): Boolean {
    val normalized = pos.trim().uppercase()
    return normalized == "N" || normalized == "NOMEN"
}

private fun genderArticle(gender: String?): String {
    val normalized = " ${gender?.trim()?.lowercase() ?: return ""} "
    val hasDer = Regex("""\bder\b""").containsMatchIn(normalized) ||
        Regex("""\bm\b""").containsMatchIn(normalized) ||
        Regex("""\br\b""").containsMatchIn(normalized)
    val hasDie = Regex("""\bdie\b""").containsMatchIn(normalized) ||
        Regex("""\bf\b""").containsMatchIn(normalized) ||
        Regex("""\be\b""").containsMatchIn(normalized)
    val hasDas = Regex("""\bdas\b""").containsMatchIn(normalized) ||
        Regex("""\bn\b""").containsMatchIn(normalized) ||
        Regex("""\bs\b""").containsMatchIn(normalized)

    return buildList {
        if (hasDer) add("der")
        if (hasDie) add("die")
        if (hasDas) add("das")
    }.joinToString("/")
}

@Composable
private fun DrillContent(
    state: WortschatzUiState,
    viewModel: WortschatzViewModel,
    onNavigateToHistory: (String) -> Unit,
    onSpeak: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DrillStatChip(
                label = "✓ ${state.drillCorrect}",
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.weight(1f).clickable { onNavigateToHistory("correct") }
            )
            DrillStatChip(
                label = "✗ ${state.drillWrong}",
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.weight(1f).clickable { onNavigateToHistory("incorrect") }
            )
            DrillStatChip("${state.drillAccuracy.roundToInt()}%", MaterialTheme.colorScheme.surfaceVariant, Modifier.weight(1f))
        }

        LinearProgressIndicator(
            progress = { state.drillProgress },
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        )
        Text(
            "${minOf(state.drillIndex + 1, state.drillQueue.size)} / ${state.drillQueue.size}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 4.dp),
        )

        if (state.drillDone) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DrillDoneCard(
                    correct = state.drillCorrect,
                    wrong = state.drillWrong,
                    accuracy = state.drillAccuracy,
                    onRestart = viewModel::restartDrill,
                )
            }
        } else if (state.drillQueue.isEmpty() && !state.isLoading) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
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
                val (displayFront, speakFront) = remember(card) {
                    if (isNoun(card.pos)) {
                        val article = genderArticle(card.gender)
                        val singularWithArticle = if (article.isNotBlank()) "$article ${card.lemma}" else card.lemma
                        val display = buildString {
                            append(singularWithArticle)
                            card.plural?.trim()?.takeIf { it.isNotEmpty() }?.let {
                                append("\n")
                                append(it)
                            }
                        }
                        display to singularWithArticle
                    } else {
                        card.lemma to card.lemma
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    DrillFlipCard(
                        card = card,
                        displayFront = displayFront,
                        speakFront = speakFront,
                        isFlipped = state.drillFlipped,
                        onFlip = viewModel::flip,
                        onSpeak = onSpeak,
                        onMarkCorrect = viewModel::markCorrect,
                        onMarkWrong = viewModel::markWrong,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (state.drillFlipped) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = viewModel::flip,
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
                            onClick = viewModel::markWrong,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor   = MaterialTheme.colorScheme.onErrorContainer,
                            ),
                            modifier = Modifier.weight(1f).height(48.dp),
                        ) { Text("✗ Falsch") }

                        Button(
                            onClick = viewModel::markCorrect,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor   = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                            modifier = Modifier.weight(1f).height(48.dp),
                        ) { Text("✓ Richtig") }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DrillFlipCard(
    card: WordEntity,
    displayFront: String,
    speakFront: String,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSpeak: (String) -> Unit,
    onMarkCorrect: () -> Unit,
    onMarkWrong: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val offsetX = remember(card) { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val rotationY by animateFloatAsState(
        targetValue    = if (isFlipped) 180f else 0f,
        animationSpec  = tween(durationMillis = 400),
        label          = "drill_flip",
    )

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer {
                rotationZ = (offsetX.value / 20).coerceIn(-15f, 15f)
                alpha = (1f - (abs(offsetX.value) / 800f)).coerceAtLeast(0.6f)
            }
            .pointerInput(card, isFlipped) {
                if (!isFlipped) return@pointerInput
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val threshold = size.width / 4f
                        if (offsetX.value > threshold) {
                            scope.launch {
                                offsetX.animateTo(size.width.toFloat() * 1.5f, tween(300))
                                onMarkCorrect()
                            }
                        } else if (offsetX.value < -threshold) {
                            scope.launch {
                                offsetX.animateTo(-size.width.toFloat() * 1.5f, tween(300))
                                onMarkWrong()
                            }
                        } else {
                            scope.launch { offsetX.animateTo(0f, tween(300)) }
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                    }
                )
            }
            .then(if (!isFlipped) Modifier.clickable { onFlip() } else Modifier)
    ) {
        if (rotationY <= 90f) {
            DrillCardFace(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { this.rotationY = rotationY },
            ) {
                // Level + POS badge row
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
                    text  = displayFront,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight   = FontWeight.SemiBold,
                    textAlign    = TextAlign.Center,
                )
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
                Text(
                    text  = card.english ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.primary,
                    textAlign  = TextAlign.Center,
                    modifier   = Modifier.fillMaxWidth()
                )
                card.exampleDe?.let { ex ->
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        shape    = MaterialTheme.shapes.small,
                        color    = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text     = ex,
                                    style    = MaterialTheme.typography.titleMedium,
                                    fontStyle = FontStyle.Italic,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { onSpeak(ex) },
                                    modifier = Modifier.offset(y = (-4).dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Sprechen")
                                }
                            }
                            card.exampleEn?.let { en ->
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text  = en,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                )
                            }
                        }
                    }
                }
            }

            // Swipe indicators
            if (abs(offsetX.value) > 50) {
                val alpha = ((abs(offsetX.value) - 50) / 150f).coerceIn(0f, 1f)
                val isCorrect = offsetX.value > 0
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = if (isCorrect) Alignment.TopStart else Alignment.TopEnd
                ) {
                    Surface(
                        color = (if (isCorrect) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer).copy(alpha = alpha),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(2.dp, if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    ) {
                        Text(
                            text = if (isCorrect) "RICHTIG" else "FALSCH",
                            color = if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrillCardFace(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    Card(
        modifier  = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier              = Modifier
                .fillMaxWidth()
                .heightIn(min = 340.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Center,
            content               = content,
        )
    }
}

@Composable
private fun DrillStatChip(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Surface(shape = MaterialTheme.shapes.small, color = color, modifier = modifier) {
        Text(
            label,
            style     = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(vertical = 4.dp),
        )
    }
}

@Composable
private fun WortschatzSkeleton() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        repeat(6) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        ShimmerItem(height = 20.dp, widthFraction = 0.6f)
                        Spacer(Modifier.height(8.dp))
                        ShimmerItem(height = 14.dp, widthFraction = 0.4f)
                    }
                    ShimmerItem(height = 16.dp, widthFraction = 0.2f)
                }
            }
        }
    }
}

@Composable
private fun DrillSkeleton() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(3) {
                ShimmerItem(height = 32.dp, modifier = Modifier.weight(1f))
            }
        }

        LinearProgressIndicator(
            progress = { 0f },
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        )

        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().heightIn(min = 340.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ShimmerItem(height = 24.dp, widthFraction = 0.2f)
                            ShimmerItem(height = 24.dp, widthFraction = 0.2f)
                        }
                        ShimmerItem(height = 24.dp, widthFraction = 0.1f)
                    }
                    Spacer(Modifier.height(32.dp))
                    ShimmerItem(height = 40.dp, widthFraction = 0.7f)
                    Spacer(Modifier.height(16.dp))
                    ShimmerItem(height = 14.dp, widthFraction = 0.4f)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun DrillDoneCard(correct: Int, wrong: Int, accuracy: Float, onRestart: () -> Unit) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Runde abgeschlossen! 🎉", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "Richtig: $correct  |  Falsch: $wrong  |  Quote: ${accuracy.roundToInt()}%",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onRestart, modifier = Modifier.height(48.dp)) {
            Text("Neu starten")
        }
    }
}

package com.germanverbmaster.android.ui.wortschatz

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.germanverbmaster.android.data.local.entity.WordEntity
import com.germanverbmaster.android.ui.components.ExamCountdownBanner
import java.time.LocalDate

@Composable
fun WortschatzScreen(
    viewModel: WortschatzViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        ExamCountdownBanner(
            examDate = LocalDate.of(2026, 4, 30),
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
        )

        // Tab row: Wortliste | Schnell-Drill
        PrimaryTabRow(selectedTabIndex = WortschatzTab.entries.indexOf(state.tab)) {
            WortschatzTab.entries.forEach { tab ->
                Tab(
                    selected = state.tab == tab,
                    onClick = { viewModel.selectTab(tab) },
                    text = { Text(tab.label, style = MaterialTheme.typography.labelLarge) },
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Level filter
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 4.dp),
        ) {
            items(LEVEL_FILTERS) { level ->
                FilterChip(
                    selected = state.selectedLevel == level,
                    onClick = { viewModel.selectLevel(level) },
                    label = { Text(level, style = MaterialTheme.typography.labelSmall) },
                )
            }
        }

        // POS filter
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 8.dp),
        ) {
            items(POS_FILTERS) { pos ->
                FilterChip(
                    selected = state.selectedPos == pos,
                    onClick = { viewModel.selectPos(pos) },
                    label = { Text(POS_LABELS[pos] ?: pos, style = MaterialTheme.typography.labelSmall) },
                )
            }
        }

        // Count label
        if (!state.isLoading) {
            Text(
                "${state.listCards.size} Wörter",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 6.dp),
            )
        }

        // Sync error banner
        state.syncError?.let { err ->
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            ) {
                Text(
                    err,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(8.dp),
                )
            }
        }

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Wörter werden geladen…", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            else -> when (state.tab) {
                WortschatzTab.LIST  -> WordListContent(state.listCards)
                WortschatzTab.DRILL -> DrillContent(state, viewModel)
            }
        }
    }
}

// ─── Word List ────────────────────────────────────────────────────────────────

@Composable
private fun WordListContent(cards: List<WordEntity>) {
    if (cards.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Keine Wörter für diese Filter.", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    // Group by POS for readability
    val grouped = cards.groupBy { it.pos }
    val posOrder = listOf("V", "N", "Adj")
    val posNames = mapOf("V" to "Verben", "N" to "Nomen", "Adj" to "Adjektive")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        val sortedGroups = grouped.entries.sortedBy { posOrder.indexOf(it.key) }
        sortedGroups.forEach { (pos, groupCards) ->
            item {
                Text(
                    text = posNames[pos] ?: pos,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
                )
            }
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        groupCards.forEachIndexed { i, card ->
                            WordRow(card)
                            if (i < groupCards.lastIndex) {
                                HorizontalDivider(thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WordRow(card: WordEntity) {
    // Build display label: add article and plural for nouns
    val displayLemma = if (card.pos == "N") {
        val article = card.gender?.let { genderArticle(it) } ?: ""
        val lemmaWithArticle = if (article.isNotEmpty()) "$article ${card.lemma}" else card.lemma
        if (!card.plural.isNullOrBlank()) "$lemmaWithArticle, die ${card.plural}" else lemmaWithArticle
    } else {
        card.lemma
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayLemma,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            card.exampleDe?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                )
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

private fun genderArticle(gender: String): String = when (gender.lowercase()) {
    "m"          -> "der"
    "f"          -> "die"
    "n"          -> "das"
    else         -> ""
}

// ─── Drill ────────────────────────────────────────────────────────────────────

@Composable
private fun DrillContent(state: WortschatzUiState, viewModel: WortschatzViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DrillStatChip("✓ ${state.drillCorrect}", MaterialTheme.colorScheme.primaryContainer, Modifier.weight(1f))
            DrillStatChip("✗ ${state.drillWrong}",   MaterialTheme.colorScheme.errorContainer,   Modifier.weight(1f))
            DrillStatChip("${state.drillAccuracy.toInt()}%", MaterialTheme.colorScheme.surfaceVariant, Modifier.weight(1f))
        }

        LinearProgressIndicator(
            progress = { state.drillProgress },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        )
        Text(
            "${minOf(state.drillIndex + 1, state.drillQueue.size)} / ${state.drillQueue.size}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 8.dp),
        )

        if (state.drillDone) {
            DrillDoneCard(
                correct  = state.drillCorrect,
                wrong    = state.drillWrong,
                accuracy = state.drillAccuracy,
                onRestart = viewModel::restartDrill,
            )
        } else {
            state.drillCurrent?.let { card ->
                DrillFlipCard(
                    card      = card,
                    isFlipped = state.drillFlipped,
                    onFlip    = viewModel::flip,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )

                if (state.drillFlipped) {
                    Spacer(Modifier.height(12.dp))
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

                        OutlinedButton(
                            onClick  = viewModel::skip,
                            modifier = Modifier.weight(1f).height(48.dp),
                        ) { Text("→ Skip") }

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
    isFlipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue    = if (isFlipped) 180f else 0f,
        animationSpec  = tween(durationMillis = 400),
        label          = "drill_flip",
    )

    val displayFront = if (card.pos == "N") {
        val article = card.gender?.let { genderArticle(it) } ?: ""
        val lemmaWithArticle = if (article.isNotEmpty()) "$article ${card.lemma}" else card.lemma
        if (!card.plural.isNullOrBlank()) "$lemmaWithArticle, die ${card.plural}" else lemmaWithArticle
    } else {
        card.lemma
    }

    Box(modifier = modifier.clickable { onFlip() }) {
        if (rotation <= 90f) {
            DrillCardFace(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = rotation },
            ) {
                // Level + POS badge row
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

        if (rotation > 90f) {
            DrillCardFace(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = rotation - 180f },
            ) {
                Text(
                    text  = card.english ?: "",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.primary,
                    textAlign  = TextAlign.Center,
                )
                card.exampleDe?.let { ex ->
                    Spacer(Modifier.height(14.dp))
                    Surface(
                        shape    = MaterialTheme.shapes.small,
                        color    = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text     = ex,
                                style    = MaterialTheme.typography.titleLarge,
                                fontStyle = FontStyle.Italic,
                            )
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
        }
    }
}

@Composable
private fun DrillCardFace(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(
        modifier  = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier              = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Center,
            content               = content,
        )
    }
}

@Composable
private fun DrillStatChip(label: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Surface(shape = MaterialTheme.shapes.small, color = color, modifier = modifier) {
        Text(
            label,
            style     = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(vertical = 6.dp),
        )
    }
}

@Composable
private fun DrillDoneCard(correct: Int, wrong: Int, accuracy: Float, onRestart: () -> Unit) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Runde abgeschlossen! 🎉", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "Richtig: $correct  |  Falsch: $wrong  |  Quote: ${accuracy.toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onRestart, modifier = Modifier.height(48.dp)) {
            Text("Neu starten")
        }
    }
}

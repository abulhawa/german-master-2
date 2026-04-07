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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
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
import com.germanverbmaster.android.domain.model.B2Card
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

        // Topic filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 8.dp),
        ) {
            items(state.topics) { topic ->
                FilterChip(
                    selected = state.selectedTopic == topic,
                    onClick = { viewModel.selectTopic(topic) },
                    label = { Text(topic, style = MaterialTheme.typography.labelSmall) },
                )
            }
        }

        when (state.tab) {
            WortschatzTab.LIST  -> WordListContent(state.listCards)
            WortschatzTab.DRILL -> DrillContent(state, viewModel)
        }
    }
}

// ─── Word List ────────────────────────────────────────────────────────────────

@Composable
private fun WordListContent(cards: List<B2Card>) {
    if (cards.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Keine Wörter für dieses Thema.", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    // Group by topic sub-label
    val grouped = cards.groupBy { it.topic ?: "Allgemein" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        grouped.forEach { (topic, groupCards) ->
            item {
                Text(
                    text = topic,
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
private fun WordRow(card: B2Card) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = card.front,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = card.example,
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            )
        }
        Spacer(Modifier.padding(horizontal = 8.dp))
        Text(
            text = card.back,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.9f),
        )
    }
}

// ─── Drill ────────────────────────────────────────────────────────────────────

@Composable
private fun ColumnScope.DrillContent(state: WortschatzUiState, viewModel: WortschatzViewModel) {
    // Stats row
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DrillStatChip("✓ ${state.drillCorrect}", MaterialTheme.colorScheme.primaryContainer, Modifier.weight(1f))
        DrillStatChip("✗ ${state.drillWrong}", MaterialTheme.colorScheme.errorContainer, Modifier.weight(1f))
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
            correct = state.drillCorrect,
            wrong = state.drillWrong,
            accuracy = state.drillAccuracy,
            onRestart = viewModel::restartDrill,
        )
    } else {
        state.drillCurrent?.let { card ->
            DrillFlipCard(
                card = card,
                isFlipped = state.drillFlipped,
                onFlip = viewModel::flip,
                modifier = Modifier
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
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) { Text("✗ Falsch") }

                    OutlinedButton(
                        onClick = viewModel::skip,
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) { Text("→ Skip") }

                    Button(
                        onClick = viewModel::markCorrect,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) { Text("✓ Richtig") }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrillFlipCard(
    card: B2Card,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "drill_flip",
    )

    Box(modifier = modifier.clickable { if (!isFlipped) onFlip() }) {
        if (rotation <= 90f) {
            DrillCardFace(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = rotation },
            ) {
                // Topic badge
                card.topic?.let { topic ->
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text(
                            topic,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
                Text(
                    text = card.front,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
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
                    text = card.back,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(14.dp))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = card.example,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(12.dp),
                    )
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
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content,
        )
    }
}

@Composable
private fun DrillStatChip(label: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Surface(shape = MaterialTheme.shapes.small, color = color, modifier = modifier) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp),
        )
    }
}

@Composable
private fun DrillDoneCard(correct: Int, wrong: Int, accuracy: Float, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
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

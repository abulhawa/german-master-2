package com.germanverbmaster.android.ui.b2practice

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.SuggestionChip
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
import com.germanverbmaster.android.domain.model.B2Category
import com.germanverbmaster.android.domain.model.CardMode
import com.germanverbmaster.android.ui.components.ExamCountdownBanner
import java.time.LocalDate

@Composable
fun B2PracticeScreen(
    viewModel: B2PracticeViewModel = hiltViewModel(),
    onNavigateToHistory: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Exam countdown
        ExamCountdownBanner(
            examDate = LocalDate.of(2026, 4, 30),
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
        )

        // Category tabs
        PrimaryScrollableTabRow(
            selectedTabIndex = B2Category.entries.indexOf(state.category),
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            B2Category.entries.forEach { cat ->
                Tab(
                    selected = state.category == cat,
                    onClick = { viewModel.setCategory(cat) },
                    text = { Text(cat.label, style = MaterialTheme.typography.labelLarge) },
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (state.category == B2Category.GRAMMAR) {
            GrammarTabContent()
        } else {
            // Mode + shuffle row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CardMode.entries.forEach { m ->
                    FilterChip(
                        selected = state.mode == m,
                        onClick = { viewModel.setMode(m) },
                        label = { Text(m.label, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.padding(end = 4.dp),
                    )
                }
                IconToggleButton(
                    checked = state.shuffle,
                    onCheckedChange = { viewModel.toggleShuffle() },
                ) {
                    Icon(Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (state.shuffle) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            }

            // Stats bar
            StatsBar(
                correct = state.correct,
                wrong = state.wrong,
                accuracy = state.accuracy,
                onNavigateToHistory = onNavigateToHistory
            )

            // Progress
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            )
            Text(
                "${minOf(state.currentIndex + 1, state.queue.size)} / ${state.queue.size}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End),
            )

            Spacer(Modifier.height(8.dp))

            if (state.sessionDone) {
                SessionDoneCard(
                    correct = state.correct,
                    wrong = state.wrong,
                    accuracy = state.accuracy,
                    onRestart = viewModel::restart,
                )
            } else {
                state.current?.let { card ->
                    FlipCard(
                        card = card,
                        mode = state.mode,
                        isFlipped = state.isFlipped,
                        onFlip = viewModel::flip,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )

                    if (state.isFlipped) {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            // Wrong
                            Button(
                                onClick = viewModel::markWrong,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                ),
                                modifier = Modifier.weight(1f).height(48.dp),
                            ) { Text("✗ Falsch") }

                            // Skip
                            OutlinedButton(
                                onClick = viewModel::skip,
                                modifier = Modifier.weight(1f).height(48.dp),
                            ) { Text("→ Skip") }

                            // Correct
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
    }
}

@Composable
fun FlipCard(
    card: B2Card,
    mode: CardMode,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "flip",
    )

    Box(modifier = modifier.clickable { if (!isFlipped) onFlip() }) {
        // Front face
        if (rotation <= 90f) {
            CardFace(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = rotation },
            ) {
                val frontText = when (mode) {
                    CardMode.DE_TO_EN -> card.front
                    CardMode.EN_TO_DE -> card.back
                    CardMode.EXAMPLE  -> card.example
                }
                Text(
                    text = frontText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                card.preposition?.let {
                    Spacer(Modifier.height(8.dp))
                    SuggestionChip(
                        onClick = {},
                        label = { Text(it, style = MaterialTheme.typography.bodyMedium) },
                    )
                }
                card.topic?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Tippen zum Aufdecken",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                )
            }
        }

        // Back face
        if (rotation > 90f) {
            CardFace(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = rotation - 180f },
            ) {
                val backText = when (mode) {
                    CardMode.DE_TO_EN -> card.back
                    CardMode.EN_TO_DE -> card.front
                    CardMode.EXAMPLE  -> card.front
                }
                Text(
                    text = backText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )
                card.preposition?.let {
                    Spacer(Modifier.height(8.dp))
                    SuggestionChip(onClick = {}, label = { Text(it) })
                }
                Spacer(Modifier.height(12.dp))
                // Example sentence
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
private fun CardFace(
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
fun StatsBar(correct: Int, wrong: Int, accuracy: Float, onNavigateToHistory: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatChip(
            label = "✓ $correct",
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.weight(1f).clickable { onNavigateToHistory("correct") }
        )
        StatChip(
            label = "✗ $wrong",
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.weight(1f).clickable { onNavigateToHistory("incorrect") }
        )
        StatChip("${accuracy.toInt()}%", MaterialTheme.colorScheme.surfaceVariant, Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
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
fun SessionDoneCard(correct: Int, wrong: Int, accuracy: Float, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Runde abgeschlossen! 🎉", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text("Richtig: $correct  |  Falsch: $wrong  |  Quote: ${accuracy.toInt()}%",
            style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onRestart, modifier = Modifier.height(48.dp)) {
            Text("Neu starten")
        }
    }
}

@Composable
fun GrammarTabContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
    ) {
        items(B2ContentData.deklTables) { table ->
            DeklTableCard(table)
        }
        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Wichtige B2-Adjektive",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    B2ContentData.b2Adjectives.forEach { (adj, en, ex) ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(adj, fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f))
                            Text(en, style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f))
                        }
                        Text(ex, style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 4.dp))
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun DeklTableCard(table: com.germanverbmaster.android.domain.model.DeklTable) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(table.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(table.note, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(Modifier.height(10.dp))
            // Header
            Row(Modifier.fillMaxWidth()) {
                listOf("Kasus", "Mask.", "Fem.", "Neut.", "Pl.").forEach { h ->
                    Text(h, fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.weight(1f))
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            table.rows.forEach { row ->
                Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Text(row.kasus, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    listOf(row.maskulin, row.feminin, row.neutrum, row.plural).forEach { ending ->
                        Text(ending, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            table.examples.forEach { ex ->
                Text("• $ex", style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 2.dp))
            }
        }
    }
}

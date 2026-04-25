package com.germanverbmaster.android.ui.history

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.ui.wortschatz.FilterBottomSheet
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AnswerHistoryScreen(
    viewModel: AnswerHistoryViewModel = hiltViewModel(),
    onNavigateToWordDetail: (Int) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showFilterSheet = remember { mutableStateOf(false) }

    if (showFilterSheet.value) {
        FilterBottomSheet(
            selectedLevels = emptySet(), // No level filter in history yet
            onLevelToggle = {},
            selectedPosSet = state.filterPosSet,
            onPosToggle = viewModel::togglePos,
            posOptions = state.posOptions,
            wordCount = if (state.isLoading) null else state.attempts.size,
            onDismiss = { showFilterSheet.value = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Verlauf",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            BadgedBox(
                badge = {
                    if (state.filterPosSet.isNotEmpty()) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(state.filterPosSet.size.toString())
                        }
                    }
                },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                IconButton(
                    onClick = { showFilterSheet.value = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filter",
                        tint = if (state.filterPosSet.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = state.filterResult == null,
                onClick = { viewModel.setFilterResult(null) },
                label = { Text("Alle") }
            )
            FilterChip(
                selected = state.filterResult == "correct",
                onClick = { viewModel.setFilterResult("correct") },
                label = { Text("Richtig") }
            )
            FilterChip(
                selected = state.filterResult == "incorrect",
                onClick = { viewModel.setFilterResult("incorrect") },
                label = { Text("Falsch") }
            )
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.attempts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Keine Einträge gefunden", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            val scope = androidx.compose.runtime.rememberCoroutineScope()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.attempts,
                    key = { it.localId } // Optimization: Stable keys for list items
                ) { attempt ->
                    HistoryItem(
                        attempt = attempt,
                        onClick = {
                            scope.launch {
                                viewModel.getWordIdForHistory(attempt)?.let { wordId ->
                                    onNavigateToWordDetail(wordId)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    attempt: PracticeHistoryEntity,
    onClick: () -> Unit = {}
) {
    val isCorrect = attempt.result == "correct"
    val displayLemma = attempt.lemma.ifBlank { "Unbekannt" }

    val dateStr = remember(attempt.submittedAt) {
        try {
            // Handle various ISO formats robustly
            val accessor = DateTimeFormatter.ISO_DATE_TIME.parse(attempt.submittedAt)
            val instant = Instant.from(accessor)
            DateTimeFormatter.ofPattern("dd.MM. HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(instant)
        } catch (_: Exception) {
            attempt.submittedAt
        }
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    displayLemma,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    attempt.taskType.replace("_", " "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(Modifier.height(4.dp))
                
                val isDrill = attempt.taskType == "vocabulary_drill" || attempt.renderer == "word_card"
                
                if (!isDrill) {
                    if (attempt.submittedAnswer.isNotBlank()) {
                        Row {
                            Text("Deine Antwort: ", style = MaterialTheme.typography.bodySmall)
                            Text(
                                attempt.submittedAnswer,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    if (!isCorrect && attempt.correctAnswer.isNotBlank()) {
                        Row {
                            Text("Richtig: ", style = MaterialTheme.typography.bodySmall)
                            Text(
                                attempt.correctAnswer,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    if (isCorrect) "✓" else "✗",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Text(
                    dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

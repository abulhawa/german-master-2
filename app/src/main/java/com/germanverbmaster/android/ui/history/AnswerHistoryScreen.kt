package com.germanverbmaster.android.ui.history

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AnswerHistoryScreen(viewModel: AnswerHistoryViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Verlauf",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = state.filterPos == null,
                onClick = { viewModel.setFilterPos(null) },
                label = { Text("Alle POS") }
            )
            FilterChip(
                selected = state.filterPos == "V",
                onClick = { viewModel.setFilterPos("V") },
                label = { Text("Verben") }
            )
            FilterChip(
                selected = state.filterPos == "N",
                onClick = { viewModel.setFilterPos("N") },
                label = { Text("Nomen") }
            )
            FilterChip(
                selected = state.filterPos == "Adj",
                onClick = { viewModel.setFilterPos("Adj") },
                label = { Text("Adj.") }
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.attempts,
                    key = { it.localId } // Optimization: Stable keys for list items
                ) { attempt ->
                    HistoryItem(attempt)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(attempt: PracticeHistoryEntity) {
    val isCorrect = attempt.result == "correct"
    val formatter = DateTimeFormatter.ofPattern("dd.MM. HH:mm")
        .withZone(ZoneId.systemDefault())
    val dateStr = try {
        formatter.format(Instant.parse(attempt.submittedAt))
    } catch (_: Exception) {
        attempt.submittedAt
    }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        attempt.lemma,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        attempt.taskType.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row {
                    Text("Deine Antwort: ", style = MaterialTheme.typography.bodySmall)
                    Text(
                        attempt.submittedAnswer,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
                if (!isCorrect) {
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

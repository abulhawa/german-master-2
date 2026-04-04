package com.germanverbmaster.android.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.germanverbmaster.android.domain.model.PracticeMode
import com.germanverbmaster.android.ui.components.ExamCountdownBanner
import com.germanverbmaster.android.ui.components.PracticeCard
import java.time.LocalDate

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        // Offline banner
        if (state.isOffline) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    "Offline — Daten aus lokalem Cache",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(8.dp),
                )
            }
        }

        ExamCountdownBanner(
            examDate = LocalDate.of(2026, 4, 30),
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
        )

        // Mode + Level selectors
        ModeSelector(
            current = state.mode,
            b2ExamMode = state.b2ExamMode,
            onModeChange = viewModel::setMode,
            onToggleB2 = viewModel::toggleB2ExamMode,
        )

        if (!state.b2ExamMode) {
            LevelSelector(current = state.cefrLevel, onChange = viewModel::setCefrLevel)
        }

        Spacer(Modifier.height(12.dp))

        // Session stats
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("✓ ${state.stats.correct}", "Richtig", Modifier.weight(1f))
            StatCard("✗ ${state.stats.incorrect}", "Falsch", Modifier.weight(1f))
            StatCard("${state.stats.accuracy.toInt()}%", "Quote", Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        // Practice card area
        when {
            state.isLoading || state.isSyncing -> {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text(if (state.isSyncing) "Synchronisiere Daten…" else "Lade Aufgaben…",
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            state.currentTask != null -> {
                PracticeCard(
                    task = state.currentTask!!,
                    onCorrect = { submitted, correct, ms -> 
                        viewModel.submitResult(state.currentTask!!, true, submitted, correct, ms) 
                    },
                    onWrong = { submitted, correct, ms -> 
                        viewModel.submitResult(state.currentTask!!, false, submitted, correct, ms) 
                    },
                    onSkip = viewModel::skip,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                )
            }
            else -> {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Keine Aufgaben verfügbar",
                            style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.setMode(state.mode) }) {
                            Text("Neu laden")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModeSelector(
    current: PracticeMode,
    b2ExamMode: Boolean,
    onModeChange: (PracticeMode) -> Unit,
    onToggleB2: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            listOf(
                PracticeMode.ALL to "Alle",
                PracticeMode.VERBS to "Verben",
                PracticeMode.NOUNS to "Nomen",
                PracticeMode.ADJECTIVES to "Adj.",
            ).forEach { (mode, label) ->
                FilterChip(
                    selected = current == mode && !b2ExamMode,
                    onClick = { onModeChange(mode) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp),
        ) {
            Switch(checked = b2ExamMode, onCheckedChange = { onToggleB2() })
            Spacer(Modifier.width(8.dp))
            Text("B2 Prüfungsmodus (B1+B2, alle Typen)",
                style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun LevelSelector(current: String, onChange: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf("A1","A2","B1","B2","C1").forEach { level ->
            FilterChip(
                selected = current == level,
                onClick = { onChange(level) },
                label = { Text(level) },
            )
        }
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

package com.germanverbmaster.android.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.germanverbmaster.android.domain.model.PracticeMode
import com.germanverbmaster.android.domain.model.TaskCard
import com.germanverbmaster.android.ui.components.ExamCountdownBanner
import com.germanverbmaster.android.ui.components.PracticeCard
import com.germanverbmaster.android.ui.components.ShimmerItem
import java.time.LocalDate

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToHistory: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreenContent(
        state = state,
        onSetMode = viewModel::setMode,
        onSetCefrLevel = viewModel::setCefrLevel,
        onRefresh = viewModel::refresh,
        onNavigateToHistory = onNavigateToHistory,
        onSubmitResult = viewModel::submitResult
    )
}

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onSetMode: (PracticeMode) -> Unit,
    onSetCefrLevel: (String?) -> Unit,
    onRefresh: () -> Unit,
    onNavigateToHistory: (String) -> Unit,
    onSubmitResult: (TaskCard, Boolean, String, String, Int) -> Unit,
) {
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

        FilterSection(
            mode = state.mode,
            onModeChange = onSetMode,
            cefrLevel = state.cefrLevel,
            onLevelChange = onSetCefrLevel,
        )

        Spacer(Modifier.height(12.dp))

        // Session stats
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                "✓ ${state.stats.correct}",
                "Richtig",
                Modifier.weight(1f).clickable { onNavigateToHistory("correct") }
            )
            StatCard(
                "✗ ${state.stats.incorrect}",
                "Falsch",
                Modifier.weight(1f).clickable { onNavigateToHistory("incorrect") }
            )
            StatCard("${state.stats.accuracy.toInt()}%", "Quote", Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        // Practice card area
        when {
            state.isLoading -> {
                PracticeSkeleton(modifier = Modifier.fillMaxWidth().weight(1f))
            }
            state.currentTask != null -> {
                PracticeCard(
                    task = state.currentTask!!,
                    onCorrect = { submitted, correct, ms -> 
                        onSubmitResult(state.currentTask!!, true, submitted, correct, ms) 
                    },
                    onWrong = { submitted, correct, ms -> 
                        onSubmitResult(state.currentTask!!, false, submitted, correct, ms) 
                    },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                )
            }
            state.isSyncing -> {
                PracticeSkeleton(modifier = Modifier.fillMaxWidth().weight(1f))
            }
            else -> {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Keine Aufgaben verfügbar",
                            style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onRefresh) {
                            Text("Neu laden")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeSkeleton(modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ShimmerItem(height = 20.dp, widthFraction = 0.15f)
                ShimmerItem(height = 16.dp, widthFraction = 0.3f)
            }
            
            Spacer(Modifier.height(32.dp))
            
            ShimmerItem(height = 16.dp, widthFraction = 0.4f)
            Spacer(Modifier.height(16.dp))
            ShimmerItem(height = 36.dp, widthFraction = 0.7f)
            Spacer(Modifier.height(12.dp))
            ShimmerItem(height = 20.dp, widthFraction = 0.5f)
            
            Spacer(Modifier.height(48.dp))
            
            ShimmerItem(height = 56.dp, widthFraction = 1f) // TextField shimmer
            
            Spacer(Modifier.weight(1f))
            
            ShimmerItem(height = 48.dp, widthFraction = 1f) // Button shimmer
        }
    }
}

@Composable
fun FilterSection(
    mode: PracticeMode,
    onModeChange: (PracticeMode) -> Unit,
    cefrLevel: String?,
    onLevelChange: (String?) -> Unit,
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
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Filter",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                ModeSelector(
                    current = mode,
                    onModeChange = onModeChange,
                )
                Spacer(Modifier.height(8.dp))
                LevelSelector(
                    current = cefrLevel,
                    onChange = onLevelChange
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun ModeSelector(
    current: PracticeMode,
    onModeChange: (PracticeMode) -> Unit,
) {
    Column {
        Text("Wortart", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
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
                    selected = current == mode,
                    onClick = { onModeChange(mode) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                )
            }
        }
    }
}

@Composable
fun LevelSelector(current: String?, onChange: (String?) -> Unit) {
    Column {
        Text("Niveau", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // "Alle" chip — passes null to disable level filtering
            FilterChip(
                selected = current == null,
                onClick = { onChange(null) },
                label = { Text("Alle", style = MaterialTheme.typography.labelSmall) },
            )
            listOf("A1", "A2", "B1", "B2", "C1").forEach { level ->
                FilterChip(
                    selected = current == level,
                    onClick = { onChange(level) },
                    label = { Text(level, style = MaterialTheme.typography.labelSmall) },
                )
            }
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

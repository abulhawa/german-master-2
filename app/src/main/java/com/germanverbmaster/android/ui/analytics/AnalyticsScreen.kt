package com.germanverbmaster.android.ui.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Analytics", style = MaterialTheme.typography.headlineSmall)

        // Today's stats
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElevatedCard(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${state.todayAccuracy.toInt()}%",
                        style = MaterialTheme.typography.headlineMedium)
                    Text("Genauigkeit heute", style = MaterialTheme.typography.labelSmall)
                }
            }
            ElevatedCard(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${state.todayTotal}",
                        style = MaterialTheme.typography.headlineMedium)
                    Text("Aufgaben heute", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Placeholder for chart — Gemini fills this in with Vico
        ElevatedCard(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Verlaufsdiagramm (Vico chart hier einbauen)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }

        Text("Aufgabentyp-Verteilung", style = MaterialTheme.typography.titleMedium)
        state.byTaskType.forEach { (type, count) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(type, style = MaterialTheme.typography.bodyMedium)
                Text("$count", style = MaterialTheme.typography.bodyMedium)
            }
            LinearProgressIndicator(
                progress = { if (state.todayTotal == 0) 0f else count.toFloat() / state.todayTotal },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            )
        }
    }
}

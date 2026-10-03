package com.germanverbmaster.android.ui.analytics

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart

@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Analytics",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // 1. Chart Item
        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Accuracy Trend (Last 7 Days)", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(16.dp))
                    
                    if (state.dailyAccuracy.isEmpty()) {
                        Box(Modifier.height(200.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Not enough data for chart", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        val modelProducer = remember { CartesianChartModelProducer() }
                        LaunchedEffect(state.dailyAccuracy) {
                            modelProducer.runTransaction {
                                lineSeries {
                                    series(state.dailyAccuracy.map { it.accuracy })
                                }
                            }
                        }
                        
                        CartesianChartHost(
                            chart = rememberCartesianChart(
                                rememberLineCartesianLayer(),
                                startAxis = VerticalAxis.rememberStart(),
                                bottomAxis = HorizontalAxis.rememberBottom(),
                            ),
                            modelProducer = modelProducer,
                            modifier = Modifier.height(200.dp)
                        )
                    }
                }
            }
        }

        // 2. Today's Summary
        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Today's Progress", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "${state.accuracyToday.toInt()}%",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Accuracy", style = MaterialTheme.typography.labelMedium)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "${state.totalToday}",
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Text("Total Tasks", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // 3. Task Type Breakdown
        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Performance by Task Type", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(16.dp))
                    
                    if (state.taskTypeStats.isEmpty()) {
                        Box(Modifier.height(100.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No data yet", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        val locale = LocalLocale.current.platformLocale
                        
                        // Use items/itemsIndexed if this list ever grows large, 
                        // but for a small fixed set of categories, we just need to avoid 
                        // redundant recompositions by using remember or unique keys if it were in a LazyColumn.
                        // Here it's inside a Column inside an 'item', so it's not lazy.
                        state.taskTypeStats.forEach { stat ->
                            TaskTypeStatRow(
                                label = stat.taskType.replace("_", " ").replaceFirstChar { 
                                    if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                                },
                                correct = stat.correctCount,
                                total = stat.totalCount
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskTypeStatRow(label: String, correct: Int, total: Int) {
    val progress = if (total == 0) 0f else correct.toFloat() / total
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("${(progress * 100).toInt()}% ($correct/$total)", style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

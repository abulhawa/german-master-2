package com.germanverbmaster.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.domain.model.TaskCard

/**
 * Renders a practice card for any of the 3 task types:
 *   - conjugate_form  : fill in verb form
 *   - noun_case_declension : fill in article+noun or choose ending
 *   - adj_ending : fill in adjective ending
 */
@Composable
fun PracticeCard(
    task: TaskCard,
    onCorrect: (submitted: String, correct: String, responseMs: Int) -> Unit,
    onWrong: (submitted: String, correct: String, responseMs: Int) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var answer by remember(task.taskId) { mutableStateOf("") }
    var revealed by remember(task.taskId) { mutableStateOf(false) }
    var hintsUsedCount by remember(task.taskId) { mutableIntStateOf(0) }
    val startMs = remember(task.taskId) { System.currentTimeMillis() }

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
            // Header: CEFR + Task Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                task.cefrLevel?.let {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            it,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Text(
                    task.taskType.replace("_", " ").uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Prompt
            PracticePrompt(task)

            Spacer(Modifier.height(24.dp))

            if (!revealed) {
                // Input area
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Antwort") },
                    placeholder = { Text("Hier schreiben...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Hints area
                if (task.hints.isNotEmpty() && hintsUsedCount < task.hints.size) {
                    TextButton(
                        onClick = { hintsUsedCount++ },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Tipp anzeigen (${hintsUsedCount}/${task.hints.size})")
                    }
                }

                if (hintsUsedCount > 0) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            for (i in 0 until hintsUsedCount) {
                                Text("• ${task.hints[i]}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onSkip,
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) { Text("Überspringen") }
                    Button(
                        onClick = { revealed = true },
                        modifier = Modifier.weight(1f).height(48.dp),
                        enabled = answer.isNotBlank(),
                    ) { Text("Prüfen") }
                }
            } else {
                // Result area
                val solution = task.solution["form"]
                    ?: task.solution["answer"]
                    ?: task.solution.values.firstOrNull()
                    ?: "—"

                val isCorrect = answer.trim().equals(solution.trim(), ignoreCase = true)

                ResultDisplay(isCorrect, answer, solution, task)

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        val ms = (System.currentTimeMillis() - startMs).toInt()
                        if (isCorrect) onCorrect(answer, solution, ms) else onWrong(answer, solution, ms)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) { Text("Weiter →") }
            }
        }
    }
}

@Composable
fun PracticePrompt(task: TaskCard) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when (task.taskType) {
            "conjugate_form" -> {
                Text(
                    task.prompt["pronoun"] ?: "er/sie/es",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "___",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "(${task.lemma})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            "noun_case_declension" -> {
                Text(
                    task.prompt["case"] ?: "Nominativ",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    task.prompt["context"] ?: task.lemma,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            "adj_ending" -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(task.prompt["article_type"] ?: "", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        task.lemma,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("___", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.width(8.dp))
                    Text(task.prompt["noun"] ?: "", style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                Text(task.lemma, style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}

@Composable
fun ResultDisplay(isCorrect: Boolean, submitted: String, solution: String, task: TaskCard) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isCorrect) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    if (isCorrect) "✓ Richtig!" else "✗ Nicht ganz",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onErrorContainer,
                )
                if (!isCorrect) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Deine Antwort: $submitted",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        "Richtige Antwort: $solution",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Example Sentence
        task.prompt["example"]?.let { ex ->
            Text("Beispiel:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text(
                ex,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }

        // Additional Info (e.g. Principal parts for verbs)
        if (task.pos == "V" && !isCorrect) {
            val parts = listOfNotNull(
                task.solution["praeteritum"],
                task.solution["perfekt"]
            ).joinToString(" | ")
            if (parts.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Stammformen:", style = MaterialTheme.typography.labelSmall)
                Text(parts, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

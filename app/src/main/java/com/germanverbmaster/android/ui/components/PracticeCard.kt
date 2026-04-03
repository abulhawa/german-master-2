package com.germanverbmaster.android.ui.components

import androidx.compose.foundation.layout.*
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
 *
 * The prompt/solution JSON is pre-parsed into Map<String,String> by GetNextTaskUseCase.
 */
@Composable
fun PracticeCard(
    task: TaskCard,
    onCorrect: (responseMs: Int) -> Unit,
    onWrong: (responseMs: Int) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var answer by remember(task.taskId) { mutableStateOf("") }
    var revealed by remember(task.taskId) { mutableStateOf(false) }
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
            // CEFR badge
            task.cefrLevel?.let {
                SuggestionChip(
                    onClick = {},
                    label = { Text(it, style = MaterialTheme.typography.labelSmall) },
                )
                Spacer(Modifier.height(8.dp))
            }

            // Prompt — largest text on screen per ui-ux-guidelines
            val promptText = when (task.taskType) {
                "conjugate_form" ->
                    "${task.prompt["pronoun"] ?: "er/sie/es"} ___ (${task.lemma})"
                "noun_case_declension" ->
                    "${task.prompt["context"] ?: task.lemma} — ${task.prompt["case"] ?: "Nominativ"}"
                "adj_ending" ->
                    "${task.prompt["article_type"] ?: ""} + ${task.lemma} + ${task.prompt["noun"] ?: "___"}"
                else -> task.lemma
            }
            Text(
                text = promptText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 20.dp),
            )

            if (!revealed) {
                // Answer input
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Deine Antwort") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onSkip,
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) { Text("Überspringen") }
                    Button(
                        onClick = { revealed = true },
                        modifier = Modifier.weight(1f).height(48.dp),
                        enabled = answer.isNotBlank(),
                    ) { Text("Überprüfen") }
                }
            } else {
                // Solution revealed
                val solution = task.solution["form"]
                    ?: task.solution["answer"]
                    ?: task.solution.values.firstOrNull()
                    ?: "—"

                val isCorrect = answer.trim().equals(solution.trim(), ignoreCase = true)

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = if (isCorrect) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            if (isCorrect) "✓ Richtig!" else "✗ Falsch",
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onErrorContainer,
                        )
                        if (!isCorrect) {
                            Spacer(Modifier.height(4.dp))
                            Text("Richtige Antwort: $solution",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Example sentence from prompt metadata
                task.prompt["example"]?.let { ex ->
                    Text(ex,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    Spacer(Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        val ms = (System.currentTimeMillis() - startMs).toInt()
                        if (isCorrect) onCorrect(ms) else onWrong(ms)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) { Text("Weiter →") }
            }
        }
    }
}

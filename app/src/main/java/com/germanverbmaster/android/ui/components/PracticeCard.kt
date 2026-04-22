package com.germanverbmaster.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.domain.model.TaskCard
import com.germanverbmaster.android.speech.TextToSpeechHelper
import kotlinx.serialization.json.Json

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
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val ttsHelper = remember { TextToSpeechHelper(context) }

    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
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
                PracticePrompt(
                    task = task,
                    translation = task.translation,
                    onSpeak = { ttsHelper.speak(it) }
                )

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
                } else {
                    // Result area
                    val solution = task.solution["form"]
                        ?: task.solution["answer"]
                        ?: task.solution.values.firstOrNull()
                        ?: "—"

                    val isCorrect = answer.trim().equals(solution.trim(), ignoreCase = true)

                    ResultDisplay(isCorrect, answer, solution, task, onSpeak = { ttsHelper.speak(it) })
                }
            }

            Spacer(Modifier.height(16.dp))

            if (!revealed) {
                Button(
                    onClick = {
                        revealed = true
                        // Speak solution when revealed
                        val solution = task.solution["form"] ?: task.solution["answer"] ?: task.solution.values.firstOrNull() ?: ""
                        ttsHelper.speak(solution)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = answer.isNotBlank(),
                ) { Text("Prüfen") }
            } else {
                val solution = task.solution["form"]
                    ?: task.solution["answer"]
                    ?: task.solution.values.firstOrNull()
                    ?: "—"
                val isCorrect = answer.trim().equals(solution.trim(), ignoreCase = true)

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
fun PracticePrompt(task: TaskCard, translation: String? = null, onSpeak: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val taskInstruction = task.prompt["instructions"]
            ?: task.prompt["description"]
            ?: task.prompt["task"]
            ?: when (task.taskType) {
                "conjugate_form" -> "Konjugiere das Verb:"
                "noun_case_declension" -> "Bilde die richtige Form:"
                "adj_ending" -> "Ergänze die Adjektivendung:"
                else -> "Löse die folgende Aufgabe:"
            }

        Text(
            text = taskInstruction,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        when (task.taskType) {
            "conjugate_form" -> {
                val person = task.prompt["person"] ?: task.prompt["pronoun"] ?: "er/sie/es"
                val tense = task.prompt["tense"]
                val label = listOfNotNull(person, tense).joinToString(", ")

                Text(
                    text = label,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        task.lemma,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = { onSpeak("$person ${task.lemma}") }) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Sprechen", modifier = Modifier.size(20.dp))
                    }
                }
                translation?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            "noun_case_declension" -> {
                val case = task.prompt["case"] ?: "Nominativ"
                val number = task.prompt["number"]
                val gender = task.prompt["gender"]
                val label = listOfNotNull(case, number, gender).joinToString(" ")
                val mainWord = task.prompt["context"] ?: task.lemma

                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (label.contains("Plural", ignoreCase = true))
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.secondary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        mainWord,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = { onSpeak(mainWord) }) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Sprechen", modifier = Modifier.size(20.dp))
                    }
                }
                translation?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
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
                    IconButton(onClick = { onSpeak("${task.prompt["article_type"] ?: ""} ${task.lemma} ${task.prompt["noun"] ?: ""}") }) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Sprechen", modifier = Modifier.size(20.dp))
                    }
                }
                translation?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            else -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(task.lemma, style = MaterialTheme.typography.headlineMedium)
                    IconButton(onClick = { onSpeak(task.lemma) }) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Sprechen", modifier = Modifier.size(20.dp))
                    }
                }
                translation?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun ResultDisplay(isCorrect: Boolean, submitted: String, solution: String, task: TaskCard, onSpeak: (String) -> Unit) {
    val examplePair = remember(task.prompt["example"]) {
        val raw = task.prompt["example"] ?: return@remember null
        try {
            if (raw.trim().startsWith("{")) {
                val map = Json.decodeFromString<Map<String, String>>(raw)
                map["de"] to map["en"]
            } else {
                raw to null
            }
        } catch (_: Exception) {
            raw to null
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isCorrect) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (isCorrect) "✓ Richtig!" else "✗ Nicht ganz",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onErrorContainer,
                    )
                    IconButton(onClick = { onSpeak(solution) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp, 
                            contentDescription = "Sprechen",
                            tint = if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer
                                   else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
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
        examplePair?.let { (german, english) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Beispiel:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { onSpeak(german ?: "") }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Sprechen", modifier = Modifier.size(16.dp))
                }
            }
            Text(
                german ?: "",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            english?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        // Additional Info (e.g. Principal parts for verbs)
        if (task.pos == "V" && !isCorrect) {
            val parts = listOfNotNull(
                task.solution["praeteritum"],
                task.solution["perfekt"]
            ).joinToString(" | ")
            if (parts.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Stammformen:", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { onSpeak(parts.replace("|", ",")) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Sprechen", modifier = Modifier.size(16.dp))
                    }
                }
                Text(parts, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


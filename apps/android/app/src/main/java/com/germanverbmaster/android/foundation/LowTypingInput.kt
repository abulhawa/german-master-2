package com.germanverbmaster.android.foundation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import java.util.Locale
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.R
import com.germanverbmaster.android.foundation.contract.*

/** Stateless answer controls: the caller persists a draft before displaying its next state. */
@Composable
fun LowTypingInput(exercise: Exercise, draft: Answer?, order: List<String> = emptyList(), german: Boolean = false,
                   onDraft: (Answer) -> Unit, onOrder: (List<String>) -> Unit = {}, readOnly: Boolean = false) {
    var activeLeft by remember(exercise.id) { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val strings = remember(context, configuration, german) {
        context.createConfigurationContext(Configuration(configuration).apply {
            setLocale(if(german) Locale.GERMAN else Locale.ENGLISH)
        }).resources
    }
    fun stringResource(id: Int) = strings.getString(id)
    val remove = stringResource(R.string.practice_remove)
    @Composable fun option(label: String, selected: Boolean = false, enabled: Boolean = true, choice: Boolean = false, click: () -> Unit) {
        val colors = MaterialTheme.colorScheme
        OutlinedButton(onClick = click, enabled = enabled && !readOnly, shape = RoundedCornerShape(14.dp),
            border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) colors.primary else colors.outline),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurface,
                containerColor = if (selected) colors.primary.copy(alpha = 0.10f) else colors.surface,
                disabledContentColor = colors.onSurface, disabledContainerColor = if (selected) colors.primary.copy(alpha = 0.10f) else colors.surface),
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).semantics { this.selected = selected }) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (choice) RadioButton(selected = selected, onClick = null, enabled = enabled && !readOnly,
                    colors = RadioButtonDefaults.colors(disabledSelectedColor = colors.primary, disabledUnselectedColor = colors.outline))
                Text(label, modifier = Modifier.weight(1f))
            }
        }
    }
    when (exercise) {
        is ExerciseChoice -> exercise.options.forEach { item ->
            option(item.text, (draft as? AnswerChoice)?.optionId == item.id, choice = true) { onDraft(AnswerChoice(item.id)) }
        }
        is ExerciseGapChoice -> {
            val selections = (draft as? AnswerGapChoice)?.selections.orEmpty()
            exercise.slots.forEach { slot ->
                Text(slot.label, Modifier.semantics { heading() })
                slot.options.forEach { item ->
                    option(item.text, selections.any { it.slotId == slot.id && it.optionId == item.id }, choice = true) {
                        onDraft(AnswerGapChoice(selections.filter { it.slotId != slot.id } + GapSelection(slot.id, item.id)))
                    }
                }
            }
        }
        is ExerciseMatching -> {
            val pairs = (draft as? AnswerMatching)?.pairs.orEmpty()
            Text(stringResource(R.string.practice_matching_instruction))
            @Composable fun leftItems(modifier: Modifier = Modifier) {
                Column(modifier) {
                    Text(stringResource(R.string.practice_left_items), Modifier.semantics { heading() })
                    exercise.left.forEach { item -> option(item.text, activeLeft == item.id) {
                        activeLeft = if (activeLeft == item.id) null else item.id
                    } }
                }
            }
            @Composable fun rightItems(modifier: Modifier = Modifier) {
                Column(modifier) {
                    Text(stringResource(R.string.practice_right_items), Modifier.semantics { heading() })
                    exercise.right.forEach { item -> option(item.text, enabled = activeLeft != null) {
                        val left = requireNotNull(activeLeft)
                        onDraft(AnswerMatching(pairs.filter { it.leftId != left && it.rightId != item.id } + MatchPair(left, item.id)))
                        activeLeft = null
                    } }
                }
            }
            BoxWithConstraints {
                if(maxWidth < 360.dp || configuration.fontScale > 1.3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { leftItems(); rightItems() }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { leftItems(Modifier.weight(1f)); rightItems(Modifier.weight(1f)) }
                }
            }
            Column(Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                pairs.forEach { pair ->
                    val label = "${exercise.left.first { it.id == pair.leftId }.text} → ${exercise.right.first { it.id == pair.rightId }.text}"
                    Text(label)
                    option("$remove: $label") { onDraft(AnswerMatching(pairs.filter { it.leftId != pair.leftId })) }
                }
            }
        }
        is ExerciseWordOrder -> {
            LazyRow(Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite; contentDescription = order.mapIndexed { index, id -> "${index + 1}. ${exercise.tokens.first { it.id == id }.text}" }.joinToString(", ") }, horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                order.forEachIndexed { index, id -> key(id) {
                    val word = exercise.tokens.first { it.id == id }.text
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.pointerInput(order, readOnly) {
                            var deltaX = 0f
                            detectDragGesturesAfterLongPress(onDragStart = { deltaX = 0f }, onDrag = { change, drag ->
                                change.consume(); deltaX += drag.x
                            }, onDragEnd = {
                                val shift = (deltaX / 72.dp.toPx()).toInt()
                                val destination = (index + shift).coerceIn(0, order.lastIndex)
                                if (!readOnly && destination != index) {
                                    val next = order.toMutableList(); val token = next.removeAt(index); next.add(destination, token); onOrder(next)
                                }
                            })
                        }.clickable(enabled = !readOnly) { onOrder(order.filter { it != id }) }
                            .semantics {
                                role = Role.Button
                                contentDescription = "${index + 1}. $word"
                                customActions = listOf(CustomAccessibilityAction("$remove: $word") {
                                    if (!readOnly) { onOrder(order.filter { it != id }); true } else false
                                })
                            }) {
                        Text(word, Modifier.padding(horizontal = 14.dp, vertical = 11.dp), color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                } }
            }
            exercise.tokens.filter { it.id !in order }.forEach { item -> option(item.text) { onOrder(order + item.id) } }
            option(stringResource(R.string.foundation_reset), enabled = order.isNotEmpty()) { onOrder(emptyList()) }
        }
        else -> error("Expected a low-typing exercise")
    }
}

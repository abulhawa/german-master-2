package com.germanverbmaster.android.foundation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.germanverbmaster.android.foundation.contract.ExerciseWordOrder

/** Drag is a temporary visual preview. Only a valid drop persists the new order. */
@Composable
internal fun WordOrderInput(exercise: ExerciseWordOrder, order: List<String>, readOnly: Boolean,
                            remove: String, moveLeft: String, moveRight: String, reset: String, answerLabel: String,
                            onOrder: (List<String>) -> Unit) {
    val currentOrder by rememberUpdatedState(order)
    val save by rememberUpdatedState(onOrder)
    val bounds = remember(exercise.id) { mutableStateMapOf<String, Rect>() }
    var dragging by remember(exercise.id, readOnly) { mutableStateOf<String?>(null) }
    var translation by remember(exercise.id, readOnly) { mutableStateOf(Offset.Zero) }
    var target by remember(exercise.id, readOnly) { mutableStateOf<String?>(null) }
    val height = (160 * LocalConfiguration.current.fontScale.coerceAtLeast(1f)).dp
    fun move(id: String, destination: Int) {
        val next = currentOrder.toMutableList()
        if (!readOnly && next.remove(id)) { next.add(destination.coerceIn(0, next.size), id); save(next) }
    }
    // Reserve answer space and keep every bank slot mounted: adding/removing a
    // word cannot move the bank, Reset or the caller's Check button.
    Text(answerLabel, style=MaterialTheme.typography.labelLarge)
    Box(Modifier.fillMaxWidth().height(height).border(1.dp,MaterialTheme.colorScheme.outlineVariant,RoundedCornerShape(12.dp)).padding(8.dp).verticalScroll(rememberScrollState())) {
        FlowRow(Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            order.forEachIndexed { index, id ->
                val word = exercise.tokens.first { it.id == id }.text
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer,
                    border = BorderStroke(if(target == id) 2.dp else 0.dp, if(target == id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.onGloballyPositioned { if(dragging != id) bounds[id] = it.boundsInParent() }
                        .zIndex(if(dragging == id) 1f else 0f)
                        .graphicsLayer { if(dragging == id) { translationX = translation.x; translationY = translation.y; shadowElevation = 8.dp.toPx() } }
                        .pointerInput(exercise.id, id, readOnly) {
                            if (!readOnly) {
                                var pointer = Offset.Zero
                                detectDragGestures(onDragStart = { start ->
                                    dragging = id; translation = Offset.Zero; target = null
                                    pointer = (bounds[id]?.topLeft ?: Offset.Zero) + start
                                }, onDrag = { change, delta ->
                                    change.consume(); translation += delta; pointer += delta
                                    target = currentOrder.firstOrNull { it != id && bounds[it]?.contains(pointer) == true }
                                }, onDragEnd = {
                                    target?.let { drop ->
                                        val destination = currentOrder.indexOf(drop)
                                        if(destination >= 0) move(id, destination)
                                    }
                                    dragging = null; translation = Offset.Zero; target = null
                                }, onDragCancel = { dragging = null; translation = Offset.Zero; target = null })
                            }
                        }
                        .clickable(enabled = !readOnly) { save(currentOrder.filter { it != id }) }
                        .semantics {
                            role = Role.Button; contentDescription = "${index + 1}. $word"
                            customActions = buildList {
                                add(CustomAccessibilityAction("$remove: $word") { if(readOnly) false else { save(currentOrder.filter { it != id }); true } })
                                if(index > 0) add(CustomAccessibilityAction("$moveLeft: $word") { if(readOnly) false else { move(id,index - 1); true } })
                                if(index < order.lastIndex) add(CustomAccessibilityAction("$moveRight: $word") { if(readOnly) false else { move(id,index + 1); true } })
                            }
                        }) {
                    Text(word, Modifier.padding(horizontal = 14.dp, vertical = 11.dp), color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
        }
    }
    FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        exercise.tokens.forEach { token ->
            val selected = token.id in order
            Box(if(selected) Modifier.clearAndSetSemantics {} else Modifier) {
                OutlinedButton(onClick = { save(currentOrder + token.id) }, enabled = !readOnly && !selected,
                    modifier = Modifier.heightIn(min = 48.dp)) { Text(token.text) }
            }
        }
    }
    TextButton(onClick = { save(emptyList()) }, enabled = !readOnly && order.isNotEmpty()) { Text(reset) }
}

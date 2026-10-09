package com.germanverbmaster.android.foundation

import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.germanverbmaster.android.R
import com.germanverbmaster.android.foundation.contract.*

@Composable
fun FoundationTheme(content: @Composable () -> Unit) {
    val c = if (isSystemInDarkTheme()) FoundationTokens.dark else FoundationTokens.light
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme(
        primary=c.primary, onPrimary=c.onPrimary, background=c.background,
        surface=c.surface, onSurface=c.text, onBackground=c.text,
        onSurfaceVariant=c.secondary, outline=c.controlBorder, error=c.error
    ) else lightColorScheme(
        primary=c.primary, onPrimary=c.onPrimary, background=c.background,
        surface=c.surface, onSurface=c.text, onBackground=c.text,
        onSurfaceVariant=c.secondary, outline=c.controlBorder, error=c.error
    ), content=content)
}

@Composable
fun PracticeCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(shape=MaterialTheme.shapes.large, color=MaterialTheme.colorScheme.surface) {
        Column(Modifier.padding(FoundationTokens.spacing[4].dp), verticalArrangement=Arrangement.spacedBy(FoundationTokens.spacing[3].dp), content=content)
    }
}

/** A new question is the keyboard entry point, even after scrolling through a long answer. */
@Composable
fun PracticeHeading(prompt: String, identity: String) {
    val focus = remember { FocusRequester() }
    val visibility = remember { BringIntoViewRequester() }
    Text(prompt, style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.bringIntoViewRequester(visibility).focusRequester(focus)
            .focusable().semantics { heading() })
    LaunchedEffect(identity) {
        focus.requestFocus()
        visibility.bringIntoView()
    }
}

@Composable
fun AnswerField(label: String, value: String, onCheck: (() -> Unit)? = null, onChange: (String) -> Unit) {
    OutlinedTextField(value=value, onValueChange=onChange, label={Text(label)},
        singleLine=onCheck != null,
        keyboardOptions=KeyboardOptions(imeAction=if(onCheck != null) ImeAction.Done else ImeAction.Default),
        keyboardActions=KeyboardActions(onDone={ onCheck?.invoke() }),
        modifier=Modifier.fillMaxWidth().heightIn(min=FoundationTokens.controlMin.dp))
}

@Composable
fun ExerciseInput(exercise: Exercise, german: Boolean = false, onAnswer: (Answer?) -> Unit) {
    var text by remember { mutableStateOf("") }
    var values by remember { mutableStateOf(mapOf<String,String>()) }
    var order by remember { mutableStateOf(listOf<String>()) }
    var draft by remember(exercise.id) { mutableStateOf<Answer?>(null) }
    if (exercise is ExerciseChoice || exercise is ExerciseWordOrder || exercise is ExerciseGapChoice || exercise is ExerciseMatching) {
        LowTypingInput(exercise, draft, order, german = german,
            onDraft = { draft = it; onAnswer(if(com.germanverbmaster.android.learner.nativeAnswerReady(exercise, it)) it else null) },
            onOrder = { order = it; onAnswer(if(it.size == (exercise as ExerciseWordOrder).tokens.size) AnswerWordOrder(it) else null) })
        return
    }
    when(exercise) {
        is ExerciseShortAnswer -> AnswerField(stringResource(R.string.foundation_answer), text) { text=it; onAnswer(if(it.isBlank()) null else AnswerShortAnswer(it)) }
        is ExerciseCloze, is ExerciseMultiSlot -> {
            val slots = when(exercise) { is ExerciseCloze -> exercise.slots; is ExerciseMultiSlot -> exercise.slots; else -> error("Unreachable") }
            slots.forEach { slot -> AnswerField(slot.label,values[slot.id].orEmpty()) { v ->
                values=values+(slot.id to v)
                val answer = slots.map { SlotValue(it.id,values[it.id].orEmpty()) }
                onAnswer(if(answer.any { it.text.isBlank() }) null else if(exercise is ExerciseCloze) AnswerCloze(answer) else AnswerMultiSlot(answer))
            } }
        }
        else -> error("Expected a typed exercise")
    }
}

@Composable
fun FoundationPreview(session: Session) {
    var index by remember { mutableIntStateOf(0) }
    var german by remember { mutableStateOf(false) }
    var answer by remember { mutableStateOf<Answer?>(null) }
    var inspected by remember { mutableStateOf(false) }
    val exercise=session.questions[index].exercise
    Surface(color=MaterialTheme.colorScheme.background,modifier=Modifier.fillMaxSize()) {
        Column(Modifier.safeDrawingPadding().imePadding().verticalScroll(rememberScrollState()).padding(FoundationTokens.spacing[3].dp).widthIn(max=FoundationTokens.practiceMax.dp),verticalArrangement=Arrangement.spacedBy(FoundationTokens.spacing[3].dp)) {
            Text(stringResource(R.string.foundation_title),style=MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.foundation_notice),color=MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth().heightIn(min=FoundationTokens.controlMin.dp).toggleable(value=german,role=Role.Switch,onValueChange={german=it})) { Switch(checked=german,onCheckedChange=null); Text(stringResource(R.string.foundation_german)) }
            PracticeCard {
                Text(stringResource(R.string.foundation_position,index+1,session.questions.size))
                PracticeHeading(exercise.prompt, exercise.id)
                Text(if(german) exercise.instruction.de else exercise.instruction.en)
                key(exercise.id) {
                    ExerciseInput(exercise, german) { answer=it;inspected=false }
                    var hint by remember { mutableStateOf(false) }
                    TextButton(onClick={hint=!hint}) { Text(stringResource(R.string.foundation_hint)) }
                    if(hint) Text(if(german) exercise.hint.de else exercise.hint.en)
                }
                Button(onClick={inspected=true},enabled=answer!=null,modifier=Modifier.fillMaxWidth().heightIn(min=FoundationTokens.controlMin.dp)) { Text(stringResource(R.string.foundation_inspect)) }
                if(inspected) {
                    Text(stringResource(R.string.foundation_ready),modifier=Modifier.semantics { liveRegion=LiveRegionMode.Polite })
                    answer?.let { Text(ContractReader.json.encodeToString<Answer>(it)) }
                }
                OutlinedButton(onClick={index=(index+1)%session.questions.size;answer=null;inspected=false},modifier=Modifier.fillMaxWidth().heightIn(min=FoundationTokens.controlMin.dp)) { Text(stringResource(R.string.foundation_next)) }
            }
        }
    }
}

package com.germanverbmaster.android.learner

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.foundation.*
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class NativePractice(
    val request: SessionRequest,
    val deviceId: String = UUID.randomUUID().toString(),
    val session: Session? = null,
    val index: Int = 0,
    val draft: Answer? = null,
    val assisted: Boolean = false,
    val order: List<String> = emptyList(),
    val pending: Attempt? = null,
    val exposure: ExposureEvent? = null,
    val evaluation: Evaluation? = null,
    val rejected: Boolean = false,
    val graded: Int = 0,
    val skipped: Int = 0,
    val correct: Int = 0,
    val focus: PracticeFocus? = null
) {
    init {
        require(index >= 0 && graded >= 0 && skipped >= 0 && correct in 0..graded)
        require(pending == null || exposure == null)
        require(session != null || (index == 0 && pending == null && exposure == null && evaluation == null))
        session?.let {
            require(index <= it.questions.size && it.questions.size <= request.questionCount)
            require(it.questions.map { q -> q.id }.distinct().size == it.questions.size)
            require(graded + skipped == index + if(evaluation != null) 1 else 0)
            if(pending != null) require(index < it.questions.size && pending.sessionQuestionId == it.questions[index].id && pending.exerciseRevision == it.questions[index].exercise.revision)
            if(exposure != null) require(index < it.questions.size && exposure.sessionQuestionId == it.questions[index].id && exposure.exerciseRevision == it.questions[index].exercise.revision)
        }
    }
    val question get() = requireNotNull(session).questions[index]
    val editable get() = pending == null && exposure == null && evaluation == null && !rejected
    fun next(skip: Boolean = false) = copy(index = index + 1, skipped = skipped + if(skip) 1 else 0, draft = null, assisted = false, order = emptyList(), pending = null, exposure = null, evaluation = null, rejected = false)
}

fun nativeAnswerReady(exercise: Exercise, answer: Answer?): Boolean = when {
    exercise is ExerciseShortAnswer && answer is AnswerShortAnswer -> answer.text.isNotBlank()
    exercise is ExerciseChoice && answer is AnswerChoice -> exercise.options.any { it.id == answer.optionId }
    exercise is ExerciseWordOrder && answer is AnswerWordOrder -> answer.tokenIds.toSet() == exercise.tokens.map { it.id }.toSet() && answer.tokenIds.size == exercise.tokens.size
    exercise is ExerciseCloze && answer is AnswerCloze -> answer.values.map { it.slotId } == exercise.slots.map { it.id } && answer.values.all { it.text.isNotBlank() }
    exercise is ExerciseMultiSlot && answer is AnswerMultiSlot -> answer.values.map { it.slotId } == exercise.slots.map { it.id } && answer.values.all { it.text.isNotBlank() }
    else -> false
}

@Composable
fun NativePracticeView(p: NativePractice, german: Boolean, busy: Boolean, action: (suspend () -> Unit) -> Unit, repository: LearnerRepository, edit: (() -> Unit) -> Unit, openProgress: () -> Unit, close: () -> Unit) {
    fun text(en: String, de: String) = if(german) de else en
    @Composable fun button(label: String, enabled: Boolean = !busy, block: () -> Unit) {
        OutlinedButton(onClick = block, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(label) }
    }
    button(text("Close practice", "Übung schließen"), block = close)
    val session = p.session
    if(session == null) {
        button(text("Retry session", "Sitzung erneut laden")) { action { repository.startPractice() } }
    } else if(p.index == session.questions.size) {
        Text(text("Confirmed summary", "Bestätigte Zusammenfassung"), Modifier.semantics { heading(); liveRegion = LiveRegionMode.Polite })
        Text(text("Graded: ${p.graded} · Skipped: ${p.skipped} · Correct: ${p.correct}", "Bewertet: ${p.graded} · Übersprungen: ${p.skipped} · Richtig: ${p.correct}"))
        Text(text("A correct answer is a first step. Retained improvement needs later unassisted checks.", "Eine richtige Antwort ist ein erster Schritt. Nachhaltiger Fortschritt braucht spätere Prüfungen ohne Hilfe."))
        Text(text("Targets covered", "Behandelte Lernziele"), Modifier.semantics { heading() })
        session.questions.distinctBy { it.exercise.targetId }.forEach { question ->
            val title = repository.state.catalog?.targets?.find { it.id == question.exercise.targetId }?.title
            Text(title?.let { if (german) it.de else it.en } ?: question.exercise.prompt)
        }
        button(text("View Progress", "Fortschritt ansehen"), block = openProgress)
        button(text("Finish", "Abschließen")) { action { repository.discardPractice() }; close() }
    } else {
        val exercise = p.question.exercise
        Text("${p.index + 1} / ${session.questions.size}")
        val preferredCount = if (p.focus == null) repository.state.profile?.preferences?.sessionQuestionCount ?: p.request.questionCount else p.request.questionCount
        if(session.questions.size < preferredCount) Text(text("Shorter session: ${session.questions.size} questions available.", "Kürzere Sitzung: ${session.questions.size} Fragen verfügbar."))
        Text(exercise.prompt, Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineSmall)
        Text(if(german) exercise.instruction.de else exercise.instruction.en)
        if(p.editable && !busy) {
            fun draft(answer: Answer?) { edit { repository.draft(answer) } }
            fun checkAnswer() {
                val current = repository.state.practice ?: return
                if (current.editable && nativeAnswerReady(current.question.exercise, current.draft)) action { repository.answer() }
            }
            when(exercise) {
                is ExerciseShortAnswer -> AnswerField(text("Answer", "Antwort"), (p.draft as? AnswerShortAnswer)?.text.orEmpty(), onCheck = ::checkAnswer) { draft(AnswerShortAnswer(it)) }
                is ExerciseChoice -> exercise.options.forEach { option -> button((if((p.draft as? AnswerChoice)?.optionId == option.id) "✓ " else "") + option.text) { draft(AnswerChoice(option.id)) } }
                is ExerciseWordOrder -> {
                    val order = p.order
                    Text(order.joinToString(" ") { id -> exercise.tokens.first { it.id == id }.text }, Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                    order.forEachIndexed { index, id ->
                        key(id) {
                            val word = exercise.tokens.first { it.id == id }.text
                            fun move(direction: Int) {
                                val next = order.toMutableList()
                                val destination = index + direction
                                if (destination in next.indices) {
                                    next[index] = next[destination]; next[destination] = id
                                    edit { repository.order(next) }
                                }
                            }
                            button(text("Move left: $word (${index + 1})", "Nach links: $word (${index + 1})"), index > 0) { move(-1) }
                            button(text("Move right: $word (${index + 1})", "Nach rechts: $word (${index + 1})"), index < order.lastIndex) { move(1) }
                        }
                    }
                    exercise.tokens.filter { it.id !in order }.forEach { token -> button(token.text) { edit { repository.order(order + token.id) } } }
                    button(text("Reset order", "Reihenfolge zurücksetzen")) { edit { repository.order(emptyList()) } }
                }
                is ExerciseCloze, is ExerciseMultiSlot -> {
                    val slots = if(exercise is ExerciseCloze) exercise.slots else (exercise as ExerciseMultiSlot).slots
                    val values = when(val answer = p.draft) { is AnswerCloze -> answer.values; is AnswerMultiSlot -> answer.values; else -> emptyList() }
                    slots.forEach { slot -> AnswerField(slot.label, values.find { it.slotId == slot.id }?.text.orEmpty(), onCheck = ::checkAnswer) { value ->
                        val next = slots.map { SlotValue(it.id, if(it.id == slot.id) value else values.find { v -> v.slotId == it.id }?.text.orEmpty()) }
                        draft(if(exercise is ExerciseCloze) AnswerCloze(next) else AnswerMultiSlot(next))
                    } }
                }
            }
            button(text("Hint", "Hinweis")) { edit { repository.hint() } }
        } else p.draft?.let { Text(foundationAnswerText(it, exercise)) }
        if(p.assisted) Text(if(german) exercise.hint.de else exercise.hint.en)
        if(p.evaluation == null && !p.rejected) {
            if(p.exposure == null) button(text(if(p.pending == null) "Check" else "Retry answer", if(p.pending == null) "Prüfen" else "Antwort erneut senden"), !busy && (p.pending != null || nativeAnswerReady(exercise, p.draft))) { action { repository.answer() } }
            if(p.pending == null) button(text(if(p.exposure == null) "Skip" else "Retry skip", if(p.exposure == null) "Überspringen" else "Überspringen erneut senden")) { action { repository.skip() } }
        }
        if(p.pending != null && p.evaluation == null || p.exposure != null) Text(text("Saved; awaiting server confirmation.", "Gespeichert; Serverbestätigung ausstehend."))
        p.evaluation?.let { evaluation ->
            Text(if(evaluation.outcome == "correct") text("Correct", "Richtig") else text("Incorrect", "Nicht richtig"), Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            if(evaluation.assisted) Text(text("Assisted answer", "Antwort mit Hilfe"))
            Text(foundationAnswerText(evaluation.acceptedAnswer, exercise))
            Text(if(german) evaluation.explanation.de else evaluation.explanation.en)
            button(text("Continue", "Weiter")) { action { repository.continuePractice() } }
        }
        if(p.rejected) Text(text("Submission rejected. Saved work remains; discard only to recover after a fixture reset.", "Übermittlung abgelehnt. Daten bleiben gespeichert; nach Zurücksetzen des Testservers verwerfen."))
    }
    if (p.session != null && p.index < p.session.questions.size) {
        var reportOpen by remember(p.question.id) { mutableStateOf(false) }
        val savedReport = repository.state.contentReport
        val recorded = repository.state.reportRecorded && savedReport?.sessionQuestionId == p.question.id
        if (recorded) Text(text("Report recorded for this revision. Your learning result is unchanged.", "Meldung für diese Version gespeichert. Ihr Lernergebnis bleibt unverändert."))
        else if (savedReport == null || repository.state.reportRecorded) {
            button(text("Report an exercise problem", "Problem mit der Übung melden")) { reportOpen = true }
            if (reportOpen) {
                listOf("incorrect_answer" to text("Incorrect accepted answer", "Falsche akzeptierte Antwort"), "ambiguous_prompt" to text("Ambiguous prompt", "Mehrdeutige Aufgabenstellung"), "other" to text("Other exercise problem", "Anderes Problem mit der Übung")).forEach { (category, label) ->
                    button(label) { reportOpen = false; action { repository.reportProblem(category) } }
                }
                button(text("Cancel", "Abbrechen")) { reportOpen = false }
            }
        }
    }
    var confirm by remember { mutableStateOf(false) }
    button(text("Discard local session…", "Lokale Sitzung verwerfen…")) { confirm = true }
    if(confirm) AlertDialog(onDismissRequest = { confirm = false }, title = { Text(text("Discard saved practice?", "Gespeicherte Übung verwerfen?")) }, text = { Text(text("Pending work and drafts will be removed from this device.", "Ausstehende Daten und Entwürfe werden auf diesem Gerät entfernt.")) }, confirmButton = { TextButton(onClick = { action { repository.discardPractice() }; close() }) { Text(text("Discard", "Verwerfen")) } }, dismissButton = { TextButton(onClick = { confirm = false }) { Text(text("Cancel", "Abbrechen")) } })
}





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
    val focus: PracticeFocus? = null,
    val completion: SessionCompletionRequest? = null,
    val completionReceipt: SessionCompletionReceipt? = null,
    val offlinePack: PreparedPack? = null,
    val outbox: List<NativeOfflineWrite> = emptyList()
) {
    init {
        require(index >= 0 && graded >= 0 && skipped >= 0 && correct in 0..graded)
        require(pending == null || exposure == null)
        require(completion == null || session != null)
        require(outbox.size <= 51 && (offlinePack != null || outbox.isEmpty()))
        if(offlinePack != null) {
            require(offlinePack.sessions.any { it == session })
            val writes = outbox.filter { it.completion == null }
            val ids = writes.map { it.attempt?.sessionQuestionId ?: requireNotNull(it.exposure).sessionQuestionId }
            require(ids.distinct().size == ids.size && writes.size == graded + skipped)
            writes.forEach { event ->
                val questionId = event.attempt?.sessionQuestionId ?: requireNotNull(event.exposure).sessionQuestionId
                val question = requireNotNull(session).questions.single { it.id == questionId }
                require((event.attempt?.exerciseRevision ?: event.exposure?.exerciseRevision) == question.exercise.revision)
                require((event.attempt?.deviceId ?: event.exposure?.deviceId) == deviceId)
            }
            require(outbox.filter { it.completion != null }.map { it.completion } == listOfNotNull(completion))
        }
        require(completionReceipt == null || (completion != null && completionReceipt.requestId == completion.requestId && completionReceipt.sessionId == session?.id && completionReceipt.mode == completion.mode))
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
    val editable get() = completion == null && pending == null && exposure == null && evaluation == null && !rejected
    fun next(skip: Boolean = false, writes: List<NativeOfflineWrite> = outbox) = copy(index = index + 1, skipped = skipped + if(skip) 1 else 0, draft = null, assisted = false, order = emptyList(), pending = null, exposure = null, evaluation = null, rejected = false, outbox = writes)
}

fun nativeAnswerReady(exercise: Exercise, answer: Answer?): Boolean = when {
    exercise is ExerciseShortAnswer && answer is AnswerShortAnswer -> answer.text.isNotBlank()
    exercise is ExerciseChoice && answer is AnswerChoice -> exercise.options.any { it.id == answer.optionId }
    exercise is ExerciseWordOrder && answer is AnswerWordOrder -> answer.tokenIds.toSet() == exercise.tokens.map { it.id }.toSet() && answer.tokenIds.size == exercise.tokens.size
    exercise is ExerciseCloze && answer is AnswerCloze -> answer.values.map { it.slotId } == exercise.slots.map { it.id } && answer.values.all { it.text.isNotBlank() }
    exercise is ExerciseMultiSlot && answer is AnswerMultiSlot -> answer.values.map { it.slotId } == exercise.slots.map { it.id } && answer.values.all { it.text.isNotBlank() }
    exercise is ExerciseGapChoice && answer is AnswerGapChoice ->
        answer.selections.size == exercise.slots.size && answer.selections.map { it.slotId }.distinct().size == exercise.slots.size &&
        answer.selections.all { v -> exercise.slots.any { s -> s.id == v.slotId && s.options.any { it.id == v.optionId } } }
    exercise is ExerciseMatching && answer is AnswerMatching ->
        answer.pairs.size == exercise.left.size && exercise.left.size == exercise.right.size &&
        answer.pairs.map { it.leftId }.toSet() == exercise.left.map { it.id }.toSet() &&
        answer.pairs.map { it.rightId }.toSet() == exercise.right.map { it.id }.toSet()
    else -> false
}

@Composable
fun NativePracticeView(p: NativePractice, german: Boolean, busy: Boolean, action: (suspend () -> Unit) -> Unit, repository: LearnerRepository, edit: (() -> Unit) -> Unit, openProgress: () -> Unit, close: () -> Unit) {
    fun text(en: String, de: String) = if(german) de else en
    @Composable fun button(label: String, enabled: Boolean = !busy, block: () -> Unit) {
        OutlinedButton(onClick = block, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(label) }
    }
    var closing by remember { mutableStateOf(false) }
    if (p.offlinePack != null) {
        Text(text("Saved on this device. Feedback is provisional until synced.", "Auf diesem Gerät gespeichert. Rückmeldungen sind bis zur Synchronisierung vorläufig."), Modifier.semantics { liveRegion = LiveRegionMode.Polite })
        button(text("Sync saved work", "Gespeicherte Arbeit synchronisieren"), !busy && p.outbox.any { !it.delivered }) { action { repository.syncSavedWork(); repository.refresh() } }
        p.outbox.forEach { event ->
            val receipt = event.attemptReceipt
            val confirmed = when(receipt) { is AttemptAcknowledgment -> receipt.evaluation; is AttemptDuplicate -> receipt.evaluation; else -> null }
            if (confirmed != null && confirmed.outcome != event.provisional?.outcome) Text(text("Server correction: ", "Serverkorrektur: ") + if(german) confirmed.explanation.de else confirmed.explanation.en)
            if (receipt is AttemptRejection || event.exposureReceipt is ExposureRejected) Text(text("A saved event was not accepted. Further sync has stopped; the event remains saved for review.", "Ein gespeichertes Ereignis wurde nicht akzeptiert. Die Synchronisierung wurde angehalten. Das Ereignis bleibt zur Prüfung gespeichert."))
        }
    }
    button(text("Close practice", "Übung schließen")) { if(p.session == null || p.completion != null || p.index == p.session.questions.size) close() else closing = true }
    if (closing) {
        AlertDialog(onDismissRequest = { closing = false }, title = { Text(text("End this session?", "Diese Übung beenden?")) },
            text = { Column { Text(if(p.offlinePack != null) text("Your answers, Skips and partial draft stay saved on this device. Sync later to confirm them.", "Antworten, übersprungene Aufgaben und Entwurf bleiben auf diesem Gerät gespeichert. Später synchronisieren, um sie zu bestätigen.") else text("Confirmed answers remain. Your partial session and draft stay saved. Retry any pending answer or Skip before ending.", "Bestätigte Antworten bleiben erhalten. Teilübung und Entwurf bleiben gespeichert. Senden Sie ausstehende Antworten oder Überspring-Anfragen zuerst erneut."))
                TextButton(enabled = !busy, onClick = { closing = false; close() }) { Text(text("Save and return Home", "Speichern und zur Startseite")) }
            } },
            confirmButton = { TextButton(enabled = !busy && (p.pending == null || p.evaluation != null) && p.exposure == null && !p.rejected, onClick = { closing = false; action { repository.finishPractice() } }) { Text(text("End session", "Übung beenden")) } },
            dismissButton = { TextButton(onClick = { closing = false }) { Text(text("Keep practising", "Weiter üben")) } })
    }
    val session = p.session
    if(session == null) {
        button(text("Retry session", "Sitzung erneut laden")) { action { repository.startPractice() } }
    } else if(p.index == session.questions.size || p.completion != null) {
        Text(if(p.completion?.mode == "partial") text("Partial session summary", "Zusammenfassung der Teilübung") else if(p.offlinePack != null && p.completionReceipt == null) text("Saved session summary", "Zusammenfassung der gespeicherten Sitzung") else text("Confirmed summary", "Bestätigte Zusammenfassung"), Modifier.semantics { heading(); liveRegion = LiveRegionMode.Polite })
        Text(if(p.completionReceipt != null) text("Session end confirmed by the server.", "Übungsende vom Server bestätigt.") else text("Session completion awaiting confirmation.", "Bestätigung des Übungsendes ausstehend."))
        if(p.completionReceipt == null && (p.offlinePack == null || p.completion == null)) button(if(p.offlinePack != null) text("Save session end", "Übungsende speichern") else if(p.completion == null) text("Confirm session completion", "Übungsende bestätigen") else text("Retry saved completion", "Gespeichertes Übungsende erneut senden")) { action { repository.finishPractice() } }
        val graded = p.completionReceipt?.gradedCount ?: p.graded
        val skipped = p.completionReceipt?.skippedCount ?: p.skipped
        val correct = p.completionReceipt?.correctCount ?: p.correct
        Text(if(p.offlinePack != null && p.completionReceipt == null) text("Saved answers: $graded · Skipped: $skipped · Provisional correct: $correct", "Gespeicherte Antworten: $graded · Übersprungen: $skipped · Vorläufig richtig: $correct") else text("Graded: $graded · Skipped: $skipped · Correct: $correct", "Bewertet: $graded · Übersprungen: $skipped · Richtig: $correct"))
        Text(text("A correct answer is a first step. Retained improvement needs later unassisted checks.", "Eine richtige Antwort ist ein erster Schritt. Nachhaltiger Fortschritt braucht spätere Prüfungen ohne Hilfe."))
        Text(text("Targets covered", "Behandelte Lernziele"), Modifier.semantics { heading() })
        session.questions.take(p.index + if(p.evaluation != null) 1 else 0).distinctBy { it.exercise.targetId }.forEach { question ->
            val title = repository.state.catalog?.targets?.find { it.id == question.exercise.targetId }?.title
            Text(title?.let { if (german) it.de else it.en } ?: question.exercise.prompt)
        }
        button(text("View Progress", "Fortschritt ansehen"), block = openProgress)
        button(text("Finish", "Abschließen"), enabled = !busy && (p.completionReceipt != null || p.offlinePack != null && p.completion != null)) { edit { repository.discardPractice() }; close() }
    } else {
        val exercise = p.question.exercise
        Text("${p.index + 1} / ${session.questions.size}")
        val preferredCount = if (p.focus == null) repository.state.profile?.preferences?.sessionQuestionCount ?: p.request.questionCount else p.request.questionCount
        if(session.questions.size < preferredCount) Text(text("Shorter session: ${session.questions.size} questions available.", "Kürzere Sitzung: ${session.questions.size} Fragen verfügbar."))
        PracticeHeading(exercise.prompt, p.question.id)
        Text(if(german) exercise.instruction.de else exercise.instruction.en)
        if(p.editable && !busy) {
            fun draft(answer: Answer?) { edit { repository.draft(answer) } }
            fun checkAnswer() {
                val current = repository.state.practice ?: return
                if (current.editable && nativeAnswerReady(current.question.exercise, current.draft)) action { repository.answer() }
            }
            when(exercise) {
                is ExerciseShortAnswer -> AnswerField(text("Answer", "Antwort"), (p.draft as? AnswerShortAnswer)?.text.orEmpty(), onCheck = ::checkAnswer) { draft(AnswerShortAnswer(it)) }
                is ExerciseChoice, is ExerciseWordOrder, is ExerciseGapChoice, is ExerciseMatching ->
                    LowTypingInput(exercise, p.draft, p.order, german = german, onDraft = ::draft,
                        onOrder = { next -> edit { repository.order(next) } })
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
    if(p.offlinePack == null) {
    var confirm by remember { mutableStateOf(false) }
    button(text("Discard local session…", "Lokale Sitzung verwerfen…")) { confirm = true }
    if(confirm) AlertDialog(onDismissRequest = { confirm = false }, title = { Text(text("Discard saved practice?", "Gespeicherte Übung verwerfen?")) }, text = { Text(text("Pending work and drafts will be removed from this device.", "Ausstehende Daten und Entwürfe werden auf diesem Gerät entfernt.")) }, confirmButton = { TextButton(onClick = { action { repository.discardPractice() }; close() }) { Text(text("Discard", "Verwerfen")) } }, dismissButton = { TextButton(onClick = { confirm = false }) { Text(text("Cancel", "Abbrechen")) } })
    }
}





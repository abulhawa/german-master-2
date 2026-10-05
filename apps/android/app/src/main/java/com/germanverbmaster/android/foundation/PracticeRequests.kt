package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URI
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

fun foundationSessionRequest() = SessionRequest("v2", UUID.randomUUID().toString(), 5,
    listOf("short_answer@1", "choice@1", "cloze@1", "word_order@1", "multi_slot@1"))

fun foundationAttempt(session: Session, index: Int, answer: Answer, assisted: Boolean, deviceId: String) = Attempt(
    UUID.randomUUID().toString(), session.questions[index].id, session.questions[index].exercise.revision,
    deviceId, answer, if (assisted) listOf("hint") else emptyList(), Instant.now().truncatedTo(ChronoUnit.SECONDS).toString(), index
)

fun foundationAnswerText(answer: Answer, exercise: Exercise): String = when (answer) {
    is AnswerShortAnswer -> answer.text
    is AnswerChoice -> (exercise as? ExerciseChoice)?.options?.find { it.id == answer.optionId }?.text.orEmpty()
    is AnswerWordOrder -> answer.tokenIds.joinToString(" ") { id -> (exercise as? ExerciseWordOrder)?.tokens?.find { it.id == id }?.text.orEmpty() }
    is AnswerCloze -> answer.values.joinToString(" · ") { "${(exercise as? ExerciseCloze)?.slots?.find { slot -> slot.id == it.slotId }?.label ?: it.slotId}: ${it.text}" }
    is AnswerMultiSlot -> answer.values.joinToString(" · ") { "${(exercise as? ExerciseMultiSlot)?.slots?.find { slot -> slot.id == it.slotId }?.label ?: it.slotId}: ${it.text}" }
}

package com.germanverbmaster.android.foundation

import com.germanverbmaster.android.foundation.contract.*
import kotlinx.serialization.json.JsonElement
import java.text.Normalizer

class OfflineGradingFailure(val code: String) : IllegalArgumentException(code)

/** Provisional deterministic feedback only. Confirmed evidence/mastery remains server-owned. */
object OfflineGrader {
    const val NORMALIZATION = "de-nfc-trim-v1"
    const val EVALUATOR = "deterministic-v1"
    // ECMAScript WhiteSpace/LineTerminator set: Kotlin trim differs for BOM and control separators.
    private val whitespace = setOf('\u0009', '\u000A', '\u000B', '\u000C', '\u000D', '\u0020', '\u00A0',
        '\u1680', '\u2028', '\u2029', '\u202F', '\u205F', '\u3000', '\uFEFF') + ('\u2000'..'\u200A')
    fun normalize(value: String, policy: String): String {
        if (policy != NORMALIZATION) throw OfflineGradingFailure("unsupported_policy")
        return Normalizer.normalize(value, Normalizer.Form.NFC).trim { it in whitespace }
    }
    private fun sameIds(actual: List<String>, expected: List<String>) =
        actual.size == expected.size && actual.distinct().size == actual.size && actual.toSet() == expected.toSet()

    fun validate(exercise: Exercise, answer: Answer) {
        validateExercise(exercise)
        when {
            exercise is ExerciseShortAnswer && answer is AnswerShortAnswer -> Unit
            exercise is ExerciseChoice && answer is AnswerChoice ->
                if (exercise.options.none { it.id == answer.optionId }) throw OfflineGradingFailure("unknown_option")
            exercise is ExerciseWordOrder && answer is AnswerWordOrder ->
                if (!sameIds(answer.tokenIds, exercise.tokens.map { it.id })) throw OfflineGradingFailure("invalid_tokens")
            exercise is ExerciseCloze && answer is AnswerCloze ->
                if (!sameIds(answer.values.map { it.slotId }, exercise.slots.map { it.id })) throw OfflineGradingFailure("invalid_slots")
            exercise is ExerciseMultiSlot && answer is AnswerMultiSlot ->
                if (!sameIds(answer.values.map { it.slotId }, exercise.slots.map { it.id })) throw OfflineGradingFailure("invalid_slots")
            exercise is ExerciseGapChoice && answer is AnswerGapChoice -> {
                if (!sameIds(answer.selections.map { it.slotId }, exercise.slots.map { it.id })) throw OfflineGradingFailure("invalid_slots")
                if (answer.selections.any { v -> exercise.slots.none { s -> s.id == v.slotId && s.options.any { it.id == v.optionId } } }) throw OfflineGradingFailure("unknown_option")
            }
            exercise is ExerciseMatching && answer is AnswerMatching ->
                if (exercise.left.size != exercise.right.size || !sameIds(answer.pairs.map { it.leftId }, exercise.left.map { it.id }) ||
                    !sameIds(answer.pairs.map { it.rightId }, exercise.right.map { it.id })) throw OfflineGradingFailure("invalid_pairs")
            else -> throw OfflineGradingFailure("answer_type_mismatch")
        }
    }
    fun validateExercise(exercise: Exercise) {
        fun unique(items: List<String>) { if(items.distinct().size != items.size) throw OfflineGradingFailure("duplicate_input_id") }
        when(exercise) {
            is ExerciseChoice -> unique(exercise.options.map { it.id })
            is ExerciseWordOrder -> unique(exercise.tokens.map { it.id })
            is ExerciseCloze -> unique(exercise.slots.map { it.id })
            is ExerciseMultiSlot -> unique(exercise.slots.map { it.id })
            is ExerciseGapChoice -> { unique(exercise.slots.map { it.id }); exercise.slots.forEach { unique(it.options.map { o -> o.id }) } }
            is ExerciseMatching -> {
                unique(exercise.left.map { it.id }); unique(exercise.right.map { it.id })
                if(exercise.left.size != exercise.right.size) throw OfflineGradingFailure("unequal_matching_sides")
            }
            else -> Unit
        }
    }
    private fun slotsEqual(actual: List<SlotValue>, expected: List<SlotValue>, policy: String) =
        actual.all { value -> expected.any { it.slotId == value.slotId && normalize(it.text, policy) == normalize(value.text, policy) } }
    private fun equivalent(actual: Answer, expected: Answer, policy: String): Boolean = when {
        actual is AnswerShortAnswer && expected is AnswerShortAnswer -> normalize(actual.text, policy) == normalize(expected.text, policy)
        actual is AnswerChoice && expected is AnswerChoice -> actual.optionId == expected.optionId
        actual is AnswerWordOrder && expected is AnswerWordOrder -> actual.tokenIds == expected.tokenIds
        actual is AnswerCloze && expected is AnswerCloze -> slotsEqual(actual.values, expected.values, policy)
        actual is AnswerMultiSlot && expected is AnswerMultiSlot -> slotsEqual(actual.values, expected.values, policy)
        actual is AnswerGapChoice && expected is AnswerGapChoice -> actual.selections.toSet() == expected.selections.toSet()
        actual is AnswerMatching && expected is AnswerMatching -> actual.pairs.toSet() == expected.pairs.toSet()
        else -> false
    }
    fun grade(exercise: Exercise, rubric: OfflineRubric, input: JsonElement, assistance: List<String>): Evaluation {
        normalize("", rubric.normalizationVersion)
        if (rubric.exerciseId != exercise.id || rubric.exerciseRevision != exercise.revision) throw OfflineGradingFailure("rubric_linkage_mismatch")
        rubric.acceptedAnswers.forEach { validate(exercise, it) }
        val answer = try {
            ContractShape.checkAnswer(input)
            ContractReader.json.decodeFromJsonElement(Answer.serializer(), input)
        } catch (_: IllegalArgumentException) { throw OfflineGradingFailure("answer_type_mismatch") }
          catch (_: IllegalStateException) { throw OfflineGradingFailure("answer_type_mismatch") }
        validate(exercise, answer)
        return Evaluation(
            outcome = if (rubric.acceptedAnswers.any { equivalent(answer, it, rubric.normalizationVersion) }) "correct" else "incorrect",
            policyVersion = "$EVALUATOR/${rubric.normalizationVersion}",
            explanation = rubric.explanation, acceptedAnswer = rubric.acceptedAnswers.first(), assisted = assistance.isNotEmpty(),
            completedAnswer = rubric.completedAnswer
        )
    }
}

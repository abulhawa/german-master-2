package com.germanverbmaster.android.domain.usecase

import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.domain.model.PracticeResult
import java.time.Instant
import javax.inject.Inject

class SubmitAnswerUseCase @Inject constructor(
    private val practiceRepository: PracticeRepository,
) {
    suspend operator fun invoke(result: PracticeResult, lemma: String, submitted: String, correct: String) {
        val entity = PracticeHistoryEntity(
            taskId      = result.taskId,
            lexemeId    = result.lexemeId,
            lemma       = lemma,
            pos         = result.pos,
            taskType    = result.taskType,
            result      = result.result,
            submittedAnswer = submitted,
            correctAnswer = correct,
            responseMs  = result.responseMs,
            cefrLevel   = result.cefrLevel,
            hintsUsed   = result.hintsUsed,
            submittedAt = Instant.now().toString(),
            synced      = false,
        )
        practiceRepository.record(entity)
    }
}

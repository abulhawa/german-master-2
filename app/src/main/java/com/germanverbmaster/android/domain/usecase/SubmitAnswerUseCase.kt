package com.germanverbmaster.android.domain.usecase

import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.repository.AuthRepository
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.domain.model.PracticeResult
import java.time.Instant
import javax.inject.Inject

class SubmitAnswerUseCase @Inject constructor(
    private val practiceRepository: PracticeRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(result: PracticeResult, lemma: String, submitted: String, correct: String) {
        val entity = PracticeHistoryEntity(
            taskId      = result.taskId,
            lexemeId    = result.lexemeId,
            lemma       = lemma,
            pos         = result.pos,
            taskType    = result.taskType,
            renderer    = result.renderer,
            result      = result.result,
            submittedAnswer = submitted,
            correctAnswer = correct,
            responseMs  = result.responseMs,
            cefrLevel   = result.cefrLevel,
            hintsUsed   = result.hintsUsed,
            submittedAt = Instant.now().toString(),
            synced      = false,
            userId      = authRepository.currentUserId
        )
        practiceRepository.record(entity)
    }
}

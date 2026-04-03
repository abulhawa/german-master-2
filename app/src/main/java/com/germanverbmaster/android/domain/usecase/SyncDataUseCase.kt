package com.germanverbmaster.android.domain.usecase

import com.germanverbmaster.android.data.repository.LexemeRepository
import com.germanverbmaster.android.data.repository.TaskRepository
import javax.inject.Inject

class SyncDataUseCase @Inject constructor(
    private val lexemeRepository: LexemeRepository,
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke() {
        lexemeRepository.sync()
        taskRepository.sync()
    }

    suspend fun needsFullSync(): Boolean = lexemeRepository.needsFullSync()
}

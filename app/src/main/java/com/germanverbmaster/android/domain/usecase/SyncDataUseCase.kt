package com.germanverbmaster.android.domain.usecase

import com.germanverbmaster.android.data.repository.InflectionRepository
import com.germanverbmaster.android.data.repository.LexemeRepository
import com.germanverbmaster.android.data.repository.TaskRepository
import javax.inject.Inject

class SyncDataUseCase @Inject constructor(
    private val lexemeRepository: LexemeRepository,
    private val taskRepository: TaskRepository,
    private val inflectionRepository: InflectionRepository,
) {
    suspend operator fun invoke() {
        lexemeRepository.sync()
        inflectionRepository.sync()
        taskRepository.sync()
    }

    suspend fun needsFullSync(): Boolean = lexemeRepository.needsFullSync()
}

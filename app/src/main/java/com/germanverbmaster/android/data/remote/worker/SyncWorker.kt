package com.germanverbmaster.android.data.remote.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.domain.usecase.SyncDataUseCase
import com.germanverbmaster.android.domain.usecase.SyncHistoryUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncDataUseCase: SyncDataUseCase,
    private val syncHistoryUseCase: SyncHistoryUseCase,
    private val practiceRepository: PracticeRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Sync lexemes and task specs from Supabase
            syncDataUseCase()
            
            // Sync user practice history
            syncHistoryUseCase()

            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed (attempt $runAttemptCount): ${e.message}", e)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}

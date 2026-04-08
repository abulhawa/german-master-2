package com.germanverbmaster.android.data.remote.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.germanverbmaster.android.data.repository.PracticeRepository
import com.germanverbmaster.android.domain.usecase.SyncDataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncDataUseCase: SyncDataUseCase,
    private val practiceRepository: PracticeRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Sync lexemes and task specs from Supabase
            syncDataUseCase()
            
            // 2. RESTORE history from Supabase (Pull)
            practiceRepository.fetchFromSupabase()
            
            // 3. Flush local practice history to Supabase (Push)
            practiceRepository.flushToSupabase()
            
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

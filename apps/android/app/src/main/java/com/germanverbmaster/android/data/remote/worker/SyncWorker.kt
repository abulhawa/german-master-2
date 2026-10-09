package com.germanverbmaster.android.data.remote.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Compatibility stub for legacy WorkManager records. Never access legacy repositories.
 * PausedLegacySyncFactory also intercepts this class before WorkManager instantiates it.
 */
class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = Result.success()
}

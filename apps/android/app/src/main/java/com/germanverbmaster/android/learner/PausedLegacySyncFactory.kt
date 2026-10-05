package com.germanverbmaster.android.learner

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

/** Existing legacy queued jobs must not instantiate legacy auth/data dependencies. */
class PausedLegacySyncFactory : WorkerFactory() {
    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? =
        if(workerClassName=="com.germanverbmaster.android.data.remote.worker.SyncWorker") object : CoroutineWorker(appContext,workerParameters) {
            override suspend fun doWork() = Result.success()
        } else null
}

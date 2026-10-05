package com.germanverbmaster.android.foundation

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.germanverbmaster.android.learner.PausedLegacySyncFactory
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class PausedLegacySyncFactoryTest {
    @Test fun persistedLegacyWorkFinishesWithoutConstructingTheLegacyWorker() = runBlocking {
        val factory=PausedLegacySyncFactory()
        val worker=factory.createWorker(mockk<Context>(relaxed=true),"com.germanverbmaster.android.data.remote.worker.SyncWorker",mockk<WorkerParameters>(relaxed=true))
        assertTrue(worker is CoroutineWorker)
        assertEquals(ListenableWorker.Result.success(),(worker as CoroutineWorker).doWork())
    }
}

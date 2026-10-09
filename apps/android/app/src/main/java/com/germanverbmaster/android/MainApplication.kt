package com.germanverbmaster.android

import android.app.Application
import androidx.work.Configuration
import com.germanverbmaster.android.learner.PausedLegacySyncFactory
import dagger.hilt.android.HiltAndroidApp

/**
 * V2 never schedules legacy synchronization. Queued work from an earlier
 * installation is safely retired without starting the old data pipeline.
 */
@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(PausedLegacySyncFactory())
            .build()
}

package com.germanverbmaster.android.data.remote

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistorySyncDeviceIdProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private companion object {
        const val PREFS_NAME = "history_sync_identity"
        const val KEY_DEVICE_ID = "practice_history_device_id"
    }

    fun get(): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_DEVICE_ID, null)?.trim().orEmpty()
        if (existing.isNotEmpty()) return existing

        val resolved = UUID.randomUUID().toString()

        prefs.edit { putString(KEY_DEVICE_ID, resolved) }
        return resolved
    }
}

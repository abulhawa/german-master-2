package com.germanverbmaster.android.domain.usecase

import android.util.Log
import com.germanverbmaster.android.data.local.dao.PracticeHistoryDao
import com.germanverbmaster.android.data.remote.SupabaseHistoryApi
import com.germanverbmaster.android.data.repository.AuthRepository
import com.germanverbmaster.android.data.repository.SyncPreferences
import javax.inject.Inject

class SyncHistoryUseCase @Inject constructor(
    private val historyDao: PracticeHistoryDao,
    private val historyApi: SupabaseHistoryApi,
    private val authRepository: AuthRepository,
    private val prefs: SyncPreferences,
) {
    suspend operator fun invoke() {
        val userId = authRepository.currentUserId
        if (userId == null) {
            Log.d("SyncHistoryUseCase", "No user logged in, skipping history sync")
            return
        }

        Log.d("SyncHistoryUseCase", "Starting history sync for user $userId")

        // 1. Upload unsynced local records
        try {
            val unsynced = historyDao.unsyncedForUser(userId)
            if (unsynced.isNotEmpty()) {
                Log.d("SyncHistoryUseCase", "Uploading ${unsynced.size} unsynced records")
                val remotes = unsynced.map { with(historyApi) { it.toRemote(userId) } }
                historyApi.upsert(remotes)
                historyDao.markSynced(unsynced.map { it.localId })
            }
        } catch (e: Exception) {
            Log.e("SyncHistoryUseCase", "Failed to upload unsynced history", e)
        }

        // 2. Download remote records since last sync
        try {
            val lastSync = prefs.getHistoryLastSync() ?: "1970-01-01T00:00:00Z"
            val remoteNew = historyApi.fetchUpdatedSince(lastSync, userId)
            if (remoteNew.isNotEmpty()) {
                Log.d("SyncHistoryUseCase", "Downloaded ${remoteNew.size} new records")
                val entities = remoteNew.map { with(historyApi) { it.toEntity() } }
                historyDao.upsertAll(entities)
                
                // Update last sync time to the latest submittedAt
                val latest = remoteNew.maxOf { it.submittedAt }
                prefs.setHistoryLastSync(latest)
            }
        } catch (e: Exception) {
            Log.e("SyncHistoryUseCase", "Failed to download remote history", e)
        }

        Log.d("SyncHistoryUseCase", "History sync completed")
    }
}

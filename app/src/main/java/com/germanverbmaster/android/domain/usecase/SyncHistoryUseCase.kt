package com.germanverbmaster.android.domain.usecase

import android.util.Log
import com.germanverbmaster.android.BuildConfig
import com.germanverbmaster.android.data.local.dao.PracticeHistoryDao
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.remote.RemoteHistory
import com.germanverbmaster.android.data.remote.SupabaseHistoryApi
import com.germanverbmaster.android.data.repository.AuthRepository
import com.germanverbmaster.android.data.repository.SyncPreferences
import com.germanverbmaster.android.data.sync.HistorySyncMapper
import javax.inject.Inject

private data class HistorySyncFingerprint(
    val userId: String,
    val taskId: String,
    val lexemeId: String,
    val pos: String,
    val taskType: String,
    val renderer: String,
    val result: String,
    val responseMs: Int,
    val hintsUsed: Boolean,
    val submittedAt: String,
)

private fun RemoteHistory.syncFingerprint() = HistorySyncFingerprint(
    userId = userId,
    taskId = taskId,
    lexemeId = lexemeId,
    pos = pos,
    taskType = taskType,
    renderer = renderer,
    result = result,
    responseMs = responseMs,
    hintsUsed = hintsUsed,
    submittedAt = submittedAt,
)

private fun PracticeHistoryEntity.syncFingerprint(userId: String) = HistorySyncFingerprint(
    userId = userId,
    taskId = taskId,
    lexemeId = lexemeId,
    pos = pos,
    taskType = taskType,
    renderer = renderer,
    result = result,
    responseMs = responseMs,
    hintsUsed = hintsUsed,
    submittedAt = submittedAt,
)

class SyncHistoryUseCase @Inject constructor(
    private val historyDao: PracticeHistoryDao,
    private val historyApi: SupabaseHistoryApi,
    private val historySyncMapper: HistorySyncMapper,
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
            val uploadBatch = mutableListOf<UploadCandidate>()
            val blockedRows = mutableListOf<PracticeHistoryEntity>()

            for (entry in unsynced) {
                val remote = historySyncMapper.toRemote(entry, userId)
                if (remote != null) {
                    uploadBatch += UploadCandidate(localId = entry.localId, remote = remote)
                } else {
                    blockedRows += entry
                }
            }

            val blockedCount = blockedRows.size
            if (blockedCount > 0) {
                Log.d(
                    "SyncHistoryUseCase",
                    "Skipping $blockedCount unsynced history rows because they could not be mapped to remote task and lexeme identities",
                )
                if (BuildConfig.DEBUG) {
                    blockedRows.take(3).forEach { row ->
                        Log.d(
                            "SyncHistoryUseCase",
                            "Blocked history row localId=${row.localId} taskId=${row.taskId} lexemeId=${row.lexemeId} taskType=${row.taskType} renderer=${row.renderer} lemma=${row.lemma}",
                        )
                    }
                }
            }

            if (uploadBatch.isNotEmpty()) {
                val remotes = uploadBatch.map { it.remote }
                Log.d("SyncHistoryUseCase", "Uploading ${remotes.size} unsynced records")
                historyApi.upsert(remotes)
                historyDao.markSynced(uploadBatch.map { it.localId }, userId)
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
                
                // Robust De-duplication: Compare downloaded records against local records.
                // We fetch all records for the user to ensure we don't miss anything that was uploaded from another device.
                val existingLocal = historyDao.allForUser(userId)
                val existingFingerprints = existingLocal.map { it.syncFingerprint(userId) }.toSet()
                
                val dedupedRemote = remoteNew.filterNot { remote ->
                    remote.syncFingerprint() in existingFingerprints
                }
                
                if (dedupedRemote.size != remoteNew.size) {
                    Log.d(
                        "SyncHistoryUseCase",
                        "Skipping ${remoteNew.size - dedupedRemote.size} records that match existing local history",
                    )
                }

                if (dedupedRemote.isNotEmpty()) {
                    val entities = mutableListOf<PracticeHistoryEntity>()
                    for (remote in dedupedRemote) {
                        entities += historySyncMapper.toLocalEntity(remote)
                    }
                    historyDao.upsertAll(entities)
                }

                val latest = remoteNew.maxOf { it.submittedAt }
                prefs.setHistoryLastSync(latest)
            }
        } catch (e: Exception) {
            Log.e("SyncHistoryUseCase", "Failed to download remote history", e)
        }

        Log.d("SyncHistoryUseCase", "History sync completed")
    }
}

private data class UploadCandidate(
    val localId: Int,
    val remote: RemoteHistory,
)

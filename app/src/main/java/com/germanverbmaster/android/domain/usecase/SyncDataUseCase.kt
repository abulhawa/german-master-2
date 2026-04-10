package com.germanverbmaster.android.domain.usecase

import android.util.Log
import androidx.room.withTransaction
import com.germanverbmaster.android.data.local.db.AppDatabase
import com.germanverbmaster.android.data.repository.InflectionRepository
import com.germanverbmaster.android.data.repository.LexemeRepository
import com.germanverbmaster.android.data.repository.TaskRepository
import javax.inject.Inject

class SyncDataUseCase @Inject constructor(
    private val lexemeRepository: LexemeRepository,
    private val taskRepository: TaskRepository,
    private val inflectionRepository: InflectionRepository,
    private val database: AppDatabase,
    private val prefs: com.germanverbmaster.android.data.repository.SyncPreferences,
) {
    suspend operator fun invoke() {
        Log.d("SyncDataUseCase", "Starting data synchronization...")
        
        // 1. Fetch data from remote (outside transaction)
        val lexemeResult = runCatching { lexemeRepository.fetchRemote() }.onFailure {
            Log.e("SyncDataUseCase", "Failed to fetch lexemes", it)
        }.getOrNull()

        val inflectionResult = runCatching { inflectionRepository.fetchRemote() }.onFailure {
            Log.e("SyncDataUseCase", "Failed to fetch inflections", it)
        }.getOrNull()

        val taskResult = runCatching { taskRepository.fetchRemote() }.onFailure {
            Log.e("SyncDataUseCase", "Failed to fetch tasks", it)
        }.getOrNull()

        val lexSize = lexemeResult?.size ?: 0
        val infSize = inflectionResult?.size ?: 0
        val taskSize = taskResult?.size ?: 0
        Log.d("SyncDataUseCase", "Remote fetch results: Lexemes=$lexSize, Inflections=$infSize, Tasks=$taskSize")

        // 2. Perform database operations in a single transaction
        var success = false
        try {
            database.withTransaction {
                // Save lexemes first
                lexemeResult?.let {
                    Log.d("SyncDataUseCase", "Saving $lexSize lexemes...")
                    lexemeRepository.saveToLocal(it)
                }

                // Build set of all known lexeme IDs in the DB to guard against FK violations
                val allLexemeIds = lexemeRepository.getAllIds().toSet()
                val totalInDb = allLexemeIds.size
                val approvedComplete = lexemeRepository.countApprovedAndComplete()
                val approvedOnly = database.lexemeDao().countApproved()
                val completeOnly = database.lexemeDao().countComplete()
                Log.d("SyncDataUseCase", "Local DB state: Total=$totalInDb, Approved=$approvedOnly, Complete=$completeOnly, Approved+Complete=$approvedComplete")

                inflectionResult?.let { inflections ->
                    val (safe, orphaned) = inflections.partition { it.lexemeId in allLexemeIds }
                    if (orphaned.isNotEmpty()) {
                        Log.w("SyncDataUseCase", "Skipping ${orphaned.size} inflections with unknown lexeme IDs. Example orphaned lexemeIds: ${orphaned.take(5).map { it.lexemeId }}")
                        Log.d("SyncDataUseCase", "Example valid lexemeIds in DB: ${allLexemeIds.take(5)}")
                    }
                    Log.d("SyncDataUseCase", "Saving ${safe.size} safe inflections...")
                    inflectionRepository.saveToLocal(safe)
                }
                
                taskResult?.let { tasks ->
                    // Build a map of Lexeme ID -> CEFR Level for level propagation
                    val lexemeLevelMap = database.lexemeDao().getAllLevels().associate { it.id to it.cefrLevel }
                    
                    val (safe, orphaned) = tasks.partition { it.lexemeId in allLexemeIds }
                    if (orphaned.isNotEmpty()) {
                        Log.w("SyncDataUseCase", "Skipping ${orphaned.size} tasks with unknown lexeme IDs.")
                    }
                    
                    // Propagate level from lexeme to task entity if task level is missing
                    val taskEntities = safe.map { remoteTask ->
                        val lexemeLevel = lexemeLevelMap[remoteTask.lexemeId]
                        with(taskRepository.api) { remoteTask.toEntity(cefrLevel = lexemeLevel) }
                    }
                    
                    Log.d("SyncDataUseCase", "Saving ${taskEntities.size} safe tasks with propagated levels...")
                    database.taskSpecDao().upsertAll(taskEntities)
                }
                success = true
            }
        } catch (e: Exception) {
            Log.e("SyncDataUseCase", "Transaction failed - database rolled back", e)
            throw e
        }

        // 3. Update sync preferences only on SUCCESS
        if (success) {
            lexemeResult?.filter { it.updatedAt.isNotEmpty() }?.maxOfOrNull { it.updatedAt }?.let {
                prefs.setLexemeLastSync(it)
                Log.d("SyncDataUseCase", "Updated lexeme last sync to $it")
            }
            inflectionResult?.filter { it.updatedAt.isNotEmpty() }?.maxOfOrNull { it.updatedAt }?.let {
                prefs.setInflectionLastSync(it)
                Log.d("SyncDataUseCase", "Updated inflection last sync to $it")
            }
            taskResult?.filter { it.updatedAt.isNotEmpty() }?.maxOfOrNull { it.updatedAt }?.let {
                prefs.setTaskLastSync(it)
                Log.d("SyncDataUseCase", "Updated task last sync to $it")
            }
        }
        
        Log.d("SyncDataUseCase", "Synchronization step completed. Success=$success")
    }
}

package com.germanverbmaster.android.data.repository

import com.germanverbmaster.android.data.local.dao.DailyAccuracy
import com.germanverbmaster.android.data.local.dao.PracticeHistoryDao
import com.germanverbmaster.android.data.local.dao.TaskTypeStat
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeRepository @Inject constructor(
    private val dao: PracticeHistoryDao,
) {
    fun observeRecent(limit: Int = 100): Flow<List<PracticeHistoryEntity>> =
        dao.observeRecent(limit)

    suspend fun record(entry: PracticeHistoryEntity): Long =
        dao.insert(entry)

    suspend fun accuracyToday(): Pair<Float, Int> {
        val since = Instant.now().minusSeconds(86400).toString()
        val result = dao.statsSince(since)
        return Pair(result?.accuracy ?: 0f, result?.total ?: 0)
    }

    fun observeTaskTypeStats(): Flow<List<TaskTypeStat>> = dao.observeTaskTypeStats()

    suspend fun getDailyAccuracy(since: String): List<DailyAccuracy> = dao.getDailyAccuracy(since)

    fun observeCorrectTaskIds(): Flow<Set<String>> = dao.observeCorrectTaskIds().map { it.toSet() }

    fun observeDistinctPos(): Flow<List<String>> = dao.observeDistinctPos()
}

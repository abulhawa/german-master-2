package com.germanverbmaster.android.data.sync

import android.util.Log
import com.germanverbmaster.android.BuildConfig
import com.germanverbmaster.android.data.local.entity.PracticeHistoryEntity
import com.germanverbmaster.android.data.remote.HistorySyncDeviceIdProvider
import com.germanverbmaster.android.data.remote.RemoteHistory
import com.germanverbmaster.android.data.repository.LexemeRepository
import com.germanverbmaster.android.data.repository.TaskRepository
import com.germanverbmaster.android.data.repository.WordRepository
import com.germanverbmaster.android.data.util.DateTimeUtils
import com.germanverbmaster.android.data.util.PosNormalizer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistorySyncMapper @Inject constructor(
    private val lexemeRepository: LexemeRepository,
    private val taskRepository: TaskRepository,
    private val wordRepository: WordRepository,
    private val deviceIdProvider: HistorySyncDeviceIdProvider,
) {
    private companion object {
        const val TAG = "HistorySyncMapper"
        const val LOCAL_WORD_PREFIX = "word_"
        const val IDENTITY_PREFIX = "identity:"
        val COLLECTIONS_JSON = Json { ignoreUnknownKeys = true }
    }

    suspend fun toRemote(entity: PracticeHistoryEntity, userId: String): RemoteHistory? {
        val resolvedIds = resolveRemoteIds(entity)

        if (resolvedIds == null && !entity.taskId.startsWith(LOCAL_WORD_PREFIX)) {
            return null
        }

        // Use a standardized content-based identity if no remote ID is found.
        // This ensures that "word_123" (local) becomes "identity:präp:dank + gen" (global).
        val canonicalPos = PartOfSpeechMapper.toCanonical(entity.pos)
        val fallbackIdentity = "$IDENTITY_PREFIX$canonicalPos:${entity.lemma.lowercase().trim()}"
        
        val finalTaskId = resolvedIds?.taskId ?: if (entity.taskId.startsWith(LOCAL_WORD_PREFIX)) fallbackIdentity else entity.taskId
        val finalLexemeId = resolvedIds?.lexemeId ?: if (entity.lexemeId.startsWith(LOCAL_WORD_PREFIX)) fallbackIdentity else entity.lexemeId

        if (BuildConfig.DEBUG && (finalTaskId != entity.taskId || finalLexemeId != entity.lexemeId)) {
            Log.d(
                TAG,
                "Resolved local history row ${entity.localId} to remote ids taskId=$finalTaskId lexemeId=$finalLexemeId",
            )
        }

        return RemoteHistory(
            remoteId = entity.remoteId.toLongOrNull(),
            userId = userId,
            taskId = finalTaskId,
            lexemeId = finalLexemeId,
            lemma = entity.lemma,
            pos = canonicalPos,
            taskType = entity.taskType,
            renderer = entity.renderer,
            deviceId = deviceIdProvider.get(),
            result = entity.result,
            submittedAnswer = entity.submittedAnswer,
            correctAnswer = entity.correctAnswer,
            responseMs = entity.responseMs,
            cefrLevel = entity.cefrLevel,
            collections = parseCollections(entity.collectionsJson),
            hintsUsed = entity.hintsUsed,
            submittedAt = DateTimeUtils.normalizeIso8601(entity.submittedAt),
        )
    }

    suspend fun toFingerprint(entity: PracticeHistoryEntity, userId: String): HistorySyncFingerprint {
        val resolvedIds = resolveRemoteIds(entity)
        val canonicalPos = PartOfSpeechMapper.toCanonical(entity.pos)
        val fallbackIdentity = "$IDENTITY_PREFIX$canonicalPos:${entity.lemma.lowercase().trim()}"
        
        val finalTaskId = resolvedIds?.taskId ?: if (entity.taskId.startsWith(LOCAL_WORD_PREFIX)) fallbackIdentity else entity.taskId
        val finalLexemeId = resolvedIds?.lexemeId ?: if (entity.lexemeId.startsWith(LOCAL_WORD_PREFIX)) fallbackIdentity else entity.lexemeId
        
        return HistorySyncFingerprint(
            userId = userId,
            taskId = finalTaskId,
            lexemeId = finalLexemeId,
            taskType = entity.taskType,
            result = entity.result,
            submittedAt = DateTimeUtils.normalizeIso8601(entity.submittedAt),
        )
    }

    fun toFingerprint(remote: RemoteHistory): HistorySyncFingerprint {
        return HistorySyncFingerprint(
            userId = remote.userId,
            taskId = remote.taskId,
            lexemeId = remote.lexemeId,
            taskType = remote.taskType,
            result = remote.result,
            submittedAt = DateTimeUtils.normalizeIso8601(remote.submittedAt),
        )
    }

    suspend fun toLocalEntity(remote: RemoteHistory): PracticeHistoryEntity {
        val lexeme = lexemeRepository.getById(remote.lexemeId)
        val lemma = remote.lemma ?: lexeme?.lemma ?: ""
        val cefrLevel = remote.cefrLevel ?: lexeme?.cefrLevel
        val localPos = PosNormalizer.normalize(remote.pos)

        if (!isWordCard(remote.taskType, remote.renderer)) {
            return PracticeHistoryEntity(
                remoteId = remote.remoteId?.toString() ?: "",
                userId = remote.userId,
                taskId = remote.taskId,
                lexemeId = remote.lexemeId,
                lemma = lemma,
                pos = localPos,
                taskType = remote.taskType,
                renderer = remote.renderer,
                result = remote.result,
                submittedAnswer = remote.submittedAnswer.orEmpty(),
                correctAnswer = remote.correctAnswer.orEmpty(),
                responseMs = remote.responseMs,
                cefrLevel = cefrLevel,
                collectionsJson = serializeCollections(remote.collections),
                hintsUsed = remote.hintsUsed,
                submittedAt = DateTimeUtils.normalizeIso8601(remote.submittedAt),
                synced = true,
            )
        }

        val localWordId = lemma
            .takeIf { it.isNotBlank() }
            ?.let { resolvedLemma -> wordRepository.findIdByLemmaAndPos(resolvedLemma, localPos) }
            ?.let { "$LOCAL_WORD_PREFIX$it" }

        return PracticeHistoryEntity(
            remoteId = remote.remoteId?.toString() ?: "",
            userId = remote.userId,
            taskId = localWordId ?: remote.taskId,
            lexemeId = localWordId ?: remote.lexemeId,
            lemma = lemma,
            pos = localPos,
            taskType = remote.taskType,
            renderer = remote.renderer,
            result = remote.result,
            submittedAnswer = remote.submittedAnswer.orEmpty(),
            correctAnswer = remote.correctAnswer.orEmpty(),
            responseMs = remote.responseMs,
            cefrLevel = cefrLevel,
            collectionsJson = serializeCollections(remote.collections),
            hintsUsed = remote.hintsUsed,
            submittedAt = DateTimeUtils.normalizeIso8601(remote.submittedAt),
            synced = true,
        )
    }

    private suspend fun resolveRemoteIds(entity: PracticeHistoryEntity): ResolvedRemoteIds? {
        val taskId = entity.taskId
        val lexemeId = entity.lexemeId
        if (taskRepository.exists(taskId) && lexemeRepository.exists(lexemeId)) {
            return ResolvedRemoteIds(taskId = taskId, lexemeId = lexemeId)
        }

        if (!isWordCard(entity.taskType, entity.renderer)) {
            if (BuildConfig.DEBUG) {
                Log.d(
                    TAG,
                    "Unable to map local history row ${entity.localId} because taskId=$taskId lexemeId=$lexemeId are not remote-backed",
                )
            }
            return null
        }

        val resolvedLexemeId = lexemeRepository.findIdByLemmaAndPos(entity.lemma, entity.pos)
            ?: run {
                if (BuildConfig.DEBUG) {
                    Log.d(
                        TAG,
                        "No lexeme match for Wortschatz row ${entity.localId} lemma=${entity.lemma} pos=${entity.pos}",
                    )
                }
                return null
            }

        val resolvedTaskId = taskRepository.findHistoryAnchorTaskId(resolvedLexemeId, entity.pos)
            ?: if (isWordCard(entity.taskType, entity.renderer)) {
                "drill_anchor_${PartOfSpeechMapper.toCanonical(entity.pos)}"
            } else null

        if (resolvedTaskId == null) {
            if (BuildConfig.DEBUG) {
                Log.d(
                    TAG,
                    "No task anchor for Wortschatz row ${entity.localId} lexemeId=$resolvedLexemeId pos=${entity.pos}",
                )
            }
            return null
        }

        return ResolvedRemoteIds(taskId = resolvedTaskId, lexemeId = resolvedLexemeId)
    }

    private fun isWordCard(taskType: String, renderer: String): Boolean {
        return taskType == "vocabulary_drill" || renderer == "word_card"
    }

    private fun parseCollections(collectionsJson: String?): List<String>? {
        if (collectionsJson.isNullOrBlank()) return null
        return runCatching { COLLECTIONS_JSON.decodeFromString<List<String>>(collectionsJson) }
            .getOrNull()
    }

    private fun serializeCollections(collections: List<String>?): String? {
        if (collections == null) return null
        return COLLECTIONS_JSON.encodeToString(collections)
    }
}

private data class ResolvedRemoteIds(
    val taskId: String,
    val lexemeId: String,
)

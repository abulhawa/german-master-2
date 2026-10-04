package com.germanverbmaster.android.learner

import android.util.AtomicFile
import com.germanverbmaster.android.foundation.ContractReader
import com.germanverbmaster.android.foundation.contract.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.util.UUID

interface LearnerApi {
    suspend fun report(request: ContentReportRequest): ContentReportReceipt = error("Reporting unavailable")
    suspend fun session(request: FocusedSessionRequest): Session = error("Focused practice unavailable")
    suspend fun session(request: SessionRequest): Session = error("Practice unavailable")
    suspend fun submit(attempt: Attempt): Acknowledgment = error("Practice unavailable")
    suspend fun expose(event: ExposureEvent): ExposureAcknowledgment = error("Practice unavailable")
    suspend fun profile(): LearnerProfile
    suspend fun save(request: ProfileRequest): LearnerProfile
    suspend fun targets(cursor: String): TargetPage
    suspend fun sync(cursor: String): SyncPage
    suspend fun catalog(): Catalog
}

/** Only an owned sync read can request full snapshot recovery. */
class SyncCursorReset : IllegalStateException("Local sync cursor needs a fresh snapshot")

/** Only the public local fixture; never production authentication. */
class LocalLearnerApi(private val port: Int = 5001) : LearnerApi {
    override suspend fun report(request: ContentReportRequest): ContentReportReceipt {
        val raw = ContractReader.json.parseToJsonElement(request("/v2/content-reports", ContractReader.json.encodeToString(request)))
        ContractShape.checkContentReportReceipt(raw)
        return ContractReader.json.decodeFromJsonElement(ContentReportReceipt.serializer(), raw).also { check(it.reportId == request.reportId) }
    }
    override suspend fun session(request: FocusedSessionRequest) = ContractReader.session(request("/v2/sessions", ContractReader.json.encodeToString(request)))
    override suspend fun session(request: SessionRequest) = ContractReader.session(request("/v2/sessions", ContractReader.json.encodeToString(request)))
    override suspend fun submit(attempt: Attempt): Acknowledgment = ContractReader.acknowledgments(request("/v2/attempts:batch", ContractReader.json.encodeToString(AttemptBatch("v2", listOf(attempt))))).acknowledgments.single().also { check(it.attemptId == attempt.attemptId) }
    override suspend fun expose(event: ExposureEvent): ExposureAcknowledgment {
        val raw = ContractReader.json.parseToJsonElement(request("/v2/exposures:batch", ContractReader.json.encodeToString(ExposureBatch("v2", listOf(event)))))
        ContractShape.checkExposureBatchResponse(raw)
        return ContractReader.json.decodeFromJsonElement(ExposureBatchResponse.serializer(), raw).acknowledgments.single().also { check(it.eventId == event.eventId) }
    }
    init { require(port in 1..65535) }
    private suspend fun request(path: String, body: String? = null, resetOnInvalidCursor: Boolean = false): String = withContext(Dispatchers.IO) {
        val connection = URI("http://127.0.0.1:$port$path").toURL().openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("Authorization", "Bearer foundation-local-demo")
            connection.setRequestProperty("Cache-Control", "no-store")
            if (body != null) {
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            }
            val status = connection.responseCode
            if (resetOnInvalidCursor && status == 400) {
                val error = runCatching {
                    val raw = ContractReader.json.parseToJsonElement(connection.errorStream.bufferedReader(Charsets.UTF_8).use { it.readText() })
                    ContractShape.checkApiError(raw)
                    ContractReader.json.decodeFromJsonElement(ApiError.serializer(), raw)
                }.getOrNull()
                if (error?.code == "invalid_cursor") throw SyncCursorReset()
            }
            check(status == 200) { "Local API request failed ($status)" }
            connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally { connection.disconnect() }
    }
    override suspend fun profile(): LearnerProfile {
        val raw = ContractReader.json.parseToJsonElement(request("/v2/profile"))
        ContractShape.checkLearnerProfile(raw)
        return ContractReader.json.decodeFromJsonElement(LearnerProfile.serializer(), raw)
    }
    override suspend fun save(request: ProfileRequest): LearnerProfile {
        val raw = ContractReader.json.parseToJsonElement(request("/v2/profile", ContractReader.json.encodeToString(request)))
        ContractShape.checkLearnerProfile(raw)
        return ContractReader.json.decodeFromJsonElement(LearnerProfile.serializer(), raw)
    }
    override suspend fun targets(cursor: String): TargetPage {
        val suffix = if (cursor.isEmpty()) "" else "?cursor=${URLEncoder.encode(cursor, "UTF-8")}"
        val raw = ContractReader.json.parseToJsonElement(request("/v2/targets$suffix"))
        ContractShape.checkTargetPage(raw)
        return ContractReader.json.decodeFromJsonElement(TargetPage.serializer(), raw)
    }
    override suspend fun catalog(): Catalog {
        val raw = ContractReader.json.parseToJsonElement(request("/v2/catalog"))
        ContractShape.checkCatalog(raw)
        return ContractReader.json.decodeFromJsonElement(Catalog.serializer(), raw)
    }
    override suspend fun sync(cursor: String): SyncPage {
        val raw = ContractReader.json.parseToJsonElement(request("/v2/sync?cursor=${URLEncoder.encode(cursor, "UTF-8")}", resetOnInvalidCursor = true))
        ContractShape.checkSyncPage(raw)
        return ContractReader.json.decodeFromJsonElement(SyncPage.serializer(), raw)
    }
}

@Serializable
data class LearnerCache(
    val version: Int = 1,
    val profile: LearnerProfile? = null,
    val pending: ProfileRequest? = null,
    val targets: List<ConfirmedTarget> = emptyList(),
    val generatedAt: String? = null,
    val catalog: Catalog? = null,
    val practice: NativePractice? = null,
    val syncCursor: String? = null,
    val contentReport: ContentReportRequest? = null,
    val reportRecorded: Boolean = false
)

interface LearnerStore {
    fun read(): LearnerCache
    fun write(value: LearnerCache)
}

/** Atomic replacement in a new debug fixture namespace. Corrupt files remain untouched. */
class AtomicLearnerStore(file: File) : LearnerStore {
    private val atomic = AtomicFile(file)
    override fun read(): LearnerCache {
        val raw = try { atomic.openRead().bufferedReader(Charsets.UTF_8).use { it.readText() } }
        catch (e: java.io.FileNotFoundException) { if (atomic.baseFile.exists()) throw e; return LearnerCache() }
        return ContractReader.json.decodeFromString<LearnerCache>(raw).also { require(it.version == 1) }
    }
    override fun write(value: LearnerCache) {
        val bytes = ContractReader.json.encodeToString(value).toByteArray(Charsets.UTF_8)
        val stream = atomic.startWrite()
        try { stream.write(bytes); atomic.finishWrite(stream) }
        catch (e: Exception) { atomic.failWrite(stream); throw e }
    }
}

class LearnerRepository(private val api: LearnerApi, private val store: LearnerStore) {
    private val mutex = Mutex()
    var state = store.read()
        private set
    private fun commit(next: LearnerCache) { store.write(next); state = next }
    suspend fun reportProblem(category: String) = mutex.withLock {
        val request = if (state.contentReport != null && !state.reportRecorded) requireNotNull(state.contentReport) else {
            val question = requireNotNull(state.practice).question
            ContentReportRequest("v2", UUID.randomUUID().toString(), question.id, question.exercise.revision, category)
        }
        commit(state.copy(contentReport = request, reportRecorded = false))
        val receipt = api.report(request)
        check(receipt.reportId == request.reportId && receipt.status == "recorded")
        commit(state.copy(reportRecorded = true))
    }
    suspend fun startPractice(focus: PracticeFocus? = null) = mutex.withLock {
        // A saved request always wins, including a request awaiting its first response.
        check(focus == null || state.practice == null) { "Resume or discard saved practice first" }
        if(state.practice?.session != null) return@withLock
        check(state.pending == null && state.profile?.setupCompleted == true)
        if (state.practice == null) {
            val catalog = requireNotNull(state.catalog) { "Refresh question availability before starting" }
            val available = when (focus) {
                null -> catalog.targets.sumOf { it.availableQuestionCount }
                is TargetFocus -> catalog.targets.singleOrNull { it.id == focus.id }?.availableQuestionCount ?: 0
                is TopicFocus -> if (catalog.topics.any { it.id == focus.id }) catalog.targets.filter { it.topicId == focus.id }.sumOf { it.availableQuestionCount } else 0
            }
            check(available > 0) { "No questions available for these preferences" }
            val count = if (focus is TargetFocus) 1 else minOf(requireNotNull(state.profile).preferences.sessionQuestionCount, available)
            commit(state.copy(practice = NativePractice(request = com.germanverbmaster.android.foundation.foundationSessionRequest().copy(questionCount = count), focus = focus)))
        }
        val p = requireNotNull(state.practice)
        if (p.session == null) {
            val session = p.focus?.let { api.session(FocusedSessionRequest(p.request.apiVersion, p.request.requestId, p.request.questionCount, p.request.capabilities, it)) } ?: api.session(p.request)
            commit(state.copy(practice = p.copy(session = session)))
        }
    }
    fun draft(answer: Answer?) { val p = requireNotNull(state.practice); check(p.editable); commit(state.copy(practice = p.copy(draft = answer))) }
    fun order(ids: List<String>) { val p = requireNotNull(state.practice); check(p.editable); val exercise = p.question.exercise as ExerciseWordOrder; check(ids.distinct() == ids && ids.all { id -> exercise.tokens.any { it.id == id } }); commit(state.copy(practice = p.copy(order = ids, draft = if(ids.size >= 2) AnswerWordOrder(ids) else null))) }
    fun hint() { val p = requireNotNull(state.practice); check(p.editable); commit(state.copy(practice = p.copy(assisted = true))) }
    suspend fun answer() = mutex.withLock {
        var p = requireNotNull(state.practice)
        check(p.exposure == null && p.evaluation == null && !p.rejected)
        if (p.pending == null) {
            check(nativeAnswerReady(p.question.exercise, p.draft))
            commit(state.copy(practice = p.copy(pending = com.germanverbmaster.android.foundation.foundationAttempt(requireNotNull(p.session), p.index, requireNotNull(p.draft), p.assisted, p.deviceId))))
            p = requireNotNull(state.practice)
        }
        val result = api.submit(requireNotNull(p.pending))
        check(result.attemptId == p.pending.attemptId)
        val evaluation = when(result) { is AttemptAcknowledgment -> result.evaluation; is AttemptDuplicate -> result.evaluation; is AttemptRejection -> null }
        commit(state.copy(practice = p.copy(evaluation = evaluation, rejected = evaluation == null,
            graded = p.graded + if(evaluation != null) 1 else 0, correct = p.correct + if(evaluation?.outcome == "correct") 1 else 0)))
    }
    suspend fun skip() = mutex.withLock {
        var p = requireNotNull(state.practice)
        check(p.pending == null && p.evaluation == null && !p.rejected)
        if(p.exposure == null) {
            commit(state.copy(practice = p.copy(exposure = ExposureEvent(UUID.randomUUID().toString(), p.question.id, p.question.exercise.revision, p.deviceId, "skip", java.time.Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS).toString()))))
            p = requireNotNull(state.practice)
        }
        val ack = api.expose(requireNotNull(p.exposure))
        check(ack.eventId == p.exposure.eventId)
        commit(state.copy(practice = if(ack is ExposureRejected) p.copy(rejected = true) else p.next(true)))
    }
    fun continuePractice() { val p = requireNotNull(state.practice); check(p.evaluation != null); commit(state.copy(practice = p.next())) }
    fun discardPractice() { commit(state.copy(practice = null)) }
    suspend fun refresh() = mutex.withLock {
        val profile = api.profile()
        val catalog = api.catalog()
        if (state.syncCursor != null) pullConfirmed()
        // Refresh time-sensitive due flags, including after successful deltas.
        val fresh = snapshot()
        commit(state.copy(profile = profile, catalog = catalog, targets = fresh.targets,
            generatedAt = fresh.generatedAt, syncCursor = fresh.syncCursor))
    }
    private data class Snapshot(val targets: List<ConfirmedTarget>, val generatedAt: String, val syncCursor: String)
    private suspend fun snapshot(): Snapshot {
        val targets = mutableListOf<ConfirmedTarget>()
        val cursors = mutableSetOf<String>()
        var cursor = ""
        var generatedAt: String? = null
        var watermark: String? = null
        do {
            check(cursors.add(cursor)) { "Repeated snapshot cursor" }
            val page = api.targets(cursor)
            if (generatedAt == null) { generatedAt = page.generatedAt; watermark = page.syncCursor }
            check(page.generatedAt == generatedAt && page.syncCursor == watermark) { "Snapshot changed during pagination" }
            targets.addAll(page.targets)
            cursor = page.nextPageCursor
        } while (cursor.isNotEmpty())
        check(targets.map { it.targetId }.distinct().size == targets.size) { "Duplicate snapshot target" }
        return Snapshot(targets, requireNotNull(generatedAt), requireNotNull(watermark))
    }
    private suspend fun pullConfirmed() {
        val seen = mutableSetOf(requireNotNull(state.syncCursor))
        while (true) {
            val page = try { api.sync(requireNotNull(state.syncCursor)) }
            catch (_: SyncCursorReset) {
                val fresh = snapshot()
                commit(state.copy(targets = fresh.targets, generatedAt = fresh.generatedAt, syncCursor = fresh.syncCursor))
                return
            }
            check(!page.hasMore || seen.add(page.nextCursor)) { "Repeated sync cursor" }
            val targets = state.targets.associateBy { it.targetId }.toMutableMap()
            for (change in page.changes) {
                val prior = targets[change.target.targetId]
                if (prior == null || change.target.lastSequence >= prior.lastSequence) targets[change.target.targetId] = change.target
            }
            // Durable data and cursor together, before requesting another page.
            commit(state.copy(targets = targets.values.toList(), syncCursor = page.nextCursor))
            if (!page.hasMore) return
        }
    }
    suspend fun save(preferences: ProfilePreferences) = mutex.withLock {
        check(state.pending == null) { "Retry or reload pending preferences first" }
        java.time.ZoneId.of(preferences.timezone)
        val profile = requireNotNull(state.profile)
        commit(state.copy(pending = ProfileRequest("v2", UUID.randomUUID().toString(), profile.revision, preferences)))
        sendPending()
    }
    suspend fun retry() = mutex.withLock { sendPending() }
    private suspend fun sendPending() {
        val request = requireNotNull(state.pending)
        api.save(request)
        // Replay may return an older revision: read latest rather than installing it.
        val latest = api.profile()
        commit(state.copy(profile = latest, pending = null))
    }
    /** Explicit recovery only: obtain current server profile before abandoning pending work. */
    suspend fun reloadProfile() = mutex.withLock {
        val latest = api.profile()
        commit(state.copy(profile = latest, pending = null))
    }
}

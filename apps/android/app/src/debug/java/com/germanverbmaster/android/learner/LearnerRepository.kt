package com.germanverbmaster.android.learner

import android.util.AtomicFile
import com.germanverbmaster.android.foundation.ContractReader
import com.germanverbmaster.android.foundation.PreparedPackReader
import com.germanverbmaster.android.foundation.OfflineGrader
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
    suspend fun deleteLearner(request: PrivacyDeleteRequest): PrivacyDeleteReceipt = error("Deletion unavailable")
    suspend fun exportLearner(): LearnerExport = error("Export unavailable")
    suspend fun preparePack(request: SessionRequest): PreparedPack = error("Prepared packs unavailable")
    suspend fun complete(sessionId: String, request: SessionCompletionRequest): SessionCompletionReceipt = error("Completion unavailable")
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
class LocalLearnerApi(private val port: Int = 5001, private val expectedSubject: String? = null) : LearnerApi {
    override suspend fun deleteLearner(request: PrivacyDeleteRequest): PrivacyDeleteReceipt {
        val raw = ContractReader.json.parseToJsonElement(request("/v2/me", ContractReader.json.encodeToString(request), method = "DELETE"))
        ContractShape.checkPrivacyDeleteReceipt(raw)
        return ContractReader.json.decodeFromJsonElement(PrivacyDeleteReceipt.serializer(),raw).also {
            check(it.requestId == request.requestId && (expectedSubject == null || it.subject.equals(expectedSubject,ignoreCase=true))) { "Deletion receipt mismatch" }
        }
    }
    override suspend fun exportLearner(): LearnerExport {
        val raw = ContractReader.json.parseToJsonElement(request("/v2/me/export"))
        ContractShape.checkLearnerExport(raw)
        return ContractReader.json.decodeFromJsonElement(LearnerExport.serializer(),raw).also {
            check(expectedSubject == null || it.subject.equals(expectedSubject,ignoreCase=true)) { "Export account mismatch" }
        }
    }
    override suspend fun preparePack(request: SessionRequest): PreparedPack = PreparedPackReader.read(request("/v2/packs", ContractReader.json.encodeToString(request))).also { check(it.packId == request.requestId) }
    override suspend fun complete(sessionId: String, request: SessionCompletionRequest): SessionCompletionReceipt {
        val raw = ContractReader.json.parseToJsonElement(request("/v2/sessions/$sessionId/complete", ContractReader.json.encodeToString(request)))
        ContractShape.checkSessionCompletionReceipt(raw)
        return ContractReader.json.decodeFromJsonElement(SessionCompletionReceipt.serializer(), raw).also {
            check(it.sessionId == sessionId && it.requestId == request.requestId && it.mode == request.mode)
        }
    }
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
    private suspend fun request(path: String, body: String? = null, resetOnInvalidCursor: Boolean = false, method: String = "POST"): String = withContext(Dispatchers.IO) {
        val connection = URI("http://127.0.0.1:$port$path").toURL().openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("Authorization", "Bearer foundation-local-demo")
            expectedSubject?.let { connection.setRequestProperty("X-Learner-Subject", it) }
            connection.setRequestProperty("Cache-Control", "no-store")
            if (body != null) {
                connection.requestMethod = method
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
    val subjectId: String? = null,
    val deletion: PrivacyDeleteRequest? = null,
    val deletionReceipt: PrivacyDeleteReceipt? = null,
    val deletionLocalComplete: Boolean = false,
    val signedOut: Boolean = false,
    val localRemovalPending: Boolean = false,
    val profile: LearnerProfile? = null,
    val pending: ProfileRequest? = null,
    val targets: List<ConfirmedTarget> = emptyList(),
    val generatedAt: String? = null,
    val catalog: Catalog? = null,
    val practice: NativePractice? = null,
    val syncCursor: String? = null,
    val contentReport: ContentReportRequest? = null,
    val reportRecorded: Boolean = false,
    val packRequest: SessionRequest? = null,
    val preparedPack: PreparedPack? = null,
    val consumedPreparedSessions: List<String> = emptyList(),
    val completedOffline: List<NativePractice> = emptyList()
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
        return ContractReader.json.decodeFromString<LearnerCache>(raw).also {
            require(it.version == 1)
            it.preparedPack?.let { pack -> PreparedPackReader.read(ContractReader.json.encodeToString(pack)) }
            (it.completedOffline + listOfNotNull(it.practice)).forEach { p -> p.offlinePack?.let { pack ->
                PreparedPackReader.read(ContractReader.json.encodeToString(pack))
                require(pack.sessions.any { session -> session == p.session })
            } }
        }
    }
    override fun write(value: LearnerCache) {
        val bytes = ContractReader.json.encodeToString(value).toByteArray(Charsets.UTF_8)
        val stream = atomic.startWrite()
        try { stream.write(bytes); atomic.finishWrite(stream) }
        catch (e: Exception) { atomic.failWrite(stream); throw e }
    }
}

class LearnerRepository(transport: LearnerApi, private val store: LearnerStore, private val account: LearnerAccount? = null) {
    private val deletionApi = account?.let { BoundLearnerApi(transport, it) } ?: transport
    private val api = ActiveLearnerApi(deletionApi) { check(state.deletion == null && !state.signedOut) { "Learner deletion pending or completed, or signed out" } }
    private val mutex = Mutex()
    var state = store.read()
        private set
    private fun commit(next: LearnerCache) { check(state.deletion == null && !state.signedOut); commitPrivacy(next) }
    private fun commitPrivacy(next: LearnerCache) { store.write(next); state = next }
    init {
        check(!state.deletionLocalComplete || state.deletionReceipt != null)
        check(!state.localRemovalPending || state.signedOut)
        check(state.deletion == null || !state.signedOut)
        check(state.deletionReceipt == null || state.deletion != null && state.deletionReceipt!!.requestId == state.deletion!!.requestId && state.deletionReceipt!!.subject == state.subjectId) { "Deletion receipt ownership mismatch" }
        account?.let {
            val subject = it.identity.subject
            check(state.subjectId == subject || state.subjectId == null && (state == LearnerCache() || subject == FIXTURE_SUBJECT)) {
                "Saved learner data belongs to a different account"
            }
            if (state.subjectId == null) commit(state.copy(subjectId = subject))
        }
    }
    /** Explicit retry only. The mutex drains earlier operations before freezing deletion. */
    suspend fun deleteLearner() = mutex.withLock {
        check(!state.signedOut)
        account?.assertCurrent()
        val subject = requireNotNull(state.subjectId) { "Deletion requires a bound learner" }
        val request = state.deletion ?: PrivacyDeleteRequest("v2",UUID.randomUUID().toString(),"delete_owned_data").also {
            commitPrivacy(state.copy(deletion = it))
        }
        val receipt = state.deletionReceipt ?: deletionApi.deleteLearner(request).also {
            account?.assertCurrent()
            check(it.subject == subject && it.requestId == request.requestId) { "Deletion receipt mismatch" }
            commitPrivacy(state.copy(deletionReceipt = it))
        }
        account?.assertCurrent()
        // One atomic replacement removes only this subject's cache; retain a terminal marker.
        if(!state.deletionLocalComplete) commitPrivacy(LearnerCache(subjectId = subject,deletion = request,deletionReceipt = receipt,deletionLocalComplete = true))
    }
    suspend fun signOut(removeLocal: Boolean) = mutex.withLock {
        check(state.deletion == null)
        account?.assertCurrent()
        val subject = requireNotNull(state.subjectId) { "Sign-out requires a bound learner" }
        if(!state.signedOut) {
            if(!removeLocal) { syncSavedWorkOwned(); account?.assertCurrent() }
            commitPrivacy(state.copy(signedOut = true,localRemovalPending = removeLocal))
        }
        if(state.localRemovalPending) {
            account?.assertCurrent()
            commitPrivacy(LearnerCache(subjectId = subject,signedOut = true))
        }
    }
    suspend fun resumeLocalFixture() = mutex.withLock {
        account?.assertCurrent()
        check(state.deletion == null && state.signedOut && !state.localRemovalPending)
        commitPrivacy(state.copy(signedOut = false))
    }
    suspend fun prepareReserve() = mutex.withLock { prepareReserveOwned() }
    private suspend fun prepareReserveOwned() {
        val request = state.packRequest ?: run {
            check(state.pending == null && state.profile?.setupCompleted == true)
            val available = requireNotNull(state.catalog).targets.sumOf { it.availableQuestionCount }
            check(available > 0)
            val request = com.germanverbmaster.android.foundation.foundationSessionRequest().copy(questionCount = minOf(requireNotNull(state.profile).preferences.sessionQuestionCount,available))
            commit(state.copy(packRequest = request))
            request
        }
        val response = api.preparePack(request)
        val pack = PreparedPackReader.read(ContractReader.json.encodeToString(response))
        check(pack.packId == request.requestId)
        // Whole validated reserve and acknowledgment together; practice and writes untouched.
        commit(state.copy(preparedPack = pack,packRequest = null, consumedPreparedSessions = emptyList()))
    }
    suspend fun startOffline(now: java.time.Instant = java.time.Instant.now()) = mutex.withLock {
        check(state.practice == null) { "Resume saved practice first" }
        val pack = PreparedPackReader.read(ContractReader.json.encodeToString(requireNotNull(state.preparedPack)))
        check(PreparedPackReader.canStart(pack,now)) { "Prepared reserve expired" }
        val session = pack.sessions.firstOrNull { it.id !in state.consumedPreparedSessions } ?: error("Prepared reserve exhausted")
        val request = com.germanverbmaster.android.foundation.foundationSessionRequest().copy(questionCount = session.questions.size)
        // Slot consumption and complete pinned practice commit together before any question is shown.
        commit(state.copy(practice = NativePractice(request = request,session = session,offlinePack = pack),
            consumedPreparedSessions = state.consumedPreparedSessions + session.id))
    }
    suspend fun syncOffline(sessionId: String) = mutex.withLock { syncOfflineOwned(sessionId) }
    private suspend fun syncOfflineOwned(sessionId: String) {
        fun current(): NativePractice = state.practice?.takeIf { it.session?.id == sessionId && it.offlinePack != null }
            ?: state.completedOffline.single { it.session?.id == sessionId }
        fun save(p: NativePractice) {
            if (state.practice?.session?.id == sessionId) commit(state.copy(practice = p))
            else commit(state.copy(completedOffline = state.completedOffline.map { if(it.session?.id == sessionId) p else it }))
        }
        PreparedPackReader.read(ContractReader.json.encodeToString(requireNotNull(current().offlinePack)))
        while (true) {
            val p = current()
            val index = p.outbox.indexOfFirst { !it.delivered }
            if (index < 0) break
            val event = p.outbox[index]
            check((event.attemptReceipt as? AttemptRejection)?.error?.retryable != false &&
                (event.exposureReceipt as? ExposureRejected)?.error?.retryable != false) { "Rejected event requires review" }
            val next = when {
                event.attempt != null -> {
                    val receipt = api.submit(event.attempt)
                    check(receipt.attemptId == event.attempt.attemptId)
                    event.copy(attemptReceipt = receipt)
                }
                event.exposure != null -> {
                    val receipt = api.expose(event.exposure)
                    check(receipt.eventId == event.exposure.eventId)
                    event.copy(exposureReceipt = receipt)
                }
                else -> {
                    val request = requireNotNull(event.completion)
                    val receipt = api.complete(sessionId,request)
                    val graded = p.outbox.count { it.attempt != null }; val skipped = p.outbox.count { it.exposure != null }
                    val correct = p.outbox.count { when(val ack = it.attemptReceipt) {
                        is AttemptAcknowledgment -> ack.evaluation.outcome == "correct"
                        is AttemptDuplicate -> ack.evaluation.outcome == "correct"
                        else -> false
                    } }
                    check(receipt.requestId == request.requestId && receipt.sessionId == sessionId && receipt.mode == request.mode)
                    check(receipt.plannedCount == p.session?.questions?.size && receipt.gradedCount == graded && receipt.skippedCount == skipped && receipt.correctCount == correct)
                    event.copy(completionReceipt = receipt)
                }
            }
            val latest = current(); val outbox = latest.outbox.toMutableList(); outbox[index] = next
            save(latest.copy(outbox = outbox,completionReceipt = next.completionReceipt ?: latest.completionReceipt))
            check(next.attemptReceipt !is AttemptRejection && next.exposureReceipt !is ExposureRejected) { "Offline event not accepted" }
        }
    }
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
    suspend fun startPractice(focus: PracticeFocus? = null) = mutex.withLock { startPracticeOwned(focus) }
    private suspend fun startPracticeOwned(focus: PracticeFocus? = null) {
        // A saved request always wins, including a request awaiting its first response.
        check(focus == null || state.practice == null) { "Resume or discard saved practice first" }
        if(state.practice?.session != null) return
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
    suspend fun answer() = mutex.withLock { answerOwned() }
    private suspend fun answerOwned() {
        var p = requireNotNull(state.practice)
        check(p.completion == null && p.exposure == null && p.evaluation == null && !p.rejected)
        if (p.pending == null) {
            check(nativeAnswerReady(p.question.exercise, p.draft))
            commit(state.copy(practice = p.copy(pending = com.germanverbmaster.android.foundation.foundationAttempt(requireNotNull(p.session), p.index, requireNotNull(p.draft), p.assisted, p.deviceId))))
            p = requireNotNull(state.practice)
        }
        if (p.offlinePack != null) {
            val question = p.question
            val rubric = p.offlinePack.rubrics.single { it.exerciseId == question.exercise.id && it.exerciseRevision == question.exercise.revision }
            val evaluation = OfflineGrader.grade(question.exercise,rubric,
                ContractReader.json.encodeToJsonElement(Answer.serializer(), requireNotNull(p.pending).answer),requireNotNull(p.pending).assistance)
            commit(state.copy(practice = p.copy(evaluation = evaluation,graded = p.graded + 1,
                correct = p.correct + if(evaluation.outcome == "correct") 1 else 0,
                outbox = p.outbox + NativeOfflineWrite(attempt = p.pending,provisional = evaluation))))
            return
        }
        val result = api.submit(requireNotNull(p.pending))
        check(result.attemptId == p.pending.attemptId)
        val evaluation = when(result) { is AttemptAcknowledgment -> result.evaluation; is AttemptDuplicate -> result.evaluation; is AttemptRejection -> null }
        commit(state.copy(practice = p.copy(evaluation = evaluation, rejected = evaluation == null,
            graded = p.graded + if(evaluation != null) 1 else 0, correct = p.correct + if(evaluation?.outcome == "correct") 1 else 0)))
    }
    suspend fun skip() = mutex.withLock { skipOwned() }
    private suspend fun skipOwned() {
        var p = requireNotNull(state.practice)
        check(p.completion == null && p.pending == null && p.evaluation == null && !p.rejected)
        if(p.exposure == null) {
            commit(state.copy(practice = p.copy(exposure = ExposureEvent(UUID.randomUUID().toString(), p.question.id, p.question.exercise.revision, p.deviceId, "skip", java.time.Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS).toString()))))
            p = requireNotNull(state.practice)
        }
        if (p.offlinePack != null) {
            commit(state.copy(practice = p.next(true,p.outbox + NativeOfflineWrite(exposure = p.exposure))))
            return
        }
        val ack = api.expose(requireNotNull(p.exposure))
        check(ack.eventId == p.exposure.eventId)
        commit(state.copy(practice = if(ack is ExposureRejected) p.copy(rejected = true) else p.next(true)))
    }
    fun continuePractice() { val p = requireNotNull(state.practice); check(p.completion == null && p.evaluation != null); commit(state.copy(practice = p.next())) }
    suspend fun finishPractice() = mutex.withLock { finishPracticeOwned() }
    private suspend fun finishPracticeOwned() {
        var p = requireNotNull(state.practice)
        val session = requireNotNull(p.session)
        check((p.pending == null || p.evaluation != null) && p.exposure == null && !p.rejected)
        if (p.completionReceipt != null) return
        if (p.offlinePack != null) {
            if (p.completion == null) {
                val request = SessionCompletionRequest("v2",UUID.randomUUID().toString(),if(p.graded + p.skipped == session.questions.size) "full" else "partial")
                commit(state.copy(practice = p.copy(completion = request,outbox = p.outbox + NativeOfflineWrite(completion = request))))
            }
            return
        }
        if (p.completion == null) {
            commit(state.copy(practice = p.copy(completion = SessionCompletionRequest("v2", UUID.randomUUID().toString(), if(p.graded + p.skipped == session.questions.size) "full" else "partial"))))
            p = requireNotNull(state.practice)
        }
        val request = requireNotNull(p.completion)
        val receipt = api.complete(session.id, request)
        check(receipt.sessionId == session.id && receipt.requestId == request.requestId && receipt.mode == request.mode)
        check(receipt.plannedCount == session.questions.size && receipt.gradedCount + receipt.skippedCount <= receipt.plannedCount && receipt.correctCount <= receipt.gradedCount)
        check(request.mode != "full" || receipt.gradedCount + receipt.skippedCount == receipt.plannedCount)
        commit(state.copy(practice = p.copy(completionReceipt = receipt)))
    }
    fun discardPractice() {
        val p = state.practice
        if (p?.offlinePack != null) {
            check(p.completion != null) { "End the offline session before returning" }
            commit(state.copy(practice = null,completedOffline = state.completedOffline + p))
        } else { check(p?.let { it.completion == null || it.completionReceipt != null } != false); commit(state.copy(practice = null)) }
    }
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
    /** One explicit, dependency-ordered pass. Each helper commits before the next write.
     * No new answer, completion, session or download is invented during reconciliation. */
    suspend fun exportLearner(syncFirst: Boolean): LearnerExport = mutex.withLock {
        if (syncFirst) syncSavedWorkOwned()
        api.exportLearner()
    }
    suspend fun syncSavedWork() = mutex.withLock { syncSavedWorkOwned() }
    private suspend fun syncSavedWorkOwned() {
        check(state.deletion == null && !state.signedOut) { "Learner deletion pending or completed, or signed out" }
        if (state.pending != null) sendPending()
        val online = state.practice?.takeIf { it.offlinePack == null }
        if (online != null) {
            check(!online.rejected) { "Rejected event requires review" }
            if (online.session == null) startPracticeOwned()
            if (online.pending != null && online.evaluation == null) answerOwned()
            if (online.exposure != null) skipOwned()
            check(state.practice?.rejected != true) { "Saved event not accepted" }
            if (online.completion != null && online.completionReceipt == null) finishPracticeOwned()
        }
        // Archive order is durable; active practice comes after archived sessions.
        val offline = state.completedOffline + listOfNotNull(state.practice?.takeIf { it.offlinePack != null })
        offline.forEach { syncOfflineOwned(requireNotNull(it.session).id) }
        val report = state.contentReport
        if (report != null && !state.reportRecorded) {
            val receipt = api.report(report)
            check(receipt.reportId == report.reportId && receipt.status == "recorded")
            commit(state.copy(reportRecorded = true))
        }
        if (state.packRequest != null) prepareReserveOwned()
    }
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

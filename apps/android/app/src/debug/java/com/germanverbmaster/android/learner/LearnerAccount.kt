package com.germanverbmaster.android.learner

import com.germanverbmaster.android.foundation.contract.*
import java.io.File
import java.util.UUID

const val FIXTURE_SUBJECT = "00000000-0000-4000-8000-000000000010"
data class LearnerIdentity(val subject: String, val generation: Long)

/** A captured subject/generation, never a bearer token. */
class LearnerAccount(identity: LearnerIdentity, private val current: () -> LearnerIdentity?) {
    val identity = identity.copy(subject = UUID.fromString(identity.subject).toString())
    init { require(identity.generation >= 0 && this.identity.subject.equals(identity.subject, ignoreCase = true)) }
    fun assertCurrent() {
        val active = current()
        check(active != null && active.subject.lowercase() == identity.subject && active.generation == identity.generation) {
            "Sign in to the saved learner account before syncing"
        }
    }
    fun store(directory: File): AtomicLearnerStore {
        val name = if (identity.subject == FIXTURE_SUBJECT) "german-master-v2-local-learner.json"
            else "german-master-v2-learner-${identity.subject}.json"
        return AtomicLearnerStore(File(directory, name))
    }
}

internal class BoundLearnerApi(private val api: LearnerApi, private val account: LearnerAccount) : LearnerApi {
    override suspend fun deleteLearner(request: PrivacyDeleteRequest) = bound { api.deleteLearner(request) }.also {
        check(it.subject == account.identity.subject && it.requestId == request.requestId) { "Deletion receipt mismatch" }
    }
    override suspend fun exportLearner() = bound { api.exportLearner() }.also {
        check(it.subject.equals(account.identity.subject,ignoreCase=true)) { "Export account mismatch" }
    }
    private suspend fun <T> bound(work: suspend () -> T): T {
        account.assertCurrent(); val result = work(); account.assertCurrent(); return result
    }
    override suspend fun preparePack(request: SessionRequest) = bound { api.preparePack(request) }
    override suspend fun complete(sessionId: String, request: SessionCompletionRequest) = bound { api.complete(sessionId, request) }
    override suspend fun report(request: ContentReportRequest) = bound { api.report(request) }
    override suspend fun session(request: FocusedSessionRequest) = bound { api.session(request) }
    override suspend fun session(request: SessionRequest) = bound { api.session(request) }
    override suspend fun submit(attempt: Attempt) = bound { api.submit(attempt) }
    override suspend fun expose(event: ExposureEvent) = bound { api.expose(event) }
    override suspend fun profile() = bound { api.profile() }
    override suspend fun save(request: ProfileRequest) = bound { api.save(request) }
    override suspend fun targets(cursor: String) = bound { api.targets(cursor) }
    override suspend fun sync(cursor: String) = bound { api.sync(cursor) }
    override suspend fun catalog() = bound { api.catalog() }
}

/** Every read/write is blocked while the durable deletion marker exists. */
internal class ActiveLearnerApi(private val api: LearnerApi, private val active: () -> Unit) : LearnerApi {
    private suspend fun <T> run(work: suspend () -> T): T { active(); val result = work(); active(); return result }
    override suspend fun exportLearner() = run { api.exportLearner() }
    override suspend fun preparePack(request: SessionRequest) = run { api.preparePack(request) }
    override suspend fun complete(sessionId: String, request: SessionCompletionRequest) = run { api.complete(sessionId,request) }
    override suspend fun report(request: ContentReportRequest) = run { api.report(request) }
    override suspend fun session(request: FocusedSessionRequest) = run { api.session(request) }
    override suspend fun session(request: SessionRequest) = run { api.session(request) }
    override suspend fun submit(attempt: Attempt) = run { api.submit(attempt) }
    override suspend fun expose(event: ExposureEvent) = run { api.expose(event) }
    override suspend fun profile() = run { api.profile() }
    override suspend fun save(request: ProfileRequest) = run { api.save(request) }
    override suspend fun targets(cursor: String) = run { api.targets(cursor) }
    override suspend fun sync(cursor: String) = run { api.sync(cursor) }
    override suspend fun catalog() = run { api.catalog() }
}
